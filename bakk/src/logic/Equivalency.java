package logic;

public class Equivalency {
	private Framework fst, snd; //first and second frameworks (for comparison)
	
	public Equivalency(){} //we want to manually add frameworks for this

	public void addFrameworks(Framework fst, Framework snd) {
		this.fst = fst;
		this.snd = snd;
	}
}
