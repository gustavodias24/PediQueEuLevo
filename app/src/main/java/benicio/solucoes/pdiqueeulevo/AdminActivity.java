package benicio.solucoes.pdiqueeulevo;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import benicio.solucoes.pdiqueeulevo.databinding.ActivityAdminBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LayoutManagerProdutoBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LoadingScreenBinding;
import benicio.solucoes.pdiqueeulevo.databinding.SelectCameraOrGaleryLayoutBinding;
import benicio.solucoes.pdiqueeulevo.model.ProdutoModel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AdminActivity extends AppCompatActivity {

    private String linkImage = "";
    private DatabaseReference refProdutos = FirebaseDatabase.getInstance().getReference().child("produtos");
    private StorageReference imgRef = FirebaseStorage.getInstance().getReference().getRoot().child("imagesProduto");
    private ActivityAdminBinding mainBinding;
    private Dialog dialogCadastroProduto, dialogSelecionarFoto, dialogCarregando;

    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CODIGO_CAMERA = 2;
    private static final int PERMISSAO_CAMERA = 3;

    private ImageView imageProdutoExibir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mainBinding.fab.setOnClickListener( view -> {
            dialogCadastroProduto.show();
        });

        configurarDialogCadastroProduto();
        configurarDialogSelecionarFoto();
        configurarDialogCarregando();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // A permissão da câmera já foi concedida, pode prosseguir com o uso da câmera.
            solicitarPermissaoCamera();
        }
    }

    private void configurarDialogCarregando() {
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void solicitarPermissaoCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // Explicar a necessidade da permissão, caso necessário.
            // Por exemplo, exibindo um diálogo explicativo.
        } else {
            // Solicitar a permissão da câmera
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSAO_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSAO_CAMERA) {
            // Verificar se a permissão foi concedida
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Conceda permissão de câmera nas configurações.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void configurarDialogCadastroProduto() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Produto");
        LayoutManagerProdutoBinding managerProdutoBinding =  LayoutManagerProdutoBinding.inflate(getLayoutInflater());
        imageProdutoExibir = managerProdutoBinding.imageProduto;
        imageProdutoExibir.setOnClickListener( view -> dialogSelecionarFoto.show());

        managerProdutoBinding.btnPronto.setOnClickListener( view ->{

            String id = UUID.randomUUID().toString();
            String nome, descri, preco;
            boolean temEstoque;

            nome = managerProdutoBinding.nomeProdutoField.getText().toString();
            descri = managerProdutoBinding.descriProdutoField.getText().toString();
            preco = managerProdutoBinding.precoField.getText().toString();

            temEstoque = managerProdutoBinding.radioEstoque.isChecked();


            if ( imageUri != null ){
                dialogCarregando.show();

                UploadTask uploadTask = imgRef.child(id + ".jpg").putFile(imageUri);

                uploadTask.addOnCompleteListener( uploadImageTask -> {
                    dialogCarregando.dismiss();
                    if (uploadImageTask.isSuccessful()){
                        imgRef.child(id + ".jpg").getDownloadUrl().addOnCompleteListener( uri -> {
                            linkImage = uri.getResult().toString();
                            cadastarNoBanco(new ProdutoModel(
                                    id, nome, descri, preco, linkImage, temEstoque
                            ));
                        });
                    }else{
                        Toast.makeText(this, "Erro ao Carregar Imagem", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                cadastarNoBanco(new ProdutoModel(
                        id, nome, descri, preco, linkImage, temEstoque
                ));
            }

            managerProdutoBinding.nomeProdutoField.setText("");
            managerProdutoBinding.descriProdutoField.setText("");
            managerProdutoBinding.precoField.setText("");
            Picasso.get().load(R.drawable.image_default).into(managerProdutoBinding.imageProduto);
            imageUri = null;

        });

        b.setView(managerProdutoBinding.getRoot());
        dialogCadastroProduto = b.create();
    }

    private void cadastarNoBanco(ProdutoModel produto){
        dialogCarregando.show();
        refProdutos.child(produto.getId()).setValue(produto).addOnCompleteListener( task -> {
            dialogCarregando.dismiss();
            dialogCadastroProduto.dismiss();
            if ( task.isSuccessful() ) {
                Toast.makeText(this, "Produto Cadastrado!", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void baterFoto(){
        ContentValues values  = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "nova picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem tirada da câmera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentCamera =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentCamera, CODIGO_CAMERA);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageProdutoExibir);
        }else if (requestCode == CODIGO_CAMERA && resultCode == RESULT_OK) {
            Log.d("mayara", "onActivityResult: " + imageUri);
            Picasso.get().load(imageUri).into(imageProdutoExibir);
        }
    }
}