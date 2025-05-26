package com.example.chesspedagogue;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class ChessMasterSelectionActivity extends AppCompatActivity {

    private String selectedMaster = "tal"; // Default selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_master_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get current selection from FineTunedModelManager
        selectedMaster = FineTunedModelManager.getInstance(this).getSelectedChessMaster();

        // Initialize radio buttons based on the current selection
        initializeRadioButtons();

        // Set up click listeners for the master cards
        setupCardClickListeners();

        // Continue button listener
        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            saveMasterSelection();
            finish();
        });
        setupCardAnimations();
    }

    private void saveMasterSelection() {
        // Simply call the method with the selected master
        saveMasterSelection(selectedMaster);
    }

    private void setupCardAnimations() {
        // Get all card views
        CardView[] cards = {
                findViewById(R.id.talCard),
                findViewById(R.id.kramnikCard),
                findViewById(R.id.karpovCard),
                findViewById(R.id.fischerCard),
                findViewById(R.id.laskerCard),
                findViewById(R.id.kasparovCard),
                findViewById(R.id.capablancaCard),
                findViewById(R.id.carlsenCard),
                findViewById(R.id.morphyCard),
                findViewById(R.id.anandCard),
                findViewById(R.id.botvinnikCard),
                findViewById(R.id.alekhineCard)
        };

        // Add beautiful touch animations to each card
        for (CardView card : cards) {
            if (card != null) {
                card.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Scale down slightly when pressed
                            v.animate()
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(100)
                                    .start();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            // Scale back up with a nice bounce
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(150)
                                    .setInterpolator(new android.view.animation.OvershootInterpolator())
                                    .start();
                            break;
                    }
                    return false; // Let the click event continue
                });
            }
        }
    }


    private void initializeRadioButtons() {
        // Find all radio buttons
        RadioButton radioTal = findViewById(R.id.radioTal);
        RadioButton radioKramnik = findViewById(R.id.radioKramnik);
        RadioButton radioKarpov = findViewById(R.id.radioKarpov);
        RadioButton radioFischer = findViewById(R.id.radioFischer);
        RadioButton radioLasker = findViewById(R.id.radioLasker);
        RadioButton radioKasparov = findViewById(R.id.radioKasparov);
        RadioButton radioCapablanca = findViewById(R.id.radioCapablanca);
        RadioButton radioCarlsen = findViewById(R.id.radioCarlsen);
        RadioButton radioMorphy = findViewById(R.id.radioMorphy);
        RadioButton radioAnand = findViewById(R.id.radioAnand);
        RadioButton radioBotvinnik = findViewById(R.id.radioBotvinnik);
        RadioButton radioAlekhine = findViewById(R.id.radioAlekhine); // NEW: Add Alekhine radio button

        // Check the appropriate radio button based on current selection
        switch(selectedMaster.toLowerCase()) {
            case "tal":
                radioTal.setChecked(true);
                break;
            case "kramnik":
                radioKramnik.setChecked(true);
                break;
            case "karpov":
                radioKarpov.setChecked(true);
                break;
            case "fischer":
                radioFischer.setChecked(true);
                break;
            case "lasker":
                radioLasker.setChecked(true);
                break;
            case "kasparov":
                radioKasparov.setChecked(true);
                break;
            case "capablanca":
                radioCapablanca.setChecked(true);
                break;
            case "carlsen":
                radioCarlsen.setChecked(true);
                break;
            case "morphy":
                radioMorphy.setChecked(true);
                break;
            case "anand":
                radioAnand.setChecked(true);
                break;
            case "botvinnik":
                radioBotvinnik.setChecked(true);
                break;
            case "alekhine": // NEW: Add Alekhine case
                radioAlekhine.setChecked(true);
                break;
        }

        // Set up radio button listeners
        radioTal.setOnClickListener(v -> selectedMaster = "tal");
        radioKramnik.setOnClickListener(v -> selectedMaster = "kramnik");
        radioKarpov.setOnClickListener(v -> selectedMaster = "karpov");
        radioFischer.setOnClickListener(v -> selectedMaster = "fischer");
        radioLasker.setOnClickListener(v -> selectedMaster = "lasker");
        radioKasparov.setOnClickListener(v -> selectedMaster = "kasparov");
        radioCapablanca.setOnClickListener(v -> selectedMaster = "capablanca");
        radioCarlsen.setOnClickListener(v -> selectedMaster = "carlsen");
        radioMorphy.setOnClickListener(v -> selectedMaster = "morphy");
        radioAnand.setOnClickListener(v -> selectedMaster = "anand");
        radioBotvinnik.setOnClickListener(v -> selectedMaster = "botvinnik");
        radioAlekhine.setOnClickListener(v -> selectedMaster = "alekhine"); // NEW: Add Alekhine listener
    }

    private void setupCardClickListeners() {
        // Get all card views
        CardView talCard = findViewById(R.id.talCard);
        CardView kramnikCard = findViewById(R.id.kramnikCard);
        CardView karpovCard = findViewById(R.id.karpovCard);
        CardView fischerCard = findViewById(R.id.fischerCard);
        CardView laskerCard = findViewById(R.id.laskerCard);
        CardView kasparovCard = findViewById(R.id.kasparovCard);
        CardView capablancaCard = findViewById(R.id.capablancaCard);
        CardView carlsenCard = findViewById(R.id.carlsenCard);
        CardView morphyCard = findViewById(R.id.morphyCard);
        CardView anandCard = findViewById(R.id.anandCard);
        CardView botvinnikCard = findViewById(R.id.botvinnikCard);
        CardView alekhineCard = findViewById(R.id.alekhineCard); // NEW: Add Alekhine card

        // Set click listeners for each card
        talCard.setOnClickListener(v -> {
            selectedMaster = "tal";
            RadioButton radio = findViewById(R.id.radioTal);
            radio.setChecked(true);
        });

        kramnikCard.setOnClickListener(v -> {
            selectedMaster = "kramnik";
            RadioButton radio = findViewById(R.id.radioKramnik);
            radio.setChecked(true);
        });

        karpovCard.setOnClickListener(v -> {
            selectedMaster = "karpov";
            RadioButton radio = findViewById(R.id.radioKarpov);
            radio.setChecked(true);
        });

        fischerCard.setOnClickListener(v -> {
            selectedMaster = "fischer";
            RadioButton radio = findViewById(R.id.radioFischer);
            radio.setChecked(true);
        });

        laskerCard.setOnClickListener(v -> {
            selectedMaster = "lasker";
            RadioButton radio = findViewById(R.id.radioLasker);
            radio.setChecked(true);
        });

        kasparovCard.setOnClickListener(v -> {
            selectedMaster = "kasparov";
            RadioButton radio = findViewById(R.id.radioKasparov);
            radio.setChecked(true);
        });

        capablancaCard.setOnClickListener(v -> {
            selectedMaster = "capablanca";
            RadioButton radio = findViewById(R.id.radioCapablanca);
            radio.setChecked(true);
        });

        carlsenCard.setOnClickListener(v -> {
            selectedMaster = "carlsen";
            RadioButton radio = findViewById(R.id.radioCarlsen);
            radio.setChecked(true);
        });

        morphyCard.setOnClickListener(v -> {
            selectedMaster = "morphy";
            RadioButton radio = findViewById(R.id.radioMorphy);
            radio.setChecked(true);
        });

        anandCard.setOnClickListener(v -> {
            selectedMaster = "anand";
            RadioButton radio = findViewById(R.id.radioAnand);
            radio.setChecked(true);
        });

        botvinnikCard.setOnClickListener(v -> {
            selectedMaster = "botvinnik";
            RadioButton radio = findViewById(R.id.radioBotvinnik);
            radio.setChecked(true);
        });

        // NEW: Add Alekhine card click listener
        alekhineCard.setOnClickListener(v -> {
            selectedMaster = "alekhine";
            RadioButton radio = findViewById(R.id.radioAlekhine);
            radio.setChecked(true);
        });
    }

    private void saveMasterSelection(String master) {
        // Save the selection to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("ChessAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selected_master", master);
        editor.apply();

        // Update the FineTunedModelManager
        FineTunedModelManager.getInstance(this).setSelectedChessMaster(master);

        // Show feedback
        Toast.makeText(this, "Coach changed to " + getDisplayName(master),
                Toast.LENGTH_SHORT).show();

        // Reset voice settings to auto for this chess master
        SharedPreferences voicePrefs = getSharedPreferences("ChessPedagoguePrefs", MODE_PRIVATE);
        SharedPreferences.Editor voiceEditor = voicePrefs.edit();
        voiceEditor.putString("voice_style", "auto");  // This means "use master-appropriate voice"
        voiceEditor.apply();

        // Update TTS settings
        ChessCoachManager.getInstance(this).updateTTSSettings("auto", true);
    }

    private String getDisplayName(String master) {
        switch(master.toLowerCase()) {
            case "tal": return "Mikhail Tal";
            case "kramnik": return "Vladimir Kramnik";
            case "karpov": return "Anatoly Karpov";
            case "fischer": return "Bobby Fischer";
            case "lasker": return "Emanuel Lasker";
            case "kasparov": return "Garry Kasparov";
            case "capablanca": return "Jose Raul Capablanca";
            case "carlsen": return "Magnus Carlsen";
            case "morphy": return "Paul Morphy";
            case "anand": return "Viswanathan Anand";
            case "alekhine": return "Alexander Alekhine"; // NEW: Add Alekhine display name
            default: return master;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}