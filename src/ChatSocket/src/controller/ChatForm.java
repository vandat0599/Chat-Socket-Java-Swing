package Controller;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JEditorPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Label;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

import Model.DataFile;
import SocketHandler.tags.Decode;
import SocketHandler.tags.Encode;
import SocketHandler.tags.Tags;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.border.EmptyBorder;


public class ChatForm {

	private static String URL_DIR = System.getProperty("user.dir");
	private static String TEMP = "/temp/";

	private ChatRoom chat;
	private Socket socketChat;
	private String nameUser = "", nameGuest = "", nameFile = "";
	private JFrame frameChatGui;
	private Label textName;
	private JPanel panelMessage;
	private JTextPane txtDisplayChat;
	private Label textState, lblReceive;
	private JButton btnDisConnect, btnSend, btnChoose;
	public boolean isStop = false, isSendFile = false, isReceiveFile = false;
	private JProgressBar progressSendFile;
	private JTextField txtPath;
	private int portServer = 0;
	private JTextField txtMessage;
	private JScrollPane scrollPane;

	public ChatForm(String user, String guest, Socket socket, int port) {
            nameUser = user;
            nameGuest = guest;
            socketChat = socket;
            this.portServer = port;
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        ChatForm window = new ChatForm(nameUser, nameGuest, socketChat, portServer, 0);
                        window.frameChatGui.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
	}

