package beta;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.apache.commons.io.FileUtils;

import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * <p> Класс реализующий графический интерфейс для выбора имени аудиозаписи и
 * непосредственную загрузку аудио
 * @author Иван
 * @value USER_PATH Переменная содержащая в себе путь директории программы
 * <br> destination Переменная содержащая в себе путь сохранения аудиозаписи
 * <br> artist Переменная содержащая в себе название исполнителя песни
 * <br> title Переменная содержащая в себе название песни
 * <br> url Переменная содержащая в себе ссылка на скачивание
 * <br> owner_id Переменная содержащая в себе id владельца песни
 * <br> aid Переменная содержащая в себе id песни
 */
public class SaveWindow {
	JFrame frame;
	private JButton savebutton;
	private JTextField txtartist;
	private JTextField txttitle;
	private JLabel lblartist;
	private JLabel lbltitle;

	static Thread thread2;
	static Thread thread;

	File USER_PATH;
	static File destination;
	String artist;
	String title;
	String url;
	String downloader;
	String friend;
	String aid;

	MainWindow MainWindow;

	/**
	 * Инициализация компонентов Swing, добавление слушателей к ним
	 * @param MainWindow 
	 * @throws Exception
	 */
	public SaveWindow(File PATH, String Artist, String Title, String url, String downloader, String friend, String aid, MainWindow MainWindow) throws Exception {
		this.USER_PATH = PATH;
		this.artist = Artist;
		this.title = Title;
		this.url = url;
		this.downloader = downloader;
		this.friend = friend;
		this.aid = aid;
		this.MainWindow = MainWindow;
		initialize();
		listeners();
		frame.setVisible(true);
	}

	public SaveWindow(MainWindow MainWindow){
		this.MainWindow = MainWindow;
		if (thread == null) {
			BeginDownload();
		}	
	}


	private void initialize() throws Exception {
		frame = new JFrame("Загрузить песню");
		frame.setBounds(0, 0, 335, 170);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setIconImage(ImageIO.read(getClass().getResource("/images/logo32.png")));
		frame.setLocationRelativeTo(null);

		savebutton = new JButton("Загрузить");
		savebutton.setBounds(98, 90, 120, 23);
		frame.getContentPane().add(savebutton);		

		/**
		 * Поле для изменения исполнителя песни
		 */
		txtartist = new JTextField();
		txtartist.setText(artist);
		txtartist.setBounds(95, 24, 214, 20);
		frame.getContentPane().add(txtartist);
		txtartist.setColumns(10);

		/**
		 * Поле для изменения названия песни
		 */
		txttitle = new JTextField();
		txttitle.setText(title);
		txttitle.setBounds(95, 57, 215, 20);
		frame.getContentPane().add(txttitle);
		txttitle.setColumns(10);

		lblartist= new JLabel("Исполнитель");
		lblartist.setBounds(10, 27, 95, 14);
		frame.getContentPane().add(lblartist);

		lbltitle = new JLabel("Название");
		lbltitle.setBounds(10, 60, 60, 14);
		frame.getContentPane().add(lbltitle);
	}

	private void listeners() {
		/**
		 * Обработчик ввода в TextField, обеспечивающий ограниченное возможное количество вводимых символов
		 * чтобы избежать ошибок при сохранении файла
		 */
		DocumentListener d = new DocumentListener() {
			public void insertUpdate(DocumentEvent arg0) {
				if ((txtartist.getText().length() + txttitle.getText().length()) >= 220) {
					savebutton.setEnabled(false);
					frame.revalidate();
				}	
			}

			public void removeUpdate(DocumentEvent e) {
				if ((txtartist.getText().length() + txttitle.getText().length()) < 220) {
					savebutton.setEnabled(true);
					frame.revalidate();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {}
		};

		txtartist.getDocument().addDocumentListener(d);
		txttitle.getDocument().addDocumentListener(d);

		/**
		 * Обработчик кнопки закрытия приложения
		 */
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				Object[] options = { "Да", "Нет" };
				int n = JOptionPane.showOptionDialog(event.getWindow(),
						"Вы действительно не хотите загружать?", "Подтверждение",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null,  options, options[0]);
				if (n == JOptionPane.YES_OPTION) event.getWindow().dispose();
			}

		});

		/**
		 * Обработчик кнопки сохранения аудио, которая добавляет очередной загружаемый файл в очередь
		 */
		savebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DownloadableAudio d = new DownloadableAudio(USER_PATH, txtartist.getText(), txttitle.getText(), url, downloader, friend, aid);
				MainWindow.queue.add(d);

				frame.dispose();

				if (MainWindow.queue.size() == 1) {
					BeginDownload();
				}	
			}

		});	
	}

	/**
	 * Функция старта загрузки (непосредственной загрузки и проверки текущего процесса загрузки)
	 */

	/**
	 * Отображение текущего статуса загрузки в очереди
	 * @value a Вес файла, который непосредственно начал загружаться (в байтах)
	 * <br> b Реальный вес загружаемого файла (в байтах)
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void checkProcess() throws IOException, InterruptedException {
		DownloadableAudio d = MainWindow.queue.get(0);

		destination = new File(d.USER_PATH + "/" + d.downloader + "/" + d.friend +  d.artist + " - " + d.title + ".mp3");
		long a = destination.length();
		long b = NetworkMethods.getFileSize(d.url);

		while (MainWindow.queue.indexOf(d) != -1) {
			String song = d.artist + " - "+ d.title;
			long percent = a*100/b;

			MainWindow.downloadProcess.setText("Загружается песня: " + (song.length() > 65? song.substring(0, 65) + "..." : song) + " " + Long.toString(percent) + "%  " + "Осталось скачать песен: " + MainWindow.queue.size());
			a = destination.length();
		}
	}

	/**
	 * Непосредственная загрузка песни
	 * @throws IOException 
	 */
	private void download() throws IOException {
		final DownloadableAudio d = MainWindow.queue.get(0);
		String path = d.USER_PATH + "/" + d.downloader + "/" + d.friend + DownloadMethods.fixWndowsFileName(d.artist + " - " + d.title) + ".mp3";
		if (path.length() >= 220) {
			path = path.substring(0, 216) + ".mp3";
		}	

		destination = new File(path); 

		try {
			FileUtils.copyURLToFile(new URL(d.url), destination);
			MainWindow.queue.remove(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DownloadMethods.SongWriter(d.aid, d.downloader, d.friend, destination.getAbsolutePath());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow.audiolist.setCellRenderer(new AudiolistRenderer(d.downloader));
					MainWindow.friendlist.setCellRenderer(new FriendlistRenderer(d.downloader));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		String offlinecount = "Доступно офлайн: " + AppFilesWorkMethods.OfflineCount(d.downloader);
		MainWindow.offlineAudioCount.setText(offlinecount);

		if (MainWindow.queue.size() > 0) {
			BeginDownload();
		}
		else {
			MainWindow.offlineAudioCount.setText(offlinecount);
			MainWindow.downloadProcess.setText("Загрузка завершена!");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			MainWindow.cancelButton.setVisible(false);
			MainWindow.downloadProcess.setText("");
		}
	}

	public void BeginDownload() {
		thread2 = new Thread() {
			public void run() {
				try {				
					checkProcess();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				this.interrupt();
			}
		};

		thread = new Thread() {
			public void run() {
				try {
					download();
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.interrupt();
			}
		};
		while (thread2.isAlive()) {}
		thread.start();	
		thread2.start();
	}

}