package com.paymybuddy.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class to represents a bank account of a connected User.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class BankAccount extends Account implements Serializable {

  @Column
  private String bic;

  @Column
  private String iban;

  // a bank Account can be open for many users ( example a couple)
  @OneToMany(cascade = {  CascadeType.MERGE, CascadeType.REFRESH }, mappedBy = "bankAccount")
  private Set<User> users = new HashSet<>();

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "bank_card_id")
  private BankCard bankCard;

  /**
   * method to add user to a bank account.
   * 
   * @param user user to add to a BankAccount
   */

  public void addUser(User user) {
    if (user != null) {
      this.users.add(user);
      user.setBankAccount(this);
    }
  }

}
