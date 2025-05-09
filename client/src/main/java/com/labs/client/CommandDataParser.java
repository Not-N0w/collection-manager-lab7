package com.labs.client;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import com.labs.common.DataContainer;
import com.labs.common.core.Coordinates;
import com.labs.common.core.Location;
import com.labs.common.core.Person;
import com.labs.common.core.Settable;
import com.labs.common.core.Ticket;
import com.labs.common.core.TicketType;

/**
 * Класс-парсер данных комманды.
 */
public class CommandDataParser {
    /** 
     * Поле сканнер
    */
    private Scanner scanner;
    /**
     * Поле флаг: происходит ли чтение из файла 
    */
    private boolean isFileReading = false;

    private Output output;

    /**
     * Конструктор - создание нового объекта
     */
    public CommandDataParser(Scanner scanner, Output output) {
        this.scanner = scanner;
        this.output = output;
    }

    /** Метод отключающий вывод приглашений к вводу в консоль */
    public void noComments() {
        isFileReading = true;
    }

    /** Метод включающий вывод приглашений к вводу в консоль */
    public void allowComments() {
        isFileReading = false;
    }

    /**
     * Метод возвращающая данные ввода определенного типа
     * 
     * @param varName имя заполняемого поля (для формирования Exeption.message)
     * @param type    ожидаемы возвращаемый тип
     */
    private <T> T scannerGet(String varName, Class<T> type) {
        try {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                return null;
            }
            if (input.charAt(0) == '!') {
                return scannerGet(varName, type);
            }

            input = input.replaceAll("\n", "");
            input = input.strip();
            return switch (type.getSimpleName()) {
                case "Double" -> type.cast(Double.parseDouble(input));
                case "Float" -> type.cast(Float.parseFloat(input));
                case "Integer" -> type.cast(Integer.parseInt(input));
                case "Long" -> type.cast(Long.parseLong(input));
                case "Boolean" -> type.cast(Boolean.parseBoolean(input));
                case "String" -> type.cast(input);
                case "LocalDate" -> type.cast(dateParse(input));
                case "TicketType" -> type.cast(( input.matches("-?\\d+") ? TicketType.getById(Integer.parseInt(input)) : TicketType.valueOf(input)));
                default -> throw new IllegalArgumentException("Unsupported type: " + type);
            };
        }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException(varName + " should be " + type.getSimpleName() + "!", exception);
        }
        catch(IllegalArgumentException exception) {
            throw new IllegalArgumentException(varName + " should be VIP(1), USUAL(2), BUDGETARY(3) or CHEAP(4).", exception);
        }
        catch(DateTimeParseException exception) {
            throw new IllegalArgumentException(varName + " should be in format YYYY-MM-DD.", exception);
        }
    }
    /**
     * Метод заполнения поля объекта
     * 
     * @param <T>
     * @param item объект, поле которого нужно заполнить
     * @param fieldName имя заполняемого поля
     * @param comment комментарий к приглашению к вводу
     * @param type тип заполняемого поля
     * @param tabs количество пробелов (для форматирования вывод приглашений к вводу)
     */
    private <T> void fillField(Settable item, String fieldName, String comment, Class<T> type, int tabs) {
        String coloredTypeString = Output.getColoredString(type.getSimpleName(), "purple");
        fieldOut(tabs, coloredTypeString + "/" + fieldName + (comment.isEmpty() ? "" : " (" + comment +")") + ": ");
        try {
            var scanerData = scannerGet(fieldName, type);
            item.set(fieldName, scanerData);
        }
        catch(Exception exception) {
            output.outError(exception.getMessage());
            fillField(item, fieldName, comment, type, tabs);
        }
    }


    /**
     * Метод парсинга координат
     * 
     * @param tabs количество табов (для форматирования приглашений для вввода)
     * @return Возвращает введенные координаты
     * @see Coordinates
     */
    private Coordinates parseCoordinates(int tabs) {
        Coordinates coordinates = new Coordinates();

        fieldOut(tabs, "Coordinates->\n");
        tabs += 1;

        fillField(coordinates, "X", "", Integer.class, tabs);
        fillField(coordinates, "Y", "", Float.class, tabs);

        return coordinates;
    }

    /**
     * Метод парсинга даты
     * 
     * @param dateString дата в строковом представлении ("YYYY-MM-DD")
     * @return Возвращает введенные дату (LocalDate)
     */
    private LocalDate dateParse(String dateString) {
        LocalDate localDate = LocalDate.parse(dateString);
        return localDate;
    }

    /**
     * Метод парсинга локации
     * 
     * @param tabs количество табов (для форматирования приглашений для вввода)
     * @return Возвращает введенную локацию
     * @see Location
     */
    private Location parseLocation(int tabs) {
        Location location = new Location();

        fieldOut(tabs, "Location->\n");
        tabs += 1;

        fillField(location, "X", "", Float.class, tabs);
        fillField(location, "Y", "", Float.class, tabs);
        fillField(location, "Z", "", Long.class, tabs);

        return location;
    }

    /**
     * Метод парсинга персоны
     * 
     * @param tabs количество табов (для форматирования приглашений к ввода)
     * @return Возвращает введенную персону
     * @see Person
     */
    private Person parsePerson(int tabs) {
        Person person = new Person();
        fieldOut(tabs, "Person->\n");
        tabs += 1;

        fillField(person, "Birthday", "YYYY-MM-DD", LocalDate.class, tabs);
        fillField(person, "Weight", "", Double.class, tabs);
        fillField(person, "PassportID", "", String.class, tabs);

        person.setLocation(parseLocation(tabs));
        return person;
    }

    /**
     * Метод вывода приглашений к вводу
     * 
     * @param tabs количество табов
     * @param in   выводимое приглашение
     */
    private void fieldOut(int tabs, String in) {
        if (isFileReading)
            return;
        System.out.print("    ".repeat(tabs) + in);
    }

    /**
     * Служебный метод для пропуска строки при парсинге Ticket
     * 
     * @return Возвращает резултат парсинга Ticket
     * @see Ticket
     */
    private Ticket skipParseTicket() {
        scanner.nextLine();
        return parseTicket();
    }

    /**
     * Метод парсинга билета
     * 
     * @return Возвращает введенный билет
     * @see Ticket
     */
    private Ticket parseTicket() {
        Ticket ticket = new Ticket();
        int tabs = 0;

        fieldOut(tabs, "Ticket->\n");
        tabs += 1;

        fillField(ticket, "Name", "", String.class, tabs);
        ticket.setCoordinates(parseCoordinates(tabs));

        fillField(ticket, "Price", "", Integer.class, tabs);
        fillField(ticket, "Refundable", "", Boolean.class, tabs);
        fillField(ticket, "TicketType", "VIP(1), USUAL(2), BUDGETARY(3), CHEAP(4)", TicketType.class, tabs);

        ticket.setPerson(parsePerson(tabs));
        return ticket;
    }

    /**
     * Метод парсинга id
     * 
     * @throws IllegalArgumentException выкидывается при id == null
     * @return Возвращает введенный id
     */
    private Long parseID() {
        Long result;
        result = scannerGet("ID", Long.class);
        if (result == null) {
            throw new IllegalArgumentException("ID value can't be null!");
        } else if (result <= 0) {
            throw new IllegalArgumentException("ID should be more than 0!");
        }
        return result;
    }

    /**
     * Метод парсинга пути к файлу
     * 
     * @return Возвращает введенный путь к файлу
     */
    private String parseString() {
        String result;
        result = scannerGet("ScriptPath", String.class);
        return result;
    }

    /**
     * Метод парсинга поля refundable
     * 
     * @return Возвращает введенный refundable
     */
    private Boolean parseRefundable() {
        Boolean result;
        result = scannerGet("Refundable", Boolean.class);
        return result;
    }

    /**
     * Основной метод парсинга: выбирает метод парсинга в зависимости от введеной
     * команды, формирует результат.
     * 
     * @return контейнер с данными комманды
     * @param command команда, данные которой необходимо считать
     * @see DataContainer
     * @throws IllegalArgumentException если команда не найдена
     */
    public DataContainer parse(String command) throws IllegalArgumentException {

        DataContainer result = new DataContainer();
        result.setCommad(command);
        switch (command) {
            case "add", "add_if_max", "add_if_min":
                ArrayList<Ticket> tickets = new ArrayList<Ticket>();
                tickets.add(skipParseTicket());
                result.add("tickets", (Object)tickets);
                break;
            case "remove_greater":
                result.add("ticket", skipParseTicket());
                break;
            case "remove_by_id":
                result.add("id", parseID());
                break;
            case "execute_script":
                String str = parseString();
                var lexems = str.split(" ");
                if(lexems.length == 2) {
                    if(lexems[0].equals("-s")) {
                        result.add("param", lexems[0].substring(1));
                        result.add("path", lexems[1]);
                    }
                    break;
                }
                result.add("path", lexems[0]);
                break;
            case "count_greater_than_refundable", "filter_greater_than_refundable":
                result.add("refundable", parseRefundable());
                break;
            case "update":
                result.add("id", parseID());
                result.add("ticket", parseTicket());
                break;
            case "show", "help", "exit", "average_of_price", "info", "clear", "conninfo":
                break;
            default:
                throw new IllegalArgumentException("Command '" + command + "' not found.");
        }
        return result;
    }
}
