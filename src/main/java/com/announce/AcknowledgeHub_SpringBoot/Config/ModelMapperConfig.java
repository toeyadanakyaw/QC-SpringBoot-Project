package com.announce.AcknowledgeHub_SpringBoot.Config;


import com.announce.AcknowledgeHub_SpringBoot.model.AuthenticationResponse;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public AuthenticationResponse authenticationResponse(){
        return  new AuthenticationResponse();
    }

}
