package com.example.chesspedagogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Consolidated TTS service with proper chunk ordering and ENHANCED cleanup
 * FIXED: Proper resource management to prevent silent failures after multiple uses
 */
public class OpenAITTSService {
    private static final String TAG = "OpenAITTSService";
    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";

    // Voice constants
    public static final String VOICE_GRANDMASTER = "onyx";
    public static final String VOICE_TUTOR = "nova";
    public static final String VOICE_SHIMMER = "shimmer";
    public static final String VOICE_ECHO = "echo";
    public static final String VOICE_ALLOY = "alloy";
    public static final String VOICE_FABLE = "fable";
    public static final String VOICE_ONYX = "onyx";
    public static final String VOICE_NOVA = "nova";

    // Model constants
    public static final String MODEL_STANDARD = "tts-1";
    public static final String MODEL_PREMIUM = "tts-1-hd";

    private static OpenAITTSService instance;

    private final OkHttpClient httpClient;
    private final Context context;
    private final Handler mainHandler;
    private final ExecutorService executorService;
    private String apiKey;
    private MediaPlayer currentPlayer;
    private String voice = VOICE_GRANDMASTER;
    private String model = MODEL_STANDARD;
    private boolean interruptRequested = false;
    private boolean isSpeaking = false;
    private SharedPreferences prefs;

    // ENHANCED: Chunk management with better cleanup
    private final ConcurrentLinkedQueue<ChunkPlaybackItem> chunkQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isPlayingChunks = new AtomicBoolean(false);
    private final AtomicInteger chunkIdCounter = new AtomicInteger(0);
    private final Map<Integer, ChunkPlaybackItem> pendingChunks = new HashMap<>();
    private final AtomicInteger nextChunkToPlay = new AtomicInteger(0); // FIXED: Use AtomicInteger

    // ADDED: Track active players for cleanup
    private final Map<Integer, MediaPlayer> activePlayers = new HashMap<>();

    private SpeechCallback speechCallback;

    // Inner class for chunk management
    private static class ChunkPlaybackItem {
        final int chunkId;
        final File audioFile;
        final String text;
        final TTSCallback callback;
        final boolean isFinalChunk;

        ChunkPlaybackItem(int chunkId, File audioFile, String text, TTSCallback callback, boolean isFinalChunk) {
            this.chunkId = chunkId;
            this.audioFile = audioFile;
            this.text = text;
            this.callback = callback;
            this.isFinalChunk = isFinalChunk;
        }
    }

