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

	/**
	 * this method compares the attackers and defenders of the attack to the given attack
	 * @param b the attack we compare this attack to
	 * @return whether the attacks are equal
	 */
	@Override
	public boolean equals(Object b){
		if(b instanceof Attack){
			if((attacker.equals(((Attack) b).getAttacker())) && (attacked.equals(((Attack) b).getAttacked()))){
				return true;
			}
		}
		return false;
	}
}
