package com.paymybuddy.model;

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
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@ToString
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column
  @NotBlank
  @Email
  private String email;

  @Column
  @NotBlank
  @Size(min = 8)
  private String password;

  @Column
  private Byte enabled;

  @NotBlank
  @Size(max=20)
  @Column(name = "last_name")
  private String lastName;

  @NotBlank
  @Size(max=20)
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
  private Set<Role> roles;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<OAuth2Provider> oauth2Identifiers = new HashSet<>();

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
}
