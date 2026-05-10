package com.eduplazas.backend.selenium;

import com.eduplazas.backend.model.EstadoSolicitudEnum;
import com.eduplazas.backend.model.Solicitud;
import com.eduplazas.backend.repository.SolicitudRepository;
import com.eduplazas.backend.service.AsignacionService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de sistema (Casos 6–10) — Selenium WebDriver + Spring Boot.
 *
 * Precondiciones:
 *   - El frontend React debe estar en ejecución: cd frontend && npm run dev (puerto 5173).
 *   - El puerto 8080 debe estar libre; este test arranca su propio backend con H2 en memoria.
 *   - ChromeDriver compatible con el Chrome instalado (gestionado por WebDriverManager).
 *
 * Credenciales del DataLoader:
 *   - Todos los usuarios tienen contraseña "1234".
 *   - estudiante1@eduplazas.es  → ENTREGADA (primera solicitud, id=7)
 *   - estudiante396@eduplazas.es → BORRADOR  (prioridades: Aeroespacial, Informática, Telecomunicación)
 *   - luis@upm.es               → RepresentanteUniversidad UPM
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EduPlazasSeleniumTest {

    private static final String FRONTEND = "http://localhost:5173";

    /** Evita conexiones SMTP reales durante los tests. */
    @MockitoBean
    JavaMailSender mailSender;

    @Autowired
    AsignacionService asignacionService;

    @Autowired
    SolicitudRepository solicitudRepository;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setUpAll() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage",
                          "--window-size=1280,800");
        // Deshabilita el gestor de contraseñas de Chrome para evitar que autocomplete
        // los campos de login con credenciales de una prueba anterior y corrompa el valor.
        opts.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false
        ));
        driver = new ChromeDriver(opts);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    void tearDownAll() {
        if (driver != null) driver.quit();
    }

    private JavascriptExecutor js() {
        return (JavascriptExecutor) driver;
    }

    /**
     * Limpia localStorage y deja el navegador en la Home con almacenamiento vacío.
     * Se limpia primero en la página actual (mismo origen) y luego tras la navegación,
     * garantizando que React no detecta cambios a mitad de render.
     * La segunda navegación a FRONTEND asegura que la Home monta estable con
     * localStorage ya vacío (necesario para que el botón de Case 6 funcione).
     */
    private void clearStorage() {
        try {
            js().executeScript("localStorage.clear()");
        } catch (Exception ignored) {
            // Primera ejecución: no hay página cargada aún
        }
        driver.get(FRONTEND);
        js().executeScript("localStorage.clear()");
        driver.get(FRONTEND);
    }

    /**
     * Rellena los inputs de email y contraseña usando el setter nativo del prototipo
     * de HTMLInputElement para forzar la actualización del estado de React.
     * Con sendKeys, los eventos de teclado pueden ser procesados en lote por React 18
     * (concurrent rendering), haciendo que el estado quede vacío al enviar el formulario.
     * El nativeInputValueSetter + dispatchEvent('input') actualiza el valor directamente
     * y dispara el onChange de React de forma síncrona, independientemente del ciclo de render.
     */
    private void loginAs(String email, String password) {
        String setReactValue =
            "var s=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
            "s.call(arguments[0],arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));";

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email']")));
        js().executeScript(setReactValue, emailInput, email);

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='password']")));
        js().executeScript(setReactValue, passwordInput, password);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Log in']"))).click();
    }

    // ── Caso 6 ──────────────────────────────────────────────────────────────────

    /**
     * Flujo completo de login de estudiante desde la Home:
     * Home → botón "ESTUDIANTES" → formulario Login → /estudiante/inicio.
     * Verifica token en localStorage, rol "ESTUDIANTE" y email visible en sidebar.
     */
    @Test
    @Order(6)
    void loginEstudiante_conCredencialesValidas_redirigidoAInicioConTokenEnLocalStorage() {
        clearStorage();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='ESTUDIANTES']"))).click();
        wait.until(ExpectedConditions.urlContains("/estudiantes/login"));

        loginAs("estudiante1@eduplazas.es", "1234");
        wait.until(ExpectedConditions.urlContains("/estudiante/inicio"));

        String token       = (String) js().executeScript("return localStorage.getItem('token')");
        String usuarioJson = (String) js().executeScript("return localStorage.getItem('usuario')");

        assertThat(token).isNotNull().isNotBlank();
        assertThat(usuarioJson)
                .contains("\"rol\":\"ESTUDIANTE\"")
                .contains("estudiante1@eduplazas.es");
        assertThat(driver.getPageSource()).contains("estudiante1@eduplazas.es");
    }

    // ── Caso 7 ──────────────────────────────────────────────────────────────────

    /**
     * Acceso directo a /estudiante/inicio sin sesión activa.
     * El frontend no tiene ProtectedRoute: la URL permanece en /estudiante/inicio
     * pero la página no muestra datos privados (ningún "@eduplazas.es").
     */
    @Test
    @Order(7)
    void accesoDirectoSinSesion_aInicioEstudiante_noMuestraDatosPrivados() {
        clearStorage();
        driver.get(FRONTEND + "/estudiante/inicio");
        wait.until(ExpectedConditions.urlToBe(FRONTEND + "/estudiante/inicio"));

        assertThat(driver.getPageSource()).doesNotContain("@eduplazas.es");
    }

    // ── Caso 8 ──────────────────────────────────────────────────────────────────

    /**
     * El representante de UPM (luis@upm.es) publica la oferta "Ingeniería de Sistemas"
     * (75 plazas, rama Ingeniería y Arquitectura, criterio Matemáticas II × 0.2)
     * y verifica que aparece en /universidad/mis-ofertas con los datos correctos.
     */
    @Test
    @Order(8)
    void publicarOferta_porRepresentanteUniversidad_ofertaAparaceEnMisOfertas() {
        clearStorage();
        driver.get(FRONTEND + "/universidades/login");
        loginAs("luis@upm.es", "1234");
        wait.until(ExpectedConditions.urlContains("/universidad/inicio"));

        driver.get(FRONTEND + "/universidad/publicar-oferta");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Ej: Ingeniería Informática']")));

        // Nombre del grado
        driver.findElement(By.xpath("//input[@placeholder='Ej: Ingeniería Informática']"))
              .sendKeys("Ingeniería de Sistemas");

        // Rama de conocimiento
        WebElement ramaSelect = driver.findElement(
                By.xpath("//div[./label[normalize-space()='Rama de conocimiento']]/select"));
        new Select(ramaSelect).selectByVisibleText("Ingeniería y Arquitectura");

        // Número de plazas
        driver.findElement(
                By.xpath("//div[./label[normalize-space()='Número de plazas']]/input"))
              .sendKeys("75");

        // Criterio de admisión: asignatura Matemáticas II, peso 0.2
        WebElement criterioRow = driver.findElement(By.cssSelector("[class*='criterioRow']"));
        List<WebElement> criterioSelects = criterioRow.findElements(By.tagName("select"));
        new Select(criterioSelects.get(0)).selectByVisibleText("Matemáticas II");
        new Select(criterioSelects.get(1)).selectByValue("0.2");

        // Publicar y esperar confirmación
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Publicar oferta']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Oferta publicada correctamente')]")));

        // Verificar en Mis Ofertas
        driver.get(FRONTEND + "/universidad/mis-ofertas");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Ingeniería de Sistemas')]")));

        String pageSource = driver.getPageSource();
        assertThat(pageSource)
                .contains("Ingeniería de Sistemas")
                .contains("75")
                .contains("2025-2026");
    }

    // ── Caso 9 ──────────────────────────────────────────────────────────────────

    /**
     * El estudiante con borrador (estudiante396@eduplazas.es) abre /estudiante/borradores,
     * comprueba que las 3 preferencias están precargadas desde el DataLoader
     * (Aeroespacial · Informática · Telecomunicación), envía la solicitud
     * y verifica que /estudiante/ver-solicitud muestra estado ENTREGADA.
     */
    @Test
    @Order(9)
    void enviarBorrador_conPreferenciasPrecargadas_estadoCambiaAEntregada() {
        clearStorage();
        driver.get(FRONTEND + "/estudiantes/login");
        loginAs("estudiante396@eduplazas.es", "1234");
        wait.until(ExpectedConditions.urlContains("/estudiante/inicio"));

        driver.get(FRONTEND + "/estudiante/borradores");
        // Las preferencias del borrador se cargan en inputs de texto (BuscadorOferta).
        // Los valores de React son propiedades DOM, no atributos HTML ni text() de XPath.
        // Se espera con JavaScript comprobando el property .value de cada input.
        wait.until(d -> Boolean.TRUE.equals(((JavascriptExecutor) d).executeScript(
                "return Array.from(document.querySelectorAll('input[type=text]'))" +
                ".some(el => (el.value||'').includes('Ingeniería Aeroespacial'))")));

        String allInputValues = (String) js().executeScript(
                "return Array.from(document.querySelectorAll('input[type=text]'))" +
                ".map(el => el.value).join('|')");
        assertThat(allInputValues)
                .contains("Ingeniería Aeroespacial")
                .contains("Ingeniería Informática")
                .contains("Ingeniería de Telecomunicación");

        // Enviar solicitud
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Enviar']"))).click();

        // El formulario muestra "Solicitud enviada correctamente" y navega a inicio tras 1 s
        wait.until(ExpectedConditions.urlContains("/estudiante/inicio"));

        // Verificar que la solicitud quedó ENTREGADA
        driver.get(FRONTEND + "/estudiante/ver-solicitud");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'ENTREGADA')]")));

        assertThat(driver.getPageSource()).contains("ENTREGADA");
    }

    // ── Caso 10 ─────────────────────────────────────────────────────────────────

    /**
     * Tras ejecutar el algoritmo de asignación directamente sobre la convocatoria 1,
     * un estudiante asignado navega a /estudiante/resultados y comprueba:
     *   - estado "ASIGNADA" visible
     *   - nota final mostrada con exactamente 3 decimales (toFixed(3))
     */
    @Test
    @Order(10)
    void verResultados_estudianteAsignado_muestraEstadoYNotaFinalConTresDecimales() {
        // Ejecutar el algoritmo de asignación para la convocatoria id=1
        asignacionService.procesarAsignaciones(1L);

        // Obtener el primer estudiante con solicitud en estado ASIGNADA
        Solicitud asignada = solicitudRepository.findAll().stream()
                .filter(s -> s.getEstado() == EstadoSolicitudEnum.ASIGNADA)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No hay solicitudes ASIGNADAS tras ejecutar procesarAsignaciones(1L)"));

        String emailAsignado = asignada.getEstudiante().getEmail();

        clearStorage();
        driver.get(FRONTEND + "/estudiantes/login");
        loginAs(emailAsignado, "1234");
        wait.until(ExpectedConditions.urlContains("/estudiante/inicio"));

        driver.get(FRONTEND + "/estudiante/resultados");

        // Esperar a que el resultado de asignación esté visible (Estado: / Grado: / Nota final:)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Estado:')]")));

        String pageSource = driver.getPageSource();
        assertThat(pageSource).contains("ASIGNADA");
        // notaFinal se renderiza con toFixed(3): un número seguido de exactamente 3 decimales
        assertThat(pageSource).matches("(?s).*\\d+\\.\\d{3}.*");
    }
}
