package org.hxzon.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hxzon.util.OgnlUtil;

public class HibernateDao extends AbstractHibernateDao implements Dao {

    private SessionFactory _sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this._sessionFactory = sessionFactory;
    }

    public HibernateDao() {
    }

    protected int doExecuteSql(final String sql) {
        Session session = _sessionFactory.getCurrentSession();

        logger.debug(sql);
        Query query = session.createSQLQuery(sql);
        return query.executeUpdate();
    }

    protected int doExecute(final String hql) {
        Session session = _sessionFactory.getCurrentSession();

        logger.debug(hql);
        Query query = session.createQuery(hql);
        return query.executeUpdate();
    }

    protected List<?> doQuery(final String hql, final long first, final long size) {
        Session session = _sessionFactory.getCurrentSession();

        logger.debug(hql);
        Query query = session.createQuery(hql);
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);
            query.setMaxResults((int) size);
        }
        return query.list();
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

    // get
    @SuppressWarnings("unchecked")
    public <M> M oneById(Class<M> clazz, String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Session session = _sessionFactory.getCurrentSession();
        return (M) session.get(clazz, id);
    }

    @SuppressWarnings("unchecked")
    protected <M> List<M> doGetList(final Class<M> clazz, final String select, final String whereAndOrderBy, final long first, final long size) {
        StringBuilder sb = buildHql(select, clazz, whereAndOrderBy);
        logger.debug(sb.toString());
        Session session = _sessionFactory.getCurrentSession();
        Query query = session.createQuery(sb.toString());
        if (first > -1 && size > 0) {
            query.setFirstResult((int) first);
            query.setMaxResults((int) size);
        }
        return (List<M>) query.list();
    }

    // one
    protected <M> M doGetOne(Class<M> clazz, String select, String whereAndOrderBy, boolean checkOnlyOne) {
        List<M> result = doGetList(clazz, select, whereAndOrderBy, -1, checkOnlyOne ? -1 : 1);
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
        final List<String> fields2 = new ArrayList<String>();
        Object value = null;
        for (int i = fields.size() - 1; i >= 0; i--) {
            value = OgnlUtil.getValue(example, fields.get(i));
            if (value != null && value instanceof String && !((String) value).isEmpty()) {
                fields2.add(fields.get(i));
            }
        }
        Class<?> clazz = example.getClass();
        StringBuilder hql = new StringBuilder(getFromString(clazz));
        appendWhereString(fields2, example, hql);
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
        return (List<M>) query.list();
    }

    // count
    @SuppressWarnings("rawtypes")
    protected <M> long doCount(Class<M> clazz, String where) {
        StringBuilder hql = new StringBuilder("select count(p.id) ");
        hql.append(getFromString(clazz));
        if (where != null && !where.isEmpty()) {
            hql.append(where);
        }
        Session session = _sessionFactory.getCurrentSession();
        List list = session.createQuery(hql.toString()).list();
        return (Long) list.get(0);
    }

    @SuppressWarnings("rawtypes")
    protected <M> long doCountLikeExample(final M example, final List<String> fields) {
        final List<String> fields2 = new ArrayList<String>();
        Object value = null;
        for (int i = fields.size() - 1; i >= 0; i--) {
            value = OgnlUtil.getValue(example, fields.get(i));
            if (value != null && value instanceof String && !((String) value).isEmpty()) {
                fields2.add(fields.get(i));
            }
        }
        StringBuilder hql = new StringBuilder("select count(p.id) ");
        hql.append(getFromString(example.getClass()));
        appendWhereString(fields2, example, hql);
        logger.debug(hql.toString());
        Session session = _sessionFactory.getCurrentSession();
        List list = session.createQuery(hql.toString()).list();
        return (Long) list.get(0);
    }

    @SuppressWarnings("rawtypes")
    protected <M> long doCountLike(Class<M> clazz, String like, List<String> fields) {
        StringBuilder hql = new StringBuilder("select count(p.id) ");
        hql.append(getFromString(clazz));
        appendWhereString(fields, like, hql);
        Session session = _sessionFactory.getCurrentSession();
        List list = session.createQuery(hql.toString()).list();
        return (Long) list.get(0);
    }

}
