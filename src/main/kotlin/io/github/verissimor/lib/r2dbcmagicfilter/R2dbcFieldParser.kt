package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.jpamagicfilter.domain.FieldType
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator
import io.github.verissimor.lib.jpamagicfilter.domain.ParsedField
import io.github.verissimor.lib.jpamagicfilter.toSingleString
import io.github.verissimor.lib.r2dbcmagicfilter.domain.R2dbcParsedField
import java.lang.reflect.Field

object R2dbcFieldParser {

  fun parseField(field: String, value: Array<String>?, clazz: Class<*>): R2dbcParsedField {
    val normalized = normalize(field)
    val filterOperator = fieldToFilterOperator(normalized)
    val resolvedFieldName = resolveFieldName(normalized, filterOperator)
    val fieldClass: Field? = fieldToClass(resolvedFieldName, clazz)

    val resolvedOperator = overloadFilterOperator(filterOperator, value, fieldClass)

    return R2dbcParsedField(resolvedOperator, resolvedFieldName, fieldClass)
  }

  private fun normalize(field: String) = field.trim().replace("[]", "")

  private fun fieldToFilterOperator(field: String): FilterOperator {
    val type = FilterOperator.values()
      .sortedByDescending { it.suffix.length }
      .firstOrNull { field.contains(it.suffix) } ?: FilterOperator.EQUAL

    return type
  }

  private fun overloadFilterOperator(filterOperator: FilterOperator, value: Array<String>?, fieldClass: Field?): FilterOperator {
    val shouldTryOverload = value != null && filterOperator == FilterOperator.EQUAL
    if (shouldTryOverload && value!!.size > 1) {
      return FilterOperator.IN
    }

    val fieldType: FieldType? = ParsedField.getFieldType(fieldClass)
    if (shouldTryOverload && fieldType == FieldType.NUMBER && value!!.toSingleString()!!.contains(",")) {
      return FilterOperator.IN
    }

    return filterOperator
  }

  private fun resolveFieldName(field: String, type: FilterOperator?) =
    type?.let { field.replace(it.suffix, "") } ?: field

  private fun fieldToClass(field: String, root: Class<*>): Field? {
    var resultField: Field? = null
    field.split(".")
      .forEach { fieldP ->
        resultField = if (resultField == null) {
          root.declaredFields.firstOrNull { it.name == fieldP }
        } else {
          resultField?.type?.declaredFields?.firstOrNull { it.name == fieldP }
        }
      }

    return resultField
  }
}
