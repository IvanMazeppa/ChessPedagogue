package com.example.chesspedagogue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 🚀 AI Enhancement Service for ChessPedagogue
 * 
 * This service integrates with the Python AI Enhancement Suite to provide:
 * - Personality amplification for chess masters
 * - Adaptive difficulty based on user skill
 * - Emotional intelligence in responses
 * - Historical context integration
 * 
 * The service communicates with a local Python server that runs the enhancement algorithms.
 */
public class AIEnhancementService {
    private static final String TAG = "AIEnhancementService";
    
    // Configuration2
    private static final String DEFAULT_ENHANCEMENT_URL = "http://localhost:8080/enhance";
    private static final String DEFAULT_HEALTH_URL = "http://localhost:8080/health";
    private static final int TIMEOUT_SECONDS = 10;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    // Singleton instance
    private static AIEnhancementService instance;
    
    private final Context context;
    private final OkHttpClient httpClient;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    // Configuration
    private String enhancementUrl = DEFAULT_ENHANCEMENT_URL;
    private String healthUrl = DEFAULT_HEALTH_URL;
    private boolean enhancementEnabled = true;
    
    /**
     * Enhancement request data structure
     */
    public static class EnhancementRequest {
        public String originalResponse;
        public String masterName;
        public String fen;
        public String userSkillLevel;
        public double positionEvaluation;
        public List<String> moveHistory;
        public String currentMove;
        public List<String> tacticalThemes;
        
        public EnhancementRequest(String originalResponse, String masterName, String fen) {
            this.originalResponse = originalResponse;
            this.masterName = masterName;
            this.fen = fen;
            this.userSkillLevel = "intermediate"; // Default
            this.positionEvaluation = 0.0; // Default
        }
    }
    
    /**
     * Enhancement response data structure
     */
    public static class EnhancementResponse {
        public boolean success;
        public String originalResponse;
        public String enhancedResponse;
        public String masterName;
        public String skillLevel;
        public List<String> enhancementsApplied;
        public String error;
        
        public boolean wasEnhanced() {
            return success && enhancedResponse != null && !enhancedResponse.equals(originalResponse);
        }
    }
    
    /**
     * Callback interface for enhancement results
     */
    public interface EnhancementCallback {
        void onEnhancementComplete(EnhancementResponse response);
        void onEnhancementError(String error);
    }
    
