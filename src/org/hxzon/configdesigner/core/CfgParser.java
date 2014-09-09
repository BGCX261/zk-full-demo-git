package org.hxzon.configdesigner.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.dom4j.Element;
import org.hxzon.demo.jt.config.MyString;
import org.hxzon.util.Dom4jUtil;
import org.hxzon.util.Dt;

public class CfgParser {
    //private static final Object Null = JSONObject.NULL;
    private boolean withNull;//

    public static CfgInfo parseSchema(String xmlStr) {
        Element root = Dom4jUtil.getRoot(xmlStr);
        return toCfgInfo(root);
    }

    private static CfgInfo toCfgInfo(Element e) {
        CfgInfo cfgInfo = new CfgInfo();
        String id = Dom4jUtil.getText(e, "@id");
        if (id == null) {
            id = e.getName();
        }
        cfgInfo.setId(id);
        cfgInfo.setLabel(Dom4jUtil.getText(e, "@label"));
        cfgInfo.setSource(Dom4jUtil.getText(e, "@source"));
        String textArea = Dom4jUtil.getText(e, "@textarea");
        cfgInfo.setTextArea("true".equals(textArea));
        String embed = Dom4jUtil.getText(e, "@embed");
        cfgInfo.setEmbed("true".equals(embed));
        //
        int type = parseCfgType(Dom4jUtil.getText(e, "@type"));
        cfgInfo.setType(type);
        cfgInfo.setDefaultValue(converValue(type, Dom4jUtil.getText(e, "@value")));
        if (type == CfgInfo.Type_Struct) {
            fillPartsInfo(cfgInfo, e);
        }
        if (type == CfgInfo.Type_List || type == CfgInfo.Type_Map) {
            int etype = parseCfgType(Dom4jUtil.getText(e, "@etype"));
            CfgInfo eCfgInfo = new CfgInfo();
            eCfgInfo.setLabel(cfgInfo.getLabel());
            eCfgInfo.setId(cfgInfo.getId());//just for show label
            eCfgInfo.setLabelKey(Dom4jUtil.getText(e, "@labelKey"));
            eCfgInfo.setType(etype);
            fillPartsInfo(eCfgInfo, e);
            //
            List<CfgInfo> parts = new ArrayList<CfgInfo>(1);
            parts.add(eCfgInfo);
            cfgInfo.setParts(parts);
        }
        return cfgInfo;
    }

    private static void fillPartsInfo(CfgInfo cfgInfo, Element e) {
        List<CfgInfo> parts = new ArrayList<CfgInfo>();
        List<Element> childrenEle = Dom4jUtil.getElements(e);
        for (Element childEle : childrenEle) {
            CfgInfo childInfo = toCfgInfo(childEle);
            parts.add(childInfo);
        }
        cfgInfo.setParts(parts);
    }

    private static int parseCfgType(String type) {
        if ("s".equals(type)) {
            return CfgInfo.Type_String;
        } else if ("i".equals(type)) {
            return CfgInfo.Type_Integer;
        } else if ("r".equals(type)) {
            return CfgInfo.Type_Real;
        } else if ("b".equals(type)) {
            return CfgInfo.Type_Boolean;
        } else if ("st".equals(type)) {
            return CfgInfo.Type_Struct;
        } else if ("m".equals(type)) {
            return CfgInfo.Type_Map;
        } else if ("l".equals(type)) {
            return CfgInfo.Type_List;
        }
        return CfgInfo.Type_Struct;
    }

    public CfgValue buildCfgValue(CfgInfo cfgInfo, Object json) {
        if (!withNull && json == null) {
            return null;
        }
        return buildCfgValue_withNull(cfgInfo, json);
    }

    public CfgValue buildCfgValue_withNull(CfgInfo cfgInfo, Object json) {
        int type = cfgInfo.getType();
        if (type < CfgInfo.Type_Combo) {
            CfgValue cfgValue = new CfgValue(cfgInfo, UUID.randomUUID());
            cfgValue.setValue(json != null ? json : cfgInfo.getDefaultValue());
            return cfgValue;
        } else if (type == CfgInfo.Type_List) {
            return buildListCfgValue(cfgInfo, json);
        } else if (type == CfgInfo.Type_Map) {
            return buildMapCfgValue(cfgInfo, json);
        } else if (type == CfgInfo.Type_Struct) {
            return buildStructCfgValue(cfgInfo, json);
        }
        throw new RuntimeException("type error");
    }

