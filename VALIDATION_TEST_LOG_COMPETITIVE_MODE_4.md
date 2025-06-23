                                           "move_scores": {
                                                                             "e2e4": {"score": 0.0, "reason": "Too classical and symmetrical; lacks the ...
22:35:24.969                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.8
22:35:24.969                                                             🎭 AI preferred moves: []
22:35:24.969                                                             💭 AI reasoning: The first move must set the stage for a battle, not a parade. e4 is the path of least resistance, not the path of glory.
22:35:25.228 ResponsesAPI                                                🏁 Response completed
22:35:25.229 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g1f3": {"score": 0.0, "reason": "Anodyne and symmetrical; the antithesis o...
22:35:25.230                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 245 of {
                                                                           "move_scores": {
                                                                             "g1f3": {"score": 0.0, "reason": "Anodyne and symmetrical; the antithesis of my approach."},
                                                                             "e4": {"score": 1.0, "reason": "The open door to complexity and creativity. My true beginning."},
                                                                             "d4": {"score": 0.6, \"reason\": \"Offers more scope than Nf3, but still too restrained for my taste.\"}
                                                                           },
                                                                           "top_choice": "e4",
                                                                           "style_reasoning": "I seek the battlefield, not the parade ground. e4 opens the way to rich, double-edged struggles.",
                                                                           "confidence": 1.0
                                                                         }
                                                                         	at org.json.JSONTokener.syntaxError(JSONTokener.java:469)
                                                                         	at org.json.JSONTokener.readLiteral(JSONTokener.java:297)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:115)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:380)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:168)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:185)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.parseAIResponse(AIStyleAdvisor.java:380)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.requestAIAdvice(AIStyleAdvisor.java:261)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.lambda$getStyleAdvice$4$com-example-chesspedagogue-AIStyleAdvisor(AIStyleAdvisor.java:172)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor$$ExternalSyntheticLambda2.run(D8$$SyntheticClass:0)
                                                                         	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
                                                                         	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
                                                                         	at java.lang.Thread.run(Thread.java:1012)
22:35:41.400 Dialog                                                      mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
22:35:41.413 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@3862d9a
22:35:41.423 ScrollView                                                  initGoToTop
22:35:41.436 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{bfd1816 V.E...... R.....I. 0,0-0,0}[MainActivity], caller=android.view.WindowManagerImpl.addView:158 android.app.Dialog.show:511 com.example.chesspedagogue.MainActivity.launchCompetitiveMode:3163 
22:35:41.437 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
22:35:41.443 VRI[MainAc...y]@f1e2597                                     synced displayState. AttachInfo displayState=2
22:35:41.445                                                             setView = com.android.internal.policy.DecorView@bfd1816 IsHRR=false TM=true
22:35:41.475 BufferQueueProducer                                         [](id:408700000002,api:0,p:0,c:16519) setDequeueTimeout:2077252342
22:35:41.476 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
22:35:41.476                                                             Access denied finding property "vendor.gpp.create_frc_extension"
22:35:41.476 VRI[MainAc...y]@f1e2597                                     Relayout returned: old=(0,100,1440,2908) new=(36,165,1404,2843) relayoutAsync=false req=(1368,2678)0 dur=5 res=0x3 s={true 0xb40000719a9a2000} ch=true seqId=0
22:35:41.476                                                             performConfigurationChange setNightDimText nightDimLevel=0
22:35:41.477                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb40000719a9a2000} hwInitialized=true
22:35:41.490 AbsListView                                                  in onLayout changed 
22:35:41.491 ScrollView                                                   onsize change changed 
22:35:41.492 VRI[MainAc...y]@f1e2597                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
22:35:41.492                                                             Setup new sync=wmsSync-VRI[MainActivity]@f1e2597#4
22:35:41.492                                                             Creating new active sync group VRI[MainActivity]@f1e2597#5
22:35:41.492                                                             registerCallbacksForSync syncBuffer=false
22:35:41.495                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
22:35:41.496                                                             mWNT: t=0xb4000071ed141f00 mBlastBufferQueue=0xb40000722686aa00 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
22:35:41.496                                                             Setting up sync and frameCommitCallback
22:35:41.498 BLASTBufferQueue                                            [VRI[MainActivity]@f1e2597#2](f:0,a:0,s:0) onFrameAvailable the first frame is available
22:35:41.498 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 54248, bufferData(ID: 70948564762635, frameNumber: 1)
22:35:41.498 VRI[MainAc...y]@f1e2597                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
22:35:41.499 HWUI                                                        CFMS:: SetUp Pid : 16519    Tid : 16577
22:35:41.499 VRI[MainAc...y]@f1e2597                                     reportDrawFinished seqId=0
22:35:41.501 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
22:35:41.530 VRI[MainAc...y]@f1e2597                                     mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb40000719a9a2000}
22:35:42.411                                                             ViewPostIme pointer 0
22:35:42.413                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[MainActivity]@f1e2597
22:35:42.498                                                             ViewPostIme pointer 1
22:35:42.499 AbsListView                                                 onTouchUp() mTouchMode : 0
22:35:42.585 WindowManager                                               WindowManagerGlobal#removeView, ty=2, view=com.android.internal.policy.DecorView{bfd1816 V.E...... R......D 0,0-1368,2678}[MainActivity], caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.app.Dialog.dismissDialog:808 
22:35:42.585 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@5aa9fa2
22:35:42.588 HWUI                                                        endAllActiveAnimators on 0xb4000071f8fbd600 (AlertController$RecycleListView) with handle 0xb400007230592f40
22:35:42.588 VRI[MainAc...y]@f1e2597                                     dispatchDetachedFromWindow
22:35:42.596 VoiceControlManager                                         📋 Unregistered voice command listener
22:35:42.602 ActivityThread                                              com.example.chesspedagogue will use render engine as VK
22:35:42.609 CompetitiveModeActivity                                     🏆 Starting competitive mode against chess master...
22:35:42.611 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@b8155c8
22:35:42.634 ScrollView                                                  initGoToTop
22:35:42.640 CompetitiveModeActivity                                     🔄 Synchronizing master selection: alekhine
22:35:42.641 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
22:35:42.641 CompetitiveModeActivity                                     🔍 Master sync verification:
22:35:42.641                                                               - Voice system (ChessFineTunedModels): alekhine
22:35:42.641                                                               - App system (ChessAppPrefs): alekhine
22:35:42.641                                                               - FineTunedModelManager: alekhine
22:35:42.641                                                             ✅ Master synchronization SUCCESS! All systems consistent.
22:35:42.641                                                             🎯 Competitive config - Master: alekhine, Color: black, Skill: 14, ELO: 2330
22:35:42.641                                                             ✅ Master synchronized across all SharedPreferences stores
22:35:42.642                                                             🎨 Initializing views...
22:35:42.642                                                             ✅ Dynamic master elements set for: alekhine
22:35:42.642                                                             ✅ Voice status indicator initialized
22:35:42.642                                                             ✅ Views initialized
22:35:42.642                                                             🎭 Initializing emotional intelligence systems...
22:35:42.642                                                             🧠 Initializing EQ system with historical emotional data...
22:35:42.644 EmotionalIntelligence                                       🧠 Loaded emotional history for alekhine vs player: 0 events, momentum: 0.25
22:35:42.644 CompetitiveModeActivity                                     ✅ EQ system initialized - emotional reactions and relationships should now work!
22:35:42.646 EmotionalS...egyLearner                                     🧠 Loaded 0 master strategy profiles from database
22:35:42.646                                                             🧠🎯 EmotionalStrategyLearner initialized - Ready for adaptive learning!
22:35:42.646 CrossMaste...ectiveness                                     ✅ Loaded global effectiveness statistics
22:35:42.647 CompetitiveModeActivity                                     🧠 Phase 3: Adaptive conversation strategy manager initialized
22:35:42.647                                                             ✅ Emotional intelligence systems initialized
22:35:42.647                                                             🎲 Initializing personality engine for alekhine...
22:35:42.647 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
22:35:42.647 DynamicRelationship                                         ✅ Loaded existing relationship dynamics
22:35:42.647 CompetitiveModeActivity                                     🎲 Creating PersonalityEngine instance...
22:35:42.647                                                             🎯 Setting current master to: alekhine
22:35:42.647                                                             🚀 CALLING initializeMasterData for: alekhine
22:35:42.647                                                             🎭 Enabling personality play...
22:35:42.648                                                             ⚖️ Using splash difficulty - ELO: 2330, Skill: 14
22:35:42.648                                                             ✅ Personality engine initialized for alekhine
22:35:42.648                                                             🔍 Running personality engine database diagnostic...
22:35:42.648 PersonalityDiagnostic                                       🔍 DIAGNOSING PERSONALITY ENGINE DATABASE FOR: alekhine
22:35:42.649                                                             📊 Master 'alekhine' - hasData: true, positions: 12276
22:35:42.649                                                             ✅ Data already exists - testing sample query...
22:35:42.649                                                             🧪 Testing sample query for alekhine...
22:35:42.650                                                             🧪 Sample query returned 5 results
22:35:42.650                                                             ✅ DATABASE QUERY WORKING! Sample position found:
22:35:42.650                                                                Master: alekhine
22:35:42.650                                                                FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq
22:35:42.650                                                                Opponent: Frank Marshall
22:35:42.653                                                             📊 Complete database stats: {anand=1870, kramnik=6707, kasparov=5010, alekhine=12276, fischer=5767, karpov=1866, capablanca=6051, carlsen=5739, total_positions=50064, tal=4778}
22:35:42.653 CompetitiveModeActivity                                     🔬 Running comprehensive PersonalityEngine system diagnostic...
22:35:42.653 PersonalitySystemDiag                                       🔬 STARTING COMPREHENSIVE PERSONALITY ENGINE DIAGNOSTIC
22:35:42.653                                                             📋 Test Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
22:35:42.653                                                             🎭 Master: alekhine (Target ELO: 2330)
22:35:42.653                                                             🔍 Phase 1: Database Analysis
22:35:42.654                                                             📊 Total alekhine positions in DB: 12276
22:35:42.656                                                             🎯 Exact FEN matches: 50
22:35:42.656                                                             🔍 Board pattern: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
22:35:42.656                                                             📋 Sample matched games: null
22:35:42.656                                                             🤖 Phase 2: AI Integration Analysis
22:35:42.656                                                             🤖 Assistant available for alekhine: true
22:35:42.656                                                             📚 Vector store connected: false
22:35:42.656                                                             🧠 AI analysis capability: Available
22:35:42.656                                                             🎨 Phase 3: Style Application Analysis
22:35:42.656                                                             ♟️ Stockfish candidates: null
22:35:42.656                                                             🎭 Personality weight: 0.0
22:35:42.656                                                             📊 Phase 4: Quality Assessment
22:35:42.656                                                             📊 Quality assessment complete - Score: 0.7
22:35:42.656                                                             ✅ DIAGNOSTIC COMPLETE
22:35:42.657                                                             === PersonalityEngine System Diagnostic ===
                                                                         Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
                                                                         Master: alekhine (Target ELO: 2330)
                                                                         
                                                                         --- Database Analysis ---
                                                                         Total positions in DB: 12276
                                                                         Exact matches: 50
                                                                         Similar matches: 0
                                                                         Intelligent matches: 0
                                                                         
                                                                         --- AI Integration ---
                                                                         Assistant available: true
                                                                         Vector store connected: false
                                                                         
                                                                         --- Style Application ---
                                                                         Selected move: Test pending
                                                                         Personality weight: 0.00
                                                                         
                                                                         --- Quality Assessment ---
                                                                         Logically consistent: true
                                                                         Authenticity score: 0.70
                                                                         Issues found: Too many exact matches (suspicious); 
