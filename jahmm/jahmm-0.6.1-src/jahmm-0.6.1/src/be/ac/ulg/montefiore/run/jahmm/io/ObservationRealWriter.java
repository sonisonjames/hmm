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
import java.io.Writer;

import be.ac.ulg.montefiore.run.jahmm.ObservationReal;
import be.ac.ulg.montefiore.run.jahmm.Pair;
import java.math.BigDecimal;

/**
 * Writes an {@link ObservationReal ObservationReal} up to (and including) the
 * semi-colon.
 */
public class ObservationRealWriter
extends ObservationWriter<ObservationReal>
{	
	public void write(ObservationReal observation, Writer writer) 
	throws IOException
	{
		writer.write(BigDecimal.valueOf(observation.value).toPlainString() + sep + " ");
	}

	@Override
	public void write(ObservationReal obs, Writer obsWriter,
			Writer stateWriter) throws IOException {
		obsWriter.write(BigDecimal.valueOf(obs.value).toPlainString() + sep +" ");
		stateWriter.write(obs.state + sep +" ");
		
	}
}
