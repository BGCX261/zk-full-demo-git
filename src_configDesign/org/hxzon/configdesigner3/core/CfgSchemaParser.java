package org.hxzon.configdesigner3.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.hxzon.util.Dom4jUtil;
import org.hxzon.util.json.JSONObject;

public class CfgSchemaParser {
    public static final Object Null = JSONObject.NULL;//有定义，值为null
    public static final Object Undefined = null;//未定义

    private static Map<String, CfgInfo> typeInfos = new HashMap<String, CfgInfo>();

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
        CfgInfo type = new CfgInfo();
        type.setType(CfgInfo.Type_Struct);
        List<CfgInfo> partsInfo = new ArrayList<CfgInfo>();
        for (Element ce : Dom4jUtil.getElements(e)) {
            CfgInfo partCfgInfo = toCfgInfo(ce);
            String typeDef = Dom4jUtil.getText(ce, "@typeDef");
            if ("true".equals(typeDef)) {
                typeInfos.put(partCfgInfo.getId(), partCfgInfo);
            }
            partsInfo.add(partCfgInfo);
        }

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
        String embed = Dom4jUtil.getText(e, "@embed");
        cfgInfo.setEmbed("true".equals(embed));
        //
        String typeStr = Dom4jUtil.getText(e, "@type");
        int type = 0;
        CfgInfo typeRef = null;
        if (typeStr == null) {
            type = CfgInfo.Type_Struct;
        }
        if ("s".equals(type)) {
            type = CfgInfo.Type_String;
        } else if ("i".equals(type)) {
            type = CfgInfo.Type_Integer;
        } else if ("r".equals(type)) {
            type = CfgInfo.Type_Real;
        } else if ("b".equals(type)) {
            type = CfgInfo.Type_Boolean;
        } else if ("st".equals(type)) {
            type = CfgInfo.Type_Struct;
            cfgInfo.setLabelKey(Dom4jUtil.getText(e, "@labelKey"));
            fillPartsInfo(cfgInfo, e);
        } else if ("l".equals(type)) {
            type = CfgInfo.Type_List;
            cfgInfo.setElementInfo(toCfgInfo(Dom4jUtil.getElement(e)));
        } else if ("m".equals(type)) {
            type = CfgInfo.Type_Map;
            cfgInfo.setElementInfo(toCfgInfo(Dom4jUtil.getElement(e)));
        } else {
            typeRef = typeInfos.get(type);
        }
        if (typeRef != null) {
            cfgInfo.setTypeRef(typeRef);
            type = typeRef.getType();
        }
        cfgInfo.setType(type);
        //
        cfgInfo.setDefaultValue(converValue(type, Dom4jUtil.getText(e, "@value")));
        if (type == CfgInfo.Type_String) {
            String textArea = Dom4jUtil.getText(e, "@textarea");
            cfgInfo.setTextArea("true".equals(textArea));
        }
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
}
