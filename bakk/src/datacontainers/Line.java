package datacontainers;

public class Line{
	private char name;
	private String description;
	private String attacks;
	private boolean exists;

	public Line(char name, String description, String attacks, boolean exists){
		this.name = String.valueOf(name).toUpperCase().charAt(0);

		this.description = description;
		this.attacks = attacks;
		this.exists = exists;
	}
	
	/**
	 * returns whether the line equals a given line b
	 * @param b another line
	 * @return true/false
	 */
	public boolean equals(Line b){
		if(this.name != b.getChar()){
			return false;
		}
		else if(!this.description.equals(b.getDescription()) && 
				!((this.description.length() == 0 && b.getDescription().equals("no description available")) ||
				this.description.equals("no description available") && b.getDescription().length() == 0)) {
			return false;
		}
		else {
			String thislow = this.attacks.toLowerCase();
			String blow = b.getAttacks().toLowerCase();
			if(!thislow.equals(blow)){
				return false;
			}
		}

		return true;
	}
	
	public char getChar(){
		return name;
	}

	public String getDescription(){
		return description;
	}

	public String getAttacks(){
		return attacks;
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

}
