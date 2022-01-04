package io.github.verissimor.lib.jpamagicfilter

import io.github.verissimor.lib.jpamagicfilter.Gender.FEMALE
import io.github.verissimor.lib.jpamagicfilter.Gender.MALE
import io.github.verissimor.lib.jpamagicfilter.Timezone.AMERICA_NEW_YORK
import io.github.verissimor.lib.jpamagicfilter.Timezone.AMERICA_SAO_PAULO
import io.github.verissimor.lib.jpamagicfilter.Timezone.EUROPE_LONDON
import io.github.verissimor.lib.jpamagicfilter.Timezone.EUROPE_PARIS
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType.STRING
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import java.time.Instant.parse as instant
import java.time.LocalDate.parse as date

@EnableJpaMagicFilter
@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
  runApplication<DemoApplication>(*args)
}

@RestController
@RequestMapping("/api/users")
class UserController(
  private val userRepository: UserRepository
) {

  @GetMapping
  fun getCurrentUser(filter: MagicFilter): List<User> {
    val specification: Specification<User> = filter.getSpec(User::class.java)
    return userRepository.findAll(specification)
  }
}

@Entity
data class User(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long?,
  val name: String,
  val age: Int,
  @Enumerated(STRING) val gender: Gender,
  @ManyToOne val city: City,
  val createdDate: LocalDate?
)

@Entity
data class City(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long?,
  val name: String,
  @ManyToOne val country: Country,
  @Enumerated(STRING) val timezone: Timezone,
  val createdAt: Instant
)

@Entity
data class Country(@Id val code: String, val name: String, val isInEurope: Boolean)

enum class Gender { FEMALE, MALE }

enum class Timezone { AMERICA_NEW_YORK, EUROPE_LONDON, EUROPE_PARIS, AMERICA_SAO_PAULO }

@Repository
interface UserRepository : JpaRepository<User, Long> {
  fun findAll(spec: Specification<User>?): List<User>
  fun findAll(spec: Specification<User>?, pageable: Pageable?): Page<User>
}

@Repository
interface CityRepository : JpaRepository<City, Long>

@Repository
interface CountryRepository : JpaRepository<Country, Long>

@Component
class LoadData(
  private val userRepository: UserRepository,
  private val cityRepository: CityRepository,
  private val countryRepository: CountryRepository,
) : ApplicationRunner {
  override fun run(args: ApplicationArguments?) {
    val countries = listOf(
      Country("US", "United States", false),
      Country("UK", "United Kingdom", true),
      Country("FR", "France", true),
      Country("BR", "Brazil", false),
    )

    val cities = listOf(
      City(null, "New York", countries[0], AMERICA_NEW_YORK, instant("2000-01-01T00:00:00.0Z")),
      City(null, "London", countries[1], EUROPE_LONDON, instant("2000-01-01T00:00:00.0Z")),
      City(null, "Paris", countries[2], EUROPE_PARIS, instant("2022-12-31T23:59:59.9Z")),
      City(null, "Rio de Janeiro", countries[3], AMERICA_SAO_PAULO, instant("2022-12-31T23:59:59.9Z")),
    )

    val users = listOf(
      User(null, "Matthew C. McAfee", 31, MALE, cities[0], date("2000-01-01")),
      User(null, "Eleanor C. Moyer", 23, FEMALE, cities[0], date("2000-02-05")),
      User(null, "Gloria D. Wells", 41, FEMALE, cities[0], date("2000-05-16")),

      User(null, "Matthew Norton", 43, MALE, cities[1], date("2000-03-12")),
      User(null, "Maddison Joyce", 66, FEMALE, cities[1], date("2000-05-16")),

      User(null, "Xarles Foucault", 55, MALE, cities[2], date("2000-07-22")),
      User(null, "Joy Rochefort", 19, FEMALE, cities[2], date("2000-08-10")),

      User(null, "Erick Melo Rodrigues", 19, MALE, cities[3], date("2000-10-30")),
      User(null, "Gloria Azevedo Melot", 35, FEMALE, cities[3], null),
    )

    countryRepository.saveAll(countries)
    cityRepository.saveAll(cities)
    userRepository.saveAll(users)
  }
}
