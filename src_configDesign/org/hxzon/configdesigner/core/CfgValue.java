package org.hxzon.configdesigner.core;

import java.util.ArrayList;
import java.util.List;

import org.hxzon.util.Dt;
import org.hxzon.util.StringParser;

public class CfgValue {

    private CfgInfo cfgInfo;
    private CfgValue parent;
    private String key;
    private Object value;
    private List<CfgValue> values;//对于列表，好维护序号变更，value也指向values

    public int indexCode() {
        return this.hashCode();
    }

    @Override
    public String toString() {
        return cfgInfo.getId() + "[" + cfgInfo.getLabel() + "]" + cfgInfo.getType().getId();
    }

    //====================
    public String getFullPath() {
        String parentPath = (parent == null) ? "" : parent.getFullPath() + "/";
        CfgType parentType = parent == null ? null : parent.getCfgInfo().getType();
        if (parentType == CfgType.List) {
            return parentPath + parent.getIndex(this);
        }
        if (parentType == CfgType.Map) {
            return parentPath + key;
        }
        return parentPath + getCfgInfo().getId();
    }

    //全路径
    public String getTitle() {
        String parentTitle = (parent == null) ? "" : parent.getTitle() + "/";
        CfgType parentType = parent == null ? null : parent.getCfgInfo().getType();
        if (parentType == CfgType.List) {
            return parentTitle + parent.getIndex(this);
        }
        if (parentType == CfgType.Map) {
            return parentTitle + key;
        }
        return parentTitle + cfgInfo.getLabelOrId();
    }

    //非全路径
    public String getLabel() {
        if (parent == null) {
            return cfgInfo.getLabel();
        }
        CfgType parentType = parent.getCfgInfo().getType();
        if (parentType.isStruct()) {
            return cfgInfo.getLabelOrId();
        }
        CfgType type = cfgInfo.getType();
        if (parentType == CfgType.List) {
            String index = String.valueOf(parent.getIndex(this));
            if (type != CfgType.Struct) {
                return index;
            }
            String labelKey = cfgInfo.getLabelKey();
            if (labelKey != null) {
                CfgValue labelValue = findCfgValue(labelKey);
                if (labelValue != null) {
                    return index + ":" + String.valueOf(labelValue.getValue());
                }
            }
            return index;
        }
        if (parentType == CfgType.ListMap) {
            String index = String.valueOf(parent.getIndex(this));
            String keyKey = cfgInfo.getKeyKey();
            if (keyKey != null) {
                CfgValue keyValue = findCfgValue(keyKey);
                if (keyValue != null) {
                    return index + ":" + String.valueOf(keyValue.getValue());
                }
            }
            return index;
        }
        if (parentType == CfgType.Map) {
            if (type != CfgType.Struct) {
                return key;
            }
            String labelKey = cfgInfo.getLabelKey();
            if (labelKey != null) {
                CfgValue labelValue = findCfgValue(labelKey);
                if (labelValue != null) {
                    return key + ":" + String.valueOf(labelValue.getValue());
                }
            }
            return key;
        }
        return cfgInfo.getLabel();//root
    }

    //================
    public boolean isElement() {
        if (parent == null) {
            return false;
        }
        return parent.getCfgInfo().getType().isElementContainer();
    }

    public boolean isMapElement() {
        CfgType parentType = (parent == null) ? null : parent.getCfgInfo().getType();
        return parentType == CfgType.Map;
    }

    public boolean isDeletable() {
        if (parent == null) {
            return false;
        }
        CfgType parentType = parent.getCfgInfo().getType();
        return parentType.isElementContainer() || cfgInfo.isOptional();
    }

    public boolean isNull() {
        if (cfgInfo.getType().isSimple()) {
            return value == null;
        }
        return values == null || values.isEmpty();
    }

    //
    private void checkType(CfgType type) {
        if (cfgInfo.getType() != type) {
            throw new RuntimeException("type error");
        }
    }

    private void checkType(boolean right) {
        if (!right) {
            throw new RuntimeException("type error");
        }
    }

    public void removeValue(CfgValue cfgValue) {
        checkType(cfgInfo.getType().isCombo());
        //cfgValue.setParent(null);//?方便回到上级，即便被删除
        values.remove(cfgValue);
    }

    public void addValue(CfgValue cfgValue) {
        checkType(cfgInfo.getType().isCombo());
        cfgValue.setParent(this);
        values.add(cfgValue);
    }

    public CfgValue getValue(int index) {
        checkType(CfgType.List);
        if (index < 0) {
            index = values.size() - index;
        }
        if (index < 0 || index > values.size()) {
            return null;
        }
        return values.get(index);
    }

    public CfgValue getValue(String key) {
        CfgType type = cfgInfo.getType();
        checkType(type.isStruct() || type == CfgType.Map);
        if (cfgInfo.getType().isStruct()) {
            for (CfgValue c : values) {
                if (c.getCfgInfo().getId().equals(key)) {
                    return c;
                }
            }
            return null;
        }
        for (CfgValue c : values) {
            if (c.getKey().equals(key)) {
                return c;
            }
        }
        return null;
    }

    public int getIndex(CfgValue c) {
        CfgType type = cfgInfo.getType();
        checkType(type == CfgType.List || type == CfgType.ListMap);
        return values.indexOf(c);
    }

    //=====================
    public CfgValue findCfgValue(String path) {
        StringParser parser = new StringParser(path);
        parser.setWithDefaultWps(true);
        parser.setWps(new char[] { '/' });
        return findCfgValue(parser);
    }

    private CfgValue findCfgValue(StringParser parser) {
        String token = parser.nextToken();
        if (token == null) {
            return this;
        }
        CfgType type = cfgInfo.getType();
        if (type.isStruct()) {
            for (CfgValue child : values) {
                String id = child.getCfgInfo().getId();
                if (id.equals(token)) {
                    return child.findCfgValue(parser);
                }
            }
        }
        if (type == CfgType.Map) {
            for (CfgValue child : values) {
                String key = child.getKey();
                if (key.equals(token)) {
                    return child.findCfgValue(parser);
                }
            }
        }
        if (type == CfgType.List) {
            int index = Dt.toInt(token, 0);
            CfgValue child = getValue(index);
            if (child != null) {
                return child.findCfgValue(parser);
            }
        }
        if (type == CfgType.ListMap) {
            for (CfgValue child : values) {
                CfgValue keyValue = child.findCfgValue(child.getCfgInfo().getKeyKey());
                if (keyValue != null && token.equals(Dt.toString(keyValue.getValue(), null))) {
                    return child.findCfgValue(parser);
                }
            }
        }
        return null;
    }

    //==================
    public CfgValue(CfgInfo cfgInfo) {
        this.cfgInfo = cfgInfo;
        if (cfgInfo.getType().isCombo()) {
            values = new ArrayList<CfgValue>();
            value = values;
        }
    }

    //
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<CfgValue> getValues() {
        return values;
    }

    public CfgInfo getCfgInfo() {
        return cfgInfo;
    }

    public CfgValue getParent() {
        return parent;
    }

    public void setParent(CfgValue parent) {
        this.parent = parent;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
