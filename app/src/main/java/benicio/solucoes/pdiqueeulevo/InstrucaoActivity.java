package benicio.solucoes.pdiqueeulevo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import benicio.solucoes.pdiqueeulevo.databinding.ActivityInstrucaoBinding;

public class InstrucaoActivity extends AppCompatActivity {

    private ActivityInstrucaoBinding mainBinding;

    private Bundle b;
    private String infoPedido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityInstrucaoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mainBinding.btnVoltarAoInicio.setOnClickListener( view -> {
            finish();
            startActivity(new Intent(this, LojaActivity.class));
        });

        b = getIntent().getExtras();
        infoPedido = b.getString("infoPedido", "");


        mainBinding.btnZap.setOnClickListener(
                view ->
                startActivity(new Intent(
                        Intent.ACTION_VIEW, Uri.parse(convertToWhatsappLink("5591984044333", infoPedido))
                )));
    }


    public static String convertToWhatsappLink(String phoneNumber, String message) {
        try {
            // Remover caracteres não numéricos do número de telefone
            String cleanPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");

            // URL encode para a mensagem
            String encodedMessage = URLEncoder.encode(message, "UTF-8");

            // Criar o link do WhatsApp
            String whatsappLink = "https://wa.me/" + cleanPhoneNumber;
            if (!encodedMessage.isEmpty()) {
                whatsappLink += "?text=" + encodedMessage;
            }

            return whatsappLink;

        } catch (UnsupportedEncodingException ignored) {}

        return null;
    }
}