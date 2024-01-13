package io.github.verissimor.lib.r2dbcmagicfilter

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
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures.NONE
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures.POSTGRES
import io.github.verissimor.lib.jpamagicfilter.unaccent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where

object R2dbcPredicateParser {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun parsePredicates(
    parsedField: ParsedField,
    dbFeatures: DbFeatures,
  ): Criteria =
    when (parsedField.filterOperator) {
      EQUAL -> parseEqual(parsedField)
      NOT_EQUAL -> parseNotEqual(parsedField)

      GREATER_THAN -> parseGreaterThan(parsedField)
      GREATER_THAN_EQUAL -> parseGreaterThanEqual(parsedField)
      LESS_THAN -> parseLessThan(parsedField)
      LESS_THAN_EQUAL -> parseLessThanEqual(parsedField)

      LIKE -> parseLike(parsedField, dbFeatures).ignoreCase(true)
      LIKE_EXP -> parseLikeExp(parsedField, dbFeatures).ignoreCase(true)
      NOT_LIKE -> parseNotLike(parsedField, dbFeatures).ignoreCase(true)
      NOT_LIKE_EXP -> parseNotLikeExp(parsedField, dbFeatures).ignoreCase(true)

      IN -> parseIn(parsedField)
      NOT_IN -> parseNotIn(parsedField)

      IS_NULL -> where(parsedField.resolvedFieldName).isNull
      IS_NOT_NULL -> where(parsedField.resolvedFieldName).isNotNull

      BETWEEN -> parseBetween(parsedField)
    }

  private fun parseLike(
    parsedField: ParsedField,
    dbFeatures: DbFeatures,
  ) = when (dbFeatures) {
    POSTGRES ->
      where("unaccent(lower(${parsedField.resolvedFieldName}))").like(
        "%${parsedField.getString().lowercase().unaccent()}%",
      )
    NONE -> where(parsedField.resolvedFieldName).like("%${parsedField.getString().lowercase()}%")
  }

  private fun parseLikeExp(
    parsedField: ParsedField,
    dbFeatures: DbFeatures,
  ) = when (dbFeatures) {
    POSTGRES ->
      where("unaccent(lower(${parsedField.resolvedFieldName}))").like(
        parsedField.getString().lowercase().unaccent(),
      )
    NONE -> where(parsedField.resolvedFieldName).like(parsedField.getString().lowercase())
  }

  private fun parseNotLike(
    parsedField: ParsedField,
    dbFeatures: DbFeatures,
  ) = when (dbFeatures) {
    POSTGRES ->
      where("unaccent(lower(${parsedField.resolvedFieldName}))").notLike(
        "%${parsedField.getString().lowercase().unaccent()}%",
      )
    NONE -> where(parsedField.resolvedFieldName).notLike("%${parsedField.getString().lowercase()}%")
  }

  private fun parseNotLikeExp(
    parsedField: ParsedField,
    dbFeatures: DbFeatures,
  ) = when (dbFeatures) {
    POSTGRES ->
      where("unaccent(lower(${parsedField.resolvedFieldName}))").notLike(
        parsedField.getString().lowercase().unaccent(),
      )
    NONE -> where(parsedField.resolvedFieldName).notLike(parsedField.getString().lowercase())
  }

  private fun parseEqual(parsedField: ParsedField) =
    when (parsedField.getFieldType()) {
      ENUMERATED -> where(parsedField.resolvedFieldName).`is`(parsedField.getString()).ignoreCase(true)
      NUMBER -> where(parsedField.resolvedFieldName).`is`(parsedField.getBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).`is`(parsedField.getLocalDate())
      INSTANT -> where(parsedField.resolvedFieldName).`is`(parsedField.getInstant())
      BOOLEAN -> where(parsedField.resolvedFieldName).`is`(parsedField.getBoolean())
      UUID -> where(parsedField.resolvedFieldName).`is`(parsedField.getString())
      GENERIC -> where(parsedField.resolvedFieldName).`is`(parsedField.getString()).ignoreCase(true)
      null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseEqual")
    }

