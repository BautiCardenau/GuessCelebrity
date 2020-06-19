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


    public class DownloadTask{

        public  (String args[]) throws IOException {
            try{
            //Instantiating the URL class
            URL url = new URL("http://www.something.com/");
            //Retrieving the contents of the specified page
            Scanner sc = new Scanner(url.openStream());
            //Instantiating the StringBuffer class to hold the result
            StringBuffer sb = new StringBuffer();
            while(sc.hasNext()) {
                sb.append(sc.next());
                //System.out.println(sc.next());
            }
            //Retrieving the String from the String Buffer object
            String result = sb.toString();
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
            System.out.println(result);
            String[] splitResult = result.split("<div class=\"lister-item-image\">");
            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);


            while (m.find()){
                celebPictures.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
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
