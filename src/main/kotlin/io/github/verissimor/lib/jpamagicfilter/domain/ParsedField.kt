package io.github.verissimor.lib.jpamagicfilter.domain

import io.github.verissimor.lib.fieldparser.domain.FieldType
import io.github.verissimor.lib.fieldparser.domain.FieldType.Companion.toFieldType
import io.github.verissimor.lib.fieldparser.domain.FilterOperator
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Root
import java.lang.reflect.Field

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

  fun getFieldType(): FieldType? = fieldClass?.toFieldType()
}
