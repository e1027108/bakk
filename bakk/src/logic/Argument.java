package logic;

public class Argument {
	private String statement; //statement describing the argument
	private char name; //arguments the argument attacks
	
	public Argument (char name, String statement){
		this.setName(name);
		this.setStatement(statement);
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
