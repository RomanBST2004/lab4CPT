import java.awt.geom.Rectangle2D;

public class Tricorn extends FractalGenerator {

    /** Максимальное число итеарций **/
    public static final int MAX_ITERATIONS = 2000;

    /**
    * Метод задает размер области по умолчанию для вычисления фрактала
    **/
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2;
        range.width = 3.5;
        range.height = 3.5;
    }

    /**
    * Метод подсчитывающий количесто итераций для заданной точки
    **/
    public int numIterations(double x, double y) {
        int iterations = 0;
        double z = 0;
        double ix;
        double iy;
        double cx = 0;
        double cy = 0;
        while ((z < 4) && (iterations < MAX_ITERATIONS)) {
            ix = cx * cx - cy * cy + x;
            iy = -2 * cx * cy + y;
            cx = ix;
            cy = iy;
            z = cx * cx + cy * cy;
            iterations++;
        }
        if (iterations == MAX_ITERATIONS) {
            return -1;
        }
        return iterations;
    }

    /**
    * Метод, выводящий название в comboBox
    **/
    public String toString() {
        return "Tricorn";
    }
}