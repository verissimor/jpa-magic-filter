package io.github.verissimor.examples.reactive.javagradlereactive.repository;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

//@Configuration
//@EnableR2dbcRepositories
//public class R2dbcConfig extends AbstractR2dbcConfiguration {
//    @Override
//    public ConnectionFactory connectionFactory() {
////        return ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
//        return ConnectionFactories.get("r2dbc:h2:mem:///test?options=CASE_INSENSITIVE_IDENTIFIERS=TRUE");
//    }
//
//    @Bean
//    public R2dbcEntityTemplate r2dbcTemplate() {
//        return new R2dbcEntityTemplate(connectionFactory());
//    }
//}