    /**
     * Private constructor for singleton
     */
    private AIEnhancementService(Context context) {
        this.context = context.getApplicationContext();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.executorService = Executors.newCachedThreadPool();
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        Log.i(TAG, "🚀 AI Enhancement Service initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized AIEnhancementService getInstance(Context context) {
        if (instance == null) {
            instance = new AIEnhancementService(context);
        }
        return instance;
    }
    
    /**
     * Configure the enhancement service
     */
    public void configure(String enhancementUrl, boolean enabled) {
        this.enhancementUrl = enhancementUrl;
        this.enhancementEnabled = enabled;
        this.healthUrl = enhancementUrl.replace("/enhance", "/health");
        
        Log.i(TAG, "🔧 Enhancement service configured: " + enhancementUrl + " (enabled: " + enabled + ")");
    }
    
    /**
     * Enhance a chess master response
     */
    public void enhanceResponse(EnhancementRequest request, EnhancementCallback callback) {
        if (!enhancementEnabled) {
            Log.d(TAG, "🔒 Enhancement disabled, returning original response");
            EnhancementResponse response = new EnhancementResponse();
            response.success = true;
            response.originalResponse = request.originalResponse;
            response.enhancedResponse = request.originalResponse;
            response.masterName = request.masterName;
            callback.onEnhancementComplete(response);
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🎯 Enhancing response for master: " + request.masterName);
                
                // Build request JSON
                JSONObject requestJson = buildRequestJson(request);
                
                // Make HTTP request
                RequestBody body = RequestBody.create(requestJson.toString(), JSON);
                Request httpRequest = new Request.Builder()
                        .url(enhancementUrl)
                        .post(body)
                        .build();
                
                Call call = httpClient.newCall(httpRequest);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.w(TAG, "❌ Enhancement request failed: " + e.getMessage());
                        mainHandler.post(() -> {
                            // Return original response on failure
                            EnhancementResponse response = new EnhancementResponse();
                            response.success = false;
                            response.originalResponse = request.originalResponse;
                            response.enhancedResponse = request.originalResponse;
                            response.error = e.getMessage();
                            callback.onEnhancementComplete(response);
                        });
                    }
                    
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String responseBody = response.body().string();
                            EnhancementResponse enhancementResponse = parseEnhancementResponse(responseBody);
                            
                            mainHandler.post(() -> {
                                if (enhancementResponse.success) {
                                    Log.d(TAG, "✅ Response enhanced successfully");
                                    callback.onEnhancementComplete(enhancementResponse);
                                } else {
                                    Log.w(TAG, "⚠️ Enhancement failed: " + enhancementResponse.error);
                                    callback.onEnhancementError(enhancementResponse.error);
                                }
                            });
                            
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Error parsing enhancement response", e);
                            mainHandler.post(() -> callback.onEnhancementError("Failed to parse response: " + e.getMessage()));
                        }
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error creating enhancement request", e);
                mainHandler.post(() -> callback.onEnhancementError("Failed to create request: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Check if enhancement service is available
     */
    public void checkServiceHealth(Callback callback) {
        Request request = new Request.Builder()
                .url(healthUrl)
                .get()
                .build();
        
        httpClient.newCall(request).enqueue(callback);
    }
    
    /**
     * Convenient method to enhance response with minimal parameters
     */
    public void enhanceResponse(String originalResponse, String masterName, String fen, 
                              EnhancementCallback callback) {
        EnhancementRequest request = new EnhancementRequest(originalResponse, masterName, fen);
        enhanceResponse(request, callback);
    }
    
    /**
     * Enhance response with additional context
     */
    public void enhanceResponse(String originalResponse, String masterName, String fen,
                              String userSkillLevel, double evaluation, List<String> moveHistory,
                              EnhancementCallback callback) {
        EnhancementRequest request = new EnhancementRequest(originalResponse, masterName, fen);
        request.userSkillLevel = userSkillLevel;
        request.positionEvaluation = evaluation;
        request.moveHistory = moveHistory;
        enhanceResponse(request, callback);
    }
    
    /**
     * Build JSON request for enhancement service
     */
    private JSONObject buildRequestJson(EnhancementRequest request) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("original_response", request.originalResponse);
        json.put("master_name", request.masterName);
        json.put("fen", request.fen);
        json.put("user_skill_level", request.userSkillLevel);
        json.put("position_evaluation", request.positionEvaluation);
        
        if (request.moveHistory != null) {
            JSONArray moveArray = new JSONArray();
            for (String move : request.moveHistory) {
                moveArray.put(move);
            }
            json.put("move_history", moveArray);
        }
        
        if (request.currentMove != null) {
            json.put("current_move", request.currentMove);
        }
        
        if (request.tacticalThemes != null) {
            JSONArray themeArray = new JSONArray();
            for (String theme : request.tacticalThemes) {
                themeArray.put(theme);
            }
            json.put("tactical_themes", themeArray);
        }
        
        return json;
    }
    
    /**
     * Parse enhancement response from JSON
     */
    private EnhancementResponse parseEnhancementResponse(String responseBody) throws JSONException {
        JSONObject json = new JSONObject(responseBody);
        
        EnhancementResponse response = new EnhancementResponse();
        response.success = json.getBoolean("success");
        
        if (response.success) {
            response.originalResponse = json.getString("original_response");
            response.enhancedResponse = json.getString("enhanced_response");
            response.masterName = json.getString("master");
            response.skillLevel = json.getString("skill_level");
            
            // Parse enhancements applied
            if (json.has("enhancements_applied")) {
                JSONArray enhancementsArray = json.getJSONArray("enhancements_applied");
                response.enhancementsApplied = new java.util.ArrayList<>();
                for (int i = 0; i < enhancementsArray.length(); i++) {
                    response.enhancementsApplied.add(enhancementsArray.getString(i));
                }
            }
        } else {
            response.error = json.optString("error", "Unknown error");
        }
        
        return response;
    }
    
    /**
     * Enable or disable enhancement
     */
    public void setEnhancementEnabled(boolean enabled) {
        this.enhancementEnabled = enabled;
        Log.i(TAG, "🔧 Enhancement " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Check if enhancement is enabled
     */
    public boolean isEnhancementEnabled() {
        return enhancementEnabled;
    }
    
    /**
     * Get current enhancement URL
     */
    public String getEnhancementUrl() {
        return enhancementUrl;
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}