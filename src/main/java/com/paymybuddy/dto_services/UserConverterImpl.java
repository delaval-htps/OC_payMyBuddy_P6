package com.paymybuddy.dto_services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.model.User;

@Service
public class UserConverterImpl implements UserConverter{

    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    public UserDto entityToDto(User userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public User dtoToEntity(UserDto userDto) {
        
        return modelMapper.map(userDto, User.class);
    }
    
}
