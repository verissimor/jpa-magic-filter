package io.github.verissimor.lib.jpamagicfilter.operator

import io.github.verissimor.lib.jpamagicfilter.BaseTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class LikeFilterOperatorTest : BaseTest() {
  @Test
  fun `should filter like`() {
    mockMvc.get("/api/users?name_like=C.")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }

  @Test
  fun `should filter like start`() {
    mockMvc.get("/api/users?name_like_exp=Gloria%")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }

  @Test
  fun `should filter like with ignore case`() {
    mockMvc.get("/api/users?name_like=gloria")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }

  @Test
  @Disabled("Needs to connect a postgre db")
  fun `should filter like with ignore case and unaccent`() {
    mockMvc.get("/api/users/postgre-like-dbs?name_like=gloria")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(2))
      }
  }

  @Test
  fun `should filter like end`() {
    mockMvc.get("/api/users?name_like_exp=%t")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(3))
      }
  }

  @Test
  fun `should filter like expression with Percent symbol`() {
    mockMvc.get("/api/users?name_like_exp=%a%t%")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(4))
      }
  }

  @Test
  fun `should filter not like`() {
    mockMvc.get("/api/users?name_like=a")
      .andExpect {
        status { isOk() }
        jsonPath("$", hasSize<Int>(7))
      }
  }
}