22:35:42.657 CompetitiveModeActivity                                     📊 System diagnostic result: === PersonalityEngine System Diagnostic ===
                                                                         Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
                                                                         Master: alekhine (Target ELO: 2330)
                                                                         
                                                                         --- Database Analysis ---
                                                                         Total positions in DB: 12276
                                                                         Exact matches: 50
                                                                         Similar matches: 0
                                                                         Intelligent matches: 0
                                                                         
                                                                         --- AI Integration ---
                                                                         Assistant available: true
                                                                         Vector store connected: false
                                                                         
                                                                         --- Style Application ---
                                                                         Selected move: Test pending
                                                                         Personality weight: 0.00
                                                                         
                                                                         --- Quality Assessment ---
                                                                         Logically consistent: true
                                                                         Authenticity score: 0.70
                                                                         Issues found: Too many exact matches (suspicious); 
22:35:42.657 PersonalitySystemDiag                                       🔍 QUICK NAME DIAGNOSTIC
22:35:42.660                                                             📊 Actual master names in database:
22:35:42.660                                                                anand: 1870 positions
22:35:42.660                                                                kramnik: 6707 positions
22:35:42.660                                                                kasparov: 5010 positions
22:35:42.660                                                                alekhine: 12276 positions
22:35:42.660                                                                fischer: 5767 positions
22:35:42.660                                                                karpov: 1866 positions
22:35:42.660                                                                capablanca: 6051 positions
22:35:42.660                                                                carlsen: 5739 positions
22:35:42.660                                                                total_positions: 50064 positions
22:35:42.660                                                                tal: 4778 positions
22:35:42.660 CompetitiveModeActivity                                     🎤 Initializing voice services...
22:35:42.660 VoiceControlManager                                         📋 Registered voice command listener: competitive_mode
22:35:42.662 CompetitiveModeActivity                                     🗣️ Updating voice for master: alekhine
22:35:42.662 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
22:35:42.662 CompetitiveModeActivity                                     🔍 FineTunedModelManager master: alekhine
22:35:42.662                                                             🔍 SharedPreferences master: alekhine
22:35:42.662 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
22:35:42.662                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
22:35:42.662                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
22:35:42.662 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
22:35:42.662 TTSServiceManager                                           ✅ Usage context set to: competitive_mode
22:35:42.662 CompetitiveModeActivity                                     ✅ Final verification - Voice system master: alekhine
22:35:42.662                                                             ✅ Voice services initializing...
22:35:42.663 EvaluationMigration                                         🔄 Migration helper initialized
22:35:42.743 WindowManager           system_server                       win=Window{a3370b2 u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity EXITING} destroySurfaces: appStopped=false cleanupOnResume=false win.mWindowRemovalAllowed=true win.mRemoveOnExit=true win.mViewVisibility=0 caller=com.android.server.wm.WindowState.onExitAnimationDone:222 com.android.server.wm.WindowState.onAnimationFinished:161 com.android.server.wm.WindowContainer$$ExternalSyntheticLambda5.onAnimationFinished:26 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda1.run:28 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda0.onAnimationFinished:65 com.android.server.wm.LocalAnimationAdapter$$ExternalSyntheticLambda0.run:10 android.os.Handler.handleCallback:959 
22:35:42.966 UnifiedEvaluationSystem com.example.chesspedagogue          🔧 Initializing Stockfish engine: /data/app/~~iYemSWo_tpkXbi-24JnPpg==/com.example.chesspedagogue-1TtfnAGImzbpLiFeavkDKQ==/lib/arm64/libstockfish.so
22:35:42.971                                                             ✅ Stockfish engine initialized successfully
22:35:42.971 EvaluationMigration                                         🔄 MIGRATION: initializeEngine -> UnifiedEvaluationSystem
22:35:42.971 UnifiedEvaluationSystem                                     🔧 Initializing Stockfish engine: /data/app/~~iYemSWo_tpkXbi-24JnPpg==/com.example.chesspedagogue-1TtfnAGImzbpLiFeavkDKQ==/lib/arm64/libstockfish.so
22:35:42.973                                                             ✅ Stockfish engine initialized successfully
22:35:42.973 GameViewModel                                               🎭 Initializing personality LiveData...
22:35:42.973                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
22:35:42.973                                                             🔍 Requesting position evaluation...
22:35:42.973 CompetitiveModeActivity                                     🎭 Configuring GameRepository personality engine for alekhine
22:35:42.975                                                             ✅ GameRepository personality engine configured successfully!
22:35:42.975                                                             🎯 Master: alekhine, Weight: 0.3, Enabled: true
22:35:42.975                                                             🔍 Personality engine availability check: true
22:35:42.975                                                             ✅ CONFIRMED: GameRepository personality engine is properly configured and available!
22:35:42.975                                                             ✅ Game view model configured for competitive mode
22:35:42.975                                                             👀 Setting up observers...
22:35:42.975                                                             🎯 Setting up chess board interaction...
22:35:42.975                                                             ✅ Chess board interaction setup complete
22:35:42.975                                                             ✅ Observers setup complete
22:35:42.976                                                             🎮 Setting up controls...
22:35:42.977                                                             ✅ Controls setup complete
22:35:42.977                                                             ✅ All competitive mode systems initialized!
22:35:42.977                                                             🏁 Starting competitive game vs alekhine
22:35:42.977 GameViewModel                                               🎮 Starting new game with configuration: black, skill=14, elo=2330
22:35:43.126 GameHistoryManager                                          Game history cleared
22:35:43.226 GameViewModel                                               🔍 Requesting position evaluation...
22:35:43.226                                                             🔄 Skipping evaluation - too soon since last request
22:35:43.226                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:35:43.226                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:35:43.226                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:35:43.226                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:35:43.226                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:35:43.226                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:35:43.226                                                             🔧 gameRepository class: GameRepository
22:35:43.226                                                             🔧 Current thread: main
22:35:43.227 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:35:43.227 GameViewModel                                               ✅ Game initialized with black vs 2330 Elo engine
22:35:43.227 AdaptiveStrategy                                            🔍 Started monitoring conversation: competitive_1750628143227 (alekhine vs player)
22:35:43.227 CompetitiveModeActivity                                     🧠 Phase 3: Started adaptive conversation monitoring for alekhine
22:35:43.228 RelationshipPersistence                                     👥 Retrieved relationship: alekhine <-> player (respect: 0.50, rivalry: 0.00, friendship: 0.00)
22:35:43.228 EmotionalS...egyLearner                                     🧠 Started learning session: alekhine vs player
22:35:43.229                                                             🎯 EXPLOITATION: alekhine using proven supportive approach vs player (50.0% success rate)
22:35:43.232 CrossMaste...ectiveness                                     🎓 Generated 0 cross-learning recommendations for alekhine vs player
22:35:43.232 AdaptiveStrategy                                            🎯 Optimal strategy for alekhine vs player: 'supportive' (confidence: 0.20)
22:35:43.232 CompetitiveModeActivity                                     🧠 Phase 3: Using adaptive strategy 'supportive' (confidence: 0.20) for greeting - Individual learning: EXPLORATION: Testing new approach 'supportive' to learn effectiveness; 
22:35:43.233                                                             🗣️ Speaking master dialogue: Alexander Alekhine at your service. Prepare for a combinatorial masterclass!
22:35:43.233 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
22:35:43.233                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
22:35:43.233                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
22:35:43.233                                                             ✅ Usage context set to: competitive_mode
22:35:43.233 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
22:35:43.233 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
22:35:43.233                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
22:35:43.235 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
22:35:43.236                                                             🎭 Updated emotion indicator: 😎
22:35:43.236                                                             ✅ Competitive game started successfully!
22:35:43.238                                                             🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
22:35:43.238                                                             📊 Evaluation updated: 0.0
22:35:43.238                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
22:35:43.238                                                             🎯 First emotional evaluation: 0.0 (threshold: 1.5)
22:35:43.238                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628143s/25s)
22:35:43.238                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:35:43.239                                                             📜 Move history updated: 0 moves
22:35:43.241 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
22:35:43.248 Choreographer                                               Skipped 77 frames!  The application may be doing too much work on its main thread.
22:35:43.275 BufferQueueProducer                                         [](id:408700000003,api:0,p:0,c:16519) setDequeueTimeout:2077252342
22:35:43.276 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
22:35:43.276                                                             Access denied finding property "vendor.gpp.create_frc_extension"
22:35:43.282 ScrollView                                                   onsize change changed 
22:35:43.320 BLASTBufferQueue                                            [VRI[CompetitiveModeActivity]@3f43d80#3](f:0,a:0,s:0) onFrameAvailable the first frame is available
22:35:43.321 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 54256, bufferData(ID: 70948564762639, frameNumber: 1)
22:35:43.321 HWUI                                                        CFMS:: SetUp Pid : 16519    Tid : 16577
22:35:43.322 CompetitiveModeActivity                                     🎤 Voice service connected to competitive mode
22:35:43.323 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
22:35:43.334                                                             Davey! duration=722ms; Flags=1, FrameTimelineVsyncId=101461388, IntendedVsync=610086042860700, Vsync=610086685367257, InputEventId=0, HandleInputStart=610086690247738, AnimationStart=610086690248728, PerformTraversalsStart=610086690331175, DrawStart=610086725789925, FrameDeadline=610086051194033, FrameInterval=610086690086436, FrameStartTime=8344241, SyncQueued=610086744117113, SyncStart=610086744200915, IssueDrawCommandsStart=610086744704717, SwapBuffers=610086762484353, FrameCompleted=610086765116905, DequeueBufferDuration=10781, QueueBufferDuration=337813, GpuCompleted=610086765116905, SwapBuffersCompleted=610086763429665, DisplayPresentTime=0, CommandSubmissionCompleted=610086762484353, 
22:35:43.360 InputMethodManagerUtils                                     startInputInner - Id : 0
22:35:43.360 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
22:35:43.821 WindowManager           system_server                       win=Window{9e89034 u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:204 com.android.server.wm.ActivityClientController.activityStopped:95 android.app.IActivityClientController$Stub.onTransact:722 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1541 android.os.Binder.execTransact:1480 
22:35:44.333 GameViewModel           com.example.chesspedagogue          ✅ Evaluation received: 0.30
22:35:44.383 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:35:44.384 AIStyleAdvisor                                              ⚡ Cache hit for alekhine - instant advice
22:35:44.433 CompetitiveModeActivity                                     📊 Evaluation updated: 0.3
22:35:44.433                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.3, emotionalManager: INITIALIZED
22:35:44.433                                                             🎯 First emotional evaluation: 0.3 (threshold: 1.5)
22:35:44.433                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628144s/25s)
22:35:44.433                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:35:44.534 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:35:44.534 AIStyleAdvisor                                              ⚡ Cache hit for alekhine - instant advice
22:35:44.544 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e2e4
22:35:44.897 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1
22:35:44.898                                                             📜 Move history updated: 1 moves
22:35:44.898 GameHistoryManager                                          Move added: e2e4
22:35:44.898 GameViewModel                                               🔍 Requesting position evaluation...
22:35:44.898                                                             🎭 Updating personality context for move: e2e4
22:35:44.898                                                             ✨ Personality context updated for move e2e4 - This is revolutionary!
22:35:44.898                                                             🔍 Checking game end conditions...
22:35:44.999                                                             ✅ Game continues - no end condition detected
22:35:44.999 Choreographer                                               Skipped 53 frames!  The application may be doing too much work on its main thread.
22:35:45.656 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.29 (move=1, alternating=ON, FEN=rnbqkbnr/pppppppp/8/)
22:35:45.662 GameViewModel                                               ✅ Evaluation received: -0.29
22:35:45.771 CompetitiveModeActivity                                     📊 Evaluation updated: -0.29
22:35:45.771                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.29, emotionalManager: INITIALIZED
22:35:45.771                                                             🎯 First emotional evaluation: -0.29 (threshold: 1.5)
22:35:45.771                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628145s/25s)
22:35:45.771                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:35:49.415                                                             🗣️ Master dialogue speech completed
22:38:52.921                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=6
22:38:52.921                                                             🎯 SQUARE TAPPED: row=0, col=6
22:38:52.921                                                             📝 Player color: black
22:38:52.921                                                             🔍 Selected row/col: -1/-1
22:38:53.280                                                             🎯 Selected piece: n at 0, 6
22:38:53.707                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
22:38:53.708                                                             🎯 SQUARE TAPPED: row=2, col=5
22:38:53.708                                                             📝 Player color: black
22:38:53.708                                                             🔍 Selected row/col: 0/6
22:38:53.708                                                             🎯 ATTEMPTING MOVE: g8f6
22:38:53.708                                                             📝 Player color: black
22:38:53.708                                                             🔄 Is player's turn: true
22:38:53.708                                                             🔄 Is white's turn: false
22:38:53.708                                                             ✅ Turn validation passed, making move: g8f6
22:38:53.708 GameViewModel                                               🎯 makePlayerMove called with: g8f6
22:38:53.759                                                             🔍 Validating move: g8f6 (attempt 1)
22:38:53.860                                                             📋 Current position: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1
22:38:53.912                                                             ⚖️ Move g8f6 legality check: LEGAL
22:38:53.912                                                             ✅ Executing validated move: g8f6
22:38:54.114                                                             📍 New position after move: rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 1 2
22:38:54.115 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 1 2
22:38:54.117                                                             📜 Move history updated: 2 moves
22:38:54.117 GameHistoryManager                                          Move added: g8f6
22:38:54.214 GameViewModel                                               🔍 Requesting position evaluation...
22:38:54.214                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:38:54.214                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:38:54.214                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:38:54.215                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:38:54.215                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:38:54.215                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:38:54.215                                                             🔧 gameRepository class: GameRepository
22:38:54.215                                                             🔧 Current thread: main
22:38:54.215 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:38:54.415 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
22:38:55.473 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.64 (move=2, alternating=ON, FEN=rnbqkb1r/pppppppp/5n)
22:38:55.473                                                             🔄 ALTERNATING: move=2, flip=false, 0.64→0.64 (diff=0.93)
22:38:55.473 GameViewModel                                               ✅ Evaluation received: 0.64
22:38:55.574 CompetitiveModeActivity                                     📊 Evaluation updated: 0.64
22:38:55.574                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.64, emotionalManager: INITIALIZED
22:38:55.574                                                             🎯 First emotional evaluation: 0.64 (threshold: 1.5)
22:38:55.574                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628335s/25s)
22:38:55.574                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:38:56.168 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:38:56.169 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #3)
22:38:56.170                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:38:56.170                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKB...
22:38:56.170                                                             🎲 Candidate moves for AI analysis: [b1c3]
22:38:56.170 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:38:56.170                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:38:56.426                                                             📋 Response ID: resp_685877f03558819fbc33d2a679e5319702ade29c46957c5e
22:38:57.306                                                             🏁 Response completed
22:38:57.306 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "b1c3": {"score": 0.0, "reason": "A passive, symmetrical response. Lacks am...
22:38:57.307                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.8
22:38:57.307                                                             🎭 AI preferred moves: []
22:38:57.307                                                             💭 AI reasoning: None of the candidate moves embody my style here. I would seek a path filled with tension and imbalance, not quiet development.
22:38:57.339 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b1c3
22:38:57.694 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/4P3/2N5/PPPP1PPP/R1BQKBNR b KQkq - 2 2
22:38:57.697                                                             📜 Move history updated: 3 moves
22:38:57.697 GameHistoryManager                                          Move added: b1c3
22:38:57.697 GameViewModel                                               🔍 Requesting position evaluation...
22:38:57.697                                                             🎭 Updating personality context for move: b1c3
22:38:57.697                                                             ✨ Personality context updated for move b1c3 - This is revolutionary!
22:38:57.698                                                             🔍 Checking game end conditions...
22:38:57.798                                                             ✅ Game continues - no end condition detected
22:38:58.654 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.14 (move=3, alternating=ON, FEN=rnbqkb1r/pppppppp/5n)
22:38:58.655                                                             🔄 ALTERNATING: move=3, flip=true, -0.14→0.14 (diff=0.50)
22:38:58.669 GameViewModel                                               ✅ Evaluation received: 0.14
22:38:58.772 CompetitiveModeActivity                                     📊 Evaluation updated: 0.14
22:38:58.772                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.14, emotionalManager: INITIALIZED
22:38:58.772                                                             🎯 First emotional evaluation: 0.14 (threshold: 1.5)
22:38:58.772                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628338s/25s)
22:38:58.772                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:39:14.499                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=3
22:39:14.499                                                             🎯 SQUARE TAPPED: row=1, col=3
22:39:14.499                                                             📝 Player color: black
22:39:14.499                                                             🔍 Selected row/col: -1/-1
22:39:14.854                                                             🎯 Selected piece: p at 1, 3
22:39:15.473                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
22:39:15.473                                                             🎯 SQUARE TAPPED: row=3, col=3
22:39:15.473                                                             📝 Player color: black
22:39:15.473                                                             🔍 Selected row/col: 1/3
22:39:15.473                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=3, reaches=false
22:39:15.473                                                             🎯 ATTEMPTING MOVE: d7d5
22:39:15.473                                                             📝 Player color: black
22:39:15.473                                                             🔄 Is player's turn: true
22:39:15.473                                                             🔄 Is white's turn: false
22:39:15.473                                                             ✅ Turn validation passed, making move: d7d5
22:39:15.473 GameViewModel                                               🎯 makePlayerMove called with: d7d5
22:39:15.524                                                             🔍 Validating move: d7d5 (attempt 1)
22:39:15.624                                                             📋 Current position: rnbqkb1r/pppppppp/5n2/8/4P3/2N5/PPPP1PPP/R1BQKBNR b KQkq - 2 2
22:39:15.675                                                             ⚖️ Move d7d5 legality check: LEGAL
22:39:15.675                                                             ✅ Executing validated move: d7d5
22:39:15.877                                                             📍 New position after move: rnbqkb1r/ppp1pppp/5n2/3p4/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq - 0 3
22:39:15.880 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp1pppp/5n2/3p4/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq - 0 3
22:39:15.882                                                             📜 Move history updated: 4 moves
22:39:15.882 GameHistoryManager                                          Move added: d7d5
22:39:15.978 GameViewModel                                               🔍 Requesting position evaluation...
22:39:15.979                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:39:15.979                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:39:15.979                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:39:15.979                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:39:15.979                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:39:15.979                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:39:15.979                                                             🔧 gameRepository class: GameRepository
22:39:15.979                                                             🔧 Current thread: main
22:39:15.979 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:39:17.035 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.30 (move=4, alternating=ON, FEN=rnbqkb1r/ppp1pppp/5n)
22:39:17.035                                                             🔄 ALTERNATING: move=4, flip=false, 0.30→0.30 (diff=0.16)
22:39:17.035 GameViewModel                                               ✅ Evaluation received: 0.30
22:39:17.136 CompetitiveModeActivity                                     📊 Evaluation updated: 0.3
22:39:17.136                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.3, emotionalManager: INITIALIZED
22:39:17.136                                                             🎯 First emotional evaluation: 0.3 (threshold: 1.5)
22:39:17.136                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628357s/25s)
22:39:17.136                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:39:17.803 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:39:17.804 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #4)
22:39:17.804                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:39:17.804                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/ppp1pppp/5n2/3p4/4P3/2N5/PPPP1PPP/R1...
22:39:17.804                                                             🎲 Candidate moves for AI analysis: [e4e5]
22:39:17.804 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:39:17.804                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:39:18.038                                                             📋 Response ID: resp_68587805d358819cacf6eca391bff8ea0824e9eccc93edb5
22:39:19.953                                                             🏁 Response completed
22:39:19.954 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e4e5": {"score": 0.0, "reason": "A dull, symmetrical advance—no tension, n...
22:39:19.954                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
22:39:19.954                                                             🎭 AI preferred moves: []
22:39:19.954                                                             💭 AI reasoning: I seek to unsettle and outwit, not to follow the path of least resistance. My best play comes from creating chaos on my terms.
22:39:19.977 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e4e5
22:39:20.334 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp1pppp/5n2/3pP3/8/2N5/PPPP1PPP/R1BQKBNR b KQkq - 0 3
22:39:20.338                                                             📜 Move history updated: 5 moves
22:39:20.338 GameHistoryManager                                          Move added: e4e5
22:39:20.338 GameViewModel                                               🔍 Requesting position evaluation...
22:39:20.339                                                             🎭 Updating personality context for move: e4e5
22:39:20.339                                                             ✨ Personality context updated for move e4e5 - This is revolutionary!
22:39:20.340                                                             🔍 Checking game end conditions...
22:39:20.442                                                             ✅ Game continues - no end condition detected
22:39:21.099 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.27 (move=5, alternating=ON, FEN=rnbqkb1r/ppp1pppp/5n)
22:39:21.099                                                             🔄 ALTERNATING: move=5, flip=true, -0.27→0.27 (diff=0.03)
22:39:21.101 GameViewModel                                               ✅ Evaluation received: 0.27
22:39:21.203 CompetitiveModeActivity                                     📊 Evaluation updated: 0.27
22:39:21.203                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.27, emotionalManager: INITIALIZED
22:39:21.204                                                             🎯 First emotional evaluation: 0.27 (threshold: 1.5)
22:39:21.204                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628361s/25s)
22:39:21.204                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:39:39.523                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
22:39:39.523                                                             🎯 SQUARE TAPPED: row=2, col=5
22:39:39.523                                                             📝 Player color: black
22:39:39.523                                                             🔍 Selected row/col: -1/-1
22:39:39.883                                                             🎯 Selected piece: n at 2, 5
22:39:40.295                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=3
22:39:40.295                                                             🎯 SQUARE TAPPED: row=1, col=3
22:39:40.295                                                             📝 Player color: black
22:39:40.295                                                             🔍 Selected row/col: 2/5
22:39:40.295                                                             🎯 ATTEMPTING MOVE: f6d7
22:39:40.295                                                             📝 Player color: black
22:39:40.295                                                             🔄 Is player's turn: true
22:39:40.295                                                             🔄 Is white's turn: false
22:39:40.295                                                             ✅ Turn validation passed, making move: f6d7
22:39:40.295 GameViewModel                                               🎯 makePlayerMove called with: f6d7
22:39:40.346                                                             🔍 Validating move: f6d7 (attempt 1)
22:39:40.446                                                             📋 Current position: rnbqkb1r/ppp1pppp/5n2/3pP3/8/2N5/PPPP1PPP/R1BQKBNR b KQkq - 0 3
22:39:40.498                                                             ⚖️ Move f6d7 legality check: LEGAL
22:39:40.498                                                             ✅ Executing validated move: f6d7
22:39:40.701                                                             📍 New position after move: rnbqkb1r/pppnpppp/8/3pP3/8/2N5/PPPP1PPP/R1BQKBNR w KQkq - 1 4
22:39:40.704 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppnpppp/8/3pP3/8/2N5/PPPP1PPP/R1BQKBNR w KQkq - 1 4
22:39:40.707                                                             📜 Move history updated: 6 moves
22:39:40.707 GameHistoryManager                                          Move added: f6d7
22:39:40.801 GameViewModel                                               🔍 Requesting position evaluation...
22:39:40.801                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:39:40.801                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:39:40.801                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:39:40.802                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:39:40.802                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:39:40.802                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:39:40.802                                                             🔧 gameRepository class: GameRepository
22:39:40.802                                                             🔧 Current thread: main
22:39:40.802 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:39:41.708 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.32 (move=6, alternating=ON, FEN=rnbqkb1r/pppnpppp/8/)
22:39:41.708                                                             🔄 ALTERNATING: move=6, flip=false, 0.32→0.32 (diff=0.05)
22:39:41.708 GameViewModel                                               ✅ Evaluation received: 0.32
22:39:41.809 CompetitiveModeActivity                                     📊 Evaluation updated: 0.32
22:39:41.809                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.32, emotionalManager: INITIALIZED
22:39:41.809                                                             🎯 First emotional evaluation: 0.32 (threshold: 1.5)
22:39:41.809                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628381s/25s)
22:39:41.809                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:39:42.403 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:39:42.403 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #5)
22:39:42.404                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:39:42.404                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pppnpppp/8/3pP3/8/2N5/PPPP1PPP/R1BQK...
22:39:42.404                                                             🎲 Candidate moves for AI analysis: [d2d4]
22:39:42.404 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:39:42.404                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:39:42.614                                                             📋 Response ID: resp_6858781e6cec81a08db00bbd6b1c45b40bc3fabf262758e3
22:39:44.369                                                             🏁 Response completed
22:39:44.369 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d2d4": {"score": 0.0, "reason": "Too straightforward and symmetrical. I se...
22:39:44.370                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 247 of {
                                                                           "move_scores": {
                                                                             "d2d4": {"score": 0.0, "reason": "Too straightforward and symmetrical. I seek complexity, not easy equality."},
                                                                             "Ng1f3": {"score": 0.5, "reason": "Solid, but lacks the spark of ambition."},
                                                                             "Nb1d2": {"score": 0.3, \"reason\": \"Passive and cramped—no glory in such positions.\"},
                                                                             "f2f4": {"score": 1.0, \"reason\": \"This is the spirit of Alekhine! Bold, unbalanced, and ready to tear open the center.\"}
                                                                           },
                                                                           "top_choice": "f2f4",
                                                                           "style_reasoning": "I choose the path that leads to chaos and creativity, not the road to dull equality.",
                                                                           "confidence": 1.0
                                                                         }
                                                                         	at org.json.JSONTokener.syntaxError(JSONTokener.java:469)
                                                                         	at org.json.JSONTokener.readLiteral(JSONTokener.java:297)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:115)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:380)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:168)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:185)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.parseAIResponse(AIStyleAdvisor.java:380)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.requestAIAdvice(AIStyleAdvisor.java:261)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.lambda$getStyleAdvice$4$com-example-chesspedagogue-AIStyleAdvisor(AIStyleAdvisor.java:172)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor$$ExternalSyntheticLambda2.run(D8$$SyntheticClass:0)
                                                                         	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
                                                                         	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
                                                                         	at java.lang.Thread.run(Thread.java:1012)
