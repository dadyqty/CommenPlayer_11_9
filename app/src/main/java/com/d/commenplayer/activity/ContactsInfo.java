package com.d.commenplayer.activity;

public class ContactsInfo {
    private int id;
    private String name;
    private String number;

    private String pinyin;

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyin() {
        return pinyin;
    }

    public ContactsInfo(int id, String name, String number, String pinyin) {
        this.pinyin = pinyin;
        this.id = id;
        this.name = name;
        this.number = number;
    }
    public ContactsInfo(){
        super();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "ContactsInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
