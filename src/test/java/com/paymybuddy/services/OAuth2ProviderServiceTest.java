package com.paymybuddy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import com.paymybuddy.exceptions.OAuth2ProviderNotFoundException;
import com.paymybuddy.model.AuthProvider;
import com.paymybuddy.model.OAuth2Provider;
import com.paymybuddy.repository.OAuth2ProviderRepository;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.service.OAuth2ProviderService;
import com.paymybuddy.service.UserService;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

        // mock a CustomOAuth2User argument from spring security considering existed!
        CustomOAuth2User mockCustomOAuth2User = mock(CustomOAuth2User.class);
        when(mockCustomOAuth2User.getClientId()).thenReturn("clientIdOfCustomOAuth2user");
        when(mockCustomOAuth2User.getClientRegistrationId()).thenReturn("FACEBOOK");

        // mock of existed User
        com.paymybuddy.model.User existedUser = mock(com.paymybuddy.model.User.class);

        // mock userService.save
        when(userService.save(Mockito.any(com.paymybuddy.model.User.class))).thenReturn(null);

        // WHEN
        OAuth2Provider saveOAuth2ProviderForUser = cut.saveOAuth2ProviderForUser(mockCustomOAuth2User, existedUser);

        // THEN

        // verify if existedUser.addOauth2identifier is call and retrieve it''s argument
        ArgumentCaptor<OAuth2Provider> oAuth2ProviderCaptor = ArgumentCaptor.forClass(OAuth2Provider.class);
        verify(existedUser, times(1)).addOAuth2Identifier(oAuth2ProviderCaptor.capture());
        assertThat(oAuth2ProviderCaptor.getValue().getProviderUserId()).isEqualTo("clientIdOfCustomOAuth2user");
        assertThat(oAuth2ProviderCaptor.getValue().getRegistrationClient()).isEqualTo(AuthProvider.FACEBOOK);

        verify(userService, times(1)).save(Mockito.any(com.paymybuddy.model.User.class));

        assertThat(saveOAuth2ProviderForUser.getProviderUserId()).isEqualTo("clientIdOfCustomOAuth2user");
        assertThat(saveOAuth2ProviderForUser.getRegistrationClient()).isEqualTo(AuthProvider.FACEBOOK);

    }


    @Test
    @Order(4)
    void saveOrUpdateOAuth2ProviderForUser_whenUserExistedAndCustomOAuth2UserHasNoClientId_thenReturnThrowsException() {

        // GIVEN

        // mock a CustomOAuth2User but with no clientRegistration and clientId!
        CustomOAuth2User mockCustomOAuth2User = mock(CustomOAuth2User.class);
        when(mockCustomOAuth2User.getClientId()).thenReturn(null);

        // mock of existed User
        com.paymybuddy.model.User existedUser = mock(com.paymybuddy.model.User.class);

        // WHEN
        assertThrows(OAuth2ProviderNotFoundException.class, () -> {
            cut.saveOAuth2ProviderForUser(mockCustomOAuth2User, existedUser);
        });

        // THEN
        verify(existedUser, never()).addOAuth2Identifier(Mockito.any(OAuth2Provider.class));
        verify(userService, never()).save(Mockito.any(com.paymybuddy.model.User.class));
    }

    @Test
    @Order(5)
    void saveOrUpdateOAuth2ProviderForUser_whenUserExistedAndCustomOAuth2UserHasNoregistrationId_thenReturnThrowsException() {

        // GIVEN

        // mock a CustomOAuth2User but with no clientRegistration and clientId!
        CustomOAuth2User mockCustomOAuth2User = mock(CustomOAuth2User.class);
        when(mockCustomOAuth2User.getClientRegistrationId()).thenReturn(null);
        when(mockCustomOAuth2User.getClientId()).thenReturn("mockClientId");
        // mock of existed User
        com.paymybuddy.model.User existedUser = mock(com.paymybuddy.model.User.class);

        // WHEN
        assertThrows(OAuth2ProviderNotFoundException.class, () -> {
            cut.saveOAuth2ProviderForUser(mockCustomOAuth2User, existedUser);
        });

        // THEN
        verify(existedUser, never()).addOAuth2Identifier(Mockito.any(OAuth2Provider.class));
        verify(userService, never()).save(Mockito.any(com.paymybuddy.model.User.class));
    }

    @Test

    @Order(6)
    void saveOrUpdateOAuth2ProviderForUser_whenUserExistedAndAuthProviderNotAuthorized_thenReturnThrowsException() {

        // GIVEN

        // mock a CustomOAuth2User but not authorized for application!
        CustomOAuth2User mockCustomOAuth2User = mock(CustomOAuth2User.class);
        when(mockCustomOAuth2User.getClientRegistrationId()).thenReturn("NOTAUTHORIZED");
        when(mockCustomOAuth2User.getClientId()).thenReturn("1L");
        // mock of existed User
        com.paymybuddy.model.User existedUser = mock(com.paymybuddy.model.User.class);

        // method static of enum AuthProvider.fromString() return null because clientregistrationId is not
        // listed in enum

        // WHEN
        assertThrows(OAuth2ProviderNotFoundException.class, () -> {
            cut.saveOAuth2ProviderForUser(mockCustomOAuth2User, existedUser);
        });

        // THEN
        verify(existedUser, never()).addOAuth2Identifier(Mockito.any(OAuth2Provider.class));
        verify(userService, never()).save(Mockito.any(com.paymybuddy.model.User.class));
    }
}
