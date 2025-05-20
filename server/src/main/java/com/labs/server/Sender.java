package com.labs.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Sender implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private final Socket clientSocket;
    private final byte[] response;

    public Sender(byte[] response, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.response = response;
    }

    public void run() {
        try {
            logger.info("Starting sending response");
            OutputStream outputStream = clientSocket.getOutputStream();

            ByteBuffer buffer = ByteBuffer.allocate(4 + response.length);
            buffer.putInt(response.length);
            buffer.put(response);

            outputStream.write(buffer.array());
            outputStream.flush();
            logger.info("Sent response of size {} bytes", response.length);
        } catch (IOException e) {
            logger.error("Failed to send response: {}", e.getMessage(), e);
        }
    }
}
