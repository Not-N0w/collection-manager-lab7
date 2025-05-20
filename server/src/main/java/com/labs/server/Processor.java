package com.labs.server;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Processor implements Runnable {
    private final byte[] request;
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private final TicketController ticketController;
    private final Socket clientSocket;
    private final ExecutorService cashedSendPool;

    public Processor(byte[] request, Socket clientSocket, ExecutorService cashedSendPool) {
        this.request = request;
        this.ticketController = new TicketController();
        this.clientSocket = clientSocket;
        this.cashedSendPool = cashedSendPool;
    }

    public void run() {
        logger.info("Starting processing data");
        var response = ticketController.process(request);

        logger.info("Data processing completed");
        cashedSendPool.submit(new Sender(response, clientSocket));
    }
}
