package benicio.solucoes.pdiqueeulevo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import benicio.solucoes.pdiqueeulevo.databinding.ActivityMainBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LayoutSenhaAdminBinding;

public class MainActivity extends AppCompatActivity {
    
    private Dialog dialogSenhaDeAdmin;

    private ActivityMainBinding mainBinding;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.logo).into((android.widget.ImageView) findViewById(R.id.imageView));
        
        configurarDialogSenhaDeAdmin();

        mainBinding.button2.setOnClickListener( view -> dialogSenhaDeAdmin.show());

    }


    private void configurarDialogSenhaDeAdmin() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutSenhaAdminBinding senhaAdminBinding = LayoutSenhaAdminBinding.inflate(getLayoutInflater());
        senhaAdminBinding.btnEntrar.setOnClickListener( view -> {
            String senhaDigitada = senhaAdminBinding.edtSenhaAdmin.getText().toString();
            
            if ( senhaDigitada.equals("adm@233")){
                startActivity(new Intent(this, AdminActivity.class));
            }else{
                Toast.makeText(this, "Senha Errada!", Toast.LENGTH_SHORT).show();
            }
        });
        b.setView(senhaAdminBinding.getRoot());
        b.setTitle("Insira a Senha de Administrador!");
        dialogSenhaDeAdmin = b.create();
    }

    public void entrarNoApp(View view){
        startActivity(new Intent(this, LojaActivity.class));
    }
}