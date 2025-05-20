package com.labs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Server {
    private com.labs.server.TicketController ticketController;
    int port = 1804;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    ConnectionHandler connectionHandler;

    public Server() {
        ticketController = new com.labs.server.TicketController();
        this.connectionHandler = new ConnectionHandler(port);
    }

    public void start() {
        //logger.info("Tickets loaded");
        try {
            connectionHandler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