22:39:44.404 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d2d4
22:39:44.764 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppnpppp/8/3pP3/3P4/2N5/PPP2PPP/R1BQKBNR b KQkq - 0 4
22:39:44.769                                                             📜 Move history updated: 7 moves
22:39:44.769 GameHistoryManager                                          Move added: d2d4
22:39:44.769 GameViewModel                                               🔍 Requesting position evaluation...
22:39:44.769                                                             🎭 Updating personality context for move: d2d4
22:39:44.769                                                             ✨ Personality context updated for move d2d4 - This is revolutionary!
22:39:44.770                                                             🔍 Checking game end conditions...
22:39:44.871                                                             ✅ Game continues - no end condition detected
22:39:45.680 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.24 (move=7, alternating=ON, FEN=rnbqkb1r/pppnpppp/8/)
22:39:45.680                                                             🔄 ALTERNATING: move=7, flip=true, -0.24→0.24 (diff=0.08)
22:39:45.684 GameViewModel                                               ✅ Evaluation received: 0.24
22:39:45.792 CompetitiveModeActivity                                     📊 Evaluation updated: 0.24
22:39:45.792                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.24, emotionalManager: INITIALIZED
22:39:45.793                                                             🎯 First emotional evaluation: 0.24 (threshold: 1.5)
22:39:45.793                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628385s/25s)
22:39:45.793                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:40:04.386                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=2
22:40:04.386                                                             🎯 SQUARE TAPPED: row=1, col=2
22:40:04.386                                                             📝 Player color: black
22:40:04.386                                                             🔍 Selected row/col: -1/-1
22:40:04.744                                                             🎯 Selected piece: p at 1, 2
22:40:05.124                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=2
22:40:05.125                                                             🎯 SQUARE TAPPED: row=3, col=2
22:40:05.125                                                             📝 Player color: black
22:40:05.125                                                             🔍 Selected row/col: 1/2
22:40:05.125                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=3, reaches=false
22:40:05.125                                                             🎯 ATTEMPTING MOVE: c7c5
22:40:05.125                                                             📝 Player color: black
22:40:05.125                                                             🔄 Is player's turn: true
22:40:05.125                                                             🔄 Is white's turn: false
22:40:05.125                                                             ✅ Turn validation passed, making move: c7c5
22:40:05.125 GameViewModel                                               🎯 makePlayerMove called with: c7c5
22:40:05.176                                                             🔍 Validating move: c7c5 (attempt 1)
22:40:05.277                                                             📋 Current position: rnbqkb1r/pppnpppp/8/3pP3/3P4/2N5/PPP2PPP/R1BQKBNR b KQkq - 0 4
22:40:05.328                                                             ⚖️ Move c7c5 legality check: LEGAL
22:40:05.328                                                             ✅ Executing validated move: c7c5
22:40:05.530                                                             📍 New position after move: rnbqkb1r/pp1npppp/8/2ppP3/3P4/2N5/PPP2PPP/R1BQKBNR w KQkq - 0 5
22:40:05.532 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1npppp/8/2ppP3/3P4/2N5/PPP2PPP/R1BQKBNR w KQkq - 0 5
22:40:05.535                                                             📜 Move history updated: 8 moves
22:40:05.536 GameHistoryManager                                          Move added: c7c5
22:40:05.632 GameViewModel                                               🔍 Requesting position evaluation...
22:40:05.632                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:40:05.632                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:40:05.632                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:40:05.632                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:40:05.632                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:40:05.632                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:40:05.632                                                             🔧 gameRepository class: GameRepository
22:40:05.632                                                             🔧 Current thread: main
22:40:05.632 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:40:06.587 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.36 (move=8, alternating=ON, FEN=rnbqkb1r/pp1npppp/8/)
22:40:06.587                                                             🔄 ALTERNATING: move=8, flip=false, 0.36→0.36 (diff=0.12)
22:40:06.588 GameViewModel                                               ✅ Evaluation received: 0.36
22:40:06.688 CompetitiveModeActivity                                     📊 Evaluation updated: 0.36
22:40:06.688                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.36, emotionalManager: INITIALIZED
22:40:06.688                                                             🎯 First emotional evaluation: 0.36 (threshold: 1.5)
22:40:06.688                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628406s/25s)
22:40:06.688                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:40:07.573 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:40:07.574 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #6)
22:40:07.574                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:40:07.574                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp1npppp/8/2ppP3/3P4/2N5/PPP2PPP/R1B...
22:40:07.574                                                             🎲 Candidate moves for AI analysis: [e5e6]
22:40:07.574 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:40:07.574                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:40:07.803                                                             📋 Response ID: resp_6858783798e0819d9d6394dba34ff49d0f55fa97e42ee5c8
22:40:10.066                                                             🏁 Response completed
22:40:10.067 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e5e6": {"score": 0.0, "reason": "A blunt, closed move—no complexity, no ch...
22:40:10.068                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
22:40:10.068                                                             🎭 AI preferred moves: []
22:40:10.068                                                             💭 AI reasoning: I seek out positions that test both calculation and nerve. Nxd5 embodies the fighting spirit and love of complexity that define my play.
22:40:10.101 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e5e6
22:40:10.461 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1npppp/4P3/2pp4/3P4/2N5/PPP2PPP/R1BQKBNR b KQkq - 0 5
22:40:10.466                                                             📜 Move history updated: 9 moves
22:40:10.466 GameHistoryManager                                          Move added: e5e6
22:40:10.466 GameViewModel                                               🔍 Requesting position evaluation...
22:40:10.467                                                             🎭 Updating personality context for move: e5e6
22:40:10.467                                                             ✨ Personality context updated for move e5e6 - This is revolutionary!
22:40:10.467                                                             🔍 Checking game end conditions...
22:40:10.568                                                             ✅ Game continues - no end condition detected
22:40:11.292 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.27 (move=9, alternating=ON, FEN=rnbqkb1r/pp1npppp/4P)
22:40:11.293                                                             🔄 ALTERNATING: move=9, flip=true, -0.27→0.27 (diff=0.09)
22:40:11.310 GameViewModel                                               ✅ Evaluation received: 0.27
22:40:11.416 CompetitiveModeActivity                                     📊 Evaluation updated: 0.27
22:40:11.416                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.27, emotionalManager: INITIALIZED
22:40:11.416                                                             🎯 First emotional evaluation: 0.27 (threshold: 1.5)
22:40:11.416                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628411s/25s)
22:40:11.416                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:40:31.134                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=5
22:40:31.134                                                             🎯 SQUARE TAPPED: row=1, col=5
22:40:31.134                                                             📝 Player color: black
22:40:31.134                                                             🔍 Selected row/col: -1/-1
22:40:31.491                                                             🎯 Selected piece: p at 1, 5
22:40:32.286                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
22:40:32.286                                                             🎯 SQUARE TAPPED: row=2, col=4
22:40:32.286                                                             📝 Player color: black
22:40:32.286                                                             🔍 Selected row/col: 1/5
22:40:32.286                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=2, reaches=false
22:40:32.286                                                             🎯 ATTEMPTING MOVE: f7e6
22:40:32.286                                                             📝 Player color: black
22:40:32.286                                                             🔄 Is player's turn: true
22:40:32.286                                                             🔄 Is white's turn: false
22:40:32.286                                                             ✅ Turn validation passed, making move: f7e6
22:40:32.286 GameViewModel                                               🎯 makePlayerMove called with: f7e6
22:40:32.337                                                             🔍 Validating move: f7e6 (attempt 1)
22:40:32.438                                                             📋 Current position: rnbqkb1r/pp1npppp/4P3/2pp4/3P4/2N5/PPP2PPP/R1BQKBNR b KQkq - 0 5
22:40:32.489                                                             ⚖️ Move f7e6 legality check: LEGAL
22:40:32.489                                                             ✅ Executing validated move: f7e6
22:40:32.691                                                             📍 New position after move: rnbqkb1r/pp1np1pp/4p3/2pp4/3P4/2N5/PPP2PPP/R1BQKBNR w KQkq - 0 6
22:40:32.694 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1np1pp/4p3/2pp4/3P4/2N5/PPP2PPP/R1BQKBNR w KQkq - 0 6
22:40:32.697                                                             📜 Move history updated: 10 moves
22:40:32.697 GameHistoryManager                                          Move added: f7e6
22:40:32.792 GameViewModel                                               🔍 Requesting position evaluation...
22:40:32.793                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:40:32.793                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:40:32.793                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:40:32.793                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:40:32.793                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:40:32.793                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:40:32.793                                                             🔧 gameRepository class: GameRepository
22:40:32.793                                                             🔧 Current thread: main
22:40:32.793 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:40:34.255 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.36 (move=10, alternating=ON, FEN=rnbqkb1r/pp1np1pp/4p)
22:40:34.256                                                             🔄 ALTERNATING: move=10, flip=false, 0.36→0.36 (diff=0.09)
22:40:34.256 GameViewModel                                               ✅ Evaluation received: 0.36
22:40:34.356 CompetitiveModeActivity                                     📊 Evaluation updated: 0.36
22:40:34.356                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.36, emotionalManager: INITIALIZED
22:40:34.356                                                             🎯 First emotional evaluation: 0.36 (threshold: 1.5)
22:40:34.356                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628434s/25s)
22:40:34.356                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:40:34.794 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:40:34.795 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #7)
22:40:34.795                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:40:34.795                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp1np1pp/4p3/2pp4/3P4/2N5/PPP2PPP/R1...
22:40:34.795                                                             🎲 Candidate moves for AI analysis: [g1f3]
22:40:34.795 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:40:34.795                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:40:35.039                                                             📋 Response ID: resp_68587852dec481a2bdfbec43d50152c90742caa2232b3e41
22:40:37.548                                                             🏁 Response completed
22:40:37.548 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g1f3": {"score": 0.0, "reason": "A passive, symmetrical choice; avoids com...
22:40:37.549                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
22:40:37.549                                                             🎭 AI preferred moves: []
22:40:37.549                                                             💭 AI reasoning: I seek out the fight in every position. My best chess comes from complexity and initiative, not from avoiding conflict.
22:40:37.590 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g1f3
22:40:37.946 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1np1pp/4p3/2pp4/3P4/2N2N2/PPP2PPP/R1BQKB1R b KQkq - 1 6
22:40:37.950                                                             📜 Move history updated: 11 moves
22:40:37.951 GameHistoryManager                                          Move added: g1f3
22:40:37.951 GameViewModel                                               🔍 Requesting position evaluation...
22:40:37.951                                                             🎭 Updating personality context for move: g1f3
22:40:37.951                                                             ✨ Personality context updated for move g1f3 - This is revolutionary!
22:40:37.952                                                             🔍 Checking game end conditions...
22:40:38.052                                                             ✅ Game continues - no end condition detected
22:40:39.120 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.16 (move=11, alternating=ON, FEN=rnbqkb1r/pp1np1pp/4p)
22:40:39.120                                                             🔄 ALTERNATING: move=11, flip=true, -0.16→0.16 (diff=0.20)
22:40:39.121 GameViewModel                                               ✅ Evaluation received: 0.16
22:40:39.224 CompetitiveModeActivity                                     📊 Evaluation updated: 0.16
22:40:39.224                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.16, emotionalManager: INITIALIZED
22:40:39.224                                                             🎯 First emotional evaluation: 0.16 (threshold: 1.5)
22:40:39.224                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628439s/25s)
22:40:39.224                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:40:46.689                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
22:40:46.689                                                             🎯 SQUARE TAPPED: row=0, col=1
22:40:46.690                                                             📝 Player color: black
22:40:46.690                                                             🔍 Selected row/col: -1/-1
22:40:47.045                                                             🎯 Selected piece: n at 0, 1
22:40:47.679                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=2
22:40:47.679                                                             🎯 SQUARE TAPPED: row=2, col=2
22:40:47.679                                                             📝 Player color: black
22:40:47.679                                                             🔍 Selected row/col: 0/1
22:40:47.679                                                             🎯 ATTEMPTING MOVE: b8c6
22:40:47.679                                                             📝 Player color: black
22:40:47.679                                                             🔄 Is player's turn: true
22:40:47.679                                                             🔄 Is white's turn: false
22:40:47.679                                                             ✅ Turn validation passed, making move: b8c6
22:40:47.679 GameViewModel                                               🎯 makePlayerMove called with: b8c6
22:40:47.680                                                             🔍 Validating move: b8c6 (attempt 1)
22:40:47.780                                                             📋 Current position: rnbqkb1r/pp1np1pp/4p3/2pp4/3P4/2N2N2/PPP2PPP/R1BQKB1R b KQkq - 1 6
22:40:47.832                                                             ⚖️ Move b8c6 legality check: LEGAL
22:40:47.832                                                             ✅ Executing validated move: b8c6
22:40:48.034                                                             📍 New position after move: r1bqkb1r/pp1np1pp/2n1p3/2pp4/3P4/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 2 7
22:40:48.036 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1np1pp/2n1p3/2pp4/3P4/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 2 7
22:40:48.040                                                             📜 Move history updated: 12 moves
22:40:48.040 GameHistoryManager                                          Move added: b8c6
22:40:48.134 GameViewModel                                               🔍 Requesting position evaluation...
22:40:48.134                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:40:48.134                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:40:48.135                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:40:48.135                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:40:48.135                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:40:48.135                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:40:48.135                                                             🔧 gameRepository class: GameRepository
22:40:48.135                                                             🔧 Current thread: main
22:40:48.135 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:40:49.741 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.23 (move=12, alternating=ON, FEN=r1bqkb1r/pp1np1pp/2n)
22:40:49.741                                                             🔄 ALTERNATING: move=12, flip=false, 0.23→0.23 (diff=0.07)
22:40:49.741 GameViewModel                                               ✅ Evaluation received: 0.23
22:40:49.842 CompetitiveModeActivity                                     📊 Evaluation updated: 0.23
22:40:49.842                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.23, emotionalManager: INITIALIZED
22:40:49.842                                                             🎯 First emotional evaluation: 0.23 (threshold: 1.5)
22:40:49.842                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628449s/25s)
22:40:49.842                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:40:50.027 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:40:50.028 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #8)
22:40:50.028                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:40:50.028                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1np1pp/2n1p3/2pp4/3P4/2N2N2/PPP2PP...
22:40:50.028                                                             🎲 Candidate moves for AI analysis: [d4c5]
22:40:50.028 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:40:50.028                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:40:50.296                                                             📋 Response ID: resp_685878620ee4819f881f97854dea29f90df66fea6e75bf34
22:40:52.392                                                             🏁 Response completed
22:40:52.393 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d4c5": {"score": 0.0, "reason": "The exchange of pawns leading to a symmet...
22:40:52.393                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
22:40:52.393                                                             🎭 AI preferred moves: []
22:40:52.393                                                             💭 AI reasoning: I seek positions where my imagination and calculation can flourish. The move d4e5 challenges my opponent and sets the stage for rich, tactical battles.
22:40:52.425 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d4c5
22:40:52.786 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1np1pp/2n1p3/2Pp4/8/2N2N2/PPP2PPP/R1BQKB1R b KQkq - 0 7
22:40:52.792                                                             📜 Move history updated: 13 moves
22:40:52.792 GameHistoryManager                                          Move added: d4c5
22:40:52.792 GameViewModel                                               🔍 Requesting position evaluation...
22:40:52.792                                                             🎭 Updating personality context for move: d4c5
22:40:52.792                                                             ✨ Personality context updated for move d4c5 - This is revolutionary!
22:40:52.792                                                             🔍 Checking game end conditions...
22:40:52.894                                                             ✅ Game continues - no end condition detected
22:40:53.905 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.31 (move=13, alternating=ON, FEN=r1bqkb1r/pp1np1pp/2n)
22:40:53.905                                                             🔄 ALTERNATING: move=13, flip=true, -0.31→0.31 (diff=0.08)
22:40:53.906 GameViewModel                                               ✅ Evaluation received: 0.31
22:40:54.008 CompetitiveModeActivity                                     📊 Evaluation updated: 0.31
22:40:54.009                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.31, emotionalManager: INITIALIZED
22:40:54.009                                                             🎯 First emotional evaluation: 0.31 (threshold: 1.5)
22:40:54.009                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628454s/25s)
22:40:54.009                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:41:30.503                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=6
22:41:30.503                                                             🎯 SQUARE TAPPED: row=1, col=6
22:41:30.504                                                             📝 Player color: black
22:41:30.504                                                             🔍 Selected row/col: -1/-1
22:41:30.861                                                             🎯 Selected piece: p at 1, 6
22:41:31.208                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=6
22:41:31.208                                                             🎯 SQUARE TAPPED: row=2, col=6
22:41:31.208                                                             📝 Player color: black
22:41:31.208                                                             🔍 Selected row/col: 1/6
22:41:31.209                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=2, reaches=false
22:41:31.209                                                             🎯 ATTEMPTING MOVE: g7g6
22:41:31.209                                                             📝 Player color: black
22:41:31.209                                                             🔄 Is player's turn: true
22:41:31.209                                                             🔄 Is white's turn: false
22:41:31.209                                                             ✅ Turn validation passed, making move: g7g6
22:41:31.209 GameViewModel                                               🎯 makePlayerMove called with: g7g6
22:41:31.260                                                             🔍 Validating move: g7g6 (attempt 1)
22:41:31.361                                                             📋 Current position: r1bqkb1r/pp1np1pp/2n1p3/2Pp4/8/2N2N2/PPP2PPP/R1BQKB1R b KQkq - 0 7
22:41:31.413                                                             ⚖️ Move g7g6 legality check: LEGAL
22:41:31.413                                                             ✅ Executing validated move: g7g6
22:41:31.615                                                             📍 New position after move: r1bqkb1r/pp1np2p/2n1p1p1/2Pp4/8/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 0 8
22:41:31.617 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1np2p/2n1p1p1/2Pp4/8/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 0 8
22:41:31.621                                                             📜 Move history updated: 14 moves
22:41:31.621 GameHistoryManager                                          Move added: g7g6
22:41:31.715 GameViewModel                                               🔍 Requesting position evaluation...
22:41:31.715                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:41:31.715                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:41:31.715                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:41:31.716                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:41:31.716                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:41:31.716                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:41:31.716                                                             🔧 gameRepository class: GameRepository
22:41:31.716                                                             🔧 Current thread: main
22:41:31.716 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:41:33.123 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.31 (move=14, alternating=ON, FEN=r1bqkb1r/pp1np2p/2n1)
22:41:33.123                                                             🔄 ALTERNATING: move=14, flip=false, 0.31→0.31 (diff=0.00)
22:41:33.124 GameViewModel                                               ✅ Evaluation received: 0.31
22:41:33.224 CompetitiveModeActivity                                     📊 Evaluation updated: 0.31
22:41:33.224                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.31, emotionalManager: INITIALIZED
22:41:33.224                                                             🎯 First emotional evaluation: 0.31 (threshold: 1.5)
22:41:33.224                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628493s/25s)
22:41:33.224                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:41:33.658 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:41:33.659 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #9)
22:41:33.659                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:41:33.659                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1np2p/2n1p1p1/2Pp4/8/2N2N2/PPP2PPP...
22:41:33.659                                                             🎲 Candidate moves for AI analysis: [f3d4]
22:41:33.659 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:41:33.659                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:41:33.920                                                             📋 Response ID: resp_6858788db2f8819d91d89a1b26f2c7bf084820b14c8816cd
22:41:36.202                                                             🏁 Response completed
22:41:36.203 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f3d4": {"score": 0.0, "reason": "Simplifies the center and leads to symmet...
22:41:36.204                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 386 of {
                                                                           "move_scores": {
                                                                             "f3d4": {"score": 0.0, "reason": "Simplifies the center and leads to symmetry—antithetical to my love for complexity."},
                                                                             "Nb3": {"score": 0.6, "reason": "A modest retreat, preserves tension but lacks ambition."},
                                                                             "Bb5": {"score": 0.7, "reason": "Pins and pressure—more in line with my style, but still somewhat conventional."},
                                                                             "b4": {"score": 1.0, \"reason\": \"Expands on the flank, creates imbalances, and invites complications—pure Alekhine.\"}
                                                                           },
                                                                           "top_choice": "b4",
                                                                           "style_reasoning": "I seek positions that roar with possibility, not whisper with safety. b4 opens the door to adventure.",
                                                                           "confidence": 1.0
                                                                         }
                                                                         	at org.json.JSONTokener.syntaxError(JSONTokener.java:469)
                                                                         	at org.json.JSONTokener.readLiteral(JSONTokener.java:297)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:115)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:380)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:168)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:185)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.parseAIResponse(AIStyleAdvisor.java:380)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.requestAIAdvice(AIStyleAdvisor.java:261)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.lambda$getStyleAdvice$4$com-example-chesspedagogue-AIStyleAdvisor(AIStyleAdvisor.java:172)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor$$ExternalSyntheticLambda2.run(D8$$SyntheticClass:0)
                                                                         	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
                                                                         	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
                                                                         	at java.lang.Thread.run(Thread.java:1012)
