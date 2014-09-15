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
    private List<CfgInfo> parts;//parts info
    private CfgInfo elementInfo;//element info
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
    @Override
    public String toString() {
        return id + "[" + label + "]" + getTypeStr();
    }

    //
    public String getLabelOrId() {
        return label == null ? id : label;
    }

    public String getTypeStr() {
        switch (type) {
        case Type_String:
            return "str";
        case Type_Integer:
            return "integer";
        case Type_Real:
            return "real";
        case Type_Boolean:
            return "bool";
        case Type_Struct:
            return "struct";
        case Type_List:
            return "list";
        case Type_Map:
            return "map";
        }
        return "unknown";
    }

    //
    public List<CfgInfo> getParts() {
        return parts;
    }

    public void setParts(List<CfgInfo> parts) {
        this.parts = parts;
    }

    public CfgInfo getElementInfo() {
        return elementInfo;
    }

    public void setElementInfo(CfgInfo elementInfo) {
        this.elementInfo = elementInfo;
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
