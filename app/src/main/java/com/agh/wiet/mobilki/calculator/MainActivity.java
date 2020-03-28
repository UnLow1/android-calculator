package com.agh.wiet.mobilki.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    LogicService logicService;
    boolean mBound = false;
    EditText number1EditText;
    EditText number2EditText;

    private ServiceConnection logicConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            LogicService.LocalBinder binder = (LogicService.LocalBinder) service;
            logicService = binder.getService();
            mBound = true;
            Toast.makeText(MainActivity.this, "Logic Service Connected!",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            logicService = null;
            mBound = false;
            Toast.makeText(MainActivity.this, "Logic Service Disconnected!",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            this.bindService(new Intent(MainActivity.this, LogicService.class),
                    logicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            mBound = false;
            this.unbindService(logicConnection);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number1EditText = findViewById(R.id.number1EditText);
        number2EditText = findViewById(R.id.number2EditText);
        final TextView resultValueTextView = findViewById(R.id.resultValueTextView);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFormValid()) return;

                double number1 = Double.parseDouble(number1EditText.getText().toString());
                double number2 = Double.parseDouble(number2EditText.getText().toString());
                double result = logicService.add(number1, number2);
                resultValueTextView.setText("" + result);
            }
        });

        Button subtractButton = findViewById(R.id.subtractButton);
        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFormValid()) return;

                double number1 = Double.parseDouble(number1EditText.getText().toString());
                double number2 = Double.parseDouble(number2EditText.getText().toString());
                double result = logicService.subtract(number1, number2);
                resultValueTextView.setText("" + result);
            }
        });

        Button multiplyButton = findViewById(R.id.multiplyButton);
        multiplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFormValid()) return;

                double number1 = Double.parseDouble(number1EditText.getText().toString());
                double number2 = Double.parseDouble(number2EditText.getText().toString());
                double result = logicService.multiply(number1, number2);
                resultValueTextView.setText("" + result);
            }
        });

        Button divideButton = findViewById(R.id.divideButton);
        divideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFormValid()) return;
                double number2 = Double.parseDouble(number2EditText.getText().toString());
                if (number2 == 0) {
                    number2EditText.setError("Can't divide by 0");
                    return;
                }

                double number1 = Double.parseDouble(number1EditText.getText().toString());
                double result = logicService.divide(number1, number2);
                resultValueTextView.setText("" + result);
            }
        });

        Button countPiButton = findViewById(R.id.countPiButton);
        countPiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PiComputeTask().execute();
            }
        });
    }

    private boolean isFormValid() {
        boolean error = true;
        String number1String = number1EditText.getText().toString();
        String number2String = number2EditText.getText().toString();

        if (number1String.isEmpty()) {
            number1EditText.setError("You have to type number");
            error = false;
        }
        if (number2String.isEmpty()) {
            number2EditText.setError("You have to type number");
            error = false;
        }
        return error;
    }

    private class PiComputeTask extends AsyncTask<Void, Integer, Double> {
        ProgressBar progressBar;
        int numberOfPoints = 100000;
        double pointsInCircle = 0;

        protected Double doInBackground(Void... voids) {


            for (int i = 0; i <= numberOfPoints; i++) {
                double x = ThreadLocalRandom.current().nextDouble(0, 1);
                double y = ThreadLocalRandom.current().nextDouble(0, 1);

                if (x * x + y * y < 1)
                    pointsInCircle++;

                publishProgress(i);
            }
            return 4 * pointsInCircle / numberOfPoints;
        }

        @Override
        protected void onPreExecute() {
            progressBar = findViewById(R.id.progressBar);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Double result) {
            number1EditText.setText(result.toString());
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progressInPercentage = values[0] * 100 / numberOfPoints;
            progressBar.setProgress(progressInPercentage);
        }
    }
}
