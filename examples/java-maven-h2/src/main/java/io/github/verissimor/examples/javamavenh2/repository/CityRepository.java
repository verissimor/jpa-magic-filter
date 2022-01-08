package io.github.verissimor.examples.javamavenh2.repository;

import io.github.verissimor.examples.javamavenh2.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
