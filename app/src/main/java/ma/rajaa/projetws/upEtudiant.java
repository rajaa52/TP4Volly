package ma.rajaa.projetws;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class upEtudiant extends AppCompatActivity {

    private static final String TAG = "UpEtudiant";
    private EditText nom, prenom;
    private Spinner ville;
    private RadioButton m, f;
    private Button add;
    private RequestQueue requestQueue;
    private final String updateUrl = "http://192.168.1.122/TP/ws/updateEtudiant.php";
    private int etudiantId;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_edit_item);

        // Initialisation des éléments de l'interface utilisateur
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        add = findViewById(R.id.add);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);

        // Récupérer les données de l'étudiant via l'Intent
        Intent intent = getIntent();
        etudiantId = intent.getIntExtra("id", -1);
        String etudiantNom = intent.getStringExtra("nom");
        String etudiantPrenom = intent.getStringExtra("prenom");
        String etudiantVille = intent.getStringExtra("ville");
        String etudiantSexe = intent.getStringExtra("sexe");

        // Remplir les champs avec les données récupérées
        nom.setText(etudiantNom);
        prenom.setText(etudiantPrenom);
        // Remplir le Spinner et les RadioButtons selon les données

        // Afficher le popup au clic sur le bouton
        add.setOnClickListener(v -> showEditPopup());
    }

    private void showEditPopup() {
        // Inflater le layout du popup
        View popupView = LayoutInflater.from(this).inflate(R.layout.student_edit_item, null);
        EditText nomPopup = popupView.findViewById(R.id.nom);
        EditText prenomPopup = popupView.findViewById(R.id.prenom);
        Spinner villePopup = popupView.findViewById(R.id.ville);
        RadioButton mPopup = popupView.findViewById(R.id.m);
        RadioButton fPopup = popupView.findViewById(R.id.f);

        // Vérifiez que les éléments ne sont pas null
        if (nomPopup == null || prenomPopup == null || villePopup == null) {
            Log.e(TAG, "Un ou plusieurs éléments du popup sont null");
            return; // Sortie anticipée pour éviter une exception
        }

        // Remplir les champs du popup avec les données actuelles
        nomPopup.setText(nom.getText().toString());
        prenomPopup.setText(prenom.getText().toString());

        // Définir le sexe
        if (m.isChecked()) {
            mPopup.setChecked(true);
        } else {
            fPopup.setChecked(true);
        }

        // Créer la boîte de dialogue
        new AlertDialog.Builder(this)
                .setTitle("Modifier Étudiant")
                .setView(popupView)
                .setPositiveButton("Valider", (dialog, which) -> {
                    // Vérifier si nomPopup et prenomPopup ne sont pas null avant de les utiliser
                    if (nomPopup != null && prenomPopup != null) {
                        String nomValue = nomPopup.getText().toString();
                        String prenomValue = prenomPopup.getText().toString();
                        String villeValue = villePopup.getSelectedItem().toString();
                        String sexeValue = mPopup.isChecked() ? "homme" : "femme";

                        // Mettre à jour l'étudiant
                        updateEtudiant(etudiantId, nomValue, prenomValue, villeValue, sexeValue);
                    }
                })
                .setNegativeButton("Annuler", null)
                .create()
                .show();
    }

    // Méthode pour mettre à jour un étudiant
    private void updateEtudiant(int id, String nom, String prenom, String ville, String sexe) {
        StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                response -> {
                    Log.d(TAG, "Response: " + response.trim());
                    if ("success".equals(response.trim())) {
                        Toast.makeText(this, "Étudiant mis à jour avec succès", Toast.LENGTH_SHORT).show();
                        finish(); // Fermer l'activité après la mise à jour
                    } else {
                        Toast.makeText(this, "Échec de la mise à jour", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Erreur : " + error.getMessage());
                    Toast.makeText(this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("ville", ville);
                params.put("sexe", sexe);
                return params;
            }
        };

        // Ajouter la requête à la file d'attente de Volley
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}