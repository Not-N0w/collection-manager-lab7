package com.labs.common.core;

import java.io.Serializable;

import com.labs.client.ValueChecker;

/**
 * Класс локации (из тз)
 */
public class Location implements Serializable, Settable, Comparable<Location> {

    private Float x; // Поле не может быть null
    private Float y; // Поле не может быть null
    private Long z; // Поле не может быть null

    /**
     * Конструктор - создание нового объекта c заданными X, Y и Z.
     * 
     * @param x значение X
     * @param y значение Y
     * @param y значение Z
     * @see Location#Location()
     */
    public Location(Float x, Float y, Long z) {
        try {
            setX(x);
            setY(y);
            setZ(z);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(this.getClass() + ": " + exception.getMessage(), exception);
        }
    }

    /**
     * Конструктор - создание нового объекта.
     *
     * @see Location#Location(Float, Float, Long)
     */
    public Location() {
    }

    /**
     * @return знасение X
     */
    public Float x() {
        return this.x;
    }

    /**
     * @return знасение Y
     */
    public Float y() {
        return this.y;
    }

    /**
     * @return знасение Z
     */
    public Long z() {
        return this.z;
    }

    /**
     * Устанавливает новое значение X
     * 
     * @param x новое значение X
     */
    public void setX(Float x) {
        ValueChecker.nullCheck(x, "X");
        this.x = x;
    }

    /**
     * Устанавливает новое значение Y
     * 
     * @param y новое значение Y
     */
    public void setY(Float y) {
        ValueChecker.nullCheck(y, "Y");
        this.y = y;
    }

    /**
     * Устанавливает новое значение Z
     * 
     * @param z новое значение Z
     */
    public void setZ(Long z) {
        ValueChecker.nullCheck(z, "Z");
        this.z = z;
    }

    @Override
    public <T> void set(String fieldName, T in) {
        switch (fieldName) {
            case "X":
                setX((Float) in);
                break;
            case "Y":
                setY((Float) in);
                break;
            case "Z":
                setZ((Long) in);
                break;
            default:
                throw new IllegalArgumentException("Key " + fieldName + " not found.");
        }
    }

    @Override
    public int compareTo(Location other) {
        int result = this.x.compareTo(other.x);
        if (result != 0) return result;

        result = this.y.compareTo(other.y);
        if (result != 0) return result;

        return this.z.compareTo(other.z);
    }

    @Override
    public String toString() {
        String result = "Location ->\n";
        result += "    X: " + String.valueOf(x) + "\n";
        result += "    Y: " + String.valueOf(y) + "\n";
        result += "    Z: " + String.valueOf(z) + "\n";
        return result;
    }
}