package ma.rajaa.projetws;/*package ma.rajaa.projetws;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ma.rajaa.projetws.R; // Assurez-vous que le package est correct
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ma.rajaa.projetws.beans.MultipartRequest;

public class AddEtudiant extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private EditText nom, prenom;
    private Spinner ville;
    private RadioButton m, f;
    private ImageView image;
    private Bitmap selectedImage;
    private Button add;
    private RequestQueue requestQueue;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String insertUrl = "http://192.168.1.122/TP/controller/addEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ajouter un Étudiant");

        // Vérification des permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
      //  image = findViewById(R.id.image);
        add = findViewById(R.id.add);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);

        add.setOnClickListener(this);
        image.setOnClickListener(view -> showImagePickerDialog());
    }


    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionnez une source d'image")
                .setItems(new CharSequence[]{"Galerie", "Appareil photo"}, (dialog, which) -> {
                    if (which == 0) openGallery();
                    else openCamera();
                })
                .show();
    }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, vous pouvez accéder au stockage
                Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show();
            } else {
                // Permission refusée, vous pouvez informer l'utilisateur
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
   private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    image.setImageBitmap(selectedImage);
                    Log.d("TAG", "Image sélectionnée depuis l'URI : " + imageUri.toString());
                } catch (IOException e) {
                    Log.e("TAG", "Erreur lors de la sélection de l'image", e);
                }
            } else {
                // Si l'image vient de la caméra, nous devons récupérer le bitmap directement
                Bundle extras = data.getExtras();
                if (extras != null) {
                    selectedImage = (Bitmap) extras.get("data");
                    image.setImageBitmap(selectedImage);
                    Log.d("TAG", "Image capturée depuis la caméra");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == add) {
            if (validateFields()) {
                uploadData();
            }
        }
    }


    private boolean validateFields() {
        if (nom.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (prenom.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un prénom", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ville.getSelectedItem() == null) {
            Toast.makeText(this, "Veuillez sélectionner une ville", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedImage == null) {
            Toast.makeText(this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }





    private void uploadData() {
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, insertUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("url")) {
                            String imageUrl = jsonResponse.getString("url");
                            Log.d("TAG", "URL de l'image uploadée : " + imageUrl);
                        } else {
                            // Gérer le cas d'erreur
                            Log.e("TAG", "Erreur lors de l'upload : " + jsonResponse.getString("error"));
                        }
                    } catch (JSONException e) {
                        Log.e("TAG", "Erreur de parsing JSON : " + e.getMessage());
                    }
                },
                error -> Log.e("TAG", "Erreur: " + error.toString()));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String filename = System.currentTimeMillis() + ".jpg"; // Nom de fichier unique

        // Ajouter les logs pour les données envoyées
        Log.d("TAG", "Nom: " + nom.getText().toString());
        Log.d("TAG", "Prénom: " + prenom.getText().toString());
        Log.d("TAG", "Ville: " + ville.getSelectedItem().toString());
        Log.d("TAG", "Sexe: " + (m.isChecked() ? "homme" : "femme"));
        Log.d("TAG", "Taille de l'image: " + byteArray.length + " octets");

        // Ajoutez les paramètres
        multipartRequest.addParam("nom", nom.getText().toString());
        multipartRequest.addParam("prenom", prenom.getText().toString());
        multipartRequest.addParam("ville", ville.getSelectedItem().toString());
        multipartRequest.addParam("sexe", m.isChecked() ? "homme" : "femme");

        // Ajoutez le fichier image
      //  multipartRequest.addFile("image", new MultipartRequest.DataPart(filename, byteArray));

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(multipartRequest);
    }
}*/

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import ma.rajaa.projetws.R;

public class AddEtudiant extends AppCompatActivity implements View.OnClickListener {
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;
    RequestQueue requestQueue;
    String insertUrl = "http://10.0.2.2/php_volley/ws/createEtudiant.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);
        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        ville = (Spinner) findViewById(R.id.ville);
        add = (Button) findViewById(R.id.add);
        m = (RadioButton) findViewById(R.id.m);
        f = (RadioButton) findViewById(R.id.f);
        add.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Log.d("ok","ok");
        if (v == add) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(Request.Method.POST,
                    insertUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String sexe = "";
                    if(m.isChecked())
                        sexe = "homme";
                    else
                        sexe = "femme";
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("nom", nom.getText().toString());
                    params.put("prenom", prenom.getText().toString());
                    params.put("ville", ville.getSelectedItem().toString());
                    params.put("sexe", sexe);
                    return params;
                }
            };
            requestQueue.add(request);
        }
    }
}