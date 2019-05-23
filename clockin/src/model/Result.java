package model;

import com.alibaba.fastjson.JSONObject;

public class Result {

    private int status;
    private String desp;

    public Result(int status, String desp) {
        this.status = status;
        this.desp = desp;
    }

    public Result() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
