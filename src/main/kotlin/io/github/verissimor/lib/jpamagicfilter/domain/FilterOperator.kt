package io.github.verissimor.lib.jpamagicfilter.domain

enum class FilterOperator(val suffix: String) {
  GREATER_THAN("_gt"),
  GREATER_THAN_EQUAL("_ge"),
  LESS_THAN("_lt"),
  LESS_THAN_EQUAL("_le"),

  LIKE_EXP("_like_exp"),
  LIKE("_like"),
  NOT_LIKE_EXP("_not_like_exp"),
  NOT_LIKE("_not_like"),

  IN("_in"),
  NOT_IN("_not_in"),

  IS_NULL("_is_null"),
  IS_NOT_NULL("_is_not_null"),

  NOT_EQUAL("_ne"),
  EQUAL("")
}
