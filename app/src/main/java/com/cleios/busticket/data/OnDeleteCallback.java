package com.cleios.busticket.data;

public interface OnDeleteCallback<T> {
    void onDelete(T e);
}