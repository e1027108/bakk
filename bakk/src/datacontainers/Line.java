package datacontainers;

public class Line{
	private int number; //TODO change to char, otherwise equality is useless bc of touppercase
	private String description;
	private String attacks;
	
	public Line(char name, String description, String attacks){
		this.number = String.valueOf(name).toUpperCase().charAt(0) - 65;

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
	
	/**
	 * returns whether the line equals a given line b
	 * note that even tiny differences (e.g. attack order) makes two lines unequal
	 * @param b another line
	 * @return true/false
	 */
	public boolean equals(Line b){
		if(this.number != b.getNumber()){
			return false;
		}
		else if(!this.description.equals(b.getDescription())){
			return false;
		}
		else if(!this.attacks.equals(b.getAttacks())){
			return false;
		}
		
		return true;
	}
}