22:41:36.237 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f3d4
22:41:36.592 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1np2p/2n1p1p1/2Pp4/3N4/2N5/PPP2PPP/R1BQKB1R b KQkq - 1 8
22:41:36.598                                                             📜 Move history updated: 15 moves
22:41:36.598 GameHistoryManager                                          Move added: f3d4
22:41:36.598 GameViewModel                                               🔍 Requesting position evaluation...
22:41:36.598                                                             🎭 Updating personality context for move: f3d4
22:41:36.599                                                             ✨ Personality context updated for move f3d4 - This is revolutionary!
22:41:36.599                                                             🔍 Checking game end conditions...
22:41:36.700                                                             ✅ Game continues - no end condition detected
22:41:37.908 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.24 (move=15, alternating=ON, FEN=r1bqkb1r/pp1np2p/2n1)
22:41:37.909                                                             🔄 ALTERNATING: move=15, flip=true, -0.24→0.24 (diff=0.07)
22:41:37.910 GameViewModel                                               ✅ Evaluation received: 0.24
22:41:38.011 CompetitiveModeActivity                                     📊 Evaluation updated: 0.24
22:41:38.012                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.24, emotionalManager: INITIALIZED
22:41:38.012                                                             🎯 First emotional evaluation: 0.24 (threshold: 1.5)
22:41:38.012                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628498s/25s)
22:41:38.012                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:41:57.164                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=3
22:41:57.164                                                             🎯 SQUARE TAPPED: row=1, col=3
22:41:57.164                                                             📝 Player color: black
22:41:57.164                                                             🔍 Selected row/col: -1/-1
22:41:57.521                                                             🎯 Selected piece: n at 1, 3
22:41:58.008                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
22:41:58.008                                                             🎯 SQUARE TAPPED: row=0, col=1
22:41:58.008                                                             📝 Player color: black
22:41:58.008                                                             🔍 Selected row/col: 1/3
22:41:58.008                                                             🎯 ATTEMPTING MOVE: d7b8
22:41:58.008                                                             📝 Player color: black
22:41:58.008                                                             🔄 Is player's turn: true
22:41:58.008                                                             🔄 Is white's turn: false
22:41:58.008                                                             ✅ Turn validation passed, making move: d7b8
22:41:58.008 GameViewModel                                               🎯 makePlayerMove called with: d7b8
22:41:58.059                                                             🔍 Validating move: d7b8 (attempt 1)
22:41:58.160                                                             📋 Current position: r1bqkb1r/pp1np2p/2n1p1p1/2Pp4/3N4/2N5/PPP2PPP/R1BQKB1R b KQkq - 1 8
22:41:58.211                                                             ⚖️ Move d7b8 legality check: LEGAL
22:41:58.211                                                             ✅ Executing validated move: d7b8
22:41:58.414                                                             📍 New position after move: rnbqkb1r/pp2p2p/2n1p1p1/2Pp4/3N4/2N5/PPP2PPP/R1BQKB1R w KQkq - 2 9
22:41:58.416 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp2p2p/2n1p1p1/2Pp4/3N4/2N5/PPP2PPP/R1BQKB1R w KQkq - 2 9
22:41:58.420                                                             📜 Move history updated: 16 moves
22:41:58.420 GameHistoryManager                                          Move added: d7b8
22:41:58.516 GameViewModel                                               🔍 Requesting position evaluation...
22:41:58.516                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:41:58.516                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:41:58.516                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:41:58.516                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:41:58.516                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:41:58.516                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:41:58.516                                                             🔧 gameRepository class: GameRepository
22:41:58.517                                                             🔧 Current thread: main
22:41:58.517 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:41:59.923 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.40 (move=16, alternating=ON, FEN=rnbqkb1r/pp2p2p/2n1p)
22:41:59.924                                                             🔄 ALTERNATING: move=16, flip=false, 0.40→0.40 (diff=0.16)
22:41:59.924 GameViewModel                                               ✅ Evaluation received: 0.40
22:42:00.024 CompetitiveModeActivity                                     📊 Evaluation updated: 0.4
22:42:00.024                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.4, emotionalManager: INITIALIZED
22:42:00.024                                                             🎯 First emotional evaluation: 0.4 (threshold: 1.5)
22:42:00.024                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628520s/25s)
22:42:00.024                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:42:00.705 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:42:00.706 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #10)
22:42:00.706                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:42:00.706                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp2p2p/2n1p1p1/2Pp4/3N4/2N5/PPP2PPP/...
22:42:00.706                                                             🎲 Candidate moves for AI analysis: [h2h4]
22:42:00.706 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:42:00.706                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:42:00.874                                                             📋 Response ID: resp_685878a8b8c4819fb272f4ed409f4db901bcc8d7e751aa76
22:42:03.256                                                             🏁 Response completed
22:42:03.257 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "h2h4": {"score": 0.0, "reason": "A flank push with no immediate consequenc...
22:42:03.257                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
22:42:03.257                                                             🎭 AI preferred moves: []
22:42:03.257                                                             💭 AI reasoning: I seek the fight in every position. Moves that provoke tension and imbalance are my favorites.
22:42:03.287 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h2h4
22:42:03.644 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp2p2p/2n1p1p1/2Pp4/3N3P/2N5/PPP2PP1/R1BQKB1R b KQkq - 0 9
22:42:03.650                                                             📜 Move history updated: 17 moves
22:42:03.650 GameHistoryManager                                          Move added: h2h4
22:42:03.650 GameViewModel                                               🔍 Requesting position evaluation...
22:42:03.650                                                             🎭 Updating personality context for move: h2h4
22:42:03.650                                                             ✨ Personality context updated for move h2h4 - This is revolutionary!
22:42:03.651                                                             🔍 Checking game end conditions...
22:42:03.752                                                             ✅ Game continues - no end condition detected
22:42:04.862 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.38 (move=17, alternating=ON, FEN=rnbqkb1r/pp2p2p/2n1p)
22:42:04.863                                                             🔄 ALTERNATING: move=17, flip=true, -0.38→0.38 (diff=0.02)
22:42:04.864 GameViewModel                                               ✅ Evaluation received: 0.38
22:42:04.965 CompetitiveModeActivity                                     📊 Evaluation updated: 0.38
22:42:04.965                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.38, emotionalManager: INITIALIZED
22:42:04.965                                                             🎯 First emotional evaluation: 0.38 (threshold: 1.5)
22:42:04.965                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628524s/25s)
22:42:04.966                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:42:19.229                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
22:42:19.229                                                             🎯 SQUARE TAPPED: row=2, col=4
22:42:19.229                                                             📝 Player color: black
22:42:19.229                                                             🔍 Selected row/col: -1/-1
22:42:19.587                                                             🎯 Selected piece: p at 2, 4
22:42:20.028                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
22:42:20.028                                                             🎯 SQUARE TAPPED: row=3, col=4
22:42:20.028                                                             📝 Player color: black
22:42:20.028                                                             🔍 Selected row/col: 2/4
22:42:20.028                                                             ♟️ Promotion check: piece=p, fromRow=2, toRow=3, reaches=false
22:42:20.028                                                             🎯 ATTEMPTING MOVE: e6e5
22:42:20.028                                                             📝 Player color: black
22:42:20.029                                                             🔄 Is player's turn: true
22:42:20.029                                                             🔄 Is white's turn: false
22:42:20.029                                                             ✅ Turn validation passed, making move: e6e5
22:42:20.029 GameViewModel                                               🎯 makePlayerMove called with: e6e5
22:42:20.079                                                             🔍 Validating move: e6e5 (attempt 1)
22:42:20.181                                                             📋 Current position: rnbqkb1r/pp2p2p/2n1p1p1/2Pp4/3N3P/2N5/PPP2PP1/R1BQKB1R b KQkq - 0 9
22:42:20.232                                                             ⚖️ Move e6e5 legality check: LEGAL
22:42:20.232                                                             ✅ Executing validated move: e6e5
22:42:20.434                                                             📍 New position after move: rnbqkb1r/pp2p2p/2n3p1/2Ppp3/3N3P/2N5/PPP2PP1/R1BQKB1R w KQkq - 0 10
22:42:20.437 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp2p2p/2n3p1/2Ppp3/3N3P/2N5/PPP2PP1/R1BQKB1R w KQkq - 0 10
22:42:20.442                                                             📜 Move history updated: 18 moves
22:42:20.442 GameHistoryManager                                          Move added: e6e5
22:42:20.535 GameViewModel                                               🔍 Requesting position evaluation...
22:42:20.535                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:42:20.535                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:42:20.535                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:42:20.535                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:42:20.535                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:42:20.535                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:42:20.535                                                             🔧 gameRepository class: GameRepository
22:42:20.535                                                             🔧 Current thread: main
22:42:20.536 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:42:22.094 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.85 (move=18, alternating=ON, FEN=rnbqkb1r/pp2p2p/2n3p)
22:42:22.095                                                             🔄 ALTERNATING: move=18, flip=false, 0.85→0.85 (diff=0.47)
22:42:22.095 GameViewModel                                               ✅ Evaluation received: 0.85
22:42:22.196 CompetitiveModeActivity                                     📊 Evaluation updated: 0.85
22:42:22.196                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.85, emotionalManager: INITIALIZED
22:42:22.196                                                             🎯 First emotional evaluation: 0.85 (threshold: 1.5)
22:42:22.196                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628542s/25s)
22:42:22.196                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:42:22.781 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:42:22.782 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #11)
22:42:22.782                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:42:22.782                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp2p2p/2n3p1/2Ppp3/3N3P/2N5/PPP2PP1/...
22:42:22.782                                                             🎲 Candidate moves for AI analysis: [d4b3]
22:42:22.782 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:42:22.782                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:42:23.072                                                             📋 Response ID: resp_685878bed23c819fa23ff343b8fd93590b803aeea0202fac
22:42:25.410                                                             🏁 Response completed
22:42:25.411 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d4b3": {"score": 0.0, "reason": "Passive retreat, avoids confrontation and...
22:42:25.411                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
22:42:25.411                                                             🎭 AI preferred moves: []
22:42:25.412                                                             💭 AI reasoning: I seek to dominate through complexity and initiative, not by accepting quiet equality.
22:42:25.443 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d4b3
22:42:25.804 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp2p2p/2n3p1/2Ppp3/7P/1NN5/PPP2PP1/R1BQKB1R b KQkq - 1 10
22:42:25.811                                                             📜 Move history updated: 19 moves
22:42:25.811 GameHistoryManager                                          Move added: d4b3
22:42:25.811 GameViewModel                                               🔍 Requesting position evaluation...
22:42:25.812                                                             🎭 Updating personality context for move: d4b3
22:42:25.812                                                             ✨ Personality context updated for move d4b3 - This is revolutionary!
22:42:25.812                                                             🔍 Checking game end conditions...
22:42:25.914                                                             ✅ Game continues - no end condition detected
22:42:26.834 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.84 (move=19, alternating=ON, FEN=rnbqkb1r/pp2p2p/2n3p)
22:42:26.834                                                             🔄 ALTERNATING: move=19, flip=true, -0.84→0.84 (diff=0.01)
22:42:26.844 GameViewModel                                               ✅ Evaluation received: 0.84
22:42:26.947 CompetitiveModeActivity                                     📊 Evaluation updated: 0.84
22:42:26.947                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.84, emotionalManager: INITIALIZED
22:42:26.947                                                             🎯 First emotional evaluation: 0.84 (threshold: 1.5)
22:42:26.947                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628546s/25s)
22:42:26.947                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:42:38.619                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=2
22:42:38.620                                                             🎯 SQUARE TAPPED: row=0, col=2
22:42:38.620                                                             📝 Player color: black
22:42:38.620                                                             🔍 Selected row/col: -1/-1
22:42:38.977                                                             🎯 Selected piece: b at 0, 2
22:42:39.495                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
22:42:39.495                                                             🎯 SQUARE TAPPED: row=2, col=4
22:42:39.495                                                             📝 Player color: black
22:42:39.496                                                             🔍 Selected row/col: 0/2
22:42:39.496                                                             🎯 ATTEMPTING MOVE: c8e6
22:42:39.496                                                             📝 Player color: black
22:42:39.496                                                             🔄 Is player's turn: true
22:42:39.496                                                             🔄 Is white's turn: false
22:42:39.496                                                             ✅ Turn validation passed, making move: c8e6
22:42:39.496 GameViewModel                                               🎯 makePlayerMove called with: c8e6
22:42:39.547                                                             🔍 Validating move: c8e6 (attempt 1)
22:42:39.648                                                             📋 Current position: rnbqkb1r/pp2p2p/2n3p1/2Ppp3/7P/1NN5/PPP2PP1/R1BQKB1R b KQkq - 1 10
22:42:39.700                                                             ⚖️ Move c8e6 legality check: LEGAL
22:42:39.700                                                             ✅ Executing validated move: c8e6
22:42:39.902                                                             📍 New position after move: rn1qkb1r/pp2p2p/2n1b1p1/2Ppp3/7P/1NN5/PPP2PP1/R1BQKB1R w KQkq - 2 11
22:42:39.905 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkb1r/pp2p2p/2n1b1p1/2Ppp3/7P/1NN5/PPP2PP1/R1BQKB1R w KQkq - 2 11
22:42:39.910                                                             📜 Move history updated: 20 moves
22:42:39.910 GameHistoryManager                                          Move added: c8e6
22:42:40.003 GameViewModel                                               🔍 Requesting position evaluation...
22:42:40.003                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:42:40.003                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:42:40.003                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:42:40.004                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:42:40.004                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:42:40.004                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:42:40.004                                                             🔧 gameRepository class: GameRepository
22:42:40.004                                                             🔧 Current thread: main
22:42:40.004 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:42:41.566 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.95 (move=20, alternating=ON, FEN=rn1qkb1r/pp2p2p/2n1b)
22:42:41.566                                                             🔄 ALTERNATING: move=20, flip=false, 0.95→0.95 (diff=0.11)
22:42:41.566 GameViewModel                                               ✅ Evaluation received: 0.95
22:42:41.667 CompetitiveModeActivity                                     📊 Evaluation updated: 0.95
22:42:41.667                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.95, emotionalManager: INITIALIZED
22:42:41.667                                                             🎯 First emotional evaluation: 0.95 (threshold: 1.5)
22:42:41.667                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628561s/25s)
22:42:41.667                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:42:42.306 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
22:42:42.307 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #12)
22:42:42.307                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:42:42.307                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rn1qkb1r/pp2p2p/2n1b1p1/2Ppp3/7P/1NN5/PPP2PP1...
22:42:42.307                                                             🎲 Candidate moves for AI analysis: [f1b5]
22:42:42.307 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:42:42.307                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:42:42.528                                                             📋 Response ID: resp_685878d2553c819dbcc223db8b25b4890cd5f0726ef8cad0
22:42:45.157                                                             🏁 Response completed
22:42:45.157 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f1b5": {"score": 0.0, "reason": "A passive exchange, leading to symmetry a...
22:42:45.158                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 293 of {
                                                                           "move_scores": {
                                                                             "f1b5": {"score": 0.0, "reason": "A passive exchange, leading to symmetry and dullness. Not worthy of the fight."},
                                                                             "cxd6": {"score": 0.9, "reason": "Opens the position and creates imbalance. Fits my love for complexity and initiative."},
                                                                             "f4": {"score": 1.0, \"reason\": \"A bold, double-edged attempt to break open the center. Perfect for Alekhine’s spirit.\"}
                                                                           },
                                                                           "top_choice": "f4",
                                                                           "style_reasoning": "I seek the fight, not the draw. Positions like this demand courage and imagination.",
                                                                           "confidence": 0.95
                                                                         }
                                                                         	at org.json.JSONTokener.syntaxError(JSONTokener.java:469)
                                                                         	at org.json.JSONTokener.readLiteral(JSONTokener.java:297)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:115)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:380)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:168)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:185)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.parseAIResponse(AIStyleAdvisor.java:380)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.requestAIAdvice(AIStyleAdvisor.java:261)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.lambda$getStyleAdvice$4$com-example-chesspedagogue-AIStyleAdvisor(AIStyleAdvisor.java:172)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor$$ExternalSyntheticLambda2.run(D8$$SyntheticClass:0)
                                                                         	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
                                                                         	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
                                                                         	at java.lang.Thread.run(Thread.java:1012)
