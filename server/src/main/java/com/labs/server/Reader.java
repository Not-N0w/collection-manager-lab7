package com.labs.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

import java.util.concurrent.ExecutorService;

public class Reader implements Runnable {
    private final Socket clientSocket;
    private static final Logger logger = LoggerFactory.getLogger(Reader.class);
    private final ExecutorService cashedSendPool;
    private final ExecutorService fixedProcessPool;

    public Reader(Socket clientSocket, ExecutorService cashedSendPool, ExecutorService fixedProcessPool) {
        this.clientSocket = clientSocket;
        this.cashedSendPool = cashedSendPool;
        this.fixedProcessPool = fixedProcessPool;
    }


    public void run() {
        logger.info("Starting reading data");
        try (
                InputStream inputStream = clientSocket.getInputStream()
        ) {
            while (!clientSocket.isClosed()) {
                byte[] sizeBuffer = new byte[4];

                int totalRead = 0;
                while (totalRead < 4) {
                    int bytesRead = inputStream.read(sizeBuffer, totalRead, 4 - totalRead);
                    if (bytesRead == -1) {
                        throw new IOException("Stream closed before reading message size");
                    }
                    totalRead += bytesRead;
                }
                ByteBuffer byteSizeBuffer = ByteBuffer.wrap(sizeBuffer);
                int objSize = byteSizeBuffer.getInt();

                logger.info("Server recieved request object size: {}", String.valueOf(objSize));


                byte[] buffer = new byte[objSize]; // serialized item


                totalRead = 0;
                while (totalRead < objSize) {
                    int bytesRead = inputStream.read(buffer, totalRead, objSize - totalRead);
                    if (bytesRead == -1) {
                        throw new IOException("Stream closed before reading message size");
                    }
                    totalRead += bytesRead;
                }

                logger.info("Server recieved object");
                fixedProcessPool.submit(new Processor(buffer, clientSocket, cashedSendPool));
            }
        }
        catch (IOException e) {
            // sth w wrong with input stre
        }
    }

}
