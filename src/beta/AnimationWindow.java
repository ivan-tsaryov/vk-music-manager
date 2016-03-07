package beta;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class AnimationWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JLabel information = new JLabel("Пожалуйста, подождите", SwingConstants.CENTER);
	
	public AnimationWindow(final String task) {
		super.setBounds(100,100, 200, 30);
		super.setLocationRelativeTo(null);
		super.setUndecorated(true);
		super.add(information);
		super.setAlwaysOnTop(true);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		super.getRootPane().setBorder(border);
	    super.setVisible(true);
		
		Thread process = new Thread() {
			public void run() {
				int count = 0;
				while (true) {
					if (count == 0) {
						information.setText(task);
						count++;
					}
					else if (count == 1) {
						information.setText(information.getText() + ".");
						count++;
					}
					else if (count == 2) {
						information.setText(information.getText() + ".");
						count++;
					}
					else if (count == 3) {
						information.setText(information.getText() + ".");
						count = 0;
					}
					try {
						Thread.sleep(550);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		process.start();
	}
}
