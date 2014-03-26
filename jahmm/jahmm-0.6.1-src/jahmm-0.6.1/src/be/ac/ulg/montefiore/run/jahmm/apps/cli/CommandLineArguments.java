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
import java.util.Arrays;
import java.util.EnumSet;



public class CommandLineArguments
{
	/*
	 * Allowed command-line arguments description
	 * The special allowed arguments list "" means any argument is allowed.
	 * The special allowed arguments list "x" means that x is the default
	 * value.
	 */
	public static enum Arguments {
		IN_HMM("-i", "-"),
		IN_KL_HMM("-ikl", ""),
		OUT_HMM("-o", "-"),
		IN_SEQ("-is", ""),
		OUT_SEQS("-os", "-"),
		OUT_STATE_SEQS("-oss", "-"),
		OPDF("-opdf", "integer", "gaussian", "gaussian_mixture",
				"multi_gaussian"),
		INTEGER_RANGE("-r", ""),
		NB_GAUSSIANS("-ng", ""),
		VECTOR_DIMENSION("-d", ""),
		NB_ITERATIONS("-ni", "10"),
		NB_STATES("-n", ""),
		N_SEQ("-N", "1"),
		N_SEQLEN("-L", "1000"),
		N_LEARN_SEQLEN("-LL", "1000"),
		OUT_PRED_PROB("-opp", "-"),
		DEBUG("-D");
		
		final String argString;       // The expected string for the arg
		final String[] allowedValues; // Accepted values.  If "", any
		private String value;         // Sub-argument value
		private String defaultValue;  // Sub-argument default value
		private boolean isSet;        // Has the argument being read?
		
		Arguments(String argString, String... allowedValues)
		{
			this.isSet = false;
			this.argString = argString;
			if (allowedValues.length == 1 && !allowedValues[0].equals("")) {
				// Default value given
				this.defaultValue = allowedValues[0];
				this.allowedValues = new String[] { "" };
			} else {
				this.defaultValue = null;
				this.allowedValues = allowedValues;
			}
		}
		
		public boolean hasAllowedValues() { return allowedValues.length != 0; }
		public void set(String o) { value = o; }
		public boolean getIsSet() { return isSet; }
		public void setIsSet(boolean isSet) { this.isSet = isSet; }
		public boolean hasDefaultValue() { return defaultValue != null; }
		
		public String get()
		throws WrongArgumentsException
		{
			if (!isSet) {
				if (hasDefaultValue())
					return defaultValue;
				else
					throw new WrongArgumentsException("Argument '" + 
							argString + "' expected");
			}
			
			return value;	
		}
		
		public String getDefault()
		{
			if (!hasDefaultValue())
				throw new UnsupportedOperationException("No default value");
			
			return defaultValue;
		}
		
		public int getAsInt()
		throws WrongArgumentsException
		{
			int i = -1;
			
			try {
				i = Integer.parseInt(get());
			} catch(NumberFormatException e) {
				throw new WrongArgumentsException("'" + get() +
				"' is not a number; number expected");
			}
			
			return i;	
		}
		
		public InputStream getAsInputStream()
		throws FileNotFoundException, WrongArgumentsException
		{
			if (get().equals("-"))
				return System.in;
			else
				return new FileInputStream(get());
		}
		
		public OutputStream getAsOutputStream()
		throws FileNotFoundException, WrongArgumentsException
		{
			if (get().equals("-"))
				return System.out;
			
			return new FileOutputStream(get());	
		}
	};
	
	
	static void parse(String[] args)
	throws WrongArgumentsException
	{
		argLoop:
		for (int i = 1; i < args.length; i++) { // args[0] is main action
			for (Arguments arg : Arguments.values())
				if (arg.argString.equals(args[i])) {
					if (arg.getIsSet())
						throw new WrongArgumentsException("Argument '" +
								args[i] + "' given twice");
					arg.setIsSet(true);
					
					if (arg.hasAllowedValues()) {
						if (i == args.length-1)
							throw new WrongArgumentsException("Argument '"
									+ args[i] + "' calls for an option");
						
						String subArg = args[++i];
						if (!arg.allowedValues[0].equals("") &&
								!Arrays.asList(arg.allowedValues).
								contains(subArg))
							throw new WrongArgumentsException("Invalid " +
									"option '" + subArg + "'");
							
						arg.set(subArg);
					}
					
					continue argLoop;
				}
			
			throw new WrongArgumentsException("Unknown argument '" +
					args[i] + "'");
		}
	}
	
	
	/**
	 * Checks the command line arguments.
	 * 
	 * @param args A set of mandatory arguments.  The arguments that directly
	 *      depends on the opdf are automatically added.
	 */
	static void checkArgs(EnumSet<Arguments> args)
	throws WrongArgumentsException
	{
		if (args.contains(Arguments.OPDF)) {
			String opdf = Arguments.OPDF.get();
			
			if (opdf.equals("integer"))
				args.add(Arguments.INTEGER_RANGE);
			else if (opdf.equals("multi_gaussian"))
				args.add(Arguments.VECTOR_DIMENSION);
			else if (opdf.equals("gaussian"))
				;
			else if (opdf.equals("gaussian_mixture"))
				args.add(Arguments.NB_GAUSSIANS);
			else
				new AssertionError("Unknown observation type '" + opdf + "'");
		}
		
		for (Arguments arg : args)
		{
			if (arg == Arguments.DEBUG)
				continue;
			if (!arg.getIsSet() && !arg.hasDefaultValue())
				throw new WrongArgumentsException("Argument '" +
						arg.argString + "' expected");
		}
		
		for (Arguments arg : EnumSet.complementOf(args))
		{
			if (arg == Arguments.DEBUG)
				continue;
			if (arg.getIsSet())
				throw new WrongArgumentsException("Argument '" + 
						arg.argString + "' not expected");
		}
	}
	
	
	static ActionHandler.Actions parseAction(String[] args)
	{
		if (args.length == 0)
			return null;
			
		ActionHandler.Actions mainAction = null;
		for (ActionHandler.Actions action : ActionHandler.Actions.values())
			if (action.toString().equals(args[0]))
				mainAction = action;
		
		return mainAction;
	}
	
	
	static void reset()
	{
		for (Arguments arg : Arguments.values())
			arg.setIsSet(false);
	}
}
