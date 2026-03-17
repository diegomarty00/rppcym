package com.uniovi.sdi.bookspace.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_ChangePasswdView extends PO_NavView{

    static public void fillForm(WebDriver driver, String password, String confirmPasswd) {
        WebElement dniInput = driver.findElement(By.id("newPassword"));
        dniInput.click();
        dniInput.clear();
        dniInput.sendKeys(password);

        WebElement passwordInput = driver.findElement(By.id("newPasswordConfirm"));
        passwordInput.click();
        passwordInput.clear();
        passwordInput.sendKeys(confirmPasswd);

        // Pulsar botón Login
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}
