package org.hxzon.configdesigner.core;

import java.util.List;

public class CfgInfo {

    public static final int Type_String = 1;
    public static final int Type_Integer = 2;
    public static final int Type_Real = 3;
    public static final int Type_Boolean = 4;
    public static final int Type_Combo = 5;
    public static final int Type_Struct = 6;
    public static final int Type_Map = 7;
    public static final int Type_List = 8;
    public static final int Type_End = 9;
    //
    private List<CfgInfo> parts;
    //
    private String id;
    private String label;
    private String labelKey;
    private int type;
    private String source;
    private Object defaultValue;
    //
    private boolean textArea;//use textArea or textInput
    private boolean embed;

    //
    public String toString() {
        return id + "[" + label + "]" + type;
    }

    //
    public String getTitle() {
        return label == null ? id : label;
    }

    //
    public List<CfgInfo> getParts() {
        return parts;
    }

    public void setParts(List<CfgInfo> parts) {
        this.parts = parts;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String elabel) {
        this.labelKey = elabel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isTextArea() {
        return textArea;
    }

    public void setTextArea(boolean textArea) {
        this.textArea = textArea;
    }

    public boolean isEmbed() {
        return embed || type < CfgInfo.Type_Combo;
    }

    public void setEmbed(boolean embed) {
        this.embed = embed;
    }

}
