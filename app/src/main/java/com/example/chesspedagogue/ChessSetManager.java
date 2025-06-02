package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🎨 Chess Set Manager - Personalized piece selection system
 * 
 * Supports chess piece styles:
 * - Classic Staunton (default vector set)
 * - Unicode symbols (for system font rendering)
 */
public class ChessSetManager {
    private static final String TAG = "ChessSetManager";
    private static final String PREFS_NAME = "ChessSetPrefs";
    private static final String KEY_SELECTED_SET = "selected_chess_set";
    private static final String DEFAULT_SET = "staunton_classic";
    
    private static ChessSetManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private String currentSetId;
    private final Map<String, ChessSet> availableSets;
    
    /**
     * Chess Set definition
     */
    public static class ChessSet {
        public final String id;
        public final String name;
        public final String description;
        public final ChessSetType type;
        public final Map<Character, Integer> pieceResources;
        public final Map<Character, String> unicodeSymbols;
        public final boolean isDefault;
        
        public ChessSet(String id, String name, String description, ChessSetType type, boolean isDefault) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.isDefault = isDefault;
            this.pieceResources = new HashMap<>();
            this.unicodeSymbols = new HashMap<>();
        }
        
        public enum ChessSetType {
            VECTOR_DRAWABLE,
            UNICODE_FONT,
            CUSTOM_FONT
        }
    }
    
    private ChessSetManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.availableSets = new HashMap<>();
        this.currentSetId = prefs.getString(KEY_SELECTED_SET, DEFAULT_SET);
        
        initializeChessSets();
        Log.i(TAG, "🎨 Chess Set Manager initialized with set: " + currentSetId);
    }
    
    public static synchronized ChessSetManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChessSetManager(context);
        }
        return instance;
    }
    
    /**
     * Initialize all available chess sets
     */
    private void initializeChessSets() {
        // 1. Classic Staunton (existing vector pieces)
        ChessSet stauntonClassic = new ChessSet(
            "staunton_classic",
            "Classic Staunton",
            "Traditional tournament-style pieces with clean lines",
            ChessSet.ChessSetType.VECTOR_DRAWABLE,
            true
        );
        setupStauntonClassicResources(stauntonClassic);
        availableSets.put(stauntonClassic.id, stauntonClassic);
        
        // 2. Unicode Classic
        ChessSet unicodeClassic = new ChessSet(
            "unicode_classic",
            "Unicode Classic",
            "Standard Unicode chess symbols - the universal chess font",
            ChessSet.ChessSetType.UNICODE_FONT,
            false
        );
        setupUnicodeClassicSymbols(unicodeClassic);
        availableSets.put(unicodeClassic.id, unicodeClassic);
        
        
        Log.d(TAG, "✅ Initialized " + availableSets.size() + " chess sets");
    }
    
    /**
     * Set up classic Staunton piece resources (existing)
     */
    private void setupStauntonClassicResources(ChessSet set) {
        // White pieces
        set.pieceResources.put('P', R.drawable.ic_white_pawn);
        set.pieceResources.put('R', R.drawable.ic_white_rook);
        set.pieceResources.put('N', R.drawable.ic_white_knight);
        set.pieceResources.put('B', R.drawable.ic_white_bishop);
        set.pieceResources.put('Q', R.drawable.ic_white_queen);
        set.pieceResources.put('K', R.drawable.ic_white_king);
        
        // Black pieces
        set.pieceResources.put('p', R.drawable.ic_black_pawn);
        set.pieceResources.put('r', R.drawable.ic_black_rook);
        set.pieceResources.put('n', R.drawable.ic_black_knight);
        set.pieceResources.put('b', R.drawable.ic_black_bishop);
        set.pieceResources.put('q', R.drawable.ic_black_queen);
        set.pieceResources.put('k', R.drawable.ic_black_king);
    }
    
    /**
     * Set up Unicode chess symbols - using actual Unicode characters
     */
    private void setupUnicodeClassicSymbols(ChessSet set) {
        // Use actual Unicode symbols rendered as text drawables
        // This guarantees perfect accuracy to the Unicode standard
        
        // Store the actual Unicode symbols (these will be rendered by UnicodeChessPieceDrawable)
        set.unicodeSymbols.put('P', "♙"); // U+2659 White Pawn
        set.unicodeSymbols.put('R', "♖"); // U+2656 White Rook
        set.unicodeSymbols.put('N', "♘"); // U+2658 White Knight
        set.unicodeSymbols.put('B', "♗"); // U+2657 White Bishop
        set.unicodeSymbols.put('Q', "♕"); // U+2655 White Queen
        set.unicodeSymbols.put('K', "♔"); // U+2654 White King
        set.unicodeSymbols.put('p', "♟"); // U+265F Black Pawn
        set.unicodeSymbols.put('r', "♜"); // U+265C Black Rook
        set.unicodeSymbols.put('n', "♞"); // U+265E Black Knight
        set.unicodeSymbols.put('b', "♝"); // U+265D Black Bishop
        set.unicodeSymbols.put('q', "♛"); // U+265B Black Queen
        set.unicodeSymbols.put('k', "♚"); // U+265A Black King
        
        // Note: No pieceResources needed - we'll use UnicodeChessPieceDrawable directly
    }
    
    
    /**
     * Get all available chess sets
     */
    public List<ChessSet> getAvailableChessSets() {
        return new ArrayList<>(availableSets.values());
    }
    
    /**
     * Get current chess set
     */
    public ChessSet getCurrentChessSet() {
        ChessSet set = availableSets.get(currentSetId);
        if (set == null) {
            Log.w(TAG, "⚠️ Current set not found, falling back to default");
            set = availableSets.get(DEFAULT_SET);
        }
        return set;
    }
    
    /**
     * Set the active chess set
     */
    public boolean setChessSet(String setId) {
        if (!availableSets.containsKey(setId)) {
            Log.e(TAG, "❌ Chess set not found: " + setId);
            return false;
        }
        
        currentSetId = setId;
        prefs.edit().putString(KEY_SELECTED_SET, setId).apply();
        
        Log.i(TAG, "🎨 Chess set changed to: " + setId);
        return true;
    }
    
    /**
     * Get piece drawable for current set
     */
    public Drawable getPieceDrawable(char piece) {
        ChessSet currentSet = getCurrentChessSet();
        
        if (currentSet.type == ChessSet.ChessSetType.UNICODE_FONT) {
            // Create drawable from Unicode symbol
            return createUnicodeDrawable(piece, currentSet);
        } else {
            // Use vector drawable resource
            Integer resourceId = currentSet.pieceResources.get(piece);
            if (resourceId != null && resourceId != 0) {
                try {
                    return ContextCompat.getDrawable(context, resourceId);
                } catch (Exception e) {
                    Log.w(TAG, "⚠️ Failed to load piece drawable: " + piece + ", falling back");
                    return getFallbackDrawable(piece);
                }
            }
        }
        
        return getFallbackDrawable(piece);
    }
    
    /**
     * Create drawable from Unicode chess symbol
     */
    private Drawable createUnicodeDrawable(char piece, ChessSet set) {
        String unicodeSymbol = set.unicodeSymbols.get(piece);
        if (unicodeSymbol != null) {
            // Create a text drawable from the Unicode symbol
            return new UnicodeChessPieceDrawable(context, unicodeSymbol, Character.isUpperCase(piece));
        }
        return getFallbackDrawable(piece);
    }
    
    /**
     * Get fallback drawable (classic Staunton)
     */
    private Drawable getFallbackDrawable(char piece) {
        ChessSet fallbackSet = availableSets.get(DEFAULT_SET);
        Integer resourceId = fallbackSet.pieceResources.get(piece);
        if (resourceId != null && resourceId != 0) {
            return ContextCompat.getDrawable(context, resourceId);
        }
        return null;
    }
    
    /**
     * Check if a chess set has all required pieces
     */
    public boolean isChessSetComplete(String setId) {
        ChessSet set = availableSets.get(setId);
        if (set == null) return false;
        
        char[] requiredPieces = {'P', 'R', 'N', 'B', 'Q', 'K', 'p', 'r', 'n', 'b', 'q', 'k'};
        
        if (set.type == ChessSet.ChessSetType.UNICODE_FONT) {
            for (char piece : requiredPieces) {
                if (!set.unicodeSymbols.containsKey(piece)) {
                    return false;
                }
            }
        } else {
            for (char piece : requiredPieces) {
                Integer resourceId = set.pieceResources.get(piece);
                if (resourceId == null || resourceId == 0) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Get chess set by ID
     */
    public ChessSet getChessSet(String setId) {
        return availableSets.get(setId);
    }
    
    /**
     * Get current chess set ID
     */
    public String getCurrentChessSetId() {
        return currentSetId;
    }
}