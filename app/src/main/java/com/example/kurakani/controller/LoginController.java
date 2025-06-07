package com.example.kurakani.controller;

import com.example.kurakani.model.LoginModel;
import com.example.kurakani.views.LoginActivity;

public class LoginController {
    LoginActivity view;
    LoginModel model;

    public LoginController(LoginActivity view){
        this.view = view;
    }

    public void validateFields(){
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty()) view.setUsernameError("Username/Email cannot be empty");
        else if(username.length()<3) view.setUsernameError("Username must be 3 character long");
        else if (password.isEmpty()) view.setPasswordError("Password cannot be empty!");
        else if (password.length()<8) view.setPasswordError("Password must be atleast 8 characters");
        else {
            view.showHomePage();
        }
    }
}
