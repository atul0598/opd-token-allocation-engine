package com.ptu.medoc.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
@Getter
@Setter
public class PageTokenResponse<T> {

    private List<T> TokenDetails;
    private long noOfRecords;
    private int availablePages;
    private int pageNumber;
    private int pageSize;
    private boolean thisLastPage;

    public PageTokenResponse(Page<T> page) {
        this.TokenDetails = page.getContent();
        this.noOfRecords = page.getTotalElements();
        this.availablePages = page.getTotalPages();
        this.pageNumber = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.thisLastPage = page.isLast();
    }
}

