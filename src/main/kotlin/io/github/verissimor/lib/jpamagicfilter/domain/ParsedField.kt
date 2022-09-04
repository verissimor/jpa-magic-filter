package io.github.verissimor.lib.jpamagicfilter.domain

import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.BOOLEAN
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.ENUMERATED
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.INSTANT
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.LOCAL_DATE
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.NUMBER
import java.lang.reflect.Field
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
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

  fun getFieldType(): FieldType? = getFieldType(fieldClass)

  companion object {
    fun getFieldType(fieldClass: Field?): FieldType? = when {
      fieldClass == null -> null
      fieldClass.type?.superclass?.name == "java.lang.Enum" -> ENUMERATED
      fieldClass.type == LocalDate::class.java -> LOCAL_DATE
      fieldClass.type == Instant::class.java -> INSTANT
      fieldClass.type == Boolean::class.java ||
        // this solves conflicts between kotlin/java
        fieldClass.type.name == "java.lang.Boolean" -> BOOLEAN

      fieldClass.type == UUID::class.java -> FieldType.UUID

      fieldClass.type == Int::class.java ||
        fieldClass.type == Long::class.java ||
        fieldClass.type == BigDecimal::class.java ||
        fieldClass.type.isAssignableFrom(Number::class.java) ||
        // this solves conflicts between kotlin/java
        fieldClass.type.name == "java.lang.Integer" ||
        fieldClass.type.name == "java.math.BigDecimal" ||
        fieldClass.type.name == "java.lang.Long" ||
        fieldClass.type.isAssignableFrom(java.lang.Number::class.java) -> NUMBER

      else -> FieldType.GENERIC
    }
  }
}
