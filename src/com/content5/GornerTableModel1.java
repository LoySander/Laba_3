package com.content5;
import javax.swing.table.AbstractTableModel;
import java.lang.String;

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
        return 3;
    }

    @Override
    public int getRowCount() {
        return new Double(Math.ceil((to-from)/step)).intValue()+1;
    }
    @Override
    public Object getValueAt(int row, int col) {
        // Вычислить значение X как НАЧАЛО_ОТРЕЗКА + ШАГ*НОМЕР_СТРОКИ
        double x = from+ step*row;
        Double result = 0.0;
        boolean z = false;
        for (int i = 0; i < coefficients.length; i++) {
            result += Math.pow(x, coefficients.length - 1 - i) * coefficients[i];
        }
        if(col==0) {
            return x;
        }
        else if (col == 1){
            return result;

        }
        else {
            String str = Double.toString(result);

            int i = 0;
            while(i < str.length()-3){
                if(str.charAt(i) == str.charAt(i+1) && str.charAt(i+2) == str.charAt(i+3)){
                  z =  true;

                }

            i++;
            }
          return z;
        }

    }
    @Override
    public String getColumnName(int col) {
        switch(col) {
            case 0:return"Значение X";
            case 1 :return"Значение многочлена";
            default:return "Две пары";

        }

    }
    @Override
    public Class<?> getColumnClass(int col) {
        if (col == 0 || col == 1){
        return Double.class;
    }
        else return boolean.class;

    }
}