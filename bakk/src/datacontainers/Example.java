package datacontainers;

public class Example {
	
	private Line[] lines;
	private String name;
	
	public Example (String name, Line[] lines){
		this.lines = lines;
		this.name = name;
	}
	
	public Line[] getLines(){
		return lines;
	}
	
	public String getName(){
		return name;
	}
}
