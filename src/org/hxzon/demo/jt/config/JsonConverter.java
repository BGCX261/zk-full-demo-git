package org.hxzon.demo.jt.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.dom4j.Element;
import org.hxzon.util.Dom4jUtil;

public class JsonConverter {
    private static final Object Null = JSONObject.NULL;

    public static TypeInfo readSchema(String xmlStr) {
        Element root = Dom4jUtil.getRoot(xmlStr);
        return toTypeInfo(root);
    }

    public static TypeInfo toTypeInfo(Element e) {
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.setTargetType(e.getName());//myType
        typeInfo.setName(Dom4jUtil.getText(e, "@name"));
        typeInfo.setFname(Dom4jUtil.getText(e, "@fname"));
        typeInfo.setFrom(Dom4jUtil.getText(e, "@from"));
        typeInfo.setNotrim(Dom4jUtil.getText(e, "@notrim"));
        typeInfo.setVtype(Dom4jUtil.getText(e, "@vtype"));
        if (!e.getName().equals("attr")) {
            List<TypeInfo> children = new ArrayList<TypeInfo>();
            List<Element> childrenEle = Dom4jUtil.getElements(e);
            for (Element childEle : childrenEle) {
                children.add(toTypeInfo(childEle));
            }
            typeInfo.setChildren(children);
        }
        return typeInfo;
    }

    public static Object convert(Object origObj, TypeInfo typeInfo) {
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
    public static Object convertAmap(Object origObj, TypeInfo typeInfo) {
        if (origObj == null) {
            return Null;
        }
        if (origObj instanceof JSONObject) {
            JSONObject origAmap = (JSONObject) origObj;
            JSONObject newAmap = new JSONObject();
            Set<String> keys = origAmap.keys();
            for (String key : keys) {
                if ("map".equals(typeInfo.getVtype())) {
                    //origValue is map
                    JSONObject origValue = (JSONObject) origAmap.opt(key);
                    JSONObject newValue = new JSONObject();
                    for (TypeInfo valueTypeInfo : typeInfo.getChildren()) {
                        Object origValueValue = origValue.opt(valueTypeInfo.getFname());
                        Object newValueValue = convert(origValueValue, valueTypeInfo);
                        newValue.put(valueTypeInfo.getName(), newValueValue);
                    }
                    newAmap.put(key, newValue);
                } else {//vtype is simple type
                    Object origValue = origAmap.opt(key);
                    Object newValue = convertAttr(origValue, typeInfo);
                    newAmap.put(key, newValue);
                }
            }
            return newAmap;
        }
        throw new RuntimeException("source type error");
    }

    //targetType:map,sourceType:map,valueType:any
    public static Object convertMap(Object origObj, TypeInfo typeInfo) {
        if (origObj == null) {
            return Null;
        } else if (origObj instanceof JSONObject) {
            JSONObject origMap = (JSONObject) origObj;
            JSONObject newMap = new JSONObject();
            for (TypeInfo valueTypeInfo : typeInfo.getChildren()) {
                Object origValue = origMap.opt(valueTypeInfo.getFname());
                Object newValue = convert(origValue, valueTypeInfo);
                newMap.put(valueTypeInfo.getName(), newValue);
            }
            return newMap;
        }
        throw new RuntimeException("source type error");
    }

    //targetType:list,sourceType:list or map(amap),childType:any
    public static Object convertList(Object origObj, TypeInfo typeInfo) {
        if (origObj == null) {
            return Null;
        } else if (origObj instanceof JSONArray) {
            JSONArray origArray = (JSONArray) origObj;
            JSONArray newArray = new JSONArray();
            if ("map".equals(typeInfo.getVtype())) {
                for (Object origEle : origArray) {
                    JSONObject origEleMap = (JSONObject) origEle;
                    JSONObject newEleMap = new JSONObject();
                    for (TypeInfo eleValueTypeInfo : typeInfo.getChildren()) {
                        Object origEleValue = origEleMap.opt(eleValueTypeInfo.getFname());
                        Object newEleValue = convert(origEleValue, eleValueTypeInfo);
                        newEleMap.put(eleValueTypeInfo.getName(), newEleValue);
                    }
                    newArray.put(newEleMap);
                }
            } else {//vtype is simple type
                for (Object origEle : origArray) {
                    Object newEle = convertAttr(origEle, typeInfo);
                    newArray.put(newEle);
                }
            }
            return newArray;
        } else if (origObj instanceof JSONObject) {
            JSONArray newArray = new JSONArray();
            JSONObject origAmap = (JSONObject) origObj;
            if (typeInfo.haveFrom()) {//vtype is simple type
                if ("key".equals(typeInfo.getFrom())) {
                    for (String key : origAmap.keys()) {
                        newArray.put(key);
                    }
                } else if (typeInfo.getFrom().startsWith("value.")) {
                    for (String key : origAmap.keys()) {
                        JSONObject origEleMap = (JSONObject) origAmap.get(key);
                        String valueKey = typeInfo.getFrom().substring(6);
                        Object newEle = convertAttr(origEleMap.opt(valueKey), typeInfo);
                        newArray.put(newEle);
                    }
                }
            }
            return newArray;
        }
        throw new RuntimeException("source type error");
    }

    public static Object convertAttr(Object o, TypeInfo typeInfo) {
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
    public static Object toString(Object o, TypeInfo typeInfo) {
        if (o instanceof String) {
            return new MyString((String) o);
        } else if (o instanceof Number) {
            return o.toString();
        } else if (o instanceof Boolean) {
            return o.toString();
        } else if (typeInfo.haveDefault()) {
            return Null;
        }
        return Null;
    }

    public static Object toInteger(Object o, TypeInfo typeInfo) {
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

    public static Object toDouble(Object o, TypeInfo typeInfo) {
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

    public static Object toBoolean(Object o, TypeInfo typeInfo) {
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
        String jsonS = FileUtils.readFileToString(new File("D:/test/s18.o"), "utf8");
        JSONObject json = new JSONObject(jsonS);
        String xmlStr = FileUtils.readFileToString(new File("D:/test/s18.xml"), "utf8");
        TypeInfo typeInfo = readSchema(xmlStr);
        JSONObject newJson = (JSONObject) convert(json, typeInfo);
        System.out.println(newJson.toString(false));
    }

}
