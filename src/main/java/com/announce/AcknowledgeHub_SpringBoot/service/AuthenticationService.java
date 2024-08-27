package com.announce.AcknowledgeHub_SpringBoot.service;


//import com.announce.AcknowledgeHub_SpringBoot.entity.Staff;

import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.exception.EmailNullException;
import com.announce.AcknowledgeHub_SpringBoot.exception.StaffNotFoundException;
import com.announce.AcknowledgeHub_SpringBoot.exception.UserAlreadyExistsException;
import com.announce.AcknowledgeHub_SpringBoot.model.AuthenticationResponse;
import com.announce.AcknowledgeHub_SpringBoot.model.UserDTO;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequestMapping("/api/user")
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private MapperService mapperService;


    public AuthenticationResponse register(UserDTO request) {

        System.out.println("register here");
        if (request.getEmail() == null) {
            throw new EmailNullException("Email not must be null");
        }

        // Retrieve the existing user by email
        User existingUser = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new StaffNotFoundException("No staff member of ACE Group with email " + request.getEmail())
        );

        System.out.println("existingUser " + existingUser.isRegister_status());

        // Check if the user is already registered
        if (existingUser.isRegister_status()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already registered.");
        }

        // Map DTO to entity and set registration status
//        User user = mapperService.mapToUserEntity(request);
//        User user = new User();
//        user.setEmail(existingUser.getEmail());
        existingUser.setRegister_name(request.getName());
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRole(existingUser.getRole());
        existingUser.setRegister_status(true);

        // Save the user entity
        existingUser = userRepository.save(existingUser);

        // Prepare the response
        AuthenticationResponse response = new AuthenticationResponse();
        response.setMessage("Registration successful.");

        return response;
    }




    public AuthenticationResponse authenticate(UserDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));



        String token = jwtService.generateToken(user);


        AuthenticationResponse response = new AuthenticationResponse();
        response.setMessage("Login successful");
        response.setToken(token);
        return response;
    }

    public boolean resetPassword(String email, String newPassword) {
        User user = userRepository.findEmail(email);
        System.out.println("find by email"+user);
        if (user == null) {
            return false; // User not found
        }

        // Hash the new password
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        userRepository.save(user); // Save the updated user
        return true;
    }


}
