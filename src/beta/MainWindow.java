
package beta;

import jaco.mp3.player.MP3Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.simple.JSONObject;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * <p> Класс графического интерфейса основного окна приложения (онлайн режим) <p>
 * @author Иван
 * <br> @value USER_PATH Путь к директории программы
 * <br> currentsong строка содержащая в себе информацию, какая песня воспроизводится в данный момент
 * <br> DownloadProcess метка, отображающая процесс загрузки
 * <br> audiolist список аудиозаписей (Исполнитель - Название)
 * <br> friendlist список друзей (Имя Фамилия)
 * <br> queue очередь загрузки песен
 */
public class MainWindow {
	boolean mode;
	String username;

	private static MainWindow window;
	JFrame frame;

	private JLabel VKname;
	private JLabel VKavatar;
	private JLabel VKaudioCount;
	private JLabel label_1;
	private JLabel label_2;
	private JScrollPane scrollableAL;
	private JScrollPane scrollableFL;
	private JButton myAudioButton;
	private JButton resumeButton;
	private JButton pauseButton; 
	private JButton stopButton;
	private JButton folderButton;
	private JButton downloadButton;
	private JButton downloadAllButton;
	private JButton onlineButton;
	private JTextField ALfilter;
	private JTextField FLfilter;

	private ActionListener onlineResumeButtonListener;
	private ActionListener offlineResumeButtonListener;
	private ActionListener onlineToMyAudioButtonListener;
	private ActionListener offlineToMyAudioButtonListener;
	private MouseAdapter onlineAudiolistListener;
	private MouseAdapter onlineFriendlistListener;
	private MouseAdapter offlineAudiolistListener;
	private MouseAdapter offlineFriendlistListener;

	JButton cancelButton;
	JLabel offlineAudioCount;
	JLabel playingSong;
	JLabel downloadProcess;
	FilteringJList audiolist;
	FilteringJList friendlist;

	ArrayList<DownloadableAudio> queue = new ArrayList<>();
	File USER_PATH;
	MP3Player player;
	String currentsong;
	String offlinelink = "";


	/**
	 * Для офлайн режима
	 */
	ArrayList<String> songPaths = new ArrayList<String>();
	ArrayList<String> songNames = new ArrayList<String>();
	ArrayList<String> friendNames = new ArrayList<String>();

	public static void main(final boolean mode, final String username) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MainWindow(mode, username);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * <p> Инициализация компонентов Swing, добавление слушателей к этим компонентам, осуществление доп. операций <p>
	 * @throws Exception
	 */
	public MainWindow(boolean mode, String username) throws Exception {
		this.mode = mode;
		this.username = username;
		initialize();
		listeners();

		if (mode) setOnlineMode();
		else setOfflineMode();
		player = new MP3Player();	

		if (username.equals("")) folderButton.setVisible(false);
		else folderButton.setVisible(true);
	}

