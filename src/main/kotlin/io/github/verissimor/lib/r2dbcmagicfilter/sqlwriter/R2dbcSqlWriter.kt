package io.github.verissimor.lib.r2dbcmagicfilter.sqlwriter

import io.github.verissimor.lib.fieldparser.domain.FieldType.BOOLEAN
import io.github.verissimor.lib.fieldparser.domain.FieldType.ENUMERATED
import io.github.verissimor.lib.fieldparser.domain.FieldType.GENERIC
import io.github.verissimor.lib.fieldparser.domain.FieldType.INSTANT
import io.github.verissimor.lib.fieldparser.domain.FieldType.LOCAL_DATE
import io.github.verissimor.lib.fieldparser.domain.FieldType.NUMBER
import io.github.verissimor.lib.fieldparser.domain.FieldType.UUID
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.BETWEEN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.GREATER_THAN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.GREATER_THAN_EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.IN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.IS_NOT_NULL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.IS_NULL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LESS_THAN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LESS_THAN_EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LIKE
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.LIKE_EXP
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_EQUAL
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_IN
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_LIKE
import io.github.verissimor.lib.fieldparser.domain.FilterOperator.NOT_LIKE_EXP
import io.github.verissimor.lib.fieldparser.domain.ParsedField

object R2dbcSqlWriter {
  fun writeSql(
    parsedFields: List<ParsedField>,
    tableAlias: String? = null,
  ): SqlBinder {
    val alias =
      when {
        tableAlias == null -> ""
        tableAlias.endsWith(".") -> tableAlias
        else -> "$tableAlias."
      }

    val params = mutableMapOf<String, Any>()

    val sqlWhere =
      parsedFields.mapIndexed { index: Int, field: ParsedField ->
        val andOr = if (index > 0) field.combineOperator.toString() else ""
        val fieldName = alias + camelToSnake(field.resolvedFieldName)
        val paramIndex = (field.group * 100) + index
        val fieldParam = field.resolvedFieldName + paramIndex

        when (field.filterOperator) {
          GREATER_THAN -> {
            // AND b.name > :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName > :$fieldParam"
          }
          GREATER_THAN_EQUAL -> {
            // AND b.name >= :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName >= :$fieldParam"
          }
          LESS_THAN -> {
            // AND b.name < :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName < :$fieldParam"
          }
          LESS_THAN_EQUAL -> {
            // AND b.name <= :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName <= :$fieldParam"
          }
          LIKE_EXP -> {
            // AND b.name LIKE :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName LIKE :$fieldParam"
          }
          LIKE -> {
            // AND b.name LIKE :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName LIKE :$fieldParam"
          }
          NOT_LIKE_EXP -> {
            // AND b.name NOT LIKE :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName NOT LIKE :$fieldParam"
          }
          NOT_LIKE -> {
            // AND b.name NOT LIKE :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName NOT LIKE :$fieldParam"
          }
          IN -> {
            // AND b.name NOT IN (:name1)
            params[fieldParam] = resolveListFieldValue(field)
            "$andOr $fieldName IN (:$fieldParam)"
          }
          NOT_IN -> {
            // AND b.name NOT IN (:name1)
            params[fieldParam] = resolveListFieldValue(field)
            "$andOr $fieldName NOT IN (:$fieldParam)"
          }
          IS_NULL -> {
            // AND b.name IS NULL
            "$andOr $fieldName IS NULL"
          }
          IS_NOT_NULL -> {
            // AND b.name IS NULL
            "$andOr $fieldName IS NOT NULL"
          }
          BETWEEN -> {
            params["${fieldParam}a"] = resolveListFieldValue(field)[0]
            params["${fieldParam}b"] = resolveListFieldValue(field)[1]
            "$andOr $fieldName BETWEEN :${fieldParam}a AND :${fieldParam}b"
          }
          NOT_EQUAL -> {
            // AND b.name <> :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName <> :$fieldParam"
          }
          EQUAL -> {
            // AND b.name = :name1
            params[fieldParam] = resolveFieldValue(field)
            "$andOr $fieldName = :$fieldParam"
          }
        }
      }.joinToString(separator = " ") { it.trim() }

    return SqlBinder(sqlWhere, params)
  }

  private fun resolveFieldValue(parsed: ParsedField) =
    when (parsed.getFieldType()) {
      ENUMERATED -> parsed.getString()
      NUMBER -> parsed.getBigDecimal()
      LOCAL_DATE -> parsed.getLocalDate()
      INSTANT -> parsed.getInstant()
      BOOLEAN -> parsed.getBoolean()
      UUID -> parsed.getUUID()
      GENERIC -> parsed.getString()
      null -> error("Invalid fieldType")
    }

  private fun resolveListFieldValue(parsed: ParsedField) =
    when (parsed.getFieldType()) {
      ENUMERATED -> parsed.getListString()
      NUMBER -> parsed.getListBigDecimal()
      LOCAL_DATE -> parsed.getListLocalDate()
      INSTANT -> parsed.getListInstant()
      BOOLEAN -> parsed.getListBoolean()
      UUID -> parsed.getListUUID()
      GENERIC -> parsed.getListString()
      null -> error("Invalid fieldType")
    }

  private fun camelToSnake(camelCase: String): String {
    val pattern = "(?<=[a-zA-Z])[A-Z]".toRegex()
    return pattern.replace(camelCase) { "_${it.value}" }.lowercase()
  }
}
