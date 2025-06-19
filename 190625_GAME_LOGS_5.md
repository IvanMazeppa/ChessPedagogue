20:06:57.839 ProfileInstaller        com.example.chesspedagogue          Installing profile for com.example.chesspedagogue
20:07:02.270 ActivityThread                                              com.example.chesspedagogue will use render engine as VK
20:07:02.282 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@7f672af
20:07:02.346 ScrollView                                                  initGoToTop
20:07:02.354                                                             initGoToTop
20:07:02.360 InputMethodManager                                          invalidateInput
20:07:02.361                                                             invalidateInput
20:07:02.364 LogThrottlerConfig                                          📊 Log throttling initialized: VERBOSE
20:07:02.364 ApiKeyConfig                                                Using API key from ApiKeys class
20:07:02.375 OpenAIService                                               API key set, length: 164
20:07:02.375 ApiKeyConfig                                                Initialized OpenAIClient with API key from config
20:07:02.376 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.376                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.376 ApiKeyConfig                                                Using API key from ApiKeys class
20:07:02.376 OpenAIService                                               API key set, length: 164
20:07:02.376 ApiKeyConfig                                                Initialized OpenAIClient with API key from config
20:07:02.376 TTSServiceManager                                           ✅ Switched to ElevenLabs TTS
20:07:02.380 🎭 VoiceEm...alAnalyzer                                     ✅ Initialized voice profiles for 12 masters
20:07:02.380                                                             🚀 Voice-Emotion Feedback System initialized
20:07:02.380 TTSServiceManager                                           🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.380 ApiKeyConfig                                                Using API key from ApiKeys class
20:07:02.380                                                             Using API key from ApiKeys class
20:07:02.380 OpenAIService                                               API key set, length: 164
20:07:02.642 ResponsesAPI                                                ✅ Created managed executor service for ResponsesAPIService
20:07:02.642 AIStyleAdvisor                                              🧠 AIStyleAdvisor initialized - ready for chess personality enhancement!
20:07:02.697 GameViewModel                                               🎭 Initializing personality LiveData...
20:07:02.698                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
20:07:02.698                                                             🔍 Requesting position evaluation...
20:07:02.703                                                             ✅ Auto-commentary system enabled
20:07:02.703 ApiKeyConfig                                                Using API key from ApiKeys class
20:07:02.704 GameViewModel                                               🎮 Starting new game with configuration: white, skill=10, elo=1750
20:07:02.705 GameHistoryManager                                          GameHistoryManager instance created
20:07:02.705                                                             Game history cleared
20:07:02.706 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.706                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.706                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.706                                                             ✅ Usage context set to: spectator_mode
20:07:02.707 EmotionalIntelligence                                       🚀 CONSTRUCTOR CALLED: EmotionalIntelligenceManager initialization starting...
20:07:02.708                                                             ✅ Persistence manager initialized for EQ database operations
20:07:02.708                                                             🔧 About to call verifyDatabaseIntegrity()...
20:07:02.709 RelationshipPersistence                                     🔧 Database integrity check - emotional_reactions table exists: true
20:07:02.709                                                             🔧 Emotional reactions table schema:
20:07:02.710                                                               - Column: id (INTEGER) NOT NULL: false
20:07:02.710                                                               - Column: master (TEXT) NOT NULL: true
20:07:02.710                                                               - Column: opponent (TEXT) NOT NULL: true
20:07:02.710                                                               - Column: topic (TEXT) NOT NULL: true
20:07:02.710                                                               - Column: emotion (TEXT) NOT NULL: true
20:07:02.710                                                               - Column: intensity (REAL) NOT NULL: true
20:07:02.710                                                               - Column: momentum (REAL) NOT NULL: false
20:07:02.710                                                               - Column: game_context (TEXT) NOT NULL: false
20:07:02.710                                                               - Column: position_evaluation (REAL) NOT NULL: false
20:07:02.710                                                               - Column: conversation_snippet (TEXT) NOT NULL: false
20:07:02.710                                                               - Column: relationship_impact (REAL) NOT NULL: false
20:07:02.710                                                               - Column: timestamp (INTEGER) NOT NULL: true
20:07:02.710                                                             🔧 Database integrity check - master_relationships table exists: true
20:07:02.710                                                             🔧 Master relationships table schema:
20:07:02.711                                                               - Column: id (INTEGER) NOT NULL: false
20:07:02.711                                                               - Column: master1 (TEXT) NOT NULL: true
20:07:02.711                                                               - Column: master2 (TEXT) NOT NULL: true
20:07:02.711                                                               - Column: respect_level (REAL) NOT NULL: false
20:07:02.711                                                               - Column: rivalry_intensity (REAL) NOT NULL: false
20:07:02.711                                                               - Column: friendship_bond (REAL) NOT NULL: false
20:07:02.711                                                               - Column: communication_style (TEXT) NOT NULL: false
20:07:02.711                                                               - Column: total_interactions (INTEGER) NOT NULL: false
20:07:02.711                                                               - Column: total_games (INTEGER) NOT NULL: false
20:07:02.711                                                               - Column: last_major_event (TEXT) NOT NULL: false
20:07:02.711                                                               - Column: last_updated (INTEGER) NOT NULL: false
20:07:02.711                                                               - Column: created_at (INTEGER) NOT NULL: false
20:07:02.711                                                             🔧 Database tables exist - testing simple operations...
20:07:02.713                                                             🔧 Current emotional_reactions records: 657
20:07:02.713                                                             🔧 Current master_relationships records: 45
20:07:02.714 EmotionalIntelligence                                       🔧 verifyDatabaseIntegrity() completed
20:07:02.714                                                             🧪 About to call testDatabaseWrites()...
20:07:02.714 RelationshipPersistence                                     🧪 Testing database writes...
20:07:02.714                                                             🔧 Recording emotional reaction - Parameters: master=alekhine, opponent=carlsen, topic=chess_aesthetics, emotion=impressed, intensity=0.80
20:07:02.806 GameViewModel                                               🔍 Requesting position evaluation...
20:07:02.806                                                             🔄 Skipping evaluation - too soon since last request
20:07:02.806                                                             ✅ Game initialized with white vs 1750 Elo engine
20:07:02.811 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.initializeGameWithConfiguration:1101 
20:07:02.812                                                             show: isDexDualMode = false
20:07:02.812                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
20:07:02.815 🔗 LiveMonitorClient                                        Built server URL: ws://192.168.0.237:8080
20:07:02.816                                                             LiveMonitorClient initialized with server URL: ws://192.168.0.237:8080
20:07:02.816                                                             🚫 Live monitor disabled via ENABLE_LIVE_MONITOR flag - skipping connection
20:07:02.820 VoiceControlManager                                         🎤 VoiceControlManager initialized
20:07:02.820                                                             📋 Registered voice command listener: main_game
20:07:02.822 RelationshipPersistence                                     🎭 Recorded emotional reaction: alekhine felt impressed (0.80) about chess_aesthetics with carlsen
20:07:02.823                                                             👥 Retrieved relationship: alekhine <-> carlsen (respect: 7.90, rivalry: 0.00, friendship: 0.00)
20:07:02.823 UserProfileManager                                          👤 Loaded user profile: Mr Ben Lockrey
20:07:02.823                                                             🎭 Integrating user profile with emotional intelligence systems...
20:07:02.825 RelationshipPersistence                                     🔧 Updating relationship - alekhine <-> carlsen (respect: 8.00, rivalry: 0.00, friendship: 0.00)
20:07:02.844 UserProfileManager                                          ✅ User successfully integrated into AI emotional ecosystem
20:07:02.845 RelationshipPersistence                                     💾 Updated relationship: alekhine <-> carlsen (respect: 8.00, rivalry: 0.00, friendship: 0.00)
20:07:02.845                                                             🧪 Database write test completed
20:07:02.845 EmotionalIntelligence                                       🧪 testDatabaseWrites() completed
20:07:02.845                                                             🎭 Enhanced Emotional Intelligence Manager initialized with database persistence
20:07:02.848 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.848                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.848                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.848 ApiKeyConfig                                                Using API key from ApiKeys class
20:07:02.848 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.848                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.848                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.848 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
20:07:02.849 GameViewModel                                               🎭 CONFIGURING PERSONALITY ENGINE: alekhine (weight=1.00, enabled=true)
20:07:02.852 ConversationVariety                                         ✅ Loaded 6 tracked phrases
20:07:02.853 EmotionalIntelligence                                       🧠 Loaded emotional history for tal vs fischer: 0 events, momentum: 0.40
20:07:02.853 ConversationFlowTester                                      🎭 OPTIMIZING CONVERSATIONS FOR SPECTATOR MODE:
20:07:02.853                                                             📊 Current template analysis:
20:07:02.853                                                             🏥 CONVERSATION HEALTH METRICS:
20:07:02.854 VoiceControlManager                                         📋 Registered voice command listener: main_game
20:07:02.856 Conversati...maTemplate                                     🌊 CREATING NATURAL FLOW TEMPLATE: Natural Decline (Pattern: DECLINING, First: 60 words, Follow-up: 60.0% of first)
20:07:02.856                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Natural Decline (Initial: 48-72 words, Followup: 8-36 words, Flow: DECLINING, Intensity: 0.8)
20:07:02.857 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
20:07:02.857 Conversati...maTemplate                                     🌊 CREATING NATURAL FLOW TEMPLATE: Building Excitement (Pattern: BUILDING, First: 35 words, Follow-up: 80.0% of first)
20:07:02.857                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Building Excitement (Initial: 28-42 words, Followup: 8-28 words, Flow: BUILDING, Intensity: 0.8)
20:07:02.858                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Wave Discussion (Pattern: WAVE, First: 50 words, Follow-up: 70.0% of first)
20:07:02.858                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Wave Discussion (Initial: 40-60 words, Followup: 8-35 words, Flow: WAVE, Intensity: 0.8)
20:07:02.858                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Explosive Debate (Pattern: EXPLOSIVE, First: 40 words, Follow-up: 50.0% of first)
20:07:02.858                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Explosive Debate (Initial: 32-48 words, Followup: 8-20 words, Flow: EXPLOSIVE, Intensity: 0.8)
20:07:02.860 ConversationFlowTester                                      🎭 CURRENT: Engaging Chess Dialogue | Opening: 4-6 turns (90% chance) | Analysis: 4-6 turns (90% chance) | Style: DRAMATIC
20:07:02.860                                                               📊 opening: 4-6 turns, 90% response chance, 2000ms intervals
20:07:02.860                                                               📊 brilliant_move: 4-6 turns, 90% response chance, 1800ms intervals
20:07:02.860                                                               📊 position_change: 4-6 turns, 90% response chance, 2200ms intervals
20:07:02.860                                                               📊 endgame: 4-6 turns, 90% response chance, 2800ms intervals
20:07:02.860                                                             🔬 Running spectator mode simulation:
20:07:02.860                                                             🔬 SIMULATING CONVERSATION FLOW:
20:07:02.860                                                             🎯 Trigger: opening
20:07:02.860                                                             📋 Schema: 4-6 turns, 90.0% response chance
20:07:02.861 Choreographer                                               Skipped 71 frames!  The application may be doing too much work on its main thread.
20:07:02.862 ConversationFlowTester                                        Turn 1: ✅ CONTINUE (Current: 1/4-6)
20:07:02.862                                                               Turn 2: ✅ CONTINUE (Current: 2/4-6)
20:07:02.862                                                               Turn 3: ✅ CONTINUE (Current: 3/4-6)
20:07:02.862                                                               Turn 4: 🛑 STOP (Current: 4/4-6)
20:07:02.862                                                             🏁 Conversation ended after 4 turns
20:07:02.862                                                             🔬 SIMULATING CONVERSATION FLOW:
20:07:02.862                                                             🎯 Trigger: position_change
20:07:02.862                                                             📋 Schema: 4-6 turns, 90.0% response chance
20:07:02.862                                                               Turn 1: ✅ CONTINUE (Current: 1/4-6)
20:07:02.862                                                               Turn 2: ✅ CONTINUE (Current: 2/4-6)
20:07:02.863                                                               Turn 3: ✅ CONTINUE (Current: 3/4-6)
20:07:02.863                                                               Turn 4: ✅ CONTINUE (Current: 4/4-6)
20:07:02.863                                                               Turn 5: ✅ CONTINUE (Current: 5/4-6)
20:07:02.863                                                               Turn 6: 🛑 STOP (Current: 6/4-6)
20:07:02.863                                                             🏁 Conversation ended after 6 turns
20:07:02.863                                                             💡 SPECTATOR MODE RECOMMENDATIONS:
20:07:02.863                                                               🎯 Use 'engaging' template for balanced multi-turn dialogues
20:07:02.863                                                               🎯 Opening conversations: 3-6 turns (current spectator games start strong)
20:07:02.863                                                               🎯 Position analysis: 4-8 turns (masters debate moves actively)
20:07:02.863                                                               🎯 85% response rate (masters usually engage with each other)
20:07:02.863                                                               🎯 2.5s intervals (natural conversation pacing)
20:07:02.863 Conversati...maTemplate                                     🎭 CONVERSATION TEMPLATE CHANGED: Engaging Chess Dialogue (Intensity: ENGAGING, Style: DRAMATIC)
20:07:02.863                                                             🔧 TESTING: Engaging conversations (recommended for spectator mode)
20:07:02.863 ConversationFlowTester                                      🎭 CONVERSATION MODE: Engaging (3-6 opening turns, 4-8 analysis turns, 85% response rate)
20:07:02.864                                                             📊 🎭 CURRENT: Engaging Chess Dialogue | Opening: 4-6 turns (90% chance) | Analysis: 4-6 turns (90% chance) | Style: DRAMATIC
20:07:02.864                                                             ✅ APPLIED: Engaging conversation template for spectator mode
20:07:02.864 🎭 EmotionalMomentum                                        🚀 Emotional Momentum Manager initialized
20:07:02.864 🎭 Phase2Bridge                                             🚀 Phase 2 Emotional Integration Bridge initialized with momentum system
20:07:02.864 ResponsesA...tionHelper                                     Responses API for tal: enabled
20:07:02.865                                                             Responses API for fischer: enabled
20:07:02.865                                                             Responses API for carlsen: enabled
20:07:02.865                                                             Responses API for anand: enabled
20:07:02.865                                                             Responses API for alekhine: enabled
20:07:02.865                                                             Responses API for kasparov: enabled
20:07:02.865                                                             🔧 FORCE ENABLED Responses API for the 6 working masters: Tal, Fischer, Carlsen, Anand, Alekhine, Kasparov
20:07:02.865                                                             📋 Other masters (Kramnik, Karpov, etc.) will use fallback dialogue
20:07:02.865                                                             🔄 Integration helper initialized. Responses API enabled: true
20:07:02.865                                                             🎯 Masters with Responses API enabled: {anand=true, kasparov=true, alekhine=true, fischer=true, carlsen=true, tal=true}
20:07:02.865                                                             📋 Master 'anand' → Responses API: true
20:07:02.865                                                             📋 Master 'kasparov' → Responses API: true
20:07:02.865                                                             📋 Master 'alekhine' → Responses API: true
20:07:02.865                                                             📋 Master 'fischer' → Responses API: true
20:07:02.865                                                             📋 Master 'carlsen' → Responses API: true
20:07:02.865                                                             📋 Master 'tal' → Responses API: true
20:07:02.882 BufferQueueProducer                                         [](id:784700000001,api:0,p:0,c:30791) setDequeueTimeout:2077252342
20:07:02.883 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
20:07:02.883                                                             Access denied finding property "vendor.gpp.create_frc_extension"
20:07:02.891 ScrollView                                                   onsize change changed 
20:07:02.900 ChessSetManager                                             ✅ Initialized 2 chess sets
20:07:02.900                                                             🎨 Chess Set Manager initialized with set: staunton_classic
20:07:02.933 BLASTBufferQueue                                            [VRI[MainActivity]@1051dc#1](f:0,a:0,s:0) onFrameAvailable the first frame is available
20:07:02.933 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 36936, bufferData(ID: 132246338011143, frameNumber: 1)
20:07:02.934 HWUI                                                        CFMS:: SetUp Pid : 30791    Tid : 30816
20:07:02.941 ApiKeyConfig                                                Using API key from ApiKeys class
20:07:02.942 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.942                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.942                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.943 ThreeStage...nseManager                                     🚀 Enhanced ThreeStageResponseManager initialized with Responses API integration!
20:07:02.943                                                             🔍 Integration Status Check:
20:07:02.943 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: fischer
20:07:02.943                                                             🎯 Master fischer Responses API enabled: true
20:07:02.943 ThreeStage...nseManager                                     🎯 Fischer eligible: true
20:07:02.943 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: tal
20:07:02.943                                                             🎯 Master tal Responses API enabled: true
20:07:02.943 ThreeStage...nseManager                                     🎯 Tal eligible: true
20:07:02.943 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: carlsen
20:07:02.943                                                             🎯 Master carlsen Responses API enabled: true
20:07:02.943 ThreeStage...nseManager                                     🎯 Carlsen eligible: true
20:07:02.944 ApiKeyConfig                                                Using API key from ApiKeys class
20:07:02.944 OpenAIService                                               API key set, length: 164
20:07:02.944 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.944                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.944                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.946 OpenAIService                                               API key set, length: 164
20:07:02.947 ConversationManager                                         Added new system message
20:07:02.947 OpenAIService                                               API key set, length: 164
20:07:02.947 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.947 ConversationManager                                         ConversationManager initialized with session: session_1750360022947
20:07:02.947 TTSServiceManager                                           🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.947                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.963 ConversationManager                                         Added new system message
20:07:02.963                                                             ✅ Started new conversation with session: session_1750360022963
20:07:02.975 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
20:07:02.989 InputMethodManagerUtils                                     startInputInner - Id : 0
20:07:02.989 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
20:07:02.990 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.990                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.990                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.990 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
20:07:02.991 GameViewModel                                               🎭 CONFIGURING PERSONALITY ENGINE: alekhine (weight=1.00, enabled=true)
20:07:02.992 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:02.992                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:02.992                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:02.992 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
20:07:03.426 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@ab4ebd2
20:07:03.443 GameViewModel                                               ✅ Evaluation received: 0.29
20:07:05.801 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:07:05.801                                                             📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:07:05.803 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #1)
20:07:05.803                                                             🧠 Requesting AI advice for alekhine (request #2)
20:07:05.803                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:05.803                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:05.803                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -...
20:07:05.804                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -...
20:07:05.804                                                             🎲 Candidate moves for AI analysis: [g1f3]
20:07:05.804                                                             🎲 Candidate moves for AI analysis: [d2d4]
20:07:05.804 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:05.804                                                             🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:05.804                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:05.804                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:05.804                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:07:05.804                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:07:06.410                                                             📋 Response ID: resp_68545fda32bc81a1a9f56271a3dfcd0b058639883b3f9775
20:07:06.457                                                             📋 Response ID: resp_68545fda3a80819db54924cf235e56c400b508536ca020b9
20:07:09.564                                                             🏁 Response completed
20:07:09.564 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["g1f3", "d2d4"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "As Alekhine,...
20:07:09.564                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
20:07:09.564                                                             🎭 AI preferred moves: [g1f3]
20:07:09.564                                                             💭 AI reasoning: As Alekhine, my style is aggressive and dynamic. I aim to control the center swiftly and develop my pieces with purpose. The move g1f3 prepares for a strong central presence with d2d4, while also ensuring rapid development.
20:07:09.674 ResponsesAPI                                                🏁 Response completed
20:07:09.674 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["e2e4", "d2d4"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "As Alekhine,...
20:07:09.675                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
20:07:09.675                                                             🎭 AI preferred moves: [d2d4]
20:07:09.675                                                             💭 AI reasoning: As Alekhine, I favor aggressive and dynamic play. Starting with 1.e4, I aim to control the center and create open lines for my pieces. This move supports rapid development and prepares for potential tactical opportunities. Alternatively, 1.d4 offers strategic central control, but with a more closed structure, allowing for a buildup before launching an attack.
20:07:26.573 Dialog                                                      mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
20:07:26.582 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@7041c02
20:07:26.590 ScrollView                                                  initGoToTop
20:07:26.607 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{8188efe V.E...... R.....I. 0,0-0,0}[MainActivity], caller=android.view.WindowManagerImpl.addView:158 android.app.Dialog.show:511 com.example.chesspedagogue.MainActivity.launchCompetitiveMode:3146 
20:07:26.609 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
20:07:26.614 VRI[MainAc...y]@47a765f                                     synced displayState. AttachInfo displayState=2
20:07:26.615                                                             setView = com.android.internal.policy.DecorView@8188efe IsHRR=false TM=true
20:07:26.648 BufferQueueProducer                                         [](id:784700000002,api:0,p:16777224,c:30791) setDequeueTimeout:2077252342
20:07:26.648 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
20:07:26.648                                                             Access denied finding property "vendor.gpp.create_frc_extension"
20:07:26.648 VRI[MainAc...y]@47a765f                                     Relayout returned: old=(0,100,1440,2908) new=(36,165,1404,2843) relayoutAsync=false req=(1368,2678)0 dur=5 res=0x3 s={true 0xb40000718bacc800} ch=true seqId=0
20:07:26.649                                                             performConfigurationChange setNightDimText nightDimLevel=0
20:07:26.649                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb40000718bacc800} hwInitialized=true
20:07:26.667 AbsListView                                                  in onLayout changed 
20:07:26.668 ScrollView                                                   onsize change changed 
20:07:26.669 VRI[MainAc...y]@47a765f                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
20:07:26.669                                                             Setup new sync=wmsSync-VRI[MainActivity]@47a765f#4
20:07:26.669                                                             Creating new active sync group VRI[MainActivity]@47a765f#5
20:07:26.669                                                             registerCallbacksForSync syncBuffer=false
20:07:26.676                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
20:07:26.676                                                             mWNT: t=0xb4000071ed18ed80 mBlastBufferQueue=0xb4000071ecff9f00 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
20:07:26.676                                                             Setting up sync and frameCommitCallback
20:07:26.679 BLASTBufferQueue                                            [VRI[MainActivity]@47a765f#2](f:0,a:0,s:0) onFrameAvailable the first frame is available
20:07:26.679 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 36949, bufferData(ID: 132246338011147, frameNumber: 1)
20:07:26.679 VRI[MainAc...y]@47a765f                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
20:07:26.679 HWUI                                                        CFMS:: SetUp Pid : 30791    Tid : 30816
20:07:26.679 VRI[MainAc...y]@47a765f                                     reportDrawFinished seqId=0
20:07:26.681 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
20:07:26.709 VRI[MainAc...y]@47a765f                                     mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb40000718bacc800}
20:07:28.151                                                             ViewPostIme pointer 0
20:07:28.152                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[MainActivity]@47a765f
20:07:28.254                                                             ViewPostIme pointer 1
20:07:28.254 AbsListView                                                 onTouchUp() mTouchMode : 2
20:07:28.267 WindowManager                                               WindowManagerGlobal#removeView, ty=2, view=com.android.internal.policy.DecorView{8188efe V.E...... R......D 0,0-1368,2678}[MainActivity], caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.app.Dialog.dismissDialog:808 
20:07:28.267 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@cea9d0a
20:07:28.270 VRI[MainAc...y]@47a765f                                     dispatchDetachedFromWindow
20:07:28.275 InputEventReceiver                                          Attempted to finish an input event but the input event receiver has already been disposed.
20:07:28.277 VoiceControlManager                                         📋 Unregistered voice command listener
20:07:28.282 ActivityThread                                              com.example.chesspedagogue will use render engine as VK
20:07:28.290 CompetitiveModeActivity                                     🏆 Starting competitive mode against chess master...
20:07:28.292 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@8205d9c
20:07:28.315 ScrollView                                                  initGoToTop
20:07:28.319 CompetitiveModeActivity                                     🔄 Synchronizing master selection: alekhine
20:07:28.319 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
20:07:28.319 CompetitiveModeActivity                                     🔍 Master sync verification:
20:07:28.319                                                               - Voice system (ChessFineTunedModels): alekhine
20:07:28.319                                                               - App system (ChessAppPrefs): alekhine
20:07:28.319                                                               - FineTunedModelManager: alekhine
20:07:28.319                                                             ✅ Master synchronization SUCCESS! All systems consistent.
20:07:28.319                                                             🎯 Competitive config - Master: alekhine, Color: white, Skill: 10, ELO: 1750
20:07:28.319                                                             ✅ Master synchronized across all SharedPreferences stores
20:07:28.320                                                             🎨 Initializing views...
20:07:28.320                                                             ✅ Dynamic master elements set for: alekhine
20:07:28.321                                                             ✅ Voice status indicator initialized
20:07:28.321                                                             ✅ Views initialized
20:07:28.321                                                             🎭 Initializing emotional intelligence systems...
20:07:28.321                                                             🧠 Initializing EQ system with historical emotional data...
20:07:28.324 EmotionalIntelligence                                       🧠 Loaded emotional history for alekhine vs player: 0 events, momentum: 0.25
20:07:28.324 CompetitiveModeActivity                                     ✅ EQ system initialized - emotional reactions and relationships should now work!
20:07:28.326 EmotionalS...egyLearner                                     🧠 Loaded 0 master strategy profiles from database
20:07:28.326                                                             🧠🎯 EmotionalStrategyLearner initialized - Ready for adaptive learning!
20:07:28.327 CrossMaste...ectiveness                                     ✅ Loaded global effectiveness statistics
20:07:28.327 CompetitiveModeActivity                                     🧠 Phase 3: Adaptive conversation strategy manager initialized
20:07:28.327                                                             ✅ Emotional intelligence systems initialized
20:07:28.327                                                             🎲 Initializing personality engine for alekhine...
20:07:28.327 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
20:07:28.327 DynamicRelationship                                         ✅ Loaded existing relationship dynamics
20:07:28.327 CompetitiveModeActivity                                     🎲 Creating PersonalityEngine instance...
20:07:28.327                                                             🎯 Setting current master to: alekhine
20:07:28.327                                                             🚀 CALLING initializeMasterData for: alekhine
20:07:28.327                                                             🎭 Enabling personality play...
20:07:28.329                                                             ⚖️ Using splash difficulty - ELO: 1750, Skill: 10
20:07:28.329                                                             ✅ Personality engine initialized for alekhine
20:07:28.329                                                             🔍 Running personality engine database diagnostic...
20:07:28.329 PersonalityDiagnostic                                       🔍 DIAGNOSING PERSONALITY ENGINE DATABASE FOR: alekhine
20:07:28.331                                                             📊 Master 'alekhine' - hasData: true, positions: 16368
20:07:28.331                                                             ✅ Data already exists - testing sample query...
20:07:28.331                                                             🧪 Testing sample query for alekhine...
20:07:28.332                                                             🧪 Sample query returned 5 results
20:07:28.332                                                             ✅ DATABASE QUERY WORKING! Sample position found:
20:07:28.332                                                                Master: alekhine
20:07:28.332                                                                FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq
20:07:28.332                                                                Opponent: Frank Marshall
20:07:28.335                                                             📊 Complete database stats: {anand=1870, kramnik=6707, kasparov=5010, alekhine=16368, fischer=5767, karpov=1866, capablanca=6051, carlsen=5739, total_positions=54156, tal=4778}
20:07:28.335 CompetitiveModeActivity                                     🔬 Running comprehensive PersonalityEngine system diagnostic...
20:07:28.335 PersonalitySystemDiag                                       🔬 STARTING COMPREHENSIVE PERSONALITY ENGINE DIAGNOSTIC
20:07:28.335                                                             📋 Test Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
20:07:28.335                                                             🎭 Master: alekhine (Target ELO: 1750)
20:07:28.335                                                             🔍 Phase 1: Database Analysis
20:07:28.336                                                             📊 Total alekhine positions in DB: 16368
20:07:28.338                                                             🎯 Exact FEN matches: 50
20:07:28.338                                                             🔍 Board pattern: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
20:07:28.338                                                             📋 Sample matched games: null
20:07:28.338                                                             🤖 Phase 2: AI Integration Analysis
20:07:28.338                                                             🤖 Assistant available for alekhine: true
20:07:28.338                                                             📚 Vector store connected: false
20:07:28.338                                                             🧠 AI analysis capability: Available
20:07:28.338                                                             🎨 Phase 3: Style Application Analysis
20:07:28.338                                                             ♟️ Stockfish candidates: null
20:07:28.338                                                             🎭 Personality weight: 0.0
20:07:28.338                                                             📊 Phase 4: Quality Assessment
20:07:28.338                                                             📊 Quality assessment complete - Score: 0.29999998
20:07:28.338                                                             ✅ DIAGNOSTIC COMPLETE
20:07:28.339                                                             === PersonalityEngine System Diagnostic ===
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
20:07:28.339 CompetitiveModeActivity                                     📊 System diagnostic result: === PersonalityEngine System Diagnostic ===
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
20:07:28.339 PersonalitySystemDiag                                       🔍 QUICK NAME DIAGNOSTIC
20:07:28.342                                                             📊 Actual master names in database:
20:07:28.342                                                                anand: 1870 positions
20:07:28.342                                                                kramnik: 6707 positions
20:07:28.342                                                                kasparov: 5010 positions
20:07:28.342                                                                alekhine: 16368 positions
20:07:28.342                                                                fischer: 5767 positions
20:07:28.342                                                                karpov: 1866 positions
20:07:28.342                                                                capablanca: 6051 positions
20:07:28.342                                                                carlsen: 5739 positions
20:07:28.342                                                                total_positions: 54156 positions
20:07:28.342                                                                tal: 4778 positions
20:07:28.343 CompetitiveModeActivity                                     🎤 Initializing voice services...
20:07:28.343 VoiceControlManager                                         📋 Registered voice command listener: competitive_mode
20:07:28.344 CompetitiveModeActivity                                     🗣️ Updating voice for master: alekhine
20:07:28.344 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
20:07:28.344 CompetitiveModeActivity                                     🔍 FineTunedModelManager master: alekhine
20:07:28.344                                                             🔍 SharedPreferences master: alekhine
20:07:28.344 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:28.344                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:28.344                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:28.344 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
20:07:28.344 TTSServiceManager                                           ✅ Usage context set to: competitive_mode
20:07:28.344 CompetitiveModeActivity                                     ✅ Final verification - Voice system master: alekhine
20:07:28.344                                                             ✅ Voice services initializing...
20:07:28.430 WindowManager           system_server                       win=Window{4e50b4d u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity EXITING} destroySurfaces: appStopped=false cleanupOnResume=false win.mWindowRemovalAllowed=true win.mRemoveOnExit=true win.mViewVisibility=0 caller=com.android.server.wm.WindowState.onExitAnimationDone:222 com.android.server.wm.WindowState.onAnimationFinished:161 com.android.server.wm.WindowContainer$$ExternalSyntheticLambda5.onAnimationFinished:26 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda1.run:28 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda0.onAnimationFinished:65 com.android.server.wm.LocalAnimationAdapter$$ExternalSyntheticLambda0.run:10 android.os.Handler.handleCallback:959 
20:07:28.648 GameViewModel           com.example.chesspedagogue          🎭 Initializing personality LiveData...
20:07:28.649                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
20:07:28.649                                                             🔍 Requesting position evaluation...
20:07:28.649 CompetitiveModeActivity                                     🎭 Configuring GameRepository personality engine for alekhine
20:07:28.651                                                             ✅ GameRepository personality engine configured successfully!
20:07:28.651                                                             🎯 Master: alekhine, Weight: 0.3, Enabled: true
20:07:28.651                                                             🔍 Personality engine availability check: true
20:07:28.651                                                             ✅ CONFIRMED: GameRepository personality engine is properly configured and available!
20:07:28.651                                                             ✅ Game view model configured for competitive mode
20:07:28.651                                                             👀 Setting up observers...
20:07:28.652                                                             🎯 Setting up chess board interaction...
20:07:28.652                                                             ✅ Chess board interaction setup complete
20:07:28.652                                                             ✅ Observers setup complete
20:07:28.652                                                             🎮 Setting up controls...
20:07:28.654                                                             ✅ Controls setup complete
20:07:28.654                                                             ✅ All competitive mode systems initialized!
20:07:28.654                                                             🏁 Starting competitive game vs alekhine
20:07:28.654 GameViewModel                                               🎮 Starting new game with configuration: white, skill=10, elo=1750
20:07:28.803 GameHistoryManager                                          Game history cleared
20:07:28.903 GameViewModel                                               🔍 Requesting position evaluation...
20:07:28.904                                                             🔄 Skipping evaluation - too soon since last request
20:07:28.904                                                             ✅ Game initialized with white vs 1750 Elo engine
20:07:28.904 AdaptiveStrategy                                            🔍 Started monitoring conversation: competitive_1750360048904 (alekhine vs player)
20:07:28.904 CompetitiveModeActivity                                     🧠 Phase 3: Started adaptive conversation monitoring for alekhine
20:07:28.905 RelationshipPersistence                                     👥 Retrieved relationship: alekhine <-> player (respect: 0.50, rivalry: 0.00, friendship: 0.00)
20:07:28.905 EmotionalS...egyLearner                                     🧠 Started learning session: alekhine vs player
20:07:28.905                                                             🔍 EXPLORATION: alekhine trying supportive approach vs player
20:07:28.908 CrossMaste...ectiveness                                     🎓 Generated 0 cross-learning recommendations for alekhine vs player
20:07:28.908 AdaptiveStrategy                                            🎯 Optimal strategy for alekhine vs player: 'supportive' (confidence: 0.20)
20:07:28.909 CompetitiveModeActivity                                     🧠 Phase 3: Using adaptive strategy 'supportive' (confidence: 0.20) for greeting - Individual learning: EXPLORATION: Testing new approach 'supportive' to learn effectiveness; 
20:07:28.909                                                             🗣️ Speaking master dialogue: Alexander Alekhine at your service. Prepare for a combinatorial masterclass!
20:07:28.909 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:07:28.909                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:07:28.909                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:07:28.909                                                             ✅ Usage context set to: competitive_mode
20:07:28.909 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
20:07:28.909 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
20:07:28.910                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
20:07:28.911 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
20:07:28.912                                                             🎭 Updated emotion indicator: 😎
20:07:28.912                                                             ✅ Competitive game started successfully!
20:07:28.914                                                             🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
20:07:28.914                                                             📊 Evaluation updated: 0.0
20:07:28.914                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
20:07:28.914                                                             🎯 First emotional evaluation: 0.0 (threshold: 1.5)
20:07:28.914                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360048s/25s)
20:07:28.915                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:07:28.915                                                             📜 Move history updated: 0 moves
20:07:28.917 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
20:07:28.920 VRI[Compet...y]@33fae3f                                     synced displayState. AttachInfo displayState=2
20:07:28.921                                                             setView = com.android.internal.policy.DecorView@6e42f38 IsHRR=false TM=true
20:07:28.921 Choreographer                                               Skipped 75 frames!  The application may be doing too much work on its main thread.
20:07:28.944 BufferQueueProducer                                         [](id:784700000003,api:0,p:0,c:30791) setDequeueTimeout:2077252342
20:07:28.945 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
20:07:28.945                                                             Access denied finding property "vendor.gpp.create_frc_extension"
20:07:28.945 VRI[Compet...y]@33fae3f                                     Relayout returned: old=(0,0,1440,3088) new=(0,0,1440,3088) relayoutAsync=false req=(1440,3088)0 dur=5 res=0x3 s={true 0xb40000718bacc800} ch=true seqId=0
20:07:28.946                                                             performConfigurationChange setNightDimText nightDimLevel=0
20:07:28.946                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb40000718bacc800} hwInitialized=true
20:07:28.952 ScrollView                                                   onsize change changed 
20:07:28.952 VRI[Compet...y]@33fae3f                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
20:07:28.952                                                             Setup new sync=wmsSync-VRI[CompetitiveModeActivity]@33fae3f#6
20:07:28.952                                                             Creating new active sync group VRI[CompetitiveModeActivity]@33fae3f#7
20:07:28.953                                                             registerCallbacksForSync syncBuffer=false
20:07:28.979                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
20:07:28.980                                                             mWNT: t=0xb40000718bd6bf00 mBlastBufferQueue=0xb40000718bd54280 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
20:07:28.980                                                             Setting up sync and frameCommitCallback
20:07:28.995 BLASTBufferQueue                                            [VRI[CompetitiveModeActivity]@33fae3f#3](f:0,a:0,s:0) onFrameAvailable the first frame is available
20:07:28.996 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 36957, bufferData(ID: 132246338011151, frameNumber: 1)
20:07:28.996 VRI[Compet...y]@33fae3f                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
20:07:28.996 HWUI                                                        CFMS:: SetUp Pid : 30791    Tid : 30816
20:07:28.996 VRI[Compet...y]@33fae3f                                     reportDrawFinished seqId=0
20:07:28.997 CompetitiveModeActivity                                     🎤 Voice service connected to competitive mode
20:07:28.997 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
20:07:29.005                                                             Davey! duration=714ms; Flags=1, FrameTimelineVsyncId=72125032, IntendedVsync=420944894816615, Vsync=420945520967240, InputEventId=0, HandleInputStart=420945528224056, AnimationStart=420945528225515, PerformTraversalsStart=420945528277025, DrawStart=420945559755931, FrameDeadline=420944903149948, FrameInterval=420945528041713, FrameStartTime=8348675, SyncQueued=420945586176869, SyncStart=420945586366087, IssueDrawCommandsStart=420945586515098, SwapBuffers=420945602324421, FrameCompleted=420945609874004, DequeueBufferDuration=8490, QueueBufferDuration=224636, GpuCompleted=420945609874004, SwapBuffersCompleted=420945602843327, DisplayPresentTime=0, CommandSubmissionCompleted=420945602324421, 
20:07:29.028 VRI[Compet...y]@33fae3f                                     mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb40000718bacc800}
20:07:29.029 InputMethodManagerUtils                                     startInputInner - Id : 0
20:07:29.029 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
20:07:29.509 GameViewModel                                               ✅ Evaluation received: 0.35
20:07:29.509 WindowManager           system_server                       win=Window{9bb58f4 u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:204 com.android.server.wm.ActivityClientController.activityStopped:95 android.app.IActivityClientController$Stub.onTransact:722 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1541 android.os.Binder.execTransact:1480 
20:07:29.609 CompetitiveModeActivity com.example.chesspedagogue          📊 Evaluation updated: 0.35
20:07:29.609                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.35, emotionalManager: INITIALIZED
20:07:29.609                                                             🎯 First emotional evaluation: 0.35 (threshold: 1.5)
20:07:29.609                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360049s/25s)
20:07:29.609                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:07:29.965 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:07:29.967 AIStyleAdvisor                                              ⚡ Cache hit for alekhine - instant advice
20:07:35.055 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
20:07:41.365 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:07:41.369                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:07:41.481                                                             ViewPostIme pointer 1
20:07:41.484 CompetitiveModeActivity                                     🔥 Using MAX difficulty - ELO: 2690, Skill: 20
20:07:41.490 Toast                                                       show: caller = com.example.chesspedagogue.CompetitiveModeActivity.toggleDifficulty:2151 
20:07:41.491                                                             show: isDexDualMode = false
20:07:41.491                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
20:07:44.483 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:07:48.883                                                             ViewPostIme pointer 0
20:07:48.884 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=3
20:07:48.884                                                             🎯 SQUARE TAPPED: row=6, col=3
20:07:48.884                                                             📝 Player color: white
20:07:48.884                                                             🔍 Selected row/col: -1/-1
20:07:49.240                                                             🎯 Selected piece: P at 6, 3
20:07:49.240 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:07:49.241                                                             ViewPostIme pointer 1
20:07:49.566                                                             ViewPostIme pointer 0
20:07:49.566 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
20:07:49.566                                                             🎯 SQUARE TAPPED: row=4, col=3
20:07:49.566                                                             📝 Player color: white
20:07:49.566                                                             🔍 Selected row/col: 6/3
20:07:49.567                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
20:07:49.567                                                             🎯 ATTEMPTING MOVE: d2d4
20:07:49.567                                                             📝 Player color: white
20:07:49.567                                                             🔄 Is player's turn: true
20:07:49.567                                                             🔄 Is white's turn: true
20:07:49.567                                                             ✅ Turn validation passed, making move: d2d4
20:07:49.568 GameViewModel                                               🎯 makePlayerMove called with: d2d4
20:07:49.618                                                             🔍 Validating move: d2d4 (attempt 1)
20:07:49.720                                                             📋 Current position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
20:07:49.772                                                             ⚖️ Move d2d4 legality check: LEGAL
20:07:49.772                                                             ✅ Executing validated move: d2d4
20:07:49.975                                                             📍 New position after move: rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1
20:07:49.977 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:07:49.978 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1
20:07:49.982                                                             📜 Move history updated: 1 moves
20:07:49.982 GameHistoryManager                                          Move added: d2d4
20:07:50.075 GameViewModel                                               🔍 Requesting position evaluation...
20:07:50.075                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:07:50.075                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:07:50.075                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:07:50.076                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:07:50.076                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:07:50.076                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:07:50.076                                                             🔧 gameRepository class: GameRepository
20:07:50.076                                                             🔧 Current thread: main
20:07:50.076 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:07:50.682 GameViewModel                                               ✅ Evaluation received: -0.25
20:07:50.782 CompetitiveModeActivity                                     📊 Evaluation updated: -0.25
20:07:50.782                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.25, emotionalManager: INITIALIZED
20:07:50.782                                                             🎯 First emotional evaluation: -0.25 (threshold: 1.5)
20:07:50.782                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360070s/25s)
20:07:50.782                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:07:51.498 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:07:51.500 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #3)
20:07:51.500                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:51.500                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq...
20:07:51.500                                                             🎲 Candidate moves for AI analysis: [g8f6]
20:07:51.500 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:51.500                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:51.500                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:07:51.659                                                             📋 Response ID: resp_6854600772d4819289243d39f54887ef01c02ec92f548f45
20:07:52.979 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:07:55.398 ResponsesAPI                                                🏁 Response completed
20:07:55.399 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["d7d5", "g8f6"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "As Black, I ...
20:07:55.400                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
20:07:55.400                                                             🎭 AI preferred moves: [g8f6]
20:07:55.400                                                             💭 AI reasoning: As Black, I prefer to counter White's initial 1.d4 with a central thrust like 1...d5. This aligns with my aggressive and dynamic style, aiming to challenge White's center immediately. The move 1...Nf6 also suits my approach, preparing for flexible development while maintaining pressure on the center.
20:07:55.421 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8f6
20:07:55.781 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 1 2
20:07:55.785                                                             📜 Move history updated: 2 moves
20:07:55.785 GameHistoryManager                                          Move added: g8f6
20:07:55.785 GameViewModel                                               🔍 Requesting position evaluation...
20:07:55.786                                                             🎭 Updating personality context for move: g8f6
20:07:55.786                                                             ✨ Personality context updated for move g8f6 - This is revolutionary!
20:07:55.787                                                             🔍 Checking game end conditions...
20:07:55.889                                                             ✅ Game continues - no end condition detected
20:07:56.392                                                             ✅ Evaluation received: 0.22
20:07:56.495 CompetitiveModeActivity                                     📊 Evaluation updated: 0.22
20:07:56.495                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.22, emotionalManager: INITIALIZED
20:07:56.495                                                             🎯 First emotional evaluation: 0.22 (threshold: 1.5)
20:07:56.495                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360076s/25s)
20:07:56.495                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:07:56.958 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:07:56.959 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=2
20:07:56.959                                                             🎯 SQUARE TAPPED: row=6, col=2
20:07:56.959                                                             📝 Player color: white
20:07:56.959                                                             🔍 Selected row/col: -1/-1
20:07:57.314                                                             🎯 Selected piece: P at 6, 2
20:07:57.314 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:07:57.315                                                             ViewPostIme pointer 1
20:07:57.315 Choreographer                                               Skipped 43 frames!  The application may be doing too much work on its main thread.
20:07:57.650 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:07:57.651 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
20:07:57.651                                                             🎯 SQUARE TAPPED: row=4, col=2
20:07:57.651                                                             📝 Player color: white
20:07:57.651                                                             🔍 Selected row/col: 6/2
20:07:57.651                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
20:07:57.651                                                             🎯 ATTEMPTING MOVE: c2c4
20:07:57.651                                                             📝 Player color: white
20:07:57.651                                                             🔄 Is player's turn: true
20:07:57.651                                                             🔄 Is white's turn: true
20:07:57.651                                                             ✅ Turn validation passed, making move: c2c4
20:07:57.651 GameViewModel                                               🎯 makePlayerMove called with: c2c4
20:07:57.702                                                             🔍 Validating move: c2c4 (attempt 1)
20:07:57.803                                                             📋 Current position: rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 1 2
20:07:57.855                                                             ⚖️ Move c2c4 legality check: LEGAL
20:07:57.855                                                             ✅ Executing validated move: c2c4
20:07:58.058                                                             📍 New position after move: rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2
20:07:58.059 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:07:58.060 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2
20:07:58.063                                                             📜 Move history updated: 3 moves
20:07:58.063 GameHistoryManager                                          Move added: c2c4
20:07:58.158 GameViewModel                                               🔍 Requesting position evaluation...
20:07:58.158                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:07:58.158                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:07:58.158                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:07:58.159                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:07:58.159                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:07:58.159                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:07:58.159                                                             🔧 gameRepository class: GameRepository
20:07:58.159                                                             🔧 Current thread: main
20:07:58.159 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:07:58.868 GameViewModel                                               ✅ Evaluation received: -0.21
20:07:58.968 CompetitiveModeActivity                                     📊 Evaluation updated: -0.21
20:07:58.968                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.21, emotionalManager: INITIALIZED
20:07:58.968                                                             🎯 First emotional evaluation: -0.21 (threshold: 1.5)
20:07:58.968                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360078s/25s)
20:07:58.968                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:07:59.637 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:07:59.638 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #4)
20:07:59.639                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:59.639                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQ...
20:07:59.639                                                             🎲 Candidate moves for AI analysis: [d7d6]
20:07:59.639 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:59.639                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:07:59.639                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:07:59.867                                                             📋 Response ID: resp_6854600f9600819cb83eedd9f46e784d045497ffb3405a23
20:08:01.061 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:08:01.105                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:04.668 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7d6
20:08:05.025 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp1pppp/3p1n2/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3
20:08:05.027                                                             📜 Move history updated: 4 moves
20:08:05.027 GameHistoryManager                                          Move added: d7d6
20:08:05.027 GameViewModel                                               🔍 Requesting position evaluation...
20:08:05.027                                                             🎭 Updating personality context for move: d7d6
20:08:05.027                                                             ✨ Personality context updated for move d7d6 - This is revolutionary!
20:08:05.027                                                             🔍 Checking game end conditions...
20:08:05.128                                                             ✅ Game continues - no end condition detected
20:08:05.172 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:05.389                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:05.533 GameViewModel                                               ✅ Evaluation received: 0.54
20:08:05.637 CompetitiveModeActivity                                     📊 Evaluation updated: 0.54
20:08:05.637                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.54, emotionalManager: INITIALIZED
20:08:05.637                                                             🎯 First emotional evaluation: 0.54 (threshold: 1.5)
20:08:05.637                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360085s/25s)
20:08:05.637                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:08:05.701 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:05.751                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:05.758                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:05.854                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:05.854                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:07.529 ResponsesAPI                                                🏁 Response completed
20:08:07.530 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["d7d6"],
                                                                           "style_weight": 0.8,
                                                                           "reasoning": "As Black, I prefer t...
