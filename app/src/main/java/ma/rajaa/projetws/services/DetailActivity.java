package ma.rajaa.projetws.services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import ma.rajaa.projetws.beans.Etudiant;
import ma.rajaa.projetws.AddEtudiant;
import ma.rajaa.projetws.R;
import ma.rajaa.projetws.upEtudiant;

public class DetailActivity extends AppCompatActivity {
    private  Etudiant etudiant ;
    private ImageView imageEtudiant;
    private TextView nom, prenom, ville, sexe, idEtudiant;
    private final String updateUrl = "http://192.168.1.122/TP/ws/updateEtudiant.php";
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        idEtudiant= findViewById(R.id.id_etudiant);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        sexe = findViewById(R.id.sexe);
        imageEtudiant = findViewById(R.id.imageEtudiant); // Assurez-vous que cet ID est correct

        etudiant = new Etudiant();
        // Récupérer les données passées
        Intent intent = getIntent();
        String etudiantNom = intent.getStringExtra("etudiant_nom");
        String etudiantPrenom = intent.getStringExtra("etudiant_prenom");
        String etudiantVille = intent.getStringExtra("etudiant_ville");
        String etudiantSexe = intent.getStringExtra("etudiant_sexe");
        String etudiantImage = intent.getStringExtra("etudiant_image");

        int etudiantId = getIntent().getIntExtra("etudiant_id", -1);  // -1 est la valeur par défaut au cas où l'ID n'existe pas
        if (etudiantId == -1) {
            Log.e("DetailActivity", "ID non valide ou inexistant !");
        } else {
            Log.d("DetailActivity", "ID récupéré : " + etudiantId);
        }


        // Afficher les données
        nom.setText(etudiantNom);
        prenom.setText(etudiantPrenom);
        ville.setText(etudiantVille);
        sexe.setText(etudiantSexe);
        Log.d("DetailActivity", "URL de l'image : " + etudiantImage);
        Glide.with(this)
                .load(etudiantImage)

                .into(imageEtudiant);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("AddEtudiant", "Menu créé");
        getMenuInflater().inflate(R.menu.menu_toolbar2, menu);
        return true;
    }
    private void updateEtudiant(String etudiantId, String newNom, String newPrenom, String newVille, String newSexe) throws UnsupportedEncodingException {
        // Mise à jour de l'URL avec l'ID correct
        String encodedId = URLEncoder.encode(etudiantId, "UTF-8");
        String updateUrl = "http://192.168.1.122/TP/ws/updateEtudiant.php"; // L'URL reste fixe

        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl,
                response -> {
                    if ("success".equals(response.trim())) {
                        Toast.makeText(this, "Étudiant mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Échec de la mise à jour", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Gérer les erreurs de requête
                    Toast.makeText(this, "Erreur de connexion : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", encodedId);  // Ajouter l'ID encodé ici dans les paramètres
                params.put("nom", newNom);
                params.put("prenom", newPrenom);
                params.put("ville", newVille);
                params.put("sexe", newSexe);
                return params;
            }
        };

        // Ajouter la requête à la file d'attente
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            showEditPopup();
            return true; // Indique que l'événement a été traité
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditPopup() {
        // Inflate le layout du popup
        View popupView = getLayoutInflater().inflate(R.layout.student_edit_item, null);

        // Initialiser les champs du popup
        EditText nomPopup = popupView.findViewById(R.id.nom);
        EditText prenomPopup = popupView.findViewById(R.id.prenom);
        Spinner villePopup = popupView.findViewById(R.id.ville);
        RadioButton mPopup = popupView.findViewById(R.id.m);
        RadioButton fPopup = popupView.findViewById(R.id.f);
        TextView idPopup = popupView.findViewById(R.id.idPop);  // Champ caché pour l'ID

        // Remplir les champs avec les données actuelles
        nomPopup.setText(nom.getText().toString());
        prenomPopup.setText(prenom.getText().toString());
        idPopup.setText(String.valueOf(etudiant.getId()));  // Définir l'ID de l'étudiant actuel
        // Définir le sexe
        if ("homme".equals(etudiant.getSexe())) {
            mPopup.setChecked(true);
        } else {
            fPopup.setChecked(true);
        }

        // Créer la boîte de dialogue
        new AlertDialog.Builder(this)
                .setTitle("Modifier Étudiant")
                .setView(popupView)
                .setPositiveButton("Valider", (dialog, which) -> {
                    String newNom = nomPopup.getText().toString();
                    String newPrenom = prenomPopup.getText().toString();
                    String newVille = villePopup.getSelectedItem().toString();
                    String newSexe = mPopup.isChecked() ? "homme" : "femme";
                    String etudiantId = idPopup.getText().toString();  // Récupérer l'ID

                    // Mettre à jour l'étudiant avec l'ID correct
                    try {
                        updateEtudiant(etudiantId, newNom, newPrenom, newVille, newSexe);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .setNegativeButton("Annuler", null)
                .create()
                .show();
    }



}