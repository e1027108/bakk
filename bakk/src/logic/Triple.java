package logic;

/**
 * A 3-tuple of a generic type
 * @author Patrick Bellositz
 * @param <A> the generic type of the triple
 */
public class Triple<A extends Object> {

	private A first; //first element of the triple
	private A second; //second element of the triple
	private A third; //third element of the triple
	
	/**
	 * creates a 3-tuple of the same generic type
	 * @param a first triple element
	 * @param b second triple element
	 * @param c third triple element
	 */
	public Triple(A a, A b, A c){
		this.setFirst(a);
		this.setSecond(b);
		this.setThird(c);
	}

	/**
	 * @return the first element of the triple
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * set the given element as new first triple element
	 * @param first the new first triple element
	 */
	public void setFirst(A first) {
		this.first = first;
	}

	/**
	 * @return the second element of the triple
	 */
	public A getSecond() {
		return second;
	}

	/**
	 * set the given element as new second triple element
	 * @param first the new second triple element
	 */
	public void setSecond(A second) {
		this.second = second;
	}

	/**
	 * @return the third element of the triple
	 */
	public A getThird() {
		return third;
	}

	/**
	 * set the given element as new third triple element
	 * @param first the new third triple element
	 */
	public void setThird(A third) {
		this.third = third;
	}
	
	/**
	 * compares every element of the triple to the corresponding element of the given triple
	 * @param triple a comparable triple
	 * @return whether first, second, and third triple elements are the same in both triples
	 */
	public boolean equals(Triple<A> triple){
		if(triple.getFirst().equals(first) && triple.getSecond().equals(second) && triple.getThird().equals(third)){
			return true;
		}
		return false;
	}
}
