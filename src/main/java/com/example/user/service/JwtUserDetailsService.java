package com.example.user.service;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.example.user.entity.UserDao;
import com.example.user.exception.UserNotFoundException;
import com.example.user.model.JwtRequest;
import com.example.user.model.ResetPasswordRequest;
import com.example.user.model.UserDto;
import com.example.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class JwtUserDetailsService implements UserDetailsService{
    public static int noOfQuickServiceThreads = 20;

    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    private ScheduledExecutorService quickService = Executors.newScheduledThreadPool(noOfQuickServiceThreads);
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDao userDao =userRepository.findByEmailId(username).block();
        return new User(userDao.getEmailId(),userDao.getPassword(),
                new ArrayList<>());
    }
    public Mono<UserDao> save(UserDto user) {
        UserDao newUser = new UserDao();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmailId(user.getEmailId());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userRepository.save(newUser).map(userDao -> {
            sendVerificationEmail(userDao);
            return userDao;
        });
    }

    private boolean sendVerificationEmail(UserDao userDao) {
        System.out.println("http://localhost:8091/user/verify/"+userDao.getId());
        quickService.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    emailService.sendEmail(userDao.getEmailId(),"Verify your Email for user registration","http://localhost:8091/user/verify/"+userDao.getId());
                }catch(Exception e){
                    logger.error("Exception occur while send a mail : ",e);
                }
            }
        });
        return false;
    }

    public Mono<UserDao> verifyEmail(String userId) {
        return userRepository.findById(userId).flatMap(userDao -> {
            userDao.setVerified(true);
            return userRepository.save(userDao);
        }).switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with given user Id")));
    }

    public Mono<UserDao> editPassword(ResetPasswordRequest resetPassword) {
        return userRepository.findByEmailId(resetPassword.getEmailId()).flatMap(userDao -> {
           userDao.setPassword(bcryptEncoder.encode(userDao.getPassword()));
           return userRepository.save(userDao);
        });
    }

    public Mono<UserDao> authenticate(JwtRequest authenticationRequest) {
        return userRepository.findByEmailId(authenticationRequest.getUsername())
                .filter(userDao -> {
                    System.out.println("--------------------------------------------------------");
                    System.out.println(bcryptEncoder.encode(authenticationRequest.getPassword()));
                    System.out.println(userDao.getPassword());
                    System.out.println(bcryptEncoder.matches(authenticationRequest.getPassword(),userDao.getPassword()));
                    return bcryptEncoder.matches(authenticationRequest.getPassword(),userDao.getPassword());
                }).switchIfEmpty(Mono.error(new UserNotFoundException("invalid creds")));
    }
}
