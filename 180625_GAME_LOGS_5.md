23:38:30.091 ActivityThread          com.example.chesspedagogue          com.example.chesspedagogue will use render engine as VK
23:38:30.105 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@3eec19a
23:38:30.159 ScrollView                                                  initGoToTop
23:38:30.166                                                             initGoToTop
23:38:30.171 InputMethodManager                                          invalidateInput
23:38:30.172                                                             invalidateInput
23:38:30.175 LogThrottlerConfig                                          📊 Log throttling initialized: VERBOSE
23:38:30.175 ApiKeyConfig                                                Using API key from ApiKeys class
23:38:30.188 OpenAIService                                               API key set, length: 164
23:38:30.188 ApiKeyConfig                                                Initialized OpenAIClient with API key from config
23:38:30.189 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.189                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.189 ApiKeyConfig                                                Using API key from ApiKeys class
23:38:30.189 OpenAIService                                               API key set, length: 164
23:38:30.189 ApiKeyConfig                                                Initialized OpenAIClient with API key from config
23:38:30.189 TTSServiceManager                                           ✅ Switched to ElevenLabs TTS
23:38:30.193 🎭 VoiceEm...alAnalyzer                                     ✅ Initialized voice profiles for 12 masters
23:38:30.193                                                             🚀 Voice-Emotion Feedback System initialized
23:38:30.193 TTSServiceManager                                           🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.193 ApiKeyConfig                                                Using API key from ApiKeys class
23:38:30.193                                                             Using API key from ApiKeys class
23:38:30.193 OpenAIService                                               API key set, length: 164
23:38:30.515 GameViewModel                                               🎭 Initializing personality LiveData...
23:38:30.515                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
23:38:30.516                                                             🔍 Requesting position evaluation...
23:38:30.519                                                             ✅ Auto-commentary system enabled
23:38:30.519 ApiKeyConfig                                                Using API key from ApiKeys class
23:38:30.520 GameViewModel                                               🎮 Starting new game with configuration: white, skill=10, elo=1750
23:38:30.520 GameHistoryManager                                          GameHistoryManager instance created
23:38:30.520                                                             Game history cleared
23:38:30.522 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.522                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.522                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.522                                                             ✅ Usage context set to: spectator_mode
23:38:30.523 EmotionalIntelligence                                       🚀 CONSTRUCTOR CALLED: EmotionalIntelligenceManager initialization starting...
23:38:30.524                                                             ✅ Persistence manager initialized for EQ database operations
23:38:30.524                                                             🔧 About to call verifyDatabaseIntegrity()...
23:38:30.526 RelationshipPersistence                                     🔧 Database integrity check - emotional_reactions table exists: true
23:38:30.526                                                             🔧 Emotional reactions table schema:
23:38:30.526                                                               - Column: id (INTEGER) NOT NULL: false
23:38:30.526                                                               - Column: master (TEXT) NOT NULL: true
23:38:30.526                                                               - Column: opponent (TEXT) NOT NULL: true
23:38:30.526                                                               - Column: topic (TEXT) NOT NULL: true
23:38:30.526                                                               - Column: emotion (TEXT) NOT NULL: true
23:38:30.526                                                               - Column: intensity (REAL) NOT NULL: true
23:38:30.526                                                               - Column: momentum (REAL) NOT NULL: false
23:38:30.526                                                               - Column: game_context (TEXT) NOT NULL: false
23:38:30.526                                                               - Column: position_evaluation (REAL) NOT NULL: false
23:38:30.526                                                               - Column: conversation_snippet (TEXT) NOT NULL: false
23:38:30.527                                                               - Column: relationship_impact (REAL) NOT NULL: false
23:38:30.527                                                               - Column: timestamp (INTEGER) NOT NULL: true
23:38:30.527                                                             🔧 Database integrity check - master_relationships table exists: true
23:38:30.527                                                             🔧 Master relationships table schema:
23:38:30.527                                                               - Column: id (INTEGER) NOT NULL: false
23:38:30.527                                                               - Column: master1 (TEXT) NOT NULL: true
23:38:30.527                                                               - Column: master2 (TEXT) NOT NULL: true
23:38:30.527                                                               - Column: respect_level (REAL) NOT NULL: false
23:38:30.527                                                               - Column: rivalry_intensity (REAL) NOT NULL: false
23:38:30.527                                                               - Column: friendship_bond (REAL) NOT NULL: false
23:38:30.528                                                               - Column: communication_style (TEXT) NOT NULL: false
23:38:30.528                                                               - Column: total_interactions (INTEGER) NOT NULL: false
23:38:30.528                                                               - Column: total_games (INTEGER) NOT NULL: false
23:38:30.528                                                               - Column: last_major_event (TEXT) NOT NULL: false
23:38:30.528                                                               - Column: last_updated (INTEGER) NOT NULL: false
23:38:30.528                                                               - Column: created_at (INTEGER) NOT NULL: false
23:38:30.528                                                             🔧 Database tables exist - testing simple operations...
23:38:30.529                                                             🔧 Current emotional_reactions records: 612
23:38:30.529                                                             🔧 Current master_relationships records: 45
23:38:30.529 EmotionalIntelligence                                       🔧 verifyDatabaseIntegrity() completed
23:38:30.529                                                             🧪 About to call testDatabaseWrites()...
23:38:30.529 RelationshipPersistence                                     🧪 Testing database writes...
23:38:30.530                                                             🔧 Recording emotional reaction - Parameters: master=alekhine, opponent=carlsen, topic=chess_aesthetics, emotion=impressed, intensity=0.80
23:38:30.621 GameViewModel                                               🔍 Requesting position evaluation...
23:38:30.621                                                             🔄 Skipping evaluation - too soon since last request
23:38:30.621                                                             ✅ Game initialized with white vs 1750 Elo engine
23:38:30.626 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.initializeGameWithConfiguration:1101 
23:38:30.627                                                             show: isDexDualMode = false
23:38:30.627                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:38:30.631 🔗 LiveMonitorClient                                        Built server URL: ws://192.168.0.237:8080
23:38:30.631                                                             LiveMonitorClient initialized with server URL: ws://192.168.0.237:8080
23:38:30.631                                                             🚫 Live monitor disabled via ENABLE_LIVE_MONITOR flag - skipping connection
23:38:30.635 VoiceControlManager                                         🎤 VoiceControlManager initialized
23:38:30.635                                                             📋 Registered voice command listener: main_game
23:38:30.636 RelationshipPersistence                                     🎭 Recorded emotional reaction: alekhine felt impressed (0.80) about chess_aesthetics with carlsen
23:38:30.636 UserProfileManager                                          👤 Loaded user profile: Mr Ben Lockrey
23:38:30.636                                                             🎭 Integrating user profile with emotional intelligence systems...
23:38:30.637 RelationshipPersistence                                     👥 Retrieved relationship: alekhine <-> carlsen (respect: 7.00, rivalry: 0.00, friendship: 0.00)
23:38:30.637                                                             🔧 Updating relationship - alekhine <-> carlsen (respect: 7.10, rivalry: 0.00, friendship: 0.00)
23:38:30.657 UserProfileManager                                          ✅ User successfully integrated into AI emotional ecosystem
23:38:30.661 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.661                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.661                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.661 ApiKeyConfig                                                Using API key from ApiKeys class
23:38:30.661 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.661                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.661                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.662 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
23:38:30.662 GameViewModel                                               🎭 CONFIGURING PERSONALITY ENGINE: carlsen (weight=1.00, enabled=true)
23:38:30.666 VoiceControlManager                                         📋 Registered voice command listener: main_game
23:38:30.668 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
23:38:30.673 Choreographer                                               Skipped 69 frames!  The application may be doing too much work on its main thread.
23:38:30.673 RelationshipPersistence                                     💾 Updated relationship: alekhine <-> carlsen (respect: 7.10, rivalry: 0.00, friendship: 0.00)
23:38:30.673                                                             🧪 Database write test completed
23:38:30.673 EmotionalIntelligence                                       🧪 testDatabaseWrites() completed
23:38:30.674                                                             🎭 Enhanced Emotional Intelligence Manager initialized with database persistence
23:38:30.679 ConversationVariety                                         ✅ Loaded 3 tracked phrases
23:38:30.680 EmotionalIntelligence                                       🧠 Loaded emotional history for tal vs fischer: 0 events, momentum: 0.40
23:38:30.680 ConversationFlowTester                                      🎭 OPTIMIZING CONVERSATIONS FOR SPECTATOR MODE:
23:38:30.680                                                             📊 Current template analysis:
23:38:30.680                                                             🏥 CONVERSATION HEALTH METRICS:
23:38:30.684 Conversati...maTemplate                                     🌊 CREATING NATURAL FLOW TEMPLATE: Natural Decline (Pattern: DECLINING, First: 60 words, Follow-up: 60.0% of first)
23:38:30.684                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Natural Decline (Initial: 48-72 words, Followup: 8-36 words, Flow: DECLINING, Intensity: 0.8)
23:38:30.685                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Building Excitement (Pattern: BUILDING, First: 35 words, Follow-up: 80.0% of first)
23:38:30.685                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Building Excitement (Initial: 28-42 words, Followup: 8-28 words, Flow: BUILDING, Intensity: 0.8)
23:38:30.685                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Wave Discussion (Pattern: WAVE, First: 50 words, Follow-up: 70.0% of first)
23:38:30.686                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Wave Discussion (Initial: 40-60 words, Followup: 8-35 words, Flow: WAVE, Intensity: 0.8)
23:38:30.686                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Explosive Debate (Pattern: EXPLOSIVE, First: 40 words, Follow-up: 50.0% of first)
23:38:30.686                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Explosive Debate (Initial: 32-48 words, Followup: 8-20 words, Flow: EXPLOSIVE, Intensity: 0.8)
23:38:30.688 ConversationFlowTester                                      🎭 CURRENT: Engaging Chess Dialogue | Opening: 4-6 turns (90% chance) | Analysis: 4-6 turns (90% chance) | Style: DRAMATIC
23:38:30.688                                                               📊 opening: 4-6 turns, 90% response chance, 2000ms intervals
23:38:30.689                                                               📊 brilliant_move: 4-6 turns, 90% response chance, 1800ms intervals
23:38:30.689                                                               📊 position_change: 4-6 turns, 90% response chance, 2200ms intervals
23:38:30.689                                                               📊 endgame: 4-6 turns, 90% response chance, 2800ms intervals
23:38:30.689                                                             🔬 Running spectator mode simulation:
23:38:30.689                                                             🔬 SIMULATING CONVERSATION FLOW:
23:38:30.689                                                             🎯 Trigger: opening
23:38:30.689                                                             📋 Schema: 4-6 turns, 90.0% response chance
23:38:30.690                                                               Turn 1: ✅ CONTINUE (Current: 1/4-6)
23:38:30.691                                                               Turn 2: ✅ CONTINUE (Current: 2/4-6)
23:38:30.691                                                               Turn 3: ✅ CONTINUE (Current: 3/4-6)
23:38:30.691                                                               Turn 4: 🛑 STOP (Current: 4/4-6)
23:38:30.691                                                             🏁 Conversation ended after 4 turns
23:38:30.691                                                             🔬 SIMULATING CONVERSATION FLOW:
23:38:30.691                                                             🎯 Trigger: position_change
23:38:30.691                                                             📋 Schema: 4-6 turns, 90.0% response chance
23:38:30.691                                                               Turn 1: ✅ CONTINUE (Current: 1/4-6)
23:38:30.691                                                               Turn 2: ✅ CONTINUE (Current: 2/4-6)
23:38:30.692                                                               Turn 3: ✅ CONTINUE (Current: 3/4-6)
23:38:30.692                                                               Turn 4: ✅ CONTINUE (Current: 4/4-6)
23:38:30.692                                                               Turn 5: ✅ CONTINUE (Current: 5/4-6)
23:38:30.694                                                               Turn 6: 🛑 STOP (Current: 6/4-6)
23:38:30.694                                                             🏁 Conversation ended after 6 turns
23:38:30.694                                                             💡 SPECTATOR MODE RECOMMENDATIONS:
23:38:30.694                                                               🎯 Use 'engaging' template for balanced multi-turn dialogues
23:38:30.694                                                               🎯 Opening conversations: 3-6 turns (current spectator games start strong)
23:38:30.694                                                               🎯 Position analysis: 4-8 turns (masters debate moves actively)
23:38:30.694                                                               🎯 85% response rate (masters usually engage with each other)
23:38:30.694                                                               🎯 2.5s intervals (natural conversation pacing)
23:38:30.694 Conversati...maTemplate                                     🎭 CONVERSATION TEMPLATE CHANGED: Engaging Chess Dialogue (Intensity: ENGAGING, Style: DRAMATIC)
23:38:30.694                                                             🔧 TESTING: Engaging conversations (recommended for spectator mode)
23:38:30.694 ConversationFlowTester                                      🎭 CONVERSATION MODE: Engaging (3-6 opening turns, 4-8 analysis turns, 85% response rate)
23:38:30.694                                                             📊 🎭 CURRENT: Engaging Chess Dialogue | Opening: 4-6 turns (90% chance) | Analysis: 4-6 turns (90% chance) | Style: DRAMATIC
23:38:30.694                                                             ✅ APPLIED: Engaging conversation template for spectator mode
23:38:30.698 🎭 EmotionalMomentum                                        🚀 Emotional Momentum Manager initialized
23:38:30.698 🎭 Phase2Bridge                                             🚀 Phase 2 Emotional Integration Bridge initialized with momentum system
23:38:30.698 ResponsesA...tionHelper                                     Responses API for tal: enabled
23:38:30.698                                                             Responses API for fischer: enabled
23:38:30.699                                                             Responses API for carlsen: enabled
23:38:30.699                                                             Responses API for anand: enabled
23:38:30.699                                                             Responses API for alekhine: enabled
23:38:30.699                                                             Responses API for kasparov: enabled
23:38:30.699                                                             🔧 FORCE ENABLED Responses API for the 6 working masters: Tal, Fischer, Carlsen, Anand, Alekhine, Kasparov
23:38:30.699 BufferQueueProducer                                         [](id:449000000001,api:0,p:0,c:17552) setDequeueTimeout:2077252342
23:38:30.699 ResponsesA...tionHelper                                     📋 Other masters (Kramnik, Karpov, etc.) will use fallback dialogue
23:38:30.699                                                             🔄 Integration helper initialized. Responses API enabled: true
23:38:30.699                                                             🎯 Masters with Responses API enabled: {anand=true, kasparov=true, alekhine=true, fischer=true, carlsen=true, tal=true}
23:38:30.699                                                             📋 Master 'anand' → Responses API: true
23:38:30.699                                                             📋 Master 'kasparov' → Responses API: true
23:38:30.699                                                             📋 Master 'alekhine' → Responses API: true
23:38:30.699                                                             📋 Master 'fischer' → Responses API: true
23:38:30.699                                                             📋 Master 'carlsen' → Responses API: true
23:38:30.699                                                             📋 Master 'tal' → Responses API: true
23:38:30.700 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
23:38:30.700                                                             Access denied finding property "vendor.gpp.create_frc_extension"
23:38:30.712 ScrollView                                                   onsize change changed 
23:38:30.723 ChessSetManager                                             ✅ Initialized 2 chess sets
23:38:30.723                                                             🎨 Chess Set Manager initialized with set: staunton_classic
23:38:30.755 BLASTBufferQueue                                            [VRI[MainActivity]@7eff861#1](f:0,a:0,s:0) onFrameAvailable the first frame is available
23:38:30.755 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 33457, bufferData(ID: 75385265979399, frameNumber: 1)
23:38:30.755 HWUI                                                        CFMS:: SetUp Pid : 17552    Tid : 17597
23:38:30.761 ApiKeyConfig                                                Using API key from ApiKeys class
23:38:30.762                                                             Using API key from ApiKeys class
23:38:30.762 OpenAIService                                               API key set, length: 164
23:38:30.762 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.762                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.762                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.763                                                             ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.763                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.763                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.763 ThreeStage...nseManager                                     🚀 Enhanced ThreeStageResponseManager initialized with Responses API integration!
23:38:30.763                                                             🔍 Integration Status Check:
23:38:30.763 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: fischer
23:38:30.763                                                             🎯 Master fischer Responses API enabled: true
23:38:30.763 ThreeStage...nseManager                                     🎯 Fischer eligible: true
23:38:30.763 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: tal
23:38:30.763                                                             🎯 Master tal Responses API enabled: true
23:38:30.763 ThreeStage...nseManager                                     🎯 Tal eligible: true
23:38:30.763 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: carlsen
23:38:30.763                                                             🎯 Master carlsen Responses API enabled: true
23:38:30.763 ThreeStage...nseManager                                     🎯 Carlsen eligible: true
23:38:30.765 OpenAIService                                               API key set, length: 164
23:38:30.766                                                             API key set, length: 164
23:38:30.766 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.767                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.767                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.768 ConversationManager                                         Added new system message
23:38:30.768                                                             ConversationManager initialized with session: session_1750286310767
23:38:30.785                                                             Retrieved 21 session IDs
23:38:30.788                                                             ✅ Loaded conversation for session: session_1749828407544 with 10 messages
23:38:30.788                                                             ✅ Resumed conversation with 10 messages
23:38:30.797 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
23:38:30.813 InputMethodManagerUtils                                     startInputInner - Id : 0
23:38:30.813 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
23:38:30.813 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.813                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.815                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.815 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
23:38:30.817 GameViewModel                                               🎭 CONFIGURING PERSONALITY ENGINE: carlsen (weight=1.00, enabled=true)
23:38:30.820 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
23:38:30.820                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
23:38:30.820                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
23:38:30.821 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
23:38:31.255 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@ab4ebd2
23:38:31.264 GameViewModel                                               ✅ Evaluation received: 0.29
23:38:35.202 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:38:35.202                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:38:35.205                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:38:35.206                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:39:57.804 GameViewModel                                               🎯 makePlayerMove called with: e2e4
23:39:57.855                                                             🔍 Validating move: e2e4 (attempt 1)
23:39:57.956                                                             📋 Current position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
23:39:57.957                                                             ⚖️ Move e2e4 legality check: LEGAL
23:39:57.957                                                             ✅ Executing validated move: e2e4
23:39:58.161                                                             📍 New position after move: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1
23:39:58.165 GameHistoryManager                                          Move added: e2e4
23:39:58.262 GameViewModel                                               🔍 Requesting position evaluation...
23:39:58.263                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:39:58.263                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:39:58.264                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:39:58.268 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:39:58.269                                                             show: isDexDualMode = false
23:39:58.269                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:39:58.272 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:39:58.272                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:39:58.272                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:39:58.272                                                             🔧 gameRepository class: GameRepository
23:39:58.272                                                             🔧 Current thread: main
23:39:58.273 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:40:00.147                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:40:00.153                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:40:00.171 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c7c5
23:40:00.529 GameHistoryManager                                          Move added: c7c5
23:40:00.529 GameViewModel                                               🔍 Requesting position evaluation...
23:40:00.530                                                             🎭 Updating personality context for move: c7c5
23:40:00.530                                                             ✨ Personality context updated for move c7c5 - This is revolutionary!
23:40:00.531                                                             🔍 Checking game end conditions...
23:40:00.632                                                             ✅ Game continues - no end condition detected
23:40:01.084                                                             ✅ Evaluation received: 0.32
23:40:30.786                                                             🎯 makePlayerMove called with: g1f3
23:40:30.836                                                             🔍 Validating move: g1f3 (attempt 1)
23:40:30.938                                                             📋 Current position: rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2
23:40:30.989                                                             ⚖️ Move g1f3 legality check: LEGAL
23:40:30.989                                                             ✅ Executing validated move: g1f3
23:40:31.191                                                             📍 New position after move: rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2
23:40:31.199 GameHistoryManager                                          Move added: g1f3
23:40:31.291 GameViewModel                                               🔍 Requesting position evaluation...
23:40:31.292                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:40:31.292                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:40:31.292                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:40:31.295 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:40:31.296                                                             show: isDexDualMode = false
23:40:31.296                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:40:31.299 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:40:31.299                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:40:31.299                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:40:31.299                                                             🔧 gameRepository class: GameRepository
23:40:31.299                                                             🔧 Current thread: main
23:40:31.299 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:40:32.921                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:40:32.926                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:40:32.941 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7d6
23:40:33.300 GameHistoryManager                                          Move added: d7d6
23:40:33.300 GameViewModel                                               🔍 Requesting position evaluation...
23:40:33.301                                                             🎭 Updating personality context for move: d7d6
23:40:33.301                                                             ✨ Personality context updated for move d7d6 - This is revolutionary!
23:40:33.301                                                             🔍 Checking game end conditions...
23:40:33.402                                                             ✅ Game continues - no end condition detected
23:40:33.854                                                             ✅ Evaluation received: 0.35
23:40:45.700                                                             🎯 makePlayerMove called with: d2d4
23:40:45.751                                                             🔍 Validating move: d2d4 (attempt 1)
23:40:45.851                                                             📋 Current position: rnbqkbnr/pp2pppp/3p4/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 3
23:40:45.903                                                             ⚖️ Move d2d4 legality check: LEGAL
23:40:45.903                                                             ✅ Executing validated move: d2d4
23:40:46.106                                                             📍 New position after move: rnbqkbnr/pp2pppp/3p4/2p5/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq - 0 3
23:40:46.113 GameHistoryManager                                          Move added: d2d4
23:40:46.206 GameViewModel                                               🔍 Requesting position evaluation...
23:40:46.207                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:40:46.207                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:40:46.207                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:40:46.209 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:40:46.210                                                             show: isDexDualMode = false
23:40:46.210                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:40:46.214 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:40:46.214                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:40:46.214                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:40:46.214                                                             🔧 gameRepository class: GameRepository
23:40:46.214                                                             🔧 Current thread: main
23:40:46.214 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:40:48.336                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:40:48.366                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:40:48.393 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c5d4
23:40:48.751 GameHistoryManager                                          Move added: c5d4
23:40:48.751 GameViewModel                                               🔍 Requesting position evaluation...
23:40:48.751                                                             🎭 Updating personality context for move: c5d4
23:40:48.751                                                             ✨ Personality context updated for move c5d4 - This is revolutionary!
23:40:48.751                                                             🔍 Checking game end conditions...
23:40:48.852                                                             ✅ Game continues - no end condition detected
23:40:49.355                                                             ✅ Evaluation received: 0.40
23:41:02.296                                                             🎯 makePlayerMove called with: f3d4
23:41:02.347                                                             🔍 Validating move: f3d4 (attempt 1)
23:41:02.448                                                             📋 Current position: rnbqkbnr/pp2pppp/3p4/8/3pP3/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 4
23:41:02.499                                                             ⚖️ Move f3d4 legality check: LEGAL
23:41:02.499                                                             ✅ Executing validated move: f3d4
23:41:02.702                                                             📍 New position after move: rnbqkbnr/pp2pppp/3p4/8/3NP3/8/PPP2PPP/RNBQKB1R b KQkq - 0 4
23:41:02.708 GameHistoryManager                                          Move added: f3d4
23:41:02.802 GameViewModel                                               🔍 Requesting position evaluation...
23:41:02.804                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:41:02.804                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:41:02.804                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:41:02.807 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:41:02.808                                                             show: isDexDualMode = false
23:41:02.808                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:41:02.813 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:41:02.813                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:41:02.813                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:41:02.813                                                             🔧 gameRepository class: GameRepository
23:41:02.813                                                             🔧 Current thread: main
23:41:02.813 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:41:04.973                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:41:05.021                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:41:05.072 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8c6
23:41:05.430 GameHistoryManager                                          Move added: b8c6
23:41:05.430 GameViewModel                                               🔍 Requesting position evaluation...
23:41:05.431                                                             🎭 Updating personality context for move: b8c6
23:41:05.431                                                             ✨ Personality context updated for move b8c6 - This is revolutionary!
23:41:05.431                                                             🔍 Checking game end conditions...
23:41:05.532                                                             ✅ Game continues - no end condition detected
23:41:06.034                                                             ✅ Evaluation received: 0.40
23:41:18.266                                                             🎯 makePlayerMove called with: b1c3
23:41:18.267                                                             🔍 Validating move: b1c3 (attempt 1)
23:41:18.367                                                             📋 Current position: r1bqkbnr/pp2pppp/2np4/8/3NP3/8/PPP2PPP/RNBQKB1R w KQkq - 1 5
23:41:18.418                                                             ⚖️ Move b1c3 legality check: LEGAL
23:41:18.418                                                             ✅ Executing validated move: b1c3
23:41:18.620                                                             📍 New position after move: r1bqkbnr/pp2pppp/2np4/8/3NP3/2N5/PPP2PPP/R1BQKB1R b KQkq - 2 5
23:41:18.627 GameHistoryManager                                          Move added: b1c3
23:41:18.720 GameViewModel                                               🔍 Requesting position evaluation...
23:41:18.721                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:41:18.721                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:41:18.721                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:41:18.724 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:41:18.725                                                             show: isDexDualMode = false
23:41:18.725                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:41:18.731 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:41:18.731                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:41:18.731                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:41:18.731                                                             🔧 gameRepository class: GameRepository
23:41:18.731                                                             🔧 Current thread: main
23:41:18.731 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:41:21.000                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:41:21.048                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:41:21.098 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a7a6
23:41:21.458 GameHistoryManager                                          Move added: a7a6
23:41:21.458 GameViewModel                                               🔍 Requesting position evaluation...
23:41:21.459                                                             🎭 Updating personality context for move: a7a6
23:41:21.459                                                             ✨ Personality context updated for move a7a6 - This is revolutionary!
23:41:21.459                                                             🔍 Checking game end conditions...
23:41:21.560                                                             ✅ Game continues - no end condition detected
23:41:22.112                                                             ✅ Evaluation received: 0.55
23:41:35.900                                                             🎯 makePlayerMove called with: d4c6
23:41:35.951                                                             🔍 Validating move: d4c6 (attempt 1)
23:41:36.052                                                             📋 Current position: r1bqkbnr/1p2pppp/p1np4/8/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq - 0 6
23:41:36.103                                                             ⚖️ Move d4c6 legality check: LEGAL
23:41:36.103                                                             ✅ Executing validated move: d4c6
23:41:36.305                                                             📍 New position after move: r1bqkbnr/1p2pppp/p1Np4/8/4P3/2N5/PPP2PPP/R1BQKB1R b KQkq - 0 6
23:41:36.311 GameHistoryManager                                          Move added: d4c6
23:41:36.407 GameViewModel                                               🔍 Requesting position evaluation...
23:41:36.408                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:41:36.408                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:41:36.409                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:41:36.412 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:41:36.413                                                             show: isDexDualMode = false
23:41:36.413                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:41:36.417 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:41:36.417                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:41:36.417                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:41:36.417                                                             🔧 gameRepository class: GameRepository
23:41:36.417                                                             🔧 Current thread: main
23:41:36.417 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:41:38.086                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:41:38.131                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:41:38.177 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b7c6
23:41:38.538 GameHistoryManager                                          Move added: b7c6
23:41:38.538 GameViewModel                                               🔍 Requesting position evaluation...
23:41:38.538                                                             🎭 Updating personality context for move: b7c6
23:41:38.539                                                             ✨ Personality context updated for move b7c6 - This is revolutionary!
23:41:38.539                                                             🔍 Checking game end conditions...
23:41:38.640                                                             ✅ Game continues - no end condition detected
23:41:39.241                                                             ✅ Evaluation received: 0.44
23:41:57.599                                                             🎯 makePlayerMove called with: f1c4
23:41:57.649                                                             🔍 Validating move: f1c4 (attempt 1)
23:41:57.750                                                             📋 Current position: r1bqkbnr/4pppp/p1pp4/8/4P3/2N5/PPP2PPP/R1BQKB1R w KQkq - 0 7
23:41:57.801                                                             ⚖️ Move f1c4 legality check: LEGAL
23:41:57.801                                                             ✅ Executing validated move: f1c4
23:41:58.004                                                             📍 New position after move: r1bqkbnr/4pppp/p1pp4/8/2B1P3/2N5/PPP2PPP/R1BQK2R b KQkq - 1 7
23:41:58.012 GameHistoryManager                                          Move added: f1c4
23:41:58.104 GameViewModel                                               🔍 Requesting position evaluation...
23:41:58.105                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:41:58.105                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:41:58.105                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:41:58.108 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:41:58.109                                                             show: isDexDualMode = false
23:41:58.109                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:41:58.113 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:41:58.113                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:41:58.113                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:41:58.114                                                             🔧 gameRepository class: GameRepository
23:41:58.114                                                             🔧 Current thread: main
23:41:58.114 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:42:00.685                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:42:00.730                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:42:00.779 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8f6
23:42:00.874 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
23:42:01.139 GameHistoryManager                                          Move added: g8f6
23:42:01.139 GameViewModel                                               🔍 Requesting position evaluation...
23:42:01.139                                                             🎭 Updating personality context for move: g8f6
23:42:01.139                                                             ✨ Personality context updated for move g8f6 - This is revolutionary!
23:42:01.140                                                             🔍 Checking game end conditions...
23:42:01.240                                                             ✅ Game continues - no end condition detected
23:42:01.843                                                             ✅ Evaluation received: 0.68
23:42:16.234                                                             🎯 makePlayerMove called with: e4e5
23:42:16.285                                                             🔍 Validating move: e4e5 (attempt 1)
23:42:16.387                                                             📋 Current position: r1bqkb1r/4pppp/p1pp1n2/8/2B1P3/2N5/PPP2PPP/R1BQK2R w KQkq - 2 8
23:42:16.439                                                             ⚖️ Move e4e5 legality check: LEGAL
23:42:16.439                                                             ✅ Executing validated move: e4e5
23:42:16.641                                                             📍 New position after move: r1bqkb1r/4pppp/p1pp1n2/4P3/2B5/2N5/PPP2PPP/R1BQK2R b KQkq - 0 8
23:42:16.650 GameHistoryManager                                          Move added: e4e5
23:42:16.741 GameViewModel                                               🔍 Requesting position evaluation...
23:42:16.742                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:42:16.742                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:42:16.742                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:42:16.745 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:42:16.746                                                             show: isDexDualMode = false
23:42:16.746                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:42:16.750 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:42:16.750                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:42:16.750                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:42:16.750                                                             🔧 gameRepository class: GameRepository
23:42:16.750                                                             🔧 Current thread: main
23:42:16.750 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:42:18.871                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:42:18.915                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:42:18.962 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f6d7
23:42:19.321 GameHistoryManager                                          Move added: f6d7
23:42:19.321 GameViewModel                                               🔍 Requesting position evaluation...
23:42:19.322                                                             🎭 Updating personality context for move: f6d7
23:42:19.322                                                             ✨ Personality context updated for move f6d7 - This is revolutionary!
23:42:19.322                                                             🔍 Checking game end conditions...
23:42:19.423                                                             ✅ Game continues - no end condition detected
23:42:20.025                                                             ✅ Evaluation received: 0.76
23:42:40.215                                                             🎯 makePlayerMove called with: c1f4
23:42:40.266                                                             🔍 Validating move: c1f4 (attempt 1)
23:42:40.367                                                             📋 Current position: r1bqkb1r/3npppp/p1pp4/4P3/2B5/2N5/PPP2PPP/R1BQK2R w KQkq - 1 9
23:42:40.418                                                             ⚖️ Move c1f4 legality check: LEGAL
23:42:40.418                                                             ✅ Executing validated move: c1f4
23:42:40.621                                                             📍 New position after move: r1bqkb1r/3npppp/p1pp4/4P3/2B2B2/2N5/PPP2PPP/R2QK2R b KQkq - 2 9
23:42:40.628 GameHistoryManager                                          Move added: c1f4
23:42:40.721 GameViewModel                                               🔍 Requesting position evaluation...
23:42:40.722                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:42:40.722                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:42:40.723                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:42:40.726 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:42:40.727                                                             show: isDexDualMode = false
23:42:40.727                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:42:40.732 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:42:40.732                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:42:40.732                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:42:40.732                                                             🔧 gameRepository class: GameRepository
23:42:40.732                                                             🔧 Current thread: main
23:42:40.732 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:42:42.848                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:42:42.893                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:42:42.939 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d6e5
23:42:43.300 GameHistoryManager                                          Move added: d6e5
23:42:43.300 GameViewModel                                               🔍 Requesting position evaluation...
23:42:43.301                                                             🎭 Updating personality context for move: d6e5
23:42:43.301                                                             ✨ Personality context updated for move d6e5 - This is revolutionary!
23:42:43.302                                                             🔍 Checking game end conditions...
23:42:43.402                                                             ✅ Game continues - no end condition detected
23:42:44.003                                                             ✅ Evaluation received: 0.99
23:43:48.399                                                             🎯 makePlayerMove called with: f4g5
23:43:48.450                                                             🔍 Validating move: f4g5 (attempt 1)
23:43:48.550                                                             📋 Current position: r1bqkb1r/3npppp/p1p5/4p3/2B2B2/2N5/PPP2PPP/R2QK2R w KQkq - 0 10
23:43:48.602                                                             ⚖️ Move f4g5 legality check: LEGAL
23:43:48.602                                                             ✅ Executing validated move: f4g5
23:43:48.804                                                             📍 New position after move: r1bqkb1r/3npppp/p1p5/4p1B1/2B5/2N5/PPP2PPP/R2QK2R b KQkq - 1 10
23:43:48.813 GameHistoryManager                                          Move added: f4g5
23:43:48.905 GameViewModel                                               🔍 Requesting position evaluation...
23:43:48.906                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:43:48.906                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:43:48.906                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:43:48.910 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:43:48.910                                                             show: isDexDualMode = false
23:43:48.910                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:43:48.915 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:43:48.915                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:43:48.915                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:43:48.915                                                             🔧 gameRepository class: GameRepository
23:43:48.915                                                             🔧 Current thread: main
23:43:48.915 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:43:51.441                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:43:51.497                                                             📊 DATABASE QUERY RESULT: carlsen returned 15 historical positions
23:43:51.543 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a8b8
23:43:51.905 GameHistoryManager                                          Move added: a8b8
23:43:51.905 GameViewModel                                               🔍 Requesting position evaluation...
23:43:51.905                                                             🎭 Updating personality context for move: a8b8
23:43:51.906                                                             ✨ Personality context updated for move a8b8 - This is revolutionary!
23:43:51.906                                                             🔍 Checking game end conditions...
23:43:52.007                                                             ✅ Game continues - no end condition detected
23:43:52.608                                                             ✅ Evaluation received: 1.10
23:44:08.380                                                             🎯 makePlayerMove called with: d1f4
23:44:08.431                                                             🔍 Validating move: d1f4 (attempt 1)
23:44:08.533                                                             📋 Current position: 1rbqkb1r/3npppp/p1p5/4p1B1/2B5/2N5/PPP2PPP/R2QK2R w KQk - 2 11
23:44:08.585                                                             ⚖️ Move d1f4 legality check: LEGAL
23:44:08.586                                                             ✅ Executing validated move: d1f4
23:44:08.688                                                             ❌ Move execution failed in gameRepository.makeMove()
23:44:08.688                                                             💔 Handling move execution failure: d1f4
23:44:08.690 Choreographer                                               Skipped 36 frames!  The application may be doing too much work on its main thread.
23:44:10.431 GameViewModel                                               🎯 makePlayerMove called with: d1f3
23:44:10.482                                                             🔍 Validating move: d1f3 (attempt 1)
23:44:10.584                                                             📋 Current position: 1rbqkb1r/3npppp/p1p5/4p1B1/2B5/2N5/PPP2PPP/R2QK2R w KQk - 2 11
23:44:10.636                                                             ⚖️ Move d1f3 legality check: LEGAL
23:44:10.636                                                             ✅ Executing validated move: d1f3
23:44:10.838                                                             📍 New position after move: 1rbqkb1r/3npppp/p1p5/4p1B1/2B5/2N2Q2/PPP2PPP/R3K2R b KQk - 3 11
23:44:10.846 GameHistoryManager                                          Move added: d1f3
23:44:10.939 GameViewModel                                               🔍 Requesting position evaluation...
23:44:10.940                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:44:10.940                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:44:10.940                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:44:10.944 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:44:10.945                                                             show: isDexDualMode = false
23:44:10.945                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:44:10.951 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:44:10.951                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:44:10.951                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:44:10.951                                                             🔧 gameRepository class: GameRepository
23:44:10.951                                                             🔧 Current thread: main
23:44:10.951 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:44:12.817                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:44:12.865                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:44:12.914 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f7f6
23:44:13.275 GameHistoryManager                                          Move added: f7f6
23:44:13.275 GameViewModel                                               🔍 Requesting position evaluation...
23:44:13.276                                                             🎭 Updating personality context for move: f7f6
23:44:13.276                                                             ✨ Personality context updated for move f7f6 - This is revolutionary!
23:44:13.277                                                             🔍 Checking game end conditions...
23:44:13.377                                                             ✅ Game continues - no end condition detected
23:44:13.981                                                             ✅ Evaluation received: 1.12
23:45:37.439                                                             🎯 makePlayerMove called with: g5e3
23:45:37.490                                                             🔍 Validating move: g5e3 (attempt 1)
23:45:37.591                                                             📋 Current position: 1rbqkb1r/3np1pp/p1p2p2/4p1B1/2B5/2N2Q2/PPP2PPP/R3K2R w KQk - 0 12
23:45:37.643                                                             ⚖️ Move g5e3 legality check: LEGAL
23:45:37.643                                                             ✅ Executing validated move: g5e3
23:45:37.845                                                             📍 New position after move: 1rbqkb1r/3np1pp/p1p2p2/4p3/2B5/2N1BQ2/PPP2PPP/R3K2R b KQk - 1 12
23:45:37.854 GameHistoryManager                                          Move added: g5e3
23:45:37.946 GameViewModel                                               🔍 Requesting position evaluation...
23:45:37.946                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:45:37.946                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:45:37.946                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:45:37.949 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:45:37.950                                                             show: isDexDualMode = false
23:45:37.950                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:45:37.955 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:45:37.955                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:45:37.955                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:45:37.955                                                             🔧 gameRepository class: GameRepository
23:45:37.955                                                             🔧 Current thread: main
23:45:37.955 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:45:40.174                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:45:40.228                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:45:40.278 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d8c7
23:45:40.640 GameHistoryManager                                          Move added: d8c7
23:45:40.640 GameViewModel                                               🔍 Requesting position evaluation...
23:45:40.641                                                             🎭 Updating personality context for move: d8c7
23:45:40.641                                                             ✨ Personality context updated for move d8c7 - This is revolutionary!
23:45:40.641                                                             🔍 Checking game end conditions...
23:45:40.742                                                             ✅ Game continues - no end condition detected
23:45:41.343                                                             ✅ Evaluation received: 1.07
23:46:21.586                                                             🎯 makePlayerMove called with: c3e4
23:46:21.637                                                             🔍 Validating move: c3e4 (attempt 1)
23:46:21.738                                                             📋 Current position: 1rb1kb1r/2qnp1pp/p1p2p2/4p3/2B5/2N1BQ2/PPP2PPP/R3K2R w KQk - 2 13
23:46:21.789                                                             ⚖️ Move c3e4 legality check: LEGAL
23:46:21.789                                                             ✅ Executing validated move: c3e4
23:46:21.991                                                             📍 New position after move: 1rb1kb1r/2qnp1pp/p1p2p2/4p3/2B1N3/4BQ2/PPP2PPP/R3K2R b KQk - 3 13
23:46:22.000 GameHistoryManager                                          Move added: c3e4
23:46:22.093 GameViewModel                                               🔍 Requesting position evaluation...
23:46:22.094                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:46:22.094                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:46:22.094                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:46:22.097 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:46:22.098                                                             show: isDexDualMode = false
23:46:22.098                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:46:22.103 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:46:22.103                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:46:22.103                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:46:22.103                                                             🔧 gameRepository class: GameRepository
23:46:22.103                                                             🔧 Current thread: main
23:46:22.103 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:46:24.925                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:46:24.970                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:46:25.020 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8b2
23:46:25.382 GameHistoryManager                                          Move added: b8b2
23:46:25.382 GameViewModel                                               🔍 Requesting position evaluation...
23:46:25.382                                                             🎭 Updating personality context for move: b8b2
23:46:25.383                                                             ✨ Personality context updated for move b8b2 - This is revolutionary!
23:46:25.383                                                             🔍 Checking game end conditions...
23:46:25.483                                                             ✅ Game continues - no end condition detected
23:46:26.086                                                             ✅ Evaluation received: 1.17
23:47:27.789                                                             🎯 makePlayerMove called with: e1g1
23:47:27.841                                                             🔍 Validating move: e1g1 (attempt 1)
23:47:27.941                                                             📋 Current position: 2b1kb1r/2qnp1pp/p1p2p2/4p3/2B1N3/4BQ2/PrP2PPP/R3K2R w KQk - 0 14
23:47:27.993                                                             ⚖️ Move e1g1 legality check: LEGAL
23:47:27.993                                                             ✅ Executing validated move: e1g1
23:47:28.195                                                             📍 New position after move: 2b1kb1r/2qnp1pp/p1p2p2/4p3/2B1N3/4BQ2/PrP2PPP/R4RK1 b k - 1 14
23:47:28.204 GameHistoryManager                                          Move added: e1g1
23:47:28.295 GameViewModel                                               🔍 Requesting position evaluation...
23:47:28.296                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:47:28.296                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:47:28.296                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:47:28.300 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:47:28.301                                                             show: isDexDualMode = false
23:47:28.301                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:47:28.306 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:47:28.306                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:47:28.306                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:47:28.306                                                             🔧 gameRepository class: GameRepository
23:47:28.306                                                             🔧 Current thread: main
23:47:28.306 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:47:30.770                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:47:30.815                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:47:30.865 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c6c5
23:47:31.227 GameHistoryManager                                          Move added: c6c5
23:47:31.227 GameViewModel                                               🔍 Requesting position evaluation...
23:47:31.228                                                             🎭 Updating personality context for move: c6c5
23:47:31.228                                                             ✨ Personality context updated for move c6c5 - This is revolutionary!
23:47:31.228                                                             🔍 Checking game end conditions...
23:47:31.329                                                             ✅ Game continues - no end condition detected
23:47:31.931                                                             ✅ Evaluation received: 2.03
23:48:53.843                                                             🎯 makePlayerMove called with: a1d1
23:48:53.895                                                             🔍 Validating move: a1d1 (attempt 1)
23:48:53.995                                                             📋 Current position: 2b1kb1r/2qnp1pp/p4p2/2p1p3/2B1N3/4BQ2/PrP2PPP/R4RK1 w k - 0 15
23:48:54.047                                                             ⚖️ Move a1d1 legality check: LEGAL
23:48:54.047                                                             ✅ Executing validated move: a1d1
23:48:54.250                                                             📍 New position after move: 2b1kb1r/2qnp1pp/p4p2/2p1p3/2B1N3/4BQ2/PrP2PPP/3R1RK1 b k - 1 15
23:48:54.259 GameHistoryManager                                          Move added: a1d1
23:48:54.350 GameViewModel                                               🔍 Requesting position evaluation...
23:48:54.351                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:48:54.351                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:48:54.352                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:48:54.355 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:48:54.356                                                             show: isDexDualMode = false
23:48:54.356                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:48:54.362 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:48:54.362                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:48:54.362                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:48:54.362                                                             🔧 gameRepository class: GameRepository
23:48:54.362                                                             🔧 Current thread: main
23:48:54.362 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:48:57.234                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:48:57.285                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:48:57.334 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b2b4
23:48:57.698 GameHistoryManager                                          Move added: b2b4
23:48:57.698 GameViewModel                                               🔍 Requesting position evaluation...
23:48:57.698                                                             🎭 Updating personality context for move: b2b4
23:48:57.698                                                             ✨ Personality context updated for move b2b4 - This is revolutionary!
23:48:57.699                                                             🔍 Checking game end conditions...
23:48:57.799                                                             ✅ Game continues - no end condition detected
23:48:58.401                                                             ✅ Evaluation received: 1.57
23:49:12.340                                                             🎯 makePlayerMove called with: c4e6
23:49:12.391                                                             🔍 Validating move: c4e6 (attempt 1)
23:49:12.493                                                             📋 Current position: 2b1kb1r/2qnp1pp/p4p2/2p1p3/1rB1N3/4BQ2/P1P2PPP/3R1RK1 w k - 2 16
23:49:12.544                                                             ⚖️ Move c4e6 legality check: LEGAL
23:49:12.544                                                             ✅ Executing validated move: c4e6
23:49:12.746                                                             📍 New position after move: 2b1kb1r/2qnp1pp/p3Bp2/2p1p3/1r2N3/4BQ2/P1P2PPP/3R1RK1 b k - 3 16
23:49:12.754 GameHistoryManager                                          Move added: c4e6
23:49:12.846 GameViewModel                                               🔍 Requesting position evaluation...
23:49:12.847                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:49:12.848                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:49:12.848                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:49:12.852 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:49:12.853                                                             show: isDexDualMode = false
23:49:12.853                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:49:12.858 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:49:12.858                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:49:12.858                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:49:12.858                                                             🔧 gameRepository class: GameRepository
23:49:12.858                                                             🔧 Current thread: main
23:49:12.858 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:49:15.025                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:49:15.069                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:49:15.119 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g7g6
23:49:15.485 GameHistoryManager                                          Move added: g7g6
23:49:15.485 GameViewModel                                               🔍 Requesting position evaluation...
23:49:15.486                                                             🎭 Updating personality context for move: g7g6
23:49:15.486                                                             ✨ Personality context updated for move g7g6 - This is revolutionary!
23:49:15.486                                                             🔍 Checking game end conditions...
23:49:15.587                                                             ✅ Game continues - no end condition detected
23:49:16.191                                                             ✅ Evaluation received: 2.31
23:50:00.157                                                             🎯 makePlayerMove called with: d1d5
23:50:00.208                                                             🔍 Validating move: d1d5 (attempt 1)
23:50:00.309                                                             📋 Current position: 2b1kb1r/2qnp2p/p3Bpp1/2p1p3/1r2N3/4BQ2/P1P2PPP/3R1RK1 w k - 0 17
23:50:00.360                                                             ⚖️ Move d1d5 legality check: LEGAL
23:50:00.360                                                             ✅ Executing validated move: d1d5
23:50:00.563                                                             📍 New position after move: 2b1kb1r/2qnp2p/p3Bpp1/2pRp3/1r2N3/4BQ2/P1P2PPP/5RK1 b k - 1 17
23:50:00.576 GameHistoryManager                                          Move added: d1d5
23:50:00.664 GameViewModel                                               🔍 Requesting position evaluation...
23:50:00.665                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:50:00.665                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:50:00.665                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:50:00.668 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:50:00.669                                                             show: isDexDualMode = false
23:50:00.669                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:50:00.673 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:50:00.673                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:50:00.673                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:50:00.673                                                             🔧 gameRepository class: GameRepository
23:50:00.673                                                             🔧 Current thread: main
23:50:00.673 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:50:02.743                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:50:02.788                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:50:02.838 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7b6
23:50:03.202 GameHistoryManager                                          Move added: d7b6
23:50:03.202 GameViewModel                                               🔍 Requesting position evaluation...
23:50:03.202                                                             🎭 Updating personality context for move: d7b6
23:50:03.203                                                             ✨ Personality context updated for move d7b6 - This is revolutionary!
23:50:03.203                                                             🔍 Checking game end conditions...
23:50:03.303                                                             ✅ Game continues - no end condition detected
23:50:03.757                                                             ✅ Evaluation received: 3.77
23:50:21.883                                                             🎯 makePlayerMove called with: d5c5
23:50:21.934                                                             🔍 Validating move: d5c5 (attempt 1)
23:50:22.034                                                             📋 Current position: 2b1kb1r/2q1p2p/pn2Bpp1/2pRp3/1r2N3/4BQ2/P1P2PPP/5RK1 w k - 2 18
23:50:22.086                                                             ⚖️ Move d5c5 legality check: LEGAL
23:50:22.086                                                             ✅ Executing validated move: d5c5
23:50:22.288                                                             📍 New position after move: 2b1kb1r/2q1p2p/pn2Bpp1/2R1p3/1r2N3/4BQ2/P1P2PPP/5RK1 b k - 0 18
23:50:22.299 GameHistoryManager                                          Move added: d5c5
23:50:22.388 GameViewModel                                               🔍 Requesting position evaluation...
23:50:22.390                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:50:22.390                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:50:22.390                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:50:22.394 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:50:22.394                                                             show: isDexDualMode = false
23:50:22.394                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:50:22.398 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:50:22.398                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:50:22.398                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:50:22.398                                                             🔧 gameRepository class: GameRepository
23:50:22.398                                                             🔧 Current thread: main
23:50:22.398 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:50:22.750 GameViewModel                                               ✅ Evaluation received: -3.90
23:50:22.857 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:50:22.916                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:50:22.987 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c7d8
23:50:23.344 GameHistoryManager                                          Move added: c7d8
23:50:23.344 GameViewModel                                               🔍 Requesting position evaluation...
23:50:23.344                                                             🎭 Updating personality context for move: c7d8
23:50:23.344                                                             ✨ Personality context updated for move c7d8 - This is revolutionary!
23:50:23.344                                                             🔍 Checking game end conditions...
23:50:23.445                                                             ✅ Game continues - no end condition detected
23:50:23.445 Choreographer                                               Skipped 54 frames!  The application may be doing too much work on its main thread.
23:50:23.795 GameViewModel                                               ✅ Evaluation received: -4.26
23:51:02.067                                                             🎯 makePlayerMove called with: c5c8
23:51:02.118                                                             🔍 Validating move: c5c8 (attempt 1)
23:51:02.218                                                             📋 Current position: 2bqkb1r/4p2p/pn2Bpp1/2R1p3/1r2N3/4BQ2/P1P2PPP/5RK1 w k - 1 19
23:51:02.269                                                             ⚖️ Move c5c8 legality check: LEGAL
23:51:02.269                                                             ✅ Executing validated move: c5c8
23:51:02.472                                                             📍 New position after move: 2Rqkb1r/4p2p/pn2Bpp1/4p3/1r2N3/4BQ2/P1P2PPP/5RK1 b k - 0 19
23:51:02.482 GameHistoryManager                                          Move added: c5c8
23:51:02.573 GameViewModel                                               🔍 Requesting position evaluation...
23:51:02.574                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:51:02.574                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:51:02.574                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:51:02.578 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:51:02.578                                                             show: isDexDualMode = false
23:51:02.578                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:51:02.583 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:51:02.583                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:51:02.583                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:51:02.583                                                             🔧 gameRepository class: GameRepository
23:51:02.583                                                             🔧 Current thread: main
23:51:02.583 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:51:02.936 GameViewModel                                               ✅ Evaluation received: -4.74
23:51:02.992 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:51:03.051                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:51:03.122 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b6c8
23:51:03.477 GameHistoryManager                                          Move added: b6c8
23:51:03.477 GameViewModel                                               🔍 Requesting position evaluation...
23:51:03.477                                                             🎭 Updating personality context for move: b6c8
23:51:03.478                                                             ✨ Personality context updated for move b6c8 - This is revolutionary!
23:51:03.478                                                             🔍 Checking game end conditions...
23:51:03.578                                                             ✅ Game continues - no end condition detected
23:51:03.579 Choreographer                                               Skipped 54 frames!  The application may be doing too much work on its main thread.
23:51:03.979 GameViewModel                                               ✅ Evaluation received: -4.84
23:51:18.641                                                             🎯 makePlayerMove called with: e4g5
23:51:18.692                                                             🔍 Validating move: e4g5 (attempt 1)
23:51:18.792                                                             📋 Current position: 2nqkb1r/4p2p/p3Bpp1/4p3/1r2N3/4BQ2/P1P2PPP/5RK1 w k - 0 20
23:51:18.844                                                             ⚖️ Move e4g5 legality check: LEGAL
23:51:18.844                                                             ✅ Executing validated move: e4g5
23:51:19.047                                                             📍 New position after move: 2nqkb1r/4p2p/p3Bpp1/4p1N1/1r6/4BQ2/P1P2PPP/5RK1 b k - 1 20
23:51:19.059 GameHistoryManager                                          Move added: e4g5
23:51:19.148 GameViewModel                                               🔍 Requesting position evaluation...
23:51:19.149                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:51:19.149                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:51:19.149                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:51:19.153 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:51:19.153                                                             show: isDexDualMode = false
23:51:19.153                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:51:19.159 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:51:19.159                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:51:19.159                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:51:19.159                                                             🔧 gameRepository class: GameRepository
23:51:19.159                                                             🔧 Current thread: main
23:51:19.159 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:51:20.926                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:51:21.038                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:51:21.087 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8g7
23:51:21.452 GameHistoryManager                                          Move added: f8g7
23:51:21.452 GameViewModel                                               🔍 Requesting position evaluation...
23:51:21.452                                                             🎭 Updating personality context for move: f8g7
23:51:21.453                                                             ✨ Personality context updated for move f8g7 - This is revolutionary!
23:51:21.453                                                             🔍 Checking game end conditions...
23:51:21.554                                                             ✅ Game continues - no end condition detected
23:51:22.005                                                             ✅ Evaluation received: 4.98
23:51:32.307                                                             🎯 makePlayerMove called with: e6f7
23:51:32.359                                                             🔍 Validating move: e6f7 (attempt 1)
23:51:32.459                                                             📋 Current position: 2nqk2r/4p1bp/p3Bpp1/4p1N1/1r6/4BQ2/P1P2PPP/5RK1 w k - 2 21
23:51:32.510                                                             ⚖️ Move e6f7 legality check: LEGAL
23:51:32.510                                                             ✅ Executing validated move: e6f7
23:51:32.713                                                             📍 New position after move: 2nqk2r/4pBbp/p4pp1/4p1N1/1r6/4BQ2/P1P2PPP/5RK1 b k - 3 21
23:51:32.723 GameHistoryManager                                          Move added: e6f7
23:51:32.813 GameViewModel                                               🔍 Requesting position evaluation...
23:51:32.815                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:51:32.815                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:51:32.815                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:51:32.819 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:51:32.820                                                             show: isDexDualMode = false
23:51:32.820                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:51:32.825 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:51:32.825                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:51:32.825                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:51:32.825                                                             🔧 gameRepository class: GameRepository
23:51:32.825                                                             🔧 Current thread: main
23:51:32.825 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:51:33.176 GameViewModel                                               ✅ Evaluation received: -4.86
23:51:33.282 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:51:33.338                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:51:33.407 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e8d7
23:51:33.762 GameHistoryManager                                          Move added: e8d7
23:51:33.762 GameViewModel                                               🔍 Requesting position evaluation...
23:51:33.763                                                             🎭 Updating personality context for move: e8d7
23:51:33.763                                                             ✨ Personality context updated for move e8d7 - This is revolutionary!
23:51:33.763                                                             🔍 Checking game end conditions...
23:51:33.863                                                             ✅ Game continues - no end condition detected
23:51:33.864 Choreographer                                               Skipped 53 frames!  The application may be doing too much work on its main thread.
23:51:34.213 GameViewModel                                               ✅ Evaluation received: -4.92
23:52:08.809                                                             🎯 makePlayerMove called with: g5e6
23:52:08.859                                                             🔍 Validating move: g5e6 (attempt 1)
23:52:08.960                                                             📋 Current position: 2nq3r/3kpBbp/p4pp1/4p1N1/1r6/4BQ2/P1P2PPP/5RK1 w - - 4 22
23:52:09.012                                                             ⚖️ Move g5e6 legality check: LEGAL
23:52:09.012                                                             ✅ Executing validated move: g5e6
23:52:09.215                                                             📍 New position after move: 2nq3r/3kpBbp/p3Npp1/4p3/1r6/4BQ2/P1P2PPP/5RK1 b - - 5 22
23:52:09.224 GameHistoryManager                                          Move added: g5e6
23:52:09.314 GameViewModel                                               🔍 Requesting position evaluation...
23:52:09.315                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:52:09.315                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:52:09.315                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:52:09.318 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:52:09.319                                                             show: isDexDualMode = false
23:52:09.319                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:52:09.323 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:52:09.323                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:52:09.323                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:52:09.323                                                             🔧 gameRepository class: GameRepository
23:52:09.323                                                             🔧 Current thread: main
23:52:09.323 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:52:09.722 GameViewModel                                               ✅ Evaluation received: -4.99
23:52:10.331 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:52:10.389                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:52:10.459 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c8d6
23:52:10.825 GameHistoryManager                                          Move added: c8d6
23:52:10.825 GameViewModel                                               🔍 Requesting position evaluation...
23:52:10.825                                                             🎭 Updating personality context for move: c8d6
23:52:10.825                                                             ✨ Personality context updated for move c8d6 - This is revolutionary!
23:52:10.826                                                             🔍 Checking game end conditions...
23:52:10.927                                                             ✅ Game continues - no end condition detected
23:52:11.228                                                             ✅ Evaluation received: 6.42
23:52:24.542                                                             🎯 makePlayerMove called with: e6c5
23:52:24.592                                                             🔍 Validating move: e6c5 (attempt 1)
23:52:24.693                                                             📋 Current position: 3q3r/3kpBbp/p2nNpp1/4p3/1r6/4BQ2/P1P2PPP/5RK1 w - - 6 23
23:52:24.745                                                             ⚖️ Move e6c5 legality check: LEGAL
23:52:24.745                                                             ✅ Executing validated move: e6c5
23:52:24.949                                                             📍 New position after move: 3q3r/3kpBbp/p2n1pp1/2N1p3/1r6/4BQ2/P1P2PPP/5RK1 b - - 7 23
23:52:24.961 GameHistoryManager                                          Move added: e6c5
23:52:25.049 GameViewModel                                               🔍 Requesting position evaluation...
23:52:25.050                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:52:25.050                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:52:25.050                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:52:25.053 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:52:25.054                                                             show: isDexDualMode = false
23:52:25.054                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:52:25.059 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:52:25.059                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:52:25.059                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:52:25.059                                                             🔧 gameRepository class: GameRepository
23:52:25.059                                                             🔧 Current thread: main
23:52:25.059 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:52:26.726                                                             📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:52:26.772                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:52:26.824 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7c7
23:52:27.137 GameHistoryManager                                          Move added: d7c7
23:52:27.137 GameViewModel                                               🔍 Requesting position evaluation...
23:52:27.138                                                             🎭 Updating personality context for move: d7c7
23:52:27.138                                                             ✨ Personality context updated for move d7c7 - This is revolutionary!
23:52:27.138                                                             🔍 Checking game end conditions...
23:52:27.239                                                             ✅ Game continues - no end condition detected
23:52:27.491                                                             ✅ Evaluation received: 6.83
23:52:35.308                                                             🎯 makePlayerMove called with: c5a6
23:52:35.359                                                             🔍 Validating move: c5a6 (attempt 1)
23:52:35.460                                                             📋 Current position: 3q3r/2k1pBbp/p2n1pp1/2N1p3/1r6/4BQ2/P1P2PPP/5RK1 w - - 8 24
23:52:35.511                                                             ⚖️ Move c5a6 legality check: LEGAL
23:52:35.511                                                             ✅ Executing validated move: c5a6
23:52:35.714                                                             📍 New position after move: 3q3r/2k1pBbp/N2n1pp1/4p3/1r6/4BQ2/P1P2PPP/5RK1 b - - 0 24
23:52:35.724 GameHistoryManager                                          Move added: c5a6
23:52:35.815 GameViewModel                                               🔍 Requesting position evaluation...
23:52:35.816                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:52:35.816                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:52:35.816                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:52:35.819 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:52:35.820                                                             show: isDexDualMode = false
23:52:35.820                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:52:35.826 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:52:35.826                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:52:35.826                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:52:35.826                                                             🔧 gameRepository class: GameRepository
23:52:35.826                                                             🔧 Current thread: main
23:52:35.826 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:52:36.328 GameViewModel                                               ✅ Evaluation received: -6.94
23:52:36.384 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:52:36.442                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:52:36.513 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c7d7
23:52:36.869 GameHistoryManager                                          Move added: c7d7
23:52:36.869 GameViewModel                                               🔍 Requesting position evaluation...
23:52:36.869                                                             🎭 Updating personality context for move: c7d7
23:52:36.869                                                             ✨ Personality context updated for move c7d7 - This is revolutionary!
23:52:36.869                                                             🔍 Checking game end conditions...
23:52:36.970                                                             ✅ Game continues - no end condition detected
23:52:36.970 Choreographer                                               Skipped 54 frames!  The application may be doing too much work on its main thread.
23:52:37.370 GameViewModel                                               ✅ Evaluation received: -6.95
23:52:45.860                                                             🎯 makePlayerMove called with: a6c5
23:52:45.911                                                             🔍 Validating move: a6c5 (attempt 1)
23:52:46.012                                                             📋 Current position: 3q3r/3kpBbp/N2n1pp1/4p3/1r6/4BQ2/P1P2PPP/5RK1 w - - 1 25
23:52:46.063                                                             ⚖️ Move a6c5 legality check: LEGAL
23:52:46.063                                                             ✅ Executing validated move: a6c5
23:52:46.266                                                             📍 New position after move: 3q3r/3kpBbp/3n1pp1/2N1p3/1r6/4BQ2/P1P2PPP/5RK1 b - - 2 25
23:52:46.279 GameHistoryManager                                          Move added: a6c5
23:52:46.367 GameViewModel                                               🔍 Requesting position evaluation...
23:52:46.368                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:52:46.368                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:52:46.368                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:52:46.371 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:52:46.371                                                             show: isDexDualMode = false
23:52:46.371                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:52:46.375 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:52:46.375                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:52:46.375                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:52:46.375                                                             🔧 gameRepository class: GameRepository
23:52:46.375                                                             🔧 Current thread: main
23:52:46.375 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:52:46.670 GameViewModel                                               ✅ Evaluation received: -7.03
23:52:48.617 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:52:48.657                                                             📊 DATABASE QUERY RESULT: carlsen returned 25 historical positions
23:52:48.701 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7c7
23:52:49.071 GameHistoryManager                                          Move added: d7c7
23:52:49.071 GameViewModel                                               🔍 Requesting position evaluation...
23:52:49.071                                                             🎭 Updating personality context for move: d7c7
23:52:49.072                                                             ✨ Personality context updated for move d7c7 - This is revolutionary!
23:52:49.072                                                             🔍 Checking game end conditions...
23:52:49.173                                                             ✅ Game continues - no end condition detected
23:52:49.426                                                             ✅ Evaluation received: 7.05
23:53:05.515                                                             🎯 makePlayerMove called with: c5e6
23:53:05.566                                                             🔍 Validating move: c5e6 (attempt 1)
23:53:05.667                                                             📋 Current position: 3q3r/2k1pBbp/3n1pp1/2N1p3/1r6/4BQ2/P1P2PPP/5RK1 w - - 3 26
23:53:05.718                                                             ⚖️ Move c5e6 legality check: LEGAL
23:53:05.718                                                             ✅ Executing validated move: c5e6
23:53:05.922                                                             📍 New position after move: 3q3r/2k1pBbp/3nNpp1/4p3/1r6/4BQ2/P1P2PPP/5RK1 b - - 4 26
23:53:05.935 GameHistoryManager                                          Move added: c5e6
23:53:06.022 GameViewModel                                               🔍 Requesting position evaluation...
23:53:06.023                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:53:06.023                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:53:06.023                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:53:06.026 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:53:06.026                                                             show: isDexDualMode = false
23:53:06.026                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:53:06.031 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:53:06.031                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:53:06.031                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:53:06.031                                                             🔧 gameRepository class: GameRepository
23:53:06.031                                                             🔧 Current thread: main
23:53:06.031 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:53:06.785 GameViewModel                                               ✅ Evaluation received: -6.98
23:53:06.893 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:53:06.950                                                             📊 DATABASE QUERY RESULT: carlsen returned 35 historical positions
23:53:07.023 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c7b8
23:53:07.379 GameHistoryManager                                          Move added: c7b8
23:53:07.379 GameViewModel                                               🔍 Requesting position evaluation...
23:53:07.379                                                             🎭 Updating personality context for move: c7b8
23:53:07.379                                                             ✨ Personality context updated for move c7b8 - This is revolutionary!
23:53:07.379                                                             🔍 Checking game end conditions...
23:53:07.480                                                             ✅ Game continues - no end condition detected
23:53:07.480 Choreographer                                               Skipped 54 frames!  The application may be doing too much work on its main thread.
23:53:07.831 GameViewModel                                               ✅ Evaluation received: -7.10
23:53:16.470                                                             🎯 makePlayerMove called with: e3a7
23:53:16.522                                                             🔍 Validating move: e3a7 (attempt 1)
23:53:16.623                                                             📋 Current position: 1k1q3r/4pBbp/3nNpp1/4p3/1r6/4BQ2/P1P2PPP/5RK1 w - - 5 27
23:53:16.674                                                             ⚖️ Move e3a7 legality check: LEGAL
23:53:16.674                                                             ✅ Executing validated move: e3a7
23:53:16.876                                                             📍 New position after move: 1k1q3r/B3pBbp/3nNpp1/4p3/1r6/5Q2/P1P2PPP/5RK1 b - - 6 27
23:53:16.887 GameHistoryManager                                          Move added: e3a7
23:53:16.977 GameViewModel                                               🔍 Requesting position evaluation...
23:53:16.978                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:53:16.978                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:53:16.978                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:53:16.982 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:53:16.983                                                             show: isDexDualMode = false
23:53:16.983                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:53:16.989 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:53:16.989                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:53:16.989                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:53:16.989                                                             🔧 gameRepository class: GameRepository
23:53:16.989                                                             🔧 Current thread: main
23:53:16.989 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:53:17.231 GameViewModel                                               ✅ Evaluation received: -11.47
23:53:19.224 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:53:19.267                                                             📊 DATABASE QUERY RESULT: carlsen returned 35 historical positions
23:53:19.316 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8a7
23:53:19.683 GameHistoryManager                                          Move added: b8a7
23:53:19.683 GameViewModel                                               🔍 Requesting position evaluation...
23:53:19.684                                                             🎭 Updating personality context for move: b8a7
23:53:19.684                                                             ✨ Personality context updated for move b8a7 - This is revolutionary!
23:53:19.684                                                             🔍 Checking game end conditions...
23:53:19.785                                                             ✅ Game continues - no end condition detected
23:53:20.037                                                             ✅ Evaluation received: M12
23:53:29.049                                                             🎯 makePlayerMove called with: f3a3
23:53:29.100                                                             🔍 Validating move: f3a3 (attempt 1)
23:53:29.200                                                             📋 Current position: 3q3r/k3pBbp/3nNpp1/4p3/1r6/5Q2/P1P2PPP/5RK1 w - - 0 28
23:53:29.252                                                             ⚖️ Move f3a3 legality check: LEGAL
23:53:29.252                                                             ✅ Executing validated move: f3a3
23:53:29.456                                                             📍 New position after move: 3q3r/k3pBbp/3nNpp1/4p3/1r6/Q7/P1P2PPP/5RK1 b - - 1 28
23:53:29.467 GameHistoryManager                                          Move added: f3a3
23:53:29.555 GameViewModel                                               🔍 Requesting position evaluation...
23:53:29.556                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:53:29.556                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:53:29.556                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:53:29.561 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:53:29.562                                                             show: isDexDualMode = false
23:53:29.562                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:53:29.567 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:53:29.567                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:53:29.567                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:53:29.567                                                             🔧 gameRepository class: GameRepository
23:53:29.567                                                             🔧 Current thread: main
23:53:29.567 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:53:29.807 GameViewModel                                               ✅ Evaluation received: M-11
23:53:31.803 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:53:31.853                                                             📊 DATABASE QUERY RESULT: carlsen returned 35 historical positions
23:53:31.908 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a7b8
23:53:32.273 GameHistoryManager                                          Move added: a7b8
23:53:32.273 GameViewModel                                               🔍 Requesting position evaluation...
23:53:32.274                                                             🎭 Updating personality context for move: a7b8
23:53:32.274                                                             ✨ Personality context updated for move a7b8 - This is revolutionary!
23:53:32.274                                                             🔍 Checking game end conditions...
23:53:32.375                                                             ✅ Game continues - no end condition detected
23:53:32.576                                                             ✅ Evaluation received: M10
23:53:40.173                                                             🎯 makePlayerMove called with: a3b4
23:53:40.224                                                             🔍 Validating move: a3b4 (attempt 1)
23:53:40.324                                                             📋 Current position: 1k1q3r/4pBbp/3nNpp1/4p3/1r6/Q7/P1P2PPP/5RK1 w - - 2 29
23:53:40.376                                                             ⚖️ Move a3b4 legality check: LEGAL
23:53:40.376                                                             ✅ Executing validated move: a3b4
23:53:40.579                                                             📍 New position after move: 1k1q3r/4pBbp/3nNpp1/4p3/1Q6/8/P1P2PPP/5RK1 b - - 0 29
23:53:40.589 GameHistoryManager                                          Move added: a3b4
23:53:40.679 GameViewModel                                               🔍 Requesting position evaluation...
23:53:40.680                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:53:40.680                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:53:40.680                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:53:40.683 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:53:40.683                                                             show: isDexDualMode = false
23:53:40.683                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:53:40.688 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:53:40.688                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:53:40.688                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:53:40.688                                                             🔧 gameRepository class: GameRepository
23:53:40.688                                                             🔧 Current thread: main
23:53:40.688 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:53:40.933 GameViewModel                                               ✅ Evaluation received: M-9
23:53:42.944 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:53:43.002                                                             📊 DATABASE QUERY RESULT: carlsen returned 35 historical positions
23:53:43.058 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d8b6
23:53:43.425 GameHistoryManager                                          Move added: d8b6
23:53:43.425 GameViewModel                                               🔍 Requesting position evaluation...
23:53:43.425                                                             🎭 Updating personality context for move: d8b6
23:53:43.425                                                             ✨ Personality context updated for move d8b6 - This is revolutionary!
23:53:43.425                                                             🔍 Checking game end conditions...
23:53:43.526                                                             ✅ Game continues - no end condition detected
23:53:43.827                                                             ✅ Evaluation received: M3
23:53:54.960                                                             🎯 makePlayerMove called with: b4b6
23:53:55.012                                                             🔍 Validating move: b4b6 (attempt 1)
23:53:55.113                                                             📋 Current position: 1k5r/4pBbp/1q1nNpp1/4p3/1Q6/8/P1P2PPP/5RK1 w - - 1 30
23:53:55.165                                                             ⚖️ Move b4b6 legality check: LEGAL
23:53:55.165                                                             ✅ Executing validated move: b4b6
23:53:55.367                                                             📍 New position after move: 1k5r/4pBbp/1Q1nNpp1/4p3/8/8/P1P2PPP/5RK1 b - - 0 30
23:53:55.377 GameHistoryManager                                          Move added: b4b6
23:53:55.468 GameViewModel                                               🔍 Requesting position evaluation...
23:53:55.469                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:53:55.469                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:53:55.469                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:53:55.472 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:53:55.473                                                             show: isDexDualMode = false
23:53:55.473                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:53:55.478 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:53:55.478                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:53:55.478                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:53:55.478                                                             🔧 gameRepository class: GameRepository
23:53:55.478                                                             🔧 Current thread: main
23:53:55.478 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:53:55.722 GameViewModel                                               ✅ Evaluation received: M-2
23:53:56.772 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:53:56.822                                                             📊 DATABASE QUERY RESULT: carlsen returned 35 historical positions
23:53:56.874 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8a8
23:53:57.246 GameHistoryManager                                          Move added: b8a8
23:53:57.246 GameViewModel                                               🔍 Requesting position evaluation...
23:53:57.247                                                             🎭 Updating personality context for move: b8a8
23:53:57.247                                                             ✨ Personality context updated for move b8a8 - This is revolutionary!
23:53:57.247                                                             🔍 Checking game end conditions...
23:53:57.349                                                             ✅ Game continues - no end condition detected
23:53:57.499                                                             ✅ Evaluation received: M1
23:54:05.517                                                             🎯 makePlayerMove called with: e6c7
23:54:05.568                                                             🔍 Validating move: e6c7 (attempt 1)
23:54:05.669                                                             📋 Current position: k6r/4pBbp/1Q1nNpp1/4p3/8/8/P1P2PPP/5RK1 w - - 1 31
23:54:05.720                                                             ⚖️ Move e6c7 legality check: LEGAL
23:54:05.720                                                             ✅ Executing validated move: e6c7
23:54:05.924                                                             📍 New position after move: k6r/2N1pBbp/1Q1n1pp1/4p3/8/8/P1P2PPP/5RK1 b - - 2 31
23:54:05.936 GameHistoryManager                                          Move added: e6c7
23:54:06.025 GameViewModel                                               🔍 Requesting position evaluation...
23:54:06.026                                                             🎭 Using PERSONALITY ENGINE for move calculation!
23:54:06.026                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
23:54:06.026                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
23:54:06.030 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$13$com-example-chesspedagogue-MainActivity:611 
23:54:06.031                                                             show: isDexDualMode = false
23:54:06.031                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
23:54:06.038 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
23:54:06.038                                                             🔧 DEBUG: gameRepository instance = NOT NULL
23:54:06.038                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
23:54:06.038                                                             🔧 gameRepository class: GameRepository
23:54:06.038                                                             🔧 Current thread: main
23:54:06.038 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
23:54:06.277 GameViewModel                                               ✅ Evaluation received: M0
23:54:07.309 System.out                                                  📊 DATABASE QUERY RESULT: Magnus Carlsen returned 0 historical positions
23:54:07.359                                                             📊 DATABASE QUERY RESULT: carlsen returned 35 historical positions
23:54:07.413 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: (none)
23:54:07.783 GameHistoryManager                                          Move added: (none)
23:54:07.783 GameViewModel                                               🔍 Requesting position evaluation...
23:54:07.783                                                             🎭 Updating personality context for move: (none)
23:54:07.783                                                             ✨ Personality context updated for move (none) - This is revolutionary!
23:54:07.783                                                             🔍 Checking game end conditions...
23:54:07.885                                                             ✅ Game continues - no end condition detected
23:54:08.035                                                             ✅ Evaluation received: M0
