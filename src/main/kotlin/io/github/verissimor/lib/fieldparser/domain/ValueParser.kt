package io.github.verissimor.lib.fieldparser.domain

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID

abstract class ValueParser(
  private val resolvedFieldName: String,
  private val sourceValue: List<String>?
) {

  fun getStringOrNull(): String? {
    val value = sourceValue?.firstOrNull()
    if (value?.isEmpty() == true) {
      return null
    }
    return value
  }

  fun getString(): String = getStringOrNull()!!
  fun getBigDecimalOrNull(): BigDecimal? = getStringOrNull()?.toBigDecimalOrNull()
  fun getBigDecimal(): BigDecimal = getBigDecimalOrNull()!!
  fun getLocalDateOrNull(): LocalDate? = getStringOrNull()?.toLocalDateOrNull()

  fun getLocalDate(): LocalDate = getLocalDateOrNull()!!
  fun getInstantOrNull(): Instant? = getStringOrNull()?.toInstantOrNull()

  fun getInstant(): Instant = getInstantOrNull()!!

  fun getBooleanOrNull(): Boolean? = getStringOrNull()?.toBooleanOrNull()

  fun getBoolean(): Boolean = getBooleanOrNull()!!
  fun getUUIDOrNull(): UUID? = getStringOrNull()?.toUUIDOrNull()

  fun getUUID(): UUID = getUUIDOrNull()!!

  fun getListStringOrNull(): List<String>? = when {
    sourceValue == null -> null
    sourceValue.size == 1 -> getStringOrNull()?.let { parseStringIntoList(it) }
    sourceValue.size > 1 -> sourceValue.toList()
    else -> error("field `$resolvedFieldName` has no value to filter by parseIn")
  }

  fun getListString(): List<String> = getListStringOrNull()!!

  fun getListBigDecimal(): List<BigDecimal> = getListString().mapNotNull { it.toBigDecimalOrNull() }
  fun getListLocalDate(): List<LocalDate> = getListString().mapNotNull { it.toLocalDateOrNull() }
  fun getListInstant(): List<Instant> = getListString().mapNotNull { it.toInstantOrNull() }
  fun getListBoolean(): List<Boolean> = getListString().mapNotNull { it.toBooleanOrNull() }
  fun getListUUID(): List<UUID> = getListString().mapNotNull { it.toUUIDOrNull() }

  companion object {
    fun parseStringIntoList(str: String?): List<String>? =
      str?.split(",")?.toList()?.map { it.trim() }?.filter { it.isNotEmpty() }

    fun String.toLocalDateOrNull(): LocalDate? = try {
      LocalDate.parse(this)
    } catch (ex: DateTimeParseException) {
      null
    }

    fun String.toInstantOrNull(): Instant? = try {
      // Attempt to parse the input as an epoch timestamp
      val timestamp = this.toLongOrNull()
      if (timestamp != null) {
        Instant.ofEpochSecond(timestamp)
      } else {
        // Attempt to parse the input as an ISO8601 date-time
        Instant.parse(this)
      }
    } catch (e: DateTimeParseException) {
      // Handle parsing errors (invalid format)
      null
    }

    fun String.toBooleanOrNull(): Boolean? = when (this.trim().lowercase()) {
      "true", "1" -> true
      "false", "0" -> false
      else -> null // Unable to parse the string as a boolean
    }
  }

  fun String.toUUIDOrNull(): UUID? = try {
    UUID.fromString(this)
  } catch (ex: IllegalArgumentException) {
    null
  }
}
