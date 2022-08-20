package io.github.verissimor.examples.reactive.javagradlereactive.repository;

import io.github.verissimor.examples.reactive.javagradlereactive.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<User, Long>, ReactiveSearchRepository<User> {

}

