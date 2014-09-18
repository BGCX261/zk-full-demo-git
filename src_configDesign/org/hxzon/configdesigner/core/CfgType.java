package org.hxzon.configdesigner.core;

public class CfgType {

    private boolean isCombo;
    private boolean isStruct;
    private boolean isElementContainer;
    private String id;

    public static final CfgType String = new CfgType("s", false, false, false);
    public static final CfgType Integer = new CfgType("i", false, false, false);
    public static final CfgType Real = new CfgType("r", false, false, false);
    public static final CfgType Boolean = new CfgType("b", false, false, false);

    public static final CfgType Struct = new CfgType("st", true, true, false);
    public static final CfgType ViewStruct = new CfgType("vst", true, true, false);
    //viewStruct只能作为 struct 或 viewStruct 的子元素

    public static final CfgType Map = new CfgType("m", true, false, true);
    public static final CfgType List = new CfgType("l", true, false, true);

    public static final CfgType[] Types = new CfgType[] { //
    String, Integer, Real, Boolean, Struct, ViewStruct, Map, List };

    public static CfgType parseCfgType(String type) {
        for (CfgType cfgType : Types) {
            if (cfgType.getId().equals(type)) {
                return cfgType;
            }
        }
        return null;
    }

    public boolean isSimple() {
        return !isCombo();
    }

    //=================
    private CfgType(String id, boolean isCombo, boolean isStruct, boolean isElementContainer) {
        this.id = id;
        this.isCombo = isCombo;
        this.isStruct = isStruct;
        this.isElementContainer = isElementContainer;
    }

    public boolean isCombo() {
        return isCombo;
    }

    public boolean isStruct() {
        return isStruct;
    }

    public boolean isElementContainer() {
        return isElementContainer;
    }

    public String getId() {
        return id;
    }

}
