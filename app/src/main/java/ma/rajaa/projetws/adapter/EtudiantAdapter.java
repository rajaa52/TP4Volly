package ma.rajaa.projetws.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ma.rajaa.projetws.AddEtudiant;
import ma.rajaa.projetws.R;
import ma.rajaa.projetws.beans.Etudiant;
import ma.rajaa.projetws.services.DetailActivity;
import ma.rajaa.projetws.services.ListActivity;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> implements Filterable {
    private final Context context;
    private final ArrayList<Etudiant> etudiants; // Liste actuelle affichée
    private final ArrayList<Etudiant> etudiantsListFull; // Liste complète pour le filtrage

    public EtudiantAdapter(Context context, ArrayList<Etudiant> etudiants) {
        this.context = context;
        this.etudiants = etudiants;
        this.etudiantsListFull = new ArrayList<>(etudiants); // Copie de la liste originale
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.etudiant_item, parent, false);
        return new EtudiantViewHolder(view);
    }
    public void updateEtudiant(int id, String nom, String prenom, String ville, String sexe) {
        for (Etudiant etudiant : etudiants) {
            if (etudiant.getId() == id) {
                etudiant.setNom(nom);
                etudiant.setPrenom(prenom);
                etudiant.setVille(ville);
                etudiant.setSexe(sexe);
                notifyDataSetChanged(); // Notifiez que les données ont changé
                break;
            }
        }
    }
    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);
        holder.nom.setText(etudiant.getNom());
        holder.prenom.setText(etudiant.getPrenom());
        holder.ville.setText(etudiant.getVille());
        holder.sexe.setText(etudiant.getSexe());
      //  holder.image.setText(etudiant.getImage());
        // Charger l'image avec Glide
        String imageUrl = "http://192.168.1.122/TP/uploads/" + etudiant.getImage(); // Assurez-vous que getImage() retourne le nom du fichier
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageEtudiant);

        // Ajouter un listener pour le clic sur l'élément
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("etudiant_id", etudiant.getId());
            intent.putExtra("etudiant_nom", etudiant.getNom());
            intent.putExtra("etudiant_prenom", etudiant.getPrenom());
            intent.putExtra("etudiant_ville", etudiant.getVille());
            intent.putExtra("etudiant_sexe", etudiant.getSexe());
            intent.putExtra("etudiant_image", imageUrl);
            context.startActivity(intent);
        });

        // Ajouter un listener pour la suppression
        holder.itemView.setOnLongClickListener(v -> {
            showDeleteConfirmationDialog(position);
            return true;
        });
    }

    public void removeItem(int position) {
        etudiants.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        Etudiant etudiant = etudiants.get(position);
        Intent intent = new Intent(context, AddEtudiant.class);
        intent.putExtra("etudiant_id", etudiant.getId());
        intent.putExtra("etudiant_nom", etudiant.getNom());
        // Ajoutez d'autres données si nécessaire
        context.startActivity(intent);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Etudiant> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(etudiantsListFull); // Retourne toute la liste
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Etudiant etudiant : etudiantsListFull) {
                        if (etudiant.getNom().toLowerCase().contains(filterPattern)) {
                            filteredList.add(etudiant); // Ajoute l'étudiant si le nom contient le motif
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                etudiants.clear();
                if (results.values != null) {
                    etudiants.addAll((List<Etudiant>) results.values); // Met à jour la liste
                }
                notifyDataSetChanged(); // Notifie l'adaptateur que les données ont changé
            }
        };
    }

    // Afficher la boîte de dialogue de confirmation pour la suppression
    private void showDeleteConfirmationDialog(int position) {
        if (context instanceof ListActivity) {
            ((ListActivity) context).showDeleteConfirmationDialog(position);
        }
    }

    public Object getContext() {
        return context; // Retourne le contexte associé à l'adaptateur

    }

    public static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView nom;
        TextView prenom;
        TextView ville;
        TextView sexe;
        ImageView imageEtudiant;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.nom);
            prenom = itemView.findViewById(R.id.prenom);
            ville = itemView.findViewById(R.id.ville);
            sexe = itemView.findViewById(R.id.sexe);
            imageEtudiant = itemView.findViewById(R.id.imageEtudiant);
        }
    }
}