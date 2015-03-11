package logic;

/**
 * The Argument class describes an argument in an abstract argumentation framework
 * @author Patrick Bellositz
 */
public class Argument {
	private char name; //name of the argument
	private String statement; //statement describing the argument
	private String attacks; //arguments the argument attacks
	
	/**
	 * creates a new argument for argument frameworks
	 * @param name name of the argument
	 * @param statement statement describing the argument
	 * @param attacks arguments the argument attacks
	 */
	public Argument(char name, String statement, String attacks){
		this.setName(name);
		this.setStatement(statement);
		this.setAttacks(attacks);
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
	 * @return name of the argument
	 */
	public char getName() {
		return name;
	}

	/**
	 * @param name name of the argument
	 */
	public void setName(char name) {
		this.name = name;
	}
}