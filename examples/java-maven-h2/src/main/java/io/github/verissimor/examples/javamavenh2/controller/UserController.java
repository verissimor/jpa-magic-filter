package io.github.verissimor.examples.javamavenh2.controller;

import java.util.List;

import io.github.verissimor.examples.javamavenh2.repository.UserRepository;
import io.github.verissimor.lib.jpamagicfilter.MagicFilter;
import io.github.verissimor.examples.javamavenh2.entity.User;
import lombok.RequiredArgsConstructor;
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
  List<User> getCurrentUser(MagicFilter filter)  {
    Specification<User> specification = filter.toSpecification(User.class);
    return userRepository.findAll(specification);
  }

}
