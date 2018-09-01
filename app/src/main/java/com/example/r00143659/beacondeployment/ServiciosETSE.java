package com.example.r00143659.beacondeployment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Page of the CIT main services in the webpage. Those are, in my point of view, the most searched by the students when using the web.
 * When the user clicks one of the buttons, it will open the browser and direct them to the service selected.
 * Some of the student, specially freshman and international/erasmus student don't know the existance of some of them
 */

public class ServiciosETSE extends AppCompatActivity {
    Button webBIBLIOTECA, webCorreo, webAULAVIRTUAL, webESPORTS, webHorariosYExamenes, webETSE, webOPAL;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicios_etse);

        webETSE = (Button) findViewById(R.id.web_etse);
        webOPAL = (Button) findViewById(R.id.web_opal);
        webBIBLIOTECA = (Button) findViewById(R.id.web_biblioteca);
        webCorreo = (Button) findViewById(R.id.web_correo);
        webAULAVIRTUAL = (Button) findViewById(R.id.aula_virtual);
        webESPORTS = (Button) findViewById(R.id.servei_esports);
        webHorariosYExamenes = (Button) findViewById(R.id.horarios_y_examenes);

        webETSE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.uv.es/etse"));
                startActivity(intent);
            }
        });

        webOPAL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://www.fundaciouv.es/opal/"));
                startActivity(intent);
            }
        });

        webBIBLIOTECA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://trobes.uv.es/"));
                startActivity(intent);
            }
        });

        webCorreo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://correu.uv.es"));
                startActivity(intent);
            }
        });

        webAULAVIRTUAL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://aulavirtual.uv.es"));
                startActivity(intent);
            }
        });

        webESPORTS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.uv.es/sesport/"));
                startActivity(intent);
            }
        });

        webHorariosYExamenes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent (android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.uv.es/uvweb/enginyeria/ca/estudis-grau/oferta-graus/horaris-dates-examen-1285847234361.html"));
                startActivity(intent);
            }
        });
    }
}
