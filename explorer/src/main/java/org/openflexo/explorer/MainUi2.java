package org.openflexo.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.explorer.graph.Graph;
import org.openflexo.explorer.graph.GraphDrawing;
import org.openflexo.explorer.graph.GraphDrawing.TestDrawingController;
import org.openflexo.explorer.graph.Node;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class MainUi2 {
	private static final Logger LOGGER = Logger.getLogger(MainUi2.class.getPackage().getName());

	public static void main(String[] args) {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		showPanel();
	}

	public static void showPanel() {
		final JDialog dialog = new JDialog((Frame) null, false);

		JPanel panel = new JPanel(new BorderLayout());

		final GraphDrawing d = makeDrawing();
		final TestDrawingController dc = new TestDrawingController(d);
		// dc.disablePaintingCache();
		dc.getDrawingView().setName("[NO_CACHE]");
		panel.add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
		// panel.add(dc.scaleSelector.getComponent(), BorderLayout.NORTH);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();

		dialog.setVisible(true);
	}

	public static GraphDrawing makeDrawing() {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		Graph graph = new Graph();
		Node node1 = new Node("node1", graph);
		Node node2 = new Node("node2", graph);
		Node node3 = new Node("node3", graph);
		node1.connectTo(node2);
		node1.connectTo(node3);
		node3.connectTo(node2);
		GraphDrawing returned = new GraphDrawing(graph, factory);
		returned.printGraphicalObjectHierarchy();
		return returned;
	}
}
