package model;

import java.util.List;

public class Plans {
    private List<Plan> finished;
    private List<Plan> unfinished;

    public Plans() {
    }

    public List<Plan> getFinished() {
        return finished;
    }

    public void setFinished(List<Plan> finished) {
        this.finished = finished;
    }

    public List<Plan> getUnfinished() {
        return unfinished;
    }

    public void setUnfinished(List<Plan> unfinished) {
        this.unfinished = unfinished;
    }
}
