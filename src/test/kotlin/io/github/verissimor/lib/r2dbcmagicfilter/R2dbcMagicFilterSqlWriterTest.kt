package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class R2dbcMagicFilterSqlWriterTest {

  @Test
  fun `test sql writer equals string`() {
    val params = listOf("name" to "Joe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name = :name0")
    assertThat(sqlBinder?.params?.get("name0")).isEqualTo("Joe")
  }

  @Test
  fun `test sql writer greater`() {
    val params = listOf("age_gt" to "35").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.age > :age0")
    assertThat(sqlBinder?.params?.get("age0")).isEqualTo(BigDecimal(35))
  }

  @Test
  fun `test sql writer greater eq`() {
    val params = listOf("age_ge" to "35").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.age >= :age0")
    assertThat(sqlBinder?.params?.get("age0")).isEqualTo(BigDecimal(35))
  }

  @Test
  fun `test sql writer less than`() {
    val params = listOf("age_lt" to "40").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.age < :age0")
    assertThat(sqlBinder?.params?.get("age0")).isEqualTo(BigDecimal(40))
  }

  @Test
  fun `test sql writer less than equal`() {
    val params = listOf("age_le" to "40").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.age <= :age0")
    assertThat(sqlBinder?.params?.get("age0")).isEqualTo(BigDecimal(40))
  }

  @Test
  fun `test sql writer like expression`() {
    val params = listOf("name_like_exp" to "John").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name LIKE :name0")
    assertThat(sqlBinder?.params?.get("name0")).isEqualTo("John")
  }

  @Test
  fun `test sql writer like`() {
    val params = listOf("name_like" to "Doe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name LIKE :name0")
    assertThat(sqlBinder?.params?.get("name0")).isEqualTo("Doe")
  }

  @Test
  fun `test sql writer not like expression`() {
    val params = listOf("name_not_like_exp" to "Smith").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name NOT LIKE :name0")
    assertThat(sqlBinder?.params?.get("name0")).isEqualTo("Smith")
  }

  @Test
  fun `test sql writer not like`() {
    val params = listOf("name_not_like" to "Johnson").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name NOT LIKE :name0")
    assertThat(sqlBinder?.params?.get("name0")).isEqualTo("Johnson")
  }

  @Test
  fun `test sql writer in`() {
    val params = listOf("age_in" to "30,35,40").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.age IN (:age0)")
    assertThat(sqlBinder?.params?.get("age0")).isEqualTo(listOf(30, 35, 40).map { it.toBigDecimal() })
  }

  @Test
  fun `test sql writer not in`() {
    val params = listOf("age_not_in" to "25,50").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.age NOT IN (:age0)")
    assertThat(sqlBinder?.params?.get("age0")).isEqualTo(listOf(25, 50).map { it.toBigDecimal() })
  }

  @Test
  fun `test sql writer is null`() {
    val params = listOf("name_is_null" to "true").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name IS NULL")
    assertThat(sqlBinder?.params).isEmpty()
  }

  @Test
  fun `test sql writer is not null`() {
    val params = listOf("name_is_not_null" to "true").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name IS NOT NULL")
    assertThat(sqlBinder?.params).isEmpty()
  }

  @Test
  fun `test sql writer sql writer between number`() {
    val params = listOf("age_is_between" to "19, 35").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder!!.sql).isEqualTo(" AND b.age BETWEEN :age0a AND :age0b")
    assertThat(sqlBinder.params["age0a"]).isEqualTo(BigDecimal(19))
    assertThat(sqlBinder.params["age0b"]).isEqualTo(BigDecimal(35))
  }

  @Test
  fun `test sql writer not equal`() {
    val params = listOf("age_ne" to "25").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.age <> :age0")
    assertThat(sqlBinder?.params?.get("age0")).isEqualTo(BigDecimal(25))
  }

  @Test
  fun `test sql writer equals date`() {
    val params = listOf("createdDate" to "2022-12-31").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.created_date = :createdDate0")
    assertThat(sqlBinder?.params?.get("createdDate0")).isEqualTo(LocalDate.parse("2022-12-31"))
  }

  @Test
  fun `test sql writer equals instant`() {
    val now = Instant.now().toString()
    val params = listOf("createdAt" to now).toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.created_at = :createdAt0")
    assertThat(sqlBinder?.params?.get("createdAt0")).isEqualTo(Instant.parse(now))
  }

  @Test
  fun `test sql writer equals boolean`() {
    val params = listOf("enabled" to "0").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.enabled = :enabled0")
    assertThat(sqlBinder?.params?.get("enabled0")).isEqualTo(false)
  }

  @Test
  fun `test sql writer with multiple parameters`() {
    val params = listOf("name" to "Joe", "age_gt" to "35").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name = :name0 AND b.age > :age1")
    assertThat(sqlBinder?.params?.get("name0")).isEqualTo("Joe")
    assertThat(sqlBinder?.params?.get("age1")).isEqualTo(BigDecimal(35))
  }

  @Test
  fun `test sql writer with multiple parameters and combine or`() {
    val params = listOf("name" to "Joe", "or__age_gt" to "35").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND b.name = :name0 OR b.age > :age1")
    assertThat(sqlBinder?.params?.get("name0")).isEqualTo("Joe")
    assertThat(sqlBinder?.params?.get("age1")).isEqualTo(BigDecimal(35))
  }

  @Test
  fun `test sql writer with multiple groups and parameters`() {
    val params = listOf("name__1" to "Joe", "or__age_gt__1" to "35", "gender__2" to "MALE").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val sqlBinder = filter.toSqlBinder(ReactiveUser::class.java, "b")

    assertThat(sqlBinder?.sql).isEqualTo(" AND ((b.name = :name100 OR b.age > :age101) AND (b.gender = :gender200))")
    assertThat(sqlBinder?.params?.get("name100")).isEqualTo("Joe")
    assertThat(sqlBinder?.params?.get("age101")).isEqualTo(BigDecimal(35))
    assertThat(sqlBinder?.params?.get("gender200")).isEqualTo("MALE")
  }
}
