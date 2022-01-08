package io.github.verissimor.examples.javamavenh2.controller;

import java.util.List;

import io.github.verissimor.examples.javamavenh2.entity.User;
import io.github.verissimor.examples.javamavenh2.repository.UserRepository;
import io.github.verissimor.lib.jpamagicfilter.MagicFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping
  List<User> getUsers(MagicFilter filter) {
    Specification<User> specification = filter.toSpecification(User.class);
    return userRepository.findAll(specification);
  }

  @GetMapping("/paged")
  Page<User> getUsersPaged(MagicFilter filter, Pageable pageable) {
    Specification<User> specification = filter.toSpecification(User.class);
    return userRepository.findAll(specification, pageable);
  }
}
