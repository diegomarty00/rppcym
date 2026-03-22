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
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2526Entrega191ApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";

    static String GeckodriverRutaDavid = "C:\\Users\\Jose David\\Downloads" +
            "\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver.exe";
    static String GeckodriverRutaAdrian = "C:\\Users\\adria\\Desktop\\Burgui\\Clase\\Ingenieria\\Asignaturas\\SDI\\p6\\PL-SDI-Sesión5-material\\geckodriver.exe";
    static String GeckodriverRutaMario = "C:\\Uniovi\\Tercero\\SDI\\Sesion6\\PL-SDI-Sesión5-material\\geckodriver.exe";
    static String GeckodriverRutaDiego = "C:\\Dev\\tools\\selenium\\geckodriver.exe";

    static WebDriver driver = getDriver(PathFirefox, GeckodriverRutaDiego);
    static String URL = "http://localhost:8090";
    private static final DateTimeFormatter DATE_TIME_FORM_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter DATE_FORM_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        return getDriverWithDownloadDir(PathFirefox, Geckodriver, null);
    }

    private static WebDriver getDriverWithDownloadDir(String PathFirefox, String Geckodriver, String downloadDir) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        FirefoxOptions options = new FirefoxOptions();
        if (downloadDir != null) {
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.dir", downloadDir);
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                    "text/csv,application/csv,application/vnd.ms-excel,text/plain");
            profile.setPreference("pdfjs.disabled", true);
            options.setProfile(profile);
        }
        driver = new FirefoxDriver(options);
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
        loginAsUser("77777777Y", "ClaveSegura#2026");
    }

    private void loginAsAdmin() {
        loginAsUser("12345678Z", "@Dm1n1str@D0r");
    }

    private void loginAsUser(String dni, String password) {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, dni, password);
    }

    private Path waitForCsvDownload(Path downloadDir, long timeoutMillis) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMillis) {
            try (Stream<Path> files = Files.list(downloadDir)) {
                Path csv = files
                        .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
                        .filter(path -> !path.getFileName().toString().endsWith(".part"))
                        .findFirst()
                        .orElse(null);
                if (csv != null && Files.size(csv) > 0) {
                    return csv;
                }
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
    }

    private LocalDateTime base = LocalDateTime.now().plusDays(1)
            .withHour(9).withMinute(0).withSecond(0).withNano(0);

    private void setDateTimeInput(String inputId, LocalDateTime value) {
        WebElement input = driver.findElement(By.id(inputId));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                input, value.format(DATE_TIME_FORM_FORMAT));
    }

    private void setDateInput(String inputId, LocalDate value) {
        WebElement input = driver.findElement(By.id(inputId));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                input, value.format(DATE_FORM_FORMAT));
    }

    private void fillReservationForm(String spaceName, LocalDateTime startDateTime,
                                     LocalDateTime endDateTime, String reason) {
        new Select(driver.findElement(By.id("spaceId"))).selectByVisibleText(spaceName);
        setDateTimeInput("startDateTime", startDateTime);
        setDateTimeInput("endDateTime", endDateTime);
        WebElement reasonInput = driver.findElement(By.id("reason"));
        reasonInput.click();
        reasonInput.clear();
        reasonInput.sendKeys(reason);

        driver.findElement(By.cssSelector("form button[type='submit']")).click();
    }

    private String getFirstAvailableSpaceName(String... candidates) {
        List<WebElement> options = driver.findElements(By.cssSelector("#spaceId option"));
        for (String candidate : candidates) {
            boolean present = options.stream().anyMatch(option -> candidate.equals(option.getText()));
            if (present) {
                return candidate;
            }
        }
        Assertions.fail("No se encontro ninguno de los espacios esperados en el formulario de reserva");
        return null;
    }

    private void fillBlockForm(String start, String end, String reason) {

        WebElement startInput = driver.findElement(By.id("startDateTime"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                startInput, start);

        WebElement endInput = driver.findElement(By.id("endDateTime"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                endInput, end);

        driver.findElement(By.id("reason")).clear();
        driver.findElement(By.id("reason")).sendKeys(reason);

        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }


    private void fillRecurringReservationForm(String spaceName, LocalDateTime startDateTime,
                                              LocalDateTime endDateTime, String reason,
                                              String recurrenceFrequency, LocalDate recurrenceEndDate) {
        new Select(driver.findElement(By.id("spaceId"))).selectByVisibleText(spaceName);
        setDateTimeInput("startDateTime", startDateTime);
        setDateTimeInput("endDateTime", endDateTime);

        WebElement reasonInput = driver.findElement(By.id("reason"));
        reasonInput.click();
        reasonInput.clear();
        reasonInput.sendKeys(reason);

        new Select(driver.findElement(By.id("recurrenceFrequency"))).selectByValue(recurrenceFrequency);
        setDateInput("recurrenceEndDate", recurrenceEndDate);
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
        loginAsAdmin();
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
        String checkText = PO_HomeView.getP().getString("spaces.title", PO_Properties.getSPANISH());
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

    // Registrar un nuevo espacio con datos válidos (administrador)
    @Test
    @Order(11)
    public void PR11() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOption(driver, "space/add", "id", "type");

        // Rellenar formulario
        driver.findElement(By.id("type")).sendKeys("AULA");
        driver.findElement(By.id("name")).sendKeys("Espacio Test 11");
        driver.findElement(By.id("location")).sendKeys("Planta 1");
        driver.findElement(By.id("capacity")).sendKeys("20");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        driver.findElement(By.id("spacesDropdown")).click();
        driver.findElement(By.xpath("//a[@href='/space']")).click();

        PO_View.checkElementBy(driver, "text", "Espacio Test 11");

    }

    // Registrar un nuevo espacio con datos inválidos (nombre vacío)
    @Test
    @Order(12)
    public void PR12() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOption(driver, "space/add", "id", "type");

        driver.findElement(By.id("type")).sendKeys("AULA");
        driver.findElement(By.id("name")).sendKeys("");
        driver.findElement(By.id("location")).sendKeys("Planta 2");
        driver.findElement(By.id("capacity")).sendKeys("15");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        PO_View.checkElementByKey(driver, "space.error.empty.name", PO_Properties.getSPANISH());

    }

    // Registrar un nuevo espacio con datos inválidos (capacidad menor que 1)
    @Test
    @Order(13)
    public void PR13() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOption(driver, "space/add", "id", "type");

        driver.findElement(By.id("type")).sendKeys("AULA");
        driver.findElement(By.id("name")).sendKeys("Espacio Inválido");
        driver.findElement(By.id("location")).sendKeys("Planta -1");
        driver.findElement(By.id("capacity")).sendKeys("0");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        PO_View.checkElementByKey(driver, "space.error.capacity", PO_Properties.getSPANISH());
    }

    // Registrar espacio con el mismo nombre que otro activo
    @Test
    @Order(14)
    public void PR14() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOption(driver, "space/add", "id", "type");

        driver.findElement(By.id("type")).sendKeys("AULA");
        driver.findElement(By.id("name")).sendKeys("Sala Azul"); // ya existe
        driver.findElement(By.id("location")).sendKeys("Planta 0");
        driver.findElement(By.id("capacity")).sendKeys("40");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        PO_View.checkElementByKey(driver, "space.error.duplicate", PO_Properties.getSPANISH());
    }

    // Editar un espacio existente con datos válidos
    @Test
    @Order(15)
    public void PR15() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();

        PO_NavView.clickOptionById(driver, "spaces-reservation");

        driver.findElements(By.xpath("//a[contains(@href,'/space/edit/')]")).get(0).click();

        WebElement name = driver.findElement(By.id("name"));
        name.clear();
        name.sendKeys("Espacio Editado 15");

        WebElement cap = driver.findElement(By.id("capacity"));
        cap.clear();
        cap.sendKeys("50");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        PO_View.checkElementBy(driver, "text", "Espacio Editado 15");
    }

    // Editar un espacio existente con datos inválidos (capacidad menor que 1)
    @Test
    @Order(16)
    public void PR16() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOptionById(driver, "spaces-reservation");

        driver.findElements(By.xpath("//a[contains(@href,'/space/edit/')]")).get(0).click();

        WebElement cap = driver.findElement(By.id("capacity"));
        cap.clear();
        cap.sendKeys("0");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        PO_View.checkElementByKey(driver, "space.error.capacity", PO_Properties.getSPANISH());
    }

    // Desactivar un espacio y verificar que no se puede reservar
    @Test
    @Order(17)
    public void PR17() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOptionById(driver, "spaces-reservation");

        driver.findElements(By.xpath("//form[contains(@action,'/space/toggle')]")).get(0).submit();

        PO_View.checkElementBy(driver, "text", "Inactivo");
    }

    // Activar un espacio y verificar que sí se puede reservar
    @Test
    @Order(18)
    public void PR18() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOptionById(driver, "spaces-reservation");

        driver.findElements(By.xpath("//form[contains(@action,'/space/toggle')]")).get(0).submit();

        PO_View.checkElementBy(driver, "text", "Activo");
    }

    // Crear un bloqueo de mantenimiento válido
    @Test
    @Order(19)
    public void PR19() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOptionById(driver, "spaces-reservation");

        driver.findElements(By.xpath("//a[contains(@href,'/space/details')]")).get(0).click();
        driver.findElement(By.xpath("//a[contains(@href,'/availability')]")).click();
        driver.findElement(By.xpath("//a[contains(@href,'/blocks/add')]")).click();

        // Crear un bloqueo válido (que NO solape)
        // Propuesta: 1 día después de base, a partir de las 20:00 → 22:00
        LocalDateTime start = base.plusDays(1).withHour(20).withMinute(0);
        LocalDateTime end = start.plusHours(2);

        fillBlockForm(start.toString(), end.toString(), "Bloqueo válido");

        PO_View.checkElementBy(driver, "text", "Bloqueo válido");
    }

    // Crear un bloqueo solapado con otro bloqueo (debe fallar)
    @Test
    @Order(20)
    public void PR20() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOptionById(driver, "spaces-reservation");

        driver.findElements(By.xpath("//a[contains(@href,'/space/details')]")).get(0).click();
        driver.findElement(By.xpath("//a[contains(@href,'/availability')]")).click();

        driver.findElement(By.xpath("//a[contains(@href,'/blocks/add')]")).click();

        // solapa con 11:00 – 13:00
        String start = base.plusHours(1).plusMinutes(30).toString(); // 10:30
        String end = base.plusHours(3).plusMinutes(50).toString(); // 12:50

        fillBlockForm(start.toString(), end.toString(), "Solape bloqueo");

        PO_View.checkElementByKey(driver, "blocks.add.error.overlap.block", PO_Properties.getSPANISH());
    }

    // Crear un bloqueo solapado con una reserva activa (debe fallar)
    @Test
    @Order(21)
    public void PR21() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOptionById(driver, "spaces-reservation");

        driver.findElements(By.xpath("//a[contains(@href,'/space/details')]")).get(0).click();
        driver.findElement(By.xpath("//a[contains(@href,'/availability')]")).click();
        driver.findElement(By.xpath("//a[contains(@href,'/blocks/add')]")).click();

        // solapa con reserva 09:00 – 11:00
        String start = base.minusMinutes(30).toString(); // 08:30
        String end = base.plusHours(1).toString();     // 10:00

        fillBlockForm(start, end, "Solape reserva");

        PO_View.checkElementByKey(driver, "blocks.add.error.overlap.reservation", PO_Properties.getSPANISH());
    }


    // Cancelar un bloqueo de mantenimiento y verificar que deja de impedir reservas
    @Test
    @Order(22)
    public void PR22() {
        loginAsAdmin();

        driver.findElement(By.id("spacesDropdown")).click();
        PO_NavView.clickOptionById(driver, "spaces-reservation");

        driver.findElements(By.xpath("//a[contains(@href,'/space/details')]")).get(0).click();
        driver.findElement(By.xpath("//a[contains(@href,'/availability')]")).click();
        driver.findElements(By.xpath("//form[contains(@action,'cancel')]/button")).get(0).click();

        PO_View.checkElementBy(driver, "text", "Cancelado");
    }

    // Consultar el listado global de reservas
    @Test
    @Order(23)
    public void PR23() {
        loginAsAdmin();

        PO_View.checkElementByKey(driver, "space.reservations.list", PO_Properties.getSPANISH());
    }

    // Filtrar listado global de reservas por espacio (desplegable)
    @Test
    @Order(24)
    public void PR24() {
        loginAsAdmin();

        driver.navigate().to(URL + "/reservations/admin");

        Select select = new Select(driver.findElement(By.id("spaceId")));
        select.selectByIndex(1); // Espacio en posición 1 del combo

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        PO_View.checkElementByKey(driver, "space.reservations.list", PO_Properties.getSPANISH());
    }


    // Filtrar listado global de reservas por rango de fechas
    @Test
    @Order(25)
    public void PR25() {
        loginAsAdmin();

        // Ir directamente al listado global
        driver.navigate().to(URL + "/reservations/admin");

        // Fechas exactas que pides: 2026-03-22T09:00 → 2026-03-22T10:00
        LocalDateTime fromDate = base.plusDays(1).withHour(9).withMinute(0);   // 2026-03-22T09:00
        LocalDateTime toDate   = base.plusDays(1).withHour(10).withMinute(0);  // 2026-03-22T10:00

        WebElement fromInput = driver.findElement(By.id("from"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                fromInput, fromDate.format(DATE_TIME_FORM_FORMAT)
        );

        WebElement toInput = driver.findElement(By.id("to"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                toInput, toDate.format(DATE_TIME_FORM_FORMAT)
        );

        // Click en Filtrar
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Debe seguir apareciendo la tabla
        PO_View.checkElementByKey(driver, "space.reservations.list", PO_Properties.getSPANISH());
    }

    // PR26 Consultar el listado de espacios disponibles (usuario estándar)
    @Test
    @Order(26)
    public void PR26() {
        loginAsStandardUser();

        driver.navigate().to(URL + "/space");
        String title = PO_HomeView.getP().getString("spaces.title", PO_Properties.getSPANISH());
        List<WebElement> titleResult = PO_View.checkElementBy(driver, "text", title);
        Assertions.assertEquals(title, titleResult.getFirst().getText());

        Assertions.assertFalse(PO_View.checkElementBy(driver, "text", "Cowork 3").isEmpty());
    }

    // PR27 Aplicar un filtro en el listado de espacios
    @Test
    @Order(27)
    public void PR27() {
        loginAsStandardUser();

        driver.navigate().to(URL + "/space");
        WebElement minCapacity = driver.findElement(By.name("minCapacity"));
        minCapacity.clear();
        minCapacity.sendKeys("20");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assertions.assertFalse(driver.findElements(By.xpath("//*[text()='Aula 101']")).isEmpty());
        Assertions.assertTrue(driver.findElements(By.xpath("//*[text()='Cowork 3']")).isEmpty());
    }

    // PR28 Acceder al detalle de un espacio desde el listado
    @Test
    @Order(28)
    public void PR28() {
        loginAsStandardUser();

        driver.navigate().to(URL + "/space");
        driver.findElement(By.xpath("//tr[td[contains(text(),'Cowork 3')]]//a[contains(@href,'/space/details')]"))
                .click();

        WebElement title = driver.findElement(By.tagName("h1"));
        Assertions.assertEquals("Cowork 3", title.getText());
    }

    // PR29 Consultar disponibilidad de un espacio para una franja de días
    @Test
    @Order(29)
    public void PR29() {
        loginAsStandardUser();

        driver.navigate().to(URL + "/space");
        driver.findElement(By.xpath("//tr[td[contains(text(),'Cowork 3')]]//a[contains(@href,'/space/details')]"))
                .click();
        driver.findElement(By.xpath("//a[contains(@href,'/availability')]")).click();

        LocalDateTime fromDate = LocalDateTime.now().minusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime toDate = LocalDateTime.now().plusDays(10).withHour(18).withMinute(0).withSecond(0).withNano(0);

        WebElement fromInput = driver.findElement(By.name("from"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                fromInput, fromDate.format(DATE_TIME_FORM_FORMAT)
        );

        WebElement toInput = driver.findElement(By.name("to"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
                toInput, toDate.format(DATE_TIME_FORM_FORMAT)
        );

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assertions.assertFalse(driver.findElements(By.xpath(
                "//table//tbody/tr")).isEmpty());
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
        String blockedSpaceName = getFirstAvailableSpaceName("Espacio Editado 15", "Sala Azul");
        fillReservationForm(blockedSpaceName, startDateTime, endDateTime, "Reserva en bloqueo");

        String errorText = PO_HomeView.getP().getString("reservations.add.error.blocked", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", errorText);
        Assertions.assertEquals(errorText, result.getFirst().getText());
    }

    // PR34 Consultar el listado de reservas propias
    @Test
    @Order(34)
    public void PR34() {
        loginAsStandardUser();

        driver.navigate().to(URL + "/reservations/list");
        String listTitle = PO_HomeView.getP().getString("reservations.mine.title", PO_Properties.getSPANISH());
        List<WebElement> titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());

        Assertions.assertFalse(PO_View.checkElementBy(driver, "text", "Aula 101").isEmpty());
    }

    // PR35 Filtrar reservas propias por estado CANCELADA
    @Test
    @Order(35)
    public void PR35() {
        loginAsUser("10000002Q", "Us3r@2-PASSW");

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(65).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva a cancelar PR35");

        WebElement cancelButton = driver.findElement(By.xpath(
                "//tr[td[contains(text(),'Cowork 3')] and td[contains(text(),'" + startDateTime + "')]]//button[@type='submit']"));
        cancelButton.click();

        Select statusSelect = new Select(driver.findElement(By.name("status")));
        statusSelect.selectByValue("CANCELLED");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assertions.assertFalse(driver.findElements(By.xpath(
                "//tr[td[contains(text(),'Cowork 3')] and td[contains(text(),'CANCELLED')]]")).isEmpty());
    }

    @Test
    @Order(36)
    public void PR36() {
        loginAsStandardUser();

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(6).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva a cancelar");

        String listTitle = PO_HomeView.getP().getString("reservations.mine.title", PO_Properties.getSPANISH());
        List<WebElement> titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());

        WebElement cancelButton = driver.findElement(By.xpath(
                "//tr[td[contains(text(),'Cowork 3')] and td[contains(text(),'" + startDateTime + "')]]//button[@type='submit']"));
        cancelButton.click();

        List<WebElement> cancelledReservation = PO_View.checkElementBy(driver, "free",
                "//tr[td[contains(text(),'Cowork 3')] and td[contains(text(),'" + startDateTime + "')] and td[contains(text(),'CANCELLED')]]");
        Assertions.assertFalse(cancelledReservation.isEmpty());

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");
        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva tras cancelacion");

        titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());
        List<WebElement> rebookedReservation = PO_View.checkElementBy(driver, "free",
                "//tr[td[contains(text(),'Cowork 3')] and td[contains(text(),'" + startDateTime + "')] and td[contains(text(),'ACTIVE')]]");
        Assertions.assertFalse(rebookedReservation.isEmpty());
    }

    //PR37 Modificar la contraseña con datos válidos.
    @Test
    @Order(37)
    public void PR37() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillLoginForm(driver, "12345678Q", "Sol1!Luz7@Mar");
        String checkText = PO_HomeView.getP().getString("user.changePasswd.title", PO_Properties.getSPANISH());
        PO_NavView.clickOption(driver, "user/changePasswd", "h2", checkText);
        PO_ChangePasswdView.fillForm(driver, "NuevaClave1!", "NuevaClave1!");
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
        loginAsAdmin();
        PO_View.checkElementBy(driver, "text", "Espacios"); // nav.spaces en español

        PO_NavView.changeLanguage(driver, "btnEnglish");
        PO_View.checkElementBy(driver, "text", "Spaces");

        PO_NavView.changeLanguage(driver, "btnFrench");
        PO_View.checkElementBy(driver, "text", "Espaces");

        PO_NavView.changeLanguage(driver, "btnSpanish");
        PO_View.checkElementBy(driver, "text", "Espacios");
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
                "//tbody/tr[td[contains(text(),'Aula 101')] and td[contains(text(),'ACTIVE')]]");
        Assertions.assertFalse(result.isEmpty(), "La reserva ajena no debería cancelarse");
    }

    @Test
    @Order(42)
    public void PR42() {
        loginAsStandardUser();

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(15).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        LocalDate recurrenceEndDate = startDateTime.toLocalDate().plusWeeks(3);
        fillRecurringReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva recurrente semanal",
                "WEEKLY", recurrenceEndDate);

        String listTitle = PO_HomeView.getP().getString("reservations.mine.title", PO_Properties.getSPANISH());
        List<WebElement> titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());
        Assertions.assertFalse(PO_View.checkElementBy(driver, "text", startDateTime.toString()).isEmpty());
        Assertions.assertFalse(PO_View.checkElementBy(driver, "text", startDateTime.plusWeeks(1).toString()).isEmpty());
        Assertions.assertFalse(PO_View.checkElementBy(driver, "text", startDateTime.plusWeeks(2).toString()).isEmpty());
        Assertions.assertFalse(PO_View.checkElementBy(driver, "text", startDateTime.plusWeeks(3).toString()).isEmpty());
    }

    @Test
    @Order(43)
    public void PR43() {
        loginAsStandardUser();

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime conflictingStartDateTime = LocalDateTime.now().plusDays(23).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime conflictingEndDateTime = conflictingStartDateTime.plusHours(2);
        fillReservationForm("Cowork 3", conflictingStartDateTime, conflictingEndDateTime, "Reserva existente");

        String listTitle = PO_HomeView.getP().getString("reservations.mine.title", PO_Properties.getSPANISH());
        List<WebElement> titleResult = PO_View.checkElementBy(driver, "text", listTitle);
        Assertions.assertFalse(titleResult.isEmpty());

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = conflictingStartDateTime.minusWeeks(1);
        LocalDateTime endDateTime = conflictingEndDateTime.minusWeeks(1);
        fillRecurringReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva recurrente invalida",
                "WEEKLY", conflictingStartDateTime.toLocalDate());

        String errorText = PO_HomeView.getP().getString("reservations.add.error.overlap", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", errorText);
        Assertions.assertEquals(errorText, result.getFirst().getText());

        driver.navigate().to(URL + "/reservations/list");
        Assertions.assertTrue(driver.findElements(By.xpath(
                "//tr[td[contains(text(),'" + startDateTime + "')]]")).isEmpty());
    }

    // PR44 Crear reservas hasta alcanzar el límite
    @Test
    @Order(44)
    public void PR44() {
        loginAsUser("10000002Q", "Us3r@2-PASSW");

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(60).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva límite 3");

    }

    // PR45 Intentar crear una reserva adicional superando el límite
    @Test
    @Order(45)
    public void PR45() {
        loginAsUser("10000002Q", "Us3r@2-PASSW");

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(61).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva límite 3");

        driver.findElement(By.id("reservationsDropdown")).click();
        PO_NavView.clickOption(driver, "reservations/add", "id", "spaceId");

        startDateTime = LocalDateTime.now().plusDays(70).withHour(10).withMinute(0).withSecond(0).withNano(0);
        endDateTime = startDateTime.plusHours(2);
        fillReservationForm("Cowork 3", startDateTime, endDateTime, "Reserva excede límite");

        String errorText = PO_HomeView.getP().getString("reservations.add.error.limit", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", errorText);
        Assertions.assertEquals(errorText, result.getFirst().getText());
    }

    // PR46 Exportación de reservas a CSV (Administrador)
    @Test
    @Order(46)
    public void PR46() {
        Path downloadDir = null;
        try {
            downloadDir = Files.createTempDirectory("csv-download-");

            driver.quit();
            driver = getDriverWithDownloadDir(PathFirefox, GeckodriverRutaDiego, downloadDir.toString());

            driver.navigate().to(URL);
            loginAsAdmin();

            driver.navigate().to(URL + "/reservations/admin");
            Select select = new Select(driver.findElement(By.id("spaceId")));
            select.selectByVisibleText("Cowork 3");
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            driver.findElement(By.linkText("Exportar CSV")).click();

            Path csvFile = waitForCsvDownload(downloadDir, 10000);
            Assertions.assertNotNull(csvFile, "CSV file was not downloaded");

            String content = Files.readString(csvFile);
            Assertions.assertTrue(content.contains("User,Space,Start,End,Status"));
            Assertions.assertTrue(content.contains("Cowork 3"));
        } catch (Exception e) {
            Assertions.fail(e);
        } finally {
            if (downloadDir != null) {
                try (Stream<Path> walk = Files.walk(downloadDir)) {
                    walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                        }
                    });
                } catch (Exception ignored) {
                }
            }
        }
    }

}
