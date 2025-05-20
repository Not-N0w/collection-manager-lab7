package com.labs.client;

import com.labs.common.DataContainer;
import com.labs.common.dataConverter.Deserializer;
import com.labs.common.dataConverter.Serializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Transmitter {

    private InetAddress socketAddress;
    private Socket socket;
    private final Serializer serializer;

    public Transmitter() {
        this.serializer = new Serializer();
    }


    public DataContainer connectionCheck() {
        if (socket == null || !socket.isConnected()) {
            var response = connect();
            response.add("message", "Reconnection: " + response.get("message"));
            return response;
        }
        return null;
    }

    public DataContainer connect() {
        DataContainer response = new DataContainer();
        int port = -1;
        String host = "";
        try {
            host = System.getenv("TO_HOST");
            port = Integer.parseInt(System.getenv("TO_PORT"));
            if(host.equals("") || port == -1) throw new Exception();
        }
        catch (Exception e) {
            response.add("from", "CLIENT");
            response.add("status", "error");
            response.add("message", "Environment variable TO_HOST or TO_PORT are not set");
            return response;
        }
        response.add("from", "SERVER");
        try {
            socket = new Socket(host, port);
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

        try {
            var inputStream = socket.getInputStream();
            byte[] sizeBuffer = new byte[4];

            int totalRead = 0;
            while (totalRead < 4) {
                int bytesRead = inputStream.read(sizeBuffer, totalRead, 4 - totalRead);
                if (bytesRead == -1) {
                    throw new IOException("Stream closed before reading message size");
                }
                totalRead += bytesRead;
            }

            ByteBuffer sizeByteBuffer = ByteBuffer.wrap(sizeBuffer);
            int objSize = sizeByteBuffer.getInt();

            byte[] objBytes = new byte[objSize];

            totalRead = 0;
            while (totalRead < objSize) {
                int bytesRead = inputStream.read(objBytes, totalRead, objSize - totalRead);
                if (bytesRead == -1) {
                    throw new IOException("Stream closed before reading message size");
                }
                totalRead += bytesRead;
            }


            DataContainer data = Deserializer.deserialize(objBytes);
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

    public DataContainer send(DataContainer data) {
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
            socket.getOutputStream().write(buffer.array());
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
        if (socket == null) {
            return "SocketChannel is not initialized.";
        }

        if (socket.isClosed()) {
            return "Connection is closed.";
        }
        if (socket.isConnected()) {
            return "Connection is established with " + socket.getInetAddress().getHostAddress();
        }
        else {
            return "Connection is not established.";
        }
    }
}
