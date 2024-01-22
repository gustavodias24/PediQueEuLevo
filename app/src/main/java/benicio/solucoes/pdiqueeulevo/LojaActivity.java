package benicio.solucoes.pdiqueeulevo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.pdiqueeulevo.adapter.AdapterProduto;
import benicio.solucoes.pdiqueeulevo.databinding.ActivityLojaBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LoadingScreenBinding;
import benicio.solucoes.pdiqueeulevo.model.ProdutoModel;

public class LojaActivity extends AppCompatActivity {

    private DatabaseReference refProdutos = FirebaseDatabase.getInstance().getReference().child("produtos");
    private ActivityLojaBinding mainBinding;
    private RecyclerView recyclerViewProdutos;
    private AdapterProduto adapterProduto;
    private List<ProdutoModel> produtos = new ArrayList<>();

    private Dialog  dialogCarregando;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityLojaBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.produtonotfound).into(mainBinding.imageView2);

        configurarDialogCarregando();
        configurarrRecyclerProdutos();
        ListenerProdutos();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == R.id.carrinhoCompra){
            startActivity(new Intent(this, CarrinhoActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarDialogCarregando() {
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }

    private void configurarrRecyclerProdutos(){
        recyclerViewProdutos = mainBinding.recyclerProdutos;
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProdutos.setHasFixedSize(true);
        recyclerViewProdutos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterProduto = new AdapterProduto(produtos, this, false, false, dialogCarregando);
        recyclerViewProdutos.setAdapter(adapterProduto);
    }

    private void ListenerProdutos() {
        dialogCarregando.show();
        refProdutos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialogCarregando.dismiss();
                produtos.clear();
                if ( snapshot.exists() ){
                    for ( DataSnapshot dado : snapshot.getChildren()){
                        ProdutoModel produtoExistente = dado.getValue(ProdutoModel.class);
                        produtos.add(produtoExistente);
                    }
                    adapterProduto.notifyDataSetChanged();
                }

                if ( produtos.isEmpty()){
                    mainBinding.imageView2.setVisibility(View.VISIBLE);
                }else{
                    mainBinding.imageView2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogCarregando.dismiss();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_carrinho, menu);
        return super.onCreateOptionsMenu(menu);
    }
}