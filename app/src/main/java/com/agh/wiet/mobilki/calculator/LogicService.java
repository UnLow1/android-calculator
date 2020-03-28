package com.agh.wiet.mobilki.calculator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LogicService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        LogicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LogicService.this;
        }
    }

    public double add(double number1, double number2) {
        return number1 + number2;
    }

    public double subtract(double number1, double number2) {
        return number1 - number2;
    }

    public double multiply(double number1, double number2) {
        return number1 * number2;
    }

    public double divide(double number1, double number2) {
        return number1 / number2;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
