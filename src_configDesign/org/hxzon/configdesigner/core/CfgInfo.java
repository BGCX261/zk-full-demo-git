package org.hxzon.configdesigner.core;

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
    //简单类型
    private Object defaultValue;
    private CfgValueValidator validator;
    //实体
    private String idPrefix;
    private String groupKey;
    //三态值，默认为false
    private String textArea;//use textArea or textInput
    private String embed;
    private String optional;

    public static final String True = "true";
    public static final String False = "false";

    //====================
    @Override
    public String toString() {
        return getLabelOrId() + "," + type.getId();
    }

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
        if (type.isStruct()) {
            for (CfgInfo part : getPartsInfo()) {
                if (part.getId().equals(token)) {
                    return part.findCfgInfo(parser);
                }
            }
        }
        if (type.isElementContainer()) {
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

    public CfgValueValidator getValidator() {
        if (validator == null && typeRef != null) {
            return typeRef.getValidator();
        }
        return validator;
    }

    public void setValidator(CfgValueValidator validator) {
        this.validator = validator;
    }

    //========================
    public String getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    //=========================
    public boolean isTextArea() {
        if (textArea == null && typeRef != null) {
            return typeRef.isTextArea();
        }
        return textArea == True;
    }

    public void setTextArea(String textArea) {
        this.textArea = textArea;
    }

    public boolean isEmbed() {
        if (embed == null && typeRef != null) {
            return typeRef.isEmbed();
        }
        return embed == True;
    }

    public void setEmbed(String embed) {
        this.embed = embed;
    }

    public boolean isOptional() {
        if (optional == null && typeRef != null) {
            return typeRef.isOptional();
        }
        return optional == True;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }
}
