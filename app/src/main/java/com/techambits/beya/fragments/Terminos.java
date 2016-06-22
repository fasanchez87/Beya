package com.techambits.beya.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.techambits.beya.R;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;


public class Terminos extends Fragment
{


    private gestionSharedPreferences sharedPreferences;
    WebView myWebView;


    public Terminos()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPreferences = new gestionSharedPreferences(this.getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terminos, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        myWebView = (WebView) this.getActivity().findViewById(R.id.webViewTerminosYCondicionesFragment);

        if(sharedPreferences.getString("tipoUsuario").equals("C"))//TERMINOS CLIENTE
        {
            myWebView.loadUrl("http://52.204.180.107/wiki/terminos_cliente.html");

        }

        else //TERMINOS ESTETICISTA
        {
            myWebView.loadUrl("http://52.204.180.107/wiki/terminos_esteticista.html");

        }


    }




}
