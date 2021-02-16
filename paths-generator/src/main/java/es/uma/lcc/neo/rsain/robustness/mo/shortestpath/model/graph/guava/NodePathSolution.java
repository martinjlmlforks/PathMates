package es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava;


import java.util.Arrays;
import java.util.List;

/**
 * Created by christian on 5/12/16.
 * Path solution in the graph
 */
public class NodePathSolution {
    private float[] objectives;
    private Long[] variables;
    private int tl = 0;

    public NodePathSolution(float[] objectives, Long[] variables) {
        this.objectives = objectives;
        this.variables = variables;
    }

    public NodePathSolution(float[] objectives, List<Node> variables) {
        this.objectives = objectives;
        this.variables = new Long[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            this.variables[i] = variables.get(i).getId();
        }
    }

    public NodePathSolution(GraphTable graphTable, List<Long> variables) {
        this.variables = new Long[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            this.variables[i] = variables.get(i);
        }
        this.objectives = graphTable.getFitness(this.variables);
    }

    public float[] getObjectives() {
        return objectives;
    }

    public Long[] getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        String s = "[";
        for (Long var : variables) {
            s += var + ",";
        }
        s += "] ";

        for (Float var : objectives) {
            s += var + ",";
        }

        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodePathSolution that = (NodePathSolution) o;

        return Arrays.equals(objectives, that.objectives);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(objectives);
    }

    public int getTl() {
        return tl;
    }

    public void setTl(int tl) {
        this.tl = tl;
    }
}