22:42:45.194 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f1b5
22:42:45.552 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkb1r/pp2p2p/2n1b1p1/1BPpp3/7P/1NN5/PPP2PP1/R1BQK2R b KQkq - 3 11
22:42:45.560                                                             📜 Move history updated: 21 moves
22:42:45.560 GameHistoryManager                                          Move added: f1b5
22:42:45.560 GameViewModel                                               🔍 Requesting position evaluation...
22:42:45.560                                                             🎭 Updating personality context for move: f1b5
22:42:45.560                                                             ✨ Personality context updated for move f1b5 - This is revolutionary!
22:42:45.560                                                             🔍 Checking game end conditions...
22:42:45.662                                                             ✅ Game continues - no end condition detected
22:42:46.977 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.37 (move=21, alternating=ON, FEN=rn1qkb1r/pp2p2p/2n1b)
22:42:46.977                                                             🔄 ALTERNATING: move=21, flip=true, -0.37→0.37 (diff=0.58)
22:42:46.978 GameViewModel                                               ✅ Evaluation received: 0.37
22:42:47.081 CompetitiveModeActivity                                     📊 Evaluation updated: 0.37
22:42:47.081                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.37, emotionalManager: INITIALIZED
22:42:47.081                                                             🎯 First emotional evaluation: 0.37 (threshold: 1.5)
22:42:47.081                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628567s/25s)
22:42:47.081                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:43:37.178                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=5
22:43:37.178                                                             🎯 SQUARE TAPPED: row=0, col=5
22:43:37.178                                                             📝 Player color: black
22:43:37.178                                                             🔍 Selected row/col: -1/-1
22:43:37.536                                                             🎯 Selected piece: b at 0, 5
22:43:37.878                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=6
22:43:37.878                                                             🎯 SQUARE TAPPED: row=1, col=6
22:43:37.878                                                             📝 Player color: black
22:43:37.878                                                             🔍 Selected row/col: 0/5
22:43:37.878                                                             🎯 ATTEMPTING MOVE: f8g7
22:43:37.878                                                             📝 Player color: black
22:43:37.878                                                             🔄 Is player's turn: true
22:43:37.878                                                             🔄 Is white's turn: false
22:43:37.878                                                             ✅ Turn validation passed, making move: f8g7
22:43:37.878 GameViewModel                                               🎯 makePlayerMove called with: f8g7
22:43:37.929                                                             🔍 Validating move: f8g7 (attempt 1)
22:43:38.030                                                             📋 Current position: rn1qkb1r/pp2p2p/2n1b1p1/1BPpp3/7P/1NN5/PPP2PP1/R1BQK2R b KQkq - 3 11
22:43:38.081                                                             ⚖️ Move f8g7 legality check: LEGAL
22:43:38.081                                                             ✅ Executing validated move: f8g7
22:43:38.284                                                             📍 New position after move: rn1qk2r/pp2p1bp/2n1b1p1/1BPpp3/7P/1NN5/PPP2PP1/R1BQK2R w KQkq - 4 12
22:43:38.286 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qk2r/pp2p1bp/2n1b1p1/1BPpp3/7P/1NN5/PPP2PP1/R1BQK2R w KQkq - 4 12
22:43:38.291                                                             📜 Move history updated: 22 moves
22:43:38.291 GameHistoryManager                                          Move added: f8g7
22:43:38.384 GameViewModel                                               🔍 Requesting position evaluation...
22:43:38.384                                                             🎭 Using PERSONALITY ENGINE for move calculation!
22:43:38.384                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
22:43:38.384                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
22:43:38.385                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
22:43:38.385                                                             🔧 DEBUG: gameRepository instance = NOT NULL
22:43:38.385                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
22:43:38.385                                                             🔧 gameRepository class: GameRepository
22:43:38.385                                                             🔧 Current thread: main
22:43:38.385 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
22:43:40.297 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.68 (move=22, alternating=ON, FEN=rn1qk2r/pp2p1bp/2n1b)
22:43:40.297                                                             🔄 ALTERNATING: move=22, flip=false, 0.68→0.68 (diff=0.31)
22:43:40.298 GameViewModel                                               ✅ Evaluation received: 0.68
22:43:40.398 CompetitiveModeActivity                                     📊 Evaluation updated: 0.68
22:43:40.398                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.68, emotionalManager: INITIALIZED
22:43:40.398                                                             🎯 First emotional evaluation: 0.68 (threshold: 1.5)
22:43:40.398                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628620s/25s)
22:43:40.398                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:43:40.681 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
22:43:40.682 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #13)
22:43:40.682                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:43:40.682                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rn1qk2r/pp2p1bp/2n1b1p1/1BPpp3/7P/1NN5/PPP2PP...
22:43:40.682                                                             🎲 Candidate moves for AI analysis: [c1d2]
22:43:40.682 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:43:40.682                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:43:40.999                                                             📋 Response ID: resp_6858790cbab481a2b044fc2ea7ee0eae06178ed8f0223652
22:43:42.767                                                             🏁 Response completed
22:43:42.768 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c1d2": {"score": 0.0, "reason": "Passive and retreating, lacks ambition an...
22:43:42.768                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
22:43:42.768                                                             🎭 AI preferred moves: []
22:43:42.768                                                             💭 AI reasoning: I would choose the path that leads to chaos and opportunity, not the safe and simple route.
22:43:42.804 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c1d2
22:43:43.160 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qk2r/pp2p1bp/2n1b1p1/1BPpp3/7P/1NN5/PPPB1PP1/R2QK2R b KQkq - 5 12
22:43:43.165                                                             📜 Move history updated: 23 moves
22:43:43.165 GameHistoryManager                                          Move added: c1d2
22:43:43.165 GameViewModel                                               🔍 Requesting position evaluation...
22:43:43.166                                                             🎭 Updating personality context for move: c1d2
22:43:43.166                                                             ✨ Personality context updated for move c1d2 - This is revolutionary!
22:43:43.166                                                             🔍 Checking game end conditions...
22:43:43.267                                                             ✅ Game continues - no end condition detected
22:43:44.791 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.67 (move=23, alternating=ON, FEN=rn1qk2r/pp2p1bp/2n1b)
22:43:44.791                                                             🔄 ALTERNATING: move=23, flip=true, 0.67→-0.67 (diff=1.35)
22:43:44.791 GameViewModel                                               ✅ Evaluation received: -0.67
22:43:44.893 CompetitiveModeActivity                                     📊 Evaluation updated: -0.67
22:43:44.893                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.67, emotionalManager: INITIALIZED
22:43:44.893                                                             🎯 First emotional evaluation: -0.67 (threshold: 1.5)
22:43:44.893                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750628624s/25s)
22:43:44.893                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
22:44:24.485                                                             🧪 Starting Competitive Mode Style Validation...
22:44:24.491 Toast                                                       show: caller = com.example.chesspedagogue.CompetitiveModeActivity.runCompetitiveValidation:2844 
22:44:24.492                                                             show: isDexDualMode = false
22:44:24.492                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
22:44:24.497 AlekhineStyleValidator                                      🧪 Starting Quick Alekhine Style Validation Test...
22:44:24.497                                                             🎯 Testing: World Championship Game 11 - Decisive attacking move
22:44:25.851 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:44:25.852 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #14)
22:44:25.852                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:25.852                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/pp2nppp/2n1p3/3pP3/2pP4/2N1BN2/PP2BP...
22:44:25.852                                                             🎲 Candidate moves for AI analysis: [e1g1]
22:44:25.852 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:25.852                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:44:26.157                                                             📋 Response ID: resp_68587939e248819db3e9eedcdb3e73650df42afaf168fda3
22:44:28.520                                                             🏁 Response completed
22:44:28.521 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e1g1": {"score": 0.0, "reason": "Castling here is premature and dulls the ...
22:44:28.521                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
22:44:28.521                                                             🎭 AI preferred moves: []
22:44:28.521                                                             💭 AI reasoning: I seek to unsettle my opponent and navigate the resulting chaos, not to settle for safe equality.
22:44:28.553 AlekhineStyleValidator                                        📊 AI Move: e1g1 | Historical: h2h4 | Match: ❌ | Style: 0.60
22:44:28.553                                                             🎯 Testing: Brilliant pawn sacrifice leading to overwhelming attack
22:44:30.259 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:44:30.260 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #15)
22:44:30.260                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:30.260                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqk2r/pp2bppp/4pn2/3p4/2PP4/2N2N2/PP2BPPP/R...
22:44:30.261                                                             🎲 Candidate moves for AI analysis: [c4c5]
22:44:30.261 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:30.261                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:44:30.561                                                             📋 Response ID: resp_6858793e46e48192a0dacd09a6894f4b08820a91f3942b64
22:44:32.212                                                             🏁 Response completed
22:44:32.213 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c4c5": {"score": 0.0, "reason": "Closing the center and locking the positi...
22:44:32.213                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
22:44:32.214                                                             🎭 AI preferred moves: []
22:44:32.214                                                             💭 AI reasoning: I crave the battlefield, not the fortress. Moves that open lines and create tension are my preference.
22:44:32.252 AlekhineStyleValidator                                        📊 AI Move: c4c5 | Historical: d4d5 | Match: ❌ | Style: 0.60
22:44:32.252                                                             🎯 Testing: Space advantage in center with positional pressure
22:44:33.861 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:44:33.863 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #16)
22:44:33.863                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:33.863                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qkb1r/1b1n1ppp/p2ppn2/1p6/3PP3/1QN2N2/PP1B1...
22:44:33.863                                                             🎲 Candidate moves for AI analysis: [a2a4]
22:44:33.863 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:33.863                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:44:34.145                                                             📋 Response ID: resp_68587941eb4c8192a64bffbc82875678052fb0ec9f61a3ab
22:44:36.657                                                             🏁 Response completed
22:44:36.658 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "a2a4": {"score": 0.0, "reason": "Passive and slow. Does not seek complicat...
22:44:36.659                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 368 of {
                                                                           "move_scores": {
                                                                             "a2a4": {"score": 0.0, "reason": "Passive and slow. Does not seek complications or imbalance."},
                                                                             "e4e5": {"score": 0.9, "reason": "Forces the issue, opens lines, and creates tension. Fits my desire for dynamic play."},
                                                                             "d4d5": {"score": 0.7, "reason": "Creates imbalance and space, but less direct than e5."},
                                                                             "b4": {"score": 1.0, \"reason\": \"Bold, imbalanced, and full of latent complications. The very embodiment of Alekhine’s chess.\"}
                                                                           },
                                                                           "top_choice": "b4",
                                                                           "style_reasoning": "I seek out the fight and the beauty hidden in chaos. Moves like b4 open the door to the kind of chess I love to play.",
                                                                           "confidence": 1.0
                                                                         }
                                                                         	at org.json.JSONTokener.syntaxError(JSONTokener.java:469)
                                                                         	at org.json.JSONTokener.readLiteral(JSONTokener.java:297)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:115)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:380)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:168)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:185)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.parseAIResponse(AIStyleAdvisor.java:380)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.requestAIAdvice(AIStyleAdvisor.java:261)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.lambda$getStyleAdvice$4$com-example-chesspedagogue-AIStyleAdvisor(AIStyleAdvisor.java:172)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor$$ExternalSyntheticLambda2.run(D8$$SyntheticClass:0)
                                                                         	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
                                                                         	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
                                                                         	at java.lang.Thread.run(Thread.java:1012)
