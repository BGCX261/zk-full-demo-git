package org.hxzon.configdesigner2.core;

import java.util.List;

import org.hxzon.util.StringParser;

public class CfgInfo {

    private String id;
    private CfgType type;
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
        if (type == CfgType.Struct) {
            for (CfgInfo part : getPartsInfo()) {
                if (part.getId().equals(token)) {
                    return part.findCfgInfo(parser);
                }
            }
        }
        if (type == CfgType.List || type == CfgType.Map) {
            if ("e".equals(token)) {
                return getElementInfo().findCfgInfo(parser);
            }
        }
        return null;
    }

    //====================
    public CfgInfo() {
    }

    public CfgInfo(CfgType type) {
        setType(type);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CfgType getType() {
        return type;
    }

    public void setType(CfgType type) {
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
