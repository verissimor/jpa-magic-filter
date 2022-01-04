package com.verissimor.lib.jpamagicfilter

import org.springframework.context.annotation.Import

@Import(MagicFilterConfigurer::class)
annotation class EnableJpaMagicFilter
