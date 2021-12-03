package it.croway;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.lifecycle.TestDescription;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public class CucumberContainers {
	private static final Logger LOG = LoggerFactory.getLogger(CucumberContainers.class);
	protected static RemoteWebDriver driver;
	private static Network NETWORK = Network.newNetwork();
	protected static GenericContainer<?> karavanContainer = new GenericContainer<>(DockerImageName.parse("ghcr.io/apache/camel-karavan:latest"))
			.withNetwork(NETWORK)
			.withNetworkAliases("camel-karavan")
			.withExposedPorts(8080)
			.waitingFor(new HttpWaitStrategy());
	private static ChromeOptions chromeOptions = new ChromeOptions();
	protected static BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>()
			.withCapabilities(chromeOptions)
			.withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("target"))
			.withNetwork(NETWORK);

	@BeforeAll
	public static void beforeAll() {
		karavanContainer.start();
		chrome.start();

		driver = setupDriver(chrome);

		LOG.info(driver.toString());
		LOG.info("Selenium remote URL is: " + chrome.getSeleniumAddress());
		LOG.info("VNC URL is: " + chrome.getVncAddress());
	}

	@AfterAll
	public static void afterAll() {
		chrome.afterTest(new TestDescription() {
			@Override
			public String getTestId() {
				return "camel-karavan";
			}

			@Override
			public String getFilesystemFriendlyName() {
				return "camel-karavan";
			}
		}, Optional.empty());

		driver.close();
		chrome.stop();
		karavanContainer.stop();
	}

	private static RemoteWebDriver setupDriver(BrowserWebDriverContainer<?> rule) {
		RemoteWebDriver driver = rule.getWebDriver();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		return driver;
	}

	static {
		chromeOptions.addArguments("--window-size=1920,1080");
	}
}
