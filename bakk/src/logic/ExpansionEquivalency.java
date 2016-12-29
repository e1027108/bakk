package logic;

public class ExpansionEquivalency extends Equivalency {

	private Framework exp;
	
	public ExpansionEquivalency(Framework fst, Framework snd, Framework exp) {
		super(fst,snd);
		this.exp = exp;
	}
	
	//we might want to change the "test"
	private void setExpansion(Framework exp){
		this.exp = exp;
	}
	
	//TODO implement expansion computations

}
