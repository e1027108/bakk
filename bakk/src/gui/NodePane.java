package gui;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.TextAlignment;
import logic.Argument;
import logic.Framework;

public class NodePane extends AnchorPane{
	
	private static final int CIRCLE_RADIUS = 15; // default circle radius
	private static final int ARROW_SIDE_LENGTH = 20;
	private static final int ARROW_POINT_ANGLE = 25;
	private static final EdgeType DIRECTED = EdgeType.DIRECTED;
	private static final int ARC_LENGTH = 270;
	private static final int ARC_ANGLE = 315;
	private Layout<String, String> layout;
	private VisualizationModel<String, String> model;
	private Group viz;
	private Framework framework;
	private DirectedSparseGraph<String, String> graph;

	public NodePane(){
		super();
		
		viz = new Group();
		//setStyle("-fx-background-color: #00ee55");
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
		
		for(Argument a: framework.getArguments()){
			String argumentName = String.valueOf(a.getName());
			String attacks = a.getAttacks();
			
			for(int i = 0; i < attacks.length(); i++){
				String attacked = String.valueOf(attacks.charAt(i)).toUpperCase();
				
				graph.addEdge(argumentName + attacked, new Pair<String>(argumentName, attacked), DIRECTED);
			}
		}
	}
	
	public void drawGraph() {
		if(framework == null || graph == null){
			return; //TODO handle?
		}
		
		int width = (int) Math.ceil(this.getPrefWidth()-20);
		int height = (int) Math.ceil(this.getPrefHeight()-20);
		
		layout = new CircleLayout<String, String>(graph);
		model = new DefaultVisualizationModel<String, String>(layout, new Dimension(width, height));
		
		renderGraph(graph, layout, viz);
		this.getChildren().add(viz);
		//AnchorPane.setLeftAnchor(viz, 10d);
		AnchorPane.setBottomAnchor(viz, 30d);
		AnchorPane.setRightAnchor(viz, 20d);
		//AnchorPane.setRightAnchor(viz, 10d);
	}
	
	private void renderGraph(Graph<String, String> graph, Layout<String, String> layout, Group viz) {
		ArrayList<Label> labellist = new ArrayList<Label>();
		ArrayList<Point2D> nodePositions = new ArrayList<Point2D>(); 
		
		for (String v : graph.getVertices()) {
			// Get the position of the vertex
			Point2D p = (Point2D) layout.transform(v);
			nodePositions.add(p);

			// draw the vertex as a circle
			Circle circle = new Circle();
			circle.setCenterX(p.getX());
			circle.setCenterY(p.getY());
			circle.setRadius(CIRCLE_RADIUS);

			// add it to the group, so it is shown on screen
			this.getChildren().add(circle);

			Label tmp = new Label(v);
			tmp.setTextFill(Color.WHITE);
			tmp.setTextAlignment(TextAlignment.CENTER);
			tmp.setTooltip(new Tooltip(framework.getArgument(v.charAt(0)).getStatement()));
			tmp.setLayoutX(p.getX()-CIRCLE_RADIUS*0.3);
			tmp.setLayoutY(p.getY()-CIRCLE_RADIUS*0.7);
			this.getChildren().add(tmp);
			labellist.add(tmp);
		}

		// draw the edges
		for (String n : graph.getEdges()) {
			// get the end points of the edge
			Pair<String> endpoints = graph.getEndpoints(n);

			// Get the end points as Point2D objects so we can use them in the 
			// builder
			Point2D pStart = (Point2D) layout.transform(endpoints.getFirst());
			Point2D pEnd = (Point2D) layout.transform(endpoints.getSecond());
			
			// Draw the line or arc
			if(endpoints.getFirst() != endpoints.getSecond()){
				drawDirectedEdge(pStart, pEnd);
			}
			else{//TODO if attacks itself make directed arc
				drawDirectedArc(pStart, getPreferredAngle(pEnd, nodePositions));
			}
		}

		for(Label l: labellist){
			l.toFront();
		}
	}

	private double getPreferredAngle(Point2D pEnd, ArrayList<Point2D> nodePositions) {
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

	private void drawDirectedArc(Point2D pStart, double nodeAngle) {
		//TODO make variable according to node position
		double arcradius = CIRCLE_RADIUS*0.8;
		Arc arc = new Arc();
		
		arc.setCenterX(pStart.getX() + CIRCLE_RADIUS*1.4 * modifyXPosition(nodeAngle));
		arc.setCenterY(pStart.getY() + CIRCLE_RADIUS*1.4 * modifyYPosition(nodeAngle));
		arc.setRadiusX(arcradius);
		arc.setRadiusY(arcradius);
		arc.setStartAngle(nodeAngle); //TODO make it so the arc is outside the circlelayout (use nodeAngle)
		arc.setLength(ARC_LENGTH);
		arc.setFill(Color.TRANSPARENT);
		arc.setStroke(Color.BLACK);
		this.getChildren().add(arc);
		
		double diffX = Math.cos(Math.toRadians(nodeAngle))*arcradius;
		double diffY = Math.sin(Math.toRadians(nodeAngle))*arcradius;
		
		int modifier = 0;
		if(arc.getCenterX() != pStart.getX() && arc.getCenterY() != pStart.getY()){
			modifier = -45;
		}
		
		//+60/80� because it should turn inward, 90� is too much
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
		
		this.getChildren().addAll(triangle);
	}

	//make dynamic for 315 angle?
	private double modifyYPosition(double nodeAngle) {
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

	//make dynamic for 315 angle?
	private double modifyXPosition(double nodeAngle) {
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

	private void drawDirectedEdge(Point2D pStart, Point2D pEnd){
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
		this.getChildren().addAll(triangle);

		// add the edges to the screen
		this.getChildren().add(line);
	}
	
	public Framework getFramework(){
		return framework;
	}
}