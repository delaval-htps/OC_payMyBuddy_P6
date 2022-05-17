package com.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import com.paymybuddy.model.AuthProvider;
import com.paymybuddy.model.OAuth2Provider;
import com.paymybuddy.repository.OAuth2ProviderRepository;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.apache.tomcat.jni.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class OAuth2ProviderServiceTest {

    @Mock
    private OAuth2ProviderRepository oAuth2ProviderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OAuth2ProviderService cut;

    @Test
    @Order(1)
    void getOAuht2ProviderByEmail_whenEmailAndRegistrationClientMatche_thenReturnAuthProvider() {

        OAuth2Provider mockOAuth2Provider = new OAuth2Provider();
        mockOAuth2Provider.setProviderUserId("59931452");
        mockOAuth2Provider.setRegistrationClient(AuthProvider.FACEBOOK);


        when(oAuth2ProviderRepository.findByEmail(Mockito.anyString(), Mockito.any(AuthProvider.class))).thenReturn(Optional.of(mockOAuth2Provider));

        Optional<OAuth2Provider> existeOAuth2Provider = cut.getOAuht2ProviderByEmail("test@gmail.com", AuthProvider.FACEBOOK);
        assertThat(existeOAuth2Provider).isNotEmpty().contains(mockOAuth2Provider);

    }

    @Test
    @Order(2)
    void getOAuht2ProviderByEmail_whenEmailAndRegistrationClientNotMatche_thenReturnOptionalEmpty() {

        when(oAuth2ProviderRepository.findByEmail(Mockito.anyString(), Mockito.any(AuthProvider.class))).thenReturn(Optional.empty());

        Optional<OAuth2Provider> existeOAuth2Provider = cut.getOAuht2ProviderByEmail("test@gmail.com", AuthProvider.FACEBOOK);
        assertThat(existeOAuth2Provider).isEmpty();

    }

    @Test
    @Order(3)
    void saveOrUpdateOAuth2ProviderForUser_whenUserExistedLoggedWithOAuth2AndProviderNotRegistred_thenOAuth2ProviderSaved() {

        // GIVEN

        // mock a CustomOAuth2User from spring security
        CustomOAuth2User mockCustomOAuth2User = mock(CustomOAuth2User.class);

        // creation of existed User
        com.paymybuddy.model.User existedUser = new com.paymybuddy.model.User();
        existedUser.setLastName("delaval");
        existedUser.setFirstName("dorian");
        existedUser.setEmail("dorian.delaval@gmail.com");

        // Oauth2provider to linked with existed user
        // OAuth2Provider newOAuth2Provider = new OAuth2Provider(1L, AuthProvider.FACEBOOK, "59945414",
        // existedUser);

        // WHEN
        when(mockCustomOAuth2User.getClientId()).thenReturn("1L");
        when(mockCustomOAuth2User.getClientRegistrationId()).thenReturn("FACEBOOK");
        when(userService.save(Mockito.any(com.paymybuddy.model.User.class))).thenReturn(existedUser);
        // THEN

        ArgumentCaptor<OAuth2Provider> OAuth2ProvideCaptor = ArgumentCaptor.forClass(OAuth2Provider.class);
        verify(oAuth2ProviderRepository, times(1)).save(OAuth2ProvideCaptor.capture());
        assertThat(OAuth2ProvideCaptor.capture().getRegistrationClient()).isEqualTo(AuthProvider.FACEBOOK);
        assertThat(OAuth2ProvideCaptor.capture().getUser()).isEqualTo(existedUser);

    }

}
