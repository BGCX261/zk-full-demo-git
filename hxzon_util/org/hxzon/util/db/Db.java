package org.hxzon.util.db;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hxzon.util.Dt;
import org.hxzon.util.db.springjdbc.NamedParameterUtils;
import org.hxzon.util.db.springjdbc.ParsedSql;
import org.hxzon.util.db.springjdbc.StatementCreatorUtils;

public class Db {
    public static final int DEFAULT_CACHE_LIMIT = 256;
    private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

    @SuppressWarnings("serial")
    private final Map<String, ParsedSql> parsedSqlCache = new LinkedHashMap<String, ParsedSql>(//
            DEFAULT_CACHE_LIMIT, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ParsedSql> eldest) {
            return size() > getCacheLimit();
        }
    };
    //
    private String name;
    private DataSource dataSource;

    //
    public Db(String name, DataSource dataSource) {
        this.name = name;
        this.dataSource = dataSource;
    }

    //params is p1,v1,p2,v2...
    public DataSet query(String sql, Object... params) {
        return query(sql, Dt.toStringMap(params));
    }

    public int update(String sql, Object... params) {
        return update(sql, Dt.toStringMap(params));
    }

    public DataSet execute(String sql, Object... params) {
        return execute(sql, Dt.toStringMap(params));
    }

    public DataSet query(String sql, Map<String, Object> params) {
        try {
            Connection conn = dataSource.getConnection();
            DataSet r = new DataSet(preparedStatement(conn, sql, params).executeQuery());
            conn.close();
            return r;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int update(String sql, Map<String, Object> params) {
        try {
            Connection conn = dataSource.getConnection();
            int num = preparedStatement(conn, sql, params).executeUpdate();
            conn.close();
            return num;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DataSet execute(String sql, Map<String, Object> params) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement stat = preparedStatement(conn, sql, params);
            DataSet r;
            if (stat.execute()) {
                r = new DataSet(stat.getResultSet());
            } else {
                r = new DataSet(stat.getUpdateCount());
            }
            conn.close();
            return r;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement preparedStatement(Connection conn, String sql, Map<String, Object> params) {
        try {
            ParsedSql parsedSql = getParsedSql(sql);
            String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, null);
            PreparedStatement stat = conn.prepareStatement(sqlToUse);
            ParameterMetaData meta = stat.getParameterMetaData();
            int paramsCount = meta.getParameterCount();
            List<String> paramNames = parsedSql.getParameterNames();
            for (int i = 1; i <= paramsCount; i++) {
                Object value = params.get(paramNames.get(i - 1));
                StatementCreatorUtils.setParameterValue(stat, i, meta.getParameterType(i), value);
            }
            return stat;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ParsedSql getParsedSql(String sql) {
        if (getCacheLimit() <= 0) {
            return NamedParameterUtils.parseSqlStatement(sql);
        }
        synchronized (this.parsedSqlCache) {
            ParsedSql parsedSql = this.parsedSqlCache.get(sql);
            if (parsedSql == null) {
                parsedSql = NamedParameterUtils.parseSqlStatement(sql);
                this.parsedSqlCache.put(sql, parsedSql);
            }
            return parsedSql;
        }
    }

    //get set
    public int getCacheLimit() {
        return this.cacheLimit;
    }

    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    public String getName() {
        return name;
    }
}
