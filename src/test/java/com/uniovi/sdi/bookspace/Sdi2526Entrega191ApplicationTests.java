package com.uniovi.sdi.bookspace;

import com.uniovi.sdi.bookspace.pageObjects.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2526Entrega191ApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    //static String Geckodriver = "C:\\Path\\geckodriver-v0.36.0-win64.exe";
    //static String Geckodriver = "C:\\Dev\\tools\\selenium\\geckodriver-v0.36.0-win64.exe";
    static String GeckodriverRutaAdrian = "C:\\Users\\adria\\Desktop\\Burgui\\Clase\\Ingenieria\\Asignaturas\\SDI\\p6\\PL-SDI-Sesión5-material\\geckodriver.exe";
    //static String PathFirefox = "/Applications/Firefox.app/Contents/MacOS/firefox-bin";
//static String Geckodriver = "/Users/USUARIO/selenium/geckodriver-v0.30.0-macos";
// Para la versión de Firefox 121 en adelante la ruta de firefo en MAC es
//static String PathFirefox = "/Applications/Firefox.app/Contents/MacOS/firefox";
//Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox, GeckodriverRutaAdrian);
    static String URL = "http://localhost:8090";

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp(){
        driver.navigate().to(URL);
    }
    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown(){
        driver.manage().deleteAllCookies();
    }
    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {}
    //Al finalizar la última prueba
    @AfterAll
    static public void end() {
        //Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }

    @Test
    @Order(1)
    void PR01() {
        PO_HomeView.checkWelcomeToPage(driver, PO_Properties.getSPANISH());
    }
    //PR02. Opción de navegación. Pinchar en el enlace Registro en la página home
    @Test
    @Order(3)
    public void PR02() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
    }
    //PR03. Opción de navegación. Pinchar en el enlace Identifícate en la página home
    @Test
    @Order(4)
    public void PR03() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
    }
    //PR04. Opción de navegación. Cambio de idioma de Español a Inglés y vuelta a Español
    @Test
    @Order(5)
    public void PR04() {
        PO_HomeView.checkChangeLanguage(driver, "btnSpanish", "btnEnglish",
                PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
    }


    @Test
    @Order(6) //habra que cambiar el texto esperado cuando se cree la pagina de listado de espacios disponibles
    public void PRSignUpValid() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "77777778A", "Josefo", "Perez", "Fuego%789Luz", "Fuego%789Luz");
        String checkText = PO_HomeView.getP().getString("title", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "h2", checkText);
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    @Test
    @Order(7)
    public void PRSingUpDifferentPasswd() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "77787777Y", "Josefo", "Perez", "Fuego%789Luz", "Fuego%789zul");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "Error.signup.passwordConfirm.coincidence",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("Error.signup.passwordConfirm.coincidence",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    @Test
    @Order(8)
    public void PRSingUpRepeatDNI() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "77777777Y", "Josefo", "Perez", "Fuego%789Luz", "Fuego%789Luz");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "Error.signup.dni.duplicate",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("Error.signup.dni.duplicate",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    @Test
    @Order(9)
    public void PRSingUShortPasswd() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "77777777Y", "Josefo", "Perez", "Fuego", "Fuego");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "Error.signup.password.invalid",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("Error.signup.password.invalid",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    @Test
    @Order(10) //habra que cambiar el texto esperado cuando se cree la pagina de listado global de reservas
    public void PRLoginAdmin() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12345678Z", "@Dm1n1str@D0r");
        String checkText = PO_HomeView.getP().getString("title", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "h2", checkText);
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    @Test
    @Order(11) //habra que cambiar el texto esperado cuando se cree la pagina de listado de espacios disponibles
    public void PRLoginStandardUser() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");
        String checkText = PO_HomeView.getP().getString("title", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "h2", checkText);
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    @Test
    @Order(12)
    public void PRLoginInvalidDNI() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12343214T", "ClaveSegura#2026");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "login.incorrect",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("login.incorrect",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    @Test
    @Order(13)
    public void PRLoginInvalidPasswd() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "1234567");
        List<WebElement> result = PO_SignUpView.checkElementByKey(driver, "login.incorrect",
                PO_Properties.getSPANISH());
        String checkText = PO_HomeView.getP().getString("login.incorrect",
                PO_Properties.getSPANISH());
        Assertions.assertEquals(checkText, result.getFirst().getText());
    }

    @Test
    @Order(14)
    public void PRLogOutStandardUser() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");
        PO_NavView.clickOption(driver, "logout", "@href", "login");
    }

    @Test
    @Order(15)
    public void PRLogOutAdmin() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12345678Z", "@Dm1n1str@D0r");
        PO_NavView.clickOption(driver, "logout", "@href", "login");
    }

    @Test
    @Order(16)
    public void PRChangePasswdValid() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12345678Q", "Sol1!Luz7@Mar");
        String checkText = PO_HomeView.getP().getString("user.changePasswd.title", PO_Properties.getSPANISH());
        PO_NavView.clickOption(driver, "user/changePasswd", "h2", checkText);
        PO_ChangePasswdView.fillForm(driver, "nueva", "nueva");
        //Comprueba que redirige al inicio lo que significa que se ha cambiado correctamente
        String checkTextSucces = PO_HomeView.getP().getString("title", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "h2", checkTextSucces);
    }

    @Test
    @Order(17)
    public void PRChangePasswdInvalid() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "77777777Y", "ClaveSegura#2026");
        String checkText = PO_HomeView.getP().getString("user.changePasswd.title", PO_Properties.getSPANISH());
        PO_NavView.clickOption(driver, "user/changePasswd", "h2", checkText);
        PO_ChangePasswdView.fillForm(driver, "", "");
        //Comprueba que no redirige y sigue en el formulario de cambio de contraseña
        List<WebElement> result = PO_View.checkElementBy(driver, "h2", checkText);
    }
}
