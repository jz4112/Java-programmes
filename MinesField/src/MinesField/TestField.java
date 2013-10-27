package MinesField;
import org.junit.Test;

    public class TestField {
        @Test
        public void testInit() {
            Minesfield mf = new Minesfield();
            System.out.println(mf);
            for (int x = 0; x < Minesfield.SIZE; x++) {
                for (int y = 0; y < Minesfield.SIZE; y++) {
                    mf.setOpended(x, y);
                }
            }
            System.out.println(mf);
        }
        @Test
        public void testMark() {
        	Minesfield mf = new Minesfield();
            System.out.println(mf);
            mf.mark(3, 5);
            mf.mark(6, 8);
            System.out.println(mf);
        }
        @Test
        public void testExplode() {
        	Minesfield mf = new Minesfield();
            System.out.println(mf);
            mf.explode();
            System.out.println(mf);
        }
    }