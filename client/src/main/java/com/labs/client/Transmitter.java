package com.labs.client;

import com.labs.common.DataContainer;
import com.labs.common.dataConverter.Deserializer;
import com.labs.common.dataConverter.Serializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Transmitter {

    private SocketAddress socketAddress;
    private SocketChannel socketChannel;
    private final Serializer serializer;

    public Transmitter() {
        this.serializer = new Serializer();
    }


    DataContainer connectionCheck() {
        if (socketChannel == null || !socketChannel.isConnected()) {
            var response = connect();
            response.add("message", "Reconnection: " + response.get("message"));
            return response;
        }
        return null;
    }

    DataContainer connect() {
        DataContainer response = new DataContainer();
        try {
            this.socketAddress = new InetSocketAddress(
                    System.getenv("TO_HOST"),
                    Integer.parseInt(System.getenv("TO_PORT"))
            );
        }
        catch (Exception e) {
            response.add("from", "CLIENT");
            response.add("status", "error");
            response.add("message", "Environment variable TO_HOST or TO_PORT are not set");
            return response;
        }
        response.add("from", "SERVER");
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(socketAddress);
        } catch (Exception e) {
            response.add("status", "error");
            response.add("message", "Connect failed");
            return response;
        }
        response.add("status", "ok");
        response.add("message", "Connected to server");
        return response;
    }

    public DataContainer getResponse() {
        DataContainer response = new DataContainer();
        response.add("from", "CLIENT");


        ByteBuffer buffer;

        try {
            ByteBuffer dataLength = ByteBuffer.allocate(4);
            while (dataLength.hasRemaining()) {
                int bytesRead = socketChannel.read(dataLength);
                if (bytesRead == -1) {
                    throw new IOException("Server closed connection");
                }
            }
            dataLength.flip();
            int length = dataLength.getInt();

            buffer = ByteBuffer.allocate(length);
            while (buffer.hasRemaining()) {
                int bytesRead = socketChannel.read(buffer);
                if (bytesRead == -1) {
                    throw new IOException("Server closed connection");
                }
            }

            DataContainer data = Deserializer.deserialize(buffer.array());

            return data;

        } catch (IOException e) {
            response.add("status", "error");
            response.add("message",  e.getMessage());
            return response;
        } catch (ClassNotFoundException e) {
            response.add("status", "error");
            response.add("message", "Response deserialization error. Invalid class.");
            return response;
        }
    }

    DataContainer send(DataContainer data) {
        DataContainer response = new DataContainer();
        response.add("from", "CLIENT");

        byte[] bytes;
        try {
            bytes = serializer.serialize(data);

        } catch (Exception e) {
            response.add("status", "error");
            response.add("message", e.getMessage());
            return response;
        }

        ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 4);
        buffer.putInt(bytes.length);
        buffer.put(bytes);

        try {
            buffer.flip();
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        } catch (Exception e) {
            response.add("status", "error");
            response.add("message", "Error writing data");
            return response;
        }

        response.add("status", "ok");
        response.add("message", "Data sent successfully");
        return response;
    }
    public String connInfo() {
            if (socketChannel == null) {
                return "SocketChannel is not initialized.";
            }

            if (!socketChannel.isOpen()) {
                return "Connection is closed.";
            }

            try {
                if (socketChannel.isConnected()) {
                    return "Connection is established with " + socketChannel.getRemoteAddress();
                } else if (socketChannel.isConnectionPending()) {
                    return "Connection is in progress...";
                } else {
                    return "Connection is not established.";
                }
            } catch (IOException e) {
                return "Error retrieving connection status: " + e.getMessage();
            }
        }
}
