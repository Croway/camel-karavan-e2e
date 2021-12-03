package it.croway.plain;

import org.junit.jupiter.api.BeforeAll;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class BaseContainers {
	private static final Logger LOG = LoggerFactory.getLogger(BaseContainers.class);

	private static Network NETWORK = Network.newNetwork();

	@Container
	protected static GenericContainer<?> karavanContainer = new GenericContainer<>(DockerImageName.parse("ghcr.io/apache/camel-karavan:latest"))
			.withNetwork(NETWORK)
				.withNetworkAliases("camel-karavan")
				.withExposedPorts(8080)
				.waitingFor(new HttpWaitStrategy());

	private static ChromeOptions chromeOptions = new ChromeOptions();

	static {
		chromeOptions.addArguments("--window-size=1920,1080");
	}

	@Container
	protected static BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>()
			.withCapabilities(chromeOptions)
			.withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("target"))
			.withNetwork(NETWORK);

	protected static RemoteWebDriver driver;

	@BeforeAll
	public static void beforeAll() {
		driver = setupDriver(chrome);

		LOG.info(driver.toString());
		LOG.info("Selenium remote URL is: " + chrome.getSeleniumAddress());
		LOG.info("VNC URL is: " + chrome.getVncAddress());
	}

	private static RemoteWebDriver setupDriver(BrowserWebDriverContainer<?> rule) {
		RemoteWebDriver driver = rule.getWebDriver();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		return driver;
	}
}
