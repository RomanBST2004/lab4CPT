import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.geom.Rectangle2D;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI.MouseHandler;

import java.io.*;
import javax.swing.*;

public class FractalExplorer {

    // Размер экрана
    private int size;

    // Ссылка на изображение
    private JImageDisplay image;

    // Ссылка на объект класса Фрактал 
    private FractalGenerator generator;

    // Диапазон в комплексной области
    private Rectangle2D.Double range;

    // Конструктор класса
    public FractalExplorer(int new_size) {
        this.size = new_size;
        range = new Rectangle2D.Double();
        generator = new Mandelbrot();
        generator.getInitialRange(range);
        image = new JImageDisplay(size, size);
    }

    // Инициализация графического интерфейса пользователя
    public void createAndShowGUI() {
        // Инициализация окна
    
        JFrame frame = new JFrame("Mandelbrot Fractal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(size, size);
        frame.setLayout(new BorderLayout());

        // Инициализация элементов

        JButton button_reset = new JButton("Reset");
        JPanel bottom_panel = new JPanel();
        JPanel top_panel = new JPanel();

        // Добавление элементов на окно

        bottom_panel.add(button_reset);
        frame.add(bottom_panel, BorderLayout.SOUTH);
        frame.add(image, BorderLayout.CENTER);
        frame.add(top_panel, BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        //  Кнопка reset

        ActionListener DisplayReset = new ResetDisplay();
        button_reset.addActionListener(DisplayReset);

        // Масштабирование по нажатию мыши

        MouseHandler zoom = new MouseHandler();
        image.addMouseListener(zoom);
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
                else {
                    image.drawPixel(x, y, rgbColor);
                    image.repaint();
                }
            }
        }
    }

    //  Сабкласс, перерисовывающий фрактал
    private class ResetDisplay implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            generator.getInitialRange(range);
            FractalExplorer.this.drawFractal();
        }
    }

    // Сабкласс, мастштабирующий фрактал
    private class MouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            double xCoord = generator.getCoord(range.x, range.x +range.width, size, x);
            double yCoord = generator.getCoord(range.y, range.y +range.height, size, y);
            generator.recenterAndZoomRange(range,xCoord, yCoord, 0.5);
            FractalExplorer.this.drawFractal();
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