package io.github.verissimor.examples.reactive.javagradlereactive.controller;

import io.github.verissimor.examples.reactive.javagradlereactive.entity.User;
import io.github.verissimor.examples.reactive.javagradlereactive.repository.UserRepository;
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures;
import io.github.verissimor.lib.r2dbcmagicfilter.R2dbcMagicFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Query.query;

@Log
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final R2dbcEntityTemplate r2dbcTemplate;
    private final UserRepository userRepository;

    @GetMapping
    Flux<User> getUsers(R2dbcMagicFilter filter) {
        Criteria criteria = filter.toCriteria(User.class, DbFeatures.NONE);
        return userRepository.findAll(criteria.ignoreCase(true), User.class);
    }

    @GetMapping("/paged")
    Mono<Page<User>> getUsersPaged(R2dbcMagicFilter filter, Pageable pageable) {
        Criteria criteria = filter.toCriteria(User.class, DbFeatures.NONE);
        return userRepository.findAll(criteria, pageable, User.class);
    }

    @GetMapping("/fluent")
    Flux<User> getUsersFluent(R2dbcMagicFilter filter, Pageable pageable) {
        Criteria criteria = filter.toCriteria(User.class, DbFeatures.NONE);
        return r2dbcTemplate.select(User.class)
                .matching(query(criteria))
                .all();
    }
}
