package com.paymybuddy.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.springframework.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity to represents a user of application .
 *
 * @author delaval
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column
  private String email;

  @Column
  private String password;

  @Column
  private Byte enabled;


  @Column(name = "last_name")
  private String lastName;


  @Column(name = "first_name")
  private String firstName;

  @Column
  private String address;

  @Column
  private int zip;

  @Column
  private String city;

  @Column
  private String phone;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<OAuth2Provider> oauth2Identifiers = new HashSet<>();

  @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinTable(name = "connection_user", joinColumns = @JoinColumn(name = "user_id", table = "user"), inverseJoinColumns = @JoinColumn(name = "user_connection_id", table = "connection_user"))
  private Set<User> connectionUsers = new HashSet<>();

  @OneToMany(mappedBy = "sender", cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  private Set<ApplicationTransaction> senderTransactions = new HashSet<>();

  @OneToMany(mappedBy = "receiver", cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
  private Set<ApplicationTransaction> receiverTransactions = new HashSet<>();

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "bank_account_id")
  private BankAccount bankAccount;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "application_account_id", nullable = false)
  private ApplicationAccount applicationAccount;

  /**
   * method to link a OAuth2Identier to a user.
   *
   * @param identifier the OAuth2identifier to add to Set of user
   */
  public void addOAuth2Identifier(OAuth2Provider identifier) {
    this.oauth2Identifiers.add(identifier);
    identifier.setUser(this);
  }

  /**
   * method to remove a OAuth2Identifer from user.
   *
   * @param identifier the OAuth2identifier to remove
   */
  public void removeOAuth2Identifier(OAuth2Provider identifier) {
    this.oauth2Identifiers.remove(identifier);
    identifier.setUser(null);
  }

  /**
   * method to add a user to the list of connectionUsers.
   */
  public void addConnectionUser(User user) {
    if (user != null) {
      connectionUsers.add(user);
    }
  }

  /**
   * method to remove a user to the list of connectionUsers.
   */
  public void removeConnectionUser(User user) {
    if (user != null && this.connectionUsers.contains(user)) {
      connectionUsers.remove(user);
    }
  }

  /**
   * method to add Role to user.
   */
  public void addRole(Role role) {
    this.roles.add(role);
  }

  /**
   * method to add senderTransaction to user.
   */
  public void addSenderTransaction(ApplicationTransaction senderTransaction) {
    if (senderTransaction != null) {
      this.senderTransactions.add(senderTransaction);
    }
  }

  /**
   * method to add senderTransaction to user.
   */
  public void addReceiverTransaction(ApplicationTransaction transaction) {
    if (transaction != null) {
      this.senderTransactions.add(transaction);
    }
  }

  /**
   * return the fullname= firstname + lastname of user.
   * 
   * @return fullname
   */
  public String getFullName() {
    return StringUtils.capitalize(this.firstName) + " " + StringUtils.capitalize(this.lastName);
  }
}
