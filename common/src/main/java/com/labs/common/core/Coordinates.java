package com.labs.common.core;

import java.io.Serializable;

import com.labs.client.ValueChecker;

/**
 * Класс координат (из тз)
 */
public class Coordinates implements Serializable, Settable,  Comparable<Coordinates> {

    /**
     * Нижний предел для X
     */
    private final int xLimit = -47;
    /**
     * Нижний предел для Y
     */
    private final float yLimit = -69;
    private int id;
    private int x; // Значение поля должно быть больше -47
    private float y; // Значение поля должно быть больше -69

    /**
     * Конструктор - создание нового объекта c заданными X и Y.
     * 
     * @param x значение X
     * @param y значение Y
     * @see Coordinates#Coordinates()
     */
    public Coordinates(Integer x, Float y) {
        try {
            setX(x);
            setY(y);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(this.getClass() + ": " + exception.getMessage(), exception);
        }
    }

    /**
     * Конструктор - создание нового объекта.
     * 
     * @see Coordinates#Coordinates(Integer, Float)
     */
    public Coordinates() {
        x = 0;
        y = 0;
    }

    /**
     * @return значение X
     */
    public int x() {
        return x;
    }

    /**
     * 
     * @return значение Y
     */
    public double y() {
        return y;
    }

    /**
     * Утанавливает значение X
     * 
     * @param x новое значение X
     */
    public void setX(Integer x) {
        ValueChecker.checkLimits(x, xLimit, null, "X");
        this.x = x;
    }

    /**
     * Утанавливает значение Y
     * 
     * @param y новое значение Y
     */
    public void setY(Float y) {
        ValueChecker.checkLimits(y, yLimit, null, "Y");
        this.y = y;
    }



    public int getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public <T> void set(String fieldName, T in) throws IllegalArgumentException {
        switch (fieldName) {
            case "X":
                setX((Integer) in);
                break;
            case "Y":
                setY((Float) in);
                break;
            default:
                throw new IllegalArgumentException("Key " + fieldName + " not found.");
        }
    }

    @Override
    public int compareTo(Coordinates other) {
        int result = Integer.compare(this.x, other.x);
        if (result != 0) return result;

        return Float.compare(this.y, other.y);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        String result = "Coordinates ->\n";
        result += "    X: " + String.valueOf(x) + "\n";
        result += "    Y: " + String.valueOf(y) + "\n";
        return result;
    }
}