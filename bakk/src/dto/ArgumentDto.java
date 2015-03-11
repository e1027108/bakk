package dto;

public class ArgumentDto {
	private char name; //name of the argument
	private String statement; //statement describing the argument
	private String attacks; //arguments the argument attacks
	
	/**
	 * creates a Data Transfer Object for arguments
	 * @param name name of the argument
	 * @param statement text or formula describing the argument
	 * @param attacks string of argument names the argument attacks
	 */
	public ArgumentDto(char name, String statement, String attacks){
		this.setName(name);
		this.setStatement(statement);
		this.setAttacks(attacks);
	}

	/**
	 * @return name of Argument
	 */
	public char getName() {
		return name;
	}

	/**
	 * @param name name of argument
	 */
	public void setName(char name) {
		this.name = name;
	}

	/**
	 * @return statement describing the argument
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 * @param statement statement describing the argument
	 */
	public void setStatement(String statement) {
		this.statement = statement;
	}

	/**
	 * @return arguments the argument attacks
	 */
	public String getAttacks() {
		return attacks;
	}

	/**
	 * @param attacks arguments the argument attacks
	 */
	public void setAttacks(String attacks) {
		this.attacks = attacks;
	}
	
	/**
	 * @param attack attack to be added to existing ones
	 */
	public void addAttack(char attack){
		this.attacks.concat("" + attack);
	}
	
	/**
	 * @param attack attack to be removed from attacks
	 */
	public void removeAttack(char attack){
		this.attacks.replace("" + attack, "");
	}
}
