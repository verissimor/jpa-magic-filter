package io.github.verissimor.examples.reactive.javagradlereactive;


import io.github.verissimor.examples.reactive.javagradlereactive.entity.City;
import io.github.verissimor.examples.reactive.javagradlereactive.entity.User;
import io.github.verissimor.examples.reactive.javagradlereactive.repository.CityRepository;
import io.github.verissimor.examples.reactive.javagradlereactive.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;

import static io.github.verissimor.examples.reactive.javagradlereactive.entity.Gender.FEMALE;
import static io.github.verissimor.examples.reactive.javagradlereactive.entity.Gender.MALE;
import static java.time.LocalDate.parse;

@Component
@AllArgsConstructor
class LoadDataRunner implements ApplicationRunner {

    private final R2dbcEntityTemplate r2dbcTemplate;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;

    @Override
    public void run(ApplicationArguments args) {

        var city1 = cityRepository.save(new City(null, "New York")).block();
        var city2 = cityRepository.save(new City(null, "London")).block();
        var city3 = cityRepository.save(new City(null, "Paris")).block();
        var city4 = cityRepository.save(new City(null, "Rio de Janeiro")).block();

        userRepository.save(new User(null, "Matthew C. McAfee", 31, MALE, city1.getId(), parse("2000-01-01"))).block();
        userRepository.save(new User(null, "Eleanor C. Moyer", 23, FEMALE, city1.getId(), parse("2000-02-05"))).block();
        userRepository.save(new User(null, "Gloria D. Wells", 41, FEMALE, city1.getId(), parse("2000-05-16"))).block();
        userRepository.save(new User(null, "Matthew Norton", 43, MALE, city2.getId(), parse("2000-03-12"))).block();
        userRepository.save(new User(null, "Maddison Joyce", 66, FEMALE, city2.getId(), parse("2000-05-16"))).block();
        userRepository.save(new User(null, "Xarles Foucault", 55, MALE, city3.getId(), parse("2000-07-22"))).block();
        userRepository.save(new User(null, "Joy Rochefort", 19, FEMALE, city3.getId(), parse("2000-08-10"))).block();
        userRepository.save(new User(null, "Erick Melo Rodrigues", 19, MALE, city4.getId(), parse("2000-10-30"))).block();
        userRepository.save(new User(null, "Gloria Azevedo Melot", 35, FEMALE, city4.getId(), null)).block();
    }
}
