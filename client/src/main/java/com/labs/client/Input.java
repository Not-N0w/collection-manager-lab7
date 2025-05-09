package com.labs.client;

import java.io.File;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.labs.common.DataContainer;

/**
 * Класс, реализующий получение пользовательских данных и их первичной обработки
 */
public class Input {
    /**
     *  Поле сканнер
    */
    private Scanner scanner;

    /** 
     * Парсер данных команды 
    */
    private CommandDataParser commandDataParser;

    /** 
     * Поле с классом, отвечающим за вывод данных.
    */
    private Output output;

    /**
     * Конструктор создание нового объекта.
     * 
     * @param output класс вывода данных
     * @see Input#Input(Output, String)
     */
    public Input(Output output) {
        this.output = output;
        scanner = new Scanner(System.in);
        commandDataParser = new CommandDataParser(scanner, output);
    }

    /**
     * Конструктор создание нового объекта.
     * 
     * @param output   класс вывода данных
     * @param filePath путь к файлу получения данных (для исполнения скриптов)
     * @see Input#Input(Output)
     */
    public Input(Output output, String filePath) {
        this.output = output;
        this.output = output;
        scannerInit(filePath);
        commandDataParser = new CommandDataParser(scanner, output);
    }

    /**
     * Метод проверяющий существование сканнера
     * 
     * @return true, если сущесвует, false - в противном случае
     */
    public boolean checkScanner() {
        return (scanner == null ? false : true);
    }

    /**
     * Метод пытающийся инициализировать сканнер
     * 
     * @return true, если сущесвует, false - в противном случае
     */
    private void scannerInit(String filePath) {
        try {
            scanner = new Scanner(new File(filePath.strip()));
        } catch (Exception exception) {
            output.fileNotExistMessage(filePath);
        }
    }

    /**
     * @return команда в строковом представлении
     * @throws NoSuchElementException - если команда не найдена
     */
    public String getCommand() {
        if (!scanner.hasNext()) {
            return "exit";
        }
            
        String command = scanner.next();
        if (command.charAt(0) == '!') {
            return getCommand();
        }
        return command;
    }

    /**
     * Метод, создающий файл коллекции
     * 
     * @return абсолютный путь к файлу коллекции
     * @throws InputMismatchException если не получилось создать файл коллекции
     */
    public String makeCollectionFile() throws InputMismatchException {
        System.out.print("Enter path of file for collection: ");
        String filePath = scanner.next();

        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("File '" + file.getName() + "' created.");
            } catch (Exception exception) {
                throw new InputMismatchException("'" + file.getName() + "' is invalid name.");
            }
        }

        return file.getAbsolutePath();
    }

    /**
     * Метод отключающий комментарии
     */
    public void noComments() {
        commandDataParser.noComments();
    }

    /**
     * Метод включающий комментарии
     */
    public void allowComments() {
        commandDataParser.allowComments();
    }

    /**
     * Метод получающие данные команды в {@link DataContainer} и выводящий
     * информацию при ошибке ввода.
     * 
     * @return данные команды.
     */
    public DataContainer getData(String command) {
        try {
            DataContainer dataContainer = commandDataParser.parse(command);
            return dataContainer;
        } catch (IllegalArgumentException exception) {
            output.outError("Input error: " + exception.getMessage());
        }
        return null;
    }

}