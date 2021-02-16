package es.uma.lcc.neo.rsain.robustness.mo.shortestpath;

import java.io.*;
import java.util.*;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.algorithm.dijkstra.Dijkstra;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.GraphTable;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.Node;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.utilities.ProcessGraph;

/**
 * 
 * Created by Rubén Saborido in 2021 based on code of Christian Cintrano on
 * 22/01/17.
 * 
 */
public class RunMain {
	public final static String GRAPH_FILE = "new-malaga-graph.xml";
	public final static String MAPPING_FILE = "mapping-malaga.txt";
	public final static String WEIGHTS_TIME_FILE = "weights_time-hbefa.xml";
	public final static Long[] INITIAL_POINT = new Long[] { 303283741L, 2507293804L };

	public static void main(String[] args) {
		System.out.println("=== START EXPERIMENTS ===");
		for (String s : args) {
			System.out.print(s + " ");
		}
		System.out.println();

		GraphTable graph = null;
		System.out.println("Loading graph...");
		graph = ProcessGraph.prepareGraph(GRAPH_FILE, WEIGHTS_TIME_FILE, MAPPING_FILE);
		ProcessGraph.fixVertexIndex(graph);

		// Points
		System.out.print("Reading points...");
		Long[] points = INITIAL_POINT;

		// Algorithm
		if (points != null) {
			System.out.println(points[0] + " " + points[1]);
			System.out.println("Run Dijkstra...");
			rDijkstraExperiments(graph, points, INITIAL_POINT);
		}

		System.out.println("=== END EXPERIMENTS ===");
	}

	private static void rDijkstraExperiments(GraphTable graph, Long[] randomPoints, Long[] initialPoint) {
		rDijkstra(graph, randomPoints, initialPoint);
	}

	private static void rDijkstra(GraphTable graph, Long[] randomPoints, Long[] initialPoint) {
		Dijkstra dj;
		List<Node> path;

		List<Long[]> points = GraphUtilitiesMain.generateRandomStartingAndEndingPoints(graph,
				graph.getMapping().get(initialPoint[0]), graph.getMapping().get(initialPoint[1]), 300, 300, 500);
		writeInFile(graph, points, "randomStartingAndEndingPoints.txt");

		int i = 0;
		for (Long[] p : points) {
			String startPoint = graph.getIntersections().get(p[0]).getLatitude().toPlainString();
			String lonStartPoint = graph.getIntersections().get(p[0]).getLongitude().toPlainString();
			String latEndPoint = graph.getIntersections().get(p[1]).getLatitude().toPlainString();
			String lonEndPoint = graph.getIntersections().get(p[1]).getLongitude().toPlainString();

			i++;
			System.out.println("Proccesing " + i + "...");
			System.out.println(
					"From [" + startPoint + "," + lonStartPoint + "] to [" + latEndPoint + "," + lonEndPoint + "]");

			dj = new Dijkstra();
			dj.setGraph(graph);
			path = dj.getPath(p[0], p[1]);

			// concatenatedData.addAll(path);
			writeInFile(Integer.valueOf(i).toString(), path, "tracks.txt", i != 1);
		}
	}

	private static void writeInFile(String id, List<Node> path, String output, boolean append) {
		try {
			FileWriter fw = new FileWriter(output, append);

			for (Node n : path) {
				fw.write(id + " " + n.getLatitude().toPlainString() + " " + n.getLongitude().toPlainString() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writeInFile(GraphTable g, List<Long[]> input, String output) {
		try {
			FileWriter fw = new FileWriter(output, false);
			for (Long[] p : input) {
				fw.write(g.getIntersections().get(p[0]).getLatitude().toPlainString() + ","
						+ g.getIntersections().get(p[0]).getLongitude().toPlainString() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
