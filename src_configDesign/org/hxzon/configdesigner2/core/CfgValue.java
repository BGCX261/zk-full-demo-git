package org.hxzon.configdesigner2.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hxzon.util.Dt;
import org.hxzon.util.StringParser;

public class CfgValue {

    private UUID uuid;
    private CfgInfo cfgInfo;
    private CfgValue parent;
    private String key;
    private List<CfgValue> children;//对于列表，好维护序号变更
    private Object value;

    public int indexCode() {
        return uuid.hashCode();
    }

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
        CfgType parentType = (parent == null) ? null : parent.getCfgInfo().getType();
        if (parentType == CfgType.Struct) {
            return cfgInfo.getLabelOrId();
        }
        CfgType type = cfgInfo.getType();
        if (parentType == CfgType.List) {
            if (type != CfgType.Struct) {
                return String.valueOf(parent.getIndex(this));
            }
            String labelKey = cfgInfo.getLabelKey();
            if (labelKey != null) {
                CfgValue labelValue = getValue(labelKey);
                if (labelValue != null) {
                    return String.valueOf(labelValue.getValue());
                }
            }
            return String.valueOf(parent.getIndex(this));
        }
        if (parentType == CfgType.Map) {
            if (type != CfgType.Struct) {
                return key;
            }
            String labelKey = cfgInfo.getLabelKey();
            if (labelKey != null) {
                CfgValue labelValue = getValue(labelKey);
                if (labelValue != null) {
                    return String.valueOf(labelValue.getValue());
                }
            }
            return key;
        }
        return cfgInfo.getLabel();//root
    }

    @Override
    public String toString() {
        return cfgInfo.getId() + "[" + cfgInfo.getLabel() + "]" + cfgInfo.getType().getId();
    }

    public boolean isSimpleValue() {
        return cfgInfo.getType().isSimple();
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
        getChildren().remove(cfgValue);
    }

    public void addValue(CfgValue cfgValue) {
        checkType(cfgInfo.getType().isCombo());
        cfgValue.setParent(this);
        getChildren().add(cfgValue);
    }

    public CfgValue getValue(int index) {
        checkType(CfgType.List);
        return getChildren().get(index);
    }

    public CfgValue getValue(String key) {
        CfgType type = cfgInfo.getType();
        checkType(type.isStruct() || type == CfgType.Map);
        children = getChildren();
        if (cfgInfo.getType() == CfgType.Struct) {
            for (CfgValue c : children) {
                if (c.getCfgInfo().getId().equals(key)) {
                    return c;
                }
            }
            return null;
        }
        for (CfgValue c : children) {
            if (c.getKey().equals(key)) {
                return c;
            }
        }
        return null;
    }

    public int getIndex(CfgValue c) {
        checkType(CfgType.List);
        return getChildren().indexOf(c);
    }

    public List<CfgValue> getChildren() {
        if (children == null) {
            children = new ArrayList<CfgValue>();
        }
        return children;
    }

    public boolean isNull() {
        if (cfgInfo.getType().isSimple()) {
            return value == null;
        }
        return children == null || children.isEmpty();
    }

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
        if (type == CfgType.Struct) {
            for (CfgValue child : children) {
                String id = child.getCfgInfo().getId();
                if (id.equals(token)) {
                    return child.findCfgValue(parser);
                }
            }
        }
        if (type == CfgType.Map) {
            for (CfgValue child : children) {
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
        return null;
    }

    //==================
    public CfgValue(CfgInfo cfgInfo, UUID uuid) {
        this.cfgInfo = cfgInfo;
        this.uuid = uuid;
    }

    //
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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
