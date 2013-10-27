package downloadApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DownloadFrame extends JFrame {
	private static final int THREAD_NUM = 4;
	private List<DownloadItem> itemList = new ArrayList<DownloadItem>();
	private transient InnerThread innerThread = new InnerThread();
	private static final String downloadInfoFile = "downloadInfo";

	public DownloadFrame() {
		setSize(500, 400);
		add(createContentPane());
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (itemList != null && itemList.size() > 0) {
					for (DownloadItem item : itemList) {
						if (item.status == DownloadItem.STATUS_START) {
							item.stop();
						}
					}
				}
				save(DownloadFrame.this);
			}
		});
		if (itemList != null && itemList.size() > 0) {
			for (DownloadItem item : itemList) {
				item.setActionListener();
			}
		}
		innerThread = new InnerThread();
		innerThread.start();
	}

	private Component createContentPane() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(new Color(35, 31, 32));
		p.add(BorderLayout.CENTER, createTabPane());
		return p;
	}

	private JPanel createTabPane() {
		JPanel p = new JPanel(new GridLayout(10, 1, 6, 6));
		p.setBackground(new Color(35, 31, 32));
		p.setBorder(new EmptyBorder(8, 8, 8, 8));
		for (int i = 0; i < THREAD_NUM; i++) {
			DownloadItem item = new DownloadItem();
			// label
			JLabel label = new JLabel();
			label.setForeground(Color.WHITE);
			item.label = label;
			// button
			JButton btn = new JButton();
			btn.setForeground(new Color(249, 250, 251));
			btn.setBackground(new Color(220, 31, 36));
			item.button = btn;
			// progressPanel
			ProgressPanel pp = new ProgressPanel();
			item.progressPanel = pp;
		
			p.add(label);
			JPanel p1 = new JPanel(new BorderLayout(6, 6));
			p1.setBackground(new Color(35, 31, 32));
			p1.add(BorderLayout.EAST, btn);
			p1.add(BorderLayout.CENTER, pp);
			p.add(p1);
			item.init();
			item.setActionListener();
			itemList.add(item);
		}
		return p;
	}

	private class ProgressPanel extends JPanel {
		private int online;

		public ProgressPanel() {
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			for (int i = 0; i < online; i++) {
				g.fill3DRect(6 + i * 4, 8, 3, 16, true);
			}
		}
	}

	private class InnerThread extends Thread {
		public InnerThread() {
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				try {
					for (int i = 0; i < itemList.size(); i++) {
						itemList.get(i).update();
					}
					repaint();
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private class DownloadItem implements Serializable {
		private static final int STATUS_INIT = 1; // initial state, 'select' on button
		private static final int STATUS_START = 2; //'stop' on button
		private static final int STATUS_STOP = 3;  //'start' on button
		ProgressPanel progressPanel;
		JLabel label;
		JButton button;
		DownloadInfo info;
		transient DownloadThread thread;
		int status; // current state

		void setActionListener() {
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switch (status) {
					case STATUS_INIT:
						String url = JOptionPane.showInputDialog("please specify download address");
						if (url == null || url.length() == 0)
							break;
						String fileName = JOptionPane
								.showInputDialog("please specify save directory");
						if (fileName == null || url.length() == 0)
							break;
						File file = new File(fileName);
						try {
							file.createNewFile();
							info = new DownloadInfo(url, fileName);
							thread = new DownloadThread(info);
							thread.start();
							start();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(DownloadFrame.this,
									"invalid file directory");
							break;
						}
						break;
					case STATUS_START:
						stop();
						break;
					case STATUS_STOP:
						thread = new DownloadThread(info);
						thread.start();
						start();
						break;
					}
				}
			});
		}

		void init() {
			status = STATUS_INIT;
			button.setText("select");
			label.setText("select a file to be downloaded");
		}

		void start() {
			status = STATUS_START;
			button.setText("stop");
		}

		void stop() {
			status = STATUS_STOP;
			thread.setStop(true);
			button.setText("start");
		}

		void update() {
			if (thread != null) {
				if (thread.isFinish()) {
					init();
					label.setText("downloading has been completed");
				} else {
					if (info.getFileSize() != -1) {
						int percent = (int) (info.getPos() * 100 / info
								.getFileSize());
						progressPanel.online = percent;
						label.setText("file : " + info.getFileName() + " is being downloaded : "
								+ percent + "%");
					} else {
						label.setText("file : " + info.getFileName() + " is being downloaded : ");
					}
				}
			}
		}

		public DownloadItem() {
		}
	}

	public static void main(String[] args) {
		DownloadFrame frame = load();
		if (frame == null) {
			frame = new DownloadFrame();
		}
		frame.init();
		frame.setVisible(true);
	}

	private static void save(DownloadFrame frame) {
		File file = new File(downloadInfoFile);
		System.out.println("save: " + file);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(frame);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("serialisation failed");
		}
	}

	private static DownloadFrame load() {
		File file = new File(downloadInfoFile);
		if (!file.exists()){
			return null;
		}
		else {
			System.out.println("load: " + file);
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			System.out.println(ois.toString());
			DownloadFrame frame = (DownloadFrame) ois.readObject();
			return frame;
		} catch (Exception e) {
			System.out.println("deserialisation failed");
			return null;
		}
	}
}