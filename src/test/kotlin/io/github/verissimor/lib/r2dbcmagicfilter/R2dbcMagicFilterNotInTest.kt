package io.github.verissimor.lib.r2dbcmagicfilter

import io.github.verissimor.lib.r2dbcmagicfilter.domain.ReactiveUser
import io.github.verissimor.lib.r2dbcmagicfilter.domain.toMultiMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class R2dbcMagicFilterNotInTest {
  @Test
  fun `test not in string comma separated`() {
    val params = listOf("name_not_in" to "Joe,Jane").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("name NOT IN ('Joe', 'Jane')")
  }

  @Test
  fun `test not in enum`() {
    val params = listOf("gender_not_in" to "MALE,FEMALE").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("gender NOT IN ('MALE', 'FEMALE')")
  }

  @Test
  fun `test not in integer`() {
    val params = listOf("age_not_in" to "19,20,21").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("age NOT IN (19, 20, 21)")
  }

  @Test
  fun `test not in long`() {
    val params = listOf("id_not_in" to "1,2").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("id NOT IN (1, 2)")
  }

  @Test
  fun `test not in date`() {
    val params = listOf("createdDate_not_in" to "2022-12-30,2022-12-31").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdDate NOT IN ('2022-12-30', '2022-12-31')")
  }

  @Test
  fun `test not in instant`() {
    val now = Instant.now().toString()
    val later = Instant.now().plusMillis(1000).toString()
    val params = listOf("createdAt_not_in" to "$now,$later").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    val criteria = filter.toCriteria(ReactiveUser::class.java)

    assertThat(criteria.toString()).isEqualTo("createdAt NOT IN ('$now', '$later')")
  }

  @Test
  fun `test not in boolean`() {
    val params = listOf("enabled_not_in" to "0,1").toMultiMap()
    val filter = R2dbcMagicFilter(params)

    assertThrows<IllegalStateException> {
      filter.toCriteria(ReactiveUser::class.java)
    }
  }
}
