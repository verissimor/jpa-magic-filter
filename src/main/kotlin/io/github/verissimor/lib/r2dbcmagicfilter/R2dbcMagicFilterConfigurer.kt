package io.github.verissimor.lib.r2dbcmagicfilter

import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import reactor.core.publisher.Mono

@Configuration
@EnableWebMvc
class R2dbcMagicFilterConfigurer : WebFluxConfigurer {
  override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
    super.configureArgumentResolvers(configurer)
    configurer.addCustomResolver(R2dbcMagicFilterAttributeResolver())
  }
}

class R2dbcMagicFilterAttributeResolver : HandlerMethodArgumentResolver {
  override fun supportsParameter(parameter: MethodParameter): Boolean {
    return parameter.parameterType == R2dbcMagicFilter::class.java
  }

  override fun resolveArgument(parameter: MethodParameter, bindingContext: BindingContext, exchange: ServerWebExchange): Mono<Any> {
    return Mono.just( R2dbcMagicFilter(exchange.request.queryParams))
  }
}
