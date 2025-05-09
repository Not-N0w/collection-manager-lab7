package com.labs.common.core;

/**
 * Enum - тип билета
 */
public enum TicketType {
    VIP,
    USUAL,
    BUDGETARY,
    CHEAP;

    /**
     * Возвращает объект TicketType по его id
     * 
     * @param id id нужного элемента
     * @return объект TicketType
     */
    public static TicketType getById(Integer id) {
        try {
            return values()[id - 1];
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid index for TicketType.");
        }
    }
}