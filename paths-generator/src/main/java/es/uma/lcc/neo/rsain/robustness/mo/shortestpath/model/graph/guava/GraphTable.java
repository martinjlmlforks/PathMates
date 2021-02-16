package es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 5/12/16.
 * Map Graph represented as a nStart -> <nEnd, Wid>
 */
public class GraphTable {
    private Map<Long, Node> intersections;
    private Map<Node, Long> inverseIntersections;
    private Table<Long, Long, Float> weightsMatrix;
    private Table<Long, Long, Long> adjacencyMatrix;
    private Table<Long, Long, TlLogic> tlMatrix;

    private Map<Long, Float> lowerBound;
    private Map<Long, Float> upperBound;

    private Map<Long,Long> mapping;

    public GraphTable() {

        intersections = new HashMap<Long, Node>();
        inverseIntersections = new HashMap<Node, Long>();
        weightsMatrix = HashBasedTable.create();
        adjacencyMatrix = HashBasedTable.create();
        tlMatrix = HashBasedTable.create();

        lowerBound = new HashMap<Long, Float>();
        upperBound = new HashMap<Long, Float>();
    }
    @Override
    public String toString() {
        return "Graph{" +
                "n nodes=" + intersections.size() +
                ", n arcs=" + adjacencyMatrix.size() +
                "}\n";
    }



    /* Interface Methods */
    public Map<Long, Node> getIntersections() {
        return intersections;
    }

    public Table<Long, Long, Float> getWeightsMatrix() {
        return weightsMatrix;
    }

    public Table<Long, Long, Long> getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public Table<Long, Long, TlLogic> getTlMatrix() {
        return tlMatrix;
    }

    public void setAdjacencyMatrix(Table<Long, Long, Long> adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public Map<Long, Float> getLowerBound() {
        return lowerBound;
    }

    public Map<Long, Float> getUpperBound() {
        return upperBound;
    }

    public float[] getFitness(List<Long> path) {
        float[] fitness = new float[getWeightsMatrix().columnKeySet().size()];
        for (int i = 0; i < path.size() - 1; i++) {
            Long arc = getAdjacencyMatrix().get(path.get(i), path.get(i + 1));

            for (Long w : getWeightsMatrix().columnKeySet()) {
                fitness[w.intValue()] += getWeightsMatrix().get(arc, w);
            }
        }

        return fitness;
    }

    public float[] getFitness(List<Node> path, String s) {
        float[] fitness = new float[getWeightsMatrix().columnKeySet().size()];
        for (int i = 0; i < path.size() - 1; i++) {
            Long arc = getAdjacencyMatrix().get(mapping.get(path.get(i).getId()), mapping.get(path.get(i + 1).getId()));

            for (Long w : getWeightsMatrix().columnKeySet()) {
                fitness[w.intValue()] += getWeightsMatrix().get(arc, w);
            }
        }

        return fitness;
    }

    public float getFitness(List<Long> path, Long weight) {
        float fitness = 0f;
        for (int i = 0; i < path.size() - 1; i++) {
            Long arc = getAdjacencyMatrix().get(path.get(i), path.get(i + 1));

            fitness += getWeightsMatrix().get(arc, weight);
        }
        return fitness;
    }

    public void setIntersections(Map<Long, Node> intersections) {
        this.intersections = intersections;
    }

    public void setWeightsMatrix(Table<Long, Long, Float> weightsMatrix) {
        this.weightsMatrix = weightsMatrix;
    }

    public float[] getFitness(Long[] path) {
        float[] fitness = new float[getWeightsMatrix().columnKeySet().size()];
        for (int i = 0; i < path.length - 1; i++) {
            Long arc = getAdjacencyMatrix().get(path[i], path[i + 1]);

            for (Long w : getWeightsMatrix().columnKeySet()) {
                fitness[w.intValue()] += getWeightsMatrix().get(arc, w);
            }
        }
        return fitness;
    }

    public void setMapping(Map<Long,Long> mapping) {
        this.mapping = mapping;
    }

    public Map<Long, Long> getMapping() {
        return mapping;
    }

    public Map<Node, Long> getInverseIntersections() {
        return inverseIntersections;
    }
}
