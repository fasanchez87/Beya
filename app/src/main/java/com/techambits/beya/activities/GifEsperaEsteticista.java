package com.techambits.beya.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.techambits.beya.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GifEsperaEsteticista extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_gif);

        GifImageView gifImageView =(GifImageView) findViewById(R.id.giv_demo);
        try
        {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.moto_espera);
            gifImageView.setImageDrawable(gifDrawable);
        }
        catch (Resources.NotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
