package com.content5;
import javax.swing.table.AbstractTableModel;

public class GornerTableModel1 extends AbstractTableModel {
    private Double[] coefficients;
    private Double from;
    private Double to;
    private Double step;

    public GornerTableModel1(Double from, Double to, Double step, Double[] coefficients) {
        this.from= from;
        this.to= to;
        this.step= step;
        this.coefficients= coefficients;
    }
    public Double getFrom() {
        return from;
    }
    public Double getTo() {
        return to;
    }
    public Double getStep() {
        return step;
    }
    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        return new Double(Math.ceil((to-from)/step)).intValue()+1;
    }
    @Override
    public Object getValueAt(int row, int col) {
        // Вычислить значение X как НАЧАЛО_ОТРЕЗКА + ШАГ*НОМЕР_СТРОКИ
        double x = from+ step*row;
        if(col==0) {
            return x;
        }
        else{
            Double result = 0.0;
            // Вычисление значения в точке по схеме Горнера. // Вспомнить 1-ый курс и реализовать//
            return result;
        }

    }
    @Override
    public String getColumnName(int col) {
        switch(col) {
            case 0:return"Значение X";
            default:return"Значение многочлена";}
    }
    @Override
    public Class<?> getColumnClass(int col) {
        return Double.class;
    }
}