package com.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationIdentifierDto {

  private String email;
  private Byte enabled;
  private String password;
  private String matchingPassword;
}
