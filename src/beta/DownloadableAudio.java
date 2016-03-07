package beta;

import java.io.File;

/** 
 * <p> Класс загружаемого обьекта </p>
 * @author Иван
 * @value USER_PATH Переменная, содержащая в себе адрес папки пользователя, который скачивает песню
 * <br> artist Переменная, содержащая в себе имя исполнителя
 * <br> title Переменная, содержащая в себе название песни
 * <br> url Переменная, содержащая в себе ссылку на скачивание песни
 * <br> owner_id Переменная, содержащая в себе id владельца песни
 * <br> aid Переменная, содержащая в себе id песни
 */
public class DownloadableAudio {
	File USER_PATH;
	String artist;
	String title;
	String url;
	String downloader;
	String friend;
	String aid;
	
	public DownloadableAudio(File USER_PATH, String artist, String title, String url, String downloader, String friend, String aid) {
		this.USER_PATH = USER_PATH;
		this.artist = artist;
		this.title = title;
		this.url = url;
		this.downloader = downloader;
		this.friend = friend;
		this.aid = aid;
	}
}
