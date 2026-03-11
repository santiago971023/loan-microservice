package com.loan_microservice.domain.model.pageable;

import lombok.Builder;

@Builder
public class DomainPageable {

    private int pageNumber;
    private int pageSize;

    public DomainPageable(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
