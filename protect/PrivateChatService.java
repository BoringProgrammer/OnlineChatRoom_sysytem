package protect;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class PrivateChatService {
	public PrivateChatWindow privateChatWindow;
	public String name = "";
	private StyledDocument doc;
	private SimpleAttributeSet attr;
	private int cursorLocation = 0;
	private int fontCount = 1;

	private final String TALK_START = "<@>";
	private final String TALK_END = "<@/>";
	private final String TEXT_END = "<text>";
	private final String FILE_START = "<file>";
	private final String FILE_END = "<file/>";
	private final String FILE_NAME_END = "?";
	private final String DEFAULT_ENCODE = "UTF-8";
	private final String ISO_ENCODE = "ISO-8859-1";

	public PrivateChatService() {
		privateChatWindow = new PrivateChatWindow();
		doc = privateChatWindow.dialogueArea.getStyledDocument();
		attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, 24);
		EmptyListener emptyListener = new EmptyListener();
		FaceChooseListener faceChooseListener = new FaceChooseListener();
		FileChooseListener fileChooseListener = new FileChooseListener();
		ImageChooseListener imageChooseListener = new ImageChooseListener();
		FontChooseListener fontChooseListener = new FontChooseListener();
		privateChatWindow.emptyButton.addActionListener(emptyListener);
		privateChatWindow.faceButton.addActionListener(faceChooseListener);
		privateChatWindow.fileButton.addActionListener(fileChooseListener);
		privateChatWindow.imageButton.addActionListener(imageChooseListener);
		privateChatWindow.fontButton.addActionListener(fontChooseListener);
	}

	private class EmptyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			privateChatWindow.messageArea.setText("");
		}
	}

	private class FontChooseListener implements ActionListener {
		SimpleAttributeSet tempAttr;

		private void first() {
			tempAttr = new SimpleAttributeSet();
			StyleConstants.setForeground(tempAttr, Color.black);
			StyleConstants.setFontSize(tempAttr, 18);
			StyleConstants.setFontFamily(tempAttr, "Dialog");
			StyleConstants.setUnderline(tempAttr, false);
			attr = tempAttr;
		}

		private void second() {
			tempAttr = new SimpleAttributeSet();
			StyleConstants.setForeground(tempAttr, Color.BLUE);
			StyleConstants.setFontSize(tempAttr, 20);
			StyleConstants.setFontFamily(tempAttr, "Arial Black");
			StyleConstants.setBold(tempAttr, true);
			attr = tempAttr;
		}

		private void third() {
			tempAttr = new SimpleAttributeSet();
			StyleConstants.setForeground(tempAttr, Color.GREEN);
			StyleConstants.setFontSize(tempAttr, 18);
			StyleConstants.setItalic(tempAttr, true);
			StyleConstants.setStrikeThrough(tempAttr, true);
			attr = tempAttr;
		}

		public void actionPerformed(ActionEvent arg0) {
			if (fontCount % 3 == 0) {
				fontCount = 1;
			} else {
				fontCount++;
			}

			if (fontCount == 1) {
				first();
			} else if (fontCount == 2) {
				second();
			} else if (fontCount == 3) {
				third();
			}
		}

	}

	private class FaceChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(".\\clientFace"));
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.showDialog(new JLabel(), "选择");
			File file = chooser.getSelectedFile();
			String tempString = file.getAbsoluteFile().toString();
			tempString = FILE_START + tempString + FILE_END;
			privateChatWindow.messageArea.append(tempString);
		}
	}

	private class ImageChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.showDialog(new JLabel(), "选择");
			File file = chooser.getSelectedFile();
			String tempString = file.getAbsoluteFile().toString();
			tempString = FILE_START + tempString + FILE_END;
			privateChatWindow.messageArea.append(tempString);
		}
	}

	private class FileChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.showDialog(new JLabel(), "选择");
			File file = chooser.getSelectedFile();
			String tempString = file.getAbsoluteFile().toString();
			tempString = FILE_START + tempString + FILE_END;
			privateChatWindow.messageArea.append(tempString);
		}
	}

	private class PrivateChatListenr implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			name = e.getActionCommand();
		}
	}

	public void readExplain(StringBuffer sb) {

		int start = sb.indexOf(TALK_START);
		if (start == 0) {
			sb.delete(0, start + TALK_START.length());
		}

		try {
			int e;
			int n = 1;
			while (n == 1) {

				int imageStart;
				int end;
				int textEnd;
				if (((textEnd = sb.indexOf(TEXT_END)) >= 0) && (end = sb.indexOf(FILE_END)) < 0) {
					String tempText = sb.substring(0, textEnd);
					tempText = new String(tempText.getBytes(ISO_ENCODE), DEFAULT_ENCODE);
					if (!tempText.equals("")) {
						doc.insertString(doc.getLength(), tempText, attr);
						cursorLocation += tempText.length();
					}
					sb.delete(0, textEnd + TEXT_END.length());
				} else if ((imageStart = sb.indexOf(FILE_START)) >= 0) {
					if (((textEnd = sb.indexOf(TEXT_END)) >= 0) && (textEnd < imageStart)) {
						String tempText = sb.substring(0, textEnd);
						tempText = new String(tempText.getBytes(ISO_ENCODE), DEFAULT_ENCODE);
						if (!tempText.equals("")) {
							doc.insertString(doc.getLength(), tempText, attr);
							cursorLocation += tempText.length();
						}
						sb.delete(0, textEnd + TEXT_END.length());
					} else {
						String tempText = sb.substring(0, imageStart);
						tempText = new String(tempText.getBytes(ISO_ENCODE), DEFAULT_ENCODE);
						if (!tempText.equals("")) {
							doc.insertString(doc.getLength(), tempText, attr);
							cursorLocation += tempText.length();
						}
						sb.delete(0, imageStart + FILE_START.length());
						sendFile(sb);
					}
				}

				e = sb.indexOf(TALK_END);
				if (e == 0) {
					n = 0;
					sb.delete(0, TALK_END.length());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendFile(StringBuffer sb) {

		try {
			int file_name_end;
			file_name_end = sb.indexOf(FILE_NAME_END);

			String file_name = new String(sb.substring(0, file_name_end).getBytes(ISO_ENCODE), DEFAULT_ENCODE);
			sb.delete(0, file_name_end + FILE_NAME_END.length());

			String imageLengthString = sb.substring(0, 8);
			byte[] imageLengthByteArray = imageLengthString.getBytes(ISO_ENCODE);
			long imageLength = bytesToLong(imageLengthByteArray);

			sb.delete(0, 8);

			byte[] image = sb.toString().getBytes(ISO_ENCODE);
			FileOutputStream fos = new FileOutputStream(new File(".\\private\\" + file_name));

			fos.write(image, 0, (int) imageLength);
			sb.delete(0, (int) imageLength);

			fos.close();

			String fileFormat = file_name.substring(file_name.lastIndexOf(".") + 1);

			if (fileFormat.equals("png") || fileFormat.equals("jpg")) {
				// 显示图片
				privateChatWindow.dialogueArea.setCaretPosition(cursorLocation);
				privateChatWindow.dialogueArea.insertIcon(new ImageIcon(".\\clientFace\\" + file_name));
				cursorLocation++;
			}

			int end;
			end = sb.indexOf(FILE_END);
			sb.delete(0, end + FILE_END.length());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] longToBytes(long n) {
		byte[] b = new byte[8];
		b[7] = (byte) (n & 0xff);
		b[6] = (byte) (n >> 8 & 0xff);
		b[5] = (byte) (n >> 16 & 0xff);
		b[4] = (byte) (n >> 24 & 0xff);
		b[3] = (byte) (n >> 32 & 0xff);
		b[2] = (byte) (n >> 40 & 0xff);
		b[1] = (byte) (n >> 48 & 0xff);
		b[0] = (byte) (n >> 56 & 0xff);
		return b;
	}

	public static long bytesToLong(byte[] array) {
		return ((((long) array[0] & 0xff) << 56) | (((long) array[1] & 0xff) << 48) | (((long) array[2] & 0xff) << 40)
				| (((long) array[3] & 0xff) << 32) | (((long) array[4] & 0xff) << 24) | (((long) array[5] & 0xff) << 16)
				| (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff) << 0));
	}

	public void flush() {
		File userFile = new File(".\\user\\user.txt");

		try {
			Reader in = new FileReader(userFile);
			BufferedReader reader = new BufferedReader(in);
			String str = null;
			int i = 1;
			while ((str = reader.readLine()) != null) {
				setUserList(str, i);
				i++;
			}
			reader.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setUserList(String str, int i) {
		if (i == 1) {
			privateChatWindow.userButton1.setText(str);
		} else if (i == 2) {
			privateChatWindow.userButton2.setText(str);
		} else if (i == 3) {
			privateChatWindow.userButton3.setText(str);
		} else if (i == 4) {
			privateChatWindow.userButton4.setText(str);
		} else if (i == 5) {
			privateChatWindow.userButton5.setText(str);
		} else if (i == 6) {
			privateChatWindow.userButton6.setText(str);
		} else if (i == 7) {
			privateChatWindow.userButton7.setText(str);
		} else if (i == 8) {
			privateChatWindow.userButton8.setText(str);
		} else if (i == 9) {
			privateChatWindow.userButton9.setText(str);
		} else if (i == 10) {
			privateChatWindow.userButton10.setText(str);
		}
	}

}
