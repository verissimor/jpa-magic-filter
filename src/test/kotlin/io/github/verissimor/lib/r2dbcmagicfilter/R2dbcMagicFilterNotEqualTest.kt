package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class R2dbcMagicFilterNotEqualTest {
  @Test
  fun `test equals string`() {
    val params = listOf("name_ne" to "Joe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("name != 'Joe'")
  }

  @Test
  fun `test equals enum`() {
    val params = listOf("gender_ne" to "MALE").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("gender != 'MALE'")
  }

  @Test
  fun `test equals integer`() {
    val params = listOf("age_ne" to "19").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("age != 19")
  }

  @Test
  fun `test equals long`() {
    val params = listOf("id_ne" to "1").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("id != 1")
  }

  @Test
  fun `test equals date`() {
    val params = listOf("createdDate_ne" to "2022-12-31").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdDate != '2022-12-31'")
  }

  @Test
  fun `test equals instant`() {
    val now = Instant.now().toString()
    val params = listOf("createdAt_ne" to now).toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdAt != '$now'")
  }

  @Test
  fun `test equals boolean`() {
    val params = listOf("enabled_ne" to "0").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("enabled != 'false'")
  }
}
