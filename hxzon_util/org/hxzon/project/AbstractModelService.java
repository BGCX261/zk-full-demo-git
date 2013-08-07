package org.hxzon.project;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractModelService<M> implements ModelService<M> {
    @Resource
    protected Dao dao;

    private final Class<M> modelClass;

    @SuppressWarnings("unchecked")
    public AbstractModelService() {
        modelClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public long countAll() {
        return dao.countList(modelClass);
    }

    public long countLikeByExample(M model) {
        return dao.countLikeByExample(model);
    }

    public long countLike(String like) {
        return dao.countLike(modelClass, like);
    }

    public void delete(String id) {
        dao.delete(modelClass, id);
    }

    public void delete(M model) {
        dao.delete(model);
    }

    public void deleteAll() {
        dao.deleteAll(modelClass);
    }

    public void deleteAll(Collection<M> models) {
        dao.deleteAll(models);
    }

    public M findById(String id) {
        return dao.oneById(modelClass, id);
    }

    public List<M> findAll() {
        return dao.list(modelClass, -1, -1);
    }

    public List<M> findAll(long first, long size) {
        return dao.list(modelClass, first, size);
    }

    public List<M> findLikeByExample(M model, long first, long size) {
        return dao.likeByExample(model, first, size);
    }

    public List<M> findLike(String like, long first, long size) {
        return dao.like(modelClass, like, first, size);
    }

    //search
    public List<M> search(String search, long first, long size) {
        return dao.search(modelClass, search, first, size);
    }

    public long countSearch(String search) {
        return dao.countSearch(modelClass, search);
    }

    public void save(M model) {
        dao.save(model);
    }

    public void saveAll(Collection<M> models) {
        dao.saveAll(models);
    }

    public void add(M model) {
        dao.add(model);
    }

    public void addAll(Collection<M> models) {
        dao.addAll(models);
    }

    public void update(M model) {
        dao.update(model);
    }

    public void updateAll(Collection<M> models) {
        dao.updateAll(models);
    }

    public String valid(M model) {
        // TODO Auto-generated method stub
        return null;
    }

}
