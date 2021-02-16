package es.uma.lcc.neo.rsain.robustness.mo.shortestpath;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava.GraphTable;
import es.uma.lcc.neo.rsain.robustness.mo.shortestpath.utilities.ProcessGraph;

/**
 * Created by Rubén Saborido on Dec 2020. Based on Christian Cintrano
 * implementation on 04/24/17
 */
public class GraphUtilitiesMain {

	public static List<Long[]> generateRandomStartingAndEndingPoints(GraphTable g, Long initialPoint, Long endPoint,
			int radiusFromInitialPoint, int radiusFromEndPoint, int quantity) {
		ArrayList<Long[]> result = new ArrayList();
		Random r = new Random(0);
		int randomNodeIndex;
		Long[] nodes = g.getIntersections().keySet().toArray(new Long[0]);

		double latInitialPoint = g.getIntersections().get(initialPoint).getLatitude().doubleValue();
		double lonInitialPoint = g.getIntersections().get(initialPoint).getLongitude().doubleValue();
		double latEndPoint = g.getIntersections().get(endPoint).getLatitude().doubleValue();
		double lonEndPoint = g.getIntersections().get(endPoint).getLongitude().doubleValue();

		for (int i = 0; i < quantity; i++) {
			Long[] extremes = new Long[2];

			boolean foundFromIntialPoint = false, foundFromEndPoint = false;
			Long newInitialPoint = 0L, newEndPoint = 0L;

			while (!foundFromIntialPoint || !foundFromEndPoint) {
				randomNodeIndex = r.nextInt(nodes.length);
				double latNewPoint = g.getIntersections().get(nodes[randomNodeIndex]).getLatitude().doubleValue();
				double lonNewPoint = g.getIntersections().get(nodes[randomNodeIndex]).getLongitude().doubleValue();

				if (!foundFromIntialPoint) {
					if ((ProcessGraph.distance(latInitialPoint, lonInitialPoint, latNewPoint, lonNewPoint, "K")
							* 1000) < radiusFromInitialPoint) {
						newInitialPoint = nodes[randomNodeIndex];
						foundFromIntialPoint = true;
					}
				}

				if (!foundFromEndPoint) {
					if ((ProcessGraph.distance(latEndPoint, lonEndPoint, latNewPoint, lonNewPoint, "K")
							* 1000) < radiusFromEndPoint) {
						newEndPoint = nodes[randomNodeIndex];
						foundFromEndPoint = true;
					}
				}
			}

			extremes[0] = newInitialPoint;
			extremes[1] = newEndPoint;

			result.add(extremes);
		}

		return result;
	}
}
