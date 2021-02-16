package es.uma.lcc.neo.rsain.robustness.mo.shortestpath.algorithm;

import java.util.List;

import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.GraphTable;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.Node;

/**
 * Created by Christian Cintrano on 26/01/17.
 * Routing Algorithm interface
 */
public interface RoutingAlgorithm {

    void setGraph(GraphTable graph);

    List<Node> getPath(Node start, Node end);
}
