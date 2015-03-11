package logic;

public class Argument {
	private char name;
	private String statement;
	private String attacks;
	
	public Argument(char name, String statement, String attacks){
		this.setName(name);
		this.setStatement(statement);
		this.setAttacks(attacks);
	}

	public String getAttacks() {
		return attacks;
	}

	public void setAttacks(String attacks) {
		this.attacks = attacks;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public char getName() {
		return name;
	}

	public void setName(char name) {
		this.name = name;
	}
}