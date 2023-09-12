package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class R2dbcMagicFilterCombineOperatorTest {
  @Test
  fun `test support for groups and operators`() {
    val params = listOf("name" to "Joe", "or__age" to "21", "name__1" to "William", "or__age__1" to "35").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("((name = 'Joe') OR (age = 21)) AND ((name = 'William') OR (age = 35))")
  }
}
