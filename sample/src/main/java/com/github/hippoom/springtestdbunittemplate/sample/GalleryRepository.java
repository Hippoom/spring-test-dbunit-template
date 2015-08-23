package com.github.hippoom.springtestdbunittemplate.sample;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GalleryRepository extends PagingAndSortingRepository<Gallery, String>,
        QueryDslPredicateExecutor<Gallery> {


}
