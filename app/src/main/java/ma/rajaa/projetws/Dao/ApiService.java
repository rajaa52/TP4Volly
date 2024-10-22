package ma.rajaa.projetws.Dao;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ApiService {
    // Méthode de suppression d'un étudiant par son ID
    @DELETE("etudiants/{id}")
    Call<Void> deleteEtudiant(@Path("id") int id);
}
