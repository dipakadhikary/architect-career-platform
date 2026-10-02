package com.acos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the Architect Career Operating System (ACOS) Spring Boot application. */
@SpringBootApplication
public class AcosApplication {

  /**
   * Boots the ACOS application.
   *
   * @param args command-line arguments passed to Spring Boot
   */
  public static void main(String[] args) {
    SpringApplication.run(AcosApplication.class, args);
  }
}
