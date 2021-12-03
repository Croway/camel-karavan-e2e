package it.croway.plain;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class KaravanTest extends BaseContainers {

	@Test
	public void testHomePageTitle() {
		driver.get("http://camel-karavan:8080");
		WebElement title = driver.findElementByXPath("//*[@id=\"root\"]/div/header/div[2]/div/div[1]/div/div[1]/div/h1");

		Assertions.assertThat(title.getText().trim()).isEqualTo("Karavan");
	}

	@Test
	public void testKameletsPage() {
		driver.get("http://camel-karavan:8080");

		waitPageLoad();

		WebElement kamelets = driver.findElementByXPath("//*[@id=\"kamelets\"]");

		kamelets.click();

		WebElement avroDeserializeCard = driver.findElementsByClassName("pf-c-card__title").stream()
				.filter(elem -> "Avro Deserialize Action".equals(elem.getText()))
				.findFirst().orElseThrow();

		Actions actions = new Actions(driver);

		actions.moveToElement(avroDeserializeCard).click().perform();

		Assertions.assertThat(driver.findElementByXPath("/html/body/div[3]/div/div/div/div/div/p").getText()).isEqualTo("Deserialize payload to Avro");
	}

	@Test
	public void testCreateIntegration() {
		driver.get("http://camel-karavan:8080");

		waitPageLoad();

		WebElement createButton = driver.findElementByXPath("//*[@id=\"toolbar-group-types\"]/div[1]/div[1]/div[3]/button");

		createButton.click();

		driver.findElementByXPath("//*[@id=\"title\"]").sendKeys("test");
		new Select(driver.findElementByXPath("/html/body/div[3]/div/div/div/div/form/div[2]/div[2]/select")).selectByVisibleText("Plain YAML");

		driver.findElementByXPath("/html/body/div[3]/div/div/div/footer/button[1]").click();

		Assertions.assertThat(
				driver.findElementByXPath("//*[@id=\"root\"]/div/main/section/section/div/div[1]/div/div[1]/h1").getText()).isEqualTo("Designer");

		driver.findElementByXPath("//*[@id=\"root\"]/div/main/section/div/section/div/div[1]/div[2]/button").click();

		driver.findElementByXPath("//*[@id=\"search\"]").sendKeys("timer");
		WebElement timerArticle = driver.findElementByXPath("/html/body/div[3]/div/div/div/div/section/section[1]/div/article");

		Actions actions = new Actions(driver);

		actions.moveToElement(timerArticle).click().perform();

		driver.findElementByXPath("//*[@id=\"yaml\"]/span").click();

		Assertions.assertThat(driver.findElementByXPath("//*[@id=\"code-content\"]/code").getText())
				.isEqualTo("""
						- from:
						    uri: 'kamelet:timer-source'""");
	}

	private void waitPageLoad() {
		int maxAttempts = 10;
		int attempt = 0;
		while (attempt < maxAttempts) {
			WebElement version = driver.findElementByXPath("//*[@id=\"root\"]/div/header/div[2]/div/div[1]/div/div[2]/div/h5");

			if ("v. 0.0.8".equals(version.getText())) {
				return;
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// do nothing
			}

			attempt++;
		}
	}
}
