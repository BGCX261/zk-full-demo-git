package org.hxzon.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.beans.BeanUtils;

public class BeanUtil {
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        return BeanUtils.getPropertyDescriptors(clazz);
    }

    public static PropertyUtilsBean propertyUtils = new PropertyUtilsBean();

    public static PropertyDescriptor[] getPropertyDescriptors2(Class<?> clazz) {
        return propertyUtils.getPropertyDescriptors(clazz);
    }

    public static boolean hasAccessibleMethod(Class<?> clazz, Method method) {
        return MethodUtils.getAccessibleMethod(clazz, method) != null;
    }
}
