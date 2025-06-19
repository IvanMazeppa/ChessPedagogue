02:24:19.924 Dialog                  com.example.chesspedagogue          mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
02:24:19.940 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@f2325fa
02:24:19.950 ScrollView                                                  initGoToTop
02:24:19.962 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{6d58e76 V.E...... R.....I. 0,0-0,0}[MainActivity], caller=android.view.WindowManagerImpl.addView:158 android.app.Dialog.show:511 com.example.chesspedagogue.MainActivity.launchCompetitiveMode:3146 
02:24:19.963 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
02:24:19.971 VRI[MainAc...y]@f99cc77                                     synced displayState. AttachInfo displayState=2
02:24:19.972                                                             setView = com.android.internal.policy.DecorView@6d58e76 IsHRR=false TM=true
02:24:20.003 BufferQueueProducer                                         [](id:418d00000002,api:0,p:0,c:16781) setDequeueTimeout:2077252342
02:24:20.004 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
02:24:20.004                                                             Access denied finding property "vendor.gpp.create_frc_extension"
02:24:20.004 VRI[MainAc...y]@f99cc77                                     Relayout returned: old=(0,100,1440,2908) new=(36,165,1404,2843) relayoutAsync=false req=(1368,2678)0 dur=4 res=0x3 s={true 0xb40000718a8be000} ch=true seqId=0
02:24:20.005                                                             performConfigurationChange setNightDimText nightDimLevel=0
02:24:20.005                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb40000718a8be000} hwInitialized=true
02:24:20.021 AbsListView                                                  in onLayout changed 
02:24:20.022 ScrollView                                                   onsize change changed 
02:24:20.022 VRI[MainAc...y]@f99cc77                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
02:24:20.023                                                             Setup new sync=wmsSync-VRI[MainActivity]@f99cc77#4
02:24:20.023                                                             Creating new active sync group VRI[MainActivity]@f99cc77#5
02:24:20.023                                                             registerCallbacksForSync syncBuffer=false
02:24:20.026                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
02:24:20.027                                                             mWNT: t=0xb4000071ed004080 mBlastBufferQueue=0xb4000071ecff9f00 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
02:24:20.027                                                             Setting up sync and frameCommitCallback
02:24:20.029 BLASTBufferQueue                                            [VRI[MainActivity]@f99cc77#2](f:0,a:0,s:0) onFrameAvailable the first frame is available
02:24:20.029 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 34428, bufferData(ID: 72073846194187, frameNumber: 1)
02:24:20.030 VRI[MainAc...y]@f99cc77                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
02:24:20.030 HWUI                                                        CFMS:: SetUp Pid : 16781    Tid : 16805
02:24:20.030 VRI[MainAc...y]@f99cc77                                     reportDrawFinished seqId=0
02:24:20.031 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
02:24:20.062 VRI[MainAc...y]@f99cc77                                     mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb40000718a8be000}
02:24:21.252                                                             ViewPostIme pointer 0
02:24:21.254                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[MainActivity]@f99cc77
02:24:21.422                                                             ViewPostIme pointer 1
02:24:21.422 AbsListView                                                 onTouchUp() mTouchMode : 2
02:24:21.434 WindowManager                                               WindowManagerGlobal#removeView, ty=2, view=com.android.internal.policy.DecorView{6d58e76 V.E...... R....... 0,0-1368,2678}[MainActivity], caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.app.Dialog.dismissDialog:808 
02:24:21.434 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@8d4b913
02:24:21.437 HWUI                                                        endAllActiveAnimators on 0xb4000071f8d8b800 (AlertController$RecycleListView) with handle 0xb400007196335e80
02:24:21.437 VRI[MainAc...y]@f99cc77                                     dispatchDetachedFromWindow
02:24:21.443 InputEventReceiver                                          Attempted to finish an input event but the input event receiver has already been disposed.
02:24:21.445 VoiceControlManager                                         📋 Unregistered voice command listener
02:24:21.450 ActivityThread                                              com.example.chesspedagogue will use render engine as VK
02:24:21.458 CompetitiveModeActivity                                     🏆 Starting competitive mode against chess master...
02:24:21.461 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@eacbd4
02:24:21.484 ScrollView                                                  initGoToTop
02:24:21.488 CompetitiveModeActivity                                     🔄 Synchronizing master selection: alekhine
02:24:21.488 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:24:21.489 CompetitiveModeActivity                                     🔍 Master sync verification:
02:24:21.489                                                               - Voice system (ChessFineTunedModels): alekhine
02:24:21.489                                                               - App system (ChessAppPrefs): alekhine
02:24:21.489                                                               - FineTunedModelManager: alekhine
02:24:21.489                                                             ✅ Master synchronization SUCCESS! All systems consistent.
02:24:21.489                                                             🎯 Competitive config - Master: alekhine, Color: white, Skill: 10, ELO: 1750
02:24:21.489                                                             ✅ Master synchronized across all SharedPreferences stores
02:24:21.489                                                             🎨 Initializing views...
02:24:21.490                                                             ✅ Dynamic master elements set for: alekhine
02:24:21.490                                                             ✅ Voice status indicator initialized
02:24:21.490                                                             ✅ Views initialized
02:24:21.490                                                             🎭 Initializing emotional intelligence systems...
02:24:21.490                                                             🧠 Initializing EQ system with historical emotional data...
02:24:21.491 EmotionalIntelligence                                       🧠 Loaded emotional history for alekhine vs player: 0 events, momentum: 0.25
02:24:21.491 CompetitiveModeActivity                                     ✅ EQ system initialized - emotional reactions and relationships should now work!
02:24:21.493 EmotionalS...egyLearner                                     🧠 Loaded 0 master strategy profiles from database
02:24:21.493                                                             🧠🎯 EmotionalStrategyLearner initialized - Ready for adaptive learning!
02:24:21.494 CrossMaste...ectiveness                                     ✅ Loaded global effectiveness statistics
02:24:21.494 CompetitiveModeActivity                                     🧠 Phase 3: Adaptive conversation strategy manager initialized
02:24:21.494                                                             ✅ Emotional intelligence systems initialized
02:24:21.494                                                             🎲 Initializing personality engine for alekhine...
02:24:21.494 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:24:21.494 DynamicRelationship                                         ✅ Loaded existing relationship dynamics
02:24:21.494 CompetitiveModeActivity                                     🎲 Creating PersonalityEngine instance...
02:24:21.494                                                             🎯 Setting current master to: alekhine
02:24:21.495                                                             🚀 CALLING initializeMasterData for: alekhine
02:24:21.495                                                             🎭 Enabling personality play...
02:24:21.497                                                             ⚖️ Using splash difficulty - ELO: 1750, Skill: 10
02:24:21.497                                                             ✅ Personality engine initialized for alekhine
02:24:21.497                                                             🔍 Running personality engine database diagnostic...
02:24:21.497 PersonalityDiagnostic                                       🔍 DIAGNOSING PERSONALITY ENGINE DATABASE FOR: alekhine
02:24:21.498                                                             📊 Master 'alekhine' - hasData: true, positions: 16368
02:24:21.499                                                             ✅ Data already exists - testing sample query...
02:24:21.499                                                             🧪 Testing sample query for alekhine...
02:24:21.499                                                             🧪 Sample query returned 5 results
02:24:21.499                                                             ✅ DATABASE QUERY WORKING! Sample position found:
02:24:21.499                                                                Master: alekhine
02:24:21.499                                                                FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq
02:24:21.499                                                                Opponent: Frank Marshall
02:24:21.502                                                             📊 Complete database stats: {anand=1870, kramnik=6707, kasparov=5010, alekhine=16368, fischer=5767, karpov=1866, capablanca=6051, carlsen=5739, total_positions=54156, tal=4778}
02:24:21.502 CompetitiveModeActivity                                     🔬 Running comprehensive PersonalityEngine system diagnostic...
02:24:21.502 PersonalitySystemDiag                                       🔬 STARTING COMPREHENSIVE PERSONALITY ENGINE DIAGNOSTIC
02:24:21.502                                                             📋 Test Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
02:24:21.502                                                             🎭 Master: alekhine (Target ELO: 1750)
02:24:21.502                                                             🔍 Phase 1: Database Analysis
02:24:21.504                                                             📊 Total alekhine positions in DB: 16368
02:24:21.506                                                             🎯 Exact FEN matches: 50
02:24:21.506                                                             🔍 Board pattern: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
02:24:21.506                                                             📋 Sample matched games: null
02:24:21.506                                                             🤖 Phase 2: AI Integration Analysis
02:24:21.506                                                             🤖 Assistant available for alekhine: true
02:24:21.506                                                             📚 Vector store connected: false
02:24:21.506                                                             🧠 AI analysis capability: Available
02:24:21.506                                                             🎨 Phase 3: Style Application Analysis
02:24:21.506                                                             ♟️ Stockfish candidates: null
02:24:21.506                                                             🎭 Personality weight: 0.0
02:24:21.506                                                             📊 Phase 4: Quality Assessment
02:24:21.506                                                             📊 Quality assessment complete - Score: 0.29999998
02:24:21.506                                                             ✅ DIAGNOSTIC COMPLETE
02:24:21.506                                                             === PersonalityEngine System Diagnostic ===
                                                                         Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
                                                                         Master: alekhine (Target ELO: 1750)
                                                                         
                                                                         --- Database Analysis ---
                                                                         Total positions in DB: 16368
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
                                                                         Logically consistent: false
                                                                         Authenticity score: 0.30
                                                                         Issues found: Too many exact matches (suspicious); Low-ELO position matching super-GM database (illogical); 
02:24:21.506 CompetitiveModeActivity                                     📊 System diagnostic result: === PersonalityEngine System Diagnostic ===
                                                                         Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
                                                                         Master: alekhine (Target ELO: 1750)
                                                                         
                                                                         --- Database Analysis ---
                                                                         Total positions in DB: 16368
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
                                                                         Logically consistent: false
                                                                         Authenticity score: 0.30
                                                                         Issues found: Too many exact matches (suspicious); Low-ELO position matching super-GM database (illogical); 
