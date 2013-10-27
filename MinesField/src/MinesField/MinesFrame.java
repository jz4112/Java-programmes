package MinesField;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MinesFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Minesfield minefield;
	private MinePanel[][] minePanels = new MinePanel[Minesfield.SIZE][Minesfield.SIZE];
	private JButton startCmd;
	private MouseAdapter ma = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getSource() instanceof MinePanel) {
				MinePanel mp = (MinePanel) e.getSource();
				if (e.getButton() == MouseEvent.BUTTON1) {
					minefield.open(mp.x, mp.y);
				}
			
				if (e.getClickCount() == 2) {
					minefield.openAround(mp.x, mp.y);
					
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					minefield.mark(mp.x, mp.y);
				}
			}
		}
	};

	public MinesFrame(Minesfield mines) {
		this.minefield = mines;
		setTitle("Mines");
		setSize(400, 450);
		init();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}

	private void init() {
		Container contentPane = getContentPane();
		contentPane.setLayout(null);
		startCmd = new JButton("start");
		startCmd.setBounds(150, 5, 100, 40);
		contentPane.add(startCmd);
		startCmd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				minefield.initMinefield();
				repaint();
			}
		});
		for (int i = 0; i < Minesfield.SIZE; i++) {
			for (int j = 0; j < Minesfield.SIZE; j++) {
				minePanels[i][j] = new MinePanel(i, j);
				contentPane.add(minePanels[i][j]);
				minePanels[i][j].addMouseListener(ma);
			}
		}
	}

	public void repaint(int x, int y) {
		minePanels[x][y].repaint();
	}

	private class MinePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private int x;
		private int y;

		MinePanel(int x, int y) {
			this.x = x;
			this.y = y;
			setBounds(20 + x * 22, 50 + y * 22, 20, 20);
		}

		@Override
		public void paint(Graphics g) {
			if (minefield.isCovered(x, y)) {
				g.setColor(Color.GRAY);
				g.fillRect(1, 1, getWidth() - 1, getHeight() - 1);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			} else if (minefield.isOpended(x, y)) {
				int num = minefield.getMineValue(x, y);
				if (!minefield.isMine(x, y)) {
					g.setColor(Color.WHITE);
					g.fillRect(1, 1, getWidth() - 1, getHeight() - 1);
					g.setColor(Color.BLACK);
					g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
					if (num > 0) {
						g.setColor(Color.BLACK);
						g.setFont(new Font("", Font.BOLD, 11));
						g.drawString("" + num, 7, 14);
					}
				} else {
					g.setColor(Color.RED);
					g.fillRect(1, 1, getWidth() - 1, getHeight() - 1);
					g.setColor(Color.BLACK);
					g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				}
			} else if (minefield.isMarked(x, y)) {
				g.setColor(Color.GRAY);
				g.fillRect(1, 1, getWidth() - 1, getHeight() - 1);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g.setColor(Color.WHITE);
				g.setFont(new Font("", Font.BOLD, 11));
				g.drawString("▲", 4, 14);
			}
		}
	}
}