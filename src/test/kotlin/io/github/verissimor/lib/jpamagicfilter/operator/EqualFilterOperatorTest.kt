package io.github.verissimor.lib.jpamagicfilter.operator

import io.github.verissimor.lib.jpamagicfilter.BaseTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class EqualFilterOperatorTest : BaseTest() {
  @Test
  fun `should return all results when no filter present`() {
    mockMvc.get("/api/users")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(9))
      }
  }

  @Test
  fun `should filter equals`() {
    mockMvc.get("/api/users?name=Eleanor C. Moyer")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(1))
      }
  }

  @Test
  fun `should filter enumerations`() {
    mockMvc.get("/api/users?gender=FEMALE")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(5))
      }
  }

  @Test
  fun `should filter with two parameters`() {
    mockMvc.get("/api/users?age=23&gender=FEMALE")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(1))
      }
  }

  @Test
  fun `should filter boolean true`() {
    mockMvc.get("/api/users?city.country.isInEurope=true")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(4))
      }
  }

  @Test
  fun `should filter boolean false`() {
    mockMvc.get("/api/users?city.country.isInEurope=false")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(5))
      }
  }
}
