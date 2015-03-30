package logic;

public class Triple<A extends Object> {

	private A first;
	private A second;
	private A third;
	
	public Triple(A a, A b, A c){
		this.setFirst(a);
		this.setSecond(b);
		this.setThird(c);
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public A getSecond() {
		return second;
	}

	public void setSecond(A second) {
		this.second = second;
	}

	public A getThird() {
		return third;
	}

	public void setThird(A third) {
		this.third = third;
	}
	
	public boolean equals(Triple<A> triple){
		if(triple.getFirst().equals(first) && triple.getSecond().equals(second) && triple.getThird().equals(third)){
			return true;
		}
		return false;
	}
}
