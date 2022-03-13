package com.paymybuddy.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount implements Serializable {

  private static final long serialVersionUID = 31L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column
  private int bankCode;

  @Column
  private int branchCode;

  @Column
  private int accountNumber;

  @Column
  private int ribKey;

  @Column
  private String bic;

  @Column
  private String iban;

  @Column
  private double balance;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "bank_card_id")
  private BankCard bankCard;

  @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH},
      mappedBy = "bankAccount")
  private User user;
}
