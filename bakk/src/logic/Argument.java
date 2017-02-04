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
	
	/**
	 * checks whether another given argument has the same name
	 * so we say it is equal
	 * @param b the argument we want to compare to this argument
	 * @return whether the arguments are "equal"
	 */
	public boolean equals(Argument b){
		if(b.getName() == this.name){
			return true;
		}
		return false;
	}
}
