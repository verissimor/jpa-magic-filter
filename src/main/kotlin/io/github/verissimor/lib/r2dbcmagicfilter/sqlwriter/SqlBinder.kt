package io.github.verissimor.lib.r2dbcmagicfilter.sqlwriter

data class SqlBinder(
  val sql: String,
  val params: Map<String, Any>,
) {
  operator fun plus(other: Any?): SqlBinder {
    if (other == null) return this

    if (other is SqlBinder) {
      return SqlBinder(
        sql = this.sql + other.sql,
        params = this.params + other.params,
      )
    }

    error("can't sum")
  }

  companion object {
    fun emptySqlBinder(): SqlBinder = SqlBinder("", emptyMap())
  }
}
