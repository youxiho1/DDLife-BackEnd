package model;

public class Plan {
    private int id;
    private int icon;
    private String title;
    private String desp;
    private String deadline;
    private boolean flag_finish;
    private String finish_time;
    private String create_time;
    private String userId;

    public Plan() {
    }

    public Plan(int id, int icon, String title, String desp, String deadline, boolean flag_finish, String finish_time, String create_time, String userId) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.desp = desp;
        this.deadline = deadline;
        this.flag_finish = flag_finish;
        this.finish_time = finish_time;
        this.create_time = create_time;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isFlag_finish() {
        return flag_finish;
    }

    public void setFlag_finish(boolean flag_finish) {
        this.flag_finish = flag_finish;
    }

    public String getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(String finish_time) {
        this.finish_time = finish_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
