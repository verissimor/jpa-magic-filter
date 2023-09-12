package io.github.verissimor.lib.fieldparser

import io.github.verissimor.lib.fieldparser.domain.CombineOperator
import io.github.verissimor.lib.fieldparser.domain.CombineOperator.AND
import io.github.verissimor.lib.fieldparser.domain.CombineOperator.OR
import io.github.verissimor.lib.fieldparser.domain.FieldType
import io.github.verissimor.lib.fieldparser.domain.FieldType.Companion.toFieldType
import io.github.verissimor.lib.fieldparser.domain.FieldType.ENUMERATED
import io.github.verissimor.lib.fieldparser.domain.FieldType.INSTANT
import io.github.verissimor.lib.fieldparser.domain.FieldType.LOCAL_DATE
import io.github.verissimor.lib.fieldparser.domain.FieldType.NUMBER
import io.github.verissimor.lib.fieldparser.domain.FieldType.UUID
import io.github.verissimor.lib.fieldparser.domain.FilterOperator
import io.github.verissimor.lib.fieldparser.domain.ParsedField
import io.github.verissimor.lib.fieldparser.domain.ValueParser.Companion.parseStringIntoList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Field

object FieldParser {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun parseFields(params: Map<String, List<String>?>, clazz: Class<*>): List<ParsedField> {
    return params.mapNotNull { (field, value) ->

      val parsedField = parseField(field, value, clazz)
      parsedField.validate()

      if (parsedField.getStringOrNull() == null && !parsedField.filterOperator.allowNullableValue) {
        log.debug("Ignoring parameter $field - value is null (you can use _is_null)")
        return@mapNotNull null
      }

      if (parsedField.fieldClass == null) {
        log.debug("Ignoring parameter $field - field not found")
        return@mapNotNull null
      }

      parsedField
    }
  }

  private fun parseField(field: String, value: List<String>?, clazz: Class<*>): ParsedField {
    val group: Int = parseGroup(field)
    val combineOperator: CombineOperator = if (field.startsWith("or__")) OR else AND
    val normalized = normalize(field, group)

    val filterOperator = fieldToFilterOperator(normalized)
    val resolvedFieldName = resolveFieldName(normalized, filterOperator)
    val fieldClass: Field? = fieldToClass(resolvedFieldName, clazz)

    val resolvedOperator = overloadFilterOperator(filterOperator, value, fieldClass)

    return ParsedField(resolvedOperator, resolvedFieldName, fieldClass, value, group, combineOperator)
  }

  private fun normalize(field: String, group: Int) = field.trim()
    .replace("[]", "") // remove array format of a few js libraries
    .replace("__$group", "") // remove the group
    .let { if (it.startsWith("or__")) it.replace("or__", "") else it } // remove the or

  private fun fieldToFilterOperator(field: String): FilterOperator {
    val type = FilterOperator.values()
      .sortedByDescending { it.suffix.length }
      .firstOrNull { field.lowercase().endsWith(it.suffix) } ?: FilterOperator.EQUAL

    return type
  }

  private fun overloadFilterOperator(
    filterOperator: FilterOperator,
    value: List<String>?,
    fieldClass: Field?
  ): FilterOperator {
    val shouldTryOverload = value != null && filterOperator == FilterOperator.EQUAL
    val fieldType: FieldType? = fieldClass.toFieldType()

    if (shouldTryOverload && value!!.size == 2 && (fieldType == LOCAL_DATE || fieldType == INSTANT)) {
      return FilterOperator.BETWEEN
    }

    if (shouldTryOverload && value!!.size > 1) {
      return FilterOperator.IN
    }

    val shouldTrySplitComma = value!!.size == 1 && listOf(ENUMERATED, NUMBER, LOCAL_DATE, UUID).contains(fieldType)
    if (shouldTryOverload && shouldTrySplitComma && value.firstOrNull()!!.contains(",")) {
      val list = parseStringIntoList(value.firstOrNull()!!)
      if (list?.isNotEmpty() == true)
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

  internal fun parseGroup(field: String): Int {
    val regex = "__(\\d+)$".toRegex()
    val matchResult = regex.find(field)

    return matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
  }
}
