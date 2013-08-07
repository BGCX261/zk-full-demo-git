package org.hxzon.project;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

public interface Dao {

    //sql
    int sqlExecute(String sql, Object... params);

    long sqlCount(String sql, Object... params);

    List<?> sqlList(String sql, long first, long size, Object... params);

    <M> List<M> sqlList(Class<M> clazz, boolean entity, String sql, long first, long size, Object... params);

    <M> List<M> sqlList(Constructor<M> c, String sql, long first, long size, Object... params);

    <M> List<M> sqlList(MyHibernateResultTransformer<M> transform, String sql, long first, long size, Object... params);

    // get
    int execute(String hql, Object... params);

    List<?> list(String hql, long first, long size, Object... params);

    // list
    <M> List<M> list(Class<M> clazz, String select, String whereAndOrderBy, long first, long size, Object... params);

    <M> List<M> list(Class<M> clazz, String whereAndOrderBy, long first, long size, Object... params);

    <M> List<M> list(Class<M> clazz, long first, long size);

    <M> long countList(Class<M> clazz, String whereAndOrderBy, Object... params);

    <M> long countList(Class<M> clazz);

    <M> List<M> listByProperty(Class<M> clazz, String fieldName, Object fieldValue, long first, long size);

    <M> long countList(Class<M> clazz, String fieldName, Object fieldValue);

    //one
    <M> M oneById(Class<M> clazz, String id);

    <M> M oneByProperty(Class<M> clazz, String fieldName, Object fieldValue);

    <M> M one(Class<M> clazz, String select, String whereAndOrderBy, boolean checkOnlyOne, Object... params);

    <M> M one(Class<M> clazz, String whereAndOrderBy, Object... params);

    <M> M first(Class<M> clazz, String whereAndOrderBy, Object... params);

    // like
    <M> List<M> like(Class<M> clazz, String like, List<String> fields, long first, long size);

    <M> List<M> like(Class<M> clazz, String like, long first, long size);

    <M> List<M> likeByExample(M example, List<String> fields, long first, long size);

    <M> List<M> likeByExample(M example, long first, long size);

    <M> long countLike(Class<M> clazz, String like, List<String> fields);

    <M> long countLike(Class<M> clazz, String like);

    <M> long countLikeByExample(M example, List<String> fields);

    <M> long countLikeByExample(M example);

    // search
    <M> List<M> search(Class<M> clazz, String search, long first, long size);

    <M> long countSearch(Class<M> clazz, String search);

    // delete
    void delete(Object model);

    <M> void delete(Class<M> clazz, String id);

    <M> void deleteAll(Class<M> clazz);

    <M> void deleteAll(Collection<M> models);

    // save
    void save(Object model);

    <M> void saveAll(Collection<M> models);

    void add(Object model);

    <M> void addAll(Collection<M> models);

    void update(Object model);

    <M> void updateAll(Collection<M> models);
}
