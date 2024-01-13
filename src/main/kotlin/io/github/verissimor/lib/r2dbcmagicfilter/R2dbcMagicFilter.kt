package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.fieldparser.FieldParser
import io.github.verissimor.lib.fieldparser.domain.CombineOperator
import io.github.verissimor.lib.fieldparser.domain.CombineOperator.AND
import io.github.verissimor.lib.fieldparser.domain.CombineOperator.OR
import io.github.verissimor.lib.fieldparser.domain.FilterOperator
import io.github.verissimor.lib.fieldparser.domain.ParsedField
import io.github.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_TYPE_AND
import io.github.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_TYPE_OR
import io.github.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_TYPE_PRM
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures.NONE
import io.github.verissimor.lib.r2dbcmagicfilter.sqlwriter.R2dbcSqlWriter
import io.github.verissimor.lib.r2dbcmagicfilter.sqlwriter.SqlBinder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.relational.core.query.Criteria
import org.springframework.util.MultiValueMap

class R2dbcMagicFilter(
  parameterMap: MultiValueMap<String, String>,
) {
  private val parameters: MutableMap<String, List<String>?> =
    parameterMap.keys.associateWith {
      parameterMap[it]?.toTypedArray()?.toList()
    }.toMutableMap()

  private val log: Logger = LoggerFactory.getLogger(R2dbcMagicFilter::class.java)

  fun toCriteria(
    clazz: Class<*>,
    dbFeatures: DbFeatures = NONE,
  ): Criteria {
    val parseFields = toParsedFields(clazz)
    val groups = parseFields.map { it.group }.distinct().sorted()

    // first reduce/fold criteria in the group
    val criteriaGroups =
      groups.map { group ->
        val fieldsOfGroup = parseFields.filter { it.group == group }

        when (fieldsOfGroup.size) {
          // just one field, reduces it as a single field
          1 -> R2dbcPredicateParser.parsePredicates(fieldsOfGroup.first(), dbFeatures)

          // many fields one record, reduces it as a collection of criteria
          else ->
            fieldsOfGroup.fold(Criteria.empty()) { acc, field ->
              val predicateOfField = R2dbcPredicateParser.parsePredicates(field, dbFeatures)
              when (field.combineOperator) {
                AND -> acc.and(predicateOfField)
                OR -> acc.or(predicateOfField)
              }
            }
        }
      }

    // then reduce the different groups
    val criteria: Criteria =
      when (criteriaGroups.size) {
        1 -> criteriaGroups.first()
        else ->
          criteriaGroups.fold(Criteria.empty()) { acc, criteria ->
            when (getSearchType()) {
              AND -> acc.and(criteria)
              OR -> acc.or(criteria)
            }
          }
      }

    log.debug(criteria.toString())

    return criteria
  }

  fun getSearchType(): CombineOperator =
    when (parameters[SEARCH_TYPE_PRM]?.firstOrNull()) {
      SEARCH_TYPE_AND, null -> AND
      SEARCH_TYPE_OR -> OR
      else -> error("Invalid searchType. Only allowed: and, or")
    }

  fun toParsedFields(clazz: Class<*>): List<ParsedField> {
    return FieldParser.parseFields(parameters, clazz)
  }

  fun addParameter(
    fieldName: String,
    operator: FilterOperator,
    value: List<Any>,
    combineOperator: CombineOperator = AND,
    group: Int = 0,
  ) {
    val combineOperatorStr = if (combineOperator == AND) "" else "or__"
    this.parameters[combineOperatorStr + fieldName + operator.suffix + "__$group"] = value.map { it.toString() }
  }

  fun addParameter(
    fieldName: String,
    operator: FilterOperator,
    value: Any,
    combineOperator: CombineOperator = AND,
    group: Int = 0,
  ) {
    addParameter(fieldName, operator, listOf(value.toString()), combineOperator, group)
  }

  fun addParameterNullable(
    fieldName: String,
    operator: FilterOperator,
    combineOperator: CombineOperator = AND,
    group: Int = 0,
  ) {
    addParameter(fieldName, operator, emptyList<String>(), combineOperator, group)
  }

  fun toCriteria(clazz: Class<*>): Criteria = toCriteria(clazz, NONE)

  fun toSqlBinder(
    clazz: Class<*>,
    tableAlias: String? = null,
    startStr: String = " AND ",
  ): SqlBinder? {
    val parseFields = toParsedFields(clazz)
    val groups = parseFields.map { it.group }.distinct().sorted()

    if (groups.isEmpty()) {
      return null
    }

    if (groups.size == 1) {
      val binder = R2dbcSqlWriter.writeSql(parseFields, tableAlias)
      return SqlBinder("$startStr(${binder.sql})", binder.params)
    }

    // first reduce/fold criteria in the group
    val groupSqlList =
      groups.map { group ->
        val fieldsOfGroup = parseFields.filter { it.group == group }
        R2dbcSqlWriter.writeSql(fieldsOfGroup, tableAlias)
      }

    val startBinder = SqlBinder("", emptyMap())
    val foldedBinder =
      groupSqlList.foldIndexed(startBinder) { idx, acc, sql ->
        val newParams = acc.params + sql.params
        val combineOperator =
          when {
            idx == 0 -> ""
            getSearchType() == AND -> "AND"
            getSearchType() == OR -> "OR"
            else -> error("Unexpected condition")
          }

        val sqlStr = "${acc.sql} $combineOperator (${sql.sql})".trim()
        SqlBinder(sqlStr, newParams)
      }
    return SqlBinder("$startStr(" + foldedBinder.sql + ")", foldedBinder.params)
  }
}
