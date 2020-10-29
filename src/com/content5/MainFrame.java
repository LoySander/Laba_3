package com.content5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import javax.swing.*;

public class MainFrame extends JFrame {
    private  static final int WIDTH= 700;
    private static final int HEIGHT= 500;
    // Массив коэффициентов многочлена
    private Double[] coefficients;
    // Объект диалогового окна для выбора файлов
    // Компонент не создаѐтся изначально, т.к. может и не понадобиться
    // пользователю если тот не собирается сохранять данные в файл
    private JFileChooser fileChooser= null;
    // Элемент меню
    private JMenuItem saveToTextMenuItem;
    private JMenuItem informationItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;
    // Поля ввода для считывания значений переменных
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;
    private Box hBoxResult;
    private  Box hBoxRange;
    // Визуализатор ячеек таблицы
    private GornerTableCellRenderer renderer= new GornerTableCellRenderer();
    // Модель данных с результатами вычислений
    private GornerTableModel1 data;


    public MainFrame(Double[] coefficients) {
        super("Табулирование многочлена на отрезке по схеме Горнера");
        this.coefficients = coefficients;
        // Установить размеры окна
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        // Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        // Создать меню
        JMenuBar menuBar = new JMenuBar();
        // Установить меню в качестве главного меню приложения
        setJMenuBar(menuBar);
        // Добавить в меню пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        // Добавить его в главное меню
        menuBar.add(fileMenu);
        // Создать пункт меню "Таблица"
        JMenu tableMenu = new JMenu("Таблица");
        // Добавить его в главное меню
        menuBar.add(tableMenu);

        JMenu referenceMenu = new JMenu("Справка"); //Создаю пункт Справка и добавляю его в меню
        menuBar.add(referenceMenu);

        JMenu aboutMenu = new JMenu("О программе");
        referenceMenu.add(aboutMenu);

        // создаю новое действие
        Action aboutProgram = new AbstractAction("Сведение об авторе") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Box information=Box.createVerticalBox();
                JLabel author = new JLabel("Мазур Станислав Валерьевич");
                JLabel group = new JLabel("группа 6");
                information.add(Box.createVerticalGlue());
                information.add(author);
                information.add(Box.createVerticalStrut(10));
                information.add(group);
                information.add(Box.createVerticalGlue());
                JOptionPane.showMessageDialog(MainFrame.this,
                        information, "" +
                                "Сведение об авторе", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("I.jpg"));

            }
        };
        informationItem=aboutMenu.add(aboutProgram);
        informationItem.setEnabled(true);


        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {

                if (fileChooser == null) {
                    // Если диалоговое окно "Открыть файл" ещѐ не создано,// то создать его
                    fileChooser = new JFileChooser();
                    // и инициализировать текущей директорией
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                    // Если результат его показа успешный, сохранить данные в // текстовыйфайл
                    saveToTextFile(fileChooser.getSelectedFile());
            }
        };

        // Добавить соответствующий пункт подменю в меню "Файл"
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
        // По умолчанию пункт меню является недоступным(данных ещё нет)
        saveToTextMenuItem.setEnabled(false);
        Action saveToGraphicsAction= new AbstractAction("Сохранить данные для построения графика") {
            public void actionPerformed(ActionEvent event)
         {
             if(fileChooser==null) {
            fileChooser= new JFileChooser();// и инициализировать текущей директорией
            fileChooser.setCurrentDirectory(new File("."));}
            // Показать диалоговое окно
            if(fileChooser.showSaveDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION);
            // Если результат его показа успешный, // сохранить данные в двоичный файл
            saveToGraphicsFile(fileChooser.getSelectedFile());
        }};
    // Добавить соответствующий пункт подменю в меню "Файл"
    saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
    saveToGraphicsMenuItem.setEnabled(false);

        // Создать новое действие по поиску значений многочлена
        Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
                // Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска", "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                // Установить введенное значение в качестве иголки в визуализаторе
                renderer.setNeedle(value);// Обновить таблицу
                getContentPane().repaint();
            }
        };
        // Добавить действие в меню "Таблица"
        searchValueMenuItem = tableMenu.add(searchValueAction);
        // По умолчанию пункт меню является недоступным (данных ещё нет)
        searchValueMenuItem.setEnabled(false);

        JLabel labelForFrom= new JLabel("X изменяется на интервале от:");
        // Создать текстовое поле для ввода значения длиной в 10 символов // со значением по умолчанию 0.0
        textFieldFrom= new JTextField("0.0", 10);
        // Установить максимальный размер равный предпочтительному, чтобы // предотвратить увеличение размера поля ввода
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());// Создать подпись для ввода левой границы отрезка
        JLabel labelForTo = new JLabel("до:");// Создать текстовое поле для ввода значения длиной в 10 символов // со значением по умолчанию 1.0
        textFieldTo= new JTextField("1.0", 10);// Установить максимальный размер равный предпочтительному, чтобы // предотвратить увеличение размера поля ввода
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
        // Создать подпись для ввода шага табулирования
        JLabel labelForStep = new JLabel("с шагом:");
        // Создать текстовое поле для ввода значения длиной в 10 символов // со значением по умолчанию 1.0
        textFieldStep= new JTextField("0.1", 10);// Установить максимальный размер равный предпочтительному, чтобы // предотвратить увеличение размера поля ввода
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());// Создать контейнер 1 типа "коробка с горизонтальной укладкой"
        hBoxRange= Box.createHorizontalBox();// Задать для контейнера тип рамки "объѐмная"
        hBoxRange.setBorder(BorderFactory.createBevelBorder(1));// Добавить "клей" C1-H1
        hBoxRange.add(Box.createHorizontalGlue());// Добавить подпись "От"
        hBoxRange.add(labelForFrom);// Добавить "распорку" C1-H2
        hBoxRange.add(Box.createHorizontalStrut(10));// Добавить поле ввода "От"
        hBoxRange.add(textFieldFrom);// Добавить "распорку" C1-H3
        hBoxRange.add(Box.createHorizontalStrut(20));// Добавить подпись "До"
        hBoxRange.add(labelForTo);// Добавить "распорку" C1-H4
        hBoxRange.add(Box.createHorizontalStrut(10));// Добавить поле ввода "До"
        hBoxRange.add(textFieldTo);// Добавить "распорку" C1-H5
        hBoxRange.add(Box.createHorizontalStrut(20));// Добавить подпись "с шагом"
        hBoxRange.add(labelForStep);// Добавить "распорку" C1-H6
        hBoxRange.add(Box.createHorizontalStrut(10));// Добавить поле для ввода шага табулирования
        hBoxRange.add(textFieldStep);// Добавить "клей" C1-H7
        hBoxRange.add(Box.createHorizontalGlue());
        hBoxRange.setPreferredSize(new Dimension(
               new Double(hBoxRange.getMaximumSize().getWidth()).intValue(), new Double(hBoxRange.getMinimumSize().getHeight()).intValue()*2));
        // Установить область в верхнюю (северную) часть компоновки
        getContentPane().add(hBoxRange, BorderLayout.NORTH);


        // Создать кнопку "Вычислить"
        JButton buttonCalc = new JButton("Вычислить");
        // Задать действие на нажатие "Вычислить" и привязать к кнопке
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent ev){
                try {// Считать значения границ отрезка, шага из полей ввода
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());
                    // На основе считанных данных создать модельтаблицы
                    data = new GornerTableModel1(from, to, step, MainFrame.this.coefficients);
                    // Создать новый экземпляр таблицы
                    JTable table = new JTable(data);
                    // Установить в качестве визуализатора ячеек для класса // Double разработанный визуализатор
                    table.setDefaultRenderer(Double.class, renderer);
                    // Установить размер строки таблицы в 30 пикселов (рис. 3.4)
                    table.setRowHeight(30);
                    // Удалить все вложенные элементы из контейнера
                    hBoxResult.removeAll();
                    // Добавить в hBoxResultтаблицу, "обѐрнутую" в панель с // полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
                    // Обновить область содержания главного окна
                    getContentPane().validate();
                    // Пометить ряд элементов меню как доступных
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                } catch (NumberFormatException ex) {
                    // В случае ошибки преобразования показать сообщение об ошибке
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в формате записи числа с плавающей точкой", "Ошибочныйформатчисла", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        // Создать кнопку "Очистить поля"
        JButton buttonReset = new JButton("Очистить поля");
        // Задать действие на нажатие "Очистить поля" и привязать к кнопке
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {


                // Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                // Удалить все вложенные элементы контейнера
                hBoxResult.removeAll();
                // Добавить в контейнер пустую панель
                hBoxResult.add(new JPanel());
                // Пометить элементы меню как недоступные
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
                // Обновить область содержания главного окна
                getContentPane().validate();
            }
        });
        // Поместить созданные кнопки в контейнер
        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области равным удвоенному
        // минимальному, чтобы при компоновке окна область совсем не сдавили
        hboxButtons.setPreferredSize(new Dimension(new Double(hboxButtons.getMaximumSize().getWidth()).intValue(), new Double(hboxButtons.getMinimumSize().getHeight()).intValue()*2));
        // Разместить контейнер с кнопками в нижней (южной) области компоновки
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);
        // Область для вывода результата пока что пустая
        hBoxResult= Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
        // Установить контейнер hBoxResultв главной области компоновки
        getContentPane().add(hBoxResult, BorderLayout.CENTER);


    }
    protected void saveToGraphicsFile(File selectedFile)
    {
        try{
            // Создать новый байтовый поток вывода, направленный в указанный файл
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
            // Записать в поток вывода попарно значение Xв точке, значение многочлена в точке
            for(int i = 0; i<data.getRowCount(); i++) {
                out.writeDouble((Double)data.getValueAt(i,0));
                out.writeDouble((Double)data.getValueAt(i,1));
            }
            // Закрыть поток вывода
            out.close();
        }
        catch(Exception e) {
            // Исключительную ситуацию "ФайлНеНайден" в данном случае можно не обрабатывать,// так как мы файл создаѐм, а не открываем для чтения
            }}
            protected void saveToTextFile(File selectedFile) {
        try{
            // Создать новый символьный поток вывода, направленный в указанный файл
            PrintStream out = new PrintStream(selectedFile);
            // Записать в поток вывода заголовочные сведения
            out.println("Результаты табулирования многочлена по схеме Горнера");
            out.print("Многочлен: ");
            for(int i=0; i<coefficients.length; i++) {
                out.print(coefficients[i] + "*X^"+ (coefficients.length-i-1));
                if(i!=coefficients.length-1)
                    out.print(" + ");
            }
            out.println("");
            out.println("Интервал от "+ data.getFrom() + " до "+ data.getTo() + " с шагом "+ data.getStep());
            out.println("====================================================");
            // Записать в поток вывода значения в точках
            for(int i = 0; i<data.getRowCount(); i++) {
                out.println("Значение в точке "+ data.getValueAt(i,0) + " равно "+ data.getValueAt(i,1));
            }
            out.close();
        }
        catch( FileNotFoundException e) {// Исключительную ситуацию "ФайлНеНайден" можно не // обрабатывать,так как мы файл создаѐм, а не открываем }}

                }
            }
    }

