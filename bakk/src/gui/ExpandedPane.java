package gui;

import java.awt.Dimension;
import java.util.ArrayList;

import datacontainers.DirectedEdge;
import datacontainers.NamedCircle;
import edu.uci.ics.jung.graph.util.Pair;
import exceptions.InvalidInputException;
import logic.Argument;
import logic.Attack;
import logic.Framework;

public class ExpandedPane extends NodePane{
	private Framework expansionFramework;
	private ArrayList<NamedCircle> expansionNodes;
	private ArrayList<DirectedEdge> expansionEdges;
	
	public ExpandedPane() {
		super();
	}
	
	// TODO call this on drawGraph method in NodePane to have control over colors?
	public void expand(Framework expansionFramework) throws InvalidInputException{
		if(graph == null){
			throw new InvalidInputException("Can't expand if there is no framework!");
		}
		
		this.expansionFramework = expansionFramework;

		for(Argument a: expansionFramework.getArguments()){
			//TODO check if it creates duplicate vertices
			graph.addVertex(String.valueOf(a.getName()));
		}
		
		for(Attack att: expansionFramework.getAttacks()){
			//TODO check if it creates duplicate edges
			String attacked = String.valueOf(att.getAttacked().getName()).toUpperCase();
			String attacker = String.valueOf(att.getAttacker().getName()).toUpperCase();

			graph.addEdge(attacker + attacked, new Pair<String>(attacker, attacked), DIRECTED);
		}
	}
	
	public void renderExpansion(){
		//TODO
	}

}
