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
//
//@Component
//@AllArgsConstructor
//class Run implements ApplicationRunner {
//
//    private final ConnectionFactory connectionFactory;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//
//        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
//
//        template.getDatabaseClient().sql("CREATE TABLE person" +
//                        "(id serial primary key," +
//                        "name VARCHAR(255)," +
//                        "age INT)")
//                .fetch()
//                .rowsUpdated()
//                .as(StepVerifier::create)
//                .expectNextCount(1)
//                .verifyComplete();
//
//        template.insert(Person.class)
//                .using(new Person(null, "Joe", 34))
//                .as(StepVerifier::create)
//                .expectNextCount(1)
//                .verifyComplete();
//
//        template.select(Person.class)
//                .first()
//                .doOnNext(it -> System.out.println(it))
//                .as(StepVerifier::create)
//                .expectNextCount(1)
//                .verifyComplete();
//
//        var p2 = template
//                .selectOne(Query.query(where("name").is("Joe")), Person.class)
//                .map((it) -> {
//                  return  it;
//                })
//                .block();
//
//        System.out.println(p2);
//    }
//}
//
//
//@Service
//@AllArgsConstructor
//class Run2 implements ApplicationRunner {
//
//    private final PersonRepository repository;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("satrt");
//        Person employee = new Person();
//        employee.setName("Joe");
//        employee.setAge(34);
//
//        ExampleMatcher matcher = matching()
//                .withMatcher("name", endsWith())
//                .withIncludeNullValues();
//        Example<Person> example = Example.of(employee, matcher);
//
//        Flux<Person> employees = repository.findAll(example);
//        System.out.println(employees.blockFirst());
//        System.out.println("end 1");
//
//        var page = Pageable.ofSize(10).withPage(0);
//        Mono<Page<Person>> employees2 = repository.findAll(where("name").is("Joe"), page);
//        System.out.println(employees2.block());
//        System.out.println("end 2");
//
//    }
//}
/*
r2dbcTemplate.getDatabaseClient().sql("insert into city (name) values ('j')").fetch().all().collectList().block();
r2dbcTemplate.getDatabaseClient().sql("select * from city").fetch().all().collectList().block();
r2dbcTemplate.getDatabaseClient().sql("SELECT city.* FROM city").fetch().all().collectList().block();
//cityRepository.findAll().collectList().block();
* */