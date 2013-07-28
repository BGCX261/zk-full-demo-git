package org.hxzon.util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.Node;
import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OgnlUtil {
    protected static final Logger logger = LoggerFactory.getLogger(OgnlUtil.class);
    private static final Map<String, Node> expressionCache = new HashMap<String, Node>();

    public static Node getExpression(String ex) {
        ex = StringUtils.trim(ex);
        Node expression = expressionCache.get(ex);
        if (expression == null) {
            try {
                expression = (Node) Ognl.parseExpression(ex);
                expressionCache.put(ex, expression);
                logger.trace(ex);
            } catch (OgnlException e) {
                logger.error(e.getMessage());
            }
        }
        return expression;
    }

    private static Object doGetValue(Object root, Node tree) {
        if (root == null || tree == null) {
            return null;
        }
        Object result = null;
        try {
            result = Ognl.getValue(tree, root);
        } catch (OgnlException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    private static Object doGetValue(Object root, String ex) {
        return doGetValue(root, getExpression(ex));
    }

    private static boolean doSetValue(Object root, Node tree, Object value) {
        if (root == null || tree == null) {
            logger.error("root or tree is null");
            return false;
        }
        try {
            Ognl.setValue(tree, root, value);
        } catch (OgnlException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private static boolean doSetValue(Object root, String ex, Object value) {
        return doSetValue(root, getExpression(ex), value);
    }

    // --------------------------

    public static Object getValue(Object o, String ex) {
        return doGetValue(o, ex);
    }

    public static String getStringValueOrEmpty(Object o, String ex) {
        Object result = doGetValue(o, ex);
        if (result == null) {
            return "";
        }
        if (result instanceof String) {
            return ((String) result).trim();
        }
        return result.toString();
    }

    public static String getStringValueEscape(Object o, String ex) {
        return escape(getStringValueOrEmpty(o, ex));
    }

    public static String escape(String orig) {
        // orig = StringEscapeUtils.escapeHtml(orig);
        // orig = StringEscapeUtils.escapeJavaScript(orig);
        orig = StringEscapeUtils.escapeSql(orig);
        return orig;
    }

    // set value---------------------
    public static boolean setValue(Object o, String ex, Object value) {
        return doSetValue(o, ex, value);
    }

    public static boolean setStringValueOrEmpty(Object o, String ex, String value) {
        if (value == null) {
            value = "";
        }
        return doSetValue(o, ex, value);
    }

    public static Class<?> getClass(Object o, String ex) {
        Object value = getValue(o, ex);
        return value == null ? null : value.getClass();
    }

    // public static Object[][] get(List list,List<String> ex){
    // int row=list.size();
    // int col=ex.size();
    // Object[][] objs=new Object[row][col];
    // for(int i=0;i<row;i++){
    // for(int j=0;j<col;j++){
    // objs[i][j]=OgnlUtils.getValue(list.get(i), ex.get(i));
    // }
    // }
    // return objs;
    // }
    //    
    // public static Vector convertToVector(List list,List<String> ex){
    // int row=list.size();
    // int col=ex.size();
    // Vector v=new Vector(row);
    // for(int i=0;i<row;i++){
    // Vector vi=new Vector(col);
    // for(int j=0;j<col;j++){
    // vi.add(OgnlUtils.getValue(list.get(i), ex.get(i)));
    // }
    // v.add(vi);
    // }
    // return v;
    // }

    public static List<String> getAllProperty(Class<?> clazz) {
        PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(clazz);
        List<String> propertyName = new ArrayList<String>();
        for (int i = 0; i < descriptors.length; i++) {
            String name = descriptors[i].getName();
            if (BeanUtil.hasAccessibleMethod(clazz, descriptors[i].getReadMethod())) {
                propertyName.add(name);
            }
        }
        return propertyName;
    }

    public static List<String> getAllStringProperty(Class<?> clazz) {
        PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(clazz);
        List<String> propertyName = new ArrayList<String>();
        for (int i = 0; i < descriptors.length; i++) {
            String name = descriptors[i].getName();
            if (BeanUtil.hasAccessibleMethod(clazz, descriptors[i].getReadMethod()) && descriptors[i].getPropertyType().equals(String.class)) {
                propertyName.add(name);
            }
        }
        propertyName.remove("id");
        return propertyName;
    }

    public static void escapeStringProperty(Object o) {
        List<String> properties = getAllStringProperty(o.getClass());
        String value;
        for (String property : properties) {
            value = escape(getStringValueOrEmpty(o, property));
            setValue(o, property, value);
        }
    }

}
