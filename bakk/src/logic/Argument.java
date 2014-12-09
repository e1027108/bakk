package logic;

import java.util.ArrayList;

public class Argument {
	private char name;
	private String statement;
	private ArrayList<Argument> attacks;
	
	public Argument(char name, String statement, ArrayList<Argument> attacks){
		this.setName(name);
		this.setStatement(statement);
		this.setAttacks(attacks);
	}

	public ArrayList<Argument> getAttacks() {
		return attacks;
	}

	public void setAttacks(ArrayList<Argument> attacks) {
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
