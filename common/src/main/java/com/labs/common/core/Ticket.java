package com.labs.common.core;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.labs.client.ValueChecker;

/**
 * Класс билета (из тз)
 */
public final class Ticket implements Serializable, Comparable<Ticket>, Settable {
    /**
     * Поле - следующий id для объекта {@link Ticket}
     */
    static Long nextID = Long.valueOf(1);

    /**
     * Нижний предел для цены
     */
    private final int priceLimit = 0;

    private Long id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого
                     // поля должно быть уникальным, Значение этого поля должно генерироваться
                     // автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private java.time.LocalDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
                                                  // автоматически
    private int price; // Значение поля должно быть больше 0
    private Boolean refundable; // Поле не может быть null
    private TicketType type; // Поле не может быть null
    private Person person; // Поле может быть null

    /**
     * Конструктор - создание нового объекта с определенными полями name,
     * coordinates, price, refundable, type, person
     * 
     * @param name
     * @param coordinates
     * @param price
     * @param refundable
     * @param type
     * @param person
     * @see Ticket#Ticket();
     */
    public Ticket(String name, Coordinates coordinates, Integer price, Boolean refundable, TicketType type,
            Person person) {
        super();
        try {
            setName(name);
            setCoordinates(coordinates);
            setPrice(price);
            setRfundable(refundable);
            setType(type);
            setPerson(person);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(this.getClass() + ": " + exception.getMessage(), exception);
        }
    }

    /**
     * Конструктор - создание нового объекта с определенными полями name,
     * coordinates, price, refundable, type, person
     * 
     * @see Ticket#Ticket(String, Coordinates, Integer, Boolean, TicketType, Person)
     */
    public Ticket() {
        this.id = nextID++;
        this.creationDate = LocalDateTime.now();
    }

    /**
     * @return id объекта {@link Ticket}
     */
    public Long id() {
        return this.id;
    }

    /**
     * @return имя объекта {@link Ticket}
     */
    public String name() {
        return this.name;
    }

    /**
     * @return координаты объекта {@link Ticket}
     */
    public Coordinates coordinates() {
        return this.coordinates;
    }

    /**
     * @return дату и время создания объекта {@link Ticket}
     */
    public java.time.LocalDateTime creationDate() {
        return this.creationDate;
    }

    /**
     * @return цену объекта {@link Ticket}
     */
    public int price() {
        return this.price;
    }

    /**
     * @return параметр refundable объекта {@link Ticket}
     */
    public Boolean refundable() {
        return this.refundable;
    }

    /**
     * @return тип объекта {@link Ticket}
     * @see {@link TicketType}
     */
    public TicketType type() {
        return this.type;
    }

    /**
     * @return person объекта {@link Ticket}
     * @see {@link Person}
     */
    public Person person() {
        return this.person;
    }

    /**
     * Устанавливает id объекта {@link Ticket}
     * 
     * @param id значение id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Устанавливает имя объекта {@link Ticket}
     * 
     * @param name значение имени
     */
    public void setName(String name) {
        ValueChecker.nullCheck(name, "Name");
        ValueChecker.stringEmptyCheck(name, "Name");
        this.name = name;
    }

    /**
     * Устанавливает координаты объекта {@link Ticket}
     * 
     * @param coordinates значение координат
     */
    public void setCoordinates(Coordinates coordinates) {
        ValueChecker.nullCheck(coordinates, "Coordinates");
        this.coordinates = coordinates;
    }

    /**
     * Устанавливает цену объекта {@link Ticket}
     * 
     * @param id значение цены
     */
    public void setPrice(Integer price) {
        ValueChecker.checkLimits(price, priceLimit, null, "Price");
        this.price = price;
    }

    /**
     * Устанавливает refundable объекта {@link Ticket}
     * 
     * @param refundable значение refundable
     */
    public void setRfundable(Boolean refundable) {
        ValueChecker.nullCheck(refundable, "Refundable");
        this.refundable = refundable;
    }

    /**
     * Устанавливает тип объекта {@link Ticket}
     * 
     * @param type значение типа
     * @see {@link TicketType}
     */
    public void setType(TicketType type) {
        ValueChecker.nullCheck(type, "Type");
        this.type = type;
    }

    /**
     * Устанавливает person объекта {@link Ticket}
     * 
     * @param person значение person
     * @see {@link Person}
     */
    public void setPerson(Person person) {
        ValueChecker.nullCheck(person, "Person");
        this.person = person;
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
    public String toString() {
        String result = "Ticket ->\n";
        result += "    Name: " + name + "\n";
        result += "    ID: " + String.valueOf(id) + "\n";
        result += tab(coordinates.toString());
        result += "    CreationDate: " + creationDate.toString() + "\n";
        result += "    Price: " + String.valueOf(price) + "\n";
        result += "    Refundable: " + String.valueOf(refundable) + "\n";
        result += "    TicketType: " + type.name() + "\n";
        result += tab(person.toString());
        return result;
    }

    @Override
    public <T> void set(String fieldName, T in) {
        switch (fieldName) {
            case "Name":
                setName((String) in);
                break;
            case "Coordiantes":
                setCoordinates((Coordinates) in);
                break;
            case "Price":
                setPrice((Integer) in);
                break;
            case "Person":
                setPerson((Person) in);
                break;
            case "Refundable":
                setRfundable((Boolean) in);
                break;
            case "TicketType":
                setType((TicketType) in);
                break;
            default:
                throw new IllegalArgumentException("Key " + fieldName + " not found.");
        }
    }

    @Override
    public int compareTo(Ticket other) {
        int result = this.name.compareTo(other.name);
        if (result != 0) return result;

        result = this.coordinates.compareTo(other.coordinates);
        if (result != 0) return result;

        result = this.creationDate.compareTo(other.creationDate);
        if (result != 0) return result;

        result = Integer.compare(this.price, other.price);
        if (result != 0) return result;

        result = this.refundable.compareTo(other.refundable);
        if (result != 0) return result;

        result = this.type.compareTo(other.type);
        if (result != 0) return result;

        if (this.person != null && other.person != null) {
            result = this.person.compareTo(other.person);
            if (result != 0) return result;
        } else if (this.person == null && other.person != null) {
            return -1;
        } else if (this.person != null && other.person == null) {
            return 1;
        }

        return 0;
    }

}