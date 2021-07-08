package com.joven.libraryportalapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.support.PagedListHolder;

import java.util.Date;

@Data
@NoArgsConstructor
public class Page {

    private Object data;
    private Date refreshDate;
    private int pageSize;
    private int page;
    private boolean firstPage;
    private boolean lastPage;
    private int totalItems;
    private int totalPages;
    private String message;

    public Page(PagedListHolder<?> pagedListHolder) {
        this.data = pagedListHolder.getPageList();
        this.refreshDate = pagedListHolder.getRefreshDate();
        this.pageSize = pagedListHolder.getPageSize();
        this.page = pagedListHolder.getPage() + 1;
        this.firstPage = pagedListHolder.isFirstPage();
        this.lastPage = pagedListHolder.isLastPage();
        this.totalItems = pagedListHolder.getNrOfElements();
        this.totalPages = pagedListHolder.getPageCount();
    }
}
