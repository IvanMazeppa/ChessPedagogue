// Create this new file: SavedGamesActivity.java
package com.example.chesspedagogue;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class SavedGamesActivity extends AppCompatActivity {
    private GameDatabaseHelper dbHelper;
    private ListView gamesListView;
    private TextView emptyTextView;
    private List<GameDatabaseHelper.SavedGame> savedGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Saved Games");
        }

        // Initialize views
        gamesListView = findViewById(R.id.gamesListView);
        emptyTextView = findViewById(R.id.emptyTextView);

        // Initialize database helper
        dbHelper = new GameDatabaseHelper(this);

        // Load saved games
        loadSavedGames();

        // Set item click listener
        gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameDatabaseHelper.SavedGame selectedGame = savedGames.get(position);
                openGameReview(selectedGame.getId());
            }
        });
    }

    private void loadSavedGames() {
        savedGames = dbHelper.getAllGames();

        if (savedGames.isEmpty()) {
            gamesListView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            gamesListView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);

            // Create a list of game descriptions for the adapter
            List<String> gameDescriptions = new ArrayList<>();
            for (GameDatabaseHelper.SavedGame game : savedGames) {
                String desc = game.getFormattedDate() + "\n" +
                        "Playing as: " + game.getPlayerColor() + "\n" +
                        game.getDescription();
                gameDescriptions.add(desc);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    gameDescriptions);

            gamesListView.setAdapter(adapter);
        }
    }

    private void openGameReview(long gameId) {
        // We'll implement this in the next step!
        Intent intent = new Intent(this, GameAnalysisActivity.class);
        intent.putExtra("GAME_ID", gameId);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}