22:44:36.692 AlekhineStyleValidator                                        📊 AI Move: a2a4 | Historical: e4e5 | Match: ❌ | Style: 0.70
22:44:36.692                                                             🎯 Testing: Aggressive piece development creating immediate threats
22:44:38.440 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:44:38.441 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #17)
22:44:38.441                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:38.441                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/3P1N2/PPP2P...
22:44:38.441                                                             🎲 Candidate moves for AI analysis: [f3g5]
22:44:38.441 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:38.441                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:44:38.858                                                             📋 Response ID: resp_685879467a7c81a0a3377cfc81870aa2084748b8c4f04698
22:44:41.215                                                             🏁 Response completed
22:44:41.216 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "Nf3g5": {"score": 0.0, "reason": "A passive, superficial attempt at pressu...
22:44:41.217                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 157 of {
                                                                           "move_scores": {
                                                                             "Nf3g5": {"score": 0.0, "reason": "A passive, superficial attempt at pressure. Lacks depth and courage."},
                                                                             "d3": {"score": 0.5, \"reason\": \"Solid but uninspired. Does not create the imbalances I seek.\"},
                                                                             "Nc3": {\"score\": 0.7, \"reason\": \"Develops with potential for complexity, but still safe.\"},
                                                                             \"O-O\": {\"score\": 0.4, \"reason\": \"Too quiet for my taste at this stage.\"},
                                                                             \"Bb5\": {\"score\": 0.9, \"reason\": \"Pins the knight and invites tension. Fits my love of complexity.\"}
                                                                           },
                                                                           \"top_choice\": \"Bb5\",
                                                                           \"style_reasoning\": \"I seek to unsettle and provoke, not merely develop. Bb5 challenges symmetry and invites complications.\",
                                                                           \"confidence\": 0.9
                                                                         }
                                                                         	at org.json.JSONTokener.syntaxError(JSONTokener.java:469)
                                                                         	at org.json.JSONTokener.readLiteral(JSONTokener.java:297)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:115)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:380)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:168)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:185)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.parseAIResponse(AIStyleAdvisor.java:380)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.requestAIAdvice(AIStyleAdvisor.java:261)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.lambda$getStyleAdvice$4$com-example-chesspedagogue-AIStyleAdvisor(AIStyleAdvisor.java:172)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor$$ExternalSyntheticLambda2.run(D8$$SyntheticClass:0)
                                                                         	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
                                                                         	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
                                                                         	at java.lang.Thread.run(Thread.java:1012)
