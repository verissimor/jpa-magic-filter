package io.github.verissimor.lib.jpamagicfilter.fieldtype

import io.github.verissimor.lib.jpamagicfilter.BaseTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class NumericFieldTypeTest : BaseTest() {
  @Test
  fun `should filter grater than`() {
    mockMvc.get("/api/users?age_gt=55")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(1))
      }
  }

  @Test
  fun `should filter grater equals than`() {
    mockMvc.get("/api/users?age_ge=55")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }
}
