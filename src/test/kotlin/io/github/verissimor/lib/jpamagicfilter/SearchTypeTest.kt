package io.github.verissimor.lib.jpamagicfilter

import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class SearchTypeTest : BaseTest() {

  @Test
  fun `should filter like`() {
    mockMvc.get("/api/users?age=19&gender=FEMALE&searchType=and")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(1))
      }
  }

  @Test
  fun `should filter like start`() {
    mockMvc.get("/api/users?age=19&gender=FEMALE&searchType=or")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(6))
      }
  }
}
