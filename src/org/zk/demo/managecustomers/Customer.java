package org.zk.demo.managecustomers;

import java.sql.Date;

public class Customer {
    private int id;
    private String name;
    private Date date;
    private boolean deleted;

    public Customer() {

    }

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
        this.deleted = false;
    }

    public Customer(int id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.deleted = false;
    }

    public Customer(int id, String name, Date date, boolean deleted) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.deleted = deleted;
    }

    public Customer(String name) {
        this.name = name;
        this.deleted = false;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean getDeleted() {
        return deleted;
    }

}
