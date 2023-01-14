package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_IN_SEPARATOR_DEF
import io.github.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_IN_SEPARATOR_PRM
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures.NONE
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures.POSTGRES
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.BOOLEAN
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.ENUMERATED
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.GENERIC
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.INSTANT
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.LOCAL_DATE
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.NUMBER
import io.github.verissimor.lib.jpamagicfilter.domain.FieldType.UUID
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.EQUAL
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.GREATER_THAN
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.GREATER_THAN_EQUAL
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.IN
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.IS_NOT_NULL
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.IS_NULL
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.LESS_THAN
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.LESS_THAN_EQUAL
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.LIKE
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.LIKE_EXP
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.NOT_EQUAL
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.NOT_IN
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.NOT_LIKE
import io.github.verissimor.lib.jpamagicfilter.domain.FilterOperator.NOT_LIKE_EXP
import io.github.verissimor.lib.jpamagicfilter.toSingleBigDecimal
import io.github.verissimor.lib.jpamagicfilter.toSingleBoolean
import io.github.verissimor.lib.jpamagicfilter.toSingleDate
import io.github.verissimor.lib.jpamagicfilter.toSingleInstant
import io.github.verissimor.lib.jpamagicfilter.toSingleParameter
import io.github.verissimor.lib.jpamagicfilter.toSingleString
import io.github.verissimor.lib.jpamagicfilter.unaccent
import io.github.verissimor.lib.r2dbcmagicfilter.domain.R2dbcParsedField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import java.time.Instant
import java.time.LocalDate

object R2dbcPredicateParser {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun parsePredicates(
    params: Map<String, Array<String>?>,
    clazz: Class<*>,
    dbFeatures: DbFeatures,
  ): List<Criteria> = params.mapNotNull { (field, value) ->
    val parsedField: R2dbcParsedField = R2dbcFieldParser.parseField(field, value, clazz)

    if (parsedField.fieldClass == null) {
      log.debug("Ignoring parameter $field - field not found")
      return@mapNotNull null
    }

    if (value == null) {
      log.debug("Ignoring parameter $field - value is null (you can use _is_null)")
      return@mapNotNull null
    }

    when (parsedField.filterOperator) {
      EQUAL -> parseEqual(parsedField, value)
      NOT_EQUAL -> parseNotEqual(parsedField, value)

      GREATER_THAN -> parseGreaterThan(parsedField, value)
      GREATER_THAN_EQUAL -> parseGreaterThanEqual(parsedField, value)
      LESS_THAN -> parseLessThan(parsedField, value)
      LESS_THAN_EQUAL -> parseLessThanEqual(parsedField, value)

      LIKE -> parseLike(parsedField, value, dbFeatures).ignoreCase(true)
      LIKE_EXP -> parseLikeExp(parsedField, value, dbFeatures).ignoreCase(true)
      NOT_LIKE -> parseNotLike(parsedField, value, dbFeatures).ignoreCase(true)
      NOT_LIKE_EXP -> parseNotLikeExp(parsedField, value, dbFeatures).ignoreCase(true)

      IN -> parseIn(parsedField, value, params)
      NOT_IN -> parseNotIn(parsedField, value, params)

      IS_NULL -> where(parsedField.resolvedFieldName).isNull
      IS_NOT_NULL -> where(parsedField.resolvedFieldName).isNotNull

      FilterOperator.BETWEEN -> parseBetween(parsedField, value, params)
    }
  }

  private fun parseLike(parsedField: R2dbcParsedField, value: Array<String>, dbFeatures: DbFeatures) = when (dbFeatures) {
    POSTGRES -> where("unaccent(lower(${parsedField.resolvedFieldName}))").like("%${value.toSingleString()?.lowercase()?.unaccent()}%")
    NONE -> where(parsedField.resolvedFieldName).like("%${value.toSingleString()?.lowercase()}%")
  }

  private fun parseLikeExp(parsedField: R2dbcParsedField, value: Array<String>, dbFeatures: DbFeatures) = when (dbFeatures) {
    POSTGRES -> where("unaccent(lower(${parsedField.resolvedFieldName}))").like(value.toSingleString()!!.lowercase().unaccent())
    NONE -> where(parsedField.resolvedFieldName).like(value.toSingleString()!!.lowercase())
  }

