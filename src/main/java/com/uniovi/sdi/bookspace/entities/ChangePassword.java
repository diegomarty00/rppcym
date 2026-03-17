package com.uniovi.sdi.bookspace.entities;

public class ChangePassword {
    private String newPassword;
    private String newPasswordConfirm;

    // getters y setters
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getNewPasswordConfirm() { return newPasswordConfirm; }
    public void setNewPasswordConfirm(String newPasswordConfirm) { this.newPasswordConfirm = newPasswordConfirm; }
}
