import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class ChatServer {

	ArrayList<PrintWriter> clientOutputStreams ;
	
	public class ClientHandler implements Runnable {

		BufferedReader reader ;
		Socket socket ; 
		
		public ClientHandler(Socket s) {
			try {
				socket = s ; 
				InputStreamReader input = new InputStreamReader(s.getInputStream()) ; 
				reader = new BufferedReader(input) ; 
			} catch(Exception e) {
				e.printStackTrace() ; 
			}
		}
		@Override
		public void run() {
			String message ;
			try {
				while((message = reader.readLine()) != null) {
					System.out.println("read " + message) ;
					tellEveryone(message) ; 
				}
				
			}
			catch(Exception e) {
				e.printStackTrace(); 
			}
		}
		
	}
	
	public static void main(String[] args) {
		new ChatServer().go() ; 
	}
	
	public void go() {
		clientOutputStreams = new ArrayList<PrintWriter>() ;
		try {
			
			ServerSocket serverSock = new ServerSocket(5000) ; 
			while(true) {
				Socket clientSocket = serverSock.accept() ; 
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream()) ;
				clientOutputStreams.add(writer) ; 
				
				Thread thread = new Thread(new ClientHandler(clientSocket)) ;
				thread.start() ; 
				System.out.println("got a connection") ;
			}
			
		} catch( Exception e) {
			e.printStackTrace() ; 
		}
	}
	
	public void tellEveryone(String message) {
		Iterator<PrintWriter> it = clientOutputStreams.iterator() ;
		while(it.hasNext()) {
			try {
				PrintWriter writer = it.next() ;
				writer.println(message) ; 
				writer.flush();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
