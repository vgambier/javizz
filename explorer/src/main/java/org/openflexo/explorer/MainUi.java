package org.openflexo.explorer;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
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
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

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

		Layout<GradleDir, String> layout = new TreeLayout<>((Forest<GradleDir, String>) graph);
		VisualizationViewer<GradleDir, String> vv = new VisualizationViewer<>(layout, new Dimension(3000, 600));
		vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(graph));
		vv.getRenderContext().setVertexLabelTransformer(gd -> gd.getName());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		JScrollPane sp = new JScrollPane(vv, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JFrame frame = new JFrame();
		frame.getContentPane().add(sp);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
