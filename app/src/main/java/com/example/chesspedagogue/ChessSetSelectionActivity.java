package com.example.chesspedagogue;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * 🎨 Chess Set Selection Activity
 * 
 * Allows users to preview and select from different chess piece styles:
 * - Classic Staunton pieces
 * - Unicode symbol pieces  
 * - Ornate decorative pieces
 * - Minimalist modern pieces
 * - Tournament standard pieces
 */
public class ChessSetSelectionActivity extends AppCompatActivity {
    private static final String TAG = "ChessSetSelection";
    
    private ChessSetManager chessSetManager;
    private GridLayout chessSetsGrid;
    private String selectedSetId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_set_selection);
        
        Log.d(TAG, "🎨 Chess Set Selection Activity created");
        
        initializeViews();
        setupChessSetManager();
        loadAvailableChessSets();
    }
    
    private void initializeViews() {
        chessSetsGrid = findViewById(R.id.chessSetsGrid);
        
        Button applyButton = findViewById(R.id.buttonApplyChessSet);
        Button cancelButton = findViewById(R.id.buttonCancel);
        
        applyButton.setOnClickListener(v -> applySelectedChessSet());
        cancelButton.setOnClickListener(v -> finish());
        
        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Choose Your Chess Set");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void setupChessSetManager() {
        chessSetManager = ChessSetManager.getInstance(this);
        selectedSetId = chessSetManager.getCurrentChessSetId();
        Log.d(TAG, "🎯 Current chess set: " + selectedSetId);
    }
    
    private void loadAvailableChessSets() {
        List<ChessSetManager.ChessSet> availableSets = chessSetManager.getAvailableChessSets();
        
        Log.d(TAG, "📋 Loading " + availableSets.size() + " chess sets");
        
        for (ChessSetManager.ChessSet chessSet : availableSets) {
            addChessSetCard(chessSet);
        }
    }
    
    private void addChessSetCard(ChessSetManager.ChessSet chessSet) {
        View cardView = getLayoutInflater().inflate(R.layout.item_chess_set_card, chessSetsGrid, false);
        
        // Get views from card
        CardView card = cardView.findViewById(R.id.chessSetCard);
        TextView nameText = cardView.findViewById(R.id.textChessSetName);
        TextView descriptionText = cardView.findViewById(R.id.textChessSetDescription);
        GridLayout previewGrid = cardView.findViewById(R.id.previewGrid);
        View selectedIndicator = cardView.findViewById(R.id.selectedIndicator);
        
        // Set chess set info
        nameText.setText(chessSet.name);
        descriptionText.setText(chessSet.description);
        
        // Create piece preview
        createChessSetPreview(previewGrid, chessSet);
        
        // Set selection state
        updateCardSelection(card, selectedIndicator, chessSet.id.equals(selectedSetId));
        
        // Set click listener
        card.setOnClickListener(v -> {
            selectChessSet(chessSet.id);
            updateAllCardSelections();
        });
        
        // Set tag for identification in updateAllCardSelections
        cardView.setTag(chessSet.id);
        
        // Add to grid
        chessSetsGrid.addView(cardView);
        
        Log.d(TAG, "✅ Added chess set card: " + chessSet.name);
    }
    
    private void createChessSetPreview(GridLayout previewGrid, ChessSetManager.ChessSet chessSet) {
        // Show a few representative pieces: King, Queen, Knight, Pawn
        char[] previewPieces = {'K', 'Q', 'N', 'P', 'k', 'q', 'n', 'p'};
        
        previewGrid.setColumnCount(4);
        previewGrid.setRowCount(2);
        
        for (int i = 0; i < previewPieces.length; i++) {
            char piece = previewPieces[i];
            
            ImageView pieceView = new ImageView(this);
            pieceView.setLayoutParams(new GridLayout.LayoutParams());
            pieceView.getLayoutParams().width = dpToPx(32);
            pieceView.getLayoutParams().height = dpToPx(32);
            pieceView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            
            // Get piece drawable based on chess set type
            if (chessSet.type == ChessSetManager.ChessSet.ChessSetType.UNICODE_FONT) {
                // For Unicode sets, create a preview drawable
                String symbol = chessSet.unicodeSymbols.get(piece);
                if (symbol != null) {
                    UnicodeChessPieceDrawable drawable = new UnicodeChessPieceDrawable(
                        this, symbol, Character.isUpperCase(piece)
                    );
                    pieceView.setImageDrawable(drawable);
                }
            } else {
                // For vector sets, use resource drawables
                Integer resourceId = chessSet.pieceResources.get(piece);
                if (resourceId != null && resourceId != 0) {
                    try {
                        pieceView.setImageDrawable(ContextCompat.getDrawable(this, resourceId));
                    } catch (Exception e) {
                        // Fall back to classic set if resource not found
                        ChessSetManager.ChessSet fallback = chessSetManager.getChessSet("staunton_classic");
                        Integer fallbackResource = fallback.pieceResources.get(piece);
                        if (fallbackResource != null) {
                            pieceView.setImageDrawable(ContextCompat.getDrawable(this, fallbackResource));
                        }
                    }
                }
            }
            
            previewGrid.addView(pieceView);
        }
    }
    
    private void selectChessSet(String setId) {
        selectedSetId = setId;
        Log.d(TAG, "🎯 Selected chess set: " + setId);
    }
    
    private void updateAllCardSelections() {
        for (int i = 0; i < chessSetsGrid.getChildCount(); i++) {
            View cardView = chessSetsGrid.getChildAt(i);
            CardView card = cardView.findViewById(R.id.chessSetCard);
            View selectedIndicator = cardView.findViewById(R.id.selectedIndicator);
            
            // Get chess set ID from tag (we'll set this when creating cards)
            String setId = (String) cardView.getTag();
            updateCardSelection(card, selectedIndicator, setId != null && setId.equals(selectedSetId));
        }
    }
    
    private void updateCardSelection(CardView card, View selectedIndicator, boolean isSelected) {
        if (isSelected) {
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_selected));
            card.setCardElevation(dpToPx(8)); // Use elevation instead of stroke
            selectedIndicator.setVisibility(View.VISIBLE);
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_surface));
            card.setCardElevation(dpToPx(4)); // Default elevation
            selectedIndicator.setVisibility(View.GONE);
        }
    }
    
    private void applySelectedChessSet() {
        if (selectedSetId != null) {
            boolean success = chessSetManager.setChessSet(selectedSetId);
            
            if (success) {
                ChessSetManager.ChessSet selectedSet = chessSetManager.getChessSet(selectedSetId);
                Toast.makeText(this, 
                    "✅ Chess set changed to: " + selectedSet.name, 
                    Toast.LENGTH_SHORT).show();
                
                Log.i(TAG, "🎨 Successfully applied chess set: " + selectedSetId);
                
                // Set result to indicate change was made
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, 
                    "❌ Failed to apply chess set", 
                    Toast.LENGTH_SHORT).show();
                Log.e(TAG, "❌ Failed to apply chess set: " + selectedSetId);
            }
        } else {
            Toast.makeText(this, 
                "Please select a chess set first", 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}