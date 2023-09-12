package io.github.verissimor.lib.fieldparser

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FieldParserGroupTest {

  @Test
  fun `should default group to zero`() {
    val params = listOf("name" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().group).isEqualTo(0)
  }

  @Test
  fun `should parse group`() {
    val params = listOf("name__1" to "Joe", "name" to "Jane", "name__2" to "Will").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(3)

    // group 1
    val group1 = parsed.first { it.group == 1 }
    assertThat(group1.group).isEqualTo(1)
    assertThat(group1.getString()).isEqualTo("Joe")

    // group 0
    val group0 = parsed.first { it.group == 0 }
    assertThat(group0.group).isEqualTo(0)
    assertThat(group0.getString()).isEqualTo("Jane")

    // group 1
    val group2 = parsed.first { it.group == 2 }
    assertThat(group2.group).isEqualTo(2)
    assertThat(group2.getString()).isEqualTo("Will")
  }
}
