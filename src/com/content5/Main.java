package com.content5;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // Если не задано ни одного аргумента командной строки -//
        // Продолжать вычисления невозможно, коэффиценты неизвестны
        if(args.length==0) {
            System.out.println("Невозможно табулировать многочлен, для которого не задано ни одного коэффициента!");
            System.exit(-1);}
        // Зарезервировать места в массиве коэффициентов столько, сколько аргументов командной строки
        Double[] coefficients = new Double[args.length];
        int i= 0;
        try{
            // Перебрать аргументы, пытаясь преобразовать их в Double
            for(String arg: args) {
                coefficients[i++] = Double.parseDouble(arg);
            }
        }
        catch(NumberFormatException x) {
            // Если преобразование невозможно -сообщить об ошибке и завершиться
            System.out.println("Ошибка преобразования строки '"+ args[i] + "' в число типа Double");
            System.exit(-2);}
        // Создать экземпляр главного окна, передав ему коэффициенты
        MainFrame frame = new MainFrame(coefficients);
        // Задать действие, выполняемое при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}


