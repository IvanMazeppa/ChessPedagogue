                Move added: f6d5
21:13:39.690 GameViewModel                                               🔍 Requesting position evaluation...
21:13:39.690                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:13:39.690                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:13:39.690                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:13:39.690                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:13:39.690                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:13:39.690                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:13:39.690                                                             🔧 gameRepository class: GameRepository
21:13:39.690                                                             🔧 Current thread: main
21:13:39.690 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:13:40.747 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.13 (move=10, alternating=ON, FEN=rnbqkb1r/ppp1pppp/8/)
21:13:40.747                                                             🔄 ALTERNATING: move=10, flip=false, 0.13→0.13 (diff=0.05)
21:13:40.747 GameViewModel                                               ✅ Evaluation received: 0.13
21:13:40.848 CompetitiveModeActivity                                     📊 Evaluation updated: 0.13
21:13:40.848                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.13, emotionalManager: INITIALIZED
21:13:40.848                                                             🎯 First emotional evaluation: 0.13 (threshold: 1.5)
21:13:40.848                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623220s/25s)
21:13:40.848                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:13:41.443 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:13:41.444 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #5)
21:13:41.444                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:13:41.444                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/ppp1pppp/8/3n4/8/3P4/PPP2PPP/RNBQKBN...
21:13:41.444                                                             🎲 Candidate moves for AI analysis: [d3d4]
21:13:41.444 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:13:41.444                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:13:41.632                                                             📋 Response ID: resp_685863f57110819ea3febe10f61ab5a701a6f7cb3bd7ec86
21:13:42.471                                                             🏁 Response completed
21:13:42.471 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d3d4": {"score": 0.0, "reason": "Dull and symmetrical. No tension, no figh...
21:13:42.472                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
21:13:42.472                                                             🎭 AI preferred moves: []
21:13:42.472                                                             💭 AI reasoning: Such a move is beneath notice. I seek storms, not calm seas.
21:13:42.506 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d3d4
21:13:42.865 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/ppp1pppp/8/3n4/3P4/8/PPP2PPP/RNBQKBNR b KQkq - 0 4
21:13:42.868                                                             📜 Move history updated: 7 moves
21:13:42.869 GameHistoryManager                                          Move added: d3d4
21:13:42.869 GameViewModel                                               🔍 Requesting position evaluation...
21:13:42.869                                                             🎭 Updating personality context for move: d3d4
21:13:42.869                                                             ✨ Personality context updated for move d3d4 - This is revolutionary!
21:13:42.870                                                             🔍 Checking game end conditions...
21:13:42.972                                                             ✅ Game continues - no end condition detected
21:13:43.778 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.10 (move=11, alternating=ON, FEN=rnbqkb1r/ppp1pppp/8/)
21:13:43.778                                                             🔄 ALTERNATING: move=11, flip=true, -0.10→0.10 (diff=0.03)
21:13:43.781 GameViewModel                                               ✅ Evaluation received: 0.10
21:13:43.890 CompetitiveModeActivity                                     📊 Evaluation updated: 0.1
21:13:43.890                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.1, emotionalManager: INITIALIZED
21:13:43.890                                                             🎯 First emotional evaluation: 0.1 (threshold: 1.5)
21:13:43.890                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623223s/25s)
21:13:43.890                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:03.557                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=2
21:14:03.557                                                             🎯 SQUARE TAPPED: row=0, col=2
21:14:03.557                                                             📝 Player color: black
21:14:03.557                                                             🔍 Selected row/col: -1/-1
21:14:03.914                                                             🎯 Selected piece: b at 0, 2
21:14:04.485                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=5
21:14:04.485                                                             🎯 SQUARE TAPPED: row=3, col=5
21:14:04.485                                                             📝 Player color: black
21:14:04.485                                                             🔍 Selected row/col: 0/2
21:14:04.485                                                             🎯 ATTEMPTING MOVE: c8f5
21:14:04.485                                                             📝 Player color: black
21:14:04.485                                                             🔄 Is player's turn: true
21:14:04.485                                                             🔄 Is white's turn: false
21:14:04.485                                                             ✅ Turn validation passed, making move: c8f5
21:14:04.485 GameViewModel                                               🎯 makePlayerMove called with: c8f5
21:14:04.536                                                             🔍 Validating move: c8f5 (attempt 1)
21:14:04.637                                                             📋 Current position: rnbqkb1r/ppp1pppp/8/3n4/3P4/8/PPP2PPP/RNBQKBNR b KQkq - 0 4
21:14:04.688                                                             ⚖️ Move c8f5 legality check: LEGAL
21:14:04.688                                                             ✅ Executing validated move: c8f5
21:14:04.890                                                             📍 New position after move: rn1qkb1r/ppp1pppp/8/3n1b2/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 1 5
21:14:04.894 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkb1r/ppp1pppp/8/3n1b2/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 1 5
21:14:04.897                                                             📜 Move history updated: 8 moves
21:14:04.897 GameHistoryManager                                          Move added: c8f5
21:14:04.991 GameViewModel                                               🔍 Requesting position evaluation...
21:14:04.991                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:14:04.991                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:14:04.991                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:14:04.991                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:14:04.991                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:14:04.991                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:14:04.991                                                             🔧 gameRepository class: GameRepository
21:14:04.991                                                             🔧 Current thread: main
21:14:04.991 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:14:06.047 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.12 (move=12, alternating=ON, FEN=rn1qkb1r/ppp1pppp/8/)
21:14:06.047                                                             🔄 ALTERNATING: move=12, flip=false, 0.12→0.12 (diff=0.02)
21:14:06.047 GameViewModel                                               ✅ Evaluation received: 0.12
21:14:06.148 CompetitiveModeActivity                                     📊 Evaluation updated: 0.12
21:14:06.148                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.12, emotionalManager: INITIALIZED
21:14:06.148                                                             🎯 First emotional evaluation: 0.12 (threshold: 1.5)
21:14:06.148                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623246s/25s)
21:14:06.148                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:06.747 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:14:06.749 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #6)
21:14:06.749                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:06.749                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rn1qkb1r/ppp1pppp/8/3n1b2/3P4/8/PPP2PPP/RNBQK...
21:14:06.749                                                             🎲 Candidate moves for AI analysis: [g1f3]
21:14:06.749 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:06.749                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:14:06.948                                                             📋 Response ID: resp_6858640ec738819e8db0482b63d0a93109498f6998004286
21:14:08.865                                                             🏁 Response completed
21:14:08.866 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g1f3": {"score": 0.0, "reason": "Passive and symmetrical. Does not create ...
21:14:08.868                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 273 of {
                                                                           "move_scores": {
                                                                             "g1f3": {"score": 0.0, "reason": "Passive and symmetrical. Does not create imbalance or pressure."},
                                                                             "c4": {"score": 0.8, "reason": "Challenges the center and invites complexity. Fits my preference for dynamic play."},
                                                                             "e4": {"score": 1.0, \"reason\": \"Direct, confrontational, and full of possibilities for attack. Embodies my fighting spirit.\"},
                                                                             "d5": {"score": 0.5, \"reason\": \"Interesting, but risks simplifying the position. Not my first choice.\"}
                                                                           },
                                                                           \"top_choice\": \"e4\",
                                                                           \"style_reasoning\": \"I seek to dominate the center and unsettle my opponent from the first move. My play is marked by boldness and a search for the initiative.\",
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
21:14:08.901 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g1f3
21:14:09.260 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkb1r/ppp1pppp/8/3n1b2/3P4/5N2/PPP2PPP/RNBQKB1R b KQkq - 2 5
21:14:09.263                                                             📜 Move history updated: 9 moves
21:14:09.263 GameHistoryManager                                          Move added: g1f3
21:14:09.263 GameViewModel                                               🔍 Requesting position evaluation...
21:14:09.264                                                             🎭 Updating personality context for move: g1f3
21:14:09.264                                                             ✨ Personality context updated for move g1f3 - This is revolutionary!
21:14:09.264                                                             🔍 Checking game end conditions...
21:14:09.366                                                             ✅ Game continues - no end condition detected
21:14:10.227 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.10 (move=13, alternating=ON, FEN=rn1qkb1r/ppp1pppp/8/)
21:14:10.227                                                             🔄 ALTERNATING: move=13, flip=true, -0.10→0.10 (diff=0.02)
21:14:10.235 GameViewModel                                               ✅ Evaluation received: 0.10
21:14:10.337 CompetitiveModeActivity                                     📊 Evaluation updated: 0.1
21:14:10.337                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.1, emotionalManager: INITIALIZED
21:14:10.337                                                             🎯 First emotional evaluation: 0.1 (threshold: 1.5)
21:14:10.337                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623250s/25s)
21:14:10.337                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:17.124                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
21:14:17.124                                                             🎯 SQUARE TAPPED: row=1, col=4
21:14:17.124                                                             📝 Player color: black
21:14:17.125                                                             🔍 Selected row/col: -1/-1
21:14:17.484                                                             🎯 Selected piece: p at 1, 4
21:14:17.836                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=4
21:14:17.836                                                             🎯 SQUARE TAPPED: row=2, col=4
21:14:17.836                                                             📝 Player color: black
21:14:17.836                                                             🔍 Selected row/col: 1/4
21:14:17.836                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=2, reaches=false
21:14:17.836                                                             🎯 ATTEMPTING MOVE: e7e6
21:14:17.836                                                             📝 Player color: black
21:14:17.836                                                             🔄 Is player's turn: true
21:14:17.836                                                             🔄 Is white's turn: false
21:14:17.836                                                             ✅ Turn validation passed, making move: e7e6
21:14:17.836 GameViewModel                                               🎯 makePlayerMove called with: e7e6
21:14:17.838                                                             🔍 Validating move: e7e6 (attempt 1)
21:14:17.938                                                             📋 Current position: rn1qkb1r/ppp1pppp/8/3n1b2/3P4/5N2/PPP2PPP/RNBQKB1R b KQkq - 2 5
21:14:17.990                                                             ⚖️ Move e7e6 legality check: LEGAL
21:14:17.990                                                             ✅ Executing validated move: e7e6
21:14:18.194                                                             📍 New position after move: rn1qkb1r/ppp2ppp/4p3/3n1b2/3P4/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 6
21:14:18.196 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkb1r/ppp2ppp/4p3/3n1b2/3P4/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 6
21:14:18.199                                                             📜 Move history updated: 10 moves
21:14:18.199 GameHistoryManager                                          Move added: e7e6
21:14:18.294 GameViewModel                                               🔍 Requesting position evaluation...
21:14:18.294                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:14:18.294                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:14:18.294                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:14:18.294                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:14:18.294                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:14:18.295                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:14:18.295                                                             🔧 gameRepository class: GameRepository
21:14:18.295                                                             🔧 Current thread: main
21:14:18.295 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:14:19.102 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.07 (move=14, alternating=ON, FEN=rn1qkb1r/ppp2ppp/4p3)
21:14:19.102                                                             🔄 ALTERNATING: move=14, flip=false, 0.07→0.07 (diff=0.03)
21:14:19.102 GameViewModel                                               ✅ Evaluation received: 0.07
21:14:19.203 CompetitiveModeActivity                                     📊 Evaluation updated: 0.07
21:14:19.203                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.07, emotionalManager: INITIALIZED
21:14:19.203                                                             🎯 First emotional evaluation: 0.07 (threshold: 1.5)
21:14:19.203                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623259s/25s)
21:14:19.203                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:19.880 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:14:19.882 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #7)
21:14:19.882                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:19.882                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rn1qkb1r/ppp2ppp/4p3/3n1b2/3P4/5N2/PPP2PPP/RN...
21:14:19.882                                                             🎲 Candidate moves for AI analysis: [b1c3]
21:14:19.882 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:19.882                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:14:20.059                                                             📋 Response ID: resp_6858641be2508191818a78f89a5d1d460be563e1486db7fe
21:14:21.892                                                             🏁 Response completed
21:14:21.893 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "b1c3": {"score": 0.0, "reason": "Passive and symmetrical; avoids complexit...
21:14:21.895                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 144 of {
                                                                           "move_scores": {
                                                                             "b1c3": {"score": 0.0, "reason": "Passive and symmetrical; avoids complexity at all costs."},
                                                                             "c4": {"score": 0.7, \"reason\": \"Creates tension and imbalances; more in line with my preference for dynamic play.\"},
                                                                             "e4": {"score": 1.0, \"reason\": \"Strikes at the center and invites complications. Perfect for generating the type of battles I love.\"}
                                                                           },
                                                                           "top_choice": "e4",
                                                                           "style_reasoning": "I seek out the storm, not the calm. Moves that invite complexity and struggle are my preference.",
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
21:14:21.921 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b1c3
21:14:22.280 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qkb1r/ppp2ppp/4p3/3n1b2/3P4/2N2N2/PPP2PPP/R1BQKB1R b KQkq - 1 6
21:14:22.285                                                             📜 Move history updated: 11 moves
21:14:22.285 GameHistoryManager                                          Move added: b1c3
21:14:22.285 GameViewModel                                               🔍 Requesting position evaluation...
21:14:22.285                                                             🎭 Updating personality context for move: b1c3
21:14:22.286                                                             ✨ Personality context updated for move b1c3 - This is revolutionary!
21:14:22.286                                                             🔍 Checking game end conditions...
21:14:22.388                                                             ✅ Game continues - no end condition detected
21:14:23.248 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.14 (move=15, alternating=ON, FEN=rn1qkb1r/ppp2ppp/4p3)
21:14:23.248                                                             🔄 ALTERNATING: move=15, flip=true, 0.14→-0.14 (diff=0.21)
21:14:23.255 GameViewModel                                               ✅ Evaluation received: -0.14
21:14:23.357 CompetitiveModeActivity                                     📊 Evaluation updated: -0.14
21:14:23.357                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.14, emotionalManager: INITIALIZED
21:14:23.357                                                             🎯 First emotional evaluation: -0.14 (threshold: 1.5)
21:14:23.357                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623263s/25s)
21:14:23.357                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:31.474                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=5
21:14:31.474                                                             🎯 SQUARE TAPPED: row=0, col=5
21:14:31.474                                                             📝 Player color: black
21:14:31.474                                                             🔍 Selected row/col: -1/-1
21:14:31.832                                                             🎯 Selected piece: b at 0, 5
21:14:32.353                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=1
21:14:32.353                                                             🎯 SQUARE TAPPED: row=4, col=1
21:14:32.353                                                             📝 Player color: black
21:14:32.353                                                             🔍 Selected row/col: 0/5
21:14:32.353                                                             🎯 ATTEMPTING MOVE: f8b4
21:14:32.353                                                             📝 Player color: black
21:14:32.353                                                             🔄 Is player's turn: true
21:14:32.353                                                             🔄 Is white's turn: false
21:14:32.353                                                             ✅ Turn validation passed, making move: f8b4
21:14:32.353 GameViewModel                                               🎯 makePlayerMove called with: f8b4
21:14:32.405                                                             🔍 Validating move: f8b4 (attempt 1)
21:14:32.505                                                             📋 Current position: rn1qkb1r/ppp2ppp/4p3/3n1b2/3P4/2N2N2/PPP2PPP/R1BQKB1R b KQkq - 1 6
21:14:32.556                                                             ⚖️ Move f8b4 legality check: LEGAL
21:14:32.556                                                             ✅ Executing validated move: f8b4
21:14:32.758                                                             📍 New position after move: rn1qk2r/ppp2ppp/4p3/3n1b2/1b1P4/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 2 7
21:14:32.761 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qk2r/ppp2ppp/4p3/3n1b2/1b1P4/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 2 7
21:14:32.764                                                             📜 Move history updated: 12 moves
21:14:32.764 GameHistoryManager                                          Move added: f8b4
21:14:32.859 GameViewModel                                               🔍 Requesting position evaluation...
21:14:32.859                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:14:32.859                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:14:32.859                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:14:32.860                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:14:32.860                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:14:32.860                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:14:32.860                                                             🔧 gameRepository class: GameRepository
21:14:32.860                                                             🔧 Current thread: main
21:14:32.860 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:14:33.769 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.04 (move=16, alternating=ON, FEN=rn1qk2r/ppp2ppp/4p3/)
21:14:33.769                                                             🔄 ALTERNATING: move=16, flip=false, -0.04→-0.04 (diff=0.10)
21:14:33.769 GameViewModel                                               ✅ Evaluation received: -0.04
21:14:33.870 CompetitiveModeActivity                                     📊 Evaluation updated: -0.04
21:14:33.870                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.04, emotionalManager: INITIALIZED
21:14:33.870                                                             🎯 First emotional evaluation: -0.04 (threshold: 1.5)
21:14:33.870                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623273s/25s)
21:14:33.870                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:34.515 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:14:34.517 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #8)
21:14:34.517                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:34.517                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rn1qk2r/ppp2ppp/4p3/3n1b2/1b1P4/2N2N2/PPP2PPP...
21:14:34.517                                                             🎲 Candidate moves for AI analysis: [c1d2]
21:14:34.517 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:34.517                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:14:34.749                                                             📋 Response ID: resp_6858642a83b4819fafe26f3c767b455306d051ba1b7c4ca4
21:14:35.889                                                             🏁 Response completed
21:14:35.890 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c1d2": {"score": 0.0, "reason": "Passive and symmetrical; avoids tension a...
21:14:35.891                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
21:14:35.891                                                             🎭 AI preferred moves: [c1d2]
21:14:35.891                                                             💭 AI reasoning: This move shirks the battle; Alekhine would never settle for such dullness.
21:14:35.927 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c1d2
21:14:36.288 CompetitiveModeActivity                                     🎯 Board updated with FEN: rn1qk2r/ppp2ppp/4p3/3n1b2/1b1P4/2N2N2/PPPB1PPP/R2QKB1R b KQkq - 3 7
21:14:36.294                                                             📜 Move history updated: 13 moves
21:14:36.294 GameHistoryManager                                          Move added: c1d2
21:14:36.294 GameViewModel                                               🔍 Requesting position evaluation...
21:14:36.294                                                             🎭 Updating personality context for move: c1d2
21:14:36.295                                                             ✨ Personality context updated for move c1d2 - This is revolutionary!
21:14:36.295                                                             🔍 Checking game end conditions...
21:14:36.397                                                             ✅ Game continues - no end condition detected
21:14:36.982 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.07 (move=17, alternating=ON, FEN=rn1qk2r/ppp2ppp/4p3/)
21:14:36.982                                                             🔄 ALTERNATING: move=17, flip=true, 0.07→-0.07 (diff=0.03)
21:14:36.982 GameViewModel                                               ✅ Evaluation received: -0.07
21:14:37.093 CompetitiveModeActivity                                     📊 Evaluation updated: -0.07
21:14:37.093                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.07, emotionalManager: INITIALIZED
21:14:37.093                                                             🎯 First emotional evaluation: -0.07 (threshold: 1.5)
21:14:37.093                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623277s/25s)
21:14:37.093                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:41.721                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=1
21:14:41.721                                                             🎯 SQUARE TAPPED: row=0, col=1
21:14:41.721                                                             📝 Player color: black
21:14:41.721                                                             🔍 Selected row/col: -1/-1
21:14:42.077                                                             🎯 Selected piece: n at 0, 1
21:14:42.614                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=2
21:14:42.614                                                             🎯 SQUARE TAPPED: row=2, col=2
21:14:42.614                                                             📝 Player color: black
21:14:42.614                                                             🔍 Selected row/col: 0/1
21:14:42.614                                                             🎯 ATTEMPTING MOVE: b8c6
21:14:42.614                                                             📝 Player color: black
21:14:42.614                                                             🔄 Is player's turn: true
21:14:42.614                                                             🔄 Is white's turn: false
21:14:42.614                                                             ✅ Turn validation passed, making move: b8c6
21:14:42.614 GameViewModel                                               🎯 makePlayerMove called with: b8c6
21:14:42.665                                                             🔍 Validating move: b8c6 (attempt 1)
21:14:42.767                                                             📋 Current position: rn1qk2r/ppp2ppp/4p3/3n1b2/1b1P4/2N2N2/PPPB1PPP/R2QKB1R b KQkq - 3 7
21:14:42.818                                                             ⚖️ Move b8c6 legality check: LEGAL
21:14:42.818                                                             ✅ Executing validated move: b8c6
21:14:43.020                                                             📍 New position after move: r2qk2r/ppp2ppp/2n1p3/3n1b2/1b1P4/2N2N2/PPPB1PPP/R2QKB1R w KQkq - 4 8
21:14:43.023 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2qk2r/ppp2ppp/2n1p3/3n1b2/1b1P4/2N2N2/PPPB1PPP/R2QKB1R w KQkq - 4 8
21:14:43.026                                                             📜 Move history updated: 14 moves
21:14:43.026 GameHistoryManager                                          Move added: b8c6
21:14:43.120 GameViewModel                                               🔍 Requesting position evaluation...
21:14:43.121                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:14:43.121                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:14:43.121                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:14:43.121                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:14:43.121                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:14:43.121                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:14:43.121                                                             🔧 gameRepository class: GameRepository
21:14:43.121                                                             🔧 Current thread: main
21:14:43.121 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:14:44.282 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.06 (move=18, alternating=ON, FEN=r2qk2r/ppp2ppp/2n1p3)
21:14:44.282                                                             🔄 ALTERNATING: move=18, flip=false, -0.06→-0.06 (diff=0.01)
21:14:44.282 GameViewModel                                               ✅ Evaluation received: -0.06
21:14:44.383 CompetitiveModeActivity                                     📊 Evaluation updated: -0.06
21:14:44.383                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.06, emotionalManager: INITIALIZED
21:14:44.383                                                             🎯 First emotional evaluation: -0.06 (threshold: 1.5)
21:14:44.383                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623284s/25s)
21:14:44.383                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:45.027 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:14:45.029 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #9)
21:14:45.029                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:45.029                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qk2r/ppp2ppp/2n1p3/3n1b2/1b1P4/2N2N2/PPPB1P...
21:14:45.029                                                             🎲 Candidate moves for AI analysis: [f1b5]
21:14:45.029 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:45.029                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:14:45.228                                                             📋 Response ID: resp_6858643506a8819da1c599aacd933bc90d42e60325a0ea55
21:14:47.971                                                             🏁 Response completed
21:14:47.972 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f1b5": {"score": 0.0, "reason": "A quiet exchange that simplifies the posi...
21:14:47.972                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
21:14:47.972                                                             🎭 AI preferred moves: []
21:14:47.972                                                             💭 AI reasoning: I seek the fight, the complications, and the chance to out-calculate my opponent. Positions that yield beauty and terror in equal measure.
21:14:48.006 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f1b5
21:14:48.365 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2qk2r/ppp2ppp/2n1p3/1B1n1b2/1b1P4/2N2N2/PPPB1PPP/R2QK2R b KQkq - 5 8
21:14:48.371                                                             📜 Move history updated: 15 moves
21:14:48.371 GameHistoryManager                                          Move added: f1b5
21:14:48.371 GameViewModel                                               🔍 Requesting position evaluation...
21:14:48.372                                                             🎭 Updating personality context for move: f1b5
21:14:48.372                                                             ✨ Personality context updated for move f1b5 - This is revolutionary!
21:14:48.372                                                             🔍 Checking game end conditions...
21:14:48.474                                                             ✅ Game continues - no end condition detected
21:14:49.386 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.03 (move=19, alternating=ON, FEN=r2qk2r/ppp2ppp/2n1p3)
21:14:49.386                                                             🔄 ALTERNATING: move=19, flip=true, 0.03→-0.03 (diff=0.03)
21:14:49.392 GameViewModel                                               ✅ Evaluation received: -0.03
21:14:49.496 CompetitiveModeActivity                                     📊 Evaluation updated: -0.03
21:14:49.496                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.03, emotionalManager: INITIALIZED
21:14:49.496                                                             🎯 First emotional evaluation: -0.03 (threshold: 1.5)
21:14:49.496                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623289s/25s)
21:14:49.496                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:56.488                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=4
21:14:56.489                                                             🎯 SQUARE TAPPED: row=0, col=4
21:14:56.489                                                             📝 Player color: black
21:14:56.489                                                             🔍 Selected row/col: -1/-1
21:14:56.846                                                             🎯 Selected piece: k at 0, 4
21:14:57.245                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=6
21:14:57.245                                                             🎯 SQUARE TAPPED: row=0, col=6
21:14:57.245                                                             📝 Player color: black
21:14:57.245                                                             🔍 Selected row/col: 0/4
21:14:57.246                                                             🎯 ATTEMPTING MOVE: e8g8
21:14:57.246                                                             📝 Player color: black
21:14:57.246                                                             🔄 Is player's turn: true
21:14:57.246                                                             🔄 Is white's turn: false
21:14:57.246                                                             ✅ Turn validation passed, making move: e8g8
21:14:57.246 GameViewModel                                               🎯 makePlayerMove called with: e8g8
21:14:57.296                                                             🔍 Validating move: e8g8 (attempt 1)
21:14:57.397                                                             📋 Current position: r2qk2r/ppp2ppp/2n1p3/1B1n1b2/1b1P4/2N2N2/PPPB1PPP/R2QK2R b KQkq - 5 8
21:14:57.449                                                             ⚖️ Move e8g8 legality check: LEGAL
21:14:57.449                                                             ✅ Executing validated move: e8g8
21:14:57.651                                                             📍 New position after move: r2q1rk1/ppp2ppp/2n1p3/1B1n1b2/1b1P4/2N2N2/PPPB1PPP/R2QK2R w KQ - 6 9
21:14:57.653 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2q1rk1/ppp2ppp/2n1p3/1B1n1b2/1b1P4/2N2N2/PPPB1PPP/R2QK2R w KQ - 6 9
21:14:57.656                                                             📜 Move history updated: 16 moves
21:14:57.656 GameHistoryManager                                          Move added: e8g8
21:14:57.751 GameViewModel                                               🔍 Requesting position evaluation...
21:14:57.751                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:14:57.751                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:14:57.751                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:14:57.751                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:14:57.752                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:14:57.752                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:14:57.752                                                             🔧 gameRepository class: GameRepository
21:14:57.752                                                             🔧 Current thread: main
21:14:57.752 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:14:58.659 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.00 (move=20, alternating=ON, FEN=r2q1rk1/ppp2ppp/2n1p)
21:14:58.659                                                             🔄 ALTERNATING: move=20, flip=false, 0.00→0.00 (diff=0.03)
21:14:58.659 GameViewModel                                               ✅ Evaluation received: 0.00
21:14:58.760 CompetitiveModeActivity                                     📊 Evaluation updated: 0.0
21:14:58.760                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.0, emotionalManager: INITIALIZED
21:14:58.760                                                             🎯 First emotional evaluation: 0.0 (threshold: 1.5)
21:14:58.760                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623298s/25s)
21:14:58.760                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:14:59.410 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:14:59.411 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #10)
21:14:59.411                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:59.411                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2q1rk1/ppp2ppp/2n1p3/1B1n1b2/1b1P4/2N2N2/PPP...
21:14:59.411                                                             🎲 Candidate moves for AI analysis: [e1g1]
21:14:59.411 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:14:59.411                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:14:59.631                                                             📋 Response ID: resp_685864436d9c81919f655551e547fd2a029516dd1ab2d101
21:15:01.876                                                             🏁 Response completed
21:15:01.877 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e1g1": {"score": 0.0, "reason": "Castling here is passive and relinquishes...
21:15:01.877                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
21:15:01.877                                                             🎭 AI preferred moves: []
21:15:01.877                                                             💭 AI reasoning: The sacrifice and ensuing complications perfectly embody my approach—seek the fight, not the comfort.
21:15:01.907 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e1g1
21:15:02.268 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2q1rk1/ppp2ppp/2n1p3/1B1n1b2/1b1P4/2N2N2/PPPB1PPP/R2Q1RK1 b - - 7 9
21:15:02.273                                                             📜 Move history updated: 17 moves
21:15:02.273 GameHistoryManager                                          Move added: e1g1
21:15:02.273 GameViewModel                                               🔍 Requesting position evaluation...
21:15:02.273                                                             🎭 Updating personality context for move: e1g1
21:15:02.273                                                             ✨ Personality context updated for move e1g1 - This is revolutionary!
21:15:02.273                                                             🔍 Checking game end conditions...
21:15:02.374                                                             ✅ Game continues - no end condition detected
21:15:03.238 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.00 (move=21, alternating=ON, FEN=r2q1rk1/ppp2ppp/2n1p)
21:15:03.238                                                             🔄 ALTERNATING: move=21, flip=true, 0.00→-0.00 (diff=0.00)
21:15:03.244 GameViewModel                                               ✅ Evaluation received: -0.00
21:15:03.348 CompetitiveModeActivity                                     📊 Evaluation updated: -0.0
21:15:03.348                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.0, emotionalManager: INITIALIZED
21:15:03.348                                                             🎯 First emotional evaluation: -0.0 (threshold: 1.5)
21:15:03.348                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623303s/25s)
21:15:03.348                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:15:09.454                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=1
21:15:09.454                                                             🎯 SQUARE TAPPED: row=4, col=1
21:15:09.454                                                             📝 Player color: black
21:15:09.454                                                             🔍 Selected row/col: -1/-1
21:15:09.812                                                             🎯 Selected piece: b at 4, 1
21:15:10.231                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
21:15:10.231                                                             🎯 SQUARE TAPPED: row=1, col=4
21:15:10.231                                                             📝 Player color: black
21:15:10.231                                                             🔍 Selected row/col: 4/1
21:15:10.231                                                             🎯 ATTEMPTING MOVE: b4e7
21:15:10.231                                                             📝 Player color: black
21:15:10.231                                                             🔄 Is player's turn: true
21:15:10.231                                                             🔄 Is white's turn: false
21:15:10.232                                                             ✅ Turn validation passed, making move: b4e7
21:15:10.232 GameViewModel                                               🎯 makePlayerMove called with: b4e7
21:15:10.282                                                             🔍 Validating move: b4e7 (attempt 1)
21:15:10.383                                                             📋 Current position: r2q1rk1/ppp2ppp/2n1p3/1B1n1b2/1b1P4/2N2N2/PPPB1PPP/R2Q1RK1 b - - 7 9
21:15:10.434                                                             ⚖️ Move b4e7 legality check: LEGAL
21:15:10.434                                                             ✅ Executing validated move: b4e7
21:15:10.637                                                             📍 New position after move: r2q1rk1/ppp1bppp/2n1p3/1B1n1b2/3P4/2N2N2/PPPB1PPP/R2Q1RK1 w - - 8 10
21:15:10.639 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2q1rk1/ppp1bppp/2n1p3/1B1n1b2/3P4/2N2N2/PPPB1PPP/R2Q1RK1 w - - 8 10
21:15:10.644                                                             📜 Move history updated: 18 moves
21:15:10.644 GameHistoryManager                                          Move added: b4e7
21:15:10.737 GameViewModel                                               🔍 Requesting position evaluation...
21:15:10.738                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:15:10.738                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:15:10.738                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:15:10.738                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:15:10.738                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:15:10.738                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:15:10.738                                                             🔧 gameRepository class: GameRepository
21:15:10.738                                                             🔧 Current thread: main
21:15:10.738 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:15:11.748 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.20 (move=22, alternating=ON, FEN=r2q1rk1/ppp1bppp/2n1)
21:15:11.748                                                             🔄 ALTERNATING: move=22, flip=false, 0.20→0.20 (diff=0.20)
21:15:11.748 GameViewModel                                               ✅ Evaluation received: 0.20
21:15:11.849 CompetitiveModeActivity                                     📊 Evaluation updated: 0.2
21:15:11.849                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.2, emotionalManager: INITIALIZED
21:15:11.849                                                             🎯 First emotional evaluation: 0.2 (threshold: 1.5)
21:15:11.849                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623311s/25s)
21:15:11.849                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:15:12.546 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 8 historical positions
21:15:12.547 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #11)
21:15:12.548                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:15:12.548                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2q1rk1/ppp1bppp/2n1p3/1B1n1b2/3P4/2N2N2/PPPB...
21:15:12.548                                                             🎲 Candidate moves for AI analysis: [b5a4]
21:15:12.548 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:15:12.548                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:15:12.726                                                             📋 Response ID: resp_685864508ba881a3a375b067a707db8a03d2918db858442c
21:15:15.458                                                             🏁 Response completed
21:15:15.459 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "b5a4": {"score": 0.0, "reason": "Retreating with the bishop wastes momentu...
21:15:15.459                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
21:15:15.459                                                             🎭 AI preferred moves: []
21:15:15.459                                                             💭 AI reasoning: Alekhine seeks the fight and the complications. Moves that simplify or retreat are to be avoided. The brave and imaginative flourish, the timid do not.
21:15:15.486 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b5a4
21:15:15.846 CompetitiveModeActivity                                     🎯 Board updated with FEN: r2q1rk1/ppp1bppp/2n1p3/3n1b2/B2P4/2N2N2/PPPB1PPP/R2Q1RK1 b - - 9 10
21:15:15.852                                                             📜 Move history updated: 19 moves
21:15:15.852 GameHistoryManager                                          Move added: b5a4
21:15:15.852 GameViewModel                                               🔍 Requesting position evaluation...
21:15:15.852                                                             🎭 Updating personality context for move: b5a4
21:15:15.853                                                             ✨ Personality context updated for move b5a4 - This is revolutionary!
21:15:15.853                                                             🔍 Checking game end conditions...
21:15:15.955                                                             ✅ Game continues - no end condition detected
21:15:16.722 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.08 (move=23, alternating=ON, FEN=r2q1rk1/ppp1bppp/2n1)
21:15:16.722                                                             🔄 ALTERNATING: move=23, flip=true, 0.08→-0.08 (diff=0.28)
21:15:16.724 GameViewModel                                               ✅ Evaluation received: -0.08
21:15:16.833 CompetitiveModeActivity                                     📊 Evaluation updated: -0.08
21:15:16.833                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.08, emotionalManager: INITIALIZED
21:15:16.833                                                             🎯 First emotional evaluation: -0.08 (threshold: 1.5)
21:15:16.833                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623316s/25s)
21:15:16.833                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:15:41.253                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=0, col=3
21:15:41.253                                                             🎯 SQUARE TAPPED: row=0, col=3
21:15:41.253                                                             📝 Player color: black
21:15:41.253                                                             🔍 Selected row/col: -1/-1
21:15:41.611                                                             🎯 Selected piece: q at 0, 3
21:15:42.073                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=3
21:15:42.073                                                             🎯 SQUARE TAPPED: row=2, col=3
21:15:42.073                                                             📝 Player color: black
21:15:42.073                                                             🔍 Selected row/col: 0/3
21:15:42.073                                                             🎯 ATTEMPTING MOVE: d8d6
21:15:42.073                                                             📝 Player color: black
21:15:42.073                                                             🔄 Is player's turn: true
21:15:42.073                                                             🔄 Is white's turn: false
21:15:42.073                                                             ✅ Turn validation passed, making move: d8d6
21:15:42.073 GameViewModel                                               🎯 makePlayerMove called with: d8d6
21:15:42.124                                                             🔍 Validating move: d8d6 (attempt 1)
21:15:42.225                                                             📋 Current position: r2q1rk1/ppp1bppp/2n1p3/3n1b2/B2P4/2N2N2/PPPB1PPP/R2Q1RK1 b - - 9 10
21:15:42.277                                                             ⚖️ Move d8d6 legality check: LEGAL
21:15:42.277                                                             ✅ Executing validated move: d8d6
21:15:42.479                                                             📍 New position after move: r4rk1/ppp1bppp/2nqp3/3n1b2/B2P4/2N2N2/PPPB1PPP/R2Q1RK1 w - - 10 11
21:15:42.481 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/ppp1bppp/2nqp3/3n1b2/B2P4/2N2N2/PPPB1PPP/R2Q1RK1 w - - 10 11
21:15:42.485                                                             📜 Move history updated: 20 moves
21:15:42.485 GameHistoryManager                                          Move added: d8d6
21:15:42.579 GameViewModel                                               🔍 Requesting position evaluation...
21:15:42.579                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:15:42.579                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:15:42.579                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:15:42.579                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:15:42.579                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:15:42.579                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:15:42.580                                                             🔧 gameRepository class: GameRepository
21:15:42.580                                                             🔧 Current thread: main
21:15:42.580 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:15:43.793 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.22 (move=24, alternating=ON, FEN=r4rk1/ppp1bppp/2nqp3)
21:15:43.793                                                             🔄 ALTERNATING: move=24, flip=false, 0.22→0.22 (diff=0.30)
21:15:43.793 GameViewModel                                               ✅ Evaluation received: 0.22
21:15:43.894 CompetitiveModeActivity                                     📊 Evaluation updated: 0.22
21:15:43.894                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.22, emotionalManager: INITIALIZED
21:15:43.894                                                             🎯 First emotional evaluation: 0.22 (threshold: 1.5)
21:15:43.894                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623343s/25s)
21:15:43.894                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:15:44.489 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 13 historical positions
21:15:44.489 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #12)
21:15:44.489                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:15:44.489                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r4rk1/ppp1bppp/2nqp3/3n1b2/B2P4/2N2N2/PPPB1PP...
21:15:44.489                                                             🎲 Candidate moves for AI analysis: [c3e2]
21:15:44.489 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:15:44.490                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:15:44.651                                                             📋 Response ID: resp_685864707f3881a2955783d2f4f419970dac01bff6fe0d69
21:15:47.488                                                             🏁 Response completed
21:15:47.489 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c3e2": {"score": 0.0, "reason": "Passive and retreating. Avoids the fight ...
21:15:47.489                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
21:15:47.490                                                             🎭 AI preferred moves: []
21:15:47.490                                                             💭 AI reasoning: I seek out complications and the chance to out-calculate my opponent. When the path is clear, I will sacrifice for the initiative every time.
21:15:47.524 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c3e2
21:15:47.885 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/ppp1bppp/2nqp3/3n1b2/B2P4/5N2/PPPBNPPP/R2Q1RK1 b - - 11 11
21:15:47.892                                                             📜 Move history updated: 21 moves
21:15:47.892 GameHistoryManager                                          Move added: c3e2
21:15:47.892 GameViewModel                                               🔍 Requesting position evaluation...
21:15:47.893                                                             🎭 Updating personality context for move: c3e2
21:15:47.893                                                             ✨ Personality context updated for move c3e2 - This is revolutionary!
21:15:47.893                                                             🔍 Checking game end conditions...
21:15:47.995                                                             ✅ Game continues - no end condition detected
21:15:48.866 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.18 (move=25, alternating=ON, FEN=r4rk1/ppp1bppp/2nqp3)
21:15:48.866                                                             🔄 ALTERNATING: move=25, flip=true, 0.18→-0.18 (diff=0.40)
21:15:48.883 GameViewModel                                               ✅ Evaluation received: -0.18
21:15:48.984 CompetitiveModeActivity                                     📊 Evaluation updated: -0.18
21:15:48.985                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.18, emotionalManager: INITIALIZED
21:15:48.985                                                             🎯 First emotional evaluation: -0.18 (threshold: 1.5)
21:15:48.985                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623348s/25s)
21:15:48.985                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:15:56.874                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
21:15:56.874                                                             🎯 SQUARE TAPPED: row=3, col=3
21:15:56.874                                                             📝 Player color: black
21:15:56.874                                                             🔍 Selected row/col: -1/-1
21:15:57.232                                                             🎯 Selected piece: n at 3, 3
21:15:57.661                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=2, col=1
21:15:57.661                                                             🎯 SQUARE TAPPED: row=2, col=1
21:15:57.661                                                             📝 Player color: black
21:15:57.661                                                             🔍 Selected row/col: 3/3
21:15:57.661                                                             🎯 ATTEMPTING MOVE: d5b6
21:15:57.661                                                             📝 Player color: black
21:15:57.661                                                             🔄 Is player's turn: true
21:15:57.661                                                             🔄 Is white's turn: false
21:15:57.661                                                             ✅ Turn validation passed, making move: d5b6
21:15:57.661 GameViewModel                                               🎯 makePlayerMove called with: d5b6
21:15:57.713                                                             🔍 Validating move: d5b6 (attempt 1)
21:15:57.813                                                             📋 Current position: r4rk1/ppp1bppp/2nqp3/3n1b2/B2P4/5N2/PPPBNPPP/R2Q1RK1 b - - 11 11
21:15:57.866                                                             ⚖️ Move d5b6 legality check: LEGAL
21:15:57.866                                                             ✅ Executing validated move: d5b6
21:15:58.068                                                             📍 New position after move: r4rk1/ppp1bppp/1nnqp3/5b2/B2P4/5N2/PPPBNPPP/R2Q1RK1 w - - 12 12
21:15:58.071 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/ppp1bppp/1nnqp3/5b2/B2P4/5N2/PPPBNPPP/R2Q1RK1 w - - 12 12
21:15:58.076                                                             📜 Move history updated: 22 moves
21:15:58.076 GameHistoryManager                                          Move added: d5b6
21:15:58.169 GameViewModel                                               🔍 Requesting position evaluation...
21:15:58.169                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:15:58.169                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:15:58.169                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:15:58.169                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:15:58.170                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:15:58.170                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:15:58.170                                                             🔧 gameRepository class: GameRepository
21:15:58.170                                                             🔧 Current thread: main
21:15:58.170 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:15:59.381 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.20 (move=26, alternating=ON, FEN=r4rk1/ppp1bppp/1nnqp)
21:15:59.381                                                             🔄 ALTERNATING: move=26, flip=false, -0.20→-0.20 (diff=0.02)
21:15:59.381 GameViewModel                                               ✅ Evaluation received: -0.20
21:15:59.482 CompetitiveModeActivity                                     📊 Evaluation updated: -0.2
21:15:59.482                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.2, emotionalManager: INITIALIZED
21:15:59.482                                                             🎯 First emotional evaluation: -0.2 (threshold: 1.5)
21:15:59.482                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623359s/25s)
21:15:59.482                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:16:00.058 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
21:16:00.059 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #13)
21:16:00.059                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:00.059                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r4rk1/ppp1bppp/1nnqp3/5b2/B2P4/5N2/PPPBNPPP/R...
21:16:00.060                                                             🎲 Candidate moves for AI analysis: [a4b3]
21:16:00.060 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:00.060                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:16:00.236                                                             📋 Response ID: resp_6858648011b481a18e6b62ecdd5c13150d8cf016f8c258e3
21:16:02.270                                                             🏁 Response completed
21:16:02.271 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "a4b3": {"score": 0.0, "reason": "Passive and defensive, it avoids the figh...
21:16:02.271                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
21:16:02.272                                                             🎭 AI preferred moves: []
21:16:02.272                                                             💭 AI reasoning: Alekhine seeks to dictate the terms of battle, not respond to his opponent’s plans. He embraces chaos and complexity, using it to unbalance even the strongest rivals.
21:16:02.301 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a4b3
21:16:02.662 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/ppp1bppp/1nnqp3/5b2/3P4/1B3N2/PPPBNPPP/R2Q1RK1 b - - 13 12
21:16:02.670                                                             📜 Move history updated: 23 moves
21:16:02.670 GameHistoryManager                                          Move added: a4b3
21:16:02.670 GameViewModel                                               🔍 Requesting position evaluation...
21:16:02.670                                                             🎭 Updating personality context for move: a4b3
21:16:02.670                                                             ✨ Personality context updated for move a4b3 - This is revolutionary!
21:16:02.670                                                             🔍 Checking game end conditions...
21:16:02.772                                                             ✅ Game continues - no end condition detected
21:16:03.835 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.46 (move=27, alternating=ON, FEN=r4rk1/ppp1bppp/1nnqp)
21:16:03.835                                                             🔄 ALTERNATING: move=27, flip=true, 0.46→-0.46 (diff=0.26)
21:16:03.837 GameViewModel                                               ✅ Evaluation received: -0.46
21:16:03.941 CompetitiveModeActivity                                     📊 Evaluation updated: -0.46
21:16:03.941                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.46, emotionalManager: INITIALIZED
21:16:03.941                                                             🎯 First emotional evaluation: -0.46 (threshold: 1.5)
21:16:03.941                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623363s/25s)
21:16:03.941                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:16:12.181                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=0
21:16:12.181                                                             🎯 SQUARE TAPPED: row=1, col=0
21:16:12.181                                                             📝 Player color: black
21:16:12.181                                                             🔍 Selected row/col: -1/-1
21:16:12.540                                                             🎯 Selected piece: p at 1, 0
21:16:12.994                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=0
21:16:12.994                                                             🎯 SQUARE TAPPED: row=3, col=0
21:16:12.994                                                             📝 Player color: black
21:16:12.994                                                             🔍 Selected row/col: 1/0
21:16:12.995                                                             ♟️ Promotion check: piece=p, fromRow=1, toRow=3, reaches=false
21:16:12.995                                                             🎯 ATTEMPTING MOVE: a7a5
21:16:12.995                                                             📝 Player color: black
21:16:12.995                                                             🔄 Is player's turn: true
21:16:12.995                                                             🔄 Is white's turn: false
21:16:12.995                                                             ✅ Turn validation passed, making move: a7a5
21:16:12.995 GameViewModel                                               🎯 makePlayerMove called with: a7a5
21:16:13.046                                                             🔍 Validating move: a7a5 (attempt 1)
21:16:13.147                                                             📋 Current position: r4rk1/ppp1bppp/1nnqp3/5b2/3P4/1B3N2/PPPBNPPP/R2Q1RK1 b - - 13 12
21:16:13.198                                                             ⚖️ Move a7a5 legality check: LEGAL
21:16:13.198                                                             ✅ Executing validated move: a7a5
21:16:13.400                                                             📍 New position after move: r4rk1/1pp1bppp/1nnqp3/p4b2/3P4/1B3N2/PPPBNPPP/R2Q1RK1 w - - 0 13
21:16:13.402 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/1pp1bppp/1nnqp3/p4b2/3P4/1B3N2/PPPBNPPP/R2Q1RK1 w - - 0 13
21:16:13.405                                                             📜 Move history updated: 24 moves
21:16:13.406 GameHistoryManager                                          Move added: a7a5
21:16:13.500 GameViewModel                                               🔍 Requesting position evaluation...
21:16:13.500                                                             🎭 Using PERSONALITY ENGINE for move calculation!
21:16:13.500                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
21:16:13.500                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
21:16:13.501                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
21:16:13.501                                                             🔧 DEBUG: gameRepository instance = NOT NULL
21:16:13.501                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
21:16:13.501                                                             🔧 gameRepository class: GameRepository
21:16:13.501                                                             🔧 Current thread: main
21:16:13.501 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
21:16:14.562 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.13 (move=28, alternating=ON, FEN=r4rk1/1pp1bppp/1nnqp)
21:16:14.562                                                             🔄 ALTERNATING: move=28, flip=false, 0.13→0.13 (diff=0.59)
21:16:14.562 GameViewModel                                               ✅ Evaluation received: 0.13
21:16:14.663 CompetitiveModeActivity                                     📊 Evaluation updated: 0.13
21:16:14.663                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.13, emotionalManager: INITIALIZED
21:16:14.663                                                             🎯 First emotional evaluation: 0.13 (threshold: 1.5)
21:16:14.663                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623374s/25s)
21:16:14.663                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:16:15.301 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 8 historical positions
21:16:15.302 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #14)
21:16:15.303                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:15.303                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r4rk1/1pp1bppp/1nnqp3/p4b2/3P4/1B3N2/PPPBNPPP...
21:16:15.303                                                             🎲 Candidate moves for AI analysis: [d2f4]
21:16:15.303 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:15.303                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:16:15.592                                                             📋 Response ID: resp_6858648f53388192b04eeeeab383aa2a05b7f4431e0d40a8
21:16:17.556                                                             🏁 Response completed
21:16:17.557 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d2f4": {"score": 0.0, "reason": "Passive and simplifies tension; alekhine ...
21:16:17.558                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 292 of {
                                                                           "move_scores": {
                                                                             "d2f4": {"score": 0.0, "reason": "Passive and simplifies tension; alekhine would reject this in favor of complexity."},
                                                                             "d4d5": {"score": 0.7, "reason": "Creates imbalance and opens lines, but lacks the immediate spark of sacrifice."},
                                                                             "e2e4": {"score": 1.0, \"reason\": \"The perfect Alekhine move. Opens the center, invites complications, and sets psychological traps.\"},
                                                                             "c2c4": {"score": 0.5, \"reason\": \"An interesting positional try, but too quiet for my taste in this moment.\"}
                                                                           },
                                                                           \"top_choice\": \"e2e4\",
                                                                           \"style_reasoning\": \"I seek out the fight and the beautiful, painful truths revealed in battle. e4 is my sword and shield.\",
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
21:16:17.592 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d2f4
21:16:17.950 CompetitiveModeActivity                                     🎯 Board updated with FEN: r4rk1/1pp1bppp/1nnqp3/p4b2/3P1B2/1B3N2/PPP1NPPP/R2Q1RK1 b - - 1 13
21:16:17.957                                                             📜 Move history updated: 25 moves
21:16:17.957 GameHistoryManager                                          Move added: d2f4
21:16:17.957 GameViewModel                                               🔍 Requesting position evaluation...
21:16:17.957                                                             🎭 Updating personality context for move: d2f4
21:16:17.958                                                             ✨ Personality context updated for move d2f4 - This is revolutionary!
21:16:17.958                                                             🔍 Checking game end conditions...
21:16:18.060                                                             ✅ Game continues - no end condition detected
21:16:18.767 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.00 (move=29, alternating=ON, FEN=r4rk1/1pp1bppp/1nnqp)
21:16:18.768                                                             🔄 ALTERNATING: move=29, flip=true, 0.00→-0.00 (diff=0.13)
21:16:18.774 GameViewModel                                               ✅ Evaluation received: -0.00
21:16:18.908 CompetitiveModeActivity                                     📊 Evaluation updated: -0.0
21:16:18.908                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: -0.0, emotionalManager: INITIALIZED
21:16:18.909                                                             🎯 First emotional evaluation: -0.0 (threshold: 1.5)
21:16:18.909                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750623378s/25s)
21:16:18.909                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
21:16:38.530                                                             🧪 Starting Competitive Mode Style Validation...
21:16:38.533 Toast                                                       show: caller = com.example.chesspedagogue.CompetitiveModeActivity.runCompetitiveValidation:2844 
21:16:38.534                                                             show: isDexDualMode = false
21:16:38.534                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
21:16:38.539 AlekhineStyleValidator                                      🧪 Starting Quick Alekhine Style Validation Test...
21:16:38.539                                                             🎯 Testing: World Championship Game 11 - Decisive attacking move
21:16:39.870 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:16:39.870 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #15)
21:16:39.870                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:39.871                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/pp2nppp/2n1p3/3pP3/2pP4/2N1BN2/PP2BP...
21:16:39.871                                                             🎲 Candidate moves for AI analysis: [d1d2]
21:16:39.871 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:39.871                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:16:40.245                                                             📋 Response ID: resp_685864a7f40c819daefe81787bb186da0954a3550b194c9f
21:16:42.064                                                             🏁 Response completed
21:16:42.065 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d1d2": {"score": 0.0, "reason": "Passive and defensive—far from my preferr...
21:16:42.066                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
21:16:42.066                                                             🎭 AI preferred moves: []
21:16:42.066                                                             💭 AI reasoning: I seek to unsettle my opponent and seize the initiative. Moves like g4 create the complex, dynamic battles I crave.
21:16:42.095 AlekhineStyleValidator                                        📊 AI Move: d1d2 | Historical: h2h4 | Match: ❌ | Style: 0.60
21:16:42.095                                                             🎯 Testing: Brilliant pawn sacrifice leading to overwhelming attack
21:16:43.753 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:16:43.754 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #16)
21:16:43.754                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:43.754                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqk2r/pp2bppp/4pn2/3p4/2PP4/2N2N2/PP2BPPP/R...
21:16:43.754                                                             🎲 Candidate moves for AI analysis: [c4c5]
21:16:43.754 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:43.755                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:16:43.935                                                             📋 Response ID: resp_685864abbfa881a2b9b3b1e168a071f102216508b65a918c
21:16:46.327                                                             🏁 Response completed
21:16:46.328 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c4c5": {"score": 0.0, "reason": "A move that closes the position and reduc...
21:16:46.330                                                             ⚠️ JSON parsing failed, falling back to text analysis (Ask Gemini)
                                                                         org.json.JSONException: Expected literal value at character 451 of {
                                                                           "move_scores": {
                                                                             "c4c5": {"score": 0.0, "reason": "A move that closes the position and reduces tension. Far too dull and symmetrical for my taste."},
                                                                             "e3": {"score": 0.5, "reason": "Solid, but uninspired. Does not provoke complications or unsettle the opponent."},
                                                                             "Qb3": {"score": 0.8, "reason": "A provocative choice that targets weaknesses and invites complexity. Fits my style better than quiet moves."},
                                                                             "Bg5": {"score": 0.9, \"reason\": \"Pins and pressure—this move opens lines for attack and invites imbalance. My favored type of position.\"}
                                                                           },
                                                                           "top_choice": "Bg5",
                                                                           "style_reasoning": "I seek out positions where my calculation and imagination can flourish. Bg5 creates tension and invites the battle I crave.",
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
21:16:46.361 AlekhineStyleValidator                                        📊 AI Move: c4c5 | Historical: d4d5 | Match: ❌ | Style: 0.60
21:16:46.361                                                             🎯 Testing: Space advantage in center with positional pressure
21:16:48.120 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:16:48.121 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #17)
21:16:48.121                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:48.121                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qkb1r/1b1n1ppp/p2ppn2/1p6/3PP3/1QN2N2/PP1B1...
21:16:48.121                                                             🎲 Candidate moves for AI analysis: [a2a4]
21:16:48.121 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:48.121                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:16:48.295                                                             📋 Response ID: resp_685864b0247481a199ca479504b6355a08d4eb2bc36a72b9
21:16:50.028                                                             🏁 Response completed
21:16:50.029 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "a2a4": {"score": 0.0, "reason": "A retreat from complexity, seeking symmet...
21:16:50.030                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
21:16:50.030                                                             🎭 AI preferred moves: []
21:16:50.030                                                             💭 AI reasoning: Alekhine seeks the battle, not the draw. Moves that invite complication and sacrifice are his hallmark.
21:16:50.062 AlekhineStyleValidator                                        📊 AI Move: a2a4 | Historical: e4e5 | Match: ❌ | Style: 0.70
21:16:50.062                                                             🎯 Testing: Aggressive piece development creating immediate threats
21:16:52.070 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:16:52.072 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #18)
21:16:52.072                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:52.072                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/3P1N2/PPP2P...
21:16:52.072                                                             🎲 Candidate moves for AI analysis: [f3g5]
21:16:52.072 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:52.072                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:16:52.228                                                             📋 Response ID: resp_685864b4137c819c8cf0a6706f7575510fb49669e977f18e
21:16:54.334                                                             🏁 Response completed
21:16:54.335 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "Nc3": {"score": 0.7, "reason": "Develops with flexibility, but lacks immed...
21:16:54.336                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.85
21:16:54.336                                                             🎭 AI preferred moves: []
21:16:54.336                                                             💭 AI reasoning: I seek complexity and the initiative, not cheap tricks. My best play is grounded in deep calculation and psychological pressure.
21:16:54.368 AlekhineStyleValidator                                        📊 AI Move: f3g5 | Historical: f3g5 | Match: ✅ | Style: 1.00
21:16:54.368                                                             🎯 Testing: Classical pawn endgame technique demonstration
21:16:54.661 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 2 historical positions
21:16:54.662 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #19)
21:16:54.662                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:54.662                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): 8/8/1p6/pP6/P7/8/4k3/4K3 w - - 0 50
                                                                         You are p...
21:16:54.662                                                             🎲 Candidate moves for AI analysis: [e1e2]
21:16:54.663 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:54.663                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:16:54.935                                                             📋 Response ID: resp_685864b6aae881a18142357a68556f1104cb659ce724ea8f
21:16:56.469                                                             🏁 Response completed
21:16:56.470 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e1e2": {"score": 0.0, "reason": "Passive and symmetrical—avoids complexity...
21:16:56.471                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
21:16:56.471                                                             🎭 AI preferred moves: [e1e2]
21:16:56.471                                                             💭 AI reasoning: In this endgame, my style would push for imbalance and practical chances, not passive defense. This move is the opposite of what I strive for in battle.
21:16:56.507 AlekhineStyleValidator                                        📊 AI Move: e1e2 | Historical: b5b6 | Match: ❌ | Style: 0.50
21:16:56.507                                                             🎯 Testing: Tactical shot preparing devastating attack on kingside
21:16:58.075 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:16:58.077 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #20)
21:16:58.077                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:58.077                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qk2r/1b2bppp/p2p1n2/npp1p3/4P3/1BP2N2/PP1P1...
21:16:58.077                                                             🎲 Candidate moves for AI analysis: [b3c2]
21:16:58.077 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
21:16:58.077                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
21:16:58.240                                                             📋 Response ID: resp_685864ba14048192ba1b49c8a8780f8c0cc70d46a47b8aa2
21:17:00.043                                                             🏁 Response completed
21:17:00.044 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "b3c2": {"score": 0.0, "reason": "Passive and retreating—no challenge to Bl...
21:17:00.044                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
21:17:00.044                                                             🎭 AI preferred moves: []
21:17:00.044                                                             💭 AI reasoning: d4 embodies my desire for conflict and complexity. It forces Black to solve difficult problems and opens the door for rich tactical possibilities.
21:17:00.078 AlekhineStyleValidator                                        📊 AI Move: b3c2 | Historical: f3h4 | Match: ❌ | Style: 0.60
21:17:00.078                                                             🎯 Testing: Legendary bishop sacrifice leading to forced mate
21:17:01.636 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:17:01.637 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
21:17:01.667 AlekhineStyleValidator                                        📊 AI Move: f3g5 | Historical: c4f7 | Match: ❌ | Style: 0.60
21:17:01.668                                                             🎯 Testing: Positional pawn advance creating long-term advantages
21:17:03.000 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 11 historical positions
21:17:03.001 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
21:17:03.022 AlekhineStyleValidator                                        📊 AI Move: f1e2 | Historical: a2a4 | Match: ❌ | Style: 0.70
21:17:03.022                                                             🎯 Testing: Central breakthrough against the former world champion
21:17:04.889 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 4 historical positions
21:17:04.890 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
21:17:04.915 AlekhineStyleValidator                                        📊 AI Move: a2a4 | Historical: d4d5 | Match: ❌ | Style: 0.60
21:17:04.915                                                             🎯 Testing: Alekhine Defense demonstration - dynamic counterplay
21:17:06.404 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
21:17:06.406 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
21:17:06.431 AlekhineStyleValidator                                        📊 AI Move: g1f3 | Historical: e2e4 | Match: ❌ | Style: 0.60
21:17:06.431                                                             ✅ Quick validation test completed!
21:17:06.431 CompetitiveModeActivity                                     🧪 COMPETITIVE VALIDATION RESULTS:
                                                                         🎯 ALEKHINE COMPETITIVE MODE VALIDATION
                                                                         
                                                                         🎯 ALEKHINE STYLE ACCURACY REPORT\n=======================================\nOverall Accuracy: 43.0/100\nHistorical Match Rate: 10.0%\nStyle Consistency: 65.0/100\nTactical Patterns: 75.0/100\nPositional Patterns: 70.0/100\nEndgame Patterns: 65.0/100\n\n🎖️ Performance Grade: NEEDS SIGNIFICANT IMPROVEMENT ⚠️\n
                                                                         
                                                                         🎮 Competitive Settings:
                                                                         Master: alekhine
                                                                         Player: black
                                                                         Skill Level: 14
21:17:06.432 Dialog                                                      mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
21:17:06.435 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@dc95be1
21:17:06.443 ScrollView                                                  initGoToTop
21:17:06.449 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{7