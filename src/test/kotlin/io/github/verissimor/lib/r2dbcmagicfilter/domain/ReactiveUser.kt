package io.github.verissimor.lib.r2dbcmagicfilter.domain

import io.github.verissimor.lib.jpamagicfilter.Gender
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

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
  val enabled: Boolean,
  val uuid: UUID,
)
