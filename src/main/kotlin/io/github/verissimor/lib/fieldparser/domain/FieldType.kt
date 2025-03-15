package io.github.verissimor.lib.fieldparser.domain

import io.github.verissimor.lib.fieldparser.domain.FilterOperator.BETWEEN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.GREATER_THAN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.GREATER_THAN_EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LESS_THAN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LESS_THAN_EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_BETWEEN
import java.lang.reflect.Field
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

enum class FieldType {
  ENUMERATED,
  NUMBER,
  LOCAL_DATE,
  INSTANT,
  BOOLEAN,
  UUID,
  GENERIC,
  ;

  companion object {
    val comparisonOperators = listOf(GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN, LESS_THAN_EQUAL, BETWEEN, NOT_BETWEEN)
    val comparisonTypes = listOf(NUMBER, LOCAL_DATE, INSTANT)

    fun Field?.toFieldType(): FieldType? =
      when {
        this == null -> null
        this.type?.superclass?.name == "java.lang.Enum" -> ENUMERATED
        this.type == LocalDate::class.java -> LOCAL_DATE
        this.type == Instant::class.java -> INSTANT
        this.type == Boolean::class.java ||
          // this solves conflicts between kotlin/java
          this.type.name == "java.lang.Boolean" -> BOOLEAN

        this.type == java.util.UUID::class.java -> UUID

        this.type == Int::class.java ||
          this.type == Long::class.java ||
          this.type == BigDecimal::class.java ||
          this.type.isAssignableFrom(Number::class.java) ||
          // this solves conflicts between kotlin/java
          this.type.name == "java.lang.Integer" ||
          this.type.name == "java.math.BigDecimal" ||
          this.type.name == "java.lang.Long" ||
          this.type.isAssignableFrom(java.lang.Number::class.java) -> NUMBER

        else -> GENERIC
      }
  }
}
