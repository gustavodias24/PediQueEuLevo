package benicio.solucoes.pdiqueeulevo.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import benicio.solucoes.pdiqueeulevo.CarrinhoActivity;
import benicio.solucoes.pdiqueeulevo.EditarProdutoActivity;
import benicio.solucoes.pdiqueeulevo.R;
import benicio.solucoes.pdiqueeulevo.databinding.LayoutManagerProdutoBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LayoutQuantidadeBinding;
import benicio.solucoes.pdiqueeulevo.databinding.SelectCameraOrGaleryLayoutBinding;
import benicio.solucoes.pdiqueeulevo.model.ProdutoModel;
import benicio.solucoes.pdiqueeulevo.util.CarrinhoUtil;
import benicio.solucoes.pdiqueeulevo.util.MathUtils;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {


    private DatabaseReference refProdutos = FirebaseDatabase.getInstance().getReference().child("produtos");
    private StorageReference imgRef = FirebaseStorage.getInstance().getReference().getRoot().child("imagesProduto");
    List<ProdutoModel> produtos;
    Activity c;

    Boolean isADM = false;

    Boolean dentroDoCarrinho = false;

    Dialog dialogCarregando;
    Dialog dialogQuantidade;


    TextView exibirQuantidadeCarrinho;
    public AdapterProduto(List<ProdutoModel> produtos, Activity c, Boolean isADM, Boolean dentroDoCarrinho, Dialog dialogCarregando) {
        this.produtos = produtos;
        this.c = c;
        this.isADM = isADM;
        this.dentroDoCarrinho = dentroDoCarrinho;
        this.dialogCarregando = dialogCarregando;
    }

    public AdapterProduto(List<ProdutoModel> produtos, Activity c, Boolean isADM, Boolean dentroDoCarrinho, Dialog dialogCarregando, TextView exibirQuantidadeCarrinho) {
        this.produtos = produtos;
        this.c = c;
        this.isADM = isADM;
        this.dentroDoCarrinho = dentroDoCarrinho;
        this.dialogCarregando = dialogCarregando;
        this.exibirQuantidadeCarrinho = exibirQuantidadeCarrinho;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_exibir_produto, parent, false));
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProdutoModel produto = produtos.get(position);

        try{
            Picasso.get().load(produto.getLinkImage()).into(
                    holder.imageDoProdutoExibicao, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.carregandoImageDoProduto.setVisibility(View.GONE);
                            holder.imageDoProdutoExibicao.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(R.drawable.image_default).into(holder.imageDoProdutoExibicao);
                            holder.carregandoImageDoProduto.setVisibility(View.GONE);
                            holder.imageDoProdutoExibicao.setVisibility(View.VISIBLE);
                        }
                    }
            );
        }catch (Exception e){
            Picasso.get().load(R.drawable.image_default).into(holder.imageDoProdutoExibicao);
            holder.carregandoImageDoProduto.setVisibility(View.GONE);
            holder.imageDoProdutoExibicao.setVisibility(View.VISIBLE);
        }


        holder.nomeDoProdutoExibicao.setText(
                produto.getNome() + " " + produto.getPreco()
        );

        holder.descricaoDoProdutoExibicao.setText(produto.toString());

       if ( isADM ){
           holder.layoutUsuario.setVisibility(View.GONE);
       }else{
           holder.layoutAdmin.setVisibility(View.GONE);
       }

       if ( dentroDoCarrinho ){
           holder.quantiadeValorCompradoProdutoCarrinho.setVisibility(View.VISIBLE);
           holder.quantiadeValorCompradoProdutoCarrinho.setText(
                   "Quantidade Comprada: "+ produto.getQuantidadeComprada() +
                   " KG\nValor: " + MathUtils.formatarMoeda(produto.getValorQuantidadeComprada())
           );
           holder.btn_add_carrinho.setVisibility(View.GONE);
       }else{
           holder.btn_remover_carrinho.setVisibility(View.GONE);
       }

       holder.btn_excluir_produto.setOnClickListener( view -> {
           dialogCarregando.show();
           refProdutos.child(produto.getId()).setValue(null).addOnCompleteListener(task -> {
               imgRef.child(produto.getId() + ".jpg").delete().addOnCompleteListener( deleteImage -> {
                   dialogCarregando.dismiss();
                   if ( task.isSuccessful()){
                       Toast.makeText(c, "Produto Removido!", Toast.LENGTH_SHORT).show();
                       this.notifyDataSetChanged();
                   }
               });

           });
       });

       holder.btn_editar_produto.setOnClickListener( view -> {

           Intent i = new Intent(c, EditarProdutoActivity.class);
           i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           i.putExtra("id", produto.getId());
           c.startActivity(i);

       });

       holder.btn_add_carrinho.setOnClickListener( view -> {
           if( produto.isTemEstoque() ){
               AlertDialog.Builder b = new AlertDialog.Builder(c);

               b.setTitle("Escolha a Quantidade");
               LayoutQuantidadeBinding quantidadeBinding = LayoutQuantidadeBinding.inflate(c.getLayoutInflater());
               quantidadeBinding.confirmarQuantidade.setOnClickListener(confirmarView -> {
                   try{
                       float quantidade = Float.parseFloat(quantidadeBinding.qunatidadeComprada.getText().toString().replace(",", "."));

                       List<ProdutoModel> produtosCarrinho = CarrinhoUtil.loadProdutosCarrinho(c);

                       produto.setQuantidadeComprada( quantidade );
                       double quantidadeCompradaValor = MathUtils.converterParaDouble(produto.getPreco()) * quantidade;
                       produto.setValorQuantidadeComprada(quantidadeCompradaValor);

                       produtosCarrinho.add(produto);
                       CarrinhoUtil.saveProdutoCarrinho(produtosCarrinho, c, 0);
                       exibirQuantidadeCarrinho.setText(produtosCarrinho.size() + " Produto(s) no Carrinho");
                       dialogQuantidade.dismiss();

                   }catch (Exception e){
                       Toast.makeText(c, "Quantidade Inválida", Toast.LENGTH_SHORT).show();
                   }
               });
               b.setView(quantidadeBinding.getRoot());
               dialogQuantidade = b.create();
               dialogQuantidade.show();
           }else{
               Toast.makeText(c, "Produto Indisponível no Momento!", Toast.LENGTH_SHORT).show();
           }
           


       });

        holder.btn_remover_carrinho.setOnClickListener( view -> {
            produtos.remove(position);
            CarrinhoUtil.saveProdutoCarrinho(produtos, c, 1);
            this.notifyDataSetChanged();
            CarrinhoActivity.verificarLista();
        });

    }


    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ProgressBar carregandoImageDoProduto;
        ImageView imageDoProdutoExibicao;

        TextView nomeDoProdutoExibicao, descricaoDoProdutoExibicao, quantiadeValorCompradoProdutoCarrinho;
        Button btn_add_carrinho, btn_remover_carrinho, btn_editar_produto, btn_excluir_produto;
        LinearLayout layoutAdmin, layoutUsuario;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            carregandoImageDoProduto = itemView.findViewById(R.id.CarregandoImageDoProduto);
            imageDoProdutoExibicao = itemView.findViewById(R.id.imageDoProdutoExibicao);
            btn_add_carrinho = itemView.findViewById(R.id.btn_add_carrinho);
            btn_remover_carrinho = itemView.findViewById(R.id.btn_remover_carrinho);
            btn_editar_produto = itemView.findViewById(R.id.btn_editar_produto);
            btn_excluir_produto = itemView.findViewById(R.id.btn_excluir_produto);
            layoutAdmin = itemView.findViewById(R.id.layoutAdmin);
            nomeDoProdutoExibicao = itemView.findViewById(R.id.nomeDoProdutoExibicao);
            descricaoDoProdutoExibicao = itemView.findViewById(R.id.descricaoDoProdutoExibicao);
            layoutUsuario = itemView.findViewById(R.id.layoutUsuario);
            quantiadeValorCompradoProdutoCarrinho = itemView.findViewById(R.id.quantiadeValorCompradoProdutoCarrinho);
        }
    }

}
