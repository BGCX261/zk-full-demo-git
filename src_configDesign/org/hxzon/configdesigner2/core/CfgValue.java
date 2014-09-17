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
        int parentType = parent == null ? 0 : parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_List) {
            return parentPath + parent.getIndex(this);
        }
        if (parentType == CfgInfo.Type_Map) {
            return parentPath + key;
        }
        return parentPath + getCfgInfo().getId();
    }

    public boolean isElement() {
        int parentType = (parent == null) ? 0 : parent.getCfgInfo().getType();
        return parentType == CfgInfo.Type_List || parentType == CfgInfo.Type_Map;
    }

    public boolean isMapElement() {
        int parentType = (parent == null) ? 0 : parent.getCfgInfo().getType();
        return parentType == CfgInfo.Type_Map;
    }

    //全路径
    public String getTitle() {
        String parentTitle = (parent == null) ? "" : parent.getTitle() + "/";
        int parentType = parent == null ? 0 : parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_List) {
            return parentTitle + parent.getIndex(this);
        }
        if (parentType == CfgInfo.Type_Map) {
            return parentTitle + key;
        }
        return parentTitle + cfgInfo.getLabelOrId();
    }

    //非全路径
    public String getLabel() {
        int parentType = (parent == null) ? 0 : parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_Struct) {
            return cfgInfo.getLabelOrId();
        }
        int type = cfgInfo.getType();
        if (parentType == CfgInfo.Type_List) {
            if (type != CfgInfo.Type_Struct) {
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
        if (parentType == CfgInfo.Type_Map) {
            if (type != CfgInfo.Type_Struct) {
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
        return cfgInfo.getId() + "[" + cfgInfo.getLabel() + "]" + cfgInfo.getTypeStr();
    }

    public boolean isSimpleValue() {
        return cfgInfo.getType() < CfgInfo.Type_Combo;
    }

    //
    private void checkType(int type) {
        if (cfgInfo.getType() != type) {
            throw new RuntimeException("type error");
        }
    }

    private void checkType(int... types) {
        int myType = cfgInfo.getType();
        for (int type : types) {
            if (myType == type) {
                return;
            }
        }
        throw new RuntimeException("type error");
    }

    public void removeValue(CfgValue cfgValue) {
        checkType(CfgInfo.Type_Struct, CfgInfo.Type_List, CfgInfo.Type_Map);
        //cfgValue.setParent(null);//?方便回到上级，即便被删除
        getChildren().remove(cfgValue);
    }

    public void addValue(CfgValue cfgValue) {
        checkType(CfgInfo.Type_Struct, CfgInfo.Type_List, CfgInfo.Type_Map);
        cfgValue.setParent(this);
        getChildren().add(cfgValue);
    }

    public CfgValue getValue(int index) {
        checkType(CfgInfo.Type_List);
        return getChildren().get(index);
    }

    public CfgValue getValue(String key) {
        checkType(CfgInfo.Type_Struct, CfgInfo.Type_Map);
        children = getChildren();
        if (cfgInfo.getType() == CfgInfo.Type_Struct) {
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
        checkType(CfgInfo.Type_List);
        return getChildren().indexOf(c);
    }

    public List<CfgValue> getChildren() {
        if (children == null) {
            children = new ArrayList<CfgValue>();
        }
        return children;
    }

    public boolean isNull() {
        if (cfgInfo.getType() < CfgInfo.Type_Combo) {
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
        int type = cfgInfo.getType();
        if (type == CfgInfo.Type_Struct) {
            for (CfgValue child : children) {
                String id = child.getCfgInfo().getId();
                if (id.equals(token)) {
                    return child.findCfgValue(parser);
                }
            }
        }
        if (type == CfgInfo.Type_Map) {
            for (CfgValue child : children) {
                String key = child.getKey();
                if (key.equals(token)) {
                    return child.findCfgValue(parser);
                }
            }
        }
        if (type == CfgInfo.Type_List) {
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
