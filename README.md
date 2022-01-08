# Spring Jpa Magic Filter

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
    <version>0.0.10</version>
</dependency>
```

GRADLE

```kotlin
implementation 'io.github.verissimor.lib:jpa-magic-filter:0.0.10'
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

| Operator           | Suffix        | Example                     |
| ------------------ | ------------- | --------------------------- |
| EQUAL              |               | `?name=Matthew`             |
| GREATER_THAN       | _gt           | `?age_gt=32`                |
| GREATER_THAN_EQUAL | _ge           | `?age_ge=32`                |
| LESS_THAN          | _lt           | `?age_lt=32`                |
| LESS_THAN_EQUAL    | _le           | `?age_le=32`                |
| LIKE               | _like         | `?name_like=Matthew`        |
| LIKE_EXP           | _like_exp     | `?name_like_exp=M%th%w`     |
| NOT_LIKE           | _not_like     | `?name_not_like=Matthew`    |
| NOT_LIKE_EXP       | _not_like_exp | `?name_not_like_exp=M%th%w` |
| IN                 | _in           | `?age_in=23,24,25,26`       |
| NOT_IN             | _not_in       | `?age_not_in=23,24,25,26`   |
| IS_NULL            | _is_null      | `?age_is_null`              |
| IS_NOT_NULL        | _is_not_null  | `?age_is_not_null`          |

### GREATER_THAN / LESS_THAN vs Between

There is no support for `between`. You can achieve it by using the combination of a `GREATER_THAN` and `LESS_THAN`.

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

## Need more information about how to use?

I'd like to suggest you have a look at the tests. You might have some fun exploring it.

Also, you will find an java+maven project example in the folder: `examples/java-maven-h2`.

## Contributing to the Project
If you'd like to contribute code to this project you can do so through GitHub by forking the repository and generating a pull request.

By contributing your code, you agree to license your contribution under the terms of the Apache Licence.
