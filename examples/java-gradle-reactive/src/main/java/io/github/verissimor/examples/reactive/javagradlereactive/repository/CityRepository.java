package io.github.verissimor.examples.reactive.javagradlereactive.repository;

import io.github.verissimor.examples.reactive.javagradlereactive.entity.City;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CityRepository extends ReactiveCrudRepository<City, Long> {
}
