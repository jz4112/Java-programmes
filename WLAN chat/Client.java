package day04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	private Socket socket; 
	
	public Client() {
		try {
			socket = new Socket("localhost"	+ "", 8088);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void start() {
		try {
			GetServerInfoHandler handler = new GetServerInfoHandler(socket);
			Thread t = new Thread(handler);
			t.setDaemon(true);
			t.start();
			
			
			OutputStream out = socket.getOutputStream();
			
			PrintWriter pw = new PrintWriter(out);
			
			
			Scanner scanner = new Scanner(System.in);
			String nickName = "";
			while(nickName.length() < 2) {
				System.out.println("Ոݔ�������ǷQ(����2λ)��");
				nickName  = scanner.nextLine().trim();
			}
			System.out.println("���" + nickName + "�� �_ʼ�����");
			while (true) {
				pw.println(nickName +": "+scanner.nextLine());
				pw.flush();
			}
			} catch(Exception e) {
			e.printStackTrace();
		}
	
	}
	public static void main(String[] args) {
		Client c = new Client();
		c.start();
	}
	class GetServerInfoHandler implements Runnable {
		
		private Socket socket;
		
		public GetServerInfoHandler(Socket s) {
			socket = s;
		}
		@Override
		public void run() {
		try {
			InputStream in = socket.getInputStream();	
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			while (true){
			String str= br.readLine();
			System.out.println(str);
		}	
		} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		 
	}

}
