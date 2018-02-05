package org.openflexo.explorer;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.openflexo.explorer.model.GradleDir;
import org.openflexo.explorer.model.Root;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

public class MainUi {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("You must specify a starting path!");
			return;
		}
		Root root = new Root(args[0]);
		root.parseBuilds();

		Graph<GradleDir, String> graph = new DelegateForest<>();

		root.addToGraph(graph);

		// graph.addEdge("RAD-A", 1, 2);
		// graph.addEdge("RAD-B", 1, 3);

		Layout<GradleDir, String> layout = new TreeLayout<>((Forest<GradleDir, String>) graph);
		VisualizationViewer<GradleDir, String> vv = new VisualizationViewer<>(layout, new Dimension(1000, 600));
		vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(graph));
		// vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setVertexLabelTransformer(gd -> gd.getName());
		// vv.getRenderContext().setEdgeLabelRenderer(new BasicVertexLabelRenderer());
		JFrame frame = new JFrame();
		frame.getContentPane().add(vv);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
