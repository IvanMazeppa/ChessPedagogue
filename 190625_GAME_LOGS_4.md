18:52:40.145 ProfileInstaller        com.example.chesspedagogue          Installing profile for com.example.chesspedagogue
18:52:40.923 ActivityThread                                              com.example.chesspedagogue will use render engine as VK
18:52:40.936 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@7f672af
18:52:41.006 ScrollView                                                  initGoToTop
18:52:41.015                                                             initGoToTop
18:52:41.026 InputMethodManager                                          invalidateInput
18:52:41.027                                                             invalidateInput
18:52:41.029 LogThrottlerConfig                                          📊 Log throttling initialized: VERBOSE
18:52:41.030 ApiKeyConfig                                                Using API key from ApiKeys class
18:52:41.062 OpenAIService                                               API key set, length: 164
18:52:41.062 ApiKeyConfig                                                Initialized OpenAIClient with API key from config
18:52:41.062 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.062                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.063 ApiKeyConfig                                                Using API key from ApiKeys class
18:52:41.063 OpenAIService                                               API key set, length: 164
18:52:41.063 ApiKeyConfig                                                Initialized OpenAIClient with API key from config
18:52:41.063 TTSServiceManager                                           ✅ Switched to ElevenLabs TTS
18:52:41.065 🎭 VoiceEm...alAnalyzer                                     ✅ Initialized voice profiles for 12 masters
18:52:41.065                                                             🚀 Voice-Emotion Feedback System initialized
18:52:41.065 TTSServiceManager                                           🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.065 ApiKeyConfig                                                Using API key from ApiKeys class
18:52:41.065                                                             Using API key from ApiKeys class
18:52:41.065 OpenAIService                                               API key set, length: 164
18:52:41.381 ResponsesAPI                                                ✅ Created managed executor service for ResponsesAPIService
18:52:41.381 AIStyleAdvisor                                              🧠 AIStyleAdvisor initialized - ready for chess personality enhancement!
18:52:41.393 GameViewModel                                               🎭 Initializing personality LiveData...
18:52:41.393                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
18:52:41.393                                                             🔍 Requesting position evaluation...
18:52:41.397                                                             ✅ Auto-commentary system enabled
18:52:41.397 ApiKeyConfig                                                Using API key from ApiKeys class
18:52:41.398 GameViewModel                                               🎮 Starting new game with configuration: white, skill=10, elo=1750
18:52:41.398 GameHistoryManager                                          GameHistoryManager instance created
18:52:41.398                                                             Game history cleared
18:52:41.399 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.399                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.399                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.399                                                             ✅ Usage context set to: spectator_mode
18:52:41.400 EmotionalIntelligence                                       🚀 CONSTRUCTOR CALLED: EmotionalIntelligenceManager initialization starting...
18:52:41.401                                                             ✅ Persistence manager initialized for EQ database operations
18:52:41.401                                                             🔧 About to call verifyDatabaseIntegrity()...
18:52:41.404 RelationshipPersistence                                     🔧 Database integrity check - emotional_reactions table exists: true
18:52:41.404                                                             🔧 Emotional reactions table schema:
18:52:41.404                                                               - Column: id (INTEGER) NOT NULL: false
18:52:41.404                                                               - Column: master (TEXT) NOT NULL: true
18:52:41.404                                                               - Column: opponent (TEXT) NOT NULL: true
18:52:41.404                                                               - Column: topic (TEXT) NOT NULL: true
18:52:41.404                                                               - Column: emotion (TEXT) NOT NULL: true
18:52:41.404                                                               - Column: intensity (REAL) NOT NULL: true
18:52:41.404                                                               - Column: momentum (REAL) NOT NULL: false
18:52:41.404                                                               - Column: game_context (TEXT) NOT NULL: false
18:52:41.404                                                               - Column: position_evaluation (REAL) NOT NULL: false
18:52:41.405                                                               - Column: conversation_snippet (TEXT) NOT NULL: false
18:52:41.405                                                               - Column: relationship_impact (REAL) NOT NULL: false
18:52:41.405                                                               - Column: timestamp (INTEGER) NOT NULL: true
18:52:41.405                                                             🔧 Database integrity check - master_relationships table exists: true
18:52:41.405                                                             🔧 Master relationships table schema:
18:52:41.405                                                               - Column: id (INTEGER) NOT NULL: false
18:52:41.405                                                               - Column: master1 (TEXT) NOT NULL: true
18:52:41.405                                                               - Column: master2 (TEXT) NOT NULL: true
18:52:41.405                                                               - Column: respect_level (REAL) NOT NULL: false
18:52:41.405                                                               - Column: rivalry_intensity (REAL) NOT NULL: false
18:52:41.405                                                               - Column: friendship_bond (REAL) NOT NULL: false
18:52:41.406                                                               - Column: communication_style (TEXT) NOT NULL: false
18:52:41.406                                                               - Column: total_interactions (INTEGER) NOT NULL: false
18:52:41.406                                                               - Column: total_games (INTEGER) NOT NULL: false
18:52:41.406                                                               - Column: last_major_event (TEXT) NOT NULL: false
18:52:41.406                                                               - Column: last_updated (INTEGER) NOT NULL: false
18:52:41.406                                                               - Column: created_at (INTEGER) NOT NULL: false
18:52:41.406                                                             🔧 Database tables exist - testing simple operations...
18:52:41.406                                                             🔧 Current emotional_reactions records: 652
18:52:41.407                                                             🔧 Current master_relationships records: 45
18:52:41.407 EmotionalIntelligence                                       🔧 verifyDatabaseIntegrity() completed
18:52:41.407                                                             🧪 About to call testDatabaseWrites()...
18:52:41.407 RelationshipPersistence                                     🧪 Testing database writes...
18:52:41.407                                                             🔧 Recording emotional reaction - Parameters: master=alekhine, opponent=carlsen, topic=chess_aesthetics, emotion=impressed, intensity=0.80
18:52:41.443                                                             🎭 Recorded emotional reaction: alekhine felt impressed (0.80) about chess_aesthetics with carlsen
18:52:41.444                                                             👥 Retrieved relationship: alekhine <-> carlsen (respect: 7.80, rivalry: 0.00, friendship: 0.00)
18:52:41.444                                                             🔧 Updating relationship - alekhine <-> carlsen (respect: 7.90, rivalry: 0.00, friendship: 0.00)
18:52:41.446                                                             💾 Updated relationship: alekhine <-> carlsen (respect: 7.90, rivalry: 0.00, friendship: 0.00)
18:52:41.446                                                             🧪 Database write test completed
18:52:41.446 EmotionalIntelligence                                       🧪 testDatabaseWrites() completed
18:52:41.446                                                             🎭 Enhanced Emotional Intelligence Manager initialized with database persistence
18:52:41.453 ConversationVariety                                         Error parsing phrase count: alekhine:my opponents find only chaos
18:52:41.453                                                             Error parsing phrase count: alekhine:one precise stroke and the combination will blossom
18:52:41.454                                                             ✅ Loaded 6 tracked phrases
18:52:41.455 EmotionalIntelligence                                       🧠 Loaded emotional history for tal vs fischer: 0 events, momentum: 0.40
18:52:41.455 ConversationFlowTester                                      🎭 OPTIMIZING CONVERSATIONS FOR SPECTATOR MODE:
18:52:41.455                                                             📊 Current template analysis:
18:52:41.455                                                             🏥 CONVERSATION HEALTH METRICS:
18:52:41.457 Conversati...maTemplate                                     🌊 CREATING NATURAL FLOW TEMPLATE: Natural Decline (Pattern: DECLINING, First: 60 words, Follow-up: 60.0% of first)
18:52:41.457                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Natural Decline (Initial: 48-72 words, Followup: 8-36 words, Flow: DECLINING, Intensity: 0.8)
18:52:41.458                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Building Excitement (Pattern: BUILDING, First: 35 words, Follow-up: 80.0% of first)
18:52:41.458                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Building Excitement (Initial: 28-42 words, Followup: 8-28 words, Flow: BUILDING, Intensity: 0.8)
18:52:41.459                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Wave Discussion (Pattern: WAVE, First: 50 words, Follow-up: 70.0% of first)
18:52:41.459                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Wave Discussion (Initial: 40-60 words, Followup: 8-35 words, Flow: WAVE, Intensity: 0.8)
18:52:41.459                                                             🌊 CREATING NATURAL FLOW TEMPLATE: Explosive Debate (Pattern: EXPLOSIVE, First: 40 words, Follow-up: 50.0% of first)
18:52:41.459                                                             🎨 CREATING ADVANCED FLOW TEMPLATE: Explosive Debate (Initial: 32-48 words, Followup: 8-20 words, Flow: EXPLOSIVE, Intensity: 0.8)
18:52:41.460 ConversationFlowTester                                      🎭 CURRENT: Engaging Chess Dialogue | Opening: 4-6 turns (90% chance) | Analysis: 4-6 turns (90% chance) | Style: DRAMATIC
18:52:41.460                                                               📊 opening: 4-6 turns, 90% response chance, 2000ms intervals
18:52:41.460                                                               📊 brilliant_move: 4-6 turns, 90% response chance, 1800ms intervals
18:52:41.460                                                               📊 position_change: 4-6 turns, 90% response chance, 2200ms intervals
18:52:41.460                                                               📊 endgame: 4-6 turns, 90% response chance, 2800ms intervals
18:52:41.460                                                             🔬 Running spectator mode simulation:
18:52:41.460                                                             🔬 SIMULATING CONVERSATION FLOW:
18:52:41.460                                                             🎯 Trigger: opening
18:52:41.461                                                             📋 Schema: 4-6 turns, 90.0% response chance
18:52:41.461                                                               Turn 1: ✅ CONTINUE (Current: 1/4-6)
18:52:41.461                                                               Turn 2: ✅ CONTINUE (Current: 2/4-6)
18:52:41.461                                                               Turn 3: ✅ CONTINUE (Current: 3/4-6)
18:52:41.462                                                               Turn 4: ✅ CONTINUE (Current: 4/4-6)
18:52:41.462                                                               Turn 5: ✅ CONTINUE (Current: 5/4-6)
18:52:41.462                                                               Turn 6: 🛑 STOP (Current: 6/4-6)
18:52:41.462                                                             🏁 Conversation ended after 6 turns
18:52:41.462                                                             🔬 SIMULATING CONVERSATION FLOW:
18:52:41.462                                                             🎯 Trigger: position_change
18:52:41.462                                                             📋 Schema: 4-6 turns, 90.0% response chance
18:52:41.462                                                               Turn 1: ✅ CONTINUE (Current: 1/4-6)
18:52:41.462                                                               Turn 2: ✅ CONTINUE (Current: 2/4-6)
18:52:41.462                                                               Turn 3: ✅ CONTINUE (Current: 3/4-6)
18:52:41.462                                                               Turn 4: ✅ CONTINUE (Current: 4/4-6)
18:52:41.462                                                               Turn 5: 🛑 STOP (Current: 5/4-6)
18:52:41.462                                                             🏁 Conversation ended after 5 turns
18:52:41.462                                                             💡 SPECTATOR MODE RECOMMENDATIONS:
18:52:41.462                                                               🎯 Use 'engaging' template for balanced multi-turn dialogues
18:52:41.462                                                               🎯 Opening conversations: 3-6 turns (current spectator games start strong)
18:52:41.462                                                               🎯 Position analysis: 4-8 turns (masters debate moves actively)
18:52:41.462                                                               🎯 85% response rate (masters usually engage with each other)
18:52:41.462                                                               🎯 2.5s intervals (natural conversation pacing)
18:52:41.462 Conversati...maTemplate                                     🎭 CONVERSATION TEMPLATE CHANGED: Engaging Chess Dialogue (Intensity: ENGAGING, Style: DRAMATIC)
18:52:41.462                                                             🔧 TESTING: Engaging conversations (recommended for spectator mode)
18:52:41.462 ConversationFlowTester                                      🎭 CONVERSATION MODE: Engaging (3-6 opening turns, 4-8 analysis turns, 85% response rate)
18:52:41.463                                                             📊 🎭 CURRENT: Engaging Chess Dialogue | Opening: 4-6 turns (90% chance) | Analysis: 4-6 turns (90% chance) | Style: DRAMATIC
18:52:41.463                                                             ✅ APPLIED: Engaging conversation template for spectator mode
18:52:41.463 🎭 EmotionalMomentum                                        🚀 Emotional Momentum Manager initialized
18:52:41.463 🎭 Phase2Bridge                                             🚀 Phase 2 Emotional Integration Bridge initialized with momentum system
18:52:41.464 ResponsesA...tionHelper                                     Responses API for tal: enabled
18:52:41.464                                                             Responses API for fischer: enabled
18:52:41.464                                                             Responses API for carlsen: enabled
18:52:41.464                                                             Responses API for anand: enabled
18:52:41.464                                                             Responses API for alekhine: enabled
18:52:41.464                                                             Responses API for kasparov: enabled
18:52:41.464                                                             🔧 FORCE ENABLED Responses API for the 6 working masters: Tal, Fischer, Carlsen, Anand, Alekhine, Kasparov
18:52:41.464                                                             📋 Other masters (Kramnik, Karpov, etc.) will use fallback dialogue
18:52:41.464                                                             🔄 Integration helper initialized. Responses API enabled: true
18:52:41.464                                                             🎯 Masters with Responses API enabled: {anand=true, kasparov=true, alekhine=true, fischer=true, carlsen=true, tal=true}
18:52:41.464                                                             📋 Master 'anand' → Responses API: true
18:52:41.464                                                             📋 Master 'kasparov' → Responses API: true
18:52:41.464                                                             📋 Master 'alekhine' → Responses API: true
18:52:41.464                                                             📋 Master 'fischer' → Responses API: true
18:52:41.464                                                             📋 Master 'carlsen' → Responses API: true
18:52:41.464                                                             📋 Master 'tal' → Responses API: true
18:52:41.499 GameViewModel                                               🔍 Requesting position evaluation...
18:52:41.499                                                             🔄 Skipping evaluation - too soon since last request
18:52:41.499                                                             ✅ Game initialized with white vs 1750 Elo engine
18:52:41.503 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.initializeGameWithConfiguration:1101 
18:52:41.505                                                             show: isDexDualMode = false
18:52:41.505                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
18:52:41.509 🔗 LiveMonitorClient                                        Built server URL: ws://192.168.0.237:8080
18:52:41.509                                                             LiveMonitorClient initialized with server URL: ws://192.168.0.237:8080
18:52:41.509                                                             🚫 Live monitor disabled via ENABLE_LIVE_MONITOR flag - skipping connection
18:52:41.513 VoiceControlManager                                         🎤 VoiceControlManager initialized
18:52:41.513                                                             📋 Registered voice command listener: main_game
18:52:41.514 UserProfileManager                                          👤 Loaded user profile: Mr Ben Lockrey
18:52:41.514                                                             🎭 Integrating user profile with emotional intelligence systems...
18:52:41.544                                                             ✅ User successfully integrated into AI emotional ecosystem
18:52:41.548 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.548                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.548                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.548 ApiKeyConfig                                                Using API key from ApiKeys class
18:52:41.548 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.548                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.548                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.548 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
18:52:41.549 GameViewModel                                               🎭 CONFIGURING PERSONALITY ENGINE: alekhine (weight=1.00, enabled=true)
18:52:41.554 VoiceControlManager                                         📋 Registered voice command listener: main_game
18:52:41.557 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
18:52:41.564 Choreographer                                               Skipped 77 frames!  The application may be doing too much work on its main thread.
18:52:41.592 BufferQueueProducer                                         [](id:1bca00000001,api:0,p:0,c:7114) setDequeueTimeout:2077252342
18:52:41.592 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
18:52:41.592                                                             Access denied finding property "vendor.gpp.create_frc_extension"
18:52:41.603 ScrollView                                                   onsize change changed 
18:52:41.613 ChessSetManager                                             ✅ Initialized 2 chess sets
18:52:41.613                                                             🎨 Chess Set Manager initialized with set: staunton_classic
18:52:41.650 BLASTBufferQueue                                            [VRI[MainActivity]@e08e64f#1](f:0,a:0,s:0) onFrameAvailable the first frame is available
18:52:41.650 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 36689, bufferData(ID: 30554397343751, frameNumber: 1)
18:52:41.650 HWUI                                                        CFMS:: SetUp Pid : 7114    Tid : 7168
18:52:41.661 ApiKeyConfig                                                Using API key from ApiKeys class
18:52:41.661                                                             Using API key from ApiKeys class
18:52:41.661 OpenAIService                                               API key set, length: 164
18:52:41.661 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.661                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.661                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.662                                                             ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.662                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.662                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.663 ThreeStage...nseManager                                     🚀 Enhanced ThreeStageResponseManager initialized with Responses API integration!
18:52:41.663                                                             🔍 Integration Status Check:
18:52:41.663 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: fischer
18:52:41.663                                                             🎯 Master fischer Responses API enabled: true
18:52:41.663 ThreeStage...nseManager                                     🎯 Fischer eligible: true
18:52:41.663 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: tal
18:52:41.663                                                             🎯 Master tal Responses API enabled: true
18:52:41.663 ThreeStage...nseManager                                     🎯 Tal eligible: true
18:52:41.663 ResponsesA...tionHelper                                     🔍 Checking Responses API eligibility for: carlsen
18:52:41.663                                                             🎯 Master carlsen Responses API enabled: true
18:52:41.663 ThreeStage...nseManager                                     🎯 Carlsen eligible: true
18:52:41.663 HWUI                                                        Davey! duration=736ms; Flags=1, FrameTimelineVsyncId=71915633, IntendedVsync=417397186001959, Vsync=417397827870109, InputEventId=0, HandleInputStart=417397830692701, AnimationStart=417397830694628, PerformTraversalsStart=417397830884472, DrawStart=417397916112337, FrameDeadline=417397194335292, FrameInterval=417397829567441, FrameStartTime=8335950, SyncQueued=417397916131347, SyncStart=417397916367857, IssueDrawCommandsStart=417397916427024, SwapBuffers=417397917021816, FrameCompleted=417397923202128, DequeueBufferDuration=9740, QueueBufferDuration=156719, GpuCompleted=417397923202128, SwapBuffersCompleted=417397917554680, DisplayPresentTime=0, CommandSubmissionCompleted=417397917021816, 
18:52:41.663                                                             Davey! duration=735ms; Flags=1, FrameTimelineVsyncId=71915633, IntendedVsync=417397186001959, Vsync=417397827870109, InputEventId=0, HandleInputStart=417397830692701, AnimationStart=417397830694628, PerformTraversalsStart=417397830884472, DrawStart=417397870986660, FrameDeadline=417397194335292, FrameInterval=417397829567441, FrameStartTime=8335950, SyncQueued=417397887013899, SyncStart=417397887123118, IssueDrawCommandsStart=417397887293430, SwapBuffers=417397914675305, FrameCompleted=417397921993066, DequeueBufferDuration=8698, QueueBufferDuration=222812, GpuCompleted=417397921993066, SwapBuffersCompleted=417397915226035, DisplayPresentTime=0, CommandSubmissionCompleted=417397914675305, 
18:52:41.665 OpenAIService                                               API key set, length: 164
18:52:41.666 ConversationManager                                         Added new system message
18:52:41.666                                                             ConversationManager initialized with session: session_1750355561666
18:52:41.670 OpenAIService                                               API key set, length: 164
18:52:41.670 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.670                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.670                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.686 ConversationManager                                         Retrieved 21 session IDs
18:52:41.690                                                             ✅ Loaded conversation for session: session_1749828407544 with 10 messages
18:52:41.690                                                             ✅ Resumed conversation with 10 messages
18:52:41.700 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
18:52:41.714 InputMethodManagerUtils                                     startInputInner - Id : 0
18:52:41.714 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
18:52:41.720 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.720                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.720                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.720 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
18:52:41.720 GameViewModel                                               🎭 CONFIGURING PERSONALITY ENGINE: alekhine (weight=1.00, enabled=true)
18:52:41.722 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:41.722                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:41.722                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:41.722 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
18:52:42.164 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@ab4ebd2
18:52:42.218 GameViewModel                                               ✅ Evaluation received: 0.35
18:52:43.972 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:52:43.975 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #1)
18:52:43.975                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:52:43.975                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -...
18:52:43.975                                                             🎲 Candidate moves for AI analysis: [g1f3]
18:52:43.976 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:52:43.976                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:52:44.421                                                             📋 Response ID: resp_68544e6c3530819e9472ce23cac89016011316ddf30deb40
18:52:44.733 Dialog                                                      mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
18:52:44.743 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@ff39620
18:52:44.754 ScrollView                                                  initGoToTop
18:52:44.763 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{25b087c V.E...... R.....I. 0,0-0,0}[MainActivity], caller=android.view.WindowManagerImpl.addView:158 android.app.Dialog.show:511 com.example.chesspedagogue.MainActivity.launchCompetitiveMode:3146 
18:52:44.764 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
18:52:44.808 BufferQueueProducer                                         [](id:1bca00000002,api:0,p:0,c:7114) setDequeueTimeout:2077252342
18:52:44.808 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
18:52:44.808                                                             Access denied finding property "vendor.gpp.create_frc_extension"
18:52:44.832 AbsListView                                                  in onLayout changed 
18:52:44.833 ScrollView                                                   onsize change changed 
18:52:44.843 BLASTBufferQueue                                            [VRI[MainActivity]@9409505#2](f:0,a:0,s:0) onFrameAvailable the first frame is available
18:52:44.843 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 36701, bufferData(ID: 30554397343755, frameNumber: 1)
18:52:44.844 HWUI                                                        CFMS:: SetUp Pid : 7114    Tid : 7168
18:52:44.847                                                             HWUI - treat SMPTE_170M as sRGB
18:52:45.563 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:52:45.565 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #2)
18:52:45.565                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:52:45.565                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -...
18:52:45.565                                                             🎲 Candidate moves for AI analysis: [g1f3]
18:52:45.565 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:52:45.565                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:52:45.846                                                             📋 Response ID: resp_68544e6d9e54819cb5136622e9a87c3003a2367a3de0d306
18:52:46.822 AbsListView                                                 onTouchUp() mTouchMode : 2
18:52:46.833 WindowManager                                               WindowManagerGlobal#removeView, ty=2, view=com.android.internal.policy.DecorView{25b087c V.E...... R....... 0,0-1368,2678}[MainActivity], caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.app.Dialog.dismissDialog:808 
18:52:46.833 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@1ba7c81
18:52:46.836 HWUI                                                        endAllActiveAnimators on 0xb400007198330200 (AlertController$RecycleListView) with handle 0xb4000071fda9bae0
18:52:46.841 InputEventReceiver                                          Attempted to finish an input event but the input event receiver has already been disposed.
18:52:46.842 VoiceControlManager                                         📋 Unregistered voice command listener
18:52:46.846 ActivityThread                                              com.example.chesspedagogue will use render engine as VK
18:52:46.855 CompetitiveModeActivity                                     🏆 Starting competitive mode against chess master...
18:52:46.858 DecorView                                                   setWindowBackground: isPopOver=false color=fff4f1e8 d=android.graphics.drawable.ColorDrawable@e822e17
18:52:46.886 ScrollView                                                  initGoToTop
18:52:46.891 CompetitiveModeActivity                                     🔄 Synchronizing master selection: alekhine
18:52:46.892 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:52:46.892 CompetitiveModeActivity                                     🔍 Master sync verification:
18:52:46.892                                                               - Voice system (ChessFineTunedModels): alekhine
18:52:46.892                                                               - App system (ChessAppPrefs): alekhine
18:52:46.892                                                               - FineTunedModelManager: alekhine
18:52:46.892                                                             ✅ Master synchronization SUCCESS! All systems consistent.
18:52:46.892                                                             🎯 Competitive config - Master: alekhine, Color: white, Skill: 10, ELO: 1750
18:52:46.892                                                             ✅ Master synchronized across all SharedPreferences stores
18:52:46.893                                                             🎨 Initializing views...
18:52:46.894                                                             ✅ Dynamic master elements set for: alekhine
18:52:46.894                                                             ✅ Voice status indicator initialized
18:52:46.894                                                             ✅ Views initialized
18:52:46.894                                                             🎭 Initializing emotional intelligence systems...
18:52:46.894                                                             🧠 Initializing EQ system with historical emotional data...
18:52:46.993 WindowManager           system_server                       win=Window{81775c0 u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity EXITING} destroySurfaces: appStopped=false cleanupOnResume=false win.mWindowRemovalAllowed=true win.mRemoveOnExit=true win.mViewVisibility=0 caller=com.android.server.wm.WindowState.onExitAnimationDone:222 com.android.server.wm.WindowState.onAnimationFinished:161 com.android.server.wm.WindowContainer$$ExternalSyntheticLambda5.onAnimationFinished:26 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda1.run:28 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda0.onAnimationFinished:65 com.android.server.wm.LocalAnimationAdapter$$ExternalSyntheticLambda0.run:10 android.os.Handler.handleCallback:959 
18:52:47.224 EmotionalIntelligence   com.example.chesspedagogue          🧠 Loaded emotional history for alekhine vs player: 0 events, momentum: 0.25
18:52:47.224 CompetitiveModeActivity                                     ✅ EQ system initialized - emotional reactions and relationships should now work!
18:52:47.229 EmotionalS...egyLearner                                     🧠 Loaded 0 master strategy profiles from database
18:52:47.229                                                             🧠🎯 EmotionalStrategyLearner initialized - Ready for adaptive learning!
18:52:47.230 CrossMaste...ectiveness                                     ✅ Loaded global effectiveness statistics
18:52:47.231 CompetitiveModeActivity                                     🧠 Phase 3: Adaptive conversation strategy manager initialized
18:52:47.231                                                             ✅ Emotional intelligence systems initialized
18:52:47.231                                                             🎲 Initializing personality engine for alekhine...
18:52:47.231 DynamicRelationship                                         ✅ Loaded existing relationship dynamics
18:52:47.231 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:52:47.231 CompetitiveModeActivity                                     🎲 Creating PersonalityEngine instance...
18:52:47.231                                                             🎯 Setting current master to: alekhine
18:52:47.231                                                             🚀 CALLING initializeMasterData for: alekhine
18:52:47.231                                                             🎭 Enabling personality play...
18:52:47.234                                                             ⚖️ Using splash difficulty - ELO: 1750, Skill: 10
18:52:47.234                                                             ✅ Personality engine initialized for alekhine
18:52:47.234                                                             🔍 Running personality engine database diagnostic...
18:52:47.234 PersonalityDiagnostic                                       🔍 DIAGNOSING PERSONALITY ENGINE DATABASE FOR: alekhine
18:52:47.237                                                             📊 Master 'alekhine' - hasData: true, positions: 12276
18:52:47.237                                                             ✅ Data already exists - testing sample query...
18:52:47.237                                                             🧪 Testing sample query for alekhine...
18:52:47.238                                                             🧪 Sample query returned 5 results
18:52:47.238                                                             ✅ DATABASE QUERY WORKING! Sample position found:
18:52:47.238                                                                Master: alekhine
18:52:47.238                                                                FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq
18:52:47.238                                                                Opponent: Frank Marshall
18:52:47.242                                                             📊 Complete database stats: {kasparov=5010, alekhine=12276, fischer=5767, carlsen=5739, total_positions=33570, tal=4778}
18:52:47.242 CompetitiveModeActivity                                     🔬 Running comprehensive PersonalityEngine system diagnostic...
18:52:47.242 PersonalitySystemDiag                                       🔬 STARTING COMPREHENSIVE PERSONALITY ENGINE DIAGNOSTIC
18:52:47.242                                                             📋 Test Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
18:52:47.242                                                             🎭 Master: alekhine (Target ELO: 1750)
18:52:47.242                                                             🔍 Phase 1: Database Analysis
18:52:47.431 ResponsesAPI                                                🏁 Response completed
18:52:47.431 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["e2e4", "g1f3"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I favor aggr...
18:52:47.431                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:52:47.431                                                             🎭 AI preferred moves: [g1f3]
18:52:47.432                                                             💭 AI reasoning: I favor aggressive and dynamic play that opens lines and develops pieces with a focus on central control.
18:52:47.474 PersonalitySystemDiag                                       📊 Total alekhine positions in DB: 12276
18:52:47.482                                                             🎯 Exact FEN matches: 50
18:52:47.482                                                             🔍 Board pattern: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
18:52:47.482                                                             📋 Sample matched games: null
18:52:47.482                                                             🤖 Phase 2: AI Integration Analysis
18:52:47.482                                                             🤖 Assistant available for alekhine: true
18:52:47.482                                                             📚 Vector store connected: false
18:52:47.482                                                             🧠 AI analysis capability: Available
18:52:47.482                                                             🎨 Phase 3: Style Application Analysis
18:52:47.482                                                             ♟️ Stockfish candidates: null
18:52:47.482                                                             🎭 Personality weight: 0.0
18:52:47.482                                                             📊 Phase 4: Quality Assessment
18:52:47.482                                                             📊 Quality assessment complete - Score: 0.29999998
18:52:47.482                                                             ✅ DIAGNOSTIC COMPLETE
18:52:47.483                                                             === PersonalityEngine System Diagnostic ===
                                                                         Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
                                                                         Master: alekhine (Target ELO: 1750)
                                                                         
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
                                                                         Logically consistent: false
                                                                         Authenticity score: 0.30
                                                                         Issues found: Too many exact matches (suspicious); Low-ELO position matching super-GM database (illogical); 
18:52:47.483 CompetitiveModeActivity                                     📊 System diagnostic result: === PersonalityEngine System Diagnostic ===
                                                                         Position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
                                                                         Master: alekhine (Target ELO: 1750)
                                                                         
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
                                                                         Logically consistent: false
                                                                         Authenticity score: 0.30
                                                                         Issues found: Too many exact matches (suspicious); Low-ELO position matching super-GM database (illogical); 
18:52:47.483 PersonalitySystemDiag                                       🔍 QUICK NAME DIAGNOSTIC
18:52:47.489                                                             📊 Actual master names in database:
18:52:47.489                                                                anand: 1870 positions
18:52:47.489                                                                kasparov: 5010 positions
18:52:47.489                                                                alekhine: 12276 positions
18:52:47.489                                                                fischer: 5767 positions
18:52:47.489                                                                carlsen: 5739 positions
18:52:47.489                                                                total_positions: 35440 positions
18:52:47.489                                                                tal: 4778 positions
18:52:47.489 CompetitiveModeActivity                                     🎤 Initializing voice services...
18:52:47.489 VoiceControlManager                                         📋 Registered voice command listener: competitive_mode
18:52:47.491 CompetitiveModeActivity                                     🗣️ Updating voice for master: alekhine
18:52:47.491 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:52:47.491 CompetitiveModeActivity                                     🔍 FineTunedModelManager master: alekhine
18:52:47.491                                                             🔍 SharedPreferences master: alekhine
18:52:47.491 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:47.491                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:47.492                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:47.492 ChessCoachManager                                           Updated TTS settings: voiceStyle=auto, usePersonality=true
18:52:47.492 TTSServiceManager                                           ✅ Usage context set to: competitive_mode
18:52:47.492 CompetitiveModeActivity                                     ✅ Final verification - Voice system master: alekhine
18:52:47.492                                                             ✅ Voice services initializing...
18:52:47.846 GameViewModel                                               🎭 Initializing personality LiveData...
18:52:47.847                                                             ✅ Personality LiveData initialized - Default: Tal Personality Engine
18:52:47.847                                                             🔍 Requesting position evaluation...
18:52:47.847 CompetitiveModeActivity                                     🎭 Configuring GameRepository personality engine for alekhine
18:52:48.378                                                             ✅ GameRepository personality engine configured successfully!
18:52:48.378                                                             🎯 Master: alekhine, Weight: 0.3, Enabled: true
18:52:48.378                                                             🔍 Personality engine availability check: true
18:52:48.378                                                             ✅ CONFIRMED: GameRepository personality engine is properly configured and available!
18:52:48.378                                                             ✅ Game view model configured for competitive mode
18:52:48.378                                                             👀 Setting up observers...
18:52:48.379                                                             🎯 Setting up chess board interaction...
18:52:48.379                                                             ✅ Chess board interaction setup complete
18:52:48.379                                                             ✅ Observers setup complete
18:52:48.379                                                             🎮 Setting up controls...
18:52:48.380                                                             ✅ Controls setup complete
18:52:48.381                                                             ✅ All competitive mode systems initialized!
18:52:48.381                                                             🏁 Starting competitive game vs alekhine
18:52:48.381 GameViewModel                                               🎮 Starting new game with configuration: white, skill=10, elo=1750
18:52:48.529 GameHistoryManager                                          Game history cleared
18:52:48.630 GameViewModel                                               🔍 Requesting position evaluation...
18:52:48.630                                                             ✅ Game initialized with white vs 1750 Elo engine
18:52:48.630 AdaptiveStrategy                                            🔍 Started monitoring conversation: competitive_1750355568630 (alekhine vs player)
18:52:48.630 CompetitiveModeActivity                                     🧠 Phase 3: Started adaptive conversation monitoring for alekhine
18:52:48.631 RelationshipPersistence                                     👥 Retrieved relationship: alekhine <-> player (respect: 0.50, rivalry: 0.00, friendship: 0.00)
18:52:48.631 EmotionalS...egyLearner                                     🧠 Started learning session: alekhine vs player
18:52:48.632                                                             🎯 EXPLOITATION: alekhine using proven supportive approach vs player (50.0% success rate)
18:52:48.632 CrossMaste...ectiveness                                     🎓 Generated 0 cross-learning recommendations for alekhine vs player
18:52:48.633 AdaptiveStrategy                                            🎯 Optimal strategy for alekhine vs player: 'supportive' (confidence: 0.20)
18:52:48.633 CompetitiveModeActivity                                     🧠 Phase 3: Using adaptive strategy 'supportive' (confidence: 0.20) for greeting - Individual learning: EXPLORATION: Testing new approach 'supportive' to learn effectiveness; 
18:52:48.633                                                             🗣️ Speaking master dialogue: Alexander Alekhine at your service. Prepare for a combinatorial masterclass!
18:52:48.633 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:52:48.633                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:52:48.634                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:52:48.634                                                             ✅ Usage context set to: competitive_mode
18:52:48.634 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:52:48.634 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
18:52:48.634                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
18:52:48.636 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
18:52:48.636                                                             🎭 Updated emotion indicator: 😎
18:52:48.636                                                             ✅ Competitive game started successfully!
18:52:48.639                                                             🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
18:52:48.639                                                             📊 Evaluation updated: 0.0
18:52:48.639                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
18:52:48.639                                                             🎯 First emotional evaluation: 0.0 (threshold: 1.5)
18:52:48.639                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355568s/25s)
18:52:48.639                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:52:48.639                                                             📜 Move history updated: 0 moves
18:52:48.642 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
18:52:48.648 Choreographer                                               Skipped 215 frames!  The application may be doing too much work on its main thread.
18:52:48.669 BufferQueueProducer                                         [](id:1bca00000003,api:0,p:0,c:7114) setDequeueTimeout:2077252342
18:52:48.670 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
18:52:48.670                                                             Access denied finding property "vendor.gpp.create_frc_extension"
18:52:48.677 ScrollView                                                   onsize change changed 
18:52:48.701 ResponsesAPI                                                🏁 Response completed
18:52:48.701 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["g1f3", "e2e4"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I favor aggr...
18:52:48.701                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:52:48.701                                                             🎭 AI preferred moves: [g1f3]
18:52:48.701                                                             💭 AI reasoning: I favor aggressive development and rapid control of the center, aiming to unsettle my opponent early.
18:52:48.716 BLASTBufferQueue                                            [VRI[CompetitiveModeActivity]@1c33f12#3](f:0,a:0,s:0) onFrameAvailable the first frame is available
18:52:48.716 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 36710, bufferData(ID: 30554397343759, frameNumber: 1)
18:52:48.717 HWUI                                                        CFMS:: SetUp Pid : 7114    Tid : 7168
18:52:48.719 CompetitiveModeActivity                                     🎤 Voice service connected to competitive mode
18:52:48.720 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
18:52:48.745                                                             Davey! duration=1867ms; Flags=1, FrameTimelineVsyncId=71920077, IntendedVsync=417403115979931, Vsync=417404909490151, InputEventId=0, HandleInputStart=417404913568792, AnimationStart=417404913570355, PerformTraversalsStart=417404913642803, DrawStart=417404943326344, FrameDeadline=417403124313264, FrameInterval=417404913398792, FrameStartTime=8341908, SyncQueued=417404959736657, SyncStart=417404959825251, IssueDrawCommandsStart=417404960063219, SwapBuffers=417404980777751, FrameCompleted=417404983357594, DequeueBufferDuration=10677, QueueBufferDuration=311719, GpuCompleted=417404983357594, SwapBuffersCompleted=417404981668688, DisplayPresentTime=417401122998429, CommandSubmissionCompleted=417404980777751, 
18:52:48.774 InputMethodManagerUtils                                     startInputInner - Id : 0
18:52:48.774 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
18:52:49.240 WindowManager           system_server                       win=Window{c23cc2e u0 com.example.chesspedagogue/com.example.chesspedagogue.MainActivity} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.ActivityRecord.destroySurfaces:25 com.android.server.wm.ActivityRecord.activityStopped:204 com.android.server.wm.ActivityClientController.activityStopped:95 android.app.IActivityClientController$Stub.onTransact:722 com.android.server.wm.ActivityClientController.onTransact:1 android.os.Binder.execTransactInternal:1541 android.os.Binder.execTransact:1480 
18:52:49.333 GameViewModel           com.example.chesspedagogue          ✅ Evaluation received: 0.35
18:52:49.441 CompetitiveModeActivity                                     📊 Evaluation updated: 0.35
18:52:49.441                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.35, emotionalManager: INITIALIZED
18:52:49.441                                                             🎯 First emotional evaluation: 0.35 (threshold: 1.5)
18:52:49.441                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355569s/25s)
18:52:49.441                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:52:49.817 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:52:49.818 AIStyleAdvisor                                              ⚡ Cache hit for alekhine - instant advice
18:52:54.433 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
18:52:55.076                                                             🔥 Using MAX difficulty - ELO: 2690, Skill: 20
18:52:55.083 Toast                                                       show: caller = com.example.chesspedagogue.CompetitiveModeActivity.toggleDifficulty:2151 
18:52:55.084                                                             show: isDexDualMode = false
18:52:55.084                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
18:53:00.182 CompetitiveModeActivity                                     🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=3
18:53:00.182                                                             🎯 SQUARE TAPPED: row=6, col=3
18:53:00.182                                                             📝 Player color: white
18:53:00.182                                                             🔍 Selected row/col: -1/-1
18:53:00.541                                                             🎯 Selected piece: P at 6, 3
18:53:00.853                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
18:53:00.853                                                             🎯 SQUARE TAPPED: row=4, col=3
18:53:00.853                                                             📝 Player color: white
18:53:00.853                                                             🔍 Selected row/col: 6/3
18:53:00.853                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
18:53:00.853                                                             🎯 ATTEMPTING MOVE: d2d4
18:53:00.853                                                             📝 Player color: white
18:53:00.853                                                             🔄 Is player's turn: true
18:53:00.853                                                             🔄 Is white's turn: true
18:53:00.853                                                             ✅ Turn validation passed, making move: d2d4
18:53:00.853 GameViewModel                                               🎯 makePlayerMove called with: d2d4
18:53:00.904                                                             🔍 Validating move: d2d4 (attempt 1)
18:53:01.005                                                             📋 Current position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
18:53:01.056                                                             ⚖️ Move d2d4 legality check: LEGAL
18:53:01.056                                                             ✅ Executing validated move: d2d4
18:53:01.258                                                             📍 New position after move: rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1
18:53:01.260 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1
18:53:01.262                                                             📜 Move history updated: 1 moves
18:53:01.263 GameHistoryManager                                          Move added: d2d4
18:53:01.359 GameViewModel                                               🔍 Requesting position evaluation...
18:53:01.359                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:53:01.359                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:53:01.359                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:53:01.359                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:53:01.359                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:53:01.359                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:53:01.359                                                             🔧 gameRepository class: GameRepository
18:53:01.359                                                             🔧 Current thread: main
18:53:01.360 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:53:02.070 GameViewModel                                               ✅ Evaluation received: -0.24
18:53:02.178 CompetitiveModeActivity                                     📊 Evaluation updated: -0.24
18:53:02.178                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.24, emotionalManager: INITIALIZED
18:53:02.178                                                             🎯 First emotional evaluation: -0.24 (threshold: 1.5)
18:53:02.178                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355582s/25s)
18:53:02.178                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:53:02.887 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:53:02.889 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #3)
18:53:02.889                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:02.889                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq...
18:53:02.889                                                             🎲 Candidate moves for AI analysis: [g8f6]
18:53:02.889 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:02.889                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:03.092                                                             📋 Response ID: resp_68544e7ed904819e86dd7a8d0dcccebf047fb5710d506708
18:53:05.705                                                             🏁 Response completed
18:53:05.706 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["g8f6"],
                                                                           "style_weight": 0.9,
                                                                           "reasoning": "Alekhine often favor...
18:53:05.706                                                             ✅ Parsed AI advice: 1 moves, weight=0.9
18:53:05.706                                                             🎭 AI preferred moves: [g8f6]
18:53:05.706                                                             💭 AI reasoning: Alekhine often favored aggressive and unorthodox openings to provoke weaknesses and create dynamic imbalances.
18:53:05.739 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8f6
18:53:06.098 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 1 2
18:53:06.101                                                             📜 Move history updated: 2 moves
18:53:06.101 GameHistoryManager                                          Move added: g8f6
18:53:06.101 GameViewModel                                               🔍 Requesting position evaluation...
18:53:06.102                                                             🎭 Updating personality context for move: g8f6
18:53:06.103                                                             ✨ Personality context updated for move g8f6 - This is revolutionary!
18:53:06.103                                                             🔍 Checking game end conditions...
18:53:06.205                                                             ✅ Game continues - no end condition detected
18:53:06.810                                                             ✅ Evaluation received: 0.30
18:53:06.912 CompetitiveModeActivity                                     📊 Evaluation updated: 0.3
18:53:06.912                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.3, emotionalManager: INITIALIZED
18:53:06.912                                                             🎯 First emotional evaluation: 0.3 (threshold: 1.5)
18:53:06.912                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355586s/25s)
18:53:06.913                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:53:12.304                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=2
18:53:12.304                                                             🎯 SQUARE TAPPED: row=6, col=2
18:53:12.304                                                             📝 Player color: white
18:53:12.304                                                             🔍 Selected row/col: -1/-1
18:53:12.659                                                             🎯 Selected piece: P at 6, 2
18:53:12.972                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
18:53:12.972                                                             🎯 SQUARE TAPPED: row=4, col=2
18:53:12.972                                                             📝 Player color: white
18:53:12.973                                                             🔍 Selected row/col: 6/2
18:53:12.973                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=4, reaches=false
18:53:12.973                                                             🎯 ATTEMPTING MOVE: c2c4
18:53:12.973                                                             📝 Player color: white
18:53:12.973                                                             🔄 Is player's turn: true
18:53:12.973                                                             🔄 Is white's turn: true
18:53:12.973                                                             ✅ Turn validation passed, making move: c2c4
18:53:12.973 GameViewModel                                               🎯 makePlayerMove called with: c2c4
18:53:13.024                                                             🔍 Validating move: c2c4 (attempt 1)
18:53:13.124                                                             📋 Current position: rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 1 2
18:53:13.176                                                             ⚖️ Move c2c4 legality check: LEGAL
18:53:13.176                                                             ✅ Executing validated move: c2c4
18:53:13.378                                                             📍 New position after move: rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2
18:53:13.380 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2
18:53:13.382                                                             📜 Move history updated: 3 moves
18:53:13.383 GameHistoryManager                                          Move added: c2c4
18:53:13.479 GameViewModel                                               🔍 Requesting position evaluation...
18:53:13.479                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:53:13.479                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:53:13.479                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:53:13.479                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:53:13.479                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:53:13.479                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:53:13.479                                                             🔧 gameRepository class: GameRepository
18:53:13.479                                                             🔧 Current thread: main
18:53:13.479 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:53:14.186 GameViewModel                                               ✅ Evaluation received: -0.27
18:53:14.295 CompetitiveModeActivity                                     📊 Evaluation updated: -0.27
18:53:14.295                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.27, emotionalManager: INITIALIZED
18:53:14.295                                                             🎯 First emotional evaluation: -0.27 (threshold: 1.5)
18:53:14.295                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355594s/25s)
18:53:14.295                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:53:15.261 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:53:15.262 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #4)
18:53:15.262                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:15.262                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQ...
18:53:15.262                                                             🎲 Candidate moves for AI analysis: [e7e6]
18:53:15.262 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:15.263                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:15.548                                                             📋 Response ID: resp_68544e8b3e34819d8ee78c41129614c20ea583675988c223
18:53:18.409                                                             🏁 Response completed
18:53:18.410 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["e7e6", "d7d5"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I favor move...
18:53:18.410                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:53:18.410                                                             🎭 AI preferred moves: [e7e6]
18:53:18.410                                                             💭 AI reasoning: I favor moves that challenge my opponent's center and prepare for dynamic play. e7e6 opens lines for the bishop and supports eventual d7d5, aiming for a flexible pawn structure.
18:53:18.435 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7e6
18:53:18.793 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppp1ppp/4pn2/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3
18:53:18.796                                                             📜 Move history updated: 4 moves
18:53:18.796 GameHistoryManager                                          Move added: e7e6
18:53:18.796 GameViewModel                                               🔍 Requesting position evaluation...
18:53:18.797                                                             🎭 Updating personality context for move: e7e6
18:53:18.797                                                             ✨ Personality context updated for move e7e6 - This is revolutionary!
18:53:18.797                                                             🔍 Checking game end conditions...
18:53:18.899                                                             ✅ Game continues - no end condition detected
18:53:19.505                                                             ✅ Evaluation received: 0.29
18:53:19.627 CompetitiveModeActivity                                     📊 Evaluation updated: 0.29
18:53:19.627                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.29, emotionalManager: INITIALIZED
18:53:19.627                                                             🎯 First emotional evaluation: 0.29 (threshold: 1.5)
18:53:19.627                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355599s/25s)
18:53:19.627                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:53:43.614                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=6
18:53:43.614                                                             🎯 SQUARE TAPPED: row=7, col=6
18:53:43.614                                                             📝 Player color: white
18:53:43.614                                                             🔍 Selected row/col: -1/-1
18:53:43.970                                                             🎯 Selected piece: N at 7, 6
18:53:43.972                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
18:53:43.972                                                             🎯 SQUARE TAPPED: row=5, col=5
18:53:43.972                                                             📝 Player color: white
18:53:43.972                                                             🔍 Selected row/col: 7/6
18:53:43.972                                                             🎯 ATTEMPTING MOVE: g1f3
18:53:43.972                                                             📝 Player color: white
18:53:43.972                                                             🔄 Is player's turn: true
18:53:43.972                                                             🔄 Is white's turn: true
18:53:43.972                                                             ✅ Turn validation passed, making move: g1f3
18:53:43.972 GameViewModel                                               🎯 makePlayerMove called with: g1f3
18:53:44.023                                                             🔍 Validating move: g1f3 (attempt 1)
18:53:44.124                                                             📋 Current position: rnbqkb1r/pppp1ppp/4pn2/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3
18:53:44.177                                                             ⚖️ Move g1f3 legality check: LEGAL
18:53:44.177                                                             ✅ Executing validated move: g1f3
18:53:44.380                                                             📍 New position after move: rnbqkb1r/pppp1ppp/4pn2/8/2PP4/5N2/PP2PPPP/RNBQKB1R b KQkq - 1 3
18:53:44.382 Choreographer                                               Skipped 48 frames!  The application may be doing too much work on its main thread.
18:53:44.384 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppp1ppp/4pn2/8/2PP4/5N2/PP2PPPP/RNBQKB1R b KQkq - 1 3
18:53:44.387                                                             📜 Move history updated: 5 moves
18:53:44.387 GameHistoryManager                                          Move added: g1f3
18:53:44.480 GameViewModel                                               🔍 Requesting position evaluation...
18:53:44.480                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:53:44.480                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:53:44.480                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:53:44.481                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:53:44.481                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:53:44.481                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:53:44.481                                                             🔧 gameRepository class: GameRepository
18:53:44.481                                                             🔧 Current thread: main
18:53:44.481 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:53:45.186 GameViewModel                                               ✅ Evaluation received: -0.24
18:53:45.287 CompetitiveModeActivity                                     📊 Evaluation updated: -0.24
18:53:45.287                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.24, emotionalManager: INITIALIZED
18:53:45.287                                                             🎯 First emotional evaluation: -0.24 (threshold: 1.5)
18:53:45.287                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355625s/25s)
18:53:45.287                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:53:46.070 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:53:46.071 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #5)
18:53:46.071                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:46.071                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkb1r/pppp1ppp/4pn2/8/2PP4/5N2/PP2PPPP/RNBQKB1R b...
18:53:46.071                                                             🎲 Candidate moves for AI analysis: [d7d5]
18:53:46.072 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:46.072                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:53:46.271                                                             📋 Response ID: resp_68544eaa049481a0bddc90ab8592e5530346e3fad52964db
18:53:49.639                                                             🏁 Response completed
18:53:49.639 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["d7d5"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I prefer to challeng...
18:53:49.639                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:53:49.640                                                             🎭 AI preferred moves: [d7d5]
18:53:49.640                                                             💭 AI reasoning: I prefer to challenge the center early, aiming to disrupt White's pawn structure and open lines for active piece play. This reflects my aggressive style, seeking dynamic imbalances.
18:53:49.679 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7d5
18:53:50.038 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/5N2/PP2PPPP/RNBQKB1R w KQkq - 0 4
18:53:50.041                                                             📜 Move history updated: 6 moves
18:53:50.041 GameHistoryManager                                          Move added: d7d5
18:53:50.041 GameViewModel                                               🔍 Requesting position evaluation...
18:53:50.042                                                             🎭 Updating personality context for move: d7d5
18:53:50.042                                                             ✨ Personality context updated for move d7d5 - This is revolutionary!
18:53:50.042                                                             🔍 Checking game end conditions...
18:53:50.144                                                             ✅ Game continues - no end condition detected
18:53:50.748                                                             ✅ Evaluation received: 0.34
18:53:50.862 CompetitiveModeActivity                                     📊 Evaluation updated: 0.34
18:53:50.862                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.34, emotionalManager: INITIALIZED
18:53:50.862                                                             🎯 First emotional evaluation: 0.34 (threshold: 1.5)
18:53:50.862                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355630s/25s)
18:53:50.862                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:53:57.356                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=1
18:53:57.356                                                             🎯 SQUARE TAPPED: row=7, col=1
18:53:57.356                                                             📝 Player color: white
18:53:57.356                                                             🔍 Selected row/col: -1/-1
18:53:57.715                                                             🎯 Selected piece: N at 7, 1
18:53:58.066                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
18:53:58.067                                                             🎯 SQUARE TAPPED: row=5, col=2
18:53:58.067                                                             📝 Player color: white
18:53:58.067                                                             🔍 Selected row/col: 7/1
18:53:58.067                                                             🎯 ATTEMPTING MOVE: b1c3
18:53:58.067                                                             📝 Player color: white
18:53:58.067                                                             🔄 Is player's turn: true
18:53:58.067                                                             🔄 Is white's turn: true
18:53:58.067                                                             ✅ Turn validation passed, making move: b1c3
18:53:58.067 GameViewModel                                               🎯 makePlayerMove called with: b1c3
18:53:58.118                                                             🔍 Validating move: b1c3 (attempt 1)
18:53:58.219                                                             📋 Current position: rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/5N2/PP2PPPP/RNBQKB1R w KQkq - 0 4
18:53:58.270                                                             ⚖️ Move b1c3 legality check: LEGAL
18:53:58.270                                                             ✅ Executing validated move: b1c3
18:53:58.472                                                             📍 New position after move: rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 1 4
18:53:58.474 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 1 4
18:53:58.476                                                             📜 Move history updated: 7 moves
18:53:58.477 GameHistoryManager                                          Move added: b1c3
18:53:58.573 GameViewModel                                               🔍 Requesting position evaluation...
18:53:58.573                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:53:58.573                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:53:58.573                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:53:58.573                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:53:58.573                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:53:58.573                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:53:58.573                                                             🔧 gameRepository class: GameRepository
18:53:58.573                                                             🔧 Current thread: main
18:53:58.573 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:53:59.281 GameViewModel                                               ✅ Evaluation received: -0.24
18:53:59.390 CompetitiveModeActivity                                     📊 Evaluation updated: -0.24
18:53:59.390                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.24, emotionalManager: INITIALIZED
18:53:59.390                                                             🎯 First emotional evaluation: -0.24 (threshold: 1.5)
18:53:59.390                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355639s/25s)
18:53:59.390                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:54:00.358 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 4 historical positions
18:54:00.359 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #6)
18:54:00.360                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:00.360                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1...
18:54:00.360                                                             🎲 Candidate moves for AI analysis: [f8b4]
18:54:00.360 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:00.360                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:00.580                                                             📋 Response ID: resp_68544eb858d88192bb8a8f7c4a42f0ad0dbe45fa7c550037
18:54:03.995                                                             🏁 Response completed
18:54:03.996 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["f8b4", "d7d5"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I am drawn t...
18:54:03.997                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:54:03.997                                                             🎭 AI preferred moves: [f8b4]
18:54:03.997                                                             💭 AI reasoning: I am drawn to dynamic and aggressive play. Moving the bishop to b4 pins the knight and applies pressure on the center. The move d7d5 is consistent with my style, aiming to open the position and increase activity.
18:54:04.022 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8b4
18:54:04.383 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/ppp2ppp/4pn2/3p4/1bPP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 2 5
18:54:04.388                                                             📜 Move history updated: 8 moves
18:54:04.388 GameHistoryManager                                          Move added: f8b4
18:54:04.388 GameViewModel                                               🔍 Requesting position evaluation...
18:54:04.388                                                             🎭 Updating personality context for move: f8b4
18:54:04.389                                                             ✨ Personality context updated for move f8b4 - This is revolutionary!
18:54:04.389                                                             🔍 Checking game end conditions...
18:54:04.490                                                             ✅ Game continues - no end condition detected
18:54:05.095                                                             ✅ Evaluation received: 0.29
18:54:05.228 CompetitiveModeActivity                                     📊 Evaluation updated: 0.29
18:54:05.228                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.29, emotionalManager: INITIALIZED
18:54:05.228                                                             🎯 First emotional evaluation: 0.29 (threshold: 1.5)
18:54:05.228                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355645s/25s)
18:54:05.228                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:54:11.784                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
18:54:11.784                                                             🎯 SQUARE TAPPED: row=4, col=2
18:54:11.784                                                             📝 Player color: white
18:54:11.784                                                             🔍 Selected row/col: -1/-1
18:54:12.141                                                             🎯 Selected piece: P at 4, 2
18:54:12.549                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
18:54:12.549                                                             🎯 SQUARE TAPPED: row=3, col=3
18:54:12.550                                                             📝 Player color: white
18:54:12.550                                                             🔍 Selected row/col: 4/2
18:54:12.550                                                             ♟️ Promotion check: piece=P, fromRow=4, toRow=3, reaches=false
18:54:12.550                                                             🎯 ATTEMPTING MOVE: c4d5
18:54:12.550                                                             📝 Player color: white
18:54:12.550                                                             🔄 Is player's turn: true
18:54:12.550                                                             🔄 Is white's turn: true
18:54:12.550                                                             ✅ Turn validation passed, making move: c4d5
18:54:12.550 GameViewModel                                               🎯 makePlayerMove called with: c4d5
18:54:12.601                                                             🔍 Validating move: c4d5 (attempt 1)
18:54:12.702                                                             📋 Current position: rnbqk2r/ppp2ppp/4pn2/3p4/1bPP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 2 5
18:54:12.753                                                             ⚖️ Move c4d5 legality check: LEGAL
18:54:12.753                                                             ✅ Executing validated move: c4d5
18:54:12.956                                                             📍 New position after move: rnbqk2r/ppp2ppp/4pn2/3P4/1b1P4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 0 5
18:54:12.959 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/ppp2ppp/4pn2/3P4/1b1P4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 0 5
18:54:12.962                                                             📜 Move history updated: 9 moves
18:54:12.962 GameHistoryManager                                          Move added: c4d5
18:54:13.061 GameViewModel                                               🔍 Requesting position evaluation...
18:54:13.061                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:54:13.061                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:54:13.061                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:54:13.061                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:54:13.061                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:54:13.061                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:54:13.061                                                             🔧 gameRepository class: GameRepository
18:54:13.061                                                             🔧 Current thread: main
18:54:13.061 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:54:13.769 GameViewModel                                               ✅ Evaluation received: -0.22
18:54:13.877 CompetitiveModeActivity                                     📊 Evaluation updated: -0.22
18:54:13.877                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.22, emotionalManager: INITIALIZED
18:54:13.877                                                             🎯 First emotional evaluation: -0.22 (threshold: 1.5)
18:54:13.878                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355653s/25s)
18:54:13.878                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:54:15.034 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:54:15.035 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #7)
18:54:15.035                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:15.035                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqk2r/ppp2ppp/4pn2/3P4/1b1P4/2N2N2/PP2PPPP/R1BQKB1...
18:54:15.035                                                             🎲 Candidate moves for AI analysis: [e6d5]
18:54:15.036 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:15.036                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:15.207                                                             📋 Response ID: resp_68544ec6fc0c81a1b5c17f179c5132a309eeb6d8f90ae943
18:54:18.913                                                             🏁 Response completed
18:54:18.914 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["e6d5"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I appreciate positio...
18:54:18.914                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:54:18.914                                                             🎭 AI preferred moves: [e6d5]
18:54:18.914                                                             💭 AI reasoning: I appreciate positions that allow for dynamic play and control of the center. With e6d5, I aim to open lines and create imbalances that can be exploited.
18:54:18.966 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e6d5
18:54:19.325 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/ppp2ppp/5n2/3p4/1b1P4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 0 6
18:54:19.329                                                             📜 Move history updated: 10 moves
18:54:19.329 GameHistoryManager                                          Move added: e6d5
18:54:19.330 GameViewModel                                               🔍 Requesting position evaluation...
18:54:19.330                                                             🎭 Updating personality context for move: e6d5
18:54:19.330                                                             ✨ Personality context updated for move e6d5 - This is revolutionary!
18:54:19.330                                                             🔍 Checking game end conditions...
18:54:19.431                                                             ✅ Game continues - no end condition detected
18:54:20.037                                                             ✅ Evaluation received: 0.17
18:54:20.141 CompetitiveModeActivity                                     📊 Evaluation updated: 0.17
18:54:20.141                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.17, emotionalManager: INITIALIZED
18:54:20.141                                                             🎯 First emotional evaluation: 0.17 (threshold: 1.5)
18:54:20.142                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355660s/25s)
18:54:20.142                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:54:30.622                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=2
18:54:30.622                                                             🎯 SQUARE TAPPED: row=7, col=2
18:54:30.622                                                             📝 Player color: white
18:54:30.622                                                             🔍 Selected row/col: -1/-1
18:54:30.978                                                             🎯 Selected piece: B at 7, 2
18:54:31.448                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=6
18:54:31.448                                                             🎯 SQUARE TAPPED: row=3, col=6
18:54:31.448                                                             📝 Player color: white
18:54:31.448                                                             🔍 Selected row/col: 7/2
18:54:31.448                                                             🎯 ATTEMPTING MOVE: c1g5
18:54:31.448                                                             📝 Player color: white
18:54:31.448                                                             🔄 Is player's turn: true
18:54:31.448                                                             🔄 Is white's turn: true
18:54:31.448                                                             ✅ Turn validation passed, making move: c1g5
18:54:31.448 GameViewModel                                               🎯 makePlayerMove called with: c1g5
18:54:31.499                                                             🔍 Validating move: c1g5 (attempt 1)
18:54:31.600                                                             📋 Current position: rnbqk2r/ppp2ppp/5n2/3p4/1b1P4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 0 6
18:54:31.651                                                             ⚖️ Move c1g5 legality check: LEGAL
18:54:31.652                                                             ✅ Executing validated move: c1g5
18:54:31.855                                                             📍 New position after move: rnbqk2r/ppp2ppp/5n2/3p2B1/1b1P4/2N2N2/PP2PPPP/R2QKB1R b KQkq - 1 6
18:54:31.857 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqk2r/ppp2ppp/5n2/3p2B1/1b1P4/2N2N2/PP2PPPP/R2QKB1R b KQkq - 1 6
18:54:31.861                                                             📜 Move history updated: 11 moves
18:54:31.861 GameHistoryManager                                          Move added: c1g5
18:54:31.955 GameViewModel                                               🔍 Requesting position evaluation...
18:54:31.956                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:54:31.956                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:54:31.956                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:54:31.956                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:54:31.956                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:54:31.956                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:54:31.956                                                             🔧 gameRepository class: GameRepository
18:54:31.956                                                             🔧 Current thread: main
18:54:31.956 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:54:32.663 GameViewModel                                               ✅ Evaluation received: -0.17
18:54:32.763 CompetitiveModeActivity                                     📊 Evaluation updated: -0.17
18:54:32.763                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.17, emotionalManager: INITIALIZED
18:54:32.763                                                             🎯 First emotional evaluation: -0.17 (threshold: 1.5)
18:54:32.764                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355672s/25s)
18:54:32.764                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:54:34.110 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:54:34.112 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #8)
18:54:34.112                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:34.112                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbqk2r/ppp2ppp/5n2/3p2B1/1b1P4/2N2N2/PP2PPPP/R2QKB1...
18:54:34.112                                                             🎲 Candidate moves for AI analysis: [c8f5]
18:54:34.112 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:34.112                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:34.289                                                             📋 Response ID: resp_68544eda0fac81a0986b67fcad436079079af4b453b1448c
18:54:37.899                                                             🏁 Response completed
18:54:37.900 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["c8f5", "h7h6"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "In this posi...
18:54:37.900                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:54:37.900                                                             🎭 AI preferred moves: [c8f5]
18:54:37.900                                                             💭 AI reasoning: In this position, I favor active piece play and direct engagement with the opponent's forces. Moving the bishop to f5 challenges White's center and prepares for potential exchanges, a hallmark of my dynamic style. The move h6 could also be considered to provoke the bishop and create imbalances.
18:54:37.925 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c8f5
18:54:38.279 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qk2r/ppp2ppp/5n2/3p1bB1/1b1P4/2N2N2/PP2PPPP/R2QKB1R w KQkq - 2 7
18:54:38.282                                                             📜 Move history updated: 12 moves
18:54:38.282 GameHistoryManager                                          Move added: c8f5
18:54:38.282 GameViewModel                                               🔍 Requesting position evaluation...
18:54:38.282                                                             🎭 Updating personality context for move: c8f5
18:54:38.282                                                             ✨ Personality context updated for move c8f5 - This is revolutionary!
18:54:38.282                                                             🔍 Checking game end conditions...
18:54:38.383                                                             ✅ Game continues - no end condition detected
18:54:38.989                                                             ✅ Evaluation received: 0.35
18:54:39.090 CompetitiveModeActivity                                     📊 Evaluation updated: 0.35
18:54:39.090                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.35, emotionalManager: INITIALIZED
18:54:39.090                                                             🎯 First emotional evaluation: 0.35 (threshold: 1.5)
18:54:39.090                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355679s/25s)
18:54:39.090                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:54:49.156                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
18:54:49.156                                                             🎯 SQUARE TAPPED: row=6, col=4
18:54:49.156                                                             📝 Player color: white
18:54:49.156                                                             🔍 Selected row/col: -1/-1
18:54:49.513                                                             🎯 Selected piece: P at 6, 4
18:54:49.877                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=4
18:54:49.877                                                             🎯 SQUARE TAPPED: row=5, col=4
18:54:49.877                                                             📝 Player color: white
18:54:49.877                                                             🔍 Selected row/col: 6/4
18:54:49.877                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=5, reaches=false
18:54:49.877                                                             🎯 ATTEMPTING MOVE: e2e3
18:54:49.877                                                             📝 Player color: white
18:54:49.877                                                             🔄 Is player's turn: true
18:54:49.877                                                             🔄 Is white's turn: true
18:54:49.877                                                             ✅ Turn validation passed, making move: e2e3
18:54:49.877 GameViewModel                                               🎯 makePlayerMove called with: e2e3
18:54:49.928                                                             🔍 Validating move: e2e3 (attempt 1)
18:54:50.028                                                             📋 Current position: rn1qk2r/ppp2ppp/5n2/3p1bB1/1b1P4/2N2N2/PP2PPPP/R2QKB1R w KQkq - 2 7
18:54:50.079                                                             ⚖️ Move e2e3 legality check: LEGAL
18:54:50.079                                                             ✅ Executing validated move: e2e3
18:54:50.281                                                             📍 New position after move: rn1qk2r/ppp2ppp/5n2/3p1bB1/1b1P4/2N1PN2/PP3PPP/R2QKB1R b KQkq - 0 7
18:54:50.283 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qk2r/ppp2ppp/5n2/3p1bB1/1b1P4/2N1PN2/PP3PPP/R2QKB1R b KQkq - 0 7
18:54:50.284                                                             📜 Move history updated: 13 moves
18:54:50.284 GameHistoryManager                                          Move added: e2e3
18:54:50.388 GameViewModel                                               🔍 Requesting position evaluation...
18:54:50.389                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:54:50.389                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:54:50.389                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:54:50.389                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:54:50.389                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:54:50.389                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:54:50.389                                                             🔧 gameRepository class: GameRepository
18:54:50.389                                                             🔧 Current thread: main
18:54:50.390 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:54:51.096 GameViewModel                                               ✅ Evaluation received: -0.23
18:54:51.197 CompetitiveModeActivity                                     📊 Evaluation updated: -0.23
18:54:51.197                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.23, emotionalManager: INITIALIZED
18:54:51.197                                                             🎯 First emotional evaluation: -0.23 (threshold: 1.5)
18:54:51.197                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355691s/25s)
18:54:51.197                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:54:52.037 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:54:52.038 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #9)
18:54:52.038                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:52.038                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rn1qk2r/ppp2ppp/5n2/3p1bB1/1b1P4/2N1PN2/PP3PPP/R2QKB...
18:54:52.038                                                             🎲 Candidate moves for AI analysis: [e8g8]
18:54:52.038 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:52.038                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:54:52.295                                                             📋 Response ID: resp_68544eec014c819d9f4cdc5bfc1861da074fba58e5d47887
18:54:55.472                                                             🏁 Response completed
18:54:55.473 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["e8g8"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "The move 0-0 (e8g8) ...
18:54:55.473                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:54:55.473                                                             🎭 AI preferred moves: [e8g8]
18:54:55.473                                                             💭 AI reasoning: The move 0-0 (e8g8) aligns with my aggressive style, focusing on king safety while preparing to mobilize my forces for a counterattack. Castling helps connect the rooks, which enhances the coordination needed for an offensive against White's center.
18:54:55.513 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e8g8
18:54:55.869 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/ppp2ppp/5n2/3p1bB1/1b1P4/2N1PN2/PP3PPP/R2QKB1R w KQ - 1 8
18:54:55.874                                                             📜 Move history updated: 14 moves
18:54:55.874 GameHistoryManager                                          Move added: e8g8
18:54:55.874 GameViewModel                                               🔍 Requesting position evaluation...
18:54:55.874                                                             🎭 Updating personality context for move: e8g8
18:54:55.875                                                             ✨ Personality context updated for move e8g8 - This is revolutionary!
18:54:55.875                                                             🔍 Checking game end conditions...
18:54:55.977                                                             ✅ Game continues - no end condition detected
18:54:56.585                                                             ✅ Evaluation received: 0.26
18:54:56.688 CompetitiveModeActivity                                     📊 Evaluation updated: 0.26
18:54:56.688                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.26, emotionalManager: INITIALIZED
18:54:56.688                                                             🎯 First emotional evaluation: 0.26 (threshold: 1.5)
18:54:56.688                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355696s/25s)
18:54:56.688                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:55:04.457                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=5
18:55:04.457                                                             🎯 SQUARE TAPPED: row=7, col=5
18:55:04.457                                                             📝 Player color: white
18:55:04.457                                                             🔍 Selected row/col: -1/-1
18:55:04.814                                                             🎯 Selected piece: B at 7, 5
18:55:05.138                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=3
18:55:05.138                                                             🎯 SQUARE TAPPED: row=5, col=3
18:55:05.138                                                             📝 Player color: white
18:55:05.138                                                             🔍 Selected row/col: 7/5
18:55:05.138                                                             🎯 ATTEMPTING MOVE: f1d3
18:55:05.138                                                             📝 Player color: white
18:55:05.138                                                             🔄 Is player's turn: true
18:55:05.138                                                             🔄 Is white's turn: true
18:55:05.138                                                             ✅ Turn validation passed, making move: f1d3
18:55:05.138 GameViewModel                                               🎯 makePlayerMove called with: f1d3
18:55:05.189                                                             🔍 Validating move: f1d3 (attempt 1)
18:55:05.290                                                             📋 Current position: rn1q1rk1/ppp2ppp/5n2/3p1bB1/1b1P4/2N1PN2/PP3PPP/R2QKB1R w KQ - 1 8
18:55:05.341                                                             ⚖️ Move f1d3 legality check: LEGAL
18:55:05.341                                                             ✅ Executing validated move: f1d3
18:55:05.543                                                             📍 New position after move: rn1q1rk1/ppp2ppp/5n2/3p1bB1/1b1P4/2NBPN2/PP3PPP/R2QK2R b KQ - 2 8
18:55:05.546 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/ppp2ppp/5n2/3p1bB1/1b1P4/2NBPN2/PP3PPP/R2QK2R b KQ - 2 8
18:55:05.549                                                             📜 Move history updated: 15 moves
18:55:05.550 GameHistoryManager                                          Move added: f1d3
18:55:05.647 GameViewModel                                               🔍 Requesting position evaluation...
18:55:05.647                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:55:05.647                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:55:05.647                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:55:05.648                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:55:05.648                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:55:05.648                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:55:05.648                                                             🔧 gameRepository class: GameRepository
18:55:05.648                                                             🔧 Current thread: main
18:55:05.648 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:55:06.355 GameViewModel                                               ✅ Evaluation received: -0.14
18:55:06.456 CompetitiveModeActivity                                     📊 Evaluation updated: -0.14
18:55:06.456                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.14, emotionalManager: INITIALIZED
18:55:06.456                                                             🎯 First emotional evaluation: -0.14 (threshold: 1.5)
18:55:06.456                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355706s/25s)
18:55:06.456                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:55:07.342 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:55:07.343 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #10)
18:55:07.343                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:07.343                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rn1q1rk1/ppp2ppp/5n2/3p1bB1/1b1P4/2NBPN2/PP3PPP/R2QK...
18:55:07.343                                                             🎲 Candidate moves for AI analysis: [b4c3]
18:55:07.343 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:07.343                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:07.550                                                             📋 Response ID: resp_68544efb514c81a180b1d6f4cef9acdf05444f9a7b079e39
18:55:10.688                                                             🏁 Response completed
18:55:10.689 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["b4c3", "f5d3"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "As Alekhine,...
18:55:10.689                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:55:10.689                                                             🎭 AI preferred moves: [b4c3]
18:55:10.689                                                             💭 AI reasoning: As Alekhine, my style is aggressive and tactical. I enjoy creating complications and applying pressure. Playing Bxc3 disrupts White's pawn structure and opens lines for potential attacks. Nxd3 would increase control over the center and challenge White's coordination.
18:55:10.724 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b4c3
18:55:10.764 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
18:55:11.082 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/ppp2ppp/5n2/3p1bB1/3P4/2bBPN2/PP3PPP/R2QK2R w KQ - 0 9
18:55:11.088                                                             📜 Move history updated: 16 moves
18:55:11.088 GameHistoryManager                                          Move added: b4c3
18:55:11.088 GameViewModel                                               🔍 Requesting position evaluation...
18:55:11.088                                                             🎭 Updating personality context for move: b4c3
18:55:11.089                                                             ✨ Personality context updated for move b4c3 - This is revolutionary!
18:55:11.089                                                             🔍 Checking game end conditions...
18:55:11.190                                                             ✅ Game continues - no end condition detected
18:55:11.494                                                             ✅ Evaluation received: 0.21
18:55:11.598 CompetitiveModeActivity                                     📊 Evaluation updated: 0.21
18:55:11.598                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.21, emotionalManager: INITIALIZED
18:55:11.598                                                             🎯 First emotional evaluation: 0.21 (threshold: 1.5)
18:55:11.598                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355711s/25s)
18:55:11.598                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:55:25.482                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=1
18:55:25.482                                                             🎯 SQUARE TAPPED: row=6, col=1
18:55:25.482                                                             📝 Player color: white
18:55:25.482                                                             🔍 Selected row/col: -1/-1
18:55:25.838                                                             🎯 Selected piece: P at 6, 1
18:55:26.269                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
18:55:26.269                                                             🎯 SQUARE TAPPED: row=5, col=2
18:55:26.269                                                             📝 Player color: white
18:55:26.269                                                             🔍 Selected row/col: 6/1
18:55:26.269                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=5, reaches=false
18:55:26.269                                                             🎯 ATTEMPTING MOVE: b2c3
18:55:26.269                                                             📝 Player color: white
18:55:26.269                                                             🔄 Is player's turn: true
18:55:26.269                                                             🔄 Is white's turn: true
18:55:26.269                                                             ✅ Turn validation passed, making move: b2c3
18:55:26.269 GameViewModel                                               🎯 makePlayerMove called with: b2c3
18:55:26.320                                                             🔍 Validating move: b2c3 (attempt 1)
18:55:26.421                                                             📋 Current position: rn1q1rk1/ppp2ppp/5n2/3p1bB1/3P4/2bBPN2/PP3PPP/R2QK2R w KQ - 0 9
18:55:26.473                                                             ⚖️ Move b2c3 legality check: LEGAL
18:55:26.473                                                             ✅ Executing validated move: b2c3
18:55:26.676                                                             📍 New position after move: rn1q1rk1/ppp2ppp/5n2/3p1bB1/3P4/2PBPN2/P4PPP/R2QK2R b KQ - 0 9
18:55:26.684 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/ppp2ppp/5n2/3p1bB1/3P4/2PBPN2/P4PPP/R2QK2R b KQ - 0 9
18:55:26.688                                                             📜 Move history updated: 17 moves
18:55:26.688 GameHistoryManager                                          Move added: b2c3
18:55:26.781 GameViewModel                                               🔍 Requesting position evaluation...
18:55:26.781                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:55:26.781                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:55:26.781                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:55:26.781                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:55:26.781                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:55:26.781                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:55:26.782                                                             🔧 gameRepository class: GameRepository
18:55:26.782                                                             🔧 Current thread: main
18:55:26.782 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:55:27.488 GameViewModel                                               ✅ Evaluation received: -0.23
18:55:27.596 CompetitiveModeActivity                                     📊 Evaluation updated: -0.23
18:55:27.596                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.23, emotionalManager: INITIALIZED
18:55:27.597                                                             🎯 First emotional evaluation: -0.23 (threshold: 1.5)
18:55:27.597                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750355727s/25s)
18:55:27.597                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:55:28.478 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:55:28.479 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #11)
18:55:28.479                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:28.479                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rn1q1rk1/ppp2ppp/5n2/3p1bB1/3P4/2PBPN2/P4PPP/R2QK2R ...
18:55:28.479                                                             🎲 Candidate moves for AI analysis: [f5c8]
18:55:28.479 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:28.479                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:28.682                                                             📋 Response ID: resp_68544f1070bc81a2b7dee3dd618dccfe07584773dab1759b
18:55:30.631                                                             🏁 Response completed
18:55:30.632 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["f5c8"],
                                                                           "style_weight": 0.8,
                                                                           "reasoning": "The move f5c8 is a d...
