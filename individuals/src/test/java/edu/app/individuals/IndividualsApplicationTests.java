package edu.app.individuals;

import edu.app.individuals.config.ContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@SpringBootTest
@ImportTestcontainers(ContainersConfig.class)
class IndividualsApplicationTests {

  @Test
  void contextLoads() { // check context
  }
}