  private fun parseNotLike(parsedField: R2dbcParsedField, value: Array<String>, dbFeatures: DbFeatures) = when (dbFeatures) {
    POSTGRES -> where("unaccent(lower(${parsedField.resolvedFieldName}))").notLike("%${value.toSingleString()?.lowercase()?.unaccent()}%")
    NONE -> where(parsedField.resolvedFieldName).notLike("%${value.toSingleString()?.lowercase()}%")
  }

  private fun parseNotLikeExp(parsedField: R2dbcParsedField, value: Array<String>, dbFeatures: DbFeatures) = when (dbFeatures) {
    POSTGRES -> where("unaccent(lower(${parsedField.resolvedFieldName}))").notLike(value.toSingleString()!!.lowercase().unaccent())
    NONE -> where(parsedField.resolvedFieldName).notLike(value.toSingleString()!!.lowercase())
  }

  private fun parseEqual(parsedField: R2dbcParsedField, value: Array<String>) = when (parsedField.getFieldType()) {
    ENUMERATED -> where(parsedField.resolvedFieldName).`is`(value.toSingleString()!!).ignoreCase(true)
    NUMBER -> where(parsedField.resolvedFieldName).`is`(value.toSingleBigDecimal()!!)
    LOCAL_DATE -> where(parsedField.resolvedFieldName).`is`(value.toSingleDate()!!)
    INSTANT -> where(parsedField.resolvedFieldName).`is`(value.toSingleInstant()!!)
    BOOLEAN -> where(parsedField.resolvedFieldName).`is`(value.toSingleBoolean()!!)
    UUID -> where(parsedField.resolvedFieldName).`is`(value.toSingleString()!!)
    GENERIC -> where(parsedField.resolvedFieldName).`is`(value.toSingleString()!!).ignoreCase(true)
    null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseEqual")
  }

  private fun parseNotEqual(parsedField: R2dbcParsedField, value: Array<String>) = when (parsedField.getFieldType()) {
    ENUMERATED -> where(parsedField.resolvedFieldName).not(value.toSingleString()!!).ignoreCase(true)
    NUMBER -> where(parsedField.resolvedFieldName).not(value.toSingleBigDecimal()!!)
    LOCAL_DATE -> where(parsedField.resolvedFieldName).not(value.toSingleDate()!!)
    INSTANT -> where(parsedField.resolvedFieldName).not(value.toSingleInstant()!!)
    BOOLEAN -> where(parsedField.resolvedFieldName).not(value.toSingleBoolean()!!)
    UUID -> where(parsedField.resolvedFieldName).not(value.toSingleString()!!)
    GENERIC -> where(parsedField.resolvedFieldName).not(value.toSingleString()!!).ignoreCase(true)
    null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseEqual")
  }

  private fun parseGreaterThan(parsedField: R2dbcParsedField, value: Array<String>?) = when (parsedField.getFieldType()) {
    NUMBER -> where(parsedField.resolvedFieldName).greaterThan(value.toSingleBigDecimal()!!)
    LOCAL_DATE -> where(parsedField.resolvedFieldName).greaterThan(value.toSingleDate()!!)
    INSTANT -> where(parsedField.resolvedFieldName).greaterThan(value.toSingleInstant()!!)
    ENUMERATED, GENERIC, BOOLEAN, UUID, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseGreaterThan")
  }

  private fun parseGreaterThanEqual(parsedField: R2dbcParsedField, value: Array<String>?) = when (parsedField.getFieldType()) {
    NUMBER -> where(parsedField.resolvedFieldName).greaterThanOrEquals(value.toSingleBigDecimal()!!)
    LOCAL_DATE -> where(parsedField.resolvedFieldName).greaterThanOrEquals(value.toSingleDate()!!)
    INSTANT -> where(parsedField.resolvedFieldName).greaterThanOrEquals(value.toSingleInstant()!!)
    ENUMERATED, GENERIC, BOOLEAN, UUID, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseGreaterThanEqual")
  }

