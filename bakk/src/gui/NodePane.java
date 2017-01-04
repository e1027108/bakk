package gui;

import interactor.GraphInstruction;
import interactor.SingleInstruction;
import interactor.SingleInstruction.Type;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import datacontainers.DirectedEdge;
import datacontainers.NamedCircle;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import exceptions.InvalidInputException;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.TextAlignment;
import logic.Argument;
import logic.Attack;
import logic.Framework;

public class NodePane extends AnchorPane{

	/** * default circle radius */
	protected static final int CIRCLE_RADIUS = 15;
	/** * default length of the two long sides of the arrow */
	protected static final int ARROW_SIDE_LENGTH = 20;
	/** * default angle between the two long sides of the arrow */
	protected static final int ARROW_POINT_ANGLE = 25;
	/** * type of the edges in the graph drawn */
	protected static final EdgeType DIRECTED = EdgeType.DIRECTED;
	/** * default length of the arc in degrees */
	protected static final int ARC_LENGTH = 270;
	/** * default angle for the start of the arc in degrees */
	protected static final int ARC_ANGLE = 315;
	protected Layout<String, String> layout; //layout in which the nodes are arranged
	protected Group viz; //parent element for the nodes, lines and labels
	protected Framework framework; //argument framework containing the nodes' data
	protected DirectedSparseGraph<String, String> graph; //data representation of the graph drawn
	protected ArrayList<NamedCircle> nodes; //list of named circles representing the nodes of the graph
	protected ArrayList<DirectedEdge> edges; //list of directed edges representing the edges of the graph

	/**
	 * creates a new nodepane to show a graph
	 */
	public NodePane(){
		super();
		viz = new Group();
	}

	/**
	 * saves the Framework given and computes the graph
	 * @param argumentFramework the framework containing arguments and attacks that are the basis for the graph to be computed
	 */
	public void createGraph(Framework argumentFramework) {
		this.framework = argumentFramework;
		graph = new DirectedSparseGraph<String, String>();

		for(Argument a: framework.getArguments()){
			graph.addVertex(String.valueOf(a.getName()));
		}
		
		for(Attack att: framework.getAttacks()){
			String attacked = String.valueOf(att.getAttacked().getName()).toUpperCase();
			String attacker = String.valueOf(att.getAttacker().getName()).toUpperCase();

			graph.addEdge(attacker + attacked, new Pair<String>(attacker, attacked), DIRECTED);
		}
	}

	/**
	 * defines where the graph is to be drawn, assigns layout and parent objects,
	 * initiates the drawing of the graph and rearranges children so they don't overlap
	 * @throws InvalidInputException throws exception if there is a problem with edge creation
	 */
	public void drawGraph() throws InvalidInputException {
		if(framework == null || graph == null){
			return;
		}

		int width = (int) Math.ceil(this.getPrefWidth()-15);
		int height = (int) Math.ceil(this.getPrefHeight());

		layout = new CircleLayout<String, String>(graph);
		new DefaultVisualizationModel<String, String>(layout, new Dimension(width, height));
		
		renderGraph(graph, layout, viz);

		arrangePositions();

		this.getChildren().add(viz);
	}

	/**
	 * moves nodes, nametags and arrows of the graph to the front
	 * of the pane, so lines are not in front
	 */
	protected void arrangePositions() {
		for(NamedCircle c: nodes){
			c.toFront();
			c.getNameTag().toFront();
		}
		for(DirectedEdge e: edges){
			e.getTriangle().toFront();
		}
	}

