package io.github.verissimor.lib.r2dbcmagicfilter.domain

import io.github.verissimor.lib.jpamagicfilter.Gender
import java.time.Instant
import java.time.LocalDate
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "app_user")
data class ReactiveUser(
  @Id
  val id: Long?,
  val name: String,
  val age: Int,
  val gender: Gender,
  val cityId: Long?,
  val createdDate: LocalDate?,
  val createdAt: Instant?,
  val enabled: Boolean
)
