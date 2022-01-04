package com.verissimor.lib.jpamagicfilter

import com.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_IN_SEPARATOR_DEF
import com.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_IN_SEPARATOR_PRM
import com.verissimor.lib.jpamagicfilter.domain.FieldType.BOOLEAN
import com.verissimor.lib.jpamagicfilter.domain.FieldType.ENUMERATED
import com.verissimor.lib.jpamagicfilter.domain.FieldType.GENERIC
import com.verissimor.lib.jpamagicfilter.domain.FieldType.INSTANT
import com.verissimor.lib.jpamagicfilter.domain.FieldType.LOCAL_DATE
import com.verissimor.lib.jpamagicfilter.domain.FieldType.NUMBER
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.EQUAL
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.GREATER_THAN
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.GREATER_THAN_EQUAL
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.IN
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.IS_NOT_NULL
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.IS_NULL
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.LESS_THAN
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.LESS_THAN_EQUAL
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.LIKE
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.LIKE_EXP
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.NOT_IN
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.NOT_LIKE
import com.verissimor.lib.jpamagicfilter.domain.FilterOperator.NOT_LIKE_EXP
import com.verissimor.lib.jpamagicfilter.domain.ParsedField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDate
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

object PredicateParser {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun <T> parsePredicates(
    params: Map<String, Array<String>?>,
    clazz: Class<*>,
    root: Root<T>,
    cb: CriteriaBuilder
  ): List<Predicate> = params.mapNotNull { (field, value) ->
    val parsedField = FieldParser.parseField(field, value, clazz, root)

    if (parsedField.fieldClass == null) {
      log.info("Ignoring parameter $field")
      return@mapNotNull null
    }

    when (parsedField.filterOperator) {
      EQUAL -> parseEqual(parsedField, value, cb)

      GREATER_THAN -> parseGreaterThan(parsedField, value, cb)
      GREATER_THAN_EQUAL -> parseGreaterThanEqual(parsedField, value, cb)
      LESS_THAN -> parseLessThan(parsedField, value, cb)
      LESS_THAN_EQUAL -> parseLessThanEqual(parsedField, value, cb)

      LIKE -> cb.like(parsedField.getPath(), "%${value.toSingleString()}%")
      LIKE_EXP -> cb.like(parsedField.getPath(), value.toSingleString())
      NOT_LIKE -> cb.notLike(parsedField.getPath(), "%${value.toSingleString()}%")
      NOT_LIKE_EXP -> cb.notLike(parsedField.getPath(), value.toSingleString())

      IN -> parseIn(parsedField, value, params)
      NOT_IN -> parseNotIn(parsedField, value, params)

      IS_NULL -> cb.isNull(parsedField.getPath<Any>())
      IS_NOT_NULL -> cb.isNotNull(parsedField.getPath<Any>())
    }
  }

  private fun <T> parseEqual(parsedField: ParsedField<T>, value: Array<String>?, cb: CriteriaBuilder) = when (parsedField.getFieldType()) {
    ENUMERATED -> cb.equal(parsedField.getPath<String>().`as`(String::class.java), value.toSingleString())
    NUMBER -> cb.equal(parsedField.getPath<Number>(), value.toSingleBigDecimal())
    LOCAL_DATE -> cb.equal(parsedField.getPath<LocalDate>(), value.toSingleDate())
    INSTANT -> cb.equal(parsedField.getPath<Instant>(), value.toSingleInstant())
    BOOLEAN -> cb.equal(parsedField.getPath<Boolean>(), value.toSingleBoolean())
    GENERIC -> cb.equal(parsedField.getPath<String>(), value.toSingleString())
    null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseEqual")
  }

  private fun <T> parseGreaterThan(parsedField: ParsedField<T>, value: Array<String>?, cb: CriteriaBuilder) = when (parsedField.getFieldType()) {
    NUMBER -> cb.gt(parsedField.getPath<Number>(), value.toSingleBigDecimal())
    LOCAL_DATE -> cb.greaterThan(parsedField.getPath<LocalDate>(), value.toSingleDate())
    INSTANT -> cb.greaterThan(parsedField.getPath<Instant>(), value.toSingleInstant())
    ENUMERATED, GENERIC, BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseGreaterThan")
  }

