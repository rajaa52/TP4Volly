package ma.rajaa.projetws.services;
import androidx.appcompat.widget.SearchView; // Correct
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ma.rajaa.projetws.AddEtudiant;
import ma.rajaa.projetws.R;
import ma.rajaa.projetws.adapter.EtudiantAdapter;
import ma.rajaa.projetws.beans.Etudiant;
import ma.rajaa.projetws.beans.SwipeToActionCallback;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private ArrayList<Etudiant> etudiantsList;
    private EtudiantAdapter adapter;
    private static final String URL = "http://192.168.1.122/TP/ws/loadEtudiant.php";
    private static final String DELETE_URL = "http://192.168.1.122/TP/controller/deleteEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etudiantsList = new ArrayList<>();
        adapter = new EtudiantAdapter(this, etudiantsList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchEtudiants();

        // Configuration du Swipe
        SwipeToActionCallback swipeToActionCallback = new SwipeToActionCallback(adapter, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToActionCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Méthode pour récupérer les étudiants depuis le serveur
    private void fetchEtudiants() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, null,
                response -> {
                    Log.d("ListActivity", "Response: " + response.toString());
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject etudiantObject = response.getJSONObject(i);
                            int id = etudiantObject.getInt("id");
                            String nomText = etudiantObject.getString("nom");
                            String prenomText = etudiantObject.getString("prenom");
                            String villeText = etudiantObject.getString("ville");
                            String sexeText = etudiantObject.getString("sexe");
                            String imageText = etudiantObject.getString("image");

                            Etudiant etudiant = new Etudiant(id, nomText, prenomText, villeText, sexeText, imageText);
                            etudiantsList.add(etudiant);
                        }

                        if (!etudiantsList.isEmpty()) {
                            adapter.notifyDataSetChanged(); // Notifiez l'adaptateur
                        } else {
                            Toast.makeText(ListActivity.this, "Aucune donnée disponible", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("ListActivity", "Erreur lors de la récupération des données", e);
                        Toast.makeText(ListActivity.this, "Erreur de chargement des étudiants", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ListActivity", "Erreur de requête", error);
                    Toast.makeText(ListActivity.this, "Erreur réseau : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonArrayRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Vérifiez si l'ID de l'item est celui de l'action_add
        if (item.getItemId() == R.id.action_add) {
            // Ouvrir l'activité pour ajouter un étudiant
            Intent intent = new Intent(this, AddEtudiant.class);
            startActivity(intent);
            return true; // Indique que l'événement a été traité
        }

        // Retourne la gestion par défaut pour les autres items
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu); // Assurez-vous que le bon menu est chargé
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Rechercher quand l'utilisateur soumet la requête
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filtrer la liste des étudiants
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }
    // Méthode pour supprimer un étudiant
    private void removeEtudiant(int position) {
        Etudiant etudiant = etudiantsList.get(position);
        Log.d("ListActivity", "ID de l'étudiant à supprimer : " + etudiant.getId());

        // URL sans ID dans l'URL
        String url = DELETE_URL;  // Utilise simplement l'URL sans ajouter l'ID dans l'URL

        // Crée une requête POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("ListActivity", "Réponse de suppression : " + response);
                    if ("success".equals(response.trim())) {
                        // Suppression réussie
                        etudiantsList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(ListActivity.this, "Étudiant supprimé de la base de données", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListActivity.this, "Échec de la suppression sur le serveur", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ListActivity", "Erreur de requête", error);
                    Toast.makeText(ListActivity.this, "Erreur de connexion : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Envoie l'ID dans le corps de la requête POST
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));  // Utilise l'ID de l'étudiant dans le corps
                return params;
            }
        };

        // Ajouter la requête à la file d'attente
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    // Méthode pour afficher la boîte de dialogue de confirmation de suppression
    public void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suppression");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer cet étudiant ?");

        // Bouton "Oui"
        builder.setPositiveButton("Oui", (dialog, which) -> {
            // Supprime l'étudiant du serveur et localement
            removeEtudiant(position);
        });

        // Bouton "Annuler"
        builder.setNegativeButton("Annuler", (dialog, which) -> {
            recyclerView.getAdapter().notifyItemChanged(position); // Restaure l'élément après le swipe
        });

        // Afficher la boîte de dialogue
        builder.show();
    }
}