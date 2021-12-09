package com.paymybuddy.service;

import java.util.List;

public interface AuthoritiesService {

  List<String> getRoles(String email);
}
