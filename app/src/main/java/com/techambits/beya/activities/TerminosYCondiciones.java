package com.techambits.beya.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.techambits.beya.R;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;

public class TerminosYCondiciones extends AppCompatActivity
{

    private gestionSharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_ycondiciones);

        sharedPreferences = new gestionSharedPreferences(this.getApplicationContext());


        Button botonAceptarTerminos = (Button) findViewById(R.id.botonAceptarTerminos);
        botonAceptarTerminos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //registrarDatosRESt();
               /* Intent intent = new Intent(TerminosYCondiciones.this, Pago.class);
                startActivity(intent);
                finish();*/
            }
        });

        Button botonNOAceptarTerminos = (Button) findViewById(R.id.botonDeclinarTerminos);
        botonNOAceptarTerminos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //registrarDatosRESt();
                Intent intent = new Intent(TerminosYCondiciones.this, Pago.class);
                startActivity(intent);
                finish();
            }
        });

        WebView myWebView = (WebView) this.findViewById(R.id.webViewTerminosYCondiciones);
        myWebView.loadUrl("http://52.204.180.107/wiki/terminos_cliente.html");
    }


}
