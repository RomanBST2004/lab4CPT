import java.awt.geom.Rectangle2D;

// Наследует класс FractalGenerator
public class Mandelbrot extends FractalGenerator {
    public static final int MAX_ITERATIONS_COUNT = 2000;
    // Назначаем более "интересную" для нас зону отображения фракатала
    public void getInitialRange(Rectangle2D.Double r) {
        r.x = -2;
        r.y = -1.5;
        r.width = 3;
        r.height = 3;
    }
    // Итеративный метод вычисления комплексных чисел для фрактала Мандельброта
    public int numIterations(double x, double y) {
        int iterations = 0;
        double z = 0;
        double ix;
        double iy;
        double cx = 0;
        double cy = 0;
        while ((z < 4) && (iterations < MAX_ITERATIONS_COUNT)) {
            ix = cx * cx - cy * cy + x;
            iy = 2 * cx * cy + y;
            cx = ix;
            cy = iy;
            z = cx * cx + cy * cy;
            iterations++;
        }
        if (iterations == MAX_ITERATIONS_COUNT) {
            return -1;
        }
        return iterations;
    }

    public static String getString() {
        return "Mandelbrot";
    }
}