package day04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Server {
	private ServerSocket server;
	private ExecutorService threadPool;
	private Vector<PrintWriter> allout;
	private BlockingDeque<String> messageQueue;

	public Server() {
		try {
			server = new ServerSocket(8088);
		
			threadPool = Executors.newCachedThreadPool();
			
			threadPool = Executors.newFixedThreadPool(50);
		
			allout = new Vector<PrintWriter>();
			
			messageQueue = new LinkedBlockingDeque<String>();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void start() {
	
		SendMsgToAllClientsHandler sendThread = new SendMsgToAllClientsHandler();
		Thread t = new Thread(sendThread);
		t.setDaemon(true);
		t.start();
		

		while (true) {
			try {
				System.out.println("等待客户端连接。。");
				Socket socket = server.accept();
				System.out.println("一个客户端连接了！");
				
				ClientHandler handler = new ClientHandler(socket);
				
				threadPool.execute(handler);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Server s = new Server();
		s.start();
	}

	
	public class ClientHandler implements Runnable {
		private Socket client;

		public ClientHandler(Socket client) {
			this.client = client;
		}

		public void run() {
		
			
			PrintWriter writer = null;
			try {
				
				writer = new PrintWriter(client.getOutputStream());
				allout.add(writer);

				InputStreamReader in = new InputStreamReader(
						client.getInputStream());
				BufferedReader reader = new BufferedReader(in);
				String str = null;
				while (true) {
					str = reader.readLine();
					
					messageQueue.offer(str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				synchronized (allout) {
					allout.remove(writer);
				}
			}

		}
	}

	class SendMsgToAllClientsHandler implements Runnable {
		
		@Override
		public void run() {
			while (true) {
				
				if (!messageQueue.isEmpty()) {
						String s = messageQueue.poll();
						
						synchronized (allout) {
							for (PrintWriter writer : allout) {
								writer.println(s);
								writer.flush();
							}
						} 
					
				}
				try {
					if (messageQueue.size() == 0)
						Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
