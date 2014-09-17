package org.hxzon.configdesigner2.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Element;
import org.hxzon.util.Dom4jUtil;
import org.hxzon.util.Dt;
import org.hxzon.util.json.JSONArray;
import org.hxzon.util.json.JSONObject;
import org.hxzon.util.json.MyString;

public class CfgParser {
    public static final Object Null = JSONObject.NULL;//有定义，值为null
    public static final Object Undefined = null;//未定义

    private static Map<String, CfgInfo> typeInfos = new HashMap<String, CfgInfo>();
    private static Map<String, CfgInfo> otypeInfos = new HashMap<String, CfgInfo>();

    public static CfgInfo parseSchema(String xmlStr) {
        Element root = Dom4jUtil.getRoot(xmlStr);
        return toCfgInfo_root(root);
    }

    private static CfgInfo toCfgInfo_root(Element e) {
        CfgInfo cfgInfo = new CfgInfo();
        String id = Dom4jUtil.getText(e, "@id");
        if (id == null) {
            id = e.getName();
        }
        cfgInfo.setId(id);
        cfgInfo.setLabel(Dom4jUtil.getText(e, "@label"));
        //
        cfgInfo.setType(CfgInfo.Type_Struct);
        List<CfgInfo> partsInfo = new ArrayList<CfgInfo>();
        for (Element ce : Dom4jUtil.getElements(e)) {
            CfgInfo partCfgInfo = toCfgInfo(ce);
            String typeDef = Dom4jUtil.getText(ce, "@typeDef");
            if ("true".equals(typeDef)) {
                typeInfos.put(partCfgInfo.getId(), partCfgInfo);
            } else {
                partsInfo.add(partCfgInfo);
                String idPrefix = Dom4jUtil.getText(e, "@idPrefix");
                if (idPrefix != null) {
                    partCfgInfo.setIdPrefix(idPrefix);
                    otypeInfos.put(partCfgInfo.getId(), partCfgInfo);
                }
            }
        }
        cfgInfo.setPartsInfo(partsInfo);

        return cfgInfo;
    }

    private static CfgInfo toCfgInfo(Element e) {
        CfgInfo cfgInfo = new CfgInfo();
        String id = Dom4jUtil.getText(e, "@id");
        if (id == null) {
            id = e.getName();
        }
        cfgInfo.setId(id);
        cfgInfo.setLabel(Dom4jUtil.getText(e, "@label"));

        String typeStr = Dom4jUtil.getText(e, "@type");
        if ("vst".equals(typeStr)) {
            cfgInfo.setType(CfgInfo.Type_ViewStruct);
            fillPartsInfo(cfgInfo, e);
            return cfgInfo;
        }
        //
        int type = 0;
        CfgInfo typeRef = null;

        if ("st".equals(typeStr) || typeStr == null) {
            type = CfgInfo.Type_Struct;
            cfgInfo.setLabelKey(Dom4jUtil.getText(e, "@labelKey"));
            fillPartsInfo(cfgInfo, e);
        } else if ("s".equals(typeStr)) {
            type = CfgInfo.Type_String;
        } else if ("i".equals(typeStr)) {
            type = CfgInfo.Type_Integer;
        } else if ("r".equals(typeStr)) {
            type = CfgInfo.Type_Real;
        } else if ("b".equals(typeStr)) {
            type = CfgInfo.Type_Boolean;
        } else if ("l".equals(typeStr)) {
            type = CfgInfo.Type_List;
            cfgInfo.setElementInfo(toCfgInfo(Dom4jUtil.getElement(e)));
        } else if ("m".equals(typeStr)) {
            type = CfgInfo.Type_Map;
            cfgInfo.setElementInfo(toCfgInfo(Dom4jUtil.getElement(e)));
        } else {
            typeRef = typeInfos.get(typeStr);
            if (typeRef != null) {
                cfgInfo.setTypeRef(typeRef);
                type = typeRef.getType();
            } else {
                throw new RuntimeException("type error:" + typeStr);
            }
        }
        cfgInfo.setType(type);
        //
        cfgInfo.setDefaultValue(converValue(type, Dom4jUtil.getText(e, "@value")));
        if (type == CfgInfo.Type_String) {
            String textArea = Dom4jUtil.getText(e, "@textarea");
            cfgInfo.setTextArea("true".equals(textArea));
        }
        String embed = Dom4jUtil.getText(e, "@embed");
        cfgInfo.setEmbed("true".equals(embed));
        return cfgInfo;
    }

    private static void fillPartsInfo(CfgInfo cfgInfo, Element e) {
        List<CfgInfo> parts = new ArrayList<CfgInfo>();
        List<Element> childrenEle = Dom4jUtil.getElements(e);
        for (Element childEle : childrenEle) {
            CfgInfo partInfo = toCfgInfo(childEle);
            parts.add(partInfo);
        }
        cfgInfo.setPartsInfo(parts);
    }