20:08:07.530                                                             ✅ Parsed AI advice: 1 moves, weight=0.8
20:08:07.530                                                             🎭 AI preferred moves: [d7d6]
20:08:07.530                                                             💭 AI reasoning: As Black, I prefer to develop my pieces harmoniously while maintaining flexibility. The move d7d6 prepares to develop the bishop and supports a solid pawn structure. This aligns with my strategic approach of controlling the center indirectly and maintaining piece coordination.
20:08:09.937 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:08:09.938 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=1
20:08:09.938                                                             🎯 SQUARE TAPPED: row=7, col=1
20:08:09.938                                                             📝 Player color: white
20:08:09.938                                                             🔍 Selected row/col: -1/-1
20:08:10.297                                                             🎯 Selected piece: N at 7, 1
20:08:10.297 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:08:10.299                                                             ViewPostIme pointer 1
20:08:10.613                                                             ViewPostIme pointer 0
20:08:10.613 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
20:08:10.613                                                             🎯 SQUARE TAPPED: row=5, col=2
20:08:10.613                                                             📝 Player color: white
20:08:10.613                                                             🔍 Selected row/col: 7/1
20:08:10.614                                                             🎯 ATTEMPTING MOVE: b1c3
20:08:10.614                                                             📝 Player color: white
20:08:10.614                                                             🔄 Is player's turn: true
20:08:10.614                                                             🔄 Is white's turn: true
20:08:10.614                                                             ✅ Turn validation passed, making move: b1c3
20:08:10.614 GameViewModel                                               🎯 makePlayerMove called with: b1c3
20:08:10.665                                                             🔍 Validating move: b1c3 (attempt 1)
20:08:10.767                                                             📋 Current position: rnbqkb1r/ppp1pppp/3p1n2/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3
20:08:10.818                                                             ⚖️ Move b1c3 legality check: LEGAL
20:08:10.818                                                             ✅ Executing validated move: b1c3
20:08:11.021                                                             📍 New position after move: rnbqkb1r/ppp1pppp/3p1n2/8/2PP4/2N5/PP2PPPP/R1BQKBNR b KQkq - 1 3
20:08:11.022 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:08:11.024 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp1pppp/3p1n2/8/2PP4/2N5/PP2PPPP/R1BQKBNR b KQkq - 1 3
20:08:11.027                                                             📜 Move history updated: 5 moves
20:08:11.027 GameHistoryManager                                          Move added: b1c3
20:08:11.121 GameViewModel                                               🔍 Requesting position evaluation...
20:08:11.121                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:08:11.122                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:08:11.122                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:08:11.122                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:08:11.122                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:08:11.122                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:08:11.122                                                             🔧 gameRepository class: GameRepository
20:08:11.122                                                             🔧 Current thread: main
20:08:11.122 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:08:11.828 GameViewModel                                               ✅ Evaluation received: -0.48
20:08:11.937 CompetitiveModeActivity                                     📊 Evaluation updated: -0.48
20:08:11.937                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.48, emotionalManager: INITIALIZED
20:08:11.937                                                             🎯 First emotional evaluation: -0.48 (threshold: 1.5)
20:08:11.937                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360091s/25s)
20:08:11.937                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:08:12.833 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:08:12.834 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #5)
20:08:12.834                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:12.834                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkb1r/ppp1pppp/3p1n2/8/2PP4/2N5/PP2PPPP/R1BQKBNR ...
20:08:12.834                                                             🎲 Candidate moves for AI analysis: [b8d7]
20:08:12.835 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:12.835                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:12.835                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:08:12.986                                                             📋 Response ID: resp_6854601cc79c8192bfe140d797487d45097d4b4275da5fff
20:08:14.025 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:08:15.810 ResponsesAPI                                                🏁 Response completed
20:08:15.811 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["b8d7", "e7e5"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I enjoy dyna...
20:08:15.811                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
20:08:15.811                                                             🎭 AI preferred moves: [b8d7]
20:08:15.811                                                             💭 AI reasoning: I enjoy dynamic and aggressive play. The move b8d7 develops a piece and maintains a solid structure, preparing for future central control. Alternatively, e7e5 challenges White's center immediately, fitting my aggressive style.
20:08:15.858 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8d7
20:08:16.219 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pppnpppp/3p1n2/8/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq - 2 4
20:08:16.223                                                             📜 Move history updated: 6 moves
20:08:16.223 GameHistoryManager                                          Move added: b8d7
20:08:16.223 GameViewModel                                               🔍 Requesting position evaluation...
20:08:16.224                                                             🎭 Updating personality context for move: b8d7
20:08:16.224                                                             ✨ Personality context updated for move b8d7 - This is revolutionary!
20:08:16.224                                                             🔍 Checking game end conditions...
20:08:16.326                                                             ✅ Game continues - no end condition detected
20:08:16.495 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:16.779 GameViewModel                                               ✅ Evaluation received: 0.84
20:08:16.882 CompetitiveModeActivity                                     📊 Evaluation updated: 0.84
20:08:16.883                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.84, emotionalManager: INITIALIZED
20:08:16.883                                                             🎯 First emotional evaluation: 0.84 (threshold: 1.5)
20:08:16.883                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360096s/25s)
20:08:16.883                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:08:16.939 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:17.038                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:17.038                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:21.362                                                             ViewPostIme pointer 0
20:08:21.362 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=6
20:08:21.362                                                             🎯 SQUARE TAPPED: row=7, col=6
20:08:21.362                                                             📝 Player color: white
20:08:21.362                                                             🔍 Selected row/col: -1/-1
20:08:21.719                                                             🎯 Selected piece: N at 7, 6
20:08:21.719 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:08:21.720                                                             ViewPostIme pointer 1
20:08:22.087                                                             ViewPostIme pointer 0
20:08:22.088 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
20:08:22.088                                                             🎯 SQUARE TAPPED: row=5, col=5
20:08:22.088                                                             📝 Player color: white
20:08:22.088                                                             🔍 Selected row/col: 7/6
20:08:22.088                                                             🎯 ATTEMPTING MOVE: g1f3
20:08:22.088                                                             📝 Player color: white
20:08:22.088                                                             🔄 Is player's turn: true
20:08:22.088                                                             🔄 Is white's turn: true
20:08:22.088                                                             ✅ Turn validation passed, making move: g1f3
20:08:22.088 GameViewModel                                               🎯 makePlayerMove called with: g1f3
20:08:22.140                                                             🔍 Validating move: g1f3 (attempt 1)
20:08:22.242                                                             📋 Current position: r1bqkb1r/pppnpppp/3p1n2/8/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq - 2 4
20:08:22.293                                                             ⚖️ Move g1f3 legality check: LEGAL
20:08:22.293                                                             ✅ Executing validated move: g1f3
20:08:22.497                                                             📍 New position after move: r1bqkb1r/pppnpppp/3p1n2/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 3 4
20:08:22.498 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:08:22.500 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pppnpppp/3p1n2/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 3 4
20:08:22.503                                                             📜 Move history updated: 7 moves
20:08:22.504 GameHistoryManager                                          Move added: g1f3
20:08:22.597 GameViewModel                                               🔍 Requesting position evaluation...
20:08:22.597                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:08:22.597                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:08:22.597                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:08:22.597                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:08:22.598                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:08:22.598                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:08:22.598                                                             🔧 gameRepository class: GameRepository
20:08:22.598                                                             🔧 Current thread: main
20:08:22.598 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:08:23.303 GameViewModel                                               ✅ Evaluation received: -0.86
20:08:23.411 CompetitiveModeActivity                                     📊 Evaluation updated: -0.86
20:08:23.411                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.86, emotionalManager: INITIALIZED
20:08:23.411                                                             🎯 First emotional evaluation: -0.86 (threshold: 1.5)
20:08:23.412                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360103s/25s)
20:08:23.412                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:08:24.103 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:08:24.104 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #6)
20:08:24.104                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:24.104                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): r1bqkb1r/pppnpppp/3p1n2/8/2PP4/2N2N2/PP2PPPP/R1BQKB1...
20:08:24.104                                                             🎲 Candidate moves for AI analysis: [g7g6]
20:08:24.104 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:24.104                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:24.104                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:08:24.293                                                             📋 Response ID: resp_6854602811588191928436cbe122452400973a296a501bd8
20:08:25.502 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:08:28.060 ResponsesAPI                                                🏁 Response completed
20:08:28.061 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["g7g6", "e7e5"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "As Black, I ...
20:08:28.062                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
20:08:28.062                                                             🎭 AI preferred moves: [g7g6]
20:08:28.062                                                             💭 AI reasoning: As Black, I seek to solidify the center while preparing for dynamic play. The move g6 aims to fianchetto the bishop, supporting central control and targeting White's center. Additionally, e5 challenges White's d4 pawn directly, opening lines for the pieces.
20:08:28.105 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g7g6
20:08:28.463 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pppnpp1p/3p1np1/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 0 5
20:08:28.467                                                             📜 Move history updated: 8 moves
20:08:28.467 GameHistoryManager                                          Move added: g7g6
20:08:28.467 GameViewModel                                               🔍 Requesting position evaluation...
20:08:28.468                                                             🎭 Updating personality context for move: g7g6
20:08:28.468                                                             ✨ Personality context updated for move g7g6 - This is revolutionary!
20:08:28.468                                                             🔍 Checking game end conditions...
20:08:28.570                                                             ✅ Game continues - no end condition detected
20:08:28.692 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:28.693                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:28.729                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:29.099 GameViewModel                                               ✅ Evaluation received: 0.67
20:08:29.202 CompetitiveModeActivity                                     📊 Evaluation updated: 0.67
20:08:29.202                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.67, emotionalManager: INITIALIZED
20:08:29.202                                                             🎯 First emotional evaluation: 0.67 (threshold: 1.5)
20:08:29.202                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360109s/25s)
20:08:29.202                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:08:29.262 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
20:08:40.913                                                             ViewPostIme pointer 0
20:08:40.914 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
20:08:40.914                                                             🎯 SQUARE TAPPED: row=6, col=4
20:08:40.914                                                             📝 Player color: white
20:08:40.914                                                             🔍 Selected row/col: -1/-1
20:08:41.272                                                             🎯 Selected piece: P at 6, 4
20:08:41.272 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:08:41.274                                                             ViewPostIme pointer 1
20:08:41.426                                                             ViewPostIme pointer 0
20:08:41.427 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=4
20:08:41.427                                                             🎯 SQUARE TAPPED: row=5, col=4
20:08:41.427                                                             📝 Player color: white
20:08:41.427                                                             🔍 Selected row/col: 6/4
20:08:41.427                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=5, reaches=false
20:08:41.427                                                             🎯 ATTEMPTING MOVE: e2e3
20:08:41.427                                                             📝 Player color: white
20:08:41.427                                                             🔄 Is player's turn: true
20:08:41.427                                                             🔄 Is white's turn: true
20:08:41.427                                                             ✅ Turn validation passed, making move: e2e3
20:08:41.427 GameViewModel                                               🎯 makePlayerMove called with: e2e3
20:08:41.478                                                             🔍 Validating move: e2e3 (attempt 1)
20:08:41.580                                                             📋 Current position: r1bqkb1r/pppnpp1p/3p1np1/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 0 5
20:08:41.631                                                             ⚖️ Move e2e3 legality check: LEGAL
20:08:41.631                                                             ✅ Executing validated move: e2e3
20:08:41.834                                                             📍 New position after move: r1bqkb1r/pppnpp1p/3p1np1/8/2PP4/2N1PN2/PP3PPP/R1BQKB1R b KQkq - 0 5
20:08:41.835 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:08:41.836 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pppnpp1p/3p1np1/8/2PP4/2N1PN2/PP3PPP/R1BQKB1R b KQkq - 0 5
20:08:41.840                                                             📜 Move history updated: 9 moves
20:08:41.840 GameHistoryManager                                          Move added: e2e3
20:08:41.842 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
20:08:41.937 GameViewModel                                               🔍 Requesting position evaluation...
20:08:41.937                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:08:41.937                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:08:41.937                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:08:41.937                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:08:41.937                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:08:41.937                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:08:41.937                                                             🔧 gameRepository class: GameRepository
20:08:41.938                                                             🔧 Current thread: main
20:08:41.938 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:08:42.644 GameViewModel                                               ✅ Evaluation received: -0.46
20:08:42.753 CompetitiveModeActivity                                     📊 Evaluation updated: -0.46
20:08:42.753                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.46, emotionalManager: INITIALIZED
20:08:42.753                                                             🎯 First emotional evaluation: -0.46 (threshold: 1.5)
20:08:42.753                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360122s/25s)
20:08:42.753                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:08:43.497 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:08:43.498 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #7)
20:08:43.498                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:43.498                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): r1bqkb1r/pppnpp1p/3p1np1/8/2PP4/2N1PN2/PP3PPP/R1BQKB...
20:08:43.498                                                             🎲 Candidate moves for AI analysis: [f8g7]
20:08:43.498 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:43.498                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:43.499                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:08:43.684                                                             📋 Response ID: resp_6854603b785c819282a40b3f54f5634b00ed3f42cab1e17e
20:08:44.837 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:08:48.533 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8g7
20:08:48.778 ResponsesAPI                                                🏁 Response completed
20:08:48.778 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["f8g7", "c8g4"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "As Black, I ...
20:08:48.778                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
20:08:48.778                                                             🎭 AI preferred moves: [f8g7]
20:08:48.778                                                             💭 AI reasoning: As Black, I prefer to focus on active piece development and controlling key central squares. Moving the bishop to g7 allows for a solid fianchetto structure, enhancing control over the central dark squares. Alternatively, playing Bg4 can pin the knight and increase pressure on the d4 square, aligning with a more aggressive, tactical style.
20:08:48.887 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pppnppbp/3p1np1/8/2PP4/2N1PN2/PP3PPP/R1BQKB1R w KQkq - 1 6
20:08:48.891                                                             📜 Move history updated: 10 moves
20:08:48.892 GameHistoryManager                                          Move added: f8g7
20:08:48.892 GameViewModel                                               🔍 Requesting position evaluation...
20:08:48.892                                                             🎭 Updating personality context for move: f8g7
20:08:48.892                                                             ✨ Personality context updated for move f8g7 - This is revolutionary!
20:08:48.892                                                             🔍 Checking game end conditions...
20:08:48.993                                                             ✅ Game continues - no end condition detected
20:08:49.402                                                             ✅ Evaluation received: 0.51
20:08:49.504 CompetitiveModeActivity                                     📊 Evaluation updated: 0.51
20:08:49.504                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.51, emotionalManager: INITIALIZED
20:08:49.504                                                             🎯 First emotional evaluation: 0.51 (threshold: 1.5)
20:08:49.504                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360129s/25s)
20:08:49.504                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:08:53.472 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:08:53.473 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=5
20:08:53.473                                                             🎯 SQUARE TAPPED: row=7, col=5
20:08:53.473                                                             📝 Player color: white
20:08:53.473                                                             🔍 Selected row/col: -1/-1
20:08:53.831                                                             🎯 Selected piece: B at 7, 5
20:08:53.832 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:08:53.832                                                             ViewPostIme pointer 1
20:08:55.158                                                             ViewPostIme pointer 0
20:08:55.159 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=3
20:08:55.159                                                             🎯 SQUARE TAPPED: row=5, col=3
20:08:55.159                                                             📝 Player color: white
20:08:55.159                                                             🔍 Selected row/col: 7/5
20:08:55.159                                                             🎯 ATTEMPTING MOVE: f1d3
20:08:55.159                                                             📝 Player color: white
20:08:55.159                                                             🔄 Is player's turn: true
20:08:55.159                                                             🔄 Is white's turn: true
20:08:55.159                                                             ✅ Turn validation passed, making move: f1d3
20:08:55.159 GameViewModel                                               🎯 makePlayerMove called with: f1d3
20:08:55.210                                                             🔍 Validating move: f1d3 (attempt 1)
20:08:55.311                                                             📋 Current position: r1bqk2r/pppnppbp/3p1np1/8/2PP4/2N1PN2/PP3PPP/R1BQKB1R w KQkq - 1 6
20:08:55.363                                                             ⚖️ Move f1d3 legality check: LEGAL
20:08:55.363                                                             ✅ Executing validated move: f1d3
20:08:55.565                                                             📍 New position after move: r1bqk2r/pppnppbp/3p1np1/8/2PP4/2NBPN2/PP3PPP/R1BQK2R b KQkq - 2 6
20:08:55.566 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:08:55.567 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pppnppbp/3p1np1/8/2PP4/2NBPN2/PP3PPP/R1BQK2R b KQkq - 2 6
20:08:55.571                                                             📜 Move history updated: 11 moves
20:08:55.571 GameHistoryManager                                          Move added: f1d3
20:08:55.666 GameViewModel                                               🔍 Requesting position evaluation...
20:08:55.667                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:08:55.667                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:08:55.667                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:08:55.667                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:08:55.667                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:08:55.667                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:08:55.667                                                             🔧 gameRepository class: GameRepository
20:08:55.667                                                             🔧 Current thread: main
20:08:55.667 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:08:56.374 GameViewModel                                               ✅ Evaluation received: -0.21
20:08:56.483 CompetitiveModeActivity                                     📊 Evaluation updated: -0.21
20:08:56.483                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.21, emotionalManager: INITIALIZED
20:08:56.483                                                             🎯 First emotional evaluation: -0.21 (threshold: 1.5)
20:08:56.483                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360136s/25s)
20:08:56.483                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:08:57.384 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:08:57.385 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #8)
20:08:57.385                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:57.385                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): r1bqk2r/pppnppbp/3p1np1/8/2PP4/2NBPN2/PP3PPP/R1BQK2R...
20:08:57.385                                                             🎲 Candidate moves for AI analysis: [e7e5]
20:08:57.385 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:57.385                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:08:57.385                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:08:57.601                                                             📋 Response ID: resp_6854604955a0819d90dd71a743c07c670e68a26c1f78e40e
20:08:57.653 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
20:08:58.570 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:09:00.073                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:09:01.942 ResponsesAPI                                                🏁 Response completed
20:09:01.943 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["e7e5", "c7c5"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "As Black, I ...
20:09:01.944                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
20:09:01.944                                                             🎭 AI preferred moves: [e7e5]
20:09:01.944                                                             💭 AI reasoning: As Black, I aim to challenge the center and create dynamic play. The move e7e5 directly contests White's central pawns and opens lines for my pieces, aligning with my aggressive style. Alternatively, c7c5 supports the fight for the center and can lead to potential pawn breaks.
20:09:01.986 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7e5
20:09:02.344 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pppn1pbp/3p1np1/4p3/2PP4/2NBPN2/PP3PPP/R1BQK2R w KQkq - 0 7
20:09:02.350                                                             📜 Move history updated: 12 moves
20:09:02.350 GameHistoryManager                                          Move added: e7e5
20:09:02.350 GameViewModel                                               🔍 Requesting position evaluation...
20:09:02.350                                                             🎭 Updating personality context for move: e7e5
20:09:02.351                                                             ✨ Personality context updated for move e7e5 - This is revolutionary!
20:09:02.351                                                             🔍 Checking game end conditions...
20:09:02.453                                                             ✅ Game continues - no end condition detected
20:09:02.454 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
20:09:02.552                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:09:02.910 GameViewModel                                               ✅ Evaluation received: 0.29
20:09:03.014 CompetitiveModeActivity                                     📊 Evaluation updated: 0.29
20:09:03.014                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.29, emotionalManager: INITIALIZED
20:09:03.014                                                             🎯 First emotional evaluation: 0.29 (threshold: 1.5)
20:09:03.014                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360143s/25s)
20:09:03.014                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:09:17.156 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:09:17.158 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
20:09:17.158                                                             🎯 SQUARE TAPPED: row=4, col=3
20:09:17.158                                                             📝 Player color: white
20:09:17.158                                                             🔍 Selected row/col: -1/-1
20:09:17.515                                                             🎯 Selected piece: P at 4, 3
20:09:17.515 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:09:17.516                                                             ViewPostIme pointer 1
20:09:17.748                                                             ViewPostIme pointer 0
20:09:17.748 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
20:09:17.749                                                             🎯 SQUARE TAPPED: row=3, col=4
20:09:17.749                                                             📝 Player color: white
20:09:17.749                                                             🔍 Selected row/col: 4/3
20:09:17.749                                                             ♟️ Promotion check: piece=P, fromRow=4, toRow=3, reaches=false
20:09:17.749                                                             🎯 ATTEMPTING MOVE: d4e5
20:09:17.749                                                             📝 Player color: white
20:09:17.749                                                             🔄 Is player's turn: true
20:09:17.749                                                             🔄 Is white's turn: true
20:09:17.749                                                             ✅ Turn validation passed, making move: d4e5
20:09:17.749 GameViewModel                                               🎯 makePlayerMove called with: d4e5
20:09:17.800                                                             🔍 Validating move: d4e5 (attempt 1)
20:09:17.901                                                             📋 Current position: r1bqk2r/pppn1pbp/3p1np1/4p3/2PP4/2NBPN2/PP3PPP/R1BQK2R w KQkq - 0 7
20:09:17.952                                                             ⚖️ Move d4e5 legality check: LEGAL
20:09:17.952                                                             ✅ Executing validated move: d4e5
20:09:18.155                                                             📍 New position after move: r1bqk2r/pppn1pbp/3p1np1/4P3/2P5/2NBPN2/PP3PPP/R1BQK2R b KQkq - 0 7
20:09:18.156 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:09:18.157 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pppn1pbp/3p1np1/4P3/2P5/2NBPN2/PP3PPP/R1BQK2R b KQkq - 0 7
20:09:18.161                                                             📜 Move history updated: 13 moves
20:09:18.161 GameHistoryManager                                          Move added: d4e5
20:09:18.256 GameViewModel                                               🔍 Requesting position evaluation...
20:09:18.256                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:09:18.256                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:09:18.256                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:09:18.257                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:09:18.257                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:09:18.257                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:09:18.257                                                             🔧 gameRepository class: GameRepository
20:09:18.257                                                             🔧 Current thread: main
20:09:18.257 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:09:18.810 GameViewModel                                               ✅ Evaluation received: 0.23
20:09:18.913 CompetitiveModeActivity                                     📊 Evaluation updated: 0.23
20:09:18.913                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.23, emotionalManager: INITIALIZED
20:09:18.913                                                             🎯 First emotional evaluation: 0.23 (threshold: 1.5)
20:09:18.913                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360158s/25s)
20:09:18.913                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:09:19.769 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:09:19.770 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #9)
20:09:19.770                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:09:19.770                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): r1bqk2r/pppn1pbp/3p1np1/4P3/2P5/2NBPN2/PP3PPP/R1BQK2...
20:09:19.770                                                             🎲 Candidate moves for AI analysis: [d6e5]
20:09:19.770 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:09:19.770                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:09:19.770                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:09:20.156                                                             📋 Response ID: resp_6854605fb93481a090387401ef8f50a4093b52aa2c80ddec
20:09:21.160 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:09:22.761 ResponsesAPI                                                🏁 Response completed
20:09:22.762 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["Nxe5"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "In this position, ca...
20:09:22.762                                                             ✅ Parsed AI advice: 0 moves, weight=0.7
20:09:22.762                                                             🎭 AI preferred moves: []
20:09:22.762                                                             💭 AI reasoning: In this position, capturing the pawn on e5 with Nxe5 opens lines and increases piece activity, which aligns with my aggressive style. It also puts pressure on the center and can lead to tactical opportunities.
20:09:22.810 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d6e5
20:09:23.168 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pppn1pbp/5np1/4p3/2P5/2NBPN2/PP3PPP/R1BQK2R w KQkq - 0 8
20:09:23.174                                                             📜 Move history updated: 14 moves
20:09:23.174 GameHistoryManager                                          Move added: d6e5
20:09:23.174 GameViewModel                                               🔍 Requesting position evaluation...
20:09:23.174                                                             🎭 Updating personality context for move: d6e5
20:09:23.175                                                             ✨ Personality context updated for move d6e5 - This is revolutionary!
20:09:23.175                                                             🔍 Checking game end conditions...
20:09:23.277                                                             ✅ Game continues - no end condition detected
20:09:23.881                                                             ✅ Evaluation received: -0.31
20:09:23.984 CompetitiveModeActivity                                     📊 Evaluation updated: -0.31
20:09:23.984                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.31, emotionalManager: INITIALIZED
20:09:23.984                                                             🎯 First emotional evaluation: -0.31 (threshold: 1.5)
20:09:23.984                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360163s/25s)
20:09:23.984                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:09:38.654 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:09:38.654 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
20:09:38.654                                                             🎯 SQUARE TAPPED: row=4, col=2
20:09:38.654                                                             📝 Player color: white
20:09:38.654                                                             🔍 Selected row/col: -1/-1
20:09:39.009                                                             🎯 Selected piece: P at 4, 2
20:09:39.009 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:09:39.009                                                             ViewPostIme pointer 1
20:09:39.219                                                             ViewPostIme pointer 0
20:09:39.219 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=4
20:09:39.220                                                             🎯 SQUARE TAPPED: row=4, col=4
20:09:39.220                                                             📝 Player color: white
20:09:39.220                                                             🔍 Selected row/col: 4/2
20:09:39.220                                                             ♟️ Promotion check: piece=P, fromRow=4, toRow=4, reaches=false
20:09:39.220                                                             🎯 ATTEMPTING MOVE: c4e4
20:09:39.220                                                             📝 Player color: white
20:09:39.220                                                             🔄 Is player's turn: true
20:09:39.220                                                             🔄 Is white's turn: true
20:09:39.220                                                             ✅ Turn validation passed, making move: c4e4
20:09:39.220 GameViewModel                                               🎯 makePlayerMove called with: c4e4
20:09:39.271                                                             🔍 Validating move: c4e4 (attempt 1)
20:09:39.373                                                             📋 Current position: r1bqk2r/pppn1pbp/5np1/4p3/2P5/2NBPN2/PP3PPP/R1BQK2R w KQkq - 0 8
20:09:39.424                                                             ⚖️ Move c4e4 legality check: LEGAL
20:09:39.424                                                             ✅ Executing validated move: c4e4
20:09:39.526                                                             ❌ Move execution failed in gameRepository.makeMove()
20:09:39.526                                                             💔 Handling move execution failure: c4e4
20:09:39.527 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:09:39.528 Choreographer                                               Skipped 36 frames!  The application may be doing too much work on its main thread.
20:09:41.932 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:09:41.933 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
20:09:41.933                                                             🎯 SQUARE TAPPED: row=5, col=2
20:09:41.933                                                             📝 Player color: white
20:09:41.933                                                             🔍 Selected row/col: -1/-1
20:09:42.287                                                             🎯 Selected piece: N at 5, 2
20:09:42.287 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:09:42.707                                                             ViewPostIme pointer 0
20:09:42.708 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=4
20:09:42.708                                                             🎯 SQUARE TAPPED: row=4, col=4
20:09:42.708                                                             📝 Player color: white
20:09:42.708                                                             🔍 Selected row/col: 5/2
20:09:42.708                                                             🎯 ATTEMPTING MOVE: c3e4
20:09:42.708                                                             📝 Player color: white
20:09:42.708                                                             🔄 Is player's turn: true
20:09:42.708                                                             🔄 Is white's turn: true
20:09:42.708                                                             ✅ Turn validation passed, making move: c3e4
20:09:42.708 GameViewModel                                               🎯 makePlayerMove called with: c3e4
20:09:42.759                                                             🔍 Validating move: c3e4 (attempt 1)
20:09:42.861                                                             📋 Current position: r1bqk2r/pppn1pbp/5np1/4p3/2P5/2NBPN2/PP3PPP/R1BQK2R w KQkq - 0 8
20:09:42.912                                                             ⚖️ Move c3e4 legality check: LEGAL
20:09:42.912                                                             ✅ Executing validated move: c3e4
20:09:43.115                                                             📍 New position after move: r1bqk2r/pppn1pbp/5np1/4p3/2P1N3/3BPN2/PP3PPP/R1BQK2R b KQkq - 1 8
20:09:43.116 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:09:43.118 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pppn1pbp/5np1/4p3/2P1N3/3BPN2/PP3PPP/R1BQK2R b KQkq - 1 8
20:09:43.122                                                             📜 Move history updated: 15 moves
20:09:43.123 GameHistoryManager                                          Move added: c3e4
20:09:43.223 GameViewModel                                               🔍 Requesting position evaluation...
20:09:43.223                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:09:43.223                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:09:43.223                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:09:43.223                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:09:43.223                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:09:43.223                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:09:43.223                                                             🔧 gameRepository class: GameRepository
20:09:43.223                                                             🔧 Current thread: main
20:09:43.223 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:09:43.930 GameViewModel                                               ✅ Evaluation received: 1.15
20:09:44.031 CompetitiveModeActivity                                     📊 Evaluation updated: 1.15
20:09:44.031                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.15, emotionalManager: INITIALIZED
20:09:44.031                                                             🎯 First emotional evaluation: 1.15 (threshold: 1.5)
20:09:44.031                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360184s/25s)
20:09:44.031                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:09:44.839 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:09:44.840 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #10)
20:09:44.840                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:09:44.840                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): r1bqk2r/pppn1pbp/5np1/4p3/2P1N3/3BPN2/PP3PPP/R1BQK2R...
20:09:44.840                                                             🎲 Candidate moves for AI analysis: [a7a5]
20:09:44.841 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:09:44.841                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:09:44.841                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:09:45.002                                                             📋 Response ID: resp_68546078c97c8192a1cbbad6989f0ede06662711229e1999
20:09:46.119 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:09:48.381 ResponsesAPI                                                🏁 Response completed
20:09:48.382 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["a5", "O-O"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "In this positio...
20:09:48.382                                                             ✅ Parsed AI advice: 0 moves, weight=0.7
20:09:48.382                                                             🎭 AI preferred moves: []
20:09:48.382                                                             💭 AI reasoning: In this position, I would aim to unbalance the position and create dynamic chances. By playing a5, I seek to expand on the queenside and potentially prepare for a future b5 advance. Castling kingside (O-O) is a solid move to complete development and bring the rook into play, maintaining flexibility for future operations.
20:09:48.426 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a7a5
20:09:48.784 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/1ppn1pbp/5np1/p3p3/2P1N3/3BPN2/PP3PPP/R1BQK2R w KQkq - 0 9
20:09:48.791                                                             📜 Move history updated: 16 moves
20:09:48.792 GameHistoryManager                                          Move added: a7a5
20:09:48.792 GameViewModel                                               🔍 Requesting position evaluation...
20:09:48.792                                                             🎭 Updating personality context for move: a7a5
20:09:48.792                                                             ✨ Personality context updated for move a7a5 - This is revolutionary!
20:09:48.793                                                             🔍 Checking game end conditions...
20:09:48.895                                                             ✅ Game continues - no end condition detected
20:09:49.499                                                             ✅ Evaluation received: -0.74
20:09:49.602 CompetitiveModeActivity                                     📊 Evaluation updated: -0.74
20:09:49.602                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.74, emotionalManager: INITIALIZED
20:09:49.602                                                             🎯 First emotional evaluation: -0.74 (threshold: 1.5)
20:09:49.603                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750360189s/25s)
20:09:49.603                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:10:00.066 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
20:10:19.763                                                             ViewPostIme pointer 0
20:10:19.765 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
20:10:19.765                                                             🎯 SQUARE TAPPED: row=5, col=5
20:10:19.765                                                             📝 Player color: white
20:10:19.765                                                             🔍 Selected row/col: -1/-1
20:10:20.124                                                             🎯 Selected piece: N at 5, 5
20:10:20.124 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:10:20.125                                                             ViewPostIme pointer 1
20:10:20.126                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:10:20.276                                                             ViewPostIme pointer 0
20:10:20.277 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
20:10:20.277                                                             🎯 SQUARE TAPPED: row=3, col=4
20:10:20.277                                                             📝 Player color: white
20:10:20.277                                                             🔍 Selected row/col: 5/5
20:10:20.278                                                             🎯 ATTEMPTING MOVE: f3e5
20:10:20.278                                                             📝 Player color: white
20:10:20.278                                                             🔄 Is player's turn: true
20:10:20.278                                                             🔄 Is white's turn: true
20:10:20.278                                                             ✅ Turn validation passed, making move: f3e5
20:10:20.278 GameViewModel                                               🎯 makePlayerMove called with: f3e5
20:10:20.329                                                             🔍 Validating move: f3e5 (attempt 1)
20:10:20.430                                                             📋 Current position: r1bqk2r/1ppn1pbp/5np1/p3p3/2P1N3/3BPN2/PP3PPP/R1BQK2R w KQkq - 0 9
20:10:20.482                                                             ⚖️ Move f3e5 legality check: LEGAL
20:10:20.482                                                             ✅ Executing validated move: f3e5
20:10:20.685                                                             📍 New position after move: r1bqk2r/1ppn1pbp/5np1/p3N3/2P1N3/3BP3/PP3PPP/R1BQK2R b KQkq - 0 9
20:10:20.686 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:10:20.688 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/1ppn1pbp/5np1/p3N3/2P1N3/3BP3/PP3PPP/R1BQK2R b KQkq - 0 9
20:10:20.694                                                             📜 Move history updated: 17 moves
20:10:20.694 GameHistoryManager                                          Move added: f3e5
20:10:20.694 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
20:10:20.790 GameViewModel                                               🔍 Requesting position evaluation...
20:10:20.790                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:10:20.790                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:10:20.790                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:10:20.791                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:10:20.791                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:10:20.791                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:10:20.791                                                             🔧 gameRepository class: GameRepository
20:10:20.791                                                             🔧 Current thread: main
20:10:20.791 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:10:21.143 GameViewModel                                               ✅ Evaluation received: 6.22
20:10:21.244 CompetitiveModeActivity                                     📊 Evaluation updated: 6.22
20:10:21.244                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 6.22, emotionalManager: INITIALIZED
20:10:21.244                                                             🎯 First emotional evaluation: 6.22 (threshold: 1.5)
20:10:21.244                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 1750360221s/25s)
20:10:21.244                                                             ✅ Emotional reaction triggered for evaluation: 6.22 (trigger: significant_disadvantage)
20:10:21.245 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
20:10:21.245                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
20:10:21.245                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
20:10:21.245                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
20:10:22.094 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:10:22.094 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #11)
20:10:22.095                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:10:22.095                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): r1bqk2r/1ppn1pbp/5np1/p3N3/2P1N3/3BP3/PP3PPP/R1BQK2R...
20:10:22.095                                                             🎲 Candidate moves for AI analysis: [d7e5]
20:10:22.095 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:10:22.095                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:10:22.095                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:10:22.283                                                             📋 Response ID: resp_6854609e11f0819fa4458dd0ff73f4fb060db856df376b9f
20:10:23.690 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:10:23.805 OpenAIService                                               ✅ Response generated: (After move 9, in a difficult position) The beast may be cornered, but it is most dangerous in its l...
20:10:23.805 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: (After move 9, in a difficult position) The beast may be cornered, but it is most dangerous in its lair. My defense will be a symphony of resourceful counterplay—watch for the hidden harmonies in my complications.
20:10:23.807                                                             🎭 Updated emotion indicator: 😤
20:10:23.807                                                             🗣️ Speaking master dialogue: (After move 9, in a difficult position) The beast may be cornered, but it is most dangerous in its lair. My defense will be a symphony of resourceful counterplay—watch for the hidden harmonies in my complications.
20:10:23.807 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
20:10:23.807                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
20:10:23.807                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
20:10:23.807                                                             ✅ Usage context set to: competitive_mode
20:10:23.807 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
20:10:23.807 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
20:10:23.807                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
20:10:23.808 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
20:10:24.948 ResponsesAPI                                                🏁 Response completed
20:10:24.949 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["d7e5"],
                                                                           "style_weight": 0.9,
                                                                           "reasoning": "As Alekhine, I thriv...