02:24:21.506 PersonalitySystemDiag                                       🔍 QUICK NAME DIAGNOSTIC
02:24:21.510                                                             📊 Actual master names in database:
02:24:21.510                                                                anand: 1870 positions
02:24:21.510                                                                kramnik: 6707 positions
02:24:21.510                                                                kasparov: 5010 positions
02:24:21.510                                                                alekhine: 16368 positions
02:24:21.510                                                                fischer: 5767 positions
02:24:21.510                                                                karpov: 1866 positions
02:24:21.510                                                                capablanca: 6051 positions
02:24:21.510                                                                carlsen: 5739 positions
02:24:21.510                                                                total_positions: 54156 positions
02:24:21.510                                                                tal: 4778 positions
02:24:21.510 CompetitiveModeActivity                                     🎤 Initializing voice services...
02:24:21.510 VoiceControlManager                                         📋 Registered voice command listener: competitive_mode
02:24:21.512 CompetitiveModeActivity                                     🗣️ Updating voice for master: alekhine
02:24:21.512 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:24:21.512 CompetitiveModeActivity                                     🔍 FineTunedModelManager master: alekhine
02:24:21.512                                                             🔍 SharedPreferences master: alekhine
02:24:21.512 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:24:21.512                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:24:21.512                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:24:21.512 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
02:24:21.512 TTSServiceManager                                           ✅ Usage context set to: competitive_mode
02:24:21.512 CompetitiveModeActivity                                     ✅ Final verification - Voice system master: alekhine
02:24:21.512                                                             ✅ Voice services initializing...
02:24:21.592 WindowManager           system_server                       win=Window{538b8f4 u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity EXITING} destroySurfaces: appStopped=false cleanupOnResume=false win.mWindowRemovalAllowed=true win.mRemoveOnExit=true win.mViewVisibility=0 caller=com.android.server.wm.WindowState.onExitAnimationDone:222 com.android.server.wm.WindowState.onAnimationFinished:161 com.android.server.wm.WindowContainer$$ExternalSyntheticLambda5.onAnimationFinished:26 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda1.run:28 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda0.onAnimationFinished:65 com.android.server.wm.LocalAnimationAdapter$$ExternalSyntheticLambda0.run:10 android.os.Handler.handleCallback:959 
02:24:21.816 GameViewModel           com.example.chesspedagogue          🎭 Initializing personality LiveData...
02:24:21.816                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
02:24:21.816                                                             🔍 Requesting position evaluation...
02:24:21.817 CompetitiveModeActivity                                     🎭 Configuring GameRepository personality engine for alekhine
02:24:21.819                                                             ✅ GameRepository personality engine configured successfully!
02:24:21.819                                                             🎯 Master: alekhine, Weight: 0.3, Enabled: true
02:24:21.819                                                             🔍 Personality engine availability check: true
02:24:21.819                                                             ✅ CONFIRMED: GameRepository personality engine is properly configured and available!
02:24:21.819                                                             ✅ Game view model configured for competitive mode
02:24:21.819                                                             👀 Setting up observers...
02:24:21.820                                                             🎯 Setting up chess board interaction...
02:24:21.820                                                             ✅ Chess board interaction setup complete
02:24:21.820                                                             ✅ Observers setup complete
02:24:21.820                                                             🎮 Setting up controls...
02:24:21.822                                                             ✅ Controls setup complete
02:24:21.822                                                             ✅ All competitive mode systems initialized!
02:24:21.822                                                             🏁 Starting competitive game vs alekhine
02:24:21.822 GameViewModel                                               Starting new game
02:24:21.822 AdaptiveStrategy                                            🔍 Started monitoring conversation: competitive_1750296261822 (alekhine vs player)
02:24:21.822 CompetitiveModeActivity                                     🧠 Phase 3: Started adaptive conversation monitoring for alekhine
02:24:21.823 RelationshipPersistence                                     👥 Retrieved relationship: alekhine <-> player (respect: 0.50, rivalry: 0.00, friendship: 0.00)
02:24:21.823 EmotionalS...egyLearner                                     🧠 Started learning session: alekhine vs player
02:24:21.824                                                             🔍 EXPLORATION: alekhine trying supportive approach vs player
02:24:21.824 CrossMaste...ectiveness                                     🎓 Generated 0 cross-learning recommendations for alekhine vs player
02:24:21.825 AdaptiveStrategy                                            🎯 Optimal strategy for alekhine vs player: 'supportive' (confidence: 0.20)
02:24:21.825 CompetitiveModeActivity                                     🧠 Phase 3: Using adaptive strategy 'supportive' (confidence: 0.20) for greeting - Individual learning: EXPLORATION: Testing new approach 'supportive' to learn effectiveness; 
02:24:21.826                                                             🗣️ Speaking master dialogue: Alexander Alekhine at your service. Prepare for a combinatorial masterclass!
02:24:21.826 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:24:21.826                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:24:21.826                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:24:21.826                                                             ✅ Usage context set to: competitive_mode
02:24:21.826 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:24:21.826 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:24:21.826                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:24:21.828 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:24:21.829                                                             🎭 Updated emotion indicator: 😎
02:24:21.829                                                             ✅ Competitive game started successfully!
02:24:21.831                                                             🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
02:24:21.831                                                             📊 Evaluation updated: 0.0
02:24:21.831                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
02:24:21.831                                                             🎯 First emotional evaluation: 0.0 (threshold: 1.5)
02:24:21.831                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296261s/25s)
02:24:21.831                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:24:21.832                                                             📜 Move history updated: 0 moves
02:24:21.834 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
02:24:21.840 Choreographer                                               Skipped 46 frames!  The application may be doing too much work on its main thread.
02:24:21.860 BufferQueueProducer                                         [](id:418d00000003,api:0,p:0,c:16781) setDequeueTimeout:2077252342
02:24:21.860 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
02:24:21.860                                                             Access denied finding property "vendor.gpp.create_frc_extension"
02:24:21.865 ScrollView                                                   onsize change changed 
02:24:21.898 BLASTBufferQueue                                            [VRI[CompetitiveModeActivity]@c8e4eac#3](f:0,a:0,s:0) onFrameAvailable the first frame is available
02:24:21.898 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 34436, bufferData(ID: 72073846194191, frameNumber: 1)
02:24:21.899 HWUI                                                        CFMS:: SetUp Pid : 16781    Tid : 16805
02:24:21.900 CompetitiveModeActivity                                     🎤 Voice service connected to competitive mode
02:24:21.900 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
02:24:21.933 InputMethodManagerUtils                                     startInputInner - Id : 0
02:24:21.933 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
02:24:21.971 CompetitiveModeActivity                                     📜 Move history updated: 0 moves
02:24:21.971                                                             🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
02:24:21.972                                                             📊 Evaluation updated: 0.0
02:24:21.972                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
02:24:21.972                                                             🎯 First emotional evaluation: 0.0 (threshold: 1.5)
02:24:21.972                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296261s/25s)
02:24:21.972                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:24:21.972 GameViewModel                                               New game initialized with evaluation tracking reset
02:24:22.412 WindowManager           system_server                       win=Window{950bbee u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:204 com.android.server.wm.ActivityClientController.activityStopped:95 android.app.IActivityClientController$Stub.onTransact:722 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1541 android.os.Binder.execTransact:1480 
02:24:22.476 GameViewModel           com.example.chesspedagogue          ✅ Evaluation received: 0.29
02:24:22.584 CompetitiveModeActivity                                     📊 Evaluation updated: 0.29
02:24:22.584                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.29, emotionalManager: INITIALIZED
02:24:22.584                                                             🎯 First emotional evaluation: 0.29 (threshold: 1.5)
02:24:22.584                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296262s/25s)
02:24:22.584                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:24:23.133 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:24:27.552 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
02:25:50.387                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=3
02:25:50.387                                                             🎯 SQUARE TAPPED: row=6, col=3
02:25:50.387                                                             📝 Player color: white
02:25:50.387                                                             🔍 Selected row/col: -1/-1
02:25:50.744                                                             🎯 Selected piece: P at 6, 3
02:25:50.954                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
02:25:50.954                                                             🎯 SQUARE TAPPED: row=4, col=3
02:25:50.954                                                             📝 Player color: white
02:25:50.954                                                             🔍 Selected row/col: 6/3
02:25:50.955                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
02:25:50.955                                                             🎯 ATTEMPTING MOVE: d2d4
02:25:50.955                                                             📝 Player color: white
02:25:50.955                                                             🔄 Is player's turn: true
02:25:50.955                                                             🔄 Is white's turn: true
02:25:50.955                                                             ✅ Turn validation passed, making move: d2d4
02:25:50.955 GameViewModel                                               🎯 makePlayerMove called with: d2d4
02:25:51.007                                                             🔍 Validating move: d2d4 (attempt 1)
02:25:51.107                                                             📋 Current position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
02:25:51.159                                                             ⚖️ Move d2d4 legality check: LEGAL
02:25:51.159                                                             ✅ Executing validated move: d2d4
02:25:51.362                                                             📍 New position after move: rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1
02:25:51.365 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1
02:25:51.367                                                             📜 Move history updated: 1 moves
02:25:51.367 GameHistoryManager                                          Move added: d2d4
02:25:51.369 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
02:25:51.463 GameViewModel                                               🔍 Requesting position evaluation...
02:25:51.464                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:25:51.464                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:25:51.464                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:25:51.464                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:25:51.464                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:25:51.464                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:25:51.464                                                             🔧 gameRepository class: GameRepository
02:25:51.464                                                             🔧 Current thread: main
02:25:51.465 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:25:51.966 GameViewModel                                               ✅ Evaluation received: -0.27
02:25:52.073 CompetitiveModeActivity                                     📊 Evaluation updated: -0.27
02:25:52.073                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.27, emotionalManager: INITIALIZED
02:25:52.073                                                             🎯 First emotional evaluation: -0.27 (threshold: 1.5)
02:25:52.073                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296352s/25s)
02:25:52.073                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:25:52.838 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:25:52.857 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7e6
02:25:53.217 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppp1ppp/4p3/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 2
02:25:53.220                                                             📜 Move history updated: 2 moves
02:25:53.220 GameHistoryManager                                          Move added: e7e6
02:25:53.220 GameViewModel                                               🔍 Requesting position evaluation...
02:25:53.221                                                             🎭 Updating personality context for move: e7e6
02:25:53.221                                                             ✨ Personality context updated for move e7e6 - This is revolutionary!
02:25:53.222                                                             🔍 Checking game end conditions...
02:25:53.324                                                             ✅ Game continues - no end condition detected
02:25:53.677                                                             ✅ Evaluation received: 0.35
02:25:53.779 CompetitiveModeActivity                                     📊 Evaluation updated: 0.35
02:25:53.779                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.35, emotionalManager: INITIALIZED
02:25:53.779                                                             🎯 First emotional evaluation: 0.35 (threshold: 1.5)
02:25:53.779                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296353s/25s)
02:25:53.779                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:26:03.908                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=2
02:26:03.908                                                             🎯 SQUARE TAPPED: row=6, col=2
02:26:03.908                                                             📝 Player color: white
02:26:03.908                                                             🔍 Selected row/col: -1/-1
02:26:04.266                                                             🎯 Selected piece: P at 6, 2
02:26:04.430                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
02:26:04.430                                                             🎯 SQUARE TAPPED: row=4, col=2
02:26:04.430                                                             📝 Player color: white
02:26:04.430                                                             🔍 Selected row/col: 6/2
02:26:04.430                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
02:26:04.431                                                             🎯 ATTEMPTING MOVE: c2c4
02:26:04.431                                                             📝 Player color: white
02:26:04.431                                                             🔄 Is player's turn: true
02:26:04.431                                                             🔄 Is white's turn: true
02:26:04.431                                                             ✅ Turn validation passed, making move: c2c4
02:26:04.431 GameViewModel                                               🎯 makePlayerMove called with: c2c4
02:26:04.481                                                             🔍 Validating move: c2c4 (attempt 1)
02:26:04.582                                                             📋 Current position: rnbqkbnr/pppp1ppp/4p3/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 2
02:26:04.633                                                             ⚖️ Move c2c4 legality check: LEGAL
02:26:04.633                                                             ✅ Executing validated move: c2c4
02:26:04.835                                                             📍 New position after move: rnbqkbnr/pppp1ppp/4p3/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2
02:26:04.838 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppp1ppp/4p3/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2
02:26:04.841                                                             📜 Move history updated: 3 moves
02:26:04.841 GameHistoryManager                                          Move added: c2c4
02:26:04.842 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
02:26:04.936 GameViewModel                                               🔍 Requesting position evaluation...
02:26:04.936                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:26:04.936                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:26:04.936                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:26:04.937                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:26:04.937                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:26:04.937                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:26:04.937                                                             🔧 gameRepository class: GameRepository
02:26:04.937                                                             🔧 Current thread: main
02:26:04.937 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:26:05.591 GameViewModel                                               ✅ Evaluation received: -0.27
02:26:05.700 CompetitiveModeActivity                                     📊 Evaluation updated: -0.27
02:26:05.700                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.27, emotionalManager: INITIALIZED
02:26:05.700                                                             🎯 First emotional evaluation: -0.27 (threshold: 1.5)
02:26:05.700                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296365s/25s)
02:26:05.700                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:26:06.362 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 4 historical positions
02:26:06.377 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a7a6
02:26:06.737 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/1ppp1ppp/p3p3/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3
02:26:06.741                                                             📜 Move history updated: 4 moves
02:26:06.741 GameHistoryManager                                          Move added: a7a6
02:26:06.741 GameViewModel                                               🔍 Requesting position evaluation...
02:26:06.742                                                             🎭 Updating personality context for move: a7a6
02:26:06.742                                                             ✨ Personality context updated for move a7a6 - This is revolutionary!
02:26:06.742                                                             🔍 Checking game end conditions...
02:26:06.844                                                             ✅ Game continues - no end condition detected
02:26:07.351                                                             ✅ Evaluation received: 0.52
02:26:07.454 CompetitiveModeActivity                                     📊 Evaluation updated: 0.52
02:26:07.454                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.52, emotionalManager: INITIALIZED
02:26:07.454                                                             🎯 First emotional evaluation: 0.52 (threshold: 1.5)
02:26:07.454                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296367s/25s)
02:26:07.454                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:26:13.399                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
02:26:13.399                                                             🎯 SQUARE TAPPED: row=6, col=4
02:26:13.399                                                             📝 Player color: white
02:26:13.399                                                             🔍 Selected row/col: -1/-1
02:26:13.756                                                             🎯 Selected piece: P at 6, 4
02:26:13.990                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=4
02:26:13.990                                                             🎯 SQUARE TAPPED: row=4, col=4
02:26:13.990                                                             📝 Player color: white
02:26:13.990                                                             🔍 Selected row/col: 6/4
02:26:13.991                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
02:26:13.991                                                             🎯 ATTEMPTING MOVE: e2e4
02:26:13.991                                                             📝 Player color: white
02:26:13.991                                                             🔄 Is player's turn: true
02:26:13.991                                                             🔄 Is white's turn: true
02:26:13.991                                                             ✅ Turn validation passed, making move: e2e4
02:26:13.991 GameViewModel                                               🎯 makePlayerMove called with: e2e4
02:26:14.042                                                             🔍 Validating move: e2e4 (attempt 1)
02:26:14.142                                                             📋 Current position: rnbqkbnr/1ppp1ppp/p3p3/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3
02:26:14.193                                                             ⚖️ Move e2e4 legality check: LEGAL
02:26:14.193                                                             ✅ Executing validated move: e2e4
02:26:14.396                                                             📍 New position after move: rnbqkbnr/1ppp1ppp/p3p3/8/2PPP3/8/PP3PPP/RNBQKBNR b KQkq - 0 3
02:26:14.404 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/1ppp1ppp/p3p3/8/2PPP3/8/PP3PPP/RNBQKBNR b KQkq - 0 3
02:26:14.406                                                             📜 Move history updated: 5 moves
02:26:14.406 GameHistoryManager                                          Move added: e2e4
02:26:14.496 GameViewModel                                               🔍 Requesting position evaluation...
02:26:14.496                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:26:14.496                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:26:14.497                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:26:14.497                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:26:14.497                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:26:14.497                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:26:14.497                                                             🔧 gameRepository class: GameRepository
02:26:14.497                                                             🔧 Current thread: main
02:26:14.497 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:26:15.205 GameViewModel                                               ✅ Evaluation received: -0.30
02:26:15.305 CompetitiveModeActivity                                     📊 Evaluation updated: -0.3
02:26:15.305                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.3, emotionalManager: INITIALIZED
02:26:15.305                                                             🎯 First emotional evaluation: -0.3 (threshold: 1.5)
02:26:15.305                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296375s/25s)
02:26:15.305                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:26:16.109 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:26:16.138 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7d6
02:26:16.501 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/1pp2ppp/p2pp3/8/2PPP3/8/PP3PPP/RNBQKBNR w KQkq - 0 4
02:26:16.505                                                             📜 Move history updated: 6 moves
02:26:16.505 GameHistoryManager                                          Move added: d7d6
02:26:16.505 GameViewModel                                               🔍 Requesting position evaluation...
02:26:16.506                                                             🎭 Updating personality context for move: d7d6
02:26:16.506                                                             ✨ Personality context updated for move d7d6 - This is revolutionary!
02:26:16.506                                                             🔍 Checking game end conditions...
02:26:16.608                                                             ✅ Game continues - no end condition detected
02:26:17.215                                                             ✅ Evaluation received: 0.89
02:26:17.317 CompetitiveModeActivity                                     📊 Evaluation updated: 0.89
02:26:17.317                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.89, emotionalManager: INITIALIZED
02:26:17.317                                                             🎯 First emotional evaluation: 0.89 (threshold: 1.5)
02:26:17.317                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296377s/25s)
02:26:17.317                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:26:27.452                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=1
02:26:27.452                                                             🎯 SQUARE TAPPED: row=7, col=1
02:26:27.452                                                             📝 Player color: white
02:26:27.452                                                             🔍 Selected row/col: -1/-1
02:26:27.808                                                             🎯 Selected piece: N at 7, 1
02:26:28.060                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
02:26:28.060                                                             🎯 SQUARE TAPPED: row=5, col=2
02:26:28.060                                                             📝 Player color: white
02:26:28.061                                                             🔍 Selected row/col: 7/1
02:26:28.061                                                             🎯 ATTEMPTING MOVE: b1c3
02:26:28.061                                                             📝 Player color: white
02:26:28.061                                                             🔄 Is player's turn: true
02:26:28.061                                                             🔄 Is white's turn: true
02:26:28.061                                                             ✅ Turn validation passed, making move: b1c3
02:26:28.061 GameViewModel                                               🎯 makePlayerMove called with: b1c3
02:26:28.112                                                             🔍 Validating move: b1c3 (attempt 1)
02:26:28.213                                                             📋 Current position: rnbqkbnr/1pp2ppp/p2pp3/8/2PPP3/8/PP3PPP/RNBQKBNR w KQkq - 0 4
02:26:28.265                                                             ⚖️ Move b1c3 legality check: LEGAL
02:26:28.265                                                             ✅ Executing validated move: b1c3
02:26:28.468                                                             📍 New position after move: rnbqkbnr/1pp2ppp/p2pp3/8/2PPP3/2N5/PP3PPP/R1BQKBNR b KQkq - 1 4
02:26:28.472 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/1pp2ppp/p2pp3/8/2PPP3/2N5/PP3PPP/R1BQKBNR b KQkq - 1 4
02:26:28.475                                                             📜 Move history updated: 7 moves
02:26:28.475 GameHistoryManager                                          Move added: b1c3
02:26:28.569 GameViewModel                                               🔍 Requesting position evaluation...
02:26:28.569                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:26:28.569                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:26:28.569                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:26:28.570                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:26:28.570                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:26:28.570                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:26:28.570                                                             🔧 gameRepository class: GameRepository
02:26:28.570                                                             🔧 Current thread: main
02:26:28.570 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:26:29.275 GameViewModel                                               ✅ Evaluation received: -0.99
02:26:29.376 CompetitiveModeActivity                                     📊 Evaluation updated: -0.99
02:26:29.376                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.99, emotionalManager: INITIALIZED
02:26:29.376                                                             🎯 First emotional evaluation: -0.99 (threshold: 1.5)
02:26:29.376                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296389s/25s)
02:26:29.376                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:26:30.235 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:26:30.264 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e6e5
02:26:30.624 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/1pp2ppp/p2p4/4p3/2PPP3/2N5/PP3PPP/R1BQKBNR w KQkq - 0 5
02:26:30.629                                                             📜 Move history updated: 8 moves
02:26:30.630 GameHistoryManager                                          Move added: e6e5
02:26:30.630 GameViewModel                                               🔍 Requesting position evaluation...
02:26:30.630                                                             🎭 Updating personality context for move: e6e5
02:26:30.630                                                             ✨ Personality context updated for move e6e5 - This is revolutionary!
02:26:30.630                                                             🔍 Checking game end conditions...
02:26:30.732                                                             ✅ Game continues - no end condition detected
02:26:31.238                                                             ✅ Evaluation received: 1.13
02:26:31.341 CompetitiveModeActivity                                     📊 Evaluation updated: 1.13
02:26:31.341                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.13, emotionalManager: INITIALIZED
02:26:31.341                                                             🎯 First emotional evaluation: 1.13 (threshold: 1.5)
02:26:31.341                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296391s/25s)
02:26:31.341                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:26:36.890                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
02:26:36.890                                                             🎯 SQUARE TAPPED: row=4, col=3
02:26:36.890                                                             📝 Player color: white
02:26:36.890                                                             🔍 Selected row/col: -1/-1
02:26:37.244                                                             🎯 Selected piece: P at 4, 3
02:26:37.496                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
02:26:37.496                                                             🎯 SQUARE TAPPED: row=3, col=4
02:26:37.496                                                             📝 Player color: white
02:26:37.496                                                             🔍 Selected row/col: 4/3
02:26:37.496                                                             ♟️ Promotion check: piece=P, fromRow=4, toRow=3, reaches=false
02:26:37.496                                                             🎯 ATTEMPTING MOVE: d4e5
02:26:37.496                                                             📝 Player color: white
02:26:37.496                                                             🔄 Is player's turn: true
02:26:37.496                                                             🔄 Is white's turn: true
02:26:37.496                                                             ✅ Turn validation passed, making move: d4e5
02:26:37.496 GameViewModel                                               🎯 makePlayerMove called with: d4e5
02:26:37.547                                                             🔍 Validating move: d4e5 (attempt 1)
02:26:37.648                                                             📋 Current position: rnbqkbnr/1pp2ppp/p2p4/4p3/2PPP3/2N5/PP3PPP/R1BQKBNR w KQkq - 0 5
02:26:37.700                                                             ⚖️ Move d4e5 legality check: LEGAL
02:26:37.700                                                             ✅ Executing validated move: d4e5
02:26:37.902                                                             📍 New position after move: rnbqkbnr/1pp2ppp/p2p4/4P3/2P1P3/2N5/PP3PPP/R1BQKBNR b KQkq - 0 5
02:26:37.905 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/1pp2ppp/p2p4/4P3/2P1P3/2N5/PP3PPP/R1BQKBNR b KQkq - 0 5
02:26:37.908                                                             📜 Move history updated: 9 moves
02:26:37.908 GameHistoryManager                                          Move added: d4e5
02:26:38.004 GameViewModel                                               🔍 Requesting position evaluation...
02:26:38.005                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:26:38.005                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:26:38.005                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:26:38.005                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:26:38.005                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:26:38.005                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:26:38.005                                                             🔧 gameRepository class: GameRepository
02:26:38.005                                                             🔧 Current thread: main
02:26:38.005 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:26:38.712 GameViewModel                                               ✅ Evaluation received: -0.66
02:26:38.812 CompetitiveModeActivity                                     📊 Evaluation updated: -0.66
02:26:38.812                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.66, emotionalManager: INITIALIZED
02:26:38.812                                                             🎯 First emotional evaluation: -0.66 (threshold: 1.5)
02:26:38.812                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750296398s/25s)
02:26:38.812                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:26:39.668 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:26:39.697 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c8e6
02:26:40.057 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkbnr/1pp2ppp/p2pb3/4P3/2P1P3/2N5/PP3PPP/R1BQKBNR w KQkq - 1 6
02:26:40.063                                                             📜 Move history updated: 10 moves
02:26:40.063 GameHistoryManager                                          Move added: c8e6
02:26:40.063 GameViewModel                                               🔍 Requesting position evaluation...
02:26:40.063                                                             🎭 Updating personality context for move: c8e6
02:26:40.064                                                             ✨ Personality context updated for move c8e6 - This is revolutionary!
02:26:40.064                                                             🔍 Checking game end conditions...
02:26:40.166                                                             ✅ Game continues - no end condition detected
02:26:40.470                                                             ✅ Evaluation received: 1.90
02:26:40.573 CompetitiveModeActivity                                     📊 Evaluation updated: 1.9
02:26:40.573                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.9, emotionalManager: INITIALIZED
02:26:40.573                                                             🎯 First emotional evaluation: 1.9 (threshold: 1.5)
02:26:40.573                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 1750296400s/25s)
02:26:40.573                                                             ✅ Emotional reaction triggered for evaluation: 1.9 (trigger: slight_disadvantage)
02:26:40.576 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:26:40.576                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:26:40.576                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:26:40.576                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
02:26:41.669                                                             ✅ Response generated: A small deficit does not deter me; it only sharpens my appetite for the beautiful and unexpected. Pr...
02:26:41.669 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: A small deficit does not deter me; it only sharpens my appetite for the beautiful and unexpected. Prepare for a storm of combinations—my harmony of pieces will find its voice.
02:26:41.672                                                             🎭 Updated emotion indicator: 🤔
02:26:41.673                                                             🗣️ Speaking master dialogue: A small deficit does not deter me; it only sharpens my appetite for the beautiful and unexpected. Prepare for a storm of combinations—my harmony of pieces will find its voice.
02:26:41.673 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:26:41.673                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:26:41.673                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:26:41.673                                                             ✅ Usage context set to: competitive_mode
02:26:41.673 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:26:41.673 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:26:41.673                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:26:41.675 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:26:49.059                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=6
02:26:49.059                                                             🎯 SQUARE TAPPED: row=7, col=6
02:26:49.059                                                             📝 Player color: white
02:26:49.059                                                             🔍 Selected row/col: -1/-1
02:26:49.416                                                             🎯 Selected piece: N at 7, 6
02:26:49.705                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
02:26:49.705                                                             🎯 SQUARE TAPPED: row=5, col=5
02:26:49.705                                                             📝 Player color: white
02:26:49.705                                                             🔍 Selected row/col: 7/6
02:26:49.705                                                             🎯 ATTEMPTING MOVE: g1f3
02:26:49.705                                                             📝 Player color: white
02:26:49.705                                                             🔄 Is player's turn: true
02:26:49.705                                                             🔄 Is white's turn: true
02:26:49.705                                                             ✅ Turn validation passed, making move: g1f3
02:26:49.705 GameViewModel                                               🎯 makePlayerMove called with: g1f3
02:26:49.756                                                             🔍 Validating move: g1f3 (attempt 1)
02:26:49.857                                                             📋 Current position: rn1qkbnr/1pp2ppp/p2pb3/4P3/2P1P3/2N5/PP3PPP/R1BQKBNR w KQkq - 1 6
02:26:49.908                                                             ⚖️ Move g1f3 legality check: LEGAL
02:26:49.908                                                             ✅ Executing validated move: g1f3
02:26:50.111                                                             📍 New position after move: rn1qkbnr/1pp2ppp/p2pb3/4P3/2P1P3/2N2N2/PP3PPP/R1BQKB1R b KQkq - 2 6
02:26:50.112 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkbnr/1pp2ppp/p2pb3/4P3/2P1P3/2N2N2/PP3PPP/R1BQKB1R b KQkq - 2 6
02:26:50.114                                                             📜 Move history updated: 11 moves
02:26:50.114 GameHistoryManager                                          Move added: g1f3
02:26:50.215 GameViewModel                                               🔍 Requesting position evaluation...
02:26:50.215                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:26:50.215                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:26:50.215                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:26:50.215                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:26:50.215                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:26:50.215                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:26:50.215                                                             🔧 gameRepository class: GameRepository
02:26:50.215                                                             🔧 Current thread: main
02:26:50.216 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:26:50.923 GameViewModel                                               ✅ Evaluation received: -1.97
02:26:51.031 CompetitiveModeActivity                                     📊 Evaluation updated: -1.97
02:26:51.031                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.97, emotionalManager: INITIALIZED
02:26:51.031                                                             🎯 Evaluation change: 3.87 (threshold: 0.8)
02:26:51.031                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 10s/25s)
02:26:51.031                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:26:51.770 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:26:51.800 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h7h6
02:26:52.159 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkbnr/1pp2pp1/p2pb2p/4P3/2P1P3/2N2N2/PP3PPP/R1BQKB1R w KQkq - 0 7
02:26:52.164                                                             📜 Move history updated: 12 moves
02:26:52.164 GameHistoryManager                                          Move added: h7h6
02:26:52.164 GameViewModel                                               🔍 Requesting position evaluation...
02:26:52.164                                                             🎭 Updating personality context for move: h7h6
02:26:52.164                                                             ✨ Personality context updated for move h7h6 - This is revolutionary!
02:26:52.165                                                             🔍 Checking game end conditions...
02:26:52.267                                                             ✅ Game continues - no end condition detected
02:26:52.624                                                             ✅ Evaluation received: 2.17
02:26:52.727 CompetitiveModeActivity                                     📊 Evaluation updated: 2.17
02:26:52.727                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.17, emotionalManager: INITIALIZED
02:26:52.727                                                             🎯 Evaluation change: 0.2700001 (threshold: 0.8)
02:26:52.727                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 12s/25s)
02:26:52.727                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:26:53.380                                                             🗣️ Master dialogue speech completed
02:26:59.088                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
02:26:59.088                                                             🎯 SQUARE TAPPED: row=3, col=4
02:26:59.088                                                             📝 Player color: white
02:26:59.088                                                             🔍 Selected row/col: -1/-1
02:26:59.445                                                             🎯 Selected piece: P at 3, 4
02:26:59.715                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=3
02:26:59.715                                                             🎯 SQUARE TAPPED: row=2, col=3
02:26:59.715                                                             📝 Player color: white
02:26:59.715                                                             🔍 Selected row/col: 3/4
02:26:59.715                                                             ♟️ Promotion check: piece=P, fromRow=3, toRow=2, reaches=false
02:26:59.715                                                             🎯 ATTEMPTING MOVE: e5d6
02:26:59.715                                                             📝 Player color: white
02:26:59.715                                                             🔄 Is player's turn: true
02:26:59.715                                                             🔄 Is white's turn: true
02:26:59.715                                                             ✅ Turn validation passed, making move: e5d6
02:26:59.715 GameViewModel                                               🎯 makePlayerMove called with: e5d6
02:26:59.766                                                             🔍 Validating move: e5d6 (attempt 1)
02:26:59.868                                                             📋 Current position: rn1qkbnr/1pp2pp1/p2pb2p/4P3/2P1P3/2N2N2/PP3PPP/R1BQKB1R w KQkq - 0 7
02:26:59.919                                                             ⚖️ Move e5d6 legality check: LEGAL
02:26:59.919                                                             ✅ Executing validated move: e5d6
02:27:00.122                                                             📍 New position after move: rn1qkbnr/1pp2pp1/p2Pb2p/8/2P1P3/2N2N2/PP3PPP/R1BQKB1R b KQkq - 0 7
02:27:00.131 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkbnr/1pp2pp1/p2Pb2p/8/2P1P3/2N2N2/PP3PPP/R1BQKB1R b KQkq - 0 7
02:27:00.134                                                             📜 Move history updated: 13 moves
02:27:00.134 GameHistoryManager                                          Move added: e5d6
02:27:00.223 GameViewModel                                               🔍 Requesting position evaluation...
02:27:00.223                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:27:00.223                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:27:00.224                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:27:00.224                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:27:00.224                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:27:00.224                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:27:00.224                                                             🔧 gameRepository class: GameRepository
02:27:00.224                                                             🔧 Current thread: main
02:27:00.224 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:27:00.677 GameViewModel                                               ✅ Evaluation received: -2.04
02:27:00.778 CompetitiveModeActivity                                     📊 Evaluation updated: -2.04
02:27:00.778                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.04, emotionalManager: INITIALIZED
02:27:00.778                                                             🎯 Evaluation change: 3.94 (threshold: 0.8)
02:27:00.778                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 20s/25s)
02:27:00.778                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:27:01.634 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:27:01.663 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8d6
02:27:02.024 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qk1nr/1pp2pp1/p2bb2p/8/2P1P3/2N2N2/PP3PPP/R1BQKB1R w KQkq - 0 8
02:27:02.032                                                             📜 Move history updated: 14 moves
02:27:02.032 GameHistoryManager                                          Move added: f8d6
02:27:02.032 GameViewModel                                               🔍 Requesting position evaluation...
02:27:02.032                                                             🎭 Updating personality context for move: f8d6
02:27:02.033                                                             ✨ Personality context updated for move f8d6 - This is revolutionary!
02:27:02.033                                                             🔍 Checking game end conditions...
02:27:02.135                                                             ✅ Game continues - no end condition detected
02:27:02.489                                                             ✅ Evaluation received: 2.20
02:27:02.592 CompetitiveModeActivity                                     📊 Evaluation updated: 2.2
02:27:02.593                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.2, emotionalManager: INITIALIZED
02:27:02.593                                                             🎯 Evaluation change: 0.30000007 (threshold: 0.8)
02:27:02.593                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 22s/25s)
02:27:02.593                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:27:08.542                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=2
02:27:08.542                                                             🎯 SQUARE TAPPED: row=7, col=2
02:27:08.542                                                             📝 Player color: white
02:27:08.542                                                             🔍 Selected row/col: -1/-1
02:27:08.901                                                             🎯 Selected piece: B at 7, 2
02:27:09.160                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=4
02:27:09.160                                                             🎯 SQUARE TAPPED: row=5, col=4
02:27:09.160                                                             📝 Player color: white
02:27:09.160                                                             🔍 Selected row/col: 7/2
02:27:09.160                                                             🎯 ATTEMPTING MOVE: c1e3
02:27:09.160                                                             📝 Player color: white
02:27:09.160                                                             🔄 Is player's turn: true
02:27:09.160                                                             🔄 Is white's turn: true
02:27:09.160                                                             ✅ Turn validation passed, making move: c1e3
02:27:09.160 GameViewModel                                               🎯 makePlayerMove called with: c1e3
02:27:09.211                                                             🔍 Validating move: c1e3 (attempt 1)
02:27:09.312                                                             📋 Current position: rn1qk1nr/1pp2pp1/p2bb2p/8/2P1P3/2N2N2/PP3PPP/R1BQKB1R w KQkq - 0 8
02:27:09.364                                                             ⚖️ Move c1e3 legality check: LEGAL
02:27:09.364                                                             ✅ Executing validated move: c1e3
02:27:09.566                                                             📍 New position after move: rn1qk1nr/1pp2pp1/p2bb2p/8/2P1P3/2N1BN2/PP3PPP/R2QKB1R b KQkq - 1 8
02:27:09.569 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qk1nr/1pp2pp1/p2bb2p/8/2P1P3/2N1BN2/PP3PPP/R2QKB1R b KQkq - 1 8
02:27:09.574                                                             📜 Move history updated: 15 moves
02:27:09.574 GameHistoryManager                                          Move added: c1e3
02:27:09.667 GameViewModel                                               🔍 Requesting position evaluation...
02:27:09.668                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:27:09.668                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:27:09.668                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:27:09.668                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:27:09.668                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:27:09.668                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:27:09.668                                                             🔧 gameRepository class: GameRepository
02:27:09.668                                                             🔧 Current thread: main
02:27:09.668 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:27:10.170 GameViewModel                                               ✅ Evaluation received: -2.13
02:27:10.276 CompetitiveModeActivity                                     📊 Evaluation updated: -2.13
02:27:10.276                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.13, emotionalManager: INITIALIZED
02:27:10.276                                                             🎯 Evaluation change: 4.03 (threshold: 0.8)
02:27:10.276                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 29s/25s)
02:27:10.276                                                             ✅ Emotional reaction triggered for evaluation: -2.13 (trigger: slight_advantage)
02:27:10.276 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:27:10.276                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:27:10.276                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:27:10.276                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
02:27:11.071 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:27:11.098 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8d7
02:27:11.459 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2qk1nr/1ppn1pp1/p2bb2p/8/2P1P3/2N1BN2/PP3PPP/R2QKB1R w KQkq - 2 9
02:27:11.465                                                             📜 Move history updated: 16 moves
02:27:11.465 GameHistoryManager                                          Move added: b8d7
02:27:11.465 GameViewModel                                               🔍 Requesting position evaluation...
02:27:11.465                                                             🎭 Updating personality context for move: b8d7
02:27:11.466                                                             ✨ Personality context updated for move b8d7 - This is revolutionary!
02:27:11.466                                                             🔍 Checking game end conditions...
02:27:11.568                                                             ✅ Game continues - no end condition detected
02:27:11.645 OpenAIService                                               ✅ Response generated: The initiative is mine, but the fight has just begun. I will seek out beauty in the complications an...
02:27:11.645 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The initiative is mine, but the fight has just begun. I will seek out beauty in the complications and press my advantage with both logic and imagination.
02:27:11.648                                                             🎭 Updated emotion indicator: 🙂
02:27:11.648                                                             🗣️ Speaking master dialogue: The initiative is mine, but the fight has just begun. I will seek out beauty in the complications and press my advantage with both logic and imagination.
02:27:11.648 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:27:11.648                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:27:11.648                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:27:11.648                                                             ✅ Usage context set to: competitive_mode
02:27:11.648 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:27:11.648 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:27:11.648                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:27:11.650 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:27:11.920 GameViewModel                                               ✅ Evaluation received: 2.21
02:27:12.022 CompetitiveModeActivity                                     📊 Evaluation updated: 2.21
02:27:12.022                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.21, emotionalManager: INITIALIZED
02:27:12.022                                                             🎯 Evaluation change: 4.34 (threshold: 0.8)
02:27:12.022                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
02:27:12.022                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:27:12.278 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
02:27:21.458 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
02:27:21.458                                                             🎯 SQUARE TAPPED: row=7, col=3
02:27:21.458                                                             📝 Player color: white
02:27:21.458                                                             🔍 Selected row/col: -1/-1
02:27:21.816                                                             🎯 Selected piece: Q at 7, 3
02:27:21.826                                                             🗣️ Master dialogue speech completed
02:27:22.074                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=0
02:27:22.074                                                             🎯 SQUARE TAPPED: row=4, col=0
02:27:22.074                                                             📝 Player color: white
02:27:22.074                                                             🔍 Selected row/col: 7/3
02:27:22.074                                                             🎯 ATTEMPTING MOVE: d1a4
02:27:22.074                                                             📝 Player color: white
02:27:22.074                                                             🔄 Is player's turn: true
02:27:22.074                                                             🔄 Is white's turn: true
02:27:22.074                                                             ✅ Turn validation passed, making move: d1a4
02:27:22.074 GameViewModel                                               🎯 makePlayerMove called with: d1a4
02:27:22.125                                                             🔍 Validating move: d1a4 (attempt 1)
02:27:22.225                                                             📋 Current position: r2qk1nr/1ppn1pp1/p2bb2p/8/2P1P3/2N1BN2/PP3PPP/R2QKB1R w KQkq - 2 9
02:27:22.276                                                             ⚖️ Move d1a4 legality check: LEGAL
02:27:22.276                                                             ✅ Executing validated move: d1a4
02:27:22.479                                                             📍 New position after move: r2qk1nr/1ppn1pp1/p2bb2p/8/Q1P1P3/2N1BN2/PP3PPP/R3KB1R b KQkq - 3 9
02:27:22.481 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2qk1nr/1ppn1pp1/p2bb2p/8/Q1P1P3/2N1BN2/PP3PPP/R3KB1R b KQkq - 3 9
02:27:22.485                                                             📜 Move history updated: 17 moves
02:27:22.485 GameHistoryManager                                          Move added: d1a4
02:27:22.582 GameViewModel                                               🔍 Requesting position evaluation...
02:27:22.583                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:27:22.583                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:27:22.583                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:27:22.583                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:27:22.583                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:27:22.583                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:27:22.583                                                             🔧 gameRepository class: GameRepository
02:27:22.583                                                             🔧 Current thread: main
02:27:22.583 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:27:23.290 GameViewModel                                               ✅ Evaluation received: -1.78
02:27:23.390 CompetitiveModeActivity                                     📊 Evaluation updated: -1.78
02:27:23.390                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.78, emotionalManager: INITIALIZED
02:27:23.390                                                             🎯 Evaluation change: 0.35000014 (threshold: 0.8)
02:27:23.390                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 13s/25s)
02:27:23.390                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:27:24.194 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:27:24.222 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c7c6
02:27:24.582 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2qk1nr/1p1n1pp1/p1pbb2p/8/Q1P1P3/2N1BN2/PP3PPP/R3KB1R w KQkq - 0 10
02:27:24.588                                                             📜 Move history updated: 18 moves
02:27:24.588 GameHistoryManager                                          Move added: c7c6
02:27:24.588 GameViewModel                                               🔍 Requesting position evaluation...
02:27:24.588                                                             🎭 Updating personality context for move: c7c6
02:27:24.589                                                             ✨ Personality context updated for move c7c6 - This is revolutionary!
02:27:24.589                                                             🔍 Checking game end conditions...
02:27:24.691                                                             ✅ Game continues - no end condition detected
02:27:25.097                                                             ✅ Evaluation received: 1.61
02:27:25.201 CompetitiveModeActivity                                     📊 Evaluation updated: 1.61
02:27:25.201                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.61, emotionalManager: INITIALIZED
02:27:25.201                                                             🎯 Evaluation change: 3.7400002 (threshold: 0.8)
02:27:25.201                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 14s/25s)
02:27:25.201                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:27:32.473                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=0
02:27:32.473                                                             🎯 SQUARE TAPPED: row=7, col=0
02:27:32.473                                                             📝 Player color: white
02:27:32.473                                                             🔍 Selected row/col: -1/-1
02:27:32.829                                                             🎯 Selected piece: R at 7, 0
02:27:33.201                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
02:27:33.201                                                             🎯 SQUARE TAPPED: row=7, col=3
02:27:33.201                                                             📝 Player color: white
02:27:33.201                                                             🔍 Selected row/col: 7/0
02:27:33.202                                                             🎯 ATTEMPTING MOVE: a1d1
02:27:33.202                                                             📝 Player color: white
02:27:33.202                                                             🔄 Is player's turn: true
02:27:33.202                                                             🔄 Is white's turn: true
02:27:33.202                                                             ✅ Turn validation passed, making move: a1d1
02:27:33.202 GameViewModel                                               🎯 makePlayerMove called with: a1d1
02:27:33.253                                                             🔍 Validating move: a1d1 (attempt 1)
02:27:33.354                                                             📋 Current position: r2qk1nr/1p1n1pp1/p1pbb2p/8/Q1P1P3/2N1BN2/PP3PPP/R3KB1R w KQkq - 0 10
02:27:33.405                                                             ⚖️ Move a1d1 legality check: LEGAL
02:27:33.405                                                             ✅ Executing validated move: a1d1
02:27:33.608                                                             📍 New position after move: r2qk1nr/1p1n1pp1/p1pbb2p/8/Q1P1P3/2N1BN2/PP3PPP/3RKB1R b Kkq - 1 10
02:27:33.611 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2qk1nr/1p1n1pp1/p1pbb2p/8/Q1P1P3/2N1BN2/PP3PPP/3RKB1R b Kkq - 1 10
02:27:33.617                                                             📜 Move history updated: 19 moves
02:27:33.617 GameHistoryManager                                          Move added: a1d1
02:27:33.714 GameViewModel                                               🔍 Requesting position evaluation...
02:27:33.714                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:27:33.714                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:27:33.714                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:27:33.714                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:27:33.714                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:27:33.714                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:27:33.714                                                             🔧 gameRepository class: GameRepository
02:27:33.714                                                             🔧 Current thread: main
02:27:33.714 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:27:34.422 GameViewModel                                               ✅ Evaluation received: -1.45
02:27:34.530 CompetitiveModeActivity                                     📊 Evaluation updated: -1.45
02:27:34.530                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.45, emotionalManager: INITIALIZED
02:27:34.530                                                             🎯 Evaluation change: 0.68000007 (threshold: 0.8)
02:27:34.530                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 24s/25s)
02:27:34.530                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:27:35.428 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
02:27:35.457 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d8e7
02:27:35.815 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k1nr/1p1nqpp1/p1pbb2p/8/Q1P1P3/2N1BN2/PP3PPP/3RKB1R w Kkq - 2 11
02:27:35.822                                                             📜 Move history updated: 20 moves
02:27:35.822 GameHistoryManager                                          Move added: d8e7
02:27:35.822 GameViewModel                                               🔍 Requesting position evaluation...
02:27:35.822                                                             🎭 Updating personality context for move: d8e7
02:27:35.822                                                             ✨ Personality context updated for move d8e7 - This is revolutionary!
02:27:35.822                                                             🔍 Checking game end conditions...
02:27:35.924                                                             ✅ Game continues - no end condition detected
02:27:36.536                                                             ✅ Evaluation received: 1.91
02:27:36.639 CompetitiveModeActivity                                     📊 Evaluation updated: 1.91
02:27:36.639                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.91, emotionalManager: INITIALIZED
02:27:36.639                                                             🎯 Evaluation change: 4.04 (threshold: 0.8)
02:27:36.639                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 26s/25s)
02:27:36.640                                                             ✅ Emotional reaction triggered for evaluation: 1.91 (trigger: slight_disadvantage)
02:27:36.640 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:27:36.640                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:27:36.640                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:27:36.641                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
02:27:37.941                                                             ✅ Response generated: Behind, but never beaten—my play will be a symphony of surprises. The true artist thrives in adversi...
02:27:37.941 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: Behind, but never beaten—my play will be a symphony of surprises. The true artist thrives in adversity, weaving beauty from the threads of struggle.
02:27:37.944                                                             🎭 Updated emotion indicator: 🤔
02:27:37.945                                                             🗣️ Speaking master dialogue: Behind, but never beaten—my play will be a symphony of surprises. The true artist thrives in adversity, weaving beauty from the threads of struggle.
02:27:37.945 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:27:37.945                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:27:37.945                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:27:37.945                                                             ✅ Usage context set to: competitive_mode
02:27:37.945 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:27:37.945 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:27:37.945                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:27:37.948 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:27:48.665                                                             🗣️ Master dialogue speech completed
02:28:03.147                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
02:28:03.147                                                             🎯 SQUARE TAPPED: row=5, col=5
02:28:03.147                                                             📝 Player color: white
02:28:03.147                                                             🔍 Selected row/col: -1/-1
02:28:03.504                                                             🎯 Selected piece: N at 5, 5
02:28:03.865                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
02:28:03.865                                                             🎯 SQUARE TAPPED: row=4, col=3
02:28:03.865                                                             📝 Player color: white
02:28:03.865                                                             🔍 Selected row/col: 5/5
02:28:03.865                                                             🎯 ATTEMPTING MOVE: f3d4
02:28:03.865                                                             📝 Player color: white
02:28:03.865                                                             🔄 Is player's turn: true
02:28:03.865                                                             🔄 Is white's turn: true
02:28:03.865                                                             ✅ Turn validation passed, making move: f3d4
02:28:03.865 GameViewModel                                               🎯 makePlayerMove called with: f3d4
02:28:03.917                                                             🔍 Validating move: f3d4 (attempt 1)
02:28:04.018                                                             📋 Current position: r3k1nr/1p1nqpp1/p1pbb2p/8/Q1P1P3/2N1BN2/PP3PPP/3RKB1R w Kkq - 2 11
02:28:04.069                                                             ⚖️ Move f3d4 legality check: LEGAL
02:28:04.069                                                             ✅ Executing validated move: f3d4
02:28:04.272                                                             📍 New position after move: r3k1nr/1p1nqpp1/p1pbb2p/8/Q1PNP3/2N1B3/PP3PPP/3RKB1R b Kkq - 3 11
02:28:04.274 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k1nr/1p1nqpp1/p1pbb2p/8/Q1PNP3/2N1B3/PP3PPP/3RKB1R b Kkq - 3 11
02:28:04.280                                                             📜 Move history updated: 21 moves
02:28:04.280 GameHistoryManager                                          Move added: f3d4
02:28:04.373 GameViewModel                                               🔍 Requesting position evaluation...
02:28:04.374                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:28:04.374                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:28:04.374                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:28:04.375                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:28:04.375                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:28:04.375                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:28:04.375                                                             🔧 gameRepository class: GameRepository
02:28:04.375                                                             🔧 Current thread: main
02:28:04.375 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:28:05.081 GameViewModel                                               ✅ Evaluation received: -1.60
02:28:05.182 CompetitiveModeActivity                                     📊 Evaluation updated: -1.6
02:28:05.182                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.6, emotionalManager: INITIALIZED
02:28:05.182                                                             🎯 Evaluation change: 3.51 (threshold: 0.8)
02:28:05.182                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 28s/25s)
02:28:05.182                                                             ✅ Emotional reaction triggered for evaluation: -1.6 (trigger: slight_advantage)
02:28:05.182 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:28:05.182                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:28:05.182                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:28:05.183                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
02:28:06.239 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:28:06.269 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d6b4
02:28:06.630 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k1nr/1p1nqpp1/p1p1b2p/8/QbPNP3/2N1B3/PP3PPP/3RKB1R w Kkq - 4 12
02:28:06.638                                                             📜 Move history updated: 22 moves
02:28:06.638 GameHistoryManager                                          Move added: d6b4
02:28:06.638 GameViewModel                                               🔍 Requesting position evaluation...
02:28:06.638                                                             🎭 Updating personality context for move: d6b4
02:28:06.639                                                             ✨ Personality context updated for move d6b4 - This is revolutionary!
02:28:06.639                                                             🔍 Checking game end conditions...
02:28:06.716 OpenAIService                                               ✅ Response generated: The initiative is mine, but the fight has just begun. I will seek out beautiful combinations and pre...
02:28:06.740 GameViewModel                                               ✅ Game continues - no end condition detected
02:28:06.744 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The initiative is mine, but the fight has just begun. I will seek out beautiful combinations and press my edge with both logic and imagination.
02:28:06.748                                                             🎭 Updated emotion indicator: 🙂
02:28:06.748                                                             🗣️ Speaking master dialogue: The initiative is mine, but the fight has just begun. I will seek out beautiful combinations and press my edge with both logic and imagination.
02:28:06.748 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:28:06.748                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:28:06.750                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:28:06.750                                                             ✅ Usage context set to: competitive_mode
02:28:06.750 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:28:06.750 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:28:06.750                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:28:06.753 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:28:07.197 GameViewModel                                               ✅ Evaluation received: 2.10
02:28:07.298 CompetitiveModeActivity                                     📊 Evaluation updated: 2.1
02:28:07.298                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.1, emotionalManager: INITIALIZED
02:28:07.298                                                             🎯 Evaluation change: 3.6999998 (threshold: 0.8)
02:28:07.298                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 2s/25s)
02:28:07.298                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:28:15.625                                                             🗣️ Master dialogue speech completed
02:28:18.178                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
02:28:18.178                                                             🎯 SQUARE TAPPED: row=4, col=3
02:28:18.178                                                             📝 Player color: white
02:28:18.178                                                             🔍 Selected row/col: -1/-1
02:28:18.539                                                             🎯 Selected piece: N at 4, 3
02:28:18.841                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
02:28:18.841                                                             🎯 SQUARE TAPPED: row=2, col=4
02:28:18.841                                                             📝 Player color: white
02:28:18.841                                                             🔍 Selected row/col: 4/3
02:28:18.841                                                             🎯 ATTEMPTING MOVE: d4e6
02:28:18.841                                                             📝 Player color: white
02:28:18.841                                                             🔄 Is player's turn: true
02:28:18.841                                                             🔄 Is white's turn: true
02:28:18.841                                                             ✅ Turn validation passed, making move: d4e6
02:28:18.841 GameViewModel                                               🎯 makePlayerMove called with: d4e6
02:28:18.892                                                             🔍 Validating move: d4e6 (attempt 1)
02:28:18.994                                                             📋 Current position: r3k1nr/1p1nqpp1/p1p1b2p/8/QbPNP3/2N1B3/PP3PPP/3RKB1R w Kkq - 4 12
02:28:19.045                                                             ⚖️ Move d4e6 legality check: LEGAL
02:28:19.045                                                             ✅ Executing validated move: d4e6
02:28:19.248                                                             📍 New position after move: r3k1nr/1p1nqpp1/p1p1N2p/8/QbP1P3/2N1B3/PP3PPP/3RKB1R b Kkq - 0 12
02:28:19.251 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k1nr/1p1nqpp1/p1p1N2p/8/QbP1P3/2N1B3/PP3PPP/3RKB1R b Kkq - 0 12
02:28:19.256                                                             📜 Move history updated: 23 moves
02:28:19.257 GameHistoryManager                                          Move added: d4e6
02:28:19.349 GameViewModel                                               🔍 Requesting position evaluation...
02:28:19.349                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:28:19.349                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:28:19.349                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:28:19.350                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:28:19.350                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:28:19.350                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:28:19.350                                                             🔧 gameRepository class: GameRepository
02:28:19.350                                                             🔧 Current thread: main
02:28:19.350 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:28:19.852 GameViewModel                                               ✅ Evaluation received: -1.80
02:28:19.957 CompetitiveModeActivity                                     📊 Evaluation updated: -1.8
02:28:19.957                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.8, emotionalManager: INITIALIZED
02:28:19.957                                                             🎯 Evaluation change: 0.19999993 (threshold: 0.8)
02:28:19.957                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 14s/25s)
02:28:19.957                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:28:20.855 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:28:20.886 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b4c3
02:28:21.246 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k1nr/1p1nqpp1/p1p1N2p/8/Q1P1P3/2b1B3/PP3PPP/3RKB1R w Kkq - 0 13
02:28:21.254                                                             📜 Move history updated: 24 moves
02:28:21.254 GameHistoryManager                                          Move added: b4c3
02:28:21.254 GameViewModel                                               🔍 Requesting position evaluation...
02:28:21.255                                                             🎭 Updating personality context for move: b4c3
02:28:21.255                                                             ✨ Personality context updated for move b4c3 - This is revolutionary!
02:28:21.255                                                             🔍 Checking game end conditions...
02:28:21.357                                                             ✅ Game continues - no end condition detected
02:28:21.611                                                             ✅ Evaluation received: 2.29
02:28:21.715 CompetitiveModeActivity                                     📊 Evaluation updated: 2.29
02:28:21.715                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.29, emotionalManager: INITIALIZED
02:28:21.716                                                             🎯 Evaluation change: 3.8899999 (threshold: 0.8)
02:28:21.716                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 16s/25s)
02:28:21.716                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:28:32.396                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=1
02:28:32.397                                                             🎯 SQUARE TAPPED: row=6, col=1
02:28:32.397                                                             📝 Player color: white
02:28:32.397                                                             🔍 Selected row/col: -1/-1
02:28:32.751                                                             🎯 Selected piece: P at 6, 1
02:28:32.933                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
02:28:32.933                                                             🎯 SQUARE TAPPED: row=5, col=2
02:28:32.933                                                             📝 Player color: white
02:28:32.933                                                             🔍 Selected row/col: 6/1
02:28:32.933                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=5, reaches=false
02:28:32.933                                                             🎯 ATTEMPTING MOVE: b2c3
02:28:32.933                                                             📝 Player color: white
02:28:32.933                                                             🔄 Is player's turn: true
02:28:32.933                                                             🔄 Is white's turn: true
02:28:32.933                                                             ✅ Turn validation passed, making move: b2c3
02:28:32.933 GameViewModel                                               🎯 makePlayerMove called with: b2c3
02:28:32.984                                                             🔍 Validating move: b2c3 (attempt 1)
02:28:33.086                                                             📋 Current position: r3k1nr/1p1nqpp1/p1p1N2p/8/Q1P1P3/2b1B3/PP3PPP/3RKB1R w Kkq - 0 13
02:28:33.137                                                             ⚖️ Move b2c3 legality check: LEGAL
02:28:33.137                                                             ✅ Executing validated move: b2c3
02:28:33.340                                                             📍 New position after move: r3k1nr/1p1nqpp1/p1p1N2p/8/Q1P1P3/2P1B3/P4PPP/3RKB1R b Kkq - 0 13
02:28:33.342 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k1nr/1p1nqpp1/p1p1N2p/8/Q1P1P3/2P1B3/P4PPP/3RKB1R b Kkq - 0 13
02:28:33.347                                                             📜 Move history updated: 25 moves
02:28:33.347 GameHistoryManager                                          Move added: b2c3
02:28:33.349 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
02:28:33.444 GameViewModel                                               🔍 Requesting position evaluation...
02:28:33.444                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:28:33.444                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:28:33.444                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:28:33.445                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:28:33.445                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:28:33.445                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:28:33.445                                                             🔧 gameRepository class: GameRepository
02:28:33.445                                                             🔧 Current thread: main
02:28:33.445 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:28:33.898 GameViewModel                                               ✅ Evaluation received: -2.14
02:28:33.998 CompetitiveModeActivity                                     📊 Evaluation updated: -2.14
02:28:33.998                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.14, emotionalManager: INITIALIZED
02:28:33.998                                                             🎯 Evaluation change: 0.5400001 (threshold: 0.8)
02:28:33.998                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 28s/25s)
02:28:33.998                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:28:34.854 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:28:34.885 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7e6
02:28:35.246 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k1nr/1p1n1pp1/p1p1q2p/8/Q1P1P3/2P1B3/P4PPP/3RKB1R w Kkq - 0 14
02:28:35.252                                                             📜 Move history updated: 26 moves
02:28:35.252 GameHistoryManager                                          Move added: e7e6
02:28:35.252 GameViewModel                                               🔍 Requesting position evaluation...
02:28:35.252                                                             🎭 Updating personality context for move: e7e6
02:28:35.252                                                             ✨ Personality context updated for move e7e6 - This is revolutionary!
02:28:35.253                                                             🔍 Checking game end conditions...
02:28:35.353                                                             ✅ Game continues - no end condition detected
02:28:35.811                                                             ✅ Evaluation received: 2.32
02:28:35.913 CompetitiveModeActivity                                     📊 Evaluation updated: 2.32
02:28:35.913                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.32, emotionalManager: INITIALIZED
02:28:35.913                                                             🎯 Evaluation change: 3.92 (threshold: 0.8)
02:28:35.913                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 30s/25s)
02:28:35.914                                                             ✅ Emotional reaction triggered for evaluation: 2.32 (trigger: slight_disadvantage)
02:28:35.915 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:28:35.915                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:28:35.915                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:28:35.915                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
02:28:36.875                                                             ✅ Response generated: Behind, but never despairing—chess is the art of fighting with beauty. I will seek the perfect combi...
02:28:36.876 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: Behind, but never despairing—chess is the art of fighting with beauty. I will seek the perfect combination, where harmony in my pieces overturns the cold arithmetic of the score.
02:28:36.878                                                             🎭 Updated emotion indicator: 🤔
02:28:36.878                                                             🗣️ Speaking master dialogue: Behind, but never despairing—chess is the art of fighting with beauty. I will seek the perfect combination, where harmony in my pieces overturns the cold arithmetic of the score.
02:28:36.878 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:28:36.878                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:28:36.878                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:28:36.878                                                             ✅ Usage context set to: competitive_mode
02:28:36.878 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:28:36.879 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:28:36.879                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:28:36.880 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:28:48.721                                                             🗣️ Master dialogue speech completed
02:28:52.378                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=5
02:28:52.378                                                             🎯 SQUARE TAPPED: row=6, col=5
02:28:52.378                                                             📝 Player color: white
02:28:52.378                                                             🔍 Selected row/col: -1/-1
02:28:52.737                                                             🎯 Selected piece: P at 6, 5
02:28:53.045                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
02:28:53.045                                                             🎯 SQUARE TAPPED: row=5, col=5
02:28:53.045                                                             📝 Player color: white
02:28:53.045                                                             🔍 Selected row/col: 6/5
02:28:53.046                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=5, reaches=false
02:28:53.046                                                             🎯 ATTEMPTING MOVE: f2f3
02:28:53.046                                                             📝 Player color: white
02:28:53.046                                                             🔄 Is player's turn: true
02:28:53.046                                                             🔄 Is white's turn: true
02:28:53.046                                                             ✅ Turn validation passed, making move: f2f3
02:28:53.046 GameViewModel                                               🎯 makePlayerMove called with: f2f3
02:28:53.097                                                             🔍 Validating move: f2f3 (attempt 1)
02:28:53.197                                                             📋 Current position: r3k1nr/1p1n1pp1/p1p1q2p/8/Q1P1P3/2P1B3/P4PPP/3RKB1R w Kkq - 0 14
02:28:53.248                                                             ⚖️ Move f2f3 legality check: LEGAL
02:28:53.248                                                             ✅ Executing validated move: f2f3
02:28:53.451                                                             📍 New position after move: r3k1nr/1p1n1pp1/p1p1q2p/8/Q1P1P3/2P1BP2/P5PP/3RKB1R b Kkq - 0 14
02:28:53.453 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k1nr/1p1n1pp1/p1p1q2p/8/Q1P1P3/2P1BP2/P5PP/3RKB1R b Kkq - 0 14
02:28:53.458                                                             📜 Move history updated: 27 moves
02:28:53.458 GameHistoryManager                                          Move added: f2f3
02:28:53.554 GameViewModel                                               🔍 Requesting position evaluation...
02:28:53.554                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:28:53.554                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:28:53.554                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:28:53.554                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:28:53.554                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:28:53.554                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:28:53.554                                                             🔧 gameRepository class: GameRepository
02:28:53.554                                                             🔧 Current thread: main
02:28:53.554 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:28:54.212 GameViewModel                                               ✅ Evaluation received: -2.24
02:28:54.312 CompetitiveModeActivity                                     📊 Evaluation updated: -2.24
02:28:54.312                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.24, emotionalManager: INITIALIZED
02:28:54.312                                                             🎯 Evaluation change: 4.56 (threshold: 0.8)
02:28:54.312                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 18s/25s)
02:28:54.312                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:28:55.005 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:28:55.036 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8f6
02:28:55.397 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k2r/1p1n1pp1/p1p1qn1p/8/Q1P1P3/2P1BP2/P5PP/3RKB1R w Kkq - 1 15
02:28:55.405                                                             📜 Move history updated: 28 moves
02:28:55.405 GameHistoryManager                                          Move added: g8f6
02:28:55.405 GameViewModel                                               🔍 Requesting position evaluation...
02:28:55.406                                                             🎭 Updating personality context for move: g8f6
02:28:55.406                                                             ✨ Personality context updated for move g8f6 - This is revolutionary!
02:28:55.406                                                             🔍 Checking game end conditions...
02:28:55.508                                                             ✅ Game continues - no end condition detected
02:28:55.914                                                             ✅ Evaluation received: 2.22
02:28:56.017 CompetitiveModeActivity                                     📊 Evaluation updated: 2.22
02:28:56.017                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.22, emotionalManager: INITIALIZED
02:28:56.017                                                             🎯 Evaluation change: 0.099999905 (threshold: 0.8)
02:28:56.017                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 20s/25s)
02:28:56.018                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:29:04.131                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=5
02:29:04.131                                                             🎯 SQUARE TAPPED: row=7, col=5
02:29:04.131                                                             📝 Player color: white
02:29:04.131                                                             🔍 Selected row/col: -1/-1
02:29:04.487                                                             🎯 Selected piece: B at 7, 5
02:29:04.990                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
02:29:04.990                                                             🎯 SQUARE TAPPED: row=6, col=4
02:29:04.990                                                             📝 Player color: white
02:29:04.990                                                             🔍 Selected row/col: 7/5
02:29:04.990                                                             🎯 ATTEMPTING MOVE: f1e2
02:29:04.990                                                             📝 Player color: white
02:29:04.990                                                             🔄 Is player's turn: true
02:29:04.990                                                             🔄 Is white's turn: true
02:29:04.990                                                             ✅ Turn validation passed, making move: f1e2
02:29:04.990 GameViewModel                                               🎯 makePlayerMove called with: f1e2
02:29:05.041                                                             🔍 Validating move: f1e2 (attempt 1)
02:29:05.142                                                             📋 Current position: r3k2r/1p1n1pp1/p1p1qn1p/8/Q1P1P3/2P1BP2/P5PP/3RKB1R w Kkq - 1 15
02:29:05.193                                                             ⚖️ Move f1e2 legality check: LEGAL
02:29:05.193                                                             ✅ Executing validated move: f1e2
02:29:05.395                                                             📍 New position after move: r3k2r/1p1n1pp1/p1p1qn1p/8/Q1P1P3/2P1BP2/P3B1PP/3RK2R b Kkq - 2 15
02:29:05.398 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3k2r/1p1n1pp1/p1p1qn1p/8/Q1P1P3/2P1BP2/P3B1PP/3RK2R b Kkq - 2 15
02:29:05.404                                                             📜 Move history updated: 29 moves
02:29:05.404 GameHistoryManager                                          Move added: f1e2
02:29:05.497 GameViewModel                                               🔍 Requesting position evaluation...
02:29:05.498                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:29:05.498                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:29:05.498                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:29:05.498                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:29:05.498                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:29:05.498                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:29:05.498                                                             🔧 gameRepository class: GameRepository
02:29:05.498                                                             🔧 Current thread: main
02:29:05.499 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:29:06.154 GameViewModel                                               ✅ Evaluation received: -2.15
02:29:06.254 CompetitiveModeActivity                                     📊 Evaluation updated: -2.15
02:29:06.254                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.15, emotionalManager: INITIALIZED
02:29:06.254                                                             🎯 Evaluation change: 4.4700003 (threshold: 0.8)
02:29:06.254                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 30s/25s)
02:29:06.254                                                             ✅ Emotional reaction triggered for evaluation: -2.15 (trigger: slight_advantage)
02:29:06.254 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:29:06.254                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:29:06.254                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:29:06.254                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
02:29:06.958 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:29:06.990 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e8g8
02:29:07.349 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/1p1n1pp1/p1p1qn1p/8/Q1P1P3/2P1BP2/P3B1PP/3RK2R w K - 3 16
02:29:07.357                                                             📜 Move history updated: 30 moves
02:29:07.357 GameHistoryManager                                          Move added: e8g8
02:29:07.357 GameViewModel                                               🔍 Requesting position evaluation...
02:29:07.357                                                             🎭 Updating personality context for move: e8g8
02:29:07.357                                                             ✨ Personality context updated for move e8g8 - This is revolutionary!
02:29:07.358                                                             🔍 Checking game end conditions...
02:29:07.459                                                             ✅ Game continues - no end condition detected
02:29:07.515 OpenAIService                                               ✅ Response generated: The position sings with possibilities—my lead is a melody, not a dirge. I will weave my combinations...
02:29:07.515 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position sings with possibilities—my lead is a melody, not a dirge. I will weave my combinations into a masterpiece, but must remain vigilant for discord.
02:29:07.518                                                             🎭 Updated emotion indicator: 🙂
02:29:07.518                                                             🗣️ Speaking master dialogue: The position sings with possibilities—my lead is a melody, not a dirge. I will weave my combinations into a masterpiece, but must remain vigilant for discord.
02:29:07.518 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:29:07.518                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:29:07.518                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:29:07.518                                                             ✅ Usage context set to: competitive_mode
02:29:07.518 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:29:07.518 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:29:07.518                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:29:07.521 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:29:07.916 GameViewModel                                               ✅ Evaluation received: 2.28
02:29:08.020 CompetitiveModeActivity                                     📊 Evaluation updated: 2.28
02:29:08.020                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.28, emotionalManager: INITIALIZED
02:29:08.020                                                             🎯 Evaluation change: 4.4300003 (threshold: 0.8)
02:29:08.020                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
02:29:08.020                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:29:17.826                                                             🗣️ Master dialogue speech completed
02:29:20.348                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=4
02:29:20.348                                                             🎯 SQUARE TAPPED: row=7, col=4
02:29:20.348                                                             📝 Player color: white
02:29:20.348                                                             🔍 Selected row/col: -1/-1
02:29:20.651                                                             🎯 Selected piece: K at 7, 4
02:29:20.958                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=6
02:29:20.958                                                             🎯 SQUARE TAPPED: row=7, col=6
02:29:20.958                                                             📝 Player color: white
02:29:20.958                                                             🔍 Selected row/col: 7/4
02:29:20.958                                                             🎯 ATTEMPTING MOVE: e1g1
02:29:20.958                                                             📝 Player color: white
02:29:20.958                                                             🔄 Is player's turn: true
02:29:20.958                                                             🔄 Is white's turn: true
02:29:20.958                                                             ✅ Turn validation passed, making move: e1g1
02:29:20.958 GameViewModel                                               🎯 makePlayerMove called with: e1g1
02:29:21.008                                                             🔍 Validating move: e1g1 (attempt 1)
02:29:21.109                                                             📋 Current position: r4rk1/1p1n1pp1/p1p1qn1p/8/Q1P1P3/2P1BP2/P3B1PP/3RK2R w K - 3 16
02:29:21.159                                                             ⚖️ Move e1g1 legality check: LEGAL
02:29:21.159                                                             ✅ Executing validated move: e1g1
02:29:21.361                                                             📍 New position after move: r4rk1/1p1n1pp1/p1p1qn1p/8/Q1P1P3/2P1BP2/P3B1PP/3R1RK1 b - - 4 16
02:29:21.363 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/1p1n1pp1/p1p1qn1p/8/Q1P1P3/2P1BP2/P3B1PP/3R1RK1 b - - 4 16
02:29:21.366                                                             📜 Move history updated: 31 moves
02:29:21.366 GameHistoryManager                                          Move added: e1g1
02:29:21.466 GameViewModel                                               🔍 Requesting position evaluation...
02:29:21.466                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:29:21.466                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:29:21.466                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:29:21.467                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:29:21.467                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:29:21.467                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:29:21.467                                                             🔧 gameRepository class: GameRepository
02:29:21.467                                                             🔧 Current thread: main
02:29:21.467 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:29:22.192 GameViewModel                                               ✅ Evaluation received: -2.10
02:29:22.292 CompetitiveModeActivity                                     📊 Evaluation updated: -2.1
02:29:22.292                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.1, emotionalManager: INITIALIZED
02:29:22.292                                                             🎯 Evaluation change: 0.05000019 (threshold: 0.8)
02:29:22.292                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 16s/25s)
02:29:22.292                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:29:23.229 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:29:23.275 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b7b6
02:29:23.627 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/3n1pp1/ppp1qn1p/8/Q1P1P3/2P1BP2/P3B1PP/3R1RK1 w - - 0 17
02:29:23.629                                                             📜 Move history updated: 32 moves
02:29:23.629 GameHistoryManager                                          Move added: b7b6
02:29:23.629 GameViewModel                                               🔍 Requesting position evaluation...
02:29:23.629                                                             🎭 Updating personality context for move: b7b6
02:29:23.629                                                             ✨ Personality context updated for move b7b6 - This is revolutionary!
02:29:23.629                                                             🔍 Checking game end conditions...
02:29:23.730                                                             ✅ Game continues - no end condition detected
02:29:24.191                                                             ✅ Evaluation received: 2.09
02:29:24.292 CompetitiveModeActivity                                     📊 Evaluation updated: 2.09
02:29:24.292                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.09, emotionalManager: INITIALIZED
02:29:24.292                                                             🎯 Evaluation change: 4.24 (threshold: 0.8)
02:29:24.292                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 18s/25s)
02:29:24.292                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:29:32.188                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
02:29:32.188                                                             🎯 SQUARE TAPPED: row=7, col=3
02:29:32.188                                                             📝 Player color: white
02:29:32.188                                                             🔍 Selected row/col: -1/-1
02:29:32.545                                                             🎯 Selected piece: R at 7, 3
02:29:32.738                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=3
02:29:32.738                                                             🎯 SQUARE TAPPED: row=6, col=3
02:29:32.738                                                             📝 Player color: white
02:29:32.738                                                             🔍 Selected row/col: 7/3
02:29:32.738                                                             🎯 ATTEMPTING MOVE: d1d2
02:29:32.738                                                             📝 Player color: white
02:29:32.738                                                             🔄 Is player's turn: true
02:29:32.738                                                             🔄 Is white's turn: true
02:29:32.738                                                             ✅ Turn validation passed, making move: d1d2
02:29:32.738 GameViewModel                                               🎯 makePlayerMove called with: d1d2
02:29:32.789                                                             🔍 Validating move: d1d2 (attempt 1)
02:29:32.890                                                             📋 Current position: r4rk1/3n1pp1/ppp1qn1p/8/Q1P1P3/2P1BP2/P3B1PP/3R1RK1 w - - 0 17
02:29:32.940                                                             ⚖️ Move d1d2 legality check: LEGAL
02:29:32.941                                                             ✅ Executing validated move: d1d2
02:29:33.142                                                             📍 New position after move: r4rk1/3n1pp1/ppp1qn1p/8/Q1P1P3/2P1BP2/P2RB1PP/5RK1 b - - 1 17
02:29:33.143 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/3n1pp1/ppp1qn1p/8/Q1P1P3/2P1BP2/P2RB1PP/5RK1 b - - 1 17
02:29:33.146                                                             📜 Move history updated: 33 moves
02:29:33.146 GameHistoryManager                                          Move added: d1d2
02:29:33.146 Choreographer                                               Skipped 48 frames!  The application may be doing too much work on its main thread.
02:29:33.250 GameViewModel                                               🔍 Requesting position evaluation...
02:29:33.250                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:29:33.250                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:29:33.251                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:29:33.251                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:29:33.251                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:29:33.251                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:29:33.251                                                             🔧 gameRepository class: GameRepository
02:29:33.251                                                             🔧 Current thread: main
02:29:33.251 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:29:33.857 GameViewModel                                               ✅ Evaluation received: -2.13
02:29:33.966 CompetitiveModeActivity                                     📊 Evaluation updated: -2.13
02:29:33.966                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.13, emotionalManager: INITIALIZED
02:29:33.966                                                             🎯 Evaluation change: 0.01999998 (threshold: 0.8)
02:29:33.966                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 27s/25s)
02:29:33.966                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:29:34.798 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:29:34.853 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a8c8
02:29:35.211 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r2rk1/3n1pp1/ppp1qn1p/8/Q1P1P3/2P1BP2/P2RB1PP/5RK1 w - - 2 18
02:29:35.217                                                             📜 Move history updated: 34 moves
02:29:35.217 GameHistoryManager                                          Move added: a8c8
02:29:35.217 GameViewModel                                               🔍 Requesting position evaluation...
02:29:35.217                                                             🎭 Updating personality context for move: a8c8
02:29:35.218                                                             ✨ Personality context updated for move a8c8 - This is revolutionary!
02:29:35.218                                                             🔍 Checking game end conditions...
02:29:35.320                                                             ✅ Game continues - no end condition detected
02:29:35.676                                                             ✅ Evaluation received: 2.40
02:29:35.780 CompetitiveModeActivity                                     📊 Evaluation updated: 2.4
02:29:35.780                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.4, emotionalManager: INITIALIZED
02:29:35.780                                                             🎯 Evaluation change: 4.55 (threshold: 0.8)
02:29:35.780                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 29s/25s)
02:29:35.780                                                             ✅ Emotional reaction triggered for evaluation: 2.4 (trigger: slight_disadvantage)
02:29:35.781 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:29:35.781                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:29:35.781                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:29:35.781                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
02:29:36.785                                                             ✅ Response generated: A lead is nothing but a challenge to be overcome by deeper calculation and braver play. My next move...
02:29:36.786 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: A lead is nothing but a challenge to be overcome by deeper calculation and braver play. My next moves will sing of sacrifice and surprise—wait for the harmony in chaos.
02:29:36.788                                                             🎭 Updated emotion indicator: 🤔
02:29:36.788                                                             🗣️ Speaking master dialogue: A lead is nothing but a challenge to be overcome by deeper calculation and braver play. My next moves will sing of sacrifice and surprise—wait for the harmony in chaos.
02:29:36.788 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:29:36.788                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:29:36.789                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:29:36.789                                                             ✅ Usage context set to: competitive_mode
02:29:36.789 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:29:36.789 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:29:36.789                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:29:36.790 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:29:48.087                                                             🗣️ Master dialogue speech completed
02:29:53.448                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=0
02:29:53.448                                                             🎯 SQUARE TAPPED: row=4, col=0
02:29:53.448                                                             📝 Player color: white
02:29:53.448                                                             🔍 Selected row/col: -1/-1
02:29:53.804                                                             🎯 Selected piece: Q at 4, 0
02:29:54.840                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=1
02:29:54.840                                                             🎯 SQUARE TAPPED: row=5, col=1
02:29:54.840                                                             📝 Player color: white
02:29:54.840                                                             🔍 Selected row/col: 4/0
02:29:54.840                                                             🎯 ATTEMPTING MOVE: a4b3
02:29:54.840                                                             📝 Player color: white
02:29:54.840                                                             🔄 Is player's turn: true
02:29:54.841                                                             🔄 Is white's turn: true
02:29:54.841                                                             ✅ Turn validation passed, making move: a4b3
02:29:54.841 GameViewModel                                               🎯 makePlayerMove called with: a4b3
02:29:54.891                                                             🔍 Validating move: a4b3 (attempt 1)
02:29:54.993                                                             📋 Current position: 2r2rk1/3n1pp1/ppp1qn1p/8/Q1P1P3/2P1BP2/P2RB1PP/5RK1 w - - 2 18
02:29:55.044                                                             ⚖️ Move a4b3 legality check: LEGAL
02:29:55.044                                                             ✅ Executing validated move: a4b3
02:29:55.246                                                             📍 New position after move: 2r2rk1/3n1pp1/ppp1qn1p/8/2P1P3/1QP1BP2/P2RB1PP/5RK1 b - - 3 18
02:29:55.249 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r2rk1/3n1pp1/ppp1qn1p/8/2P1P3/1QP1BP2/P2RB1PP/5RK1 b - - 3 18
02:29:55.255                                                             📜 Move history updated: 35 moves
02:29:55.255 GameHistoryManager                                          Move added: a4b3
02:29:55.351 GameViewModel                                               🔍 Requesting position evaluation...
02:29:55.352                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:29:55.352                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:29:55.352                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:29:55.352                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:29:55.352                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:29:55.352                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:29:55.352                                                             🔧 gameRepository class: GameRepository
02:29:55.352                                                             🔧 Current thread: main
02:29:55.352 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:29:55.958 GameViewModel                                               ✅ Evaluation received: -2.29
02:29:56.067 CompetitiveModeActivity                                     📊 Evaluation updated: -2.29
02:29:56.067                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.29, emotionalManager: INITIALIZED
02:29:56.067                                                             🎯 Evaluation change: 4.69 (threshold: 0.8)
02:29:56.067                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 20s/25s)
02:29:56.067                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:29:56.759 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:29:56.791 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e6e7
02:29:57.150 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r2rk1/3nqpp1/ppp2n1p/8/2P1P3/1QP1BP2/P2RB1PP/5RK1 w - - 4 19
02:29:57.158                                                             📜 Move history updated: 36 moves
02:29:57.158 GameHistoryManager                                          Move added: e6e7
02:29:57.158 GameViewModel                                               🔍 Requesting position evaluation...
02:29:57.158                                                             🎭 Updating personality context for move: e6e7
02:29:57.158                                                             ✨ Personality context updated for move e6e7 - This is revolutionary!
02:29:57.158                                                             🔍 Checking game end conditions...
02:29:57.260                                                             ✅ Game continues - no end condition detected
02:29:57.670                                                             ✅ Evaluation received: 2.32
02:29:57.773 CompetitiveModeActivity                                     📊 Evaluation updated: 2.32
02:29:57.773                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.32, emotionalManager: INITIALIZED
02:29:57.773                                                             🎯 Evaluation change: 0.08000016 (threshold: 0.8)
02:29:57.773                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 21s/25s)
02:29:57.773                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:30:05.359                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=5
02:30:05.359                                                             🎯 SQUARE TAPPED: row=7, col=5
02:30:05.359                                                             📝 Player color: white
02:30:05.359                                                             🔍 Selected row/col: -1/-1
02:30:05.719                                                             🎯 Selected piece: R at 7, 5
02:30:06.058                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
02:30:06.058                                                             🎯 SQUARE TAPPED: row=7, col=3
02:30:06.058                                                             📝 Player color: white
02:30:06.058                                                             🔍 Selected row/col: 7/5
02:30:06.058                                                             🎯 ATTEMPTING MOVE: f1d1
02:30:06.058                                                             📝 Player color: white
02:30:06.058                                                             🔄 Is player's turn: true
02:30:06.058                                                             🔄 Is white's turn: true
02:30:06.058                                                             ✅ Turn validation passed, making move: f1d1
02:30:06.058 GameViewModel                                               🎯 makePlayerMove called with: f1d1
02:30:06.109                                                             🔍 Validating move: f1d1 (attempt 1)
02:30:06.210                                                             📋 Current position: 2r2rk1/3nqpp1/ppp2n1p/8/2P1P3/1QP1BP2/P2RB1PP/5RK1 w - - 4 19
02:30:06.262                                                             ⚖️ Move f1d1 legality check: LEGAL
02:30:06.262                                                             ✅ Executing validated move: f1d1
02:30:06.464                                                             📍 New position after move: 2r2rk1/3nqpp1/ppp2n1p/8/2P1P3/1QP1BP2/P2RB1PP/3R2K1 b - - 5 19
02:30:06.467 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r2rk1/3nqpp1/ppp2n1p/8/2P1P3/1QP1BP2/P2RB1PP/3R2K1 b - - 5 19
02:30:06.475                                                             📜 Move history updated: 37 moves
02:30:06.475 GameHistoryManager                                          Move added: f1d1
02:30:06.565 GameViewModel                                               🔍 Requesting position evaluation...
02:30:06.565                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:30:06.565                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:30:06.565                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:30:06.565                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:30:06.565                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:30:06.566                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:30:06.566                                                             🔧 gameRepository class: GameRepository
02:30:06.566                                                             🔧 Current thread: main
02:30:06.566 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:30:07.118 GameViewModel                                               ✅ Evaluation received: -2.32
02:30:07.223 CompetitiveModeActivity                                     📊 Evaluation updated: -2.32
02:30:07.223                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.32, emotionalManager: INITIALIZED
02:30:07.223                                                             🎯 Evaluation change: 4.7200003 (threshold: 0.8)
02:30:07.223                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 31s/25s)
02:30:07.223                                                             ✅ Emotional reaction triggered for evaluation: -2.32 (trigger: slight_advantage)
02:30:07.223 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:30:07.223                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:30:07.223                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:30:07.223                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
02:30:07.974 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:30:08.005 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a6a5
02:30:08.367 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r2rk1/3nqpp1/1pp2n1p/p7/2P1P3/1QP1BP2/P2RB1PP/3R2K1 w - - 0 20
02:30:08.374                                                             📜 Move history updated: 38 moves
02:30:08.374 GameHistoryManager                                          Move added: a6a5
02:30:08.374 GameViewModel                                               🔍 Requesting position evaluation...
02:30:08.374                                                             🎭 Updating personality context for move: a6a5
02:30:08.375                                                             ✨ Personality context updated for move a6a5 - This is revolutionary!
02:30:08.375                                                             🔍 Checking game end conditions...
02:30:08.475                                                             ✅ Game continues - no end condition detected
02:30:08.554 OpenAIService                                               ✅ Response generated: The position sings with possibilities. I will not rush, but the beauty of my lead will reveal itself...
02:30:08.554 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position sings with possibilities. I will not rush, but the beauty of my lead will reveal itself through carefully woven combinations.
02:30:08.557                                                             🎭 Updated emotion indicator: 🙂
02:30:08.557                                                             🗣️ Speaking master dialogue: The position sings with possibilities. I will not rush, but the beauty of my lead will reveal itself through carefully woven combinations.
02:30:08.557 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:30:08.557                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:30:08.557                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:30:08.557                                                             ✅ Usage context set to: competitive_mode
02:30:08.557 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:30:08.558 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:30:08.558                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:30:08.560 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:30:08.781 GameViewModel                                               ✅ Evaluation received: 2.39
02:30:08.885 CompetitiveModeActivity                                     📊 Evaluation updated: 2.39
02:30:08.885                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.39, emotionalManager: INITIALIZED
02:30:08.885                                                             🎯 Evaluation change: 4.71 (threshold: 0.8)
02:30:08.885                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
02:30:08.885                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:30:16.398                                                             🗣️ Master dialogue speech completed
02:30:28.075                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=1
02:30:28.075                                                             🎯 SQUARE TAPPED: row=5, col=1
02:30:28.075                                                             📝 Player color: white
02:30:28.075                                                             🔍 Selected row/col: -1/-1
02:30:28.433                                                             🎯 Selected piece: Q at 5, 1
02:30:30.111                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=1
02:30:30.111                                                             🎯 SQUARE TAPPED: row=7, col=1
02:30:30.111                                                             📝 Player color: white
02:30:30.111                                                             🔍 Selected row/col: 5/1
02:30:30.111                                                             🎯 ATTEMPTING MOVE: b3b1
02:30:30.111                                                             📝 Player color: white
02:30:30.111                                                             🔄 Is player's turn: true
02:30:30.111                                                             🔄 Is white's turn: true
02:30:30.111                                                             ✅ Turn validation passed, making move: b3b1
02:30:30.111 GameViewModel                                               🎯 makePlayerMove called with: b3b1
02:30:30.162                                                             🔍 Validating move: b3b1 (attempt 1)
02:30:30.264                                                             📋 Current position: 2r2rk1/3nqpp1/1pp2n1p/p7/2P1P3/1QP1BP2/P2RB1PP/3R2K1 w - - 0 20
02:30:30.315                                                             ⚖️ Move b3b1 legality check: LEGAL
02:30:30.315                                                             ✅ Executing validated move: b3b1
02:30:30.518                                                             📍 New position after move: 2r2rk1/3nqpp1/1pp2n1p/p7/2P1P3/2P1BP2/P2RB1PP/1Q1R2K1 b - - 1 20
02:30:30.521 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r2rk1/3nqpp1/1pp2n1p/p7/2P1P3/2P1BP2/P2RB1PP/1Q1R2K1 b - - 1 20
02:30:30.529                                                             📜 Move history updated: 39 moves
02:30:30.529 GameHistoryManager                                          Move added: b3b1
02:30:30.624 GameViewModel                                               🔍 Requesting position evaluation...
02:30:30.625                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:30:30.625                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:30:30.625                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:30:30.625                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:30:30.625                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:30:30.625                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:30:30.626                                                             🔧 gameRepository class: GameRepository
02:30:30.626                                                             🔧 Current thread: main
02:30:30.626 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:30:31.332 GameViewModel                                               ✅ Evaluation received: -2.26
02:30:31.440 CompetitiveModeActivity                                     📊 Evaluation updated: -2.26
02:30:31.440                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.26, emotionalManager: INITIALIZED
02:30:31.440                                                             🎯 Evaluation change: 0.059999943 (threshold: 0.8)
02:30:31.440                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 24s/25s)
02:30:31.440                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:30:32.135 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:30:32.165 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8e8
02:30:32.526 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r1r1k1/3nqpp1/1pp2n1p/p7/2P1P3/2P1BP2/P2RB1PP/1Q1R2K1 w - - 2 21
02:30:32.536                                                             📜 Move history updated: 40 moves
02:30:32.536 GameHistoryManager                                          Move added: f8e8
02:30:32.536 GameViewModel                                               🔍 Requesting position evaluation...
02:30:32.536                                                             🎭 Updating personality context for move: f8e8
02:30:32.537                                                             ✨ Personality context updated for move f8e8 - This is revolutionary!
02:30:32.537                                                             🔍 Checking game end conditions...
02:30:32.637                                                             ✅ Game continues - no end condition detected
02:30:33.045                                                             ✅ Evaluation received: 2.40
02:30:33.149 CompetitiveModeActivity                                     📊 Evaluation updated: 2.4
02:30:33.149                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.4, emotionalManager: INITIALIZED
02:30:33.149                                                             🎯 Evaluation change: 4.7200003 (threshold: 0.8)
02:30:33.150                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 25s/25s)
02:30:33.150                                                             ✅ Emotional reaction triggered for evaluation: 2.4 (trigger: slight_disadvantage)
02:30:33.151 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:30:33.151                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:30:33.151                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:30:33.152                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
02:30:33.923                                                             ✅ Response generated: A small deficit only sharpens my resolve. The board is a canvas—now to paint a masterpiece of compli...
02:30:33.924 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: A small deficit only sharpens my resolve. The board is a canvas—now to paint a masterpiece of complications and daring combinations.
02:30:33.926                                                             🎭 Updated emotion indicator: 🤔
02:30:33.926                                                             🗣️ Speaking master dialogue: A small deficit only sharpens my resolve. The board is a canvas—now to paint a masterpiece of complications and daring combinations.
02:30:33.926 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:30:33.926                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:30:33.926                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:30:33.926                                                             ✅ Usage context set to: competitive_mode
02:30:33.926 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:30:33.926 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:30:33.927                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:30:33.929 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:30:42.091                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=4
02:30:42.091                                                             🎯 SQUARE TAPPED: row=5, col=4
02:30:42.091                                                             📝 Player color: white
02:30:42.091                                                             🔍 Selected row/col: -1/-1
02:30:42.448                                                             🎯 Selected piece: B at 5, 4
02:30:42.526                                                             🗣️ Master dialogue speech completed
02:30:42.708                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=5
02:30:42.708                                                             🎯 SQUARE TAPPED: row=6, col=5
02:30:42.708                                                             📝 Player color: white
02:30:42.708                                                             🔍 Selected row/col: 5/4
02:30:42.708                                                             🎯 ATTEMPTING MOVE: e3f2
02:30:42.708                                                             📝 Player color: white
02:30:42.708                                                             🔄 Is player's turn: true
02:30:42.708                                                             🔄 Is white's turn: true
02:30:42.708                                                             ✅ Turn validation passed, making move: e3f2
02:30:42.708 GameViewModel                                               🎯 makePlayerMove called with: e3f2
02:30:42.759                                                             🔍 Validating move: e3f2 (attempt 1)
02:30:42.860                                                             📋 Current position: 2r1r1k1/3nqpp1/1pp2n1p/p7/2P1P3/2P1BP2/P2RB1PP/1Q1R2K1 w - - 2 21
02:30:42.911                                                             ⚖️ Move e3f2 legality check: LEGAL
02:30:42.911                                                             ✅ Executing validated move: e3f2
02:30:43.113                                                             📍 New position after move: 2r1r1k1/3nqpp1/1pp2n1p/p7/2P1P3/2P2P2/P2RBBPP/1Q1R2K1 b - - 3 21
02:30:43.116 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r1r1k1/3nqpp1/1pp2n1p/p7/2P1P3/2P2P2/P2RBBPP/1Q1R2K1 b - - 3 21
02:30:43.121                                                             📜 Move history updated: 41 moves
02:30:43.121 GameHistoryManager                                          Move added: e3f2
02:30:43.217 GameViewModel                                               🔍 Requesting position evaluation...
02:30:43.217                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:30:43.218                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:30:43.218                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:30:43.218                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:30:43.218                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:30:43.218                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:30:43.218                                                             🔧 gameRepository class: GameRepository
02:30:43.218                                                             🔧 Current thread: main
02:30:43.218 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:30:43.772 GameViewModel                                               ✅ Evaluation received: -2.28
02:30:43.875 CompetitiveModeActivity                                     📊 Evaluation updated: -2.28
02:30:43.875                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.28, emotionalManager: INITIALIZED
02:30:43.875                                                             🎯 Evaluation change: 4.6800003 (threshold: 0.8)
02:30:43.875                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 10s/25s)
02:30:43.875                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:30:44.663 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:30:44.691 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c8a8
02:30:45.051 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3r1k1/3nqpp1/1pp2n1p/p7/2P1P3/2P2P2/P2RBBPP/1Q1R2K1 w - - 4 22
02:30:45.060                                                             📜 Move history updated: 42 moves
02:30:45.060 GameHistoryManager                                          Move added: c8a8
02:30:45.060 GameViewModel                                               🔍 Requesting position evaluation...
02:30:45.061                                                             🎭 Updating personality context for move: c8a8
02:30:45.061                                                             ✨ Personality context updated for move c8a8 - This is revolutionary!
02:30:45.061                                                             🔍 Checking game end conditions...
02:30:45.163                                                             ✅ Game continues - no end condition detected
02:30:45.470                                                             ✅ Evaluation received: 2.53
02:30:45.573 CompetitiveModeActivity                                     📊 Evaluation updated: 2.53
02:30:45.573                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.53, emotionalManager: INITIALIZED
02:30:45.573                                                             🎯 Evaluation change: 0.12999988 (threshold: 0.8)
02:30:45.574                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 12s/25s)
02:30:45.574                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:30:54.792                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=3
02:30:54.792                                                             🎯 SQUARE TAPPED: row=6, col=3
02:30:54.792                                                             📝 Player color: white
02:30:54.792                                                             🔍 Selected row/col: -1/-1
02:30:55.150                                                             🎯 Selected piece: R at 6, 3
02:30:55.479                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=3
02:30:55.479                                                             🎯 SQUARE TAPPED: row=2, col=3
02:30:55.479                                                             📝 Player color: white
02:30:55.479                                                             🔍 Selected row/col: 6/3
02:30:55.479                                                             🎯 ATTEMPTING MOVE: d2d6
02:30:55.479                                                             📝 Player color: white
02:30:55.479                                                             🔄 Is player's turn: true
02:30:55.479                                                             🔄 Is white's turn: true
02:30:55.479                                                             ✅ Turn validation passed, making move: d2d6
02:30:55.479 GameViewModel                                               🎯 makePlayerMove called with: d2d6
02:30:55.531                                                             🔍 Validating move: d2d6 (attempt 1)
02:30:55.633                                                             📋 Current position: r3r1k1/3nqpp1/1pp2n1p/p7/2P1P3/2P2P2/P2RBBPP/1Q1R2K1 w - - 4 22
02:30:55.684                                                             ⚖️ Move d2d6 legality check: LEGAL
02:30:55.684                                                             ✅ Executing validated move: d2d6
02:30:55.886                                                             📍 New position after move: r3r1k1/3nqpp1/1ppR1n1p/p7/2P1P3/2P2P2/P3BBPP/1Q1R2K1 b - - 5 22
02:30:55.888 CompetitiveModeActivity                                     🎯 Board updated with FEN: r3r1k1/3nqpp1/1ppR1n1p/p7/2P1P3/2P2P2/P3BBPP/1Q1R2K1 b - - 5 22
02:30:55.895                                                             📜 Move history updated: 43 moves
02:30:55.895 GameHistoryManager                                          Move added: d2d6
02:30:55.987 GameViewModel                                               🔍 Requesting position evaluation...
02:30:55.987                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:30:55.987                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:30:55.987                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:30:55.987                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:30:55.987                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:30:55.987                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:30:55.987                                                             🔧 gameRepository class: GameRepository
02:30:55.987                                                             🔧 Current thread: main
02:30:55.988 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:30:56.595 GameViewModel                                               ✅ Evaluation received: -2.46
02:30:56.695 CompetitiveModeActivity                                     📊 Evaluation updated: -2.46
02:30:56.695                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.46, emotionalManager: INITIALIZED
02:30:56.695                                                             🎯 Evaluation change: 4.86 (threshold: 0.8)
02:30:56.695                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 23s/25s)
02:30:56.695                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:30:57.446 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:30:57.478 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e8c8
02:30:57.839 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1r3k1/3nqpp1/1ppR1n1p/p7/2P1P3/2P2P2/P3BBPP/1Q1R2K1 w - - 6 23
02:30:57.848                                                             📜 Move history updated: 44 moves
02:30:57.848 GameHistoryManager                                          Move added: e8c8
02:30:57.848 GameViewModel                                               🔍 Requesting position evaluation...
02:30:57.849                                                             🎭 Updating personality context for move: e8c8
02:30:57.849                                                             ✨ Personality context updated for move e8c8 - This is revolutionary!
02:30:57.849                                                             🔍 Checking game end conditions...
02:30:57.951                                                             ✅ Game continues - no end condition detected
02:30:58.359                                                             ✅ Evaluation received: 3.57
02:30:58.462 CompetitiveModeActivity                                     📊 Evaluation updated: 3.57
02:30:58.462                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 3.57, emotionalManager: INITIALIZED
02:30:58.463                                                             🎯 Evaluation change: 1.1699998 (threshold: 0.8)
02:30:58.463                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 25s/25s)
02:30:58.463                                                             ✅ Emotional reaction triggered for evaluation: 3.57 (trigger: significant_disadvantage)
02:30:58.463 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:30:58.464                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:30:58.464                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:30:58.464                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
02:31:00.352                                                             ✅ Response generated: 22...Qh4! Even in adversity, the board sings its secrets. My queen’s dance on the kingside will crea...
02:31:00.352 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: 22...Qh4! Even in adversity, the board sings its secrets. My queen’s dance on the kingside will create havoc—combinations born of pain are often the most beautiful.
02:31:00.354                                                             🎭 Updated emotion indicator: 😤
02:31:00.354                                                             🗣️ Speaking master dialogue: 22...Qh4! Even in adversity, the board sings its secrets. My queen’s dance on the kingside will create havoc—combinations born of pain are often the most beautiful.
02:31:00.354 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:31:00.354                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:31:00.354                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:31:00.354                                                             ✅ Usage context set to: competitive_mode
02:31:00.354 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:31:00.354 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:31:00.354                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:31:00.355 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:31:13.258                                                             🗣️ Master dialogue speech completed
02:31:34.894                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
02:31:34.894                                                             🎯 SQUARE TAPPED: row=5, col=5
02:31:34.894                                                             📝 Player color: white
02:31:34.894                                                             🔍 Selected row/col: -1/-1
02:31:35.250                                                             🎯 Selected piece: P at 5, 5
02:31:35.400                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
02:31:35.400                                                             🎯 SQUARE TAPPED: row=4, col=5
02:31:35.400                                                             📝 Player color: white
02:31:35.400                                                             🔍 Selected row/col: 5/5
02:31:35.400                                                             ♟️ Promotion check: piece=P, fromRow=5, toRow=4, reaches=false
02:31:35.400                                                             🎯 ATTEMPTING MOVE: f3f4
02:31:35.400                                                             📝 Player color: white
02:31:35.400                                                             🔄 Is player's turn: true
02:31:35.400                                                             🔄 Is white's turn: true
02:31:35.400                                                             ✅ Turn validation passed, making move: f3f4
02:31:35.400 GameViewModel                                               🎯 makePlayerMove called with: f3f4
02:31:35.452                                                             🔍 Validating move: f3f4 (attempt 1)
02:31:35.554                                                             📋 Current position: r1r3k1/3nqpp1/1ppR1n1p/p7/2P1P3/2P2P2/P3BBPP/1Q1R2K1 w - - 6 23
02:31:35.605                                                             ⚖️ Move f3f4 legality check: LEGAL
02:31:35.605                                                             ✅ Executing validated move: f3f4
02:31:35.808                                                             📍 New position after move: r1r3k1/3nqpp1/1ppR1n1p/p7/2P1PP2/2P5/P3BBPP/1Q1R2K1 b - - 0 23
02:31:35.810 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1r3k1/3nqpp1/1ppR1n1p/p7/2P1PP2/2P5/P3BBPP/1Q1R2K1 b - - 0 23
02:31:35.817                                                             📜 Move history updated: 45 moves
02:31:35.817 GameHistoryManager                                          Move added: f3f4
02:31:35.818 Choreographer                                               Skipped 50 frames!  The application may be doing too much work on its main thread.
02:31:35.908 GameViewModel                                               🔍 Requesting position evaluation...
02:31:35.908                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:31:35.908                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:31:35.908                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:31:35.908                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:31:35.908                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:31:35.909                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:31:35.909                                                             🔧 gameRepository class: GameRepository
02:31:35.909                                                             🔧 Current thread: main
02:31:35.909 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:31:36.360 GameViewModel                                               ✅ Evaluation received: -3.29
02:31:36.461 CompetitiveModeActivity                                     📊 Evaluation updated: -3.29
02:31:36.461                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -3.29, emotionalManager: INITIALIZED
02:31:36.461                                                             🎯 Evaluation change: 6.8599997 (threshold: 0.8)
02:31:36.461                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 37s/25s)
02:31:36.461                                                             ✅ Emotional reaction triggered for evaluation: -3.29 (trigger: significant_advantage)
02:31:36.461 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:31:36.461                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:31:36.461                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:31:36.462                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
02:31:37.319 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:31:37.350 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7f8
02:31:37.577 OpenAIService                                               ✅ Response generated: The board sings when the pieces align—my advantage is not just calculation, but a melody of threats....
02:31:37.709 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1r2nk1/4qpp1/1ppR1n1p/p7/2P1PP2/2P5/P3BBPP/1Q1R2K1 w - - 1 24
02:31:37.719                                                             📜 Move history updated: 46 moves
02:31:37.719 GameHistoryManager                                          Move added: d7f8
02:31:37.719 GameViewModel                                               🔍 Requesting position evaluation...
02:31:37.720                                                             🎭 Updating personality context for move: d7f8
02:31:37.720                                                             ✨ Personality context updated for move d7f8 - This is revolutionary!
02:31:37.720                                                             🔍 Checking game end conditions...
02:31:37.822                                                             ✅ Game continues - no end condition detected
02:31:37.823 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings when the pieces align—my advantage is not just calculation, but a melody of threats. The final combination will be beautiful, and inevitable.
02:31:37.830                                                             🎭 Updated emotion indicator: 😏
02:31:37.830                                                             🗣️ Speaking master dialogue: The board sings when the pieces align—my advantage is not just calculation, but a melody of threats. The final combination will be beautiful, and inevitable.
02:31:37.830 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:31:37.830                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:31:37.830                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:31:37.830                                                             ✅ Usage context set to: competitive_mode
02:31:37.831 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:31:37.831 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:31:37.831                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:31:37.833 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:31:38.129 GameViewModel                                               ✅ Evaluation received: 3.54
02:31:38.233 CompetitiveModeActivity                                     📊 Evaluation updated: 3.54
02:31:38.233                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 3.54, emotionalManager: INITIALIZED
02:31:38.233                                                             🎯 Evaluation change: 6.83 (threshold: 0.8)
02:31:38.233                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
02:31:38.233                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:31:48.066                                                             🗣️ Master dialogue speech completed
02:31:53.539                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=4
02:31:53.539                                                             🎯 SQUARE TAPPED: row=4, col=4
02:31:53.539                                                             📝 Player color: white
02:31:53.539                                                             🔍 Selected row/col: -1/-1
02:31:53.896                                                             🎯 Selected piece: P at 4, 4
02:31:54.067                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
02:31:54.067                                                             🎯 SQUARE TAPPED: row=3, col=4
02:31:54.067                                                             📝 Player color: white
02:31:54.067                                                             🔍 Selected row/col: 4/4
02:31:54.067                                                             ♟️ Promotion check: piece=P, fromRow=4, toRow=3, reaches=false
02:31:54.067                                                             🎯 ATTEMPTING MOVE: e4e5
02:31:54.067                                                             📝 Player color: white
02:31:54.067                                                             🔄 Is player's turn: true
02:31:54.067                                                             🔄 Is white's turn: true
02:31:54.068                                                             ✅ Turn validation passed, making move: e4e5
02:31:54.068 GameViewModel                                               🎯 makePlayerMove called with: e4e5
02:31:54.118                                                             🔍 Validating move: e4e5 (attempt 1)
02:31:54.220                                                             📋 Current position: r1r2nk1/4qpp1/1ppR1n1p/p7/2P1PP2/2P5/P3BBPP/1Q1R2K1 w - - 1 24
02:31:54.271                                                             ⚖️ Move e4e5 legality check: LEGAL
02:31:54.271                                                             ✅ Executing validated move: e4e5
02:31:54.473                                                             📍 New position after move: r1r2nk1/4qpp1/1ppR1n1p/p3P3/2P2P2/2P5/P3BBPP/1Q1R2K1 b - - 0 24
02:31:54.475 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1r2nk1/4qpp1/1ppR1n1p/p3P3/2P2P2/2P5/P3BBPP/1Q1R2K1 b - - 0 24
02:31:54.482                                                             📜 Move history updated: 47 moves
02:31:54.483 GameHistoryManager                                          Move added: e4e5
02:31:54.483 Choreographer                                               Skipped 50 frames!  The application may be doing too much work on its main thread.
02:31:54.576 GameViewModel                                               🔍 Requesting position evaluation...
02:31:54.577                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:31:54.577                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:31:54.577                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:31:54.577                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:31:54.577                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:31:54.577                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:31:54.577                                                             🔧 gameRepository class: GameRepository
02:31:54.577                                                             🔧 Current thread: main
02:31:54.577 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:31:55.029 GameViewModel                                               ✅ Evaluation received: -3.28
02:31:55.130 CompetitiveModeActivity                                     📊 Evaluation updated: -3.28
02:31:55.130                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -3.28, emotionalManager: INITIALIZED
02:31:55.130                                                             🎯 Evaluation change: 0.00999999 (threshold: 0.8)
02:31:55.130                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 18s/25s)
02:31:55.130                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:31:55.982 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:31:56.012 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a8b8
02:31:56.372 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr2nk1/4qpp1/1ppR1n1p/p3P3/2P2P2/2P5/P3BBPP/1Q1R2K1 w - - 1 25
02:31:56.383                                                             📜 Move history updated: 48 moves
02:31:56.383 GameHistoryManager                                          Move added: a8b8
02:31:56.383 GameViewModel                                               🔍 Requesting position evaluation...
02:31:56.383                                                             🎭 Updating personality context for move: a8b8
02:31:56.383                                                             ✨ Personality context updated for move a8b8 - This is revolutionary!
02:31:56.383                                                             🔍 Checking game end conditions...
02:31:56.485                                                             ✅ Game continues - no end condition detected
02:31:56.892                                                             ✅ Evaluation received: 3.88
02:31:56.995 CompetitiveModeActivity                                     📊 Evaluation updated: 3.88
02:31:56.996                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 3.88, emotionalManager: INITIALIZED
02:31:56.996                                                             🎯 Evaluation change: 7.17 (threshold: 0.8)
02:31:56.996                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 20s/25s)
02:31:56.996                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:32:30.425                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
02:32:30.425                                                             🎯 SQUARE TAPPED: row=0, col=1
02:32:30.426                                                             📝 Player color: white
02:32:30.426                                                             🔍 Selected row/col: -1/-1
02:32:30.426                                                             🎯 Invalid selection: opponent's piece
02:32:41.207                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=5
02:32:41.207                                                             🎯 SQUARE TAPPED: row=6, col=5
02:32:41.207                                                             📝 Player color: white
02:32:41.207                                                             🔍 Selected row/col: -1/-1
02:32:41.565                                                             🎯 Selected piece: B at 6, 5
02:32:41.843                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=7
02:32:41.843                                                             🎯 SQUARE TAPPED: row=4, col=7
02:32:41.843                                                             📝 Player color: white
02:32:41.843                                                             🔍 Selected row/col: 6/5
02:32:41.843                                                             🎯 ATTEMPTING MOVE: f2h4
02:32:41.843                                                             📝 Player color: white
02:32:41.843                                                             🔄 Is player's turn: true
02:32:41.843                                                             🔄 Is white's turn: true
02:32:41.843                                                             ✅ Turn validation passed, making move: f2h4
02:32:41.843 GameViewModel                                               🎯 makePlayerMove called with: f2h4
02:32:41.894                                                             🔍 Validating move: f2h4 (attempt 1)
02:32:41.995                                                             📋 Current position: 1rr2nk1/4qpp1/1ppR1n1p/p3P3/2P2P2/2P5/P3BBPP/1Q1R2K1 w - - 1 25
02:32:42.046                                                             ⚖️ Move f2h4 legality check: LEGAL
02:32:42.046                                                             ✅ Executing validated move: f2h4
02:32:42.249                                                             📍 New position after move: 1rr2nk1/4qpp1/1ppR1n1p/p3P3/2P2P1B/2P5/P3B1PP/1Q1R2K1 b - - 2 25
02:32:42.252 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr2nk1/4qpp1/1ppR1n1p/p3P3/2P2P1B/2P5/P3B1PP/1Q1R2K1 b - - 2 25
02:32:42.259                                                             📜 Move history updated: 49 moves
02:32:42.259 GameHistoryManager                                          Move added: f2h4
02:32:42.351 GameViewModel                                               🔍 Requesting position evaluation...
02:32:42.351                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:32:42.352                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:32:42.352                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:32:42.352                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:32:42.352                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:32:42.352                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:32:42.352                                                             🔧 gameRepository class: GameRepository
02:32:42.352                                                             🔧 Current thread: main
02:32:42.352 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:32:42.753 GameViewModel                                               ✅ Evaluation received: -3.59
02:32:42.854 CompetitiveModeActivity                                     📊 Evaluation updated: -3.59
02:32:42.854                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -3.59, emotionalManager: INITIALIZED
02:32:42.854                                                             🎯 Evaluation change: 0.29999995 (threshold: 0.8)
02:32:42.854                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 66s/25s)
02:32:42.854                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:32:43.760 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
02:32:43.789 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8g6
02:32:44.150 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qpp1/1ppR1nnp/p3P3/2P2P1B/2P5/P3B1PP/1Q1R2K1 w - - 3 26
02:32:44.160                                                             📜 Move history updated: 50 moves
02:32:44.160 GameHistoryManager                                          Move added: f8g6
02:32:44.160 GameViewModel                                               🔍 Requesting position evaluation...
02:32:44.160                                                             🎭 Updating personality context for move: f8g6
02:32:44.160                                                             ✨ Personality context updated for move f8g6 - This is revolutionary!
02:32:44.160                                                             🔍 Checking game end conditions...
02:32:44.262                                                             ✅ Game continues - no end condition detected
02:32:44.567                                                             ✅ Evaluation received: 4.17
02:32:44.670 CompetitiveModeActivity                                     📊 Evaluation updated: 4.17
02:32:44.670                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 4.17, emotionalManager: INITIALIZED
02:32:44.670                                                             🎯 Evaluation change: 7.46 (threshold: 0.8)
02:32:44.670                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 68s/25s)
02:32:44.670                                                             ✅ Emotional reaction triggered for evaluation: 4.17 (trigger: significant_disadvantage)
02:32:44.672 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:32:44.672                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:32:44.672                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:32:44.673                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
02:32:46.048                                                             ✅ Response generated: Difficult, yes—but never despair! In every crisis lies the seed of a brilliant combination. I will s...
02:32:46.048 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: Difficult, yes—but never despair! In every crisis lies the seed of a brilliant combination. I will search for harmony amid the chaos and strike when the moment is right.
02:32:46.053                                                             🎭 Updated emotion indicator: 😤
02:32:46.053                                                             🗣️ Speaking master dialogue: Difficult, yes—but never despair! In every crisis lies the seed of a brilliant combination. I will search for harmony amid the chaos and strike when the moment is right.
02:32:46.053 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:32:46.053                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:32:46.053                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:32:46.053                                                             ✅ Usage context set to: competitive_mode
02:32:46.054 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:32:46.054 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:32:46.054                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:32:46.055 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:32:57.332                                                             🗣️ Master dialogue speech completed
02:33:08.736                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=7
02:33:08.736                                                             🎯 SQUARE TAPPED: row=4, col=7
02:33:08.736                                                             📝 Player color: white
02:33:08.736                                                             🔍 Selected row/col: -1/-1
02:33:09.094                                                             🎯 Selected piece: B at 4, 7
02:33:09.407                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
02:33:09.407                                                             🎯 SQUARE TAPPED: row=2, col=5
02:33:09.407                                                             📝 Player color: white
02:33:09.407                                                             🔍 Selected row/col: 4/7
02:33:09.407                                                             🎯 ATTEMPTING MOVE: h4f6
02:33:09.407                                                             📝 Player color: white
02:33:09.407                                                             🔄 Is player's turn: true
02:33:09.407                                                             🔄 Is white's turn: true
02:33:09.407                                                             ✅ Turn validation passed, making move: h4f6
02:33:09.407 GameViewModel                                               🎯 makePlayerMove called with: h4f6
02:33:09.458                                                             🔍 Validating move: h4f6 (attempt 1)
02:33:09.560                                                             📋 Current position: 1rr3k1/4qpp1/1ppR1nnp/p3P3/2P2P1B/2P5/P3B1PP/1Q1R2K1 w - - 3 26
02:33:09.611                                                             ⚖️ Move h4f6 legality check: LEGAL
02:33:09.612                                                             ✅ Executing validated move: h4f6
02:33:09.814                                                             📍 New position after move: 1rr3k1/4qpp1/1ppR1Bnp/p3P3/2P2P2/2P5/P3B1PP/1Q1R2K1 b - - 0 26
02:33:09.817 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qpp1/1ppR1Bnp/p3P3/2P2P2/2P5/P3B1PP/1Q1R2K1 b - - 0 26
02:33:09.824                                                             📜 Move history updated: 51 moves
02:33:09.824 GameHistoryManager                                          Move added: h4f6
02:33:09.916 GameViewModel                                               🔍 Requesting position evaluation...
02:33:09.916                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:33:09.916                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:33:09.916                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:33:09.917                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:33:09.917                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:33:09.917                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:33:09.917                                                             🔧 gameRepository class: GameRepository
02:33:09.917                                                             🔧 Current thread: main
02:33:09.917 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:33:10.370 GameViewModel                                               ✅ Evaluation received: -4.07
02:33:10.471 CompetitiveModeActivity                                     📊 Evaluation updated: -4.07
02:33:10.471                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.07, emotionalManager: INITIALIZED
02:33:10.471                                                             🎯 Evaluation change: 8.24 (threshold: 0.8)
02:33:10.471                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 25s/25s)
02:33:10.471                                                             ✅ Emotional reaction triggered for evaluation: -4.07 (trigger: significant_advantage)
02:33:10.472 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:33:10.472                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:33:10.472                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:33:10.472                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
02:33:11.374 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
02:33:11.408 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g7f6
02:33:11.769 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/1ppR1pnp/p3P3/2P2P2/2P5/P3B1PP/1Q1R2K1 w - - 0 27
02:33:11.778                                                             📜 Move history updated: 52 moves
02:33:11.778 GameHistoryManager                                          Move added: g7f6
02:33:11.778 GameViewModel                                               🔍 Requesting position evaluation...
02:33:11.778                                                             🎭 Updating personality context for move: g7f6
02:33:11.779                                                             ✨ Personality context updated for move g7f6 - This is revolutionary!
02:33:11.779                                                             🔍 Checking game end conditions...
02:33:11.847 OpenAIService                                               ✅ Response generated: The position sings in my favor—each piece plays its part in a beautiful, winning harmony. Now to wea...
02:33:11.880 GameViewModel                                               ✅ Game continues - no end condition detected
02:33:11.883 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position sings in my favor—each piece plays its part in a beautiful, winning harmony. Now to weave the final combination and claim the prize.
02:33:11.885                                                             🎭 Updated emotion indicator: 😏
02:33:11.886                                                             🗣️ Speaking master dialogue: The position sings in my favor—each piece plays its part in a beautiful, winning harmony. Now to weave the final combination and claim the prize.
02:33:11.886 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:33:11.886                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:33:11.886                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:33:11.886                                                             ✅ Usage context set to: competitive_mode
02:33:11.886 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:33:11.886 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:33:11.886                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:33:11.888 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:33:12.137 GameViewModel                                               ✅ Evaluation received: 4.24
02:33:12.240 CompetitiveModeActivity                                     📊 Evaluation updated: 4.24
02:33:12.240                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 4.24, emotionalManager: INITIALIZED
02:33:12.241                                                             🎯 Evaluation change: 8.309999 (threshold: 0.8)
02:33:12.241                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
02:33:12.241                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:33:20.186                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=3
02:33:20.186                                                             🎯 SQUARE TAPPED: row=2, col=3
02:33:20.186                                                             📝 Player color: white
02:33:20.186                                                             🔍 Selected row/col: -1/-1
02:33:20.542                                                             🎯 Selected piece: R at 2, 3
02:33:20.784                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
02:33:20.784                                                             🎯 SQUARE TAPPED: row=2, col=5
02:33:20.784                                                             📝 Player color: white
02:33:20.784                                                             🔍 Selected row/col: 2/3
02:33:20.785                                                             🎯 ATTEMPTING MOVE: d6f6
02:33:20.785                                                             📝 Player color: white
02:33:20.785                                                             🔄 Is player's turn: true
02:33:20.785                                                             🔄 Is white's turn: true
02:33:20.785                                                             ✅ Turn validation passed, making move: d6f6
02:33:20.785 GameViewModel                                               🎯 makePlayerMove called with: d6f6
02:33:20.836                                                             🔍 Validating move: d6f6 (attempt 1)
02:33:20.936                                                             📋 Current position: 1rr3k1/4qp2/1ppR1pnp/p3P3/2P2P2/2P5/P3B1PP/1Q1R2K1 w - - 0 27
02:33:20.987                                                             ⚖️ Move d6f6 legality check: LEGAL
02:33:20.988                                                             ✅ Executing validated move: d6f6
02:33:21.189                                                             📍 New position after move: 1rr3k1/4qp2/1pp2Rnp/p3P3/2P2P2/2P5/P3B1PP/1Q1R2K1 b - - 0 27
02:33:21.192 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/1pp2Rnp/p3P3/2P2P2/2P5/P3B1PP/1Q1R2K1 b - - 0 27
02:33:21.200                                                             📜 Move history updated: 53 moves
02:33:21.200 GameHistoryManager                                          Move added: d6f6
02:33:21.293 GameViewModel                                               🔍 Requesting position evaluation...
02:33:21.293                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:33:21.293                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:33:21.293                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:33:21.293                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:33:21.294                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:33:21.294                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:33:21.294                                                             🔧 gameRepository class: GameRepository
02:33:21.294                                                             🔧 Current thread: main
02:33:21.294 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:33:21.321 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
02:33:21.746 GameViewModel                                               ✅ Evaluation received: -4.09
02:33:21.847 CompetitiveModeActivity                                     📊 Evaluation updated: -4.09
02:33:21.847                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.09, emotionalManager: INITIALIZED
02:33:21.847                                                             🎯 Evaluation change: 0.01999998 (threshold: 0.8)
02:33:21.847                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 11s/25s)
02:33:21.847                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
02:33:22.694 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
02:33:22.729 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b6b5
02:33:23.089 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/2p2Rnp/pp2P3/2P2P2/2P5/P3B1PP/1Q1R2K1 w - - 0 28
02:33:23.102                                                             📜 Move history updated: 54 moves
02:33:23.102 GameHistoryManager                                          Move added: b6b5
02:33:23.102 GameViewModel                                               🔍 Requesting position evaluation...
02:33:23.102                                                             🎭 Updating personality context for move: b6b5
02:33:23.102                                                             ✨ Personality context updated for move b6b5 - This is revolutionary!
02:33:23.103                                                             🔍 Checking game end conditions...
02:33:23.204                                                             ✅ Game continues - no end condition detected
02:33:23.509                                                             ✅ Evaluation received: 4.78
02:33:23.613 CompetitiveModeActivity                                     📊 Evaluation updated: 4.78
02:33:23.613                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 4.78, emotionalManager: INITIALIZED
02:33:23.613                                                             🎯 Evaluation change: 8.85 (threshold: 0.8)
02:33:23.613                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 13s/25s)
02:33:23.614                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:33:39.403                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
02:33:39.403                                                             🎯 SQUARE TAPPED: row=7, col=3
02:33:39.403                                                             📝 Player color: white
02:33:39.403                                                             🔍 Selected row/col: -1/-1
02:33:39.764                                                             🎯 Selected piece: R at 7, 3
02:33:40.079                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=3
02:33:40.079                                                             🎯 SQUARE TAPPED: row=2, col=3
02:33:40.079                                                             📝 Player color: white
02:33:40.079                                                             🔍 Selected row/col: 7/3
02:33:40.080                                                             🎯 ATTEMPTING MOVE: d1d6
02:33:40.080                                                             📝 Player color: white
02:33:40.080                                                             🔄 Is player's turn: true
02:33:40.080                                                             🔄 Is white's turn: true
02:33:40.080                                                             ✅ Turn validation passed, making move: d1d6
02:33:40.080 GameViewModel                                               🎯 makePlayerMove called with: d1d6
02:33:40.131                                                             🔍 Validating move: d1d6 (attempt 1)
02:33:40.231                                                             📋 Current position: 1rr3k1/4qp2/2p2Rnp/pp2P3/2P2P2/2P5/P3B1PP/1Q1R2K1 w - - 0 28
02:33:40.283                                                             ⚖️ Move d1d6 legality check: LEGAL
02:33:40.283                                                             ✅ Executing validated move: d1d6
02:33:40.486                                                             📍 New position after move: 1rr3k1/4qp2/2pR1Rnp/pp2P3/2P2P2/2P5/P3B1PP/1Q4K1 b - - 1 28
02:33:40.488 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/2pR1Rnp/pp2P3/2P2P2/2P5/P3B1PP/1Q4K1 b - - 1 28
02:33:40.496                                                             📜 Move history updated: 55 moves
02:33:40.496 GameHistoryManager                                          Move added: d1d6
02:33:40.594 GameViewModel                                               🔍 Requesting position evaluation...
02:33:40.594                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:33:40.594                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:33:40.594                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:33:40.594                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:33:40.594                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:33:40.594                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:33:40.595                                                             🔧 gameRepository class: GameRepository
02:33:40.595                                                             🔧 Current thread: main
02:33:40.595 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:33:41.097 GameViewModel                                               ✅ Evaluation received: -3.98
02:33:41.202 CompetitiveModeActivity                                     📊 Evaluation updated: -3.98
02:33:41.202                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -3.98, emotionalManager: INITIALIZED
02:33:41.202                                                             🎯 Evaluation change: 0.09000015 (threshold: 0.8)
02:33:41.202                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 30s/25s)
02:33:41.202                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
02:33:42.055 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
02:33:42.088 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b5c4
02:33:42.447 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/2pR1Rnp/p3P3/2p2P2/2P5/P3B1PP/1Q4K1 w - - 0 29
02:33:42.456                                                             📜 Move history updated: 56 moves
02:33:42.456 GameHistoryManager                                          Move added: b5c4
02:33:42.456 GameViewModel                                               🔍 Requesting position evaluation...
02:33:42.457                                                             🎭 Updating personality context for move: b5c4
02:33:42.457                                                             ✨ Personality context updated for move b5c4 - This is revolutionary!
02:33:42.457                                                             🔍 Checking game end conditions...
02:33:42.558                                                             ✅ Game continues - no end condition detected
02:33:42.866                                                             ✅ Evaluation received: 4.98
02:33:42.969 CompetitiveModeActivity                                     📊 Evaluation updated: 4.98
02:33:42.969                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 4.98, emotionalManager: INITIALIZED
02:33:42.969                                                             🎯 Evaluation change: 9.05 (threshold: 0.8)
02:33:42.969                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 32s/25s)
02:33:42.969                                                             ✅ Emotional reaction triggered for evaluation: 4.98 (trigger: significant_disadvantage)
02:33:42.969 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:33:42.970                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:33:42.970                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:33:42.970                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
02:33:43.744                                                             ✅ Response generated: (28) The position may seem lost, but chess is the art of resourcefulness. I will seek out a hidden c...
02:33:43.745 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: (28) The position may seem lost, but chess is the art of resourcefulness. I will seek out a hidden combination—where there is chaos, there is also beauty.
02:33:43.747                                                             🎭 Updated emotion indicator: 😤
02:33:43.747                                                             🗣️ Speaking master dialogue: (28) The position may seem lost, but chess is the art of resourcefulness. I will seek out a hidden combination—where there is chaos, there is also beauty.
02:33:43.747 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:33:43.747                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:33:43.747                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:33:43.747                                                             ✅ Usage context set to: competitive_mode
02:33:43.747 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:33:43.747 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:33:43.747                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:33:43.749 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:33:53.558                                                             🗣️ Master dialogue speech completed
02:34:02.521                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=1
02:34:02.521                                                             🎯 SQUARE TAPPED: row=7, col=1
02:34:02.521                                                             📝 Player color: white
02:34:02.521                                                             🔍 Selected row/col: -1/-1
02:34:02.884                                                             🎯 Selected piece: Q at 7, 1
02:34:03.060                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=5
02:34:03.060                                                             🎯 SQUARE TAPPED: row=3, col=5
02:34:03.060                                                             📝 Player color: white
02:34:03.060                                                             🔍 Selected row/col: 7/1
02:34:03.060                                                             🎯 ATTEMPTING MOVE: b1f5
02:34:03.060                                                             📝 Player color: white
02:34:03.060                                                             🔄 Is player's turn: true
02:34:03.060                                                             🔄 Is white's turn: true
02:34:03.060                                                             ✅ Turn validation passed, making move: b1f5
02:34:03.060 GameViewModel                                               🎯 makePlayerMove called with: b1f5
02:34:03.111                                                             🔍 Validating move: b1f5 (attempt 1)
02:34:03.212                                                             📋 Current position: 1rr3k1/4qp2/2pR1Rnp/p3P3/2p2P2/2P5/P3B1PP/1Q4K1 w - - 0 29
02:34:03.263                                                             ⚖️ Move b1f5 legality check: LEGAL
02:34:03.263                                                             ✅ Executing validated move: b1f5
02:34:03.465                                                             📍 New position after move: 1rr3k1/4qp2/2pR1Rnp/p3PQ2/2p2P2/2P5/P3B1PP/6K1 b - - 1 29
02:34:03.468 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/2pR1Rnp/p3PQ2/2p2P2/2P5/P3B1PP/6K1 b - - 1 29
02:34:03.476                                                             📜 Move history updated: 57 moves
02:34:03.476 GameHistoryManager                                          Move added: b1f5
02:34:03.477 Choreographer                                               Skipped 50 frames!  The application may be doing too much work on its main thread.
02:34:03.570 GameViewModel                                               🔍 Requesting position evaluation...
02:34:03.570                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:34:03.570                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:34:03.570                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:34:03.570                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:34:03.570                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:34:03.570                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:34:03.571                                                             🔧 gameRepository class: GameRepository
02:34:03.571                                                             🔧 Current thread: main
02:34:03.571 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:34:04.073 GameViewModel                                               ✅ Evaluation received: -4.97
02:34:04.178 CompetitiveModeActivity                                     📊 Evaluation updated: -4.97
02:34:04.178                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.97, emotionalManager: INITIALIZED
02:34:04.178                                                             🎯 Evaluation change: 9.95 (threshold: 0.8)
02:34:04.178                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 21s/25s)
02:34:04.178                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:34:05.027 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
02:34:05.064 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g6h4
02:34:05.423 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/2pR1R1p/p3PQ2/2p2P1n/2P5/P3B1PP/6K1 w - - 2 30
02:34:05.433                                                             📜 Move history updated: 58 moves
02:34:05.433 GameHistoryManager                                          Move added: g6h4
02:34:05.433 GameViewModel                                               🔍 Requesting position evaluation...
02:34:05.434                                                             🎭 Updating personality context for move: g6h4
02:34:05.434                                                             ✨ Personality context updated for move g6h4 - This is revolutionary!
02:34:05.434                                                             🔍 Checking game end conditions...
02:34:05.536                                                             ✅ Game continues - no end condition detected
02:34:05.792                                                             ✅ Evaluation received: 6.08
02:34:05.895 CompetitiveModeActivity                                     📊 Evaluation updated: 6.08
02:34:05.895                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 6.08, emotionalManager: INITIALIZED
02:34:05.895                                                             🎯 Evaluation change: 1.0999999 (threshold: 0.8)
02:34:05.895                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 22s/25s)
02:34:05.895                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:34:24.932                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=5
02:34:24.932                                                             🎯 SQUARE TAPPED: row=3, col=5
02:34:24.932                                                             📝 Player color: white
02:34:24.932                                                             🔍 Selected row/col: -1/-1
02:34:25.290                                                             🎯 Selected piece: Q at 3, 5
02:34:25.979                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=6
02:34:25.979                                                             🎯 SQUARE TAPPED: row=4, col=6
02:34:25.980                                                             📝 Player color: white
02:34:25.980                                                             🔍 Selected row/col: 3/5
02:34:25.980                                                             🎯 ATTEMPTING MOVE: f5g4
02:34:25.980                                                             📝 Player color: white
02:34:25.980                                                             🔄 Is player's turn: true
02:34:25.980                                                             🔄 Is white's turn: true
02:34:25.980                                                             ✅ Turn validation passed, making move: f5g4
02:34:25.980 GameViewModel                                               🎯 makePlayerMove called with: f5g4
02:34:26.030                                                             🔍 Validating move: f5g4 (attempt 1)
02:34:26.132                                                             📋 Current position: 1rr3k1/4qp2/2pR1R1p/p3PQ2/2p2P1n/2P5/P3B1PP/6K1 w - - 2 30
02:34:26.183                                                             ⚖️ Move f5g4 legality check: LEGAL
02:34:26.183                                                             ✅ Executing validated move: f5g4
02:34:26.385                                                             📍 New position after move: 1rr3k1/4qp2/2pR1R1p/p3P3/2p2PQn/2P5/P3B1PP/6K1 b - - 3 30
02:34:26.388 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/2pR1R1p/p3P3/2p2PQn/2P5/P3B1PP/6K1 b - - 3 30
02:34:26.395                                                             📜 Move history updated: 59 moves
02:34:26.395 GameHistoryManager                                          Move added: f5g4
02:34:26.487 GameViewModel                                               🔍 Requesting position evaluation...
02:34:26.487                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:34:26.487                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:34:26.487                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:34:26.487                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:34:26.487                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:34:26.487                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:34:26.487                                                             🔧 gameRepository class: GameRepository
02:34:26.487                                                             🔧 Current thread: main
02:34:26.488 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:34:26.940 GameViewModel                                               ✅ Evaluation received: -6.51
02:34:27.041 CompetitiveModeActivity                                     📊 Evaluation updated: -6.51
02:34:27.041                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.51, emotionalManager: INITIALIZED
02:34:27.041                                                             🎯 Evaluation change: 11.49 (threshold: 0.8)
02:34:27.041                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 44s/25s)
02:34:27.041                                                             ✅ Emotional reaction triggered for evaluation: -6.51 (trigger: significant_advantage)
02:34:27.041 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:34:27.041                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:34:27.041                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:34:27.041                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
02:34:27.900 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
02:34:27.935 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h4g6
02:34:28.176 OpenAIService                                               ✅ Response generated: The board sings in my favor—every piece, every pawn, moves toward a single climax. My combinations a...
02:34:28.291 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/2pR1Rnp/p3P3/2p2PQ1/2P5/P3B1PP/6K1 w - - 4 31
02:34:28.301                                                             📜 Move history updated: 60 moves
02:34:28.301 GameHistoryManager                                          Move added: h4g6
02:34:28.301 GameViewModel                                               🔍 Requesting position evaluation...
02:34:28.301                                                             🎭 Updating personality context for move: h4g6
02:34:28.302                                                             ✨ Personality context updated for move h4g6 - This is revolutionary!
02:34:28.302                                                             🔍 Checking game end conditions...
02:34:28.403                                                             ✅ Game continues - no end condition detected
02:34:28.404 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings in my favor—every piece, every pawn, moves toward a single climax. My combinations are not just calculation, but poetry in motion.
02:34:28.411                                                             🎭 Updated emotion indicator: 😏
02:34:28.411                                                             🗣️ Speaking master dialogue: The board sings in my favor—every piece, every pawn, moves toward a single climax. My combinations are not just calculation, but poetry in motion.
02:34:28.412 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:34:28.412                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:34:28.412                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:34:28.412                                                             ✅ Usage context set to: competitive_mode
02:34:28.412 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:34:28.412 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:34:28.412                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:34:28.415 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:34:28.658 GameViewModel                                               ✅ Evaluation received: 6.83
02:34:28.762 CompetitiveModeActivity                                     📊 Evaluation updated: 6.83
02:34:28.762                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 6.83, emotionalManager: INITIALIZED
02:34:28.762                                                             🎯 Evaluation change: 13.34 (threshold: 0.8)
02:34:28.762                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
02:34:28.762                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:34:37.668                                                             🗣️ Master dialogue speech completed
02:34:58.749                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
02:34:58.749                                                             🎯 SQUARE TAPPED: row=6, col=4
02:34:58.749                                                             📝 Player color: white
02:34:58.749                                                             🔍 Selected row/col: -1/-1
02:34:59.105                                                             🎯 Selected piece: B at 6, 4
02:34:59.406                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
02:34:59.406                                                             🎯 SQUARE TAPPED: row=4, col=2
02:34:59.406                                                             📝 Player color: white
02:34:59.406                                                             🔍 Selected row/col: 6/4
02:34:59.406                                                             🎯 ATTEMPTING MOVE: e2c4
02:34:59.406                                                             📝 Player color: white
02:34:59.406                                                             🔄 Is player's turn: true
02:34:59.406                                                             🔄 Is white's turn: true
02:34:59.406                                                             ✅ Turn validation passed, making move: e2c4
02:34:59.407 GameViewModel                                               🎯 makePlayerMove called with: e2c4
02:34:59.457                                                             🔍 Validating move: e2c4 (attempt 1)
02:34:59.558                                                             📋 Current position: 1rr3k1/4qp2/2pR1Rnp/p3P3/2p2PQ1/2P5/P3B1PP/6K1 w - - 4 31
02:34:59.609                                                             ⚖️ Move e2c4 legality check: LEGAL
02:34:59.609                                                             ✅ Executing validated move: e2c4
02:34:59.812                                                             📍 New position after move: 1rr3k1/4qp2/2pR1Rnp/p3P3/2B2PQ1/2P5/P5PP/6K1 b - - 0 31
02:34:59.813 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr3k1/4qp2/2pR1Rnp/p3P3/2B2PQ1/2P5/P5PP/6K1 b - - 0 31
02:34:59.821                                                             📜 Move history updated: 61 moves
02:34:59.821 GameHistoryManager                                          Move added: e2c4
02:34:59.915 GameViewModel                                               🔍 Requesting position evaluation...
02:34:59.915                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:34:59.915                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:34:59.915                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:34:59.915                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:34:59.915                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:34:59.915                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:34:59.915                                                             🔧 gameRepository class: GameRepository
02:34:59.916                                                             🔧 Current thread: main
02:34:59.916 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:35:00.523 GameViewModel                                               ✅ Evaluation received: -7.65
02:35:00.631 CompetitiveModeActivity                                     📊 Evaluation updated: -7.65
02:35:00.631                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -7.65, emotionalManager: INITIALIZED
02:35:00.631                                                             🎯 Evaluation change: 1.1399999 (threshold: 0.8)
02:35:00.631                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 33s/25s)
02:35:00.631                                                             ✅ Emotional reaction triggered for evaluation: -7.65 (trigger: significant_advantage)
02:35:00.631 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
02:35:00.631                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:35:00.631                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:35:00.632                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
02:35:01.421 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
02:35:01.456 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8h8
02:35:01.521 OpenAIService                                               ✅ Response generated: The position sings with possibilities—my pieces are a symphony, his a discord. One more creative str...
02:35:01.816 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr4k/4qp2/2pR1Rnp/p3P3/2B2PQ1/2P5/P5PP/6K1 w - - 1 32
02:35:01.826                                                             📜 Move history updated: 62 moves
02:35:01.826 GameHistoryManager                                          Move added: g8h8
02:35:01.826 GameViewModel                                               🔍 Requesting position evaluation...
02:35:01.827                                                             🎭 Updating personality context for move: g8h8
02:35:01.827                                                             ✨ Personality context updated for move g8h8 - This is revolutionary!
02:35:01.827                                                             🔍 Checking game end conditions...
02:35:01.929                                                             ✅ Game continues - no end condition detected
02:35:01.929 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position sings with possibilities—my pieces are a symphony, his a discord. One more creative stroke and the game is mine.
02:35:01.935                                                             🎭 Updated emotion indicator: 😏
02:35:01.935                                                             🗣️ Speaking master dialogue: The position sings with possibilities—my pieces are a symphony, his a discord. One more creative stroke and the game is mine.
02:35:01.935 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
02:35:01.935                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
02:35:01.935                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
02:35:01.935                                                             ✅ Usage context set to: competitive_mode
02:35:01.935 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
02:35:01.936 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
02:35:01.936                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
02:35:01.937 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
02:35:02.135 GameViewModel                                               ✅ Evaluation received: 9.17
02:35:02.238 CompetitiveModeActivity                                     📊 Evaluation updated: 9.17
02:35:02.238                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 9.17, emotionalManager: INITIALIZED
02:35:02.238                                                             🎯 Evaluation change: 16.82 (threshold: 0.8)
02:35:02.238                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
02:35:02.238                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:35:09.724                                                             🗣️ Master dialogue speech completed
02:35:10.219                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
02:35:10.219                                                             🎯 SQUARE TAPPED: row=2, col=5
02:35:10.219                                                             📝 Player color: white
02:35:10.219                                                             🔍 Selected row/col: -1/-1
02:35:10.577                                                             🎯 Selected piece: R at 2, 5
02:35:10.765                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=5
02:35:10.765                                                             🎯 SQUARE TAPPED: row=1, col=5
02:35:10.765                                                             📝 Player color: white
02:35:10.765                                                             🔍 Selected row/col: 2/5
02:35:10.765                                                             🎯 ATTEMPTING MOVE: f6f7
02:35:10.765                                                             📝 Player color: white
02:35:10.765                                                             🔄 Is player's turn: true
02:35:10.765                                                             🔄 Is white's turn: true
02:35:10.765                                                             ✅ Turn validation passed, making move: f6f7
02:35:10.765 GameViewModel                                               🎯 makePlayerMove called with: f6f7
02:35:10.816                                                             🔍 Validating move: f6f7 (attempt 1)
02:35:10.917                                                             📋 Current position: 1rr4k/4qp2/2pR1Rnp/p3P3/2B2PQ1/2P5/P5PP/6K1 w - - 1 32
02:35:10.968                                                             ⚖️ Move f6f7 legality check: LEGAL
02:35:10.968                                                             ✅ Executing validated move: f6f7
02:35:11.171                                                             📍 New position after move: 1rr4k/4qR2/2pR2np/p3P3/2B2PQ1/2P5/P5PP/6K1 b - - 0 32
02:35:11.174 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr4k/4qR2/2pR2np/p3P3/2B2PQ1/2P5/P5PP/6K1 b - - 0 32
02:35:11.184                                                             📜 Move history updated: 63 moves
02:35:11.184 GameHistoryManager                                          Move added: f6f7
02:35:11.186 Choreographer                                               Skipped 50 frames!  The application may be doing too much work on its main thread.
02:35:11.277 GameViewModel                                               🔍 Requesting position evaluation...
02:35:11.277                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:35:11.277                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:35:11.277                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:35:11.277                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:35:11.277                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:35:11.277                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:35:11.277                                                             🔧 gameRepository class: GameRepository
02:35:11.277                                                             🔧 Current thread: main
02:35:11.277 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:35:11.628 GameViewModel                                               ✅ Evaluation received: -9.04
02:35:11.729 CompetitiveModeActivity                                     📊 Evaluation updated: -9.04
02:35:11.729                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -9.04, emotionalManager: INITIALIZED
02:35:11.729                                                             🎯 Evaluation change: 1.3899999 (threshold: 0.8)
02:35:11.729                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 11s/25s)
02:35:11.729                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:35:12.588 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
02:35:12.624 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a5a4
02:35:12.985 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr4k/4qR2/2pR2np/4P3/p1B2PQ1/2P5/P5PP/6K1 w - - 0 33
02:35:12.999                                                             📜 Move history updated: 64 moves
02:35:12.999 GameHistoryManager                                          Move added: a5a4
02:35:12.999 GameViewModel                                               🔍 Requesting position evaluation...
02:35:12.999                                                             🎭 Updating personality context for move: a5a4
02:35:12.999                                                             ✨ Personality context updated for move a5a4 - This is revolutionary!
02:35:12.999                                                             🔍 Checking game end conditions...
02:35:13.101                                                             ✅ Game continues - no end condition detected
02:35:13.254                                                             ✅ Evaluation received: M6
02:35:13.357 CompetitiveModeActivity                                     📊 Evaluation updated: 0.0
02:35:13.357                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
02:35:13.357                                                             🎯 Evaluation change: 7.65 (threshold: 0.8)
02:35:13.357                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 12s/25s)
02:35:13.357                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:35:20.033                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=5
02:35:20.033                                                             🎯 SQUARE TAPPED: row=1, col=5
02:35:20.033                                                             📝 Player color: white
02:35:20.033                                                             🔍 Selected row/col: -1/-1
02:35:20.391                                                             🎯 Selected piece: R at 1, 5
02:35:20.555                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
02:35:20.555                                                             🎯 SQUARE TAPPED: row=1, col=4
02:35:20.555                                                             📝 Player color: white
02:35:20.555                                                             🔍 Selected row/col: 1/5
02:35:20.555                                                             🎯 ATTEMPTING MOVE: f7e7
02:35:20.555                                                             📝 Player color: white
02:35:20.555                                                             🔄 Is player's turn: true
02:35:20.555                                                             🔄 Is white's turn: true
02:35:20.555                                                             ✅ Turn validation passed, making move: f7e7
02:35:20.555 GameViewModel                                               🎯 makePlayerMove called with: f7e7
02:35:20.606                                                             🔍 Validating move: f7e7 (attempt 1)
02:35:20.708                                                             📋 Current position: 1rr4k/4qR2/2pR2np/4P3/p1B2PQ1/2P5/P5PP/6K1 w - - 0 33
02:35:20.759                                                             ⚖️ Move f7e7 legality check: LEGAL
02:35:20.759                                                             ✅ Executing validated move: f7e7
02:35:20.961                                                             📍 New position after move: 1rr4k/4R3/2pR2np/4P3/p1B2PQ1/2P5/P5PP/6K1 b - - 0 33
02:35:20.964 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1rr4k/4R3/2pR2np/4P3/p1B2PQ1/2P5/P5PP/6K1 b - - 0 33
02:35:20.973                                                             📜 Move history updated: 65 moves
02:35:20.973 GameHistoryManager                                          Move added: f7e7
02:35:20.974 Choreographer                                               Skipped 50 frames!  The application may be doing too much work on its main thread.
02:35:21.065 GameViewModel                                               🔍 Requesting position evaluation...
02:35:21.066                                                             🎭 Using PERSONALITY ENGINE for move calculation!
02:35:21.066                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
02:35:21.066                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
02:35:21.066                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
02:35:21.066                                                             🔧 DEBUG: gameRepository instance = NOT NULL
02:35:21.066                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
02:35:21.066                                                             🔧 gameRepository class: GameRepository
02:35:21.066                                                             🔧 Current thread: main
02:35:21.066 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
02:35:21.317 GameViewModel                                               ✅ Evaluation received: M-7
02:35:21.418 CompetitiveModeActivity                                     📊 Evaluation updated: 0.0
02:35:21.418                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
02:35:21.418                                                             🎯 Evaluation change: 7.65 (threshold: 0.8)
02:35:21.418                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 20s/25s)
02:35:21.418                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
02:35:22.324 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
02:35:22.359 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8b1
02:35:22.718 CompetitiveModeActivity                                     🎯 Board updated with FEN: 2r4k/4R3/2pR2np/4P3/p1B2PQ1/2P5/P5PP/1r4K1 w - - 1 34
02:35:22.727                                                             📜 Move history updated: 66 moves
02:35:22.727 GameHistoryManager                                          Move added: b8b1
02:35:22.727 GameViewModel                                               🔍 Requesting position evaluation...
02:35:22.727                                                             🎭 Updating personality context for move: b8b1
02:35:22.727                                                             ✨ Personality context updated for move b8b1 - This is revolutionary!
02:35:22.727                                                             🔍 Checking game end conditions...
02:35:22.828                                                             ✅ Game continues - no end condition detected
02:35:22.983                                                             ✅ Evaluation received: M5
02:35:23.085 CompetitiveModeActivity                                     📊 Evaluation updated: 0.0
02:35:23.086                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
02:35:23.086                                                             🎯 Evaluation change: 7.65 (threshold: 0.8)
02:35:23.086                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 22s/25s)
02:35:23.086                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
