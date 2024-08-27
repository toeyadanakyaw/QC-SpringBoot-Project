package com.announce.AcknowledgeHub_SpringBoot.service;


import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.model.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MapperService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User mapToUserEntity(UserDTO userDTO) {
        User userEntity = modelMapper.map(userDTO, User.class);
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userEntity;
    }

    public UserDTO convertToDTO(User userEntity) {
        return modelMapper.map(userEntity, UserDTO.class);
    }


}