  private fun <T> parseGreaterThanEqual(parsedField: ParsedField<T>, value: Array<String>?, cb: CriteriaBuilder) = when (parsedField.getFieldType()) {
    NUMBER -> cb.ge(parsedField.getPath<Number>(), value.toSingleBigDecimal())
    LOCAL_DATE -> cb.greaterThanOrEqualTo(parsedField.getPath<LocalDate>(), value.toSingleDate())
    INSTANT -> cb.greaterThanOrEqualTo(parsedField.getPath<Instant>(), value.toSingleInstant())
    ENUMERATED, GENERIC, BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseGreaterThanEqual")
  }

  private fun <T> parseLessThan(parsedField: ParsedField<T>, value: Array<String>?, cb: CriteriaBuilder) = when (parsedField.getFieldType()) {
    NUMBER -> cb.lt(parsedField.getPath<Number>(), value.toSingleBigDecimal())
    LOCAL_DATE -> cb.lessThan(parsedField.getPath<LocalDate>(), value.toSingleDate())
    INSTANT -> cb.lessThan(parsedField.getPath<Instant>(), value.toSingleInstant())
    ENUMERATED, GENERIC, BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseLessThan")
  }

  private fun <T> parseLessThanEqual(parsedField: ParsedField<T>, value: Array<String>?, cb: CriteriaBuilder) = when (parsedField.getFieldType()) {
    NUMBER -> cb.le(parsedField.getPath<Number>(), value.toSingleBigDecimal())
    LOCAL_DATE -> cb.lessThanOrEqualTo(parsedField.getPath<LocalDate>(), value.toSingleDate())
    INSTANT -> cb.lessThanOrEqualTo(parsedField.getPath<Instant>(), value.toSingleInstant())
    ENUMERATED, GENERIC, BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseLessThanEqual")
  }

  private fun <T> parseInValues(parsedField: ParsedField<T>, value: Array<String>?, params: Map<String, Array<String>?>): List<String> {
    val separator: String = params.toSingleParameter(SEARCH_IN_SEPARATOR_PRM)?.toString() ?: SEARCH_IN_SEPARATOR_DEF

    return when {
      value == null -> emptyList()
      value.size == 1 -> value[0].split(separator).toList()
      value.size > 1 -> value.toList()
      else -> error("field `${parsedField.resolvedFieldName}` has no value to filter by parseIn")
    }
  }

  private fun <T> parseIn(parsedField: ParsedField<T>, value: Array<String>?, params: Map<String, Array<String>?>): Predicate? {
    val values = parseInValues(parsedField, value, params)

    return when (parsedField.getFieldType()) {
      ENUMERATED -> parsedField.getPath<String>().`as`(String::class.java).`in`(values)
      NUMBER -> parsedField.getPath<Number>().`in`(values.map { it.toBigDecimal() })
      LOCAL_DATE -> parsedField.getPath<LocalDate>().`in`(values.map { LocalDate.parse(it) })
      INSTANT -> parsedField.getPath<Instant>().`in`(values.map { Instant.parse(it) })
      GENERIC -> parsedField.getPath<String>().`in`(values)
      BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseIn")
    }
  }

  private fun <T> parseNotIn(parsedField: ParsedField<T>, value: Array<String>?, params: Map<String, Array<String>?>): Predicate? {
    val values = parseInValues(parsedField, value, params)

    return when (parsedField.getFieldType()) {
      ENUMERATED -> parsedField.getPath<String>().`as`(String::class.java).`in`(values).not()
      NUMBER -> parsedField.getPath<Number>().`in`(values.map { it.toBigDecimal() }).not()
      LOCAL_DATE -> parsedField.getPath<LocalDate>().`in`(values.map { LocalDate.parse(it) }).not()
      INSTANT -> parsedField.getPath<Instant>().`in`(values.map { Instant.parse(it) }).not()
      GENERIC -> parsedField.getPath<String>().`in`(values).not()
      BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseNotIn")
    }
  }
}
