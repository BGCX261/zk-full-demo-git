package org.hxzon.configdesigner2.core;

import java.util.List;

import org.hxzon.util.StringParser;

public class CfgInfo {
    public static final int Type_String = 1;
    public static final int Type_Integer = 2;
    public static final int Type_Real = 3;
    public static final int Type_Boolean = 4;
    public static final int Type_Combo = 5;
    public static final int Type_Struct = 6;
    public static final int Type_Map = 7;
    public static final int Type_List = 8;
    public static final int Type_ViewStruct = 9;
    public static final int Type_End = 10;

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
    private String idPrefix;

    //====================
    public String getLabelOrId() {
        String label = getLabel();
        if (label != null) {
            return label;
        }
        return id;
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

    public CfgInfo findCfgInfo(String path) {
        StringParser parser = new StringParser(path);
        parser.setWithDefaultWps(true);
        parser.setWps(new char[] { '/' });
        return findCfgInfo(parser);
    }

    private CfgInfo findCfgInfo(StringParser parser) {
        String token = parser.nextToken();
        if (token == null) {
            return this;
        }
        if (type == CfgInfo.Type_Struct) {
            for (CfgInfo part : getPartsInfo()) {
                if (part.getId().equals(token)) {
                    return part.findCfgInfo(parser);
                }
            }
        }
        if (type == CfgInfo.Type_List || type == CfgInfo.Type_Struct) {
            if ("e".equals(token)) {
                return getElementInfo().findCfgInfo(parser);
            }
        }
        return null;
    }

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
        if (label == null && typeRef != null) {
            return typeRef.getLabel();
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelKey() {
        if (labelKey == null && typeRef != null) {
            return typeRef.getLabelKey();
        }
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    public List<CfgInfo> getPartsInfo() {
        if (typeRef != null) {
            return typeRef.getPartsInfo();
        }
        return partsInfo;
    }

    public void setPartsInfo(List<CfgInfo> partsInfo) {
        this.partsInfo = partsInfo;
    }

    public CfgInfo getElementInfo() {
        if (typeRef != null) {
            return typeRef.getElementInfo();
        }
        return elementInfo;
    }

    public void setElementInfo(CfgInfo elementInfo) {
        this.elementInfo = elementInfo;
    }

    public Object getDefaultValue() {
        if (defaultValue == null && typeRef != null) {
            return typeRef.getDefaultValue();
        }
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isTextArea() {
        if (!textArea && typeRef != null) {
            return typeRef.isTextArea();
        }
        return textArea;
    }

    public void setTextArea(boolean textArea) {
        this.textArea = textArea;
    }

    public boolean isEmbed() {
        if (!embed && typeRef != null) {
            return typeRef.isEmbed();
        }
        return embed;
    }

    public void setEmbed(boolean embed) {
        this.embed = embed;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

}
