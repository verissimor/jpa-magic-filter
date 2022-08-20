package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_TYPE_AND
import io.github.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_TYPE_OR
import io.github.verissimor.lib.jpamagicfilter.MagicFilter.Companion.SEARCH_TYPE_PRM
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures
import io.github.verissimor.lib.jpamagicfilter.domain.DbFeatures.NONE
import io.github.verissimor.lib.jpamagicfilter.toSingleParameter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.relational.core.query.Criteria
import org.springframework.util.MultiValueMap

class R2dbcMagicFilter(
  private val parameterMap: MultiValueMap<String, String>
) {

  val log: Logger = LoggerFactory.getLogger(R2dbcMagicFilter::class.java)

  fun toCriteria(clazz: Class<*>, dbFeatures: DbFeatures = NONE): Criteria {

    val map: Map<String, Array<String>?> = parameterMap.keys.associateWith { parameterMap[it]?.toTypedArray() }
    val parsed = R2dbcPredicateParser.parsePredicates(map, clazz, dbFeatures)

    if (parsed.isEmpty()) {
      return Criteria.empty()
    }

    val criteria = when (map.toSingleParameter(SEARCH_TYPE_PRM)) {
      SEARCH_TYPE_AND, null -> parsed.reduce { acc, criteria -> acc.and(criteria) }
      SEARCH_TYPE_OR -> parsed.reduce { acc, criteria -> acc.or(criteria) }
      else -> error("Invalid searchType. Only allowed: and, or")
    }

    log.info(criteria.toString())

    return criteria
  }
}
