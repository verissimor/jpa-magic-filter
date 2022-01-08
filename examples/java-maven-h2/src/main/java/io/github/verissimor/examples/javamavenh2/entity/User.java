package io.github.verissimor.examples.javamavenh2.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Nullable
  Long id;
  String name;
  Integer age;
  @Enumerated(STRING)
  Gender gender;
  @ManyToOne
  City city;
  @Nullable
  LocalDate createdDate;
}