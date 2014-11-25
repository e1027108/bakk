package dto;

public class ArgumentDto {
	private char name;
	private String statement;
	private String attacks;
	
	public ArgumentDto(char name, String statement, String attacks){
		this.setName(name);
		this.setStatement(statement);
		this.setAttacks(attacks);
	}

	public char getName() {
		return name;
	}

	public void setName(char name) {
		this.name = name;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public String getAttacks() {
		return attacks;
	}

	public void setAttacks(String attacks) {
		this.attacks = attacks;
	}
	
	public void addAttack(char attack){
		this.attacks.concat("" + attack);
	}
	
	public void removeAttack(char attack){
		this.attacks.replace("" + attack, "");
	}
}
