package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class R2dbcMagicFilterInTest {
  @Test
  fun `test in string comma separated`() {
    val params = listOf("name_in" to "Joe,Jane").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("name IN ('Joe', 'Jane')")
  }

  @Test
  fun `test in string repeated param`() {
    val params = listOf("name" to "Joe", "name" to "Jane").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("name IN ('Joe', 'Jane')")
  }

  @Test
  fun `test in enum`() {
    val params = listOf("gender_in" to "MALE,FEMALE").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("gender IN ('MALE', 'FEMALE')")
  }

  @Test
  fun `test in integer`() {
    val params = listOf("age_in" to "19,20,21").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("age IN (19, 20, 21)")
  }

  @Test
  fun `test in long`() {
    val params = listOf("id_in" to "1,2").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("id IN (1, 2)")
  }

  @Test
  fun `test in date`() {
    val params = listOf("createdDate_in" to "2022-12-30,2022-12-31").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdDate IN ('2022-12-30', '2022-12-31')")
  }

  @Test
  fun `test in instant`() {
    val now = Instant.now().toString()
    val later = Instant.now().plusMillis(1000).toString()
    val params = listOf("createdAt_in" to "$now,$later").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdAt IN ('$now', '$later')")
  }

  @Test
  fun `test in boolean`() {
    val params = listOf("enabled_in" to "0,1").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    assertThrows<IllegalStateException> {
      filter.toCriteria(ReactiveUser::class.java)
    }
  }
}
