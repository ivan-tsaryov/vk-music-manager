package beta;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * <p> Методы, работающие с VK API </p>
 * @author Иван
 * @methods Получение списка аудиозаписей, получение списка друзей, получение ФИО человека с VK, получения аватара человека с VK, получение количества песен человека с VK
 * @value audiolist Список аудиозаписей с ВК
 * <br> friendslist Список друзей с ВК
 * <br> JSONfriens Массив response запроса друзей ВК
 * <br> JSONaudio Массив response запроса аудиозаписей ВК
 */
public class VK_API_Methods {
	public static ArrayList<String> audiolist;
	public static ArrayList<String> friendslist;
	public static JSONArray JSONfriends;
	public static JSONArray JSONaudio;	

	/**
	 * Получает список аудиозаписей с ВК
	 * @param uid Параметр, хранящий в себе айди человека, у кого получаем аудиозаписи
	 * @return Список аудиозаписей (Исполнитель - Название)
	 * @throws Exception
	 */
	public static ArrayList<String> audioGet(String uid) throws Exception { // Получение списка аудиозаписей
		audiolist = new ArrayList<String>();
		String URL = "https://api.vk.com/method/audio.get?oid="+ uid + "&need_user=0&count=0&offset=0&access_token="+Authorization.ACCESS_TOKEN;

		JSONParser parser = new JSONParser();

		JSONObject jsonResponse = (JSONObject) parser.parse(NetworkMethods.getUrlSource(URL));
		JSONaudio = (JSONArray) jsonResponse.get("response");

		for (int i = 0; i < JSONaudio.size(); i++) {
			JSONObject mp3 = (JSONObject) JSONaudio.get(i);

			String artist = mp3.get("artist").toString().replaceAll("amp;", "");
			String title = mp3.get("title").toString().replaceAll("amp;", "");
			String fullname = artist + " - " +  title;
			audiolist.add(fullname);
		}		
		return audiolist;
	}
	
	/**
	 * Получает список друзкй с ВК 
	 * @param uid Параметр, хранящий в себе айди человека, у кого получаем друзей
	 * @return Список друзей (Имя Фамилия)
	 * @throws Exception
	 */
	public static ArrayList<String> friendsGet(String uid) throws Exception { // Получение списка друзей
		friendslist = new ArrayList<String>();
		String URL = "https://api.vk.com/method/friends.get?uid="+ uid + "&fields=uid,first_name,last_name&offset=0";

		JSONParser parser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) parser.parse(NetworkMethods.getUrlSource(URL));
		JSONfriends = (JSONArray) jsonResponse.get("response");

		for (int i = 0; i < JSONfriends.size(); i++) {
			JSONObject friend = (JSONObject) JSONfriends.get(i);

			String firstname = friend.get("first_name").toString();
			String lastname = friend.get("last_name").toString();
			String fullname = firstname + " " +  lastname;
			friendslist.add(fullname);	
		}	

		return friendslist;
	}

	/**
	 * Получение  ФИО человека с ВК в соответствии с падежом
	 * @param uid ID человека, у которого получаем ФИО
	 * @param namecase Падеж для склонения имени и фамилии пользователя. Возможные значения: именительный – nom, родительный – gen, дательный – dat, винительный – acc, творительный – ins, предложный – abl. По умолчанию nom. 
	 * @return ФИО в соответствии с падежом
	 * @throws Exception
	 */
	public static String getNameById(String uid, String namecase) throws Exception { // Получение ФИО в VK в соответствии с падежом
		String URL = "https://api.vk.com/method/users.get?user_ids=" + uid + "&fields=photo_50,city,verified,home_town" + "&name_case=" + namecase;
		JSONParser parser = new JSONParser();
		JSONObject response = (JSONObject) parser.parse(NetworkMethods.getUrlSource(URL));
		JSONArray arr = (JSONArray) response.get("response");
		JSONObject human = (JSONObject) arr.get(0);
		String s = human.get("first_name").toString() + " " +  human.get("last_name").toString();
		return s;	
	}

	/**
	 * Получение аватара человека с ВК
	 * @param uid ID человека, у которого получаем аватару
	 * @return Ссылка на квадратную фотографию пользователя, имеющую ширину 50 пикселей. В случае отсутствия у пользователя фотографии возвращается стандартный аватар ВК
	 * @throws Exception
	 */
	public static String getAvatarById(String uid) throws Exception { // Получения аватара VK
		String URL = "https://api.vk.com/method/users.get?user_ids=" + uid + "&fields=photo_50,city,verified,home_town&name_case=nom";
		JSONParser parser = new JSONParser();
		JSONObject response = (JSONObject) parser.parse(NetworkMethods.getUrlSource(URL));
		JSONArray arr = (JSONArray) response.get("response");
		JSONObject human = (JSONObject) arr.get(0);
		String s = human.get("photo_50").toString();
		return s;	
	}
	
	/**
	 * Получение количества аудиозаписей в ВК
	 * @param uid ID человека, у кого получаем кол-во аудиозаписей
	 * @return Количество аудиозаписей
	 * @throws Exception
	 */
	public static String AudioCount(String uid) throws Exception { // Получаем количество аудиозаписей в VK
		String URL = "https://api.vk.com/method/audio.getCount?owner_id=" + uid + "&access_token=" + Authorization.ACCESS_TOKEN;
		JSONParser parser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) parser.parse(NetworkMethods.getUrlSource(URL));
		String number = jsonResponse.get("response").toString();
		return number;
	}
	
	/**
	 * Добавляет данные о текущем сеансе в статистику посещаемости приложения в ВК.
	 * @throws IOException
	 */
	public static void trackVisitor() throws IOException {
		URL u = new URL("https://api.vk.com/method/stats.trackVisitor?access_token=" + Authorization.ACCESS_TOKEN);
		u.openConnection().getInputStream();
	}
}
