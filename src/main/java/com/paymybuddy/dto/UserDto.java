package com.paymybuddy.dto;

import javax.validation.Constraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.ConstraintComposition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  @NotBlank
  @Size(max = 20)
  private String lastName;
  @NotBlank
  @Size(max= 20)
  private String firstName;
  @Email
  @NotNull
  private String email;

  private String address;
  private String city;
  private String phone;
  private int zip;

  @NotBlank
  @Size(min = 8)
  private String password;

  @NotBlank
  @Size(min = 8)
  private String matchingPassword;
}
