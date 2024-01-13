package io.github.verissimor.lib.jpamagicfilter

import io.github.verissimor.lib.fieldparser.domain.FilterOperator
import io.github.verissimor.lib.jpamagicfilter.domain.ParsedField
import jakarta.persistence.criteria.Root
import java.lang.reflect.Field

object FieldParser {
  fun <T> parseField(
    field: String,
    value: Array<String>?,
    clazz: Class<*>,
    root: Root<T>,
  ): ParsedField<T> {
    val normalized = normalize(field)
    val filterType = fieldToType(normalized, value)
    val resolvedFieldName = resolveFieldName(normalized, filterType)
    val fieldClass: Field? = fieldToClass(resolvedFieldName, clazz)

    return ParsedField(root, filterType, resolvedFieldName, fieldClass)
  }

  private fun normalize(field: String) = field.trim().replace("[]", "")

  private fun fieldToType(
    field: String,
    value: Array<String>?,
  ): FilterOperator {
    val type =
      FilterOperator.values()
        .sortedByDescending { it.suffix.length }
        .firstOrNull { field.contains(it.suffix) } ?: FilterOperator.EQUAL

    if (value != null && type == FilterOperator.EQUAL && value.size > 1) {
      return FilterOperator.IN
    }

    return type
  }

  private fun resolveFieldName(
    field: String,
    type: FilterOperator?,
  ) = type?.let { field.replace(it.suffix, "") } ?: field

  private fun fieldToClass(
    field: String,
    root: Class<*>,
  ): Field? {
    var resultField: Field? = null
    field.split(".")
      .forEach { fieldP ->
        resultField =
          if (resultField == null) {
            root.declaredFields.firstOrNull { it.name == fieldP }
          } else {
            resultField?.type?.declaredFields?.firstOrNull { it.name == fieldP }
          }
      }

    return resultField
  }
}
