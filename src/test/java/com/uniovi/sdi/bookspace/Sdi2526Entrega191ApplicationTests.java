package com.uniovi.sdi.bookspace;

import com.uniovi.sdi.bookspace.pageObjects.PO_ChangePasswdView;
import com.uniovi.sdi.bookspace.pageObjects.PO_HomeView;
import com.uniovi.sdi.bookspace.pageObjects.PO_LoginView;
import com.uniovi.sdi.bookspace.pageObjects.PO_NavView;
import com.uniovi.sdi.bookspace.pageObjects.PO_Properties;
import com.uniovi.sdi.bookspace.pageObjects.PO_SignUpView;
import com.uniovi.sdi.bookspace.pageObjects.PO_View;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import com.uniovi.sdi.bookspace.pageObjects.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2526Entrega191ApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";

    static String GeckodriverRutaAdrian = "C:\\Users\\adria\\Desktop\\Burgui\\Clase\\Ingenieria\\Asignaturas\\SDI\\p6\\PL-SDI-SesiÃ³n5-material\\geckodriver.exe";
    static String GeckodriverRutaMario = "C:\\Uniovi\\Tercero\\SDI\\Sesion6\\PL-SDI-Sesión5-material\\geckodriver.exe";

    static WebDriver driver = getDriver(PathFirefox, GeckodriverRutaMario);
    static String URL = "http://localhost:8090";
    private static final DateTimeFormatter DATE_TIME_FORM_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp() {
        driver.navigate().to(URL);
    }

    @AfterEach
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    @BeforeAll
    static public void begin() {
    }

    @AfterAll
    static public void end() {
        driver.quit();
    }

    private void loginAsStandardUser() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");
    }

    private void fillReservationForm(String spaceName, LocalDateTime startDateTime,
                                     LocalDateTime endDateTime, String reason) {
        new Select(driver.findElement(By.id("spaceId"))).selectByVisibleText(spaceName);

        WebElement startInput = driver.findElement(By.id("startDateTime"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                startInput, startDateTime.format(DATE_TIME_FORM_FORMAT));

        WebElement endInput = driver.findElement(By.id("endDateTime"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                endInput, endDateTime.format(DATE_TIME_FORM_FORMAT));

        WebElement reasonInput = driver.findElement(By.id("reason"));
        reasonInput.click();
        reasonInput.clear();
        reasonInput.sendKeys(reason);

        driver.findElement(By.cssSelector("form button[type='submit']")).click();
    }

    //PR01 Registro de usuario estándar con datos válidos
    @Test
    @Order(1)
    public void PR01() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "77777778A", "Josefo", "Perez", "Fuego%789Luz", "Fuego%789Luz");
        String checkText = PO_HomeView.getP().getString("spaces.title", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    //PR02 Registro de usuario estándar con datos inválidos (duplicada incorrectamente)
    @Test
    @Order(2)
    public void PR02() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "77787777Y", "Josefo", "Perez", "Fuego%789Luz", "Fuego%789zul");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "Error.signup.passwordConfirm.coincidence",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("Error.signup.passwordConfirm.coincidence",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    //PR03 Registro de usuario estándar con datos inválidos (usuario con mismo DNI ya registrado).
    @Test
    @Order(3)
    public void PR03() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "77777777Y", "Josefo", "Perez", "Fuego%789Luz", "Fuego%789Luz");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "Error.signup.dni.duplicate",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("Error.signup.dni.duplicate",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    //PR04 Registro con contraseña que no cumple requisitos (longitud o símbolos).
    @Test
    @Order(4)
    public void PR04() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "77777777Y", "Josefo", "Perez", "Fuego", "Fuego");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "Error.signup.password.invalid",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("Error.signup.password.invalid",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    //PR05 Inicio de sesión con datos válidos (administrador).
    @Test
    @Order(5)
    public void PR05() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12345678Z", "@Dm1n1str@D0r");
        String checkText = PO_HomeView.getP().getString("space.reservations.list", PO_Properties.getSPANISH());
        WebElement title = driver.findElement(By.tagName("h1"));
        Assertions.assertEquals(checkText, title.getText());
    }

    //PR06 Inicio de sesión con datos válidos (usuario estándar)
    @Test
    @Order(6)
    public void PR06() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");
        String checkText = PO_HomeView.getP().getString("space.title", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    //PR07
    @Test
    @Order(7)
    public void PR07() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12343214T", "ClaveSegura#2026");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "login.incorrect",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("login.incorrect",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    //PR08
    @Test
    @Order(8)
    public void PR08() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "1234567");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "login.incorrect",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("login.incorrect",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    //PR09 Salir de sesión con usuario estándar
    @Test
    @Order(9)
    public void PR09() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");
        PO_NavView.clickOption(driver, "logout", "@href", "login");
    }

    //PR10 Salir de sesión con administrador.
    @Test
    @Order(10)
    public void PR10() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12345678Z", "@Dm1n1str@D0r");
        PO_NavView.clickOption(driver, "logout", "@href", "login");
    }



    @Test
    @Order(30)
    public void PR30() {
        loginAsStandardUser();
        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        String reason = "Reserva de prueba valida";
        fillReservationForm("Cowork 3", startDateTime, endDateTime, reason);

        String listTitle = PO_HomeView.getP().getString("reservations.mine.title", PO_Properties.getSPANISH());
        List<WebElement> titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());
        Assertions.assertFalse(PO_View.checkElementBy(driver, "text", "Cowork 3").isEmpty());
    }

    @Test
    @Order(31)
    public void PR31() {
        loginAsStandardUser();

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2).withHour(13).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.minusHours(1);

        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva invalida");

        String errorText = PO_HomeView.getP().getString("reservations.add.error.range", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", errorText);
        Assertions.assertEquals(errorText, result.getFirst().getText());
    }

    @Test
    @Order(32)
    public void PR32() {
        loginAsStandardUser();

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime firstStartDateTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime firstEndDateTime = firstStartDateTime.plusHours(2);
        fillReservationForm("Cowork 3", firstStartDateTime, firstEndDateTime, "Primera reserva");

        String listTitle = PO_HomeView.getP().getString("reservations.mine.title", PO_Properties.getSPANISH());
        List<WebElement> titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime secondStartDateTime = firstStartDateTime.plusMinutes(30);
        LocalDateTime secondEndDateTime = firstEndDateTime.plusHours(1);
        fillReservationForm("Cowork 3", secondStartDateTime, secondEndDateTime, "Reserva solapada");

        String errorText = PO_HomeView.getP().getString("reservations.add.error.overlap", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", errorText);
        Assertions.assertEquals(errorText, result.getFirst().getText());
    }

    @Test
    @Order(33)
    public void PR33() {
        loginAsStandardUser();

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusHours(1);
        fillReservationForm("Sala Azul", startDateTime, endDateTime, "Reserva en bloqueo");

        String errorText = PO_HomeView.getP().getString("reservations.add.error.blocked", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", errorText);
        Assertions.assertEquals(errorText, result.getFirst().getText());
    }

    @Test
    @Order(36)
    public void PR36() {
        loginAsStandardUser();

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva a cancelar");

        String listTitle = PO_HomeView.getP().getString("reservations.mine.title", PO_Properties.getSPANISH());
        List<WebElement> titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());

        WebElement cancelButton = driver.findElement(By.xpath(
                "//tr[td[contains(text(),'Cowork 3')] and td[contains(text(),'ACTIVE')]]//button[@type='submit']"));
        cancelButton.click();

        List<WebElement> cancelledReservation = PO_View.checkElementBy(driver, "free",
                "//tr[td[contains(text(),'Cowork 3')] and td[contains(text(),'CANCELLED')]]");
        Assertions.assertFalse(cancelledReservation.isEmpty());

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");
        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva tras cancelacion");

        titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());
        Assertions.assertFalse(PO_View.checkElementBy(driver, "text", "Reserva tras cancelacion").isEmpty());
    }

    //PR37 Modificar la contraseña con datos válidos.
    @Test
    @Order(37)
    public void PR37() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12345678Q", "Sol1!Luz7@Mar");
        String checkText = PO_HomeView.getP().getString("user.changePasswd.title", PO_Properties.getSPANISH());
        PO_NavView.clickOption(driver, "user/changePasswd", "h2", checkText);
        PO_ChangePasswdView.fillForm(driver, "nueva", "nueva");
        String checkTextSucces = PO_HomeView.getP().getString("title", PO_Properties.getSPANISH());
        PO_View.checkElementBy(driver, "h2", checkTextSucces);
    }

    //PR38 Modificar la contraseña con datos inválidos (vacía).
    @Test
    @Order(38)
    public void PR38() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");
        String checkText = PO_HomeView.getP().getString("user.changePasswd.title", PO_Properties.getSPANISH());
        PO_NavView.clickOption(driver, "user/changePasswd", "h2", checkText);
        PO_ChangePasswdView.fillForm(driver, "", "");

        //Comprueba que no redirige y sigue en el formulario de cambio de contraseña
        PO_View.checkElementBy(driver, "h2", checkText);
    }

    //PR39. Opción de navegación. Cambio de idioma de Español a Inglés y vuelta a Español
    @Test
    @Order(39)
    public void PR39() {
        PO_HomeView.checkChangeLanguage(driver, "btnSpanish", "btnEnglish",
                PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
    }

    //Pr40 Acceso denegado de usuario estándar a recursos de administración
    @Test
    @Order(40)
    public void PR40() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");

        driver.navigate().to(URL + "/reservations/admin");

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(
                pageSource.contains("403") || pageSource.toLowerCase().contains("forbidden"),
                "Un usuario estándar no debería poder acceder a /reservations/admin"
        );
    }

    //PR41 Intento de cancelar reserva ajena (debe fallar).
    @Test
    @Order(41)
    public void PR41() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");

        ((JavascriptExecutor) driver).executeScript("""
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = arguments[0];
                document.body.appendChild(form);
                form.submit();
                """, URL + "/reservations/1/cancel");

        driver.navigate().to(URL + "/logout");

        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "10000001S", "Us3r@1-PASSW");
        driver.navigate().to(URL + "/reservations/list");

        List<WebElement> result = PO_View.checkElementBy(driver, "free",
                "//tbody/tr[td[contains(text(),'Sala Azul')] and td[contains(text(),'ACTIVE')]]");
        Assertions.assertFalse(result.isEmpty(), "La reserva ajena no debería cancelarse");
    }

}
