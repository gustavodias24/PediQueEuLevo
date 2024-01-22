package benicio.solucoes.pdiqueeulevo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import benicio.solucoes.pdiqueeulevo.adapter.AdapterProduto;
import benicio.solucoes.pdiqueeulevo.databinding.ActivityCarrinhoBinding;
import benicio.solucoes.pdiqueeulevo.databinding.ActivityEditarProdutoBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LoadingScreenBinding;
import benicio.solucoes.pdiqueeulevo.model.ProdutoModel;
import benicio.solucoes.pdiqueeulevo.util.CarrinhoUtil;
import benicio.solucoes.pdiqueeulevo.util.MathUtils;

public class CarrinhoActivity extends AppCompatActivity {

    public static LinearLayout layoutCompra;
    public static ActivityCarrinhoBinding mainBinding;
    private RecyclerView recyclerViewProdutos;
    private AdapterProduto adapterProduto;
    public static List<ProdutoModel> produtos = new ArrayList<>();

    private Dialog dialogCarregando;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCarrinhoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        layoutCompra = mainBinding.layoutFinalizarCompra;
        getSupportActionBar().setTitle("Carrinho");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Picasso.get().load(R.raw.produtonotfound).into(mainBinding.imageView4);

        configurarDialogCarregando();
        configurarrRecyclerProdutos();

    }


    private void configurarrRecyclerProdutos(){
        recyclerViewProdutos = mainBinding.recyclerProdutos;
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProdutos.setHasFixedSize(true);
        recyclerViewProdutos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        produtos.addAll(CarrinhoUtil.loadProdutosCarrinho(this));
        verificarLista();
        adapterProduto = new AdapterProduto(produtos, this, false, true, dialogCarregando);
        recyclerViewProdutos.setAdapter(adapterProduto);
    }

    public static void verificarLista(){
        if( produtos.isEmpty() ){
            layoutCompra.setVisibility(View.GONE);
            mainBinding.imageView4.setVisibility(View.VISIBLE);
            mainBinding.recyclerProdutos.setVisibility(View.GONE);
        }else{
            layoutCompra.setVisibility(View.VISIBLE);
            mainBinding.imageView4.setVisibility(View.GONE);
            mainBinding.recyclerProdutos.setVisibility(View.VISIBLE);
            calcularValor();
        }
    }

    @SuppressLint("SetTextI18n")
    public static void calcularValor(){
        double soma = 0.0;
        for ( ProdutoModel produtoModel : produtos){
            soma += MathUtils.converterParaDouble(produtoModel.getPreco());
        }

        mainBinding.textTotalCompra.setText(
                "Total da Compra: " + MathUtils.formatarMoeda(soma)
        );
    }
    private void configurarDialogCarregando() {
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}