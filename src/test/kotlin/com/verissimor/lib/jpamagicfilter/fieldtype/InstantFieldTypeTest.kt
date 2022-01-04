package com.verissimor.lib.jpamagicfilter.fieldtype

import com.verissimor.lib.jpamagicfilter.BaseTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class InstantFieldTypeTest : BaseTest() {

  @Test
  fun `should filter equals a Instant`() {
    mockMvc.get("/api/users?city.createdAt=2000-01-01T00:00:00.0Z")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(5))
      }
  }

  @Test
  fun `should filter grater equals a Instant`() {
    mockMvc.get("/api/users?city.createdAt_gt=2000-01-01T00:00:00.0Z")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(4))
      }
  }

  @Test
  fun `should filter IN two Instant`() {
    mockMvc.get("/api/users?city.createdAt_in=2000-01-01T00:00:00.0Z,2022-12-31T23:59:59.9Z")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(9))
      }
  }

  @Test
  fun `should filter NOT IN two Instant`() {
    mockMvc.get("/api/users?city.createdAt_not_in=2000-01-01T00:00:00.0Z,2022-12-31T23:59:59.9Z")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(0))
      }
  }
}
