package beta;

import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.simple.JSONObject;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * <p>Расширение базового класса DefaultListCellRenderer</p>
 * @author Иван
 */
public class AudiolistRenderer extends DefaultListCellRenderer { 
	private static final long serialVersionUID = 1L;
	String username;

	public AudiolistRenderer(String username) {
		this.username = username;
	}
	
	/** 
	 * <p>Переопределение метода getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus);</p>
	 * @author Иван
	 * @value c Переменная, хранящая в себе компонент JList, т.е. элемент списка.
	 * <br> index Переменная, хранящая индекс элемента списка в списке, полученным с VK
	 * <br> mp3 Переменная хранящая в себе JSON обьект, полученного с VK
	 */
	@SuppressWarnings({ "rawtypes", "resource" })
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (!isSelected) {
			if (index%2 != 0) c.setBackground(new Color(226, 230, 233));
			else c.setBackground(new Color(236, 240, 243));
		}
		index = VK_API_Methods.audiolist.indexOf(value);

		JSONObject mp3 = (JSONObject) VK_API_Methods.JSONaudio.get(index);
		String aid = mp3.get("aid").toString();

		try {
			InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);

			for (Row row : sheet) {
				if (aid.equals(row.getCell(0).getStringCellValue()) && username.equals(row.getCell(1).getStringCellValue())) {
					c.setForeground(new Color(134,25,12));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return c;
	}
}