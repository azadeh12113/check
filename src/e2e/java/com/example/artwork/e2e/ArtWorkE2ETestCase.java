package com.example.artwork.e2e;

import org.junit.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArtWorkE2ETestCase { 

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ArtWorkE2ETestCase.class);

  private static int port =
      Integer.parseInt(System.getProperty("server.port", "8080"));

  private static String baseUrl = "http://localhost:" + port;

  private WebDriver driver;

  @BeforeClass
  public static void setupClass() {
    WebDriverManager.chromedriver().setup();
  }

  @Before
  public void setup() throws Exception {
    
    port = Integer.parseInt(System.getProperty("server.port", "8080"));
    baseUrl = "http://localhost:" + port;

    
    
    cleanupAllArtists();

    driver = new ChromeDriver();
    LOGGER.info("Starting test with baseUrl={}", baseUrl);
  }

  @After
  public void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void testHome_NavigationButtonsWork() {
    WebDriverWait wait = new WebDriverWait(driver, 10);

    
    driver.get(baseUrl + "/");
    driver.findElement(By.cssSelector("form[action='/artists/new'] button[type='submit']")).click();
    wait.until(ExpectedConditions.urlContains("/artists/new"));
    assertThat(driver.getCurrentUrl()).contains("/artists/new");
    LOGGER.info("Navigated to /artists/new successfully");

    
    driver.get(baseUrl + "/");
    driver.findElement(By.cssSelector("form[action='/artists'] button[type='submit']")).click();
    wait.until(ExpectedConditions.urlContains("/artists"));
    assertThat(driver.getCurrentUrl()).contains("/artists");
    LOGGER.info("Navigated to /artists successfully");
  }

  @Test
  public void testCreateNewArtist_DisplayedInList() {
    WebDriverWait wait = new WebDriverWait(driver, 10);


    driver.get(baseUrl + "/artists/new");
    driver.findElement(By.name("name")).sendKeys("Mona");
    driver.findElement(By.name("artName")).sendKeys("Painting");
    driver.findElement(By.name("score"))
          .sendKeys(Keys.chord(Keys.CONTROL, "a") + "2");

    driver.findElement(By.name("btn_submit")).click();

    wait.until(ExpectedConditions.urlContains("/artists"));
    WebElement table = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.cssSelector("table"))
    );
    assertThat(table.getText()).contains("Mona", "Painting", "2");
    LOGGER.info("Artist created via UI and visible in the list");
  }

  @Test
  public void testEditArtistScore_UpdatesRow() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, 10);

  
    String id = postArtistJson("Mina", "Photo", 1);
    LOGGER.info("Seeded artist via REST with id={}", id);

    driver.get(baseUrl + "/artists");
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));

    driver.findElement(By.xpath("//a[contains(@href,'artists/edit/" + id + "')]")).click();

    WebElement scoreField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("score")));
    scoreField.clear();
    scoreField.sendKeys("3");
    
    driver.findElement(By.name("btn_submit")).click();

    wait.until(ExpectedConditions.urlContains("/artists"));
    WebElement table = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.cssSelector("table"))
    );
    assertThat(table.getText()).contains("Mina", "Photo", "3");
    LOGGER.info("Edited artist score via UI and verified on the list page");
  }

  @Test
  public void testDeleteArtist_RemovesRowFromList() throws Exception {
      WebDriverWait wait = new WebDriverWait(driver, 10);

      String id = postArtistJson("Delete", "Painting", 2);

      driver.get(baseUrl + "/artists");

      WebElement tableBeforeDelete = wait.until(
          ExpectedConditions.presenceOfElementLocated(By.id("artist_table"))
      );
      assertThat(tableBeforeDelete.getText()).contains("Delete", "Painting", "2");

      driver.findElement(
          By.xpath("//a[contains(@href,'artists/delete/" + id + "')]")
      ).click();

      wait.until(ExpectedConditions.urlContains("/artists"));
      WebElement tableAfterDelete = wait.until(
          ExpectedConditions.presenceOfElementLocated(By.id("artist_table"))
      );
      assertThat(tableAfterDelete.getText()).doesNotContain("Delete", "Painting");
  }
  @Test
public void testHighestScoreArtists_ShowsOnlyTopScorers() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, 10);

    postArtistJson("Top1", "Oil", 3);
    postArtistJson("Top2", "Watercolor", 3);
    postArtistJson("Low", "Sketch", 2);

    driver.get(baseUrl + "/artists/highestScore");

    WebElement table = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.id("highest_Score_table"))
    );

    String tableText = table.getText();

    assertThat(tableText)
    .contains("Top1", "Oil", "3")
    .contains("Top2", "Watercolor", "3")
    .doesNotContain("Low", "Sketch");
}
  private String postArtistJson(String name, String artName, int score) throws Exception {
    RestTemplate rest = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper(); 

    Map<String, Object> body = new HashMap<>();
    body.put("name", name);
    body.put("artName", artName);
    body.put("score", score);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    String json =
        rest.postForEntity(baseUrl + "/api/artists/new", entity, String.class)
            .getBody();

    return mapper.readTree(json).get("id").asText();
  }

  @SuppressWarnings("unchecked")
  private void cleanupAllArtists() throws Exception {
    RestTemplate rest = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();

    String json = rest.getForObject(baseUrl + "/api/artists", String.class);
    if (json == null || json.isBlank()) {
      return;
    }

    List<Map<String, Object>> artists = mapper.readValue(json, List.class);
    for (Map<String, Object> a : artists) {
      Object id = a.get("id");
      if (id != null) {
        try {
          rest.delete(baseUrl + "/api/artists/" + id.toString());
        } catch (Exception ex) {
          
          LOGGER.warn("Could not delete artist id={} during cleanup", id, ex);
        }
      }
    }
  }
}