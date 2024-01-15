package benicio.solucoes.pdiqueeulevo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.squareup.picasso.Picasso;

import benicio.solucoes.pdiqueeulevo.databinding.ActivityLojaBinding;

public class LojaActivity extends AppCompatActivity {

    private ActivityLojaBinding mainBinding;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityLojaBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.produtonotfound).into(mainBinding.imageView2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_carrinho, menu);
        return super.onCreateOptionsMenu(menu);
    }
}