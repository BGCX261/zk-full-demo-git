package org.hxzon.util.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class DataSet {

    private int columnCount;
    private List<List<Object>> results;
    private List<String> columnNames;
    private int[] columnTypes;
    private int rowNums;
    private boolean isUpdate;

    public DataSet(ResultSet set) {
        try {
            ResultSetMetaData meta = set.getMetaData();
            columnCount = meta.getColumnCount();
            columnNames = new ArrayList<String>();
            columnTypes = new int[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(meta.getColumnName(i));
                columnTypes[i - 1] = meta.getColumnType(i);
            }
            results = new ArrayList<List<Object>>();
            while (set.next()) {
                List<Object> row = new ArrayList<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(set.getObject(i));
                }
                results.add(row);
            }
            isUpdate = false;
            rowNums = results.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DataSet(int rowNums) {
        isUpdate = true;
        this.rowNums = rowNums;
    }

    //==========
    public int getColumnCount() {
        return columnCount;
    }

    public List<List<Object>> getResults() {
        return results;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public int[] getColumnTypes() {
        return columnTypes;
    }

    public int getRowNums() {
        return rowNums;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

}
