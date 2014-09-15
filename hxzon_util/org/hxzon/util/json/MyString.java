package org.hxzon.util.json;


public class MyString implements JSONString {
    private final String text;

    public MyString(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public String toJSONString() {
        return "\"" + text + "\"";
    }

}
