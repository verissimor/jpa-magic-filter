package io.github.verissimor.examples.reactive.javagradlereactive.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @Nullable
    Long id;
    String name;
    Integer age;
    Gender gender;
    Long cityId;
    @Nullable
    LocalDate createdDate;
}
