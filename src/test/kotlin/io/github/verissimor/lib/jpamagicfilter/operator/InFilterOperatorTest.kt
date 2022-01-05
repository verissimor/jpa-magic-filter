package io.github.verissimor.lib.jpamagicfilter.operator

import io.github.verissimor.lib.jpamagicfilter.BaseTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class InFilterOperatorTest : BaseTest() {

  @Test
  fun `should filter in many values separated by comma`() {
    mockMvc.get("/api/users?age_in=19,21,31")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(3))
      }
  }

  @Test
  fun `should filter NOT IN many values separated by comma`() {
    mockMvc.get("/api/users?age_not_in=19,21,31")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(6))
      }
  }

  @Test
  fun `should filter only one value`() {
    mockMvc.get("/api/users?age_in=19")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }

  @Test
  fun `should filter with two times same parameter as array`() {
    mockMvc.get("/api/users?age[]=19&age[]=23")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(3))
      }
  }

  @Test
  fun `should filter with two times same parameter repeated`() {
    mockMvc.get("/api/users?age=19&age=23")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(3))
      }
  }

  @Test
  fun `should filter in many values separated by semi-colon`() {
    mockMvc.get("/api/users?age_in=19;21;31&searchInSeparator=;")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(3))
      }
  }
}
