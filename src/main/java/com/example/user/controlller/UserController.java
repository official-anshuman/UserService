package com.example.user.controlller;

import com.example.user.entity.UserDao;
import com.example.user.model.ResetPasswordRequest;
import com.example.user.model.Response;
import com.example.user.model.UserDto;
import com.example.user.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "hello",method = RequestMethod.GET)
    public String User(){
        return "Hello User";
    }

    @RequestMapping(value = "register",method = RequestMethod.POST)
    public Mono<ResponseEntity<Response>> saveUser(@RequestBody UserDto userDto){
        return userDetailsService.save(userDto).map(userDao -> {
            UserDto user = new UserDto();
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDao.getLastName());
            user.setPhoneNumber(userDao.getPhoneNumber());
            user.setEmailId(userDao.getEmailId());
            return ResponseEntity.ok(new Response(200,"user saved successfully",user));
        });
    }
    @RequestMapping(value = "user/verify/{userId}",method = RequestMethod.GET)
    public Mono<ResponseEntity<Response>> verifyEmailOfUser(@PathVariable("userId") String userId){
        return userDetailsService
                .verifyEmail(userId).map(userDao -> {
                    Response response = new Response(200,"successfully verified email",new UserDto(userDao));
                    return ResponseEntity.ok(response);
        });
    }
    @RequestMapping(value = "user/password",method = RequestMethod.PUT)
    public Mono<ResponseEntity<Response>> resetPasswordForUser(@Valid @RequestBody ResetPasswordRequest resetPassword){
        return userDetailsService.editPassword(resetPassword).map(userDao -> {
            Response response = new Response(200,"successfully changed password email",new UserDto(userDao));
            return ResponseEntity.ok(response);
        });

    }
}
