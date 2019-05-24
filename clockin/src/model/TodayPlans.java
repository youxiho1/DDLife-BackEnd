package model;

import java.util.List;

public class TodayPlans {
    private List<Plan> finished;
    private List<Plan> unfinished;

    public TodayPlans() {
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
