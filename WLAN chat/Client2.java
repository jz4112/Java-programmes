package day04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client2 {
	private Socket socket;
	public Client2() {
		try {
			socket = new Socket("localhost", 8088);
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		GetServerInfoHandler handler = new GetServerInfoHandler(socket);
		Thread t = new Thread(handler);
		t.setDaemon(true);
		t.start();
		
		try {
			OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
			PrintWriter writer = new PrintWriter(osw);
			Scanner sc = new Scanner(System.in);
			while (true) {
				writer.println(sc.nextLine());
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Client2 c = new Client2();
		c.start();
	}
	
	class GetServerInfoHandler implements Runnable {
	private Socket socket;
	
	public GetServerInfoHandler(Socket s) {
		socket = s;
	}
		@Override
		public void run() {
			InputStreamReader isr = null;
			try {
				isr = new InputStreamReader(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			BufferedReader reader = new BufferedReader(isr);
			
			while (true) {
				String str = null;
				try {
					str = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(str);
			}
		}
	}
	
	
}
