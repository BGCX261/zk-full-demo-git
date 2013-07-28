package org.hxzon.project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hxzon.util.OgnlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHibernateDao implements Dao {

    protected abstract int doExecute(final String hql);

    protected abstract int doExecuteSql(final String sql);

    protected abstract List<?> doQuery(final String hql, final long first, final long size);

    protected abstract <M> List<M> doGetList(Class<M> clazz, String select, String whereAdnOrderBy, long first, long size);

    protected abstract <M> List<M> doGetLikeExample(final M example, List<String> fields, final long first, final long size);

    protected abstract <M> M doGetOne(Class<M> clazz, String select, String whereAndOrderBy, boolean checkOnlyOne);

    protected abstract <M> List<M> doGetLikeString(final Class<M> clazz, final String like, final List<String> fields, final long first, final long size);

    protected abstract <M> long doCount(Class<M> clazz, String where);

    protected abstract <M> long doCountLikeExample(final M example, final List<String> fields);

    protected abstract <M> long doCountLike(Class<M> clazz, String like, List<String> fields);

    //
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @SuppressWarnings("rawtypes")
    private final Map<Class, List<String>> _stringFields = new HashMap<Class, List<String>>();
    @SuppressWarnings("rawtypes")
    private final Map<Class, String> _fromString = new HashMap<Class, String>();

    public AbstractHibernateDao() {
    }

    //sql,hql
    public int executeSql(final String sql) {
        return doExecuteSql(sql);
    }

    public int execute(String hql) {
        return doExecute(hql);
    }

    public List<?> query(String hql, long first, long size) {
        return doQuery(hql, first, size);
    }

    public List<?> query(String hql) {
        return doQuery(hql, -1, -1);
    }

    // all

    // one
    public <M> M one(Class<M> clazz, String select, String whereAndOrderBy, boolean checkOnlyOne) {
        return doGetOne(clazz, select, whereAndOrderBy, checkOnlyOne);
    }

    public <M> M one(Class<M> clazz, String select, String whereAndOrderBy) {
        return doGetOne(clazz, select, whereAndOrderBy, true);
    }

    public <M> M one(Class<M> clazz, String whereAndOrderBy) {
        return doGetOne(clazz, null, whereAndOrderBy, true);
    }

    public <M> M first(Class<M> clazz, String select, String whereAndOrderBy) {
        return doGetOne(clazz, select, whereAndOrderBy, false);
    }

    public <M> M first(Class<M> clazz, String whereAndOrderBy) {
        return doGetOne(clazz, null, whereAndOrderBy, false);
    }

    //list
    public <M> List<M> list(Class<M> clazz, String select, String whereAndOrderBy, long first, long size) {
        return doGetList(clazz, select, whereAndOrderBy, first, size);
    }

    public <M> List<M> list(Class<M> clazz, String select, String whereAndOrderBy) {
        return doGetList(clazz, select, whereAndOrderBy, -1, -1);
    }

    public <M> List<M> list(Class<M> clazz, String whereAndOrderBy, long first, long size) {
        return doGetList(clazz, null, whereAndOrderBy, first, size);
    }

    public <M> List<M> list(Class<M> clazz, String whereAndOrderBy) {
        return doGetList(clazz, null, whereAndOrderBy, -1, -1);
    }

    public <M> List<M> list(Class<M> clazz) {
        return doGetList(clazz, null, null, -1, -1);
    }

    public <M> List<M> list(Class<M> clazz, long first, long size) {
        return doGetList(clazz, null, null, first, size);
    }

    public <M> List<M> listByProperty(Class<M> clazz, String fieldName, Object fieldValue) {
        return listByProperty(clazz, fieldName, fieldValue, -1, -1);
    }

    public <M> List<M> listByProperty(Class<M> clazz, String fieldName, Object fieldValue, long first, long size) {
        if (fieldValue instanceof String) {
            fieldValue = "'" + OgnlUtil.escape((String) fieldValue) + "'";
        }
        return doGetList(clazz, null, " where p." + fieldName + " = " + fieldValue, first, size);
    }

    public <M> M oneByProperty(Class<M> clazz, String fieldName, Object fieldValue) {
        if (fieldName == null || fieldName.isEmpty() || fieldValue == null) {
            return null;
        }
        List<M> list = listByProperty(clazz, fieldName, fieldValue, -1, -1);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //like
    public <M> List<M> likeByExample(final M example, List<String> fields, final long first, final long size) {
        return doGetLikeExample(example, fields, first, size);
    }

    public <M> List<M> likeByExample(final M modelExample, final long first, final long size) {
        return likeByExample(modelExample, getStringFields(modelExample.getClass()), first, size);
    }

    public <M> List<M> like(Class<M> clazz, final String like, final List<String> fields, final long first, final long size) {
        return doGetLikeString(clazz, like, fields, first, size);
    }

    public <M> List<M> like(Class<M> clazz, String like, long first, long size) {
        return like(clazz, like, getStringFields(clazz), first, size);
    }

    // count
    public <M> long countList(Class<M> clazz) {
        return doCount(clazz, null);
    }

    public <M> long countList(Class<M> clazz, String where) {
        return doCount(clazz, where);
    }

    public <M> long countList(Class<M> clazz, String fieldName, Object fieldValue) {
        if (fieldValue instanceof String) {
            fieldValue = "'" + OgnlUtil.escape((String) fieldValue) + "'";
        }
        return doCount(clazz, " where p." + fieldName + " = " + fieldValue);
    }

    public <M> long countLikeByExample(final M modelExample) {
        return doCountLikeExample(modelExample, getStringFields(modelExample.getClass()));
    }

    public <M> long countLike(Class<M> clazz, String like) {
        return countLike(clazz, like, getStringFields(clazz));
    }

    public <M> long countLike(Class<M> clazz, String like, List<String> fields) {
        return doCountLike(clazz, like, fields);
    }

    public <M> long countLikeByExample(M example, List<String> fields) {
        return doCountLikeExample(example, fields);
    }

    //search------------------
//  @SuppressWarnings("unchecked")
    public <M> List<M> search(Class<M> clazz, String search, long first, long size) {
//      QueryParser parser = new QueryParser(Version.LUCENE_30,"search", new StandardAnalyzer(Version.LUCENE_30));//new ChineseAnalyzer());
//      org.apache.lucene.search.Query luceneQuery;
//      try {
//          luceneQuery = parser.parse(search);
//          FullTextSession s = Search.getFullTextSession(getSession());
//          FullTextQuery query = s.createFullTextQuery(luceneQuery, clazz);
//          if (first > -1 && size > 0) {
//              query.setFirstResult((int) first);// fixme
//              query.setMaxResults((int) size);
//          }
//          return (List<M>)query.list();
//      } catch (ParseException e) {
//          return new ArrayList<M>();
//      }
        return null;
    }

    public <M> long countSearch(Class<M> clazz, String search) {
//      QueryParser parser = new QueryParser(Version.LUCENE_30,"search", new StandardAnalyzer(Version.LUCENE_30));//new ChineseAnalyzer());
//      org.apache.lucene.search.Query luceneQuery;
//      try {
//          luceneQuery = parser.parse(search);
//          FullTextSession s = Search.getFullTextSession(getSession());
//          FullTextQuery query = s.createFullTextQuery(luceneQuery, clazz);
//          return query.getResultSize();
//      } catch (ParseException e) {
//          return 0;
//      }
        return 0;
    }

    //-----------------------------------
    protected StringBuilder buildHql(String select, Class<?> clazz, String whereAndOrderBy) {
        StringBuilder sb = new StringBuilder();
        if (select != null && !select.isEmpty()) {
            sb.append(select);
        }
        sb.append(getFromString(clazz));
        if (whereAndOrderBy != null && !whereAndOrderBy.isEmpty()) {
            sb.append(whereAndOrderBy);
        }
        if (sb.indexOf("order by") == -1 && !getDefaultOrderByString(clazz).isEmpty()) {
            sb.append(getDefaultOrderByString(clazz));
        }
        return sb;
    }

    protected String appendWhereString(List<String> fields, String like, StringBuilder hql) {
        like = OgnlUtil.escape(like);
        if (!fields.isEmpty()) {// fixme not return String
            hql.append(" where 1=0 ");
            for (String field : fields) {
                hql.append(" or p.").append(field).append(" like '%").append(like).append("%'");
            }
        }
        return hql.toString();
    }

    protected <M> void appendWhereString(List<String> fields, M example, StringBuilder hql) {
        if (!fields.isEmpty()) {
            hql.append(" where 1=1 ");
            for (String field : fields) {
                hql.append(" and p.").append(field).append(" like '%").append(OgnlUtil.getStringValueOrEmpty(example, field).replace("'", "''")).append("%'");
            }
        }
    }

    protected String getDefaultOrderByString(Class<?> clazz) {
        return "";
    }

    protected <M> String getFromString(Class<M> clazz) {
        String s = _fromString.get(clazz);
        if (s == null) {
            s = " from " + clazz.getName() + " as p ";
            _fromString.put(clazz, s);
        }
        return s;
    }

    protected <M> List<String> getStringFields(Class<M> clazz) {
        List<String> ls = _stringFields.get(clazz);
        if (ls == null) {
            ls = OgnlUtil.getAllStringProperty(clazz);
            _stringFields.put(clazz, ls);
        }
        return ls;
    }

    // static final class StringPropertySelecter implements PropertySelector{
    //
    // public boolean include(Object propertyValue, String propertyName, Type
    // type) {
    // if(propertyValue ==null || !(propertyValue instanceof String)){
    // return false;
    // }
    // if(((String)propertyValue).isEmpty()){
    // return false;
    // }
    // return true;
    // }
    //      
    // }
    // private static final PropertySelector stringProperty=new
    // StringPropertySelecter();

}
