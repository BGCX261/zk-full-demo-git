package org.hxzon.configdesigner.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CfgValue {

    private UUID uuid;
    private CfgInfo cfgInfo;
    private CfgValue parent;
    private String key;
    private Object value;

    public int indexCode() {
        return uuid.hashCode();
    }

    public String getFullPath() {
        String parentPath = (parent == null) ? "" : parent.getFullPath() + "/";
        int parentType = parent == null ? 0 : parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_List) {
            return parentPath + parent.getValues().indexOf(this);
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

    public String getTitle() {
        String parentTitle = (parent == null) ? "" : parent.getTitle() + "/";
        int parentType = parent == null ? 0 : parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_List) {
            return parentTitle + parent.getValues().indexOf(this);
        }
        if (parentType == CfgInfo.Type_Map) {
            return parentTitle + key;
        }
        return parentTitle + cfgInfo.getTitle();
    }

    public String getLabel() {
        int parentType = (parent == null) ? 0 : parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_List || parentType == CfgInfo.Type_Map) {
            CfgInfo eCfgInfo = parent.getCfgInfo().getParts().get(0);
            if (eCfgInfo.getType() < CfgInfo.Type_Combo) {
                return String.valueOf(value);
            }
            String labelKey = eCfgInfo.getLabelKey();
            List<CfgValue> valueParts = getValues();
            for (CfgValue v : valueParts) {
                if (v.getCfgInfo().getTitle().equals(labelKey)) {
                    return String.valueOf(v.getValue());
                }
            }
            return String.valueOf(valueParts.get(0).getValue());
        }
        return cfgInfo.getTitle();
    }

    public String toString() {
        return cfgInfo.getId() + "[" + cfgInfo.getLabel() + "]" + cfgInfo.getType();
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

    @SuppressWarnings("unchecked")
    public void removeValue(CfgValue cfgValue) {
        checkType(CfgInfo.Type_List, CfgInfo.Type_Map);
        int type = cfgInfo.getType();
        if (type == CfgInfo.Type_List) {
            ((List<CfgValue>) value).remove(cfgValue);
        } else if (type == CfgInfo.Type_Map) {
            ((Map<String, CfgValue>) value).remove(cfgValue.getKey());
        }
    }

    @SuppressWarnings("unchecked")
    public void removeValue(String key) {
        checkType(CfgInfo.Type_Map);
        ((Map<String, CfgValue>) value).remove(key);
    }

    public void addValue(CfgValue cfgValue) {
        checkType(CfgInfo.Type_Struct, CfgInfo.Type_List);
        cfgValue.setParent(this);
        if (value == null) {
            value = new ArrayList<CfgValue>();
        }
        getValues().add(cfgValue);
    }

    public void addValue(String key, CfgValue cfgValue) {
        checkType(CfgInfo.Type_Map);
        cfgValue.setParent(this);
        if (value == null) {
            value = new LinkedHashMap<String, CfgValue>();
        }
        cfgValue.setKey(key);
        getMapValues().put(key, cfgValue);
    }

    public List<CfgValue> getValues(boolean isSimple) {
        List<CfgValue> r = new ArrayList<CfgValue>();
        for (CfgValue v : getValues()) {
            if (v.isSimpleValue() == isSimple) {
                r.add(v);
            }
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    public List<CfgValue> getValues() {
        if (value == null) {
            return Collections.emptyList();
        }
        int type = cfgInfo.getType();
        if (type == CfgInfo.Type_List || type == CfgInfo.Type_Struct) {
            return (List<CfgValue>) value;
        }
        if (type == CfgInfo.Type_Map) {
            List<CfgValue> r = new ArrayList<CfgValue>();
            Map<String, CfgValue> mapsValue = (Map<String, CfgValue>) value;
            r.addAll(mapsValue.values());
            return r;
        }
        return Collections.emptyList();//null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, CfgValue> getMapValues() {
        if (value == null) {
            return Collections.emptyMap();
        }
        return (Map<String, CfgValue>) value;
    }

    //
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getStrValue() {
        return (String) value;
    }

    public void setStrValue(String value) {
        this.value = value;
    }

    public int getIntValue() {
        return (Integer) value;
    }

    public void setIntValue(int value) {
        this.value = value;
    }

    public double getRealValue() {
        return (Double) value;
    }

    public void setRealValue(double value) {
        this.value = value;
    }

    public boolean getBoolValue() {
        return (Boolean) value;
    }

    public void setBoolValue(boolean value) {
        this.value = value;
    }

    //
    public CfgValue(CfgInfo cfgInfo, UUID uuid) {
        this.cfgInfo = cfgInfo;
        this.uuid = uuid;
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