18:55:30.633                                                             ✅ Parsed AI advice: 1 moves, weight=0.8
18:55:30.633                                                             🎭 AI preferred moves: [f5c8]
18:55:30.633                                                             💭 AI reasoning: The move f5c8 is a defensive retreat, which isn't typically my first instinct. However, conserving material while maintaining a flexible position allows for creative maneuvers. It sets the stage for potential counterplay.
18:55:30.673 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f5c8
18:55:31.032 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/ppp2ppp/5n2/3p2B1/3P4/2PBPN2/P4PPP/R2QK2R w KQ - 1 10
18:55:31.038                                                             📜 Move history updated: 18 moves
18:55:31.038 GameHistoryManager                                          Move added: f5c8
18:55:31.038 GameViewModel                                               🔍 Requesting position evaluation...
18:55:31.038                                                             🎭 Updating personality context for move: f5c8
18:55:31.038                                                             ✨ Personality context updated for move f5c8 - This is revolutionary!
18:55:31.038                                                             🔍 Checking game end conditions...
18:55:31.140                                                             ✅ Game continues - no end condition detected
18:55:31.747                                                             ✅ Evaluation received: 1.62
18:55:31.847 CompetitiveModeActivity                                     📊 Evaluation updated: 1.62
18:55:31.847                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.62, emotionalManager: INITIALIZED
18:55:31.847                                                             🎯 First emotional evaluation: 1.62 (threshold: 1.5)
18:55:31.847                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 1750355731s/25s)
18:55:31.847                                                             ✅ Emotional reaction triggered for evaluation: 1.62 (trigger: slight_disadvantage)
18:55:31.847 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
18:55:31.847                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:55:31.847                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:55:31.848                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
18:55:33.578                                                             ✅ Response generated: Being slightly behind is no excuse for passivity. I will seek out complications and sacrifice harmon...
18:55:33.578 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: Being slightly behind is no excuse for passivity. I will seek out complications and sacrifice harmony if necessary—chess rewards the brave, not the cautious.
18:55:33.584                                                             🎭 Updated emotion indicator: 🤔
18:55:33.584                                                             🗣️ Speaking master dialogue: Being slightly behind is no excuse for passivity. I will seek out complications and sacrifice harmony if necessary—chess rewards the brave, not the cautious.
18:55:33.584 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:55:33.584                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:55:33.584                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:55:33.584                                                             ✅ Usage context set to: competitive_mode
18:55:33.584 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:55:33.584 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
18:55:33.585                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
18:55:33.588 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
18:55:44.849                                                             🗣️ Master dialogue speech completed
18:55:50.229                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=4
18:55:50.229                                                             🎯 SQUARE TAPPED: row=7, col=4
18:55:50.229                                                             📝 Player color: white
18:55:50.229                                                             🔍 Selected row/col: -1/-1
18:55:50.586                                                             🎯 Selected piece: K at 7, 4
18:55:50.834                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=6
18:55:50.834                                                             🎯 SQUARE TAPPED: row=7, col=6
18:55:50.835                                                             📝 Player color: white
18:55:50.835                                                             🔍 Selected row/col: 7/4
18:55:50.835                                                             🎯 ATTEMPTING MOVE: e1g1
18:55:50.835                                                             📝 Player color: white
18:55:50.835                                                             🔄 Is player's turn: true
18:55:50.835                                                             🔄 Is white's turn: true
18:55:50.835                                                             ✅ Turn validation passed, making move: e1g1
18:55:50.835 GameViewModel                                               🎯 makePlayerMove called with: e1g1
18:55:50.886                                                             🔍 Validating move: e1g1 (attempt 1)
18:55:50.987                                                             📋 Current position: rnbq1rk1/ppp2ppp/5n2/3p2B1/3P4/2PBPN2/P4PPP/R2QK2R w KQ - 1 10
18:55:51.039                                                             ⚖️ Move e1g1 legality check: LEGAL
18:55:51.039                                                             ✅ Executing validated move: e1g1
18:55:51.241                                                             📍 New position after move: rnbq1rk1/ppp2ppp/5n2/3p2B1/3P4/2PBPN2/P4PPP/R2Q1RK1 b - - 2 10
18:55:51.244 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/ppp2ppp/5n2/3p2B1/3P4/2PBPN2/P4PPP/R2Q1RK1 b - - 2 10
18:55:51.248                                                             📜 Move history updated: 19 moves
18:55:51.249 GameHistoryManager                                          Move added: e1g1
18:55:51.345 GameViewModel                                               🔍 Requesting position evaluation...
18:55:51.345                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:55:51.345                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:55:51.345                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:55:51.345                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:55:51.345                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:55:51.345                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:55:51.345                                                             🔧 gameRepository class: GameRepository
18:55:51.345                                                             🔧 Current thread: main
18:55:51.345 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:55:52.053 GameViewModel                                               ✅ Evaluation received: -1.46
18:55:52.154 CompetitiveModeActivity                                     📊 Evaluation updated: -1.46
18:55:52.154                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.46, emotionalManager: INITIALIZED
18:55:52.154                                                             🎯 Evaluation change: 3.08 (threshold: 0.8)
18:55:52.154                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 20s/25s)
18:55:52.154                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
18:55:53.343 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
18:55:53.345 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #12)
18:55:53.345                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:53.345                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbq1rk1/ppp2ppp/5n2/3p2B1/3P4/2PBPN2/P4PPP/R2Q1RK1 ...
18:55:53.345                                                             🎲 Candidate moves for AI analysis: [c7c5]
18:55:53.345 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:53.345                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:55:53.548                                                             📋 Response ID: resp_68544f294cc4819e95509c733ccf1ae20441f3480a45ff60
18:55:55.397                                                             🏁 Response completed
18:55:55.398 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["c5", "h6"],
                                                                           "style_weight": 0.85,
                                                                           "reasoning": "The move c5 aim...
