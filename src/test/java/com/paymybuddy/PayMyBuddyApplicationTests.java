package com.paymybuddy;

import static org.assertj.core.api.Assertions.assertThat;

import com.paymybuddy.controllers.UserController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PayMyBuddyApplicationTests {

  @Autowired
  private UserController controllerUnderTest;

  @Test
  void contextLoads() {
    assertThat(controllerUnderTest).isNotNull();
  }

}
