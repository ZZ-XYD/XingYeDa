package com.xingyeda.ehome.bean;

/**
 * Created by LDL on 2017/6/9.
 */

public class OnOffBean {
    private String name;
    private int id;

    public OnOffBean() {
    }

    public OnOffBean(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
