package io.github.verissimor.lib.fieldparser

import io.github.verissimor.lib.fieldparser.domain.FilterOperator.BETWEEN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.GREATER_THAN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.GREATER_THAN_EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.IN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.IS_NOT_NULL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.IS_NULL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LESS_THAN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LESS_THAN_EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LIKE
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LIKE_EXP
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_IN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_LIKE
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_LIKE_EXP
import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FieldParserOperatorTest {

  @Test
  fun `should parse operator EQUAL`() {
    val params = listOf("name" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(EQUAL)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse operator NOT_EQUAL`() {
    val params = listOf("name_ne" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(NOT_EQUAL)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse operator GREATER_THAN`() {
    val params = listOf("age_gt" to "25").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(GREATER_THAN)
    assertThat(parsed.first().getString()).isEqualTo("25")
  }

  @Test
  fun `should parse operator GREATER_THAN_EQUAL`() {
    val params = listOf("age_ge" to "25").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(GREATER_THAN_EQUAL)
    assertThat(parsed.first().getString()).isEqualTo("25")
  }

  @Test
  fun `should parse operator LESS_THAN`() {
    val params = listOf("age_lt" to "25").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(LESS_THAN)
    assertThat(parsed.first().getString()).isEqualTo("25")
  }

  @Test
  fun `should parse operator LESS_THAN_EQUAL`() {
    val params = listOf("age_le" to "25").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(LESS_THAN_EQUAL)
    assertThat(parsed.first().getString()).isEqualTo("25")
  }

  @Test
  fun `should parse operator LIKE`() {
    val params = listOf("name_like" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(LIKE)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse operator LIKE_EXP`() {
    val params = listOf("name_like_exp" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(LIKE_EXP)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse operator NOT_LIKE`() {
    val params = listOf("name_not_like" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(NOT_LIKE)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse operator NOT_LIKE_EXP`() {
    val params = listOf("name_not_like_exp" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(NOT_LIKE_EXP)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse operator IN`() {
    val params = listOf("name_in" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(IN)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse operator NOT_IN`() {
    val params = listOf("name_not_in" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(NOT_IN)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse operator IS_NULL`() {
    val params = listOf("name_is_null" to null).toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(IS_NULL)
    assertThat(parsed.first().getStringOrNull()).isEqualTo(null)
  }

  @Test
  fun `should parse operator IS_NOT_NULL`() {
    val params = listOf("name_is_not_null" to null).toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(IS_NOT_NULL)
    assertThat(parsed.first().getStringOrNull()).isEqualTo(null)
  }

  @Test
  fun `should parse field type BETWEEN`() {
    val params = listOf("age_is_between" to "21,30").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(BETWEEN)
    assertThat(parsed.first().getListStringOrNull()).contains("21", "30")
  }

  @Test
  fun `should assert field type BETWEEN has two parameters`() {
    val params = listOf("age_is_between" to "21").toMultiMap()

    assertThrows<IllegalStateException> {
      FieldParser.parseFields(params, ReactiveUser::class.java)
    }
  }

  @Test
  fun `should overload field type EQUAL to BETWEEN for LocalDate`() {
    val params = listOf("createdDate" to "2023-01-01", "createdDate" to "2023-12-31").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(BETWEEN)
    assertThat(parsed.first().getListStringOrNull()).contains("2023-01-01", "2023-12-31")
  }

  @Test
  fun `should overload field type EQUAL to BETWEEN for Instant`() {
    val params = listOf("createdAt" to "1672531200", "createdAt" to "1672417200").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(BETWEEN)
    assertThat(parsed.first().getListStringOrNull()).contains("1672417200", "1672417200")
  }

  @Test
  fun `should overload field type EQUAL to IN for repeated values`() {
    val params = listOf("id" to "1", "id" to "2").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(IN)
    assertThat(parsed.first().getListStringOrNull()).contains("1", "2")
  }

  @Test
  fun `should overload field type repeated EQUAL to IN for comma values`() {
    val params = listOf("id" to "1, 2").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().filterOperator).isEqualTo(IN)
    assertThat(parsed.first().getListStringOrNull()).contains("1", "2")
  }

  @Test
  fun `should validate operator works with type`() {
    val params = listOf("name_ge" to "Joe").toMultiMap()
    assertThrows<IllegalStateException> {
      FieldParser.parseFields(params, ReactiveUser::class.java)
    }
  }
}
