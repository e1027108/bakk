package gui;

import javafx.scene.shape.Arc;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;

public class DirectedEdge {
	
	private Arc arc;
	private Polygon triangle;
	private Line line;
	private String direction;

	public DirectedEdge(Arc arc, Polygon triangle, String direction){
		this.arc = arc;
		this.line = null;
		this.triangle = triangle;
		this.direction = direction;
	}
	
	public DirectedEdge(Line line, Polygon triangle, String direction){
		this.line = line;
		this.arc = null;
		this.triangle = triangle;
		this.direction = direction;
	}
	
	public Arc getArc(){
		return arc;
	}
	
	public Line getLine(){
		return line;
	}
	
	public Polygon getTriangle(){
		return triangle;
	}
	
	public String getDirection(){
		return direction;
	}
	
	public boolean hasArc(){
		if(arc != null){
			return true;
		}
		return false;
	}
	
	public boolean hasLine(){
		if(line != null){
			return true;
		}
		return false;
	}
}
