package benicio.solucoes.pdiqueeulevo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.pdiqueeulevo.model.ProdutoModel;

public class CarrinhoUtil {
    public final static String prefs_name = "carrinho_prefs";
    public final static String prods_carrinho = "prods_carrinho";

    public static void saveProdutoCarrinho(List<ProdutoModel> produtos, Context c, int t){
        SharedPreferences sharedPreferences = c.getSharedPreferences(prefs_name, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        if ( t == 0){
            Toast.makeText(c, "Produto Adicionado!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c, "Produto Removido!", Toast.LENGTH_SHORT).show();
        }

        editor.putString(prods_carrinho, new Gson().toJson(produtos)).apply();
    }

    public static List<ProdutoModel> loadProdutosCarrinho(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(prefs_name, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        Type type = new TypeToken<List<ProdutoModel>>(){}.getType();

        List<ProdutoModel> produtos = new ArrayList<>();
        List<ProdutoModel> produtosExistentes = gson.fromJson(sharedPreferences.getString(prods_carrinho, ""), type);

        if ( produtosExistentes != null){
            produtos.addAll(produtosExistentes);
        }

        return produtos;
    }
}
