package com.paymybuddy.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(name = "oauth2_identifier")
public class OAuth2AuthProviderUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "oauht2_identifier_id")
  private Long oauth2IdentifierId;

  @NotBlank
  @Column(name = "network_provider_name")
  @Enumerated(EnumType.STRING)
  private AuthProvider registrationId;

  @NotBlank
  @Column(name = "provider_user_id")
  private String providerUserId;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id") // pour determiner la fk
  private User user;
}
