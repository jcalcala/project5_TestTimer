package com.murach.ch10_ex5;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView messageTextView, rssTV;
    private Button startButton, stopButton;

    private Timer timer;
    private TimerTask task;
    private long elapsedMillis;
    private int downloadCounter = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        rssTV = (TextView) findViewById(R.id.rssTV);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        startTimer();
    }

    //this will run every 10 secs
    private void startTimer() {
        final long startMillis = System.currentTimeMillis();
        timer = new Timer(true);
        task = new TimerTask() {
            
            @Override
            public void run() {
                elapsedMillis = System.currentTimeMillis() - startMillis;
                updateView(elapsedMillis);
                downloadFile();
            }
        };
        timer.schedule(task, 0, 10000);
    }

    private void updateView(final long elapsedMillis) {
        // UI changes need to be run on the UI thread
        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis/1000;
            int seconds = elapsedSeconds/10;

            @Override
            public void run() {

                rssTV.setText("File downloaded "+downloadCounter+" time(s)");
                downloadCounter = downloadCounter +1;

                messageTextView.setText("Seconds: " + seconds);
            }
        });
    }

    public void startTime(View view) {
        startTimer();
    }

    public void stopTime(View view) {
        timeStopper();
    }

    public void timeStopper() {
        timer.cancel();
    }

    @Override
    protected void onPause() {
        timeStopper();
        super.onPause();
    }

    public void downloadFile() {

        final String URL_STRING = "http://rss.cnn.com/rss/cnn_tech.rss";
        final String FILENAME = "news_feed.xml";

        try{
            // get the URL
            URL url = new URL(URL_STRING);

            // get the input stream
            InputStream in = url.openStream();

            // get the output stream
            FileOutputStream out = openFileOutput(FILENAME, MODE_PRIVATE);

            // read input and write output
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1)
            {
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            out.close();
            in.close();
        }
        catch (IOException e) {
            Log.e("News reader", e.toString());
        }
    }
}