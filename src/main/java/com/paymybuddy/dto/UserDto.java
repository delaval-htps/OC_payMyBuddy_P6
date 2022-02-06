package com.paymybuddy.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  @NotBlank(message = "The lastname must be not null or empty !")
  @Size(max = 20, message = "The lastname must contain more than 20 characters !")
  private String lastName;
  
  @NotBlank(message = "The firstname must be not null or empty !")
  @Size(max= 20,message = "The firstname must contain more than 20 characters !")
  private String firstName;
  
  @Email(message = "The email must contains a correct email with @ !")
  @NotBlank(message = "The email can't be empty or null !")
  private String email;

  private String address;
  private String city;
  private String phone;
  private int zip;

  @NotBlank(message = "The password must be not null or empty !")
  @Size(min = 8, message = "The password must contains more than 8 characters !")
  private String password;

  @NotBlank(message = "The password must be not null or empty !")
  @Size(min = 8, message = "The password must be not null or empty !")
  private String matchingPassword;

}
