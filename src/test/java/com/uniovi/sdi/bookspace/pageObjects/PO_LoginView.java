package com.uniovi.sdi.bookspace.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_LoginView extends PO_NavView{

    static public void fillLoginForm(WebDriver driver, String dni, String password) {
        WebElement dniInput = driver.findElement(By.id("username"));
        dniInput.click();
        dniInput.clear();
        dniInput.sendKeys(dni);

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.click();
        passwordInput.clear();
        passwordInput.sendKeys(password);

        // Pulsar botón Login
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}
