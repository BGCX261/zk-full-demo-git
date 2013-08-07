package org.hxzon.project;

import java.util.List;

import org.hibernate.transform.ResultTransformer;

public class MyHibernateResultTransformer<M> implements ResultTransformer {

    private static final long serialVersionUID = 1L;

    @Override
    public M transformTuple(Object[] tuple, String[] aliases) {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
        return collection;
    }

    @SuppressWarnings("unchecked")
    protected <C> C getColumnValue(Class<C> clazz, String name, Object[] tuple, String[] aliases) {
        int index = 0;
        for (String aliase : aliases) {
            if (aliase.equalsIgnoreCase(name)) {
                return (C) tuple[index];
            }
            index++;
        }
        return null;
    }

}
