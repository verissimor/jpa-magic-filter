package io.github.verissimor.lib.r2dbcmagicfilter.domain

import org.springframework.http.HttpHeaders

fun List<Pair<String, String?>>.toMultiMap(): HttpHeaders {
  val result = HttpHeaders()
  this.map { it.first }.distinct().forEach { key ->
    result[key] = this.filter { it.first == key }.map { it.second }
  }
  return result
}
