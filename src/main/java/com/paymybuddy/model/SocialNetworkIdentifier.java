package com.paymybuddy.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocialNetworkIdentifier {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "social_network_identifier_id")
  private Long socialNetWorkIdentifierId;

  @NotBlank
  @Column(name = "network_provider_name")
  private String networkProviderName;


  @NotBlank
  @Column(name = "provider_user_id")
  private Long providerUserId;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id") // pour determiner la fk dans la table SocialNetWorkIdentifier
  private User user;
}