    public static int parseCfgType(String type) {
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
            return Undefined;
        }
    }

    //==============
    public static CfgValue buildCfgValue(CfgInfo cfgInfo, Object json, //
            int notNullValueDeep, int nullValueDeep) {
        if (notNullValueDeep <= 0) {
            return null;
        }
        if (json == null && nullValueDeep <= 0) {
            return null;
        }
        int type = cfgInfo.getType();
        if (type < CfgInfo.Type_Combo) {
            return buildSimpleCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
        } else if (type == CfgInfo.Type_List) {
            return buildListCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
        } else if (type == CfgInfo.Type_Map) {
            return buildMapCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
        } else if (type == CfgInfo.Type_Struct) {
            return buildStructCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
        }
        throw new RuntimeException("type error");
    }

    private static CfgValue buildSimpleCfgValue(CfgInfo cfgInfo, Object json, //
            int notNullValueDeep, int nullValueDeep) {
        CfgValue cfgValue = new CfgValue(cfgInfo, UUID.randomUUID());
        cfgValue.setValue(json != null ? json : cfgInfo.getDefaultValue());
        return cfgValue;
    }

    private static CfgValue buildStructCfgValue(CfgInfo mapCfgInfo, Object mapJson, //
            int notNullValueDeep, int nullValueDeep) {
        CfgValue cfgValue = new CfgValue(mapCfgInfo, UUID.randomUUID());
        for (CfgInfo partInfo : mapCfgInfo.getPartsInfo()) {
            Object partJson = null;
            if (mapJson != null && mapJson instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) mapJson;
                partJson = jsonObj.opt(partInfo.getId());
            }
            CfgValue part = buildCfgValue(partInfo, partJson, notNullValueDeep - 1, nullValueDeep - 1);
            if (part != null) {
                cfgValue.addValue(part);//add part
            }
        }
        return cfgValue;
    }

    private static CfgValue buildListCfgValue(CfgInfo listCfgInfo, Object listJson, //
            int notNullValueDeep, int nullValueDeep) {
        CfgValue listCfgValue = new CfgValue(listCfgInfo, UUID.randomUUID());
        CfgInfo eCfgInfo = listCfgInfo.getElementInfo();
        if (listJson != null && listJson instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) listJson;
            for (Object eleJson : jsonArray) {
                CfgValue e = buildCfgValue(eCfgInfo, eleJson, notNullValueDeep - 1, nullValueDeep - 1);
                listCfgValue.addValue(e);//add element
            }
        }
        return listCfgValue;
    }

    private static CfgValue buildMapCfgValue(CfgInfo mapsCfgInfo, Object mapsJson, //
            int notNullValueDeep, int nullValueDeep) {
        CfgValue mapsCfgValue = new CfgValue(mapsCfgInfo, UUID.randomUUID());
        CfgInfo eCfgInfo = mapsCfgInfo.getElementInfo();
        if (mapsJson != null && mapsJson instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) mapsJson;
            for (String key : jsonObj.keys()) {
                CfgValue e = buildCfgValue(eCfgInfo, jsonObj.get(key), notNullValueDeep - 1, nullValueDeep - 1);
                e.setKey(key);
                mapsCfgValue.addValue(e);//add element
            }
        }
        return mapsCfgValue;
    }

    public static CfgValue buildListElementCfgValue(CfgValue parent, int deep) {
        if (deep <= 0) {
            return null;
        }
        CfgInfo listCfgInfo = parent.getCfgInfo();
        if (listCfgInfo.getType() == CfgInfo.Type_List || listCfgInfo.getType() == CfgInfo.Type_Map) {
            CfgInfo eCfgInfo = listCfgInfo.getElementInfo();
            CfgValue r = buildCfgValue(eCfgInfo, null, 1, deep);
            r.setParent(parent);
            return r;
        }
        throw new RuntimeException("type error");
    }

    public static Object toJson(CfgValue root, boolean trim) {
        CfgInfo cfgInfo = root.getCfgInfo();
        int type = cfgInfo.getType();
        switch (type) {
        case CfgInfo.Type_Boolean:
            boolean rb = Dt.toBoolean(root.getValue(), false);
            return (trim && !rb) ? Undefined : rb;
        case CfgInfo.Type_Integer:
            long rl = Dt.toLong(root.getValue(), 0);
            return (trim && rl == 0) ? Undefined : rl;
        case CfgInfo.Type_Real:
            double rr = Dt.toDouble(root.getValue(), 0);
            return (trim && rr == 0) ? Undefined : rr;
        case CfgInfo.Type_String:
            String rs = Dt.toString(root.getValue(), "");
            return (trim && rs.isEmpty()) ? Undefined : new MyString(rs);
        case CfgInfo.Type_Struct: {
            JSONObject json = new JSONObject();
            for (CfgValue e : root.getChildren()) {
                Object re = toJson(e, trim);
                if (!trim || re != Undefined) {
                    json.put(e.getCfgInfo().getId(), re);
                }
            }
            return (trim && json.length() == 0) ? Undefined : json;
        }
        case CfgInfo.Type_List: {
            JSONArray jsonA = new JSONArray();
            for (CfgValue e : root.getChildren()) {
                jsonA.put(toJson(e, trim));
            }
            return (trim && jsonA.length() == 0) ? Undefined : jsonA;
        }
        case CfgInfo.Type_Map: {
            JSONObject json = new JSONObject();
            for (CfgValue e : root.getChildren()) {
                json.put(e.getKey(), toJson(e, trim));
            }
            return (trim && json.length() == 0) ? Undefined : json;
        }
        default:
            return Undefined;
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
        if (origCfgValue.isMapElement()) {
            r.setKey(origCfgValue.getKey());
        }
        return r;
    }
}
