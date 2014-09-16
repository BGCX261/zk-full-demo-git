package org.hxzon.configdesigner3.core;

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
    private String id;
    private int type;
    private CfgInfo typeRef;
    //结构体字段
    private String label;
    //结构体
    private List<CfgInfo> partsInfo;
    private String labelKey;
    //列表或映射表
    private CfgInfo elementInfo;
    //
    private Object defaultValue;
    private boolean textArea;//use textArea or textInput
    private boolean embed;

    //====================
    public CfgInfo() {
    }

    public CfgInfo(int type) {
        setType(type);
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

    public CfgInfo getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(CfgInfo typeRef) {
        this.typeRef = typeRef;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    public List<CfgInfo> getPartsInfo() {
        return partsInfo;
    }

    public void setPartsInfo(List<CfgInfo> partsInfo) {
        this.partsInfo = partsInfo;
    }

    public CfgInfo getElementInfo() {
        return elementInfo;
    }

    public void setElementInfo(CfgInfo elementInfo) {
        this.elementInfo = elementInfo;
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
        return embed;
    }

    public void setEmbed(boolean embed) {
        this.embed = embed;
    }

}
