package benicio.solucoes.pdiqueeulevo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import benicio.solucoes.pdiqueeulevo.databinding.ActivityEditarProdutoBinding;
import benicio.solucoes.pdiqueeulevo.databinding.ActivityLojaBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LoadingScreenBinding;
import benicio.solucoes.pdiqueeulevo.databinding.SelectCameraOrGaleryLayoutBinding;
import benicio.solucoes.pdiqueeulevo.model.ProdutoModel;

public class EditarProdutoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CODIGO_CAMERA = 2;
    private static final int PERMISSAO_CAMERA = 3;
    private Uri imageUri;
    private String linkImage = "";
    private Dialog dialogCarregando, dialogSelecionarFoto;
    private ProdutoModel produtoModel;
    private Bundle b;
    private ActivityEditarProdutoBinding mainBinding;
    private DatabaseReference refProdutos = FirebaseDatabase.getInstance().getReference().child("produtos");
    private StorageReference imgRef = FirebaseStorage.getInstance().getReference().getRoot().child("imagesProduto");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityEditarProdutoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        getSupportActionBar().setTitle("Editar Produto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarDialogCarregando();
        configurarDialogSelecionarFoto();
        dialogCarregando.show();
        b = getIntent().getExtras();
        
        refProdutos.child(b.getString("id", "")).get().addOnCompleteListener( task -> {
            dialogCarregando.dismiss();
            
            if ( task.isSuccessful() ){
                produtoModel = task.getResult().getValue(ProdutoModel.class);
                configurarProduto(produtoModel);
            }else{
                Toast.makeText(this, "Problema de Conexão", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void configurarProduto(ProdutoModel produto) {
        linkImage = produto.getLinkImage();
        try{
            Picasso.get().load(produto.getLinkImage()).into(mainBinding.imageProduto);
        }catch (Exception e){
            Picasso.get().load(R.drawable.image_default).into(mainBinding.imageProduto);
        }
       mainBinding.imageProduto.setOnClickListener( selectView ->  dialogSelecionarFoto.show());
       mainBinding.nomeProdutoField.setText(produto.getNome());
       mainBinding.descriProdutoField.setText(produto.getDescri());
       mainBinding.precoField.setText(produto.getPreco());
       mainBinding.radioEstoque.setChecked( produto.isTemEstoque() );
       mainBinding.radioButton2.setChecked( !produto.isTemEstoque() );
       mainBinding.btnPronto.setText("Atualizar");

       mainBinding.btnPronto.setOnClickListener( view -> {
           String nome, descri, preco;
           boolean temEstoque;

           nome = mainBinding.nomeProdutoField.getText().toString();
           descri = mainBinding.descriProdutoField.getText().toString();
           preco = mainBinding.precoField.getText().toString();
           temEstoque = mainBinding.radioEstoque.isChecked();

           if ( imageUri != null ){
                   dialogCarregando.show();

                   UploadTask uploadTask = imgRef.child(produto.getId() + ".jpg").putFile(imageUri);

                   uploadTask.addOnCompleteListener( uploadImageTask -> {
                       dialogCarregando.dismiss();
                       if (uploadImageTask.isSuccessful()){
                           imgRef.child(produto.getId() + ".jpg").getDownloadUrl().addOnCompleteListener( uri -> {
                               linkImage = uri.getResult().toString();
                               cadastarNoBanco(new ProdutoModel(
                                       produto.getId(), nome, descri, preco, linkImage, temEstoque
                               ));
                           });
                       }else{
                           Toast.makeText(this, "Erro ao Carregar Imagem", Toast.LENGTH_SHORT).show();
                       }
                   });
           }else{
               cadastarNoBanco(new ProdutoModel(
                       produto.getId(), nome, descri, preco, linkImage, temEstoque
                   ));
           }
       });
    }

    private void configurarDialogCarregando() {
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void cadastarNoBanco(ProdutoModel produto){
        dialogCarregando.show();
        refProdutos.child(produto.getId()).setValue(produto).addOnCompleteListener( task -> {
            dialogCarregando.dismiss();
            if ( task.isSuccessful() ) {
                Toast.makeText(this, "Produto Atualizado!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void baterFoto(){
        ContentValues values  = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "nova picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem tirada da câmera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentCamera =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentCamera, CODIGO_CAMERA);
    }

    private void configurarDialogSelecionarFoto() {
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(this);
        SelectCameraOrGaleryLayoutBinding cameraOrGalery = SelectCameraOrGaleryLayoutBinding.inflate(getLayoutInflater());
        b.setTitle("Selecione: ");

        cameraOrGalery.btnCamera.setOnClickListener( view -> {
            baterFoto();
            dialogSelecionarFoto.dismiss();
        });

        cameraOrGalery.btnGaleria.setOnClickListener( view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            dialogSelecionarFoto.dismiss();
        });

        b.setView(cameraOrGalery.getRoot());
        dialogSelecionarFoto = b.create();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(mainBinding.imageProduto);
        }else if (requestCode == CODIGO_CAMERA && resultCode == RESULT_OK) {
            Log.d("mayara", "onActivityResult: " + imageUri);
            Picasso.get().load(imageUri).into(mainBinding.imageProduto);
        }
    }
}