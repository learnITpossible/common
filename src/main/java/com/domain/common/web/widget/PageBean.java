package com.domain.common.web.widget;

import java.io.Serializable;

public class PageBean implements Serializable {

    private static final long serialVersionUID = 3911399534500835310L;

    public static final int FIRST_PAGE = 1;

    public static final int PAGE_MODE_NORMAL = 0;

    public static final int PAGE_MODE_ONLY_NEXT_PREV = 1;

    int pageSize = 50;

    int pageNum = 1;

    int totalSize;

    int totalPageCount;

    boolean isLast;

    /**
     * 0: 普通翻页，有总数，有页数
     * 1: 只有上下面，没有总数和页数
     */
    int pageMode = 0;

    public PageBean(int pageNum) {

        super();
        setPageNum(pageNum);
    }

    public PageBean(int pageSize, int pageNum) {

        super();
        setPageNum(pageNum);
        setPageSize(pageSize);
    }

    public PageBean(int pageSize, int pageNum, int pageMode) {

        super();
        setPageNum(pageNum);
        setPageSize(pageSize);
        this.pageMode = pageMode;
    }

    public int getPageSize() {

        return pageSize;
    }

    private void setPageSize(int pageSize) {

        this.pageSize = pageSize;
    }

    public int getPageNum() {

        return pageNum;
    }

    public void setPageNum(int pageNum) {

        this.pageNum = pageNum;
        if (this.pageNum < FIRST_PAGE) {
            this.pageNum = FIRST_PAGE;
        }
    }

    public int getTotalSize() {

        return totalSize;
    }

    public void setTotalSize(int totalSize) {

        if (pageMode == PAGE_MODE_ONLY_NEXT_PREV) {
            throw new IllegalArgumentException("page mode is PAGE_MODE_ONLY_NEXT_PREV doesn't support setTotalSize!");
        }
        this.totalSize = totalSize;
        totalPageCount = this.totalSize / pageSize;
        if (totalPageCount < FIRST_PAGE) {
            totalPageCount = FIRST_PAGE;
        }

        if (this.totalSize % pageSize > 0 && this.totalSize > pageSize) {
            totalPageCount++;
        }
        if (1 == totalPageCount) {
            isLast = true;
        } else if (pageNum >= totalPageCount) {
            isLast = true;
        }
    }

    public int getTotalPageCount() {

        return totalPageCount;
    }

    public boolean isFirst() {

        return pageNum <= FIRST_PAGE;
    }

    public boolean isLast() {

        return isLast;
    }

    public int getPrevPage() {

        if (pageNum <= FIRST_PAGE) {
            return FIRST_PAGE;
        } else {
            return pageNum - 1;
        }
    }

    public int getNextPage() {

        if (pageMode == PAGE_MODE_NORMAL && pageNum >= totalPageCount) {
            return totalPageCount;
        } else {
            return pageNum + 1;
        }
    }

    public int getFirstPage() {

        return FIRST_PAGE;
    }

    public int getLastPage() {

        return totalPageCount;
    }

    public int getStart() {

        int start = (getPageNum() - 1) * getPageSize();
        return start;
    }

    public int getPageMode() {

        return pageMode;
    }

}
