package beta;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;

import java.awt.Color;

import javax.swing.ImageIcon;

import java.awt.Font;

import javax.swing.JPasswordField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel;

/**
 * <p>Класс графического интерфейса основного окна</p>
 * Используется Substance Mariner Java Look & Feel
 * @value rememberPasswordFlag Переменная, хранящая в себе параметр, означающий нажал ли пользователь "Сохранить пароль"
 */
public class AuthorizationWindow  { // Окно авторизации
	JFrame frame;
	private JTextField loginField;
	private JPasswordField passwordField;
	private JCheckBox showPassword;
	private JCheckBox rememberPassword;
	private JLabel image;
	private JLabel welcome;
	private JLabel instructions;
	private JLabel warning;
	private JLabel login;
	private JLabel password;
	private JButton loginButton;

	boolean rememberPasswordFlag = false;
	String username;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceMarinerLookAndFeel());
					setUIFont(new Font("Segoe UI", Font.PLAIN, 12));
					AuthorizationWindow window = new AuthorizationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void setUIFont(Font font) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, font);
		}
	}

	/**
	 * <p>Конструктор</p>
	 * Инициализируются компоненты, "слушатели" компонентов и производятся доп. операции
	 */
	public AuthorizationWindow() throws IOException {
		initialize();
		listeners();
		operations();
		username = "";
	}

	private void initialize() throws IOException {
		Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
		Border empty = new EmptyBorder(0, 3, 0, 0);
		CompoundBorder textFieldBorder = new CompoundBorder(line, empty);

		frame = new JFrame("VK Music Manager Beta");
		frame.setIconImage(ImageIO.read(getClass().getResource("/images/logo32.png")));
		frame.setResizable(false);
		frame.setBounds(600, 200, 312, 395);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		image = new JLabel();
		image.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/logo.png"))));
		image.setBounds(84, 12, 146, 133);
		frame.getContentPane().add(image);

		welcome = new JLabel("Добро пожаловать!", SwingConstants.CENTER);
		welcome.setFont(new Font("Tahoma", Font.BOLD, 13));
		welcome.setBounds(10, 150, 286, 20);
		frame.getContentPane().add(welcome);

		instructions = new JLabel("Войдите под своей учетной записью ВКонтакте", SwingConstants.CENTER);
		instructions.setFont(new Font("Tahoma", Font.PLAIN, 12));
		instructions.setBounds(10, 174, 286, 20);
		frame.getContentPane().add(instructions);

		warning = new JLabel("(требуется подключение к интернету)", SwingConstants.CENTER);
		warning.setFont(new Font("Tahoma", Font.PLAIN, 11));
		warning.setBounds(10, 199, 286, 14);
		frame.getContentPane().add(warning);

		login = new JLabel("Логин");
		login.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		login.setBounds(28, 229, 50, 22);
		frame.getContentPane().add(login);

		password = new JLabel("Пароль");
		password.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		password.setBounds(28, 257, 50, 22);
		frame.getContentPane().add(password);

		loginField = new JTextField();
		loginField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		loginField.setBounds(84, 229, 158, 20);
		frame.getContentPane().add(loginField);

		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		passwordField.setBounds(84, 257, 158, 20);
		passwordField.setEchoChar('●');
		frame.getContentPane().add(passwordField);

		passwordField.setBorder(textFieldBorder);
		loginField.setBorder(textFieldBorder);

		showPassword = new JCheckBox("Показать пароль");
		showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		showPassword.setOpaque(false);
		showPassword.setFocusPainted(false);
		showPassword.setBounds(84, 282, 158, 23);
		frame.getContentPane().add(showPassword);

		rememberPassword = new JCheckBox("Запомнить пароль");
		rememberPassword.setOpaque(false);
		rememberPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		rememberPassword.setFocusPainted(false);
		rememberPassword.setBounds(84, 302, 158, 23);
		frame.getContentPane().add(rememberPassword);

		loginButton = new JButton("Войти");
		loginButton.setBounds(106, 332, 94, 22);	
		loginButton.setFocusPainted(false);
		loginButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		frame.getContentPane().add(loginButton);
	}

	private void listeners() {
		/**
		 * <p>Слушатель для loginField, passwordField, loginButton, осуществляющий авторизацию по нажатию "Enter"</p>
		 */
		KeyListener k = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {                
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					Thread thread = new Thread() {
						public void run() {
							try {
								Authorize();
							}
							catch (Exception e) {
								loginButton.setEnabled(true);
							}
							this.interrupt();
						}
					};
					loginButton.setEnabled(false);
					thread.start();
				}
			}        
		};

		/**
		 * <p>Слушатель для frame, который в случае закрытия окна вызывает окно подтверждения</p>
		 */
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				Object[] options = { "Да", "Нет" };
				int n = JOptionPane
						.showOptionDialog(event.getWindow(), "Вы действительно хотите выйти?",
								"Подтверждение", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				if (n == JOptionPane.YES_OPTION) System.exit(0);
			}
		});

		/**
		 * <p>Слушатель для showPassword, который либо показывает пароль, либо скрывает, в зависимости от того, что выбрал пользователь</p>
		 */
		showPassword.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				loginButton.requestFocus();
				if (e.getStateChange() == ItemEvent.SELECTED) passwordField.setEchoChar((char) 0);
				else passwordField.setEchoChar('●');
			}

		}); 

		/**
		 * <p>Слушатель для rememberPassword, который либо запоминает пароль, либо нет, в зависимости от того, что выбрал пользователь</p>
		 */
		rememberPassword.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				loginButton.requestFocus();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					rememberPasswordFlag = true;
				}
				else rememberPasswordFlag = false;
			}
		}); 

		/**
		 * <p>Слушатель для loginButton, который производит авторизацию, если была нажата эта кнопка</p>
		 */
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)  {
				Thread thread = new Thread() {
					public void run() {
						try {
							Authorize();
						}
						catch (Exception e) {
							loginButton.setEnabled(true);
						}
						this.interrupt();
					}
				};
				loginButton.setEnabled(false);
				thread.start();
			}
		});

		passwordField.addKeyListener(k);
		loginField.addKeyListener(k);
		loginButton.addKeyListener(k);
	}

	/**
	 * <p>Создание директории программы при ее отсутствии</p>
	 * <p>Автоматическая вставка авторизационных данных при их наличии</p>
	 */
	private void operations() {
		File log = new File(System.getProperty("user.home")+"/VK Music Manager/log.txt");
		if (log.exists()) {
			try {
				String s = FileUtils.readFileToString(log, System.getProperty("file.encoding"));

				loginField.setText(s.split("\n")[0]);
				passwordField.setText(s.split("\n")[1]);

				rememberPassword.setSelected(true);
				rememberPasswordFlag = true;
			} catch (IOException e) {
				log.delete();
			}	
		}
	}

	/**
	 * <p>Запуск авторизации пользователя</p>
	 * Если пользователь нажал "Запомнить пароль", то авторизационные данные запишутся в файл
	 * <br> Если пользователь убрал "Запомнить пароль", то авторизационные данные, при их наличии удаляться
	 * 
	 * @value process Визуализация процесса авторизации 
	 * <br> session Обьект класса Authorization
	 * @return MainWindow.main(null); в случае успешной авторизации
	 * <br> OfflineWindow.main(null); в случае отсутствия интернет-соединения
	 * @throws Exception 
	 */
	private void Authorize() throws Exception {
		Thread process = new Thread() {
			public void run() {
				int count = 0;
				while (!loginButton.isEnabled()) {
					if (count == 0) {
						warning.setText("");
						count++;
					}
					else if (count == 1) {
						warning.setText("●");
						count++;
					}
					else if (count == 2) {
						warning.setText("● ●");
						count++;
					}
					else if (count == 3) {
						warning.setText("● ● ●");
						count = 0;
					}
					try {
						Thread.sleep(550);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.interrupt();
			}
		};
		process.start();

		Authorization session = new Authorization(loginField.getText(), String.valueOf(passwordField.getPassword()));
		String status = session.Authorize();
		if (status.equals("no_internet")) {
			final File directory = new File(System.getProperty("user.home")+"/VK Music Manager/");
			directory.mkdirs();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (directory.list().length > 0) {
						String[] profiles = AppFilesWorkMethods.folders(directory);
						username = (String) JOptionPane.showInputDialog(frame, "Интернет-соединение отсутствует. Выберите профиль", "Выбор профиля", JOptionPane.QUESTION_MESSAGE,
								null, profiles, profiles[0]);
						if (username != null) {
							frame.dispose();
							try {
								MainWindow.main(false, username);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						else {
							loginButton.setEnabled(true);
							warning.setText("(требуется подключение к интернету");
							frame.revalidate();
						}
					}
					else {
						username = "";
						frame.dispose();
						try {
							MainWindow.main(false, username); // Офлайн
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
		else if (status.equals("incorrect_data")) {
			loginField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
			passwordField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
			warning.setText("Не удалось, попробуйте еще раз.");
			loginButton.setEnabled(true);
		}
		else if (status.equals("captcha")) {
			warning.setText("Слишком много попыток, попробуйте чуть позже.");
			loginButton.setEnabled(true);
		}
		else if (status.equals("success")) {
			String authorizedUsername = VK_API_Methods.getNameById(Authorization.UID, "nom");
			File directory = new File(System.getProperty("user.home")+"/VK Music Manager/" + authorizedUsername + "/");	
			directory.mkdirs();

			if (rememberPasswordFlag) {
				File log = new File(System.getProperty("user.home")+"/VK Music Manager/log.txt");
				if (!log.exists()) {
					log.createNewFile();
				}
				try {
					FileWriter fw = new FileWriter(log, false);
					fw.append(loginField.getText());
					fw.append("\n");
					fw.append(String.valueOf(passwordField.getPassword()));
					fw.flush();
					fw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

			}
			else {
				File log = new File(System.getProperty("user.home")+"/VK Music Manager/log.txt");
				if (log.exists()) {
					log.delete();
				}
			}

			frame.dispose();
			try {
				VK_API_Methods.trackVisitor();
				MainWindow.main(true, authorizedUsername);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}