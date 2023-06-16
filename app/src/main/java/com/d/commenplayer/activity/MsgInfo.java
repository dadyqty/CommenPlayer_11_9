package com.d.commenplayer.activity;

public class MsgInfo  {
    private int id;
    private String number;
    private String msg;
    private String gpsjd;
    private String gpswd;
    private String time;
    private String rili;
    private int type;
    private int img;


    public MsgInfo(int id, String number, String msg, String gpsjd, String gpswd, String time, String rili, int type,int img) {
        this.id = id;
        this.number = number;
        this.msg = msg;
        this.gpsjd = gpsjd;
        this.gpswd = gpswd;
        this.time = time;
        this.rili = rili;
        this.type = type;
        this.img = img;
    }

    protected MsgInfo() {

    }

    public String getRili() {
        return rili;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getMsg() {
        return msg;
    }

    public String getGpsjd() {
        return gpsjd;
    }

    public String getGpswd() {
        return gpswd;
    }

    public void setRili(String rili) {
        this.rili = rili;
    }

    public String getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setGpsjd(String gpsjd) {
        this.gpsjd = gpsjd;
    }

    public void setGpswd(String gpswd) {
        this.gpswd = gpswd;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "MsgInfo{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", msg='" + msg + '\'' +
                ", gpsjd='" + gpsjd + '\'' +
                ", gpswd='" + gpswd + '\'' +
                ", time='" + time + '\'' +
                ", rili='" + rili + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
