# Spring Jpa Magic Filter
[![CI Pipeline](https://github.com/verissimor/jpa-magic-filter/actions/workflows/ci-pipeline.yml/badge.svg)](https://github.com/verissimor/jpa-magic-filter/actions/workflows/ci-pipeline.yml) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.verissimor.lib/jpa-magic-filter/badge.svg)](https://search.maven.org/artifact/io.github.verissimor.lib/jpa-magic-filter)
[![Coverage](.github/badges/jacoco.svg)](jacoco.svg)
[![License](https://img.shields.io/github/license/verissimor/jpa-magic-filter)](https://github.com/verissimor/jpa-magic-filter/blob/master/LICENSE)

This library handles conversion between spring rest `Request Params` and `JPA Specification`. It can be considered a simpler alternative for `Querydsl Web Support`.

## A quick overview

Let's suppose that you have the following entity:

```java  
@Entity
public class User {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  Long id;
  String name;
  Integer age;
  @ManyToOne City city;
}
```  

Using this library, the lines of code:

```java
// just an overview, check out `Getting Started` 
@GetMapping
List<User> getCurrentUser(MagicFilter filter)  {
  Specification<User> specification = filter.toSpecification(User.class);
  return userRepository.findAll(specification);
}
```

Can handle automatically the following `GET calls`:

- /api/users?**id**=1
- /api/users?**name**=Matthew C. McAfee
- /api/users?name_**like**=Matthew
- /api/users?**age**=32
- /api/users?age_**gt**=32
- /api/users?**city.name**=London
- /api/users?**city.id_in**=123,758,997
- and others...

## Getting Started

MAVEN

```xml

<dependency>
    <groupId>io.github.verissimor.lib</groupId>
    <artifactId>jpa-magic-filter</artifactId>
    <version>${version}</version>
</dependency>
```

GRADLE

```kotlin
implementation 'io.github.verissimor.lib:jpa-magic-filter:${version}'
```

Enable it on your application:

```java
@EnableJpaMagicFilter
@SpringBootApplication
public class DemoApplication
```

Set up an `entity` and a repository that receives a `Specification`:

```java
public interface UserRepository extends JpaRepository<User, Long> {
  List<User> findAll(Specification<User> spec);
}
```

Receive a `MagicFilter` as parameter of a `@GetMapping` and use `filter.getSpec(XXX::class.java)` to resolve a `Specification`:

```java
//@RestController
//@RequestMapping("/api/users")
//@RequiredArgsConstructor
//public class UserController {
//  private final UserRepository userRepository;

  @GetMapping
  List<User> getCurrentUser(MagicFilter filter)  {
    Specification<User> specification = filter.toSpecification(User.class);
    return userRepository.findAll(specification);
  }
//}
```

## Enable support for Spring WebFlux and R2DBC

This lib is compatible with R2DBC. Check out an example in the `examples\java-gradle-reactive`.

```java
@EnableR2dbcMagicFilter
//@SpringBootApplication
//public class DemoApplication
```

You can either use the fluent api:
```java
// https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#r2dbc.entityoperations.fluent-api
@GetMapping
Flux<User> getUsers(R2dbcMagicFilter filter, Pageable pageable) {
    Criteria criteria = filter.toCriteria(User.class, DbFeatures.NONE);
    return r2dbcTemplate.select(User.class).matching(query(criteria)).all();
}
```

Or, extend ReactiveCrudRepository and implement ReactiveSearchRepository

```java
// see how on my discussion on stack overflow (https://stackoverflow.com/questions/73424096/reactivecrudrepository-criteria-query)
@GetMapping("/paged")
Mono<Page<User>> getUsersPaged(R2dbcMagicFilter filter, Pageable pageable) {
    Criteria criteria = filter.toCriteria(User.class, DbFeatures.NONE);
    return userRepository.findAll(criteria, pageable, User.class);
}
```

## How to use

A `filter predicate` is composed of a `field`, an `operator`, and a `value`.

The `field` must follow your class definition, eg.:
- User.name => name
- User.createdAt => createdAt

The `operator` is the suffix of the field, separated by an underscore. It follows the Underscores naming convention, so it's easy to differentiate field and operator.

Finally, the `value` is the expression after the `=` and represents what the filter will apply to. 

```sql
-- example
/api/users?name_like=Matthew&age_gt=32&city.name=London

-- would render something as
where u.name like '%Matthew%' and u.age > 23 and c.name = 'London'
```

## Supported Operators

| Operator           | Suffix           | Example                     |
| ------------------ | ---------------- | --------------------------- |
| EQUAL              |                  | `?name=Matthew`             |
| NOT_EQUAL          | _ne              | `?age_ne=32`                |
| GREATER_THAN       | _gt              | `?age_gt=32`                |
| GREATER_THAN_EQUAL | _ge              | `?age_ge=32`                |
| LESS_THAN          | _lt              | `?age_lt=32`                |
| LESS_THAN_EQUAL    | _le              | `?age_le=32`                |
| LIKE               | _like            | `?name_like=Matthew`        |
| LIKE_EXP           | _like_exp        | `?name_like_exp=M%th%w`     |
| NOT_LIKE           | _not_like        | `?name_not_like=Matthew`    |
| NOT_LIKE_EXP       | _not_like_exp    | `?name_not_like_exp=M%th%w` |
| IN                 | _in              | `?age_in=23,24,25,26`       |
| NOT_IN             | _not_in          | `?age_not_in=23,24,25,26`   |
| IS_NULL            | _is_null         | `?age_is_null`              |
| IS_NOT_NULL        | _is_not_null     | `?age_is_not_null`          |
| BETWEEN            | _is_between      | `?age_is_between=22,30`     |
| NOT_BETWEEN        | _is_not_between  | `?age_is_not_between=22,30` |

### LIKE vs LIKE_EXP

Like by default adds a `%` symbol at beginning and end of the expression;

Like_exp lets the input add its `%`. Therefore:
- ?name_like=Matthew => `where user.name like '%Matthew%'` => contains
- ?name_like_exp=Matthew% => `where user.name like 'Matthew%'` => starts with
- ?name_like_exp=%Matthew => `where user.name like '%Matthew'` => ends with
- ?name_like_exp=M%th%w => `where user.name like 'M%th%w'` 

### IN / NOT_IN queries

The following ways of support a list are supported:
- ?age_in=23,24,25,26 => `where user.age in (23,24,25,26)`
- ?age_in=23;24;25;26&searchInSeparator=; => `where user.age in (23,24,25,26)`
- ?age=23&age=24&age=25&age=26 => `where user.age in (23,24,25,26)`
- ?age[]=23&age[]=24&age[]=25&age[]=26 => `where user.age in (23,24,25,26)`

## Supported Types

### ENUMERATED

For attributes where are mapped as `@Enumerated(STRING) val gender: Gender`.

### NUMBER

Supporting `Int`, `Long` and `BigDecimal`.

### BOOLEAN

Simply use `?active=true`.

### STRING / GENERIC

If the value can't be resolved as any of the above, the lib will resolve it as `String`.

### Support for new types

Please submit a pull request. However, if you don't know how to do it, open an issue.

# Integrate with pagination

Add a paginated method to your repository:
```java
//public interface UserRepository extends JpaRepository<User, Long> {
//  List<User> findAll(Specification<User> spec);
  Page<User> findAll(Specification<User> spec, Pageable page);
//}
```

Change your controller to receive a pageable:
```java
@GetMapping("/paged")
Page<User> getUsersPaged(MagicFilter filter, Pageable pageable)  {
  Specification<User> specification = filter.toSpecification(User.class);
  return userRepository.findAll(specification, pageable);
}
```

Execute a GET call using:
```java
/api/users/paged?city.name=London&?size=20&sort=id,desc&page=0
// more info: https://docs.spring.io/spring-data/rest/docs/current/reference/html/#paging-and-sorting.sorting
```

# Groups and combine Operators

It's supported to change the operator. To do so, add `or__` at the start of your parameter.

Eg.: the following expression:
`?success=1&or__active=1`, would render as:
`success = 1 or active = 1`

It's also supported groups, by adding a double underscore and a number at the end of the parameter. 

Each group will be rendered inside a 'and/or' criteria.

Eg.: the following expression:
`?name__1=Joe&age__1=35&success__2=1&active__2=1`, would render as:
`(name = 'Joe' and age = 35) and (success = 1 and active = 1)`

The concatenation of groups is done by `&searchType=and` or `&searchType=or`.

# Integrate with other business rules

Let's say there is in place a rule that says: `Users can only see users that are in the same city`.

Implement a predicate:
```java
// java
public static Specification<User> isSameCity(User currentUser) {
  return (root, query, cb) -> cb.equal(root.get("city").get("id"), currentUser.getCity().getId());
}

// --  or kotlin --
fun isSameCity(currentUser: User): Specification<User> = Specification { root, query, cb -> 
  cb.equal(root.get<City>("city").get<Long>("id"), currentUser.city.id) 
}
```

Change your controller (or service) to use `and` specification: 

```java
@GetMapping
List<User> getUsers(MagicFilter filter, @AuthenticationPrincipal User currentUser) {
    Specification<User> specification = filter.toSpecification(User.class);
    return userRepository.findAll(specification.and(isSameCity(currentUser)));
}
```

## Kotlin typing safe

When using Kotlin is possible to use the helpers functions to write typing safe queries:

```kotlin
val filter = mapOf(
  User::name.like("Matthew"),
  User::age.gt(20)
).toR2dbcMagicFilter()
```

## Writing native sql queries

Spring R2dbc doesn't have support to joins 🫠. When this is required, then it's needed to use native queries.

```kotlin
@Autowired protected lateinit var template: R2dbcEntityTemplate
@Autowired protected lateinit var converter: MappingR2dbcConverter

val sqlBinder = r2dbcMagicFilter.toSqlBinder(ReactiveUser::class.java, "t")
val sql = """
  SELECT t.*
  FROM user t
    LEFT JOIN city c ON c.id = t.city_id
  WHERE c.country = :country ${sqlBinder?.sql}
  """
val rows = template.databaseClient.sql(sql, sqlBinder)
  .bind("country", "US")
  .map { row, metadata -> converter.read(ReactiveUser::class.java, row, metadata) }
  .all()
  .asFlow()
```

## Advanced Postgres Function

Execute the following piece of code on your db, [more info](https://stackoverflow.com/a/11007216/5795553): 

```sql
CREATE EXTENSION public.unaccent;
```

Then enable postgres extensions through:

```kotlin
filter.toSpecification(User::class.java, DbFeatures.POSTGRES)
```

## Need more information about how to use?

I'd like to suggest you have a look at the tests. You might have some fun exploring it.

Also, you will find a java+maven project example in the folder: `examples/java-maven-h2`.

## Contributing to the Project
If you'd like to contribute code to this project you can do so through GitHub by forking the repository and generating a pull request.

By contributing your code, you agree to license your contribution under the terms of the Apache Licence.

# Spring Compatibility

| jpa-magic-filter version | Spring Version |
|--------------------------|----------------|
| 1.0.*                    | 2.7.*          |
| 3.2.*                    | 3.2.*          |
