package model;

import java.util.List;

public class DatePlan {
    private String Date;
    private List<Plan> list;

    public DatePlan() {
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public List<Plan> getList() {
        return list;
    }

    public void setList(List<Plan> list) {
        this.list = list;
    }
}
