package io.github.verissimor.examples.javamavenh2.repository;

import java.util.List;

import io.github.verissimor.examples.javamavenh2.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  List<User> findAll(Specification<User> spec);
  Page<User> findAll(Specification<User> spec, Pageable page);
}
