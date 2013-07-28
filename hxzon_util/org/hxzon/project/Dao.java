package org.hxzon.project;

import java.util.Collection;
import java.util.List;

public interface Dao {
    public int execute(String hql);

    public int executeSql(String sql);

    public List<?> query(String hql, long first, long size);

    public List<?> query(String hql);

    // delete
    public void delete(Object model);

    public <M> void delete(Class<M> clazz, String id);

    public <M> void deleteAll(Class<M> clazz);

    public <M> void deleteAll(Collection<M> models);

    // save
    public void save(Object model);

    public <M> void saveAll(Collection<M> models);

    public void add(Object model);

    public <M> void addAll(Collection<M> models);

    public void update(Object model);

    public <M> void updateAll(Collection<M> models);

    // get

    // list
    public <M> M oneById(Class<M> clazz, String id);

    public <M> M oneByProperty(Class<M> clazz, String fieldName, Object fieldValue);

    public <M> M one(Class<M> clazz, String select, String whereAndOrderBy, boolean checkOnlyOne);

    public <M> M one(Class<M> clazz, String select, String whereAndOrderBy);

    public <M> M one(Class<M> clazz, String whereAndOrderBy);

    public <M> M first(Class<M> clazz, String select, String whereAndOrderBy);

    public <M> M first(Class<M> clazz, String whereAndOrderBy);

    public <M> List<M> list(Class<M> clazz, String select, String whereAndOrderBy, long first, long size);

    public <M> List<M> list(Class<M> clazz, String select, String whereAndOrderBy);

    public <M> List<M> list(Class<M> clazz, String whereAndOrderBy);

    public <M> List<M> list(Class<M> clazz, String whereAndOrderBy, long first, long size);

    public <M> List<M> list(Class<M> clazz, long first, long size);

    public <M> List<M> list(Class<M> clazz);

    public <M> long countList(Class<M> clazz, String whereAndOrderBy);

    public <M> long countList(Class<M> clazz);

    public <M> List<M> listByProperty(Class<M> clazz, String fieldName, Object fieldValue);

    public <M> List<M> listByProperty(Class<M> clazz, String fieldName, Object fieldValue, long first, long size);

    public <M> long countList(Class<M> clazz, String fieldName, Object fieldValue);

    // like
    public <M> List<M> like(Class<M> clazz, String like, List<String> fields, long first, long size);

    public <M> List<M> like(Class<M> clazz, String like, long first, long size);

    public <M> List<M> likeByExample(M example, List<String> fields, long first, long size);

    public <M> List<M> likeByExample(M example, long first, long size);

    public <M> long countLike(Class<M> clazz, String like, List<String> fields);

    public <M> long countLike(Class<M> clazz, String like);

    public <M> long countLikeByExample(M example, List<String> fields);

    public <M> long countLikeByExample(M example);

    // search
    public <M> List<M> search(Class<M> clazz, String search, long first, long size);

    public <M> long countSearch(Class<M> clazz, String search);
}
