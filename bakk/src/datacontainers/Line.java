package datacontainers;

public class Line{
	private int number;
	private String description;
	private String attacks;

	public Line(int number, String description, String attacks){
		this.number = number;
		this.description = description;
		this.attacks = attacks;
	}

	public Line(char name, String description, String attacks){
		this.number = String.valueOf(name).toUpperCase().charAt(0) - 65; //TODO check if it works

		this.description = description;
		this.attacks = attacks;
	}

	public int getNumber(){
		return number;
	}

	public String getDescription(){
		return description;
	}

	public String getAttacks(){
		return attacks;
	}
}
