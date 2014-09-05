package org.hxzon.demo.jt.config;

import org.apache.tapestry5.json.JSONString;

public class MyString implements JSONString {
    private final String text;

    public MyString(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public String toJSONString() {
        return "\"" + text + "\"";
    }

}
