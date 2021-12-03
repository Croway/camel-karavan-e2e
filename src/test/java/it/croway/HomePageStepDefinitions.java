package it.croway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebElement;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class HomePageStepDefinitions {

	@Given("Browser is opened on {string}")
	public void browser_is_opened(String url) {
		CucumberContainers.driver.get(url);
	}

	@Given("wait page load")
	public void wait_page_load() {
		waitPageLoad();
	}

	@Then("the page title {string} can be seen")
	public void the_page_title_can_be_seen(String title) {
		WebElement titleElement = CucumberContainers.driver.findElementByXPath("//*[@id=\"root\"]/div/header/div[2]/div/div[1]/div/div[1]/div/h1");

		Assertions.assertThat(titleElement.getText().trim()).isEqualTo(title);
	}

	@Given("click on xpath {string}")
	public void click_on_xpath(String string) {
		WebElement createButton = CucumberContainers.driver.findElementByXPath("//*[@id=\"toolbar-group-types\"]/div[1]/div[1]/div[3]/button");

		createButton.click();
	}

	@Given("write {string} in xpath {string}")
	public void write_in_xpath(String text, String xpath) {
		CucumberContainers.driver.findElementByXPath("//*[@id=\"title\"]").sendKeys("test");
	}

	private void waitPageLoad() {
		int maxAttempts = 10;
		int attempt = 0;
		while (attempt < maxAttempts) {
			WebElement version = CucumberContainers.driver.findElementByXPath("//*[@id=\"root\"]/div/header/div[2]/div/div[1]/div/div[2]/div/h5");

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
