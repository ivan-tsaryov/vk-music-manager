package beta;

import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * <p>Расширение базового класса DefaultListCellRenderer
 * @author Иван
 */
public class FriendlistRenderer extends DefaultListCellRenderer { // Пользовательский JList Cells рендерер
	private static final long serialVersionUID = 1L;
	String username;

	public FriendlistRenderer(String username) {
		this.username = username;
	}

	/**
	 * <p> Переопределение метода getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	 * @value file Переменная, хранящая в себе путь к папке, которая проверяется на наличие содержимого
	 */
	@SuppressWarnings({ "rawtypes", "resource" })
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (!isSelected)
			if (index%2 != 0) c.setBackground(new Color(226, 230, 233));
			else c.setBackground(new Color(236, 240, 243));
		
		try {
			InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
			HSSFWorkbook wb = new HSSFWorkbook(in);
			
			Sheet sheet = wb.getSheetAt(0);

			for (Row row : sheet) 
				if ((row.getCell(1).getStringCellValue() + row.getCell(2).getStringCellValue()).equals(username+value.toString())) {
					c.setForeground(new Color(134,25,12));
					break;
				}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return c;
	}
}