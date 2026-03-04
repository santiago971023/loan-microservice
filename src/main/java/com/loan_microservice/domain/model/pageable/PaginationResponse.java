package com.loan_microservice.domain.model.pageable;

import com.loan_microservice.domain.model.loan.LoanApplicationDetail;

import java.util.List;

public class PaginationResponse<T> {

    private List<T> data;
    private Long totalElements;
    private DomainPageable domainPageable;

    public PaginationResponse(List<T> data, Long totalElements, DomainPageable domainPageable) {
        this.data = data;
        this.totalElements = totalElements;
        this.domainPageable = domainPageable;
    }

    public PaginationResponse() {
    }

    public PaginationResponse(DomainPageable detailsList, Long totalElements, DomainPageable domainPageable) {
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public DomainPageable getDomainPageable() {
        return domainPageable;
    }

    public void setDomainPageable(DomainPageable domainPageable) {
        this.domainPageable = domainPageable;
    }
}
