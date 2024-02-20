package com.cleios.busticket.data;

import com.cleios.busticket.model.DataOrError;

public interface ResultCallback<T, E> {
    void onComplete(DataOrError<T, E> result);
}