18:55:55.399                                                             ✅ Parsed AI advice: 0 moves, weight=0.85
18:55:55.399                                                             🎭 AI preferred moves: []
18:55:55.399                                                             💭 AI reasoning: The move c5 aims to undermine White's central pawn structure, creating dynamic imbalances, which I relish. Playing h6 would force the exchange of bishops, simplifying the position slightly and mitigating White's pressure on the kingside.
18:55:55.438 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c7c5
18:55:55.798 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/pp3ppp/5n2/2pp2B1/3P4/2PBPN2/P4PPP/R2Q1RK1 w - - 0 11
18:55:55.806                                                             📜 Move history updated: 20 moves
18:55:55.806 GameHistoryManager                                          Move added: c7c5
18:55:55.806 GameViewModel                                               🔍 Requesting position evaluation...
18:55:55.806                                                             🎭 Updating personality context for move: c7c5
18:55:55.807                                                             ✨ Personality context updated for move c7c5 - This is revolutionary!
18:55:55.807                                                             🔍 Checking game end conditions...
18:55:55.909                                                             ✅ Game continues - no end condition detected
18:55:56.517                                                             ✅ Evaluation received: 1.52
18:55:56.618 CompetitiveModeActivity                                     📊 Evaluation updated: 1.52
18:55:56.618                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.52, emotionalManager: INITIALIZED
18:55:56.619                                                             🎯 Evaluation change: 0.100000024 (threshold: 0.8)
18:55:56.619                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 24s/25s)
18:55:56.619                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
18:56:12.200                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
18:56:12.200                                                             🎯 SQUARE TAPPED: row=4, col=3
18:56:12.200                                                             📝 Player color: white
18:56:12.200                                                             🔍 Selected row/col: -1/-1
18:56:12.559                                                             🎯 Selected piece: P at 4, 3
18:56:12.889                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=2
18:56:12.889                                                             🎯 SQUARE TAPPED: row=3, col=2
18:56:12.889                                                             📝 Player color: white
18:56:12.889                                                             🔍 Selected row/col: 4/3
18:56:12.889                                                             ♟️ Promotion check: piece=P, fromRow=4, toRow=3, reaches=false
18:56:12.889                                                             🎯 ATTEMPTING MOVE: d4c5
18:56:12.889                                                             📝 Player color: white
18:56:12.889                                                             🔄 Is player's turn: true
18:56:12.890                                                             🔄 Is white's turn: true
18:56:12.890                                                             ✅ Turn validation passed, making move: d4c5
18:56:12.890 GameViewModel                                               🎯 makePlayerMove called with: d4c5
18:56:12.940                                                             🔍 Validating move: d4c5 (attempt 1)
18:56:13.042                                                             📋 Current position: rnbq1rk1/pp3ppp/5n2/2pp2B1/3P4/2PBPN2/P4PPP/R2Q1RK1 w - - 0 11
18:56:13.094                                                             ⚖️ Move d4c5 legality check: LEGAL
18:56:13.094                                                             ✅ Executing validated move: d4c5
18:56:13.297                                                             📍 New position after move: rnbq1rk1/pp3ppp/5n2/2Pp2B1/8/2PBPN2/P4PPP/R2Q1RK1 b - - 0 11
18:56:13.299 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/pp3ppp/5n2/2Pp2B1/8/2PBPN2/P4PPP/R2Q1RK1 b - - 0 11
18:56:13.304                                                             📜 Move history updated: 21 moves
18:56:13.304 GameHistoryManager                                          Move added: d4c5
18:56:13.400 GameViewModel                                               🔍 Requesting position evaluation...
18:56:13.400                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:56:13.400                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:56:13.400                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:56:13.400                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:56:13.400                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:56:13.400                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:56:13.400                                                             🔧 gameRepository class: GameRepository
18:56:13.400                                                             🔧 Current thread: main
18:56:13.400 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:56:14.107 GameViewModel                                               ✅ Evaluation received: -0.93
18:56:14.216 CompetitiveModeActivity                                     📊 Evaluation updated: -0.93
18:56:14.216                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.93, emotionalManager: INITIALIZED
18:56:14.216                                                             🎯 Evaluation change: 2.55 (threshold: 0.8)
18:56:14.216                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 42s/25s)
18:56:14.216                                                             ✅ Emotional reaction triggered for evaluation: -0.93 (trigger: position_shift)
18:56:14.216 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
18:56:14.216                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:56:14.216                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:56:14.216                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
18:56:15.245 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
18:56:15.247 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #13)
18:56:15.247                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:56:15.247                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbq1rk1/pp3ppp/5n2/2Pp2B1/8/2PBPN2/P4PPP/R2Q1RK1 b ...
18:56:15.247                                                             🎲 Candidate moves for AI analysis: [h7h6]
18:56:15.247 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:56:15.247                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:56:15.452                                                             📋 Response ID: resp_68544f3f34c4819fa02a7c65d64d4d510a86fa977241bd58
18:56:15.710 OpenAIService                                               ✅ Response generated: At move 11, the balance is an opportunity, not a limitation. I seek the thread that will unravel—com...
18:56:15.710 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: At move 11, the balance is an opportunity, not a limitation. I seek the thread that will unravel—combinations hide beneath harmony, waiting for the bold to reveal them.
18:56:15.712                                                             🎭 Updated emotion indicator: 🧐
18:56:15.713                                                             🗣️ Speaking master dialogue: At move 11, the balance is an opportunity, not a limitation. I seek the thread that will unravel—combinations hide beneath harmony, waiting for the bold to reveal them.
18:56:15.713 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:56:15.713                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:56:15.713                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:56:15.713                                                             ✅ Usage context set to: competitive_mode
18:56:15.713 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:56:15.713 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
18:56:15.713                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
18:56:15.714 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
18:56:17.760 ResponsesAPI                                                🏁 Response completed
18:56:17.760 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["h7h6", "c8g4"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I prefer dyn...
18:56:17.760                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:56:17.760                                                             🎭 AI preferred moves: [h7h6]
18:56:17.760                                                             💭 AI reasoning: I prefer dynamic and aggressive positions where I can create imbalances and exploit weaknesses. The move h7h6 is a preparatory step to challenge White's dark square bishop, while c8g4 looks to develop a piece with an eye on the e4 pawn.
18:56:17.791 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h7h6
18:56:18.149 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/pp3pp1/5n1p/2Pp2B1/8/2PBPN2/P4PPP/R2Q1RK1 w - - 0 12
18:56:18.156                                                             📜 Move history updated: 22 moves
18:56:18.156 GameHistoryManager                                          Move added: h7h6
18:56:18.156 GameViewModel                                               🔍 Requesting position evaluation...
18:56:18.156                                                             🎭 Updating personality context for move: h7h6
18:56:18.156                                                             ✨ Personality context updated for move h7h6 - This is revolutionary!
18:56:18.156                                                             🔍 Checking game end conditions...
18:56:18.258                                                             ✅ Game continues - no end condition detected
18:56:18.865                                                             ✅ Evaluation received: 1.47
18:56:18.968 CompetitiveModeActivity                                     📊 Evaluation updated: 1.47
18:56:18.968                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.47, emotionalManager: INITIALIZED
18:56:18.968                                                             🎯 Evaluation change: 2.4 (threshold: 0.8)
18:56:18.968                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 4s/25s)
18:56:18.968                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
18:56:26.458                                                             🗣️ Master dialogue speech completed
18:56:46.555                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=6
18:56:46.555                                                             🎯 SQUARE TAPPED: row=3, col=6
18:56:46.555                                                             📝 Player color: white
18:56:46.555                                                             🔍 Selected row/col: -1/-1
18:56:46.913                                                             🎯 Selected piece: B at 3, 6
18:56:47.488                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
18:56:47.488                                                             🎯 SQUARE TAPPED: row=4, col=5
18:56:47.488                                                             📝 Player color: white
18:56:47.488                                                             🔍 Selected row/col: 3/6
18:56:47.488                                                             🎯 ATTEMPTING MOVE: g5f4
18:56:47.488                                                             📝 Player color: white
18:56:47.488                                                             🔄 Is player's turn: true
18:56:47.488                                                             🔄 Is white's turn: true
18:56:47.488                                                             ✅ Turn validation passed, making move: g5f4
18:56:47.488 GameViewModel                                               🎯 makePlayerMove called with: g5f4
18:56:47.539                                                             🔍 Validating move: g5f4 (attempt 1)
18:56:47.641                                                             📋 Current position: rnbq1rk1/pp3pp1/5n1p/2Pp2B1/8/2PBPN2/P4PPP/R2Q1RK1 w - - 0 12
18:56:47.692                                                             ⚖️ Move g5f4 legality check: LEGAL
18:56:47.692                                                             ✅ Executing validated move: g5f4
18:56:47.894                                                             📍 New position after move: rnbq1rk1/pp3pp1/5n1p/2Pp4/5B2/2PBPN2/P4PPP/R2Q1RK1 b - - 1 12
18:56:47.897 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/pp3pp1/5n1p/2Pp4/5B2/2PBPN2/P4PPP/R2Q1RK1 b - - 1 12
18:56:47.902                                                             📜 Move history updated: 23 moves
18:56:47.902 GameHistoryManager                                          Move added: g5f4
18:56:47.998 GameViewModel                                               🔍 Requesting position evaluation...
18:56:47.999                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:56:47.999                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:56:47.999                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:56:47.999                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:56:47.999                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:56:47.999                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:56:47.999                                                             🔧 gameRepository class: GameRepository
18:56:47.999                                                             🔧 Current thread: main
18:56:47.999 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:56:48.705 GameViewModel                                               ✅ Evaluation received: -1.59
18:56:48.814 CompetitiveModeActivity                                     📊 Evaluation updated: -1.59
18:56:48.814                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.59, emotionalManager: INITIALIZED
18:56:48.814                                                             🎯 Evaluation change: 0.66 (threshold: 0.8)
18:56:48.814                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 34s/25s)
18:56:48.814                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:56:49.714 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
18:56:49.715 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #14)
18:56:49.715                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:56:49.715                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbq1rk1/pp3pp1/5n1p/2Pp4/5B2/2PBPN2/P4PPP/R2Q1RK1 b...
18:56:49.715                                                             🎲 Candidate moves for AI analysis: [b7b6]
18:56:49.715 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:56:49.716                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:56:49.891                                                             📋 Response ID: resp_68544f61aaec8191ada012e0885d474f0bf93623d7a60851
18:56:53.748                                                             🏁 Response completed
18:56:53.749 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["b6", "Nc6"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "My style thrive...
18:56:53.750                                                             ✅ Parsed AI advice: 0 moves, weight=0.7
18:56:53.750                                                             🎭 AI preferred moves: []
18:56:53.750                                                             💭 AI reasoning: My style thrives on dynamic and unbalanced positions, often seeking to create complications and provoke weaknesses in my opponent's setup. The move b6 aims to develop the queenside bishop, which could potentially exert pressure on the c5 pawn and strengthen Black's position. Nc6 is another move that develops a piece with tempo, adding pressure to the center.
18:56:53.795 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b7b6
18:56:54.156 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/p4pp1/1p3n1p/2Pp4/5B2/2PBPN2/P4PPP/R2Q1RK1 w - - 0 13
18:56:54.162                                                             📜 Move history updated: 24 moves
18:56:54.162 GameHistoryManager                                          Move added: b7b6
18:56:54.162 GameViewModel                                               🔍 Requesting position evaluation...
18:56:54.162                                                             🎭 Updating personality context for move: b7b6
18:56:54.162                                                             ✨ Personality context updated for move b7b6 - This is revolutionary!
18:56:54.162                                                             🔍 Checking game end conditions...
18:56:54.264                                                             ✅ Game continues - no end condition detected
18:56:54.874                                                             ✅ Evaluation received: 1.85
18:56:54.977 CompetitiveModeActivity                                     📊 Evaluation updated: 1.85
18:56:54.978                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.85, emotionalManager: INITIALIZED
18:56:54.978                                                             🎯 Evaluation change: 2.78 (threshold: 0.8)
18:56:54.978                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 40s/25s)
18:56:54.978                                                             ✅ Emotional reaction triggered for evaluation: 1.85 (trigger: slight_disadvantage)
18:56:54.978 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
18:56:54.978                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:56:54.978                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:56:54.979                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
18:56:57.284                                                             ✅ Response generated: A small deficit is merely an invitation to create chaos. I will seek the beautiful combination that ...
18:56:57.284 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: A small deficit is merely an invitation to create chaos. I will seek the beautiful combination that turns the tables—chess is won by those who dare to dream in calculation.
18:56:57.287                                                             🎭 Updated emotion indicator: 🤔
18:56:57.287                                                             🗣️ Speaking master dialogue: A small deficit is merely an invitation to create chaos. I will seek the beautiful combination that turns the tables—chess is won by those who dare to dream in calculation.
18:56:57.287 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:56:57.287                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:56:57.287                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:56:57.287                                                             ✅ Usage context set to: competitive_mode
18:56:57.287 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:56:57.287 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
18:56:57.287                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
18:56:57.288 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
18:57:08.540                                                             🗣️ Master dialogue speech completed
18:57:24.178                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=0
18:57:24.178                                                             🎯 SQUARE TAPPED: row=7, col=0
18:57:24.178                                                             📝 Player color: white
18:57:24.179                                                             🔍 Selected row/col: -1/-1
18:57:24.534                                                             🎯 Selected piece: R at 7, 0
18:57:24.802                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=1
18:57:24.802                                                             🎯 SQUARE TAPPED: row=7, col=1
18:57:24.802                                                             📝 Player color: white
18:57:24.802                                                             🔍 Selected row/col: 7/0
18:57:24.802                                                             🎯 ATTEMPTING MOVE: a1b1
18:57:24.802                                                             📝 Player color: white
18:57:24.802                                                             🔄 Is player's turn: true
18:57:24.802                                                             🔄 Is white's turn: true
18:57:24.802                                                             ✅ Turn validation passed, making move: a1b1
18:57:24.802 GameViewModel                                               🎯 makePlayerMove called with: a1b1
18:57:24.853                                                             🔍 Validating move: a1b1 (attempt 1)
18:57:24.953                                                             📋 Current position: rnbq1rk1/p4pp1/1p3n1p/2Pp4/5B2/2PBPN2/P4PPP/R2Q1RK1 w - - 0 13
18:57:25.005                                                             ⚖️ Move a1b1 legality check: LEGAL
18:57:25.005                                                             ✅ Executing validated move: a1b1
18:57:25.208                                                             📍 New position after move: rnbq1rk1/p4pp1/1p3n1p/2Pp4/5B2/2PBPN2/P4PPP/1R1Q1RK1 b - - 1 13
18:57:25.210 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/p4pp1/1p3n1p/2Pp4/5B2/2PBPN2/P4PPP/1R1Q1RK1 b - - 1 13
18:57:25.215                                                             📜 Move history updated: 25 moves
18:57:25.215 GameHistoryManager                                          Move added: a1b1
18:57:25.313 GameViewModel                                               🔍 Requesting position evaluation...
18:57:25.313                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:57:25.313                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:57:25.313                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:57:25.314                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:57:25.314                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:57:25.314                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:57:25.314                                                             🔧 gameRepository class: GameRepository
18:57:25.314                                                             🔧 Current thread: main
18:57:25.314 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:57:26.020 GameViewModel                                               ✅ Evaluation received: -1.72
18:57:26.128 CompetitiveModeActivity                                     📊 Evaluation updated: -1.72
18:57:26.128                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.72, emotionalManager: INITIALIZED
18:57:26.128                                                             🎯 Evaluation change: 3.5700002 (threshold: 0.8)
18:57:26.128                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 31s/25s)
18:57:26.128                                                             ✅ Emotional reaction triggered for evaluation: -1.72 (trigger: slight_advantage)
18:57:26.129 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
18:57:26.129                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:57:26.129                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:57:26.129                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
18:57:27.080 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
18:57:27.081 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #15)
18:57:27.081                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:57:27.081                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbq1rk1/p4pp1/1p3n1p/2Pp4/5B2/2PBPN2/P4PPP/1R1Q1RK1...
18:57:27.082                                                             🎲 Candidate moves for AI analysis: [f6e4]
18:57:27.082 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:57:27.082                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:57:27.234 OpenAIService                                               ✅ Response generated: The initiative is mine, but the fight has just begun. I will seek out the deepest combinations—true ...
18:57:27.234 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The initiative is mine, but the fight has just begun. I will seek out the deepest combinations—true beauty reveals itself in struggle.
18:57:27.236                                                             🎭 Updated emotion indicator: 🙂
18:57:27.236                                                             🗣️ Speaking master dialogue: The initiative is mine, but the fight has just begun. I will seek out the deepest combinations—true beauty reveals itself in struggle.
18:57:27.236 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:57:27.236                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:57:27.236                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:57:27.236                                                             ✅ Usage context set to: competitive_mode
18:57:27.236 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:57:27.236 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
18:57:27.237                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
18:57:27.238 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
18:57:27.289 ResponsesAPI                                                📋 Response ID: resp_68544f870ea8819fb6629c63b8049c060dd17873327ce0a6
18:57:30.037                                                             🏁 Response completed
18:57:30.038 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["f6e4", "b8c6"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I favor dyna...
18:57:30.038                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:57:30.038                                                             🎭 AI preferred moves: [f6e4]
18:57:30.038                                                             💭 AI reasoning: I favor dynamic and aggressive play, seeking to challenge the opponent with tactical complexities. The move f6e4 opens lines and invites sharp play.
18:57:30.084 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f6e4
18:57:30.443 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/p4pp1/1p5p/2Pp4/4nB2/2PBPN2/P4PPP/1R1Q1RK1 w - - 2 14
18:57:30.450                                                             📜 Move history updated: 26 moves
18:57:30.450 GameHistoryManager                                          Move added: f6e4
18:57:30.450 GameViewModel                                               🔍 Requesting position evaluation...
18:57:30.450                                                             🎭 Updating personality context for move: f6e4
18:57:30.450                                                             ✨ Personality context updated for move f6e4 - This is revolutionary!
18:57:30.451                                                             🔍 Checking game end conditions...
18:57:30.553                                                             ✅ Game continues - no end condition detected
18:57:31.160                                                             ✅ Evaluation received: 1.79
18:57:31.263 CompetitiveModeActivity                                     📊 Evaluation updated: 1.79
18:57:31.263                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 1.79, emotionalManager: INITIALIZED
18:57:31.263                                                             🎯 Evaluation change: 3.51 (threshold: 0.8)
18:57:31.263                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 5s/25s)
18:57:31.263                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
18:57:35.456                                                             🗣️ Master dialogue speech completed
18:58:01.967                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=3
18:58:01.967                                                             🎯 SQUARE TAPPED: row=5, col=3
18:58:01.967                                                             📝 Player color: white
18:58:01.967                                                             🔍 Selected row/col: -1/-1
18:58:02.321                                                             🎯 Selected piece: B at 5, 3
18:58:02.498                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=4
18:58:02.498                                                             🎯 SQUARE TAPPED: row=4, col=4
18:58:02.498                                                             📝 Player color: white
18:58:02.498                                                             🔍 Selected row/col: 5/3
18:58:02.500                                                             🎯 ATTEMPTING MOVE: d3e4
18:58:02.500                                                             📝 Player color: white
18:58:02.500                                                             🔄 Is player's turn: true
18:58:02.500                                                             🔄 Is white's turn: true
18:58:02.500                                                             ✅ Turn validation passed, making move: d3e4
18:58:02.500 GameViewModel                                               🎯 makePlayerMove called with: d3e4
18:58:02.551                                                             🔍 Validating move: d3e4 (attempt 1)
18:58:02.651                                                             📋 Current position: rnbq1rk1/p4pp1/1p5p/2Pp4/4nB2/2PBPN2/P4PPP/1R1Q1RK1 w - - 2 14
18:58:02.702                                                             ⚖️ Move d3e4 legality check: LEGAL
18:58:02.702                                                             ✅ Executing validated move: d3e4
18:58:02.905                                                             📍 New position after move: rnbq1rk1/p4pp1/1p5p/2Pp4/4BB2/2P1PN2/P4PPP/1R1Q1RK1 b - - 0 14
18:58:02.907 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/p4pp1/1p5p/2Pp4/4BB2/2P1PN2/P4PPP/1R1Q1RK1 b - - 0 14
18:58:02.913                                                             📜 Move history updated: 27 moves
18:58:02.913 GameHistoryManager                                          Move added: d3e4
18:58:02.914 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
18:58:03.010 GameViewModel                                               🔍 Requesting position evaluation...
18:58:03.010                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:58:03.010                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:58:03.010                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:58:03.010                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:58:03.010                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:58:03.010                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:58:03.010                                                             🔧 gameRepository class: GameRepository
18:58:03.010                                                             🔧 Current thread: main
18:58:03.010 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:58:03.464 GameViewModel                                               ✅ Evaluation received: -1.94
18:58:03.565 CompetitiveModeActivity                                     📊 Evaluation updated: -1.94
18:58:03.565                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.94, emotionalManager: INITIALIZED
18:58:03.565                                                             🎯 Evaluation change: 0.22000003 (threshold: 0.8)
18:58:03.565                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 37s/25s)
18:58:03.565                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
18:58:04.423 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
18:58:04.425 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #16)
18:58:04.425                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:58:04.425                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbq1rk1/p4pp1/1p5p/2Pp4/4BB2/2P1PN2/P4PPP/1R1Q1RK1 ...
18:58:04.425                                                             🎲 Candidate moves for AI analysis: [d5e4]
18:58:04.425 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:58:04.425                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:58:04.900                                                             📋 Response ID: resp_68544fac9ac881a181e96f33c598b404019ca6d4fef2079f
18:58:06.850                                                             🏁 Response completed
18:58:06.851 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["d5e4"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "The move d5e4 opens ...
18:58:06.852                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:58:06.852                                                             🎭 AI preferred moves: [d5e4]
18:58:06.852                                                             💭 AI reasoning: The move d5e4 opens lines for an aggressive counterattack, a hallmark of my dynamic and tactical style. It challenges White's central control and aims to create imbalances.
18:58:06.906 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d5e4
18:58:07.263 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/p4pp1/1p5p/2P5/4pB2/2P1PN2/P4PPP/1R1Q1RK1 w - - 0 15
18:58:07.269                                                             📜 Move history updated: 28 moves
18:58:07.270 GameHistoryManager                                          Move added: d5e4
18:58:07.270 GameViewModel                                               🔍 Requesting position evaluation...
18:58:07.270                                                             🎭 Updating personality context for move: d5e4
18:58:07.270                                                             ✨ Personality context updated for move d5e4 - This is revolutionary!
18:58:07.270                                                             🔍 Checking game end conditions...
18:58:07.372                                                             ✅ Game continues - no end condition detected
18:58:07.879                                                             ✅ Evaluation received: 2.07
18:58:07.982 CompetitiveModeActivity                                     📊 Evaluation updated: 2.07
18:58:07.982                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.07, emotionalManager: INITIALIZED
18:58:07.982                                                             🎯 Evaluation change: 3.79 (threshold: 0.8)
18:58:07.983                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 41s/25s)
18:58:07.983                                                             ✅ Emotional reaction triggered for evaluation: 2.07 (trigger: slight_disadvantage)
18:58:07.983 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
18:58:07.983                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:58:07.983                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:58:07.984                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
18:58:10.020                                                             ✅ Response generated: A small deficit is merely an invitation to create beauty. I will weave complications and seek the pe...
18:58:10.020 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: A small deficit is merely an invitation to create beauty. I will weave complications and seek the perfect moment to strike—chess rewards the bold, not the cautious.
18:58:10.023                                                             🎭 Updated emotion indicator: 🤔
18:58:10.024                                                             🗣️ Speaking master dialogue: A small deficit is merely an invitation to create beauty. I will weave complications and seek the perfect moment to strike—chess rewards the bold, not the cautious.
18:58:10.024 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:58:10.024                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:58:10.024                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:58:10.024                                                             ✅ Usage context set to: competitive_mode
18:58:10.024 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:58:10.024 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
18:58:10.025                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
18:58:10.027 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
18:58:20.442                                                             🗣️ Master dialogue speech completed
18:58:45.780                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
18:58:45.780                                                             🎯 SQUARE TAPPED: row=4, col=5
18:58:45.780                                                             📝 Player color: white
18:58:45.780                                                             🔍 Selected row/col: -1/-1
18:58:46.141                                                             🎯 Selected piece: B at 4, 5
18:58:46.408                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=3
18:58:46.409                                                             🎯 SQUARE TAPPED: row=2, col=3
18:58:46.409                                                             📝 Player color: white
18:58:46.409                                                             🔍 Selected row/col: 4/5
18:58:46.409                                                             🎯 ATTEMPTING MOVE: f4d6
18:58:46.409                                                             📝 Player color: white
18:58:46.409                                                             🔄 Is player's turn: true
18:58:46.409                                                             🔄 Is white's turn: true
18:58:46.409                                                             ✅ Turn validation passed, making move: f4d6
18:58:46.409 GameViewModel                                               🎯 makePlayerMove called with: f4d6
18:58:46.460                                                             🔍 Validating move: f4d6 (attempt 1)
18:58:46.561                                                             📋 Current position: rnbq1rk1/p4pp1/1p5p/2P5/4pB2/2P1PN2/P4PPP/1R1Q1RK1 w - - 0 15
18:58:46.613                                                             ⚖️ Move f4d6 legality check: LEGAL
18:58:46.613                                                             ✅ Executing validated move: f4d6
18:58:46.817                                                             📍 New position after move: rnbq1rk1/p4pp1/1p1B3p/2P5/4p3/2P1PN2/P4PPP/1R1Q1RK1 b - - 1 15
18:58:46.820 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbq1rk1/p4pp1/1p1B3p/2P5/4p3/2P1PN2/P4PPP/1R1Q1RK1 b - - 1 15
18:58:46.825                                                             📜 Move history updated: 29 moves
18:58:46.825 GameHistoryManager                                          Move added: f4d6
18:58:46.925 GameViewModel                                               🔍 Requesting position evaluation...
18:58:46.925                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:58:46.925                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:58:46.925                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:58:46.925                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:58:46.925                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:58:46.926                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:58:46.926                                                             🔧 gameRepository class: GameRepository
18:58:46.926                                                             🔧 Current thread: main
18:58:46.926 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:58:47.634 GameViewModel                                               ✅ Evaluation received: -1.63
18:58:47.742 CompetitiveModeActivity                                     📊 Evaluation updated: -1.63
18:58:47.742                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.63, emotionalManager: INITIALIZED
18:58:47.742                                                             🎯 Evaluation change: 3.6999998 (threshold: 0.8)
18:58:47.742                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 39s/25s)
18:58:47.742                                                             ✅ Emotional reaction triggered for evaluation: -1.63 (trigger: slight_advantage)
18:58:47.742 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
18:58:47.742                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:58:47.742                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:58:47.742                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
18:58:48.594                                                             ✅ Response generated: The position sings with latent energy—one precise combination will reveal its true beauty. My lead i...
18:58:48.594 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position sings with latent energy—one precise combination will reveal its true beauty. My lead is not just a number, but a promise of artistry to come.
18:58:48.596                                                             🎭 Updated emotion indicator: 🙂
18:58:48.596                                                             🗣️ Speaking master dialogue: The position sings with latent energy—one precise combination will reveal its true beauty. My lead is not just a number, but a promise of artistry to come.
18:58:48.596 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:58:48.596                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:58:48.597                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:58:48.597                                                             ✅ Usage context set to: competitive_mode
18:58:48.597 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:58:48.597 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
18:58:48.597                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
18:58:48.598 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
18:58:48.686 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
18:58:48.687 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #17)
18:58:48.687                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:58:48.687                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rnbq1rk1/p4pp1/1p1B3p/2P5/4p3/2P1PN2/P4PPP/1R1Q1RK1 ...
18:58:48.687                                                             🎲 Candidate moves for AI analysis: [c8a6]
18:58:48.688 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:58:48.688                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:58:48.921                                                             📋 Response ID: resp_68544fd8a8d4819fbe06ac4bb1b956630a376a569e37132a
18:58:51.833                                                             🏁 Response completed
18:58:51.833 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["c8a6"],
                                                                           "style_weight": 0.9,
                                                                           "reasoning": "In this dynamic posi...
