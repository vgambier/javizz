/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.explorer.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openflexo.fge.geom.FGEPoint;

public class Node {

	private String name;
	private final Graph graph;
	private List<Edge> inputEdges;
	private List<Edge> outputEdges;
	private double x, y;

	private static final double CENTER_X = 300;
	private static final double CENTER_Y = 300;
	private static final double RADIUS = 100;

	public Node(String name, Graph graph) {
		inputEdges = new ArrayList<>();
		outputEdges = new ArrayList<>();
		this.name = name;
		this.graph = graph;
		x = Math.random() * 500;
		y = Math.random() * 500;
		graph.addToNodes(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		System.out.println("Set node name with " + name);
		this.name = name;
	}

	public Graph getGraph() {
		return graph;
	}

	public List<Edge> getInputEdges() {
		return inputEdges;
	}

	public void setInputEdges(List<Edge> inputEdges) {
		this.inputEdges = inputEdges;
	}

	public void addToInputEdges(Edge aNode) {
		inputEdges.add(aNode);
	}

	public void removeFromInputEdges(Edge aNode) {
		inputEdges.remove(aNode);
	}

	public List<Edge> getOutputEdges() {
		return outputEdges;
	}

	public void setOutputEdges(List<Edge> outputEdges) {
		this.outputEdges = outputEdges;
	}

	public void addToOutputEdges(Edge aNode) {
		outputEdges.add(aNode);
	}

	public void removeFromOutputEdges(Edge aNode) {
		outputEdges.remove(aNode);
	}

	public void connectTo(Node toNode) {
		new Edge(this, toNode);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	private Double teta = null;
	private double parentAngle;
	private double startAngle;

	private boolean isUpToDate = false;

	public double getParentAngle() {
		if (!isUpToDate) {
			updateParentAngle();
		}
		return parentAngle;
	}

	public double getStartAngle() {
		if (!isUpToDate) {
			updateParentAngle();
		}
		return startAngle;
	}

	public double getTeta() {
		if (teta == null) {
			List<Node> siblings = getSiblingNodes();
			int index = siblings.indexOf(this);
			teta = getStartAngle() + getParentAngle() / siblings.size() * index;
		}
		return teta;
	}

	private void updateParentAngle() {
		if (getInputEdges().size() == 0) {
			parentAngle = 2 * Math.PI;
			startAngle = 0;
		}
		else {
			Node parentNode = getInputEdges().get(0).getStartNode();
			List<Node> parentSiblings = parentNode.getSiblingNodes();
			int parentIndex = parentSiblings.indexOf(parentNode);
			parentAngle = 2 * Math.PI / (parentSiblings.size());
			startAngle = 2 * Math.PI * parentIndex / parentSiblings.size();// - parentAngle / 2;
		}
		isUpToDate = true;
	}

	public double getCircularX() {
		return CENTER_X + (getDepth() * RADIUS) * Math.cos(getTeta());
	}

	public double getCircularY() {
		return CENTER_Y + (getDepth() * RADIUS) * Math.sin(getTeta());
	}

	public void setCircularX(double circX) {
		if (teta != null) {
			updateTetaWithNewPosition(new FGEPoint(circX, getCircularY()));
		}
	}

	public void setCircularY(double circY) {
		if (teta != null) {
			updateTetaWithNewPosition(new FGEPoint(getCircularX(), circY));
		}
	}

	private void updateTetaWithNewPosition(FGEPoint newPosition) {
		// System.out.println("teta was = " + getTeta());
		teta = Math.atan2(newPosition.y - CENTER_Y, newPosition.x - CENTER_X);
		// System.out.println("teta is now = " + getTeta());
	}

	public int getDepth() {
		if (getInputEdges().size() == 0) {
			return 0;
		}
		Node parentNode = getInputEdges().get(0).getStartNode();
		return parentNode.getDepth() + 1;
	}

	public List<Node> getSiblingNodes() {
		if (getInputEdges().size() == 0) {
			return Collections.singletonList(this);
		}
		List<Node> returned = new ArrayList<>();
		for (Edge e : getInputEdges().get(0).getStartNode().getOutputEdges()) {
			returned.add(e.getEndNode());
		}
		return returned;
	}

	@Override
	public String toString() {
		return "GraphNode[" + name + "](" + x + "," + y + ")";
	}

	private double labelX = 30;
	private double labelY = 0;

	public double getLabelX() {
		return labelX;
	}

	public void setLabelX(double labelX) {
		this.labelX = labelX;
	}

	public double getLabelY() {
		return labelY;
	}

	public void setLabelY(double labelY) {
		this.labelY = labelY;
	}

}
