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

public class Server2 {

	private ServerSocket s;
	private ExecutorService threadPool;
	private Vector<PrintWriter> allout;
	private BlockingDeque<String> msgQueue;

	public Server2() {
		try {
			s = new ServerSocket(8088);
			threadPool = Executors.newCachedThreadPool();
			allout = new Vector<PrintWriter>();
			msgQueue = new LinkedBlockingDeque<String>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {

		SendMsgToAllClientsHandler h = new SendMsgToAllClientsHandler();
		Thread thread = new Thread(h);
		thread.setDaemon(true);
		thread.start();

		while (true) {
			try {
				System.out.println("connecting...");
				Socket socket = s.accept();
				System.out.println("connection succeed!");
				ClientHandler handler = new ClientHandler(socket);
				threadPool.execute(handler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Server2 s = new Server2();
		s.start();
	}

	class ClientHandler implements Runnable {
		private Socket server;

		public ClientHandler(Socket server) {
			this.server = server;
		}

		public void run() {
			
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(server.getOutputStream());
					allout.add(writer);
					InputStreamReader isr = new InputStreamReader(
							server.getInputStream());
					BufferedReader reader = new BufferedReader(isr);
					while (true) {
						String str = reader.readLine();
						msgQueue.offer(str);
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
				// get a message, print out it for all output stream
				if (msgQueue.peek() != null) {
					String msg = msgQueue.poll();
					synchronized (allout) {
						for (PrintWriter writer : allout) {
							writer.println(msg);
							writer.flush();
						}
					}
				}
				// if the queue is empty, wait for a while to see if it is
				// filled again
				if (msgQueue.size() == 0) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
