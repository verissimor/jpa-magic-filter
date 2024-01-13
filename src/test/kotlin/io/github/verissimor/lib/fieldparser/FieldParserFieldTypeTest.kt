package io.github.verissimor.lib.fieldparser

import io.github.verissimor.lib.fieldparser.domain.FieldType.BOOLEAN
import io.github.verissimor.lib.fieldparser.domain.FieldType.ENUMERATED
import io.github.verissimor.lib.fieldparser.domain.FieldType.GENERIC
import io.github.verissimor.lib.fieldparser.domain.FieldType.INSTANT
import io.github.verissimor.lib.fieldparser.domain.FieldType.LOCAL_DATE
import io.github.verissimor.lib.fieldparser.domain.FieldType.NUMBER
import io.github.verissimor.lib.fieldparser.domain.FieldType.UUID
import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class FieldParserFieldTypeTest {
  @Test
  fun `should parse field type Generic String`() {
    val params = listOf("name" to "Joe").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(GENERIC)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
  }

  @Test
  fun `should parse field type Enumerated`() {
    val params = listOf("gender" to "FEMALE").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(ENUMERATED)
    assertThat(parsed.first().getString()).isEqualTo("FEMALE")
  }

  @Test
  fun `should parse field type Number`() {
    val params = listOf("age" to "21").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(NUMBER)
    assertThat(parsed.first().getBigDecimal()).isEqualTo(BigDecimal(21))
  }

  @Test
  fun `should parse field type Local date`() {
    val params = listOf("createdDate" to "2023-01-01").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(LOCAL_DATE)
    assertThat(parsed.first().getLocalDate()).isEqualTo(LocalDate.parse("2023-01-01"))
  }

  @Test
  fun `should parse field type Instant`() {
    val params = listOf("createdAt" to "2023-09-08T10:21:05.482094Z").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(INSTANT)
    assertThat(parsed.first().getInstant()).isEqualTo(Instant.parse("2023-09-08T10:21:05.482094Z"))
  }

  @Test
  fun `should parse field type Instant Epoch`() {
    val params = listOf("createdAt" to "1694170702").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(INSTANT)
    assertThat(parsed.first().getInstant()).isEqualTo(Instant.ofEpochSecond(1694170702))
  }

  @Test
  fun `should parse field type Boolean`() {
    val params = listOf("enabled" to "true").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(BOOLEAN)
    assertThat(parsed.first().getBoolean()).isEqualTo(true)
  }

  @Test
  fun `should parse field type Boolean on numeric basis`() {
    val params = listOf("enabled" to "1").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(BOOLEAN)
    assertThat(parsed.first().getBoolean()).isEqualTo(true)
  }

  @Test
  fun `should parse field type UUID`() {
    val params = listOf("uuid" to "EE22D2E2-5492-43A3-BCD5-8A76BF84D7D9").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(1)
    assertThat(parsed.first().getFieldType()).isEqualTo(UUID)
    assertThat(parsed.first().getUUID()).isEqualTo(java.util.UUID.fromString("EE22D2E2-5492-43A3-BCD5-8A76BF84D7D9"))
  }

  @Test
  fun `should parse many fields`() {
    val params = listOf("name" to "Joe", "age" to "22").toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).hasSize(2)
    assertThat(parsed.first().getFieldType()).isEqualTo(GENERIC)
    assertThat(parsed.first().getString()).isEqualTo("Joe")
    assertThat(parsed[1].getFieldType()).isEqualTo(NUMBER)
    assertThat(parsed[1].getString()).isEqualTo("22")
  }

  @Test
  fun `should ignore filter if value is null`() {
    val params = listOf("name" to null).toMultiMap()
    val parsed = FieldParser.parseFields(params, ReactiveUser::class.java)

    assertThat(parsed).isEmpty()
  }
}
