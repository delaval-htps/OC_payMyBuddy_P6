package com.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  private String lastName;
  private String firstName;
  private String email;
  private String address;
  private String city;
  private String phone;
  private int zip;
  private String password;
  private String matchingPassword;
}
