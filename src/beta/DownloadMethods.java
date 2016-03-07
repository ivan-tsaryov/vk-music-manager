package beta;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * <p>Методы, работающие с загрузкой файла.</p>
 * @author Иван
 * @methods Запись информации о файле
 * <br> Корректировка названия
 */
public class DownloadMethods {
	
	/**
	 * <p> Записывает информацию о скачанной песне в файл aids.txt
	 * 
	 * @param aid Параметр, хранящий id песни
	 * @throws IOException 
	 */
	@SuppressWarnings("resource")
	public static void SongWriter(String aid, String downloader, String friend, String path) throws IOException { // Запись информации о закаченной песне в файл songs.xls
		InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
		HSSFWorkbook wb = new HSSFWorkbook(in);
		
		Sheet sheet = wb.getSheetAt(0);
		Row row = sheet.createRow(sheet.getLastRowNum()+1);
		
		row.createCell(0).setCellValue(aid);
		row.createCell(1).setCellValue(downloader);
		row.createCell(2).setCellValue(friend.split("/")[0]);
		row.createCell(3).setCellValue(path);
		
		FileOutputStream fout = new FileOutputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
		wb.write(fout);
		fout.close();
	}
	
	/**
	 * <p> Корректирует название загружаемого файла (удаляет спец. символы)
	 * 
	 * @param Путь сохраняемого файла
	 * @return Путь сохраняемого файла после корректировки
	 */
	public static String fixWndowsFileName(String pathname) { // Корректировка названия сохраняемого файла
		String[] forbiddenSymbols = new String[] {"<", ">", ":", "\"", "/", "\\", "|", "?", "*"};
		String result = pathname;
		for (String forbiddenSymbol: forbiddenSymbols) {
			result = StringUtils.replace(result, forbiddenSymbol, "");
		}
		return StringEscapeUtils.unescapeXml(result); 
	}
}
