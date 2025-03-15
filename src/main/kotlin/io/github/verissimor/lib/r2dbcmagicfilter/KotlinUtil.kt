package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.sqlwriter.SqlBinder
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import kotlin.reflect.KProperty1

fun Map<String, Any>.toR2dbcMagicFilter(): R2dbcMagicFilter {
  val map: MultiValueMap<String, String> = LinkedMultiValueMap()
  this.forEach { map.add(it.key, it.value.toString()) }
  return R2dbcMagicFilter(map)
}

fun magicFilterOfR2dbc(vararg prm: Pair<String, Any>): R2dbcMagicFilter = prm.toMap().toR2dbcMagicFilter()

fun <T, V> KProperty1<T, V?>.eq(value: Any): Pair<String, Any> = name to value.toString()

fun <T, V> KProperty1<T, V?>.gt(value: Any): Pair<String, Any> = name + "_gt" to value.toString()

fun <T, V> KProperty1<T, V?>.ge(value: Any): Pair<String, Any> = name + "_ge" to value.toString()

fun <T, V> KProperty1<T, V?>.lt(value: Any): Pair<String, Any> = name + "_lt" to value.toString()

fun <T, V> KProperty1<T, V?>.le(value: Any): Pair<String, Any> = name + "_le" to value.toString()

fun <T, V> KProperty1<T, V?>.like(value: Any): Pair<String, Any> = name + "_like" to value.toString()

fun <T, V> KProperty1<T, V?>.likeExp(value: Any): Pair<String, Any> = name + "_like_exp" to value.toString()

fun <T, V> KProperty1<T, V?>.notLike(value: Any): Pair<String, Any> = name + "_not_like" to value.toString()

fun <T, V> KProperty1<T, V?>.notLikeExp(value: Any): Pair<String, Any> = name + "_not_like_exp" to value.toString()

fun <T, V> KProperty1<T, V?>.inValues(value: Collection<Any>): Pair<String, Any> = name + "_in" to value.joinToString(",") { it.toString() }

fun <T, V> KProperty1<T, V?>.notInValues(value: Collection<Any>): Pair<String, Any> = name + "_not_in" to value.joinToString(",") { it.toString() }

fun <T, V> KProperty1<T, V?>.isNull(): Pair<String, Any> = name + "_is_null" to ""

fun <T, V> KProperty1<T, V?>.isNotNull(): Pair<String, Any> = name + "_is_not_null" to ""

fun <T, V> KProperty1<T, V?>.between(
  value1: Any,
  value2: Any,
): Pair<String, Any> = name + "_is_between" to "$value1,$value2"

fun <T, V> KProperty1<T, V?>.notBetween(
  value1: Any,
  value2: Any,
): Pair<String, Any> = name + "_is_not_between" to "$value1,$value2"

fun DatabaseClient.sql(
  sql: String,
  binder: SqlBinder?,
): DatabaseClient.GenericExecuteSpec {
  val startSql = this.sql(sql)

  if (binder == null) return startSql

  return binder.params.toList().fold(startSql) { acc, pair ->
    acc.bind(pair.first, pair.second)
  }
}