    public OpenAITTSService(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("ChessPedagoguePrefs", Context.MODE_PRIVATE);
        this.httpClient = OpenAIService.getInstance().getHttpClient();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public static synchronized OpenAITTSService getInstance(Context context) {
        if (instance == null) {
            instance = new OpenAITTSService(context);
        }
        return instance;
    }

    // Simple speech callback interface
    public interface SpeechCallback {
        void onSpeechCompleted(String text);
        void onSpeechInterrupted();
    }

    public interface OnSpeechCompletedListener {
        void onSpeechCompleted();
    }

    public void setSpeechCallback(SpeechCallback callback) {
        this.speechCallback = callback;
    }

    /**
     * ENHANCED: Main speak method with proper cleanup between sessions
     */
    public void speak(String text, OnSpeechCompletedListener listener) {
        Log.d(TAG, "Speaking text, length: " + (text != null ? text.length() : 0));

        if (text == null || text.isEmpty()) {
            if (listener != null) {
                listener.onSpeechCompleted();
            }
            return;
        }

        // CRITICAL: Clean up any previous session before starting new one
        cleanupPreviousSession();

        isSpeaking = true;
        interruptRequested = false;

        // Reset chunk counter for this speech session
        chunkIdCounter.set(0);
        nextChunkToPlay.set(0);
        chunkQueue.clear();
        pendingChunks.clear();

        // Use a single TTSCallback that manages ordering
        TTSCallback orderingCallback = new TTSCallback() {
            @Override
            public void onSpeechStarted() {
                Log.d(TAG, "✅ Speech started successfully");
            }

            @Override
            public void onSpeechReady(File audioFile) {
                Log.d(TAG, "✅ Audio file ready: " + audioFile.getName());
            }

            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "✅ All chunks completed successfully");
                mainHandler.post(() -> {
                    cleanupCurrentSession(); // ADDED: Clean up after completion
                    isSpeaking = false;
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ TTS Error: " + errorMessage);
                mainHandler.post(() -> {
                    cleanupCurrentSession(); // ADDED: Clean up after error
                    isSpeaking = false;
                    if (listener != null) {
                        listener.onSpeechCompleted();
                    }
                });
            }
        };

        // Generate a single chunk for the entire text
        generateTTSChunk(text, 0, true, orderingCallback);
    }

    /**
     * ADDED: Clean up any previous session's resources
     */
    private void cleanupPreviousSession() {
        try {
            Log.d(TAG, "🧹 Cleaning up previous session...");

            // Stop any active playback
            if (currentPlayer != null) {
                try {
                    if (currentPlayer.isPlaying()) {
                        currentPlayer.stop();
                    }
                    currentPlayer.release();
                    currentPlayer = null;
                } catch (Exception e) {
                    Log.w(TAG, "Error cleaning up current player", e);
                }
            }

            // Clean up all active players
            synchronized (activePlayers) {
                for (MediaPlayer player : activePlayers.values()) {
                    try {
                        if (player.isPlaying()) {
                            player.stop();
                        }
                        player.release();
                    } catch (Exception e) {
                        Log.w(TAG, "Error releasing active player", e);
                    }
                }
                activePlayers.clear();
            }

            // Clear all pending work
            chunkIdCounter.set(0);
            nextChunkToPlay.set(0);
            chunkQueue.clear();
            pendingChunks.clear();
            isPlayingChunks.set(false);

            Log.d(TAG, "✅ Previous session cleaned up");
        } catch (Exception e) {
            Log.e(TAG, "Error during session cleanup", e);
        }
    }

    /**
     * ADDED: Clean up current session's resources
     */
    private void cleanupCurrentSession() {
        try {
            // Clean up temporary audio files
            File cacheDir = new File(context.getCacheDir(), "tts_cache");
            if (cacheDir.exists()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().startsWith("tts_") &&
                                System.currentTimeMillis() - file.lastModified() > 60000) { // Older than 1 minute
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error cleaning up audio files", e);
        }
    }

    /**
     * 🎭 ENHANCED: Create voice instructions with more focus on energy/emotion than specific accents
     */
    private String createNaturalVoiceInstructions(String master) {
        try {
            // Focus more on energy and emotion since accents might not work reliably
            switch (master.toLowerCase()) {
                case "tal":
                    return "Speak with passionate enthusiasm and high energy. Sound warm, creative, and genuinely excited about chess. Use an animated, expressive delivery.";

                case "fischer":
                    return "Speak with intense conviction and absolute certainty. Sound demanding, precise, and uncompromising. Use a direct, authoritative tone with no hesitation.";

                case "kasparov":
                    return "Speak with dynamic passion and fierce determination. Sound energetic, competitive, and compelling with strong conviction.";

                case "karpov":
                    return "Speak with calm confidence and measured wisdom. Sound diplomatic, patient, and quietly authoritative.";

                case "kramnik":
                    return "Speak with analytical precision and systematic clarity. Sound methodical, technical, and thoughtfully measured.";

                case "capablanca":
                    return "Speak with elegant confidence and natural authority. Sound effortlessly refined and gracefully assured.";

                case "carlsen":
                    return "Speak with modern confidence and relaxed authority. Sound pragmatic, adaptable, and naturally assured.";

                case "alekhine":
                    return "Speak with sophisticated intelligence and cultured authority. Sound intellectually engaging and refined.";

                default:
                    return "Speak with the wisdom and authority of an experienced chess master.";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating voice instructions for " + master, e);
            return "Speak with natural confidence and chess master authority.";
        }
    }

    /**
     * FIXED: Try to play the next chunks in order with proper debugging
     */
    private void tryPlayNextChunks() {
        synchronized (pendingChunks) {
            Log.d(TAG, "🔄 tryPlayNextChunks - pendingChunks size: " + pendingChunks.size() +
                    ", nextChunkToPlay: " + nextChunkToPlay.get() +
                    ", chunkQueue size: " + chunkQueue.size() +
                    ", isPlayingChunks: " + isPlayingChunks.get());

            // Add any ready chunks to the queue
            while (pendingChunks.containsKey(nextChunkToPlay.get())) {
                ChunkPlaybackItem chunk = pendingChunks.remove(nextChunkToPlay.get());
                chunkQueue.offer(chunk);
                Log.d(TAG, "✅ Added chunk " + nextChunkToPlay.get() + " to playback queue");
                nextChunkToPlay.incrementAndGet();
            }

            // Start playback if not already playing
            if (!isPlayingChunks.get() && !chunkQueue.isEmpty()) {
                Log.d(TAG, "🎵 Starting playback - queue size: " + chunkQueue.size());
                playNextChunk();
            } else if (isPlayingChunks.get()) {
                Log.d(TAG, "⏳ Already playing chunks, queue size: " + chunkQueue.size());
            } else if (chunkQueue.isEmpty()) {
                Log.d(TAG, "📭 Queue is empty, waiting for more chunks");
            }
        }
    }



    /**
     * ENHANCED: Play the next chunk in the queue with better error handling
     */
    private void playNextChunk() {
        if (interruptRequested) {
            cleanupPlayback();
            return;
        }

        ChunkPlaybackItem chunk = chunkQueue.poll();
        if (chunk == null) {
            isPlayingChunks.set(false);
            return;
        }

        isPlayingChunks.set(true);

        mainHandler.post(() -> {
            try {
                Log.d(TAG, "▶️ Playing chunk " + chunk.chunkId + " (" + chunk.audioFile.getName() + ")");

                if (chunk.callback != null) {
                    chunk.callback.onSpeechReady(chunk.audioFile);
                }

                MediaPlayer player = new MediaPlayer();

                // ADDED: Track this player for cleanup
                synchronized (activePlayers) {
                    activePlayers.put(chunk.chunkId, player);
                }

                player.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                );

                player.setOnPreparedListener(mp -> {
                    currentPlayer = mp;
                    mp.start();
                    Log.d(TAG, "▶️ Started playing chunk " + chunk.chunkId);
                    if (chunk.callback != null) {
                        chunk.callback.onSpeechStarted();
                    }
                });

                player.setOnCompletionListener(mp -> {
                    Log.d(TAG, "✅ Chunk " + chunk.chunkId + " completed successfully");

                    // ENHANCED: Proper cleanup
                    try {
                        mp.release();
                        synchronized (activePlayers) {
                            activePlayers.remove(chunk.chunkId);
                        }
                        currentPlayer = null;
                    } catch (Exception e) {
                        Log.w(TAG, "Error during player cleanup", e);
                    }

                    // Play next chunk or finish
                    if (chunk.isFinalChunk && chunkQueue.isEmpty()) {
                        isPlayingChunks.set(false);
                        isSpeaking = false;
                        if (chunk.callback != null) {
                            chunk.callback.onSpeechCompleted();
                        }
                    } else {
                        // Small delay between chunks for natural speech
                        mainHandler.postDelayed(() -> playNextChunk(), 50);
                    }
                });

                player.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "❌ MediaPlayer error for chunk " + chunk.chunkId + ": " + what + ", " + extra);

                    // ENHANCED: Proper error cleanup
                    try {
                        mp.release();
                        synchronized (activePlayers) {
                            activePlayers.remove(chunk.chunkId);
                        }
                        currentPlayer = null;
                    } catch (Exception e) {
                        Log.w(TAG, "Error during error cleanup", e);
                    }

                    if (chunk.callback != null) {
                        chunk.callback.onError("Playback error: " + what);
                    }

                    // Try to continue with next chunk
                    mainHandler.postDelayed(() -> playNextChunk(), 100);
                    return true;
                });

                player.setDataSource(context, Uri.fromFile(chunk.audioFile));
                player.prepareAsync();

            } catch (Exception e) {
                Log.e(TAG, "❌ Error playing chunk " + chunk.chunkId, e);
                if (chunk.callback != null) {
                    chunk.callback.onError("Playback error: " + e.getMessage());
                }

                // Try to continue with next chunk
                mainHandler.postDelayed(() -> playNextChunk(), 100);
            }
        });
    }

    /**
     * Enhanced streaming TTS with proper chunk ordering
     */
    public void speakStreamingText(String text, TTSCallback callback) {
        Log.d(TAG, "🎤 Speaking streaming text with chunking");

        if (text == null || text.isEmpty()) {
            if (callback != null) {
                callback.onError("Empty text");
            }
            return;
        }

        // CRITICAL: Clean up before starting
        cleanupPreviousSession();

        isSpeaking = true;
        interruptRequested = false;

        // Reset chunk management
        chunkIdCounter.set(0);
        nextChunkToPlay.set(0);
        chunkQueue.clear();
        pendingChunks.clear();

        // Split text into natural chunks
        String[] sentences = text.split("(?<=[.!?])\\s+");

        for (int i = 0; i < sentences.length; i++) {
            String chunk = sentences[i];
            boolean isFinal = (i == sentences.length - 1);

            // Generate TTS for each chunk with proper ordering
            generateTTSChunk(chunk, i, isFinal, callback);
        }
    }

    /**
     * Simple direct TTS without chunking complexity
     */
    public void speakDirect(String text, TTSCallback callback) {
        generateTTSChunk(text, 0, true, callback);
    }

    /**
     * NEW: Fallback method without instructions if the enhanced version fails
     */
    private void generateTTSChunkFallback(String text, int chunkId, boolean isFinalChunk,
                                          TTSCallback callback, String voiceToUse) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "🔄 Using fallback TTS without instructions");

                JSONObject payload = new JSONObject();
                payload.put("model", "gpt-4o-mini-tts");
                payload.put("voice", voiceToUse);
                payload.put("speed", 1.0);
                payload.put("input", text);
                // NO instructions parameter for fallback

                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        payload.toString()
                );

                Request request = new Request.Builder()
                        .url(TTS_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        byte[] audioData = response.body().bytes();
                        File audioFile = saveAudioToFile(audioData);

                        ChunkPlaybackItem chunkItem = new ChunkPlaybackItem(
                                chunkId, audioFile, text, callback, isFinalChunk);

                        synchronized (pendingChunks) {
                            pendingChunks.put(chunkId, chunkItem);
                            tryPlayNextChunks();
                        }

                        Log.d(TAG, "✅ Fallback TTS successful");
                    } else {
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError("Fallback TTS failed"));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Fallback TTS error", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("Fallback TTS error: " + e.getMessage()));
                }
            }
        });
    }

    /**
     * DEBUGGING VERSION: Enhanced TTS chunk generation with extensive logging for Tal
     * Add this method to OpenAITTSService.java to replace the existing generateTTSChunk method
     */
    private void generateTTSChunk(String text, int chunkId, boolean isFinalChunk, TTSCallback callback) {
        executorService.execute(() -> {
            try {
                String voiceToUse = getVoiceForCurrentMaster();
                String selectedMaster = getCurrentChessMaster();

                // CRITICAL DEBUG: Log everything for Tal specifically
                Log.d(TAG, "🔍 DEBUG TTS GENERATION:");
                Log.d(TAG, "   Master: " + selectedMaster);
                Log.d(TAG, "   Voice: " + voiceToUse);
                Log.d(TAG, "   Text: " + text.substring(0, Math.min(50, text.length())) + "...");
                Log.d(TAG, "   Chunk ID: " + chunkId);

                // FORCE Tal to use enhanced instructions
                boolean isTal = "tal".equalsIgnoreCase(selectedMaster);
                Log.d(TAG, "   Is Tal: " + isTal);

                String modelToUse = "gpt-4o-mini-tts";

                // Create enhanced instructions with special Tal handling
                String enhancedInstructions = null;
                if (isTal) {
                    // FORCE Tal instructions manually for debugging
                    enhancedInstructions = "Speak with a warm Latvian-Russian accent. " +
                            "Roll your 'r' sounds softly and pronounce vowels with Slavic warmth. " +
                            "Use passionate, enthusiastic delivery that shows genuine love for chess. " +
                            "Let your excitement bubble through when discussing tactics and sacrifices. " +
                            "Sound like Mikhail Tal from Latvia with his characteristic warmth and creativity.";
                    Log.d(TAG, "🎭 FORCED TAL INSTRUCTIONS: " + enhancedInstructions);
                } else {
                    // Use the enhanced method for other masters
                    enhancedInstructions = createAccentSpecificInstructions(selectedMaster, text);
                    Log.d(TAG, "🎭 ENHANCED INSTRUCTIONS FOR " + selectedMaster + ": " + enhancedInstructions);
                }

                // Build the TTS request
                JSONObject payload = new JSONObject();
                payload.put("model", modelToUse);
                payload.put("voice", voiceToUse);
                payload.put("speed", getSpeedForMaster(selectedMaster));
                payload.put("input", text);

                // ALWAYS add instructions for debugging
                if (enhancedInstructions != null && !enhancedInstructions.trim().isEmpty()) {
                    payload.put("instructions", enhancedInstructions);
                    Log.d(TAG, "✅ INSTRUCTIONS ADDED TO PAYLOAD");
                } else {
                    Log.e(TAG, "❌ NO INSTRUCTIONS - THIS IS THE PROBLEM!");
                }

                // DEBUG: Log the complete payload
                Log.d(TAG, "📝 COMPLETE TTS PAYLOAD:");
                Log.d(TAG, payload.toString(2));

                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        payload.toString()
                );

                Request request = new Request.Builder()
                        .url(TTS_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build();

                Log.d(TAG, "🚀 SENDING TTS REQUEST FOR " + selectedMaster.toUpperCase());

                try (Response response = httpClient.newCall(request).execute()) {
                    Log.d(TAG, "📡 TTS API RESPONSE CODE: " + response.code());

                    if (!response.isSuccessful()) {
                        String error = "TTS API error: " + response.code();
                        String errorBody = "";
                        if (response.body() != null) {
                            errorBody = response.body().string();
                            Log.e(TAG, "❌ TTS API ERROR DETAILS: " + errorBody);
                        }

                        // Check if it's an instructions-related error
                        if (errorBody.contains("instructions") && enhancedInstructions != null) {
                            Log.w(TAG, "🔄 RETRYING WITHOUT INSTRUCTIONS DUE TO API ERROR");
                            generateTTSChunkFallback(text, chunkId, isFinalChunk, callback, voiceToUse);
                            return;
                        }

                        if (callback != null) {
                            mainHandler.post(() -> callback.onError(error));
                        }
                        return;
                    }

                    if (response.body() == null) {
                        Log.e(TAG, "❌ TTS API RETURNED NULL BODY");
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError("Empty response from TTS API"));
                        }
                        return;
                    }

                    byte[] audioData = response.body().bytes();
                    File audioFile = saveAudioToFile(audioData);

                    Log.d(TAG, "✅ SUCCESSFULLY GENERATED AUDIO FOR " + selectedMaster.toUpperCase());
                    Log.d(TAG, "   File: " + audioFile.getName() + " (" + audioData.length + " bytes)");

                    // Create chunk item and add to pending
                    ChunkPlaybackItem chunkItem = new ChunkPlaybackItem(
                            chunkId, audioFile, text, callback, isFinalChunk);

                    synchronized (pendingChunks) {
                        if (chunkId == 0) {
                            nextChunkToPlay.set(0);
                            Log.d(TAG, "🔄 Reset nextChunkToPlay to 0 for new speech");
                        }

                        pendingChunks.put(chunkId, chunkItem);
                        Log.d(TAG, "📦 Added chunk " + chunkId + " for " + selectedMaster + " to pending queue");

                        tryPlayNextChunks();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ CRITICAL TTS ERROR FOR " + getCurrentChessMaster(), e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("TTS error: " + e.getMessage()));
                }
            }
        });
    }

    /**
     * CORRECTED: Voice selection with proper male voice for Tal
     * Replace the getVoiceForCurrentMaster method in OpenAITTSService.java
     */
    private String getVoiceForCurrentMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        String currentMaster = masterPrefs.getString("selected_master", "tal");
        String voiceOverride = prefs.getString("voice_style", "auto");

        Log.d(TAG, "🎤 VOICE SELECTION DEBUG:");
        Log.d(TAG, "   Current Master: " + currentMaster);
        Log.d(TAG, "   Voice Override: " + voiceOverride);

        if (!"auto".equals(voiceOverride)) {
            Log.d(TAG, "   Using Override Voice: " + voiceOverride);
            return voiceOverride;
        }

        // CORRECTED: Proper male voices for all chess masters
        String selectedVoice;
        switch (currentMaster.toLowerCase()) {
            case "tal":
                selectedVoice = "echo";  // FIXED: Warm, expressive MALE voice for Tal
                Log.d(TAG, "   TAL GETS ECHO VOICE (male, warm, expressive)");
                break;
            case "fischer":
                selectedVoice = "onyx";  // Strong, authoritative MALE voice for Fischer
                Log.d(TAG, "   FISCHER GETS ONYX VOICE (male, strong, authoritative)");
                break;
            case "kasparov":
                selectedVoice = "alloy"; // Dynamic MALE voice for Kasparov
                Log.d(TAG, "   KASPAROV GETS ALLOY VOICE (male, dynamic)");
                break;
            case "karpov":
                selectedVoice = "fable"; // Calm, refined MALE voice for Karpov
                Log.d(TAG, "   KARPOV GETS FABLE VOICE (male, calm, refined)");
                break;
            case "kramnik":
                selectedVoice = "alloy"; // Technical, precise MALE voice for Kramnik
                Log.d(TAG, "   KRAMNIK GETS ALLOY VOICE (male, technical)");
                break;
            case "capablanca":
                selectedVoice = "fable"; // Elegant MALE voice for Capablanca
                Log.d(TAG, "   CAPABLANCA GETS FABLE VOICE (male, elegant)");
                break;
            case "alekhine":
                selectedVoice = "echo";  // Sophisticated MALE voice for Alekhine
                Log.d(TAG, "   ALEKHINE GETS ECHO VOICE (male, sophisticated)");
                break;
            case "carlsen":
                selectedVoice = "alloy"; // Modern MALE voice for Carlsen
                Log.d(TAG, "   CARLSEN GETS ALLOY VOICE (male, modern)");
                break;
            case "morphy":
                selectedVoice = "fable"; // Dignified MALE voice for Morphy
                Log.d(TAG, "   MORPHY GETS FABLE VOICE (male, dignified)");
                break;
            case "lasker":
                selectedVoice = "echo";  // Thoughtful MALE voice for Lasker
                Log.d(TAG, "   LASKER GETS ECHO VOICE (male, thoughtful)");
                break;
            case "anand":
                selectedVoice = "alloy"; // Friendly MALE voice for Anand
                Log.d(TAG, "   ANAND GETS ALLOY VOICE (male, friendly)");
                break;
            case "botvinnik":
                selectedVoice = "onyx";  // Authoritative MALE voice for Botvinnik
                Log.d(TAG, "   BOTVINNIK GETS ONYX VOICE (male, authoritative)");
                break;
            default:
                selectedVoice = "alloy"; // Default MALE voice
                break;
        }

        Log.d(TAG, "   Final Voice Selection: " + selectedVoice + " (MALE)");
        return selectedVoice;
    }

    /**
     * DEBUG: Enhanced current master detection
     */
    private String getCurrentChessMaster() {
        SharedPreferences masterPrefs = context.getSharedPreferences("ChessAppPrefs", Context.MODE_PRIVATE);
        String master = masterPrefs.getString("selected_master", "tal");

        Log.d(TAG, "🎭 CURRENT CHESS MASTER: " + master);

        return master;
    }

    /**
     * ENHANCED: Create accent-specific instructions with Tal debugging
     */
    private String createAccentSpecificInstructions(String master, String text) {
        Log.d(TAG, "🎨 CREATING ACCENT INSTRUCTIONS FOR: " + master);

        try {
            StringBuilder instructions = new StringBuilder();

            switch (master.toLowerCase()) {
                case "tal":
                    Log.d(TAG, "   Processing TAL accent instructions...");
                    instructions.append("Speak with a warm Latvian-Russian accent. ");
                    instructions.append("Roll your 'r' sounds softly and pronounce vowels with Slavic warmth. ");
                    instructions.append("Use passionate, enthusiastic delivery that shows genuine love for chess. ");
                    instructions.append("Let your excitement bubble through when discussing tactics and sacrifices. ");
                    instructions.append("Sound like Mikhail Tal from Latvia with his characteristic warmth and creativity.");
                    Log.d(TAG, "   ✅ TAL INSTRUCTIONS CREATED");
                    break;

                case "fischer":
                    Log.d(TAG, "   Processing FISCHER accent instructions...");
                    instructions.append("Speak with a confident American accent from New York. ");
                    instructions.append("Use intense, demanding delivery with absolute precision. ");
                    instructions.append("Emphasize words with unwavering conviction and authority. ");
                    instructions.append("Sound supremely confident and uncompromising. ");
                    instructions.append("Deliver every word with the perfectionist intensity of Bobby Fischer.");
                    Log.d(TAG, "   ✅ FISCHER INSTRUCTIONS CREATED");
                    break;

                case "kasparov":
                    instructions.append("Speak with a dynamic Russian accent from Baku. ");
                    instructions.append("Use passionate, energetic delivery with fierce determination. ");
                    instructions.append("Roll 'r' sounds distinctly and emphasize strong consonants. ");
                    instructions.append("Show competitive fire and intensity in every word. ");
                    instructions.append("Speak with the commanding presence of Garry Kasparov.");
                    break;

                case "karpov":
                    instructions.append("Speak with a refined, diplomatic Russian accent. ");
                    instructions.append("Use calm, measured delivery with quiet authority. ");
                    instructions.append("Maintain elegant pronunciation and thoughtful pauses. ");
                    instructions.append("Sound patient, wise, and diplomatically confident. ");
                    instructions.append("Speak with the sophisticated elegance of Anatoly Karpov.");
                    break;

                case "kramnik":
                    instructions.append("Speak with a modern Russian accent with precise articulation. ");
                    instructions.append("Use methodical, analytical delivery with technical precision. ");
                    instructions.append("Emphasize logical flow and systematic thinking. ");
                    instructions.append("Sound thoroughly analytical and scientifically precise. ");
                    instructions.append("Speak with the technical mastery of Vladimir Kramnik.");
                    break;

                default:
                    instructions.append("Speak with the natural confidence and wisdom of a chess grandmaster. ");
                    instructions.append("Use authoritative delivery that conveys deep chess knowledge.");
                    break;
            }

            String result = instructions.toString().trim();
            Log.d(TAG, "🎯 FINAL INSTRUCTIONS FOR " + master + " (" + result.length() + " chars):");
            Log.d(TAG, "   " + result.substring(0, Math.min(100, result.length())) + "...");

            return result;

        } catch (Exception e) {
            Log.e(TAG, "❌ ERROR CREATING ACCENT INSTRUCTIONS FOR " + master, e);
            return "Speak with the natural confidence and authority of " + master + ", the chess grandmaster.";
        }
    }

    /**
     * ENHANCED: Get speech speed for master
     */
    private double getSpeedForMaster(String master) {
        double speed;
        switch (master.toLowerCase()) {
            case "tal":
                speed = 1.1;  // Slightly faster, enthusiastic
                break;
            case "fischer":
                speed = 0.95; // Slightly slower, deliberate and precise
                break;
            case "kasparov":
                speed = 1.15; // Faster, energetic
                break;
            case "karpov":
                speed = 0.9;  // Slower, thoughtful
                break;
            case "kramnik":
                speed = 0.95; // Deliberate, methodical
                break;
            default:
                speed = 1.0;  // Standard speed
                break;
        }

        Log.d(TAG, "⚡ SPEED FOR " + master + ": " + speed);
        return speed;
    }

    /**
     * Check if currently speaking
     */
    public boolean isSpeaking() {
        return isSpeaking;
    }

    /**
     * Stop/interrupt speech
     */
    public void stopSpeech() {
        interrupt();
    }

    /**
     * ENHANCED: Interrupt current speech with thorough cleanup and better error handling
     */
    public void interrupt() {
        Log.d(TAG, "🛑 Interrupting speech with full cleanup");
        interruptRequested = true;

        // Clear all pending chunks
        chunkQueue.clear();
        pendingChunks.clear();

        // Stop current playback with better error handling
        if (currentPlayer != null) {
            try {
                // SAFER: Check state before calling isPlaying()
                if (currentPlayer.isPlaying()) {
                    currentPlayer.stop();
                }
            } catch (IllegalStateException e) {
                // Player might be in an invalid state - just release it
                Log.w(TAG, "MediaPlayer in invalid state during interrupt, releasing");
            } finally {
                try {
                    currentPlayer.release();
                } catch (Exception e) {
                    Log.w(TAG, "Error releasing MediaPlayer during interrupt", e);
                }
                currentPlayer = null;
            }
        }

        // ENHANCED: Stop all active players with better error handling
        synchronized (activePlayers) {
            for (MediaPlayer player : activePlayers.values()) {
                try {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                } catch (IllegalStateException e) {
                    // Player in invalid state, skip to release
                    Log.w(TAG, "Active player in invalid state, skipping stop");
                } finally {
                    try {
                        player.release();
                    } catch (Exception e) {
                        Log.w(TAG, "Error releasing active player", e);
                    }
                }
            }
            activePlayers.clear();
        }

        isSpeaking = false;
        isPlayingChunks.set(false);

        if (speechCallback != null) {
            speechCallback.onSpeechInterrupted();
        }

        Log.d(TAG, "✅ Speech interruption completed safely");
    }

    /**
     * Cleanup playback resources
     */
    private void cleanupPlayback() {
        chunkQueue.clear();
        pendingChunks.clear();
        isPlayingChunks.set(false);
        isSpeaking = false;

        synchronized (activePlayers) {
            activePlayers.clear();
        }
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Save audio data to file
     */
    private File saveAudioToFile(byte[] audioData) throws IOException {
        File cacheDir = new File(context.getCacheDir(), "tts_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        String fileName = "tts_" + UUID.randomUUID().toString() + ".mp3";
        File audioFile = new File(cacheDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
            fos.write(audioData);
        }

        return audioFile;
    }

    /**
     * Legacy method support
     */
    public void speak(String text) {
        speak(text, (OnSpeechCompletedListener) null);
    }

    public void speak(String text, String voice, String model, TTSCallback callback) {
        this.voice = voice;
        this.model = model;
        speakDirect(text, callback);
    }

    public void speakWithChunking(String text, TTSCallback callback) {
        speakStreamingText(text, callback);
    }

    public void setVoicePersonalization(boolean usePersonality) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("use_master_personality", usePersonality);
        editor.apply();
    }

    public void setVoiceOverride(String voiceStyle) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("voice_style", voiceStyle);
        editor.apply();
    }

    public void clearVoiceOverride() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("voice_style", "auto");
        editor.apply();
    }

    public void setVoicePersonalizationInstructions(String instructions) {
        // Not used with simplified approach - keeping for compatibility
    }

    public void stopPlayback() {
        interrupt();
    }

    /**
     * ENHANCED: Shutdown with thorough cleanup
     */
    public void shutdown() {
        Log.d(TAG, "🛑 Shutting down TTS service");
        stopPlayback();
        cleanupCurrentSession();
        executorService.shutdown();
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }

    public interface TTSCallback {
        void onSpeechStarted();
        void onSpeechReady(File audioFile);
        void onSpeechCompleted();
        void onError(String errorMessage);
    }
}