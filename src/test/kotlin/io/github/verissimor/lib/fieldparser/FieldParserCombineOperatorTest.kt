package io.github.verissimor.lib.fieldparser

import io.github.verissimor.lib.fieldparser.domain.CombineOperator.AND
import io.github.verissimor.lib.fieldparser.domain.CombineOperator.OR
import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FieldParserCombineOperatorTest {

  @Test
  fun `should parse combine operator`() {
    val params = listOf("name" to "Joe", "or__name" to "Jane").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(2)
    assertThat(parsed.first().combineOperator).isEqualTo(AND)
    assertThat(parsed[1].combineOperator).isEqualTo(OR)
  }

  @Test
  fun `should parse groups and operators`() {
    val params = listOf("name" to "Joe", "or__name" to "Jane", "name__1" to "Joe", "or__name__1" to "Jane").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(4)
    assertThat(parsed[0].combineOperator).isEqualTo(AND)
    assertThat(parsed[0].group).isEqualTo(0)

    assertThat(parsed[1].combineOperator).isEqualTo(OR)
    assertThat(parsed[1].group).isEqualTo(0)

    assertThat(parsed[2].combineOperator).isEqualTo(AND)
    assertThat(parsed[2].group).isEqualTo(1)

    assertThat(parsed[3].combineOperator).isEqualTo(OR)
    assertThat(parsed[3].group).isEqualTo(1)
  }
}
