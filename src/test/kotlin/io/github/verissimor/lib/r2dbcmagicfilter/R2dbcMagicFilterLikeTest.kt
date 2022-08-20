package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class R2dbcMagicFilterLikeTest {
  @Test
  fun `test like string`() {
    val params = listOf("name_like" to "joe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)
    // when applied to sql this will become: upper(name) like upper('%joe%')
    // see PredicateParser.kt: EQUAL -> parseEqual(parsedField, value).ignoreCase(true)
    assertThat(criteria.toString()).isEqualTo("name LIKE '%joe%'")
  }

  @Test
  fun `test like exp string`() {
    val params = listOf("name_like_exp" to "j%e").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)
    assertThat(criteria.toString()).isEqualTo("name LIKE 'j%e'")
  }

  @Test
  fun `test not like string`() {
    val params = listOf("name_not_like" to "joe").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)
    assertThat(criteria.toString()).isEqualTo("name NOT LIKE '%joe%'")
  }

  @Test
  fun `test not like exp string`() {
    val params = listOf("name_not_like_exp" to "j%e").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)
    assertThat(criteria.toString()).isEqualTo("name NOT LIKE 'j%e'")
  }
}
