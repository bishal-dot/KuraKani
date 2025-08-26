package com.example.kurakani.model;

public class ChangePasswordRequest {
    private String current_password;
    private String new_password;
    private String new_password_confirmation;

    public ChangePasswordRequest(String current, String newPass, String confirm) {
        this.current_password = current;
        this.new_password = newPass;
        this.new_password_confirmation = confirm;
    }
}