	public static void main(String[] args) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        ChatForm window = new ChatForm();
                        window.frameChatGui.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
	}

	public void updateChat_receive(String msg) {
            appendToPane(txtDisplayChat, "<div class='left' style='width: 40%; background-color: #73AD21;padding: 10px;border-radius: 25px;'>"+ msg +"</div>");
	}

	public void updateChat_send(String msg) {
            appendToPane(txtDisplayChat, "<table class='bang' style='color: black; clear:both; width: 100%;'>"
                            + "<tr align='left'>"
                            + "<td style='width: 59%; '></td>"
                            + "<td style='width: 40%; border: 2px solid #73AD21;padding: 10px;border-radius: 25px;'>" + msg 
                            +"</td> </tr>"
                            + "</table>");
	}
	
	public void updateChat_notify(String msg) {
            appendToPane(txtDisplayChat, "<table class='bang' style='color: white; clear:both; width: 100%;'>"
                            + "<tr align='right'>"
                            + "<td style='width: 59%; '></td>"
                            + "<td style='width: 40%; background-color: #f1c40f;'>" + msg 
                            +"</td> </tr>"
                            + "</table>");
	}
	
	public ChatForm() {
            initialize();
	}

	public ChatForm(String user, String guest, Socket socket, int port, int a)
			throws Exception {
            nameUser = user;
            nameGuest = guest;
            socketChat = socket;
            this.portServer = port;
            initialize();
            chat = new ChatRoom(socketChat, nameUser, nameGuest);
            chat.start();
	}

	private void initialize() {
            File fileTemp = new File(URL_DIR + "/temp");
            if (!fileTemp.exists()) {
                    fileTemp.mkdirs();
            }
            frameChatGui = new JFrame();
            frameChatGui.setTitle("Private Chat");
            frameChatGui.setResizable(false);
            frameChatGui.setBounds(200, 200, 673, 645);
            frameChatGui.getContentPane().setLayout(null);
            frameChatGui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            JLabel lblClientIP = new JLabel("");
            lblClientIP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblClientIP.setBounds(30, 6, 41, 40);
            frameChatGui.getContentPane().add(lblClientIP);

            textName = new Label(nameUser);
            textName.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            textName.setBounds(261, 6, 148, 40);
            frameChatGui.getContentPane().add(textName);
            textName.setText(nameGuest);


            panelMessage = new JPanel();
            panelMessage.setBounds(6, 363, 649, 201);
            panelMessage.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Message"));
            frameChatGui.getContentPane().add(panelMessage);
            panelMessage.setLayout(null);

            txtMessage = new JTextField("");
            txtMessage.setBounds(10, 21, 479, 62);
            panelMessage.add(txtMessage);
            txtMessage.setColumns(10);

            btnSend = new JButton("Send");
            btnSend.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btnSend.setBounds(540, 33, 100, 39);
            panelMessage.add(btnSend);

            btnChoose = new JButton("Chọn File");
            btnChoose.setBounds(540, 115, 100, 36);
            panelMessage.add(btnChoose);
            btnChoose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btnChoose.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System
                                    .getProperty("user.home")));
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int result = fileChooser.showOpenDialog(frameChatGui);
                    if (result == JFileChooser.APPROVE_OPTION) {
                            isSendFile = true;
                            String path_send = (fileChooser.getSelectedFile()
                                            .getAbsolutePath()) ;
                            System.out.println(path_send);
                            nameFile = fileChooser.getSelectedFile().getName();
                            txtPath.setText(path_send);
                    }
                }
            });

            txtPath = new JTextField("");
            txtPath.setBounds(76, 120, 433, 25);
            panelMessage.add(txtPath);
            txtPath.setEditable(false);
            txtPath.setColumns(10);


            Label label = new Label("Path");
            label.setBounds(10, 120, 39, 22);
            panelMessage.add(label);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            //action when press button Send
            btnSend.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (isSendFile)
                        try {
                                chat.sendMessage(Encode.sendFile(nameFile));
                        } catch (Exception e) {
                                e.printStackTrace();
                        }

                    if (isStop) {
                        updateChat_send(txtMessage.getText().toString());
                        txtMessage.setText(""); //reset text Send
                        return;
                    }
                    String msg = txtMessage.getText();
                    if (msg.equals(""))
                        return;
                    try {
                        chat.sendMessage(Encode.sendMessage(msg));
                        updateChat_send(msg);
                        txtMessage.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            txtMessage.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent arg0) {

                }

                @Override
                public void keyReleased(KeyEvent arg0) {

                }

                @Override
                public void keyPressed(KeyEvent arg0) {
                    if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                        String msg = txtMessage.getText();
                        if (isStop) {
                                updateChat_send(txtMessage.getText().toString());
                                txtMessage.setText("");
                                return;
                        }
                        if (msg.equals("")) {
                                txtMessage.setText("");
                                txtMessage.setCaretPosition(0);
                                return;
                        }
                        try {
                                chat.sendMessage(Encode.sendMessage(msg));
                                updateChat_send(msg);
                                txtMessage.setText("");
                                txtMessage.setCaretPosition(0);
                        } catch (Exception e) {
                                txtMessage.setText("");
                                txtMessage.setCaretPosition(0);
                        }
                    }
                }
            });

            btnDisConnect = new JButton("EXIT");
            btnDisConnect.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btnDisConnect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    int result = Tags.show(frameChatGui, "Exit now!!", true);
                    if (result == 0) {
                        try {
                            isStop = true;
                            frameChatGui.dispose();
                            chat.sendMessage(Tags.CHAT_CLOSE_TAG);
                            chat.stopChat();
                            System.gc();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            btnDisConnect.setBounds(540, 6, 113, 40);
            frameChatGui.getContentPane().add(btnDisConnect);

            progressSendFile = new JProgressBar(0, 100);
            progressSendFile.setBounds(170, 577, 388, 14);
            progressSendFile.setStringPainted(true);
            frameChatGui.getContentPane().add(progressSendFile);
            progressSendFile.setVisible(false);

            textState = new Label("");
            textState.setBounds(6, 570, 158, 22);
            textState.setVisible(false);
            frameChatGui.getContentPane().add(textState);

            lblReceive = new Label("Receiving ...");
            lblReceive.setBounds(564, 577, 83, 14);
            lblReceive.setVisible(false);
            frameChatGui.getContentPane().add(lblReceive);

            txtDisplayChat = new JTextPane();
            txtDisplayChat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txtDisplayChat.setEditable(false);
            txtDisplayChat.setContentType( "text/html" );
            txtDisplayChat.setMargin(new Insets(6, 6, 6, 6));
            txtDisplayChat.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
            txtDisplayChat.setBounds(6, 59, 670, 291);
            appendToPane(txtDisplayChat, "<div class='clear' style='background-color:white'></div>"); //set default background

            frameChatGui.getContentPane().add(txtDisplayChat);

            scrollPane = new JScrollPane(txtDisplayChat);
            scrollPane.setBounds(6, 59, 649, 291);
            frameChatGui.getContentPane().add(scrollPane);
	}

	public class ChatRoom extends Thread {
		private Socket connect;
		private ObjectOutputStream outPeer;
		private ObjectInputStream inPeer;
		private boolean continueSendFile = true, finishReceive = false;
		private int sizeOfSend = 0, sizeOfData = 0, sizeFile = 0,
				sizeReceive = 0;
		private String nameFileReceive = "";
		private InputStream inFileSend;
		private DataFile dataFile;

		public ChatRoom(Socket connection, String name, String guest)
				throws Exception {
                    connect = new Socket();
                    connect = connection;
                    nameGuest = guest;
		}

		@Override
		public void run() {
                    super.run();
                    OutputStream out = null;
                    while (!isStop) {
                        try {
                            inPeer = new ObjectInputStream(connect.getInputStream());
                            Object obj = inPeer.readObject();
                            if (obj instanceof String) {
                                String msgObj = obj.toString();
                                if (msgObj.equals(Tags.CHAT_CLOSE_TAG)) {
                                    isStop = true;
                                    Tags.show(frameChatGui, "Disconnected with " + nameGuest, false);
                                    try {	
                                        isStop = true;
                                        frameChatGui.dispose();
                                        chat.sendMessage(Tags.CHAT_CLOSE_TAG);
                                        chat.stopChat();
                                        System.gc();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    connect.close();
                                    break;
                                }
                                if (Decode.checkFile(msgObj)) {
                                    isReceiveFile = true;
                                    nameFileReceive = msgObj.substring(10,
                                                    msgObj.length() - 11);
                                    int result = Tags.show(frameChatGui, nameGuest
                                                    + " send file " + nameFileReceive
                                                    + " to you", true);
                                    if (result == 0) {
                                            File fileReceive = new File(URL_DIR + TEMP
                                                            + "/" + nameFileReceive);
                                            if (!fileReceive.exists()) {
                                                    fileReceive.createNewFile();
                                            }
                                            String msg = Tags.FILE_REQ_ACK_OPEN_TAG
                                                            + Integer.toBinaryString(portServer)
                                                            + Tags.FILE_REQ_ACK_CLOSE_TAG;
                                            sendMessage(msg);
                                    } else {
                                            sendMessage(Tags.FILE_REQ_NOACK_TAG);
                                    }
                                } else if (Decode.checkFeedBack(msgObj)) {
                                    btnChoose.setEnabled(false);

                                    new Thread(new Runnable() {
                                            public void run() {
                                                try {
                                                        sendMessage(Tags.FILE_DATA_BEGIN_TAG);
                                                        updateChat_notify("Sending file: " + nameFile);
                                                        isSendFile = false;
                                                        sendFile(txtPath.getText());
                                                } catch (Exception e) {
                                                        e.printStackTrace();
                                                }
                                            }
                                    }).start();
                                } else if (msgObj.equals(Tags.FILE_REQ_NOACK_TAG)) {
                                    Tags.show(frameChatGui, nameGuest
                                                    + " don't want receive file", false);
                                } else if (msgObj.equals(Tags.FILE_DATA_BEGIN_TAG)) {
                                    finishReceive = false;
                                    lblReceive.setVisible(true);
                                    out = new FileOutputStream(URL_DIR + TEMP
                                                    + nameFileReceive);
                                } else if (msgObj.equals(Tags.FILE_DATA_CLOSE_TAG)) {
                                    updateChat_receive("You received file: " + nameFileReceive + " with size " + sizeReceive + " KB");
                                    sizeReceive = 0;
                                    out.flush();
                                    out.close();
                                    lblReceive.setVisible(false);
                                    new Thread(new Runnable() {

                                            @Override
                                            public void run() {
                                                    showSaveFile();
                                            }
                                    }).start();
                                    finishReceive = true;
                                } else {
                                    String message = Decode.getMessage(msgObj);
                                    updateChat_receive(message);
                                }
                            } else if (obj instanceof DataFile) {
                                    DataFile data = (DataFile) obj;
                                    ++sizeReceive;
                                    out.write(data.data);
                            }
                        } catch (Exception e) {
                            File fileTemp = new File(URL_DIR + TEMP + nameFileReceive);
                            if (fileTemp.exists() && !finishReceive) {
                                    fileTemp.delete();
                            }
                        }
                    }
		}

		
		private void getData(String path) throws Exception {
                    File fileData = new File(path);
                    if (fileData.exists()) {
                            sizeOfSend = 0;
                            dataFile = new DataFile();
                            sizeFile = (int) fileData.length();
                            sizeOfData = sizeFile % 1024 == 0 ? (int) (fileData.length() / 1024)
                                            : (int) (fileData.length() / 1024) + 1;
                            inFileSend = new FileInputStream(fileData);
                    }
		}

		public void sendFile(String path) throws Exception {
                    getData(path);
                    textState.setVisible(true);
                    if (sizeOfData > Tags.MAX_MSG_SIZE/1024) {
                            textState.setText("File is too large...");
                            inFileSend.close();
//				isFileLarge = true;
//				sendMessage(Tags.FILE_DATA_CLOSE_TAG);
                            txtPath.setText("");
                            btnChoose.setEnabled(true);
                            isSendFile = false;
                            inFileSend.close();
                            return;
                    }

                    progressSendFile.setVisible(true);
                    progressSendFile.setValue(0);

                    textState.setText("Sending ...");
                    do {
                            System.out.println("sizeOfSend : " + sizeOfSend);
                            if (continueSendFile) {
                                continueSendFile = false;
//					updateChat_notify("If duoc thuc thi: " + String.valueOf(continueSendFile));
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                            try {
                                                inFileSend.read(dataFile.data);
                                                sendMessage(dataFile);
                                                sizeOfSend++;
                                                if (sizeOfSend == sizeOfData - 1) {
                                                        int size = sizeFile - sizeOfSend * 1024;
                                                        dataFile = new DataFile(size);
                                                }
                                                progressSendFile.setValue((int) (sizeOfSend * 100 / sizeOfData));
                                                if (sizeOfSend >= sizeOfData) {
                                                    inFileSend.close();
                                                    isSendFile = true;
                                                    sendMessage(Tags.FILE_DATA_CLOSE_TAG);
                                                    progressSendFile.setVisible(false);
                                                    textState.setVisible(false);
                                                    isSendFile = false;
                                                    txtPath.setText("");
                                                    btnChoose.setEnabled(true);
                                                    updateChat_notify("File sent completed");
                                                    inFileSend.close();
                                                }
                                                continueSendFile = true;
                                            } catch (Exception e) {
                                                    e.printStackTrace();
                                            }
                                        }
                                }).start();
                            }
                    } while (sizeOfSend < sizeOfData);
		}

		private void showSaveFile() {
			while (true) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System
						.getProperty("user.home")));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showSaveDialog(frameChatGui);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = new File(fileChooser.getSelectedFile()
							.getAbsolutePath() + "/" + nameFileReceive );
					if (!file.exists()) {
						try {
							file.createNewFile();
							Thread.sleep(1000);
							InputStream input = new FileInputStream(URL_DIR
									+ TEMP + nameFileReceive);
							OutputStream output = new FileOutputStream(
									file.getAbsolutePath());
							copyFileReceive(input, output, URL_DIR + TEMP
									+ nameFileReceive);
						} catch (Exception e) {
							Tags.show(frameChatGui, "Your file receive has error!!!",
									false);
						}
						break;
					} else {
						int resultContinue = Tags.show(frameChatGui,
								"File is exists. You want save file?", true);
						if (resultContinue == 0)
							continue;
						else
							break;
					}
				}
			}
		}
		
		
		//void send Message
		public synchronized void sendMessage(Object obj) throws Exception {
			outPeer = new ObjectOutputStream(connect.getOutputStream());
			// only send text
			if (obj instanceof String) {
				String message = obj.toString();
				outPeer.writeObject(message);
				outPeer.flush();
				if (isReceiveFile)
					isReceiveFile = false;
			} 
			// send attach file
			else if (obj instanceof DataFile) {
				outPeer.writeObject(obj);
				outPeer.flush();
			}
		}

		public void stopChat() {
			try {
				connect.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void copyFileReceive(InputStream inputStr, OutputStream outputStr,
			String path) throws IOException {
		byte[] buffer = new byte[1024];
		int lenght;
		while ((lenght = inputStr.read(buffer)) > 0) {
			outputStr.write(buffer, 0, lenght);
		}
		inputStr.close();
		outputStr.close();
		File fileTemp = new File(path);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
	}
	
	// send html to pane
	  private void appendToPane(JTextPane tp, String msg){
	    HTMLDocument doc = (HTMLDocument)tp.getDocument();
	    HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
	    try {
	    	
	      editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
	      tp.setCaretPosition(doc.getLength());
	      
	    } catch(Exception e){
	      e.printStackTrace();
	    }
	  }
}
