package gui;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

public class NamedCircle extends Circle{

	private Label nametag;
	
	public NamedCircle(Label nametag){
		super();
		this.nametag = nametag;
	}
	
	public void setNameTag(Label nametag){
		this.nametag = nametag;
	}
	
	public Label getNameTag(){
		return nametag;
	}
	
	public String getName(){
		if(nametag != null){
			return nametag.getText();
		}
		else{
			return null;
		}
	}
}
