package com.example.user.controlller;

import com.example.user.exception.UserNotFoundException;
import com.example.user.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.example.user.service.JwtUserDetailsService;


import com.example.user.config.JwtTokenUtil;
import com.example.user.model.JwtRequest;
import com.example.user.model.JwtResponse;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public Mono<ResponseEntity<Response>> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

//        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        return userDetailsService.authenticate(authenticationRequest).map(userDao->{
            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new Response(200,"jwt token generated",new JwtResponse(token)));
        });

    }

//    private void authenticate(String username, String password) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new Exception("USER_DISABLED", e);
//        } catch (BadCredentialsException e) {
//            throw new Exception("INVALID_CREDENTIALS", e);
//        }
//    }
}
