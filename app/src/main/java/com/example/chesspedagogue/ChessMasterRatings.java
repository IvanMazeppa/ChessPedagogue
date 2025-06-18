// ChessMasterRatings.java - Historical Peak Ratings System
package com.example.chesspedagogue;

import java.util.HashMap;
import java.util.Map;

public class ChessMasterRatings {

    private static final Map<String, Integer> HISTORICAL_PEAK_RATINGS = new HashMap<>();

    static {
        // Initialize with historically accurate peak ratings
        
        // Modern Era (Official FIDE ratings)
        HISTORICAL_PEAK_RATINGS.put("carlsen", 2882);    // Magnus Carlsen (2014) - Highest official rating ever
        HISTORICAL_PEAK_RATINGS.put("kasparov", 2851);   // Garry Kasparov (1999) - Peak during reign
        HISTORICAL_PEAK_RATINGS.put("kramnik", 2817);    // Vladimir Kramnik (2001) - Peak after defeating Kasparov
        HISTORICAL_PEAK_RATINGS.put("anand", 2817);      // Viswanathan Anand (2011) - Peak during world championship
        HISTORICAL_PEAK_RATINGS.put("karpov", 2780);     // Anatoly Karpov (1994) - Estimated peak
        HISTORICAL_PEAK_RATINGS.put("fischer", 2785);    // Bobby Fischer (1972) - Peak before retirement
        
        // Contemporary Masters
        HISTORICAL_PEAK_RATINGS.put("hikaru", 2816);     // Hikaru Nakamura (2015) - Peak FIDE rating
        HISTORICAL_PEAK_RATINGS.put("nakamura", 2816);   // Alternative name for Hikaru
        HISTORICAL_PEAK_RATINGS.put("gukesh", 2794);     // Gukesh Dommaraju (2024) - Current rising star
        HISTORICAL_PEAK_RATINGS.put("dommaraju", 2794);  // Alternative name for Gukesh
        HISTORICAL_PEAK_RATINGS.put("short", 2680);      // Nigel Short (1993) - Peak during world championship cycle
        HISTORICAL_PEAK_RATINGS.put("nigel_short", 2680); // Alternative name for Nigel Short
        
        // Classical Era Legends (Expert estimates based on game quality)
        HISTORICAL_PEAK_RATINGS.put("tal", 2705);        // Mikhail Tal (1980) - Dynamic attacking genius
        HISTORICAL_PEAK_RATINGS.put("capablanca", 2725); // José Capablanca (1920s) - Natural endgame master
        HISTORICAL_PEAK_RATINGS.put("lasker", 2720);     // Emanuel Lasker (1910s) - Longest reigning champion
        HISTORICAL_PEAK_RATINGS.put("alekhine", 2690);   // Alexander Alekhine (1930s) - Combinational master
        HISTORICAL_PEAK_RATINGS.put("botvinnik", 2680);  // Mikhail Botvinnik (1950s) - Scientific approach
        HISTORICAL_PEAK_RATINGS.put("petrosian", 2645);  // Tigran Petrosian (1960s) - Positional master
        HISTORICAL_PEAK_RATINGS.put("morphy", 2650);     // Paul Morphy (1858) - Romantic era genius
        HISTORICAL_PEAK_RATINGS.put("nimzowitsch", 2620); // Aron Nimzowitsch (1920s) - Hypermodern pioneer
    }

    /**
     * Get the historical peak rating for a chess master
     */
    public static int getPeakRating(String master) {
        return HISTORICAL_PEAK_RATINGS.getOrDefault(master.toLowerCase(), 2600);
    }

    /**
     * Get all available masters with their ratings
     */
    public static Map<String, Integer> getAllPeakRatings() {
        return new HashMap<>(HISTORICAL_PEAK_RATINGS);
    }

    /**
     * Convert ELO rating to appropriate Stockfish configuration
     * This fixes the ELO accuracy issue!
     */
    public static StockfishConfig getStockfishConfigForElo(int targetElo) {
        // More accurate ELO to Stockfish mapping
        if (targetElo <= 1200) {
            return new StockfishConfig(0, 1200, 50);   // Skill 0, very limited time
        } else if (targetElo <= 1400) {
            return new StockfishConfig(2, 1400, 100);  // Skill 2
        } else if (targetElo <= 1600) {
            return new StockfishConfig(5, 1600, 200);  // Skill 5  
        } else if (targetElo <= 1800) {
            return new StockfishConfig(12, 1800, 500);  // Skill 12 - Much stronger for 1750+ ELO
        } else if (targetElo <= 2000) {
            return new StockfishConfig(14, 2000, 750); // Skill 14
        } else if (targetElo <= 2200) {
            return new StockfishConfig(16, 2200, 1000); // Skill 16
        } else if (targetElo <= 2400) {
            return new StockfishConfig(18, 2400, 1200); // Skill 18
        } else if (targetElo <= 2600) {
            return new StockfishConfig(19, 2600, 1500); // Skill 19
        } else if (targetElo <= 2800) {
            return new StockfishConfig(20, 2800, 2000); // Skill 20, longer think time
        } else {
            return new StockfishConfig(20, targetElo, 3000); // Maximum strength for 2800+
        }
    }

    /**
     * Configuration class for Stockfish settings
     */
    public static class StockfishConfig {
        public final int skillLevel;
        public final int targetElo;
        public final int thinkTimeMs;

        public StockfishConfig(int skillLevel, int targetElo, int thinkTimeMs) {
            this.skillLevel = skillLevel;
            this.targetElo = targetElo;
            this.thinkTimeMs = thinkTimeMs;
        }

        @Override
        public String toString() {
            return String.format("StockfishConfig{skill=%d, elo=%d, time=%dms}",
                    skillLevel, targetElo, thinkTimeMs);
        }
    }
}