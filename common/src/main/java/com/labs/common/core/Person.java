package com.labs.common.core;

import java.io.Serializable;
import java.time.LocalDate;

import com.labs.client.ValueChecker;

/**
 * Класс персоны (из тз)
 */
public class Person implements Serializable, Settable,  Comparable<Person> {

    /**
     * Нижняя граница для weight
     */
    private final double weightLimit = 0;

    private java.time.LocalDate birthday; // Поле не может быть null
    private double weight; // Значение поля должно быть больше 0
    private String passportID; // Строка не может быть пустой, Поле может быть null
    private Location location; // Поле может быть null

    public double getWeightLimit() {
        return weightLimit;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public double getWeight() {
        return weight;
    }

    public String getPassportID() {
        return passportID;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Конструктор - создание кового объекта с заданными параметрами birthday,
     * weight, passportID, location.
     * 
     * @param birthday
     * @param weight
     * @param passportID
     * @param location
     * @see Person#Person()
     */
    public Person(java.time.LocalDate birthday, Double weight, String passportID, Location location) {
        try {
            setBirthday(birthday);
            setLocation(location);
            setPassportID(passportID);
            setWeight(weight);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(this.getClass() + ": " + exception.getMessage(), exception);
        }
    }

    /**
     * Конструктор - создание кового объекта.
     *
     * @see Person#Person(java.time.LocalDate, Double, String, Location)
     */
    public Person() {
    }

    /**
     * @return дата дня рождения
     */
    public java.time.LocalDate birthday() {
        return this.birthday;
    }

    /**
     * 
     * @return значение веса
     */
    public double weight() {
        return this.weight;
    }

    /**
     * 
     * @return значение passportID
     */
    public String passportID() {
        return this.passportID;
    }

    /**
     * 
     * @return location
     * @see {@link Location}
     */
    public Location location() {
        return this.location;
    }

    /**
     * Устанавливает дату рождения
     * 
     * @param birthday новая дата рождения
     */
    public void setBirthday(java.time.LocalDate birthday) {
        ValueChecker.nullCheck(birthday, "Birthday");
        this.birthday = birthday;
    }

    /**
     * Устанавливает значение веса
     * 
     * @param weight новое значение веса
     */
    public void setWeight(Double weight) {
        ValueChecker.checkLimits(weight, weightLimit, null, "Weight");
        this.weight = weight;
    }

    /**
     * Устанавливает новое значение ID паспорта
     * 
     * @param passportID значение ID паспорта
     */
    public void setPassportID(String passportID) {
        ValueChecker.nullCheck(passportID, "PassportID");
        ValueChecker.stringEmptyCheck(passportID, "PassportID");
        this.passportID = passportID;
    }

    /**
     * Устанавливает новое значение для location
     * 
     * @param location овое значение location
     */
    public void setLocation(Location location) {
        ValueChecker.nullCheck(location, "Location");

        this.location = location;
    }

    /**
     * Отображает вложенный объект {@link obj} с пробелами в начале
     * 
     * @param obj вложенный объект
     * @return строку-отображение объекта {@link obj} с пробелами в начале каждой
     *         новой строки.
     */
    private String tab(Object obj) {
        String[] objStrings = obj.toString().split("\n");
        String result = "";
        for (String str : objStrings) {
            result += "    " + str + "\n";
        }
        return result;
    }

    @Override
    public <T> void set(String fieldName, T in) {
        switch (fieldName) {
            case "Birthday":
                setBirthday((java.time.LocalDate) in);
                break;
            case "Weight":
                setWeight((Double) in);
                break;
            case "PassportID":
                setPassportID((String) in);
                break;
            case "Location":
                setLocation((Location) in);
                break;
            default:
                throw new IllegalArgumentException("Key " + fieldName + " not found.");
        }
    }
    @Override
    public int compareTo(Person other) {
        int result = this.birthday.compareTo(other.birthday);
        if (result != 0) return result;

        result = Double.compare(this.weight, other.weight);
        if (result != 0) return result;

        result = this.passportID.compareTo(other.passportID);
        if (result != 0) return result;

        result = this.location.compareTo(other.location);
        return result;
    }

    @Override
    public String toString() {
        String result = "Person ->\n";
        result += "    Birthday: " + birthday.toString() + "\n";
        result += "    Weight: " + String.valueOf(weight) + "\n";
        result += "    PassportID: " + passportID + "\n";
        result += tab(location.toString());
        return result;
    }

}