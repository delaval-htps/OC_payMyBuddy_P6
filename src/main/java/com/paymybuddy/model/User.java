package com.paymybuddy.model;

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
 *
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

  @NotBlank
  @Column(name = "last_name")
  private String lastName;

  @NotBlank
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

  @OneToMany(
      mappedBy = "user", // nom de l'attribut dans SocialNetworkIdentifier
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<SocialNetworkIdentifier> socialNetWorkIdentifiers = new HashSet<>();

  /**
   * method to link a SocialNetworkIdentier to a user.
   *
   * @param sni the socialnetworkidentifier to add to Set of user
   */
  public void addSocialNetworkIdentifier(SocialNetworkIdentifier sni) {
    this.socialNetWorkIdentifiers.add(sni);
    sni.setUser(this);
  }

  /**
   * method to remove a SocialNetWorkIdentifer from user.
   *
   * @param sni the socialnetworkidentifier to remove
   */
  public void removeSocialNetworkIdentifier(SocialNetworkIdentifier sni) {
    this.socialNetWorkIdentifiers.remove(sni);
    sni.setUser(null);
  }

  @OneToOne
  @JoinColumn(name = "application_identifier_id")
  private ApplicationIdentifier applicationIdentifier;
}
