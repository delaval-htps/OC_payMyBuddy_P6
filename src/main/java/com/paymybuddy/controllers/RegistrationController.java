package com.paymybuddy.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.validation.Valid;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.exceptions.ApplicationAccountException;
import com.paymybuddy.model.Role;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.ApplicationAccountService;
import com.paymybuddy.service.OAuth2ProviderService;
import com.paymybuddy.service.RoleService;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class RegistrationController {

  @Autowired
  private UserService userService;

  @Autowired
  private OAuth2ProviderService oAuth2ProviderService;

  @Autowired
  private RoleService roleService;

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private ApplicationAccountService appAccountService;

  @Autowired
  PasswordEncoder passwordEncoder;

  /**
   * display a registration page to register a new user or a Oauth2Login not yet registred.
   * 
   * @param model the model to give to view registration
   * @param authentication authentication of user in the page registration
   * @return return the view without any restriction.
   */
  @GetMapping("/registration")
  public String registerNewUser(Model model, Authentication authentication) {

    UserDto userDto = new UserDto();

    if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
      CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
      userDto.setEmail(oAuth2User.getEmail());
      userDto.setLastName(oAuth2User.getLastName());
      userDto.setFirstName(oAuth2User.getFirstName());
    }

    model.addAttribute("user", userDto);
    return "registration";
  }

  /**
   * save a user just registrated with form registration.
   * 
   * @param model model to pass pojo with information about user
   * @param userDto informations of user form the form registration
   * @param bindingResult list of errors if problem of validation
   * @return page registration if there is a validation's error or home with sucessfull
   *         authentication.
   */
  @PostMapping("/registration")
  public String saveNewUser(Model model, @Valid @ModelAttribute(value = "user") UserDto userDto,
      BindingResult bindingResult, Authentication authentication) {

    if (bindingResult.hasErrors()) {
      return "registration";
    }

    Optional<User> existedUser = userService.findByEmail(userDto.getEmail());

    if (!existedUser.isPresent()) {
      User newUser = new User();
      newUser.setLastName(userDto.getLastName());
      newUser.setFirstName(userDto.getFirstName());
      newUser.setAddress(userDto.getAddress());
      newUser.setPhone(userDto.getPhone());
      newUser.setZip(userDto.getZip());
      newUser.setCity(userDto.getCity());
      newUser.setEmail(userDto.getEmail());
      newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
      newUser.setEnabled((byte) 1);
      Role userRole = roleService.findByName("ROLE_USER");
      newUser.addRole(userRole);

        // creation of his application account
      try {
        appAccountService.createAccountforUser(newUser);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        throw new ApplicationAccountException(
            "A problem occurs because , can't be able to create a account for new user"
                + newUser.getFullName());
      }

      User saveUser = userService.save(newUser);

      // case of new user but logged with OAuth2login()
      if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {

        oAuth2ProviderService
            .saveOAuth2ProviderForUser((CustomOAuth2User) authentication.getPrincipal(), saveUser);
      }
      // convertion of Oauth2Token to UsernamePasswordToken
      SecurityContext context = SecurityContextHolder.getContext();

      UserDetails userDetails = customUserDetailsService.loadUserByUsername(saveUser.getEmail());

      UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
          userDetails, userDetails.getPassword(), userDetails.getAuthorities());

      context.setAuthentication(userToken);


    } else {
      bindingResult.addError(new FieldError("user", "duplicatedUser",
          "Please chose another names and email because they already use by another user!"));

      log.error("user {} {} already existed in database.", userDto.getLastName(),
          userDto.getFirstName());
      return "registration";
    }

    return "redirect:/home";
  }
}
