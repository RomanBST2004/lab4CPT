import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;

public class FractalExplorer {

    // Размер экрана 
    private int size;

    // Ссылка на изображение 
    private JImageDisplay image;

    // Ссылка на объект фрактал
    private FractalGenerator generator;

    // Показываемый диапазон в комплексной области
    private Rectangle2D.Double range;

    // Конструктор класса
    public FractalExplorer(int new_size) {
        this.size = new_size;
        range = new Rectangle2D.Double();
        generator = new Mandelbrot();
        generator.getInitialRange(range);
        image = new JImageDisplay(size, size);
    }

    // Инициализация графического интерфейса
    public void createAndShowGUI() {
        // Инициализация окна
        JFrame frame = new JFrame("Fractal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(size, size);
        frame.setLayout(new BorderLayout());

        // Инициализация элементов
        JComboBox<FractalGenerator> comboBox = new JComboBox<>();
        JButton button_reset = new JButton("Reset");
        JButton button_save = new JButton("Save");
        JPanel bottom_panel = new JPanel();
        JPanel top_panel = new JPanel();
        JLabel lable = new JLabel("Fractal: ");
        FractalGenerator fractal_mandelbrot = new Mandelbrot();
        FractalGenerator fractal_tricorn = new Tricorn();
        FractalGenerator fractal_burningship = new BurningShip();

        // Добавление элементов на окно
        bottom_panel.add(button_save);
        bottom_panel.add(button_reset);
        top_panel.add(lable);
        top_panel.add(comboBox);
        frame.add(bottom_panel, BorderLayout.SOUTH);
        frame.add(image, BorderLayout.CENTER);
        comboBox.addItem(fractal_mandelbrot);
        comboBox.addItem(fractal_tricorn);
        comboBox.addItem(fractal_burningship);
        frame.add(top_panel, BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        // Действие кнопки reset
        ActionListener DisplayReset = new ResetDisplay();
        button_reset.addActionListener(DisplayReset);

        // Масштабирование по клику мышки
        MouseListener zoom = new ZoomFractal();
        image.addMouseListener(zoom);

        // Выбор фрактала
        ActionListener choose = new ChooseFractal();
        comboBox.addActionListener(choose);

        // Сохранение изображения
        ActionListener save = new SaveImage();
        button_save.addActionListener(save);
    }

    // Отрисовка фрактала
    public void drawFractal() {
        double xCoord;
        double yCoord;
        int x;
        int y;
        int iterations_number;
        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, size, x);
                yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, size, y);
                iterations_number = generator.numIterations(xCoord, yCoord);
                float hue = 0.7f + (float) iterations_number / 200f;
                int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                if (iterations_number == -1){
                    image.drawPixel(x, y, 0);
                    image.repaint();
                }
                else{
                    image.drawPixel(x, y, rgbColor);
                    image.repaint();
                }
            }
        }
    }

    // Подкласс для перерисовки фрактала
    private class ResetDisplay implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            generator.getInitialRange(range);
            FractalExplorer.this.drawFractal();
        }
    }

    // Подкласс для масштабирования фрактала
    private class ZoomFractal extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            double xCoord = FractalGenerator.getCoord(range.x, range.x +range.width, size, x);
            double yCoord = FractalGenerator.getCoord(range.y, range.y +range.height, size, y);
            generator.recenterAndZoomRange(range,xCoord, yCoord, 0.5);
            FractalExplorer.this.drawFractal();
        }
    }

    // Подкласс для переключения фракталов
    private class ChooseFractal implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox<FractalGenerator> target = (JComboBox<FractalGenerator>) e.getSource();
            generator = (FractalGenerator) target.getSelectedItem();
            generator.getInitialRange(range);
            FractalExplorer.this.drawFractal();
        }
    }

   // Подкласс для сохранения изображения
    private class SaveImage implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
            chooser.setFileFilter(filter);
            chooser.setAcceptAllFileFilterUsed(false);
            int userSelection = chooser.showSaveDialog(image);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();
                try {
                    BufferedImage displayImage = image.getImage();
                    javax.imageio.ImageIO.write(displayImage, "png", file);
                }
                catch (Exception exception) {
                    JOptionPane.showMessageDialog(image, exception.getMessage(), "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Точка входа
    public static void main(String[] args) {
        int ScreenSize = 600;
        FractalExplorer fractal = new FractalExplorer(ScreenSize);
        fractal.createAndShowGUI();
        fractal.drawFractal();
    }

}