package model;

import java.util.List;

public class Habits {
    private List<Habit> allTime;
    private List<Habit> morning;
    private List<Habit> noon;
    private List<Habit> evening;

    public Habits() {
    }

    public Habits(List<Habit> allTime, List<Habit> morning, List<Habit> noon, List<Habit> evening) {
        this.allTime = allTime;
        this.morning = morning;
        this.noon = noon;
        this.evening = evening;
    }

    public List<Habit> getAllTime() {
        return allTime;
    }

    public void setAllTime(List<Habit> allTime) {
        this.allTime = allTime;
    }

    public List<Habit> getMorning() {
        return morning;
    }

    public void setMorning(List<Habit> morning) {
        this.morning = morning;
    }

    public List<Habit> getNoon() {
        return noon;
    }

    public void setNoon(List<Habit> noon) {
        this.noon = noon;
    }

    public List<Habit> getEvening() {
        return evening;
    }

    public void setEvening(List<Habit> evening) {
        this.evening = evening;
    }
}
