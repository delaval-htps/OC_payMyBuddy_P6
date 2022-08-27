package com.paymybuddy.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class that represents a application account of connected user .
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ApplicationAccount extends Account implements Serializable {

  @Column
  @NotBlank(message = "the number of account must be not null.")
  private String accountNumber;

  @OneToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, mappedBy = "applicationAccount")
  private User user;

}
