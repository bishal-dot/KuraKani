package com.example.kurakani.Controlller;

import com.example.kurakani.model.User;
import com.example.kurakani.views.SignupActivity;

public class SignUpController {

    SignupActivity view;
    User model;

    public SignUpController(SignupActivity view){
        this.view = view;
    }

    public void validateFields(){

        String name = view.getName();
        String email = view.getEmail();
        String password = view.getPassword();
        String confirmPassword = view.getConfirmPassword();

        if (name.isEmpty())
            view.setNameError("Username can't be blank.");
        else if (email.isEmpty())
            view.setEmailError("Please enter a valid email address!");
        else if (password.isEmpty())
            view.setPasswordError("Password cannot be empty!");
        else if (password.length() < 8)
            view.setPasswordError("Password must be atleast 8 character long!");
        else if (confirmPassword.equals(password))
            view.setConfirmPasswordError("Password didn't match");
        else if (!view.isTermsChecked())
            view.showError("Please review terms and conditions.");
        else {
            model = new User(name, email, password, confirmPassword);
            createAccount(model);
        }
    }
    public void createAccount(User model){

        //Perform backend logic, API calling.....
        view.showProfileSetup();
    }
}
