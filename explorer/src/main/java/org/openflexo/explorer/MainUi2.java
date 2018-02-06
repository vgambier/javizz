package org.openflexo.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.explorer.graph.Graph;
import org.openflexo.explorer.graph.GraphDrawing;
import org.openflexo.explorer.graph.Node;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class MainUi2 {
	private static final Logger LOGGER = Logger.getLogger(MainUi2.class.getPackage().getName());

	public static void main(String[] args) {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
			FGEModelFactory factory = new FGEModelFactoryImpl();
			GraphDrawing d = new GraphDrawing(buildGraph(), factory);
			d.printGraphicalObjectHierarchy();

			// The graph editor
			final JDianaInteractiveEditor<Graph> dc = new JDianaInteractiveEditor<>(d, d.getFactory(), SwingViewFactory.INSTANCE,
					SwingToolFactory.DEFAULT);
			dc.getDrawingView().setName("The title");
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
			// The frame
			final JFrame frame = new JFrame();
			frame.setPreferredSize(new Dimension(550, 600));
			frame.getContentPane().add(panel);
			frame.validate();
			frame.pack();
			frame.setVisible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	public static Graph buildGraph() {
		Graph graph = new Graph();
		Node node1 = new Node("node1", graph);
		Node node2 = new Node("node2", graph);
		Node node3 = new Node("node3", graph);
		node1.connectTo(node2);
		node1.connectTo(node3);
		node3.connectTo(node2);
		return graph;
	}
}
