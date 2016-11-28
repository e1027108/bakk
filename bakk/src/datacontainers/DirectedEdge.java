package datacontainers;

import exceptions.InvalidInputException;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;

/**
 * DirectedEdge represents a directed edge in a mathematical graph
 * it consists of a line and a triangle (the arrow of the edge)
 * the line can be an arc pointing at the node it originates from
 * @author patrick.bellositz
 */
public class DirectedEdge {
	
	private Arc arc; //an arc pointing to the node it originates from
	private Polygon triangle; //the arrow of the edge indicating the edge's direction
	private Line line; //the line that graphically represents the edge
	private String direction; //the name of the edge
	
	/**
	 * Constructor of a directed edge pointing to the edge it originates from
	 * @param arc the self-pointing arc
	 * @param triangle the arrow/point of the arc
	 * @param direction String representing the the arc's direction
	 * @throws InvalidInputException if directionString is not 2 characters long
	 */
	public DirectedEdge(Arc arc, Polygon triangle, String direction) throws InvalidInputException{
		this.arc = arc;
		this.line = null;
		this.triangle = triangle;
		setDirection(direction);
	}

	/**
	 * Constructor of a directed edge
	 * @param arc the line representing the edge
	 * @param triangle the arrow/point of the edge
	 * @param direction String representing the the line's direction
	 * @throws InvalidInputException if directionString is not 2 characters long
	 */
	public DirectedEdge(Line line, Polygon triangle, String direction) throws InvalidInputException{
		this.line = line;
		this.arc = null;
		this.triangle = triangle;
		setDirection(direction);
	}
	
	/**
	 * sets the given String as direction if it's 2 characters long
	 * @param direction the new direction
	 * @throws InvalidInputException throws Exception if input is not 2 characters long
	 */
	private void setDirection(String direction) throws InvalidInputException {
		if(direction.length() != 2){
			throw new InvalidInputException("A directed edge needs a direction String of exactly 2 characters! (" + direction.length() + ")");
		}
		this.direction = direction;
	}
	
	/**
	 * @return the arc (self-pointing edge)
	 */
	public Arc getArc(){
		return arc;
	}
	
	/**
	 * @return the line pointing from one node to another
	 */
	public Line getLine(){
		return line;
	}
	
	/**
	 * @return the triangle (arrow) of an edge
	 */
	public Polygon getTriangle(){
		return triangle;
	}
	
	/**
	 * @return the string representing the edge's direction
	 */
	public String getDirection(){
		return direction;
	}
	
	/**
	 * @return if the edge is an arc (therefore not a line)
	 */
	public boolean hasArc(){
		if(arc != null){
			return true;
		}
		return false;
	}
	
	/**
	 * @return if the edge is a line (therefore not an arc)
	 */
	public boolean hasLine(){
		if(line != null){
			return true;
		}
		return false;
	}
}
