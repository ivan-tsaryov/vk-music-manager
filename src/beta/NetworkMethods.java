package beta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>Методы, работающие с интернет-соединением.</p>
 * @author Иван
 * @methods Проверка наличия интернет-соединения, получение содержимого веб-страницы, получение размера содержимого веб-страницы
 */
public class NetworkMethods {
	
	
	/**
	 * <p>Проверяет наличие интернет-соединения.</p>
	 * @return true, если интернет есть, и false, если нет
	 */
	public static boolean isConnected() {
		try {
			URL URL = new URL("http://google.com/");
			URLConnection URLConnection = URL.openConnection();
			URLConnection.connect();
		} 
		catch (MalformedURLException e) { 
			System.out.println("Неправильный URL!");
		} 
		catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * <p>Получает содержимое веб-страницы.</p>
	 *
	 * @param url Адрес страницы, содержимое которой нужно получить
	 * @return Содержимое страницы в виде строки.
	 */
	public static String getUrlSource(String url) throws Exception {
		URL u = new URL(url);
		URLConnection urlconn = u.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream(), "UTF-8"));
		String s;
		StringBuilder a = new StringBuilder();
		while ((s = in.readLine()) != null)
			a.append(s);
		in.close();
		return a.toString();
	}

	/**
	 * <p>Получение содержимого веб-страницы.</p>
	 * @param url Адрес страницы, размер содержимого которой нужно получить
	 * @return Размер содержимого в виде
	 */
	public static int getFileSize(String url) throws IOException { 
		URLConnection urlconn = new URL(url).openConnection();
		return urlconn.getContentLength();
	}
}