22:44:41.253 AlekhineStyleValidator                                        📊 AI Move: f3g5 | Historical: f3g5 | Match: ✅ | Style: 1.00
22:44:41.253                                                             🎯 Testing: Classical pawn endgame technique demonstration
22:44:41.538 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 2 historical positions
22:44:41.539 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #18)
22:44:41.539                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:41.539                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): 8/8/1p6/pP6/P7/8/4k3/4K3 w - - 0 50
                                                                         You are p...
22:44:41.539                                                             🎲 Candidate moves for AI analysis: [e1e2]
22:44:41.540 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:41.540                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:44:41.826                                                             📋 Response ID: resp_685879498ecc819e82e34018edbf958d0058130995aa0d4d
22:44:42.995                                                             🏁 Response completed
22:44:42.995 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e1e2": {"score": 0.0, "reason": "Passive and symmetrical; avoids complexit...
22:44:42.996                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
22:44:42.996                                                             🎭 AI preferred moves: [e1e2]
22:44:42.996                                                             💭 AI reasoning: In this position, any move that concedes the initiative is a betrayal of the fighting spirit. Alekhine would never settle for dull equality.
22:44:43.026 AlekhineStyleValidator                                        📊 AI Move: e1e2 | Historical: b5b6 | Match: ❌ | Style: 0.50
22:44:43.026                                                             🎯 Testing: Tactical shot preparing devastating attack on kingside
22:44:44.561 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:44:44.562 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #19)
22:44:44.562                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:44.562                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qk2r/1b2bppp/p2p1n2/npp1p3/4P3/1BP2N2/PP1P1...
22:44:44.562                                                             🎲 Candidate moves for AI analysis: [f3h4]
22:44:44.562 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:44.562                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:44:44.794                                                             📋 Response ID: resp_6858794c96108191be954af1db00c1ed0c19b856123bccbd
22:44:47.060                                                             🏁 Response completed
22:44:47.061 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f3h4": {"score": 0.0, "reason": "A passive, retreating move—antithetical t...
22:44:47.062                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Unterminated object at character 499 of {
                                                                           "move_scores": {
                                                                             "f3h4": {"score": 0.0, "reason": "A passive, retreating move—antithetical to my preference for confrontation and initiative."},
                                                                             "d4": {"score": 0.7, "reason": "Opens the center and invites complexity, though lacks immediate spark."},
                                                                             "a4": {"score": 0.8, "reason": "Challenges the queenside structure and seeks imbalance—more in line with my style."},
                                                                             "b4": {"score": 0.9, "reason": "Bold and provocative, aiming to unsettle the opponent and create open lines.""}
                                                                           },
                                                                           "top_choice": "b4",
                                                                           "style_reasoning": "I seek to dictate the terms of battle, not respond timidly. Moves like b4 embody my desire for complexity and attack.",
                                                                           "confidence": 0.9
                                                                         }
                                                                         	at org.json.JSONTokener.syntaxError(JSONTokener.java:469)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:412)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONTokener.readObject(JSONTokener.java:403)
                                                                         	at org.json.JSONTokener.nextValue(JSONTokener.java:104)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:168)
                                                                         	at org.json.JSONObject.<init>(JSONObject.java:185)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.parseAIResponse(AIStyleAdvisor.java:380)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.requestAIAdvice(AIStyleAdvisor.java:261)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor.lambda$getStyleAdvice$4$com-example-chesspedagogue-AIStyleAdvisor(AIStyleAdvisor.java:172)
                                                                         	at com.example.chesspedagogue.AIStyleAdvisor$$ExternalSyntheticLambda2.run(D8$$SyntheticClass:0)
                                                                         	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
                                                                         	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:644)
                                                                         	at java.lang.Thread.run(Thread.java:1012)