    private CfgValue buildStructCfgValue(CfgInfo mapCfgInfo, Object mapJson) {
        CfgValue cfgValue = new CfgValue(mapCfgInfo, UUID.randomUUID());
        for (CfgInfo partInfo : mapCfgInfo.getParts()) {
            Object partJson = null;
            if (mapJson != null && mapJson instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) mapJson;
                partJson = jsonObj.get(partInfo.getId());
            }
            CfgValue part = buildCfgValue(partInfo, partJson);
            if (part != null) {
                cfgValue.addValue(part);//add part
            }
        }
        return cfgValue;
    }

    private CfgValue buildListCfgValue(CfgInfo listCfgInfo, Object listJson) {
        CfgValue listCfgValue = new CfgValue(listCfgInfo, UUID.randomUUID());
        CfgInfo eCfgInfo = listCfgInfo.getParts().get(0);
        if (listJson != null && listJson instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) listJson;
            for (Object eleJson : jsonArray) {
                listCfgValue.addValue(buildCfgValue(eCfgInfo, eleJson));//add element
            }
        }
        return listCfgValue;
    }

    private CfgValue buildMapCfgValue(CfgInfo mapsCfgInfo, Object mapsJson) {
        CfgValue mapsCfgValue = new CfgValue(mapsCfgInfo, UUID.randomUUID());
        CfgInfo eCfgInfo = mapsCfgInfo.getParts().get(0);
        if (mapsJson != null && mapsJson instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) mapsJson;
            for (String key : jsonObj.keys()) {
                CfgValue e = buildCfgValue(eCfgInfo, jsonObj.get(key));
                e.setKey(key);
                mapsCfgValue.addValue(e);//add element
            }
        }
        return mapsCfgValue;
    }

    public CfgValue buildListElementCfgValue(CfgValue parent) {
        CfgInfo listCfgInfo = parent.getCfgInfo();
        if (listCfgInfo.getType() == CfgInfo.Type_List || listCfgInfo.getType() == CfgInfo.Type_Map) {
            CfgInfo eCfgInfo = listCfgInfo.getParts().get(0);
            CfgValue r = buildCfgValue_withNull(eCfgInfo, null);
            r.setParent(parent);
            return r;
        }
        throw new RuntimeException("type error");
    }

    public static Object toJson(CfgValue root) {
        CfgInfo cfgInfo = root.getCfgInfo();
        int type = cfgInfo.getType();
        switch (type) {
        case CfgInfo.Type_Boolean:
            return Dt.toBoolean(root.getValue(), false);
        case CfgInfo.Type_Integer:
            return Dt.toLong(root.getValue(), 0);
        case CfgInfo.Type_Real:
            return Dt.toDouble(root.getValue(), 0);
        case CfgInfo.Type_String:
            return new MyString(Dt.toString(root.getValue(), ""));
        case CfgInfo.Type_Struct: {
            JSONObject json = new JSONObject();
            for (CfgValue e : root.getChildren()) {
                json.put(e.getCfgInfo().getId(), toJson(e));
            }
            return json;
        }
        case CfgInfo.Type_List: {
            JSONArray jsonA = new JSONArray();
            for (CfgValue e : root.getChildren()) {
                jsonA.put(toJson(e));
            }
            return jsonA;
        }
        case CfgInfo.Type_Map: {
            JSONObject json = new JSONObject();
            for (CfgValue e : root.getChildren()) {
                json.put(e.getKey(), toJson(e));
            }
            return json;
        }
        default:
            return null;
        }
    }

    public static Object converValue(int type, String strValue) {
        switch (type) {
        case CfgInfo.Type_Boolean:
            return "true".equals(strValue);
        case CfgInfo.Type_Integer:
            return strValue == null ? 0 : Integer.valueOf(strValue);
        case CfgInfo.Type_Real:
            return strValue == null ? 0.0 : Double.valueOf(strValue);
        case CfgInfo.Type_String:
            return strValue;
        default:
            return null;
        }
    }

    public static CfgValue copy(CfgValue origCfgValue) {
        CfgInfo cfgInfo = origCfgValue.getCfgInfo();
        CfgValue r = new CfgValue(cfgInfo, UUID.randomUUID());
        switch (cfgInfo.getType()) {
        case CfgInfo.Type_List:
        case CfgInfo.Type_Struct:
        case CfgInfo.Type_Map: {
            for (CfgValue childCfgValue : origCfgValue.getChildren()) {
                r.addValue(copy(childCfgValue));
            }
            break;
        }
        default:
            r.setValue(origCfgValue.getValue());
        }
        return r;
    }

    //=============
    public static Object convertValue(Object o, CfgInfo cfgInfo) {
        if (cfgInfo.getType() == CfgInfo.Type_String) {
            return toString(o, cfgInfo);
        } else if (cfgInfo.getType() == CfgInfo.Type_Integer) {
            return toInteger(o, cfgInfo);
        } else if (cfgInfo.getType() == CfgInfo.Type_Real) {
            return toDouble(o, cfgInfo);
        } else if (cfgInfo.getType() == CfgInfo.Type_Boolean) {
            return toBoolean(o, cfgInfo);
        }
        throw new RuntimeException("is not simple type");
    }

    //-------------------
    public static Object toString(Object o, CfgInfo cfgInfo) {
        if (o instanceof String) {
            return new MyString((String) o);
        } else if (o instanceof Number) {
            return o.toString();
        } else if (o instanceof Boolean) {
            return o.toString();
        }
        return cfgInfo.getDefaultValue();
    }

    public static Object toInteger(Object o, CfgInfo cfgInfo) {
        if (o instanceof Long) {
            return o;
        } else if (o instanceof Number) {//int,double
            return ((Number) o).longValue();
        } else if (o instanceof String) {
            return Long.valueOf((String) o);
        }
        return cfgInfo.getDefaultValue();
    }

    public static Object toDouble(Object o, CfgInfo cfgInfo) {
        if (o instanceof Double) {
            return o;
        } else if (o instanceof Number) {//int,long
            return ((Number) o).doubleValue();
        } else if (o instanceof String) {
            return Double.valueOf((String) o);
        }
        return cfgInfo.getDefaultValue();
    }

    public static Object toBoolean(Object o, CfgInfo cfgInfo) {
        if (o instanceof Boolean) {
            return o;
        } else if (o instanceof Number) {
            return ((Number) o).doubleValue() != 0;
        } else if (o instanceof String) {
            return Boolean.valueOf((String) o);
        }
        return cfgInfo;
    }

    //=======
    public boolean isWithNull() {
        return withNull;
    }

    public void setWithNull(boolean withNull) {
        this.withNull = withNull;
    }

}
