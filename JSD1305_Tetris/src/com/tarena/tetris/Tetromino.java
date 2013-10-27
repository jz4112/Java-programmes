package com.tarena.tetris;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.Random;

/**
  * 4 types of tetromino: I T S Z J L O
 * 
 */
public abstract class Tetromino {

	public static Image T;
	public static Image I;
	public static Image L;
	public static Image J;
	public static Image S;
	public static Image Z;
	public static Image O;
	
	static{
		//loading tetromino pictures
		Toolkit tk = Toolkit.getDefaultToolkit();
		Class cls  = Tetromino.class;
		T  = tk.createImage(cls.getResource("T.png"));
		L  = tk.createImage(cls.getResource("L.png"));
		S  = tk.createImage(cls.getResource("S.png"));
		I  = tk.createImage(cls.getResource("I.png"));
		J  = tk.createImage(cls.getResource("J.png"));
		Z  = tk.createImage(cls.getResource("Z.png"));
		O  = tk.createImage(cls.getResource("O.png"));
	}
	
	// Tetromino consists of 4 cells
	protected Cell[] cells = new Cell[4];

	protected Offset[] states;// rotation states

	protected class Offset {
		int row0, col0;//**offset is the distance between a cell and central cell
		int row1, col1;
		int row2, col2;
		int row3, col3;

		public Offset(int row0, int col0, int row1, int col1, int row2, int col2,
				int row3, int col3) {
			this.row0 = row0;
			this.col0 = col0;
			this.row1 = row1;
			this.col1 = col1;
			this.row2 = row2;
			this.col2 = col2;
			this.row3 = row3;
			this.col3 = col3;
		}
	}

	/**
	 * cells[0] = new Cell(0, 4, T_COLOR); cells[1] = new Cell(0, 3, T_COLOR);
	 * cells[2] = new Cell(0, 5, T_COLOR); cells[3] = new Cell(1, 4, T_COLOR);
	 * axis = (0,4) 1 2 3 offset = (0, 0, -1, 0, 1, 0, 0,-1), cells[1].row = 0+-1
	 * cells[1].col = 4+0 cells[2].row = 0+1 cells[2].col = 4+0 cells[2].row = 0+0
	 * cells[2].col = 4+-1
	 */
	
	private int index = 2033000;

	
	public void rotateRight() {
		index++;
		Offset offset = states[index % states.length];
		Cell axis = cells[0];/// cell[0] is the central cell
		cells[1].setRow(offset.row1 + axis.getRow());
		cells[1].setCol(offset.col1 + axis.getCol());
		cells[2].setRow(offset.row2 + axis.getRow());
		cells[2].setCol(offset.col2 + axis.getCol());
		cells[3].setRow(offset.row3 + axis.getRow());
		cells[3].setCol(offset.col3 + axis.getCol());
	}

	public void rotateLeft() {
		index--;
		Offset offset = states[index % states.length];
		Cell axis = cells[0];
		cells[1].setRow(offset.row1 + axis.getRow());
		cells[1].setCol(offset.col1 + axis.getCol());
		cells[2].setRow(offset.row2 + axis.getRow());
		cells[2].setCol(offset.col2 + axis.getCol());
		cells[3].setRow(offset.row3 + axis.getRow());
		cells[3].setCol(offset.col3 + axis.getCol());
	}


	public static Tetromino randomTetromino() {
		Random random = new Random();
		int type = random.nextInt(7);// 0~6
		switch (type) {
		case 0:
			return new I();
		case 1:
			return new T();
		case 2:
			return new S();
		case 3:
			return new J();
		case 4:
			return new Z();
		case 5:
			return new L();
		case 6:
			return new O();
		}
		return null;
	}

	//check if a tetromino has a cell which is already taken
	public boolean contains(int row, int col) {
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			if (cell.getRow() == row && cell.getCol() == col) {
				return true;
			}
		}
		return false;
	}

	public void softDrop() {
		for (int i = 0; i < cells.length; i++) {
			cells[i].dorp();
		}
	}

	public void moveLeft() {
		
		for (Cell c: cells) {
			c.left();
		}
	}

	public void moveRight() {
		
		for (Cell cell : cells) {
			cell.right();
		}
	}

	public Cell[] getCells() {
		return cells;
	}
	
	

	public String toString() {
		return Arrays.toString(cells);
	}

}

class I extends Tetromino {
	public I() {
		cells[0] = new Cell(0, 4, I);
		cells[1] = new Cell(0, 3, I);
		cells[2] = new Cell(0, 5, I);
		cells[3] = new Cell(0, 6, I);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 0, 2, 0),
				new Offset(0, 0, 0, -1, 0, 1, 0, 2) };
	}
}

class T extends Tetromino {
	public T() {
		cells[0] = new Cell(0, 4,  T);
		cells[1] = new Cell(0, 3,  T);
		cells[2] = new Cell(0, 5,  T);
		cells[3] = new Cell(1, 4,  T);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 0, 0, -1),
				new Offset(0, 0, 0, 1, 0, -1, -1, 0),
				new Offset(0, 0, 1, 0, -1, 0, 0, 1),
				new Offset(0, 0, 0, -1, 0, 1, 1, 0) };
	}
}

class L extends Tetromino {
	public L() {
		cells[0] = new Cell(0, 4,  L);
		cells[1] = new Cell(0, 3,  L);
		cells[2] = new Cell(0, 5, L);
		cells[3] = new Cell(1, 3,  L);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 0, -1, -1),
				new Offset(0, 0, 0, 1, 0, -1, -1, 1),
				new Offset(0, 0, 1, 0, -1, 0, 1, 1),
				new Offset(0, 0, 0, -1, 0, 1, 1, -1) };
	}
}

class J extends Tetromino {
	public J() {
		cells[0] = new Cell(0, 4,  J);
		cells[1] = new Cell(0, 3,  J);
		cells[2] = new Cell(0, 5,  J);
		cells[3] = new Cell(1, 5,  J);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 0, 1, -1),
				new Offset(0, 0, 0, 1, 0, -1, -1, -1),
				new Offset(0, 0, 1, 0, -1, 0, -1, 1),
				new Offset(0, 0, 0, -1, 0, 1, 1, 1) };
	}
}

class S extends Tetromino {
	public S() {
		cells[0] = new Cell(0, 4, S);
		cells[1] = new Cell(0, 5, S);
		cells[2] = new Cell(1, 3, S);
		cells[3] = new Cell(1, 4, S);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 1, 0, 1),
				new Offset(0, 0, 0, 1, 1, -1, 1, 0) };
	}
}

class Z extends Tetromino {
	public Z() {
		cells[0] = new Cell(1, 4, Z);
		cells[1] = new Cell(0, 3, Z);
		cells[2] = new Cell(0, 4, Z);
		cells[3] = new Cell(1, 5, Z);
		states = new Offset[] { new Offset(0, 0, -1, 1, 0, 1, 1, 0),
				new Offset(0, 0, -1, -1, -1, 0, 0, 1) };
	}
}

class O extends Tetromino {
	public O() {
		cells[0] = new Cell(0, 4, O);
		cells[1] = new Cell(0, 5, O);
		cells[2] = new Cell(1, 4, O);
		cells[3] = new Cell(1, 5, O);
		states = new Offset[] { new Offset(0, 0, 0, 1, 1, 0, 1, 1),
				new Offset(0, 0, 0, 1, 1, 0, 1, 1) };
	}
}
