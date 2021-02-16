package es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Christian Cintrano on 5/12/16.
 * Vertex of the graph
 */
public class Node{
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private long id;
    private float noiseMean;
    private float noiseSD;

    private Node(String name) {
        this.name = name;
    }

    private Node(long id) {
        this(String.valueOf(id));
        this.id = id;
    }
    public Node(long id, String latitude, String longitude) {
        this(id);
        this.latitude = new BigDecimal(latitude);
        this.longitude = new BigDecimal(longitude);
    }
    public Node(long id, double latitude, double longitude) {
        this(id);
        this.latitude = new BigDecimal(latitude).setScale(6, RoundingMode.HALF_UP);
        this.longitude = new BigDecimal(longitude).setScale(6, RoundingMode.HALF_UP);
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "" + id;
    }

    public long getId() {
        return id;
    }

    public void setNoiseMean(float noiseMean) {
        this.noiseMean = noiseMean;
    }

    public void setNoiseSD(float noiseSD) {
        this.noiseSD = noiseSD;
    }

    public float getNoiseMean() {
        return noiseMean;
    }

    public float getNoiseSD() {
        return noiseSD;
    }
}
