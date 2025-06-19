package edu.example.fin_tech_app;

import org.springframework.boot.SpringApplication;

public class TestFinTechAppApplication {

  public static void main(String[] args) {
    SpringApplication.from(FinTechAppApplication::main).with(TestcontainersConfiguration.class).run(args);
  }
}
