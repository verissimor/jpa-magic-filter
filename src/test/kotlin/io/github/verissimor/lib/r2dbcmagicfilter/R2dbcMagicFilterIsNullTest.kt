package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class R2dbcMagicFilterIsNullTest {
  @Test
  fun `test is null string`() {
    val params = listOf("name_is_null" to null).toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("name IS NULL")
  }

  @Test
  fun `test is not null string`() {
    val params = listOf("name_is_not_null" to null).toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("name IS NOT NULL")
  }
}
