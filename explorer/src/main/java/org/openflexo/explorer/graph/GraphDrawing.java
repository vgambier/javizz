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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.connie.DataBinding;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.actions.MouseDragControlImpl;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;

public class GraphDrawing extends DrawingImpl<Graph> {

	private DrawingGraphicalRepresentation graphRepresentation;
	private ShapeGraphicalRepresentation nodeRepresentation;
	private ConnectorGraphicalRepresentation edgeRepresentation;

	public GraphDrawing(Graph graph, FGEModelFactory factory) {
		super(graph, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void init() {
		graphRepresentation = getFactory().makeDrawingGraphicalRepresentation();
		// graphRepresentation.setBackgroundColor(Color.RED);
		nodeRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		// nodeRepresentation.setX(50);
		// nodeRepresentation.setY(50);
		nodeRepresentation.setWidth(20);
		nodeRepresentation.setHeight(20);
		nodeRepresentation.setAbsoluteTextX(30);
		nodeRepresentation.setAbsoluteTextY(0);
		edgeRepresentation = getFactory().makeConnectorGraphicalRepresentation(ConnectorType.LINE);

		MouseDragControl moveControl = nodeRepresentation.getMouseDragControl("Move");
		nodeRepresentation.removeFromMouseDragControls(moveControl);
		nodeRepresentation.addToMouseDragControls(new MouseDragControlImpl("dragNode", MouseButton.LEFT, new MoveAction() {

			@Override
			public boolean handleMouseReleased(org.openflexo.fge.Drawing.DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> editor,
					MouseControlContext context, boolean isSignificativeDrag) {
				boolean returned = super.handleMouseReleased(node, editor, context, isSignificativeDrag);
				System.out.println("Detected mouse released");
				return returned;
			}
		}, false, false, false, false, getFactory().getEditingContext()));

		final DrawingGRBinding<Graph> graphBinding = bindDrawing(Graph.class, "graph", new DrawingGRProvider<Graph>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Graph drawable, FGEModelFactory factory) {
				return graphRepresentation;
			}
		});
		final ShapeGRBinding<Node> nodeBinding = bindShape(Node.class, "node", new ShapeGRProvider<Node>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(Node drawable, FGEModelFactory factory) {
				return nodeRepresentation;
			}
		});
		final ConnectorGRBinding<Edge> edgeBinding = bindConnector(Edge.class, "edge", nodeBinding, nodeBinding, graphBinding,
				new ConnectorGRProvider<Edge>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(Edge drawable, FGEModelFactory factory) {
						return edgeRepresentation;
					}
				});

		graphBinding.addToWalkers(new GRStructureVisitor<Graph>() {

			@Override
			public void visit(Graph graph) {
				for (Node node : graph.getNodes()) {
					drawShape(nodeBinding, node);
				}
			}
		});

		nodeBinding.addToWalkers(new GRStructureVisitor<Node>() {
			@Override
			public void visit(Node node) {
				System.out.println("Walking for edges ");
				for (Edge edge : node.getInputEdges()) {
					drawConnector(edgeBinding, edge, edge.getStartNode(), edge.getEndNode(), node.getGraph());
				}
				for (Edge edge : node.getOutputEdges()) {
					drawConnector(edgeBinding, edge, edge.getStartNode(), edge.getEndNode(), node.getGraph());
				}
			}
		});

		nodeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		// nodeBinding.setDynamicPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, new DataBinding<Double>("drawable.labelX"));
		// nodeBinding.setDynamicPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, new DataBinding<Double>("drawable.labelY"));
		nodeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.x"), true);
		nodeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.y"), true);

	}

	public static class TestDrawingController extends JDianaInteractiveEditor<Graph> {
		private final JPopupMenu contextualMenu;
		private final JDianaScaleSelector scaleSelector;

		public TestDrawingController(GraphDrawing aDrawing) {
			super(aDrawing, aDrawing.getFactory(), SwingViewFactory.INSTANCE, SwingToolFactory.DEFAULT);
			scaleSelector = (JDianaScaleSelector) getToolFactory().makeDianaScaleSelector(this);
			contextualMenu = new JPopupMenu();
			contextualMenu.add(new JMenuItem("Item"));
		}
	}
}
