package model;

import com.alibaba.fastjson.annotation.JSONField;

public class Habit {
    private int id;
    private String userId;
    private String name;
    private int icon;
    private int category;
    private int weekday;
    private String createTime;
    private int clockinDays;
    private boolean flag_today;
    private int insistDays;

    public Habit() {
    }

    public Habit(int id, String userId, String name, int icon, int category, int weekday, String createTime, int clockinDays, boolean flag_today, int insistDays) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.icon = icon;
        this.category = category;
        this.weekday = weekday;
        this.createTime = createTime;
        this.clockinDays = clockinDays;
        this.flag_today = flag_today;
        this.insistDays = insistDays;
    }

    @JSONField(ordinal = 1)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JSONField(serialize = false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JSONField(ordinal = 2)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JSONField(ordinal = 3)
    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @JSONField(ordinal = 4)
    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @JSONField(ordinal = 5)
    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    @JSONField(ordinal = 6)
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @JSONField(ordinal = 7)
    public int getClockinDays() {
        return clockinDays;
    }

    public void setClockinDays(int clockinDays) {
        this.clockinDays = clockinDays;
    }

    @JSONField(ordinal = 8)
    public boolean isFlag_today() {
        return flag_today;
    }

    public void setFlag_today(boolean flag_today) {
        this.flag_today = flag_today;
    }

    @JSONField(ordinal = 9)
    public int getInsistDays() {
        return insistDays;
    }

    public void setInsistDays(int insistDays) {
        this.insistDays = insistDays;
    }
}