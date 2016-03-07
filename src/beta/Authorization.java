package beta;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * <p>Класс авторизации с полями login и password</p>
 * @author Иван
 * С помощью библиотеки Selenium имитируется браузер и производится авторизация
 */
public class Authorization { // Класс авторизации
	String login;
	String password;
	public static String UID;
	public static String ACCESS_TOKEN;
	
	/**
	 * <p> Конструктор класса </p>
	 * @param login
	 * @param password
	 */
	public Authorization(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	/**
	 * <p>Производит авторизацию с помощью login и password</p>
	 * @return "no_internet" В случае отсутствия интернет-соединения
	 * <br> "incorrect_data" В случае неправильного логина или пароля
	 * <br> "captcha" В случае присутствия на веб-странице captch'и
	 * <br> "success" В случае успешной авторизаци
	 */
	String Authorize() {
		WebDriver driver = new HtmlUnitDriver();

		try {
			driver.get("https://oauth.vk.com/authorize?client_id=4581930&redirect_uri=http://api.vk.com/blank.html&scope=audio&display=wap&response_type=token");
		}
		catch (Exception e) {
			return "no_internet";
		}

		if (driver.getPageSource().contains("Unknown host")) {
			return "no_internet";
		}

		WebElement login = driver.findElement(By.name("email"));
		WebElement password = driver.findElement(By.name("pass"));
		login.sendKeys(this.login);
		password.sendKeys(this.password);
		password.submit();

		if (driver.getPageSource().contains("service_msg service_msg_warning")) {
			return "incorrect_data";
		}
		if (driver.getPageSource().contains("captcha")) {
			return "captcha";
		}

		if (driver.getCurrentUrl().indexOf("access_token") == -1) {
			WebElement allow = driver.findElement(By.className("button"));
			allow.click();
		}

		ACCESS_TOKEN = driver.getCurrentUrl().split("access_token=")[1].split("&")[0];
		UID = driver.getCurrentUrl().split("user_id=")[1];
		
		driver.quit();

		return "success";
	}
}