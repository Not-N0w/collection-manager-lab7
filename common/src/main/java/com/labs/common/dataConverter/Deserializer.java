package com.labs.common.dataConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Класс - десериаализует объекты
 */
public class Deserializer {
    /**
     * Десериализует объект
     * 
     * @param <T>   тип объекта, который должен получиться из байтов.
     * @param bytes объект в байтовом представлении
     * @return объект типа T
     * @throws IOException            не удалось считать bytes
     * @throws ClassNotFoundException Не удалось привести к нужному классу.
     */
    public static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis);
        @SuppressWarnings("unchecked")
        T deserialized = (T) in.readObject();
        return deserialized;
    }
}
