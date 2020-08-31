package protect;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;

public class PrivateChatWindow extends JFrame {

	JTextArea messageArea;
	JButton sendButton, emptyButton;
	JButton fontButton, faceButton, imageButton, fileButton;
	JButton blank1, blank2, blank3, blank4, blank5, blank6, blank7, blank8, blank9; // 没什么用，无视这些
	JLabel onlineLabel;
	JTextPane dialogueArea;
	JButton userButton1, userButton2, userButton3, userButton4, userButton5, userButton6, userButton7, userButton8,
			userButton9, userButton10;

	PrivateChatWindow() {
		// 窗口大小、位置
		this.setSize(650, 490);
		this.setLocation(150, 250);
		Container con = this.getContentPane();
		con.setLayout(new BorderLayout()); // 设置窗体布局为BorderLayout

		// 初始化
		dialogueArea = new JTextPane();
		messageArea = new JTextArea();
		sendButton = new JButton("发送");
		emptyButton = new JButton("清空");
		onlineLabel = new JLabel("在线成员：                           ");
		blank1 = new JButton("");
		blank2 = new JButton("");
		blank3 = new JButton("");
		blank4 = new JButton("");
		blank5 = new JButton("");
		blank6 = new JButton("");
		blank7 = new JButton("");
		blank8 = new JButton("");
		blank9 = new JButton("");

		// 用户
		userButton1 = new JButton("");
		userButton2 = new JButton("");
		userButton3 = new JButton("");
		userButton4 = new JButton("");
		userButton5 = new JButton("");
		userButton6 = new JButton("");
		userButton7 = new JButton("");
		userButton8 = new JButton("");
		userButton9 = new JButton("");
		userButton10 = new JButton("");

		JPanel pp1 = new JPanel();
		JPanel pp2 = new JPanel();
		JPanel p3 = new JPanel();
		JPanel ppp = new JPanel();
		JPanel pp3 = new JPanel();
		JPanel p = new JPanel();
		JPanel pp4 = new JPanel();
		JScrollPane sp = new JScrollPane(dialogueArea);

		ImageIcon iconFont = new ImageIcon(".\\icon\\font.png");
		fontButton = new JButton(iconFont);
		fontButton.setContentAreaFilled(false);
		fontButton.setBorderPainted(false);
		fontButton.setRolloverIcon(new ImageIcon(".\\icon\\font1.png"));
		fontButton.setPressedIcon(new ImageIcon(".\\icon\\font2.png"));

		ImageIcon iconFace = new ImageIcon(".\\icon\\face.png");
		faceButton = new JButton(iconFace);
		faceButton.setContentAreaFilled(false);
		faceButton.setBorderPainted(false);
		faceButton.setRolloverIcon(new ImageIcon(".\\icon\\face1.png"));
		faceButton.setPressedIcon(new ImageIcon(".\\icon\\face2.png"));

		ImageIcon iconImage = new ImageIcon(".\\icon\\image.png");
		imageButton = new JButton(iconImage);
		imageButton.setContentAreaFilled(false);
		imageButton.setBorderPainted(false);
		imageButton.setRolloverIcon(new ImageIcon(".\\icon\\image1.png"));
		imageButton.setPressedIcon(new ImageIcon(".\\icon\\image2.png"));

		ImageIcon iconFile = new ImageIcon(".\\icon\\file.png");
		fileButton = new JButton(iconFile);
		fileButton.setContentAreaFilled(false);
		fileButton.setBorderPainted(false);
		fileButton.setRolloverIcon(new ImageIcon(".\\icon\\file1.png"));
		fileButton.setPressedIcon(new ImageIcon(".\\icon\\file2.png"));
		blank1.setBorderPainted(false);
		blank2.setBorderPainted(false);
		blank3.setBorderPainted(false);
		blank4.setBorderPainted(false);
		blank5.setBorderPainted(false);
		blank6.setBorderPainted(false);
		blank7.setBorderPainted(false);
		blank8.setBorderPainted(false);
		blank9.setBorderPainted(false);
		blank1.setContentAreaFilled(false);
		blank2.setContentAreaFilled(false);
		blank3.setContentAreaFilled(false);
		blank4.setContentAreaFilled(false);
		blank5.setContentAreaFilled(false);
		blank6.setContentAreaFilled(false);
		blank7.setContentAreaFilled(false);
		blank8.setContentAreaFilled(false);
		blank9.setContentAreaFilled(false);

		userButton1.setBorderPainted(false);
		userButton2.setBorderPainted(false);
		userButton3.setBorderPainted(false);
		userButton4.setBorderPainted(false);
		userButton5.setBorderPainted(false);
		userButton6.setBorderPainted(false);
		userButton7.setBorderPainted(false);
		userButton8.setBorderPainted(false);
		userButton9.setBorderPainted(false);
		userButton10.setBorderPainted(false);
		userButton1.setContentAreaFilled(false);
		userButton2.setContentAreaFilled(false);
		userButton3.setContentAreaFilled(false);
		userButton4.setContentAreaFilled(false);
		userButton5.setContentAreaFilled(false);
		userButton6.setContentAreaFilled(false);
		userButton7.setContentAreaFilled(false);
		userButton8.setContentAreaFilled(false);
		userButton9.setContentAreaFilled(false);
		userButton10.setContentAreaFilled(false);

		MatteBorder bottomBorder = new MatteBorder(0, 0, 2, 0, new Color(192, 192, 192)); // 边框
		MatteBorder topBorder = new MatteBorder(2, 0, 0, 0, new Color(192, 192, 192));
		MatteBorder rightBorder = new MatteBorder(0, 0, 0, 2, new Color(192, 192, 192));
		dialogueArea.setBorder(bottomBorder);
		pp1.setBorder(bottomBorder);
		ppp.setBorder(rightBorder);
		pp3.setBorder(topBorder);

		p.setLayout(new GridLayout(1, 10));

		pp2.setLayout(new BorderLayout());
		pp2.add(sp, BorderLayout.NORTH);
		pp2.add(p, BorderLayout.CENTER);
		pp2.add(messageArea, BorderLayout.SOUTH);

		pp4.setLayout(new GridLayout(20, 1));
		pp3.setLayout(new BorderLayout());
		pp3.add(onlineLabel, BorderLayout.NORTH);
		pp3.add(pp4, BorderLayout.CENTER);

		ppp.setLayout(new BorderLayout());
		ppp.add(pp2, BorderLayout.CENTER);
		ppp.add(p3, BorderLayout.SOUTH);

		p3.setLayout(new FlowLayout(FlowLayout.RIGHT));

		con.add(pp1, BorderLayout.NORTH);
		con.add(ppp, BorderLayout.CENTER);
		con.add(pp3, BorderLayout.EAST);

		sp.setPreferredSize(new Dimension(0, 250));
		messageArea.setPreferredSize(new Dimension(0, 140));
		dialogueArea.setEditable(false); // 对话窗口设置成只读属性

		p3.add(sendButton);
		p3.add(emptyButton);
		p.add(fontButton);
		p.add(faceButton);
		p.add(imageButton);
		p.add(fileButton);
		p.add(blank1);
		p.add(blank2);
		p.add(blank3);
		p.add(blank4);
		p.add(blank5);
		p.add(blank6);
		p.add(blank7);
		p.add(blank8);
		p.add(blank9);
		pp4.add(userButton1);
		pp4.add(userButton2);
		pp4.add(userButton3);
		pp4.add(userButton4);
		pp4.add(userButton5);
		pp4.add(userButton6);
		pp4.add(userButton7);
		pp4.add(userButton8);
		pp4.add(userButton9);
		pp4.add(userButton10);

		pp3.setBackground(Color.WHITE);
		p.setBackground(Color.WHITE);
		p3.setBackground(Color.WHITE);

		this.setResizable(false);
		this.setVisible(true);
	}

	public static void main(String args[]) {
		PrivateChatWindow wm = new PrivateChatWindow();
		wm.show();
	}
}
