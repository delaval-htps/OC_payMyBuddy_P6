package com.paymybuddy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.paymybuddy.controllers.LoginController;

@SpringBootTest
class PayMyBuddyApplicationTests {

  @Autowired
  private LoginController controllerUnderTest;

  @Test
  void contextLoads() {
    assertThat(controllerUnderTest).isNotNull();
  }

}
