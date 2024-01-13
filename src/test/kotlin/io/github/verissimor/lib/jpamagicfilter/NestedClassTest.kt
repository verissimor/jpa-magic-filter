package io.github.verissimor.lib.jpamagicfilter

import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class NestedClassTest : BaseTest() {
  @Test
  fun `should filter a nested class`() {
    mockMvc.get("/api/users?city.name=London")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }

  @Test
  fun `should filter two nested classes`() {
    mockMvc.get("/api/users?city.country.name=France")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }

  @Test
  fun `should filter nested class by enum`() {
    mockMvc.get("/api/users?city.timezone=AMERICA_NEW_YORK")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(3))
      }
  }

  @Test
  fun `should filter a nested class using like`() {
    mockMvc.get("/api/users?city.country.name_like=United")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(5))
      }
  }
}