	/**
	 * creates a graphical representation for each edge and vertice of the graph
	 * and initiates their being drawn and positions them so the graph is visualized properly
	 * @param graph the data source for the graph
	 * @param layout the layout for the graphical representation
	 * @param viz the parent object for the graphs' elements
	 * @throws InvalidInputException throws exception if there is a problem with edge creation
	 */
	protected void renderGraph(Graph<String, String> graph, Layout<String, String> layout, Group viz) throws InvalidInputException {
		ArrayList<Point2D> nodePositions = new ArrayList<Point2D>(); 
		nodes = new ArrayList<NamedCircle>();
		edges = new ArrayList<DirectedEdge>();

		boolean useLayout = true;

		if(graph.getVertices().size() <= 1){
			useLayout = false;
		}

		for (String v : graph.getVertices()) {
			// Get the position of the vertex
			Point2D p;

			if(useLayout){
				p = (Point2D) layout.transform(v);
			}
			else{
				p = new Point2D.Double(this.getPrefWidth()/2, this.getPrefHeight()/2);
			}

			nodePositions.add(p);

			// draw the vertex as a circle
			NamedCircle circle = new NamedCircle(new Label(v));
			circle.setCenterX(p.getX());
			circle.setCenterY(p.getY());
			circle.setRadius(CIRCLE_RADIUS);

			// add it to the group, so it is shown on screen
			this.getChildren().add(circle);

			Label tmp = circle.getNameTag();
			tmp.setTextFill(Color.WHITE);
			tmp.setTextAlignment(TextAlignment.CENTER);
			tmp.setTooltip(new Tooltip(framework.getArgument(v.charAt(0)).getStatement()));
			tmp.setLayoutX(p.getX()-CIRCLE_RADIUS*0.3);
			tmp.setLayoutY(p.getY()-CIRCLE_RADIUS*0.65);
			this.getChildren().add(tmp);

			nodes.add(circle);
		}

		// draw the edges
		for (String direction : graph.getEdges()) {
			// get the end points of the edge
			Pair<String> endpoints = graph.getEndpoints(direction);

			// Get the end points as Point2D objects so we can use them in the builder
			Point2D pStart, pEnd;

			if(useLayout){
				pStart = (Point2D) layout.transform(endpoints.getFirst());
				pEnd = (Point2D) layout.transform(endpoints.getSecond());
			}
			else{
				pStart = pEnd = new Point2D.Double(this.getPrefWidth()/2, this.getPrefHeight()/2);
			}

			// Draw the line or arc
			if(pStart.getX() != pEnd.getX() || pStart.getY() != pEnd.getY()){
				drawDirectedEdge(pStart, pEnd, direction);
			}
			else{
				drawDirectedArc(pStart, getPreferredAngle(pEnd, nodePositions), direction);
			}
		}
	}

	/**
	 * computes the angle at which an arc (an edge from a node to itself) is to be placed so it
	 * lies outside the circlelayout and doesn't intersect with straight line edges
	 * @param pEnd the end position of the arc
	 * @param nodePositions a list of positions for all nodes to determine the angle
	 * 		at which the arc is to be placed
	 * @return the computed preferred angle at which the arc is to be placed
	 */
	protected double getPreferredAngle(Point2D pEnd, ArrayList<Point2D> nodePositions) {
		ArrayList<Point2D> tmpPositions = new ArrayList<Point2D>();
		tmpPositions.addAll(nodePositions);

		if(tmpPositions.contains(pEnd)){
			tmpPositions.remove(pEnd);
		}

		int above, below, left, right, angle;
		above = below = left = right = angle = 0;

		int pX, pEX, pY, pEY;
		pEX = (int) Math.ceil(pEnd.getX());
		pEY = (int) Math.ceil(pEnd.getY());

		for(Point2D p: tmpPositions){
			pX = (int) Math.ceil(p.getX());
			pY = (int) Math.ceil(p.getY());

			if(pX > pEX){
				right++;
			}
			else if(pX < pEX){
				left++;
			}
			else{
				right++;
				left++;
			}

			if(pY > pEY){
				above++;
			}
			else if(pY < pEY){
				below++;
			}
			else{
				above++;
				below++;
			}
		}

		int size = tmpPositions.size();

		if(above == size){
			angle = ARC_ANGLE;
		}
		else if(below == size){
			angle = ARC_ANGLE - 180;
		}
		else if(right == size){
			angle = ARC_ANGLE + 90;
		}
		else if(left == size){
			angle = ARC_ANGLE - 90;
		}
		else if(above > below){
			if(left > right){
				angle = ARC_ANGLE - 45;
			}
			else{
				angle = ARC_ANGLE + 45;
			}
		}
		else if(below > above){
			if(left > right){
				angle = ARC_ANGLE - 135;
			}
			else{
				angle = ARC_ANGLE + 135;
			}
		}

		return angle%360;
	}

