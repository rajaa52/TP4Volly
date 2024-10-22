package ma.rajaa.projetws.Dao;

import ma.rajaa.projetws.beans.Etudiant;
import java.util.List;

public interface IDao {
    void addEtudiant(Etudiant etudiant);
    List<Etudiant> getAllEtudiants();
    Etudiant getEtudiantById(int id);
    boolean deleteEtudiant(int id);
}

