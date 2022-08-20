package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class R2dbcMagicFilterLowerTest {
  @Test
  fun `test lower string`() {
    val params = listOf("name_lt" to "Joe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    assertThrows<IllegalStateException> { filter.toCriteria(ReactiveUser::class.java) }
  }

  @Test
  fun `test lower integer`() {
    val params = listOf("age_lt" to "19").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("age < 19")
  }

  @Test
  fun `test lower long`() {
    val params = listOf("id_lt" to "1").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("id < 1")
  }

  @Test
  fun `test lower date`() {
    val params = listOf("createdDate_lt" to "2022-12-31").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdDate < '2022-12-31'")
  }

  @Test
  fun `test lower instant`() {
    val now = Instant.now().toString()
    val params = listOf("createdAt_lt" to now).toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdAt < '$now'")
  }

  @Test
  fun `test lower equals string`() {
    val params = listOf("name_le" to "Joe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    assertThrows<IllegalStateException> { filter.toCriteria(ReactiveUser::class.java) }
  }

  @Test
  fun `test lower equals integer`() {
    val params = listOf("age_le" to "19").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("age <= 19")
  }

  @Test
  fun `test lower equals long`() {
    val params = listOf("id_le" to "1").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("id <= 1")
  }

  @Test
  fun `test lower equals date`() {
    val params = listOf("createdDate_le" to "2022-12-31").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdDate <= '2022-12-31'")
  }

  @Test
  fun `test lower equals instant`() {
    val now = Instant.now().toString()
    val params = listOf("createdAt_le" to now).toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdAt <= '$now'")
  }
}