	/**
	 * draws an arc from a node to itself at an appropriately computed angle
	 * @param pStart the starting point of the arc edge
	 * @param nodeAngle the angle at which the arc is to be placed
	 * @param direction the direction of the edge (should be a string repeating a character twice)
	 * @throws InvalidInputException throws exception if there is a problem with edge creation
	 */
	protected void drawDirectedArc(Point2D pStart, double nodeAngle, String direction) throws InvalidInputException {
		double arcradius = CIRCLE_RADIUS*0.8;
		Arc arc = new Arc();

		arc.setCenterX(pStart.getX() + CIRCLE_RADIUS*1.4 * modifyXPosition(nodeAngle));
		arc.setCenterY(pStart.getY() + CIRCLE_RADIUS*1.4 * modifyYPosition(nodeAngle));

		arc.setRadiusX(arcradius);
		arc.setRadiusY(arcradius);
		arc.setStartAngle(nodeAngle);
		arc.setLength(ARC_LENGTH);
		arc.setFill(Color.TRANSPARENT);
		arc.setStroke(Color.BLACK);

		double diffX = Math.cos(Math.toRadians(nodeAngle))*arcradius;
		double diffY = Math.sin(Math.toRadians(nodeAngle))*arcradius;

		int modifier = 0;
		if(arc.getCenterX() != pStart.getX() && arc.getCenterY() != pStart.getY()){
			modifier = -45;
		}

		//+60/80° because it should turn inward, 90° is too much
		double rightX = Math.sin(Math.toRadians(nodeAngle - ARROW_POINT_ANGLE/2 + 60 + modifier)) * ARROW_SIDE_LENGTH/2;
		double rightY = Math.cos(Math.toRadians(nodeAngle - ARROW_POINT_ANGLE/2 + 60 + modifier)) * ARROW_SIDE_LENGTH/2;
		double leftX = Math.sin(Math.toRadians(nodeAngle + ARROW_POINT_ANGLE/2 + 80 + modifier)) * ARROW_SIDE_LENGTH/2;
		double leftY = Math.cos(Math.toRadians(nodeAngle + ARROW_POINT_ANGLE/2 + 80 + modifier)) * ARROW_SIDE_LENGTH/2;

		if(arc.getCenterY() != pStart.getY()){
			diffY *= -1;
			leftY *= -1;
			rightY *= -1;
		}
		if(arc.getCenterY() == pStart.getY()){
			diffY *= -1;
		}
		if(arc.getCenterX() != pStart.getX()){
			leftX *= -1;
			rightX *= -1;
		}

		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(new Double[]{
				arc.getCenterX() + diffX, arc.getCenterY() + diffY,
				arc.getCenterX() + diffX + rightX, arc.getCenterY() + diffY + rightY,
				arc.getCenterX() + diffX + leftX, arc.getCenterY() + diffY + leftY,
		});

		edges.add(new DirectedEdge(arc, triangle, direction));
		this.getChildren().addAll(arc, triangle);
	}

	/**
	 * returns the modifier for the y coordinate for the arc depending on the given angle
	 * @param nodeAngle angle at which the arc is to be placed from the corresponding node
	 * @return the computed y coordinate modifiers
	 */
	protected double modifyYPosition(double nodeAngle) {
		double value = 1;

		if(nodeAngle%90 == 0){
			value = 0.75;
		}

		if(nodeAngle >= 270 || nodeAngle == 0){
			value *= -1;
		}
		else if(nodeAngle%180 == 45){
			value = 0;
		}

		return value;
	}

	/**
	 * returns the modifier for the x coordinate for the arc depending on the given angle
	 * @param nodeAngle angle at which the arc is to be placed from the corresponding node
	 * @return the computed x coordinate modifiers
	 */
	protected double modifyXPosition(double nodeAngle) {
		double value = 1;

		if(nodeAngle%90 == 0){
			value = 0.75;
		}

		if(nodeAngle <= 90){
			value *= -1;
		}
		else if(nodeAngle%180 == 135){
			value = 0;
		}

		return value;
	}

