package gui;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

/**
 * A Circle representing the node of a graph with a label containing the node's name
 * @author patrick.bellositz
 */
public class NamedCircle extends Circle{

	private Label nametag; //the label containing the node's name
	
	/**
	 * creates a new named circle object
	 * @param nametag the label containing the name of the node
	 */
	public NamedCircle(Label nametag){
		super();
		this.nametag = nametag;
	}
	
	/**
	 * sets a label as nametag for the circle
	 * @param nametag the label containing the name of the node (circle)
	 */
	public void setNameTag(Label nametag){
		this.nametag = nametag;
	}
	
	/**
	 * @return the label containing the name of the node
	 */
	public Label getNameTag(){
		return nametag;
	}
	
	/**
	 * @return the content of the nametag (label) of the node
	 */
	public String getName(){
		if(nametag != null){
			return nametag.getText();
		}
		else{
			return null;
		}
	}
}
