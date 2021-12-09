package com.paymybuddy.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
  @Column(name = "user_id")
  private Long userId;

  @Column @NotBlank @Email private String email;

  @Column @NotBlank private String password;

  @Column private Byte enabled;

  @NotBlank
  @Column(name = "last_name")
  private String lastName;

  @NotBlank
  @Column(name = "first_name")
  private String firstName;

  @Column private String address;

  @Column private int zip;

  @Column private String city;

  @Column private String phone;

  @OneToMany(
      mappedBy = "user", // nom de l'attribut dans SocialNetworkIdentifier
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<Oauht2Identifier> oauth2Identifiers = new HashSet<>();

  /**
   * method to link a SocialNetworkIdentier to a user.
   *
   * @param sni the socialnetworkidentifier to add to Set of user
   */
  public void addSocialNetworkIdentifier(Oauht2Identifier identifier) {
    this.oauth2Identifiers.add(identifier);
    identifier.setUser(this);
  }

  /**
   * method to remove a SocialNetWorkIdentifer from user.
   *
   * @param sni the socialnetworkidentifier to remove
   */
  public void removeSocialNetworkIdentifier(Oauht2Identifier identifier) {
    this.oauth2Identifiers.remove(identifier);
    identifier.setUser(null);
  }
}