22:44:47.100 AlekhineStyleValidator                                        📊 AI Move: f3h4 | Historical: f3h4 | Match: ✅ | Style: 1.00
22:44:47.100                                                             🎯 Testing: Legendary bishop sacrifice leading to forced mate
22:44:48.650 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:44:48.651 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #20)
22:44:48.651                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:48.651                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1p1ppp/2n2n2/2p1p3/2B1P3/3P1N2/PPP...
22:44:48.651                                                             🎲 Candidate moves for AI analysis: [f3g5]
22:44:48.651 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
22:44:48.651                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
22:44:48.892                                                             📋 Response ID: resp_68587950ab8081a085239cb9f94c247a05e6ec9a51b71f76
22:44:50.996                                                             🏁 Response completed
22:44:50.997 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f3g5": {"score": 0.0, "reason": "A passive, superficial attempt at aggress...
22:44:50.998                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
22:44:50.998                                                             🎭 AI preferred moves: []
22:44:50.998                                                             💭 AI reasoning: I seek positions where my calculation and imagination can flourish. d4 offers the battlefield I desire.
22:44:51.037 AlekhineStyleValidator                                        📊 AI Move: f3g5 | Historical: c4f7 | Match: ❌ | Style: 0.60
22:44:51.037                                                             🎯 Testing: Positional pawn advance creating long-term advantages
22:44:52.336 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
22:44:52.337 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
22:44:52.356 AlekhineStyleValidator                                        📊 AI Move: f1d3 | Historical: a2a4 | Match: ❌ | Style: 0.70
22:44:52.356                                                             🎯 Testing: Central breakthrough against the former world champion
22:44:54.139 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 2 historical positions
22:44:54.140 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
22:44:54.158 AlekhineStyleValidator                                        📊 AI Move: c4b3 | Historical: d4d5 | Match: ❌ | Style: 0.60
22:44:54.158                                                             🎯 Testing: Alekhine Defense demonstration - dynamic counterplay
22:44:55.727 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
22:44:55.728 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
22:44:55.748 AlekhineStyleValidator                                        📊 AI Move: e2e4 | Historical: e2e4 | Match: ✅ | Style: 1.00
22:44:55.748                                                             ✅ Quick validation test completed!
22:44:55.748 CompetitiveModeActivity                                     🧪 COMPETITIVE VALIDATION RESULTS:
                                                                         🎯 ALEKHINE COMPETITIVE MODE VALIDATION
                                                                         
                                                                         🎯 ALEKHINE STYLE ACCURACY REPORT\n=======================================\nOverall Accuracy: 55.8/100\nHistorical Match Rate: 30.0%\nStyle Consistency: 73.0/100\nTactical Patterns: 75.0/100\nPositional Patterns: 70.0/100\nEndgame Patterns: 65.0/100\n\n🎖️ Performance Grade: NEEDS SIGNIFICANT IMPROVEMENT ⚠️\n
                                                                         
                                                                         🎮 Competitive Settings:
                                                                         Master: alekhine
                                                                         Player: black
                                                                         Skill Level: 14