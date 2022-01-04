package com.verissimor.lib.jpamagicfilter

import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class MagicFilter(
  private val parameterMap: Map<String, Array<String>>
) {

  fun <T> getSpec(clazz: Class<*>): Specification<T> = Specification { root, _, cb ->
    val parsed = PredicateParser.parsePredicates(parameterMap, clazz, root, cb)

    when (parameterMap.toSingleParameter(SEARCH_TYPE_PRM)) {
      SEARCH_TYPE_AND, null -> cb.and(*parsed.toTypedArray())
      SEARCH_TYPE_OR -> cb.or(*parsed.toTypedArray())
      else -> error("Invalid searchType. Only allowed: and, or")
    }
  }

  companion object {
    const val SEARCH_TYPE_PRM = "searchType"
    const val SEARCH_TYPE_AND = "and"
    const val SEARCH_TYPE_OR = "or"
    const val SEARCH_IN_SEPARATOR_PRM = "searchInSeparator"
    const val SEARCH_IN_SEPARATOR_DEF = ","
  }
}

fun Map<String, Array<String>?>.toSingleParameter(key: String): Any? = this[key]?.firstOrNull()
fun Array<String>?.toSingleBigDecimal(): BigDecimal? = this?.firstOrNull()?.toString()?.toBigDecimal()
fun Array<String>?.toSingleString(): String? = this?.firstOrNull()?.toString()
fun Array<String>?.toSingleDate(): LocalDate? = this?.firstOrNull()?.toString()?.let { LocalDate.parse(it) }
fun Array<String>?.toSingleInstant(): Instant? = this?.firstOrNull()?.toString()?.let { Instant.parse(it) }
fun Array<String>?.toSingleBoolean(): Boolean? = this?.firstOrNull()?.toString()?.toBoolean()
