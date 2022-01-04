package com.verissimor.lib.jpamagicfilter.fieldtype

import com.verissimor.lib.jpamagicfilter.BaseTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class IsNullFieldTypeTest : BaseTest() {

  @Test
  fun `should filter equals a Instant`() {
    mockMvc.get("/api/users?createdDate_is_null")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(1))
      }
  }

  @Test
  fun `should filter grater equals a Instant`() {
    mockMvc.get("/api/users?createdDate_is_not_null")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(8))
      }
  }
}
