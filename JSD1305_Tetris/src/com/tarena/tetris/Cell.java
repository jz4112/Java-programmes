package com.tarena.tetris;

import java.awt.Image;

public class Cell {
	private int row;
	private int col;
	private Image img;

	public Cell(int row, int col, Image img) {
		super();
		this.row = row;
		this.col = col;
		this.img = img;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}

	public void left() {
		col--;
	}

	public void right() {
		col++;
	}

	public void dorp() {
		row++;
	}

	public String toString() {
		return row + "," + col;
	}
}
