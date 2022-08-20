package io.github.verissimor.lib.r2dbcmagicfilter.domain

import io.github.verissimor.lib.jpamagicfilter.domain.FieldType
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator
import io.github.verissimor.lib.jpamagicfilter.domain.ParsedField
import java.lang.reflect.Field

data class R2dbcParsedField(
  val filterOperator: FilterOperator,
  val resolvedFieldName: String,
  val fieldClass: Field?,
) {

  fun getFieldType(): FieldType? = ParsedField.getFieldType(fieldClass)
}
