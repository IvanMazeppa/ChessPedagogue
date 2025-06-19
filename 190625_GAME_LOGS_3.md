--------- beginning of main
--------- beginning of system
03:50:59.429 ActivityThread          com.example.chesspedagogue          com.example.chesspedagogue will use render engine as VK
03:50:59.443 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@9ac6645
03:50:59.509 ScrollView                                                  initGoToTop
03:50:59.518                                                             initGoToTop
03:50:59.525 InputMethodManager                                          invalidateInput
03:50:59.526                                                             invalidateInput
03:50:59.529 LogThrottlerConfig                                          📊 Log throttling initialized: VERBOSE
03:50:59.529 ApiKeyConfig                                                Using API key from ApiKeys class
03:50:59.541 OpenAIService                                               API key set, length: 164
03:50:59.541 ApiKeyConfig                                                Initialized OpenAIClient with API key from config
03:50:59.541 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:50:59.541                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:50:59.542 ApiKeyConfig                                                Using API key from ApiKeys class
03:50:59.542 OpenAIService                                               API key set, length: 164
03:50:59.542 ApiKeyConfig                                                Initialized OpenAIClient with API key from config
03:50:59.542 TTSServiceManager                                           ✅ Switched to ElevenLabs TTS
03:50:59.545 🎭 VoiceEm...alAnalyzer                                     ✅ Initialized voice profiles for 12 masters
03:50:59.545                                                             🚀 Voice-Emotion Feedback System initialized
03:50:59.546 TTSServiceManager                                           🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:50:59.546 ApiKeyConfig                                                Using API key from ApiKeys class
03:50:59.546                                                             Using API key from ApiKeys class
03:50:59.546 OpenAIService                                               API key set, length: 164
03:50:59.944 GameViewModel                                               🎭 Initializing personality LiveData...
03:50:59.944                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
03:50:59.944                                                             🔍 Requesting position evaluation...
03:50:59.949                                                             ✅ Auto-commentary system enabled
03:50:59.949 ApiKeyConfig                                                Using API key from ApiKeys class
03:50:59.950 GameViewModel                                               🎮 Starting new game with configuration: white, skill=10, elo=1750
03:50:59.950 GameHistoryManager                                          GameHistoryManager instance created
03:50:59.950                                                             Game history cleared
03:50:59.951 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:50:59.951                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:50:59.951                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:50:59.951                                                             ✅ Usage context set to: spectator_mode
03:50:59.952 EmotionalIntelligence                                       🚀 CONSTRUCTOR CALLED: EmotionalIntelligenceManager initialization starting...
03:50:59.953                                                             ✅ Persistence manager initialized for EQ database operations
03:50:59.953                                                             🔧 About to call verifyDatabaseIntegrity()...
03:50:59.954 RelationshipPersistence                                     🔧 Database integrity check - emotional_reactions table exists: true
03:50:59.954                                                             🔧 Emotional reactions table schema:
03:50:59.954                                                               - Column: id (INTEGER) NOT NULL: false
03:50:59.954                                                               - Column: master (TEXT) NOT NULL: true
03:50:59.954                                                               - Column: opponent (TEXT) NOT NULL: true
03:50:59.954                                                               - Column: topic (TEXT) NOT NULL: true
03:50:59.955                                                               - Column: emotion (TEXT) NOT NULL: true
03:50:59.955                                                               - Column: intensity (REAL) NOT NULL: true
03:50:59.955                                                               - Column: momentum (REAL) NOT NULL: false
03:50:59.955                                                               - Column: game_context (TEXT) NOT NULL: false
03:50:59.955                                                               - Column: position_evaluation (REAL) NOT NULL: false
03:50:59.955                                                               - Column: conversation_snippet (TEXT) NOT NULL: false
03:50:59.955                                                               - Column: relationship_impact (REAL) NOT NULL: false
03:50:59.955                                                               - Column: timestamp (INTEGER) NOT NULL: true
03:50:59.955                                                             🔧 Database integrity check - master_relationships table exists: true
03:50:59.955                                                             🔧 Master relationships table schema:
03:50:59.955                                                               - Column: id (INTEGER) NOT NULL: false
03:50:59.955                                                               - Column: master1 (TEXT) NOT NULL: true
03:50:59.955                                                               - Column: master2 (TEXT) NOT NULL: true
03:50:59.955                                                               - Column: respect_level (REAL) NOT NULL: false
03:50:59.955                                                               - Column: rivalry_intensity (REAL) NOT NULL: false
03:50:59.955                                                               - Column: friendship_bond (REAL) NOT NULL: false
03:50:59.956                                                               - Column: communication_style (TEXT) NOT NULL: false
03:50:59.956                                                               - Column: total_interactions (INTEGER) NOT NULL: false
03:50:59.956                                                               - Column: total_games (INTEGER) NOT NULL: false
03:50:59.956                                                               - Column: last_major_event (TEXT) NOT NULL: false
03:50:59.956                                                               - Column: last_updated (INTEGER) NOT NULL: false
03:50:59.956                                                               - Column: created_at (INTEGER) NOT NULL: false
03:50:59.956                                                             🔧 Database tables exist - testing simple operations...
03:50:59.957                                                             🔧 Current emotional_reactions records: 642
03:50:59.957                                                             🔧 Current master_relationships records: 45
03:50:59.957 EmotionalIntelligence                                       🔧 verifyDatabaseIntegrity() completed
03:50:59.957                                                             🧪 About to call testDatabaseWrites()...
03:50:59.957 RelationshipPersistence                                     🧪 Testing database writes...
03:50:59.958                                                             🔧 Recording emotional reaction - Parameters: master=alekhine, opponent=carlsen, topic=chess_aesthetics, emotion=impressed, intensity=0.80
03:50:59.963                                                             🎭 Recorded emotional reaction: alekhine felt impressed (0.80) about chess_aesthetics with carlsen
03:50:59.963                                                             👥 Retrieved relationship: alekhine <-> carlsen (respect: 7.60, rivalry: 0.00, friendship: 0.00)
03:50:59.963                                                             🔧 Updating relationship - alekhine <-> carlsen (respect: 7.70, rivalry: 0.00, friendship: 0.00)
03:50:59.964                                                             💾 Updated relationship: alekhine <-> carlsen (respect: 7.70, rivalry: 0.00, friendship: 0.00)
03:50:59.964                                                             🧪 Database write test completed
03:50:59.964 EmotionalIntelligence                                       🧪 testDatabaseWrites() completed
03:50:59.965                                                             🎭 Enhanced Emotional Intelligence Manager initialized with database persistence
03:50:59.969 ConversationVariety                                         Error parsing phrase count: alekhine:a small deficit does not deter me
03:50:59.970                                                             ✅ Loaded 4 tracked phrases
03:50:59.971 EmotionalIntelligence                                       🧠 Loaded emotional history for tal vs fischer: 0 events, momentum: 0.40
03:50:59.971 ConversationFlowTester                                      🎭 OPTIMIZING CONVERSATIONS FOR SPECTATOR MODE:
03:50:59.971                                                             📊 Current template analysis:
03:50:59.971                                                             🏥 CONVERSATION HEALTH METRICS:
03:50:59.973 Conversati...maTemplate                                     🌊 CREATING NATURAL FLOW TEMPLATE: Natural Decline (Pattern: DECLINING, First: 60 words, Follow-up: 60.0% of first)
03:50:59.974                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Natural Decline (Initial: 48-72 words, Followup: 8-36 words, Flow: DECLINING, Intensity: 0.8)
03:50:59.974                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Building Excitement (Pattern: BUILDING, First: 35 words, Follow-up: 80.0% of first)
03:50:59.974                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Building Excitement (Initial: 28-42 words, Followup: 8-28 words, Flow: BUILDING, Intensity: 0.8)
03:50:59.974                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Wave Discussion (Pattern: WAVE, First: 50 words, Follow-up: 70.0% of first)
03:50:59.974                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Wave Discussion (Initial: 40-60 words, Followup: 8-35 words, Flow: WAVE, Intensity: 0.8)
03:50:59.975                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Explosive Debate (Pattern: EXPLOSIVE, First: 40 words, Follow-up: 50.0% of first)
03:50:59.975                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Explosive Debate (Initial: 32-48 words, Followup: 8-20 words, Flow: EXPLOSIVE, Intensity: 0.8)
03:50:59.975 ConversationFlowTester                                      🎭 CURRENT: Engaging Chess Dialogue | Opening: 4-6 turns (90% chance) | Analysis: 4-6 turns (90% chance) | Style: DRAMATIC
03:50:59.975                                                               📊 opening: 4-6 turns, 90% response chance, 2000ms intervals
03:50:59.976                                                               📊 brilliant_move: 4-6 turns, 90% response chance, 1800ms intervals
03:50:59.976                                                               📊 position_change: 4-6 turns, 90% response chance, 2200ms intervals
03:50:59.976                                                               📊 endgame: 4-6 turns, 90% response chance, 2800ms intervals
03:50:59.976                                                             🔬 Running spectator mode simulation:
03:50:59.976                                                             🔬 SIMULATING CONVERSATION FLOW:
03:50:59.976                                                             🎯 Trigger: opening
03:50:59.976                                                             📋 Schema: 4-6 turns, 90.0% response chance
03:50:59.976                                                               Turn 1: ✅ CONTINUE (Current: 1/4-6)
03:50:59.976                                                               Turn 2: ✅ CONTINUE (Current: 2/4-6)
03:50:59.976                                                               Turn 3: ✅ CONTINUE (Current: 3/4-6)
03:50:59.976                                                               Turn 4: ✅ CONTINUE (Current: 4/4-6)
03:50:59.976                                                               Turn 5: ✅ CONTINUE (Current: 5/4-6)
03:50:59.977                                                               Turn 6: 🛑 STOP (Current: 6/4-6)
03:50:59.977                                                             🏁 Conversation ended after 6 turns
03:50:59.977                                                             🔬 SIMULATING CONVERSATION FLOW:
03:50:59.977                                                             🎯 Trigger: position_change
03:50:59.977                                                             📋 Schema: 4-6 turns, 90.0% response chance
03:50:59.977                                                               Turn 1: ✅ CONTINUE (Current: 1/4-6)
03:50:59.977                                                               Turn 2: ✅ CONTINUE (Current: 2/4-6)
03:50:59.977                                                               Turn 3: ✅ CONTINUE (Current: 3/4-6)
03:50:59.977                                                               Turn 4: 🛑 STOP (Current: 4/4-6)
03:50:59.977                                                             🏁 Conversation ended after 4 turns
03:50:59.977                                                             💡 SPECTATOR MODE RECOMMENDATIONS:
03:50:59.977                                                               🎯 Use 'engaging' template for balanced multi-turn dialogues
03:50:59.977                                                               🎯 Opening conversations: 3-6 turns (current spectator games start strong)
03:50:59.977                                                               🎯 Position analysis: 4-8 turns (masters debate moves actively)
03:50:59.977                                                               🎯 85% response rate (masters usually engage with each other)
03:50:59.977                                                               🎯 2.5s intervals (natural conversation pacing)
03:50:59.977 Conversati...maTemplate                                     🎭 CONVERSATION TEMPLATE CHANGED: Engaging Chess Dialogue (Intensity: ENGAGING, Style: DRAMATIC)
03:50:59.977                                                             🔧 TESTING: Engaging conversations (recommended for spectator mode)
03:50:59.977 ConversationFlowTester                                      🎭 CONVERSATION MODE: Engaging (3-6 opening turns, 4-8 analysis turns, 85% response rate)
03:50:59.977                                                             📊 🎭 CURRENT: Engaging Chess Dialogue | Opening: 4-6 turns (90% chance) | Analysis: 4-6 turns (90% chance) | Style: DRAMATIC
03:50:59.977                                                             ✅ APPLIED: Engaging conversation template for spectator mode
03:50:59.978 🎭 EmotionalMomentum                                        🚀 Emotional Momentum Manager initialized
03:50:59.978 🎭 Phase2Bridge                                             🚀 Phase 2 Emotional Integration Bridge initialized with momentum system
03:50:59.978 ResponsesA...tionHelper                                     Responses API for tal: enabled
03:50:59.978                                                             Responses API for fischer: enabled
03:50:59.978                                                             Responses API for carlsen: enabled
03:50:59.978                                                             Responses API for anand: enabled
03:50:59.978                                                             Responses API for alekhine: enabled
03:50:59.978                                                             Responses API for kasparov: enabled
03:50:59.978                                                             🔧 FORCE ENABLED Responses API for the 6 working masters: Tal, Fischer, Carlsen, Anand, Alekhine, Kasparov
03:50:59.978                                                             📋 Other masters (Kramnik, Karpov, etc.) will use fallback dialogue
03:50:59.978                                                             🔄 Integration helper initialized. Responses API enabled: true
03:50:59.978                                                             🎯 Masters with Responses API enabled: {anand=true, kasparov=true, alekhine=true, fischer=true, carlsen=true, tal=true}
03:50:59.978                                                             📋 Master 'anand' → Responses API: true
03:50:59.978                                                             📋 Master 'kasparov' → Responses API: true
03:50:59.978                                                             📋 Master 'alekhine' → Responses API: true
03:50:59.978                                                             📋 Master 'fischer' → Responses API: true
03:50:59.978                                                             📋 Master 'carlsen' → Responses API: true
03:50:59.978                                                             📋 Master 'tal' → Responses API: true
03:51:00.051 GameViewModel                                               🔍 Requesting position evaluation...
03:51:00.051                                                             🔄 Skipping evaluation - too soon since last request
03:51:00.051                                                             ✅ Game initialized with white vs 1750 Elo engine
03:51:00.056 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.initializeGameWithConfiguration:1101 
03:51:00.057                                                             show: isDexDualMode = false
03:51:00.057                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
03:51:00.060 🔗 LiveMonitorClient                                        Built server URL: ws://192.168.0.237:8080
03:51:00.061                                                             LiveMonitorClient initialized with server URL: ws://192.168.0.237:8080
03:51:00.061                                                             🚫 Live monitor disabled via ENABLE_LIVE_MONITOR flag - skipping connection
03:51:00.064 VoiceControlManager                                         🎤 VoiceControlManager initialized
03:51:00.064                                                             📋 Registered voice command listener: main_game
03:51:00.066 UserProfileManager                                          👤 Loaded user profile: Mr Ben Lockrey
03:51:00.066                                                             🎭 Integrating user profile with emotional intelligence systems...
03:51:00.086                                                             ✅ User successfully integrated into AI emotional ecosystem
03:51:00.090 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:51:00.090                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:51:00.090                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:51:00.090 ApiKeyConfig                                                Using API key from ApiKeys class
03:51:00.090 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:51:00.090                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:51:00.090                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:51:00.090 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
03:51:00.090 GameViewModel                                               🎭 CONFIGURING PERSONALITY ENGINE: alekhine (weight=1.00, enabled=true)
03:51:00.095 VoiceControlManager                                         📋 Registered voice command listener: main_game
03:51:00.097 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
03:51:00.101 Choreographer                                               Skipped 80 frames!  The application may be doing too much work on its main thread.
03:51:00.122 BufferQueueProducer                                         [](id:55900000001,api:0,p:0,c:1369) setDequeueTimeout:2077252342
03:51:00.123 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
03:51:00.123                                                             Access denied finding property "vendor.gpp.create_frc_extension"
03:51:00.134 ScrollView                                                   onsize change changed 
03:51:00.145 ChessSetManager                                             ✅ Initialized 2 chess sets
03:51:00.145                                                             🎨 Chess Set Manager initialized with set: staunton_classic
03:51:00.177 BLASTBufferQueue                                            [VRI[MainActivity]@7eff861#1](f:0,a:0,s:0) onFrameAvailable the first frame is available
03:51:00.177 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 34755, bufferData(ID: 5879810228231, frameNumber: 1)
03:51:00.177 HWUI                                                        CFMS:: SetUp Pid : 1369    Tid : 1470
03:51:00.186 ApiKeyConfig                                                Using API key from ApiKeys class
03:51:00.186                                                             Using API key from ApiKeys class
03:51:00.186 OpenAIService                                               API key set, length: 164
03:51:00.186 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:51:00.186                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:51:00.186                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:51:00.187                                                             ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:51:00.187                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:51:00.187                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:51:00.187 ThreeStage...nseManager                                     🚀 Enhanced ThreeStageResponseManager initialized with Responses API integration!
03:51:00.187                                                             🔍 Integration Status Check:
03:51:00.187 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: fischer
03:51:00.188                                                             🎯 Master fischer Responses API enabled: true
03:51:00.188 ThreeStage...nseManager                                     🎯 Fischer eligible: true
03:51:00.188 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: tal
03:51:00.188                                                             🎯 Master tal Responses API enabled: true
03:51:00.188 ThreeStage...nseManager                                     🎯 Tal eligible: true
03:51:00.188 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: carlsen
03:51:00.188                                                             🎯 Master carlsen Responses API enabled: true
03:51:00.188 ThreeStage...nseManager                                     🎯 Carlsen eligible: true
03:51:00.189 HWUI                                                        Davey! duration=750ms; Flags=1, FrameTimelineVsyncId=67305333, IntendedVsync=383835059304810, Vsync=383835725747050, InputEventId=0, HandleInputStart=383835730668525, AnimationStart=383835730669410, PerformTraversalsStart=383835730809879, DrawStart=383835807600556, FrameDeadline=383835067638143, FrameInterval=383835730492171, FrameStartTime=8330528, SyncQueued=383835807618890, SyncStart=383835807680504, IssueDrawCommandsStart=383835807728108, SwapBuffers=383835808291754, FrameCompleted=383835809460192, DequeueBufferDuration=9427, QueueBufferDuration=172292, GpuCompleted=383835809460192, SwapBuffersCompleted=383835808770504, DisplayPresentTime=0, CommandSubmissionCompleted=383835808291754, 
03:51:00.189                                                             Davey! duration=749ms; Flags=1, FrameTimelineVsyncId=67305333, IntendedVsync=383835059304810, Vsync=383835725747050, InputEventId=0, HandleInputStart=383835730668525, AnimationStart=383835730669410, PerformTraversalsStart=383835730809879, DrawStart=383835767016754, FrameDeadline=383835067638143, FrameInterval=383835730492171, FrameStartTime=8330528, SyncQueued=383835781765765, SyncStart=383835781890244, IssueDrawCommandsStart=383835782044515, SwapBuffers=383835805845660, FrameCompleted=383835808782327, DequeueBufferDuration=7500, QueueBufferDuration=343854, GpuCompleted=383835808782327, SwapBuffersCompleted=383835806707067, DisplayPresentTime=0, CommandSubmissionCompleted=383835805845660, 
03:51:00.190 OpenAIService                                               API key set, length: 164
03:51:00.190                                                             API key set, length: 164
03:51:00.190 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:51:00.190                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:51:00.190                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:51:00.191 ConversationManager                                         Added new system message
03:51:00.191                                                             ConversationManager initialized with session: session_1750301460191
03:51:00.204                                                             Added new system message
03:51:00.204                                                             ✅ Started new conversation with session: session_1750301460204
03:51:00.212 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
03:51:00.226 InputMethodManagerUtils                                     startInputInner - Id : 0
03:51:00.226 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
03:51:00.227 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:51:00.227                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:51:00.227                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:51:00.227 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
03:51:00.227 GameViewModel                                               🎭 CONFIGURING PERSONALITY ENGINE: alekhine (weight=1.00, enabled=true)
03:51:00.228 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:51:00.228                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:51:00.228                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:51:00.228 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
03:51:00.665 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@ab4ebd2
03:51:00.679 GameViewModel                                               ✅ Evaluation received: 0.29
03:51:02.743 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:51:02.743                                                             📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:52:21.546 Dialog                                                      mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
03:52:21.560 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@a6f2edd
03:52:21.569 ScrollView                                                  initGoToTop
03:52:21.581 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{e776d49 V.E...... R.....I. 0,0-0,0}[MainActivity], caller=android.view.WindowManagerImpl.addView:158 android.app.Dialog.show:511 com.example.chesspedagogue.MainActivity.launchCompetitiveMode:3146 
03:52:21.583 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
03:52:21.587 VRI[MainAc...y]@b9bf64e                                     synced displayState. AttachInfo displayState=2
03:52:21.588                                                             setView = com.android.internal.policy.DecorView@e776d49 IsHRR=false TM=true
03:52:21.620 BufferQueueProducer                                         [](id:55900000002,api:0,p:16777224,c:1369) setDequeueTimeout:2077252342
03:52:21.621 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
03:52:21.621                                                             Access denied finding property "vendor.gpp.create_frc_extension"
03:52:21.621 VRI[MainAc...y]@b9bf64e                                     Relayout returned: old=(0,100,1440,2908) new=(36,165,1404,2843) relayoutAsync=false req=(1368,2678)0 dur=5 res=0x3 s={true 0xb400007199d01000} ch=true seqId=0
03:52:21.621                                                             performConfigurationChange setNightDimText nightDimLevel=0
03:52:21.622                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb400007199d01000} hwInitialized=true
03:52:21.634 AbsListView                                                  in onLayout changed 
03:52:21.635 ScrollView                                                   onsize change changed 
03:52:21.635 VRI[MainAc...y]@b9bf64e                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
03:52:21.635                                                             Setup new sync=wmsSync-VRI[MainActivity]@b9bf64e#4
03:52:21.635                                                             Creating new active sync group VRI[MainActivity]@b9bf64e#5
03:52:21.635                                                             registerCallbacksForSync syncBuffer=false
03:52:21.638                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
03:52:21.639                                                             mWNT: t=0xb4000071ed1c7500 mBlastBufferQueue=0xb4000071ecffa980 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
03:52:21.639                                                             Setting up sync and frameCommitCallback
03:52:21.642 BLASTBufferQueue                                            [VRI[MainActivity]@b9bf64e#2](f:0,a:0,s:0) onFrameAvailable the first frame is available
03:52:21.642 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 34768, bufferData(ID: 5879810228235, frameNumber: 1)
03:52:21.642 VRI[MainAc...y]@b9bf64e                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
03:52:21.642 HWUI                                                        CFMS:: SetUp Pid : 1369    Tid : 1470
03:52:21.643 VRI[MainAc...y]@b9bf64e                                     reportDrawFinished seqId=0
03:52:21.644 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
03:52:21.672 VRI[MainAc...y]@b9bf64e                                     mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb400007199d01000}
03:52:23.175                                                             ViewPostIme pointer 0
03:52:23.176                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[MainActivity]@b9bf64e
03:52:23.292                                                             ViewPostIme pointer 1
03:52:23.292 AbsListView                                                 onTouchUp() mTouchMode : 2
03:52:23.309 WindowManager                                               WindowManagerGlobal#removeView, ty=2, view=com.android.internal.policy.DecorView{e776d49 V.E...... R....... 0,0-1368,2678}[MainActivity], caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.app.Dialog.dismissDialog:808 
03:52:23.309 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@4c5295a
03:52:23.313 HWUI                                                        endAllActiveAnimators on 0xb400007199bcea00 (AlertController$RecycleListView) with handle 0xb40000723058e3a0
03:52:23.313 VRI[MainAc...y]@b9bf64e                                     dispatchDetachedFromWindow
03:52:23.320 InputEventReceiver                                          Attempted to finish an input event but the input event receiver has already been disposed.
03:52:23.322 VoiceControlManager                                         📋 Unregistered voice command listener
03:52:23.327 ActivityThread                                              com.example.chesspedagogue will use render engine as VK
03:52:23.335 CompetitiveModeActivity                                     🏆 Starting competitive mode against chess master...
03:52:23.337 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@12d140
03:52:23.360 ScrollView                                                  initGoToTop
03:52:23.364 CompetitiveModeActivity                                     🔄 Synchronizing master selection: alekhine
03:52:23.364 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:52:23.364 CompetitiveModeActivity                                     🔍 Master sync verification:
03:52:23.364                                                               - Voice system (ChessFineTunedModels): alekhine
03:52:23.364                                                               - App system (ChessAppPrefs): alekhine
03:52:23.364                                                               - FineTunedModelManager: alekhine
03:52:23.364                                                             ✅ Master synchronization SUCCESS! All systems consistent.
03:52:23.364                                                             🎯 Competitive config - Master: alekhine, Color: white, Skill: 10, ELO: 1750
03:52:23.364                                                             ✅ Master synchronized across all SharedPreferences stores
03:52:23.365                                                             🎨 Initializing views...
03:52:23.365                                                             ✅ Dynamic master elements set for: alekhine
03:52:23.366                                                             ✅ Voice status indicator initialized
03:52:23.366                                                             ✅ Views initialized
03:52:23.366                                                             🎭 Initializing emotional intelligence systems...
03:52:23.366                                                             🧠 Initializing EQ system with historical emotional data...
03:52:23.369 EmotionalIntelligence                                       🧠 Loaded emotional history for alekhine vs player: 0 events, momentum: 0.25
03:52:23.369 CompetitiveModeActivity                                     ✅ EQ system initialized - emotional reactions and relationships should now work!
03:52:23.371 EmotionalS...egyLearner                                     🧠 Loaded 0 master strategy profiles from database
03:52:23.371                                                             🧠🎯 EmotionalStrategyLearner initialized - Ready for adaptive learning!
03:52:23.371 CrossMaste...ectiveness                                     ✅ Loaded global effectiveness statistics
03:52:23.372 CompetitiveModeActivity                                     🧠 Phase 3: Adaptive conversation strategy manager initialized
03:52:23.372                                                             ✅ Emotional intelligence systems initialized
03:52:23.372                                                             🎲 Initializing personality engine for alekhine...
03:52:23.372 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:52:23.372 DynamicRelationship                                         ✅ Loaded existing relationship dynamics
03:52:23.372 CompetitiveModeActivity                                     🎲 Creating PersonalityEngine instance...
03:52:23.372                                                             🎯 Setting current master to: alekhine
03:52:23.372                                                             🚀 CALLING initializeMasterData for: alekhine
03:52:23.372                                                             🎭 Enabling personality play...
03:52:23.374                                                             ⚖️ Using splash difficulty - ELO: 1750, Skill: 10
03:52:23.374                                                             ✅ Personality engine initialized for alekhine
03:52:23.374                                                             🔍 Running personality engine database diagnostic...
03:52:23.374 PersonalityDiagnostic                                       🔍 DIAGNOSING PERSONALITY ENGINE DATABASE FOR: alekhine
03:52:23.376                                                             📊 Master 'alekhine' - hasData: true, positions: 16368
03:52:23.376                                                             ✅ Data already exists - testing sample query...
03:52:23.376                                                             🧪 Testing sample query for alekhine...
03:52:23.377                                                             🧪 Sample query returned 5 results
03:52:23.377                                                             ✅ DATABASE QUERY WORKING! Sample position found:
03:52:23.377                                                                Master: alekhine
03:52:23.377                                                                FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq
03:52:23.377                                                                Opponent: Frank Marshall
03:52:23.380                                                             📊 Complete database stats: {anand=1870, kramnik=6707, kasparov=5010, alekhine=16368, fischer=5767, karpov=1866, capablanca=6051, carlsen=5739, total_positions=54156, tal=4778}
03:52:23.380 CompetitiveModeActivity                                     🔬 Running comprehensive PersonalityEngine system diagnostic...
03:52:23.380 PersonalitySystemDiag                                       🔬 STARTING COMPREHENSIVE PERSONALITY ENGINE DIAGNOSTIC
03:52:23.380                                                             📋 Test Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
03:52:23.380                                                             🎭 Master: alekhine (Target ELO: 1750)
03:52:23.380                                                             🔍 Phase 1: Database Analysis
03:52:23.381                                                             📊 Total alekhine positions in DB: 16368
03:52:23.383                                                             🎯 Exact FEN matches: 50
03:52:23.383                                                             🔍 Board pattern: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
03:52:23.383                                                             📋 Sample matched games: null
03:52:23.383                                                             🤖 Phase 2: AI Integration Analysis
03:52:23.383                                                             🤖 Assistant available for alekhine: true
03:52:23.383                                                             📚 Vector store connected: false
03:52:23.383                                                             🧠 AI analysis capability: Available
03:52:23.383                                                             🎨 Phase 3: Style Application Analysis
03:52:23.383                                                             ♟️ Stockfish candidates: null
03:52:23.384                                                             🎭 Personality weight: 0.0
03:52:23.384                                                             📊 Phase 4: Quality Assessment
03:52:23.384                                                             📊 Quality assessment complete - Score: 0.29999998
03:52:23.384                                                             ✅ DIAGNOSTIC COMPLETE
03:52:23.384                                                             === PersonalityEngine System Diagnostic ===
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
03:52:23.384 CompetitiveModeActivity                                     📊 System diagnostic result: === PersonalityEngine System Diagnostic ===
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
03:52:23.384 PersonalitySystemDiag                                       🔍 QUICK NAME DIAGNOSTIC
03:52:23.388                                                             📊 Actual master names in database:
03:52:23.388                                                                anand: 1870 positions
03:52:23.388                                                                kramnik: 6707 positions
03:52:23.388                                                                kasparov: 5010 positions
03:52:23.388                                                                alekhine: 16368 positions
03:52:23.388                                                                fischer: 5767 positions
03:52:23.388                                                                karpov: 1866 positions
03:52:23.388                                                                capablanca: 6051 positions
03:52:23.388                                                                carlsen: 5739 positions
03:52:23.388                                                                total_positions: 54156 positions
03:52:23.388                                                                tal: 4778 positions
03:52:23.388 CompetitiveModeActivity                                     🎤 Initializing voice services...
03:52:23.388 VoiceControlManager                                         📋 Registered voice command listener: competitive_mode
03:52:23.389 CompetitiveModeActivity                                     🗣️ Updating voice for master: alekhine
03:52:23.389 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:52:23.389 CompetitiveModeActivity                                     🔍 FineTunedModelManager master: alekhine
03:52:23.389                                                             🔍 SharedPreferences master: alekhine
03:52:23.389 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:52:23.389                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:52:23.389                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:52:23.389 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
03:52:23.389 TTSServiceManager                                           ✅ Usage context set to: competitive_mode
03:52:23.389 CompetitiveModeActivity                                     ✅ Final verification - Voice system master: alekhine
03:52:23.389                                                             ✅ Voice services initializing...
03:52:23.470 WindowManager           system_server                       win=Window{52a1115 u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity EXITING} destroySurfaces: appStopped=false cleanupOnResume=false win.mWindowRemovalAllowed=true win.mRemoveOnExit=true win.mViewVisibility=0 caller=com.android.server.wm.WindowState.onExitAnimationDone:222 com.android.server.wm.WindowState.onAnimationFinished:161 com.android.server.wm.WindowContainer$$ExternalSyntheticLambda5.onAnimationFinished:26 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda1.run:28 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda0.onAnimationFinished:65 com.android.server.wm.LocalAnimationAdapter$$ExternalSyntheticLambda0.run:10 android.os.Handler.handleCallback:959 
03:52:23.693 GameViewModel           com.example.chesspedagogue          🎭 Initializing personality LiveData...
03:52:23.693                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
03:52:23.693                                                             🔍 Requesting position evaluation...
03:52:23.693 CompetitiveModeActivity                                     🎭 Configuring GameRepository personality engine for alekhine
03:52:23.696                                                             ✅ GameRepository personality engine configured successfully!
03:52:23.696                                                             🎯 Master: alekhine, Weight: 0.3, Enabled: true
03:52:23.696                                                             🔍 Personality engine availability check: true
03:52:23.696                                                             ✅ CONFIRMED: GameRepository personality engine is properly configured and available!
03:52:23.696                                                             ✅ Game view model configured for competitive mode
03:52:23.696                                                             👀 Setting up observers...
03:52:23.696                                                             🎯 Setting up chess board interaction...
03:52:23.696                                                             ✅ Chess board interaction setup complete
03:52:23.696                                                             ✅ Observers setup complete
03:52:23.696                                                             🎮 Setting up controls...
03:52:23.698                                                             ✅ Controls setup complete
03:52:23.698                                                             ✅ All competitive mode systems initialized!
03:52:23.698                                                             🏁 Starting competitive game vs alekhine
03:52:23.698 GameViewModel                                               🎮 Starting new game with configuration: white, skill=10, elo=1750
03:52:23.847 GameHistoryManager                                          Game history cleared
03:52:23.948 GameViewModel                                               🔍 Requesting position evaluation...
03:52:23.948                                                             🔄 Skipping evaluation - too soon since last request
03:52:23.948                                                             ✅ Game initialized with white vs 1750 Elo engine
03:52:23.948 AdaptiveStrategy                                            🔍 Started monitoring conversation: competitive_1750301543948 (alekhine vs player)
03:52:23.948 CompetitiveModeActivity                                     🧠 Phase 3: Started adaptive conversation monitoring for alekhine
03:52:23.949 RelationshipPersistence                                     👥 Retrieved relationship: alekhine <-> player (respect: 0.50, rivalry: 0.00, friendship: 0.00)
03:52:23.949 EmotionalS...egyLearner                                     🧠 Started learning session: alekhine vs player
03:52:23.949                                                             🔍 EXPLORATION: alekhine trying supportive approach vs player
03:52:23.950 CrossMaste...ectiveness                                     🎓 Generated 0 cross-learning recommendations for alekhine vs player
03:52:23.950 AdaptiveStrategy                                            🎯 Optimal strategy for alekhine vs player: 'supportive' (confidence: 0.20)
03:52:23.950 CompetitiveModeActivity                                     🧠 Phase 3: Using adaptive strategy 'supportive' (confidence: 0.20) for greeting - Individual learning: EXPLORATION: Testing new approach 'supportive' to learn effectiveness; 
03:52:23.951                                                             🗣️ Speaking master dialogue: Alexander Alekhine at your service. Prepare for a combinatorial masterclass!
03:52:23.951 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:52:23.951                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:52:23.951                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:52:23.951                                                             ✅ Usage context set to: competitive_mode
03:52:23.951 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:52:23.951 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:52:23.951                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:52:23.953 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:52:23.953                                                             🎭 Updated emotion indicator: 😎
03:52:23.953                                                             ✅ Competitive game started successfully!
03:52:23.955                                                             🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
03:52:23.956                                                             📊 Evaluation updated: 0.0
03:52:23.956                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
03:52:23.956                                                             🎯 First emotional evaluation: 0.0 (threshold: 1.5)
03:52:23.956                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301543s/25s)
03:52:23.956                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:52:23.956                                                             📜 Move history updated: 0 moves
03:52:23.958 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
03:52:23.961 VRI[Compet...y]@bee13b0                                     synced displayState. AttachInfo displayState=2
03:52:23.962                                                             setView = com.android.internal.policy.DecorView@8f5921c IsHRR=false TM=true
03:52:23.962 Choreographer                                               Skipped 76 frames!  The application may be doing too much work on its main thread.
03:52:23.986 BufferQueueProducer                                         [](id:55900000003,api:0,p:0,c:1369) setDequeueTimeout:2077252342
03:52:23.987 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
03:52:23.987                                                             Access denied finding property "vendor.gpp.create_frc_extension"
03:52:23.987 VRI[Compet...y]@bee13b0                                     Relayout returned: old=(0,0,1440,3088) new=(0,0,1440,3088) relayoutAsync=false req=(1440,3088)0 dur=5 res=0x3 s={true 0xb400007226a6e800} ch=true seqId=0
03:52:23.987                                                             performConfigurationChange setNightDimText nightDimLevel=0
03:52:23.987                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb400007226a6e800} hwInitialized=true
03:52:23.992 ScrollView                                                   onsize change changed 
03:52:23.993 VRI[Compet...y]@bee13b0                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
03:52:23.993                                                             Setup new sync=wmsSync-VRI[CompetitiveModeActivity]@bee13b0#6
03:52:23.993                                                             Creating new active sync group VRI[CompetitiveModeActivity]@bee13b0#7
03:52:23.993                                                             registerCallbacksForSync syncBuffer=false
03:52:24.021                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
03:52:24.021                                                             mWNT: t=0xb400007226aa4e80 mBlastBufferQueue=0xb4000070f627cb80 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
03:52:24.021                                                             Setting up sync and frameCommitCallback
03:52:24.038 BLASTBufferQueue                                            [VRI[CompetitiveModeActivity]@bee13b0#3](f:0,a:0,s:0) onFrameAvailable the first frame is available
03:52:24.038 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 34776, bufferData(ID: 5879810228239, frameNumber: 1)
03:52:24.038 VRI[Compet...y]@bee13b0                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
03:52:24.039 HWUI                                                        CFMS:: SetUp Pid : 1369    Tid : 1470
03:52:24.039 VRI[Compet...y]@bee13b0                                     reportDrawFinished seqId=0
03:52:24.039 CompetitiveModeActivity                                     🎤 Voice service connected to competitive mode
03:52:24.040 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
03:52:24.053                                                             Davey! duration=712ms; Flags=1, FrameTimelineVsyncId=67310402, IntendedVsync=383918957006439, Vsync=383919591530591, InputEventId=0, HandleInputStart=383919591931931, AnimationStart=383919591932712, PerformTraversalsStart=383919591996149, DrawStart=383919623128701, FrameDeadline=383918965339772, FrameInterval=383919591789951, FrameStartTime=8349002, SyncQueued=383919650362764, SyncStart=383919650449483, IssueDrawCommandsStart=383919650617451, SwapBuffers=383919667008128, FrameCompleted=383919669669170, DequeueBufferDuration=10208, QueueBufferDuration=309323, GpuCompleted=383919669669170, SwapBuffersCompleted=383919667890420, DisplayPresentTime=0, CommandSubmissionCompleted=383919667008128, 
03:52:24.068 VRI[Compet...y]@bee13b0                                     mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb400007226a6e800}
03:52:24.068 InputMethodManagerUtils                                     startInputInner - Id : 0
03:52:24.068 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
03:52:24.539 WindowManager           system_server                       win=Window{bf733dc u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:204 com.android.server.wm.ActivityClientController.activityStopped:95 android.app.IActivityClientController$Stub.onTransact:722 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1541 android.os.Binder.execTransact:1480 
03:52:24.543 GameViewModel           com.example.chesspedagogue          ✅ Evaluation received: 0.35
03:52:24.644 CompetitiveModeActivity                                     📊 Evaluation updated: 0.35
03:52:24.644                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.35, emotionalManager: INITIALIZED
03:52:24.644                                                             🎯 First emotional evaluation: 0.35 (threshold: 1.5)
03:52:24.644                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301544s/25s)
03:52:24.644                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:52:25.008 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:52:27.474 VRI[Compet...y]@bee13b0                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
03:52:29.310 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
03:52:34.018 VRI[Compet...y]@bee13b0                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
03:52:37.485                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:53:27.592                                                             ViewPostIme pointer 0
03:53:27.593 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=2
03:53:27.593                                                             🎯 SQUARE TAPPED: row=6, col=2
03:53:27.593                                                             📝 Player color: white
03:53:27.593                                                             🔍 Selected row/col: -1/-1
03:53:27.951                                                             🎯 Selected piece: P at 6, 2
03:53:27.951 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:53:27.952                                                             ViewPostIme pointer 1
03:53:27.952                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:53:28.398                                                             ViewPostIme pointer 0
03:53:28.398 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
03:53:28.398                                                             🎯 SQUARE TAPPED: row=4, col=2
03:53:28.398                                                             📝 Player color: white
03:53:28.398                                                             🔍 Selected row/col: 6/2
03:53:28.399                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
03:53:28.399                                                             🎯 ATTEMPTING MOVE: c2c4
03:53:28.399                                                             📝 Player color: white
03:53:28.399                                                             🔄 Is player's turn: true
03:53:28.399                                                             🔄 Is white's turn: true
03:53:28.399                                                             ✅ Turn validation passed, making move: c2c4
03:53:28.399 GameViewModel                                               🎯 makePlayerMove called with: c2c4
03:53:28.450                                                             🔍 Validating move: c2c4 (attempt 1)
03:53:28.551                                                             📋 Current position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
03:53:28.602                                                             ⚖️ Move c2c4 legality check: LEGAL
03:53:28.603                                                             ✅ Executing validated move: c2c4
03:53:28.805                                                             📍 New position after move: rnbqkbnr/pppppppp/8/8/2P5/8/PP1PPPPP/RNBQKBNR b KQkq - 0 1
03:53:28.807 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:53:28.809 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/2P5/8/PP1PPPPP/RNBQKBNR b KQkq - 0 1
03:53:28.812                                                             📜 Move history updated: 1 moves
03:53:28.812 GameHistoryManager                                          Move added: c2c4
03:53:28.906 GameViewModel                                               🔍 Requesting position evaluation...
03:53:28.906                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:53:28.906                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:53:28.906                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:53:28.907                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:53:28.907                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:53:28.907                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:53:28.907                                                             🔧 gameRepository class: GameRepository
03:53:28.907                                                             🔧 Current thread: main
03:53:28.907 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:53:29.460 GameViewModel                                               ✅ Evaluation received: -0.22
03:53:29.563 CompetitiveModeActivity                                     📊 Evaluation updated: -0.22
03:53:29.563                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.22, emotionalManager: INITIALIZED
03:53:29.563                                                             🎯 First emotional evaluation: -0.22 (threshold: 1.5)
03:53:29.563                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301609s/25s)
03:53:29.563                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:53:30.346 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:53:30.360 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7e5
03:53:30.719 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppp1ppp/8/4p3/2P5/8/PP1PPPPP/RNBQKBNR w KQkq - 0 2
03:53:30.722                                                             📜 Move history updated: 2 moves
03:53:30.722 GameHistoryManager                                          Move added: e7e5
03:53:30.722 GameViewModel                                               🔍 Requesting position evaluation...
03:53:30.722                                                             🎭 Updating personality context for move: e7e5
03:53:30.723                                                             ✨ Personality context updated for move e7e5 - This is revolutionary!
03:53:30.723                                                             🔍 Checking game end conditions...
03:53:30.824                                                             ✅ Game continues - no end condition detected
03:53:31.231                                                             ✅ Evaluation received: 0.22
03:53:31.334 CompetitiveModeActivity                                     📊 Evaluation updated: 0.22
03:53:31.334                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.22, emotionalManager: INITIALIZED
03:53:31.334                                                             🎯 First emotional evaluation: 0.22 (threshold: 1.5)
03:53:31.334                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301611s/25s)
03:53:31.334                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:53:31.807 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:53:38.334                                                             ViewPostIme pointer 0
03:53:38.334 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=1
03:53:38.334                                                             🎯 SQUARE TAPPED: row=7, col=1
03:53:38.334                                                             📝 Player color: white
03:53:38.334                                                             🔍 Selected row/col: -1/-1
03:53:38.691                                                             🎯 Selected piece: N at 7, 1
03:53:38.691 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:53:38.692                                                             ViewPostIme pointer 1
03:53:39.037                                                             ViewPostIme pointer 0
03:53:39.039 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
03:53:39.039                                                             🎯 SQUARE TAPPED: row=5, col=2
03:53:39.039                                                             📝 Player color: white
03:53:39.039                                                             🔍 Selected row/col: 7/1
03:53:39.039                                                             🎯 ATTEMPTING MOVE: b1c3
03:53:39.039                                                             📝 Player color: white
03:53:39.039                                                             🔄 Is player's turn: true
03:53:39.039                                                             🔄 Is white's turn: true
03:53:39.039                                                             ✅ Turn validation passed, making move: b1c3
03:53:39.039 GameViewModel                                               🎯 makePlayerMove called with: b1c3
03:53:39.090                                                             🔍 Validating move: b1c3 (attempt 1)
03:53:39.190                                                             📋 Current position: rnbqkbnr/pppp1ppp/8/4p3/2P5/8/PP1PPPPP/RNBQKBNR w KQkq - 0 2
03:53:39.242                                                             ⚖️ Move b1c3 legality check: LEGAL
03:53:39.242                                                             ✅ Executing validated move: b1c3
03:53:39.444                                                             📍 New position after move: rnbqkbnr/pppp1ppp/8/4p3/2P5/2N5/PP1PPPPP/R1BQKBNR b KQkq - 1 2
03:53:39.445 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:53:39.447 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppp1ppp/8/4p3/2P5/2N5/PP1PPPPP/R1BQKBNR b KQkq - 1 2
03:53:39.449                                                             📜 Move history updated: 3 moves
03:53:39.449 GameHistoryManager                                          Move added: b1c3
03:53:39.545 GameViewModel                                               🔍 Requesting position evaluation...
03:53:39.546                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:53:39.546                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:53:39.546                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:53:39.546                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:53:39.546                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:53:39.546                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:53:39.546                                                             🔧 gameRepository class: GameRepository
03:53:39.547                                                             🔧 Current thread: main
03:53:39.547 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:53:40.252 GameViewModel                                               ✅ Evaluation received: -0.16
03:53:40.361 CompetitiveModeActivity                                     📊 Evaluation updated: -0.16
03:53:40.361                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.16, emotionalManager: INITIALIZED
03:53:40.361                                                             🎯 First emotional evaluation: -0.16 (threshold: 1.5)
03:53:40.361                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301620s/25s)
03:53:40.361                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:53:41.194 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 8 historical positions
03:53:41.208 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h7h6
03:53:41.568 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppp1pp1/7p/4p3/2P5/2N5/PP1PPPPP/R1BQKBNR w KQkq - 0 3
03:53:41.571                                                             📜 Move history updated: 4 moves
03:53:41.571 GameHistoryManager                                          Move added: h7h6
03:53:41.571 GameViewModel                                               🔍 Requesting position evaluation...
03:53:41.572                                                             🎭 Updating personality context for move: h7h6
03:53:41.572                                                             ✨ Personality context updated for move h7h6 - This is revolutionary!
03:53:41.572                                                             🔍 Checking game end conditions...
03:53:41.674                                                             ✅ Game continues - no end condition detected
03:53:42.179                                                             ✅ Evaluation received: 0.41
03:53:42.283 CompetitiveModeActivity                                     📊 Evaluation updated: 0.41
03:53:42.283                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.41, emotionalManager: INITIALIZED
03:53:42.284                                                             🎯 First emotional evaluation: 0.41 (threshold: 1.5)
03:53:42.284                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301622s/25s)
03:53:42.284                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:53:42.446 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:53:47.991                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:53:50.863                                                             ViewPostIme pointer 0
03:53:50.863 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=6
03:53:50.863                                                             🎯 SQUARE TAPPED: row=7, col=6
03:53:50.863                                                             📝 Player color: white
03:53:50.863                                                             🔍 Selected row/col: -1/-1
03:53:51.221                                                             🎯 Selected piece: N at 7, 6
03:53:51.221 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:53:51.222                                                             ViewPostIme pointer 1
03:53:51.223                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:53:51.671                                                             ViewPostIme pointer 0
03:53:51.671 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
03:53:51.671                                                             🎯 SQUARE TAPPED: row=5, col=5
03:53:51.671                                                             📝 Player color: white
03:53:51.671                                                             🔍 Selected row/col: 7/6
03:53:51.671                                                             🎯 ATTEMPTING MOVE: g1f3
03:53:51.671                                                             📝 Player color: white
03:53:51.671                                                             🔄 Is player's turn: true
03:53:51.671                                                             🔄 Is white's turn: true
03:53:51.671                                                             ✅ Turn validation passed, making move: g1f3
03:53:51.671 GameViewModel                                               🎯 makePlayerMove called with: g1f3
03:53:51.672                                                             🔍 Validating move: g1f3 (attempt 1)
03:53:51.773                                                             📋 Current position: rnbqkbnr/pppp1pp1/7p/4p3/2P5/2N5/PP1PPPPP/R1BQKBNR w KQkq - 0 3
03:53:51.824                                                             ⚖️ Move g1f3 legality check: LEGAL
03:53:51.824                                                             ✅ Executing validated move: g1f3
03:53:52.028                                                             📍 New position after move: rnbqkbnr/pppp1pp1/7p/4p3/2P5/2N2N2/PP1PPPPP/R1BQKB1R b KQkq - 1 3
03:53:52.030 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:53:52.031 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppp1pp1/7p/4p3/2P5/2N2N2/PP1PPPPP/R1BQKB1R b KQkq - 1 3
03:53:52.034                                                             📜 Move history updated: 5 moves
03:53:52.035 GameHistoryManager                                          Move added: g1f3
03:53:52.129 GameViewModel                                               🔍 Requesting position evaluation...
03:53:52.129                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:53:52.129                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:53:52.129                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:53:52.129                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:53:52.129                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:53:52.129                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:53:52.129                                                             🔧 gameRepository class: GameRepository
03:53:52.129                                                             🔧 Current thread: main
03:53:52.129 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:53:52.838 GameViewModel                                               ✅ Evaluation received: -0.35
03:53:52.945 CompetitiveModeActivity                                     📊 Evaluation updated: -0.35
03:53:52.945                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.35, emotionalManager: INITIALIZED
03:53:52.945                                                             🎯 First emotional evaluation: -0.35 (threshold: 1.5)
03:53:52.945                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301632s/25s)
03:53:52.945                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:53:53.892 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:53:53.922 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8d6
03:53:54.283 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/pppp1pp1/3b3p/4p3/2P5/2N2N2/PP1PPPPP/R1BQKB1R w KQkq - 2 4
03:53:54.288                                                             📜 Move history updated: 6 moves
03:53:54.288 GameHistoryManager                                          Move added: f8d6
03:53:54.288 GameViewModel                                               🔍 Requesting position evaluation...
03:53:54.288                                                             🎭 Updating personality context for move: f8d6
03:53:54.289                                                             ✨ Personality context updated for move f8d6 - This is revolutionary!
03:53:54.289                                                             🔍 Checking game end conditions...
03:53:54.391                                                             ✅ Game continues - no end condition detected
03:53:54.995                                                             ✅ Evaluation received: 0.90
03:53:55.031 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:53:55.082                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:53:55.106 CompetitiveModeActivity                                     📊 Evaluation updated: 0.9
03:53:55.107                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.9, emotionalManager: INITIALIZED
03:53:55.107                                                             🎯 First emotional evaluation: 0.9 (threshold: 1.5)
03:53:55.107                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301635s/25s)
03:53:55.107                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:53:55.116 VRI[Compet...y]@bee13b0                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
03:53:55.202                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:53:55.208                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:53:57.591                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:54:05.303                                                             ViewPostIme pointer 0
03:54:05.304 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
03:54:05.304                                                             🎯 SQUARE TAPPED: row=6, col=4
03:54:05.304                                                             📝 Player color: white
03:54:05.304                                                             🔍 Selected row/col: -1/-1
03:54:05.661                                                             🎯 Selected piece: P at 6, 4
03:54:05.661 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:05.663                                                             ViewPostIme pointer 1
03:54:05.664                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:54:06.059                                                             ViewPostIme pointer 0
03:54:06.059 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=4
03:54:06.059                                                             🎯 SQUARE TAPPED: row=5, col=4
03:54:06.059                                                             📝 Player color: white
03:54:06.059                                                             🔍 Selected row/col: 6/4
03:54:06.059                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=5, reaches=false
03:54:06.059                                                             🎯 ATTEMPTING MOVE: e2e3
03:54:06.059                                                             📝 Player color: white
03:54:06.059                                                             🔄 Is player's turn: true
03:54:06.059                                                             🔄 Is white's turn: true
03:54:06.059                                                             ✅ Turn validation passed, making move: e2e3
03:54:06.059 GameViewModel                                               🎯 makePlayerMove called with: e2e3
03:54:06.110                                                             🔍 Validating move: e2e3 (attempt 1)
03:54:06.211                                                             📋 Current position: rnbqk1nr/pppp1pp1/3b3p/4p3/2P5/2N2N2/PP1PPPPP/R1BQKB1R w KQkq - 2 4
03:54:06.262                                                             ⚖️ Move e2e3 legality check: LEGAL
03:54:06.262                                                             ✅ Executing validated move: e2e3
03:54:06.465                                                             📍 New position after move: rnbqk1nr/pppp1pp1/3b3p/4p3/2P5/2N1PN2/PP1P1PPP/R1BQKB1R b KQkq - 0 4
03:54:06.466 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:54:06.467 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/pppp1pp1/3b3p/4p3/2P5/2N1PN2/PP1P1PPP/R1BQKB1R b KQkq - 0 4
03:54:06.470                                                             📜 Move history updated: 7 moves
03:54:06.470 GameHistoryManager                                          Move added: e2e3
03:54:06.565 GameViewModel                                               🔍 Requesting position evaluation...
03:54:06.565                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:54:06.565                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:54:06.565                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:54:06.565                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:54:06.565                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:54:06.565                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:54:06.565                                                             🔧 gameRepository class: GameRepository
03:54:06.566                                                             🔧 Current thread: main
03:54:06.566 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:54:07.276 GameViewModel                                               ✅ Evaluation received: -0.80
03:54:07.384 CompetitiveModeActivity                                     📊 Evaluation updated: -0.8
03:54:07.384                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.8, emotionalManager: INITIALIZED
03:54:07.384                                                             🎯 First emotional evaluation: -0.8 (threshold: 1.5)
03:54:07.384                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301647s/25s)
03:54:07.384                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:54:08.231 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:54:08.263 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a7a6
03:54:08.623 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/1ppp1pp1/p2b3p/4p3/2P5/2N1PN2/PP1P1PPP/R1BQKB1R w KQkq - 0 5
03:54:08.626                                                             📜 Move history updated: 8 moves
03:54:08.626 GameHistoryManager                                          Move added: a7a6
03:54:08.627 GameViewModel                                               🔍 Requesting position evaluation...
03:54:08.627                                                             🎭 Updating personality context for move: a7a6
03:54:08.627                                                             ✨ Personality context updated for move a7a6 - This is revolutionary!
03:54:08.627                                                             🔍 Checking game end conditions...
03:54:08.729                                                             ✅ Game continues - no end condition detected
03:54:09.334                                                             ✅ Evaluation received: 0.95
03:54:09.437 CompetitiveModeActivity                                     📊 Evaluation updated: 0.95
03:54:09.437                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.95, emotionalManager: INITIALIZED
03:54:09.437                                                             🎯 First emotional evaluation: 0.95 (threshold: 1.5)
03:54:09.437                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301649s/25s)
03:54:09.437                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:54:09.470 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:14.946                                                             ViewPostIme pointer 0
03:54:14.946 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=3
03:54:14.946                                                             🎯 SQUARE TAPPED: row=6, col=3
03:54:14.946                                                             📝 Player color: white
03:54:14.946                                                             🔍 Selected row/col: -1/-1
03:54:15.302                                                             🎯 Selected piece: P at 6, 3
03:54:15.302 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:15.303                                                             ViewPostIme pointer 1
03:54:15.620                                                             ViewPostIme pointer 0
03:54:15.620 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
03:54:15.620                                                             🎯 SQUARE TAPPED: row=4, col=3
03:54:15.620                                                             📝 Player color: white
03:54:15.620                                                             🔍 Selected row/col: 6/3
03:54:15.621                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
03:54:15.621                                                             🎯 ATTEMPTING MOVE: d2d4
03:54:15.621                                                             📝 Player color: white
03:54:15.621                                                             🔄 Is player's turn: true
03:54:15.621                                                             🔄 Is white's turn: true
03:54:15.621                                                             ✅ Turn validation passed, making move: d2d4
03:54:15.621 GameViewModel                                               🎯 makePlayerMove called with: d2d4
03:54:15.671                                                             🔍 Validating move: d2d4 (attempt 1)
03:54:15.772                                                             📋 Current position: rnbqk1nr/1ppp1pp1/p2b3p/4p3/2P5/2N1PN2/PP1P1PPP/R1BQKB1R w KQkq - 0 5
03:54:15.824                                                             ⚖️ Move d2d4 legality check: LEGAL
03:54:15.824                                                             ✅ Executing validated move: d2d4
03:54:16.026                                                             📍 New position after move: rnbqk1nr/1ppp1pp1/p2b3p/4p3/2PP4/2N1PN2/PP3PPP/R1BQKB1R b KQkq - 0 5
03:54:16.027 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:54:16.028 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/1ppp1pp1/p2b3p/4p3/2PP4/2N1PN2/PP3PPP/R1BQKB1R b KQkq - 0 5
03:54:16.032                                                             📜 Move history updated: 9 moves
03:54:16.032 GameHistoryManager                                          Move added: d2d4
03:54:16.130 GameViewModel                                               🔍 Requesting position evaluation...
03:54:16.130                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:54:16.131                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:54:16.131                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:54:16.131                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:54:16.131                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:54:16.131                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:54:16.131                                                             🔧 gameRepository class: GameRepository
03:54:16.131                                                             🔧 Current thread: main
03:54:16.131 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:54:16.788 GameViewModel                                               ✅ Evaluation received: -0.96
03:54:16.888 CompetitiveModeActivity                                     📊 Evaluation updated: -0.96
03:54:16.888                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.96, emotionalManager: INITIALIZED
03:54:16.888                                                             🎯 First emotional evaluation: -0.96 (threshold: 1.5)
03:54:16.888                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301656s/25s)
03:54:16.888                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:54:17.643 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:54:17.672 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e5d4
03:54:18.029 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/1ppp1pp1/p2b3p/8/2Pp4/2N1PN2/PP3PPP/R1BQKB1R w KQkq - 0 6
03:54:18.033                                                             📜 Move history updated: 10 moves
03:54:18.033 GameHistoryManager                                          Move added: e5d4
03:54:18.033 GameViewModel                                               🔍 Requesting position evaluation...
03:54:18.034                                                             🎭 Updating personality context for move: e5d4
03:54:18.034                                                             ✨ Personality context updated for move e5d4 - This is revolutionary!
03:54:18.034                                                             🔍 Checking game end conditions...
03:54:18.135                                                             ✅ Game continues - no end condition detected
03:54:18.737                                                             ✅ Evaluation received: 0.93
03:54:18.838 CompetitiveModeActivity                                     📊 Evaluation updated: 0.93
03:54:18.838                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.93, emotionalManager: INITIALIZED
03:54:18.838                                                             🎯 First emotional evaluation: 0.93 (threshold: 1.5)
03:54:18.838                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301658s/25s)
03:54:18.838                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:54:19.026 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:23.204                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:54:23.854                                                             ViewPostIme pointer 0
03:54:23.855 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=4
03:54:23.855                                                             🎯 SQUARE TAPPED: row=5, col=4
03:54:23.855                                                             📝 Player color: white
03:54:23.855                                                             🔍 Selected row/col: -1/-1
03:54:24.208                                                             🎯 Selected piece: P at 5, 4
03:54:24.208 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:24.208                                                             ViewPostIme pointer 1
03:54:24.209                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:54:24.549                                                             ViewPostIme pointer 0
03:54:24.549 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
03:54:24.549                                                             🎯 SQUARE TAPPED: row=4, col=3
03:54:24.549                                                             📝 Player color: white
03:54:24.549                                                             🔍 Selected row/col: 5/4
03:54:24.549                                                             ♟️ Promotion check: piece=P, fromRow=5, toRow=4, reaches=false
03:54:24.549                                                             🎯 ATTEMPTING MOVE: e3d4
03:54:24.549                                                             📝 Player color: white
03:54:24.549                                                             🔄 Is player's turn: true
03:54:24.549                                                             🔄 Is white's turn: true
03:54:24.550                                                             ✅ Turn validation passed, making move: e3d4
03:54:24.550 GameViewModel                                               🎯 makePlayerMove called with: e3d4
03:54:24.550                                                             🔍 Validating move: e3d4 (attempt 1)
03:54:24.651                                                             📋 Current position: rnbqk1nr/1ppp1pp1/p2b3p/8/2Pp4/2N1PN2/PP3PPP/R1BQKB1R w KQkq - 0 6
03:54:24.702                                                             ⚖️ Move e3d4 legality check: LEGAL
03:54:24.702                                                             ✅ Executing validated move: e3d4
03:54:24.905                                                             📍 New position after move: rnbqk1nr/1ppp1pp1/p2b3p/8/2PP4/2N2N2/PP3PPP/R1BQKB1R b KQkq - 0 6
03:54:24.906 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:54:24.908 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/1ppp1pp1/p2b3p/8/2PP4/2N2N2/PP3PPP/R1BQKB1R b KQkq - 0 6
03:54:24.911                                                             📜 Move history updated: 11 moves
03:54:24.912 GameHistoryManager                                          Move added: e3d4
03:54:25.009 GameViewModel                                               🔍 Requesting position evaluation...
03:54:25.009                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:54:25.009                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:54:25.009                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:54:25.010                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:54:25.010                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:54:25.010                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:54:25.010                                                             🔧 gameRepository class: GameRepository
03:54:25.010                                                             🔧 Current thread: main
03:54:25.010 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:54:25.717 GameViewModel                                               ✅ Evaluation received: -0.89
03:54:25.825 CompetitiveModeActivity                                     📊 Evaluation updated: -0.89
03:54:25.825                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.89, emotionalManager: INITIALIZED
03:54:25.825                                                             🎯 First emotional evaluation: -0.89 (threshold: 1.5)
03:54:25.825                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750301665s/25s)
03:54:25.825                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:54:26.781 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:54:26.811 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a6a5
03:54:27.172 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/1ppp1pp1/3b3p/p7/2PP4/2N2N2/PP3PPP/R1BQKB1R w KQkq - 0 7
03:54:27.178                                                             📜 Move history updated: 12 moves
03:54:27.178 GameHistoryManager                                          Move added: a6a5
03:54:27.178 GameViewModel                                               🔍 Requesting position evaluation...
03:54:27.178                                                             🎭 Updating personality context for move: a6a5
03:54:27.179                                                             ✨ Personality context updated for move a6a5 - This is revolutionary!
03:54:27.179                                                             🔍 Checking game end conditions...
03:54:27.281                                                             ✅ Game continues - no end condition detected
03:54:27.887                                                             ✅ Evaluation received: 1.59
03:54:27.909 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:27.990 CompetitiveModeActivity                                     📊 Evaluation updated: 1.59
03:54:27.990                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.59, emotionalManager: INITIALIZED
03:54:27.990                                                             🎯 First emotional evaluation: 1.59 (threshold: 1.5)
03:54:27.990                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 1750301667s/25s)
03:54:27.991                                                             ✅ Emotional reaction triggered for evaluation: 1.59 (trigger: slight_disadvantage)
03:54:27.993 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:54:27.993                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:54:27.993                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:54:27.993                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
03:54:29.175                                                             ✅ Response generated: Behind, yes—but never out. The board is a canvas, and I will paint a masterpiece of combinations and...
03:54:29.175 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: Behind, yes—but never out. The board is a canvas, and I will paint a masterpiece of combinations and sacrifice. My harmony may seem disturbed, but from chaos comes the most beautiful melody.
03:54:29.178                                                             🎭 Updated emotion indicator: 🤔
03:54:29.178                                                             🗣️ Speaking master dialogue: Behind, yes—but never out. The board is a canvas, and I will paint a masterpiece of combinations and sacrifice. My harmony may seem disturbed, but from chaos comes the most beautiful melody.
03:54:29.178 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:54:29.178                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:54:29.178                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:54:29.178                                                             ✅ Usage context set to: competitive_mode
03:54:29.178 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:54:29.178 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:54:29.178                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:54:29.180 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:54:39.093 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 0
03:54:39.094 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=5
03:54:39.094                                                             🎯 SQUARE TAPPED: row=7, col=5
03:54:39.094                                                             📝 Player color: white
03:54:39.094                                                             🔍 Selected row/col: -1/-1
03:54:39.451                                                             🎯 Selected piece: B at 7, 5
03:54:39.451 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:39.453                                                             ViewPostIme pointer 1
03:54:39.875                                                             ViewPostIme pointer 0
03:54:39.875 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=3
03:54:39.875                                                             🎯 SQUARE TAPPED: row=5, col=3
03:54:39.875                                                             📝 Player color: white
03:54:39.875                                                             🔍 Selected row/col: 7/5
03:54:39.875                                                             🎯 ATTEMPTING MOVE: f1d3
03:54:39.875                                                             📝 Player color: white
03:54:39.875                                                             🔄 Is player's turn: true
03:54:39.876                                                             🔄 Is white's turn: true
03:54:39.876                                                             ✅ Turn validation passed, making move: f1d3
03:54:39.876 GameViewModel                                               🎯 makePlayerMove called with: f1d3
03:54:39.927                                                             🔍 Validating move: f1d3 (attempt 1)
03:54:40.027                                                             📋 Current position: rnbqk1nr/1ppp1pp1/3b3p/p7/2PP4/2N2N2/PP3PPP/R1BQKB1R w KQkq - 0 7
03:54:40.079                                                             ⚖️ Move f1d3 legality check: LEGAL
03:54:40.079                                                             ✅ Executing validated move: f1d3
03:54:40.282                                                             📍 New position after move: rnbqk1nr/1ppp1pp1/3b3p/p7/2PP4/2NB1N2/PP3PPP/R1BQK2R b KQkq - 1 7
03:54:40.283 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:54:40.284 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/1ppp1pp1/3b3p/p7/2PP4/2NB1N2/PP3PPP/R1BQK2R b KQkq - 1 7
03:54:40.289                                                             📜 Move history updated: 13 moves
03:54:40.289 GameHistoryManager                                          Move added: f1d3
03:54:40.384 GameViewModel                                               🔍 Requesting position evaluation...
03:54:40.384                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:54:40.384                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:54:40.384                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:54:40.384                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:54:40.384                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:54:40.384                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:54:40.384                                                             🔧 gameRepository class: GameRepository
03:54:40.384                                                             🔧 Current thread: main
03:54:40.384 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:54:41.093 GameViewModel                                               ✅ Evaluation received: -1.71
03:54:41.200 CompetitiveModeActivity                                     📊 Evaluation updated: -1.71
03:54:41.200                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.71, emotionalManager: INITIALIZED
03:54:41.200                                                             🎯 Evaluation change: 3.3000002 (threshold: 0.8)
03:54:41.200                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 13s/25s)
03:54:41.200                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:54:42.149 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:54:42.178 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d6e7
03:54:42.535 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/1pppbpp1/7p/p7/2PP4/2NB1N2/PP3PPP/R1BQK2R w KQkq - 2 8
03:54:42.540                                                             📜 Move history updated: 14 moves
03:54:42.540 GameHistoryManager                                          Move added: d6e7
03:54:42.540 GameViewModel                                               🔍 Requesting position evaluation...
03:54:42.541                                                             🎭 Updating personality context for move: d6e7
03:54:42.541                                                             ✨ Personality context updated for move d6e7 - This is revolutionary!
03:54:42.541                                                             🔍 Checking game end conditions...
03:54:42.643                                                             ✅ Game continues - no end condition detected
03:54:42.667 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
03:54:43.149 GameViewModel                                               ✅ Evaluation received: 2.09
03:54:43.253 CompetitiveModeActivity                                     📊 Evaluation updated: 2.09
03:54:43.253                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.09, emotionalManager: INITIALIZED
03:54:43.253                                                             🎯 Evaluation change: 0.49999988 (threshold: 0.8)
03:54:43.253                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 15s/25s)
03:54:43.253                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
03:54:43.283 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:54.559                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:54:58.135                                                             ViewPostIme pointer 0
03:54:58.135 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=4
03:54:58.135                                                             🎯 SQUARE TAPPED: row=7, col=4
03:54:58.135                                                             📝 Player color: white
03:54:58.135                                                             🔍 Selected row/col: -1/-1
03:54:58.492                                                             🎯 Selected piece: K at 7, 4
03:54:58.492 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:54:58.494                                                             ViewPostIme pointer 1
03:54:58.495                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:54:58.956                                                             ViewPostIme pointer 0
03:54:58.956 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=6
03:54:58.956                                                             🎯 SQUARE TAPPED: row=7, col=6
03:54:58.957                                                             📝 Player color: white
03:54:58.957                                                             🔍 Selected row/col: 7/4
03:54:58.957                                                             🎯 ATTEMPTING MOVE: e1g1
03:54:58.957                                                             📝 Player color: white
03:54:58.957                                                             🔄 Is player's turn: true
03:54:58.957                                                             🔄 Is white's turn: true
03:54:58.957                                                             ✅ Turn validation passed, making move: e1g1
03:54:58.957 GameViewModel                                               🎯 makePlayerMove called with: e1g1
03:54:59.008                                                             🔍 Validating move: e1g1 (attempt 1)
03:54:59.109                                                             📋 Current position: rnbqk1nr/1pppbpp1/7p/p7/2PP4/2NB1N2/PP3PPP/R1BQK2R w KQkq - 2 8
03:54:59.160                                                             ⚖️ Move e1g1 legality check: LEGAL
03:54:59.160                                                             ✅ Executing validated move: e1g1
03:54:59.363                                                             📍 New position after move: rnbqk1nr/1pppbpp1/7p/p7/2PP4/2NB1N2/PP3PPP/R1BQ1RK1 b kq - 3 8
03:54:59.364 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:54:59.366 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk1nr/1pppbpp1/7p/p7/2PP4/2NB1N2/PP3PPP/R1BQ1RK1 b kq - 3 8
03:54:59.370                                                             📜 Move history updated: 15 moves
03:54:59.371 GameHistoryManager                                          Move added: e1g1
03:54:59.465 GameViewModel                                               🔍 Requesting position evaluation...
03:54:59.465                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:54:59.465                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:54:59.465                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:54:59.465                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:54:59.465                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:54:59.465                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:54:59.465                                                             🔧 gameRepository class: GameRepository
03:54:59.465                                                             🔧 Current thread: main
03:54:59.465 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:55:00.173 GameViewModel                                               ✅ Evaluation received: -1.98
03:55:00.273 CompetitiveModeActivity                                     📊 Evaluation updated: -1.98
03:55:00.273                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.98, emotionalManager: INITIALIZED
03:55:00.273                                                             🎯 Evaluation change: 3.5700002 (threshold: 0.8)
03:55:00.273                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 32s/25s)
03:55:00.273                                                             ✅ Emotional reaction triggered for evaluation: -1.98 (trigger: slight_advantage)
03:55:00.274 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:55:00.274                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:55:00.274                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:55:00.274                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
03:55:00.923 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:55:00.953 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8f6
03:55:01.312 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/1pppbpp1/5n1p/p7/2PP4/2NB1N2/PP3PPP/R1BQ1RK1 w kq - 4 9
03:55:01.318                                                             📜 Move history updated: 16 moves
03:55:01.318 GameHistoryManager                                          Move added: g8f6
03:55:01.318 GameViewModel                                               🔍 Requesting position evaluation...
03:55:01.319                                                             🎭 Updating personality context for move: g8f6
03:55:01.319                                                             ✨ Personality context updated for move g8f6 - This is revolutionary!
03:55:01.319                                                             🔍 Checking game end conditions...
03:55:01.421                                                             ✅ Game continues - no end condition detected
03:55:01.565 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
03:55:01.695 OpenAIService                                               ✅ Response generated: The initiative is mine, but the fight has only just begun. I will weave my combinations like a compo...
03:55:01.695 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The initiative is mine, but the fight has only just begun. I will weave my combinations like a composer—every note must lead to a beautiful resolution.
03:55:01.697                                                             🎭 Updated emotion indicator: 🙂
03:55:01.697                                                             🗣️ Speaking master dialogue: The initiative is mine, but the fight has only just begun. I will weave my combinations like a composer—every note must lead to a beautiful resolution.
03:55:01.697 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:55:01.697                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:55:01.697                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:55:01.697                                                             ✅ Usage context set to: competitive_mode
03:55:01.697 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:55:01.697 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:55:01.697                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:55:01.698 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:55:01.978 GameViewModel                                               ✅ Evaluation received: 2.06
03:55:02.081 CompetitiveModeActivity                                     📊 Evaluation updated: 2.06
03:55:02.081                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.06, emotionalManager: INITIALIZED
03:55:02.081                                                             🎯 Evaluation change: 4.04 (threshold: 0.8)
03:55:02.081                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
03:55:02.081                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:55:02.365 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:55:08.837                                                             ViewPostIme pointer 0
03:55:08.838 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=2
03:55:08.838                                                             🎯 SQUARE TAPPED: row=7, col=2
03:55:08.838                                                             📝 Player color: white
03:55:08.838                                                             🔍 Selected row/col: -1/-1
03:55:09.191                                                             🎯 Selected piece: B at 7, 2
03:55:09.191 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:55:09.192                                                             ViewPostIme pointer 1
03:55:09.788                                                             ViewPostIme pointer 0
03:55:09.789 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
03:55:09.789                                                             🎯 SQUARE TAPPED: row=4, col=5
03:55:09.789                                                             📝 Player color: white
03:55:09.789                                                             🔍 Selected row/col: 7/2
03:55:09.789                                                             🎯 ATTEMPTING MOVE: c1f4
03:55:09.789                                                             📝 Player color: white
03:55:09.789                                                             🔄 Is player's turn: true
03:55:09.789                                                             🔄 Is white's turn: true
03:55:09.789                                                             ✅ Turn validation passed, making move: c1f4
03:55:09.789 GameViewModel                                               🎯 makePlayerMove called with: c1f4
03:55:09.840                                                             🔍 Validating move: c1f4 (attempt 1)
03:55:09.941                                                             📋 Current position: rnbqk2r/1pppbpp1/5n1p/p7/2PP4/2NB1N2/PP3PPP/R1BQ1RK1 w kq - 4 9
03:55:09.992                                                             ⚖️ Move c1f4 legality check: LEGAL
03:55:09.992                                                             ✅ Executing validated move: c1f4
03:55:10.196                                                             📍 New position after move: rnbqk2r/1pppbpp1/5n1p/p7/2PP1B2/2NB1N2/PP3PPP/R2Q1RK1 b kq - 5 9
03:55:10.197 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:55:10.199 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/1pppbpp1/5n1p/p7/2PP1B2/2NB1N2/PP3PPP/R2Q1RK1 b kq - 5 9
03:55:10.205                                                             📜 Move history updated: 17 moves
03:55:10.205 GameHistoryManager                                          Move added: c1f4
03:55:10.299 GameViewModel                                               🔍 Requesting position evaluation...
03:55:10.299                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:55:10.299                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:55:10.299                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:55:10.299                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:55:10.300                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:55:10.300                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:55:10.300                                                             🔧 gameRepository class: GameRepository
03:55:10.300                                                             🔧 Current thread: main
03:55:10.300 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:55:11.006 GameViewModel                                               ✅ Evaluation received: -1.99
03:55:11.114 CompetitiveModeActivity                                     📊 Evaluation updated: -1.99
03:55:11.114                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.99, emotionalManager: INITIALIZED
03:55:11.114                                                             🎯 Evaluation change: 0.00999999 (threshold: 0.8)
03:55:11.114                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 10s/25s)
03:55:11.114                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
03:55:11.374                                                             🗣️ Master dialogue speech completed
03:55:11.916 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:55:11.949 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7d5
03:55:12.308 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/1pp1bpp1/5n1p/p2p4/2PP1B2/2NB1N2/PP3PPP/R2Q1RK1 w kq - 0 10
03:55:12.315                                                             📜 Move history updated: 18 moves
03:55:12.315 GameHistoryManager                                          Move added: d7d5
03:55:12.316 GameViewModel                                               🔍 Requesting position evaluation...
03:55:12.316                                                             🎭 Updating personality context for move: d7d5
03:55:12.316                                                             ✨ Personality context updated for move d7d5 - This is revolutionary!
03:55:12.316                                                             🔍 Checking game end conditions...
03:55:12.418                                                             ✅ Game continues - no end condition detected
03:55:12.978                                                             ✅ Evaluation received: 2.43
03:55:13.081 CompetitiveModeActivity                                     📊 Evaluation updated: 2.43
03:55:13.081                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.43, emotionalManager: INITIALIZED
03:55:13.081                                                             🎯 Evaluation change: 4.41 (threshold: 0.8)
03:55:13.082                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 12s/25s)
03:55:13.082                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:55:13.198 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:55:33.992                                                             ViewPostIme pointer 0
03:55:33.992 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
03:55:33.992                                                             🎯 SQUARE TAPPED: row=5, col=2
03:55:33.992                                                             📝 Player color: white
03:55:33.992                                                             🔍 Selected row/col: -1/-1
03:55:34.349                                                             🎯 Selected piece: N at 5, 2
03:55:34.349 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:55:34.350                                                             ViewPostIme pointer 1
03:55:34.829                                                             ViewPostIme pointer 0
03:55:34.829 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
03:55:34.829                                                             🎯 SQUARE TAPPED: row=3, col=3
03:55:34.829                                                             📝 Player color: white
03:55:34.829                                                             🔍 Selected row/col: 5/2
03:55:34.830                                                             🎯 ATTEMPTING MOVE: c3d5
03:55:34.830                                                             📝 Player color: white
03:55:34.830                                                             🔄 Is player's turn: true
03:55:34.830                                                             🔄 Is white's turn: true
03:55:34.830                                                             ✅ Turn validation passed, making move: c3d5
03:55:34.830 GameViewModel                                               🎯 makePlayerMove called with: c3d5
03:55:34.880                                                             🔍 Validating move: c3d5 (attempt 1)
03:55:34.981                                                             📋 Current position: rnbqk2r/1pp1bpp1/5n1p/p2p4/2PP1B2/2NB1N2/PP3PPP/R2Q1RK1 w kq - 0 10
03:55:35.032                                                             ⚖️ Move c3d5 legality check: LEGAL
03:55:35.032                                                             ✅ Executing validated move: c3d5
03:55:35.235                                                             📍 New position after move: rnbqk2r/1pp1bpp1/5n1p/p2N4/2PP1B2/3B1N2/PP3PPP/R2Q1RK1 b kq - 0 10
03:55:35.236 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:55:35.237 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/1pp1bpp1/5n1p/p2N4/2PP1B2/3B1N2/PP3PPP/R2Q1RK1 b kq - 0 10
03:55:35.243                                                             📜 Move history updated: 19 moves
03:55:35.243 GameHistoryManager                                          Move added: c3d5
03:55:35.341 GameViewModel                                               🔍 Requesting position evaluation...
03:55:35.341                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:55:35.341                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:55:35.341                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:55:35.341                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:55:35.341                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:55:35.341                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:55:35.342                                                             🔧 gameRepository class: GameRepository
03:55:35.342                                                             🔧 Current thread: main
03:55:35.342 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:55:35.844 GameViewModel                                               ✅ Evaluation received: -2.32
03:55:35.949 CompetitiveModeActivity                                     📊 Evaluation updated: -2.32
03:55:35.949                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.32, emotionalManager: INITIALIZED
03:55:35.949                                                             🎯 Evaluation change: 0.3399999 (threshold: 0.8)
03:55:35.949                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 35s/25s)
03:55:35.949                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:55:36.742 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
03:55:36.772 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f6d5
03:55:37.132 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/1pp1bpp1/7p/p2n4/2PP1B2/3B1N2/PP3PPP/R2Q1RK1 w kq - 0 11
03:55:37.139                                                             📜 Move history updated: 20 moves
03:55:37.140 GameHistoryManager                                          Move added: f6d5
03:55:37.140 GameViewModel                                               🔍 Requesting position evaluation...
03:55:37.140                                                             🎭 Updating personality context for move: f6d5
03:55:37.140                                                             ✨ Personality context updated for move f6d5 - This is revolutionary!
03:55:37.140                                                             🔍 Checking game end conditions...
03:55:37.241                                                             ✅ Game continues - no end condition detected
03:55:37.498                                                             ✅ Evaluation received: 2.32
03:55:37.601 CompetitiveModeActivity                                     📊 Evaluation updated: 2.32
03:55:37.601                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.32, emotionalManager: INITIALIZED
03:55:37.601                                                             🎯 Evaluation change: 4.3 (threshold: 0.8)
03:55:37.601                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 37s/25s)
03:55:37.601                                                             ✅ Emotional reaction triggered for evaluation: 2.32 (trigger: slight_disadvantage)
03:55:37.602 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:55:37.603                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:55:37.603                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:55:37.603                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
03:55:38.236 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:55:38.511 OpenAIService                                               ✅ Response generated: Behind, but never out—expect a tempest of combinations. My pieces will sing in harmony, and from the...
03:55:38.512 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: Behind, but never out—expect a tempest of combinations. My pieces will sing in harmony, and from the depths of difficulty, I will create beauty and seize the initiative.
03:55:38.515                                                             🎭 Updated emotion indicator: 🤔
03:55:38.515                                                             🗣️ Speaking master dialogue: Behind, but never out—expect a tempest of combinations. My pieces will sing in harmony, and from the depths of difficulty, I will create beauty and seize the initiative.
03:55:38.515 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:55:38.515                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:55:38.515                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:55:38.515                                                             ✅ Usage context set to: competitive_mode
03:55:38.515 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:55:38.516 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:55:38.516                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:55:38.517 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:55:46.363 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 0
03:55:46.364 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
03:55:46.364                                                             🎯 SQUARE TAPPED: row=4, col=2
03:55:46.364                                                             📝 Player color: white
03:55:46.364                                                             🔍 Selected row/col: -1/-1
03:55:46.720                                                             🎯 Selected piece: P at 4, 2
03:55:46.721 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:55:46.722                                                             ViewPostIme pointer 1
03:55:47.066                                                             ViewPostIme pointer 0
03:55:47.067 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
03:55:47.067                                                             🎯 SQUARE TAPPED: row=3, col=3
03:55:47.067                                                             📝 Player color: white
03:55:47.067                                                             🔍 Selected row/col: 4/2
03:55:47.067                                                             ♟️ Promotion check: piece=P, fromRow=4, toRow=3, reaches=false
03:55:47.067                                                             🎯 ATTEMPTING MOVE: c4d5
03:55:47.067                                                             📝 Player color: white
03:55:47.067                                                             🔄 Is player's turn: true
03:55:47.067                                                             🔄 Is white's turn: true
03:55:47.067                                                             ✅ Turn validation passed, making move: c4d5
03:55:47.067 GameViewModel                                               🎯 makePlayerMove called with: c4d5
03:55:47.118                                                             🔍 Validating move: c4d5 (attempt 1)
03:55:47.219                                                             📋 Current position: rnbqk2r/1pp1bpp1/7p/p2n4/2PP1B2/3B1N2/PP3PPP/R2Q1RK1 w kq - 0 11
03:55:47.269                                                             ⚖️ Move c4d5 legality check: LEGAL
03:55:47.269                                                             ✅ Executing validated move: c4d5
03:55:47.472                                                             📍 New position after move: rnbqk2r/1pp1bpp1/7p/p2P4/3P1B2/3B1N2/PP3PPP/R2Q1RK1 b kq - 0 11
03:55:47.473 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:55:47.474 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/1pp1bpp1/7p/p2P4/3P1B2/3B1N2/PP3PPP/R2Q1RK1 b kq - 0 11
03:55:47.477                                                             📜 Move history updated: 21 moves
03:55:47.477 GameHistoryManager                                          Move added: c4d5
03:55:47.576 GameViewModel                                               🔍 Requesting position evaluation...
03:55:47.577                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:55:47.577                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:55:47.577                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:55:47.577                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:55:47.577                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:55:47.577                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:55:47.577                                                             🔧 gameRepository class: GameRepository
03:55:47.577                                                             🔧 Current thread: main
03:55:47.577 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:55:48.184 GameViewModel                                               ✅ Evaluation received: -2.25
03:55:48.284 CompetitiveModeActivity                                     📊 Evaluation updated: -2.25
03:55:48.284                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.25, emotionalManager: INITIALIZED
03:55:48.284                                                             🎯 Evaluation change: 4.5699997 (threshold: 0.8)
03:55:48.284                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 10s/25s)
03:55:48.284                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:55:49.086 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:55:49.118 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e8g8
03:55:49.476 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/1pp1bpp1/7p/p2P4/3P1B2/3B1N2/PP3PPP/R2Q1RK1 w - - 1 12
03:55:49.484                                                             📜 Move history updated: 22 moves
03:55:49.484 GameHistoryManager                                          Move added: e8g8
03:55:49.484 GameViewModel                                               🔍 Requesting position evaluation...
03:55:49.484                                                             🎭 Updating personality context for move: e8g8
03:55:49.485                                                             ✨ Personality context updated for move e8g8 - This is revolutionary!
03:55:49.485                                                             🔍 Checking game end conditions...
03:55:49.587                                                             ✅ Game continues - no end condition detected
03:55:50.094                                                             ✅ Evaluation received: 2.15
03:55:50.197 CompetitiveModeActivity                                     📊 Evaluation updated: 2.15
03:55:50.197                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.15, emotionalManager: INITIALIZED
03:55:50.197                                                             🎯 Evaluation change: 0.16999984 (threshold: 0.8)
03:55:50.197                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 12s/25s)
03:55:50.197                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
03:55:50.473 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:55:51.189 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
03:55:59.841 VRI[Compet...y]@bee13b0                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
03:56:21.551                                                             ViewPostIme pointer 0
03:56:21.552 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=5
03:56:21.552                                                             🎯 SQUARE TAPPED: row=7, col=5
03:56:21.552                                                             📝 Player color: white
03:56:21.552                                                             🔍 Selected row/col: -1/-1
03:56:21.910                                                             🎯 Selected piece: R at 7, 5
03:56:21.911 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:56:21.912                                                             ViewPostIme pointer 1
03:56:21.912                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:56:22.204                                                             ViewPostIme pointer 0
03:56:22.204 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=4
03:56:22.204                                                             🎯 SQUARE TAPPED: row=7, col=4
03:56:22.205                                                             📝 Player color: white
03:56:22.205                                                             🔍 Selected row/col: 7/5
03:56:22.205                                                             🎯 ATTEMPTING MOVE: f1e1
03:56:22.205                                                             📝 Player color: white
03:56:22.205                                                             🔄 Is player's turn: true
03:56:22.205                                                             🔄 Is white's turn: true
03:56:22.205                                                             ✅ Turn validation passed, making move: f1e1
03:56:22.205 GameViewModel                                               🎯 makePlayerMove called with: f1e1
03:56:22.256                                                             🔍 Validating move: f1e1 (attempt 1)
03:56:22.356                                                             📋 Current position: rnbq1rk1/1pp1bpp1/7p/p2P4/3P1B2/3B1N2/PP3PPP/R2Q1RK1 w - - 1 12
03:56:22.407                                                             ⚖️ Move f1e1 legality check: LEGAL
03:56:22.407                                                             ✅ Executing validated move: f1e1
03:56:22.610                                                             📍 New position after move: rnbq1rk1/1pp1bpp1/7p/p2P4/3P1B2/3B1N2/PP3PPP/R2QR1K1 b - - 2 12
03:56:22.611 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:56:22.613 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/1pp1bpp1/7p/p2P4/3P1B2/3B1N2/PP3PPP/R2QR1K1 b - - 2 12
03:56:22.618                                                             📜 Move history updated: 23 moves
03:56:22.618 GameHistoryManager                                          Move added: f1e1
03:56:22.713 GameViewModel                                               🔍 Requesting position evaluation...
03:56:22.713                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:56:22.713                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:56:22.713                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:56:22.714                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:56:22.714                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:56:22.714                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:56:22.714                                                             🔧 gameRepository class: GameRepository
03:56:22.714                                                             🔧 Current thread: main
03:56:22.714 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:56:23.419 GameViewModel                                               ✅ Evaluation received: -1.99
03:56:23.528 CompetitiveModeActivity                                     📊 Evaluation updated: -1.99
03:56:23.528                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.99, emotionalManager: INITIALIZED
03:56:23.528                                                             🎯 Evaluation change: 4.31 (threshold: 0.8)
03:56:23.528                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 45s/25s)
03:56:23.528                                                             ✅ Emotional reaction triggered for evaluation: -1.99 (trigger: slight_advantage)
03:56:23.528 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:56:23.528                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:56:23.528                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:56:23.528                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
03:56:24.274 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:56:24.304 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c8g4
03:56:24.329 OpenAIService                                               ✅ Response generated: The position sings with possibilities—my lead is not just in material, but in the harmony of my piec...
03:56:24.662 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/1pp1bpp1/7p/p2P4/3P1Bb1/3B1N2/PP3PPP/R2QR1K1 w - - 3 13
03:56:24.670                                                             📜 Move history updated: 24 moves
03:56:24.670 GameHistoryManager                                          Move added: c8g4
03:56:24.670 GameViewModel                                               🔍 Requesting position evaluation...
03:56:24.671                                                             🎭 Updating personality context for move: c8g4
03:56:24.671                                                             ✨ Personality context updated for move c8g4 - This is revolutionary!
03:56:24.671                                                             🔍 Checking game end conditions...
03:56:24.773                                                             ✅ Game continues - no end condition detected
03:56:24.774 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position sings with possibilities—my lead is not just in material, but in the harmony of my pieces. A single, beautiful combination could decide the game.
03:56:24.780                                                             🎭 Updated emotion indicator: 🙂
03:56:24.780                                                             🗣️ Speaking master dialogue: The position sings with possibilities—my lead is not just in material, but in the harmony of my pieces. A single, beautiful combination could decide the game.
03:56:24.780 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:56:24.780                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:56:24.781                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:56:24.781                                                             ✅ Usage context set to: competitive_mode
03:56:24.781 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:56:24.781 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:56:24.781                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:56:24.784 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:56:25.226 GameViewModel                                               ✅ Evaluation received: 2.73
03:56:25.329 CompetitiveModeActivity                                     📊 Evaluation updated: 2.73
03:56:25.330                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.73, emotionalManager: INITIALIZED
03:56:25.330                                                             🎯 Evaluation change: 4.7200003 (threshold: 0.8)
03:56:25.330                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
03:56:25.330                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:56:25.612 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:56:35.527 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
03:56:43.363 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 0
03:56:43.363 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
03:56:43.363                                                             🎯 SQUARE TAPPED: row=7, col=3
03:56:43.363                                                             📝 Player color: white
03:56:43.363                                                             🔍 Selected row/col: -1/-1
03:56:43.721                                                             🎯 Selected piece: Q at 7, 3
03:56:43.721 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:56:43.723                                                             ViewPostIme pointer 1
03:56:44.076                                                             ViewPostIme pointer 0
03:56:44.077 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
03:56:44.077                                                             🎯 SQUARE TAPPED: row=6, col=4
03:56:44.077                                                             📝 Player color: white
03:56:44.077                                                             🔍 Selected row/col: 7/3
03:56:44.077                                                             🎯 ATTEMPTING MOVE: d1e2
03:56:44.077                                                             📝 Player color: white
03:56:44.077                                                             🔄 Is player's turn: true
03:56:44.077                                                             🔄 Is white's turn: true
03:56:44.077                                                             ✅ Turn validation passed, making move: d1e2
03:56:44.077 GameViewModel                                               🎯 makePlayerMove called with: d1e2
03:56:44.129                                                             🔍 Validating move: d1e2 (attempt 1)
03:56:44.230                                                             📋 Current position: rn1q1rk1/1pp1bpp1/7p/p2P4/3P1Bb1/3B1N2/PP3PPP/R2QR1K1 w - - 3 13
03:56:44.281                                                             ⚖️ Move d1e2 legality check: LEGAL
03:56:44.281                                                             ✅ Executing validated move: d1e2
03:56:44.483                                                             📍 New position after move: rn1q1rk1/1pp1bpp1/7p/p2P4/3P1Bb1/3B1N2/PP2QPPP/R3R1K1 b - - 4 13
03:56:44.484 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:56:44.485 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/1pp1bpp1/7p/p2P4/3P1Bb1/3B1N2/PP2QPPP/R3R1K1 b - - 4 13
03:56:44.491                                                             📜 Move history updated: 25 moves
03:56:44.491 GameHistoryManager                                          Move added: d1e2
03:56:44.585 GameViewModel                                               🔍 Requesting position evaluation...
03:56:44.585                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:56:44.585                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:56:44.585                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:56:44.585                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:56:44.585                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:56:44.585                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:56:44.585                                                             🔧 gameRepository class: GameRepository
03:56:44.585                                                             🔧 Current thread: main
03:56:44.585 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:56:45.293 GameViewModel                                               ✅ Evaluation received: -2.80
03:56:45.393 CompetitiveModeActivity                                     📊 Evaluation updated: -2.8
03:56:45.393                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.8, emotionalManager: INITIALIZED
03:56:45.393                                                             🎯 Evaluation change: 0.80999994 (threshold: 0.8)
03:56:45.393                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 21s/25s)
03:56:45.393                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:56:46.146 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:56:46.179 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7b4
03:56:46.539 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/1pp2pp1/7p/p2P4/1b1P1Bb1/3B1N2/PP2QPPP/R3R1K1 w - - 5 14
03:56:46.546                                                             📜 Move history updated: 26 moves
03:56:46.546 GameHistoryManager                                          Move added: e7b4
03:56:46.546 GameViewModel                                               🔍 Requesting position evaluation...
03:56:46.546                                                             🎭 Updating personality context for move: e7b4
03:56:46.547                                                             ✨ Personality context updated for move e7b4 - This is revolutionary!
03:56:46.547                                                             🔍 Checking game end conditions...
03:56:46.649                                                             ✅ Game continues - no end condition detected
03:56:47.007                                                             ✅ Evaluation received: 2.99
03:56:47.110 CompetitiveModeActivity                                     📊 Evaluation updated: 2.99
03:56:47.110                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.99, emotionalManager: INITIALIZED
03:56:47.110                                                             🎯 Evaluation change: 4.98 (threshold: 0.8)
03:56:47.110                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 23s/25s)
03:56:47.110                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:56:47.484 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:57:00.052                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:57:01.043                                                             ViewPostIme pointer 0
03:57:01.043 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
03:57:01.043                                                             🎯 SQUARE TAPPED: row=6, col=4
03:57:01.043                                                             📝 Player color: white
03:57:01.043                                                             🔍 Selected row/col: -1/-1
03:57:01.401                                                             🎯 Selected piece: Q at 6, 4
03:57:01.401 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:57:01.402                                                             ViewPostIme pointer 1
03:57:01.403                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:57:01.723                                                             ViewPostIme pointer 0
03:57:01.723 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=4
03:57:01.723                                                             🎯 SQUARE TAPPED: row=4, col=4
03:57:01.723                                                             📝 Player color: white
03:57:01.723                                                             🔍 Selected row/col: 6/4
03:57:01.723                                                             🎯 ATTEMPTING MOVE: e2e4
03:57:01.723                                                             📝 Player color: white
03:57:01.723                                                             🔄 Is player's turn: true
03:57:01.723                                                             🔄 Is white's turn: true
03:57:01.723                                                             ✅ Turn validation passed, making move: e2e4
03:57:01.723 GameViewModel                                               🎯 makePlayerMove called with: e2e4
03:57:01.774                                                             🔍 Validating move: e2e4 (attempt 1)
03:57:01.875                                                             📋 Current position: rn1q1rk1/1pp2pp1/7p/p2P4/1b1P1Bb1/3B1N2/PP2QPPP/R3R1K1 w - - 5 14
03:57:01.926                                                             ⚖️ Move e2e4 legality check: LEGAL
03:57:01.926                                                             ✅ Executing validated move: e2e4
03:57:02.128                                                             📍 New position after move: rn1q1rk1/1pp2pp1/7p/p2P4/1b1PQBb1/3B1N2/PP3PPP/R3R1K1 b - - 6 14
03:57:02.129 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:57:02.131 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/1pp2pp1/7p/p2P4/1b1PQBb1/3B1N2/PP3PPP/R3R1K1 b - - 6 14
03:57:02.136                                                             📜 Move history updated: 27 moves
03:57:02.136 GameHistoryManager                                          Move added: e2e4
03:57:02.232 GameViewModel                                               🔍 Requesting position evaluation...
03:57:02.233                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:57:02.233                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:57:02.233                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:57:02.233                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:57:02.233                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:57:02.233                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:57:02.233                                                             🔧 gameRepository class: GameRepository
03:57:02.233                                                             🔧 Current thread: main
03:57:02.234 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:57:02.787 GameViewModel                                               ✅ Evaluation received: -3.25
03:57:02.889 CompetitiveModeActivity                                     📊 Evaluation updated: -3.25
03:57:02.889                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -3.25, emotionalManager: INITIALIZED
03:57:02.889                                                             🎯 Evaluation change: 1.26 (threshold: 0.8)
03:57:02.889                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 39s/25s)
03:57:02.889                                                             ✅ Emotional reaction triggered for evaluation: -3.25 (trigger: significant_advantage)
03:57:02.889 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:57:02.889                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:57:02.889                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:57:02.890                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
03:57:03.693 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:57:03.726 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f7f5
03:57:04.086 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/1pp3p1/7p/p2P1p2/1b1PQBb1/3B1N2/PP3PPP/R3R1K1 w - - 0 15
03:57:04.095                                                             📜 Move history updated: 28 moves
03:57:04.095 GameHistoryManager                                          Move added: f7f5
03:57:04.095 GameViewModel                                               🔍 Requesting position evaluation...
03:57:04.095                                                             🎭 Updating personality context for move: f7f5
03:57:04.095                                                             ✨ Personality context updated for move f7f5 - This is revolutionary!
03:57:04.096                                                             🔍 Checking game end conditions...
03:57:04.196                                                             ✅ Game continues - no end condition detected
03:57:04.371 OpenAIService                                               ✅ Response generated: The position sings with possibilities—my pieces are in harmony, and the combination awaits. At -3.25...
03:57:04.371 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position sings with possibilities—my pieces are in harmony, and the combination awaits. At -3.25, my task is to create beauty from advantage, not just convert.
03:57:04.372                                                             🎭 Updated emotion indicator: 😏
03:57:04.372                                                             🗣️ Speaking master dialogue: The position sings with possibilities—my pieces are in harmony, and the combination awaits. At -3.25, my task is to create beauty from advantage, not just convert.
03:57:04.372 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:57:04.372                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:57:04.373                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:57:04.373                                                             ✅ Usage context set to: competitive_mode
03:57:04.373 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:57:04.373 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:57:04.373                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:57:04.374 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:57:04.553 GameViewModel                                               ✅ Evaluation received: 3.15
03:57:04.657 CompetitiveModeActivity                                     📊 Evaluation updated: 3.15
03:57:04.658                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 3.15, emotionalManager: INITIALIZED
03:57:04.658                                                             🎯 Evaluation change: 6.4 (threshold: 0.8)
03:57:04.658                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
03:57:04.658                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:57:05.131 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:57:15.756 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
03:57:27.472 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 0
03:57:27.473 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=4
03:57:27.473                                                             🎯 SQUARE TAPPED: row=4, col=4
03:57:27.473                                                             📝 Player color: white
03:57:27.473                                                             🔍 Selected row/col: -1/-1
03:57:27.832                                                             🎯 Selected piece: Q at 4, 4
03:57:27.832 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:57:27.833                                                             ViewPostIme pointer 1
03:57:28.186                                                             ViewPostIme pointer 0
03:57:28.187 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
03:57:28.187                                                             🎯 SQUARE TAPPED: row=2, col=4
03:57:28.187                                                             📝 Player color: white
03:57:28.187                                                             🔍 Selected row/col: 4/4
03:57:28.188                                                             🎯 ATTEMPTING MOVE: e4e6
03:57:28.188                                                             📝 Player color: white
03:57:28.188                                                             🔄 Is player's turn: true
03:57:28.188                                                             🔄 Is white's turn: true
03:57:28.188                                                             ✅ Turn validation passed, making move: e4e6
03:57:28.188 GameViewModel                                               🎯 makePlayerMove called with: e4e6
03:57:28.239                                                             🔍 Validating move: e4e6 (attempt 1)
03:57:28.340                                                             📋 Current position: rn1q1rk1/1pp3p1/7p/p2P1p2/1b1PQBb1/3B1N2/PP3PPP/R3R1K1 w - - 0 15
03:57:28.391                                                             ⚖️ Move e4e6 legality check: LEGAL
03:57:28.392                                                             ✅ Executing validated move: e4e6
03:57:28.594                                                             📍 New position after move: rn1q1rk1/1pp3p1/4Q2p/p2P1p2/1b1P1Bb1/3B1N2/PP3PPP/R3R1K1 b - - 1 15
03:57:28.595 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:57:28.597 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/1pp3p1/4Q2p/p2P1p2/1b1P1Bb1/3B1N2/PP3PPP/R3R1K1 b - - 1 15
03:57:28.603                                                             📜 Move history updated: 29 moves
03:57:28.603 GameHistoryManager                                          Move added: e4e6
03:57:28.698 GameViewModel                                               🔍 Requesting position evaluation...
03:57:28.698                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:57:28.698                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:57:28.698                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:57:28.699                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:57:28.699                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:57:28.699                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:57:28.699                                                             🔧 gameRepository class: GameRepository
03:57:28.699                                                             🔧 Current thread: main
03:57:28.699 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:57:29.251 GameViewModel                                               ✅ Evaluation received: -3.15
03:57:29.354 CompetitiveModeActivity                                     📊 Evaluation updated: -3.15
03:57:29.354                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -3.15, emotionalManager: INITIALIZED
03:57:29.354                                                             🎯 Evaluation change: 0.099999905 (threshold: 0.8)
03:57:29.354                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 26s/25s)
03:57:29.354                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:57:30.103 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:57:30.135 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8h8
03:57:30.494 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1r1k/1pp3p1/4Q2p/p2P1p2/1b1P1Bb1/3B1N2/PP3PPP/R3R1K1 w - - 2 16
03:57:30.502                                                             📜 Move history updated: 30 moves
03:57:30.502 GameHistoryManager                                          Move added: g8h8
03:57:30.502 GameViewModel                                               🔍 Requesting position evaluation...
03:57:30.502                                                             🎭 Updating personality context for move: g8h8
03:57:30.503                                                             ✨ Personality context updated for move g8h8 - This is revolutionary!
03:57:30.503                                                             🔍 Checking game end conditions...
03:57:30.604                                                             ✅ Game continues - no end condition detected
03:57:30.961                                                             ✅ Evaluation received: 3.48
03:57:31.064 CompetitiveModeActivity                                     📊 Evaluation updated: 3.48
03:57:31.064                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 3.48, emotionalManager: INITIALIZED
03:57:31.064                                                             🎯 Evaluation change: 6.73 (threshold: 0.8)
03:57:31.064                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 28s/25s)
03:57:31.064                                                             ✅ Emotional reaction triggered for evaluation: 3.48 (trigger: significant_disadvantage)
03:57:31.065 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:57:31.065                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:57:31.065                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:57:31.066                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
03:57:31.596 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:57:32.164 OpenAIService                                               ✅ Response generated: (15) The danger is real, but so is my imagination. In adversity, I seek the spark of a hidden combin...
03:57:32.164 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: (15) The danger is real, but so is my imagination. In adversity, I seek the spark of a hidden combination—chess is pain, but also poetry.
03:57:32.167                                                             🎭 Updated emotion indicator: 😤
03:57:32.168                                                             🗣️ Speaking master dialogue: (15) The danger is real, but so is my imagination. In adversity, I seek the spark of a hidden combination—chess is pain, but also poetry.
03:57:32.168 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:57:32.168                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:57:32.168                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:57:32.168                                                             ✅ Usage context set to: competitive_mode
03:57:32.168 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:57:32.169 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:57:32.169                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:57:32.172 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:57:41.766                                                             🗣️ Master dialogue speech completed
03:57:53.030 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 0
03:57:53.031 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
03:57:53.031                                                             🎯 SQUARE TAPPED: row=4, col=5
03:57:53.031                                                             📝 Player color: white
03:57:53.031                                                             🔍 Selected row/col: -1/-1
03:57:53.389                                                             🎯 Selected piece: B at 4, 5
03:57:53.389 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:57:53.390                                                             ViewPostIme pointer 1
03:57:53.819                                                             ViewPostIme pointer 0
03:57:53.820 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
03:57:53.820                                                             🎯 SQUARE TAPPED: row=3, col=4
03:57:53.820                                                             📝 Player color: white
03:57:53.820                                                             🔍 Selected row/col: 4/5
03:57:53.820                                                             🎯 ATTEMPTING MOVE: f4e5
03:57:53.820                                                             📝 Player color: white
03:57:53.820                                                             🔄 Is player's turn: true
03:57:53.820                                                             🔄 Is white's turn: true
03:57:53.820                                                             ✅ Turn validation passed, making move: f4e5
03:57:53.820 GameViewModel                                               🎯 makePlayerMove called with: f4e5
03:57:53.872                                                             🔍 Validating move: f4e5 (attempt 1)
03:57:53.974                                                             📋 Current position: rn1q1r1k/1pp3p1/4Q2p/p2P1p2/1b1P1Bb1/3B1N2/PP3PPP/R3R1K1 w - - 2 16
03:57:54.025                                                             ⚖️ Move f4e5 legality check: LEGAL
03:57:54.025                                                             ✅ Executing validated move: f4e5
03:57:54.228                                                             📍 New position after move: rn1q1r1k/1pp3p1/4Q2p/p2PBp2/1b1P2b1/3B1N2/PP3PPP/R3R1K1 b - - 3 16
03:57:54.229 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:57:54.230 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1r1k/1pp3p1/4Q2p/p2PBp2/1b1P2b1/3B1N2/PP3PPP/R3R1K1 b - - 3 16
03:57:54.237                                                             📜 Move history updated: 31 moves
03:57:54.237 GameHistoryManager                                          Move added: f4e5
03:57:54.335 GameViewModel                                               🔍 Requesting position evaluation...
03:57:54.335                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:57:54.335                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:57:54.335                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:57:54.336                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:57:54.336                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:57:54.336                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:57:54.336                                                             🔧 gameRepository class: GameRepository
03:57:54.336                                                             🔧 Current thread: main
03:57:54.336 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:57:54.739 GameViewModel                                               ✅ Evaluation received: -3.58
03:57:54.839 CompetitiveModeActivity                                     📊 Evaluation updated: -3.58
03:57:54.839                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -3.58, emotionalManager: INITIALIZED
03:57:54.839                                                             🎯 Evaluation change: 7.06 (threshold: 0.8)
03:57:54.839                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 23s/25s)
03:57:54.839                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:57:55.743 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:57:55.775 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h8h7
03:57:56.134 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1r2/1pp3pk/4Q2p/p2PBp2/1b1P2b1/3B1N2/PP3PPP/R3R1K1 w - - 4 17
03:57:56.142                                                             📜 Move history updated: 32 moves
03:57:56.142 GameHistoryManager                                          Move added: h8h7
03:57:56.142 GameViewModel                                               🔍 Requesting position evaluation...
03:57:56.142                                                             🎭 Updating personality context for move: h8h7
03:57:56.143                                                             ✨ Personality context updated for move h8h7 - This is revolutionary!
03:57:56.143                                                             🔍 Checking game end conditions...
03:57:56.245                                                             ✅ Game continues - no end condition detected
03:57:56.599                                                             ✅ Evaluation received: 3.93
03:57:56.703 CompetitiveModeActivity                                     📊 Evaluation updated: 3.93
03:57:56.703                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 3.93, emotionalManager: INITIALIZED
03:57:56.703                                                             🎯 Evaluation change: 0.45000005 (threshold: 0.8)
03:57:56.703                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 25s/25s)
03:57:56.703                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:57:57.229 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:58:00.062                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:58:14.639                                                             ViewPostIme pointer 0
03:58:14.640 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=7
03:58:14.640                                                             🎯 SQUARE TAPPED: row=6, col=7
03:58:14.640                                                             📝 Player color: white
03:58:14.640                                                             🔍 Selected row/col: -1/-1
03:58:14.997                                                             🎯 Selected piece: P at 6, 7
03:58:14.997 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:58:14.998                                                             ViewPostIme pointer 1
03:58:14.999                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:58:15.367                                                             ViewPostIme pointer 0
03:58:15.368 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=7
03:58:15.368                                                             🎯 SQUARE TAPPED: row=5, col=7
03:58:15.368                                                             📝 Player color: white
03:58:15.368                                                             🔍 Selected row/col: 6/7
03:58:15.368                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=5, reaches=false
03:58:15.368                                                             🎯 ATTEMPTING MOVE: h2h3
03:58:15.368                                                             📝 Player color: white
03:58:15.368                                                             🔄 Is player's turn: true
03:58:15.368                                                             🔄 Is white's turn: true
03:58:15.368                                                             ✅ Turn validation passed, making move: h2h3
03:58:15.368 GameViewModel                                               🎯 makePlayerMove called with: h2h3
03:58:15.419                                                             🔍 Validating move: h2h3 (attempt 1)
03:58:15.521                                                             📋 Current position: rn1q1r2/1pp3pk/4Q2p/p2PBp2/1b1P2b1/3B1N2/PP3PPP/R3R1K1 w - - 4 17
03:58:15.572                                                             ⚖️ Move h2h3 legality check: LEGAL
03:58:15.572                                                             ✅ Executing validated move: h2h3
03:58:15.776                                                             📍 New position after move: rn1q1r2/1pp3pk/4Q2p/p2PBp2/1b1P2b1/3B1N1P/PP3PP1/R3R1K1 b - - 0 17
03:58:15.778 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:58:15.780 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1r2/1pp3pk/4Q2p/p2PBp2/1b1P2b1/3B1N1P/PP3PP1/R3R1K1 b - - 0 17
03:58:15.787                                                             📜 Move history updated: 33 moves
03:58:15.787 GameHistoryManager                                          Move added: h2h3
03:58:15.884 GameViewModel                                               🔍 Requesting position evaluation...
03:58:15.884                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:58:15.884                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:58:15.884                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:58:15.885                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:58:15.885                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:58:15.885                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:58:15.885                                                             🔧 gameRepository class: GameRepository
03:58:15.885                                                             🔧 Current thread: main
03:58:15.885 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:58:16.492 GameViewModel                                               ✅ Evaluation received: -3.82
03:58:16.592 CompetitiveModeActivity                                     📊 Evaluation updated: -3.82
03:58:16.592                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -3.82, emotionalManager: INITIALIZED
03:58:16.592                                                             🎯 Evaluation change: 7.3 (threshold: 0.8)
03:58:16.592                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 45s/25s)
03:58:16.592                                                             ✅ Emotional reaction triggered for evaluation: -3.82 (trigger: significant_advantage)
03:58:16.593 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:58:16.593                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:58:16.593                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:58:16.593                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
03:58:17.239 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:58:17.271 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8e8
03:58:17.630 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qr3/1pp3pk/4Q2p/p2PBp2/1b1P2b1/3B1N1P/PP3PP1/R3R1K1 w - - 1 18
03:58:17.639                                                             📜 Move history updated: 34 moves
03:58:17.639 GameHistoryManager                                          Move added: f8e8
03:58:17.639 GameViewModel                                               🔍 Requesting position evaluation...
03:58:17.639                                                             🎭 Updating personality context for move: f8e8
03:58:17.639                                                             ✨ Personality context updated for move f8e8 - This is revolutionary!
03:58:17.639                                                             🔍 Checking game end conditions...
03:58:17.741                                                             ✅ Game continues - no end condition detected
03:58:17.997                                                             ✅ Evaluation received: 4.91
03:58:18.040 OpenAIService                                               ✅ Response generated: The board sings with my intentions—every piece in harmony. One precise stroke and the combination wi...
03:58:18.041 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings with my intentions—every piece in harmony. One precise stroke and the combination will blossom; my advantage is not just material, but musical.
03:58:18.044                                                             🎭 Updated emotion indicator: 😏
03:58:18.044                                                             🗣️ Speaking master dialogue: The board sings with my intentions—every piece in harmony. One precise stroke and the combination will blossom; my advantage is not just material, but musical.
03:58:18.044 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:58:18.044                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:58:18.045                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:58:18.045                                                             ✅ Usage context set to: competitive_mode
03:58:18.045 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:58:18.045 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:58:18.045                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:58:18.048 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:58:18.100                                                             📊 Evaluation updated: 4.91
03:58:18.100                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 4.91, emotionalManager: INITIALIZED
03:58:18.100                                                             🎯 Evaluation change: 8.73 (threshold: 0.8)
03:58:18.100                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
03:58:18.101                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:58:18.779 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:58:28.728 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
03:59:00.063 VRI[Compet...y]@bee13b0                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
03:59:03.841                                                             ViewPostIme pointer 0
03:59:03.842 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=3
03:59:03.842                                                             🎯 SQUARE TAPPED: row=5, col=3
03:59:03.842                                                             📝 Player color: white
03:59:03.842                                                             🔍 Selected row/col: -1/-1
03:59:04.200                                                             🎯 Selected piece: B at 5, 3
03:59:04.200 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:59:04.201                                                             ViewPostIme pointer 1
03:59:04.202                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
03:59:04.632                                                             ViewPostIme pointer 0
03:59:04.632 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=5
03:59:04.632                                                             🎯 SQUARE TAPPED: row=3, col=5
03:59:04.632                                                             📝 Player color: white
03:59:04.632                                                             🔍 Selected row/col: 5/3
03:59:04.632                                                             🎯 ATTEMPTING MOVE: d3f5
03:59:04.632                                                             📝 Player color: white
03:59:04.632                                                             🔄 Is player's turn: true
03:59:04.632                                                             🔄 Is white's turn: true
03:59:04.632                                                             ✅ Turn validation passed, making move: d3f5
03:59:04.632 GameViewModel                                               🎯 makePlayerMove called with: d3f5
03:59:04.683                                                             🔍 Validating move: d3f5 (attempt 1)
03:59:04.785                                                             📋 Current position: rn1qr3/1pp3pk/4Q2p/p2PBp2/1b1P2b1/3B1N1P/PP3PP1/R3R1K1 w - - 1 18
03:59:04.837                                                             ⚖️ Move d3f5 legality check: LEGAL
03:59:04.837                                                             ✅ Executing validated move: d3f5
03:59:05.039                                                             📍 New position after move: rn1qr3/1pp3pk/4Q2p/p2PBB2/1b1P2b1/5N1P/PP3PP1/R3R1K1 b - - 0 18
03:59:05.040 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:59:05.041 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qr3/1pp3pk/4Q2p/p2PBB2/1b1P2b1/5N1P/PP3PP1/R3R1K1 b - - 0 18
03:59:05.048                                                             📜 Move history updated: 35 moves
03:59:05.048 GameHistoryManager                                          Move added: d3f5
03:59:05.143 GameViewModel                                               🔍 Requesting position evaluation...
03:59:05.143                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:59:05.144                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:59:05.144                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:59:05.144                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:59:05.144                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:59:05.144                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:59:05.144                                                             🔧 gameRepository class: GameRepository
03:59:05.144                                                             🔧 Current thread: main
03:59:05.144 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:59:05.396 GameViewModel                                               ✅ Evaluation received: -5.02
03:59:05.497 CompetitiveModeActivity                                     📊 Evaluation updated: -5.02
03:59:05.497                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.02, emotionalManager: INITIALIZED
03:59:05.497                                                             🎯 Evaluation change: 1.2 (threshold: 0.8)
03:59:05.497                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 48s/25s)
03:59:05.497                                                             ✅ Emotional reaction triggered for evaluation: -5.02 (trigger: significant_advantage)
03:59:05.497 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:59:05.497                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:59:05.497                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:59:05.497                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
03:59:06.399 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:59:06.430 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g4f5
03:59:06.789 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qr3/1pp3pk/4Q2p/p2PBb2/1b1P4/5N1P/PP3PP1/R3R1K1 w - - 0 19
03:59:06.799                                                             📜 Move history updated: 36 moves
03:59:06.799 GameHistoryManager                                          Move added: g4f5
03:59:06.799 GameViewModel                                               🔍 Requesting position evaluation...
03:59:06.799                                                             🎭 Updating personality context for move: g4f5
03:59:06.799                                                             ✨ Personality context updated for move g4f5 - This is revolutionary!
03:59:06.800                                                             🔍 Checking game end conditions...
03:59:06.901                                                             ✅ Game continues - no end condition detected
03:59:07.001 OpenAIService                                               ✅ Response generated: The board sings in my favor—every piece is a note in a perfect harmony. One precise combination, and...
03:59:07.002 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings in my favor—every piece is a note in a perfect harmony. One precise combination, and the victory will be as beautiful as it is inevitable.
03:59:07.004                                                             🎭 Updated emotion indicator: 😏
03:59:07.004                                                             🗣️ Speaking master dialogue: The board sings in my favor—every piece is a note in a perfect harmony. One precise combination, and the victory will be as beautiful as it is inevitable.
03:59:07.004 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:59:07.004                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:59:07.004                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:59:07.004                                                             ✅ Usage context set to: competitive_mode
03:59:07.004 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:59:07.004 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:59:07.005                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:59:07.006 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
03:59:07.102 GameViewModel                                               ✅ Evaluation received: 5.01
03:59:07.204 CompetitiveModeActivity                                     📊 Evaluation updated: 5.01
03:59:07.205                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 5.01, emotionalManager: INITIALIZED
03:59:07.205                                                             🎯 Evaluation change: 10.030001 (threshold: 0.8)
03:59:07.205                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
03:59:07.205                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:59:08.041 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:59:15.835                                                             ViewPostIme pointer 0
03:59:15.836 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
03:59:15.836                                                             🎯 SQUARE TAPPED: row=2, col=4
03:59:15.836                                                             📝 Player color: white
03:59:15.836                                                             🔍 Selected row/col: -1/-1
03:59:16.192                                                             🎯 Selected piece: Q at 2, 4
03:59:16.192 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:59:16.193                                                             ViewPostIme pointer 1
03:59:16.508                                                             ViewPostIme pointer 0
03:59:16.509 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=5
03:59:16.509                                                             🎯 SQUARE TAPPED: row=3, col=5
03:59:16.509                                                             📝 Player color: white
03:59:16.509                                                             🔍 Selected row/col: 2/4
03:59:16.509                                                             🎯 ATTEMPTING MOVE: e6f5
03:59:16.509                                                             📝 Player color: white
03:59:16.509                                                             🔄 Is player's turn: true
03:59:16.509                                                             🔄 Is white's turn: true
03:59:16.509                                                             ✅ Turn validation passed, making move: e6f5
03:59:16.509 GameViewModel                                               🎯 makePlayerMove called with: e6f5
03:59:16.560                                                             🔍 Validating move: e6f5 (attempt 1)
03:59:16.661                                                             📋 Current position: rn1qr3/1pp3pk/4Q2p/p2PBb2/1b1P4/5N1P/PP3PP1/R3R1K1 w - - 0 19
03:59:16.712                                                             ⚖️ Move e6f5 legality check: LEGAL
03:59:16.712                                                             ✅ Executing validated move: e6f5
03:59:16.914                                                             📍 New position after move: rn1qr3/1pp3pk/7p/p2PBQ2/1b1P4/5N1P/PP3PP1/R3R1K1 b - - 0 19
03:59:16.915 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:59:16.925 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qr3/1pp3pk/7p/p2PBQ2/1b1P4/5N1P/PP3PP1/R3R1K1 b - - 0 19
03:59:16.931                                                             📜 Move history updated: 37 moves
03:59:16.931 GameHistoryManager                                          Move added: e6f5
03:59:16.932 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
03:59:17.022 GameViewModel                                               🔍 Requesting position evaluation...
03:59:17.022                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:59:17.023                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:59:17.023                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:59:17.023                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:59:17.023                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:59:17.023                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:59:17.023                                                             🔧 gameRepository class: GameRepository
03:59:17.023                                                             🔧 Current thread: main
03:59:17.023 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:59:17.325 GameViewModel                                               ✅ Evaluation received: -5.09
03:59:17.426 CompetitiveModeActivity                                     📊 Evaluation updated: -5.09
03:59:17.426                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.09, emotionalManager: INITIALIZED
03:59:17.426                                                             🎯 Evaluation change: 0.07000017 (threshold: 0.8)
03:59:17.426                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 11s/25s)
03:59:17.426                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
03:59:18.328 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:59:18.359 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h7h8
03:59:18.717 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qr2k/1pp3p1/7p/p2PBQ2/1b1P4/5N1P/PP3PP1/R3R1K1 w - - 1 20
03:59:18.724                                                             📜 Move history updated: 38 moves
03:59:18.725 GameHistoryManager                                          Move added: h7h8
03:59:18.725 GameViewModel                                               🔍 Requesting position evaluation...
03:59:18.725                                                             🎭 Updating personality context for move: h7h8
03:59:18.725                                                             ✨ Personality context updated for move h7h8 - This is revolutionary!
03:59:18.726                                                             🔍 Checking game end conditions...
03:59:18.828                                                             ✅ Game continues - no end condition detected
03:59:19.083                                                             ✅ Evaluation received: 5.75
03:59:19.186 CompetitiveModeActivity                                     📊 Evaluation updated: 5.75
03:59:19.186                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 5.75, emotionalManager: INITIALIZED
03:59:19.186                                                             🎯 Evaluation change: 10.77 (threshold: 0.8)
03:59:19.187                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 13s/25s)
03:59:19.187                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
03:59:19.917 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:59:48.164                                                             ViewPostIme pointer 0
03:59:48.165 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=5
03:59:48.165                                                             🎯 SQUARE TAPPED: row=3, col=5
03:59:48.165                                                             📝 Player color: white
03:59:48.165                                                             🔍 Selected row/col: -1/-1
03:59:48.524                                                             🎯 Selected piece: Q at 3, 5
03:59:48.524 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
03:59:48.525                                                             ViewPostIme pointer 1
03:59:49.795                                                             ViewPostIme pointer 0
03:59:49.795 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=6
03:59:49.795                                                             🎯 SQUARE TAPPED: row=2, col=6
03:59:49.795                                                             📝 Player color: white
03:59:49.795                                                             🔍 Selected row/col: 3/5
03:59:49.796                                                             🎯 ATTEMPTING MOVE: f5g6
03:59:49.796                                                             📝 Player color: white
03:59:49.796                                                             🔄 Is player's turn: true
03:59:49.796                                                             🔄 Is white's turn: true
03:59:49.796                                                             ✅ Turn validation passed, making move: f5g6
03:59:49.796 GameViewModel                                               🎯 makePlayerMove called with: f5g6
03:59:49.846                                                             🔍 Validating move: f5g6 (attempt 1)
03:59:49.947                                                             📋 Current position: rn1qr2k/1pp3p1/7p/p2PBQ2/1b1P4/5N1P/PP3PP1/R3R1K1 w - - 1 20
03:59:49.999                                                             ⚖️ Move f5g6 legality check: LEGAL
03:59:49.999                                                             ✅ Executing validated move: f5g6
03:59:50.201                                                             📍 New position after move: rn1qr2k/1pp3p1/6Qp/p2PB3/1b1P4/5N1P/PP3PP1/R3R1K1 b - - 2 20
03:59:50.203 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
03:59:50.205 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qr2k/1pp3p1/6Qp/p2PB3/1b1P4/5N1P/PP3PP1/R3R1K1 b - - 2 20
03:59:50.213                                                             📜 Move history updated: 39 moves
03:59:50.213 GameHistoryManager                                          Move added: f5g6
03:59:50.303 GameViewModel                                               🔍 Requesting position evaluation...
03:59:50.304                                                             🎭 Using PERSONALITY ENGINE for move calculation!
03:59:50.304                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
03:59:50.304                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
03:59:50.304                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
03:59:50.304                                                             🔧 DEBUG: gameRepository instance = NOT NULL
03:59:50.304                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
03:59:50.304                                                             🔧 gameRepository class: GameRepository
03:59:50.304                                                             🔧 Current thread: main
03:59:50.304 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
03:59:50.606 GameViewModel                                               ✅ Evaluation received: -5.79
03:59:50.707 CompetitiveModeActivity                                     📊 Evaluation updated: -5.79
03:59:50.707                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.79, emotionalManager: INITIALIZED
03:59:50.707                                                             🎯 Evaluation change: 0.77 (threshold: 0.8)
03:59:50.707                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 45s/25s)
03:59:50.707                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
03:59:51.703 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
03:59:51.732 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e8e7
03:59:52.094 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q3k/1pp1r1p1/6Qp/p2PB3/1b1P4/5N1P/PP3PP1/R3R1K1 w - - 3 21
03:59:52.104                                                             📜 Move history updated: 40 moves
03:59:52.104 GameHistoryManager                                          Move added: e8e7
03:59:52.104 GameViewModel                                               🔍 Requesting position evaluation...
03:59:52.104                                                             🎭 Updating personality context for move: e8e7
03:59:52.105                                                             ✨ Personality context updated for move e8e7 - This is revolutionary!
03:59:52.105                                                             🔍 Checking game end conditions...
03:59:52.207                                                             ✅ Game continues - no end condition detected
03:59:52.509                                                             ✅ Evaluation received: 5.81
03:59:52.612 CompetitiveModeActivity                                     📊 Evaluation updated: 5.81
03:59:52.613                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 5.81, emotionalManager: INITIALIZED
03:59:52.613                                                             🎯 Evaluation change: 10.83 (threshold: 0.8)
03:59:52.613                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 47s/25s)
03:59:52.613                                                             ✅ Emotional reaction triggered for evaluation: 5.81 (trigger: significant_disadvantage)
03:59:52.614 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
03:59:52.614                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:59:52.614                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:59:52.615                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
03:59:53.203 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
03:59:53.772 OpenAIService                                               ✅ Response generated: A difficult position is a composer’s challenge—here, I seek not only survival but the beauty of a hi...
03:59:53.772 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: A difficult position is a composer’s challenge—here, I seek not only survival but the beauty of a hidden combination. My opponents find only chaos; I find harmony in the calculation.
03:59:53.777                                                             🎭 Updated emotion indicator: 😤
03:59:53.777                                                             🗣️ Speaking master dialogue: A difficult position is a composer’s challenge—here, I seek not only survival but the beauty of a hidden combination. My opponents find only chaos; I find harmony in the calculation.
03:59:53.777 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
03:59:53.777                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
03:59:53.777                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
03:59:53.778                                                             ✅ Usage context set to: competitive_mode
03:59:53.778 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
03:59:53.778 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
03:59:53.778                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
03:59:53.781 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
04:00:00.057 VRI[Compet...y]@bee13b0                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
04:00:01.127                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
04:00:05.512                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
04:00:05.647                                                             handleResized, frames=ClientWindowFrames{frame=[0,0][1440,3088] display=[0,0][1440,3088] parentFrame=[0,0][0,0]} displayId=0 dragResizing=false compatScale=1.0 frameChanged=false attachedFrameChanged=false configChanged=true displayChanged=false compatScaleChanged=false dragResizingChanged=false
04:00:05.647                                                             performConfigurationChange setNightDimText nightDimLevel=38
04:00:05.647                                                             handleResized mSyncSeqId = 0
04:00:05.647                                                             reportNextDraw android.view.ViewRootImpl.handleResized:2864 android.view.ViewRootImpl.-$$Nest$mhandleResized:0 android.view.ViewRootImpl$W.resized:13691 android.app.servertransaction.WindowStateResizeItem.execute:64 android.app.servertransaction.WindowStateTransactionItem.execute:59 
04:00:05.681                                                             Relayout returned: old=(0,0,1440,3088) new=(0,0,1440,3088) relayoutAsync=true req=(1440,3088)0 dur=0 res=0x0 s={true 0xb400007226a6e800} ch=false seqId=0
04:00:05.682                                                             Setup new sync=wmsSync-VRI[CompetitiveModeActivity]@bee13b0#8
04:00:05.682                                                             Creating new active sync group VRI[CompetitiveModeActivity]@bee13b0#9
04:00:05.684                                                             registerCallbacksForSync syncBuffer=false
04:00:05.687                                                             Received frameDrawingCallback syncResult=0 frameNum=4711.
04:00:05.687                                                             mWNT: t=0xb4000070f6375680 mBlastBufferQueue=0xb4000070f627cb80 fn= 4711 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
04:00:05.687                                                             Setting up sync and frameCommitCallback
04:00:05.694                                                             Received frameCommittedCallback lastAttemptedDrawFrameNum=4711 didProduceBuffer=true
04:00:05.694                                                             reportDrawFinished seqId=0
04:00:05.694                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
04:00:05.847 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
04:00:09.563 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 0
04:00:09.563 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
04:00:09.563                                                             🎯 SQUARE TAPPED: row=3, col=4
04:00:09.563                                                             📝 Player color: white
04:00:09.563                                                             🔍 Selected row/col: -1/-1
04:00:09.922                                                             🎯 Selected piece: B at 3, 4
04:00:09.922 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:00:09.923                                                             ViewPostIme pointer 1
04:00:10.266                                                             ViewPostIme pointer 0
04:00:10.266 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=6
04:00:10.266                                                             🎯 SQUARE TAPPED: row=1, col=6
04:00:10.266                                                             📝 Player color: white
04:00:10.266                                                             🔍 Selected row/col: 3/4
04:00:10.267                                                             🎯 ATTEMPTING MOVE: e5g7
04:00:10.267                                                             📝 Player color: white
04:00:10.267                                                             🔄 Is player's turn: true
04:00:10.267                                                             🔄 Is white's turn: true
04:00:10.267                                                             ✅ Turn validation passed, making move: e5g7
04:00:10.267 GameViewModel                                               🎯 makePlayerMove called with: e5g7
04:00:10.318                                                             🔍 Validating move: e5g7 (attempt 1)
04:00:10.419                                                             📋 Current position: rn1q3k/1pp1r1p1/6Qp/p2PB3/1b1P4/5N1P/PP3PP1/R3R1K1 w - - 3 21
04:00:10.470                                                             ⚖️ Move e5g7 legality check: LEGAL
04:00:10.470                                                             ✅ Executing validated move: e5g7
04:00:10.673                                                             📍 New position after move: rn1q3k/1pp1r1B1/6Qp/p2P4/1b1P4/5N1P/PP3PP1/R3R1K1 b - - 0 21
04:00:10.674 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:00:10.675 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q3k/1pp1r1B1/6Qp/p2P4/1b1P4/5N1P/PP3PP1/R3R1K1 b - - 0 21
04:00:10.681                                                             📜 Move history updated: 41 moves
04:00:10.681 GameHistoryManager                                          Move added: e5g7
04:00:10.780 GameViewModel                                               🔍 Requesting position evaluation...
04:00:10.780                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:00:10.780                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:00:10.780                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:00:10.781                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:00:10.781                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:00:10.781                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:00:10.781                                                             🔧 gameRepository class: GameRepository
04:00:10.781                                                             🔧 Current thread: main
04:00:10.781 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:00:11.082 GameViewModel                                               ✅ Evaluation received: -5.71
04:00:11.183 CompetitiveModeActivity                                     📊 Evaluation updated: -5.71
04:00:11.183                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.71, emotionalManager: INITIALIZED
04:00:11.183                                                             🎯 Evaluation change: 11.52 (threshold: 0.8)
04:00:11.183                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 18s/25s)
04:00:11.183                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
04:00:12.090 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
04:00:12.122 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7g7
04:00:12.481 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q3k/1pp3r1/6Qp/p2P4/1b1P4/5N1P/PP3PP1/R3R1K1 w - - 0 22
04:00:12.491                                                             📜 Move history updated: 42 moves
04:00:12.491 GameHistoryManager                                          Move added: e7g7
04:00:12.491 GameViewModel                                               🔍 Requesting position evaluation...
04:00:12.491                                                             🎭 Updating personality context for move: e7g7
04:00:12.491                                                             ✨ Personality context updated for move e7g7 - This is revolutionary!
04:00:12.492                                                             🔍 Checking game end conditions...
04:00:12.594                                                             ✅ Game continues - no end condition detected
04:00:12.844                                                             ✅ Evaluation received: 5.72
04:00:12.947 CompetitiveModeActivity                                     📊 Evaluation updated: 5.72
04:00:12.947                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 5.72, emotionalManager: INITIALIZED
04:00:12.947                                                             🎯 Evaluation change: 0.09000015 (threshold: 0.8)
04:00:12.947                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 20s/25s)
04:00:12.947                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
04:00:13.674 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:00:19.909                                                             ViewPostIme pointer 0
04:00:19.909 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=4
04:00:19.910                                                             🎯 SQUARE TAPPED: row=7, col=4
04:00:19.910                                                             📝 Player color: white
04:00:19.910                                                             🔍 Selected row/col: -1/-1
04:00:20.268                                                             🎯 Selected piece: R at 7, 4
04:00:20.268 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:00:20.270                                                             ViewPostIme pointer 1
04:00:20.694                                                             ViewPostIme pointer 0
04:00:20.695 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=4
04:00:20.695                                                             🎯 SQUARE TAPPED: row=0, col=4
04:00:20.695                                                             📝 Player color: white
04:00:20.695                                                             🔍 Selected row/col: 7/4
04:00:20.695                                                             🎯 ATTEMPTING MOVE: e1e8
04:00:20.695                                                             📝 Player color: white
04:00:20.695                                                             🔄 Is player's turn: true
04:00:20.695                                                             🔄 Is white's turn: true
04:00:20.695                                                             ✅ Turn validation passed, making move: e1e8
04:00:20.695 GameViewModel                                               🎯 makePlayerMove called with: e1e8
04:00:20.746                                                             🔍 Validating move: e1e8 (attempt 1)
04:00:20.847                                                             📋 Current position: rn1q3k/1pp3r1/6Qp/p2P4/1b1P4/5N1P/PP3PP1/R3R1K1 w - - 0 22
04:00:20.898                                                             ⚖️ Move e1e8 legality check: LEGAL
04:00:20.898                                                             ✅ Executing validated move: e1e8
04:00:21.101                                                             📍 New position after move: rn1qR2k/1pp3r1/6Qp/p2P4/1b1P4/5N1P/PP3PP1/R5K1 b - - 1 22
04:00:21.102 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:00:21.103 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qR2k/1pp3r1/6Qp/p2P4/1b1P4/5N1P/PP3PP1/R5K1 b - - 1 22
04:00:21.111                                                             📜 Move history updated: 43 moves
04:00:21.111 GameHistoryManager                                          Move added: e1e8
04:00:21.203 GameViewModel                                               🔍 Requesting position evaluation...
04:00:21.203                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:00:21.204                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:00:21.204                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:00:21.204                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:00:21.204                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:00:21.204                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:00:21.204                                                             🔧 gameRepository class: GameRepository
04:00:21.204                                                             🔧 Current thread: main
04:00:21.204 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:00:21.606 GameViewModel                                               ✅ Evaluation received: -5.88
04:00:21.707 CompetitiveModeActivity                                     📊 Evaluation updated: -5.88
04:00:21.707                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.88, emotionalManager: INITIALIZED
04:00:21.707                                                             🎯 Evaluation change: 11.690001 (threshold: 0.8)
04:00:21.707                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 29s/25s)
04:00:21.707                                                             ✅ Emotional reaction triggered for evaluation: -5.88 (trigger: significant_advantage)
04:00:21.707 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
04:00:21.707                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:00:21.707                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:00:21.708                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
04:00:22.993                                                             ✅ Response generated: At -5.88, the board sings my tune—every piece a note in a perfect harmony of attack. Now, to weave t...
04:00:22.994 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: At -5.88, the board sings my tune—every piece a note in a perfect harmony of attack. Now, to weave the final combination and let my creativity decide the outcome.
04:00:22.994                                                             🎭 Updated emotion indicator: 😏
04:00:22.994                                                             🗣️ Speaking master dialogue: At -5.88, the board sings my tune—every piece a note in a perfect harmony of attack. Now, to weave the final combination and let my creativity decide the outcome.
04:00:22.994 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
04:00:22.994                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
04:00:22.994                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
04:00:22.994                                                             ✅ Usage context set to: competitive_mode
04:00:22.995 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:00:22.995 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
04:00:22.995                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
04:00:22.995 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
04:00:23.369 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
04:00:23.400 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d8e8
04:00:23.759 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn2q2k/1pp3r1/6Qp/p2P4/1b1P4/5N1P/PP3PP1/R5K1 w - - 0 23
04:00:23.769                                                             📜 Move history updated: 44 moves
04:00:23.769 GameHistoryManager                                          Move added: d8e8
04:00:23.769 GameViewModel                                               🔍 Requesting position evaluation...
04:00:23.769                                                             🎭 Updating personality context for move: d8e8
04:00:23.769                                                             ✨ Personality context updated for move d8e8 - This is revolutionary!
04:00:23.769                                                             🔍 Checking game end conditions...
04:00:23.871                                                             ✅ Game continues - no end condition detected
04:00:24.102 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:00:24.125 GameViewModel                                               ✅ Evaluation received: 5.64
04:00:24.229 CompetitiveModeActivity                                     📊 Evaluation updated: 5.64
04:00:24.229                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 5.64, emotionalManager: INITIALIZED
04:00:24.229                                                             🎯 Evaluation change: 11.52 (threshold: 0.8)
04:00:24.229                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 2s/25s)
04:00:24.229                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
04:00:26.385 VRI[Compet...y]@bee13b0                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
04:00:31.205                                                             ViewPostIme pointer 0
04:00:31.205 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=6
04:00:31.205                                                             🎯 SQUARE TAPPED: row=2, col=6
04:00:31.205                                                             📝 Player color: white
04:00:31.205                                                             🔍 Selected row/col: -1/-1
04:00:31.561                                                             🎯 Selected piece: Q at 2, 6
04:00:31.562 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:00:31.562                                                             ViewPostIme pointer 1
04:00:31.562                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
04:00:31.868                                                             ViewPostIme pointer 0
04:00:31.868 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=4
04:00:31.868                                                             🎯 SQUARE TAPPED: row=0, col=4
04:00:31.868                                                             📝 Player color: white
04:00:31.869                                                             🔍 Selected row/col: 2/6
04:00:31.869                                                             🎯 ATTEMPTING MOVE: g6e8
04:00:31.869                                                             📝 Player color: white
04:00:31.869                                                             🔄 Is player's turn: true
04:00:31.869                                                             🔄 Is white's turn: true
04:00:31.869                                                             ✅ Turn validation passed, making move: g6e8
04:00:31.869 GameViewModel                                               🎯 makePlayerMove called with: g6e8
04:00:31.920                                                             🔍 Validating move: g6e8 (attempt 1)
04:00:32.021                                                             📋 Current position: rn2q2k/1pp3r1/6Qp/p2P4/1b1P4/5N1P/PP3PP1/R5K1 w - - 0 23
04:00:32.072                                                             ⚖️ Move g6e8 legality check: LEGAL
04:00:32.072                                                             ✅ Executing validated move: g6e8
04:00:32.274                                                             📍 New position after move: rn2Q2k/1pp3r1/7p/p2P4/1b1P4/5N1P/PP3PP1/R5K1 b - - 0 23
04:00:32.275 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:00:32.277 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn2Q2k/1pp3r1/7p/p2P4/1b1P4/5N1P/PP3PP1/R5K1 b - - 0 23
04:00:32.283                                                             📜 Move history updated: 45 moves
04:00:32.283 GameHistoryManager                                          Move added: g6e8
04:00:32.378 GameViewModel                                               🔍 Requesting position evaluation...
04:00:32.378                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:00:32.378                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:00:32.378                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:00:32.378                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:00:32.378                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:00:32.378                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:00:32.378                                                             🔧 gameRepository class: GameRepository
04:00:32.378                                                             🔧 Current thread: main
04:00:32.378 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:00:32.631 GameViewModel                                               ✅ Evaluation received: -5.83
04:00:32.732 CompetitiveModeActivity                                     📊 Evaluation updated: -5.83
04:00:32.732                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.83, emotionalManager: INITIALIZED
04:00:32.732                                                             🎯 Evaluation change: 0.05000019 (threshold: 0.8)
04:00:32.732                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 11s/25s)
04:00:32.732                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
04:00:33.692 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
04:00:33.726 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g7g8
04:00:34.083 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn2Q1rk/1pp5/7p/p2P4/1b1P4/5N1P/PP3PP1/R5K1 w - - 1 24
04:00:34.093                                                             📜 Move history updated: 46 moves
04:00:34.093 GameHistoryManager                                          Move added: g7g8
04:00:34.093 GameViewModel                                               🔍 Requesting position evaluation...
04:00:34.093                                                             🎭 Updating personality context for move: g7g8
04:00:34.093                                                             ✨ Personality context updated for move g7g8 - This is revolutionary!
04:00:34.093                                                             🔍 Checking game end conditions...
04:00:34.194                                                             ✅ Game continues - no end condition detected
04:00:34.219 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
04:00:34.401 GameViewModel                                               ✅ Evaluation received: 5.91
04:00:34.504 CompetitiveModeActivity                                     📊 Evaluation updated: 5.91
04:00:34.504                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 5.91, emotionalManager: INITIALIZED
04:00:34.504                                                             🎯 Evaluation change: 11.79 (threshold: 0.8)
04:00:34.505                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 12s/25s)
04:00:34.505                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
04:00:35.277 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:00:52.867                                                             ViewPostIme pointer 0
04:00:52.868 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=4
04:00:52.868                                                             🎯 SQUARE TAPPED: row=0, col=4
04:00:52.868                                                             📝 Player color: white
04:00:52.868                                                             🔍 Selected row/col: -1/-1
04:00:53.228                                                             🎯 Selected piece: Q at 0, 4
04:00:53.228 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:00:53.230                                                             ViewPostIme pointer 1
04:00:53.568                                                             ViewPostIme pointer 0
04:00:53.569 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=7
04:00:53.569                                                             🎯 SQUARE TAPPED: row=3, col=7
04:00:53.569                                                             📝 Player color: white
04:00:53.569                                                             🔍 Selected row/col: 0/4
04:00:53.569                                                             🎯 ATTEMPTING MOVE: e8h5
04:00:53.569                                                             📝 Player color: white
04:00:53.569                                                             🔄 Is player's turn: true
04:00:53.569                                                             🔄 Is white's turn: true
04:00:53.569                                                             ✅ Turn validation passed, making move: e8h5
04:00:53.569 GameViewModel                                               🎯 makePlayerMove called with: e8h5
04:00:53.620                                                             🔍 Validating move: e8h5 (attempt 1)
04:00:53.721                                                             📋 Current position: rn2Q1rk/1pp5/7p/p2P4/1b1P4/5N1P/PP3PP1/R5K1 w - - 1 24
04:00:53.772                                                             ⚖️ Move e8h5 legality check: LEGAL
04:00:53.772                                                             ✅ Executing validated move: e8h5
04:00:53.973                                                             📍 New position after move: rn4rk/1pp5/7p/p2P3Q/1b1P4/5N1P/PP3PP1/R5K1 b - - 2 24
04:00:53.975 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:00:53.976 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn4rk/1pp5/7p/p2P3Q/1b1P4/5N1P/PP3PP1/R5K1 b - - 2 24
04:00:53.983                                                             📜 Move history updated: 47 moves
04:00:53.983 GameHistoryManager                                          Move added: e8h5
04:00:54.076 GameViewModel                                               🔍 Requesting position evaluation...
04:00:54.076                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:00:54.076                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:00:54.076                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:00:54.077                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:00:54.077                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:00:54.077                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:00:54.077                                                             🔧 gameRepository class: GameRepository
04:00:54.077                                                             🔧 Current thread: main
04:00:54.077 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:00:54.378 GameViewModel                                               ✅ Evaluation received: -5.85
04:00:54.479 CompetitiveModeActivity                                     📊 Evaluation updated: -5.85
04:00:54.479                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.85, emotionalManager: INITIALIZED
04:00:54.479                                                             🎯 Evaluation change: 0.03000021 (threshold: 0.8)
04:00:54.479                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 32s/25s)
04:00:54.479                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
04:00:55.387 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
04:00:55.422 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h8h7
04:00:55.781 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn4r1/1pp4k/7p/p2P3Q/1b1P4/5N1P/PP3PP1/R5K1 w - - 3 25
04:00:55.791                                                             📜 Move history updated: 48 moves
04:00:55.791 GameHistoryManager                                          Move added: h8h7
04:00:55.791 GameViewModel                                               🔍 Requesting position evaluation...
04:00:55.791                                                             🎭 Updating personality context for move: h8h7
04:00:55.791                                                             ✨ Personality context updated for move h8h7 - This is revolutionary!
04:00:55.791                                                             🔍 Checking game end conditions...
04:00:55.893                                                             ✅ Game continues - no end condition detected
04:00:56.099                                                             ✅ Evaluation received: 6.62
04:00:56.202 CompetitiveModeActivity                                     📊 Evaluation updated: 6.62
04:00:56.202                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 6.62, emotionalManager: INITIALIZED
04:00:56.202                                                             🎯 Evaluation change: 12.5 (threshold: 0.8)
04:00:56.202                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 34s/25s)
04:00:56.202                                                             ✅ Emotional reaction triggered for evaluation: 6.62 (trigger: significant_disadvantage)
04:00:56.203 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
04:00:56.203                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:00:56.203                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:00:56.204                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
04:00:56.976 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:00:57.644 OpenAIService                                               ✅ Response generated: (24) The position is fierce, but I sense the hidden harmony of my pieces. One deep combination will ...
04:00:57.644 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: (24) The position is fierce, but I sense the hidden harmony of my pieces. One deep combination will turn the tide—chess rewards the bold and imaginative.
04:00:57.646                                                             🎭 Updated emotion indicator: 😤
04:00:57.646                                                             🗣️ Speaking master dialogue: (24) The position is fierce, but I sense the hidden harmony of my pieces. One deep combination will turn the tide—chess rewards the bold and imaginative.
04:00:57.647 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
04:00:57.647                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
04:00:57.647                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
04:00:57.647                                                             ✅ Usage context set to: competitive_mode
04:00:57.647 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:00:57.647 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
04:00:57.647                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
04:00:57.649 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
04:01:00.054 VRI[Compet...y]@bee13b0                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
04:01:06.405                                                             ViewPostIme pointer 0
04:01:06.405 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
04:01:06.405                                                             🎯 SQUARE TAPPED: row=5, col=5
04:01:06.405                                                             📝 Player color: white
04:01:06.405                                                             🔍 Selected row/col: -1/-1
04:01:06.762                                                             🎯 Selected piece: N at 5, 5
04:01:06.762 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:06.764                                                             ViewPostIme pointer 1
04:01:06.764                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
04:01:07.089                                                             ViewPostIme pointer 0
04:01:07.090 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
04:01:07.090                                                             🎯 SQUARE TAPPED: row=3, col=4
04:01:07.090                                                             📝 Player color: white
04:01:07.090                                                             🔍 Selected row/col: 5/5
04:01:07.090                                                             🎯 ATTEMPTING MOVE: f3e5
04:01:07.090                                                             📝 Player color: white
04:01:07.090                                                             🔄 Is player's turn: true
04:01:07.090                                                             🔄 Is white's turn: true
04:01:07.090                                                             ✅ Turn validation passed, making move: f3e5
04:01:07.090 GameViewModel                                               🎯 makePlayerMove called with: f3e5
04:01:07.141                                                             🔍 Validating move: f3e5 (attempt 1)
04:01:07.242                                                             📋 Current position: rn4r1/1pp4k/7p/p2P3Q/1b1P4/5N1P/PP3PP1/R5K1 w - - 3 25
04:01:07.293                                                             ⚖️ Move f3e5 legality check: LEGAL
04:01:07.293                                                             ✅ Executing validated move: f3e5
04:01:07.495                                                             📍 New position after move: rn4r1/1pp4k/7p/p2PN2Q/1b1P4/7P/PP3PP1/R5K1 b - - 4 25
04:01:07.496 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:01:07.498 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn4r1/1pp4k/7p/p2PN2Q/1b1P4/7P/PP3PP1/R5K1 b - - 4 25
04:01:07.505                                                             📜 Move history updated: 49 moves
04:01:07.505 GameHistoryManager                                          Move added: f3e5
04:01:07.596 GameViewModel                                               🔍 Requesting position evaluation...
04:01:07.597                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:01:07.597                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:01:07.597                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:01:07.597                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:01:07.597                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:01:07.597                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:01:07.597                                                             🔧 gameRepository class: GameRepository
04:01:07.597                                                             🔧 Current thread: main
04:01:07.597 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:01:07.736 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
04:01:07.899 GameViewModel                                               ✅ Evaluation received: -6.00
04:01:08.000 CompetitiveModeActivity                                     📊 Evaluation updated: -6.0
04:01:08.000                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.0, emotionalManager: INITIALIZED
04:01:08.000                                                             🎯 Evaluation change: 12.62 (threshold: 0.8)
04:01:08.000                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 11s/25s)
04:01:08.000                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
04:01:08.965 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
04:01:08.998 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8g5
04:01:09.358 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn6/1pp4k/7p/p2PN1rQ/1b1P4/7P/PP3PP1/R5K1 w - - 5 26
04:01:09.368                                                             📜 Move history updated: 50 moves
04:01:09.368 GameHistoryManager                                          Move added: g8g5
04:01:09.368 GameViewModel                                               🔍 Requesting position evaluation...
04:01:09.368                                                             🎭 Updating personality context for move: g8g5
04:01:09.368                                                             ✨ Personality context updated for move g8g5 - This is revolutionary!
04:01:09.368                                                             🔍 Checking game end conditions...
04:01:09.471                                                             ✅ Game continues - no end condition detected
04:01:09.675                                                             ✅ Evaluation received: 6.23
04:01:09.778 CompetitiveModeActivity                                     📊 Evaluation updated: 6.23
04:01:09.778                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 6.23, emotionalManager: INITIALIZED
04:01:09.778                                                             🎯 Evaluation change: 0.38999987 (threshold: 0.8)
04:01:09.778                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 13s/25s)
04:01:09.778                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
04:01:10.498 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:16.246                                                             ViewPostIme pointer 0
04:01:16.246 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=7
04:01:16.247                                                             🎯 SQUARE TAPPED: row=3, col=7
04:01:16.247                                                             📝 Player color: white
04:01:16.247                                                             🔍 Selected row/col: -1/-1
04:01:16.607                                                             🎯 Selected piece: Q at 3, 7
04:01:16.607 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:16.609                                                             ViewPostIme pointer 1
04:01:16.825                                                             ViewPostIme pointer 0
04:01:16.825 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=5
04:01:16.825                                                             🎯 SQUARE TAPPED: row=1, col=5
04:01:16.825                                                             📝 Player color: white
04:01:16.825                                                             🔍 Selected row/col: 3/7
04:01:16.825                                                             🎯 ATTEMPTING MOVE: h5f7
04:01:16.825                                                             📝 Player color: white
04:01:16.825                                                             🔄 Is player's turn: true
04:01:16.825                                                             🔄 Is white's turn: true
04:01:16.825                                                             ✅ Turn validation passed, making move: h5f7
04:01:16.826 GameViewModel                                               🎯 makePlayerMove called with: h5f7
04:01:16.876                                                             🔍 Validating move: h5f7 (attempt 1)
04:01:16.977                                                             📋 Current position: rn6/1pp4k/7p/p2PN1rQ/1b1P4/7P/PP3PP1/R5K1 w - - 5 26
04:01:17.028                                                             ⚖️ Move h5f7 legality check: LEGAL
04:01:17.028                                                             ✅ Executing validated move: h5f7
04:01:17.230                                                             📍 New position after move: rn6/1pp2Q1k/7p/p2PN1r1/1b1P4/7P/PP3PP1/R5K1 b - - 6 26
04:01:17.231 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:01:17.232 Choreographer                                               Skipped 48 frames!  The application may be doing too much work on its main thread.
04:01:17.233 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn6/1pp2Q1k/7p/p2PN1r1/1b1P4/7P/PP3PP1/R5K1 b - - 6 26
04:01:17.240                                                             📜 Move history updated: 51 moves
04:01:17.240 GameHistoryManager                                          Move added: h5f7
04:01:17.335 GameViewModel                                               🔍 Requesting position evaluation...
04:01:17.335                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:01:17.335                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:01:17.335                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:01:17.336                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:01:17.336                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:01:17.336                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:01:17.336                                                             🔧 gameRepository class: GameRepository
04:01:17.336                                                             🔧 Current thread: main
04:01:17.336 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:01:17.586 GameViewModel                                               ✅ Evaluation received: -6.31
04:01:17.687 CompetitiveModeActivity                                     📊 Evaluation updated: -6.31
04:01:17.687                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.31, emotionalManager: INITIALIZED
04:01:17.687                                                             🎯 Evaluation change: 12.93 (threshold: 0.8)
04:01:17.687                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 21s/25s)
04:01:17.687                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
04:01:18.599 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
04:01:18.634 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g5g7
04:01:18.994 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn6/1pp2Qrk/7p/p2PN3/1b1P4/7P/PP3PP1/R5K1 w - - 7 27
04:01:19.007                                                             📜 Move history updated: 52 moves
04:01:19.007 GameHistoryManager                                          Move added: g5g7
04:01:19.007 GameViewModel                                               🔍 Requesting position evaluation...
04:01:19.007                                                             🎭 Updating personality context for move: g5g7
04:01:19.007                                                             ✨ Personality context updated for move g5g7 - This is revolutionary!
04:01:19.008                                                             🔍 Checking game end conditions...
04:01:19.109                                                             ✅ Game continues - no end condition detected
04:01:19.316                                                             ✅ Evaluation received: 6.08
04:01:19.417 CompetitiveModeActivity                                     📊 Evaluation updated: 6.08
04:01:19.417                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 6.08, emotionalManager: INITIALIZED
04:01:19.417                                                             🎯 Evaluation change: 0.53999996 (threshold: 0.8)
04:01:19.417                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 23s/25s)
04:01:19.418                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
04:01:20.234 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:28.087                                                             ViewPostIme pointer 0
04:01:28.087 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=5
04:01:28.087                                                             🎯 SQUARE TAPPED: row=1, col=5
04:01:28.087                                                             📝 Player color: white
04:01:28.087                                                             🔍 Selected row/col: -1/-1
04:01:28.445                                                             🎯 Selected piece: Q at 1, 5
04:01:28.445 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:28.446                                                             ViewPostIme pointer 1
04:01:28.760                                                             ViewPostIme pointer 0
04:01:28.760 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=5
04:01:28.760                                                             🎯 SQUARE TAPPED: row=3, col=5
04:01:28.760                                                             📝 Player color: white
04:01:28.760                                                             🔍 Selected row/col: 1/5
04:01:28.761                                                             🎯 ATTEMPTING MOVE: f7f5
04:01:28.761                                                             📝 Player color: white
04:01:28.761                                                             🔄 Is player's turn: true
04:01:28.761                                                             🔄 Is white's turn: true
04:01:28.761                                                             ✅ Turn validation passed, making move: f7f5
04:01:28.761 GameViewModel                                               🎯 makePlayerMove called with: f7f5
04:01:28.812                                                             🔍 Validating move: f7f5 (attempt 1)
04:01:28.912                                                             📋 Current position: rn6/1pp2Qrk/7p/p2PN3/1b1P4/7P/PP3PP1/R5K1 w - - 7 27
04:01:28.963                                                             ⚖️ Move f7f5 legality check: LEGAL
04:01:28.963                                                             ✅ Executing validated move: f7f5
04:01:29.166                                                             📍 New position after move: rn6/1pp3rk/7p/p2PNQ2/1b1P4/7P/PP3PP1/R5K1 b - - 8 27
04:01:29.167 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:01:29.168 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn6/1pp3rk/7p/p2PNQ2/1b1P4/7P/PP3PP1/R5K1 b - - 8 27
04:01:29.176                                                             📜 Move history updated: 53 moves
04:01:29.176 GameHistoryManager                                          Move added: f7f5
04:01:29.268 GameViewModel                                               🔍 Requesting position evaluation...
04:01:29.268                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:01:29.268                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:01:29.268                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:01:29.268                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:01:29.268                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:01:29.268                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:01:29.268                                                             🔧 gameRepository class: GameRepository
04:01:29.268                                                             🔧 Current thread: main
04:01:29.268 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:01:29.519 GameViewModel                                               ✅ Evaluation received: -6.47
04:01:29.620 CompetitiveModeActivity                                     📊 Evaluation updated: -6.47
04:01:29.620                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.47, emotionalManager: INITIALIZED
04:01:29.620                                                             🎯 Evaluation change: 13.09 (threshold: 0.8)
04:01:29.620                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 33s/25s)
04:01:29.620                                                             ✅ Emotional reaction triggered for evaluation: -6.47 (trigger: significant_advantage)
04:01:29.620 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
04:01:29.620                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:01:29.620                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:01:29.621                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
04:01:30.581 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
04:01:30.617 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h7g8
04:01:30.976 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn4k1/1pp3r1/7p/p2PNQ2/1b1P4/7P/PP3PP1/R5K1 w - - 9 28
04:01:30.985                                                             📜 Move history updated: 54 moves
04:01:30.985 GameHistoryManager                                          Move added: h7g8
04:01:30.985 GameViewModel                                               🔍 Requesting position evaluation...
04:01:30.985                                                             🎭 Updating personality context for move: h7g8
04:01:30.985                                                             ✨ Personality context updated for move h7g8 - This is revolutionary!
04:01:30.985                                                             🔍 Checking game end conditions...
04:01:31.087                                                             ✅ Game continues - no end condition detected
04:01:31.118 OpenAIService                                               ✅ Response generated: The board sings in my favor—every piece is a note in a perfect harmony. Now, I shall unleash the com...
04:01:31.119 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings in my favor—every piece is a note in a perfect harmony. Now, I shall unleash the combination that turns beauty into victory.
04:01:31.122                                                             🎭 Updated emotion indicator: 😏
04:01:31.123                                                             🗣️ Speaking master dialogue: The board sings in my favor—every piece is a note in a perfect harmony. Now, I shall unleash the combination that turns beauty into victory.
04:01:31.123 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
04:01:31.123                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
04:01:31.123                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
04:01:31.123                                                             ✅ Usage context set to: competitive_mode
04:01:31.123 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:01:31.124 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
04:01:31.124                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
04:01:31.127 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
04:01:31.292 GameViewModel                                               ✅ Evaluation received: 6.47
04:01:31.394 CompetitiveModeActivity                                     📊 Evaluation updated: 6.47
04:01:31.395                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 6.47, emotionalManager: INITIALIZED
04:01:31.395                                                             🎯 Evaluation change: 12.94 (threshold: 0.8)
04:01:31.395                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
04:01:31.395                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
04:01:32.170 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:39.108 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
04:01:39.321 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 0
04:01:39.321 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=5
04:01:39.322                                                             🎯 SQUARE TAPPED: row=3, col=5
04:01:39.322                                                             📝 Player color: white
04:01:39.322                                                             🔍 Selected row/col: -1/-1
04:01:39.681                                                             🎯 Selected piece: Q at 3, 5
04:01:39.681 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:39.682                                                             ViewPostIme pointer 1
04:01:40.755                                                             ViewPostIme pointer 0
04:01:40.755 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=2
04:01:40.755                                                             🎯 SQUARE TAPPED: row=0, col=2
04:01:40.755                                                             📝 Player color: white
04:01:40.755                                                             🔍 Selected row/col: 3/5
04:01:40.755                                                             🎯 ATTEMPTING MOVE: f5c8
04:01:40.755                                                             📝 Player color: white
04:01:40.755                                                             🔄 Is player's turn: true
04:01:40.755                                                             🔄 Is white's turn: true
04:01:40.755                                                             ✅ Turn validation passed, making move: f5c8
04:01:40.755 GameViewModel                                               🎯 makePlayerMove called with: f5c8
04:01:40.806                                                             🔍 Validating move: f5c8 (attempt 1)
04:01:40.907                                                             📋 Current position: rn4k1/1pp3r1/7p/p2PNQ2/1b1P4/7P/PP3PP1/R5K1 w - - 9 28
04:01:40.958                                                             ⚖️ Move f5c8 legality check: LEGAL
04:01:40.958                                                             ✅ Executing validated move: f5c8
04:01:41.161                                                             📍 New position after move: rnQ3k1/1pp3r1/7p/p2PN3/1b1P4/7P/PP3PP1/R5K1 b - - 10 28
04:01:41.162 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:01:41.165 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnQ3k1/1pp3r1/7p/p2PN3/1b1P4/7P/PP3PP1/R5K1 b - - 10 28
04:01:41.173                                                             📜 Move history updated: 55 moves
04:01:41.173 GameHistoryManager                                          Move added: f5c8
04:01:41.270 GameViewModel                                               🔍 Requesting position evaluation...
04:01:41.271                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:01:41.271                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:01:41.271                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:01:41.271                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:01:41.271                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:01:41.271                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:01:41.271                                                             🔧 gameRepository class: GameRepository
04:01:41.271                                                             🔧 Current thread: main
04:01:41.271 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:01:41.522 GameViewModel                                               ✅ Evaluation received: -6.64
04:01:41.623 CompetitiveModeActivity                                     📊 Evaluation updated: -6.64
04:01:41.623                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.64, emotionalManager: INITIALIZED
04:01:41.623                                                             🎯 Evaluation change: 0.17000008 (threshold: 0.8)
04:01:41.623                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 12s/25s)
04:01:41.623                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
04:01:42.533 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
04:01:42.569 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b4f8
04:01:42.930 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnQ2bk1/1pp3r1/7p/p2PN3/3P4/7P/PP3PP1/R5K1 w - - 11 29
04:01:42.940                                                             📜 Move history updated: 56 moves
04:01:42.940 GameHistoryManager                                          Move added: b4f8
04:01:42.940 GameViewModel                                               🔍 Requesting position evaluation...
04:01:42.940                                                             🎭 Updating personality context for move: b4f8
04:01:42.940                                                             ✨ Personality context updated for move b4f8 - This is revolutionary!
04:01:42.941                                                             🔍 Checking game end conditions...
04:01:43.043                                                             ✅ Game continues - no end condition detected
04:01:43.248                                                             ✅ Evaluation received: 7.05
04:01:43.351 CompetitiveModeActivity                                     📊 Evaluation updated: 7.05
04:01:43.351                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 7.05, emotionalManager: INITIALIZED
04:01:43.351                                                             🎯 Evaluation change: 13.52 (threshold: 0.8)
04:01:43.352                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 13s/25s)
04:01:43.352                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
04:01:44.165 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:56.921                                                             ViewPostIme pointer 0
04:01:56.921 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=2
04:01:56.921                                                             🎯 SQUARE TAPPED: row=0, col=2
04:01:56.921                                                             📝 Player color: white
04:01:56.921                                                             🔍 Selected row/col: -1/-1
04:01:57.279                                                             🎯 Selected piece: Q at 0, 2
04:01:57.279 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@bee13b0
04:01:57.280                                                             ViewPostIme pointer 1
04:01:57.783                                                             ViewPostIme pointer 0
04:01:57.783 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=1
04:01:57.783                                                             🎯 SQUARE TAPPED: row=1, col=1
04:01:57.783                                                             📝 Player color: white
04:01:57.783                                                             🔍 Selected row/col: 0/2
04:01:57.784                                                             🎯 ATTEMPTING MOVE: c8b7
04:01:57.784                                                             📝 Player color: white
04:01:57.784                                                             🔄 Is player's turn: true
04:01:57.784                                                             🔄 Is white's turn: true
04:01:57.784                                                             ✅ Turn validation passed, making move: c8b7
04:01:57.784 GameViewModel                                               🎯 makePlayerMove called with: c8b7
04:01:57.835                                                             🔍 Validating move: c8b7 (attempt 1)
04:01:57.935                                                             📋 Current position: rnQ2bk1/1pp3r1/7p/p2PN3/3P4/7P/PP3PP1/R5K1 w - - 11 29
04:01:57.987                                                             ⚖️ Move c8b7 legality check: LEGAL
04:01:57.987                                                             ✅ Executing validated move: c8b7
04:01:58.190                                                             📍 New position after move: rn3bk1/1Qp3r1/7p/p2PN3/3P4/7P/PP3PP1/R5K1 b - - 0 29
04:01:58.191 VRI[Compet...y]@bee13b0                                     ViewPostIme pointer 1
04:01:58.193 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn3bk1/1Qp3r1/7p/p2PN3/3P4/7P/PP3PP1/R5K1 b - - 0 29
04:01:58.201                                                             📜 Move history updated: 57 moves
04:01:58.201 GameHistoryManager                                          Move added: c8b7
04:01:58.291 GameViewModel                                               🔍 Requesting position evaluation...
04:01:58.292                                                             🎭 Using PERSONALITY ENGINE for move calculation!
04:01:58.292                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
04:01:58.292                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
04:01:58.292                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
04:01:58.292                                                             🔧 DEBUG: gameRepository instance = NOT NULL
04:01:58.292                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
04:01:58.292                                                             🔧 gameRepository class: GameRepository
04:01:58.292                                                             🔧 Current thread: main
04:01:58.292 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
04:01:58.593 GameViewModel                                               ✅ Evaluation received: -6.65
04:01:58.694 CompetitiveModeActivity                                     📊 Evaluation updated: -6.65
04:01:58.694                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.65, emotionalManager: INITIALIZED
04:01:58.694                                                             🎯 Evaluation change: 0.1800003 (threshold: 0.8)
04:01:58.694                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 29s/25s)
04:01:58.694                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
04:01:59.604 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
04:01:59.648 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c7c5
04:02:00.006 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn3bk1/1Q4r1/7p/p1pPN3/3P4/7P/PP3PP1/R5K1 w - c6 0 30
04:02:00.014                                                             📜 Move history updated: 58 moves
04:02:00.014 GameHistoryManager                                          Move added: c7c5
04:02:00.014 GameViewModel                                               🔍 Requesting position evaluation...
04:02:00.015                                                             🎭 Updating personality context for move: c7c5
04:02:00.015                                                             ✨ Personality context updated for move c7c5 - This is revolutionary!
04:02:00.015                                                             🔍 Checking game end conditions...
04:02:00.116                                                             ✅ Game continues - no end condition detected
04:02:00.323                                                             ✅ Evaluation received: 7.78
04:02:00.426 CompetitiveModeActivity                                     📊 Evaluation updated: 7.78
04:02:00.426                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 7.78, emotionalManager: INITIALIZED
04:02:00.426                                                             🎯 Evaluation change: 14.25 (threshold: 0.8)
04:02:00.426                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 30s/25s)
04:02:00.426                                                             ✅ Emotional reaction triggered for evaluation: 7.78 (trigger: significant_disadvantage)
04:02:00.427 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
04:02:00.427                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:02:00.427                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:02:00.428                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
04:02:01.192 VRI[Compet...y]@bee13b0                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@bee13b0
04:02:01.258 OpenAIService                                               ✅ Response generated: (29) This adversity stirs my deepest creativity—expect a storm of combinations. My pieces will find ...
04:02:01.258 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: (29) This adversity stirs my deepest creativity—expect a storm of combinations. My pieces will find harmony even in chaos.
04:02:01.261                                                             🎭 Updated emotion indicator: 😤
04:02:01.261                                                             🗣️ Speaking master dialogue: (29) This adversity stirs my deepest creativity—expect a storm of combinations. My pieces will find harmony even in chaos.
04:02:01.262 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
04:02:01.262                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
04:02:01.262                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
04:02:01.262                                                             ✅ Usage context set to: competitive_mode
04:02:01.262 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
04:02:01.262 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
04:02:01.262                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
04:02:01.264 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
04:02:09.863                                                             🗣️ Master dialogue speech completed
