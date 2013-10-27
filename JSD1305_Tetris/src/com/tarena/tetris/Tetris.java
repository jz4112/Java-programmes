package com.tarena.tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;


public class Tetris extends JPanel {
	public static final int ROWS = 20;
	public static final int COLS = 10; 

	public static final int CELL_SIZE = 26;
	private static final int[] SCORE_LEVEL = { 0, 1, 4, 10, 100 };

	public static final int FONT_COLOR = 0x607491;
	public static final int FONT_SIZE = 30;
	
	public static final int WIDTH = 525;
	public static final int HEIGHT = 550;
	
	private static Image background;
	static{
		//loading pictures
		Toolkit tk = Toolkit.getDefaultToolkit();
		Class cls = Tetris.class;
		background = tk.createImage(cls.getResource("tetris.png"));
	}
	
	private Cell[][] wall = new Cell[ROWS][COLS];
	
	private Tetromino tetromino;
	
	private Tetromino nextOne;

	private int score;//accumulative scores
	private int lines;//number of destroyed lines

	private boolean pause = false;
	private boolean gameOver = false;

	private Timer timer;

	
	private void startGameAction() {
		gameOver = false;
		pause = false;
		score = 0;
		lines = 0;
		emptyWall();//set all cells to null
		nextTetromino();//get the 1st tetromino randomly
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				softDropAction();
				repaint();
			}
		}, 500, 500);
	}
	
	
	private void gameOverAction() {
		timer.cancel();
	}
	private void pauseAction() {
		pause = true;
		timer.cancel();
	}

	private void continueAction() {
		pause = false;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				softDropAction();
				repaint();
			}
		}, 500, 500);
	}


	//game over when there is no tetromino coming down
	private boolean gameOver() {
		gameOver = wall[0][4] != null;
		return gameOver;
	}

	public void rotateRightAction() {
		tetromino.rotateRight();
		if (outOfBounds() || coincide()) {
			tetromino.rotateLeft();
		}
	}
	public void rotateLeftAction() {
		tetromino.rotateLeft();
		if (outOfBounds() || coincide()) {
			tetromino.rotateRight();
		}
	}

	public void emptyWall() {
		for (int row = 0; row < ROWS; row++) {
			Arrays.fill(wall[row], null);
		}
	}

	//check if there is any line with full tetromino
	public void destroy() {
		int lines = 0;
		for (int row = 0; row < ROWS; row++) {
			if (fullCells(row)) {
				clearLine(row);
				lines++;
			}
		}
		score += SCORE_LEVEL[lines];
		this.lines += lines;
	}
	
	public void clearLine(int row) {
		for (int i = row; i >= 1; i--) {
			System.arraycopy(wall[i - 1], 0, wall[i], 0, COLS);
		}
		Arrays.fill(wall[0], null);
	}

	public boolean fullCells(int row) {
		Cell[] line = wall[row];
		for (int i = 0; i < line.length; i++) {
			Cell cell = line[i];
			if (cell == null) {
				return false;
			}
		}
		return true;
	}

	public String toString() {//print out the whole wall
		String str = "";
		for (int row = 0; row < ROWS; row++) {
			Cell[] line = wall[row];
			for (int col = 0; col < COLS; col++) {
				Cell cell = line[col];
				if (tetromino.contains(row, col)) {
					str += row + "," + col + " ";
				} else {
					str += cell + " ";
				}
			}
			str += "\n";
		}
		return str;
	}

	//check if can drop each time before dropping each time
	public void softDropAction() {
		if (canDrop()) {
			tetromino.softDrop();
		} else {
			tetrominoLandToWall();
			destroy();
			if (gameOver()) {
				gameOverAction();
				return;
			}
			nextTetromino();
		}
	}

	//if canDrop, drop till land to wall 
	public void hardDorpAction() {
		while (canDrop()) {
			tetromino.softDrop();
		}
		tetrominoLandToWall();
		destroy();
		if (gameOver()) {
			gameOverAction();
			return;
		}
		nextTetromino();
	}

	
	public boolean canDrop() {
		
		Cell[] cells = tetromino.getCells();
		for (Cell cell : cells) {
			//check if cell hits the bottom
			if (cell.getRow() == ROWS - 1) {
				return false;
			}
		}
		// check if next line is null
		for (Cell cell : cells) {
			int row = cell.getRow();
			int col = cell.getCol();
			Cell block = wall[row + 1][col];
			if (block != null) {
				return false;
			}
		}
		return true;
	}

	//land to wall when a tetromino can't drop anymore
	public void tetrominoLandToWall() {
		Cell[] cells = tetromino.getCells();
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			int row = cell.getRow();
			int col = cell.getCol();
			wall[row][col] = cell;
		}
	}

    //update tetromino
	public void nextTetromino() {
		if (nextOne == null) {
			nextOne = Tetromino.randomTetromino();
		}
		tetromino = nextOne;
		nextOne = Tetromino.randomTetromino();
	}


	public void moveLeftAction() {
		tetromino.moveLeft();
		if (outOfBounds() || coincide()) {
			tetromino.moveRight();
		}
	}
	
	public void moveRightAction() {
		tetromino.moveRight();
		if (outOfBounds() || coincide()) {
			tetromino.moveLeft();
		}
	}

	private boolean outOfBounds() {
		for (Cell cell : tetromino.getCells()) {
			int row = cell.getRow();
			int col = cell.getCol();
			if (row >= ROWS || col < 0 || col >= COLS){
				return true;
			}
		}
		return false;
	}

	private boolean coincide() {
		for (Cell cell : tetromino.getCells()) {
			int row = cell.getRow();
			int col = cell.getCol();
			if (row >= 0 && row < ROWS && col >= 0 && col < COLS
					&& wall[row][col] != null) {
				return true;
			}
		}
		return false;
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame("welcome to Tetris");
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		Tetris panel = new Tetris();
		panel.setBorder(new LineBorder(Color.black));
		frame.add(panel);
		frame.setVisible(true);
		panel.action();
	}
 
	private void action() {
		// wall[18][2] = new Cell(18, 2, 0xff0000);
		// nextTetromino();
		repaint(); 
		startGameAction();
		//request focus of all inputs
		this.requestFocusInWindow(); 
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(key== KeyEvent.VK_Q){
					System.exit(0);
				}
				if (gameOver) {
					if (key == KeyEvent.VK_S) {
						startGameAction(); 
					}
					return;
				}
				if (pause) {
					if (key == KeyEvent.VK_C) {
						continueAction();
					}
					return;
				}
				switch (key) {
				case KeyEvent.VK_RIGHT:
					moveRightAction();
					break;
				case KeyEvent.VK_LEFT:
					moveLeftAction();
					break;
				case KeyEvent.VK_DOWN:
					softDropAction();
					break;
				case KeyEvent.VK_UP:
					rotateRightAction();
					break;
				case KeyEvent.VK_SPACE:
					hardDorpAction();
					break;
				case KeyEvent.VK_P:
					pauseAction();
					break;

				}
				 
				repaint();
			}

		});
	}

	

	public void paint(Graphics g) {
		
		g.drawImage(background, 0, 0, this);//image, x, y , image observer
		g.translate(15,15);//translate the origin of the graphics context to the point(x,y) in the current coordinate system
		paintWall(g);
		paintTetromino(g);
		paintNextOne(g);
		paintScore(g);
 
	}


	private void paintScore(Graphics g) {
		int x = 285;
		int y = 160;
		
		Font font = new Font(getFont().getName(), Font.BOLD, FONT_SIZE);
		g.setColor(new Color(FONT_COLOR)); 
		g.setFont(font); 
		
		String str = "SCORE:" + score;
		g.drawString(str, x, y);
		y += 56;
		str = "LINES:" + lines;
		g.drawString(str, x, y);
		str = "[P]Pause";
		if (gameOver) {
			str = "[S]Start";
		}
		if (pause) {
			str = "[C]Continue";
		}  
		y += 56;
		g.drawString(str, x, y);
	}

	private void paintNextOne(Graphics g) {
		if (nextOne == null) {
			return;
		}
		for (Cell cell : nextOne.getCells()) {
			int row = cell.getRow() + 1;
			int col = cell.getCol() + 10;
			int x = col * CELL_SIZE;
			int y = row * CELL_SIZE;
			//g.setColor(new Color(CELL_BORDER_COLOR));
			//g.fillRect(x-2, y-2, CELL_SIZE+3, CELL_SIZE+3);
			//g.setColor(new Color(cell.getColor()));
			//g.fill3DRect(x+1, y+1, CELL_SIZE-2, CELL_SIZE-2, true);
			g.drawImage(cell.getImg(), x-3, y-3, this);
		}
	}

	private void paintTetromino(Graphics g) {
		if (tetromino == null) {
			return;
		}
		for (Cell cell : tetromino.getCells()) {
			int row = cell.getRow();
			int col = cell.getCol();
			int x = col * CELL_SIZE;
			int y = row * CELL_SIZE;
			//g.setColor(new Color(CELL_BORDER_COLOR));
			//g.fillRect(x-2, y-2, CELL_SIZE+3, CELL_SIZE+3);
			//g.setColor(new Color(cell.getColor()));
			//g.fill3DRect(x+1, y+1, CELL_SIZE-2, CELL_SIZE-2, true);
			g.drawImage(cell.getImg(), x-3, y-3, this);
		}
	}
 

	private void paintWall(Graphics g) {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Cell cell = wall[row][col];
				int x = col * CELL_SIZE;
				int y = row * CELL_SIZE;
				if (cell == null) {
					 //g.setColor(new Color(FONT_COLOR));
					 //g.drawRect(x,y,CELL_SIZE, CELL_SIZE);
				} else {
					//g.setColor(new Color(CELL_BORDER_COLOR));
					//g.fillRect(x-2, y-2, CELL_SIZE+3, CELL_SIZE+3);
					//g.setColor(new Color(cell.getColor()));
					//g.fill3DRect(x+1, y+1, CELL_SIZE-2, CELL_SIZE-2, true);
					g.drawImage(cell.getImg(), x-3, y-3, this);
				}
			}
		}
	}
 
}
