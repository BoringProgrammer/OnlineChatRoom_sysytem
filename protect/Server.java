package protect;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Server {
	private String name;
	private ServerWindow window;
	private ArrayList<OutputStream> outPutClientStream;
	private HashMap<String, Socket> socketList;
	private StyledDocument doc;
	private SimpleAttributeSet attr;
	private int cursorLocation = 0;
	private int fontCount = 1;

	private final String TALK_START = "<@>";
	private final String TALK_END = "<@/>";
	private final String TEXT_END = "<text>";
	private final String FILE_NAME_END = "?";
	private final String FILE_START = "<file>";
	private final String FILE_END = "<file/>";
	private final String DEFAULT_ENCODE = "UTF-8";
	private final String ISO_ENCODE = "ISO-8859-1";

	Server() {
		window = new ServerWindow();
		doc = window.dialogueArea.getStyledDocument();
		attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, 24);
	}

	public void run() {
		ConnectListener connectListener = new ConnectListener();
		NameListener nameListener = new NameListener();
		SendListener sendListener = new SendListener();
		EmptyListener emptyListener = new EmptyListener();
		FaceChooseListener faceChooseListener = new FaceChooseListener();
		FileChooseListener fileChooseListener = new FileChooseListener();
		ImageChooseListener imageChooseListener = new ImageChooseListener();
		FontChooseListener fontChooseListener = new FontChooseListener();
		window.connectButton.addActionListener(connectListener);
		window.confirmButton.addActionListener(nameListener);
		window.sendButton.addActionListener(sendListener);
		window.emptyButton.addActionListener(emptyListener);
		window.faceButton.addActionListener(faceChooseListener);
		window.fileButton.addActionListener(fileChooseListener);
		window.imageButton.addActionListener(imageChooseListener);
		window.fontButton.addActionListener(fontChooseListener);
	}

	private class ConnectListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String tempPort;
			outPutClientStream = new ArrayList<OutputStream>();
			socketList = new HashMap<>();
			tempPort = window.portField.getText();
			if (tempPort.equals("") || Integer.parseInt(tempPort) < 0) {
				JOptionPane.showMessageDialog(null, "请输入正确的IP地址和端口号");
			} else {
				Runnable serverRun = new Runnable() {
					public void run() {
						try {
							ServerSocket serverSocket = new ServerSocket(Integer.parseInt(tempPort));
							doc.insertString(doc.getLength(), "服务器已连接...\n", attr);
							cursorLocation += "服务器已连接...\n".length();
							begin();
							while (true) {
								Socket clientSocket = serverSocket.accept();
								OutputStream writer = clientSocket.getOutputStream();
								outPutClientStream.add(writer);
								Thread clientThread = new Thread(new ClientThread(clientSocket));
								clientThread.start();
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
				};
				Thread serverThread = new Thread(serverRun);
				serverThread.start();
			}
		}
	}

	private class NameListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String tempName = "";
			tempName = window.nameField.getText();
			if (tempName.equals("")) {
				JOptionPane.showMessageDialog(null, "请输入昵称");
			} else {
				name = tempName;
			}
		}
	}

	private class SendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String tempMessage = window.messageArea.getText();
			StringBuffer strb = new StringBuffer(tempMessage);
			flush();

			try {
				if (tempMessage.equals("")) {
					JOptionPane.showMessageDialog(null, "发送内容不能为空，请重新输入");
				} else {
					doc.insertString(doc.getLength(), name + ":", attr);
					cursorLocation += name.length();
					cursorLocation += ":".length();
					sendAll(name + ":");
					while (true) {
						int locationStart = strb.indexOf(FILE_START);
						int locationEnd = strb.indexOf(FILE_END);
						String tempString = "";
						if (((locationStart >= 0) && (locationEnd >= 0)) && (locationStart < locationEnd)) {
							if (locationStart > 0) {
								tempString = strb.substring(0, locationStart);
								doc.insertString(doc.getLength(), tempString, attr);
								cursorLocation += tempString.length();
								sendAll(tempString + TEXT_END);
								tempString = "";
								strb.delete(0, locationStart);
							}
							strb.delete(0, FILE_START.length());
							locationEnd = strb.indexOf(FILE_END);
							tempString = strb.substring(0, locationEnd);

							window.dialogueArea.setCaretPosition(cursorLocation);
							window.dialogueArea.insertIcon(new ImageIcon(tempString));
							cursorLocation++;
							// 发送图片
							Iterator<OutputStream> itss = outPutClientStream.iterator();
							while (itss.hasNext()) {
								File imageFile = new File(tempString);
								InputStream fileIs = new FileInputStream(imageFile);
								long fileLenth = imageFile.length();

								int lenth;

								OutputStream writer = (OutputStream) itss.next();
								writer.write(FILE_START.getBytes());
								writer.write(imageFile.getName().getBytes());
								writer.write(FILE_NAME_END.getBytes());

								byte[] bs = longToBytes(fileLenth);
								writer.write(bs);
								byte[] b = new byte[1024];
								while ((lenth = fileIs.read(b)) > 0) {
									writer.write(b, 0, lenth);
									writer.flush();
								}
								writer.write(FILE_END.getBytes());
								fileIs.close();
							}

							strb.delete(0, locationEnd + FILE_END.length());
							tempString = "";
						} else {
							break;
						}
					}
					String tempString = "";
					if (!strb.equals("")) {
						tempString = strb.toString();
						doc.insertString(doc.getLength(), tempString, attr);
						cursorLocation += tempString.length();
						sendAll(tempString);
					}
					doc.insertString(doc.getLength(), "\n", attr);
					cursorLocation += "\n".length();
					sendAll("\n" + TEXT_END);
				}
			} catch (BadLocationException a) {
				a.printStackTrace();

			} catch (Exception a) {
				a.printStackTrace();
			}

			window.messageArea.setText("");
		}

	}

	private class EmptyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			window.messageArea.setText("");
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
			chooser.setCurrentDirectory(new File(".\\serverFace"));
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.showDialog(new JLabel(), "选择");
			File file = chooser.getSelectedFile();
			String tempString = file.getAbsoluteFile().toString();
			tempString = FILE_START + tempString + FILE_END;
			window.messageArea.append(tempString);
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
			window.messageArea.append(tempString);
		}
	}

	private class FileChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.showDialog(new JLabel(), "选择");
			File file = chooser.getSelectedFile();
			try {
				Iterator<OutputStream> itss = outPutClientStream.iterator();

				while (itss.hasNext()) {
					FileInputStream fileIn = new FileInputStream(file);
					long fileLenth = file.length();
					int lenth;
					OutputStream writer = (OutputStream) itss.next();
					writer.write(FILE_START.getBytes());
					writer.write(file.getName().getBytes());
					writer.write(FILE_NAME_END.getBytes());

					byte[] bs = longToBytes(fileLenth);
					writer.write(bs);
					byte[] b = new byte[1024];
					while ((lenth = fileIn.read(b)) > 0) {
						writer.write(b, 0, lenth);
						writer.flush();
					}
					writer.write(FILE_END.getBytes());
					fileIn.close();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (Exception a) {
				a.printStackTrace();
			}

		}
	}

	public class ClientThread implements Runnable {
		Socket socket;

		ClientThread(Socket tempSocket) {
			socket = tempSocket;
			flush();
		}

		public void run() {
			reads(socket);
		}
	}

	public void begin() {
		File userFile = new File(".\\user\\user.txt");

		try {
			Writer out = new FileWriter(userFile);
			BufferedWriter writer = new BufferedWriter(out);
			writer.write("");
			writer.flush();
			writer.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reads(Socket socket) {

		try {
			InputStream is = socket.getInputStream();
			StringBuffer sb = new StringBuffer();

			int first = -1;

			while ((first = sb.indexOf(TEXT_END)) < 0) {
				readToBuffer(is, sb);
			}

			String firstStr = new String(sb.substring(0, first).getBytes(ISO_ENCODE), DEFAULT_ENCODE);
			sb.delete(0, first + TEXT_END.length());
			socketList.put(firstStr, socket);

			while (true) {

				int talkStart;
				int talkEnd;
				String str = "";

				if ((talkStart = sb.indexOf(TALK_START)) >= 0) {
					int n = 1;
					while (n==1) {
						if ((talkEnd = sb.indexOf(TALK_END)) >= 0) {
							int f1, f2;
							f1 = sb.indexOf("?");
							f2 = sb.indexOf(":");

							System.out.println(f1);
							String sendName = new String(
									sb.substring(talkStart + TALK_START.length(), f1).getBytes(ISO_ENCODE),
									DEFAULT_ENCODE);
							String reseverName = new String(sb.substring(f1 + "?".length(), f2).getBytes(ISO_ENCODE),
									DEFAULT_ENCODE);
							String message = new String(sb.substring(f2 + ":".length(), talkEnd).getBytes(ISO_ENCODE),
									DEFAULT_ENCODE);
							
							Iterator<HashMap.Entry<String, Socket>> entries = socketList.entrySet().iterator();
							while (entries.hasNext()) {
								HashMap.Entry<String, Socket> entry = entries.next();

								if ((entry.getKey()).equals(sendName)) {
									Socket s1 = entry.getValue();
									OutputStream writer = s1.getOutputStream();
									writer.write((TALK_START+sendName + ":" + message+TALK_END).getBytes());
									writer.flush();
								}

								if ((entry.getKey()).equals(reseverName)) {
									Socket s2 = entry.getValue();
									OutputStream writer = s2.getOutputStream();
									writer.write((TALK_START+sendName + ":" + message+TALK_END).getBytes());
									writer.flush();
								}

							}
							sb.delete(talkStart, talkEnd + TALK_END.length());
							n=0;
						} else {
							readToBuffer(is, sb);
						}
					}
				}
				readExplain(is, sb);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readExplain(InputStream is, StringBuffer sb) {
		try {
			int imageStart;
			int end;
			int textEnd;
			if (((textEnd = sb.indexOf(TEXT_END)) >= 0) && (end = sb.indexOf(FILE_END)) < 0) {
				String tempText = sb.substring(0, textEnd);
				tempText = new String(tempText.getBytes(ISO_ENCODE), DEFAULT_ENCODE);
				if (!tempText.equals("")) {
					try {
						doc.insertString(doc.getLength(), tempText, attr);
						cursorLocation += tempText.length();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					sendAll(tempText + TEXT_END);
				}
				sb.delete(0, textEnd + TEXT_END.length());
			} else if ((imageStart = sb.indexOf(FILE_START)) >= 0) {
				if (((textEnd = sb.indexOf(TEXT_END)) >= 0) && (textEnd < imageStart)) {
					String tempText = sb.substring(0, textEnd);
					tempText = new String(tempText.getBytes(ISO_ENCODE), DEFAULT_ENCODE);
					if (!tempText.equals("")) {
						sendAll(tempText + TEXT_END);
						try {
							doc.insertString(doc.getLength(), tempText, attr);
							cursorLocation += tempText.length();
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
					sb.delete(0, textEnd + TEXT_END.length());
				} else {
					String tempText = sb.substring(0, imageStart);
					tempText = new String(tempText.getBytes(ISO_ENCODE), DEFAULT_ENCODE);
					if (!tempText.equals("")) {
						sendAll(tempText + TEXT_END);
						try {
							doc.insertString(doc.getLength(), tempText, attr);
							cursorLocation += tempText.length();
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
					sb.delete(0, imageStart + FILE_START.length());
					sendFile(is, sb);
				}
			} else {
				readToBuffer(is, sb);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeImage(InputStream is, FileOutputStream fos, long length) throws Exception {
		byte[] imageByte = new byte[1024];
		int oneTimeReadLength;

		for (long readLength = 0; readLength < length;) {
			if (readLength + imageByte.length <= length) {
				oneTimeReadLength = is.read(imageByte);
			} else {
				oneTimeReadLength = is.read(imageByte, 0, (int) (length - readLength));
			}

			if (oneTimeReadLength >= 0) {
				readLength += oneTimeReadLength;
				fos.write(imageByte, 0, oneTimeReadLength);
			}
		}
	}

	public static long bytesToLong(byte[] array) {
		return ((((long) array[0] & 0xff) << 56) | (((long) array[1] & 0xff) << 48) | (((long) array[2] & 0xff) << 40)
				| (((long) array[3] & 0xff) << 32) | (((long) array[4] & 0xff) << 24) | (((long) array[5] & 0xff) << 16)
				| (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff) << 0));
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

	private void readToBuffer(InputStream is, StringBuffer sb) throws Exception {
		int readLength;
		byte[] b = new byte[1024];

		readLength = is.read(b);
		if (readLength >= 0) {
			String s = new String(b, 0, readLength, ISO_ENCODE);
			sb.append(s);
		}
	}

	public void sendFile(InputStream is, StringBuffer sb) {

		try {
			int file_name_end;
			while ((file_name_end = sb.indexOf(FILE_NAME_END)) < 0) {
				readToBuffer(is, sb);
			}
			String file_name = new String(sb.substring(0, file_name_end).getBytes(ISO_ENCODE), DEFAULT_ENCODE);
			sendAll(FILE_START + file_name + FILE_NAME_END);
			sb.delete(0, file_name_end + FILE_NAME_END.length());

			while (sb.length() < 8) {
				readToBuffer(is, sb);
			}
			String imageLengthString = sb.substring(0, 8);
			byte[] imageLengthByteArray = imageLengthString.getBytes(ISO_ENCODE);
			long imageLength = bytesToLong(imageLengthByteArray);

			sb.delete(0, 8);

			byte[] image = sb.toString().getBytes(ISO_ENCODE);
			FileOutputStream fos = new FileOutputStream(new File(".\\server\\" + file_name));

			if (imageLength > image.length) {
				fos.write(image);
				writeImage(is, fos, imageLength - image.length);
				sb.delete(0, sb.length());
			} else {
				fos.write(image, 0, (int) imageLength);
				sb.delete(0, (int) imageLength);
			}
			fos.close();

			int end;
			while ((end = sb.indexOf(FILE_END)) < 0) {
				readToBuffer(is, sb);
			}
			sb.delete(0, end + FILE_END.length());

			String fileFormat = file_name.substring(file_name.lastIndexOf(".") + 1);

			if (fileFormat.equals("png") || fileFormat.equals("jpg")) {
				// 显示图片
				window.dialogueArea.setCaretPosition(cursorLocation);
				window.dialogueArea.insertIcon(new ImageIcon(".\\server\\" + file_name));
				cursorLocation++;
			}

			// 发送文件
			Iterator<OutputStream> its = outPutClientStream.iterator();
			while (its.hasNext()) {
				File imageFile = new File(".\\server\\" + file_name);
				InputStream fileIs = new FileInputStream(imageFile);
				long fileLenth = imageFile.length();

				int lenth;

				OutputStream writer = (OutputStream) its.next();
				byte[] bs = longToBytes(fileLenth);
				writer.write(bs);
				byte[] b = new byte[1024];
				while ((lenth = fileIs.read(b)) > 0) {
					writer.write(b, 0, lenth);
					writer.flush();
				}
				writer.write(FILE_END.getBytes());
				fileIs.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendAll(String str) {
		Iterator<OutputStream> it = outPutClientStream.iterator();

		try {
			while (it.hasNext()) {
				OutputStream writer = (OutputStream) it.next();
				writer.write(str.getBytes());
				writer.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			window.userButton1.setText(str);
		} else if (i == 2) {
			window.userButton2.setText(str);
		} else if (i == 3) {
			window.userButton3.setText(str);
		} else if (i == 4) {
			window.userButton4.setText(str);
		} else if (i == 5) {
			window.userButton5.setText(str);
		} else if (i == 6) {
			window.userButton6.setText(str);
		} else if (i == 7) {
			window.userButton7.setText(str);
		} else if (i == 8) {
			window.userButton8.setText(str);
		} else if (i == 9) {
			window.userButton9.setText(str);
		} else if (i == 10) {
			window.userButton10.setText(str);
		}

	}

	public static void main(String[] args) {
		Server a = new Server();
		a.run();
	}

}
