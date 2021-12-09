package com.paymybuddy.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuthoritiesServiceImpl implements AuthoritiesService {

  @Override
  public List<String> getRoles(String email) {
    List<String> roles = new ArrayList<>();
    roles.add("ROLE_USER");
    return roles;
  }
}
