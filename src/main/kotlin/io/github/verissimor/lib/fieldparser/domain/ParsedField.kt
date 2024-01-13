package io.github.verissimor.lib.fieldparser.domain

import io.github.verissimor.lib.fieldparser.domain.FieldType.Companion.comparisonOperators
import io.github.verissimor.lib.fieldparser.domain.FieldType.Companion.comparisonTypes
import io.github.verissimor.lib.fieldparser.domain.FieldType.Companion.toFieldType
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.BETWEEN
import java.lang.reflect.Field

data class ParsedField(
  val filterOperator: FilterOperator,
  val resolvedFieldName: String,
  val fieldClass: Field?,
  val sourceValue: List<String>?,
  val group: Int,
  val combineOperator: CombineOperator,
) : ValueParser(resolvedFieldName, sourceValue) {
  fun getFieldType(): FieldType? = fieldClass.toFieldType()

  fun validate() {
    if (comparisonOperators.contains(filterOperator) && !comparisonTypes.contains(getFieldType())) {
      error("The field $resolvedFieldName is not compatible with operator $filterOperator")
    }

    if (filterOperator == BETWEEN && getListStringOrNull()?.size != 2) {
      error("The field $resolvedFieldName uses the $filterOperator which requires 2 parameters $sourceValue")
    }
  }
}
