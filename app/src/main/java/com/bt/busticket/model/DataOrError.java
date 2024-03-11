package com.bt.busticket.model;

public class DataOrError<T, E> {
    public T data;
    public E error;

    public DataOrError(T data, E error) {
        this.data = data;
        this.error = error;
    }

    public DataOrError() {
    }
}