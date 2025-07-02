package edu.app.individuals;

import org.springframework.boot.SpringApplication;

public class TestFinTechAppApplication {

  public static void main(String[] args) {
    SpringApplication.from(IndividualsApplication::main).with(TestcontainersConfiguration.class).run(args);
  }
}
