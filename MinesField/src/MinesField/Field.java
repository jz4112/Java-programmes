package MinesField; 
public class Field {
        public static final int STYLE_COVERD = 1;
        public static final int STYLE_OPENED = 2;
        public static final int STYLE_MARKED = 3;
        private int x;
        private int y;
        private int style = STYLE_COVERD;
        private int mineValue = 0;
        public Field(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public void setStyle(int style) {
            this.style = style;
        }
        public int getStyle() {
            return style;
        }
        public void setMineValue(int mineValue) {
            this.mineValue = mineValue;
        }
        public int getMineValue() {
            return mineValue;
        }
        public void setMine() {
            this.mineValue = -100;
        }
        public boolean isMine() {
            return mineValue == -100;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Field) {
                Field field = (Field) obj;
                return x == field.x && y == field.y;
            } else {
                return false;
            }
        }
        @Override
        public String toString() {
            if (STYLE_COVERD == style) {
                return "O";
            } else if (STYLE_MARKED == style) {
                return "^";
            } else if (STYLE_OPENED == style) {
                if (isMine()) {
                    return "@";
                } else {
                    return "" + mineValue;
                }
            }
            return "";
        }
    }