18:58:51.834                                                             ✅ Parsed AI advice: 1 moves, weight=0.9
18:58:51.834                                                             🎭 AI preferred moves: [c8a6]
18:58:51.834                                                             💭 AI reasoning: In this dynamic position, I prefer to create complexities that can lead to tactical opportunities. The move c8a6 is aimed at developing the knight and potentially supporting the e4 pawn, while preparing to challenge White's central dominance.
18:58:51.885 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c8a6
18:58:52.244 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1rk1/p4pp1/bp1B3p/2P5/4p3/2P1PN2/P4PPP/1R1Q1RK1 w - - 2 16
18:58:52.252                                                             📜 Move history updated: 30 moves
18:58:52.252 GameHistoryManager                                          Move added: c8a6
18:58:52.252 GameViewModel                                               🔍 Requesting position evaluation...
18:58:52.252                                                             🎭 Updating personality context for move: c8a6
18:58:52.253                                                             ✨ Personality context updated for move c8a6 - This is revolutionary!
18:58:52.253                                                             🔍 Checking game end conditions...
18:58:52.354                                                             ✅ Game continues - no end condition detected
18:58:52.913                                                             ✅ Evaluation received: 2.20
18:58:53.016 CompetitiveModeActivity                                     📊 Evaluation updated: 2.2
18:58:53.016                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 2.2, emotionalManager: INITIALIZED
18:58:53.016                                                             🎯 Evaluation change: 3.83 (threshold: 0.8)
18:58:53.016                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 5s/25s)
18:58:53.016                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
18:58:58.247                                                             🗣️ Master dialogue speech completed
18:59:07.009                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=3
18:59:07.009                                                             🎯 SQUARE TAPPED: row=2, col=3
18:59:07.009                                                             📝 Player color: white
18:59:07.009                                                             🔍 Selected row/col: -1/-1
18:59:07.367                                                             🎯 Selected piece: B at 2, 3
18:59:07.606                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=5
18:59:07.606                                                             🎯 SQUARE TAPPED: row=0, col=5
18:59:07.606                                                             📝 Player color: white
18:59:07.607                                                             🔍 Selected row/col: 2/3
18:59:07.607                                                             🎯 ATTEMPTING MOVE: d6f8
18:59:07.607                                                             📝 Player color: white
18:59:07.607                                                             🔄 Is player's turn: true
18:59:07.607                                                             🔄 Is white's turn: true
18:59:07.607                                                             ✅ Turn validation passed, making move: d6f8
18:59:07.607 GameViewModel                                               🎯 makePlayerMove called with: d6f8
18:59:07.658                                                             🔍 Validating move: d6f8 (attempt 1)
18:59:07.759                                                             📋 Current position: rn1q1rk1/p4pp1/bp1B3p/2P5/4p3/2P1PN2/P4PPP/1R1Q1RK1 w - - 2 16
18:59:07.811                                                             ⚖️ Move d6f8 legality check: LEGAL
18:59:07.811                                                             ✅ Executing validated move: d6f8
18:59:08.014                                                             📍 New position after move: rn1q1Bk1/p4pp1/bp5p/2P5/4p3/2P1PN2/P4PPP/1R1Q1RK1 b - - 0 16
18:59:08.017 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1Bk1/p4pp1/bp5p/2P5/4p3/2P1PN2/P4PPP/1R1Q1RK1 b - - 0 16
18:59:08.022                                                             📜 Move history updated: 31 moves
18:59:08.022 GameHistoryManager                                          Move added: d6f8
18:59:08.117 GameViewModel                                               🔍 Requesting position evaluation...
18:59:08.117                                                             🎭 Using PERSONALITY ENGINE for move calculation!
18:59:08.117                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
18:59:08.117                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
18:59:08.117                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
18:59:08.117                                                             🔧 DEBUG: gameRepository instance = NOT NULL
18:59:08.117                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
18:59:08.117                                                             🔧 gameRepository class: GameRepository
18:59:08.117                                                             🔧 Current thread: main
18:59:08.118 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
18:59:08.571 GameViewModel                                               ✅ Evaluation received: -2.09
18:59:08.672 CompetitiveModeActivity                                     📊 Evaluation updated: -2.09
18:59:08.672                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.09, emotionalManager: INITIALIZED
18:59:08.672                                                             🎯 Evaluation change: 0.45999992 (threshold: 0.8)
18:59:08.672                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 20s/25s)
18:59:08.672                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
18:59:09.553 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
18:59:09.554 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #18)
18:59:09.554                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:59:09.554                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rn1q1Bk1/p4pp1/bp5p/2P5/4p3/2P1PN2/P4PPP/1R1Q1RK1 b ...
18:59:09.554                                                             🎲 Candidate moves for AI analysis: [a6d3]
18:59:09.555 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
18:59:09.555                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
18:59:09.752                                                             📋 Response ID: resp_68544fed84c0819eb67b2038d91f6a2e04feab9fdb531360
18:59:14.610 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a6d3
18:59:14.970 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q1Bk1/p4pp1/1p5p/2P5/4p3/2PbPN2/P4PPP/1R1Q1RK1 w - - 1 17
18:59:14.977                                                             📜 Move history updated: 32 moves
18:59:14.978 GameHistoryManager                                          Move added: a6d3
18:59:14.978 GameViewModel                                               🔍 Requesting position evaluation...
18:59:14.978                                                             🎭 Updating personality context for move: a6d3
18:59:14.978                                                             ✨ Personality context updated for move a6d3 - This is revolutionary!
18:59:14.978                                                             🔍 Checking game end conditions...
18:59:15.079                                                             ✅ Game continues - no end condition detected
18:59:15.388                                                             ✅ Evaluation received: 4.54
18:59:15.490 CompetitiveModeActivity                                     📊 Evaluation updated: 4.54
18:59:15.490                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 4.54, emotionalManager: INITIALIZED
18:59:15.490                                                             🎯 Evaluation change: 6.17 (threshold: 0.8)
18:59:15.490                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 27s/25s)
18:59:15.490                                                             ✅ Emotional reaction triggered for evaluation: 4.54 (trigger: significant_disadvantage)
18:59:15.491 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
18:59:15.491                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:59:15.491                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:59:15.492                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
18:59:15.889 ResponsesAPI                                                🏁 Response completed
18:59:15.890 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["a6d3", "e4f3"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "The move a6d...
18:59:15.890                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
18:59:15.890                                                             🎭 AI preferred moves: [a6d3]
18:59:15.890                                                             💭 AI reasoning: The move a6d3 reflects my aggressive style by threatening the queen and gaining control over the center. e4f3 is a tactical shot that disrupts White's pawn structure and prepares an attack.
18:59:16.846 OpenAIService                                               ✅ Response generated: Difficulty is the raw material of brilliance. I will weave a web of complications—my opponent may fi...
18:59:16.846 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: Difficulty is the raw material of brilliance. I will weave a web of complications—my opponent may find the path, but I will make it as twisted as possible.
18:59:16.851                                                             🎭 Updated emotion indicator: 😤
18:59:16.851                                                             🗣️ Speaking master dialogue: Difficulty is the raw material of brilliance. I will weave a web of complications—my opponent may find the path, but I will make it as twisted as possible.
18:59:16.851 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
18:59:16.851                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
18:59:16.852                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
18:59:16.852                                                             ✅ Usage context set to: competitive_mode
18:59:16.852 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
18:59:16.852 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
18:59:16.852                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
18:59:16.855 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
18:59:25.642                                                             🗣️ Master dialogue speech completed
19:00:16.831                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=5
19:00:16.831                                                             🎯 SQUARE TAPPED: row=0, col=5
19:00:16.831                                                             📝 Player color: white
19:00:16.831                                                             🔍 Selected row/col: -1/-1
19:00:17.188                                                             🎯 Selected piece: B at 0, 5
19:00:17.527                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=6
19:00:17.527                                                             🎯 SQUARE TAPPED: row=1, col=6
19:00:17.527                                                             📝 Player color: white
19:00:17.527                                                             🔍 Selected row/col: 0/5
19:00:17.528                                                             🎯 ATTEMPTING MOVE: f8g7
19:00:17.528                                                             📝 Player color: white
19:00:17.528                                                             🔄 Is player's turn: true
19:00:17.528                                                             🔄 Is white's turn: true
19:00:17.528                                                             ✅ Turn validation passed, making move: f8g7
19:00:17.528 GameViewModel                                               🎯 makePlayerMove called with: f8g7
19:00:17.578                                                             🔍 Validating move: f8g7 (attempt 1)
19:00:17.679                                                             📋 Current position: rn1q1Bk1/p4pp1/1p5p/2P5/4p3/2PbPN2/P4PPP/1R1Q1RK1 w - - 1 17
19:00:17.730                                                             ⚖️ Move f8g7 legality check: LEGAL
19:00:17.730                                                             ✅ Executing validated move: f8g7
19:00:17.933                                                             📍 New position after move: rn1q2k1/p4pB1/1p5p/2P5/4p3/2PbPN2/P4PPP/1R1Q1RK1 b - - 0 17
19:00:17.936 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1q2k1/p4pB1/1p5p/2P5/4p3/2PbPN2/P4PPP/1R1Q1RK1 b - - 0 17
19:00:17.942                                                             📜 Move history updated: 33 moves
19:00:17.942 GameHistoryManager                                          Move added: f8g7
19:00:18.038 GameViewModel                                               🔍 Requesting position evaluation...
19:00:18.039                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:00:18.039                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:00:18.039                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:00:18.039                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:00:18.039                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:00:18.039                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:00:18.039                                                             🔧 gameRepository class: GameRepository
19:00:18.039                                                             🔧 Current thread: main
19:00:18.039 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:00:18.492 GameViewModel                                               ✅ Evaluation received: -4.53
19:00:18.592 CompetitiveModeActivity                                     📊 Evaluation updated: -4.53
19:00:18.592                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.53, emotionalManager: INITIALIZED
19:00:18.592                                                             🎯 Evaluation change: 9.07 (threshold: 0.8)
19:00:18.592                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 63s/25s)
19:00:18.592                                                             ✅ Emotional reaction triggered for evaluation: -4.53 (trigger: significant_advantage)
19:00:18.592 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
19:00:18.592                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:00:18.592                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:00:18.593                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
19:00:19.503 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
19:00:19.504 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #19)
19:00:19.504                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
19:00:19.504                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): rn1q2k1/p4pB1/1p5p/2P5/4p3/2PbPN2/P4PPP/1R1Q1RK1 b -...
19:00:19.504                                                             🎲 Candidate moves for AI analysis: [b8c6]
19:00:19.505 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
19:00:19.505                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
19:00:19.623 OpenAIService                                               ✅ Response generated: The position sings with possibilities—my pieces are a harmonious orchestra, and soon I shall unleash...
19:00:19.623 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position sings with possibilities—my pieces are a harmonious orchestra, and soon I shall unleash a dazzling combination.
19:00:19.625                                                             🎭 Updated emotion indicator: 😏
19:00:19.625                                                             🗣️ Speaking master dialogue: The position sings with possibilities—my pieces are a harmonious orchestra, and soon I shall unleash a dazzling combination.
19:00:19.625 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
19:00:19.625                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
19:00:19.625                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
19:00:19.625                                                             ✅ Usage context set to: competitive_mode
19:00:19.625 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:00:19.625 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
19:00:19.625                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
19:00:19.626 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
19:00:19.924 ResponsesAPI                                                📋 Response ID: resp_68545033761c819ea789e9d7cb6c14a606d95d4ef057e7b3
19:00:22.632                                                             🏁 Response completed
19:00:22.632 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["c6"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "The move Nc6 reactivat...
19:00:22.633                                                             ✅ Parsed AI advice: 0 moves, weight=0.7
19:00:22.633                                                             🎭 AI preferred moves: []
19:00:22.633                                                             💭 AI reasoning: The move Nc6 reactivates the knight, bringing it closer to the center and offering potential pressure on the d4 pawn. It helps in consolidating the position and preparing a counterattack.
19:00:22.675 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8c6
19:00:23.034 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2q2k1/p4pB1/1pn4p/2P5/4p3/2PbPN2/P4PPP/1R1Q1RK1 w - - 1 18
19:00:23.042                                                             📜 Move history updated: 34 moves
19:00:23.042 GameHistoryManager                                          Move added: b8c6
19:00:23.042 GameViewModel                                               🔍 Requesting position evaluation...
19:00:23.042                                                             🎭 Updating personality context for move: b8c6
19:00:23.042                                                             ✨ Personality context updated for move b8c6 - This is revolutionary!
19:00:23.042                                                             🔍 Checking game end conditions...
19:00:23.144                                                             ✅ Game continues - no end condition detected
19:00:23.400                                                             ✅ Evaluation received: 4.88
19:00:23.502 CompetitiveModeActivity                                     📊 Evaluation updated: 4.88
19:00:23.502                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 4.88, emotionalManager: INITIALIZED
19:00:23.502                                                             🎯 Evaluation change: 9.41 (threshold: 0.8)
19:00:23.502                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 4s/25s)
19:00:23.502                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:00:27.277                                                             🗣️ Master dialogue speech completed
19:00:54.267                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=5
19:00:54.267                                                             🎯 SQUARE TAPPED: row=5, col=5
19:00:54.267                                                             📝 Player color: white
19:00:54.267                                                             🔍 Selected row/col: -1/-1
19:00:54.625                                                             🎯 Selected piece: N at 5, 5
19:00:55.004                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
19:00:55.005                                                             🎯 SQUARE TAPPED: row=3, col=4
19:00:55.005                                                             📝 Player color: white
19:00:55.005                                                             🔍 Selected row/col: 5/5
19:00:55.005                                                             🎯 ATTEMPTING MOVE: f3e5
19:00:55.005                                                             📝 Player color: white
19:00:55.005                                                             🔄 Is player's turn: true
19:00:55.005                                                             🔄 Is white's turn: true
19:00:55.005                                                             ✅ Turn validation passed, making move: f3e5
19:00:55.005 GameViewModel                                               🎯 makePlayerMove called with: f3e5
19:00:55.056                                                             🔍 Validating move: f3e5 (attempt 1)
19:00:55.158                                                             📋 Current position: r2q2k1/p4pB1/1pn4p/2P5/4p3/2PbPN2/P4PPP/1R1Q1RK1 w - - 1 18
19:00:55.209                                                             ⚖️ Move f3e5 legality check: LEGAL
19:00:55.210                                                             ✅ Executing validated move: f3e5
19:00:55.413                                                             📍 New position after move: r2q2k1/p4pB1/1pn4p/2P1N3/4p3/2PbP3/P4PPP/1R1Q1RK1 b - - 2 18
19:00:55.416 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2q2k1/p4pB1/1pn4p/2P1N3/4p3/2PbP3/P4PPP/1R1Q1RK1 b - - 2 18
19:00:55.423                                                             📜 Move history updated: 35 moves
19:00:55.423 GameHistoryManager                                          Move added: f3e5
19:00:55.518 GameViewModel                                               🔍 Requesting position evaluation...
19:00:55.518                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:00:55.518                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:00:55.518                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:00:55.518                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:00:55.518                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:00:55.518                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:00:55.518                                                             🔧 gameRepository class: GameRepository
19:00:55.518                                                             🔧 Current thread: main
19:00:55.518 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:00:55.870 GameViewModel                                               ✅ Evaluation received: -4.74
19:00:55.971 CompetitiveModeActivity                                     📊 Evaluation updated: -4.74
19:00:55.971                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.74, emotionalManager: INITIALIZED
19:00:55.971                                                             🎯 Evaluation change: 0.20999956 (threshold: 0.8)
19:00:55.972                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 37s/25s)
19:00:55.972                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
19:00:56.829 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
19:00:56.831 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #20)
19:00:56.831                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
19:00:56.831                                                             📝 Prompt preview: CHESS POSITION ANALYSIS REQUEST
                                                                         Position (FEN): r2q2k1/p4pB1/1pn4p/2P1N3/4p3/2PbP3/P4PPP/1R1Q1RK1 b ...
