package benicio.solucoes.pdiqueeulevo;

import static com.google.gson.internal.$Gson$Types.arrayOf;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import benicio.solucoes.pdiqueeulevo.databinding.ActivityAdminBinding;
import benicio.solucoes.pdiqueeulevo.databinding.LayoutManagerProdutoBinding;
import benicio.solucoes.pdiqueeulevo.databinding.SelectCameraOrGaleryLayoutBinding;

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

import com.squareup.picasso.Picasso;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding mainBinding;
    private Dialog dialogCadastroProduto, dialogSelecionarFoto;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // A permissão da câmera já foi concedida, pode prosseguir com o uso da câmera.
            solicitarPermissaoCamera();
        }
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
        LayoutManagerProdutoBinding managerProdutoBinding =  LayoutManagerProdutoBinding.inflate(getLayoutInflater());
        imageProdutoExibir = managerProdutoBinding.imageProduto;
        imageProdutoExibir.setOnClickListener( view -> dialogSelecionarFoto.show());
        // TO DO...
        b.setView(managerProdutoBinding.getRoot());
        dialogCadastroProduto = b.create();
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