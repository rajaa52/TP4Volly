package ma.rajaa.projetws.beans;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import ma.rajaa.projetws.adapter.EtudiantAdapter;
import ma.rajaa.projetws.services.ListActivity;

public class SwipeToActionCallback extends ItemTouchHelper.Callback {

    private final EtudiantAdapter adapter;
    private final ColorDrawable deleteBackground;

    public SwipeToActionCallback(EtudiantAdapter adapter, ListActivity listActivity) {
        this.adapter = adapter;
        this.deleteBackground = new ColorDrawable(Color.RED); // Fond rouge pour la suppression
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // Permet uniquement le swipe vers la gauche
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // Pas de mouvement d'items
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Récupérer la position de l'élément swipé
        int position = viewHolder.getAdapterPosition();

        // Afficher la boîte de dialogue de confirmation
        ListActivity listActivity = (ListActivity) adapter.getContext();
        listActivity.showDeleteConfirmationDialog(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // Limiter le swipe à une distance définie
        float limitedDX = Math.max(dX, -300); // Limite le swipe à gauche avec un décalage max de 300px

        int itemHeight = itemView.getBottom() - itemView.getTop();

        // Dessiner uniquement le carré rouge pour suppression
        if (limitedDX < 0) { // Swipe à gauche
            // Dessiner le fond rouge couvrant toute la hauteur de l'item
            deleteBackground.setBounds(itemView.getRight() + (int) limitedDX, itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
            deleteBackground.draw(c);

            // Dessiner le texte "Supprimer" centré dans le fond rouge
            Paint deleteTextPaint = new Paint();
            deleteTextPaint.setColor(Color.WHITE); // Couleur du texte
            deleteTextPaint.setTextSize(50); // Taille du texte fixe
            deleteTextPaint.setTextAlign(Paint.Align.CENTER); // Centrer le texte horizontalement

            String deleteText = "Supprimer";
            float deleteTextX = itemView.getRight() - Math.abs(limitedDX) / 2; // Centrer horizontalement dans la zone
            float deleteTextY = itemView.getTop() + (itemHeight / 2) - (deleteTextPaint.descent() + deleteTextPaint.ascent()) / 2; // Centrer verticalement
            c.drawText(deleteText, deleteTextX, deleteTextY, deleteTextPaint);
        }

        // Limiter la translation de l'item (effet léger)
        itemView.setTranslationX(limitedDX * 0.2f); // Ajustement de translation

        super.onChildDraw(c, recyclerView, viewHolder, limitedDX, dY, actionState, isCurrentlyActive);
    }
}