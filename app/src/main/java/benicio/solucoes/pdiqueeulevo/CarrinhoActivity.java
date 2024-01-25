package benicio.solucoes.pdiqueeulevo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import benicio.solucoes.pdiqueeulevo.adapter.AdapterProduto;
import benicio.solucoes.pdiqueeulevo.databinding.ActivityCarrinhoBinding;
import benicio.solucoes.pdiqueeulevo.databinding.ActivityEditarProdutoBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LayoutPagamentoEntregaBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LoadingScreenBinding;
import benicio.solucoes.pdiqueeulevo.model.BodyItems;
import benicio.solucoes.pdiqueeulevo.model.ItemModel;
import benicio.solucoes.pdiqueeulevo.model.ProdutoModel;
import benicio.solucoes.pdiqueeulevo.model.ResponseMercadoPagoModel;
import benicio.solucoes.pdiqueeulevo.services.MercadoPagoService;
import benicio.solucoes.pdiqueeulevo.util.CarrinhoUtil;
import benicio.solucoes.pdiqueeulevo.util.MathUtils;
import benicio.solucoes.pdiqueeulevo.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CarrinhoActivity extends AppCompatActivity {

    public static LinearLayout layoutCompra;
    public static ActivityCarrinhoBinding mainBinding;
    private RecyclerView recyclerViewProdutos;
    private AdapterProduto adapterProduto;
    public static List<ProdutoModel> produtos = new ArrayList<>();

    private Retrofit retrofit;
    private MercadoPagoService service;
    private Dialog dialogCarregando;

    private static StringBuilder infoPedido = new StringBuilder();

    private static Random random;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCarrinhoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        random = new Random();
        layoutCompra = mainBinding.layoutFinalizarCompra;
        getSupportActionBar().setTitle("Carrinho");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Picasso.get().load(R.raw.produtonotfound).into(mainBinding.imageView4);

        configurarDialogCarregando();
        configurarrRecyclerProdutos();
        configurarRetrofit();

        mainBinding.btnFinalizarCompra.setOnClickListener( view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Atenção!");
            b.setMessage("O pagamento vai ser realizado na entrega?");
            b.setPositiveButton("Sim", (dialogInterface, i) -> pagamentoPresencial());

            b.setNegativeButton("Não", (dialogInterface, i) -> pagamentoOnline());

            b.create().show();
        });

    }

    private void pagamentoPresencial(){
        AlertDialog.Builder dialogDinheiroCartao = new AlertDialog.Builder(CarrinhoActivity.this);
        dialogDinheiroCartao.setTitle("Escolha o método");
        LayoutPagamentoEntregaBinding pagamentoEntregaBinding = LayoutPagamentoEntregaBinding.inflate(getLayoutInflater());

        pagamentoEntregaBinding.radioDinheiro.setOnClickListener(view1 -> pagamentoEntregaBinding.layoutDinheiro.setVisibility(View.VISIBLE));
        pagamentoEntregaBinding.radioCartao.setOnClickListener(view1 -> pagamentoEntregaBinding.layoutDinheiro.setVisibility(View.GONE));


        pagamentoEntregaBinding.finalizarCompraPresencial.setOnClickListener( view -> {

            CarrinhoUtil.saveProdutoCarrinho(new ArrayList<>(), CarrinhoActivity.this, 3);
            infoPedido.append("-Pagamento na Entrega-").append("\n");
            infoPedido.append("Método: ").append( pagamentoEntregaBinding.radioCartao.isChecked() ? "Cartão" : "Dinheiro").append("\n");
            String troco = pagamentoEntregaBinding.edtTroco.getText().toString();
            if ( !troco.isEmpty() ){
                infoPedido.append("Troco: ").append( troco ).append("\n");
            }else{
                infoPedido.append("Não Precisa de Troco.").append( troco ).append("\n");
            }
            startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                    InstrucaoActivity.convertToWhatsappLink("5566996825146", infoPedido.toString())
                            )
                    )
            );

        });
        dialogDinheiroCartao.setView(pagamentoEntregaBinding.getRoot());
        dialogDinheiroCartao.create().show();
    }
    private void pagamentoOnline(){
        dialogCarregando.show();
        List<ItemModel> items = new ArrayList<>();
        for ( ProdutoModel produto : produtos){
            items.add(
                    new ItemModel(produto.getNome(),
                             produto.getQuantidadeComprada()+"X "+ produto.getDescri(),
                            produto.getLinkImage(),
                            1,
                            produto.getValorQuantidadeComprada())
            );

            Log.d("mayara1", "pagamentoOnline: " + produto.getValorQuantidadeComprada());
        }
        BodyItems bodyItems = new BodyItems();
        bodyItems.setItems(items);

        service.criarLinkPagamento(bodyItems).enqueue(new Callback<ResponseMercadoPagoModel>() {
            @Override
            public void onResponse(Call<ResponseMercadoPagoModel> call, Response<ResponseMercadoPagoModel> response) {
                dialogCarregando.dismiss();
                if ( response.isSuccessful()){
                    CarrinhoUtil.saveProdutoCarrinho(new ArrayList<>(), CarrinhoActivity.this, 3);
                    assert response.body() != null;
                    String url = response.body().getInit_point();

                    infoPedido.append("\uD83D\uDCC2\uD83D\uDCCESegue o Comprovante:");

                    Intent intentInstrucao = new Intent(CarrinhoActivity.this, InstrucaoActivity.class);
                    intentInstrucao.putExtra("infoPedido", infoPedido.toString());
                    startActivity(intentInstrucao);

                    CustomTabsIntent intent = new CustomTabsIntent.Builder()
                            .build();
                    intent.launchUrl(CarrinhoActivity.this, Uri.parse(url));
                }else{
                    Toast.makeText(CarrinhoActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseMercadoPagoModel> call, Throwable t) {
                Toast.makeText(CarrinhoActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                dialogCarregando.dismiss();
            }
        });
    }

    private void configurarRetrofit(){
        retrofit = RetrofitUtil.createRetrofit();
        service = RetrofitUtil.createService(retrofit);
    }
    private void configurarrRecyclerProdutos(){
        recyclerViewProdutos = mainBinding.recyclerProdutos;
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProdutos.setHasFixedSize(true);
        recyclerViewProdutos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        produtos.clear();
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
        infoPedido = new StringBuilder();
        infoPedido.append("*Informações da Compra*").append("\n").append("\n");
        infoPedido.append("_Número do Peidido:_ ").append(gerarNumeroAleatorio(10, random)).append("\n").append("\n");
        infoPedido.append("Item(s):").append("\n").append("\n");
        for ( ProdutoModel produtoModel : produtos){
            Log.d("mayara2", "pagamentoOnline: " + produtoModel.getValorQuantidadeComprada());
            soma += produtoModel.getValorQuantidadeComprada();
            infoPedido.append("Produto: ").append(produtoModel.getNome()).append("\n");
            infoPedido.append("Preço: ").append(produtoModel.getPreco()).append("\n");
            infoPedido.append("Quantidade: ").append(produtoModel.getQuantidadeComprada()).append(" KG").append("\n");
            infoPedido.append("Valor da Compra: ").append(MathUtils.formatarMoeda(produtoModel.getValorQuantidadeComprada())).append("\n").append("\n");

        }

        mainBinding.textTotalCompra.setText(
                "Total da Compra: " + MathUtils.formatarMoeda(soma)
        );
        infoPedido.append("Total da Compra: " + MathUtils.formatarMoeda(soma)).append("\n").append("\n");
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

    private static long gerarNumeroAleatorio(int numeroDigitos, Random random) {
        // Fórmula para gerar um número aleatório com o número de dígitos especificado
        long limiteInferior = (long) Math.pow(10, numeroDigitos - 1);
        long limiteSuperior = (long) Math.pow(10, numeroDigitos) - 1;

        // Garantir que o número gerado seja positivo
        return Math.abs(limiteInferior + random.nextLong() % (limiteSuperior - limiteInferior + 1));
    }
}