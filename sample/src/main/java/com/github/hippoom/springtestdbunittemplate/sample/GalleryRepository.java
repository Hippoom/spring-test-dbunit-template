package com.github.hippoom.springtestdbunittemplate.sample;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface GalleryRepository extends CrudRepository<Gallery, String>,
        QueryDslPredicateExecutor<Gallery>, GalleryQuery {


}