19:00:56.831                                                             🎲 Candidate moves for AI analysis: [d3f1]
19:00:56.831 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
19:00:56.831                                                             🧪 TESTING: Using base model instead of fine-tuned for asst_wnshRkbnaca2vkRxYqYZDcLu
19:00:57.060                                                             📋 Response ID: resp_68545058d038819fae46e3bb907f260a0e3b1dad60bfe366
19:01:01.887 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d3f1
19:01:02.245 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2q2k1/p4pB1/1pn4p/2P1N3/4p3/2P1P3/P4PPP/1R1Q1bK1 w - - 0 19
19:01:02.248                                                             📜 Move history updated: 36 moves
19:01:02.248 GameHistoryManager                                          Move added: d3f1
19:01:02.248 GameViewModel                                               🔍 Requesting position evaluation...
19:01:02.248                                                             🎭 Updating personality context for move: d3f1
19:01:02.248                                                             ✨ Personality context updated for move d3f1 - This is revolutionary!
19:01:02.249                                                             🔍 Checking game end conditions...
19:01:02.351                                                             ✅ Game continues - no end condition detected
19:01:02.555                                                             ✅ Evaluation received: 5.33
19:01:02.658 CompetitiveModeActivity                                     📊 Evaluation updated: 5.33
19:01:02.658                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 5.33, emotionalManager: INITIALIZED
19:01:02.659                                                             🎯 Evaluation change: 9.860001 (threshold: 0.8)
19:01:02.659                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 44s/25s)
19:01:02.659                                                             ✅ Emotional reaction triggered for evaluation: 5.33 (trigger: significant_disadvantage)
19:01:02.660 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
19:01:02.660                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:01:02.660                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:01:02.661                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
19:01:04.331                                                             ✅ Response generated: The position is a tempest, but within chaos lies a hidden melody. I will seek the harmony of my piec...
19:01:04.331 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The position is a tempest, but within chaos lies a hidden melody. I will seek the harmony of my pieces and strike with a daring combination—this is where true chess begins.
19:01:04.334                                                             🎭 Updated emotion indicator: 😤
19:01:04.334                                                             🗣️ Speaking master dialogue: The position is a tempest, but within chaos lies a hidden melody. I will seek the harmony of my pieces and strike with a daring combination—this is where true chess begins.
19:01:04.334 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
19:01:04.334                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
19:01:04.334                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
19:01:04.334                                                             ✅ Usage context set to: competitive_mode
19:01:04.334 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:01:04.334 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
19:01:04.334                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
19:01:04.336 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
19:01:04.720 ResponsesAPI                                                🏁 Response completed
19:01:04.721 AIStyleAdvisor                                              ✅ Received AI response for alekhine: ```json
                                                                         {
                                                                           "preferred_moves": ["d3f1"],
                                                                           "style_weight": 0.7,
                                                                           "reasoning": "I favor positions wh...
19:01:04.722                                                             ✅ Parsed AI advice: 1 moves, weight=0.7
19:01:04.722                                                             🎭 AI preferred moves: [d3f1]
19:01:04.722                                                             💭 AI reasoning: I favor positions where tactical opportunities abound. The move Bxf1 allows me to exchange a strong bishop and potentially disrupt White's king safety. It also opens lines for my pieces to create threats.
19:01:15.847 CompetitiveModeActivity                                     🗣️ Master dialogue speech completed
19:01:39.004                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=6
19:01:39.004                                                             🎯 SQUARE TAPPED: row=7, col=6
19:01:39.004                                                             📝 Player color: white
19:01:39.005                                                             🔍 Selected row/col: -1/-1
19:01:39.361                                                             🎯 Selected piece: K at 7, 6
19:01:39.569                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=5
19:01:39.569                                                             🎯 SQUARE TAPPED: row=7, col=5
19:01:39.569                                                             📝 Player color: white
19:01:39.569                                                             🔍 Selected row/col: 7/6
19:01:39.569                                                             🎯 ATTEMPTING MOVE: g1f1
19:01:39.569                                                             📝 Player color: white
19:01:39.569                                                             🔄 Is player's turn: true
19:01:39.569                                                             🔄 Is white's turn: true
19:01:39.569                                                             ✅ Turn validation passed, making move: g1f1
19:01:39.569 GameViewModel                                               🎯 makePlayerMove called with: g1f1
19:01:39.620                                                             🔍 Validating move: g1f1 (attempt 1)
19:01:39.721                                                             📋 Current position: r2q2k1/p4pB1/1pn4p/2P1N3/4p3/2P1P3/P4PPP/1R1Q1bK1 w - - 0 19
19:01:39.772                                                             ⚖️ Move g1f1 legality check: LEGAL
19:01:39.772                                                             ✅ Executing validated move: g1f1
19:01:39.974                                                             📍 New position after move: r2q2k1/p4pB1/1pn4p/2P1N3/4p3/2P1P3/P4PPP/1R1Q1K2 b - - 0 19
19:01:39.976 Choreographer                                               Skipped 48 frames!  The application may be doing too much work on its main thread.
19:01:39.978 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2q2k1/p4pB1/1pn4p/2P1N3/4p3/2P1P3/P4PPP/1R1Q1K2 b - - 0 19
19:01:39.984                                                             📜 Move history updated: 37 moves
19:01:39.984 GameHistoryManager                                          Move added: g1f1
19:01:40.076 GameViewModel                                               🔍 Requesting position evaluation...
19:01:40.077                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:01:40.077                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:01:40.077                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:01:40.077                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:01:40.077                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:01:40.077                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:01:40.077                                                             🔧 gameRepository class: GameRepository
19:01:40.077                                                             🔧 Current thread: main
19:01:40.077 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:01:40.429 GameViewModel                                               ✅ Evaluation received: -4.96
19:01:40.530 CompetitiveModeActivity                                     📊 Evaluation updated: -4.96
19:01:40.530                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.96, emotionalManager: INITIALIZED
19:01:40.530                                                             🎯 Evaluation change: 10.29 (threshold: 0.8)
19:01:40.530                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 37s/25s)
19:01:40.530                                                             ✅ Emotional reaction triggered for evaluation: -4.96 (trigger: significant_advantage)
19:01:40.530 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
19:01:40.530                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:01:40.530                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:01:40.530                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
19:01:41.440 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
19:01:41.441 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:01:41.474 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d8h4
19:01:41.756 OpenAIService                                               ✅ Response generated: The board sings its secrets to those who listen—my advantage is not just in material, but in the har...
19:01:41.834 CompetitiveModeActivity                                     🎯 Board updated with FEN: r5k1/p4pB1/1pn4p/2P1N3/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 1 20
19:01:41.841                                                             📜 Move history updated: 38 moves
19:01:41.841 GameHistoryManager                                          Move added: d8h4
19:01:41.841 GameViewModel                                               🔍 Requesting position evaluation...
19:01:41.841                                                             🎭 Updating personality context for move: d8h4
19:01:41.842                                                             ✨ Personality context updated for move d8h4 - This is revolutionary!
19:01:41.842                                                             🔍 Checking game end conditions...
19:01:41.943                                                             ✅ Game continues - no end condition detected
19:01:41.944 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings its secrets to those who listen—my advantage is not just in material, but in the harmony of my pieces. The final combination will be a work of art.
19:01:41.947                                                             🎭 Updated emotion indicator: 😏
19:01:41.947                                                             🗣️ Speaking master dialogue: The board sings its secrets to those who listen—my advantage is not just in material, but in the harmony of my pieces. The final combination will be a work of art.
19:01:41.947 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
19:01:41.947                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
19:01:41.948                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
19:01:41.948                                                             ✅ Usage context set to: competitive_mode
19:01:41.948 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:01:41.948 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
19:01:41.948                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
19:01:41.949 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
19:01:42.149 GameViewModel                                               ✅ Evaluation received: 5.40
19:01:42.252 CompetitiveModeActivity                                     📊 Evaluation updated: 5.4
19:01:42.252                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 5.4, emotionalManager: INITIALIZED
19:01:42.252                                                             🎯 Evaluation change: 10.360001 (threshold: 0.8)
19:01:42.252                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
19:01:42.252                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:01:53.064                                                             🗣️ Master dialogue speech completed
19:02:14.805                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
19:02:14.805                                                             🎯 SQUARE TAPPED: row=3, col=4
19:02:14.805                                                             📝 Player color: white
19:02:14.805                                                             🔍 Selected row/col: -1/-1
19:02:15.163                                                             🎯 Selected piece: N at 3, 4
19:02:15.597                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=2
19:02:15.597                                                             🎯 SQUARE TAPPED: row=2, col=2
19:02:15.597                                                             📝 Player color: white
19:02:15.597                                                             🔍 Selected row/col: 3/4
19:02:15.597                                                             🎯 ATTEMPTING MOVE: e5c6
19:02:15.597                                                             📝 Player color: white
19:02:15.597                                                             🔄 Is player's turn: true
19:02:15.597                                                             🔄 Is white's turn: true
19:02:15.597                                                             ✅ Turn validation passed, making move: e5c6
19:02:15.597 GameViewModel                                               🎯 makePlayerMove called with: e5c6
19:02:15.649                                                             🔍 Validating move: e5c6 (attempt 1)
19:02:15.750                                                             📋 Current position: r5k1/p4pB1/1pn4p/2P1N3/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 1 20
19:02:15.801                                                             ⚖️ Move e5c6 legality check: LEGAL
19:02:15.801                                                             ✅ Executing validated move: e5c6
19:02:16.003                                                             📍 New position after move: r5k1/p4pB1/1pN4p/2P5/4p2q/2P1P3/P4PPP/1R1Q1K2 b - - 0 20
19:02:16.005 CompetitiveModeActivity                                     🎯 Board updated with FEN: r5k1/p4pB1/1pN4p/2P5/4p2q/2P1P3/P4PPP/1R1Q1K2 b - - 0 20
19:02:16.009                                                             📜 Move history updated: 39 moves
19:02:16.009 GameHistoryManager                                          Move added: e5c6
19:02:16.105 GameViewModel                                               🔍 Requesting position evaluation...
19:02:16.106                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:02:16.106                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:02:16.106                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:02:16.106                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:02:16.106                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:02:16.106                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:02:16.106                                                             🔧 gameRepository class: GameRepository
19:02:16.106                                                             🔧 Current thread: main
19:02:16.106 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:02:16.559 GameViewModel                                               ✅ Evaluation received: -5.31
19:02:16.660 CompetitiveModeActivity                                     📊 Evaluation updated: -5.31
19:02:16.660                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.31, emotionalManager: INITIALIZED
19:02:16.660                                                             🎯 Evaluation change: 0.3499999 (threshold: 0.8)
19:02:16.660                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 36s/25s)
19:02:16.660                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
19:02:17.467 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 25 historical positions
19:02:17.468 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:02:17.501 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a7a5
19:02:17.859 CompetitiveModeActivity                                     🎯 Board updated with FEN: r5k1/5pB1/1pN4p/p1P5/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 0 21
19:02:17.865                                                             📜 Move history updated: 40 moves
19:02:17.865 GameHistoryManager                                          Move added: a7a5
19:02:17.865 GameViewModel                                               🔍 Requesting position evaluation...
19:02:17.865                                                             🎭 Updating personality context for move: a7a5
19:02:17.866                                                             ✨ Personality context updated for move a7a5 - This is revolutionary!
19:02:17.866                                                             🔍 Checking game end conditions...
19:02:17.966                                                             ✅ Game continues - no end condition detected
19:02:18.118                                                             ✅ Evaluation received: 7.31
19:02:18.220 CompetitiveModeActivity                                     📊 Evaluation updated: 7.31
19:02:18.220                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 7.31, emotionalManager: INITIALIZED
19:02:18.220                                                             🎯 Evaluation change: 12.27 (threshold: 0.8)
19:02:18.221                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 37s/25s)
19:02:18.221                                                             ✅ Emotional reaction triggered for evaluation: 7.31 (trigger: significant_disadvantage)
19:02:18.221 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
19:02:18.221                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:02:18.221                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:02:18.222                                                             🎯 Variety params applied: temp=0.7, penalties=0.55/0.3
19:02:19.296                                                             ✅ Response generated: Difficult? No, this is where the real music begins. My pieces may seem strained, but wait for the ha...
19:02:19.297 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: Difficult? No, this is where the real music begins. My pieces may seem strained, but wait for the harmony of the combination—therein lies my counterattack.
19:02:19.299                                                             🎭 Updated emotion indicator: 😤
19:02:19.299                                                             🗣️ Speaking master dialogue: Difficult? No, this is where the real music begins. My pieces may seem strained, but wait for the harmony of the combination—therein lies my counterattack.
19:02:19.299 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
19:02:19.299                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
19:02:19.299                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
19:02:19.299                                                             ✅ Usage context set to: competitive_mode
19:02:19.299 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:02:19.300 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
19:02:19.300                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
19:02:19.301 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
19:02:29.752                                                             🗣️ Master dialogue speech completed
19:03:00.900                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=2
19:03:00.900                                                             🎯 SQUARE TAPPED: row=3, col=2
19:03:00.900                                                             📝 Player color: white
19:03:00.900                                                             🔍 Selected row/col: -1/-1
19:03:01.258                                                             🎯 Selected piece: P at 3, 2
19:03:01.696                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=1
19:03:01.696                                                             🎯 SQUARE TAPPED: row=2, col=1
19:03:01.696                                                             📝 Player color: white
19:03:01.696                                                             🔍 Selected row/col: 3/2
19:03:01.696                                                             ♟️ Promotion check: piece=P, fromRow=3, toRow=2, reaches=false
19:03:01.696                                                             🎯 ATTEMPTING MOVE: c5b6
19:03:01.696                                                             📝 Player color: white
19:03:01.696                                                             🔄 Is player's turn: true
19:03:01.697                                                             🔄 Is white's turn: true
19:03:01.697                                                             ✅ Turn validation passed, making move: c5b6
19:03:01.697 GameViewModel                                               🎯 makePlayerMove called with: c5b6
19:03:01.748                                                             🔍 Validating move: c5b6 (attempt 1)
19:03:01.850                                                             📋 Current position: r5k1/5pB1/1pN4p/p1P5/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 0 21
19:03:01.901                                                             ⚖️ Move c5b6 legality check: LEGAL
19:03:01.901                                                             ✅ Executing validated move: c5b6
19:03:02.103                                                             📍 New position after move: r5k1/5pB1/1PN4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 b - - 0 21
19:03:02.105 CompetitiveModeActivity                                     🎯 Board updated with FEN: r5k1/5pB1/1PN4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 b - - 0 21
19:03:02.112                                                             📜 Move history updated: 41 moves
19:03:02.112 GameHistoryManager                                          Move added: c5b6
19:03:02.206 GameViewModel                                               🔍 Requesting position evaluation...
19:03:02.206                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:03:02.206                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:03:02.206                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:03:02.207                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:03:02.207                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:03:02.207                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:03:02.207                                                             🔧 gameRepository class: GameRepository
19:03:02.207                                                             🔧 Current thread: main
19:03:02.207 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:03:02.609 GameViewModel                                               ✅ Evaluation received: -7.11
19:03:02.710 CompetitiveModeActivity                                     📊 Evaluation updated: -7.11
19:03:02.710                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -7.11, emotionalManager: INITIALIZED
19:03:02.710                                                             🎯 Evaluation change: 14.42 (threshold: 0.8)
19:03:02.710                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 44s/25s)
19:03:02.710                                                             ✅ Emotional reaction triggered for evaluation: -7.11 (trigger: significant_advantage)
19:03:02.710 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
19:03:02.710                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:03:02.710                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:03:02.710                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
19:03:03.613 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
19:03:03.614 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:03:03.648 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8g7
19:03:03.677 OpenAIService                                               ✅ Response generated: The board sings in my favor—every piece is a note in a perfect harmony. Now, I shall compose a maste...
19:03:04.007 CompetitiveModeActivity                                     🎯 Board updated with FEN: r7/5pk1/1PN4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 0 22
19:03:04.015                                                             📜 Move history updated: 42 moves
19:03:04.015 GameHistoryManager                                          Move added: g8g7
19:03:04.015 GameViewModel                                               🔍 Requesting position evaluation...
19:03:04.015                                                             🎭 Updating personality context for move: g8g7
19:03:04.015                                                             ✨ Personality context updated for move g8g7 - This is revolutionary!
19:03:04.015                                                             🔍 Checking game end conditions...
19:03:04.117                                                             ✅ Game continues - no end condition detected
19:03:04.118 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings in my favor—every piece is a note in a perfect harmony. Now, I shall compose a masterpiece of combinations and finish with a flourish.
19:03:04.124                                                             🎭 Updated emotion indicator: 😏
19:03:04.124                                                             🗣️ Speaking master dialogue: The board sings in my favor—every piece is a note in a perfect harmony. Now, I shall compose a masterpiece of combinations and finish with a flourish.
19:03:04.124 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
19:03:04.124                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
19:03:04.124                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
19:03:04.124                                                             ✅ Usage context set to: competitive_mode
19:03:04.124 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:03:04.125 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
19:03:04.125                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
19:03:04.127 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
19:03:04.322 GameViewModel                                               ✅ Evaluation received: 7.43
19:03:04.425 CompetitiveModeActivity                                     📊 Evaluation updated: 7.43
19:03:04.426                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 7.43, emotionalManager: INITIALIZED
19:03:04.426                                                             🎯 Evaluation change: 14.54 (threshold: 0.8)
19:03:04.426                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
19:03:04.426                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:03:13.136                                                             🗣️ Master dialogue speech completed
19:03:17.040                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=1
19:03:17.040                                                             🎯 SQUARE TAPPED: row=2, col=1
19:03:17.040                                                             📝 Player color: white
19:03:17.040                                                             🔍 Selected row/col: -1/-1
19:03:17.396                                                             🎯 Selected piece: P at 2, 1
19:03:17.713                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=1
19:03:17.713                                                             🎯 SQUARE TAPPED: row=1, col=1
19:03:17.713                                                             📝 Player color: white
19:03:17.713                                                             🔍 Selected row/col: 2/1
19:03:17.713                                                             ♟️ Promotion check: piece=P, fromRow=2, toRow=1, reaches=false
19:03:17.713                                                             🎯 ATTEMPTING MOVE: b6b7
19:03:17.713                                                             📝 Player color: white
19:03:17.713                                                             🔄 Is player's turn: true
19:03:17.714                                                             🔄 Is white's turn: true
19:03:17.714                                                             ✅ Turn validation passed, making move: b6b7
19:03:17.714 GameViewModel                                               🎯 makePlayerMove called with: b6b7
19:03:17.765                                                             🔍 Validating move: b6b7 (attempt 1)
19:03:17.866                                                             📋 Current position: r7/5pk1/1PN4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 0 22
19:03:17.919                                                             ⚖️ Move b6b7 legality check: LEGAL
19:03:17.919                                                             ✅ Executing validated move: b6b7
19:03:18.123                                                             📍 New position after move: r7/1P3pk1/2N4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 b - - 0 22
19:03:18.126 CompetitiveModeActivity                                     🎯 Board updated with FEN: r7/1P3pk1/2N4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 b - - 0 22
19:03:18.134                                                             📜 Move history updated: 43 moves
19:03:18.135 GameHistoryManager                                          Move added: b6b7
19:03:18.229 GameViewModel                                               🔍 Requesting position evaluation...
19:03:18.229                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:03:18.229                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:03:18.229                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:03:18.229                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:03:18.229                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:03:18.229                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:03:18.229                                                             🔧 gameRepository class: GameRepository
19:03:18.229                                                             🔧 Current thread: main
19:03:18.229 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:03:18.531 GameViewModel                                               ✅ Evaluation received: -7.51
19:03:18.632 CompetitiveModeActivity                                     📊 Evaluation updated: -7.51
19:03:18.632                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -7.51, emotionalManager: INITIALIZED
19:03:18.632                                                             🎯 Evaluation change: 0.4000001 (threshold: 0.8)
19:03:18.632                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 15s/25s)
19:03:18.632                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
19:03:19.539 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
19:03:19.540 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:03:19.575 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a8g8
19:03:19.935 CompetitiveModeActivity                                     🎯 Board updated with FEN: 6r1/1P3pk1/2N4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 1 23
19:03:19.944                                                             📜 Move history updated: 44 moves
19:03:19.944 GameHistoryManager                                          Move added: a8g8
19:03:19.944 GameViewModel                                               🔍 Requesting position evaluation...
19:03:19.944                                                             🎭 Updating personality context for move: a8g8
19:03:19.944                                                             ✨ Personality context updated for move a8g8 - This is revolutionary!
19:03:19.944                                                             🔍 Checking game end conditions...
19:03:20.047                                                             ✅ Game continues - no end condition detected
19:03:20.247                                                             ✅ Evaluation received: 7.79
19:03:20.349 CompetitiveModeActivity                                     📊 Evaluation updated: 7.79
19:03:20.349                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 7.79, emotionalManager: INITIALIZED
19:03:20.349                                                             🎯 Evaluation change: 14.9 (threshold: 0.8)
19:03:20.349                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 17s/25s)
19:03:20.349                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:03:34.769                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=1
19:03:34.769                                                             🎯 SQUARE TAPPED: row=1, col=1
19:03:34.769                                                             📝 Player color: white
19:03:34.769                                                             🔍 Selected row/col: -1/-1
19:03:35.126                                                             🎯 Selected piece: P at 1, 1
19:03:35.275                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
19:03:35.275                                                             🎯 SQUARE TAPPED: row=0, col=1
19:03:35.275                                                             📝 Player color: white
19:03:35.275                                                             🔍 Selected row/col: 1/1
19:03:35.275                                                             ♟️ Promotion check: piece=P, fromRow=1, toRow=0, reaches=true
19:03:35.275                                                             ♟️ PAWN PROMOTION DETECTED: b7b8
19:03:35.275                                                             🔧 DEBUG: Creating promotion dialog for move: b7b8
19:03:35.278 Dialog                                                      mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
19:03:35.280 CompetitiveModeActivity                                     🔧 DEBUG: About to show promotion dialog
19:03:35.284 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@788137b
19:03:35.293 ScrollView                                                  initGoToTop
19:03:35.300 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{b4402e3 V.E...... R.....I. 0,0-0,0}[CompetitiveModeActivity], caller=android.view.WindowManagerImpl.addView:158 android.app.Dialog.show:511 com.example.chesspedagogue.CompetitiveModeActivity.showPromotionDialog:2789 
19:03:35.302 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
19:03:35.309 VRI[Compet...y]@36894e0                                     synced displayState. AttachInfo displayState=2
19:03:35.311                                                             setView = com.android.internal.policy.DecorView@b4402e3 IsHRR=false TM=true
19:03:35.311 CompetitiveModeActivity                                     🔧 DEBUG: Promotion dialog shown
19:03:35.322 BufferQueueProducer                                         [](id:1bca00000004,api:0,p:0,c:7114) setDequeueTimeout:2077252342
19:03:35.323 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
19:03:35.323                                                             Access denied finding property "vendor.gpp.create_frc_extension"
19:03:35.323 VRI[Compet...y]@36894e0                                     Relayout returned: old=(0,100,1440,2908) new=(36,1155,1404,1853) relayoutAsync=false req=(1368,698)0 dur=3 res=0x3 s={true 0xb4000070e038d800} ch=true seqId=0
19:03:35.323                                                             performConfigurationChange setNightDimText nightDimLevel=0
19:03:35.323                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb4000070e038d800} hwInitialized=true
19:03:35.326 ScrollView                                                   onsize change changed 
19:03:35.326 VRI[Compet...y]@36894e0                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
19:03:35.326                                                             Setup new sync=wmsSync-VRI[CompetitiveModeActivity]@36894e0#8
19:03:35.326                                                             Creating new active sync group VRI[CompetitiveModeActivity]@36894e0#9
19:03:35.326                                                             registerCallbacksForSync syncBuffer=false
19:03:35.332                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
19:03:35.332                                                             mWNT: t=0xb4000070e0041480 mBlastBufferQueue=0xb4000070e005d000 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
19:03:35.332                                                             Setting up sync and frameCommitCallback
19:03:35.335 BLASTBufferQueue                                            [VRI[CompetitiveModeActivity]@36894e0#4](f:0,a:0,s:0) onFrameAvailable the first frame is available
19:03:35.335 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 36727, bufferData(ID: 30554397343763, frameNumber: 1)
19:03:35.335 VRI[Compet...y]@36894e0                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
19:03:35.336 HWUI                                                        CFMS:: SetUp Pid : 7114    Tid : 7168
19:03:35.336 VRI[Compet...y]@36894e0                                     reportDrawFinished seqId=0
19:03:35.338 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
19:03:35.364 VRI[Compet...y]@36894e0                                     mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb4000070e038d800}
19:03:36.946                                                             ViewPostIme pointer 0
19:03:36.947                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@36894e0
19:03:37.008                                                             ViewPostIme pointer 1
19:03:37.014 CompetitiveModeActivity                                     ♟️ PAWN PROMOTION TO QUEEN: b7b8q
19:03:37.014                                                             🎯 ATTEMPTING PROMOTION MOVE: b7b8q
19:03:37.014                                                             📝 Player color: white
19:03:37.014                                                             🔄 Is player's turn: true
19:03:37.014                                                             🔄 Is white's turn: true
19:03:37.014 GameViewModel                                               🎯 makePlayerMove called with: b7b8q
19:03:37.065                                                             🔍 Validating move: b7b8q (attempt 1)
19:03:37.165                                                             📋 Current position: 6r1/1P3pk1/2N4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 1 23
19:03:37.216                                                             ⚖️ Move b7b8q legality check: LEGAL
19:03:37.216                                                             ✅ Executing validated move: b7b8q
19:03:37.419                                                             📍 New position after move: 1Q4r1/5pk1/2N4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 b - - 0 23
19:03:37.420 WindowManager                                               WindowManagerGlobal#removeView, ty=2, view=com.android.internal.policy.DecorView{b4402e3 V.E...... R....... 0,0-1368,698}[CompetitiveModeActivity], caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.app.Dialog.dismissDialog:808 
19:03:37.420 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@ea9ea3f
19:03:37.426 VRI[Compet...y]@36894e0                                     dispatchDetachedFromWindow
19:03:37.436 Choreographer                                               Skipped 50 frames!  The application may be doing too much work on its main thread.
19:03:37.440 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1Q4r1/5pk1/2N4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 b - - 0 23
19:03:37.446                                                             📜 Move history updated: 45 moves
19:03:37.446 GameHistoryManager                                          Move added: b7b8q
19:03:37.451 InputMethodManagerUtils                                     startInputInner - Id : 0
19:03:37.451 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
19:03:37.522 GameViewModel                                               🔍 Requesting position evaluation...
19:03:37.522                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:03:37.522                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:03:37.522                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:03:37.522                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:03:37.522                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:03:37.522                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:03:37.522                                                             🔧 gameRepository class: GameRepository
19:03:37.522                                                             🔧 Current thread: main
19:03:37.523 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:03:37.588 WindowManager           system_server                       win=Window{26b93c2 u0 com.example.chesspedagogue/com.example.chesspedagogue.CompetitiveModeActivity EXITING} destroySurfaces: appStopped=false cleanupOnResume=false win.mWindowRemovalAllowed=true win.mRemoveOnExit=true win.mViewVisibility=0 caller=com.android.server.wm.WindowState.onExitAnimationDone:222 com.android.server.wm.WindowState.onAnimationFinished:161 com.android.server.wm.WindowContainer$$ExternalSyntheticLambda5.onAnimationFinished:26 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda1.run:28 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda0.onAnimationFinished:65 com.android.server.wm.LocalAnimationAdapter$$ExternalSyntheticLambda0.run:10 android.os.Handler.handleCallback:959 
19:03:37.824 GameViewModel           com.example.chesspedagogue          ✅ Evaluation received: -7.66
19:03:37.925 CompetitiveModeActivity                                     📊 Evaluation updated: -7.66
19:03:37.925                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -7.66, emotionalManager: INITIALIZED
19:03:37.925                                                             🎯 Evaluation change: 0.5499997 (threshold: 0.8)
19:03:37.925                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 35s/25s)
19:03:37.925                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
19:03:38.835 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
19:03:38.836 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:03:38.872 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8b8
19:03:39.231 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1r6/5pk1/2N4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 0 24
19:03:39.240                                                             📜 Move history updated: 46 moves
19:03:39.240 GameHistoryManager                                          Move added: g8b8
19:03:39.240 GameViewModel                                               🔍 Requesting position evaluation...
19:03:39.240                                                             🎭 Updating personality context for move: g8b8
19:03:39.241                                                             ✨ Personality context updated for move g8b8 - This is revolutionary!
19:03:39.241                                                             🔍 Checking game end conditions...
19:03:39.343                                                             ✅ Game continues - no end condition detected
19:03:39.495                                                             ✅ Evaluation received: 8.62
19:03:39.598 CompetitiveModeActivity                                     📊 Evaluation updated: 8.62
19:03:39.598                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 8.62, emotionalManager: INITIALIZED
19:03:39.598                                                             🎯 Evaluation change: 15.73 (threshold: 0.8)
19:03:39.598                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 36s/25s)
19:03:39.598                                                             ✅ Emotional reaction triggered for evaluation: 8.62 (trigger: significant_disadvantage)
19:03:39.600 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
19:03:39.600                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:03:39.600                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:03:39.601                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
19:03:41.815                                                             ✅ Response generated: (23) The position is a tempest, but within chaos lies the spark of brilliance. I will seek the hidde...
19:03:41.815 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: (23) The position is a tempest, but within chaos lies the spark of brilliance. I will seek the hidden harmony in my opponent’s assault and strike with a thunderbolt of calculation.
19:03:41.821                                                             🎭 Updated emotion indicator: 😤
19:03:41.821                                                             🗣️ Speaking master dialogue: (23) The position is a tempest, but within chaos lies the spark of brilliance. I will seek the hidden harmony in my opponent’s assault and strike with a thunderbolt of calculation.
19:03:41.822 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
19:03:41.822                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
19:03:41.822                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
19:03:41.822                                                             ✅ Usage context set to: competitive_mode
19:03:41.822 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:03:41.822 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
19:03:41.823                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
19:03:41.825 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
19:03:47.866                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=1
19:03:47.866                                                             🎯 SQUARE TAPPED: row=7, col=1
19:03:47.866                                                             📝 Player color: white
19:03:47.866                                                             🔍 Selected row/col: -1/-1
19:03:48.223                                                             🎯 Selected piece: R at 7, 1
19:03:48.609                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
19:03:48.609                                                             🎯 SQUARE TAPPED: row=0, col=1
19:03:48.609                                                             📝 Player color: white
19:03:48.609                                                             🔍 Selected row/col: 7/1
19:03:48.610                                                             🎯 ATTEMPTING MOVE: b1b8
19:03:48.610                                                             📝 Player color: white
19:03:48.610                                                             🔄 Is player's turn: true
19:03:48.610                                                             🔄 Is white's turn: true
19:03:48.610                                                             ✅ Turn validation passed, making move: b1b8
19:03:48.610 GameViewModel                                               🎯 makePlayerMove called with: b1b8
19:03:48.660                                                             🔍 Validating move: b1b8 (attempt 1)
19:03:48.761                                                             📋 Current position: 1r6/5pk1/2N4p/p7/4p2q/2P1P3/P4PPP/1R1Q1K2 w - - 0 24
19:03:48.761                                                             ⚖️ Move b1b8 legality check: LEGAL
19:03:48.761                                                             ✅ Executing validated move: b1b8
19:03:48.963                                                             📍 New position after move: 1R6/5pk1/2N4p/p7/4p2q/2P1P3/P4PPP/3Q1K2 b - - 0 24
19:03:48.965 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1R6/5pk1/2N4p/p7/4p2q/2P1P3/P4PPP/3Q1K2 b - - 0 24
19:03:48.971                                                             📜 Move history updated: 47 moves
19:03:48.972 GameHistoryManager                                          Move added: b1b8
19:03:49.070 GameViewModel                                               🔍 Requesting position evaluation...
19:03:49.071                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:03:49.071                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:03:49.071                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:03:49.071                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:03:49.071                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:03:49.071                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:03:49.071                                                             🔧 gameRepository class: GameRepository
19:03:49.071                                                             🔧 Current thread: main
19:03:49.071 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:03:49.476 GameViewModel                                               ✅ Evaluation received: -8.98
19:03:49.577 CompetitiveModeActivity                                     📊 Evaluation updated: -8.98
19:03:49.577                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -8.98, emotionalManager: INITIALIZED
19:03:49.577                                                             🎯 Evaluation change: 17.599998 (threshold: 0.8)
19:03:49.577                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 9s/25s)
19:03:49.577                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:03:50.528 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
19:03:50.529 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:03:50.570 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h6h5
19:03:50.926 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1R6/5pk1/2N5/p6p/4p2q/2P1P3/P4PPP/3Q1K2 w - - 0 25
19:03:50.934                                                             📜 Move history updated: 48 moves
19:03:50.935 GameHistoryManager                                          Move added: h6h5
19:03:50.935 GameViewModel                                               🔍 Requesting position evaluation...
19:03:50.935                                                             🎭 Updating personality context for move: h6h5
19:03:50.935                                                             ✨ Personality context updated for move h6h5 - This is revolutionary!
19:03:50.935                                                             🔍 Checking game end conditions...
19:03:51.037                                                             ✅ Game continues - no end condition detected
19:03:51.189                                                             ✅ Evaluation received: 9.21
19:03:51.292 CompetitiveModeActivity                                     📊 Evaluation updated: 9.21
19:03:51.292                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 9.21, emotionalManager: INITIALIZED
19:03:51.292                                                             🎯 Evaluation change: 0.59000015 (threshold: 0.8)
19:03:51.292                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 11s/25s)
19:03:51.292                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
19:03:53.426                                                             🗣️ Master dialogue speech completed
19:04:06.413                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
19:04:06.413                                                             🎯 SQUARE TAPPED: row=7, col=3
19:04:06.413                                                             📝 Player color: white
19:04:06.413                                                             🔍 Selected row/col: -1/-1
19:04:06.773                                                             🎯 Selected piece: Q at 7, 3
19:04:07.119                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
19:04:07.119                                                             🎯 SQUARE TAPPED: row=4, col=3
19:04:07.119                                                             📝 Player color: white
19:04:07.119                                                             🔍 Selected row/col: 7/3
19:04:07.119                                                             🎯 ATTEMPTING MOVE: d1d4
19:04:07.119                                                             📝 Player color: white
19:04:07.119                                                             🔄 Is player's turn: true
19:04:07.119                                                             🔄 Is white's turn: true
19:04:07.119                                                             ✅ Turn validation passed, making move: d1d4
19:04:07.119 GameViewModel                                               🎯 makePlayerMove called with: d1d4
19:04:07.170                                                             🔍 Validating move: d1d4 (attempt 1)
19:04:07.272                                                             📋 Current position: 1R6/5pk1/2N5/p6p/4p2q/2P1P3/P4PPP/3Q1K2 w - - 0 25
19:04:07.323                                                             ⚖️ Move d1d4 legality check: LEGAL
19:04:07.323                                                             ✅ Executing validated move: d1d4
19:04:07.525                                                             📍 New position after move: 1R6/5pk1/2N5/p6p/3Qp2q/2P1P3/P4PPP/5K2 b - - 1 25
19:04:07.528 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1R6/5pk1/2N5/p6p/3Qp2q/2P1P3/P4PPP/5K2 b - - 1 25
19:04:07.535                                                             📜 Move history updated: 49 moves
19:04:07.535 GameHistoryManager                                          Move added: d1d4
19:04:07.626 GameViewModel                                               🔍 Requesting position evaluation...
19:04:07.626                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:04:07.626                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:04:07.626                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:04:07.626                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:04:07.626                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:04:07.626                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:04:07.626                                                             🔧 gameRepository class: GameRepository
19:04:07.626                                                             🔧 Current thread: main
19:04:07.626 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:04:07.879 GameViewModel                                               ✅ Evaluation received: -9.25
19:04:07.978 CompetitiveModeActivity                                     📊 Evaluation updated: -9.25
19:04:07.978                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -9.25, emotionalManager: INITIALIZED
19:04:07.978                                                             🎯 Evaluation change: 17.869999 (threshold: 0.8)
19:04:07.978                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 28s/25s)
19:04:07.978                                                             ✅ Emotional reaction triggered for evaluation: -9.25 (trigger: significant_advantage)
19:04:07.979 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
19:04:07.979                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:04:07.979                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:04:07.979                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
19:04:08.884 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
19:04:08.885 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:04:08.920 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h4f6
19:04:09.039 OpenAIService                                               ✅ Response generated: The board sings in my favor—every piece harmonizes in a winning combination. Now, to convert with bo...
19:04:09.280 CompetitiveModeActivity                                     🎯 Board updated with FEN: 1R6/5pk1/2N2q2/p6p/3Qp3/2P1P3/P4PPP/5K2 w - - 2 26
19:04:09.290                                                             📜 Move history updated: 50 moves
19:04:09.290 GameHistoryManager                                          Move added: h4f6
19:04:09.290 GameViewModel                                               🔍 Requesting position evaluation...
19:04:09.290                                                             🎭 Updating personality context for move: h4f6
19:04:09.290                                                             ✨ Personality context updated for move h4f6 - This is revolutionary!
19:04:09.290                                                             🔍 Checking game end conditions...
19:04:09.392                                                             ✅ Game continues - no end condition detected
19:04:09.392 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings in my favor—every piece harmonizes in a winning combination. Now, to convert with both precision and flair.
19:04:09.397                                                             🎭 Updated emotion indicator: 😏
19:04:09.397                                                             🗣️ Speaking master dialogue: The board sings in my favor—every piece harmonizes in a winning combination. Now, to convert with both precision and flair.
19:04:09.397 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
19:04:09.397                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
19:04:09.397                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
19:04:09.397                                                             ✅ Usage context set to: competitive_mode
19:04:09.397 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
19:04:09.398 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
19:04:09.398                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
19:04:09.400 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
19:04:09.552 GameViewModel                                               ✅ Evaluation received: 9.29
19:04:09.654 CompetitiveModeActivity                                     📊 Evaluation updated: 9.29
19:04:09.655                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 9.29, emotionalManager: INITIALIZED
19:04:09.655                                                             🎯 Evaluation change: 18.54 (threshold: 0.8)
19:04:09.655                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 1s/25s)
19:04:09.655                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:04:18.119                                                             🗣️ Master dialogue speech completed
19:04:18.671                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
19:04:18.671                                                             🎯 SQUARE TAPPED: row=0, col=1
19:04:18.671                                                             📝 Player color: white
19:04:18.671                                                             🔍 Selected row/col: -1/-1
19:04:19.028                                                             🎯 Selected piece: R at 0, 1
19:04:19.352                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=6
19:04:19.352                                                             🎯 SQUARE TAPPED: row=0, col=6
19:04:19.352                                                             📝 Player color: white
19:04:19.352                                                             🔍 Selected row/col: 0/1
19:04:19.352                                                             🎯 ATTEMPTING MOVE: b8g8
19:04:19.352                                                             📝 Player color: white
19:04:19.352                                                             🔄 Is player's turn: true
19:04:19.352                                                             🔄 Is white's turn: true
19:04:19.352                                                             ✅ Turn validation passed, making move: b8g8
19:04:19.353 GameViewModel                                               🎯 makePlayerMove called with: b8g8
19:04:19.403                                                             🔍 Validating move: b8g8 (attempt 1)
19:04:19.505                                                             📋 Current position: 1R6/5pk1/2N2q2/p6p/3Qp3/2P1P3/P4PPP/5K2 w - - 2 26
19:04:19.556                                                             ⚖️ Move b8g8 legality check: LEGAL
19:04:19.556                                                             ✅ Executing validated move: b8g8
19:04:19.758                                                             📍 New position after move: 6R1/5pk1/2N2q2/p6p/3Qp3/2P1P3/P4PPP/5K2 b - - 3 26
19:04:19.762 CompetitiveModeActivity                                     🎯 Board updated with FEN: 6R1/5pk1/2N2q2/p6p/3Qp3/2P1P3/P4PPP/5K2 b - - 3 26
19:04:19.768                                                             📜 Move history updated: 51 moves
19:04:19.768 GameHistoryManager                                          Move added: b8g8
19:04:19.861 GameViewModel                                               🔍 Requesting position evaluation...
19:04:19.861                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:04:19.861                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:04:19.861                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:04:19.861                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:04:19.861                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:04:19.861                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:04:19.861                                                             🔧 gameRepository class: GameRepository
19:04:19.861                                                             🔧 Current thread: main
19:04:19.861 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:04:20.113 GameViewModel                                               ✅ Evaluation received: M-4
19:04:20.214 CompetitiveModeActivity                                     📊 Evaluation updated: 0.0
19:04:20.214                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
19:04:20.214                                                             🎯 Evaluation change: 9.25 (threshold: 0.8)
19:04:20.214                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 12s/25s)
19:04:20.214                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:04:21.124 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
19:04:21.125 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:04:21.161 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g7g8
19:04:21.520 CompetitiveModeActivity                                     🎯 Board updated with FEN: 6k1/5p2/2N2q2/p6p/3Qp3/2P1P3/P4PPP/5K2 w - - 0 27
19:04:21.529                                                             📜 Move history updated: 52 moves
19:04:21.529 GameHistoryManager                                          Move added: g7g8
19:04:21.529 GameViewModel                                               🔍 Requesting position evaluation...
19:04:21.529                                                             🎭 Updating personality context for move: g7g8
19:04:21.529                                                             ✨ Personality context updated for move g7g8 - This is revolutionary!
19:04:21.529                                                             🔍 Checking game end conditions...
19:04:21.630                                                             ✅ Game continues - no end condition detected
19:04:21.785                                                             ✅ Evaluation received: M4
19:04:21.887 CompetitiveModeActivity                                     📊 Evaluation updated: 0.0
19:04:21.888                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
19:04:21.888                                                             🎯 Evaluation change: 9.25 (threshold: 0.8)
19:04:21.888                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 13s/25s)
19:04:21.888                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:04:26.364                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
19:04:26.364                                                             🎯 SQUARE TAPPED: row=4, col=3
19:04:26.364                                                             📝 Player color: white
19:04:26.364                                                             🔍 Selected row/col: -1/-1
19:04:26.721                                                             🎯 Selected piece: Q at 4, 3
19:04:27.078                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
19:04:27.078                                                             🎯 SQUARE TAPPED: row=2, col=5
19:04:27.078                                                             📝 Player color: white
19:04:27.078                                                             🔍 Selected row/col: 4/3
19:04:27.078                                                             🎯 ATTEMPTING MOVE: d4f6
19:04:27.078                                                             📝 Player color: white
19:04:27.078                                                             🔄 Is player's turn: true
19:04:27.078                                                             🔄 Is white's turn: true
19:04:27.078                                                             ✅ Turn validation passed, making move: d4f6
19:04:27.079 GameViewModel                                               🎯 makePlayerMove called with: d4f6
19:04:27.129                                                             🔍 Validating move: d4f6 (attempt 1)
19:04:27.230                                                             📋 Current position: 6k1/5p2/2N2q2/p6p/3Qp3/2P1P3/P4PPP/5K2 w - - 0 27
19:04:27.281                                                             ⚖️ Move d4f6 legality check: LEGAL
19:04:27.281                                                             ✅ Executing validated move: d4f6
19:04:27.484                                                             📍 New position after move: 6k1/5p2/2N2Q2/p6p/4p3/2P1P3/P4PPP/5K2 b - - 0 27
19:04:27.485 CompetitiveModeActivity                                     🎯 Board updated with FEN: 6k1/5p2/2N2Q2/p6p/4p3/2P1P3/P4PPP/5K2 b - - 0 27
19:04:27.492                                                             📜 Move history updated: 53 moves
19:04:27.492 GameHistoryManager                                          Move added: d4f6
19:04:27.588 GameViewModel                                               🔍 Requesting position evaluation...
19:04:27.588                                                             🎭 Using PERSONALITY ENGINE for move calculation!
19:04:27.589                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
19:04:27.589                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
19:04:27.589                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
19:04:27.589                                                             🔧 DEBUG: gameRepository instance = NOT NULL
19:04:27.589                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
19:04:27.589                                                             🔧 gameRepository class: GameRepository
19:04:27.589                                                             🔧 Current thread: main
19:04:27.589 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
19:04:27.841 GameViewModel                                               ✅ Evaluation received: M-3
19:04:27.944 CompetitiveModeActivity                                     📊 Evaluation updated: 0.0
19:04:27.944                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
19:04:27.944                                                             🎯 Evaluation change: 9.25 (threshold: 0.8)
19:04:27.944                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 19s/25s)
19:04:27.944                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
19:04:27.985 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 35 historical positions
19:04:27.986 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
19:04:28.021 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8h7
19:04:28.382 CompetitiveModeActivity                                     🎯 Board updated with FEN: 8/5p1k/2N2Q2/p6p/4p3/2P1P3/P4PPP/5K2 w - - 1 28
19:04:28.392                                                             📜 Move history updated: 54 moves
19:04:28.392 GameHistoryManager                                          Move added: g8h7
19:04:28.392 GameViewModel                                               🔍 Requesting position evaluation...
19:04:28.392                                                             🎭 Updating personality context for move: g8h7
19:04:28.392                                                             ✨ Personality context updated for move g8h7 - This is revolutionary!
19:04:28.393                                                             🔍 Checking game end conditions...
19:04:28.495                                                             ✅ Game continues - no end condition detected
19:04:28.656                                                             ✅ Evaluation received: M3
19:04:28.757 CompetitiveModeActivity                                     📊 Evaluation updated: 0.0
19:04:28.757                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
19:04:28.757                                                             🎯 Evaluation change: 9.25 (threshold: 0.8)
19:04:28.757                                                             🔍 Emotional reaction check - Change: true, Cooldown: false (time since last: 20s/25s)
19:04:28.757                                                             🚫 Emotional reaction blocked - Change: true, Cooldown: false
