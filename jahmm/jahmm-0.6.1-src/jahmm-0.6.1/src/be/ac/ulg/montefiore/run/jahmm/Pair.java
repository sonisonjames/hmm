/**
 * 
 */
package be.ac.ulg.montefiore.run.jahmm;

/**
 * @author sjames
 *
 */
public class Pair<F, S> {
	public final F first;
	public final S second;

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) {
			return false;
		}
		Pair<?, ?> p = (Pair<? ,?>)o;
		return p.first.equals(first) && p.second.equals(second);
	}
	
	@Override
	public int hashCode() {
		return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
	}
	
	public static <X, Y> Pair<X, Y> makePair(X first, Y second) {
		return new Pair<X, Y>(first, second);
	}

}
