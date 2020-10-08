package com.example.user.model;

import com.example.user.entity.UserDao;
import org.hibernate.validator.constraints.Length;


public class UserDto {
    @Length(min = 3,max = 30,message = "first name should have characters between 3 to 30 ")
    private String firstName;
    private String lastName;
    private String emailId;
    private String password;
    private String phoneNumber;

    public UserDto() {
    }

    public UserDto(UserDao userDao) {
        this.firstName = userDao.getFirstName();
        this.lastName = userDao.getLastName();
        this.emailId = userDao.getEmailId();
        this.password = null;
        this.phoneNumber = userDao.getPhoneNumber();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
