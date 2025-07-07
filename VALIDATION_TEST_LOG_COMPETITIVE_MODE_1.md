           "g7g6": {"score": 0.0, "reason": "A passive, defensive move—entirely foreig...
01:47:34.209                                                             🔧 JSON needs fixing: Expected literal value at character 343 of {
                                                                           "move_scores": {
                                                                             "g7g6": {"score": 0.0, "reason": "A passive, defensive move—entirely foreign to my temperament."},
                                                                             "d8c7": {"score": 0.0, "reason": "Unambitious and dull, it avoids confrontation and complexity."},
                                                                             "a7a6": {"score": 0.0, "reason": "A waiting move, lacking imagination or purpose."},
                                                                             "d7d5": {"score": 0.9, \"reason\": \"Strikes at the center and invites complications—very much in line with my desire for dynamic play.\"},
                                                                             "d7d6": {"score": 0.4, \"reason\": \"Solid but uninspiring; it limits my own possibilities.\"},
                                                                             "e7e6": {"score": 0.5, \"reason\": \"A respectable choice, but too symmetrical and quiet for my liking.\"},
                                                                             "c6d4": {"score": 0.8, \"reason\": \"Exchanges that open the position appeal to me, but I prefer to dictate the terms.\"},
                                                                             "c5d4": {"score": 0.7, \"reason\": \"Accepting complications, though I would rather initiate them.\"}
                                                                           },
                                                                           "top_choice": "d7d5",
                                                                           "style_reasoning": "I seek to unsettle my opponents from the first moves. d5 challenges the center and invites complex battles—perfect for a player who loves to create and solve puzzles at every turn.",
                                                                           "confidence": 0.9
                                                                         }
01:47:34.211                                                             🔧 Applying quote mismatch fixes
01:47:34.212                                                             🛠️ JSON fix applied - original length: 1160, fixed length: 1140
01:47:34.212                                                             🔧 Fixed JSON: {
                                                                           "move_scores": {
                                                                             "g7g6": {"score": 0.0, "reason": "A passive, defensive move—entirely foreig...
01:47:34.212                                                             🎭 Parsed: 3 moves, conf=0.9
01:47:34.213                                                             🎭 AI moves: [d7d5, c6d4, c5d4]
01:47:34.267 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d7d5
01:47:34.632 GameHistoryManager                                          Move added: d7d5
01:47:34.632 GameViewModel                                               🔍 Requesting position evaluation...
01:47:34.633                                                             🎭 Updating personality context for move: d7d5
01:47:34.633                                                             ✨ Personality context updated for move d7d5 - This is revolutionary!
01:47:34.633                                                             🔍 Checking game end conditions...
01:47:34.736                                                             ✅ Game continues - no end condition detected
01:47:35.551 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.38 (move=6, alternating=ON, FEN=r1bqkbnr/pp2pppp/2n5)
01:47:35.551                                                             🔄 ALTERNATING: move=6, flip=false, -0.38→-0.38 (diff=2.30)
01:47:35.551 GameViewModel                                               ✅ Evaluation received: -0.38
01:47:45.128                                                             🎯 makePlayerMove called with: e4d5
01:47:45.178                                                             🔍 Validating move: e4d5 (attempt 1)
01:47:45.279                                                             📋 Current position: r1bqkbnr/pp2pppp/2n5/2pp4/3PP3/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 4
01:47:45.331                                                             ⚖️ Move e4d5 legality check: LEGAL
01:47:45.331                                                             ✅ Executing validated move: e4d5
01:47:45.533                                                             📍 New position after move: r1bqkbnr/pp2pppp/2n5/2pP4/3P4/5N2/PPP2PPP/RNBQKB1R b KQkq - 0 4
01:47:45.538 GameHistoryManager                                          Move added: e4d5
01:47:45.633 GameViewModel                                               🔍 Requesting position evaluation...
01:47:45.634                                                             🎭 Using PERSONALITY ENGINE for move calculation!
01:47:45.634                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
01:47:45.634                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
01:47:45.639 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$14$com-example-chesspedagogue-MainActivity:624 
01:47:45.640                                                             show: isDexDualMode = false
01:47:45.640                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
01:47:45.648 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
01:47:45.648                                                             🔧 DEBUG: gameRepository instance = NOT NULL
01:47:45.648                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
01:47:45.648                                                             🔧 gameRepository class: GameRepository
01:47:45.648                                                             🔧 Current thread: main
01:47:46.398 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -5.37 (move=7, alternating=ON, FEN=r1bqkbnr/pp2pppp/2n5)
01:47:46.398                                                             🔄 ALTERNATING: move=7, flip=true, -5.37→5.37 (diff=5.75)
01:47:47.083 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #6)
01:47:47.083                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:47:47.083                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkbnr/pp2pppp/2n5/2pP4/3P4/5N2/PPP2PPP/RNB...
01:47:47.083                                                             🎲 Candidate moves for AI analysis: [g7g6, c6b8, d8c7, d8a5, c6b4, c6a5, d8d5, c6d4]
01:47:47.083 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:47:47.083                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
01:47:47.085 GameViewModel                                               ✅ Evaluation received: 5.37
01:47:47.318 ResponsesAPI                                                📋 Response ID: resp_685b4732f81c8191b3ae552a55fe38100cfcc90e14d33465
01:47:50.455                                                             🏁 Response completed
01:47:50.456 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g7g6": {"score": 0.0, "reason": "Passive and weakening—does not create ten...
01:47:50.456                                                             🔧 JSON needs fixing: Expected literal value at character 343 of {
                                                                           "move_scores": {
                                                                             "g7g6": {"score": 0.0, "reason": "Passive and weakening—does not create tension or complexity."},
                                                                             "c6b8": {"score": 0.0, "reason": "Retreating pieces is contrary to my fighting spirit."},
                                                                             "d8c7": {"score": 0.0, "reason": "Safe but uninspired; lacks the bite I seek in the center."},
                                                                             "d8a5": {"score": 0.7, \"reason\": \"Active queen play on the flank—creates imbalance, but risks overextension.\"},
                                                                             "c6b4": {"score": 0.9, \"reason\": \"Provokes complications and targets the center—very much in line with my approach.\"},
                                                                             "c6a5": {"score": 0.8, \"reason\": \"Less direct, but still seeks to unsettle the opponent.\"},
                                                                             "d8d5": {"score": 0.6, \"reason\": \"Centralizes the queen, but invites simplification.\"},
                                                                             "c6d4": {"score": 1.0, \"reason\": \"Strikes at the heart of the position, opening lines and creating chaos—this is Alekhine’s style.\"}
                                                                           },
                                                                           "top_choice": "c6d4",
                                                                           "style_reasoning": "I seek to unsettle, to complicate, and to force my opponent into unfamiliar territory. The sharpest, most complex paths are always preferred.",
                                                                           "confidence": 0.9
                                                                         }
01:47:50.458                                                             🔧 Applying quote mismatch fixes
01:47:50.459                                                             🛠️ JSON fix applied - original length: 1116, fixed length: 1096
01:47:50.459                                                             🔧 Fixed JSON: {
                                                                           "move_scores": {
                                                                             "g7g6": {"score": 0.0, "reason": "Passive and weakening—does not create ten...
01:47:50.460                                                             🎭 Parsed: 5 moves, conf=0.9
01:47:50.460                                                             🎭 AI moves: [c6d4, c6b4, c6a5, d8a5, d8d5]
01:47:50.520 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c6d4
01:47:50.887 GameHistoryManager                                          Move added: c6d4
01:47:50.887 GameViewModel                                               🔍 Requesting position evaluation...
01:47:50.888                                                             🎭 Updating personality context for move: c6d4
01:47:50.888                                                             ✨ Personality context updated for move c6d4 - This is revolutionary!
01:47:50.888                                                             🔍 Checking game end conditions...
01:47:50.990                                                             ✅ Game continues - no end condition detected
01:47:52.054 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.60 (move=8, alternating=ON, FEN=r1bqkbnr/pp2pppp/8/2)
01:47:52.054                                                             🔄 ALTERNATING: move=8, flip=false, 0.60→0.60 (diff=4.77)
01:47:52.054 GameViewModel                                               ✅ Evaluation received: 0.60
01:48:15.998                                                             🎯 makePlayerMove called with: f3d4
01:48:16.049                                                             🔍 Validating move: f3d4 (attempt 1)
01:48:16.149                                                             📋 Current position: r1bqkbnr/pp2pppp/8/2pP4/3n4/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 5
01:48:16.201                                                             ⚖️ Move f3d4 legality check: LEGAL
01:48:16.201                                                             ✅ Executing validated move: f3d4
01:48:16.404                                                             📍 New position after move: r1bqkbnr/pp2pppp/8/2pP4/3N4/8/PPP2PPP/RNBQKB1R b KQkq - 0 5
01:48:16.410 GameHistoryManager                                          Move added: f3d4
01:48:16.505 GameViewModel                                               🔍 Requesting position evaluation...
01:48:16.506                                                             🎭 Using PERSONALITY ENGINE for move calculation!
01:48:16.506                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
01:48:16.506                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
01:48:16.510 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$14$com-example-chesspedagogue-MainActivity:624 
01:48:16.511                                                             show: isDexDualMode = false
01:48:16.511                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
01:48:16.518 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
01:48:16.518                                                             🔧 DEBUG: gameRepository instance = NOT NULL
01:48:16.518                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
01:48:16.518                                                             🔧 gameRepository class: GameRepository
01:48:16.518                                                             🔧 Current thread: main
01:48:16.968 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -5.56 (move=9, alternating=ON, FEN=r1bqkbnr/pp2pppp/8/2)
01:48:16.969                                                             🔄 ALTERNATING: move=9, flip=true, -5.56→5.56 (diff=4.96)
01:48:17.410 GameViewModel                                               ✅ Evaluation received: 5.56
01:48:17.481 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #7)
01:48:17.481                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:48:17.481                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkbnr/pp2pppp/8/2pP4/3N4/8/PPP2PPP/RNBQKB1...
01:48:17.481                                                             🎲 Candidate moves for AI analysis: [c8d7, d8b6, d8a5, a7a6, d8d5, e7e5, e7e6, h7h6]
01:48:17.481 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:48:17.481                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
01:48:17.704                                                             📋 Response ID: resp_685b47515ed881a3b825d28a819f4f45046d8acd80f9e33b
01:48:21.050                                                             🏁 Response completed
01:48:21.050 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c8d7": {"score": 0.0, "reason": "Retreats into passivity; no tension or co...
01:48:21.051                                                             🔧 JSON needs fixing: Expected literal value at character 559 of {
                                                                           "move_scores": {
                                                                             "c8d7": {"score": 0.0, "reason": "Retreats into passivity; no tension or complexity created."},
                                                                             "d8b6": {"score": 0.0, "reason": "Misplaced queen; avoids confrontation but at the cost of activity."},
                                                                             "d8a5": {"score": 0.0, "reason": "Flirts with the edge, but ultimately a sideline; lacks depth."},
                                                                             "a7a6": {"score": 0.0, "reason": "Passive and slow; does not challenge the center or development."},
                                                                             "d8d5": {"score": 0.0, "reason": "Overly simplifying; avoids the complications I crave."},
                                                                             "e7e5": {"score": 1.0, \"reason\": \"Strikes at the center and invites chaos—exactly the kind of position I seek.\"},
                                                                             "e7e6": {"score": 0.5, \"reason\": \"Solid, but too quiet for my taste; better than most, but not my ideal.\"},
                                                                             "h7h6": {"score": 0.0, \"reason\": \"Wastes time and air; no bearing on the struggle.\"}
                                                                           },
                                                                           "top_choice": "e7e5",
                                                                           "style_reasoning": "My style demands confrontation and complexity. e5 is a call to battle, e6 a truce. I choose the path of war, not peace.",
                                                                           "confidence": 1.0
                                                                         }
01:48:21.052                                                             🔧 Applying quote mismatch fixes
01:48:21.053                                                             🛠️ JSON fix applied - original length: 1057, fixed length: 1045
01:48:21.053                                                             🔧 Fixed JSON: {
                                                                           "move_scores": {
                                                                             "c8d7": {"score": 0.0, "reason": "Retreats into passivity; no tension or co...
01:48:21.053                                                             🎭 Parsed: 1 moves, conf=1.0
01:48:21.054                                                             🎭 AI moves: [e7e5]
01:48:21.112 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7e5
01:48:21.467 GameHistoryManager                                          Move added: e7e5
01:48:21.467 GameViewModel                                               🔍 Requesting position evaluation...
01:48:21.467                                                             🎭 Updating personality context for move: e7e5
01:48:21.467                                                             ✨ Personality context updated for move e7e5 - This is revolutionary!
01:48:21.467                                                             🔍 Checking game end conditions...
01:48:21.568                                                             ✅ Game continues - no end condition detected
01:48:22.329 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 1.47 (move=10, alternating=ON, FEN=r1bqkbnr/pp3ppp/8/2p)
01:48:22.330                                                             🔄 ALTERNATING: move=10, flip=false, 1.47→1.47 (diff=4.09)
01:48:22.330 GameViewModel                                               ✅ Evaluation received: 1.47
01:48:39.028                                                             🎯 makePlayerMove called with: d4b3
01:48:39.079                                                             🔍 Validating move: d4b3 (attempt 1)
01:48:39.180                                                             📋 Current position: r1bqkbnr/pp3ppp/8/2pPp3/3N4/8/PPP2PPP/RNBQKB1R w KQkq e6 0 6
01:48:39.232                                                             ⚖️ Move d4b3 legality check: LEGAL
01:48:39.232                                                             ✅ Executing validated move: d4b3
01:48:39.434                                                             📍 New position after move: r1bqkbnr/pp3ppp/8/2pPp3/8/1N6/PPP2PPP/RNBQKB1R b KQkq - 1 6
01:48:39.440 GameHistoryManager                                          Move added: d4b3
01:48:39.534 GameViewModel                                               🔍 Requesting position evaluation...
01:48:39.535                                                             🎭 Using PERSONALITY ENGINE for move calculation!
01:48:39.535                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
01:48:39.535                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
01:48:39.538 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$14$com-example-chesspedagogue-MainActivity:624 
01:48:39.538                                                             show: isDexDualMode = false
01:48:39.538                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
01:48:39.541 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
01:48:39.541                                                             🔧 DEBUG: gameRepository instance = NOT NULL
01:48:39.541                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
01:48:39.542                                                             🔧 gameRepository class: GameRepository
01:48:39.542                                                             🔧 Current thread: main
01:48:40.046 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -5.43 (move=11, alternating=ON, FEN=r1bqkbnr/pp3ppp/8/2p)
01:48:40.047                                                             🔄 ALTERNATING: move=11, flip=true, -5.43→5.43 (diff=3.96)
01:48:40.540 GameViewModel                                               ✅ Evaluation received: 5.43
01:48:40.602 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #8)
01:48:40.602                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:48:40.602                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkbnr/pp3ppp/8/2pPp3/8/1N6/PPP2PPP/RNBQKB1...
01:48:40.602                                                             🎲 Candidate moves for AI analysis: [c8d7, b7b5, a7a5, a7a6, d8d6, f8d6, g8e7, h7h5]
01:48:40.602 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:48:40.602                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
01:48:40.809                                                             📋 Response ID: resp_685b47687ba48192ba487357beef5e2f0c94cb7a457197fb
01:48:44.709                                                             🏁 Response completed
01:48:44.710 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c8d7": {"score": 0.0, "reason": "Passive and unambitious; avoids conflict ...
01:48:44.711                                                             ✅ JSON is already valid - no fixes needed
01:48:44.711                                                             🔧 Fixed JSON: {
                                                                           "move_scores": {
                                                                             "c8d7": {"score": 0.0, "reason": "Passive and unambitious; avoids conflict ...
01:48:44.712                                                             🎭 Parsed: 2 moves, conf=0.9
01:48:44.712                                                             🎭 AI moves: [b7b5, h7h5]
01:48:44.772 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b7b5
01:48:45.138 GameHistoryManager                                          Move added: b7b5
01:48:45.138 GameViewModel                                               🔍 Requesting position evaluation...
01:48:45.138                                                             🎭 Updating personality context for move: b7b5
01:48:45.139                                                             ✨ Personality context updated for move b7b5 - This is revolutionary!
01:48:45.139                                                             🔍 Checking game end conditions...
01:48:45.240                                                             ✅ Game continues - no end condition detected
01:48:45.952 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 4.33 (move=12, alternating=ON, FEN=r1bqkbnr/p4ppp/8/1pp)
01:48:45.953                                                             🔄 ALTERNATING: move=12, flip=false, 4.33→4.33 (diff=1.10)
01:48:45.953 GameViewModel                                               ✅ Evaluation received: 4.33
01:48:54.674 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
01:48:55.154 GameViewModel                                               🎯 makePlayerMove called with: f1b5
01:48:55.206                                                             🔍 Validating move: f1b5 (attempt 1)
01:48:55.306                                                             📋 Current position: r1bqkbnr/p4ppp/8/1ppPp3/8/1N6/PPP2PPP/RNBQKB1R w KQkq - 0 7
01:48:55.358                                                             ⚖️ Move f1b5 legality check: LEGAL
01:48:55.358                                                             ✅ Executing validated move: f1b5
01:48:55.560                                                             📍 New position after move: r1bqkbnr/p4ppp/8/1BpPp3/8/1N6/PPP2PPP/RNBQK2R b KQkq - 0 7
01:48:55.567 GameHistoryManager                                          Move added: f1b5
01:48:55.661 GameViewModel                                               🔍 Requesting position evaluation...
01:48:55.662                                                             🎭 Using PERSONALITY ENGINE for move calculation!
01:48:55.662                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
01:48:55.662                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
01:48:55.667 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$14$com-example-chesspedagogue-MainActivity:624 
01:48:55.669                                                             show: isDexDualMode = false
01:48:55.669                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
01:48:55.676 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
01:48:55.676                                                             🔧 DEBUG: gameRepository instance = NOT NULL
01:48:55.676                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
01:48:55.676                                                             🔧 gameRepository class: GameRepository
01:48:55.676                                                             🔧 Current thread: main
01:48:56.019 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -7.66 (move=13, alternating=ON, FEN=r1bqkbnr/p4ppp/8/1Bp)
01:48:56.020                                                             🔄 ALTERNATING: move=13, flip=true, -7.66→7.66 (diff=3.33)
01:48:56.420 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #9)
01:48:56.420                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:48:56.420                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkbnr/p4ppp/8/1BpPp3/8/1N6/PPP2PPP/RNBQK2R...
01:48:56.420                                                             🎲 Candidate moves for AI analysis: [c8d7, d8d7, e8e7]
01:48:56.420 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:48:56.420                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
01:48:56.718 GameViewModel                                               ✅ Evaluation received: 7.66
01:48:56.732 ResponsesAPI                                                📋 Response ID: resp_685b47785d208191b22d188be38df4ec03f1e93fc8d1a83d
01:48:58.563                                                             🏁 Response completed
01:48:58.564 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c8d7": {"score": 0.8, "reason": "Develops with tension; invites complicati...
01:48:58.564                                                             ✅ JSON is already valid - no fixes needed
01:48:58.564                                                             🔧 Fixed JSON: {
                                                                           "move_scores": {
                                                                             "c8d7": {"score": 0.8, "reason": "Develops with tension; invites complicati...
01:48:58.564                                                             🎭 Parsed: 1 moves, conf=0.8
01:48:58.564                                                             🎭 AI moves: [c8d7]
01:48:58.599 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c8d7
01:48:58.964 GameHistoryManager                                          Move added: c8d7
01:48:58.964 GameViewModel                                               🔍 Requesting position evaluation...
01:48:58.965                                                             🎭 Updating personality context for move: c8d7
01:48:58.965                                                             ✨ Personality context updated for move c8d7 - This is revolutionary!
01:48:58.965                                                             🔍 Checking game end conditions...
01:48:59.067                                                             ✅ Game continues - no end condition detected
01:48:59.628 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 4.62 (move=14, alternating=ON, FEN=r2qkbnr/p2b1ppp/8/1B)
01:48:59.628                                                             🔄 ALTERNATING: move=14, flip=false, 4.62→4.62 (diff=3.04)
01:48:59.629 GameViewModel                                               ✅ Evaluation received: 4.62
01:49:16.446                                                             🎯 makePlayerMove called with: d1e2
01:49:16.497                                                             🔍 Validating move: d1e2 (attempt 1)
01:49:16.598                                                             📋 Current position: r2qkbnr/p2b1ppp/8/1BpPp3/8/1N6/PPP2PPP/RNBQK2R w KQkq - 1 8
01:49:16.650                                                             ⚖️ Move d1e2 legality check: LEGAL
01:49:16.650                                                             ✅ Executing validated move: d1e2
01:49:16.852                                                             📍 New position after move: r2qkbnr/p2b1ppp/8/1BpPp3/8/1N6/PPP1QPPP/RNB1K2R b KQkq - 2 8
01:49:16.859 GameHistoryManager                                          Move added: d1e2
01:49:16.952 GameViewModel                                               🔍 Requesting position evaluation...
01:49:16.953                                                             🎭 Using PERSONALITY ENGINE for move calculation!
01:49:16.953                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
01:49:16.953                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
01:49:16.956 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$14$com-example-chesspedagogue-MainActivity:624 
01:49:16.957                                                             show: isDexDualMode = false
01:49:16.957                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
01:49:16.963 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
01:49:16.963                                                             🔧 DEBUG: gameRepository instance = NOT NULL
01:49:16.963                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
01:49:16.963                                                             🔧 gameRepository class: GameRepository
01:49:16.963                                                             🔧 Current thread: main
01:49:17.414 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -6.05 (move=15, alternating=ON, FEN=r2qkbnr/p2b1ppp/8/1B)
01:49:17.414                                                             🔄 ALTERNATING: move=15, flip=true, -6.05→6.05 (diff=1.43)
01:49:17.857 GameViewModel                                               ✅ Evaluation received: 6.05
01:49:17.920 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #10)
01:49:17.920                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:49:17.920                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qkbnr/p2b1ppp/8/1BpPp3/8/1N6/PPP1QPPP/RNB1K...
01:49:17.920                                                             🎲 Candidate moves for AI analysis: [a8c8, a7a6, d8c8, d7b5, f8d6, h7h5, c5c4, g8f6]
01:49:17.920 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:49:17.920                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
01:49:18.164                                                             📋 Response ID: resp_685b478dd548819fb515c341a304e8370bfd0fc4a825c727
01:49:21.406                                                             🏁 Response completed
01:49:21.407 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "a8c8": {"score": 0.0, "reason": "A passive retreat, abandoning central ten...
01:49:21.408                                                             🔧 JSON needs fixing: Expected literal value at character 732 of {
                                                                           "move_scores": {
                                                                             "a8c8": {"score": 0.0, "reason": "A passive retreat, abandoning central tension. Not in keeping with my fighting spirit."},
                                                                             "a7a6": {"score": 0.2, "reason": "A useful waiting move, but lacks ambition and fails to create complications."},
                                                                             "d8c8": {"score": 0.0, "reason": "Unnecessary simplification. Avoided unless forced."},
                                                                             "d7b5": {"score": 0.3, "reason": "An attempt to complicate, but misplaces the queen and wastes time."},
                                                                             "f8d6": {"score": 0.6, "reason": "Develops with purpose, but the resulting position is too balanced for my taste."},
                                                                             "h7h5": {"score": 0.7, "reason": "A provocative move, seeking to unsettle and create weaknesses on the flank."},
                                                                             "c5c4": {"score": 1.0, \"reason\": \"Perfectly Alekhine: seizes space, unbalances the position, and opens lines for attack. The kind of move that leads to rich, complex battles.\"},
                                                                             "g8f6": {"score": 0.5, \"reason\": \"Solid development, but misses an opportunity to fight for the initiative.\"}
                                                                           },
                                                                           "top_choice": "c5c4",
                                                                           "style_reasoning": "I seek the fight in every position. Moves that simplify or retreat are not in my nature. I prefer those that open doors for creativity and complicate the path for my opponent.",
                                                                           "confidence": 1.0
                                                                         }
01:49:21.410                                                             🔧 Applying quote mismatch fixes
01:49:21.411                                                             🛠️ JSON fix applied - original length: 1259, fixed length: 1251
01:49:21.411                                                             🔧 Fixed JSON: {
                                                                           "move_scores": {
                                                                             "a8c8": {"score": 0.0, "reason": "A passive retreat, abandoning central ten...
01:49:21.412                                                             🎭 Parsed: 3 moves, conf=1.0
01:49:21.412                                                             🎭 AI moves: [c5c4, h7h5, f8d6]
01:49:21.459 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c5c4
01:49:21.826 GameHistoryManager                                          Move added: c5c4
01:49:21.826 GameViewModel                                               🔍 Requesting position evaluation...
01:49:21.827                                                             🎭 Updating personality context for move: c5c4
01:49:21.827                                                             ✨ Personality context updated for move c5c4 - This is revolutionary!
01:49:21.828                                                             🔍 Checking game end conditions...
01:49:21.929                                                             ✅ Game continues - no end condition detected
01:49:22.590 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 4.19 (move=16, alternating=ON, FEN=r2qkbnr/p2b1ppp/8/1B)
01:49:22.590                                                             🔄 ALTERNATING: move=16, flip=false, 4.19→4.19 (diff=1.86)
01:49:22.590 GameViewModel                                               ✅ Evaluation received: 4.19
01:49:38.268                                                             🎯 makePlayerMove called with: e2e5
01:49:38.318                                                             🔍 Validating move: e2e5 (attempt 1)
01:49:38.419                                                             📋 Current position: r2qkbnr/p2b1ppp/8/1B1Pp3/2p5/1N6/PPP1QPPP/RNB1K2R w KQkq - 0 9
01:49:38.470                                                             ⚖️ Move e2e5 legality check: LEGAL
01:49:38.470                                                             ✅ Executing validated move: e2e5
01:49:38.673                                                             📍 New position after move: r2qkbnr/p2b1ppp/8/1B1PQ3/2p5/1N6/PPP2PPP/RNB1K2R b KQkq - 0 9
01:49:38.680 GameHistoryManager                                          Move added: e2e5
01:49:38.773 GameViewModel                                               🔍 Requesting position evaluation...
01:49:38.774                                                             🎭 Using PERSONALITY ENGINE for move calculation!
01:49:38.774                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
01:49:38.774                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
01:49:38.778 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$14$com-example-chesspedagogue-MainActivity:624 
01:49:38.779                                                             show: isDexDualMode = false
01:49:38.779                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
01:49:38.786 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
01:49:38.786                                                             🔧 DEBUG: gameRepository instance = NOT NULL
01:49:38.786                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
01:49:38.786                                                             🔧 gameRepository class: GameRepository
01:49:38.786                                                             🔧 Current thread: main
01:49:39.080 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -5.90 (move=17, alternating=ON, FEN=r2qkbnr/p2b1ppp/8/1B)
01:49:39.080                                                             🔄 ALTERNATING: move=17, flip=true, -5.90→5.90 (diff=1.71)
01:49:39.474 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #11)
01:49:39.474                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:49:39.474                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qkbnr/p2b1ppp/8/1B1PQ3/2p5/1N6/PPP2PPP/RNB1...
01:49:39.474                                                             🎲 Candidate moves for AI analysis: [d8e7, f8e7, g8e7]
01:49:39.474 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:49:39.474                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
01:49:39.704                                                             📋 Response ID: resp_685b47a35c4481a1ae3045167293cafa07b9e85f37a30704
01:49:39.828 GameViewModel                                               ✅ Evaluation received: 5.90
01:49:41.338 ResponsesAPI                                                🏁 Response completed
01:49:41.339 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d8e7": {"score": 0.0, "reason": "A passive retreat from danger. Such simpl...
01:49:41.339                                                             ✅ JSON is already valid - no fixes needed
01:49:41.340                                                             🔧 Fixed JSON: {
                                                                           "move_scores": {
                                                                             "d8e7": {"score": 0.0, "reason": "A passive retreat from danger. Such simpl...
01:49:41.340                                                             🎭 Parsed: 1 moves, conf=0.9
01:49:41.340                                                             🎭 AI moves: [g8e7]
01:49:41.382 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g8e7
01:49:41.750 GameHistoryManager                                          Move added: g8e7
01:49:41.750 GameViewModel                                               🔍 Requesting position evaluation...
01:49:41.750                                                             🎭 Updating personality context for move: g8e7
01:49:41.750                                                             ✨ Personality context updated for move g8e7 - This is revolutionary!
01:49:41.751                                                             🔍 Checking game end conditions...
01:49:41.853                                                             ✅ Game continues - no end condition detected
01:49:42.363 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 4.84 (move=18, alternating=ON, FEN=r2qkb1r/p2bnppp/8/1B)
01:49:42.363                                                             🔄 ALTERNATING: move=18, flip=false, 4.84→4.84 (diff=1.06)
01:49:42.364 GameViewModel                                               ✅ Evaluation received: 4.84
01:49:50.134                                                             🎯 makePlayerMove called with: b5c4
01:49:50.185                                                             🔍 Validating move: b5c4 (attempt 1)
01:49:50.287                                                             📋 Current position: r2qkb1r/p2bnppp/8/1B1PQ3/2p5/1N6/PPP2PPP/RNB1K2R w KQkq - 1 10
01:49:50.338                                                             ⚖️ Move b5c4 legality check: LEGAL
01:49:50.338                                                             ✅ Executing validated move: b5c4
01:49:50.540                                                             📍 New position after move: r2qkb1r/p2bnppp/8/3PQ3/2B5/1N6/PPP2PPP/RNB1K2R b KQkq - 0 10
01:49:50.547 GameHistoryManager                                          Move added: b5c4
01:49:50.640 GameViewModel                                               🔍 Requesting position evaluation...
01:49:50.641                                                             🎭 Using PERSONALITY ENGINE for move calculation!
01:49:50.641                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
01:49:50.641                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
01:49:50.645 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.lambda$setupPersonalityObservers$14$com-example-chesspedagogue-MainActivity:624 
01:49:50.645                                                             show: isDexDualMode = false
01:49:50.646                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
01:49:50.651 GameViewModel                                               🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
01:49:50.651                                                             🔧 DEBUG: gameRepository instance = NOT NULL
01:49:50.651                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
01:49:50.651                                                             🔧 gameRepository class: GameRepository
01:49:50.651                                                             🔧 Current thread: main
01:49:51.052 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -6.87 (move=19, alternating=ON, FEN=r2qkb1r/p2bnppp/8/3P)
01:49:51.052                                                             🔄 ALTERNATING: move=19, flip=true, -6.87→6.87 (diff=2.03)
01:49:51.593 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #12)
01:49:51.593                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:49:51.593                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qkb1r/p2bnppp/8/3PQ3/2B5/1N6/PPP2PPP/RNB1K2...
01:49:51.593                                                             🎲 Candidate moves for AI analysis: [a8c8, a8b8, d8b8, d8b6, a7a5, d8c8, h7h5, f7f6]
01:49:51.593 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
01:49:51.593                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
01:49:51.595 GameViewModel                                               ✅ Evaluation received: 6.87
01:49:51.778 ResponsesAPI                                                📋 Response ID: resp_685b47af798081a19747a2c766f6cc820896c5f27f58b14a
01:49:55.117                                                             🏁 Response completed
01:49:55.118 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "a8c8": {"score": 0.0, "reason": "Passive and retreating; avoids confrontat...
01:49:55.119                                                             ✅ JSON is already valid - no fixes needed
01:49:55.119                                                             🔧 Fixed JSON: {
                                                                           "move_scores": {
                                                                             "a8c8": {"score": 0.0, "reason": "Passive and retreating; avoids confrontat...
01:49:55.119                                                             🎭 Parsed: 3 moves, conf=1.0
01:49:55.120                                                             🎭 AI moves: [f7f6, h7h5, d8b6]
01:49:55.157 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f7f6
01:49:55.524 GameHistoryManager                                          Move added: f7f6
01:49:55.524 GameViewModel                                               🔍 Requesting position evaluation...
01:49:55.524                                                             🎭 Updating personality context for move: f7f6
01:49:55.525                                                             ✨ Personality context updated for move f7f6 - This is revolutionary!
01:49:55.525                                                             🔍 Checking game end conditions...
01:49:55.627                                                             ✅ Game continues - no end condition detected
01:49:56.143 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 4.61 (move=20, alternating=ON, FEN=r2qkb1r/p2bn1pp/5p2/)
01:49:56.143                                                             🔄 ALTERNATING: move=20, flip=false, 4.61→4.61 (diff=2.26)
01:49:56.143 GameViewModel                                               ✅ Evaluation received: 4.61
01:50:20.507 WindowManager                                               WindowManagerGlobal#addView, ty=1002, view=android.widget.PopupWindow$PopupDecorView{c16192 V.E...... R.....I. 0,0-0,0}, caller=android.view.WindowManagerImpl.addView:158 android.widget.PopupWindow.invokePopup:1712 android.widget.PopupWindow.showAsDropDown:1499 
01:50:20.508 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
01:50:20.514 VRI[PopupW...d]@6fd3763                                     synced displayState. AttachInfo displayState=2
01:50:20.515                                                             setView = android.widget.PopupWindow$PopupDecorView@c16192 IsHRR=false TM=true
01:50:20.534 BufferQueueProducer                                         [](id:451600000002,api:0,p:557780971,c:17686) setDequeueTimeout:2077252342
01:50:20.535 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
01:50:20.535                                                             Access denied finding property "vendor.gpp.create_frc_extension"
01:50:20.535 VRI[PopupW...d]@6fd3763                                     Relayout returned: old=(658,116,1425,116) new=(658,116,1425,2516) relayoutAsync=false req=(767,2400)0 dur=5 res=0x3 s={true 0xb4000070e7cdd800} ch=true seqId=0
01:50:20.535                                                             performConfigurationChange setNightDimText nightDimLevel=0
01:50:20.535                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb4000070e7cdd800} hwInitialized=true
01:50:20.558 AbsListView                                                  in onLayout changed 
01:50:20.559 VRI[PopupW...d]@6fd3763                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
01:50:20.560                                                             Setup new sync=wmsSync-VRI[PopupWindow:bd62b1d]@6fd3763#4
01:50:20.560                                                             Creating new active sync group VRI[PopupWindow:bd62b1d]@6fd3763#5
01:50:20.560                                                             registerCallbacksForSync syncBuffer=false
01:50:20.562                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
01:50:20.562                                                             mWNT: t=0xb4000070e779ae00 mBlastBufferQueue=0xb4000070e7760600 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
01:50:20.562                                                             Setting up sync and frameCommitCallback
01:50:20.568 BLASTBufferQueue                                            [VRI[PopupWindow:bd62b1d]@6fd3763#2](f:0,a:0,s:0) onFrameAvailable the first frame is available
01:50:20.568 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 64949, bufferData(ID: 75960791597067, frameNumber: 1)
01:50:20.569 VRI[PopupW...d]@6fd3763                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
01:50:20.569 HWUI                                                        CFMS:: SetUp Pid : 17686    Tid : 17713
01:50:20.569 VRI[PopupW...d]@6fd3763                                     reportDrawFinished seqId=0
01:50:20.570 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
01:50:20.571 VRI[PopupW...d]@6fd3763                                     handleResized, frames=ClientWindowFrames{frame=[658,116][1425,2516] display=[-100000,-100000][100000,100000] parentFrame=[0,0][0,0] attachedFrame=[0,0][1440,3088]} displayId=0 dragResizing=false compatScale=1.0 frameChanged=false attachedFrameChanged=false configChanged=false displayChanged=false compatScaleChanged=false dragResizingChanged=false
01:50:20.571                                                             handleResized mSyncSeqId = 0
01:50:20.571                                                             reportNextDraw android.view.ViewRootImpl.handleResized:2864 android.view.ViewRootImpl.-$$Nest$mhandleResized:0 android.view.ViewRootImpl$W.resized:13691 android.app.servertransaction.WindowStateResizeItem.execute:64 android.app.servertransaction.WindowStateTransactionItem.execute:59 
01:50:20.577                                                             Setup new sync=wmsSync-VRI[PopupWindow:bd62b1d]@6fd3763#6
01:50:20.577                                                             Creating new active sync group VRI[PopupWindow:bd62b1d]@6fd3763#7
01:50:20.577                                                             registerCallbacksForSync syncBuffer=false
01:50:20.577                                                             Received frameDrawingCallback syncResult=0 frameNum=2.
01:50:20.577                                                             Setting up sync and frameCommitCallback
01:50:20.578                                                             Received frameCommittedCallback lastAttemptedDrawFrameNum=2 didProduceBuffer=true
01:50:20.579                                                             reportDrawFinished seqId=0
01:50:20.593                                                             mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb4000070e7cdd800}
01:50:22.058                                                             ViewPostIme pointer 0
01:50:22.060                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[PopupWindow:bd62b1d]@6fd3763
01:50:22.209                                                             ViewPostIme pointer 1
01:50:22.210 AbsListView                                                 onTouchUp() mTouchMode : 2
01:50:22.214 Toast                                                       show: caller = com.example.chesspedagogue.MainActivity.runQuickStyleValidation:3554 
01:50:22.214                                                             show: isDexDualMode = false
01:50:22.215                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
01:50:22.224 VRI[PopupW...d]@6fd3763                                     Relayout returned: old=(658,116,1425,2516) new=(658,116,1425,2516) relayoutAsync=true req=(767,2400)0 dur=0 res=0x0 s={true 0xb4000070e7cdd800} ch=false seqId=0
01:50:22.224 LightweightValidator                                        🚀 Starting lightweight Alekhine validation...
01:50:22.225 VRI[PopupW...d]@6fd3763                                     registerCallbackForPendingTransactions
01:50:22.228                                                             mWNT: t=0xb4000070e779b280 mBlastBufferQueue=0xb4000070e7760600 fn= 39 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$9.onFrameDraw:6276 android.view.ViewRootImpl$3.onFrameDraw:2440 android.view.ThreadedRenderer$1.onFrameDraw:761 
01:50:22.243 LightweightValidator                                        📦 Loaded 20 test positions from Alekhine games
01:50:22.243                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:50:22.243 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:50:22.244                                                             📤 Validation request body length: 2747
01:50:22.248 InputMethodManagerUtils                                     startInputInner - Id : 0
01:50:22.248 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
01:50:22.530 WindowManager                                               WindowManagerGlobal#removeView, ty=1002, view=android.widget.PopupWindow$PopupDecorView{c16192 V.E...... R.....ID 0,0-767,2400}, caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.widget.PopupWindow.dismissImmediate:2421 
01:50:22.531 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@31390ea
01:50:22.535 VRI[PopupW...d]@6fd3763                                     dispatchDetachedFromWindow
01:50:28.189 OpenAIService                                               📥 Validation response received, length: 2554
01:50:28.190                                                             ✅ Style validation response extracted successfully
01:50:28.190 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:50:28.190                                                             📊 Position assessed: 0.34 confidence, identified as: Garry Kasparov
01:50:28.190                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:50:28.190 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:50:28.191                                                             📤 Validation request body length: 2750
01:50:33.111                                                             📥 Validation response received, length: 2220
01:50:33.111                                                             ✅ Style validation response extracted successfully
01:50:33.112 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:50:33.112                                                             📊 Position assessed: 0.4 confidence, identified as: José Capablanca
01:50:33.112                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:50:33.112 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:50:33.113                                                             📤 Validation request body length: 2769
01:50:37.838                                                             📥 Validation response received, length: 2186
01:50:37.839                                                             ✅ Style validation response extracted successfully
01:50:37.839 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:50:37.839                                                             📊 Position assessed: 0.4 confidence, identified as: Garry Kasparov
01:50:37.840                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:50:37.840 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:50:37.841                                                             📤 Validation request body length: 2750
01:50:44.377                                                             📥 Validation response received, length: 2719
01:50:44.377                                                             ✅ Style validation response extracted successfully
01:50:44.377 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:50:44.378                                                             📊 Position assessed: 0.32 confidence, identified as: Garry Kasparov
01:50:44.378                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:50:44.378 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:50:44.379                                                             📤 Validation request body length: 2757
01:50:50.168                                                             📥 Validation response received, length: 2128
01:50:50.168                                                             ✅ Style validation response extracted successfully
01:50:50.169 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:50:50.169                                                             📊 Position assessed: 0.38 confidence, identified as: José Capablanca
01:50:50.169                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:50:50.169 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:50:50.170                                                             📤 Validation request body length: 2748
01:50:55.975                                                             📥 Validation response received, length: 2143
01:50:55.975                                                             ✅ Style validation response extracted successfully
01:50:55.976 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:50:55.976                                                             📊 Position assessed: 0.82 confidence, identified as: José Capablanca
01:50:55.977                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:50:55.977 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:50:55.979                                                             📤 Validation request body length: 2767
01:51:03.152                                                             📥 Validation response received, length: 2016
01:51:03.152                                                             ✅ Style validation response extracted successfully
01:51:03.152 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:03.153                                                             📊 Position assessed: 0.82 confidence, identified as: José Capablanca
01:51:03.153                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:03.153 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:03.154                                                             📤 Validation request body length: 2767
01:51:07.561                                                             📥 Validation response received, length: 2133
01:51:07.561                                                             ✅ Style validation response extracted successfully
01:51:07.561 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:07.562                                                             📊 Position assessed: 0.82 confidence, identified as: José Capablanca
01:51:07.562                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:07.562 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:07.563                                                             📤 Validation request body length: 2749
01:51:15.309                                                             📥 Validation response received, length: 2215
01:51:15.309                                                             ✅ Style validation response extracted successfully
01:51:15.310 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:15.310                                                             📊 Position assessed: 0.82 confidence, identified as: José Capablanca
01:51:15.310                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:15.311 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:15.312                                                             📤 Validation request body length: 2753
01:51:20.427                                                             📥 Validation response received, length: 2037
01:51:20.427                                                             ✅ Style validation response extracted successfully
01:51:20.428 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:20.428                                                             📊 Position assessed: 0.78 confidence, identified as: José Capablanca
01:51:20.429                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:20.429 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:20.431                                                             📤 Validation request body length: 2738
01:51:24.693                                                             📥 Validation response received, length: 1993
01:51:24.694                                                             ✅ Style validation response extracted successfully
01:51:24.695 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:24.695                                                             📊 Position assessed: 0.82 confidence, identified as: José Capablanca
01:51:24.696                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:24.696 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:24.698                                                             📤 Validation request body length: 2741
01:51:28.686                                                             📥 Validation response received, length: 2068
01:51:28.686                                                             ✅ Style validation response extracted successfully
01:51:28.687 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:28.687                                                             📊 Position assessed: 0.78 confidence, identified as: José Capablanca
01:51:28.688                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:28.688 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:28.690                                                             📤 Validation request body length: 2760
01:51:34.357                                                             📥 Validation response received, length: 2184
01:51:34.358                                                             ✅ Style validation response extracted successfully
01:51:34.358 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:34.359                                                             📊 Position assessed: 0.8 confidence, identified as: Alexander Alekhine
01:51:34.359                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:34.359 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:34.361                                                             📤 Validation request body length: 2738
01:51:41.594                                                             📥 Validation response received, length: 2129
01:51:41.595                                                             ✅ Style validation response extracted successfully
01:51:41.596 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:41.596                                                             📊 Position assessed: 0.78 confidence, identified as: José Capablanca
01:51:41.597                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:41.597 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:41.599                                                             📤 Validation request body length: 2760
01:51:47.870                                                             📥 Validation response received, length: 2181
01:51:47.870                                                             ✅ Style validation response extracted successfully
01:51:47.871 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:47.872                                                             📊 Position assessed: 0.87 confidence, identified as: Alexander Alekhine
01:51:47.872                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:47.872 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:47.875                                                             📤 Validation request body length: 2750
01:51:52.871                                                             📥 Validation response received, length: 2406
01:51:52.872                                                             ✅ Style validation response extracted successfully
01:51:52.872 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:52.873                                                             📊 Position assessed: 0.32 confidence, identified as: Garry Kasparov
01:51:52.873                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:52.874 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:52.876                                                             📤 Validation request body length: 2741
01:51:56.578                                                             📥 Validation response received, length: 2060
01:51:56.579                                                             ✅ Style validation response extracted successfully
01:51:56.579 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:51:56.580                                                             📊 Position assessed: 0.83 confidence, identified as: José Capablanca
01:51:56.580                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:51:56.580 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:51:56.582                                                             📤 Validation request body length: 2750
01:52:00.948                                                             📥 Validation response received, length: 2300
01:52:00.949                                                             ✅ Style validation response extracted successfully
01:52:00.949 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:52:00.950                                                             📊 Position assessed: 0.72 confidence, identified as: José Capablanca
01:52:00.950                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:52:00.950 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:52:00.952                                                             📤 Validation request body length: 2743
01:52:05.581                                                             📥 Validation response received, length: 2093
01:52:05.582                                                             ✅ Style validation response extracted successfully
01:52:05.582 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:52:05.583                                                             📊 Position assessed: 0.78 confidence, identified as: Alexander Alekhine
01:52:05.583                                                             🤖 Calling GPT-4.1-2025-04-14 for style validation...
01:52:05.584 OpenAIService                                               🤖 Sending style validation request to gpt-4.1-2025-04-14
01:52:05.586                                                             📤 Validation request body length: 2744
01:52:11.725                                                             📥 Validation response received, length: 2194
01:52:11.725                                                             ✅ Style validation response extracted successfully
01:52:11.726 LightweightValidator                                        ✅ GPT-4.1 validation response received
01:52:11.726                                                             📊 Position assessed: 0.78 confidence, identified as: Alexander Alekhine
01:52:11.727                                                             ✅ Validation complete: 4/20 correct (20.0%)