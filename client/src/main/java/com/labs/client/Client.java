package com.labs.client;

import com.labs.common.DataContainer;
import com.labs.common.user.User;


/**
 * Класс-связка клиента. Через него осуществляется вызов клиента извне, а так же запуск основного цикла приложения. 
 */
public class Client {
    /**
     * Поле с классом, отвечающим за получение данных от пользователя.
    */
    private Input input;
    User user;
    /**
     * Поле с классом, отвечающим за вывод данных.
    */
    private Output output;
    private UserManager userManager;

    /**
     * Поле с классом, отвечающим за обработку данных.
    */
    private DataManager dataManager;

    /**
     * Полу с экземпляром главного цикла.
     */
    private Cycle cycle;
    private Transmitter transmitter;

    public Client() {
        output = new Output();
        transmitter = new Transmitter();
        dataManager = new DataManager(output, transmitter);
        input = new Input(output);
        userManager = new UserManager(input, dataManager, transmitter, output);
        cycle = new Cycle(input,output,dataManager, false, userManager);
    }



    /**
     * Метод, осуществляющий валидацию файла коллекции, отправку этих данных на сервер, запуск главного цикла.
     */
    @SuppressWarnings("unchecked")
    public void run() {
        DataContainer response = transmitter.connect();
        output.responseOut(response);

        userManager.authenticateUser();

        cycle.cycle();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}