20:10:24.949                                                             ✅ Parsed AI advice: 1 moves, weight=0.9
20:10:24.950                                                             🎭 AI preferred moves: [d7e5]
20:10:24.950                                                             💭 AI reasoning: As Alekhine, I thrive on dynamic, tactical play. The move Nxe5 challenges White's knight on e5 and opens lines for potential tactics. This suits my aggressive style, encouraging sharp, complex positions.
20:10:24.988 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7e5
20:10:25.347 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/1pp2pbp/5np1/p3n3/2P1N3/3BP3/PP3PPP/R1BQK2R w KQkq - 0 10
20:10:25.353                                                             📜 Move history updated: 18 moves
20:10:25.353 GameHistoryManager                                          Move added: d7e5
20:10:25.353 GameViewModel                                               🔍 Requesting position evaluation...
20:10:25.354                                                             🎭 Updating personality context for move: d7e5
20:10:25.354                                                             ✨ Personality context updated for move d7e5 - This is revolutionary!
20:10:25.354                                                             🔍 Checking game end conditions...
20:10:25.455                                                             ✅ Game continues - no end condition detected
20:10:25.662                                                             ✅ Evaluation received: -5.72
20:10:25.764 CompetitiveModeActivity                                     📊 Evaluation updated: -5.72
20:10:25.764                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.72, emotionalManager: INITIALIZED
20:10:25.764                                                             🎯 Evaluation change: 11.94 (threshold: 0.8)
20:10:25.764                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 4s/25s)
20:10:25.764                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
20:10:36.912                                                             🗣️ Master dialogue speech completed
20:10:37.083 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 0
20:10:37.083 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
20:10:37.083                                                             🎯 SQUARE TAPPED: row=7, col=3
20:10:37.083                                                             📝 Player color: white
20:10:37.084                                                             🔍 Selected row/col: -1/-1
20:10:37.440                                                             🎯 Selected piece: Q at 7, 3
20:10:37.440 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@33fae3f
20:10:37.441                                                             ViewPostIme pointer 1
20:10:37.751                                                             ViewPostIme pointer 0
20:10:37.751 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=6
20:10:37.752                                                             🎯 SQUARE TAPPED: row=4, col=6
20:10:37.752                                                             📝 Player color: white
20:10:37.752                                                             🔍 Selected row/col: 7/3
20:10:37.752                                                             🎯 ATTEMPTING MOVE: d1g4
20:10:37.752                                                             📝 Player color: white
20:10:37.752                                                             🔄 Is player's turn: true
20:10:37.752                                                             🔄 Is white's turn: true
20:10:37.752                                                             ✅ Turn validation passed, making move: d1g4
20:10:37.752 GameViewModel                                               🎯 makePlayerMove called with: d1g4
20:10:37.803                                                             🔍 Validating move: d1g4 (attempt 1)
20:10:37.903                                                             📋 Current position: r1bqk2r/1pp2pbp/5np1/p3n3/2P1N3/3BP3/PP3PPP/R1BQK2R w KQkq - 0 10
20:10:37.954                                                             ⚖️ Move d1g4 legality check: LEGAL
20:10:37.954                                                             ✅ Executing validated move: d1g4
20:10:38.157                                                             📍 New position after move: r1bqk2r/1pp2pbp/5np1/p3n3/2P1N1Q1/3BP3/PP3PPP/R1B1K2R b KQkq - 1 10
20:10:38.158 VRI[Compet...y]@33fae3f                                     ViewPostIme pointer 1
20:10:38.160 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/1pp2pbp/5np1/p3n3/2P1N1Q1/3BP3/PP3PPP/R1B1K2R b KQkq - 1 10
20:10:38.165                                                             📜 Move history updated: 19 moves
20:10:38.165 GameHistoryManager                                          Move added: d1g4
20:10:38.260 GameViewModel                                               🔍 Requesting position evaluation...
20:10:38.261                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:10:38.261                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:10:38.261                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:10:38.261                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:10:38.261                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:10:38.261                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:10:38.261                                                             🔧 gameRepository class: GameRepository
20:10:38.261                                                             🔧 Current thread: main
20:10:38.261 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:10:38.514 GameViewModel                                               ✅ Evaluation received: 10.33
20:10:38.615 CompetitiveModeActivity                                     📊 Evaluation updated: 10.33
20:10:38.615                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 10.33, emotionalManager: INITIALIZED
20:10:38.615                                                             🎯 Evaluation change: 4.11 (threshold: 0.8)
20:10:38.615                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 17s/25s)
20:10:38.615                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
20:10:39.514 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:10:39.515 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #12)
20:10:39.515                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:10:39.515                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): r1bqk2r/1pp2pbp/5np1/p3n3/2P1N1Q1/3BP3/PP3PPP/R1B1K2...
20:10:39.515                                                             🎲 Candidate moves for AI analysis: [e5d3]
20:10:39.515 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:10:39.515                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for asst_wnshRkbnaca2vkRxYqYZDcLu
20:10:39.515                                                             ⚠️ Unknown master: asst_wnshRkbnaca2vkRxYqYZDcLu, using base model
20:10:39.698                                                             📋 Response ID: resp_685460af782c819ea58823bd34646bfd03523b8b4bb8142b
20:10:41.162 VRI[Compet...y]@33fae3f                                     call setFrameRateCategory for touch hint category=no preference, reason=boost timeout, vri=VRI[CompetitiveModeActivity]@33fae3f
20:10:44.382 ResponsesAPI                                                🏁 Response completed
20:10:44.383 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["e5d3"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "In this position, ca...
20:10:44.384                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
20:10:44.384                                                             🎭 AI preferred moves: [e5d3]
20:10:44.384                                                             💭 AI reasoning: In this position, capturing on d3 with the knight aligns with my aggressive style, seeking to disrupt White's pawn structure and maintain pressure on the center. The move also opens lines for my pieces and supports active play.
20:10:44.423 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e5d3
20:10:44.783 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/1pp2pbp/5np1/p7/2P1N1Q1/3nP3/PP3PPP/R1B1K2R w KQkq - 0 11
20:10:44.790                                                             📜 Move history updated: 20 moves
20:10:44.790 GameHistoryManager                                          Move added: e5d3
20:10:44.790 GameViewModel                                               🔍 Requesting position evaluation...
20:10:44.790                                                             🎭 Updating personality context for move: e5d3
20:10:44.791                                                             ✨ Personality context updated for move e5d3 - This is revolutionary!
20:10:44.791                                                             🔍 Checking game end conditions...
20:10:44.892                                                             ✅ Game continues - no end condition detected
20:10:45.046                                                             ✅ Evaluation received: -10.37
20:10:45.149 CompetitiveModeActivity                                     📊 Evaluation updated: -10.37
20:10:45.149                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -10.37, emotionalManager: INITIALIZED
20:10:45.149                                                             🎯 Evaluation change: 16.59 (threshold: 0.8)
20:10:45.149                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 23s/25s)
20:10:45.149                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
20:11:00.076 VRI[Compet...y]@33fae3f                                     onDisplayChanged oldDisplayState=2 newDisplayState=2
