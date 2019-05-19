package model;

public class Habit {
    private int id;
    private String userId;
    private String name;
    private int icon;
    private int color;
    private int category;
    private int weekday;
    private String createTime;
    private int clockinDays;
    private boolean flag_auto;

    public Habit() {
    }

    public Habit(int id, String userId, String name, int icon, int color, int category, int weekday, String createTime, int clockinDays, boolean flag_auto) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.category = category;
        this.weekday = weekday;
        this.createTime = createTime;
        this.clockinDays = clockinDays;
        this.flag_auto = flag_auto;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getClockinDays() {
        return clockinDays;
    }

    public void setClockinDays(int clockinDays) {
        this.clockinDays = clockinDays;
    }

    public boolean isFlag_auto() {
        return flag_auto;
    }

    public void setFlag_auto(boolean flag_auto) {
        this.flag_auto = flag_auto;
    }
}
