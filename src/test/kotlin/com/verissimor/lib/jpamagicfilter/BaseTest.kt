package com.verissimor.lib.jpamagicfilter

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext

@AutoConfigureMockMvc
@SpringBootTest(classes = [DemoApplication::class])
abstract class BaseTest {

  @Autowired
  lateinit var objectMapper: ObjectMapper

  @Autowired
  lateinit var context: WebApplicationContext

  @Autowired
  lateinit var mockMvc: MockMvc
}
