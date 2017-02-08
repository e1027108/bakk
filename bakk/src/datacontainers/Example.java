package datacontainers;

public class Example{
	
	private Line[] lines;
	private String name;
	
	public Example (String name, Line[] lines){
		this.lines = lines;
		this.name = name;
	}
	
	/**
	 * this function determines if a given example equals this example
	 * regardless of name of example
	 * @param b the given other example
	 * @return equality between this and b
	 */
	public boolean equals(Example b){
		if(this.lines == null && null == b.getLines()){
			return true;
		}
		else if(this.lines == null || null == b.getLines()){
			return false;
		}
		else if(this.lines.length != b.getLines().length){
			return false;
		}
		else{
			for(int i = 0;i<this.lines.length;i++){
				if(!this.lines[i].equals(b.getLines()[i])){
					return false;
				}
			}
		}
		
		return true;
	}
	
	public Line[] getLines(){
		return lines;
	}
	
	public void setLines(Line[] lines){
		this.lines = lines;
	}
	
	public String getName(){
		return name;
	}
}
