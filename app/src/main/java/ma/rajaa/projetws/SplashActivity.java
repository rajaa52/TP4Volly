package ma.rajaa.projetws;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ma.rajaa.projetws.services.ListActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView6);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d("SplashActivity", "SplashActivity started");
        startFadeInAnimation();
    }

    private void startFadeInAnimation() {
        // Initialiser l'opacité à 0 (transparent)
        imageView.setAlpha(0f);

        // Animation de l'opacité
        imageView.animate()
                .alpha(1f) // Opacité normale
                .setDuration(2000) // Durée de 5000 ms
                .withEndAction(() -> navigateToListActivity()); // Naviguer après l'animation
    }

    private void navigateToListActivity() {
        Intent intent = new Intent(SplashActivity.this, ListActivity.class);
        startActivity(intent);
        finish(); // Terminer l'activité de splash
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}