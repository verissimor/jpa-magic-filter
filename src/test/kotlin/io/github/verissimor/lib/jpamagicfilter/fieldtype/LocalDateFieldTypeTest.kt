package io.github.verissimor.lib.jpamagicfilter.fieldtype

import io.github.verissimor.lib.jpamagicfilter.BaseTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class LocalDateFieldTypeTest : BaseTest() {

  @Test
  fun `should filter equals a date`() {
    mockMvc.get("/api/users?createdDate=2000-05-16")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }

  @Test
  fun `should filter grater than`() {
    mockMvc.get("/api/users?createdDate_gt=2000-10-01")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(1))
      }
  }

  @Test
  fun `should filter less than`() {
    mockMvc.get("/api/users?createdDate_lt=2000-10-01")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(7))
      }
  }

  @Test
  fun `should filter in`() {
    mockMvc.get("/api/users?createdDate_in=2000-01-01,2000-02-05")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }
}
