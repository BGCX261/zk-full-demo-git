package org.hxzon.jt.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.dom4j.Element;
import org.hxzon.util.Dom4jUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonConverter {
    private static final Object Null = null;//JSONObject.NULL;

    public static OutputSchemaInfo readOutputSchema(String xmlStr) {
        Element root = Dom4jUtil.getRoot(xmlStr);
        return toOutputSchemaInfo(root);
    }

    public static OutputSchemaInfo toOutputSchemaInfo(Element e) {
        OutputSchemaInfo typeInfo = new OutputSchemaInfo();
        typeInfo.setTargetType(e.getName());//myType
        typeInfo.setName(Dom4jUtil.getText(e, "@name"));
        typeInfo.setFname(Dom4jUtil.getText(e, "@fname"));//source key
        typeInfo.setFrom(Dom4jUtil.getText(e, "@from"));
        typeInfo.setNotrim(Dom4jUtil.getText(e, "@notrim"));
        typeInfo.setVtype(Dom4jUtil.getText(e, "@vtype"));
        if (!e.getName().equals("attr")) {
            List<OutputSchemaInfo> children = new ArrayList<OutputSchemaInfo>();
            List<Element> childrenEle = Dom4jUtil.getElements(e);
            for (Element childEle : childrenEle) {
                children.add(toOutputSchemaInfo(childEle));
            }
            typeInfo.setChildren(children);
        }
        return typeInfo;
    }

    public static Object convert(Object origObj, OutputSchemaInfo typeInfo) {
        if ("attr".equals(typeInfo.getTargetType())) {
            return convertAttr(origObj, typeInfo);
        } else if ("amap".equals(typeInfo.getTargetType())) {
            return convertAmap(origObj, typeInfo);
        } else if ("list".equals(typeInfo.getTargetType())) {
            return convertList(origObj, typeInfo);
        } else if ("map".equals(typeInfo.getTargetType())) {
            return convertMap(origObj, typeInfo);
        } else {
            return convertMap(origObj, typeInfo);//default is map
        }
        //throw new RuntimeException("myType error");
    }

    //targetType:map(amap),sourceType:map(amap),valueType:any
    public static Object convertAmap(Object origObj, OutputSchemaInfo typeInfo) {
        if (origObj == null) {
            return Null;
        }
        if (origObj instanceof JSONObject) {
            JSONObject origAmap = (JSONObject) origObj;
            JSONObject newAmap = new JSONObject();
            Set<String> keys = origAmap.keySet();
            for (String key : keys) {
                if ("map".equals(typeInfo.getVtype())) {
                    //origValue is map
                    JSONObject origValue = (JSONObject) origAmap.get(key);
                    JSONObject newValue = new JSONObject();
                    for (OutputSchemaInfo valueTypeInfo : typeInfo.getChildren()) {
                        Object origValueValue = origValue.get(valueTypeInfo.getFname());
                        Object newValueValue = convert(origValueValue, valueTypeInfo);
                        newValue.put(valueTypeInfo.getName(), newValueValue);
                    }
                    newAmap.put(key, newValue);
                } else {//vtype is simple type
                    Object origValue = origAmap.get(key);
                    Object newValue = convertAttr(origValue, typeInfo);
                    newAmap.put(key, newValue);
                }
            }
            return newAmap;
        }
        throw new RuntimeException("source type error");
    }

    //targetType:map,sourceType:map,valueType:any
    public static Object convertMap(Object origObj, OutputSchemaInfo typeInfo) {
        if (origObj == null) {
            return Null;
        } else if (origObj instanceof JSONObject) {
            JSONObject origMap = (JSONObject) origObj;
            JSONObject newMap = new JSONObject();
            for (OutputSchemaInfo valueTypeInfo : typeInfo.getChildren()) {
                Object origValue = origMap.get(valueTypeInfo.getFname());
                Object newValue = convert(origValue, valueTypeInfo);
                newMap.put(valueTypeInfo.getName(), newValue);
            }
            return newMap;
        }
        throw new RuntimeException("source type error");
    }

    //targetType:list,sourceType:list or map(amap),childType:any
    public static Object convertList(Object origObj, OutputSchemaInfo typeInfo) {
        if (origObj == null) {
            return Null;
        } else if (origObj instanceof JSONArray) {
            JSONArray origArray = (JSONArray) origObj;
            JSONArray newArray = new JSONArray();
            if ("map".equals(typeInfo.getVtype())) {
                for (Object origEle : origArray) {
                    JSONObject origEleMap = (JSONObject) origEle;
                    JSONObject newEleMap = new JSONObject();
                    for (OutputSchemaInfo eleValueTypeInfo : typeInfo.getChildren()) {
                        Object origEleValue = origEleMap.get(eleValueTypeInfo.getFname());
                        Object newEleValue = convert(origEleValue, eleValueTypeInfo);
                        newEleMap.put(eleValueTypeInfo.getName(), newEleValue);
                    }
                    newArray.add(newEleMap);
                }
            } else {//vtype is simple type
                for (Object origEle : origArray) {
                    Object newEle = convertAttr(origEle, typeInfo);
                    newArray.add(newEle);
                }
            }
            return newArray;
        } else if (origObj instanceof JSONObject) {
            JSONArray newArray = new JSONArray();
            JSONObject origAmap = (JSONObject) origObj;
            if (typeInfo.haveFrom()) {//vtype is simple type
                if ("key".equals(typeInfo.getFrom())) {
                    for (String key : origAmap.keySet()) {
                        newArray.add(key);
                    }
                } else if (typeInfo.getFrom().startsWith("value.")) {
                    for (String key : origAmap.keySet()) {
                        JSONObject origEleMap = (JSONObject) origAmap.get(key);
                        String valueKey = typeInfo.getFrom().substring(6);
                        Object newEle = convertAttr(origEleMap.get(valueKey), typeInfo);
                        newArray.add(newEle);
                    }
                }
            }
            return newArray;
        }
        throw new RuntimeException("source type error");
    }

    public static Object convertAttr(Object o, OutputSchemaInfo typeInfo) {
        if ("string".equals(typeInfo.getVtype())) {
            return toString(o, typeInfo);
        } else if ("integer".equals(typeInfo.getVtype())) {
            return toInteger(o, typeInfo);
        } else if ("double".equals(typeInfo.getVtype())) {
            return toDouble(o, typeInfo);
        } else if ("boolean".equals(typeInfo.getVtype())) {
            return toBoolean(o, typeInfo);
        }
        throw new RuntimeException("is not simple type");
    }

    //-------------------
    public static Object toString(Object o, OutputSchemaInfo typeInfo) {
        if (o instanceof String) {
            return o;//new MyString((String) o);
        } else if (o instanceof Number) {
            return o.toString();
        } else if (o instanceof Boolean) {
            return o.toString();
        } else if (typeInfo.haveDefault()) {
            return Null;
        }
        return Null;
    }

    public static Object toInteger(Object o, OutputSchemaInfo typeInfo) {
        if (o instanceof Long) {
            return o;
        } else if (o instanceof Number) {//int,double
            return ((Number) o).longValue();
        } else if (o instanceof String) {
            return Long.valueOf((String) o);
        } else if (typeInfo.haveDefault()) {
            return 0L;
        }
        return Null;
    }

    public static Object toDouble(Object o, OutputSchemaInfo typeInfo) {
        if (o instanceof Double) {
            return o;
        } else if (o instanceof Number) {//int,long
            return ((Number) o).doubleValue();
        } else if (o instanceof String) {
            return Double.valueOf((String) o);
        } else if (typeInfo.haveDefault()) {
            return 0.0;
        }
        return Null;
    }

    public static Object toBoolean(Object o, OutputSchemaInfo typeInfo) {
        if (o instanceof Boolean) {
            return o;
        } else if (o instanceof Number) {
            return ((Number) o).doubleValue() != 0;
        } else if (o instanceof String) {
            return Boolean.valueOf((String) o);
        } else if (typeInfo.haveDefault()) {
            return false;
        }
        return Null;
    }

    //--------------
    public static void main(String args[]) throws IOException {
        String jsonS = IOUtils.toString(JsonConverter.class.getResource("test/s18.o"), "utf8");
        Object json = JSON.parse(jsonS);
        String xmlStr = IOUtils.toString(JsonConverter.class.getResource("test/s18.xml"), "utf8");
        OutputSchemaInfo typeInfo = readOutputSchema(xmlStr);
        JSONObject newJson = (JSONObject) convert(json, typeInfo);
        System.out.println(JSON.toJSONString(newJson, true));
    }

}
