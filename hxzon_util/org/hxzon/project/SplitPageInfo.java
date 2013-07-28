package org.hxzon.project;

public class SplitPageInfo {
    private long _currentPage = 1;
    private long _pageSize = 50;
    private long _rowCount = 0;

    // protected long pageCount = 0;

    public long getCurrentPage() {
        return _currentPage;
    }

    public void setCurrentPage(long currentPage) {
        if (currentPage >= 1) {
            this._currentPage = currentPage;
        }
    }

    public long getCurrentRow() {
        return (_currentPage - 1) * _pageSize;
    }

    public void setCurrentRow(long currentRow) {
        throw new RuntimeException("can't set current row");
    }

    public long getPageSize() {
        return _pageSize;
    }

    public void setPageSize(long pageSize) {
        if (pageSize > 0) {
            this._pageSize = pageSize;
        }
    }

    public long getRowCount() {
        return _rowCount;
    }

    public void setRowCount(long rowCount) {
        this._rowCount = rowCount;
    }

    public long getPageCount() {
        return (_rowCount + _pageSize - 1) / _pageSize;
    }

    public void setPageCount(long pageCount) {
        // this.pagecount = pagecount;
    }

    public boolean isFirst() {
        return _currentPage == 1;
    }

    public boolean isLast() {
        return _currentPage == getPageCount();
    }

    public boolean isPre() {
        return _currentPage > 1;
    }

    public boolean isNext() {
        return _currentPage < getPageCount();
    }

    public String toString() {
        return "currentPage:" + this._currentPage + ";pageSize:" + this._pageSize;
    }
}
