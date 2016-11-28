package datacontainers;

import java.util.ArrayList;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class TextData {

	private String name;
	private ArrayList<?> data[];
	private ArrayList<Boolean> checkBoxes;
	private ArrayList<String> statements, attacks;
	
	public TextData(String name, ArrayList<CheckBox> checkBoxes, ArrayList<TextField> statements, ArrayList<TextField> attacks){
		this.setName(name);
		data = new ArrayList<?>[3];
		
		this.checkBoxes = new ArrayList<Boolean>();
		this.statements = new ArrayList<String>();
		this.attacks = new ArrayList<String>();
		
		for(CheckBox c: checkBoxes){
			this.checkBoxes.add(c.isSelected());
		}
		for(TextField t: statements){
			this.statements.add(t.getText());
		}
		for(TextField t: attacks){
			this.attacks.add(t.getText());
		}
		
		data[0] = this.checkBoxes;
		data[1] = this.statements;
		data[2] = this.attacks;
	}

	public ArrayList<?>[] getData() {
		return data;
	}

	public void setData(ArrayList<?>[] data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
