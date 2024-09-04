//package com.announce.AcknowledgeHub_SpringBoot.controller;
//
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//
//public class SecurityConfig {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors().and().csrf().disable()  // Disable CSRF for testing, configure properly for production
//                .authorizeRequests()
//                .antMatchers("/api/request-announce/create").permitAll()  // Allow public access to this endpoint
//                .anyRequest().authenticated();
//    }
//
//}
