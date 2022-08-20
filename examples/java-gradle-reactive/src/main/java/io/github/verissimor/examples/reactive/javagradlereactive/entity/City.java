package io.github.verissimor.examples.reactive.javagradlereactive.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    @Id
    @Nullable
    Long id;
    String name;
}
