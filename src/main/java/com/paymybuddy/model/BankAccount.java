package com.paymybuddy.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount  extends Account implements  Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column
  private String bic;

  @Column
  private String iban;

  @Column
  @Getter(onMethod = @__(@Override))
  private double balance;

  // a bank Account can be open for many users ( example a couple)
  @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "bankAccount")
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
