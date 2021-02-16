package es.uma.lcc.neo.rsain.robustness.mo.shortestpath.utilities;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.GraphTable;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

/**
 * Created by Christian Cintrano on 01/19/17. Support class to handle with
 * graphs
 */
public class ProcessGraph {

	/**
	 * Get graph object from a XML file
	 * 
	 * @param file File
	 * @return a GraphTable object
	 */
	public static GraphTable parserFile(String file) {
		String extension = file.split("\\.")[file.split("\\.").length - 1];
		if (extension.equals("xml")) {
			return parserXMLFile(file);
		}
		if (extension.equals("co")) {
			return parserCOFile(file);
		}
		return null;
	}

	private static GraphTable parserXMLFile(String file) {
		GraphTable graph = new GraphTable();
		try {
			File fXmlFile = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("node");
			System.out.println("Load nodes: " + nList.getLength());
			int size = nList.getLength();
			for (int temp = 0; temp < size; temp++) {
				org.w3c.dom.Node nNode = nList.item(temp);
				Element eElement = (Element) nNode;
				graph.getIntersections().put(Long.parseLong(eElement.getAttribute("id")),
						new Node(Long.parseLong(eElement.getAttribute("id")), eElement.getAttribute("lat"),
								eElement.getAttribute("lon")));
				graph.getInverseIntersections().put(
						graph.getIntersections().get(Long.parseLong(eElement.getAttribute("id"))),
						Long.parseLong(eElement.getAttribute("id")));
			}

			nList = doc.getElementsByTagName("arc");
			System.out.println("Load arcs: " + nList.getLength());
			Element eElement;
			size = nList.getLength();
			for (int temp = 0; temp < size; temp++) {
				eElement = (Element) nList.item(temp);

				graph.getAdjacencyMatrix().put(Long.parseLong(eElement.getAttribute("from")),
						Long.parseLong(eElement.getAttribute("to")), Long.parseLong(eElement.getAttribute("arcid")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return graph;
	}

	private static GraphTable parserCOFile(String file) {
		GraphTable graph = new GraphTable();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = br.readLine();

			while (line != null) {
				String[] array = line.split(" ");
				if (array[0].equals("v")) {
					Long id = new Long(array[1]);
					graph.getIntersections().put(id, new Node(id, Double.parseDouble(array[3]) / 1000000d,
							Double.parseDouble(array[2]) / 1000000d));
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return graph;
	}

	/**
	 * Add the arcs labels to the graph pass to the reference from a XML file
	 * 
	 * @param graph GraphTable object
	 * @param file  XML file with the arcs labels
	 * @return the graph object
	 */
	public static GraphTable applyWeights(GraphTable graph, String file) {
		System.out.print("Adding weight to the graph...");
		try {
			File fXmlFile = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("weight");
			int size = nList.getLength();
			Element eElement;
			for (int temp = 0; temp < size; temp++) {
				eElement = (Element) nList.item(temp);
				graph.getWeightsMatrix().put(Long.parseLong(eElement.getAttribute("arcid")),
						Long.parseLong(eElement.getAttribute("type")),
						Float.parseFloat(eElement.getAttribute("value")));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("added");
		return graph;
	}

	public static GraphTable applyMapping(GraphTable graph, String file) {
		System.out.print("Adding mapping to the graph...");
		BufferedReader br = null;
		Map<Long, Long> mapping = new HashMap<Long, Long>();
		try {
			br = new BufferedReader(new FileReader(file));
			String line = br.readLine();

			while (line != null && line.length() > 0) {
				String[] array = line.split(" ");
				mapping.put(Long.parseLong(array[0]), Long.parseLong(array[1]));
				line = br.readLine();
			}
			graph.setMapping(mapping);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		System.out.println("added");
		return graph;
	}

	/**
	 * Apply the mapping to the intersections and adjacencyMatrix
	 * 
	 * @param graph the graph
	 * @return the graph
	 */
	public static GraphTable fixVertexIndex(GraphTable graph) {
		Table<Long, Long, Long> newAdjacencyMatrix = HashBasedTable.create();
		Map<Long, Node> newIntersections = new HashMap<Long, Node>();

		for (Long l : graph.getIntersections().keySet()) {
			newIntersections.put(graph.getMapping().get(l), graph.getIntersections().get(l));
		}
		for (Long r : graph.getAdjacencyMatrix().rowKeySet()) {
			for (Long c : graph.getAdjacencyMatrix().row(r).keySet()) {
				newAdjacencyMatrix.put(graph.getMapping().get(r), graph.getMapping().get(c),
						graph.getAdjacencyMatrix().get(r, c));
			}
		}
		graph.setIntersections(newIntersections);
		graph.setAdjacencyMatrix(newAdjacencyMatrix);
		return graph;
	}

	public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit.equals("K")) {
			dist = dist * 1.609344;
		} else if (unit.equals("N")) {
			dist = dist * 0.8684;
		}

		return (dist);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	public static GraphTable prepareGraph(String graphFilePath, String weightFilePath0, String mapping) {
		// Graph
		GraphTable graph = ProcessGraph.parserFile(graphFilePath);
		applyWeights(graph, weightFilePath0);

		assert graph != null;
		graph.getWeightsMatrix().column(10L).clear();
		ProcessGraph.applyMapping(graph, mapping);
		return graph;
	}
}
