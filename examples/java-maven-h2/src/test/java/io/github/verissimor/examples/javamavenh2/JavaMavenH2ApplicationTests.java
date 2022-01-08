package io.github.verissimor.examples.javamavenh2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JavaMavenH2ApplicationTests {
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldFilterUsingEquals() throws Exception {
    mockMvc.perform(get("/api/users?name=Matthew C. McAfee"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void shouldFilterUsingLike() throws Exception {
    mockMvc.perform(get("/api/users?name_like=Gloria"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void shouldFilterNumber() throws Exception {
    mockMvc.perform(get("/api/users?age=19"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void shouldFilterNested() throws Exception {
    mockMvc.perform(get("/api/users?city.name=London"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  // you can find more examples on the main tests of the project
}
