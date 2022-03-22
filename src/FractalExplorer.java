import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.event.*;
import javax.swing.JFileChooser.*;
import javax.swing.filechooser.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;

public class FractalExplorer {

    // Элементы, которые будут блокироваться при процессе вычисления
    private JComboBox comboBox;
    private JButton button_reset;
    private JButton button_save;

    // Размер экрана 
    private int size;

    // Ссылка на изображение
    private JImageDisplay image;

    // Ссылка на объект фрактал
    private FractalGenerator generator;

    // Показываемый диапазон в комплексной области
    private Rectangle2D.Double range;
    
    // Количество оставшихся строк для подсчёта цвета точек
    private int rowsRemaining;

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
        comboBox = new JComboBox();
        button_reset = new JButton("Reset");
        button_save = new JButton("Save");
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
         enableUI(false);
         rowsRemaining = size;
         for (int x = 0; x < size; x++){
             FractalWorker drawRow = new FractalWorker(x);
             drawRow.execute();
         }
    }

    // Метод для вкл/выкл элементов интерфейса во время вычисления 
    private void enableUI(boolean value) {
        comboBox.setEnabled(value);
        button_reset.setEnabled(value);
        button_save.setEnabled(value);
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
            if (rowsRemaining != 0) {
                return;
            }
            int x = e.getX();
            int y = e.getY();
            double xCoord = generator.getCoord(range.x, range.x +range.width, size, x);
            double yCoord = generator.getCoord(range.y, range.y +range.height, size, y);
            generator.recenterAndZoomRange(range,xCoord, yCoord, 0.5);
            FractalExplorer.this.drawFractal();
        }
    }

    // Подкласс для переключения фракталов
    private class ChooseFractal implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox target = (JComboBox) e.getSource();
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
                String file_name = file.toString();
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

    // Подкласс для многопоточной отрисовки фракталов 
    private class FractalWorker extends SwingWorker<Object, Object> {

        // y-координата вычисляемой строки для потока 
        int yCoordinate;

        // Массив хранения цветов точек  строки
        int[] RGB_arr;

       // Конструкток внутреннего класса, получающий y-координату
        private FractalWorker(int target_row) {
            yCoordinate = target_row;
        }

        // Метод, который выполняет фоновые операции, 
        // вычисляет значения цветов точек для строки
        protected Object doInBackground() {
            
            RGB_arr = new int[size];
            double xCoord;
            double yCoord;
            int iteration;
            for (int i = 0; i < RGB_arr.length; i++) {
                xCoord = generator.getCoord(range.x, range.x + range.width, size, i);
                yCoord = generator.getCoord(range.y, range.y + range.height, size, yCoordinate);
                iteration = generator.numIterations(xCoord, yCoord);
                if (iteration == -1){
                    RGB_arr[i] = 0;
                }
                else {
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    RGB_arr[i] = rgbColor;
                }
            }
            return null;
        }

        // Метод, который рисует данную строку,
        // когда фоновая задача завершена
        protected void done() {
            for (int i = 0; i < RGB_arr.length; i++) {
                image.drawPixel(i, yCoordinate, RGB_arr[i]);
            }
            image.repaint(0, 0, yCoordinate, size, 1);
            rowsRemaining--;
            if (rowsRemaining == 0) {
                enableUI(true);
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