package com.labs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class Server {
    private com.labs.server.TicketController ticketController;
    int port = 1804;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    public Server() {

        ticketController = new com.labs.server.TicketController();
    }

    static class Attachment {
        enum State {LENGTH_STATE, MAIN_DATA_STATE}

        public Attachment(State state, ByteBuffer buffer) {
            this.state = state;
            if (state == State.LENGTH_STATE) {
                lengthBuffer = buffer;
            }
            else if (state == State.MAIN_DATA_STATE) {
                dataBuffer = buffer;
            }
        }

        State state;
        ByteBuffer lengthBuffer;
        ByteBuffer dataBuffer;
    }

    private void doAccept(SelectionKey key) throws IOException {
        var serverSocketChannel = (ServerSocketChannel) key.channel();
        var socketChannel = serverSocketChannel.accept();

        if (socketChannel != null) {
            socketChannel.configureBlocking(false);
            Attachment attachment = new Attachment(Attachment.State.LENGTH_STATE, ByteBuffer.allocate(4));
            socketChannel.register(key.selector(), SelectionKey.OP_READ, attachment);
            logger.info("Accepted connection from " + socketChannel.getRemoteAddress());
        }
    }

    private void doRead(SelectionKey key) throws IOException {
        var socketChannel = (SocketChannel) key.channel();
        var attachment = (Attachment) key.attachment();

        try {
            if(attachment.state == Attachment.State.LENGTH_STATE) {
                int bytesRead = socketChannel.read(attachment.lengthBuffer);
                if(bytesRead == -1) {
                    socketChannel.close();
                    key.cancel();
                    return;
                }
                if(!attachment.lengthBuffer.hasRemaining()) {
                    attachment.lengthBuffer.flip();
                    int length = attachment.lengthBuffer.getInt();
                    attachment.dataBuffer = ByteBuffer.allocate(length);
                    attachment.state = Attachment.State.MAIN_DATA_STATE;
                }
            }

            if(attachment.state == Attachment.State.MAIN_DATA_STATE) {
                int bytesRead = socketChannel.read(attachment.dataBuffer);
                if(bytesRead == -1) {
                    socketChannel.close();
                    key.cancel();
                    return;
                }
                if(!attachment.dataBuffer.hasRemaining()) {
                    logger.info("Received data from " + socketChannel.getRemoteAddress());
                    attachment.dataBuffer.flip();
                    byte[] data = attachment.dataBuffer.array();
                    byte[] response = ticketController.process(data);
                    ticketController.saveTickets();
                    logger.info("tickets saved.");
                    ByteBuffer responseBuffer = ByteBuffer.allocate(4 + response.length);
                    responseBuffer.putInt(response.length);
                    responseBuffer.put(response);
                    responseBuffer.flip();
                    key.attach(responseBuffer);
                    key.interestOps(SelectionKey.OP_WRITE);

                }
            }

        } catch (IOException e) {
            try {
                socketChannel.close();
            } catch (IOException ignored) {}
            key.cancel();
            logger.info("Client disconnected (read): " + e.getMessage());
        }
    }

    private void doWrite(SelectionKey key) throws IOException {
        var socketChannel = (SocketChannel) key.channel();
        var buffer = (ByteBuffer) key.attachment();
        try {
            socketChannel.write(buffer);
            if (!buffer.hasRemaining()) {

                Attachment attachment = new Attachment(Attachment.State.LENGTH_STATE, ByteBuffer.allocate(4));
                key.attach(attachment);
                key.interestOps(SelectionKey.OP_READ);
                logger.info("Response sent to " + socketChannel.getRemoteAddress());
            }
        } catch (IOException e) {
            try {
                socketChannel.close();
            } catch (IOException ignored) {}
            key.cancel();
            logger.info("Client disconnected (write): " + e.getMessage());
        }
    }

    public void run() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("Server started on port " + port);

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (var iter = keys.iterator(); iter.hasNext(); ) {
                SelectionKey key = iter.next();
                iter.remove();

                try {
                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        doAccept(key);
                    } else if (key.isReadable()) {
                        doRead(key);
                    } else if (key.isWritable()) {
                        doWrite(key);
                    }
                } catch (CancelledKeyException e) {
                    logger.error("Cancelled key encountered: " + e.getMessage());
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ignored) {}
                    logger.error("Exception while handling key: " + e.getMessage());
                }
            }
        }
    }

    public void start() {
        ticketController.loadTickets();
        logger.info("Tickets loaded");
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
