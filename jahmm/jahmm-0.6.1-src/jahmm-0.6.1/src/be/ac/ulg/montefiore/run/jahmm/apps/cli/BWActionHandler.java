/* jahmm package - v0.6.1 */

/*
 *  *  Copyright (c) 2004-2006, Jean-Marc Francois.
 *
 *  This file is part of Jahmm.
 *  Jahmm is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Jahmm is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Jahmm; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */

package be.ac.ulg.montefiore.run.jahmm.apps.cli;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import sun.dc.pr.PRException;
import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.apps.cli.CommandLineArguments.Arguments;
import be.ac.ulg.montefiore.run.jahmm.io.*;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchScaledLearner;


/**
 * Applies the Baum-Welch learning algorithm.
 */
class BWActionHandler
extends ActionHandler
{
	public void act()
	throws FileNotFoundException, IOException, FileFormatException, 
	AbnormalTerminationException
	{
		EnumSet<Arguments> args = EnumSet.of(
				Arguments.OPDF,
				Arguments.OUT_HMM,
				Arguments.IN_HMM,
				Arguments.IN_SEQ,
				Arguments.NB_ITERATIONS,
				Arguments.N_LEARN_SEQLEN,
				Arguments.OUT_PRED_PROB);
		CommandLineArguments.checkArgs(args);
		
		int nbIterations = Arguments.NB_ITERATIONS.getAsInt();
		int learnSeqLen = Arguments.N_LEARN_SEQLEN.getAsInt();
		OutputStream outStream = Arguments.OUT_HMM.getAsOutputStream();
		Writer hmmWriter = new OutputStreamWriter(outStream);
		InputStream hmmStream = Arguments.IN_HMM.getAsInputStream();
		InputStream seqStream = Arguments.IN_SEQ.getAsInputStream();
		OutputStream outPredProbStream = Arguments.OUT_PRED_PROB.getAsOutputStream();
		Writer predProbWriter = new OutputStreamWriter(outPredProbStream);
		Reader hmmReader = new InputStreamReader(hmmStream);
		Reader seqReader = new InputStreamReader(seqStream);
		
		learnAndPredict(Types.relatedObjs(), hmmReader, seqReader, learnSeqLen,
				hmmWriter, predProbWriter, nbIterations);
		
		hmmWriter.flush();
		predProbWriter.flush();
	}
	
	/*
	private <O extends Observation & CentroidFactory<O>> void
	learn(RelatedObjs<O> relatedObjs, Reader hmmFileReader,
			Reader seqFileReader, Writer hmmFileWriter,
			int nbIterations)
	throws IOException, FileFormatException
	{
		List<List<O>> seqs = relatedObjs.readSequences(seqFileReader);
		OpdfReader<? extends Opdf<O>> opdfReader = relatedObjs.opdfReader();
		OpdfWriter<? extends Opdf<O>> opdfWriter = relatedObjs.opdfWriter();
		
		Hmm<O> initHmm = HmmReader.read(hmmFileReader, opdfReader);
		BaumWelchLearner bw = new BaumWelchScaledLearner();
		bw.setNbIterations(nbIterations);
		Hmm<O> hmm = bw.learn(initHmm, seqs);
		HmmWriter.write(hmmFileWriter, opdfWriter, hmm);		
	}
	*/
	
	private double sumAlphaAij(double[][] alpha, double[][] a_ij, int j, int t)
	{
		double sum = 0.0;
		for (int i = 0, M = alpha[t].length; i < M; ++i)
		{
			sum += (alpha[t][i] * a_ij[i][j]);
		}
		return sum;
	}
	
	private double probPred(double[][] alpha, double[][] a_ij, int j, int t)
	{
		double numer = sumAlphaAij(alpha, a_ij, j, t);
		double deno = 0.0;
		for (int j2 = 0, M = alpha[t].length; j2 < M; ++j2)
		{
			deno += sumAlphaAij(alpha, a_ij, j2, t);
		}
		
		return numer/deno;
	}
	
	private <O extends Observation & CentroidFactory<O>> void 
	writeStateProbabilities(BaumWelchLearner bw, Hmm<O> hmm, ArrayList<List<O>> seqs,
			Writer ppFileWriter) throws IOException
	{
		double[][][] alphas = bw.getAlpha();
		DecimalFormat formatter = new DecimalFormat();
		
		for (int x = 0, N = seqs.size(); x < N; ++x)
		{
			double[][] alpha = alphas[x];
			int t = alpha.length;
			double[][] a_ij = hmm.getAij();
			double[] stateProbabilities = new double[hmm.nbStates()];
			for (int j = 0, M = hmm.nbStates(); j < M; ++j)
			{
				stateProbabilities[j] = probPred(alpha, a_ij, j, t - 1);
			}
			for (int j = 0, M = hmm.nbStates() - 1; j < M; ++j)
			{
				ppFileWriter.write(formatter.format(stateProbabilities[j]) + ",");
			}
			ppFileWriter.write(formatter.format(stateProbabilities[hmm.nbStates() - 1]));
		}
		ppFileWriter.write("\n");
		ppFileWriter.flush();
	}
	
	private <O extends Observation & CentroidFactory<O>> void
	learnAndPredict(RelatedObjs<O> relatedObjs, Reader hmmFileReader,
			Reader seqFileReader, int learnSeqLen, Writer hmmFileWriter,
			Writer ppFileWriter, int nbIterations)
	throws IOException, FileFormatException
	{
		// first is arraylist of obs seqs
		// second is arraylist of obs to be used for prediction testing
		Pair<ArrayList<List<O>>, ArrayList<List<O>>> seqs = relatedObjs.readSequences(seqFileReader, learnSeqLen);
		OpdfReader<? extends Opdf<O>> opdfReader = relatedObjs.opdfReader();
		OpdfWriter<? extends Opdf<O>> opdfWriter = relatedObjs.opdfWriter();
		
		Hmm<O> initHmm = HmmReader.read(hmmFileReader, opdfReader);
		BaumWelchLearner bw = new BaumWelchScaledLearner();
		bw.setNbIterations(nbIterations);
		// learn the params of the hmm using the first list of seqs
		Hmm<O> hmm = bw.learn(initHmm, seqs.first);
		HmmWriter.write(hmmFileWriter, opdfWriter, hmm);
		
		ArrayList<List<O>> learnSeqs = seqs.first;
		ArrayList<List<O>> predSeqs = seqs.second;
		
		writeStateProbabilities(bw, hmm, learnSeqs, ppFileWriter);
		
		int seq_sz = learnSeqs.size();
		while (predSeqs.get(0).size() > 0)
		{
			for(int indx = 0; indx < seq_sz; ++indx)
			{
				learnSeqs.get(indx).add(predSeqs.get(indx).remove(0));
			}
			hmm = bw.learn(hmm, learnSeqs);
			writeStateProbabilities(bw, hmm, learnSeqs, ppFileWriter);
		}
	}
}
