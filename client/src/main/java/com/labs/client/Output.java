package com.labs.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.labs.common.DataContainer;
import com.labs.common.core.Ticket;

/**
 * Класс, реализующий вывод данных
 */
public class Output {
    /** Reset symbol for coloring strings */
    public static final String ANSI_RESET = "\u001B[0m";
    /** Symbol for red color of string */
    public static final String ANSI_RED = "\u001B[31m";
    /** Symbol for green color of string */
    public static final String ANSI_GREEN = "\u001B[32m";
    /** Symbol for yellow color of string */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /** Symbol for blue color of string */
    public static final String ANSI_BLUE = "\u001B[34m";
    /** Symbol for purple color of string */
    public static final String ANSI_PURPLE = "\u001B[35m";
    /** Symbol for cyan color of string */
    public static final String ANSI_CYAN = "\u001B[36m";
    /** Symbol for white color of string */
    public static final String ANSI_WHITE = "\u001B[37m";
    /** 
     * Map with color symbols
     * String - String
     * Example: "red" - "\u001B[31m"
    */
    private static Map<String, String> colors;

    static {
        colors = new HashMap<>();
        colors.put("red", ANSI_RED);
        colors.put("green", ANSI_GREEN);
        colors.put("yellow", ANSI_YELLOW);
        colors.put("purple", ANSI_PURPLE);
        colors.put("cyan", ANSI_CYAN);
        colors.put("white", ANSI_WHITE);
        colors.put("blue", ANSI_BLUE);
    }

    /** 
     * Поле, указывающее на необходимость комментариев 
    */
    private boolean isCommentsAllowed = true;

    /** 
     * Вспомогательное поле, показывающие длинну выводимой строки, без цвета
    */
    private int lengthWithoutColor = 0;

    /** 
     * Метод, отключающий комментарии 
    */
    public void noComments() {
        isCommentsAllowed = false;
    }

    /**
     *  Метод, включающий комментарии
    */
    public void allowComments() {
        isCommentsAllowed = true;
    }

    /**
     * Метод, создающий блок из текста
     * 
     * @param inner  контент блока
     * @param header заголовок блока
     * @return блок в строковом представлении
     */
    private String makeBlock(String inner, String header) {
        String result = "-- " + header + " " + "-".repeat(46 - lengthWithoutColor) + '\n';
        result += inner + '\n';
        result += "-".repeat(50) + '\n';
        return result;
    }

    /**
     * Метод, форматирующий сообщение об ошибке
     * 
     * @param in сообщение об ошибке
     * @return блок с ошибкой в строковом представлении
     */
    private String makeError(String in) {
        lengthWithoutColor = 6;
        return makeBlock(ANSI_RED + in + ANSI_RESET, ANSI_RED + "ERROR" + ANSI_RESET);
    }

    /**
     * Метод, форматирующий сообщение об успехе
     * 
     * @param in сообщение об успехе
     * @return блок с сообщением об успехе в строковом представлении
     */
    private String makeOk(String in) {
        if (!isCommentsAllowed)
            return "";
        lengthWithoutColor = 2;
        return makeBlock(ANSI_GREEN + in + ANSI_RESET, ANSI_GREEN + "OK" + ANSI_RESET);
    }

    /**
     * Метод формирующий сообщение об отсутствии файла
     * 
     * @param filePath путь к файлу
     * @return сообщение об отсутствии файла в строковом представлении
     */
    public String fileNotExistMessage(String filePath) {
        return makeError("File '" + filePath + "' not found.");
    }

    /**
     * Метод, выводящий текст (без \n в конце)
     * 
     * @param out выводимый текст
     */
    public void out(String out) {
        System.out.print(out);
    }

    /**
     * Метод, выводящий сообщение об ожидании команд.
     */
    public void waiting() {
        if (!isCommentsAllowed)
            return;
        out(ANSI_BLUE + "Waiting for commands\n" + ANSI_RESET);
    }

    /**
     * Метод, формирующий блок с ошибкой и выводящий его.
     */
    public void outError(String in) {
        out(makeError(in));
    }

    private void processData(DataContainer response) {
        Object responseData = response.get("data");

        String content = "";
        if (responseData instanceof ArrayList<?>) {
            @SuppressWarnings("unchecked")
            ArrayList<Ticket> tickets = (ArrayList<Ticket>) responseData;
            for (var ticket : tickets) {
                content += "> " + ticket.toString();
            }
        } else if (responseData instanceof Integer) {
            Integer number = (Integer) responseData;
            content = String.valueOf(number);
        } else if (responseData instanceof Long) {
            Long number = (Long) responseData;
            content = String.valueOf(number);
        } else if (responseData instanceof String) {
            String text = (String) responseData;
            content = text;
        }

            String command = response.getCommand();
            lengthWithoutColor = command.length() + 10;
            out(makeBlock(content, ANSI_PURPLE + response.getCommand() + " -> " + "OUTPUT" + ANSI_RESET));

    }
    /**
     * Метод, форматирующий и выводящий ответ с сервера {@link DataContainer}
     * 
     * @param response ответ с сервера
     * @see DataContainer
     */
    public void responseOut(DataContainer response) {
        if (response == null)
            return;
        else if(response.get("status").equals("error")) {
            out(makeError((String) response.get("message")));
        }
        else if (response.get("status").equals("ok")) {
            out(makeOk((String) response.get("message")));
        }

        if (response.get("data") == null) return;
        processData(response);
    }
    public void outDelimetr() {
        out("-".repeat(50) + '\n');
    }
    public void outHeadDelimetr(String header) {
        String result = "-- " + header + " " + "-".repeat(46 - lengthWithoutColor - header.length()) + '\n';
        out(result);
    }
    public static String getColoredString(String in, String color) {
        return colors.get(color) + in + ANSI_RESET;
    }
}


/*
*
* DC :
* server_command_response
*
*
*
*
* */