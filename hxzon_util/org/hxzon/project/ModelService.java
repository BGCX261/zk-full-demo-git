package org.hxzon.project;

import java.util.Collection;
import java.util.List;

public interface ModelService<M> {
    public void add(M model);

    public void addAll(Collection<M> models);

    public void update(M model);

    public void updateAll(Collection<M> models);

    public void save(M model);

    public void saveAll(Collection<M> models);

    public void delete(String id);

    public void delete(M model);

    public void deleteAll();

    public void deleteAll(Collection<M> models);

    public M findById(String id);

    public List<M> findAll();

    public List<M> findAll(long first, long size);

    public List<M> findLikeByExample(M model, long first, long size);

    public List<M> findLike(String like, long first, long size);

    public long countAll();

    public long countLikeByExample(M model);

    public long countLike(String like);

    public String valid(M model);

    public List<M> search(String search, long first, long size);

    public long countSearch(String search);
}
