package com.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * the Dto of a conneted user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedUserDto {

    private String email;
    private String fullName;

}
