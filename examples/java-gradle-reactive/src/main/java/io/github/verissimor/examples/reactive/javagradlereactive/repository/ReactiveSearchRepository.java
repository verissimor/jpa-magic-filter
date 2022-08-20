package io.github.verissimor.examples.reactive.javagradlereactive.repository;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static org.springframework.data.relational.core.query.Query.query;

interface ReactiveSearchRepository<T> {
    Flux<T> findAll(Criteria criteria, Class<T> domainType);

    public Mono<Page<T>> findAll(Criteria criteria, Pageable pageable, Class<T> domainType);
}

@AllArgsConstructor
class ReactiveSearchRepositoryImpl<T> implements ReactiveSearchRepository<T> {

    private final R2dbcEntityTemplate r2dbcTemplate;

    public Flux<T> findAll(Criteria criteria, Class<T> domainType) {
        return r2dbcTemplate.select(domainType)
                .matching(query(criteria))
                .all();
    }

    public Mono<Page<T>> findAll(Criteria criteria, Pageable pageable, Class<T> domainType) {
        Mono<List<T>> list = r2dbcTemplate.select(domainType)
                .matching(query(criteria).with(pageable))
                .all()
                .collectList();
        Mono<Long> count = r2dbcTemplate.select(domainType)
                .matching(query(criteria))
                .count();
        return Mono.zip(list, count)
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
}
