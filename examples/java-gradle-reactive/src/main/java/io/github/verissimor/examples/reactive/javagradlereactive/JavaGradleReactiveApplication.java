package io.github.verissimor.examples.reactive.javagradlereactive;

import io.github.verissimor.lib.r2dbcmagicfilter.EnableR2dbcMagicFilter;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.endsWith;
import static org.springframework.data.domain.ExampleMatcher.matching;
import static org.springframework.data.relational.core.query.Criteria.where;

@EnableR2dbcMagicFilter
@SpringBootApplication
public class JavaGradleReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaGradleReactiveApplication.class, args);
    }

}
