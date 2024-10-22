package ma.rajaa.projetws.services;

import ma.rajaa.projetws.Dao.IDao;
import ma.rajaa.projetws.beans.Etudiant;
import java.util.ArrayList;
import java.util.List;

public class EtudiantService implements IDao {
    private List<Etudiant> etudiants = new ArrayList<>();

    // Ajouter un étudiant
    @Override
    public void addEtudiant(Etudiant etudiant) {
        etudiants.add(etudiant);
    }

    // Récupérer tous les étudiants
    @Override
    public List<Etudiant> getAllEtudiants() {
        return etudiants;
    }

    // Trouver un étudiant par ID
    @Override
    public Etudiant getEtudiantById(int id) {
        for (Etudiant etudiant : etudiants) {
            if (etudiant.getId() == id) {
                return etudiant;
            }
        }
        return null; // Étudiant non trouvé
    }

    // Supprimer un étudiant
    @Override
    public boolean deleteEtudiant(int id) {
        Etudiant etudiant = getEtudiantById(id);
        if (etudiant != null) {
            etudiants.remove(etudiant);
            return true;
        }
        return false;
    }
}
