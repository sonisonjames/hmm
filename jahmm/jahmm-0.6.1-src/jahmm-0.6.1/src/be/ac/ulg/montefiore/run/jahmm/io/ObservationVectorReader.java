/* jahmm package - v0.6.1 */

/*
  *  Copyright (c) 2004-2006, Jean-Marc Francois.
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

package be.ac.ulg.montefiore.run.jahmm.io;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.ObservationVector;


/**
 * Reads an {@link be.ac.ulg.montefiore.run.jahmm.ObservationVector
 * ObservationVector} up to (and including) a semi-colon.
 * <p>
 * The format of this observation is an opening bracket (<tt>[</tt>) followed
 * by the components of the vector separated by spaces or tabs.  Each
 * component is a number (following the format
 * [+-]?[0123456789]+[.]?[0123456789]*).
 * <p>
 * For example, reading
 * <pre>[76 45. -2.23];</pre>
 * creates an observation such as the one generated by
 * <code>new ObservationVector(new double[] {76., 45., -2.23});</code>
 */
public class ObservationVectorReader
extends ObservationReader<ObservationVector>
{
	private int dimension;
	
	
	/**
	 * Constructs a reader of {@link ObservationVector ObservationVector}.
	 */
	public ObservationVectorReader()
	{
		dimension = -1;
	}
	
	
	/**
	 * Constructs a reader of {@link ObservationVector ObservationVector}.
	 * Verifies the dimension of the observations read.
	 *
	 * @param dimension The dimension of each observation.
	 */
	public ObservationVectorReader(int dimension)
	{
		if (dimension <= 0)
			throw new IllegalArgumentException("Argument must be strictly " +
			"positive");
		
		this.dimension = dimension;
	}
	
	
	/**
	 * An {@link be.ac.ulg.montefiore.run.jahmm.ObservationInteger
	 * ObservationInteger} reader, as explained in 
	 * {@link ObservationReader ObservationReader}.
	 *
	 * @param st A stream tokenizer.
	 * @return An {@link be.ac.ulg.montefiore.run.jahmm.ObservationInteger
	 *         ObservationInteger}.
	 */
	public ObservationVector read(StreamTokenizer st) 
	throws IOException, FileFormatException
	{
		if (st.nextToken() != (int) '[')
			throw new FileFormatException(st.lineno(), "'[' expected");
		
		List<Double> values = new ArrayList<Double>();
		
		loop: 
			while(true)
				switch (st.nextToken()) {
				case StreamTokenizer.TT_NUMBER:
					values.add(new Double(st.nval));
					break;
					
				case ']':
					if (values.size() == 0)
						throw new FileFormatException(st.lineno(), 
								"Empty vector found");
					break loop;
					
				default:
					throw new FileFormatException(st.lineno(),
							"Number or ']' expected");
				}
		
		if (st.nextToken() != (int) ';')
			throw new FileFormatException(st.lineno(), "';' expected");
		
		if (dimension > 0 && values.size() != dimension)
			throw new FileFormatException(st.lineno(),
					"Bad observation: wrong dimension (" + values.size() +
					" instead of " + dimension +")");
		
		double[] valuesArray = new double[values.size()];
		for (int i = 0; i < values.size(); i++)
			valuesArray[i] = (values.get(i)).doubleValue();
		
		return new ObservationVector(valuesArray, -1);
	}
}
