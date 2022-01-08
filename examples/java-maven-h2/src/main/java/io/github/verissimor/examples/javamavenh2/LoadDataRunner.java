package io.github.verissimor.examples.javamavenh2;


import java.util.ArrayList;
import java.util.List;

import io.github.verissimor.examples.javamavenh2.entity.City;
import io.github.verissimor.examples.javamavenh2.entity.User;
import io.github.verissimor.examples.javamavenh2.repository.CityRepository;
import io.github.verissimor.examples.javamavenh2.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static io.github.verissimor.examples.javamavenh2.entity.Gender.FEMALE;
import static io.github.verissimor.examples.javamavenh2.entity.Gender.MALE;
import static java.time.LocalDate.parse;

@Component
@AllArgsConstructor
class LoadDataRunner implements ApplicationRunner {

  private final UserRepository userRepository;
  private final CityRepository cityRepository;

  @Override
  public void run(ApplicationArguments args) {
    List<City> cities = new ArrayList();

    cities.add(new City(null, "New York"));
    cities.add(new City(null, "London"));
    cities.add(new City(null, "Paris"));
    cities.add(new City(null, "Rio de Janeiro"));

    List<User> users = new ArrayList();
    users.add(new User(null, "Matthew C. McAfee", 31, MALE, cities.get(0), parse("2000-01-01")));
    users.add(new User(null, "Eleanor C. Moyer", 23, FEMALE, cities.get(0), parse("2000-02-05")));
    users.add(new User(null, "Gloria D. Wells", 41, FEMALE, cities.get(0), parse("2000-05-16")));
    users.add(new User(null, "Matthew Norton", 43, MALE, cities.get(1), parse("2000-03-12")));
    users.add(new User(null, "Maddison Joyce", 66, FEMALE, cities.get(1), parse("2000-05-16")));
    users.add(new User(null, "Xarles Foucault", 55, MALE, cities.get(2), parse("2000-07-22")));
    users.add(new User(null, "Joy Rochefort", 19, FEMALE, cities.get(2), parse("2000-08-10")));
    users.add(new User(null, "Erick Melo Rodrigues", 19, MALE, cities.get(3), parse("2000-10-30")));
    users.add(new User(null, "Gloria Azevedo Melot", 35, FEMALE, cities.get(3), null));

    cityRepository.saveAll(cities);
    userRepository.saveAll(users);
  }
}
