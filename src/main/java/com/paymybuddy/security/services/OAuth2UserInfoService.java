package com.paymybuddy.security.services;

import java.security.Principal;
import java.util.Map;

public interface OAuth2UserInfoService {
    public Map<String, Object> getOauth2LoginInfo(Principal user);
}
