package com.secure_user_management.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.secure_user_management.dao.UserDAO;
import com.secure_user_management.dto.LoginRequestDTO;
import com.secure_user_management.dto.UserRegistrationDTO;
import com.secure_user_management.entity.User;
import com.secure_user_management.exceptions.UserAlreadyExistsException;
import com.secure_user_management.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final EncryptionService encryptionService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserService(EncryptionService encryptionService, UserDAO userDAO, JwtService jwtService, UserRepository userRepository) {
        super();
        this.encryptionService = encryptionService;
        this.userDAO = userDAO;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Autowired
    UserDAO userDAO;

    public void registerUser(User user) throws UserAlreadyExistsException {
        if (userDAO.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User Already Exists With Given Email");
        } else if (userDAO.findByPhone(user.getPhone()).isPresent()) {
            throw new UserAlreadyExistsException("User Already Exists With Given Phone");
        } else if (userDAO.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User Already Exists With Given Username");
        }
        user.setPassword(encryptionService.encryptPassword(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public String loginUserByUsername(@Valid LoginRequestDTO loginBody) {
        Optional<User> localUser = userDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if (localUser.isPresent()) {
            User user = localUser.get();
            if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                return jwtService.generateJWT(user);
            }
        }
        return null;
    }

    public String loginUserByEmail(@Valid LoginRequestDTO loginBody) {
        Optional<User> localUser = userDAO.findByEmailIgnoreCase(loginBody.getEmail());
        if (localUser.isPresent()) {
            User user = localUser.get();
            if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                return jwtService.generateJWT(user);
            }
        }
        return null;
    }

    public User getUserById(Long userId) {
        Optional<User> userOptional = userDAO.findById(userId);
        return userOptional.orElse(null);
    }

    public List<UserRegistrationDTO> listAllUsers() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return userRepository.findAll().stream()
                .map(user -> {
                    UserRegistrationDTO userResponse = new UserRegistrationDTO();
                    userResponse.setId(user.getId());
                    userResponse.setUsername(user.getUsername());
                    userResponse.setFirstName(user.getFirstName());
                    userResponse.setLastName(user.getLastName());
                    userResponse.setPhone(user.getPhone());
                    userResponse.setEmail(user.getEmail());
                    userResponse.setGender(user.getGender());
                    userResponse.setAge(user.getAge());
                    userResponse.setRoles(user.getRoles());
                    userResponse.setCreatedAt(user.getCreatedAt().format(formatter));
                    return userResponse;
                })
                .collect(Collectors.toList());
    }
}