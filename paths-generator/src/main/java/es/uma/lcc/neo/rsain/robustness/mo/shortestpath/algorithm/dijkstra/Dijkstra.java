package es.uma.lcc.neo.rsain.robustness.mo.shortestpath.algorithm.dijkstra;

import java.util.*;
import java.util.Map.Entry;

import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.GraphTable;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.Node;

/**
 * Dijkstra algorithm
 */
public class Dijkstra {

	private GraphTable graph;
	private Map<Long, Float> distances;
	private Map<Long, Long> predecessors;
	private HashSet<Long> unvisited;
	private Node from;
	private Node to;

	public Dijkstra() {
		distances = new HashMap<Long, Float>();
		predecessors = new HashMap<Long, Long>();
		unvisited = new HashSet<Long>();
	}

	GraphTable getGraph() {
		return graph;
	}

	public void setGraph(GraphTable graph) {
		this.graph = graph;
	}

	public List<Node> getPath(Long from, Long to) {
		if (from == to) { // path with a single node
			List<Node> path = new LinkedList<Node>();
			path.add(graph.getIntersections().get(from));
			return path;
		}

		setShortestDistance(from, 0f);
		unvisited.add(from);

		while (!unvisited.isEmpty()) {
			Long node = minDistance(unvisited);
			if (node == to)
				return computePath(to);
			unvisited.remove(node);
			visitNeighbors(node);
		}

		return computePath(to);
	}

	private void visitNeighbors(Long source) {
		Map<Long, Long> neighbors = getNeighbors(source);
		for (Entry<Long, Long> neighbor : neighbors.entrySet()) {
			if (getShortestDistance(neighbor.getKey()) > getShortestDistance(source)
					+ getEdgeDistance(source, neighbor)) {
				setShortestDistance(neighbor.getKey(), getShortestDistance(source) + getEdgeDistance(source, neighbor));
				predecessors.put(neighbor.getKey(), source);
				unvisited.add(neighbor.getKey());
			}
		}
	}

	public float getEdgeDistance(Long source, Entry<Long, Long> target) {
		return graph.getWeightsMatrix().get(target.getValue(), 0L);
	}

	private float getShortestDistance(long destId) {
		if (!distances.containsKey(destId)) {
			distances.put(destId, Float.MAX_VALUE);
		}
		return distances.get(destId);
	}

	private void setShortestDistance(long destId, float distance) {
		distances.put(destId, distance);
	}

	public Map<Long, Long> getNeighbors(Long sourceId) {
		return graph.getAdjacencyMatrix().row(sourceId);
	}

	private Long minDistance(HashSet<Long> nodes) {
		Long minimum = null;
		for (Long vertex : nodes) {
			if (minimum == null) {
				minimum = vertex;
			} else if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
				minimum = vertex;
			}
		}
		return minimum;
	}

	private List<Node> computePath(Long target) {
		List<Node> path = new LinkedList<Node>();
		Long step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			if (getGraph().getAdjacencyMatrix().get(from.getId(), to.getId()) != null) {
				path.add(from);
				path.add(to);
				return path;
			}
			return path;
		}
		path.add(graph.getIntersections().get(step));
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(graph.getIntersections().get(step));
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}

}
