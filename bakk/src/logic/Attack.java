package logic;

public class Attack {
	private Argument attacker;
	private Argument attacked;
	
	public Attack(Argument attacker, Argument argument) {
		this.attacker = attacker;
		this.attacked = argument;
	}
	
	public Argument getAttacker(){
		return attacker;
	}

	public Argument getAttacked(){
		return attacked;
	}
}