  private fun parseLessThan(parsedField: R2dbcParsedField, value: Array<String>?) = when (parsedField.getFieldType()) {
    NUMBER -> where(parsedField.resolvedFieldName).lessThan(value.toSingleBigDecimal()!!)
    LOCAL_DATE -> where(parsedField.resolvedFieldName).lessThan(value.toSingleDate()!!)
    INSTANT -> where(parsedField.resolvedFieldName).lessThan(value.toSingleInstant()!!)
    ENUMERATED, GENERIC, BOOLEAN, UUID, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseLessThan")
  }

  private fun parseLessThanEqual(parsedField: R2dbcParsedField, value: Array<String>?) = when (parsedField.getFieldType()) {
    NUMBER -> where(parsedField.resolvedFieldName).lessThanOrEquals(value.toSingleBigDecimal()!!)
    LOCAL_DATE -> where(parsedField.resolvedFieldName).lessThanOrEquals(value.toSingleDate()!!)
    INSTANT -> where(parsedField.resolvedFieldName).lessThanOrEquals(value.toSingleInstant()!!)
    ENUMERATED, GENERIC, BOOLEAN, UUID, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseLessThanEqual")
  }

  private fun parseInValues(parsedField: R2dbcParsedField, value: Array<String>?, params: Map<String, Array<String>?>): List<String> {
    val separator: String = params.toSingleParameter(SEARCH_IN_SEPARATOR_PRM)?.toString() ?: SEARCH_IN_SEPARATOR_DEF

    return when {
      value == null -> emptyList()
      value.size == 1 -> value[0].split(separator).toList()
      value.size > 1 -> value.toList()
      else -> error("field `${parsedField.resolvedFieldName}` has no value to filter by parseIn")
    }
  }

  private fun parseIn(parsedField: R2dbcParsedField, value: Array<String>?, params: Map<String, Array<String>?>): Criteria {
    val values = parseInValues(parsedField, value, params)

    return when (parsedField.getFieldType()) {
      ENUMERATED -> where(parsedField.resolvedFieldName).`in`(values)
      NUMBER -> where(parsedField.resolvedFieldName).`in`(values.map { it.toBigDecimal() })
      LOCAL_DATE -> where(parsedField.resolvedFieldName).`in`(values.map { LocalDate.parse(it) })
      INSTANT -> where(parsedField.resolvedFieldName).`in`(values.map { Instant.parse(it) })
      GENERIC, UUID -> where(parsedField.resolvedFieldName).`in`(values)
      BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseIn")
    }
  }

  private fun parseNotIn(parsedField: R2dbcParsedField, value: Array<String>?, params: Map<String, Array<String>?>): Criteria {
    val values = parseInValues(parsedField, value, params)

    return when (parsedField.getFieldType()) {
      ENUMERATED -> where(parsedField.resolvedFieldName).notIn(values)
      NUMBER -> where(parsedField.resolvedFieldName).notIn(values.map { it.toBigDecimal() })
      LOCAL_DATE -> where(parsedField.resolvedFieldName).notIn(values.map { LocalDate.parse(it) })
      INSTANT -> where(parsedField.resolvedFieldName).notIn(values.map { Instant.parse(it) })
      GENERIC, UUID -> where(parsedField.resolvedFieldName).notIn(values)
      BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseNotIn")
    }
  }

  private fun parseBetween(parsedField: R2dbcParsedField, value: Array<String>?, params: Map<String, Array<String>?>): Criteria {
    val values = parseInValues(parsedField, value, params)

    return when (parsedField.getFieldType()) {
      NUMBER -> where(parsedField.resolvedFieldName).between(values[0].toBigDecimal(), values[1].toBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).between(values[0].let { LocalDate.parse(it) }, values[1].let { LocalDate.parse(it) })
      INSTANT -> where(parsedField.resolvedFieldName).between(values[0].let { Instant.parse(it) }, values[1].let { Instant.parse(it) })
      GENERIC, UUID, ENUMERATED, BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseIn")
    }
  }
}
