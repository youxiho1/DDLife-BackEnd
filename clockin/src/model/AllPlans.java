package model;

import java.util.List;

public class AllPlans {
    private List<DatePlan> unfinished;
    private List<DatePlan> finished;

    public AllPlans() {
    }

    public List<DatePlan> getUnfinished() {
        return unfinished;
    }

    public void setUnfinished(List<DatePlan> unfinished) {
        this.unfinished = unfinished;
    }

    public List<DatePlan> getFinished() {
        return finished;
    }

    public void setFinished(List<DatePlan> finished) {
        this.finished = finished;
    }
}
