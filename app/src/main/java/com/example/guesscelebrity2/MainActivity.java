package com.example.guesscelebrity2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebPictures = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    ImageView imageView;
    int chosenCeleb = 0;
    String[] answers = new String[4];
    int positionOfCorrect = 0;


    public void buttonPressed (View view) {

        Button button = (Button) view;
        int tappedAnswer = Integer.parseInt(button.getTag().toString());
        Log.i("TAG", Integer.toString(tappedAnswer));

        if (tappedAnswer == positionOfCorrect) {

            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, "Wrong! It was " + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }

        newQuestion();
    }




    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try{
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;

            } catch (Exception e){

                e.printStackTrace();
                return null;

            }
        }
    }


    public class DownloadTask  extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                result = Jsoup.connect(url.toString()).get().html();;

                return result;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }


    public void newQuestion () {
        try {
            Random rand = new Random();
            chosenCeleb = rand.nextInt(celebPictures.size());
            ImageDownloader imageDownloader = new ImageDownloader();
            Bitmap myImage = imageDownloader.execute(celebPictures.get(chosenCeleb)).get();
            imageView.setImageBitmap(myImage);
            positionOfCorrect = rand.nextInt(4);
            int incorrectLocation;
            for (int i = 0; i < 4; i++) {

                if (i == positionOfCorrect) {

                    answers[i] = celebNames.get(chosenCeleb);

                } else {

                    incorrectLocation = rand.nextInt(celebNames.size());
                    while (incorrectLocation == chosenCeleb) {
                        incorrectLocation = rand.nextInt(celebNames.size());
                    }
                    answers[i] = celebNames.get(incorrectLocation);
                }

            }
            Button button1 = findViewById(R.id.button4);
            Button button2 = findViewById(R.id.button3);
            Button button3 = findViewById(R.id.button2);
            Button button4 = findViewById(R.id.button);

            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        String result = null;
        imageView = findViewById(R.id.imageView);

        try {
            //NEED TO CHANGE WEBSITE, NOT WORKING ANYMORE
            //try with this one when you fix it: https://www.imdb.com/list/ls052283250/
            result = task.execute("https://www.imdb.com/list/ls052283250/").get();
            //System.out.println(result);
            String[] splitResult = result.split("<div class=\"lister-item-image\">");
            System.out.println(splitResult.toString());  //Chequear que es lo que esta dividiendo
            //Hasta aca por lo menos me devuelve el html en una string, por ahi el problema es que me lo da con espacios"
            Pattern p = Pattern.compile(" src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);


            while (m.find()){
                celebPictures.add(m.group(1));
                Log.i("sources", m.group(1));
            }

            p = Pattern.compile(" alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()){
                celebNames.add(m.group(1));
            }

            newQuestion();

        } catch (Exception e){

            e.printStackTrace();

        }
    }
}