	/**
	 * draws a line representing the edge of the graph
	 * @param pStart start position of the line
	 * @param pEnd end position of the line
	 * @param direction direction containing from which to which other node the line is to be drawn
	 * @throws InvalidInputException throws exception if there is a problem with edge creation
	 */
	protected void drawDirectedEdge(Point2D pStart, Point2D pEnd, String direction) throws InvalidInputException{
		//draw line
		Line line = new Line();
		line.setStartX(pStart.getX());
		line.setStartY(pStart.getY());
		line.setEndX(pEnd.getX());
		line.setEndY(pEnd.getY());

		//compute arrow position
		double adjacent = Math.abs(pStart.getY() - pEnd.getY());
		double opposite = Math.abs(pStart.getX() - pEnd.getX());
		double hypotenuse = Math.abs(Math.sqrt(Math.pow(adjacent, 2) + Math.pow(opposite, 2)));
		double angle = Math.asin(opposite / hypotenuse); //radians
		double diffX = Math.sin(angle) * CIRCLE_RADIUS;
		double diffY = Math.cos(angle) * CIRCLE_RADIUS;
		double rightX = Math.sin(angle - Math.toRadians(ARROW_POINT_ANGLE/2)) * ARROW_SIDE_LENGTH;
		double rightY = Math.cos(angle - Math.toRadians(ARROW_POINT_ANGLE/2)) * ARROW_SIDE_LENGTH;
		double leftX = Math.sin(angle + Math.toRadians(ARROW_POINT_ANGLE/2)) * ARROW_SIDE_LENGTH;
		double leftY = Math.cos(angle + Math.toRadians(ARROW_POINT_ANGLE/2)) * ARROW_SIDE_LENGTH;

		//adjust for different line angles
		if(pStart.getX() <= pEnd.getX()){
			diffX *= -1;
			leftX *= -1;
			rightX *= -1;
		}

		if(pStart.getY() <= pEnd.getY()){
			diffY *= -1;
			leftY *= -1;
			rightY *= -1;
		}

		//draw arrow
		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(new Double[]{
				pEnd.getX() + diffX, pEnd.getY() + diffY,
				pEnd.getX() + diffX + rightX, pEnd.getY() + diffY + rightY,
				pEnd.getX() + diffX + leftX, pEnd.getY() + diffY + leftY
		});

		edges.add(new DirectedEdge(line, triangle, direction));
		// add the edge to the screen
		this.getChildren().addAll(line, triangle);
	}

	/**
	 * modifies the color of lines and and nodes according to the given instruction
	 * @param instruction specifying the the colors for every edge and node to be changed
	 * @throws InvalidInputException if there is a faulty Instruction (false InstructionType)
	 */
	public void executeInstruction(GraphInstruction instruction) throws InvalidInputException{
		resetColors();

		if(instruction == null){
			return;
		}

		ArrayList<SingleInstruction> nodeInstructions = instruction.getNodeInstructions();
		ArrayList<SingleInstruction> edgeInstructions = instruction.getEdgeInstructions();		

		if(nodeInstructions != null){
			for(SingleInstruction i: nodeInstructions){
				if(i.getType() != Type.NODE){
					throw new InvalidInputException("Instruction (name: " + i.getName() + ") is not a node instruction.");
				}
				
				NamedCircle tmp = getCircleByName(i.getName());

				if(tmp != null){
					tmp.setFill(i.getColor());
				}
			}
		}

		if(edgeInstructions != null){
			for(SingleInstruction i: edgeInstructions){
				if(i.getType() != Type.EDGE){
					throw new InvalidInputException("Instruction (name: " + i.getName() + ") is not an edge instruction.");
				}
				
				DirectedEdge tmp = getEdgeByName(i.getName()); //name of edge = direction

				if(tmp != null){
					if(tmp.hasArc()){
						tmp.getArc().setStroke(i.getColor());
					}
					else if(tmp.hasLine()){
						tmp.getLine().setStroke(i.getColor());

						DirectedEdge reverse = getEdgeByName(""+i.getName().charAt(1)+i.getName().charAt(0));

						if(reverse != null){
							reverse.getLine().setStroke(Color.TRANSPARENT);
						}
					}
					tmp.getTriangle().setFill(i.getColor());
				}
			}
		}
	}

	/**
	 * finds an edge by given name
	 * @param direction the names of the edges
	 * @return the edge that was found
	 */
	protected DirectedEdge getEdgeByName(String direction) {
		for(DirectedEdge e: edges){
			String tmp = e.getDirection();
			if(tmp != null && tmp.equals(direction)){
				return e;
			}
		}
		return null;
	}

	/**
	 * finds a node by given name
	 * @param name the name of the searched for node
	 * @return the node that was found
	 */
	protected NamedCircle getCircleByName(String name) {
		for(NamedCircle n: nodes){
			String tmp = n.getName();
			if(tmp != null && tmp.equals(name)){
				return n;
			}
		}
		return null;
	}

	/**
	 * resets all nodes' and edges' colors to black,
	 * also sets label colors to white
	 */
	protected void resetColors() {
		for(NamedCircle n: nodes){
			n.setFill(Color.BLACK);
			n.getNameTag().setTextFill(Color.WHITE);
		}
		for(DirectedEdge e: edges){
			if(e.hasArc()){
				e.getArc().setStroke(Color.BLACK);
			}
			else if(e.hasLine()){
				e.getLine().setStroke(Color.BLACK);
			}
			e.getTriangle().setFill(Color.BLACK);
		}
	}
}
