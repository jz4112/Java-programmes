package MinesField;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;
    import javax.swing.UIManager;
    import javax.swing.UIManager.LookAndFeelInfo;
    public class Minesfield {
        public static final int SIZE = 16;
        public static final int MINES_NUM = 40;
        private List<Field> fields;
         private MinesFrame view;
        public void initMinefield() {
            fields = new ArrayList<Field>();
            Random r = new Random();
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    fields.add(new Field(x, y));
                }
            }
            for (int i = 0; i < MINES_NUM; i++) {
                int x, y;
                do {
                    x = r.nextInt(SIZE);
                    y = r.nextInt(SIZE);
                } while (isMine(x, y));
                getField(x, y).setMine();
                
                List<Field> arroundList = getAround(x, y);
                for (Field field : arroundList) {
                    if (!field.isMine()) {
                        field.setMineValue(field.getMineValue() + 1);
                    }
                }
            }
        }
        public boolean open(int x, int y) {
            if (isCovered(x, y)) {
                if (isMine(x, y)) {
                    explode();
                    return true;
                }
                setOpended(x, y);
                 view.repaint(x, y);
                if (getField(x, y).getMineValue() == 0) {
                    List<Field> arroundList = getAround(x, y);
                    for (Field field : arroundList) {
                        open(field.getX(), field.getY());
                    }
                }
            }
            return false;
        }
        public boolean openAround(int x, int y) {
            if (isOpended(x, y) && getMineValue(x, y) > 0) {
                List<Field> arroundList = getAround(x, y);
                int markedNum = 0;
                for (Field field : arroundList) {
                    if (field.getStyle() == Field.STYLE_MARKED) {
                        markedNum++;
                    }
                }
                if (markedNum == getMineValue(x, y)) {
                    for (Field field : arroundList) {
                        boolean isExplode =
    open(field.getX(), field.getY());
                        if (isExplode)
                            return true;
                    }
                }
            }
            return false;
        }
        public void mark(int x, int y) {
            if (isCovered(x, y)) {
                setMarked(x, y);
            } else if (isMarked(x, y)) {
                setCovered(x, y);
            }
             view.repaint(x, y);
        }
        public void explode() {
            for (Field field : fields) {
                field.setStyle(Field.STYLE_OPENED);
                 view.repaint(field.getX(), field.getY());
            }
        }
        public Field getField(int x, int y) {
            int index = fields.indexOf(new Field(x, y));
            if (index >= 0) {
                return fields.get(index);
            } else {
                return null;
            }
        }
        public List<Field> getAround(int x, int y) {
            List<Field> list = new ArrayList<Field>();
            for (int m = -1; m <= 1; m++) {
                for (int n = -1; n <= 1; n++) {
                    if (m == 0 && n == 0)
                        continue;
                    int index = fields.indexOf(new Field(x + m, y + n));
                    if (index >= 0) {
                        list.add(fields.get(index));
                    }
                }
            }
            return list;
        }
        public List<Field> getAround(Field field) {
            return getAround(field.getX(), field.getY());
        }
        public int getFieldStyle(int x, int y) {
            return getField(x, y).getStyle();
        }
        public void setFieldStyle(int x, int y, int style) {
            getField(x, y).setStyle(style);
        }
        public int getMineValue(int x, int y) {
            return getField(x, y).getMineValue();
        }
        public boolean isMine(int x, int y) {
            return getField(x, y).isMine();
        }
        public boolean isCovered(int x, int y) {
            return getField(x, y).getStyle() == Field.STYLE_COVERD;
        }
        public boolean isOpended(int x, int y) {
            return getField(x, y).getStyle() == Field.STYLE_OPENED;
        }
        public boolean isMarked(int x, int y) {
            return getField(x, y).getStyle() == Field.STYLE_MARKED;
        }
        public void setCovered(int x, int y) {
            getField(x, y).setStyle(Field.STYLE_COVERD);
        }
        public void setOpended(int x, int y) {
            getField(x, y).setStyle(Field.STYLE_OPENED);
        }
        public void setMarked(int x, int y) {
            getField(x, y).setStyle(Field.STYLE_MARKED);
        }
        public Minesfield() {
             view = new MinesFrame(this);
            initMinefield();
        }
         public void show() {
         view.setVisible(true);
         }
        public static void main(String[] args) throws Exception {
            for (LookAndFeelInfo info :
    UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            Minesfield mines = new Minesfield();
            for (int x = 0; x < Minesfield.SIZE; x++) {
                for (int y = 0; y < Minesfield.SIZE; y++) {
                    mines.setOpended(x, y);
                }
            }
            System.out.println(mines);
            for (int x = 0; x < Minesfield.SIZE; x++) {
                for (int y = 0; y < Minesfield.SIZE; y++) {
                    mines.setCovered(x, y);
                }
            }
            mines.show();
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    sb.append(getField(x, y)).append(" ");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }