package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class R2dbcMagicFilterGreaterTest {
  @Test
  fun `test greater string`() {
    val params = listOf("name_gt" to "Joe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    assertThrows<IllegalStateException> { filter.toCriteria(ReactiveUser::class.java) }
  }

  @Test
  fun `test greater integer`() {
    val params = listOf("age_gt" to "19").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("age > 19")
  }

  @Test
  fun `test greater long`() {
    val params = listOf("id_gt" to "1").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("id > 1")
  }

  @Test
  fun `test greater date`() {
    val params = listOf("createdDate_gt" to "2022-12-31").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdDate > '2022-12-31'")
  }

  @Test
  fun `test greater instant`() {
    val now = Instant.now().toString()
    val params = listOf("createdAt_gt" to now).toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdAt > '$now'")
  }

  @Test
  fun `test greater equals string`() {
    val params = listOf("name_ge" to "Joe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    assertThrows<IllegalStateException> { filter.toCriteria(ReactiveUser::class.java) }
  }

  @Test
  fun `test greater equals integer`() {
    val params = listOf("age_ge" to "19").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("age >= 19")
  }

  @Test
  fun `test greater equals long`() {
    val params = listOf("id_ge" to "1").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("id >= 1")
  }

  @Test
  fun `test greater equals date`() {
    val params = listOf("createdDate_ge" to "2022-12-31").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdDate >= '2022-12-31'")
  }

  @Test
  fun `test greater equals instant`() {
    val now = Instant.now().toString()
    val params = listOf("createdAt_ge" to now).toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdAt >= '$now'")
  }
}
