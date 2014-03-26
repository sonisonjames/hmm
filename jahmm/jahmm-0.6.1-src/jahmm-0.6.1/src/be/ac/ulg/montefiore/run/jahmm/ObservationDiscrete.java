package be.ac.ulg.montefiore.run.jahmm;

import java.text.NumberFormat;


/**
 * This class implements observations whose values are taken out of a finite
 * set implemented as an enumeration.
 */
public class ObservationDiscrete<E extends Enum<E>>
extends Observation
{
	/**
	 * This observation value.
	 */
	public final E value;
	public final Integer state;
	
	
	public ObservationDiscrete(E value, Integer state)
	{
		this.value = value;
		this.state = state;
	}
	
	
	public String toString()
	{
		return value.toString();
	}
	
	
	public String toString(NumberFormat nf)
	{
		return toString();
	}
	
	public String stateToString()
	{
		return state.toString();
	}
	
	
	public String stateToString(NumberFormat nf)
	{
		return stateToString();
	}
}