  private fun parseNotEqual(parsedField: ParsedField) =
    when (parsedField.getFieldType()) {
      ENUMERATED -> where(parsedField.resolvedFieldName).not(parsedField.getString()).ignoreCase(true)
      NUMBER -> where(parsedField.resolvedFieldName).not(parsedField.getBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).not(parsedField.getLocalDate())
      INSTANT -> where(parsedField.resolvedFieldName).not(parsedField.getInstant())
      BOOLEAN -> where(parsedField.resolvedFieldName).not(parsedField.getBoolean())
      UUID -> where(parsedField.resolvedFieldName).not(parsedField.getString())
      GENERIC -> where(parsedField.resolvedFieldName).not(parsedField.getString()).ignoreCase(true)
      null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseEqual")
    }

  private fun parseGreaterThan(parsedField: ParsedField) =
    when (parsedField.getFieldType()) {
      NUMBER -> where(parsedField.resolvedFieldName).greaterThan(parsedField.getBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).greaterThan(parsedField.getLocalDate())
      INSTANT -> where(parsedField.resolvedFieldName).greaterThan(parsedField.getInstant())
      ENUMERATED, GENERIC, BOOLEAN, UUID, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseGreaterThan")
    }

  private fun parseGreaterThanEqual(parsedField: ParsedField) =
    when (parsedField.getFieldType()) {
      NUMBER -> where(parsedField.resolvedFieldName).greaterThanOrEquals(parsedField.getBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).greaterThanOrEquals(parsedField.getLocalDate())
      INSTANT -> where(parsedField.resolvedFieldName).greaterThanOrEquals(parsedField.getInstant())
      ENUMERATED, GENERIC, BOOLEAN, UUID, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseGreaterThanEqual")
    }

  private fun parseLessThan(parsedField: ParsedField) =
    when (parsedField.getFieldType()) {
      NUMBER -> where(parsedField.resolvedFieldName).lessThan(parsedField.getBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).lessThan(parsedField.getLocalDate())
      INSTANT -> where(parsedField.resolvedFieldName).lessThan(parsedField.getInstant())
      ENUMERATED, GENERIC, BOOLEAN, UUID, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseLessThan")
    }

  private fun parseLessThanEqual(parsedField: ParsedField) =
    when (parsedField.getFieldType()) {
      NUMBER -> where(parsedField.resolvedFieldName).lessThanOrEquals(parsedField.getBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).lessThanOrEquals(parsedField.getLocalDate())
      INSTANT -> where(parsedField.resolvedFieldName).lessThanOrEquals(parsedField.getInstant())
      ENUMERATED, GENERIC, BOOLEAN, UUID, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseLessThanEqual")
    }

  private fun parseIn(parsedField: ParsedField): Criteria {
    return when (parsedField.getFieldType()) {
      ENUMERATED -> where(parsedField.resolvedFieldName).`in`(parsedField.getListString())
      NUMBER -> where(parsedField.resolvedFieldName).`in`(parsedField.getListBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).`in`(parsedField.getListLocalDate())
      INSTANT -> where(parsedField.resolvedFieldName).`in`(parsedField.getListInstant())
      GENERIC, UUID -> where(parsedField.resolvedFieldName).`in`(parsedField.getListString())
      BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseIn")
    }
  }

  private fun parseNotIn(parsedField: ParsedField): Criteria {
    return when (parsedField.getFieldType()) {
      ENUMERATED -> where(parsedField.resolvedFieldName).notIn(parsedField.getListString())
      NUMBER -> where(parsedField.resolvedFieldName).notIn(parsedField.getListBigDecimal())
      LOCAL_DATE -> where(parsedField.resolvedFieldName).notIn(parsedField.getListLocalDate())
      INSTANT -> where(parsedField.resolvedFieldName).notIn(parsedField.getListInstant())
      GENERIC, UUID -> where(parsedField.resolvedFieldName).notIn(parsedField.getListString())
      BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseNotIn")
    }
  }

  private fun parseBetween(parsedField: ParsedField): Criteria {
    return when (parsedField.getFieldType()) {
      NUMBER -> {
        val values = parsedField.getListBigDecimal()
        where(parsedField.resolvedFieldName).between(values[0], values[1])
      }
      LOCAL_DATE -> {
        val values = parsedField.getListLocalDate()
        where(parsedField.resolvedFieldName).between(values[0], values[1])
      }
      INSTANT -> {
        val values = parsedField.getListInstant()
        where(parsedField.resolvedFieldName).between(values[0], values[1])
      }
      GENERIC, UUID, ENUMERATED, BOOLEAN, null -> error("field `${parsedField.resolvedFieldName}` is `${parsedField.fieldClass}` and doesn't support parseBetween")
    }
  }
}
