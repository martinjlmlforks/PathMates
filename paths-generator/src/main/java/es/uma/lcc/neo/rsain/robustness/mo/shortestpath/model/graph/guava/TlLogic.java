package es.uma.lcc.neo.rsain.robustness.mo.shortestpath.model.graph.guava;

public class TlLogic {
    char[] type;
    int timeR, timeG, timeY;

    public TlLogic() {
        timeR = 0;
        timeG = 0;
        timeY = 0;
        type = new char[3];
    }

    public char[] getType() {
        return type;
    }

    public void setType(char[] type) {
        this.type = type;
    }

    public void setType(int index, char type) {
        this.type[index] = type;
    }

    public int getTimeR() {
        return timeR;
    }

    public void setTimeR(int timeR) {
        this.timeR = timeR;
    }

    public int getTimeG() {
        return timeG;
    }

    public void setTimeG(int timeG) {
        this.timeG = timeG;
    }

    public int getTimeY() {
        return timeY;
    }

    public void setTimeY(int timeY) {
        this.timeY = timeY;
    }

    public char calculateStade(int time) { // depends of the type
        int tlTotalTime = timeR + timeG + timeY;
        int rest = time % tlTotalTime;
        for (int i = 0; i < 3; i++) {
            int lightTime = getTime(i);
            if (rest < lightTime) {
                return type[i];
            } else {
                rest = rest - lightTime;
            }
        }
        return 'E'; // Error
    }

    private int getTime(int light) {
        switch (type[light]) {
            case 'R': return timeR;
            case 'G': return timeG;
            case 'Y': return timeY;
            default: return 'E';
        }
    }

    public int getTime(char light) {
        switch (light) {
            case 'R': return timeR;
            case 'G': return timeG;
            case 'Y': return timeY;
            default: return 'E';
        }
    }

    public int calculateTimeStop(long time) {
        int tlTotalTime = timeR + timeG + timeY;
        long rest = time % tlTotalTime;
        int stopTime = 0;
        for (int i = 0; i < 3; i++) {
            int lightTime = getTime(i);
            if (rest < lightTime) {
                if (type[i] == 'R') {
                    stopTime += lightTime - rest;
                } // TODO Add threshold to stop in the yellow light
                return stopTime;
            } else {
                rest = rest - lightTime;
            }
        }
        return stopTime;
    }

    public void addTime(int index, int time) {
        switch (type[index]) {
            case 'R': timeR += time; break;
            case 'G': timeG += time; break;
            case 'Y': timeY += time; break;
        }
    }

    public void addTime(char type, int time) {
        switch (type) {
            case 'R': timeR += time; break;
            case 'G': timeG += time; break;
            case 'Y': timeY += time; break;
        }
    }
}
