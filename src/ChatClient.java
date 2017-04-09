import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


public class ChatClient {

	JTextArea incoming ; 
	JTextField outgoing ; 
	Socket socket ; 
	BufferedReader reader ;
	PrintWriter writer ; 
	
	public static void main(String[] args) {
		new ChatClient().go() ; 
	}
	public void go() {
		JFrame frame = new JFrame("Simple Chat Client") ; 
		
		JPanel mainPanel = new JPanel() ; 
		
		incoming = new JTextArea(30, 25) ; 
		incoming.setLineWrap(true) ; 
		incoming.setWrapStyleWord(true) ; 
		incoming.setEditable(false) ; 
		
		JScrollPane qScroller = new JScrollPane(incoming) ; 
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS) ; 
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) ;
		
		
		outgoing = new JTextField(25) ; 
		
		JButton sendButton = new JButton("Send") ;
		sendButton.addActionListener(new SendButtonListener()) ; 
		
		mainPanel.add(qScroller) ; 
		mainPanel.add(outgoing) ; 
		mainPanel.add(sendButton) ; 
		
		setUpNetworking() ;
		
		Thread readerThread = new Thread(new IncomingReader()) ; 
		readerThread.start() ; 
		
		frame.getContentPane().add(BorderLayout.CENTER , mainPanel) ; 
		frame.setSize(400,500) ; 
		frame.setVisible(true) ; 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ; 
	}
	
	private void setUpNetworking() {
		try {
			socket = new Socket("127.0.0.1",5000) ; 
			InputStreamReader streamReader = new InputStreamReader(socket.getInputStream()) ;
			reader = new BufferedReader(streamReader) ; 
			writer = new PrintWriter(socket.getOutputStream()) ; 
			System.out.println("Networking established") ; 
		}
		catch (IOException e){
			e.printStackTrace() ; 
		}
	}
	
	class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				writer.println(outgoing.getText()) ;
				writer.flush() ; 
			}
			catch (Exception e2) {
				e2.printStackTrace() ; 
			}
			outgoing.setText("") ; 
			outgoing.requestFocus() ;
		}
	}
	
	class IncomingReader implements Runnable {

		@Override
		public void run() {
			String message ;
			try {
				while((message = reader.readLine()) != null) {
					System.out.println("read " + message) ;
					incoming.append(message + "\n") ; 
				}
			}
			catch (Exception e3) {
				e3.printStackTrace() ; 
			}
			
		}
		
	}
}
