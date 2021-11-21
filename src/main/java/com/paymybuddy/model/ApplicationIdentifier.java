package com.paymybuddy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_identifier")
public class ApplicationIdentifier {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "application_identifier_id")
  private Long applicationIdentifierId;

  @Column
  @NotBlank
  @Email
  private String email;

  @Column
  @NotBlank
  private String password;

  @Column
  private Byte enabled;
}
