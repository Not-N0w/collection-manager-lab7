package com.labs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionHandler {
    private final int port;
    private ServerSocket serverSocket;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private ExecutorService cashedReadPool;
    private ExecutorService cashedSendPool;
    private ExecutorService fixedProcessPool;
    private boolean running;

    public ConnectionHandler(int port) {
        this.port = port;
        running = true;
    }

    public void init() {
        try {
            this.serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cashedReadPool = Executors.newCachedThreadPool();
        fixedProcessPool = Executors.newFixedThreadPool(10);
        cashedSendPool = Executors.newCachedThreadPool();
    }

    public void run() {
        while (running) {
            try  {
                Socket clientSocket = serverSocket.accept();

                Reader reader = new Reader(clientSocket, cashedSendPool, fixedProcessPool);
                cashedReadPool.submit(reader);
            } catch (IOException e) {
                logger.error("Client accepting failed");
            }
        }
    }

    public void start() {
        init();
        run();
    }

}
