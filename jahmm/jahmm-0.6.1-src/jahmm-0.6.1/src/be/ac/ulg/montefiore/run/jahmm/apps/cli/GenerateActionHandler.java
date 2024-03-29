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
import java.util.*;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.apps.cli.CommandLineArguments.Arguments;
import be.ac.ulg.montefiore.run.jahmm.io.*;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;


/**
 * Generates observation sequences from a HMM and write it to file.
 */
class GenerateActionHandler
extends ActionHandler
{
	public void act()
	throws FileNotFoundException, IOException, FileFormatException,
	AbnormalTerminationException
	{
		EnumSet<Arguments> args = EnumSet.of(
				Arguments.OPDF,
				Arguments.OUT_SEQS,
				Arguments.OUT_STATE_SEQS,
				Arguments.IN_HMM,
				Arguments.N_SEQ,
				Arguments.N_SEQLEN);
		CommandLineArguments.checkArgs(args);
		
		InputStream hmmStream = Arguments.IN_HMM.getAsInputStream();
		Reader hmmFileReader = new InputStreamReader(hmmStream);
		OutputStream seqsStream = Arguments.OUT_SEQS.getAsOutputStream();		
		Writer seqsFileWriter = new OutputStreamWriter(seqsStream);
		OutputStream stateSeqsStream = Arguments.OUT_STATE_SEQS.getAsOutputStream();
		Writer stateSeqsFileWriter = new OutputStreamWriter(stateSeqsStream);
		
		write(hmmFileReader, seqsFileWriter, stateSeqsFileWriter , Types.relatedObjs());
		
		seqsFileWriter.flush();
		stateSeqsFileWriter.flush();
	}
	
	
	private <O extends Observation & CentroidFactory<O>> void
	write(Reader hmmFileReader, Writer seqsFileWriter,
			Writer stateSeqsFileWriter, RelatedObjs<O> relatedObjs)
	throws IOException, FileFormatException, WrongArgumentsException
	{
		ObservationWriter<O> obsWriter = relatedObjs.observationWriter();
		OpdfReader<? extends Opdf<O>> opdfReader = relatedObjs.opdfReader();
		Hmm<O> hmm = HmmReader.read(hmmFileReader, opdfReader);
		
		MarkovGenerator<O> generator = relatedObjs.generator(hmm);
		
		List<List<O>> seqs = new ArrayList<List<O>>();
		for (int i = 0; i < Arguments.N_SEQ.getAsInt(); i++)
			seqs.add(generator.observationSequence(Arguments.N_SEQLEN.getAsInt()));
		
		ObservationSequencesWriter.write(seqsFileWriter, stateSeqsFileWriter, obsWriter, seqs);
	}
	/*
	private <O extends Observation & CentroidFactory<O>> void
	write(Reader hmmFileReader, Writer seqsFileWriter,
			RelatedObjs<O> relatedObjs)
	throws IOException, FileFormatException, WrongArgumentsException
	{
		ObservationWriter<O> obsWriter = relatedObjs.observationWriter();
		OpdfReader<? extends Opdf<O>> opdfReader = relatedObjs.opdfReader();
		Hmm<O> hmm = HmmReader.read(hmmFileReader, opdfReader);
		
		MarkovGenerator<O> generator = relatedObjs.generator(hmm);
		
		List<List<Pair<O, Integer>>> seqs = new ArrayList<List<Pair<O, Integer>>>();
		for (int i = 0; i < Arguments.N_SEQ.getAsInt(); i++)
			seqs.add(generator.observationStateSequence(Arguments.N_SEQLEN.getAsInt()));
		
		ObservationSequencesWriter.writeStateAndSequence(seqsFileWriter, obsWriter, seqs);
	}
	*/
}
