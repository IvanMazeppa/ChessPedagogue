// ChessMasterRatings.java - Historical Peak Ratings System
package com.example.chesspedagogue;

import java.util.HashMap;
import java.util.Map;

public class ChessMasterRatings {

    private static final Map<String, Integer> HISTORICAL_PEAK_RATINGS = new HashMap<>();

    static {
        // Initialize with historically accurate peak ratings
        HISTORICAL_PEAK_RATINGS.put("carlsen", 2882);    // Magnus Carlsen (2014)
        HISTORICAL_PEAK_RATINGS.put("kasparov", 2851);   // Garry Kasparov (1999) 
        HISTORICAL_PEAK_RATINGS.put("fischer", 2785);    // Bobby Fischer (1972)
        HISTORICAL_PEAK_RATINGS.put("karpov", 2780);     // Anatoly Karpov (1994)
        HISTORICAL_PEAK_RATINGS.put("kramnik", 2817);    // Vladimir Kramnik (2001)
        HISTORICAL_PEAK_RATINGS.put("anand", 2817);      // Viswanathan Anand (2011)
        HISTORICAL_PEAK_RATINGS.put("tal", 2705);        // Mikhail Tal (1980) 
        HISTORICAL_PEAK_RATINGS.put("capablanca", 2725); // José Capablanca (estimated)
        HISTORICAL_PEAK_RATINGS.put("lasker", 2720);     // Emanuel Lasker (estimated)
        HISTORICAL_PEAK_RATINGS.put("alekhine", 2690);   // Alexander Alekhine (estimated)
        HISTORICAL_PEAK_RATINGS.put("morphy", 2650);     // Paul Morphy (estimated)
        HISTORICAL_PEAK_RATINGS.put("botvinnik", 2680);  // Mikhail Botvinnik (estimated)
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
            return new StockfishConfig(8, 1800, 300);  // Skill 8
        } else if (targetElo <= 2000) {
            return new StockfishConfig(11, 2000, 500); // Skill 11
        } else if (targetElo <= 2200) {
            return new StockfishConfig(14, 2200, 750); // Skill 14
        } else if (targetElo <= 2400) {
            return new StockfishConfig(16, 2400, 1000); // Skill 16
        } else if (targetElo <= 2600) {
            return new StockfishConfig(18, 2600, 1500); // Skill 18
        } else if (targetElo <= 2800) {
            return new StockfishConfig(19, 2800, 2000); // Skill 19, longer think time
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