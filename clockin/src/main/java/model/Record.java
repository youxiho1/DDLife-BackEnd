package model;

public class Record {
    private int id;
    private String userId;
    private int habitId;
    private String description;
    private String clockin_time;
    private String clockin_date;

    public Record() {
    }

    public Record(int id, String userId, int habitId, String description, String clockin_time, String clockin_date) {
        this.id = id;
        this.userId = userId;
        this.habitId = habitId;
        this.description = description;
        this.clockin_time = clockin_time;
        this.clockin_date = clockin_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClockin_time() {
        return clockin_time;
    }

    public void setClockin_time(String clockin_time) {
        this.clockin_time = clockin_time;
    }

    public String getClockin_date() {
        return clockin_date;
    }

    public void setClockin_date(String clockin_date) {
        this.clockin_date = clockin_date;
    }
}
