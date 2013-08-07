package org.hxzon.project;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.Transformers;
import org.hxzon.util.OgnlUtil;

public class HibernateDao extends AbstractHibernateDao implements Dao {

    private SessionFactory _sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this._sessionFactory = sessionFactory;
    }

    public HibernateDao() {
    }

    //================================
    //sql
    protected int doSqlExecute(final String sql, Object... params) {
        Session session = _sessionFactory.getCurrentSession();
        logger.debug(sql);
        Query query = session.createSQLQuery(sql);
        setParams(query, params);
        return query.executeUpdate();
    }

    protected long doSqlCount(String sql, Object... params) {
        Session session = _sessionFactory.getCurrentSession();
        logger.debug(sql);
        Query query = session.createSQLQuery(sql);
        setParams(query, params);
        List<?> list = query.list();
        Object result = list.get(0);
        return ((Number) result).longValue();
    }

    protected List<?> doSqlList(String sql, final long first, final long size, Object... params) {
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(sql);
        setParams(query, params);
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);
            query.setMaxResults((int) size);
        }
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected <M> List<M> doSqlList(Class<M> clazz, boolean entity, String sql, final long first, final long size, Object... params) {
        Session session = _sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(sql);
        if (entity) {
            query.addEntity(clazz);
        } else {
            query.setResultTransformer(Transformers.aliasToBean(clazz));
        }
        setParams(query, params);
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);
            query.setMaxResults((int) size);
        }

        return (List<M>) query.list();
    }

    @SuppressWarnings("unchecked")
    protected <M> List<M> doSqlList(Constructor<M> constructor, String sql, final long first, final long size, Object... params) {
        Session session = _sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setResultTransformer(new AliasToBeanConstructorResultTransformer(constructor));
        setParams(query, params);
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);
            query.setMaxResults((int) size);
        }
        return (List<M>) query.list();
    }

    @SuppressWarnings("unchecked")
    protected <M> List<M> doSqlList(MyHibernateResultTransformer<M> transformer, String sql, final long first, final long size, Object... params) {
        Session session = _sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setResultTransformer(transformer);
        setParams(query, params);
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);
            query.setMaxResults((int) size);
        }
        return (List<M>) query.list();
    }

    //list
    protected int doExecute(final String hql, Object... params) {
        Session session = _sessionFactory.getCurrentSession();
        logger.debug(hql);
        Query query = session.createQuery(hql);
        setParams(query, params);
        return query.executeUpdate();
    }

    protected List<?> doGetList(final String hql, final long first, final long size, Object... params) {
        Session session = _sessionFactory.getCurrentSession();
        logger.debug(hql);
        Query query = session.createQuery(hql);
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);
            query.setMaxResults((int) size);
        }
        setParams(query, params);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected <M> List<M> doGetList(final Class<M> clazz, final String select, final String whereAndOrderBy, final long first, final long size, Object... params) {
        StringBuilder sb = buildHql(select, clazz, whereAndOrderBy);
        logger.debug(sb.toString());
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createQuery(sb.toString());
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);
            query.setMaxResults((int) size);
        }
        setParams(query, params);
        return (List<M>) query.list();
    }

    // one
    protected <M> M doGetOne(Class<M> clazz, String select, String whereAndOrderBy, boolean checkOnlyOne, Object... params) {
        List<M> result = doGetList(clazz, select, whereAndOrderBy, -1, checkOnlyOne ? -1 : 1, params);
        if (checkOnlyOne && result.size() > 1) {
            logger.warn("not only one" + clazz.getCanonicalName() + ":" + whereAndOrderBy);
        }
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    @SuppressWarnings("unchecked")
    protected <M> List<M> doGetLikeExample(final M example, List<String> fields, final long first, final long size) {
        Class<?> clazz = example.getClass();
        StringBuilder hql = new StringBuilder(getFromString(clazz));
        appendWhereString(fields, example, hql);
        if (hql.indexOf("order by") == -1 && !getDefaultOrderByString(clazz).isEmpty()) {
            hql.append(getDefaultOrderByString(clazz));
        }
        logger.debug(hql.toString());
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);// fixme
            query.setMaxResults((int) size);
        }
        setParams(query, fields, example);
        return (List<M>) query.list();
    }

    @SuppressWarnings("unchecked")
    protected <M> List<M> doGetLikeString(final Class<M> clazz, final String like, final List<String> fields, final long first, final long size) {
        StringBuilder hql = new StringBuilder(getFromString(clazz));
        appendWhereString(fields, like, hql);
        if (hql.indexOf("order by") == -1 && !getDefaultOrderByString(clazz).isEmpty()) {
            hql.append(getDefaultOrderByString(clazz));
        }
        logger.debug(hql.toString());
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);// fixme
            query.setMaxResults((int) size);
        }
        setParams(query, "like", ("%" + like + "%"));
        return (List<M>) query.list();
    }

    // count
    protected <M> long doCount(Class<M> clazz, String where, Object... params) {
        StringBuilder hql = new StringBuilder("select count(p.id) ");
        hql.append(getFromString(clazz));
        if (where != null && !where.isEmpty()) {
            hql.append(where);
        }
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        setParams(query, params);
        return (Long) query.list().get(0);
    }

    protected <M> long doCountLikeExample(final M example, final List<String> fields) {
        StringBuilder hql = new StringBuilder("select count(p.id) ");
        hql.append(getFromString(example.getClass()));
        appendWhereString(fields, example, hql);
        logger.debug(hql.toString());
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        setParams(query, fields, example);
        return (Long) query.list().get(0);
    }

    protected <M> long doCountLike(Class<M> clazz, String like, List<String> fields) {
        StringBuilder hql = new StringBuilder("select count(p.id) ");
        hql.append(getFromString(clazz));
        appendWhereString(fields, like, hql);
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        setParams(query, "like", ("%" + like + "%"));
        return (Long) query.list().get(0);
    }

    // get
    @SuppressWarnings("unchecked")
    public <M> M oneById(Class<M> clazz, String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Session session = _sessionFactory.getCurrentSession();
        return (M) session.get(clazz, id);
    }

    // save delete
    public void delete(Object model) {
        if (model == null) {
            return;
        }
        Session session = _sessionFactory.getCurrentSession();
        session.delete(model);
    }

    public <M> void delete(Class<M> clazz, String id) {
        if (id == null || id.isEmpty()) {
            return;
        }
        delete(oneById(clazz, id));
    }

    public <M> void deleteAll(final Class<M> clazz) {
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createQuery(" delete " + clazz.getName());
        query.executeUpdate();
    }

    public <M> void deleteAll(Collection<M> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        Session session = _sessionFactory.getCurrentSession();
        for (M model : models) {
            session.delete(model);
        }
    }

    // save
    public void save(Object model) {
        if (model == null) {
            return;
        }
        if (model instanceof BaseEntity) {
            BaseEntity m = (BaseEntity) model;
            m.updateTimeInfo();
        }
        Session session = _sessionFactory.getCurrentSession();
        session.saveOrUpdate(model);
    }

    public <M> void saveAll(Collection<M> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        for (Object model : models) {
            if (model instanceof BaseEntity) {
                BaseEntity m = (BaseEntity) model;
                m.updateTimeInfo();
            }
        }
        Session session = _sessionFactory.getCurrentSession();
        for (M model : models) {
            session.saveOrUpdate(model);
        }
    }

    public void add(Object model) {
        if (model == null) {
            return;
        }
        if (model instanceof BaseEntity) {
            BaseEntity m = (BaseEntity) model;
            m.updateTimeInfo();
        }
        Session session = _sessionFactory.getCurrentSession();
        session.save(model);
    }

    public <M> void addAll(Collection<M> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        for (Object model : models) {
            if (model instanceof BaseEntity) {
                BaseEntity m = (BaseEntity) model;
                m.updateTimeInfo();
            }
        }
        Session session = _sessionFactory.getCurrentSession();
        for (M model : models) {
            session.save(model);
        }
    }

    public void update(Object model) {
        if (model == null) {
            return;
        }
        if (model instanceof BaseEntity) {
            BaseEntity m = (BaseEntity) model;
            m.updateTimeInfo();
        }
        Session session = _sessionFactory.getCurrentSession();
        session.update(model);
    }

    public <M> void updateAll(Collection<M> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        for (Object model : models) {
            if (model instanceof BaseEntity) {
                BaseEntity m = (BaseEntity) model;
                m.updateTimeInfo();
            }
        }
        Session session = _sessionFactory.getCurrentSession();
        for (M model : models) {
            session.update(model);
        }
    }

    private void setParams(Query query, Object... params) {
        if (params == null || params.length == 0) {
            return;
        }
        StringBuilder paramsStr = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            query.setParameter((String) params[i], params[i + 1]);
            paramsStr.append(params[i]).append(":").append(params[i + 1]).append(";");
        }
        logger.debug(paramsStr.toString());
    }

    private <M> void setParams(Query query, List<String> fields, M example) {
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            String fieldValue = OgnlUtil.getStringValueOrEmpty(example, field);
            fieldValue = ("%" + fieldValue + "%");
            query.setParameter(field, fieldValue);
            sb.append(field).append(":").append(fieldValue).append("; ");
        }
        logger.debug(sb.toString());
    }

}
