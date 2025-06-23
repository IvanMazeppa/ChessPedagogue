nalityMove() ENTRY! 🚨🚨🚨
00:48:43.488 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.64 (move=4, alternating=ON, FEN=rnbqkb1r/pppppppp/5n)
00:48:43.488                                                             🔄 ALTERNATING: move=4, flip=false, 0.64→0.64 (diff=0.35)
00:48:43.489 GameViewModel                                               ✅ Evaluation received: 0.64
00:48:43.589 CompetitiveModeActivity                                     📊 Evaluation updated: 0.64
00:48:43.589                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.64, emotionalManager: INITIALIZED
00:48:43.589                                                             🎯 First emotional evaluation: 0.64 (threshold: 1.5)
00:48:43.589                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636123s/25s)
00:48:43.589                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:48:44.095 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:48:44.096 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #3)
00:48:44.096                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:48:44.096                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKB...
00:48:44.096                                                             🎲 Candidate moves for AI analysis: [d1e2]
00:48:44.097 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:48:44.097                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:48:44.332                                                             📋 Response ID: resp_6858965c21dc819295d5b4c32f480abf06367b1b58a61162
00:48:45.414                                                             🏁 Response completed
00:48:45.414 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d1e2": {"score": 0.0, "reason": "Passive and retreating, this move avoids ...
00:48:45.415                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
00:48:45.415                                                             🎭 AI preferred moves: []
00:48:45.415                                                             💭 AI reasoning: I would never willingly enter dullness. Every move must pursue struggle and beauty.
00:48:45.471 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d1e2
00:48:45.826 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPPQPPP/RNB1KBNR b KQkq - 2 2
00:48:45.829                                                             📜 Move history updated: 3 moves
00:48:45.829 GameHistoryManager                                          Move added: d1e2
00:48:45.829 GameViewModel                                               🔍 Requesting position evaluation...
00:48:45.830                                                             🎭 Updating personality context for move: d1e2
00:48:45.830                                                             ✨ Personality context updated for move d1e2 - This is revolutionary!
00:48:45.830                                                             🔍 Checking game end conditions...
00:48:45.931                                                             ✅ Game continues - no end condition detected
00:48:46.634 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.39 (move=5, alternating=ON, FEN=rnbqkb1r/pppppppp/5n)
00:48:46.635                                                             🔄 ALTERNATING: move=5, flip=true, 0.39→-0.39 (diff=1.03)
00:48:46.669 GameViewModel                                               ✅ Evaluation received: -0.39
00:48:46.773 CompetitiveModeActivity                                     📊 Evaluation updated: -0.39
00:48:46.773                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.39, emotionalManager: INITIALIZED
00:48:46.773                                                             🎯 First emotional evaluation: -0.39 (threshold: 1.5)
00:48:46.773                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636126s/25s)
00:48:46.773                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:50:19.439                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
00:50:19.439                                                             🎯 SQUARE TAPPED: row=1, col=4
00:50:19.439                                                             📝 Player color: black
00:50:19.439                                                             🔍 Selected row/col: -1/-1
00:50:19.796                                                             🎯 Selected piece: p at 1, 4
00:50:20.277                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
00:50:20.277                                                             🎯 SQUARE TAPPED: row=3, col=4
00:50:20.277                                                             📝 Player color: black
00:50:20.277                                                             🔍 Selected row/col: 1/4
00:50:20.277                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=3, reaches=false
00:50:20.277                                                             🎯 ATTEMPTING MOVE: e7e5
00:50:20.277                                                             📝 Player color: black
00:50:20.277                                                             🔄 Is player's turn: true
00:50:20.277                                                             🔄 Is white's turn: false
00:50:20.277                                                             ✅ Turn validation passed, making move: e7e5
00:50:20.277 GameViewModel                                               🎯 makePlayerMove called with: e7e5
00:50:20.328                                                             🔍 Validating move: e7e5 (attempt 1)
00:50:20.430                                                             📋 Current position: rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPPQPPP/RNB1KBNR b KQkq - 2 2
00:50:20.481                                                             ⚖️ Move e7e5 legality check: LEGAL
00:50:20.481                                                             ✅ Executing validated move: e7e5
00:50:20.684                                                             📍 New position after move: rnbqkb1r/pppp1ppp/5n2/4p3/4P3/8/PPPPQPPP/RNB1KBNR w KQkq - 0 3
00:50:20.686 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppp1ppp/5n2/4p3/4P3/8/PPPPQPPP/RNB1KBNR w KQkq - 0 3
00:50:20.688                                                             📜 Move history updated: 4 moves
00:50:20.688 GameHistoryManager                                          Move added: e7e5
00:50:20.784 GameViewModel                                               🔍 Requesting position evaluation...
00:50:20.784                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:50:20.784                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:50:20.784                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:50:20.784                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:50:20.784                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:50:20.784                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:50:20.785                                                             🔧 gameRepository class: GameRepository
00:50:20.785                                                             🔧 Current thread: main
00:50:20.785 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:50:22.091 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.34 (move=6, alternating=ON, FEN=rnbqkb1r/pppp1ppp/5n)
00:50:22.091                                                             🔄 ALTERNATING: move=6, flip=false, -0.34→-0.34 (diff=0.05)
00:50:22.092 GameViewModel                                               ✅ Evaluation received: -0.34
00:50:22.192 CompetitiveModeActivity                                     📊 Evaluation updated: -0.34
00:50:22.192                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.34, emotionalManager: INITIALIZED
00:50:22.192                                                             🎯 First emotional evaluation: -0.34 (threshold: 1.5)
00:50:22.192                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636222s/25s)
00:50:22.192                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:50:22.961 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:50:22.963 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #4)
00:50:22.963                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:50:22.963                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pppp1ppp/5n2/4p3/4P3/8/PPPPQPPP/RNB1...
00:50:22.963                                                             🎲 Candidate moves for AI analysis: [d2d4]
00:50:22.963 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:50:22.964                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:50:23.248                                                             📋 Response ID: resp_685896befc8481a19f68f5335c3404a60e3ac2f32e051338
00:50:24.718                                                             🏁 Response completed
00:50:24.719 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d2d4": {"score": 0.0, "reason": "Dull and symmetrical. No imbalance, no co...
00:50:24.721                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 269 of {
                                                                           "move_scores": {
                                                                             "d2d4": {"score": 0.0, "reason": "Dull and symmetrical. No imbalance, no complications—exactly what I avoid."},
                                                                             "Nc3": {"score": 0.7, "reason": "Develops with a hint of tension, but still too quiet for my taste."},
                                                                             "f4": {"score": 1.0, \"reason\": \"Bold, unbalanced, and full of attacking possibilities. This is the move I would choose.\"}
                                                                           },
                                                                           "top_choice": "f4",
                                                                           "style_reasoning": "My style demands complexity and psychological pressure. I seek to unsettle my opponent from the first move.",
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
00:50:24.744 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d2d4
00:50:25.099 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppp1ppp/5n2/4p3/3PP3/8/PPP1QPPP/RNB1KBNR b KQkq - 0 3
00:50:25.104                                                             📜 Move history updated: 5 moves
00:50:25.104 GameHistoryManager                                          Move added: d2d4
00:50:25.104 GameViewModel                                               🔍 Requesting position evaluation...
00:50:25.104                                                             🎭 Updating personality context for move: d2d4
00:50:25.104                                                             ✨ Personality context updated for move d2d4 - This is revolutionary!
00:50:25.105                                                             🔍 Checking game end conditions...
00:50:25.205                                                             ✅ Game continues - no end condition detected
00:50:26.010 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.35 (move=7, alternating=ON, FEN=rnbqkb1r/pppp1ppp/5n)
00:50:26.010                                                             🔄 ALTERNATING: move=7, flip=true, 0.35→-0.35 (diff=0.01)
00:50:26.015 GameViewModel                                               ✅ Evaluation received: -0.35
00:50:26.125 CompetitiveModeActivity                                     📊 Evaluation updated: -0.35
00:50:26.125                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.35, emotionalManager: INITIALIZED
00:50:26.125                                                             🎯 First emotional evaluation: -0.35 (threshold: 1.5)
00:50:26.125                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636226s/25s)
00:50:26.125                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:50:55.558                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=4
00:50:55.558                                                             🎯 SQUARE TAPPED: row=3, col=4
00:50:55.558                                                             📝 Player color: black
00:50:55.558                                                             🔍 Selected row/col: -1/-1
00:50:55.914                                                             🎯 Selected piece: p at 3, 4
00:50:56.299                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=3
00:50:56.299                                                             🎯 SQUARE TAPPED: row=4, col=3
00:50:56.299                                                             📝 Player color: black
00:50:56.299                                                             🔍 Selected row/col: 3/4
00:50:56.299                                                             ♟️ Promotion check: piece=p, fromRow=3, toRow=4, reaches=false
00:50:56.299                                                             🎯 ATTEMPTING MOVE: e5d4
00:50:56.299                                                             📝 Player color: black
00:50:56.299                                                             🔄 Is player's turn: true
00:50:56.299                                                             🔄 Is white's turn: false
00:50:56.299                                                             ✅ Turn validation passed, making move: e5d4
00:50:56.299 GameViewModel                                               🎯 makePlayerMove called with: e5d4
00:50:56.350                                                             🔍 Validating move: e5d4 (attempt 1)
00:50:56.451                                                             📋 Current position: rnbqkb1r/pppp1ppp/5n2/4p3/3PP3/8/PPP1QPPP/RNB1KBNR b KQkq - 0 3
00:50:56.501                                                             ⚖️ Move e5d4 legality check: LEGAL
00:50:56.501                                                             ✅ Executing validated move: e5d4
00:50:56.704                                                             📍 New position after move: rnbqkb1r/pppp1ppp/5n2/8/3pP3/8/PPP1QPPP/RNB1KBNR w KQkq - 0 4
00:50:56.707 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppp1ppp/5n2/8/3pP3/8/PPP1QPPP/RNB1KBNR w KQkq - 0 4
00:50:56.710                                                             📜 Move history updated: 6 moves
00:50:56.710 GameHistoryManager                                          Move added: e5d4
00:50:56.804 GameViewModel                                               🔍 Requesting position evaluation...
00:50:56.804                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:50:56.804                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:50:56.804                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:50:56.805                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:50:56.805                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:50:56.805                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:50:56.805                                                             🔧 gameRepository class: GameRepository
00:50:56.805                                                             🔧 Current thread: main
00:50:56.805 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:50:58.161 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.29 (move=8, alternating=ON, FEN=rnbqkb1r/pppp1ppp/5n)
00:50:58.161                                                             🔄 ALTERNATING: move=8, flip=false, -0.29→-0.29 (diff=0.06)
00:50:58.162 GameViewModel                                               ✅ Evaluation received: -0.29
00:50:58.262 CompetitiveModeActivity                                     📊 Evaluation updated: -0.29
00:50:58.262                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.29, emotionalManager: INITIALIZED
00:50:58.262                                                             🎯 First emotional evaluation: -0.29 (threshold: 1.5)
00:50:58.262                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636258s/25s)
00:50:58.262                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:50:58.682 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:50:58.684 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #5)
00:50:58.684                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:50:58.684                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pppp1ppp/5n2/8/3pP3/8/PPP1QPPP/RNB1K...
00:50:58.684                                                             🎲 Candidate moves for AI analysis: [e4e5]
00:50:58.684 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:50:58.684                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:50:58.986                                                             📋 Response ID: resp_685896e2bcdc819eb9bba7248dabf54302e3f2a9fed33818
00:51:01.000                                                             🏁 Response completed
00:51:01.001 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e4e5": {"score": 0.0, "reason": "Symmetrical and dull, this move leads to ...
00:51:01.002                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
00:51:01.002                                                             🎭 AI preferred moves: []
00:51:01.002                                                             💭 AI reasoning: I seek the fight, the complications, the positions where my calculation and imagination can flourish. Nxd4 embodies this spirit.
00:51:01.025 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e4e5
00:51:01.382 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppp1ppp/5n2/4P3/3p4/8/PPP1QPPP/RNB1KBNR b KQkq - 0 4
00:51:01.386                                                             📜 Move history updated: 7 moves
00:51:01.386 GameHistoryManager                                          Move added: e4e5
00:51:01.386 GameViewModel                                               🔍 Requesting position evaluation...
00:51:01.386                                                             🎭 Updating personality context for move: e4e5
00:51:01.387                                                             ✨ Personality context updated for move e4e5 - This is revolutionary!
00:51:01.387                                                             🔍 Checking game end conditions...
00:51:01.489                                                             ✅ Game continues - no end condition detected
00:51:02.293 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.34 (move=9, alternating=ON, FEN=rnbqkb1r/pppp1ppp/5n)
00:51:02.293                                                             🔄 ALTERNATING: move=9, flip=true, 0.34→-0.34 (diff=0.05)
00:51:02.299 GameViewModel                                               ✅ Evaluation received: -0.34
00:51:02.408 CompetitiveModeActivity                                     📊 Evaluation updated: -0.34
00:51:02.408                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.34, emotionalManager: INITIALIZED
00:51:02.408                                                             🎯 First emotional evaluation: -0.34 (threshold: 1.5)
00:51:02.408                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636262s/25s)
00:51:02.408                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:51:16.193                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
00:51:16.193                                                             🎯 SQUARE TAPPED: row=2, col=5
00:51:16.193                                                             📝 Player color: black
00:51:16.193                                                             🔍 Selected row/col: -1/-1
00:51:16.551                                                             🎯 Selected piece: n at 2, 5
00:51:17.178                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
00:51:17.178                                                             🎯 SQUARE TAPPED: row=3, col=3
00:51:17.178                                                             📝 Player color: black
00:51:17.178                                                             🔍 Selected row/col: 2/5
00:51:17.179                                                             🎯 ATTEMPTING MOVE: f6d5
00:51:17.179                                                             📝 Player color: black
00:51:17.179                                                             🔄 Is player's turn: true
00:51:17.179                                                             🔄 Is white's turn: false
00:51:17.179                                                             ✅ Turn validation passed, making move: f6d5
00:51:17.179 GameViewModel                                               🎯 makePlayerMove called with: f6d5
00:51:17.230                                                             🔍 Validating move: f6d5 (attempt 1)
00:51:17.332                                                             📋 Current position: rnbqkb1r/pppp1ppp/5n2/4P3/3p4/8/PPP1QPPP/RNB1KBNR b KQkq - 0 4
00:51:17.383                                                             ⚖️ Move f6d5 legality check: LEGAL
00:51:17.383                                                             ✅ Executing validated move: f6d5
00:51:17.586                                                             📍 New position after move: rnbqkb1r/pppp1ppp/8/3nP3/3p4/8/PPP1QPPP/RNB1KBNR w KQkq - 1 5
00:51:17.589 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppp1ppp/8/3nP3/3p4/8/PPP1QPPP/RNB1KBNR w KQkq - 1 5
00:51:17.592                                                             📜 Move history updated: 8 moves
00:51:17.592 GameHistoryManager                                          Move added: f6d5
00:51:17.686 GameViewModel                                               🔍 Requesting position evaluation...
00:51:17.686                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:51:17.686                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:51:17.686                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:51:17.687                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:51:17.687                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:51:17.687                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:51:17.687                                                             🔧 gameRepository class: GameRepository
00:51:17.687                                                             🔧 Current thread: main
00:51:17.687 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:51:18.641 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.31 (move=10, alternating=ON, FEN=rnbqkb1r/pppp1ppp/8/)
00:51:18.641                                                             🔄 ALTERNATING: move=10, flip=false, -0.31→-0.31 (diff=0.03)
00:51:18.641 GameViewModel                                               ✅ Evaluation received: -0.31
00:51:18.742 CompetitiveModeActivity                                     📊 Evaluation updated: -0.31
00:51:18.742                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.31, emotionalManager: INITIALIZED
00:51:18.742                                                             🎯 First emotional evaluation: -0.31 (threshold: 1.5)
00:51:18.742                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636278s/25s)
00:51:18.742                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:51:19.533 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:51:19.534 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #6)
00:51:19.534                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:51:19.534                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pppp1ppp/8/3nP3/3p4/8/PPP1QPPP/RNB1K...
00:51:19.534                                                             🎲 Candidate moves for AI analysis: [e2e4]
00:51:19.534 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:51:19.534                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:51:19.775                                                             📋 Response ID: resp_685896f78fd48192ace354e42e3119d80b467725a7961fc5
00:51:21.104 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
00:51:21.584 ResponsesAPI                                                🏁 Response completed
00:51:21.585 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e2e4": {"score": 0.0, "reason": "Passive and symmetrical; no tension or im...
00:51:21.586                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:51:21.586                                                             🎭 AI preferred moves: []
00:51:21.586                                                             💭 AI reasoning: I seek positions that demand original thought and allow for creative combinations. Nc3 invites complexity and psychological warfare.
00:51:21.622 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e2e4
00:51:21.981 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pppp1ppp/8/3nP3/3pQ3/8/PPP2PPP/RNB1KBNR b KQkq - 2 5
00:51:21.986                                                             📜 Move history updated: 9 moves
00:51:21.986 GameHistoryManager                                          Move added: e2e4
00:51:21.986 GameViewModel                                               🔍 Requesting position evaluation...
00:51:21.987                                                             🎭 Updating personality context for move: e2e4
00:51:21.987                                                             ✨ Personality context updated for move e2e4 - This is revolutionary!
00:51:21.987                                                             🔍 Checking game end conditions...
00:51:22.088                                                             ✅ Game continues - no end condition detected
00:51:22.752 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.32 (move=11, alternating=ON, FEN=rnbqkb1r/pppp1ppp/8/)
00:51:22.752                                                             🔄 ALTERNATING: move=11, flip=true, 0.32→-0.32 (diff=0.01)
00:51:22.757 GameViewModel                                               ✅ Evaluation received: -0.32
00:51:22.866 CompetitiveModeActivity                                     📊 Evaluation updated: -0.32
00:51:22.866                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.32, emotionalManager: INITIALIZED
00:51:22.866                                                             🎯 First emotional evaluation: -0.32 (threshold: 1.5)
00:51:22.866                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636282s/25s)
00:51:22.866                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:51:58.150                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
00:51:58.150                                                             🎯 SQUARE TAPPED: row=3, col=3
00:51:58.151                                                             📝 Player color: black
00:51:58.151                                                             🔍 Selected row/col: -1/-1
00:51:58.511                                                             🎯 Selected piece: n at 3, 3
00:51:58.762                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
00:51:58.762                                                             🎯 SQUARE TAPPED: row=1, col=4
00:51:58.762                                                             📝 Player color: black
00:51:58.762                                                             🔍 Selected row/col: 3/3
00:51:58.763                                                             🎯 ATTEMPTING MOVE: d5e7
00:51:58.763                                                             📝 Player color: black
00:51:58.763                                                             🔄 Is player's turn: true
00:51:58.763                                                             🔄 Is white's turn: false
00:51:58.763                                                             ✅ Turn validation passed, making move: d5e7
00:51:58.763 GameViewModel                                               🎯 makePlayerMove called with: d5e7
00:51:58.814                                                             🔍 Validating move: d5e7 (attempt 1)
00:51:58.915                                                             📋 Current position: rnbqkb1r/pppp1ppp/8/3nP3/3pQ3/8/PPP2PPP/RNB1KBNR b KQkq - 2 5
00:51:58.967                                                             ⚖️ Move d5e7 legality check: LEGAL
00:51:58.967                                                             ✅ Executing validated move: d5e7
00:51:59.169                                                             📍 New position after move: rnbqkb1r/ppppnppp/8/4P3/3pQ3/8/PPP2PPP/RNB1KBNR w KQkq - 3 6
00:51:59.171 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppppnppp/8/4P3/3pQ3/8/PPP2PPP/RNB1KBNR w KQkq - 3 6
00:51:59.175                                                             📜 Move history updated: 10 moves
00:51:59.175 GameHistoryManager                                          Move added: d5e7
00:51:59.269 GameViewModel                                               🔍 Requesting position evaluation...
00:51:59.269                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:51:59.269                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:51:59.269                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:51:59.269                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:51:59.269                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:51:59.269                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:51:59.269                                                             🔧 gameRepository class: GameRepository
00:51:59.269                                                             🔧 Current thread: main
00:51:59.269 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:52:00.128 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.23 (move=12, alternating=ON, FEN=rnbqkb1r/ppppnppp/8/)
00:52:00.128                                                             🔄 ALTERNATING: move=12, flip=false, -0.23→-0.23 (diff=0.09)
00:52:00.131 GameViewModel                                               ✅ Evaluation received: -0.23
00:52:00.231 CompetitiveModeActivity                                     📊 Evaluation updated: -0.23
00:52:00.231                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.23, emotionalManager: INITIALIZED
00:52:00.231                                                             🎯 First emotional evaluation: -0.23 (threshold: 1.5)
00:52:00.231                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636320s/25s)
00:52:00.231                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:52:00.916 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:52:00.917 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #7)
00:52:00.917                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:52:00.917                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/ppppnppp/8/4P3/3pQ3/8/PPP2PPP/RNB1KB...
00:52:00.917                                                             🎲 Candidate moves for AI analysis: [g1f3]
00:52:00.917 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:52:00.917                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:52:01.142                                                             📋 Response ID: resp_68589720f02c819e8b825cd91d25673d0d76e9ad8e01ba8e
00:52:02.237                                                             🏁 Response completed
00:52:02.238 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g1f3": {"score": 0.0, "reason": "Too passive and simple for my taste. I se...
00:52:02.238                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
00:52:02.238                                                             🎭 AI preferred moves: [g1f3]
00:52:02.238                                                             💭 AI reasoning: The avoidance of complexity is a betrayal of chess itself. I would never settle for such a quiet move in a critical position.
00:52:02.280 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g1f3
00:52:02.636 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppppnppp/8/4P3/3pQ3/5N2/PPP2PPP/RNB1KB1R b KQkq - 4 6
00:52:02.640                                                             📜 Move history updated: 11 moves
00:52:02.640 GameHistoryManager                                          Move added: g1f3
00:52:02.641 GameViewModel                                               🔍 Requesting position evaluation...
00:52:02.641                                                             🎭 Updating personality context for move: g1f3
00:52:02.641                                                             ✨ Personality context updated for move g1f3 - This is revolutionary!
00:52:02.641                                                             🔍 Checking game end conditions...
00:52:02.742                                                             ✅ Game continues - no end condition detected
00:52:03.550 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.28 (move=13, alternating=ON, FEN=rnbqkb1r/ppppnppp/8/)
00:52:03.551                                                             🔄 ALTERNATING: move=13, flip=true, 0.28→-0.28 (diff=0.05)
00:52:03.553 GameViewModel                                               ✅ Evaluation received: -0.28
00:52:03.654 CompetitiveModeActivity                                     📊 Evaluation updated: -0.28
00:52:03.654                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.28, emotionalManager: INITIALIZED
00:52:03.654                                                             🎯 First emotional evaluation: -0.28 (threshold: 1.5)
00:52:03.654                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636323s/25s)
00:52:03.654                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:52:23.728                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
00:52:23.728                                                             🎯 SQUARE TAPPED: row=0, col=1
00:52:23.728                                                             📝 Player color: black
00:52:23.728                                                             🔍 Selected row/col: -1/-1
00:52:24.086                                                             🎯 Selected piece: n at 0, 1
00:52:24.468                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=2
00:52:24.468                                                             🎯 SQUARE TAPPED: row=2, col=2
00:52:24.468                                                             📝 Player color: black
00:52:24.468                                                             🔍 Selected row/col: 0/1
00:52:24.468                                                             🎯 ATTEMPTING MOVE: b8c6
00:52:24.468                                                             📝 Player color: black
00:52:24.468                                                             🔄 Is player's turn: true
00:52:24.468                                                             🔄 Is white's turn: false
00:52:24.468                                                             ✅ Turn validation passed, making move: b8c6
00:52:24.468 GameViewModel                                               🎯 makePlayerMove called with: b8c6
00:52:24.519                                                             🔍 Validating move: b8c6 (attempt 1)
00:52:24.620                                                             📋 Current position: rnbqkb1r/ppppnppp/8/4P3/3pQ3/5N2/PPP2PPP/RNB1KB1R b KQkq - 4 6
00:52:24.671                                                             ⚖️ Move b8c6 legality check: LEGAL
00:52:24.671                                                             ✅ Executing validated move: b8c6
00:52:24.874                                                             📍 New position after move: r1bqkb1r/ppppnppp/2n5/4P3/3pQ3/5N2/PPP2PPP/RNB1KB1R w KQkq - 5 7
00:52:24.876 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/ppppnppp/2n5/4P3/3pQ3/5N2/PPP2PPP/RNB1KB1R w KQkq - 5 7
00:52:24.880                                                             📜 Move history updated: 12 moves
00:52:24.880 GameHistoryManager                                          Move added: b8c6
00:52:24.974 GameViewModel                                               🔍 Requesting position evaluation...
00:52:24.974                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:52:24.974                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:52:24.974                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:52:24.975                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:52:24.975                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:52:24.975                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:52:24.975                                                             🔧 gameRepository class: GameRepository
00:52:24.975                                                             🔧 Current thread: main
00:52:24.975 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:52:25.980 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.34 (move=14, alternating=ON, FEN=r1bqkb1r/ppppnppp/2n)
00:52:25.980                                                             🔄 ALTERNATING: move=14, flip=false, -0.34→-0.34 (diff=0.06)
00:52:25.981 GameViewModel                                               ✅ Evaluation received: -0.34
00:52:26.081 CompetitiveModeActivity                                     📊 Evaluation updated: -0.34
00:52:26.081                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.34, emotionalManager: INITIALIZED
00:52:26.081                                                             🎯 First emotional evaluation: -0.34 (threshold: 1.5)
00:52:26.081                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636346s/25s)
00:52:26.081                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:52:26.600 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:52:26.601 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #8)
00:52:26.601                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:52:26.601                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/ppppnppp/2n5/4P3/3pQ3/5N2/PPP2PPP/RN...
00:52:26.601                                                             🎲 Candidate moves for AI analysis: [c1f4]
00:52:26.601 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:52:26.601                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:52:26.842                                                             📋 Response ID: resp_6858973aa0fc819c9d8d4145315b23ad0c239be95b5fa387
00:52:28.691                                                             🏁 Response completed
00:52:28.692 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c1f4": {"score": 0.0, "reason": "Passive and undeveloped. Does not create ...
00:52:28.692                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:52:28.693                                                             🎭 AI preferred moves: []
00:52:28.693                                                             💭 AI reasoning: I seek out the battle, not the parade. Nxd4 throws down the gauntlet and forces my opponent into the deep woods of calculation.
00:52:28.724 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c1f4
00:52:29.083 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/ppppnppp/2n5/4P3/3pQB2/5N2/PPP2PPP/RN2KB1R b KQkq - 6 7
00:52:29.087                                                             📜 Move history updated: 13 moves
00:52:29.087 GameHistoryManager                                          Move added: c1f4
00:52:29.087 GameViewModel                                               🔍 Requesting position evaluation...
00:52:29.087                                                             🎭 Updating personality context for move: c1f4
00:52:29.087                                                             ✨ Personality context updated for move c1f4 - This is revolutionary!
00:52:29.087                                                             🔍 Checking game end conditions...
00:52:29.188                                                             ✅ Game continues - no end condition detected
00:52:30.101 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.39 (move=15, alternating=ON, FEN=r1bqkb1r/ppppnppp/2n)
00:52:30.102                                                             🔄 ALTERNATING: move=15, flip=true, 0.39→-0.39 (diff=0.05)
00:52:30.109 GameViewModel                                               ✅ Evaluation received: -0.39
00:52:30.211 CompetitiveModeActivity                                     📊 Evaluation updated: -0.39
00:52:30.211                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.39, emotionalManager: INITIALIZED
00:52:30.211                                                             🎯 First emotional evaluation: -0.39 (threshold: 1.5)
00:52:30.211                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636350s/25s)
00:52:30.211                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:53:01.530                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
00:53:01.530                                                             🎯 SQUARE TAPPED: row=1, col=4
00:53:01.530                                                             📝 Player color: black
00:53:01.530                                                             🔍 Selected row/col: -1/-1
00:53:01.887                                                             🎯 Selected piece: n at 1, 4
00:53:02.280                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=6
00:53:02.280                                                             🎯 SQUARE TAPPED: row=2, col=6
00:53:02.280                                                             📝 Player color: black
00:53:02.280                                                             🔍 Selected row/col: 1/4
00:53:02.280                                                             🎯 ATTEMPTING MOVE: e7g6
00:53:02.280                                                             📝 Player color: black
00:53:02.280                                                             🔄 Is player's turn: true
00:53:02.280                                                             🔄 Is white's turn: false
00:53:02.280                                                             ✅ Turn validation passed, making move: e7g6
00:53:02.281 GameViewModel                                               🎯 makePlayerMove called with: e7g6
00:53:02.331                                                             🔍 Validating move: e7g6 (attempt 1)
00:53:02.432                                                             📋 Current position: r1bqkb1r/ppppnppp/2n5/4P3/3pQB2/5N2/PPP2PPP/RN2KB1R b KQkq - 6 7
00:53:02.484                                                             ⚖️ Move e7g6 legality check: LEGAL
00:53:02.484                                                             ✅ Executing validated move: e7g6
00:53:02.687                                                             📍 New position after move: r1bqkb1r/pppp1ppp/2n3n1/4P3/3pQB2/5N2/PPP2PPP/RN2KB1R w KQkq - 7 8
00:53:02.689 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pppp1ppp/2n3n1/4P3/3pQB2/5N2/PPP2PPP/RN2KB1R w KQkq - 7 8
00:53:02.693                                                             📜 Move history updated: 14 moves
00:53:02.693 GameHistoryManager                                          Move added: e7g6
00:53:02.787 GameViewModel                                               🔍 Requesting position evaluation...
00:53:02.787                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:53:02.787                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:53:02.787                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:53:02.787                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:53:02.787                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:53:02.787                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:53:02.787                                                             🔧 gameRepository class: GameRepository
00:53:02.787                                                             🔧 Current thread: main
00:53:02.787 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:53:04.444 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.42 (move=16, alternating=ON, FEN=r1bqkb1r/pppp1ppp/2n)
00:53:04.444                                                             🔄 ALTERNATING: move=16, flip=false, -0.42→-0.42 (diff=0.03)
00:53:04.444 GameViewModel                                               ✅ Evaluation received: -0.42
00:53:04.545 CompetitiveModeActivity                                     📊 Evaluation updated: -0.42
00:53:04.545                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.42, emotionalManager: INITIALIZED
00:53:04.545                                                             🎯 First emotional evaluation: -0.42 (threshold: 1.5)
00:53:04.545                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636384s/25s)
00:53:04.545                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:53:05.212 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:53:05.213 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #9)
00:53:05.213                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:53:05.213                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pppp1ppp/2n3n1/4P3/3pQB2/5N2/PPP2PPP...
00:53:05.213                                                             🎲 Candidate moves for AI analysis: [b1d2]
00:53:05.214 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:53:05.214                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:53:05.454                                                             📋 Response ID: resp_685897613da081a0ad63a5fc053e02b50b9bb5f61f5a7d5c
00:53:07.093                                                             🏁 Response completed
00:53:07.093 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "b1d2": {"score": 0.0, "reason": "Too passive and retreating for my taste. ...
00:53:07.094                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
00:53:07.094                                                             🎭 AI preferred moves: []
00:53:07.094                                                             💭 AI reasoning: I am drawn to the sharpest swords, not the dullest plowshares. My play is defined by risk, not safety.
00:53:07.122 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b1d2
00:53:07.482 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pppp1ppp/2n3n1/4P3/3pQB2/5N2/PPPN1PPP/R3KB1R b KQkq - 8 8
00:53:07.488                                                             📜 Move history updated: 15 moves
00:53:07.489 GameHistoryManager                                          Move added: b1d2
00:53:07.489 GameViewModel                                               🔍 Requesting position evaluation...
00:53:07.489                                                             🎭 Updating personality context for move: b1d2
00:53:07.489                                                             ✨ Personality context updated for move b1d2 - This is revolutionary!
00:53:07.489                                                             🔍 Checking game end conditions...
00:53:07.591                                                             ✅ Game continues - no end condition detected
00:53:08.653 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.61 (move=17, alternating=ON, FEN=r1bqkb1r/pppp1ppp/2n)
00:53:08.653                                                             🔄 ALTERNATING: move=17, flip=true, 0.61→-0.61 (diff=0.19)
00:53:08.655 GameViewModel                                               ✅ Evaluation received: -0.61
00:53:08.757 CompetitiveModeActivity                                     📊 Evaluation updated: -0.61
00:53:08.757                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.61, emotionalManager: INITIALIZED
00:53:08.757                                                             🎯 First emotional evaluation: -0.61 (threshold: 1.5)
00:53:08.757                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636388s/25s)
00:53:08.757                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:53:28.692                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=5
00:53:28.692                                                             🎯 SQUARE TAPPED: row=0, col=5
00:53:28.692                                                             📝 Player color: black
00:53:28.692                                                             🔍 Selected row/col: -1/-1
00:53:29.050                                                             🎯 Selected piece: b at 0, 5
00:53:29.823                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=2
00:53:29.823                                                             🎯 SQUARE TAPPED: row=3, col=2
00:53:29.823                                                             📝 Player color: black
00:53:29.823                                                             🔍 Selected row/col: 0/5
00:53:29.823                                                             🎯 ATTEMPTING MOVE: f8c5
00:53:29.823                                                             📝 Player color: black
00:53:29.823                                                             🔄 Is player's turn: true
00:53:29.823                                                             🔄 Is white's turn: false
00:53:29.823                                                             ✅ Turn validation passed, making move: f8c5
00:53:29.823 GameViewModel                                               🎯 makePlayerMove called with: f8c5
00:53:29.874                                                             🔍 Validating move: f8c5 (attempt 1)
00:53:29.975                                                             📋 Current position: r1bqkb1r/pppp1ppp/2n3n1/4P3/3pQB2/5N2/PPPN1PPP/R3KB1R b KQkq - 8 8
00:53:30.027                                                             ⚖️ Move f8c5 legality check: LEGAL
00:53:30.027                                                             ✅ Executing validated move: f8c5
00:53:30.229                                                             📍 New position after move: r1bqk2r/pppp1ppp/2n3n1/2b1P3/3pQB2/5N2/PPPN1PPP/R3KB1R w KQkq - 9 9
00:53:30.232 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pppp1ppp/2n3n1/2b1P3/3pQB2/5N2/PPPN1PPP/R3KB1R w KQkq - 9 9
00:53:30.237                                                             📜 Move history updated: 16 moves
00:53:30.237 GameHistoryManager                                          Move added: f8c5
00:53:30.331 GameViewModel                                               🔍 Requesting position evaluation...
00:53:30.331                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:53:30.331                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:53:30.331                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:53:30.331                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:53:30.331                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:53:30.331                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:53:30.331                                                             🔧 gameRepository class: GameRepository
00:53:30.331                                                             🔧 Current thread: main
00:53:30.331 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:53:32.041 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.57 (move=18, alternating=ON, FEN=r1bqk2r/pppp1ppp/2n3)
00:53:32.041                                                             🔄 ALTERNATING: move=18, flip=false, -0.57→-0.57 (diff=0.04)
00:53:32.041 GameViewModel                                               ✅ Evaluation received: -0.57
00:53:32.142 CompetitiveModeActivity                                     📊 Evaluation updated: -0.57
00:53:32.142                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.57, emotionalManager: INITIALIZED
00:53:32.142                                                             🎯 First emotional evaluation: -0.57 (threshold: 1.5)
00:53:32.142                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636412s/25s)
00:53:32.142                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:53:32.458 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:53:32.460 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #10)
00:53:32.460                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:53:32.460                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqk2r/pppp1ppp/2n3n1/2b1P3/3pQB2/5N2/PPPN1P...
00:53:32.460                                                             🎲 Candidate moves for AI analysis: [f4g5]
00:53:32.460 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:53:32.460                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:53:32.794                                                             📋 Response ID: resp_6858977c8080819da228e5b2029f6d400d94b80cfe34b7af
00:53:34.646                                                             🏁 Response completed
00:53:34.646 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f4g5": {"score": 0.0, "reason": "Overly committal and lacking subtlety—fai...
00:53:34.647                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:53:34.647                                                             🎭 AI preferred moves: []
00:53:34.647                                                             💭 AI reasoning: Ne4 embodies my love of dynamic imbalance and psychological pressure. It sets a trap for the opponent and opens the door to beautiful combinations.
00:53:34.669 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f4g5
00:53:35.028 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pppp1ppp/2n3n1/2b1P1B1/3pQ3/5N2/PPPN1PPP/R3KB1R b KQkq - 10 9
00:53:35.033                                                             📜 Move history updated: 17 moves
00:53:35.033 GameHistoryManager                                          Move added: f4g5
00:53:35.033 GameViewModel                                               🔍 Requesting position evaluation...
00:53:35.033                                                             🎭 Updating personality context for move: f4g5
00:53:35.034                                                             ✨ Personality context updated for move f4g5 - This is revolutionary!
00:53:35.034                                                             🔍 Checking game end conditions...
00:53:35.136                                                             ✅ Game continues - no end condition detected
00:53:36.001 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.56 (move=19, alternating=ON, FEN=r1bqk2r/pppp1ppp/2n3)
00:53:36.001                                                             🔄 ALTERNATING: move=19, flip=true, 0.56→-0.56 (diff=0.01)
00:53:36.006 GameViewModel                                               ✅ Evaluation received: -0.56
00:53:36.108 CompetitiveModeActivity                                     📊 Evaluation updated: -0.56
00:53:36.108                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.56, emotionalManager: INITIALIZED
00:53:36.108                                                             🎯 First emotional evaluation: -0.56 (threshold: 1.5)
00:53:36.108                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636416s/25s)
00:53:36.108                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:53:48.255                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=2
00:53:48.255                                                             🎯 SQUARE TAPPED: row=3, col=2
00:53:48.255                                                             📝 Player color: black
00:53:48.255                                                             🔍 Selected row/col: -1/-1
00:53:48.613                                                             🎯 Selected piece: b at 3, 2
00:53:49.085                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
00:53:49.085                                                             🎯 SQUARE TAPPED: row=1, col=4
00:53:49.085                                                             📝 Player color: black
00:53:49.085                                                             🔍 Selected row/col: 3/2
00:53:49.085                                                             🎯 ATTEMPTING MOVE: c5e7
00:53:49.085                                                             📝 Player color: black
00:53:49.085                                                             🔄 Is player's turn: true
00:53:49.085                                                             🔄 Is white's turn: false
00:53:49.085                                                             ✅ Turn validation passed, making move: c5e7
00:53:49.085 GameViewModel                                               🎯 makePlayerMove called with: c5e7
00:53:49.136                                                             🔍 Validating move: c5e7 (attempt 1)
00:53:49.236                                                             📋 Current position: r1bqk2r/pppp1ppp/2n3n1/2b1P1B1/3pQ3/5N2/PPPN1PPP/R3KB1R b KQkq - 10 9
00:53:49.287                                                             ⚖️ Move c5e7 legality check: LEGAL
00:53:49.288                                                             ✅ Executing validated move: c5e7
00:53:49.490                                                             📍 New position after move: r1bqk2r/ppppbppp/2n3n1/4P1B1/3pQ3/5N2/PPPN1PPP/R3KB1R w KQkq - 11 10
00:53:49.492 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/ppppbppp/2n3n1/4P1B1/3pQ3/5N2/PPPN1PPP/R3KB1R w KQkq - 11 10
00:53:49.496                                                             📜 Move history updated: 18 moves
00:53:49.496 GameHistoryManager                                          Move added: c5e7
00:53:49.590 GameViewModel                                               🔍 Requesting position evaluation...
00:53:49.590                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:53:49.590                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:53:49.590                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:53:49.590                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:53:49.590                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:53:49.590                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:53:49.590                                                             🔧 gameRepository class: GameRepository
00:53:49.590                                                             🔧 Current thread: main
00:53:49.590 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:53:50.447 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.53 (move=20, alternating=ON, FEN=r1bqk2r/ppppbppp/2n3)
00:53:50.447                                                             🔄 ALTERNATING: move=20, flip=false, -0.53→-0.53 (diff=0.03)
00:53:50.447 GameViewModel                                               ✅ Evaluation received: -0.53
00:53:50.548 CompetitiveModeActivity                                     📊 Evaluation updated: -0.53
00:53:50.548                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.53, emotionalManager: INITIALIZED
00:53:50.548                                                             🎯 First emotional evaluation: -0.53 (threshold: 1.5)
00:53:50.548                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750636430s/25s)
00:53:50.548                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:53:51.362 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
00:53:51.363 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #11)
00:53:51.363                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:53:51.363                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqk2r/ppppbppp/2n3n1/4P1B1/3pQ3/5N2/PPPN1PP...
00:53:51.363                                                             🎲 Candidate moves for AI analysis: [g5f4]
00:53:51.363 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:53:51.363                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:53:51.535                                                             📋 Response ID: resp_6858978f62c081a18bfc2fdfa535394903be3ea764f7e81e
00:53:54.222                                                             🏁 Response completed
00:53:54.222 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g5f4": {"score": 0.0, "reason": "Passive and retreating—no spark, no chall...
00:53:54.223                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
00:53:54.223                                                             🎭 AI preferred moves: []
00:53:54.223                                                             💭 AI reasoning: I choose the path that offers the richest soil for my combinations. Sacrifices and complications are my true allies.
00:53:54.243 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g5f4
00:53:54.603 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/ppppbppp/2n3n1/4P3/3pQB2/5N2/PPPN1PPP/R3KB1R b KQkq - 12 10
00:53:54.609                                                             📜 Move history updated: 19 moves
00:53:54.609 GameHistoryManager                                          Move added: g5f4
00:53:54.609 GameViewModel                                               🔍 Requesting position evaluation...
00:53:54.610                                                             🎭 Updating personality context for move: g5f4
00:53:54.610                                                             ✨ Personality context updated for move g5f4 - This is revolutionary!
00:53:54.610                                                             🔍 Checking game end conditions...
00:53:54.712                                                             ✅ Game continues - no end condition detected
00:53:55.476 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 2.25 (move=21, alternating=ON, FEN=r1bqk2r/ppppbppp/2n3)
00:53:55.476                                                             🔄 ALTERNATING: move=21, flip=true, 2.25→-2.25 (diff=1.72)
00:53:55.485 GameViewModel                                               ✅ Evaluation received: -2.25
00:53:55.593 CompetitiveModeActivity                                     📊 Evaluation updated: -2.25
00:53:55.593                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.25, emotionalManager: INITIALIZED
00:53:55.594                                                             🎯 First emotional evaluation: -2.25 (threshold: 1.5)
00:53:55.594                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 1750636435s/25s)
00:53:55.594                                                             ✅ Emotional reaction triggered for evaluation: -2.25 (trigger: slight_advantage)
00:53:55.596 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
00:53:55.596                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:53:55.596                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:53:55.596                                                             🎯 Variety params applied: temp=0.85, penalties=0.7/0.45
00:53:58.229                                                             ✅ Response generated: The initiative is mine, but the game’s true beauty will reveal itself in the complications. I must n...
00:53:58.230 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The initiative is mine, but the game’s true beauty will reveal itself in the complications. I must not rush—each move a brushstroke in my masterpiece.
00:53:58.235                                                             🎭 Updated emotion indicator: 🙂
00:53:58.235                                                             🗣️ Speaking master dialogue: The initiative is mine, but the game’s true beauty will reveal itself in the complications. I must not rush—each move a brushstroke in my masterpiece.
00:53:58.235 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
00:53:58.235                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
00:53:58.235                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
00:53:58.235                                                             ✅ Usage context set to: competitive_mode
00:53:58.235 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:53:58.235 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
00:53:58.235                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
00:53:58.237 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
00:54:07.314                                                             🗣️ Master dialogue speech completed
00:54:28.939                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=4
00:54:28.939                                                             🎯 SQUARE TAPPED: row=0, col=4
00:54:28.939                                                             📝 Player color: black
00:54:28.939                                                             🔍 Selected row/col: -1/-1
00:54:29.296                                                             🎯 Selected piece: k at 0, 4
00:54:29.884                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=6
00:54:29.884                                                             🎯 SQUARE TAPPED: row=0, col=6
00:54:29.884                                                             📝 Player color: black
00:54:29.884                                                             🔍 Selected row/col: 0/4
00:54:29.884                                                             🎯 ATTEMPTING MOVE: e8g8
00:54:29.884                                                             📝 Player color: black
00:54:29.884                                                             🔄 Is player's turn: true
00:54:29.884                                                             🔄 Is white's turn: false
00:54:29.884                                                             ✅ Turn validation passed, making move: e8g8
00:54:29.884 GameViewModel                                               🎯 makePlayerMove called with: e8g8
00:54:29.935                                                             🔍 Validating move: e8g8 (attempt 1)
00:54:30.036                                                             📋 Current position: r1bqk2r/ppppbppp/2n3n1/4P3/3pQB2/5N2/PPPN1PPP/R3KB1R b KQkq - 12 10
00:54:30.087                                                             ⚖️ Move e8g8 legality check: LEGAL
00:54:30.087                                                             ✅ Executing validated move: e8g8
00:54:30.290                                                             📍 New position after move: r1bq1rk1/ppppbppp/2n3n1/4P3/3pQB2/5N2/PPPN1PPP/R3KB1R w KQ - 13 11
00:54:30.291 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppppbppp/2n3n1/4P3/3pQB2/5N2/PPPN1PPP/R3KB1R w KQ - 13 11
00:54:30.295                                                             📜 Move history updated: 20 moves
00:54:30.295 GameHistoryManager                                          Move added: e8g8
00:54:30.390 GameViewModel                                               🔍 Requesting position evaluation...
00:54:30.390                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:54:30.390                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:54:30.390                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:54:30.390                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:54:30.390                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:54:30.391                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:54:30.391                                                             🔧 gameRepository class: GameRepository
00:54:30.391                                                             🔧 Current thread: main
00:54:30.391 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:54:31.348 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -1.98 (move=22, alternating=ON, FEN=r1bq1rk1/ppppbppp/2n)
00:54:31.348                                                             🔄 ALTERNATING: move=22, flip=false, -1.98→-1.98 (diff=0.27)
00:54:31.348 GameViewModel                                               ✅ Evaluation received: -1.98
00:54:31.449 CompetitiveModeActivity                                     📊 Evaluation updated: -1.98
00:54:31.449                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -1.98, emotionalManager: INITIALIZED
00:54:31.449                                                             🎯 Evaluation change: 0.26999998 (threshold: 0.8)
00:54:31.449                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 35s/25s)
00:54:31.449                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:54:32.068 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
00:54:32.070 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #12)
00:54:32.070                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:54:32.070                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/ppppbppp/2n3n1/4P3/3pQB2/5N2/PPPN1PP...
00:54:32.070                                                             🎲 Candidate moves for AI analysis: [e1c1]
00:54:32.070 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:54:32.070                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:54:32.391                                                             📋 Response ID: resp_685897b82c4481a187c6bda93c1eb44b056c1833f34fc250
00:54:34.173                                                             🏁 Response completed
00:54:34.174 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e1c1": {"score": 0.0, "reason": "A retreat and simplification—antithetical...
00:54:34.175                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:54:34.175                                                             🎭 AI preferred moves: []
00:54:34.175                                                             💭 AI reasoning: I am drawn to the fire of battle, not the calm of the drawing room. My best play is forged in the heat of complexity.
00:54:34.198 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e1c1
00:54:34.552 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppppbppp/2n3n1/4P3/3pQB2/5N2/PPPN1PPP/2KR1B1R b - - 14 11
00:54:34.554                                                             📜 Move history updated: 21 moves
00:54:34.554 GameHistoryManager                                          Move added: e1c1
00:54:34.554 GameViewModel                                               🔍 Requesting position evaluation...
00:54:34.554                                                             🎭 Updating personality context for move: e1c1
00:54:34.554                                                             ✨ Personality context updated for move e1c1 - This is revolutionary!
00:54:34.554                                                             🔍 Checking game end conditions...
00:54:34.655                                                             ✅ Game continues - no end condition detected
00:54:35.419 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 2.35 (move=23, alternating=ON, FEN=r1bq1rk1/ppppbppp/2n)
00:54:35.420                                                             🔄 ALTERNATING: move=23, flip=true, 2.35→-2.35 (diff=0.37)
00:54:35.422 GameViewModel                                               ✅ Evaluation received: -2.35
00:54:35.531 CompetitiveModeActivity                                     📊 Evaluation updated: -2.35
00:54:35.531                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.35, emotionalManager: INITIALIZED
00:54:35.531                                                             🎯 Evaluation change: 0.099999905 (threshold: 0.8)
00:54:35.531                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 39s/25s)
00:54:35.531                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:55:02.221                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=3
00:55:02.221                                                             🎯 SQUARE TAPPED: row=1, col=3
00:55:02.222                                                             📝 Player color: black
00:55:02.222                                                             🔍 Selected row/col: -1/-1
00:55:02.580                                                             🎯 Selected piece: p at 1, 3
00:55:02.957                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
00:55:02.957                                                             🎯 SQUARE TAPPED: row=3, col=3
00:55:02.957                                                             📝 Player color: black
00:55:02.957                                                             🔍 Selected row/col: 1/3
00:55:02.957                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=3, reaches=false
00:55:02.957                                                             🎯 ATTEMPTING MOVE: d7d5
00:55:02.957                                                             📝 Player color: black
00:55:02.957                                                             🔄 Is player's turn: true
00:55:02.957                                                             🔄 Is white's turn: false
00:55:02.957                                                             ✅ Turn validation passed, making move: d7d5
00:55:02.957 GameViewModel                                               🎯 makePlayerMove called with: d7d5
00:55:03.008                                                             🔍 Validating move: d7d5 (attempt 1)
00:55:03.109                                                             📋 Current position: r1bq1rk1/ppppbppp/2n3n1/4P3/3pQB2/5N2/PPPN1PPP/2KR1B1R b - - 14 11
00:55:03.161                                                             ⚖️ Move d7d5 legality check: LEGAL
00:55:03.161                                                             ✅ Executing validated move: d7d5
00:55:03.365                                                             📍 New position after move: r1bq1rk1/ppp1bppp/2n3n1/3pP3/3pQB2/5N2/PPPN1PPP/2KR1B1R w - d6 0 12
00:55:03.368 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1bppp/2n3n1/3pP3/3pQB2/5N2/PPPN1PPP/2KR1B1R w - d6 0 12
00:55:03.373                                                             📜 Move history updated: 22 moves
00:55:03.373 GameHistoryManager                                          Move added: d7d5
00:55:03.465 GameViewModel                                               🔍 Requesting position evaluation...
00:55:03.466                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:55:03.466                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:55:03.466                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:55:03.466                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:55:03.466                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:55:03.466                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:55:03.466                                                             🔧 gameRepository class: GameRepository
00:55:03.466                                                             🔧 Current thread: main
00:55:03.466 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:55:04.070 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -2.35 (move=24, alternating=ON, FEN=r1bq1rk1/ppp1bppp/2n)
00:55:04.070                                                             🔄 ALTERNATING: move=24, flip=false, -2.35→-2.35 (diff=0.00)
00:55:04.071 GameViewModel                                               ✅ Evaluation received: -2.35
00:55:04.171 CompetitiveModeActivity                                     📊 Evaluation updated: -2.35
00:55:04.171                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -2.35, emotionalManager: INITIALIZED
00:55:04.171                                                             🎯 Evaluation change: 0.099999905 (threshold: 0.8)
00:55:04.171                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 68s/25s)
00:55:04.171                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:55:04.992 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
00:55:04.993 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #13)
00:55:04.993                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:55:04.993                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/ppp1bppp/2n3n1/3pP3/3pQB2/5N2/PPPN1P...
00:55:04.993                                                             🎲 Candidate moves for AI analysis: [e4e1]
00:55:04.993 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:55:04.993                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:55:05.466                                                             📋 Response ID: resp_685897d922f8819eb58a883520dbb2300c090891192dde5a
00:55:07.979                                                             🏁 Response completed
00:55:07.980 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e4e1": {"score": 0.0, "reason": "Retreating the queen into a passive posit...
00:55:07.980                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:55:07.981                                                             🎭 AI preferred moves: []
00:55:07.981                                                             💭 AI reasoning: Bb5 stirs the pot and forces my opponent into difficult decisions. This is where my style flourishes—amid complexity and conflict.
00:55:08.011 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e4e1
00:55:08.370 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1bppp/2n3n1/3pP3/3p1B2/5N2/PPPN1PPP/2KRQB1R b - - 1 12
00:55:08.378                                                             📜 Move history updated: 23 moves
00:55:08.378 GameHistoryManager                                          Move added: e4e1
00:55:08.378 GameViewModel                                               🔍 Requesting position evaluation...
00:55:08.378                                                             🎭 Updating personality context for move: e4e1
00:55:08.379                                                             ✨ Personality context updated for move e4e1 - This is revolutionary!
00:55:08.379                                                             🔍 Checking game end conditions...
00:55:08.481                                                             ✅ Game continues - no end condition detected
00:55:08.995 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 4.67 (move=25, alternating=ON, FEN=r1bq1rk1/ppp1bppp/2n)
00:55:08.995                                                             🔄 ALTERNATING: move=25, flip=true, 4.67→-4.67 (diff=2.32)
00:55:08.997 GameViewModel                                               ✅ Evaluation received: -4.67
00:55:09.099 CompetitiveModeActivity                                     📊 Evaluation updated: -4.67
00:55:09.100                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.67, emotionalManager: INITIALIZED
00:55:09.100                                                             🎯 Evaluation change: 2.42 (threshold: 0.8)
00:55:09.100                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 73s/25s)
00:55:09.100                                                             ✅ Emotional reaction triggered for evaluation: -4.67 (trigger: significant_advantage)
00:55:09.102 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
00:55:09.102                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:55:09.102                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:55:09.102                                                             🎯 Variety params applied: temp=0.75, penalties=0.6/0.35
00:55:10.091                                                             ✅ Response generated: The board sings a beautiful melody—my pieces are in harmony, and the combination is near. At twelve ...
00:55:10.092 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings a beautiful melody—my pieces are in harmony, and the combination is near. At twelve moves, the seeds of victory are already sown.
00:55:10.093                                                             🎭 Updated emotion indicator: 😏
00:55:10.093                                                             🗣️ Speaking master dialogue: The board sings a beautiful melody—my pieces are in harmony, and the combination is near. At twelve moves, the seeds of victory are already sown.
00:55:10.093 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
00:55:10.094                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
00:55:10.094                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
00:55:10.094                                                             ✅ Usage context set to: competitive_mode
00:55:10.094 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:55:10.094 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
00:55:10.094                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
00:55:10.095 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
00:55:19.917                                                             🗣️ Master dialogue speech completed
00:55:29.306                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=6
00:55:29.306                                                             🎯 SQUARE TAPPED: row=2, col=6
00:55:29.306                                                             📝 Player color: black
00:55:29.306                                                             🔍 Selected row/col: -1/-1
00:55:29.664                                                             🎯 Selected piece: n at 2, 6
00:55:30.071                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
00:55:30.071                                                             🎯 SQUARE TAPPED: row=4, col=5
00:55:30.071                                                             📝 Player color: black
00:55:30.071                                                             🔍 Selected row/col: 2/6
00:55:30.071                                                             🎯 ATTEMPTING MOVE: g6f4
00:55:30.071                                                             📝 Player color: black
00:55:30.071                                                             🔄 Is player's turn: true
00:55:30.071                                                             🔄 Is white's turn: false
00:55:30.071                                                             ✅ Turn validation passed, making move: g6f4
00:55:30.071 GameViewModel                                               🎯 makePlayerMove called with: g6f4
00:55:30.122                                                             🔍 Validating move: g6f4 (attempt 1)
00:55:30.223                                                             📋 Current position: r1bq1rk1/ppp1bppp/2n3n1/3pP3/3p1B2/5N2/PPPN1PPP/2KRQB1R b - - 1 12
00:55:30.274                                                             ⚖️ Move g6f4 legality check: LEGAL
00:55:30.274                                                             ✅ Executing validated move: g6f4
00:55:30.476                                                             📍 New position after move: r1bq1rk1/ppp1bppp/2n5/3pP3/3p1n2/5N2/PPPN1PPP/2KRQB1R w - - 0 13
00:55:30.478 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1bppp/2n5/3pP3/3p1n2/5N2/PPPN1PPP/2KRQB1R w - - 0 13
00:55:30.483                                                             📜 Move history updated: 24 moves
00:55:30.483 GameHistoryManager                                          Move added: g6f4
00:55:30.576 GameViewModel                                               🔍 Requesting position evaluation...
00:55:30.576                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:55:30.576                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:55:30.576                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:55:30.576                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:55:30.576                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:55:30.576                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:55:30.576                                                             🔧 gameRepository class: GameRepository
00:55:30.576                                                             🔧 Current thread: main
00:55:30.576 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:55:31.180 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -4.46 (move=26, alternating=ON, FEN=r1bq1rk1/ppp1bppp/2n)
00:55:31.180                                                             🔄 ALTERNATING: move=26, flip=false, -4.46→-4.46 (diff=0.21)
00:55:31.181 GameViewModel                                               ✅ Evaluation received: -4.46
00:55:31.281 CompetitiveModeActivity                                     📊 Evaluation updated: -4.46
00:55:31.281                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.46, emotionalManager: INITIALIZED
00:55:31.281                                                             🎯 Evaluation change: 0.21000004 (threshold: 0.8)
00:55:31.281                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 22s/25s)
00:55:31.281                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
00:55:32.049 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
00:55:32.050 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #14)
00:55:32.050                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:55:32.050                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/ppp1bppp/2n5/3pP3/3p1n2/5N2/PPPN1PPP...
00:55:32.050                                                             🎲 Candidate moves for AI analysis: [g2g3]
00:55:32.051 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:55:32.051                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:55:32.296                                                             📋 Response ID: resp_685897f419b0819d9e7303de7be0c9170c5e69aa9a7eb73e
00:55:33.393                                                             🏁 Response completed
00:55:33.393 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g2g3": {"score": 0.0, "reason": "Passive and defensive—avoids conflict and...
00:55:33.394                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
00:55:33.394                                                             🎭 AI preferred moves: [g2g3]
00:55:33.394                                                             💭 AI reasoning: This move shirks the battle; Alekhine seeks to provoke and outwit, not retreat.
00:55:33.416 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g2g3
00:55:33.771 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1bppp/2n5/3pP3/3p1n2/5NP1/PPPN1P1P/2KRQB1R b - - 0 13
00:55:33.779                                                             📜 Move history updated: 25 moves
00:55:33.780 GameHistoryManager                                          Move added: g2g3
00:55:33.780 GameViewModel                                               🔍 Requesting position evaluation...
00:55:33.780                                                             🎭 Updating personality context for move: g2g3
00:55:33.780                                                             ✨ Personality context updated for move g2g3 - This is revolutionary!
00:55:33.780                                                             🔍 Checking game end conditions...
00:55:33.882                                                             ✅ Game continues - no end condition detected
00:55:34.348 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 4.67 (move=27, alternating=ON, FEN=r1bq1rk1/ppp1bppp/2n)
00:55:34.348                                                             🔄 ALTERNATING: move=27, flip=true, 4.67→-4.67 (diff=0.21)
00:55:34.348 GameViewModel                                               ✅ Evaluation received: -4.67
00:55:34.450 CompetitiveModeActivity                                     📊 Evaluation updated: -4.67
00:55:34.450                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.67, emotionalManager: INITIALIZED
00:55:34.450                                                             🎯 Evaluation change: 0.0 (threshold: 0.8)
00:55:34.450                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 25s/25s)
00:55:34.450                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:55:40.570                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
00:55:40.570                                                             🎯 SQUARE TAPPED: row=4, col=5
00:55:40.570                                                             📝 Player color: black
00:55:40.570                                                             🔍 Selected row/col: -1/-1
00:55:40.929                                                             🎯 Selected piece: n at 4, 5
00:55:41.195                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
00:55:41.195                                                             🎯 SQUARE TAPPED: row=2, col=4
00:55:41.195                                                             📝 Player color: black
00:55:41.195                                                             🔍 Selected row/col: 4/5
00:55:41.195                                                             🎯 ATTEMPTING MOVE: f4e6
00:55:41.195                                                             📝 Player color: black
00:55:41.195                                                             🔄 Is player's turn: true
00:55:41.195                                                             🔄 Is white's turn: false
00:55:41.195                                                             ✅ Turn validation passed, making move: f4e6
00:55:41.195 GameViewModel                                               🎯 makePlayerMove called with: f4e6
00:55:41.246                                                             🔍 Validating move: f4e6 (attempt 1)
00:55:41.348                                                             📋 Current position: r1bq1rk1/ppp1bppp/2n5/3pP3/3p1n2/5NP1/PPPN1P1P/2KRQB1R b - - 0 13
00:55:41.399                                                             ⚖️ Move f4e6 legality check: LEGAL
00:55:41.399                                                             ✅ Executing validated move: f4e6
00:55:41.603                                                             📍 New position after move: r1bq1rk1/ppp1bppp/2n1n3/3pP3/3p4/5NP1/PPPN1P1P/2KRQB1R w - - 1 14
00:55:41.605 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1bppp/2n1n3/3pP3/3p4/5NP1/PPPN1P1P/2KRQB1R w - - 1 14
00:55:41.611                                                             📜 Move history updated: 26 moves
00:55:41.612 GameHistoryManager                                          Move added: f4e6
00:55:41.703 GameViewModel                                               🔍 Requesting position evaluation...
00:55:41.703                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:55:41.703                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:55:41.703                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:55:41.703                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:55:41.703                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:55:41.703                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:55:41.703                                                             🔧 gameRepository class: GameRepository
00:55:41.703                                                             🔧 Current thread: main
00:55:41.703 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:55:42.357 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -4.33 (move=28, alternating=ON, FEN=r1bq1rk1/ppp1bppp/2n)
00:55:42.357                                                             🔄 ALTERNATING: move=28, flip=false, -4.33→-4.33 (diff=0.34)
00:55:42.357 GameViewModel                                               ✅ Evaluation received: -4.33
00:55:42.458 CompetitiveModeActivity                                     📊 Evaluation updated: -4.33
00:55:42.458                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.33, emotionalManager: INITIALIZED
00:55:42.458                                                             🎯 Evaluation change: 0.34000015 (threshold: 0.8)
00:55:42.458                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 33s/25s)
00:55:42.458                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:55:43.145 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 8 historical positions
00:55:43.146 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #15)
00:55:43.146                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:55:43.146                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/ppp1bppp/2n1n3/3pP3/3p4/5NP1/PPPN1P1...
00:55:43.146                                                             🎲 Candidate moves for AI analysis: [g3g4]
00:55:43.146 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:55:43.146                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:55:43.355                                                             📋 Response ID: resp_685897ff2afc81a39dea00ed35dabbdc064adaa59f733395
00:55:45.996                                                             🏁 Response completed
00:55:45.997 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g3g4": {"score": 0.0, "reason": "Too slow and self-limiting. I would never...
00:55:45.998                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 410 of {
                                                                           "move_scores": {
                                                                             "g3g4": {"score": 0.0, "reason": "Too slow and self-limiting. I would never choose a move that dulls the tension."},
                                                                             "e4e5": {"score": 0.9, "reason": "Strikes at the center and opens lines for attack. Fits my love of complexity and initiative."},
                                                                             "b4": {"score": 0.8, "reason": "A provocative flank thrust, creating imbalance and practical chances."},
                                                                             "h4": {"score": 0.7, \"reason\": \"An interesting try for attack, but less grounded in calculation.\"},
                                                                             "f4": {"score": 1.0, \"reason\": \"This is the perfect Alekhine move. Bold, dynamic, and full of latent energy.\"}
                                                                           },
                                                                           "top_choice": "f4",
                                                                           "style_reasoning": "I seek to unsettle my opponent at every turn. F4 is a declaration of war—complex, risky, and full of possibility.",
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
00:55:46.026 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g3g4
00:55:46.385 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1bppp/2n1n3/3pP3/3p2P1/5N2/PPPN1P1P/2KRQB1R b - - 0 14
00:55:46.393                                                             📜 Move history updated: 27 moves
00:55:46.393 GameHistoryManager                                          Move added: g3g4
00:55:46.393 GameViewModel                                               🔍 Requesting position evaluation...
00:55:46.393                                                             🎭 Updating personality context for move: g3g4
00:55:46.393                                                             ✨ Personality context updated for move g3g4 - This is revolutionary!
00:55:46.394                                                             🔍 Checking game end conditions...
00:55:46.495                                                             ✅ Game continues - no end condition detected
00:55:47.065 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 5.07 (move=29, alternating=ON, FEN=r1bq1rk1/ppp1bppp/2n)
00:55:47.065                                                             🔄 ALTERNATING: move=29, flip=true, 5.07→-5.07 (diff=0.74)
00:55:47.065 GameViewModel                                               ✅ Evaluation received: -5.07
00:55:47.175 CompetitiveModeActivity                                     📊 Evaluation updated: -5.07
00:55:47.175                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.07, emotionalManager: INITIALIZED
00:55:47.175                                                             🎯 Evaluation change: 0.4000001 (threshold: 0.8)
00:55:47.175                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 38s/25s)
00:55:47.175                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:55:58.662                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=5
00:55:58.662                                                             🎯 SQUARE TAPPED: row=1, col=5
00:55:58.662                                                             📝 Player color: black
00:55:58.662                                                             🔍 Selected row/col: -1/-1
00:55:59.019                                                             🎯 Selected piece: p at 1, 5
00:55:59.188                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=5
00:55:59.188                                                             🎯 SQUARE TAPPED: row=2, col=5
00:55:59.188                                                             📝 Player color: black
00:55:59.188                                                             🔍 Selected row/col: 1/5
00:55:59.188                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=2, reaches=false
00:55:59.188                                                             🎯 ATTEMPTING MOVE: f7f6
00:55:59.188                                                             📝 Player color: black
00:55:59.188                                                             🔄 Is player's turn: true
00:55:59.188                                                             🔄 Is white's turn: false
00:55:59.188                                                             ✅ Turn validation passed, making move: f7f6
00:55:59.188 GameViewModel                                               🎯 makePlayerMove called with: f7f6
00:55:59.239                                                             🔍 Validating move: f7f6 (attempt 1)
00:55:59.341                                                             📋 Current position: r1bq1rk1/ppp1bppp/2n1n3/3pP3/3p2P1/5N2/PPPN1P1P/2KRQB1R b - - 0 14
00:55:59.392                                                             ⚖️ Move f7f6 legality check: LEGAL
00:55:59.392                                                             ✅ Executing validated move: f7f6
00:55:59.595                                                             📍 New position after move: r1bq1rk1/ppp1b1pp/2n1np2/3pP3/3p2P1/5N2/PPPN1P1P/2KRQB1R w - - 0 15
00:55:59.597 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1b1pp/2n1np2/3pP3/3p2P1/5N2/PPPN1P1P/2KRQB1R w - - 0 15
00:55:59.603                                                             📜 Move history updated: 28 moves
00:55:59.603 GameHistoryManager                                          Move added: f7f6
00:55:59.604 Choreographer                                               Skipped 50 frames!  The application may be doing too much work on its main thread.
00:55:59.695 GameViewModel                                               🔍 Requesting position evaluation...
00:55:59.695                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:55:59.695                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:55:59.695                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:55:59.695                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:55:59.695                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:55:59.695                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:55:59.695                                                             🔧 gameRepository class: GameRepository
00:55:59.695                                                             🔧 Current thread: main
00:55:59.695 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:56:00.351 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -4.57 (move=30, alternating=ON, FEN=r1bq1rk1/ppp1b1pp/2n)
00:56:00.351                                                             🔄 ALTERNATING: move=30, flip=false, -4.57→-4.57 (diff=0.50)
00:56:00.352 GameViewModel                                               ✅ Evaluation received: -4.57
00:56:00.452 CompetitiveModeActivity                                     📊 Evaluation updated: -4.57
00:56:00.452                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -4.57, emotionalManager: INITIALIZED
00:56:00.452                                                             🎯 Evaluation change: 0.099999905 (threshold: 0.8)
00:56:00.452                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 51s/25s)
00:56:00.452                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:56:01.071 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
00:56:01.072 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #16)
00:56:01.072                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:56:01.072                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/ppp1b1pp/2n1np2/3pP3/3p2P1/5N2/PPPN1...
00:56:01.072                                                             🎲 Candidate moves for AI analysis: [f1g2]
00:56:01.072 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:56:01.072                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:56:01.377                                                             📋 Response ID: resp_685898111c3c81919514de212c468a3e0a20c91497349a72
00:56:02.642                                                             🏁 Response completed
00:56:02.642 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f1g2": {"score": 0.0, "reason": "A passive retreat, unnecessary in a posit...
00:56:02.643                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:56:02.643                                                             🎭 AI preferred moves: []
00:56:02.643                                                             💭 AI reasoning: The best move seizes the center and opens lines for the attack—true to my style. I seek to create, not avoid, complexity.
00:56:02.674 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f1g2
00:56:03.029 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1b1pp/2n1np2/3pP3/3p2P1/5N2/PPPN1PBP/2KRQ2R b - - 1 15
00:56:03.039                                                             📜 Move history updated: 29 moves
00:56:03.039 GameHistoryManager                                          Move added: f1g2
00:56:03.039 GameViewModel                                               🔍 Requesting position evaluation...
00:56:03.039                                                             🎭 Updating personality context for move: f1g2
00:56:03.039                                                             ✨ Personality context updated for move f1g2 - This is revolutionary!
00:56:03.040                                                             🔍 Checking game end conditions...
00:56:03.140                                                             ✅ Game continues - no end condition detected
00:56:03.619 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 6.11 (move=31, alternating=ON, FEN=r1bq1rk1/ppp1b1pp/2n)
00:56:03.619                                                             🔄 ALTERNATING: move=31, flip=true, 6.11→-6.11 (diff=1.54)
00:56:03.620 GameViewModel                                               ✅ Evaluation received: -6.11
00:56:03.722 CompetitiveModeActivity                                     📊 Evaluation updated: -6.11
00:56:03.722                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.11, emotionalManager: INITIALIZED
00:56:03.722                                                             🎯 Evaluation change: 1.44 (threshold: 0.8)
00:56:03.723                                                             🔍 Emotional reaction check - Change: true, Cooldown: true (time since last: 54s/25s)
00:56:03.723                                                             ✅ Emotional reaction triggered for evaluation: -6.11 (trigger: significant_advantage)
00:56:03.724 OpenAIService                                               🔍 Model check: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD - isFineTuned: true, isKnown: true
00:56:03.724                                                             ✅ GUARDRAIL PASSED: Fine-tuned model validated: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:56:03.724                                                             🎭 Using FINE-TUNED model with variety: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:56:03.724                                                             🎯 Variety params applied: temp=0.8, penalties=0.65/0.4
00:56:05.188                                                             ✅ Response generated: The board sings in my favor—every piece harmonizes in a deadly combination. Victory is not given, it...
00:56:05.189 CompetitiveModeActivity                                     🧠 AI-generated emotional response for alekhine: The board sings in my favor—every piece harmonizes in a deadly combination. Victory is not given, it is composed.
00:56:05.190                                                             🎭 Updated emotion indicator: 😏
00:56:05.190                                                             🗣️ Speaking master dialogue: The board sings in my favor—every piece harmonizes in a deadly combination. Victory is not given, it is composed.
00:56:05.190 TTSServiceManager                                           ⚠️ DEPRECATED: getOpenAITTSService called - redirecting to ElevenLabs
00:56:05.190                                                             🎤 GUARDRAIL: Creating ElevenLabs-only OpenAI-compatible wrapper
00:56:05.191                                                             🎤 ElevenLabs-ONLY wrapper created - ALL methods overridden
00:56:05.191                                                             ✅ Usage context set to: competitive_mode
00:56:05.191 OpenAIService                                               ✅ Selected fine-tuned model for alekhine: ft:gpt-4.1-mini-2025-04-14:personal:alekhine:BePlLXyD
00:56:05.191 TTSServiceManager                                           🎭 Speaking with specific master: alekhine (bypassing global preference)
00:56:05.191                                                             🎭 Setting emotional state for alekhine: analytical (intensity: 0.5)
00:56:05.193 CompetitiveModeActivity                                     ✅ TTS request sent for master: alekhine
00:56:14.290                                                             🗣️ Master dialogue speech completed
00:56:18.157                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
00:56:18.157                                                             🎯 SQUARE TAPPED: row=2, col=4
00:56:18.157                                                             📝 Player color: black
00:56:18.157                                                             🔍 Selected row/col: -1/-1
00:56:18.514                                                             🎯 Selected piece: n at 2, 4
00:56:18.771                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
00:56:18.771                                                             🎯 SQUARE TAPPED: row=4, col=5
00:56:18.771                                                             📝 Player color: black
00:56:18.771                                                             🔍 Selected row/col: 2/4
00:56:18.771                                                             🎯 ATTEMPTING MOVE: e6f4
00:56:18.771                                                             📝 Player color: black
00:56:18.771                                                             🔄 Is player's turn: true
00:56:18.771                                                             🔄 Is white's turn: false
00:56:18.771                                                             ✅ Turn validation passed, making move: e6f4
00:56:18.771 GameViewModel                                               🎯 makePlayerMove called with: e6f4
00:56:18.822                                                             🔍 Validating move: e6f4 (attempt 1)
00:56:18.923                                                             📋 Current position: r1bq1rk1/ppp1b1pp/2n1np2/3pP3/3p2P1/5N2/PPPN1PBP/2KRQ2R b - - 1 15
00:56:18.975                                                             ⚖️ Move e6f4 legality check: LEGAL
00:56:18.975                                                             ✅ Executing validated move: e6f4
00:56:19.177                                                             📍 New position after move: r1bq1rk1/ppp1b1pp/2n2p2/3pP3/3p1nP1/5N2/PPPN1PBP/2KRQ2R w - - 2 16
00:56:19.178 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1b1pp/2n2p2/3pP3/3p1nP1/5N2/PPPN1PBP/2KRQ2R w - - 2 16
00:56:19.180                                                             📜 Move history updated: 30 moves
00:56:19.180 GameHistoryManager                                          Move added: e6f4
00:56:19.277 GameViewModel                                               🔍 Requesting position evaluation...
00:56:19.277                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:56:19.277                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:56:19.277                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:56:19.277                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:56:19.277                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:56:19.277                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:56:19.277                                                             🔧 gameRepository class: GameRepository
00:56:19.277                                                             🔧 Current thread: main
00:56:19.278 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:56:19.883 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -5.67 (move=32, alternating=ON, FEN=r1bq1rk1/ppp1b1pp/2n)
00:56:19.883                                                             🔄 ALTERNATING: move=32, flip=false, -5.67→-5.67 (diff=0.44)
00:56:19.883 GameViewModel                                               ✅ Evaluation received: -5.67
00:56:19.984 CompetitiveModeActivity                                     📊 Evaluation updated: -5.67
00:56:19.984                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -5.67, emotionalManager: INITIALIZED
00:56:19.984                                                             🎯 Evaluation change: 0.44000006 (threshold: 0.8)
00:56:19.984                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 16s/25s)
00:56:19.984                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
00:56:20.648 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
00:56:20.650 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #17)
00:56:20.650                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:56:20.650                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/ppp1b1pp/2n2p2/3pP3/3p1nP1/5N2/PPPN1...
00:56:20.650                                                             🎲 Candidate moves for AI analysis: [h2h4]
00:56:20.650 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:56:20.650                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:56:20.935                                                             📋 Response ID: resp_68589824a91c819ca6818472299790920c5fe8b96d64e5bc
00:56:22.675                                                             🏁 Response completed
00:56:22.675 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "h2h4": {"score": 0.0, "reason": "A flank pawn push with no immediate conse...
00:56:22.676                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
00:56:22.676                                                             🎭 AI preferred moves: []
00:56:22.676                                                             💭 AI reasoning: The e6 sacrifice captures the essence of my style: bold, imaginative, and relentlessly seeking the initiative. I would rather sacrifice a pawn than accept a dull equality.
00:56:22.709 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: h2h4
00:56:23.064 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1b1pp/2n2p2/3pP3/3p1nPP/5N2/PPPN1PB1/2KRQ2R b - - 0 16
00:56:23.073                                                             📜 Move history updated: 31 moves
00:56:23.073 GameHistoryManager                                          Move added: h2h4
00:56:23.073 GameViewModel                                               🔍 Requesting position evaluation...
00:56:23.073                                                             🎭 Updating personality context for move: h2h4
00:56:23.074                                                             ✨ Personality context updated for move h2h4 - This is revolutionary!
00:56:23.074                                                             🔍 Checking game end conditions...
00:56:23.174                                                             ✅ Game continues - no end condition detected
00:56:23.646 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 6.37 (move=33, alternating=ON, FEN=r1bq1rk1/ppp1b1pp/2n)
00:56:23.646                                                             🔄 ALTERNATING: move=33, flip=true, 6.37→-6.37 (diff=0.70)
00:56:23.647 GameViewModel                                               ✅ Evaluation received: -6.37
00:56:23.749 CompetitiveModeActivity                                     📊 Evaluation updated: -6.37
00:56:23.749                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.37, emotionalManager: INITIALIZED
00:56:23.749                                                             🎯 Evaluation change: 0.25999975 (threshold: 0.8)
00:56:23.749                                                             🔍 Emotional reaction check - Change: false, Cooldown: false (time since last: 20s/25s)
00:56:23.749                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: false
00:56:33.600                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
00:56:33.600                                                             🎯 SQUARE TAPPED: row=4, col=5
00:56:33.601                                                             📝 Player color: black
00:56:33.601                                                             🔍 Selected row/col: -1/-1
00:56:33.959                                                             🎯 Selected piece: n at 4, 5
00:56:34.153                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=6
00:56:34.153                                                             🎯 SQUARE TAPPED: row=6, col=6
00:56:34.153                                                             📝 Player color: black
00:56:34.153                                                             🔍 Selected row/col: 4/5
00:56:34.153                                                             🎯 ATTEMPTING MOVE: f4g2
00:56:34.153                                                             📝 Player color: black
00:56:34.153                                                             🔄 Is player's turn: true
00:56:34.153                                                             🔄 Is white's turn: false
00:56:34.153                                                             ✅ Turn validation passed, making move: f4g2
00:56:34.153 GameViewModel                                               🎯 makePlayerMove called with: f4g2
00:56:34.204                                                             🔍 Validating move: f4g2 (attempt 1)
00:56:34.304                                                             📋 Current position: r1bq1rk1/ppp1b1pp/2n2p2/3pP3/3p1nPP/5N2/PPPN1PB1/2KRQ2R b - - 0 16
00:56:34.356                                                             ⚖️ Move f4g2 legality check: LEGAL
00:56:34.356                                                             ✅ Executing validated move: f4g2
00:56:34.558                                                             📍 New position after move: r1bq1rk1/ppp1b1pp/2n2p2/3pP3/3p2PP/5N2/PPPN1Pn1/2KRQ2R w - - 0 17
00:56:34.561 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1b1pp/2n2p2/3pP3/3p2PP/5N2/PPPN1Pn1/2KRQ2R w - - 0 17
00:56:34.568                                                             📜 Move history updated: 32 moves
00:56:34.568 GameHistoryManager                                          Move added: f4g2
00:56:34.569 Choreographer                                               Skipped 50 frames!  The application may be doing too much work on its main thread.
00:56:34.658 GameViewModel                                               🔍 Requesting position evaluation...
00:56:34.659                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:56:34.659                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:56:34.659                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:56:34.659                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:56:34.659                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:56:34.659                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:56:34.659                                                             🔧 gameRepository class: GameRepository
00:56:34.659                                                             🔧 Current thread: main
00:56:34.659 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:56:35.213 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -6.33 (move=34, alternating=ON, FEN=r1bq1rk1/ppp1b1pp/2n)
00:56:35.213                                                             🔄 ALTERNATING: move=34, flip=false, -6.33→-6.33 (diff=0.04)
00:56:35.213 GameViewModel                                               ✅ Evaluation received: -6.33
00:56:35.314 CompetitiveModeActivity                                     📊 Evaluation updated: -6.33
00:56:35.314                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.33, emotionalManager: INITIALIZED
00:56:35.314                                                             🎯 Evaluation change: 0.21999979 (threshold: 0.8)
00:56:35.314                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 31s/25s)
00:56:35.314                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:56:36.033 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
00:56:36.034 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #18)
00:56:36.034                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:56:36.034                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/ppp1b1pp/2n2p2/3pP3/3p2PP/5N2/PPPN1P...
00:56:36.034                                                             🎲 Candidate moves for AI analysis: [e5f6]
00:56:36.034 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:56:36.034                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:56:36.296                                                             📋 Response ID: resp_685898340f8c81a18fd5644a69e074100b950471fa7509cb
00:56:38.893                                                             🏁 Response completed
00:56:38.895 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e5f6": {"score": 0.0, "reason": "A sterile exchange that dissipates tensio...
00:56:38.895                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
00:56:38.895                                                             🎭 AI preferred moves: []
00:56:38.895                                                             💭 AI reasoning: Alekhine's chess is about creating problems for his opponent to solve, not following the path of least resistance. He would embrace the messy, double-edged battles.
00:56:38.918 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e5f6
00:56:39.277 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1b1pp/2n2P2/3p4/3p2PP/5N2/PPPN1Pn1/2KRQ2R b - - 0 17
00:56:39.287                                                             📜 Move history updated: 33 moves
00:56:39.287 GameHistoryManager                                          Move added: e5f6
00:56:39.287 GameViewModel                                               🔍 Requesting position evaluation...
00:56:39.288                                                             🎭 Updating personality context for move: e5f6
00:56:39.288                                                             ✨ Personality context updated for move e5f6 - This is revolutionary!
00:56:39.288                                                             🔍 Checking game end conditions...
00:56:39.389                                                             ✅ Game continues - no end condition detected
00:56:39.853 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 6.63 (move=35, alternating=ON, FEN=r1bq1rk1/ppp1b1pp/2n)
00:56:39.853                                                             🔄 ALTERNATING: move=35, flip=true, 6.63→-6.63 (diff=0.30)
00:56:39.855 GameViewModel                                               ✅ Evaluation received: -6.63
00:56:39.957 CompetitiveModeActivity                                     📊 Evaluation updated: -6.63
00:56:39.958                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.63, emotionalManager: INITIALIZED
00:56:39.958                                                             🎯 Evaluation change: 0.52 (threshold: 0.8)
00:56:39.958                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 36s/25s)
00:56:39.958                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:56:49.547                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=6
00:56:49.547                                                             🎯 SQUARE TAPPED: row=6, col=6
00:56:49.547                                                             📝 Player color: black
00:56:49.547                                                             🔍 Selected row/col: -1/-1
00:56:49.905                                                             🎯 Selected piece: n at 6, 6
00:56:50.085                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=4
00:56:50.085                                                             🎯 SQUARE TAPPED: row=7, col=4
00:56:50.085                                                             📝 Player color: black
00:56:50.086                                                             🔍 Selected row/col: 6/6
00:56:50.086                                                             🎯 ATTEMPTING MOVE: g2e1
00:56:50.086                                                             📝 Player color: black
00:56:50.086                                                             🔄 Is player's turn: true
00:56:50.086                                                             🔄 Is white's turn: false
00:56:50.086                                                             ✅ Turn validation passed, making move: g2e1
00:56:50.086 GameViewModel                                               🎯 makePlayerMove called with: g2e1
00:56:50.137                                                             🔍 Validating move: g2e1 (attempt 1)
00:56:50.237                                                             📋 Current position: r1bq1rk1/ppp1b1pp/2n2P2/3p4/3p2PP/5N2/PPPN1Pn1/2KRQ2R b - - 0 17
00:56:50.289                                                             ⚖️ Move g2e1 legality check: LEGAL
00:56:50.289                                                             ✅ Executing validated move: g2e1
00:56:50.492                                                             📍 New position after move: r1bq1rk1/ppp1b1pp/2n2P2/3p4/3p2PP/5N2/PPPN1P2/2KRn2R w - - 0 18
00:56:50.494 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
00:56:50.497 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1b1pp/2n2P2/3p4/3p2PP/5N2/PPPN1P2/2KRn2R w - - 0 18
00:56:50.503                                                             📜 Move history updated: 34 moves
00:56:50.503 GameHistoryManager                                          Move added: g2e1
00:56:50.592 GameViewModel                                               🔍 Requesting position evaluation...
00:56:50.592                                                             🎭 Using PERSONALITY ENGINE for move calculation!
00:56:50.592                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
00:56:50.592                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
00:56:50.592                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
00:56:50.592                                                             🔧 DEBUG: gameRepository instance = NOT NULL
00:56:50.593                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
00:56:50.593                                                             🔧 gameRepository class: GameRepository
00:56:50.593                                                             🔧 Current thread: main
00:56:50.593 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
00:56:51.145 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -6.70 (move=36, alternating=ON, FEN=r1bq1rk1/ppp1b1pp/2n)
00:56:51.145                                                             🔄 ALTERNATING: move=36, flip=false, -6.70→-6.70 (diff=0.07)
00:56:51.145 GameViewModel                                               ✅ Evaluation received: -6.70
00:56:51.246 CompetitiveModeActivity                                     📊 Evaluation updated: -6.7
00:56:51.246                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.7, emotionalManager: INITIALIZED
00:56:51.246                                                             🎯 Evaluation change: 0.5899997 (threshold: 0.8)
00:56:51.246                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 47s/25s)
00:56:51.246                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
00:56:51.914 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
00:56:51.915 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #19)
00:56:51.915                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:56:51.915                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/ppp1b1pp/2n2P2/3p4/3p2PP/5N2/PPPN1P2...
00:56:51.915                                                             🎲 Candidate moves for AI analysis: [f6e7]
00:56:51.915 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
00:56:51.915                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
00:56:52.169                                                             📋 Response ID: resp_68589843f01081a1ae4e04f7232408b40b0db1146f26e82c
00:56:55.007                                                             🏁 Response completed
00:56:55.008 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f6e7": {"score": 0.0, "reason": "A simple queen exchange? No, this dulls t...
00:56:55.008                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
00:56:55.008                                                             🎭 AI preferred moves: []
00:56:55.008                                                             💭 AI reasoning: Alekhine seeks to complicate and dominate the battlefield, not simplify and share the spoils. His best play is born of struggle and sacrifice.
00:56:55.032 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f6e7
00:56:55.391 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bq1rk1/ppp1P1pp/2n5/3p4/3p2PP/5N2/PPPN1P2/2KRn2R b - - 0 18
00:56:55.401                                                             📜 Move history updated: 35 moves
00:56:55.401 GameHistoryManager                                          Move added: f6e7
00:56:55.401 GameViewModel                                               🔍 Requesting position evaluation...
00:56:55.401                                                             🎭 Updating personality context for move: f6e7
00:56:55.401                                                             ✨ Personality context updated for move f6e7 - This is revolutionary!
00:56:55.401                                                             🔍 Checking game end conditions...
00:56:55.503                                                             ✅ Game continues - no end condition detected
00:56:55.961 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 6.76 (move=37, alternating=ON, FEN=r1bq1rk1/ppp1P1pp/2n)
00:56:55.961                                                             🔄 ALTERNATING: move=37, flip=true, 6.76→-6.76 (diff=0.06)
00:56:55.963 GameViewModel                                               ✅ Evaluation received: -6.76
00:56:56.065 CompetitiveModeActivity                                     📊 Evaluation updated: -6.76
00:56:56.065                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -6.76, emotionalManager: INITIALIZED
00:56:56.065                                                             🎯 Evaluation change: 0.6500001 (threshold: 0.8)
00:56:56.065                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 52s/25s)
00:56:56.065                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true