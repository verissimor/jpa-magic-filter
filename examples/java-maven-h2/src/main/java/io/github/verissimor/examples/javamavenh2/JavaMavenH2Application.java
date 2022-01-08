package io.github.verissimor.examples.javamavenh2;

import io.github.verissimor.lib.jpamagicfilter.EnableJpaMagicFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJpaMagicFilter
@SpringBootApplication
public class JavaMavenH2Application {

  public static void main(String[] args) {
    SpringApplication.run(JavaMavenH2Application.class, args);
  }

}
