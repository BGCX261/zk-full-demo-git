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
    private static Map<String, CfgInfo> entityTypeInfos = new HashMap<String, CfgInfo>();

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
        cfgInfo.setType(CfgType.Struct);
        List<CfgInfo> partsInfo = new ArrayList<CfgInfo>();
        for (Element ce : Dom4jUtil.getElements(e)) {
            CfgInfo partCfgInfo = toCfgInfo(ce);
            String typeDef = Dom4jUtil.getText(ce, "@typeDef");
            if ("true".equals(typeDef)) {
                typeInfos.put(partCfgInfo.getId(), partCfgInfo);
            } else {
                partsInfo.add(partCfgInfo);
                String idPrefix = Dom4jUtil.getText(ce, "@idPrefix");
                if (idPrefix != null) {
                    partCfgInfo.setIdPrefix(idPrefix);
                    entityTypeInfos.put(partCfgInfo.getId(), partCfgInfo);
                    //实体不能作为类型，否则导致实体在别处定义？
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
        CfgInfo typeRef = typeInfos.get(typeStr);
        if (typeRef != null) {
            cfgInfo.setTypeRef(typeRef);
            cfgInfo.setType(typeRef.getType());
        } else {
            CfgType type = CfgType.parseCfgType(typeStr);
            if (type == null) {
                type = CfgType.Struct;
            }
            cfgInfo.setType(type);
            if (type == CfgType.ViewStruct) {
                fillPartsInfo(cfgInfo, e);
                return cfgInfo;
            }
            //

            if (type == CfgType.Struct) {
                cfgInfo.setLabelKey(Dom4jUtil.getText(e, "@labelKey"));
                fillPartsInfo(cfgInfo, e);
            }
            if (type.isElementContainer()) {
                cfgInfo.setElementInfo(toCfgInfo(Dom4jUtil.getElement(e)));
            }
            if (type.isSimple()) {
                cfgInfo.setDefaultValue(converValue(type, Dom4jUtil.getText(e, "@value")));
            }
            if (type == CfgType.String) {
                String textArea = Dom4jUtil.getText(e, "@textarea");
                cfgInfo.setTextArea("true".equals(textArea));
            }

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

    public static Object converValue(CfgType type, String strValue) {
        if (type == CfgType.Boolean) {
            return "true".equals(strValue);
        }
        if (type == CfgType.Integer) {
            return strValue == null ? 0 : Integer.valueOf(strValue);
        }
        if (type == CfgType.Real) {
            return strValue == null ? 0.0 : Double.valueOf(strValue);
        }
        if (type == CfgType.String) {
            return strValue;
        }
        return Undefined;
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
        CfgType type = cfgInfo.getType();
        if (type.isSimple()) {
            return buildSimpleCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
        }
        if (type == CfgType.List) {
            return buildListCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
        }
        if (type == CfgType.Map) {
            return buildMapCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
        }
        if (type == CfgType.Struct) {
            return buildStructCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
        }
        if (type == CfgType.ViewStruct) {
            return buildViewStructCfgValue(cfgInfo, json, notNullValueDeep, nullValueDeep);
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

    private static CfgValue buildViewStructCfgValue(CfgInfo mapCfgInfo, Object mapJson, //
            int notNullValueDeep, int nullValueDeep) {//TODO
        CfgValue cfgValue = new CfgValue(mapCfgInfo, UUID.randomUUID());
        Object partJson = mapJson;//
        for (CfgInfo partInfo : mapCfgInfo.getPartsInfo()) {
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
        if (listCfgInfo.getType().isElementContainer()) {
            CfgInfo eCfgInfo = listCfgInfo.getElementInfo();
            CfgValue r = buildCfgValue(eCfgInfo, null, 1, deep);
            r.setParent(parent);
            return r;
        }
        throw new RuntimeException("type error");
    }

    public static Object toJson(CfgValue root, boolean trim) {
        CfgInfo cfgInfo = root.getCfgInfo();
        CfgType type = cfgInfo.getType();
        if (type == CfgType.Boolean) {
            boolean rb = Dt.toBoolean(root.getValue(), false);
            return (trim && !rb) ? Undefined : rb;
        }
        if (type == CfgType.Integer) {
            long rl = Dt.toLong(root.getValue(), 0);
            return (trim && rl == 0) ? Undefined : rl;
        }
        if (type == CfgType.Real) {
            double rr = Dt.toDouble(root.getValue(), 0);
            return (trim && rr == 0) ? Undefined : rr;
        }
        if (type == CfgType.String) {
            String rs = Dt.toString(root.getValue(), "");
            return (trim && rs.isEmpty()) ? Undefined : new MyString(rs);
        }
        if (type == CfgType.Struct) {
            JSONObject json = new JSONObject();
            for (CfgValue e : root.getChildren()) {
                if (e.getCfgInfo().getType() == CfgType.ViewStruct) {
                    for (CfgValue ee : e.getChildren()) {
                        Object ree = toJson(ee, trim);
                        if (!trim || ree != Undefined) {
                            json.put(ee.getCfgInfo().getId(), ree);
                        }
                    }
                    continue;
                }
                Object re = toJson(e, trim);
                if (!trim || re != Undefined) {
                    json.put(e.getCfgInfo().getId(), re);
                }
            }
            return (trim && json.length() == 0) ? Undefined : json;
        }
        if (type == CfgType.List) {
            JSONArray jsonA = new JSONArray();
            for (CfgValue e : root.getChildren()) {
                jsonA.put(toJson(e, trim));
            }
            return (trim && jsonA.length() == 0) ? Undefined : jsonA;
        }
        if (type == CfgType.Map) {
            JSONObject json = new JSONObject();
            for (CfgValue e : root.getChildren()) {
                json.put(e.getKey(), toJson(e, trim));
            }
            return (trim && json.length() == 0) ? Undefined : json;
        }
        return Undefined;
    }

    public static CfgValue copy(CfgValue origCfgValue) {
        CfgInfo cfgInfo = origCfgValue.getCfgInfo();
        CfgValue r = new CfgValue(cfgInfo, UUID.randomUUID());
        if (cfgInfo.getType().isCombo()) {
            for (CfgValue childCfgValue : origCfgValue.getChildren()) {
                r.addValue(copy(childCfgValue));
            }
        } else {
            r.setValue(origCfgValue.getValue());
        }
        if (origCfgValue.isMapElement()) {
            r.setKey(origCfgValue.getKey());
        }
        return r;
    }
}
