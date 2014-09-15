package org.hxzon.jt.config;

import java.util.List;

public class OutputSchemaInfo {

    private String targetType;
    private String name;
    private String fname;
    private String vtype;
    private String from;
    private String notrim;
    private List<OutputSchemaInfo> children;

    //--------------
    public String toString() {
        return getFname() + ",myType:" + getTargetType() + ",vtype:" + getVtype();
    }

    //--------------

    public String getName() {
        return name;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFname() {
        if (fname == null || fname.isEmpty()) {
            fname = name;
        }
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getVtype() {
        return vtype;
    }

    public void setVtype(String vtype) {
        this.vtype = vtype;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean haveFrom() {
        return from != null && !from.isEmpty();
    }

    public String getNotrim() {
        return notrim;
    }

    public void setNotrim(String notrim) {
        this.notrim = notrim;
    }

    public boolean haveDefault() {
        return "true".equals(notrim);
    }

    public List<OutputSchemaInfo> getChildren() {
        return children;
    }

    public void setChildren(List<OutputSchemaInfo> children) {
        this.children = children;
    }

}
