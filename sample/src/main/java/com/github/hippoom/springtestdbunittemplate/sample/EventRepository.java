package com.github.hippoom.springtestdbunittemplate.sample;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventRepository extends PagingAndSortingRepository<Event, String>, QueryDslPredicateExecutor<Event> {


}
