package io.github.verissimor.lib.r2dbcmagicfilter

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
fun <T, V> KProperty1<T, V?>.`in`(value: Any): Pair<String, Any> = name + "_in" to value.toString()
fun <T, V> KProperty1<T, V?>.notIn(value: Any): Pair<String, Any> = name + "_not_in" to value.toString()
fun <T, V> KProperty1<T, V?>.isNull(value: Any): Pair<String, Any> = name + "_is_null" to value.toString()
fun <T, V> KProperty1<T, V?>.isNotNull(value: Any): Pair<String, Any> = name + "_is_not_null" to value.toString()