	private void initialize() throws Exception {	
		USER_PATH = new File(System.getProperty("user.home")+"/VK Music Manager/");
		USER_PATH.mkdirs();	 
		File songs = new File(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
		if (!songs.exists()) {
			OutputStream out = new FileOutputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
			HSSFWorkbook wb = new HSSFWorkbook();
			wb.createSheet();
			wb.write(out);

			out.close();
			wb.close();
		}
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

		/**
		 * Фрэйм окна
		 */
		frame = new JFrame("VK Music Manager Beta");
		frame.setResizable(false);
		frame.setIconImage(ImageIO.read(getClass().getResource("/images/logo32.png")));
		frame.setBounds(100, 100, 800, 570);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		/**
		 * Аватар авторизованного пользователя
		 */
		VKavatar = new JLabel();
		VKavatar.setToolTipText("Мой профиль VK");
		VKavatar.setBounds(25, 8, 51, 50);
		frame.getContentPane().add(VKavatar);

		/**
		 * Имя авторизованного ВКонтакте
		 */
		VKname = new JLabel();
		VKname.setBounds(84, 10, 150, 14);
		VKname.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		frame.getContentPane().add(VKname);

		/**
		 * Количество аудиозаписей пользователя ВКонтакте
		 */
		VKaudioCount = new JLabel();
		VKaudioCount.setBounds(84, 27, 150, 14);
		VKaudioCount.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		frame.getContentPane().add(VKaudioCount);

		/**
		 * Количество доступных офлайн песен
		 */
		offlineAudioCount = new JLabel();
		offlineAudioCount.setBounds(84, 44, 150, 14);
		offlineAudioCount.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		offlineAudioCount.setForeground(new Color(255,19,0));
		frame.getContentPane().add(offlineAudioCount);

		label_1 = new JLabel("Список песен:");
		label_1.setBounds(25, 70, 500, 14);
		label_1.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		frame.getContentPane().add(label_1);

		label_2 = new JLabel("Список друзей:");
		label_2.setBounds(570, 70, 100, 14);
		label_2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		frame.getContentPane().add(label_2);


		/**
		 * Список песен с фильтрацией и скроллингом 
		 */
		audiolist = new FilteringJList();
		audiolist.setBounds(27, 90, 500,350);
		audiolist.setFixedCellHeight(20);
		scrollableAL = new JScrollPane(audiolist);
		scrollableAL.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollableAL.setBorder(border);
		scrollableAL.setBounds(25, 115, 500, 330);
		frame.getContentPane().add(scrollableAL, BorderLayout.CENTER);

		downloadAllButton = new JButton("Скачать все аудиозаписи");
		scrollableAL.setColumnHeaderView(downloadAllButton);

		/**
		 * Фильтр списка аудиозаписей
		 */
		ALfilter = new JTextField("Искать");
		ALfilter.setForeground(Color.LIGHT_GRAY);
		ALfilter.setHorizontalAlignment(JTextField.CENTER);
		ALfilter.setBounds(25, 90, 500, 26);
		ALfilter.setBorder(new EmptyBorder(5,5, 5, 5));
		ALfilter.setBorder(border);
		audiolist.installJTextField(ALfilter);
		frame.getContentPane().add(ALfilter, BorderLayout.NORTH);

		/**
		 * Список друзей с фильтрацией и скроллингом
		 */
		friendlist = new FilteringJList();
		friendlist.setBounds(570, 90, 200, 330);
		friendlist.setFixedCellHeight(20);
		friendlist.setCellRenderer(new FriendlistRenderer(username));
		friendlist.setBorder(new EmptyBorder(5,5, 5, 5));
		scrollableFL = new JScrollPane(friendlist);
		scrollableFL.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollableFL.setBorder(border);
		scrollableFL.setBounds(570, 115, 200, 330);
		frame.getContentPane().add(scrollableFL, BorderLayout.CENTER);

		/**
		 * Кнопка возврата к аудио авторизованного пользователя
		 */
		myAudioButton = new JButton("К моим аудиозаписям");
		myAudioButton.setBackground(UIManager.getColor("ToolBar.light"));
		scrollableFL.setColumnHeaderView(myAudioButton);

		/**
		 * Фильтр списка друзей
		 */
		FLfilter = new JTextField("Искать");
		FLfilter.setForeground(Color.LIGHT_GRAY);
		FLfilter.setHorizontalAlignment(JTextField.CENTER);
		FLfilter.setBorder(border);
		FLfilter.setBounds(570, 90, 200, 26);
		friendlist.installJTextField(FLfilter);
		frame.getContentPane().add(FLfilter, BorderLayout.NORTH);

		/**
		 * Кнопка продолжающая воспроизведение
		 */
		resumeButton = new JButton("");
		resumeButton.setToolTipText("Продолжить");
		resumeButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/play2_new.png"))));
		resumeButton.setBounds(340, 18, 32, 31);
		resumeButton.setFocusPainted(false);
		frame.getContentPane().add(resumeButton);

		/**
		 * Кнопка паузы воспроизведения
		 */
		pauseButton = new JButton("");
		pauseButton.setToolTipText("Пауза");
		pauseButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/pause_new.png"))));
		pauseButton.setBounds(380, 18, 32, 31);
		pauseButton.setFocusPainted(false);
		frame.getContentPane().add(pauseButton);

		/**
		 * Кнопка остановки воспроизведения
		 */
		stopButton = new JButton("");
		stopButton.setToolTipText("Остановить");
		stopButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/stop_new.png"))));
		stopButton.setBounds(420, 18, 32, 31);
		stopButton.setFocusPainted(false);
		frame.getContentPane().add(stopButton);

		/**
		 * Кнопка скачивания
		 */
		downloadButton = new JButton("Скачать");
		downloadButton.setToolTipText("Скачать выбранную песню");
		downloadButton.setBounds(234, 23, 91, 25);
		downloadButton.setFocusPainted(false);
		frame.getContentPane().add(downloadButton);

		onlineButton = new JButton("Онлайн");
		onlineButton.setToolTipText("Перейти к онлайн-версии");
		onlineButton.setBounds(234, 23, 91, 25);
		onlineButton.setFocusPainted(false);
		frame.getContentPane().add(onlineButton);

		/**
		 *  Кнопка перехода в папку загрузок
		 */
		folderButton = new JButton("Папка загрузок");
		folderButton.setToolTipText("Открыть папку с песнями");
		folderButton.setBounds(647, 23, 123, 25);
		frame.getContentPane().add(folderButton);

		/**
		 * Информация о текущем воспроизведении музыки
		 */
		playingSong = new JLabel("", SwingConstants.CENTER);
		playingSong.setBounds(25, 460, 746, 20);
		playingSong.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		frame.getContentPane().add(playingSong);
		/**
		 * Информация о процессе загрузки
		 */
		downloadProcess = new JLabel("", SwingConstants.CENTER);
		downloadProcess.setBounds(25, 486, 746, 20);
		downloadProcess.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		frame.getContentPane().add(downloadProcess);

		cancelButton = new JButton("Очистить очередь загрузки");
		cancelButton.setBounds(309, 508, 181, 23);
		cancelButton.setVisible(false);
		frame.getContentPane().add(cancelButton);
	}
	private void listeners() {
		/**
		 * Слушатель выполняющий открытия директории программы
		 */
		folderButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(USER_PATH.getAbsolutePath() + "/" + username));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		VKavatar.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 5) {
					JOptionPane.showMessageDialog(frame,
							"Над программой работали:\nЦарёв Иван, Борисов Александр, Буртаев Андрей, Никулин Сергей, Жиляев Александр, Фидлер Сергей\n"
									+ "В рамках курсового проекта по дисциплине `Программирование на языках высокого уровня` под руководством преподавателя Власенко Олега Федосовича.\nУльяновск, 2014/2015",
									"Разработчики",
									JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		/**
		 * Слушатель выполняющий открытия окна подтверждения при закрытия основного окна
		 */
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				Object[] options = { "Да", "Нет" };
				int n = JOptionPane.showOptionDialog(event.getWindow(),
						"Вы действительно хотите выйти?", "Подтверждение",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null,  options, options[0]);
				if (n == JOptionPane.YES_OPTION) System.exit(0);
			}
		});

		/**
		 * Слушатель, осуществляющий загрузку выбранной песни. После клика по кнопке, если интернет-соединение
		 * отсутствует, высвечивается информация об этом и программа переходит в офлайн режим.
		 * <br> В ином случае, если песня уже не закачивается, открывается окно сохранения.
		 */
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!audiolist.isSelectionEmpty()) {
					int index = 0;
					index = VK_API_Methods.audiolist.indexOf(audiolist.getSelectedValue());
					JSONObject mp3 = (JSONObject) VK_API_Methods.JSONaudio.get(index);
					String artist = mp3.get("artist").toString().replaceAll("amp;", "");
					String title = mp3.get("title").toString().replaceAll("amp;", "");

					if (!NetworkMethods.isConnected()) {
						if (player.getPlayList().size() > 0 && !player.getPlayList().get(0).toString().contains("file")) {
							player.stop();
							playingSong.setText("");
						}
						JOptionPane.showMessageDialog(frame,
								"Интернет-подключение отсутствует. Сейчас программа перейдет в офлайн-режим.",
								"Предупреждение",
								JOptionPane.WARNING_MESSAGE);
						try {
							setOfflineMode();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					else {
						boolean flag = false;
						String url = mp3.get("url").toString();
						String aid = mp3.get("aid").toString();
						for (int i = 0; i < queue.size(); i++) 
							if (queue.get(i).aid.equals(aid)) {
								JOptionPane.showMessageDialog(frame,
										"Эта песня уже добавлена в очередь скачивания.",
										"Предупреждение",
										JOptionPane.WARNING_MESSAGE);
								flag = true;
								break;
							}
						try {
							if (!flag) {
								if (mp3.get("owner_id").toString().equals(Authorization.UID))
									new SaveWindow(USER_PATH, artist, title, url, username, "", aid, window);
								else new SaveWindow(USER_PATH, artist, title, url, username, (VK_API_Methods.getNameById(mp3.get("owner_id").toString(), "nom") + "/"), aid, window);
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}
						audiolist.clearSelection();
					}
				}
				else {
					JOptionPane.showMessageDialog(frame,
							"Сначала выберите аудиозапись.",
							"Внимание",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		downloadAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] options = { "Да", "Нет" };
				int n = JOptionPane.showOptionDialog(frame,
						"Процесс загрузки, возможно займёт длительное время. И в данном случае Вы не сможете поменять названия загружаемых песен.\nВы действительно хотите загрузить ВСЕ аудиозаписи? \n", "Подтверждение",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null,  options, options[0]);
				if (n == JOptionPane.YES_OPTION) {
					SwingWorker<String, Void> makeQueue = new SwingWorker<String, Void>() {
						AnimationWindow aw = new AnimationWindow("Формируется очередь загрузки");
						@Override
						protected String doInBackground() throws InterruptedException {
							for (int i = 0; i < VK_API_Methods.JSONaudio.size(); i++) {
								boolean flag = true;
								JSONObject mp3 = (JSONObject) VK_API_Methods.JSONaudio.get(i);
								String artist = mp3.get("artist").toString().replaceAll("amp;", "");
								String title = mp3.get("title").toString().replaceAll("amp;", "");
								String url = mp3.get("url").toString();
								String aid = mp3.get("aid").toString();
								for (int j = 0; j < queue.size(); j++) {
									if (queue.get(j).aid.equals(aid)) {
										flag = false;
										break;
									}
								}
								if (AppFilesWorkMethods.OfflineAccess(mp3.get("aid").toString()).equals("") && flag) {
									if (mp3.get("owner_id").toString().equals(Authorization.UID)) {
										DownloadableAudio d = new DownloadableAudio(USER_PATH, artist, title, url, username, "", aid);
										queue.add(d);
									}
									else {
										try {
											DownloadableAudio d = new DownloadableAudio(USER_PATH, artist, title, url, username, (VK_API_Methods.getNameById(mp3.get("owner_id").toString(), "nom") + "/"), aid);
											queue.add(d);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
							try {
								new SaveWindow(window);
							} catch (Exception e) {
								e.printStackTrace();
							}
							cancelButton.setVisible(true);
							
							return null; 
						}
						@Override
						protected void done() {
							aw.dispose();
						}
					};
					makeQueue.execute();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				queue.subList(1, queue.size()).clear();
				cancelButton.setVisible(false);
			}
		});

		/**
		 * Слушатель кнопки переключения на список аудио авторизованного пользователя. Если интернет-соединение отсутствует,
		 * то программа предупреждает об этом и переходит в офлайн режим. Иначе с помощью метода получаем список авторизован
		 * ного пользователя.
		 */
		onlineToMyAudioButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!NetworkMethods.isConnected()) {
					if (player.getPlayList().size() > 0 && !player.getPlayList().get(0).toString().contains("file")) {
						player.stop();
						playingSong.setText("");
					}
					JOptionPane.showMessageDialog(frame,
							"Интернет-подключение отсутствует. Сейчас программа перейдёт в офлайн-режим.",
							"Предупреждение",
							JOptionPane.WARNING_MESSAGE);
					try {
						setOfflineMode();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					audiolist.clear();
					try {
						audiolist.addElements(VK_API_Methods.audioGet(Authorization.UID));
					} catch (Exception e) {
						e.printStackTrace();
					}
					friendlist.clearSelection();
					label_1.setText("Список песен: ");
					audiolist.clearSelection();
					audiolist.setCellRenderer(new AudiolistRenderer(username));
				}
			}
		};

		offlineToMyAudioButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				audiolist.clearSelection();
				songPaths = new ArrayList<String>();
				songNames = new ArrayList<String>();

				try {
					InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
					HSSFWorkbook wb = new HSSFWorkbook(in);

					Sheet sheet = wb.getSheetAt(0);

					for (Row row : sheet)
						if (row.getCell(1).getStringCellValue().equals(username) && row.getCell(2).getStringCellValue().equals("")) {
							String s = row.getCell(3).getStringCellValue();
							songPaths.add(s);
							songNames.add(s.substring(s.lastIndexOf("\\")+1, s.length()-4));
						}
					in.close();
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}


				audiolist.clear();
				audiolist.addElements(songNames);
			}
		};

		/**
		 * Слушатель кнопки продолжения воспроизведения. Если воспроизведение приостановлено или в плейлисте содержится песня,
		 * то воспроизводим. В ином случае получаем ссылку на песню, и путь к песне, если путь к песне существует, то воспроизводим песню.
		 * Если путь к песне не существует и если интернет-соединение присутствует, то воспроизводим песню по ссылке, иначе переходим в офлайн-режим.
		 */
		onlineResumeButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (player.isPaused() || player.getPlayList().size() > 0 && audiolist.isSelectionEmpty()) {
					player.play();
					playingSong.setText(currentsong);
				}
				else {
					JSONObject mp3;
					if (!audiolist.isSelectionEmpty()) {
						int index = VK_API_Methods.audiolist.indexOf(audiolist.getSelectedValue());
						mp3 = (JSONObject) VK_API_Methods.JSONaudio.get(index);
					}
					else {
						mp3 = (JSONObject) VK_API_Methods.JSONaudio.get(0);
					}
					String onlinelink = (String) mp3.get("url").toString();
					offlinelink = AppFilesWorkMethods.OfflineAccess(mp3.get("aid").toString());
					String artist = mp3.get("artist").toString().replaceAll("amp;", "");
					String title = mp3.get("title").toString().replaceAll("amp;", "");

					if (!offlinelink.equals("")) {
						player.stop();
						player = new MP3Player(new File(offlinelink));
						player.play();
						currentsong = "Сейчас играет: " + artist + " - " + title;
						playingSong.setText(currentsong);	
						offlinelink = "";
					}
					else {
						if (NetworkMethods.isConnected()) {
							try {
								player.stop();
								player = new MP3Player(new URL(onlinelink));
								player.play();
								currentsong = "Сейчас играет: " + artist + " - " + title;
								playingSong.setText(currentsong);
							} catch (MalformedURLException e) {
								playingSong.setText("Ошибка воспроизведения");
							}					
						}
						else {
							if (player.getPlayList().size() > 0 && !player.getPlayList().get(0).toString().contains("file")) {
								player.stop();
								playingSong.setText("");
							}
							JOptionPane.showMessageDialog(frame,
									"Интернет-подключение отсутствует. Сейчас программа перейдет в офлайн-режим.",
									"Предупреждение",
									JOptionPane.WARNING_MESSAGE);
							try {
								setOfflineMode();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		};

		offlineResumeButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (player.isPaused() || player.getPlayList().size() > 0 && audiolist.isSelectionEmpty()) {
					player.play();
					playingSong.setText(currentsong);
				}
				else {
					File f;
					if (!audiolist.isSelectionEmpty()) {
						int index = songNames.indexOf(audiolist.getSelectedValue());
						f = new File(songPaths.get(index));
					}
					else {
						f = new File(songPaths.get(0));
					}
					player.stop();
					player = new MP3Player(f);
					player.play();

					currentsong = "Сейчас играет: " + songNames.get(0);
					playingSong.setText(currentsong);
				}
			}
		};

		/**
		 */
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.pause();
				playingSong.setText(playingSong.getText().replaceAll("Сейчас играет", "Приостановлено"));
			}
		});

		/**
		 * Слушатель кнопки остановки
		 */
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.stop();
				playingSong.setText("");
			}
		});

		onlineButton.addActionListener(new ActionListener() { // Переход к онлайн-режиму
			public void actionPerformed(ActionEvent arg0) {
				if (NetworkMethods.isConnected()) {
					File log = new File(System.getProperty("user.home")+"/VK Music Manager/log.txt");
					if (log.exists()) {
						String s = "";
						try {
							s = FileUtils.readFileToString(log);
						} catch (IOException e) {
							e.printStackTrace();
						}
						String login = s.split("\n")[0];
						String password = s.split("\n")[1];
						Authorization session = new Authorization(login, password);

						String status = session.Authorize();
						if (status.equals("success")) {
							try {
								setOnlineMode();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							JOptionPane.showMessageDialog(frame,
									"Не удалось. Попробуйте еще раз :(",
									"Неудача",
									JOptionPane.WARNING_MESSAGE);
						}
					}
					else {
						frame.dispose();
						try {
							player.stop();
							AuthorizationWindow window = new AuthorizationWindow();
							window.frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}	
					}
				}
				else {
					JOptionPane.showMessageDialog(frame,
							"Интернет-подключение отсутствует.",
							"Предупреждение",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		/**
		 * Обработчик кликов по списку аудиозаписей. Получаем ссылку на песню и путь к песне.
		 * Если было 2 клика по списку, то если путь к песне существует, то песня вопроизводится, иначе если интернет-соединение
		 * присутствует, то песня вопроизводится по ссылке, иначе программа переходит в офлайн режим.
		 */
		onlineAudiolistListener = new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
				int modifiers = e.getModifiers();
				if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
					int index = audiolist.locationToIndex(e.getPoint());
					index = VK_API_Methods.audiolist.indexOf(audiolist.getSelectedValue());

					JSONObject mp3 = (JSONObject) VK_API_Methods.JSONaudio.get(index);

					String onlinelink = mp3.get("url").toString();
					offlinelink = AppFilesWorkMethods.OfflineAccess(mp3.get("aid").toString());
					String artist = mp3.get("artist").toString().replaceAll("amp;", "");
					String title = mp3.get("title").toString().replaceAll("amp;", "");

					if (e.getClickCount() == 1) {
						if (offlinelink.equals("")) downloadButton.setEnabled(true);
						else downloadButton.setEnabled(false);
						frame.revalidate();
					}
					if (e.getClickCount() == 2) {
						if (!offlinelink.equals("")) {
							player.stop();
							player = new MP3Player(new File(offlinelink));
							player.play();
							currentsong = "Сейчас играет: " + artist + " - " + title;
							playingSong.setText(currentsong);
							offlinelink = "";
						}
						else {
							if (NetworkMethods.isConnected()) {
								player.stop();
								try {
									player = new MP3Player(new URL(onlinelink));
								} catch (MalformedURLException e1) {
									playingSong.setText("Ошибка воспроизведения");	
								}
								player.play();
								currentsong = "Сейчас играет: " + artist + " - " + title;
								playingSong.setText(currentsong);	
							}
							else {
								if (player.getPlayList().size() > 0 && !player.getPlayList().get(0).toString().contains("file")) {
									player.stop();
									playingSong.setText("");
								}
								JOptionPane.showMessageDialog(frame,
										"Интернет-подключение отсутствует. Сейчас программа перейдет в офлайн-режим.",
										"Предупреждение",
										JOptionPane.WARNING_MESSAGE);
								try {
									setOfflineMode();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		};

		offlineAudiolistListener = new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
				int modifiers = e.getModifiers();
				if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
					if (e.getClickCount() == 2) {
						int index = songNames.indexOf(audiolist.getSelectedValue());

						File f = new File(songPaths.get(index));
						offlinelink = f.getAbsolutePath();
						player.stop();
						player = new MP3Player(f);
						player.play();

						currentsong = "Сейчас играет: " + songNames.get(index);
						playingSong.setText(currentsong);
					}
				}
			}
		};

		/**
		 * Обработчик кликов по списку друзей. Если интернет-соединение отсутствует, то программа переходит в офлайн режим,
		 * иначе если аудио друга открыты настройками приватности, то получаем список аудио, иначе выводим сообщение об ошибке.
		 */
		onlineFriendlistListener = new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
				audiolist.clearSelection();
				int index = friendlist.locationToIndex(e.getPoint());
				index = VK_API_Methods.friendslist.indexOf(friendlist.getSelectedValue());

				if (e.getClickCount() == 2) {
					if (!NetworkMethods.isConnected()) {
						if (player.getPlayList().size() > 0 && !player.getPlayList().get(0).toString().contains("file")) {
							player.stop();
							playingSong.setText("");
						}
						JOptionPane.showMessageDialog(frame,
								"Интернет-подключение отсутствует. Сейчас программа перейдет в офлайн-режим.",
								"Предупреждение",
								JOptionPane.WARNING_MESSAGE);
						try {
							setOfflineMode();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					else {
						JSONObject friend = (JSONObject) VK_API_Methods.JSONfriends.get(index);
						String uid = (String) friend.get("uid").toString();

						audiolist.clear();
						try {
							audiolist.addElements(VK_API_Methods.audioGet(uid));
							label_1.setText("Список песен " + VK_API_Methods.getNameById(uid, "gen") + ":");
							audiolist.clearSelection();
							audiolist.setCellRenderer(new AudiolistRenderer(username));
							downloadButton.setEnabled(true);
							frame.revalidate();
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(frame,
									"Аудиозаписи этого человека скрыты.",
									"Неудача",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		};

		offlineFriendlistListener = new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					audiolist.clearSelection();
					int index = friendNames.indexOf(friendlist.getSelectedValue());
					String owner = friendNames.get(index);
					songPaths = new ArrayList<String>();
					songNames = new ArrayList<String>();

					try {
						InputStream in = new FileInputStream(System.getProperty("user.home")+"/VK Music Manager/songs.xls");
						HSSFWorkbook wb = new HSSFWorkbook(in);

						Sheet sheet = wb.getSheetAt(0);

						for (Row row : sheet) {
							if (row.getCell(1).getStringCellValue().equals(username) && row.getCell(2).getStringCellValue().equals(owner)) {
								String s = row.getCell(3).getStringCellValue();
								songPaths.add(s);
								songNames.add(s.substring(s.lastIndexOf("\\")+1, s.length()-4));
							}
						}
						in.close();
						wb.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					audiolist.clear();
					audiolist.addElements(songNames);
				}
			}
		};

		/**
		 * Обработчики кликов по фильтру списков
		 */
		ALfilter.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent fe) {
				if (ALfilter.getText().equals("Искать")) {
					ALfilter.setForeground(Color.BLACK);
					ALfilter.setHorizontalAlignment(JTextField.LEFT);
					ALfilter.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent fe) {
				if (ALfilter.getText().equals("")) {
					ALfilter.setForeground(Color.LIGHT_GRAY);
					ALfilter.setHorizontalAlignment(JTextField.CENTER);
					audiolist.uninstallJTextField(ALfilter);
					ALfilter.setText("Искать");
					audiolist.installJTextField(ALfilter);
				}
			}
		});
		FLfilter.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (FLfilter.getText().equals("Искать")) {
					FLfilter.setForeground(Color.BLACK);
					FLfilter.setHorizontalAlignment(JTextField.LEFT);
					FLfilter.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (FLfilter.getText().equals("")) {
					FLfilter.setForeground(Color.LIGHT_GRAY);
					FLfilter.setHorizontalAlignment(JTextField.CENTER);
					friendlist.uninstallJTextField(FLfilter);
					FLfilter.setText("Искать");
					friendlist.installJTextField(FLfilter);
				}
			}
		});
	}
	private void setOnlineMode() throws Exception {
		ALfilter.setForeground(Color.LIGHT_GRAY);
		ALfilter.setHorizontalAlignment(JTextField.CENTER);
		audiolist.uninstallJTextField(ALfilter);
		ALfilter.setText("Искать");
		audiolist.installJTextField(ALfilter);

		FLfilter.setForeground(Color.LIGHT_GRAY);
		FLfilter.setHorizontalAlignment(JTextField.CENTER);
		friendlist.uninstallJTextField(FLfilter);
		FLfilter.setText("Искать");
		friendlist.installJTextField(FLfilter);

		resumeButton.removeActionListener(offlineResumeButtonListener);
		myAudioButton.removeActionListener(offlineToMyAudioButtonListener);
		audiolist.removeMouseListener(offlineAudiolistListener);
		friendlist.removeMouseListener(offlineFriendlistListener);

		resumeButton.addActionListener(onlineResumeButtonListener);
		myAudioButton.addActionListener(onlineToMyAudioButtonListener);
		audiolist.addMouseListener(onlineAudiolistListener);
		friendlist.addMouseListener(onlineFriendlistListener);

		VKname.setText("VK: " + username);
		VKaudioCount.setText("Песен: " + VK_API_Methods.AudioCount(Authorization.UID));
		VKavatar.setIcon(new ImageIcon(new URL(VK_API_Methods.getAvatarById(Authorization.UID))));
		offlineAudioCount.setText("Песен офлайн: " + AppFilesWorkMethods.OfflineCount(username));

		VKaudioCount.setVisible(true);
		downloadButton.setVisible(true);
		onlineButton.setVisible(false);
		scrollableAL.setColumnHeaderView(downloadAllButton);
		scrollableAL.revalidate();

		audiolist.clear();
		friendlist.clear();
		initOnlineAudioList(VK_API_Methods.audioGet(Authorization.UID));
		initOnlineFriendList(VK_API_Methods.friendsGet(Authorization.UID));

		/*
		 * Проверяем, есть ли нескачанные песни. Если все песни скачаны, делаем кнопку "Скачать все аудиозаписи" не активной.
		 */
		for (Component c : audiolist.getComponents()) {
			if (!c.getForeground().equals(new Color(134,25,12))) {
				downloadAllButton.setEnabled(true);
				break;
			}
		}
	}

	private void setOfflineMode() throws IOException {
		ALfilter.setForeground(Color.LIGHT_GRAY);
		ALfilter.setHorizontalAlignment(JTextField.CENTER);
		audiolist.uninstallJTextField(ALfilter);
		ALfilter.setText("Искать");
		audiolist.installJTextField(ALfilter);

		FLfilter.setForeground(Color.LIGHT_GRAY);
		FLfilter.setHorizontalAlignment(JTextField.CENTER);
		friendlist.uninstallJTextField(FLfilter);
		FLfilter.setText("Искать");
		friendlist.installJTextField(FLfilter);

		audiolist.setCellRenderer(new DefaultListCellRenderer());
		friendlist.setCellRenderer(new DefaultListCellRenderer());
		resumeButton.removeActionListener(onlineResumeButtonListener);
		myAudioButton.removeActionListener(onlineToMyAudioButtonListener);
		audiolist.removeMouseListener(onlineAudiolistListener);
		friendlist.removeMouseListener(onlineFriendlistListener);

		resumeButton.addActionListener(offlineResumeButtonListener);
		myAudioButton.addActionListener(offlineToMyAudioButtonListener);
		audiolist.addMouseListener(offlineAudiolistListener);
		friendlist.addMouseListener(offlineFriendlistListener);

		VKname.setText("Офлайн-режим");
		VKaudioCount.setText(username);
		try {
			VKavatar.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/avatar_off.jpg"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		offlineAudioCount.setText("Песен офлайн: " + AppFilesWorkMethods.OfflineCount(username));
		label_1.setText("Список песен: ");

		downloadButton.setVisible(false);
		onlineButton.setVisible(true);
		scrollableAL.setColumnHeaderView(null);
		scrollableAL.revalidate();

		audiolist.clear();
		friendlist.clear();
		initOfflineAudioList(AppFilesWorkMethods.getAudioByName(username));
		initOfflineFriendList(AppFilesWorkMethods.getFriendByName(username));
	}

	private void initOnlineAudioList(ArrayList<String> audioByID) {
		audiolist.addElements(audioByID);
		audiolist.setCellRenderer(new AudiolistRenderer(username));
	}

	private void initOnlineFriendList(ArrayList<String> friendsByID) {
		friendlist.addElements(friendsByID);
		friendlist.setCellRenderer(new FriendlistRenderer(username));
	}


	private void initOfflineAudioList(ArrayList<String> audioByName) {
		songPaths.clear();
		songNames.clear();
		songPaths = audioByName;
		for (String s : songPaths)
			songNames.add(s.substring(s.lastIndexOf("\\")+1, s.length()-4));
		audiolist.addElements(songNames);
	}

	private void initOfflineFriendList(ArrayList<String> friendsByName) {
		friendNames.clear();
		friendNames = friendsByName;
		friendlist.addElements(friendNames);
	}
}
