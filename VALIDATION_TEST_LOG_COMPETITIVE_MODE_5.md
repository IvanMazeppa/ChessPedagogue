00:11:14.517 CompetitiveModeActivity com.example.chesspedagogue          🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=6
00:11:14.517                                                             🎯 SQUARE TAPPED: row=0, col=6
00:11:14.517                                                             📝 Player color: black
00:11:14.517                                                             🔍 Selected row/col: -1/-1
00:11:14.874                                                             🎯 Selected piece: n at 0, 6
00:11:15.334                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
00:11:15.334                                                             🎯 SQUARE TAPPED: row=2, col=5
00:11:15.334                                                             📝 Player color: black
00:11:15.334                                                             🔍 Selected row/col: 0/6
00:11:15.334                                                             🎯 ATTEMPTING MOVE: g8f6
00:11:15.334                                                             📝 Player color: black
00:11:15.334                                                             🔄 Is player's turn: true
00:11:15.334                                                             🔄 Is white's turn: false
00:11:15.334                                                             ✅ Turn validation passed, making move: g8f6
00:11:15.334 GameViewModel                                               🎯 makePlayerMove called with: g8f6
00:11:15.385                                                             🔍 Validating move: g8f6 (attempt 1)
00:11:15.486                                                             📋 Current position: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1
00:11:15.537                                                             ⚖️ Move g8f6 legality check: LEGAL
00:11:15.537                                                             ✅ Executing validated move: g8f6
00:11:15.739                                                             📍 New position after move: rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 1 2
00:11:15.742 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 1 2
00:11:15.744                                                             📜 Move history updated: 2 moves
00:11:15.744 GameHistoryManager                                          Move added: g8f6
00:11:15.840 GameViewModel                                               🔍 Requesting position evaluation...
00:11:15.840                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:11:15.840                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:11:15.840                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:11:15.840                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:11:15.840                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:11:15.840                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:11:15.840                                                             🔧 gameRepository class: GameRepository
00:11:15.840                                                             🔧 Current thread: main
00:11:15.840 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:11:17.097 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.64 (move=10, alternating=ON, FEN=rnbqkb1r/pppppppp/5n)
00:11:17.097                                                             🔄 ALTERNATING: move=10, flip=false, 0.64→0.64 (diff=0.35)
00:11:17.097 GameViewModel                                               ✅ Evaluation received: 0.64
00:11:17.198 CompetitiveModeActivity                                     📊 Evaluation updated: 0.64
00:11:17.198                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.64, emotionalManager: INITIALIZED
00:11:17.198                                                             🎯 First emotional evaluation: 0.64 (threshold: 1.5)
00:11:17.198                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633877s/25s)
00:11:17.198                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:11:17.743 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:11:17.744 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #2)
00:11:17.744                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:11:17.744                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKB...
00:11:17.744                                                             🎲 Candidate moves for AI analysis: [d2d3]
00:11:17.744 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:11:17.744                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:11:17.934                                                             📋 Response ID: resp_68588d95c300819e932de4349344989907b48f271d51b73d
00:11:18.929                                                             🏁 Response completed
00:11:18.930 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d2d3": {"score": 0.0, "reason": "Passive and unambitious. Such a move woul...
00:11:18.931                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.9
00:11:18.931                                                             🎭 AI preferred moves: [d2d3]
00:11:18.931                                                             💭 AI reasoning: My style demands struggle and complexity from the first move. Quiet moves betray a lack of vision.
00:11:18.964 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d2d3
00:11:19.319 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/4P3/3P4/PPP2PPP/RNBQKBNR b KQkq - 0 2
00:11:19.322                                                             📜 Move history updated: 3 moves
00:11:19.322 GameHistoryManager                                          Move added: d2d3
00:11:19.322 GameViewModel                                               🔍 Requesting position evaluation...
00:11:19.323                                                             🎭 Updating personality context for move: d2d3
00:11:19.323                                                             ✨ Personality context updated for move d2d3 - This is revolutionary!
00:11:19.323                                                             🔍 Checking game end conditions...
00:11:19.424                                                             ✅ Game continues - no end condition detected
00:11:20.284 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.06 (move=11, alternating=ON, FEN=rnbqkb1r/pppppppp/5n)
00:11:20.284                                                             🔄 ALTERNATING: move=11, flip=true, 0.06→-0.06 (diff=0.70)
00:11:20.288 GameViewModel                                               ✅ Evaluation received: -0.06
00:11:20.390 CompetitiveModeActivity                                     📊 Evaluation updated: -0.06
00:11:20.391                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.06, emotionalManager: INITIALIZED
00:11:20.391                                                             🎯 First emotional evaluation: -0.06 (threshold: 1.5)
00:11:20.391                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633880s/25s)
00:11:20.391                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:11:30.856                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=3
00:11:30.856                                                             🎯 SQUARE TAPPED: row=1, col=3
00:11:30.856                                                             📝 Player color: black
00:11:30.857                                                             🔍 Selected row/col: -1/-1
00:11:31.212                                                             🎯 Selected piece: p at 1, 3
00:11:31.772                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
00:11:31.772                                                             🎯 SQUARE TAPPED: row=3, col=3
00:11:31.772                                                             📝 Player color: black
00:11:31.772                                                             🔍 Selected row/col: 1/3
00:11:31.772                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=3, reaches=false
00:11:31.772                                                             🎯 ATTEMPTING MOVE: d7d5
00:11:31.772                                                             📝 Player color: black
00:11:31.772                                                             🔄 Is player's turn: true
00:11:31.772                                                             🔄 Is white's turn: false
00:11:31.772                                                             ✅ Turn validation passed, making move: d7d5
00:11:31.772 GameViewModel                                               🎯 makePlayerMove called with: d7d5
00:11:31.824                                                             🔍 Validating move: d7d5 (attempt 1)
00:11:31.925                                                             📋 Current position: rnbqkb1r/pppppppp/5n2/8/4P3/3P4/PPP2PPP/RNBQKBNR b KQkq - 0 2
00:11:31.978                                                             ⚖️ Move d7d5 legality check: LEGAL
00:11:31.978                                                             ✅ Executing validated move: d7d5
00:11:32.180                                                             📍 New position after move: rnbqkb1r/ppp1pppp/5n2/3p4/4P3/3P4/PPP2PPP/RNBQKBNR w KQkq - 0 3
00:11:32.182 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp1pppp/5n2/3p4/4P3/3P4/PPP2PPP/RNBQKBNR w KQkq - 0 3
00:11:32.184                                                             📜 Move history updated: 4 moves
00:11:32.184 GameHistoryManager                                          Move added: d7d5
00:11:32.281 GameViewModel                                               🔍 Requesting position evaluation...
00:11:32.281                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:11:32.281                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:11:32.281                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:11:32.282                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:11:32.282                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:11:32.282                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:11:32.282                                                             🔧 gameRepository class: GameRepository
00:11:32.282                                                             🔧 Current thread: main
00:11:32.282 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:11:33.190 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.47 (move=12, alternating=ON, FEN=rnbqkb1r/ppp1pppp/5n)
00:11:33.190                                                             🔄 ALTERNATING: move=12, flip=false, 0.47→0.47 (diff=0.53)
00:11:33.190 GameViewModel                                               ✅ Evaluation received: 0.47
00:11:33.290 CompetitiveModeActivity                                     📊 Evaluation updated: 0.47
00:11:33.290                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.47, emotionalManager: INITIALIZED
00:11:33.291                                                             🎯 First emotional evaluation: 0.47 (threshold: 1.5)
00:11:33.291                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633893s/25s)
00:11:33.291                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:11:33.959 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:11:33.960 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #3)
00:11:33.960                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:11:33.960                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/ppp1pppp/5n2/3p4/4P3/3P4/PPP2PPP/RNB...
00:11:33.960                                                             🎲 Candidate moves for AI analysis: [e4e5]
00:11:33.960 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:11:33.960                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:11:34.239                                                             📋 Response ID: resp_68588da602d081a18be0bccd1ec232c5097b052cbd4d24ec
00:11:36.173                                                             🏁 Response completed
00:11:36.173 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e4e5": {"score": 0.0, "reason": "Symmetrical and simplistic. No fight, no ...
00:11:36.175                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 403 of {
                                                                           "move_scores": {
                                                                             "e4e5": {"score": 0.0, "reason": "Symmetrical and simplistic. No fight, no complexity—antithetical to my nature."},
                                                                             "d4d5": {"score": 0.3, "reason": "Slightly more ambitious, but still too balanced and predictable."},
                                                                             "d3d4": {"score": 0.5, "reason": "Offers potential for tension, but requires further provocation to become truly complex."},
                                                                             "d4e5": {"score": 0.9, \"reason\": \"Bold and unbalancing. Seeks out the complications where I can out-calculate my opponent.\"}
                                                                           },
                                                                           "top_choice": "d4e5",
                                                                           "style_reasoning": "I seek out the fight and the labyrinth, not the straight path. My best play comes from chaos and clarity, not from dull equality.",
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
00:11:36.205 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e4e5
00:11:36.561 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp1pppp/5n2/3pP3/8/3P4/PPP2PPP/RNBQKBNR b KQkq - 0 3
00:11:36.564                                                             📜 Move history updated: 5 moves
00:11:36.565 GameHistoryManager                                          Move added: e4e5
00:11:36.565 GameViewModel                                               🔍 Requesting position evaluation...
00:11:36.565                                                             🎭 Updating personality context for move: e4e5
00:11:36.566                                                             ✨ Personality context updated for move e4e5 - This is revolutionary!
00:11:36.566                                                             🔍 Checking game end conditions...
00:11:36.667                                                             ✅ Game continues - no end condition detected
00:11:37.425 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.40 (move=13, alternating=ON, FEN=rnbqkb1r/ppp1pppp/5n)
00:11:37.425                                                             🔄 ALTERNATING: move=13, flip=true, -0.40→0.40 (diff=0.07)
00:11:37.427 GameViewModel                                               ✅ Evaluation received: 0.40
00:11:37.535 CompetitiveModeActivity                                     📊 Evaluation updated: 0.4
00:11:37.535                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.4, emotionalManager: INITIALIZED
00:11:37.535                                                             🎯 First emotional evaluation: 0.4 (threshold: 1.5)
00:11:37.535                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633897s/25s)
00:11:37.535                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:11:45.902                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
00:11:45.902                                                             🎯 SQUARE TAPPED: row=2, col=5
00:11:45.902                                                             📝 Player color: black
00:11:45.902                                                             🔍 Selected row/col: -1/-1
00:11:46.261                                                             🎯 Selected piece: n at 2, 5
00:11:46.714                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=3
00:11:46.714                                                             🎯 SQUARE TAPPED: row=1, col=3
00:11:46.714                                                             📝 Player color: black
00:11:46.714                                                             🔍 Selected row/col: 2/5
00:11:46.714                                                             🎯 ATTEMPTING MOVE: f6d7
00:11:46.714                                                             📝 Player color: black
00:11:46.714                                                             🔄 Is player's turn: true
00:11:46.714                                                             🔄 Is white's turn: false
00:11:46.714                                                             ✅ Turn validation passed, making move: f6d7
00:11:46.714 GameViewModel                                               🎯 makePlayerMove called with: f6d7
00:11:46.765                                                             🔍 Validating move: f6d7 (attempt 1)
00:11:46.866                                                             📋 Current position: rnbqkb1r/ppp1pppp/5n2/3pP3/8/3P4/PPP2PPP/RNBQKBNR b KQkq - 0 3
00:11:46.917                                                             ⚖️ Move f6d7 legality check: LEGAL
00:11:46.917                                                             ✅ Executing validated move: f6d7
00:11:47.119                                                             📍 New position after move: rnbqkb1r/pppnpppp/8/3pP3/8/3P4/PPP2PPP/RNBQKBNR w KQkq - 1 4
00:11:47.121 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppnpppp/8/3pP3/8/3P4/PPP2PPP/RNBQKBNR w KQkq - 1 4
00:11:47.124                                                             📜 Move history updated: 6 moves
00:11:47.125 GameHistoryManager                                          Move added: f6d7
00:11:47.219 GameViewModel                                               🔍 Requesting position evaluation...
00:11:47.219                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:11:47.219                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:11:47.219                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:11:47.219                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:11:47.219                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:11:47.219                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:11:47.219                                                             🔧 gameRepository class: GameRepository
00:11:47.219                                                             🔧 Current thread: main
00:11:47.220 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:11:48.174 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.39 (move=14, alternating=ON, FEN=rnbqkb1r/pppnpppp/8/)
00:11:48.174                                                             🔄 ALTERNATING: move=14, flip=false, 0.39→0.39 (diff=0.01)
00:11:48.175 GameViewModel                                               ✅ Evaluation received: 0.39
00:11:48.275 CompetitiveModeActivity                                     📊 Evaluation updated: 0.39
00:11:48.275                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.39, emotionalManager: INITIALIZED
00:11:48.275                                                             🎯 First emotional evaluation: 0.39 (threshold: 1.5)
00:11:48.275                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633908s/25s)
00:11:48.275                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:11:48.914 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:11:48.915 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #4)
00:11:48.915                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:11:48.915                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pppnpppp/8/3pP3/8/3P4/PPP2PPP/RNBQKB...
00:11:48.915                                                             🎲 Candidate moves for AI analysis: [d3d4]
00:11:48.915 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:11:48.915                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:11:49.193                                                             📋 Response ID: resp_68588db4f1288191acb6ca6bdbe78e4d0506aa22d19d1053
00:11:51.161                                                             🏁 Response completed
00:11:51.162 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d3d4": {"score": 0.0, "reason": "Too straightforward and symmetrical. Lack...
00:11:51.162                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
00:11:51.162                                                             🎭 AI preferred moves: [d3d4]
00:11:51.162                                                             💭 AI reasoning: This move does not align with my desire for dynamic, double-edged play. I would seek alternatives that unsettle the opponent and create rich tactical possibilities.
00:11:51.200 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d3d4
00:11:51.559 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppnpppp/8/3pP3/3P4/8/PPP2PPP/RNBQKBNR b KQkq - 0 4
00:11:51.562                                                             📜 Move history updated: 7 moves
00:11:51.563 GameHistoryManager                                          Move added: d3d4
00:11:51.563 GameViewModel                                               🔍 Requesting position evaluation...
00:11:51.563                                                             🎭 Updating personality context for move: d3d4
00:11:51.563                                                             ✨ Personality context updated for move d3d4 - This is revolutionary!
00:11:51.563                                                             🔍 Checking game end conditions...
00:11:51.665                                                             ✅ Game continues - no end condition detected
00:11:52.373 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.29 (move=15, alternating=ON, FEN=rnbqkb1r/pppnpppp/8/)
00:11:52.374                                                             🔄 ALTERNATING: move=15, flip=true, -0.29→0.29 (diff=0.10)
00:11:52.382 GameViewModel                                               ✅ Evaluation received: 0.29
00:11:52.491 CompetitiveModeActivity                                     📊 Evaluation updated: 0.29
00:11:52.491                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.29, emotionalManager: INITIALIZED
00:11:52.491                                                             🎯 First emotional evaluation: 0.29 (threshold: 1.5)
00:11:52.491                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633912s/25s)
00:11:52.491                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:12:01.254                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=2
00:12:01.254                                                             🎯 SQUARE TAPPED: row=1, col=2
00:12:01.254                                                             📝 Player color: black
00:12:01.254                                                             🔍 Selected row/col: -1/-1
00:12:01.611                                                             🎯 Selected piece: p at 1, 2
00:12:03.432                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=2
00:12:03.432                                                             🎯 SQUARE TAPPED: row=3, col=2
00:12:03.432                                                             📝 Player color: black
00:12:03.432                                                             🔍 Selected row/col: 1/2
00:12:03.432                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=3, reaches=false
00:12:03.432                                                             🎯 ATTEMPTING MOVE: c7c5
00:12:03.432                                                             📝 Player color: black
00:12:03.432                                                             🔄 Is player's turn: true
00:12:03.433                                                             🔄 Is white's turn: false
00:12:03.433                                                             ✅ Turn validation passed, making move: c7c5
00:12:03.433 GameViewModel                                               🎯 makePlayerMove called with: c7c5
00:12:03.484                                                             🔍 Validating move: c7c5 (attempt 1)
00:12:03.584                                                             📋 Current position: rnbqkb1r/pppnpppp/8/3pP3/3P4/8/PPP2PPP/RNBQKBNR b KQkq - 0 4
00:12:03.635                                                             ⚖️ Move c7c5 legality check: LEGAL
00:12:03.635                                                             ✅ Executing validated move: c7c5
00:12:03.838                                                             📍 New position after move: rnbqkb1r/pp1npppp/8/2ppP3/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 0 5
00:12:03.840 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1npppp/8/2ppP3/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 0 5
00:12:03.843                                                             📜 Move history updated: 8 moves
00:12:03.843 GameHistoryManager                                          Move added: c7c5
00:12:03.938 GameViewModel                                               🔍 Requesting position evaluation...
00:12:03.938                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:12:03.938                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:12:03.938                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:12:03.938                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:12:03.938                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:12:03.938                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:12:03.938                                                             🔧 gameRepository class: GameRepository
00:12:03.938                                                             🔧 Current thread: main
00:12:03.938 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:12:05.146 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.23 (move=16, alternating=ON, FEN=rnbqkb1r/pp1npppp/8/)
00:12:05.146                                                             🔄 ALTERNATING: move=16, flip=false, 0.23→0.23 (diff=0.06)
00:12:05.146 GameViewModel                                               ✅ Evaluation received: 0.23
00:12:05.246 CompetitiveModeActivity                                     📊 Evaluation updated: 0.23
00:12:05.247                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.23, emotionalManager: INITIALIZED
00:12:05.247                                                             🎯 First emotional evaluation: 0.23 (threshold: 1.5)
00:12:05.247                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633925s/25s)
00:12:05.247                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:12:05.734 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:12:05.734 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #5)
00:12:05.734                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:12:05.734                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp1npppp/8/2ppP3/3P4/8/PPP2PPP/RNBQK...
00:12:05.734                                                             🎲 Candidate moves for AI analysis: [g1f3]
00:12:05.735 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:12:05.735                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:12:05.983                                                             📋 Response ID: resp_68588dc5c11081a38aa19c1cc5371fdf08523d36d0b00592
00:12:07.940                                                             🏁 Response completed
00:12:07.940 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g1f3": {"score": 0.0, "reason": "Passive and symmetrical. Does not seek co...
00:12:07.941                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
00:12:07.941                                                             🎭 AI preferred moves: []
00:12:07.941                                                             💭 AI reasoning: I seek positions where my calculation and imagination can flourish. g1f3 is safe, but I play to create storms, not calm seas.
00:12:07.977 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g1f3
00:12:08.332 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1npppp/8/2ppP3/3P4/5N2/PPP2PPP/RNBQKB1R b KQkq - 1 5
00:12:08.337                                                             📜 Move history updated: 9 moves
00:12:08.337 GameHistoryManager                                          Move added: g1f3
00:12:08.337 GameViewModel                                               🔍 Requesting position evaluation...
00:12:08.338                                                             🎭 Updating personality context for move: g1f3
00:12:08.338                                                             ✨ Personality context updated for move g1f3 - This is revolutionary!
00:12:08.338                                                             🔍 Checking game end conditions...
00:12:08.439                                                             ✅ Game continues - no end condition detected
00:12:09.448 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.23 (move=17, alternating=ON, FEN=rnbqkb1r/pp1npppp/8/)
00:12:09.448                                                             🔄 ALTERNATING: move=17, flip=true, 0.23→-0.23 (diff=0.46)
00:12:09.449 GameViewModel                                               ✅ Evaluation received: -0.23
00:12:09.551 CompetitiveModeActivity                                     📊 Evaluation updated: -0.23
00:12:09.551                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.23, emotionalManager: INITIALIZED
00:12:09.551                                                             🎯 First emotional evaluation: -0.23 (threshold: 1.5)
00:12:09.551                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633929s/25s)
00:12:09.551                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:12:29.696                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=2
00:12:29.696                                                             🎯 SQUARE TAPPED: row=3, col=2
00:12:29.696                                                             📝 Player color: black
00:12:29.696                                                             🔍 Selected row/col: -1/-1
00:12:30.052                                                             🎯 Selected piece: p at 3, 2
00:12:30.503                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
00:12:30.503                                                             🎯 SQUARE TAPPED: row=4, col=3
00:12:30.503                                                             📝 Player color: black
00:12:30.503                                                             🔍 Selected row/col: 3/2
00:12:30.503                                                             ♟️ Promotion check: piece=p, fromRow=3, toRow=4, reaches=false
00:12:30.503                                                             🎯 ATTEMPTING MOVE: c5d4
00:12:30.503                                                             📝 Player color: black
00:12:30.503                                                             🔄 Is player's turn: true
00:12:30.503                                                             🔄 Is white's turn: false
00:12:30.503                                                             ✅ Turn validation passed, making move: c5d4
00:12:30.503 GameViewModel                                               🎯 makePlayerMove called with: c5d4
00:12:30.554                                                             🔍 Validating move: c5d4 (attempt 1)
00:12:30.654                                                             📋 Current position: rnbqkb1r/pp1npppp/8/2ppP3/3P4/5N2/PPP2PPP/RNBQKB1R b KQkq - 1 5
00:12:30.706                                                             ⚖️ Move c5d4 legality check: LEGAL
00:12:30.706                                                             ✅ Executing validated move: c5d4
00:12:30.909                                                             📍 New position after move: rnbqkb1r/pp1npppp/8/3pP3/3p4/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 6
00:12:30.911 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1npppp/8/3pP3/3p4/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 6
00:12:30.915                                                             📜 Move history updated: 10 moves
00:12:30.915 GameHistoryManager                                          Move added: c5d4
00:12:31.010 GameViewModel                                               🔍 Requesting position evaluation...
00:12:31.010                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:12:31.010                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:12:31.010                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:12:31.011                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:12:31.011                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:12:31.011                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:12:31.011                                                             🔧 gameRepository class: GameRepository
00:12:31.011                                                             🔧 Current thread: main
00:12:31.011 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:12:32.315 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.23 (move=18, alternating=ON, FEN=rnbqkb1r/pp1npppp/8/)
00:12:32.316                                                             🔄 ALTERNATING: move=18, flip=false, -0.23→-0.23 (diff=0.00)
00:12:32.316 GameViewModel                                               ✅ Evaluation received: -0.23
00:12:32.416 CompetitiveModeActivity                                     📊 Evaluation updated: -0.23
00:12:32.416                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.23, emotionalManager: INITIALIZED
00:12:32.416                                                             🎯 First emotional evaluation: -0.23 (threshold: 1.5)
00:12:32.416                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633952s/25s)
00:12:32.416                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:12:32.942 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:12:32.943 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #6)
00:12:32.943                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:12:32.943                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp1npppp/8/3pP3/3p4/5N2/PPP2PPP/RNBQ...
00:12:32.943                                                             🎲 Candidate moves for AI analysis: [d1d4]
00:12:32.943 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:12:32.943                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:12:33.222                                                             📋 Response ID: resp_68588de0fbb481a1ad2158cac78344ca0b8dbde1883fe3f1
00:12:34.457                                                             🏁 Response completed
00:12:34.458 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d1d4": {"score": 0.0, "reason": "A dull, symmetrical choice. Avoids comple...
00:12:34.458                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
00:12:34.458                                                             🎭 AI preferred moves: [d1d4]
00:12:34.459                                                             💭 AI reasoning: In this moment, d4 would be a betrayal of my instincts. I seek chaos and challenge, not ease and order.
00:12:34.484 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d1d4
00:12:34.839 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1npppp/8/3pP3/3Q4/5N2/PPP2PPP/RNB1KB1R b KQkq - 0 6
00:12:34.844                                                             📜 Move history updated: 11 moves
00:12:34.844 GameHistoryManager                                          Move added: d1d4
00:12:34.844 GameViewModel                                               🔍 Requesting position evaluation...
00:12:34.845                                                             🎭 Updating personality context for move: d1d4
00:12:34.845                                                             ✨ Personality context updated for move d1d4 - This is revolutionary!
00:12:34.845                                                             🔍 Checking game end conditions...
00:12:34.946                                                             ✅ Game continues - no end condition detected
00:12:35.757 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.24 (move=19, alternating=ON, FEN=rnbqkb1r/pp1npppp/8/)
00:12:35.758                                                             🔄 ALTERNATING: move=19, flip=true, 0.24→-0.24 (diff=0.01)
00:12:35.767 GameViewModel                                               ✅ Evaluation received: -0.24
00:12:35.868 CompetitiveModeActivity                                     📊 Evaluation updated: -0.24
00:12:35.868                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.24, emotionalManager: INITIALIZED
00:12:35.868                                                             🎯 First emotional evaluation: -0.24 (threshold: 1.5)
00:12:35.868                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633955s/25s)
00:12:35.868                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:12:41.152                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
00:12:41.152                                                             🎯 SQUARE TAPPED: row=1, col=4
00:12:41.152                                                             📝 Player color: black
00:12:41.152                                                             🔍 Selected row/col: -1/-1
00:12:41.509                                                             🎯 Selected piece: p at 1, 4
00:12:41.894                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
00:12:41.894                                                             🎯 SQUARE TAPPED: row=2, col=4
00:12:41.894                                                             📝 Player color: black
00:12:41.894                                                             🔍 Selected row/col: 1/4
00:12:41.894                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=2, reaches=false
00:12:41.894                                                             🎯 ATTEMPTING MOVE: e7e6
00:12:41.894                                                             📝 Player color: black
00:12:41.894                                                             🔄 Is player's turn: true
00:12:41.894                                                             🔄 Is white's turn: false
00:12:41.894                                                             ✅ Turn validation passed, making move: e7e6
00:12:41.894 GameViewModel                                               🎯 makePlayerMove called with: e7e6
00:12:41.946                                                             🔍 Validating move: e7e6 (attempt 1)
00:12:42.048                                                             📋 Current position: rnbqkb1r/pp1npppp/8/3pP3/3Q4/5N2/PPP2PPP/RNB1KB1R b KQkq - 0 6
00:12:42.099                                                             ⚖️ Move e7e6 legality check: LEGAL
00:12:42.099                                                             ✅ Executing validated move: e7e6
00:12:42.301                                                             📍 New position after move: rnbqkb1r/pp1n1ppp/4p3/3pP3/3Q4/5N2/PPP2PPP/RNB1KB1R w KQkq - 0 7
00:12:42.304 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1n1ppp/4p3/3pP3/3Q4/5N2/PPP2PPP/RNB1KB1R w KQkq - 0 7
00:12:42.307                                                             📜 Move history updated: 12 moves
00:12:42.307 GameHistoryManager                                          Move added: e7e6
00:12:42.402 GameViewModel                                               🔍 Requesting position evaluation...
00:12:42.402                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:12:42.402                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:12:42.402                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:12:42.403                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:12:42.403                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:12:42.403                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:12:42.403                                                             🔧 gameRepository class: GameRepository
00:12:42.403                                                             🔧 Current thread: main
00:12:42.403 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:12:43.712 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.20 (move=20, alternating=ON, FEN=rnbqkb1r/pp1n1ppp/4p)
00:12:43.712                                                             🔄 ALTERNATING: move=20, flip=false, -0.20→-0.20 (diff=0.04)
00:12:43.712 GameViewModel                                               ✅ Evaluation received: -0.20
00:12:43.812 CompetitiveModeActivity                                     📊 Evaluation updated: -0.2
00:12:43.812                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.2, emotionalManager: INITIALIZED
00:12:43.812                                                             🎯 First emotional evaluation: -0.2 (threshold: 1.5)
00:12:43.812                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633963s/25s)
00:12:43.812                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:12:44.134 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:12:44.135 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #7)
00:12:44.135                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:12:44.135                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp1n1ppp/4p3/3pP3/3Q4/5N2/PPP2PPP/RN...
00:12:44.135                                                             🎲 Candidate moves for AI analysis: [d4f4]
00:12:44.135 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:12:44.135                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:12:44.384                                                             📋 Response ID: resp_68588dec326c81a2a518fc38cb6ee337094695402e8d0ca5
00:12:46.385                                                             🏁 Response completed
00:12:46.386 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d4f4": {"score": 0.0, "reason": "Passive and simplistic; avoids complexity...
00:12:46.387                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
00:12:46.387                                                             🎭 AI preferred moves: []
00:12:46.387                                                             💭 AI reasoning: I seek to unsettle my opponents at every turn, to draw them into the labyrinths of my creation.
00:12:46.411 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d4f4
00:12:46.766 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp1n1ppp/4p3/3pP3/5Q2/5N2/PPP2PPP/RNB1KB1R b KQkq - 1 7
00:12:46.771                                                             📜 Move history updated: 13 moves
00:12:46.772 GameHistoryManager                                          Move added: d4f4
00:12:46.772 GameViewModel                                               🔍 Requesting position evaluation...
00:12:46.772                                                             🎭 Updating personality context for move: d4f4
00:12:46.772                                                             ✨ Personality context updated for move d4f4 - This is revolutionary!
00:12:46.773                                                             🔍 Checking game end conditions...
00:12:46.874                                                             ✅ Game continues - no end condition detected
00:12:47.784 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.40 (move=21, alternating=ON, FEN=rnbqkb1r/pp1n1ppp/4p)
00:12:47.784                                                             🔄 ALTERNATING: move=21, flip=true, 0.40→-0.40 (diff=0.20)
00:12:47.785 GameViewModel                                               ✅ Evaluation received: -0.40
00:12:47.888 CompetitiveModeActivity                                     📊 Evaluation updated: -0.4
00:12:47.888                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.4, emotionalManager: INITIALIZED
00:12:47.888                                                             🎯 First emotional evaluation: -0.4 (threshold: 1.5)
00:12:47.888                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750633967s/25s)
00:12:47.888                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:13:20.644                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
00:13:20.644                                                             🎯 SQUARE TAPPED: row=0, col=1
00:13:20.644                                                             📝 Player color: black
00:13:20.644                                                             🔍 Selected row/col: -1/-1
00:13:21.002                                                             🎯 Selected piece: n at 0, 1
00:13:21.609                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=2
00:13:21.610                                                             🎯 SQUARE TAPPED: row=2, col=2
00:13:21.610                                                             📝 Player color: black
00:13:21.610                                                             🔍 Selected row/col: 0/1
00:13:21.610                                                             🎯 ATTEMPTING MOVE: b8c6
00:13:21.610                                                             📝 Player color: black
00:13:21.610                                                             🔄 Is player's turn: true
00:13:21.610                                                             🔄 Is white's turn: false
00:13:21.610                                                             ✅ Turn validation passed, making move: b8c6
00:13:21.610 GameViewModel                                               🎯 makePlayerMove called with: b8c6
00:13:21.661                                                             🔍 Validating move: b8c6 (attempt 1)
00:13:21.762                                                             📋 Current position: rnbqkb1r/pp1n1ppp/4p3/3pP3/5Q2/5N2/PPP2PPP/RNB1KB1R b KQkq - 1 7
00:13:21.814                                                             ⚖️ Move b8c6 legality check: LEGAL
00:13:21.814                                                             ✅ Executing validated move: b8c6
00:13:22.016                                                             📍 New position after move: r1bqkb1r/pp1n1ppp/2n1p3/3pP3/5Q2/5N2/PPP2PPP/RNB1KB1R w KQkq - 2 8
00:13:22.019 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1n1ppp/2n1p3/3pP3/5Q2/5N2/PPP2PPP/RNB1KB1R w KQkq - 2 8
00:13:22.023                                                             📜 Move history updated: 14 moves
00:13:22.023 GameHistoryManager                                          Move added: b8c6
00:13:22.117 GameViewModel                                               🔍 Requesting position evaluation...
00:13:22.118                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:13:22.118                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:13:22.118                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:13:22.118                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:13:22.118                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:13:22.118                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:13:22.118                                                             🔧 gameRepository class: GameRepository
00:13:22.118                                                             🔧 Current thread: main
00:13:22.118 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:13:23.932 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.40 (move=22, alternating=ON, FEN=r1bqkb1r/pp1n1ppp/2n)
00:13:23.932                                                             🔄 ALTERNATING: move=22, flip=false, -0.40→-0.40 (diff=0.00)
00:13:23.933 GameViewModel                                               ✅ Evaluation received: -0.40
00:13:24.033 CompetitiveModeActivity                                     📊 Evaluation updated: -0.4
00:13:24.033                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.4, emotionalManager: INITIALIZED
00:13:24.033                                                             🎯 First emotional evaluation: -0.4 (threshold: 1.5)
00:13:24.033                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750634004s/25s)
00:13:24.033                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:13:24.446 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:13:24.447 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #8)
00:13:24.447                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:13:24.447                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1n1ppp/2n1p3/3pP3/5Q2/5N2/PPP2PPP/...
00:13:24.447                                                             🎲 Candidate moves for AI analysis: [b1c3]
00:13:24.448 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:13:24.448                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:13:24.730                                                             📋 Response ID: resp_68588e147cd081a08cccd26f38b890360fce015d4fdd8e19
00:13:26.903                                                             🏁 Response completed
00:13:26.911 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "b1c3": {"score": 0.0, "reason": "Passive and symmetrical; avoids complexit...
00:13:26.912                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 248 of {
                                                                           "move_scores": {
                                                                             "b1c3": {"score": 0.0, "reason": "Passive and symmetrical; avoids complexity and initiative."},
                                                                             "Nc3": {"score": 0.0, "reason": "Same as above—no difference in notation, but lacks ambition."},
                                                                             "Qf4": {"score": 0.5, \"reason\": \"Maintains tension but does not create imbalances.\"},
                                                                             \"Ng5\": {\"score\": 0.9, \"reason\": \"Provokes weaknesses and invites complications—very Alekhine.\"},
                                                                             \"Bg5\": {\"score\": 0.7, \"reason\": \"More direct, but less forcing than Ng5.\"}
                                                                           },
                                                                           \"top_choice\": \"Ng5\",
                                                                           \"style_reasoning\": \"Seek out complications and the initiative at all costs. The passive move b1c3 is anathema to my style.\",
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
00:13:26.936 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b1c3
00:13:26.956 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
00:13:27.296 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1n1ppp/2n1p3/3pP3/5Q2/2N2N2/PPP2PPP/R1B1KB1R b KQkq - 3 8
00:13:27.301                                                             📜 Move history updated: 15 moves
00:13:27.301 GameHistoryManager                                          Move added: b1c3
00:13:27.302 GameViewModel                                               🔍 Requesting position evaluation...
00:13:27.302                                                             🎭 Updating personality context for move: b1c3
00:13:27.302                                                             ✨ Personality context updated for move b1c3 - This is revolutionary!
00:13:27.302                                                             🔍 Checking game end conditions...
00:13:27.404                                                             ✅ Game continues - no end condition detected
00:13:28.519 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.55 (move=23, alternating=ON, FEN=r1bqkb1r/pp1n1ppp/2n)
00:13:28.519                                                             🔄 ALTERNATING: move=23, flip=true, 0.55→-0.55 (diff=0.15)
00:13:28.520 GameViewModel                                               ✅ Evaluation received: -0.55
00:13:28.622 CompetitiveModeActivity                                     📊 Evaluation updated: -0.55
00:13:28.623                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.55, emotionalManager: INITIALIZED
00:13:28.623                                                             🎯 First emotional evaluation: -0.55 (threshold: 1.5)
00:13:28.623                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750634008s/25s)
00:13:28.623                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:14:39.643                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=5
00:14:39.643                                                             🎯 SQUARE TAPPED: row=1, col=5
00:14:39.643                                                             📝 Player color: black
00:14:39.643                                                             🔍 Selected row/col: -1/-1
00:14:40.001                                                             🎯 Selected piece: p at 1, 5
00:14:40.507                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
00:14:40.507                                                             🎯 SQUARE TAPPED: row=2, col=5
00:14:40.507                                                             📝 Player color: black
00:14:40.507                                                             🔍 Selected row/col: 1/5
00:14:40.507                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=2, reaches=false
00:14:40.507                                                             🎯 ATTEMPTING MOVE: f7f6
00:14:40.507                                                             📝 Player color: black
00:14:40.507                                                             🔄 Is player's turn: true
00:14:40.507                                                             🔄 Is white's turn: false
00:14:40.507                                                             ✅ Turn validation passed, making move: f7f6
00:14:40.507 GameViewModel                                               🎯 makePlayerMove called with: f7f6
00:14:40.558                                                             🔍 Validating move: f7f6 (attempt 1)
00:14:40.659                                                             📋 Current position: r1bqkb1r/pp1n1ppp/2n1p3/3pP3/5Q2/2N2N2/PPP2PPP/R1B1KB1R b KQkq - 3 8
00:14:40.711                                                             ⚖️ Move f7f6 legality check: LEGAL
00:14:40.711                                                             ✅ Executing validated move: f7f6
00:14:40.913                                                             📍 New position after move: r1bqkb1r/pp1n2pp/2n1pp2/3pP3/5Q2/2N2N2/PPP2PPP/R1B1KB1R w KQkq - 0 9
00:14:40.915 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1n2pp/2n1pp2/3pP3/5Q2/2N2N2/PPP2PPP/R1B1KB1R w KQkq - 0 9
00:14:40.920                                                             📜 Move history updated: 16 moves
00:14:40.920 GameHistoryManager                                          Move added: f7f6
00:14:41.013 GameViewModel                                               🔍 Requesting position evaluation...
00:14:41.013                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:14:41.013                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:14:41.013                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:14:41.014                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:14:41.014                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:14:41.014                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:14:41.014                                                             🔧 gameRepository class: GameRepository
00:14:41.014                                                             🔧 Current thread: main
00:14:41.014 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:14:42.574 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.26 (move=24, alternating=ON, FEN=r1bqkb1r/pp1n2pp/2n1)
00:14:42.574                                                             🔄 ALTERNATING: move=24, flip=false, -0.26→-0.26 (diff=0.29)
00:14:42.574 GameViewModel                                               ✅ Evaluation received: -0.26
00:14:42.674 CompetitiveModeActivity                                     📊 Evaluation updated: -0.26
00:14:42.674                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.26, emotionalManager: INITIALIZED
00:14:42.674                                                             🎯 First emotional evaluation: -0.26 (threshold: 1.5)
00:14:42.674                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750634082s/25s)
00:14:42.674                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:14:43.309 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:14:43.310 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #9)
00:14:43.310                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:14:43.310                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1n2pp/2n1pp2/3pP3/5Q2/2N2N2/PPP2PP...
00:14:43.310                                                             🎲 Candidate moves for AI analysis: [e5f6]
00:14:43.310 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:14:43.310                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:14:43.578                                                             📋 Response ID: resp_68588e6359b481a2b462c1dfe4f5a2cc042fb51c09d070a4
00:14:45.416                                                             🏁 Response completed
00:14:45.416 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e5f6": {"score": 0.0, "reason": "A sterile exchange that leads to simplifi...
00:14:45.417                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:14:45.417                                                             🎭 AI preferred moves: []
00:14:45.417                                                             💭 AI reasoning: I seek out positions where my calculation and imagination can shine. Qg4 keeps the initiative and forces my opponent into difficult decisions.
00:14:45.450 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e5f6
00:14:45.806 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1n2pp/2n1pP2/3p4/5Q2/2N2N2/PPP2PPP/R1B1KB1R b KQkq - 0 9
00:14:45.812                                                             📜 Move history updated: 17 moves
00:14:45.813 GameHistoryManager                                          Move added: e5f6
00:14:45.813 GameViewModel                                               🔍 Requesting position evaluation...
00:14:45.813                                                             🎭 Updating personality context for move: e5f6
00:14:45.814                                                             ✨ Personality context updated for move e5f6 - This is revolutionary!
00:14:45.814                                                             🔍 Checking game end conditions...
00:14:45.915                                                             ✅ Game continues - no end condition detected
00:14:46.790 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.40 (move=25, alternating=ON, FEN=r1bqkb1r/pp1n2pp/2n1)
00:14:46.791                                                             🔄 ALTERNATING: move=25, flip=true, 0.40→-0.40 (diff=0.14)
00:14:46.797 GameViewModel                                               ✅ Evaluation received: -0.40
00:14:46.899 CompetitiveModeActivity                                     📊 Evaluation updated: -0.4
00:14:46.899                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.4, emotionalManager: INITIALIZED
00:14:46.899                                                             🎯 First emotional evaluation: -0.4 (threshold: 1.5)
00:14:46.899                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750634086s/25s)
00:14:46.899                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:15:15.373                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=3
00:15:15.373                                                             🎯 SQUARE TAPPED: row=1, col=3
00:15:15.373                                                             📝 Player color: black
00:15:15.373                                                             🔍 Selected row/col: -1/-1
00:15:15.730                                                             🎯 Selected piece: n at 1, 3
00:15:16.188                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
00:15:16.188                                                             🎯 SQUARE TAPPED: row=2, col=5
00:15:16.188                                                             📝 Player color: black
00:15:16.188                                                             🔍 Selected row/col: 1/3
00:15:16.189                                                             🎯 ATTEMPTING MOVE: d7f6
00:15:16.189                                                             📝 Player color: black
00:15:16.189                                                             🔄 Is player's turn: true
00:15:16.189                                                             🔄 Is white's turn: false
00:15:16.189                                                             ✅ Turn validation passed, making move: d7f6
00:15:16.189 GameViewModel                                               🎯 makePlayerMove called with: d7f6
00:15:16.240                                                             🔍 Validating move: d7f6 (attempt 1)
00:15:16.340                                                             📋 Current position: r1bqkb1r/pp1n2pp/2n1pP2/3p4/5Q2/2N2N2/PPP2PPP/R1B1KB1R b KQkq - 0 9
00:15:16.392                                                             ⚖️ Move d7f6 legality check: LEGAL
00:15:16.392                                                             ✅ Executing validated move: d7f6
00:15:16.594                                                             📍 New position after move: r1bqkb1r/pp4pp/2n1pn2/3p4/5Q2/2N2N2/PPP2PPP/R1B1KB1R w KQkq - 0 10
00:15:16.597 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp4pp/2n1pn2/3p4/5Q2/2N2N2/PPP2PPP/R1B1KB1R w KQkq - 0 10
00:15:16.602                                                             📜 Move history updated: 18 moves
00:15:16.602 GameHistoryManager                                          Move added: d7f6
00:15:16.695 GameViewModel                                               🔍 Requesting position evaluation...
00:15:16.696                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:15:16.696                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:15:16.696                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:15:16.696                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:15:16.696                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:15:16.696                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:15:16.696                                                             🔧 gameRepository class: GameRepository
00:15:16.696                                                             🔧 Current thread: main
00:15:16.696 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:15:19.154 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.40 (move=26, alternating=ON, FEN=r1bqkb1r/pp4pp/2n1pn)
00:15:19.154                                                             🔄 ALTERNATING: move=26, flip=false, -0.40→-0.40 (diff=0.00)
00:15:19.154 GameViewModel                                               ✅ Evaluation received: -0.40
00:15:19.255 CompetitiveModeActivity                                     📊 Evaluation updated: -0.4
00:15:19.255                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.4, emotionalManager: INITIALIZED
00:15:19.255                                                             🎯 First emotional evaluation: -0.4 (threshold: 1.5)
00:15:19.255                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750634119s/25s)
00:15:19.255                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:15:19.537 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:15:19.537 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #10)
00:15:19.538                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:15:19.538                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp4pp/2n1pn2/3p4/5Q2/2N2N2/PPP2PPP/R...
00:15:19.538                                                             🎲 Candidate moves for AI analysis: [f4h4]
00:15:19.538 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:15:19.538                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:15:19.936                                                             📋 Response ID: resp_68588e87900c81a3814682fc8362ba060c1bb7220366d98b
00:15:21.596                                                             🏁 Response completed
00:15:21.596 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f4h4": {"score": 0.0, "reason": "A flank pawn move with no clear purpose—t...
00:15:21.598                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 397 of {
                                                                           "move_scores": {
                                                                             "f4h4": {"score": 0.0, "reason": "A flank pawn move with no clear purpose—too slow and symmetrical for my taste."},
                                                                             "d4": {"score": 0.7, "reason": "Opens the center and invites complications, but lacks the bite of deeper calculation."},
                                                                             "e4": {"score": 0.9, "reason": "Bold central thrust, creates imbalance and opens lines for attack."},
                                                                             "b4": {"score": 0.6, \"reason\": \"An interesting try, but too speculative without concrete follow-up.\"}
                                                                           },
                                                                           "top_choice": "e4",
                                                                           "style_reasoning": "I seek the fight and the beautiful, tangled battle. My moves must reflect the artist’s heart and the warrior’s mind.",
                                                                           "confidence": 0.9
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
00:15:21.633 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f4h4
00:15:21.988 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp4pp/2n1pn2/3p4/7Q/2N2N2/PPP2PPP/R1B1KB1R b KQkq - 1 10
00:15:21.994                                                             📜 Move history updated: 19 moves
00:15:21.994 GameHistoryManager                                          Move added: f4h4
00:15:21.994 GameViewModel                                               🔍 Requesting position evaluation...
00:15:21.994                                                             🎭 Updating personality context for move: f4h4
00:15:21.994                                                             ✨ Personality context updated for move f4h4 - This is revolutionary!
00:15:21.994                                                             🔍 Checking game end conditions...
00:15:22.095                                                             ✅ Game continues - no end condition detected
00:15:23.105 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.49 (move=27, alternating=ON, FEN=r1bqkb1r/pp4pp/2n1pn)
00:15:23.105                                                             🔄 ALTERNATING: move=27, flip=true, 0.49→-0.49 (diff=0.09)
00:15:23.106 GameViewModel                                               ✅ Evaluation received: -0.49
00:15:23.208 CompetitiveModeActivity                                     📊 Evaluation updated: -0.49
00:15:23.208                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.49, emotionalManager: INITIALIZED
00:15:23.208                                                             🎯 First emotional evaluation: -0.49 (threshold: 1.5)
00:15:23.208                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750634123s/25s)
00:15:23.208                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:16:11.187                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=5
00:16:11.187                                                             🎯 SQUARE TAPPED: row=0, col=5
00:16:11.187                                                             📝 Player color: black
00:16:11.187                                                             🔍 Selected row/col: -1/-1
00:16:11.545                                                             🎯 Selected piece: b at 0, 5
00:16:12.056                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=3
00:16:12.056                                                             🎯 SQUARE TAPPED: row=2, col=3
00:16:12.056                                                             📝 Player color: black
00:16:12.056                                                             🔍 Selected row/col: 0/5
00:16:12.056                                                             🎯 ATTEMPTING MOVE: f8d6
00:16:12.056                                                             📝 Player color: black
00:16:12.056                                                             🔄 Is player's turn: true
00:16:12.056                                                             🔄 Is white's turn: false
00:16:12.056                                                             ✅ Turn validation passed, making move: f8d6
00:16:12.056 GameViewModel                                               🎯 makePlayerMove called with: f8d6
00:16:12.107                                                             🔍 Validating move: f8d6 (attempt 1)
00:16:12.208                                                             📋 Current position: r1bqkb1r/pp4pp/2n1pn2/3p4/7Q/2N2N2/PPP2PPP/R1B1KB1R b KQkq - 1 10
00:16:12.259                                                             ⚖️ Move f8d6 legality check: LEGAL
00:16:12.259                                                             ✅ Executing validated move: f8d6
00:16:12.462                                                             📍 New position after move: r1bqk2r/pp4pp/2nbpn2/3p4/7Q/2N2N2/PPP2PPP/R1B1KB1R w KQkq - 2 11
00:16:12.464 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pp4pp/2nbpn2/3p4/7Q/2N2N2/PPP2PPP/R1B1KB1R w KQkq - 2 11
00:16:12.468                                                             📜 Move history updated: 20 moves
00:16:12.468 GameHistoryManager                                          Move added: f8d6
00:16:12.562 GameViewModel                                               🔍 Requesting position evaluation...
00:16:12.562                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:16:12.562                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:16:12.562                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:16:12.562                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:16:12.562                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:16:12.562                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:16:12.562                                                             🔧 gameRepository class: GameRepository
00:16:12.562                                                             🔧 Current thread: main
00:16:12.562 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:16:14.322 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.47 (move=28, alternating=ON, FEN=r1bqk2r/pp4pp/2nbpn2)
00:16:14.322                                                             🔄 ALTERNATING: move=28, flip=false, -0.47→-0.47 (diff=0.02)
00:16:14.323 GameViewModel                                               ✅ Evaluation received: -0.47
00:16:14.423 CompetitiveModeActivity                                     📊 Evaluation updated: -0.47
00:16:14.423                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.47, emotionalManager: INITIALIZED
00:16:14.423                                                             🎯 First emotional evaluation: -0.47 (threshold: 1.5)
00:16:14.423                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750634174s/25s)
00:16:14.423                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:16:14.906 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 5 historical positions
00:16:14.906 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #11)
00:16:14.906                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:16:14.906                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqk2r/pp4pp/2nbpn2/3p4/7Q/2N2N2/PPP2PPP/R1B...
00:16:14.906                                                             🎲 Candidate moves for AI analysis: [f1d3]
00:16:14.906 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:16:14.907                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:16:15.124                                                             📋 Response ID: resp_68588ebeedbc81a0be4cba849345002a00920ffe72eefd8c
00:16:16.917                                                             🏁 Response completed
00:16:16.918 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f1d3": {"score": 0.0, "reason": "Passive and defensive, it avoids the figh...
00:16:16.919                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
00:16:16.919                                                             🎭 AI preferred moves: []
00:16:16.919                                                             💭 AI reasoning: Alekhine’s best play is marked by boldness and complexity. He seeks to unsettle his opponent at every turn, not merely to equalize.
00:16:16.946 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f1d3
00:16:17.306 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pp4pp/2nbpn2/3p4/7Q/2NB1N2/PPP2PPP/R1B1K2R b KQkq - 3 11
00:16:17.313                                                             📜 Move history updated: 21 moves
00:16:17.313 GameHistoryManager                                          Move added: f1d3
00:16:17.313 GameViewModel                                               🔍 Requesting position evaluation...
00:16:17.314                                                             🎭 Updating personality context for move: f1d3
00:16:17.314                                                             ✨ Personality context updated for move f1d3 - This is revolutionary!
00:16:17.314                                                             🔍 Checking game end conditions...
00:16:17.416                                                             ✅ Game continues - no end condition detected
00:16:19.030 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.43 (move=29, alternating=ON, FEN=r1bqk2r/pp4pp/2nbpn2)
00:16:19.030                                                             🔄 ALTERNATING: move=29, flip=true, 0.43→-0.43 (diff=0.04)
00:16:19.030 GameViewModel                                               ✅ Evaluation received: -0.43
00:16:19.132 CompetitiveModeActivity                                     📊 Evaluation updated: -0.43
00:16:19.132                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.43, emotionalManager: INITIALIZED
00:16:19.132                                                             🎯 First emotional evaluation: -0.43 (threshold: 1.5)
00:16:19.133                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750634179s/25s)
00:16:19.133                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:18:13.753                                                             🧪 Starting Competitive Mode Style Validation...
00:18:13.755 Toast                                                       show: caller = com.example.chesspedagogue.CompetitiveModeActivity.runCompetitiveValidation:2844 
00:18:13.757                                                             show: isDexDualMode = false
00:18:13.757                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
00:18:13.762 AlekhineStyleValidator                                      🧪 Starting Quick Alekhine Style Validation Test...
00:18:13.762                                                             🎯 Testing: World Championship Game 11 - Decisive attacking move
00:18:15.147 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:18:15.148 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #12)
00:18:15.148                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:15.149                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/pp2nppp/2n1p3/3pP3/2pP4/2N1BN2/PP2BP...
00:18:15.149                                                             🎲 Candidate moves for AI analysis: [d1d2]
00:18:15.149 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:15.149                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:15.344                                                             📋 Response ID: resp_68588f372c24819189b580ed7a9d6ffd00349703d4c9a38e
00:18:16.937                                                             🏁 Response completed
00:18:16.938 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d1d2": {"score": 0.0, "reason": "Passive and defensive; avoids conflict an...
00:18:16.939                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
00:18:16.939                                                             🎭 AI preferred moves: []
00:18:16.939                                                             💭 AI reasoning: g4 embodies my desire to create tension and test my opponent’s nerve. It opens the path for attack and keeps the position rich with possibilities.
00:18:16.971 AlekhineStyleValidator                                        📊 AI Move: d1d2 | Historical: h2h4 | Match: ❌ | Style: 0.60
00:18:16.971                                                             🎯 Testing: Brilliant pawn sacrifice leading to overwhelming attack
00:18:18.811 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:18:18.811 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #13)
00:18:18.811                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:18.811                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqk2r/pp2bppp/4pn2/3p4/2PP4/2N2N2/PP2BPPP/R...
00:18:18.811                                                             🎲 Candidate moves for AI analysis: [c4c5]
00:18:18.811 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:18.812                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:19.132                                                             📋 Response ID: resp_68588f3ad678819fbe847da298243eaf014ed66b71ee74d8
00:18:21.433                                                             🏁 Response completed
00:18:21.434 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c4c5": {"score": 0.0, "reason": "A closed, static thrust that limits compl...
00:18:21.435                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 306 of {
                                                                           "move_scores": {
                                                                             "c4c5": {"score": 0.0, "reason": "A closed, static thrust that limits complexity—antithetical to my preference for dynamic imbalance."},
                                                                             "e3e4": {"score": 0.7, "reason": "Offers scope for central tension and open lines, though somewhat predictable."},
                                                                             "b4": {"score": 0.9, \"reason\": \"A sharp, provocative flank advance that invites complications—very much in my spirit.\"},
                                                                             "cxd5": {"score": 0.6, \"reason\": \"Simplifies the center, reducing tension—useful, but not my first choice.\"}
                                                                           },
                                                                           \"top_choice\": \"b4\",
                                                                           \"style_reasoning\": \"The b4 thrust embodies my desire to unbalance the game and seize the initiative. It forces the opponent to navigate unfamiliar waters.\",
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
00:18:21.468 AlekhineStyleValidator                                        📊 AI Move: c4c5 | Historical: d4d5 | Match: ❌ | Style: 0.60
00:18:21.468                                                             🎯 Testing: Space advantage in center with positional pressure
00:18:23.079 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:18:23.080 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #14)
00:18:23.080                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:23.080                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qkb1r/1b1n1ppp/p2ppn2/1p6/3PP3/1QN2N2/PP1B1...
00:18:23.080                                                             🎲 Candidate moves for AI analysis: [a2a4]
00:18:23.080 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:23.080                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:23.435                                                             📋 Response ID: resp_68588f3f1bd0819191a4bb0d57036a400762bf7b7e6a09bc
00:18:24.292                                                             🏁 Response completed
00:18:24.293 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "a2a4": {"score": 0.0, "reason": "Passive and symmetrical—no tension, no ch...
00:18:24.294                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
00:18:24.294                                                             🎭 AI preferred moves: [a2a4]
00:18:24.294                                                             💭 AI reasoning: This move shirks responsibility and avoids conflict. Such play is beneath a true artist.
00:18:24.329 AlekhineStyleValidator                                        📊 AI Move: a2a4 | Historical: e4e5 | Match: ❌ | Style: 0.70
00:18:24.329                                                             🎯 Testing: Aggressive piece development creating immediate threats
00:18:26.039 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:18:26.039 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #15)
00:18:26.039                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:26.039                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/3P1N2/PPP2P...
00:18:26.039                                                             🎲 Candidate moves for AI analysis: [f3g5]
00:18:26.040 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:26.040                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:26.215                                                             📋 Response ID: resp_68588f4210388191b1636fb87c6ecdae0b2860ad814fe80a
00:18:28.392                                                             🏁 Response completed
00:18:28.394 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f3g5": {"score": 0.0, "reason": "A passive, weakening move that invites tr...
00:18:28.395                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 319 of {
                                                                           "move_scores": {
                                                                             "f3g5": {"score": 0.0, "reason": "A passive, weakening move that invites trouble. Not in keeping with my fighting spirit."},
                                                                             "d4": {"score": 0.7, "reason": "More in line with my desire for central tension and open lines. But lacks the bite of a true sacrifice."},
                                                                             "Ng5": {"score": 0.9, \"reason\": \"Aggressive, seeking complications. Would consider if analysis supported it.\"},
                                                                             "Nc3": {"score": 0.6, \"reason\": \"A solid developing move, but too quiet for my taste at this stage.\"}
                                                                           },
                                                                           "top_choice": "Ng5",
                                                                           "style_reasoning": "I yearn for positions that test my calculation and nerve, not safe harbors. Ng5 promises the storms I seek.",
                                                                           "confidence": 0.9
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
00:18:28.436 AlekhineStyleValidator                                        📊 AI Move: f3g5 | Historical: f3g5 | Match: ✅ | Style: 1.00
00:18:28.436                                                             🎯 Testing: Classical pawn endgame technique demonstration
00:18:28.730 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 4 historical positions
00:18:28.730 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #16)
00:18:28.730                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:28.730                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): 8/8/1p6/pP6/P7/8/4k3/4K3 w - - 0 50
                                                                         You are p...
00:18:28.730                                                             🎲 Candidate moves for AI analysis: [e1e2]
00:18:28.731 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:28.731                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:29.070                                                             📋 Response ID: resp_68588f44c3e881a08609392c11efd0d10dd5d1c5ba66b7c0
00:18:30.059                                                             🏁 Response completed
00:18:30.060 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e1e2": {"score": 0.0, "reason": "Passive and symmetrical; avoids complexit...
00:18:30.060                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
00:18:30.060                                                             🎭 AI preferred moves: [e1e2]
00:18:30.060                                                             💭 AI reasoning: In this position, any move that avoids struggle is a betrayal of chess itself. I would not settle for e1e2—my play demands conflict, not capitulation.
00:18:30.092 AlekhineStyleValidator                                        📊 AI Move: e1e2 | Historical: b5b6 | Match: ❌ | Style: 0.50
00:18:30.092                                                             🎯 Testing: Tactical shot preparing devastating attack on kingside
00:18:31.625 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:18:31.625 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #17)
00:18:31.625                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:31.625                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qk2r/1b2bppp/p2p1n2/npp1p3/4P3/1BP2N2/PP1P1...
00:18:31.625                                                             🎲 Candidate moves for AI analysis: [d2d4]
00:18:31.625 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:31.626                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:31.832                                                             📋 Response ID: resp_68588f47a434819e9ec6dcd14b3a2941026c08fdad97f0f1
00:18:34.447                                                             🏁 Response completed
00:18:34.448 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d2d4": {"score": 0.0, "reason": "Too simple and symmetrical. Does not crea...
00:18:34.449                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
00:18:34.449                                                             🎭 AI preferred moves: []
00:18:34.449                                                             💭 AI reasoning: I seek out positions that test both my calculation and my opponent’s nerves. The quiet path is for others.
00:18:34.480 AlekhineStyleValidator                                        📊 AI Move: d2d4 | Historical: f3h4 | Match: ❌ | Style: 0.60
00:18:34.480                                                             🎯 Testing: Legendary bishop sacrifice leading to forced mate
00:18:35.997 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:18:35.999 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #18)
00:18:35.999                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:35.999                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1p1ppp/2n2n2/2p1p3/2B1P3/3P1N2/PPP...
00:18:35.999                                                             🎲 Candidate moves for AI analysis: [c1g5]
00:18:35.999 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:35.999                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:36.337                                                             📋 Response ID: resp_68588f4c08348191952b43de2022419e02ba9d92c1fe2d89
00:18:38.997                                                             🏁 Response completed
00:18:38.998 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c1g5": {"score": 0.0, "reason": "Passive and symmetrical; no tension or co...
00:18:38.999                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 344 of {
                                                                           "move_scores": {
                                                                             "c1g5": {"score": 0.0, "reason": "Passive and symmetrical; no tension or complexity created."},
                                                                             "d2d4": {"score": 0.7, "reason": "Opens the center and invites imbalance, but lacks immediate spark."},
                                                                             "b1c3": {"score": 0.6, "reason": "Develops naturally, but too safe for my taste."},
                                                                             "f1g5": {"score": 0.9, \"reason\": \"Pins the knight and creates latent tension—begins to build pressure.\"},
                                                                             \"c3d5\": {\"score\": 1.0, \"reason\": \"(If possible) A brave sacrifice for initiative—perfectly Alekhine.\"}
                                                                           },
                                                                           \"top_choice\": \"f1g5\",
                                                                           \"style_reasoning\": \"I seek to unsettle my opponent from the first. Moves that create complexity and psychological pressure are my preference.\",
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
00:18:39.038 AlekhineStyleValidator                                        📊 AI Move: c1g5 | Historical: c4f7 | Match: ❌ | Style: 0.60
00:18:39.038                                                             🎯 Testing: Positional pawn advance creating long-term advantages
00:18:40.404 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:18:40.406 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #19)
00:18:40.406                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:40.406                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2q1rk1/1b2bppp/p2ppn2/1p6/3PP3/1QN2N2/PP1B1P...
00:18:40.406                                                             🎲 Candidate moves for AI analysis: [a2a3]
00:18:40.406 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:40.406                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:40.638                                                             📋 Response ID: resp_68588f506d00819da20ea113ce2de1ee0965ab4159640c01
00:18:42.867                                                             🏁 Response completed
00:18:42.868 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "a2a3": {"score": 0.0, "reason": "Passive and unnecessary; avoids conflict ...
00:18:42.869                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:18:42.869                                                             🎭 AI preferred moves: []
00:18:42.869                                                             💭 AI reasoning: I seek out the battlefield, not the quiet garden. Moves that stir the pot and create chaos are my preference.
00:18:42.919 AlekhineStyleValidator                                        📊 AI Move: a2a3 | Historical: a2a4 | Match: ❌ | Style: 0.70
00:18:42.919                                                             🎯 Testing: Central breakthrough against the former world champion
00:18:44.675 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 6 historical positions
00:18:44.676 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #20)
00:18:44.676                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:44.676                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/pp1nbppp/2pp1n2/4p3/2BPP3/2N2N2/PP3P...
00:18:44.676                                                             🎲 Candidate moves for AI analysis: [a2a4]
00:18:44.676 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:18:44.676                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:18:44.937                                                             📋 Response ID: resp_68588f54b53881929f3c07fc3304d70a0aae4043f86b0467
00:18:46.613                                                             🏁 Response completed
00:18:46.614 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "a2a4": {"score": 0.0, "reason": "Passive and slow; avoids conflict and com...
00:18:46.615                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
00:18:46.615                                                             🎭 AI preferred moves: []
00:18:46.615                                                             💭 AI reasoning: The sacrifice embodies my belief that the initiative is worth more than material. I seek to create positions where my calculation and imagination can flourish.
00:18:46.650 AlekhineStyleValidator                                        📊 AI Move: a2a4 | Historical: d4d5 | Match: ❌ | Style: 0.60
00:18:46.650                                                             🎯 Testing: Alekhine Defense demonstration - dynamic counterplay
00:18:48.267 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:18:48.268 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
00:18:48.286 AlekhineStyleValidator                                        📊 AI Move: e2e4 | Historical: e2e4 | Match: ✅ | Style: 1.00
00:18:48.287                                                             ✅ Quick validation test completed!
00:18:48.287 CompetitiveModeActivity                                     🧪 COMPETITIVE VALIDATION RESULTS:
                                                                         🎯 ALEKHINE COMPETITIVE MODE VALIDATION
                                                                         
                                                                         🎯 ALEKHINE STYLE ACCURACY REPORT\n=======================================\nOverall Accuracy: 49.4/100\nHistorical Match Rate: 20.0%\nStyle Consistency: 69.0/100\nTactical Patterns: 75.0/100\nPositional Patterns: 70.0/100\nEndgame Patterns: 65.0/100\n\n🎖️ Performance Grade: NEEDS SIGNIFICANT IMPROVEMENT ⚠️\n
                                                                         
                                                                         🎮 Competitive Settings:
                                                                         Master: alekhine
                                                                         Player: black
                                                                         Skill Level: 14