package model;

import java.util.ArrayList;
import java.util.List;

public class DatePlan {
    private String Date;
    private List<Plan> list;

    public DatePlan() {
        list = new ArrayList<>();
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

    public void addPlan(Plan plan) {
        list.add(plan);
    }

}
