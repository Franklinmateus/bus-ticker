package com.bt.busticket.data;

import com.bt.busticket.model.DataOrError;

public interface ResultCallback<T, E> {
    void onComplete(DataOrError<T, E> result);
}