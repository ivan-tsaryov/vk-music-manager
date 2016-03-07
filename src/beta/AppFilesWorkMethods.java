package beta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * <p>Методы, необходимые для осуществления воспроизведения песен.</p>
 * @author Иван
 * 
 * @methods Подсчет доступных аудиозаписей офлайн
 * <br> Проверка доступности песни оффлайн
 */
public class AppFilesWorkMethods {
	static FilenameFilter directoryFilter = new FilenameFilter() {
		public boolean accept(File file, String name) {
			File d = new File(file.getAbsolutePath() + "/" + name);
			return d.isDirectory();
		}
	};
	
	/**
	 * <p> Считает количество аудиозаписей доступных офлайн по количеству строчек в aids.txt
	 * @return Количество аудиозаписей, доступных оффлайн
	 * @throws IOException 
	 */
	@SuppressWarnings("resource")
	public static String OfflineCount(String owner) throws IOException { // Количество доступных офлайн аудиозаписей
		int count = 0;
		
		InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
		HSSFWorkbook wb = new HSSFWorkbook(in);
		
		Sheet sheet = wb.getSheetAt(0);
		
		for (Row row : sheet) {
			if (row.getCell(1).getStringCellValue().equals(owner)) count++;
		}
		
		return Integer.toString(count);
	}

	/**
	 * <p> Проверяет доступность песни офлайн
	 * @param aid Параметр, содержащий в себе id песни
	 * @return Путь к файлу в случае наличия офлайн, null в случае отсутствия
	 */
	@SuppressWarnings("resource")
	public static String OfflineAccess(String aid) { // Проверка доступности песни офлайн
		try {
			InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);

			Sheet sheet = wb.getSheetAt(0);
			
			for (Row row : sheet) {
				if (aid.equals(row.getCell(0).getStringCellValue()))
					return row.getCell(3).getStringCellValue();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static ArrayList<String> getAudioByName(String owner) throws IOException {
		ArrayList<String> songPaths = new ArrayList<String>();

		InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
		@SuppressWarnings("resource")
		HSSFWorkbook wb = new HSSFWorkbook(in);
		
		Sheet sheet = wb.getSheetAt(0);
		
		for (Row row : sheet) {
			if (row.getCell(1).getStringCellValue().equals(owner) && row.getCell(2).getStringCellValue().equals("")) {
				songPaths.add(row.getCell(3).getStringCellValue());
			}
		}
		return songPaths;
	}

	@SuppressWarnings("resource")
	public static ArrayList<String> getFriendByName(String owner) {
		ArrayList<String> friends = new ArrayList<String>();
		
		try {
			InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);

			Sheet sheet = wb.getSheetAt(0);
			
			for (Row row : sheet) {
				if (row.getCell(1).getStringCellValue().equals(owner) && (!row.getCell(2).getStringCellValue().equals("")) && !friends.contains(row.getCell(2).getStringCellValue())) {
					friends.add(row.getCell(2).getStringCellValue());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return friends;
	}
	
	public static String[] folders(File directory) {
		if (directory.exists()) return directory.list(directoryFilter);
		else return null;
	}
}