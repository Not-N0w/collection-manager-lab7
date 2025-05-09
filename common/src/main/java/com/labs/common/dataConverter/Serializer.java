package com.labs.common.dataConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Класс - сериаализует объекты
 */
public class Serializer {
    /**
     * Cериаализует объекты
     * 
     * @param <T> тип объекта, который необходимо сериализовать
     * @param obj объект, который необходимо сериализовать
     * @return байтовое представления объекта obj
     * @throws IOException
     */
    public static <T extends Serializable> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        out.flush();
        byte[] bytes = bos.toByteArray();
        return bytes;
    }
}
