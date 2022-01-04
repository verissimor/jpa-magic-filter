package com.verissimor.lib.jpamagicfilter.domain

import com.verissimor.lib.jpamagicfilter.domain.FieldType.BOOLEAN
import com.verissimor.lib.jpamagicfilter.domain.FieldType.ENUMERATED
import com.verissimor.lib.jpamagicfilter.domain.FieldType.INSTANT
import com.verissimor.lib.jpamagicfilter.domain.FieldType.LOCAL_DATE
import com.verissimor.lib.jpamagicfilter.domain.FieldType.NUMBER
import java.lang.reflect.Field
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import javax.persistence.Enumerated
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root

data class ParsedField<T>(
  val root: Root<T>,
  val filterOperator: FilterOperator,
  val resolvedFieldName: String,
  val fieldClass: Field?,
) {

  fun <Y> getPath(): Path<Y> {
    var fullPath: Path<Y>? = null
    resolvedFieldName.split(".")
      .forEach { fieldP ->
        fullPath = (fullPath ?: root).get(fieldP)
      }

    return fullPath ?: error("unexpected condition to parse path of $resolvedFieldName")
  }

  fun getFieldType(): FieldType? = when {
    fieldClass == null -> null
    fieldClass.getDeclaredAnnotationsByType(Enumerated::class.java).isNotEmpty() -> ENUMERATED
    fieldClass.type == LocalDate::class.java -> LOCAL_DATE
    fieldClass.type == Instant::class.java -> INSTANT
    fieldClass.type == Boolean::class.java -> BOOLEAN

    fieldClass.type == Int::class.java ||
      fieldClass.type == Long::class.java ||
      fieldClass.type == BigDecimal::class.java ||
      fieldClass.type.isNestmateOf(Number::class.java) -> NUMBER

    else -> FieldType.GENERIC
  }
}
