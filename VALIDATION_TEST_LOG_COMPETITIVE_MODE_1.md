meRepository instance = NOT NULL
20:21:48.655                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:21:48.655                                                             🔧 gameRepository class: GameRepository
20:21:48.655                                                             🔧 Current thread: main
20:21:48.655 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:21:49.967 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.32 (move=7, alternating=ON, FEN=rnbqkb1r/pp2pppp/2p2)
20:21:49.968                                                             🔄 ALTERNATING: move=7, flip=true, -0.32→0.32 (diff=0.03)
20:21:49.968 GameViewModel                                               ✅ Evaluation received: 0.32
20:21:50.069 CompetitiveModeActivity                                     📊 Evaluation updated: 0.32
20:21:50.069                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.32, emotionalManager: INITIALIZED
20:21:50.069                                                             🎯 First emotional evaluation: 0.32 (threshold: 1.5)
20:21:50.069                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620110s/25s)
20:21:50.069                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:21:50.735 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:21:50.736 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #6)
20:21:50.736                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:21:50.736                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp2pppp/2p2n2/3p4/2PP4/2N2N2/PP2PPPP...
20:21:50.736                                                             🎲 Candidate moves for AI analysis: [g7g6]
20:21:50.736 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:21:50.736                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:21:50.972                                                             📋 Response ID: resp_685857cec0a0819ca0eaa19eed18f3aa0c575b22ef520be4
20:21:52.440                                                             🏁 Response completed
20:21:52.441 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "g7g6": {"score": 0.0, "reason": "Passive and symmetrical. Avoids tension a...
20:21:52.442                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
20:21:52.442                                                             🎭 AI preferred moves: []
20:21:52.442                                                             💭 AI reasoning: I seek to unbalance the game from the start, forcing my opponent into unfamiliar territory.
20:21:52.492 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: g7g6
20:21:52.848 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp2pp1p/2p2np1/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 0 5
20:21:52.852                                                             📜 Move history updated: 8 moves
20:21:52.852 GameHistoryManager                                          Move added: g7g6
20:21:52.852 GameViewModel                                               🔍 Requesting position evaluation...
20:21:52.853                                                             🎭 Updating personality context for move: g7g6
20:21:52.853                                                             ✨ Personality context updated for move g7g6 - This is revolutionary!
20:21:52.853                                                             🔍 Checking game end conditions...
20:21:52.956                                                             ✅ Game continues - no end condition detected
20:21:53.813 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.57 (move=8, alternating=ON, FEN=rnbqkb1r/pp2pp1p/2p2)
20:21:53.813                                                             🔄 ALTERNATING: move=8, flip=false, 0.57→0.57 (diff=0.25)
20:21:53.818 GameViewModel                                               ✅ Evaluation received: 0.57
20:21:53.921 CompetitiveModeActivity                                     📊 Evaluation updated: 0.57
20:21:53.921                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.57, emotionalManager: INITIALIZED
20:21:53.921                                                             🎯 First emotional evaluation: 0.57 (threshold: 1.5)
20:21:53.921                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620113s/25s)
20:21:53.921                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:22:02.178                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=2
20:22:02.178                                                             🎯 SQUARE TAPPED: row=7, col=2
20:22:02.178                                                             📝 Player color: white
20:22:02.178                                                             🔍 Selected row/col: -1/-1
20:22:02.535                                                             🎯 Selected piece: B at 7, 2
20:22:02.868                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
20:22:02.868                                                             🎯 SQUARE TAPPED: row=4, col=5
20:22:02.868                                                             📝 Player color: white
20:22:02.868                                                             🔍 Selected row/col: 7/2
20:22:02.868                                                             🎯 ATTEMPTING MOVE: c1f4
20:22:02.868                                                             📝 Player color: white
20:22:02.868                                                             🔄 Is player's turn: true
20:22:02.868                                                             🔄 Is white's turn: true
20:22:02.868                                                             ✅ Turn validation passed, making move: c1f4
20:22:02.868 GameViewModel                                               🎯 makePlayerMove called with: c1f4
20:22:02.919                                                             🔍 Validating move: c1f4 (attempt 1)
20:22:03.021                                                             📋 Current position: rnbqkb1r/pp2pp1p/2p2np1/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 0 5
20:22:03.072                                                             ⚖️ Move c1f4 legality check: LEGAL
20:22:03.072                                                             ✅ Executing validated move: c1f4
20:22:03.274                                                             📍 New position after move: rnbqkb1r/pp2pp1p/2p2np1/3p4/2PP1B2/2N2N2/PP2PPPP/R2QKB1R b KQkq - 1 5
20:22:03.277 CompetitiveModeActivity                                     🎯 Board updated with FEN: rnbqkb1r/pp2pp1p/2p2np1/3p4/2PP1B2/2N2N2/PP2PPPP/R2QKB1R b KQkq - 1 5
20:22:03.280                                                             📜 Move history updated: 9 moves
20:22:03.281 GameHistoryManager                                          Move added: c1f4
20:22:03.375 GameViewModel                                               🔍 Requesting position evaluation...
20:22:03.376                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:22:03.376                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:22:03.376                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:22:03.376                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:22:03.376                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:22:03.376                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:22:03.376                                                             🔧 gameRepository class: GameRepository
20:22:03.376                                                             🔧 Current thread: main
20:22:03.376 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:22:05.042 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.42 (move=9, alternating=ON, FEN=rnbqkb1r/pp2pp1p/2p2)
20:22:05.043                                                             🔄 ALTERNATING: move=9, flip=true, -0.42→0.42 (diff=0.15)
20:22:05.043 GameViewModel                                               ✅ Evaluation received: 0.42
20:22:05.144 CompetitiveModeActivity                                     📊 Evaluation updated: 0.42
20:22:05.144                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.42, emotionalManager: INITIALIZED
20:22:05.144                                                             🎯 First emotional evaluation: 0.42 (threshold: 1.5)
20:22:05.144                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620125s/25s)
20:22:05.144                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:22:05.702 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:22:05.703 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #7)
20:22:05.703                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:22:05.703                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqkb1r/pp2pp1p/2p2np1/3p4/2PP1B2/2N2N2/PP2P...
20:22:05.703                                                             🎲 Candidate moves for AI analysis: [b8d7]
20:22:05.703 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:22:05.703                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:22:05.881                                                             📋 Response ID: resp_685857ddb4e0819fb0b19b63bd0047200ead21fd14099736
20:22:07.529                                                             🏁 Response completed
20:22:07.530 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "b8d7": {"score": 0.0, "reason": "Passive development, avoids tension—antit...
20:22:07.531                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
20:22:07.531                                                             🎭 AI preferred moves: []
20:22:07.531                                                             💭 AI reasoning: I seek to create chaos and challenge my opponent’s nerves at every turn.
20:22:07.582 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: b8d7
20:22:07.943 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1npp1p/2p2np1/3p4/2PP1B2/2N2N2/PP2PPPP/R2QKB1R w KQkq - 2 6
20:22:07.948                                                             📜 Move history updated: 10 moves
20:22:07.948 GameHistoryManager                                          Move added: b8d7
20:22:07.948 GameViewModel                                               🔍 Requesting position evaluation...
20:22:07.949                                                             🎭 Updating personality context for move: b8d7
20:22:07.949                                                             ✨ Personality context updated for move b8d7 - This is revolutionary!
20:22:07.949                                                             🔍 Checking game end conditions...
20:22:08.051                                                             ✅ Game continues - no end condition detected
20:22:08.918 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.78 (move=10, alternating=ON, FEN=r1bqkb1r/pp1npp1p/2p)
20:22:08.918                                                             🔄 ALTERNATING: move=10, flip=false, 0.78→0.78 (diff=0.36)
20:22:08.919 GameViewModel                                               ✅ Evaluation received: 0.78
20:22:09.022 CompetitiveModeActivity                                     📊 Evaluation updated: 0.78
20:22:09.022                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.78, emotionalManager: INITIALIZED
20:22:09.022                                                             🎯 First emotional evaluation: 0.78 (threshold: 1.5)
20:22:09.022                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620129s/25s)
20:22:09.022                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:22:41.914                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=2
20:22:41.914                                                             🎯 SQUARE TAPPED: row=4, col=2
20:22:41.914                                                             📝 Player color: white
20:22:41.914                                                             🔍 Selected row/col: -1/-1
20:22:42.272                                                             🎯 Selected piece: P at 4, 2
20:22:42.414                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
20:22:42.414                                                             🎯 SQUARE TAPPED: row=3, col=3
20:22:42.414                                                             📝 Player color: white
20:22:42.414                                                             🔍 Selected row/col: 4/2
20:22:42.414                                                             ♟️ Promotion check: piece=P, fromRow=4, toRow=3, reaches=false
20:22:42.414                                                             🎯 ATTEMPTING MOVE: c4d5
20:22:42.414                                                             📝 Player color: white
20:22:42.414                                                             🔄 Is player's turn: true
20:22:42.415                                                             🔄 Is white's turn: true
20:22:42.415                                                             ✅ Turn validation passed, making move: c4d5
20:22:42.415 GameViewModel                                               🎯 makePlayerMove called with: c4d5
20:22:42.416                                                             🔍 Validating move: c4d5 (attempt 1)
20:22:42.517                                                             📋 Current position: r1bqkb1r/pp1npp1p/2p2np1/3p4/2PP1B2/2N2N2/PP2PPPP/R2QKB1R w KQkq - 2 6
20:22:42.569                                                             ⚖️ Move c4d5 legality check: LEGAL
20:22:42.569                                                             ✅ Executing validated move: c4d5
20:22:42.773                                                             📍 New position after move: r1bqkb1r/pp1npp1p/2p2np1/3P4/3P1B2/2N2N2/PP2PPPP/R2QKB1R b KQkq - 0 6
20:22:42.776 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1npp1p/2p2np1/3P4/3P1B2/2N2N2/PP2PPPP/R2QKB1R b KQkq - 0 6
20:22:42.780                                                             📜 Move history updated: 11 moves
20:22:42.780 GameHistoryManager                                          Move added: c4d5
20:22:42.781 Choreographer                                               Skipped 44 frames!  The application may be doing too much work on its main thread.
20:22:42.875 GameViewModel                                               🔍 Requesting position evaluation...
20:22:42.875                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:22:42.875                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:22:42.875                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:22:42.875                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:22:42.875                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:22:42.875                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:22:42.875                                                             🔧 gameRepository class: GameRepository
20:22:42.875                                                             🔧 Current thread: main
20:22:42.875 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:22:43.784 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.75 (move=11, alternating=ON, FEN=r1bqkb1r/pp1npp1p/2p)
20:22:43.784                                                             🔄 ALTERNATING: move=11, flip=true, -0.75→0.75 (diff=0.03)
20:22:43.785 GameViewModel                                               ✅ Evaluation received: 0.75
20:22:43.886 CompetitiveModeActivity                                     📊 Evaluation updated: 0.75
20:22:43.886                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.75, emotionalManager: INITIALIZED
20:22:43.886                                                             🎯 First emotional evaluation: 0.75 (threshold: 1.5)
20:22:43.886                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620163s/25s)
20:22:43.886                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:22:44.646 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:22:44.647 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #8)
20:22:44.648                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:22:44.648                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1npp1p/2p2np1/3P4/3P1B2/2N2N2/PP2P...
20:22:44.648                                                             🎲 Candidate moves for AI analysis: [f6d5]
20:22:44.648 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:22:44.648                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:22:44.834                                                             📋 Response ID: resp_68585804a648819f998513649eadf4a609cac9fafb1d8f42
20:22:47.497                                                             🏁 Response completed
20:22:47.498 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f6d5": {"score": 0.0, "reason": "Exchanging in the center so early leads t...
20:22:47.499                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
20:22:47.499                                                             🎭 AI preferred moves: []
20:22:47.499                                                             💭 AI reasoning: I seek to create and maintain tension. Moves that simplify too early are anathema to my approach. Chess is a fight, not a negotiation.
20:22:47.552 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f6d5
20:22:47.911 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1npp1p/2p3p1/3n4/3P1B2/2N2N2/PP2PPPP/R2QKB1R w KQkq - 0 7
20:22:47.917                                                             📜 Move history updated: 12 moves
20:22:47.917 GameHistoryManager                                          Move added: f6d5
20:22:47.917 GameViewModel                                               🔍 Requesting position evaluation...
20:22:47.917                                                             🎭 Updating personality context for move: f6d5
20:22:47.917                                                             ✨ Personality context updated for move f6d5 - This is revolutionary!
20:22:47.918                                                             🔍 Checking game end conditions...
20:22:48.018                                                             ✅ Game continues - no end condition detected
20:22:48.886 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.81 (move=12, alternating=ON, FEN=r1bqkb1r/pp1npp1p/2p)
20:22:48.886                                                             🔄 ALTERNATING: move=12, flip=false, 0.81→0.81 (diff=0.06)
20:22:48.887 GameViewModel                                               ✅ Evaluation received: 0.81
20:22:48.990 CompetitiveModeActivity                                     📊 Evaluation updated: 0.81
20:22:48.990                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.81, emotionalManager: INITIALIZED
20:22:48.990                                                             🎯 First emotional evaluation: 0.81 (threshold: 1.5)
20:22:48.991                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620168s/25s)
20:22:48.991                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:22:54.299                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=2
20:22:54.299                                                             🎯 SQUARE TAPPED: row=5, col=2
20:22:54.299                                                             📝 Player color: white
20:22:54.299                                                             🔍 Selected row/col: -1/-1
20:22:54.657                                                             🎯 Selected piece: N at 5, 2
20:22:54.905                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=3, col=3
20:22:54.905                                                             🎯 SQUARE TAPPED: row=3, col=3
20:22:54.905                                                             📝 Player color: white
20:22:54.905                                                             🔍 Selected row/col: 5/2
20:22:54.905                                                             🎯 ATTEMPTING MOVE: c3d5
20:22:54.905                                                             📝 Player color: white
20:22:54.905                                                             🔄 Is player's turn: true
20:22:54.905                                                             🔄 Is white's turn: true
20:22:54.905                                                             ✅ Turn validation passed, making move: c3d5
20:22:54.905 GameViewModel                                               🎯 makePlayerMove called with: c3d5
20:22:54.956                                                             🔍 Validating move: c3d5 (attempt 1)
20:22:55.057                                                             📋 Current position: r1bqkb1r/pp1npp1p/2p3p1/3n4/3P1B2/2N2N2/PP2PPPP/R2QKB1R w KQkq - 0 7
20:22:55.108                                                             ⚖️ Move c3d5 legality check: LEGAL
20:22:55.108                                                             ✅ Executing validated move: c3d5
20:22:55.310                                                             📍 New position after move: r1bqkb1r/pp1npp1p/2p3p1/3N4/3P1B2/5N2/PP2PPPP/R2QKB1R b KQkq - 0 7
20:22:55.312 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1npp1p/2p3p1/3N4/3P1B2/5N2/PP2PPPP/R2QKB1R b KQkq - 0 7
20:22:55.316                                                             📜 Move history updated: 13 moves
20:22:55.316 GameHistoryManager                                          Move added: c3d5
20:22:55.412 GameViewModel                                               🔍 Requesting position evaluation...
20:22:55.412                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:22:55.412                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:22:55.412                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:22:55.412                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:22:55.412                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:22:55.412                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:22:55.412                                                             🔧 gameRepository class: GameRepository
20:22:55.412                                                             🔧 Current thread: main
20:22:55.412 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:22:56.069 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.58 (move=13, alternating=ON, FEN=r1bqkb1r/pp1npp1p/2p)
20:22:56.069                                                             🔄 ALTERNATING: move=13, flip=true, -0.58→0.58 (diff=0.23)
20:22:56.078 GameViewModel                                               ✅ Evaluation received: 0.58
20:22:56.178 CompetitiveModeActivity                                     📊 Evaluation updated: 0.58
20:22:56.178                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.58, emotionalManager: INITIALIZED
20:22:56.178                                                             🎯 First emotional evaluation: 0.58 (threshold: 1.5)
20:22:56.178                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620176s/25s)
20:22:56.178                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:22:56.906 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:22:56.907 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #9)
20:22:56.907                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:22:56.907                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1npp1p/2p3p1/3N4/3P1B2/5N2/PP2PPPP...
20:22:56.907                                                             🎲 Candidate moves for AI analysis: [c6d5]
20:22:56.907 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:22:56.907                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:22:57.121                                                             📋 Response ID: resp_68585810ecdc81a2959a5add1b1d9d980812be19ec07fbc1
20:22:57.608 SQLiteConnectionPool                                        A SQLiteConnection object for database '/data/user/0/com.example.chesspedagogue/databases/chess_games.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
20:22:59.306 ResponsesAPI                                                🏁 Response completed
20:22:59.307 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c6d5": {"score": 0.0, "reason": "Simple exchange, leads to symmetry and cl...
20:22:59.308                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
20:22:59.308                                                             🎭 AI preferred moves: []
20:22:59.308                                                             💭 AI reasoning: I seek to unsettle and complicate from the first moves. Quiet positions are wasted opportunities.
20:22:59.347 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: c6d5
20:22:59.708 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1npp1p/6p1/3p4/3P1B2/5N2/PP2PPPP/R2QKB1R w KQkq - 0 8
20:22:59.714                                                             📜 Move history updated: 14 moves
20:22:59.714 GameHistoryManager                                          Move added: c6d5
20:22:59.714 GameViewModel                                               🔍 Requesting position evaluation...
20:22:59.715                                                             🎭 Updating personality context for move: c6d5
20:22:59.715                                                             ✨ Personality context updated for move c6d5 - This is revolutionary!
20:22:59.715                                                             🔍 Checking game end conditions...
20:22:59.817                                                             ✅ Game continues - no end condition detected
20:23:00.935 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.65 (move=14, alternating=ON, FEN=r1bqkb1r/pp1npp1p/6p)
20:23:00.935                                                             🔄 ALTERNATING: move=14, flip=false, 0.65→0.65 (diff=0.07)
20:23:00.936 GameViewModel                                               ✅ Evaluation received: 0.65
20:23:01.039 CompetitiveModeActivity                                     📊 Evaluation updated: 0.65
20:23:01.039                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.65, emotionalManager: INITIALIZED
20:23:01.039                                                             🎯 First emotional evaluation: 0.65 (threshold: 1.5)
20:23:01.040                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620181s/25s)
20:23:01.040                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:23:07.977                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=3
20:23:07.977                                                             🎯 SQUARE TAPPED: row=7, col=3
20:23:07.977                                                             📝 Player color: white
20:23:07.977                                                             🔍 Selected row/col: -1/-1
20:23:08.333                                                             🎯 Selected piece: Q at 7, 3
20:23:08.476                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=2
20:23:08.476                                                             🎯 SQUARE TAPPED: row=6, col=2
20:23:08.476                                                             📝 Player color: white
20:23:08.476                                                             🔍 Selected row/col: 7/3
20:23:08.476                                                             🎯 ATTEMPTING MOVE: d1c2
20:23:08.476                                                             📝 Player color: white
20:23:08.476                                                             🔄 Is player's turn: true
20:23:08.476                                                             🔄 Is white's turn: true
20:23:08.476                                                             ✅ Turn validation passed, making move: d1c2
20:23:08.476 GameViewModel                                               🎯 makePlayerMove called with: d1c2
20:23:08.527                                                             🔍 Validating move: d1c2 (attempt 1)
20:23:08.628                                                             📋 Current position: r1bqkb1r/pp1npp1p/6p1/3p4/3P1B2/5N2/PP2PPPP/R2QKB1R w KQkq - 0 8
20:23:08.679                                                             ⚖️ Move d1c2 legality check: LEGAL
20:23:08.679                                                             ✅ Executing validated move: d1c2
20:23:08.883                                                             📍 New position after move: r1bqkb1r/pp1npp1p/6p1/3p4/3P1B2/5N2/PPQ1PPPP/R3KB1R b KQkq - 1 8
20:23:08.885 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1npp1p/6p1/3p4/3P1B2/5N2/PPQ1PPPP/R3KB1R b KQkq - 1 8
20:23:08.889                                                             📜 Move history updated: 15 moves
20:23:08.889 GameHistoryManager                                          Move added: d1c2
20:23:08.891 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
20:23:08.988 GameViewModel                                               🔍 Requesting position evaluation...
20:23:08.989                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:23:08.989                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:23:08.989                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:23:08.989                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:23:08.989                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:23:08.989                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:23:08.989                                                             🔧 gameRepository class: GameRepository
20:23:08.990                                                             🔧 Current thread: main
20:23:08.990 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:23:10.403 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.44 (move=15, alternating=ON, FEN=r1bqkb1r/pp1npp1p/6p)
20:23:10.403                                                             🔄 ALTERNATING: move=15, flip=true, -0.44→0.44 (diff=0.21)
20:23:10.403 GameViewModel                                               ✅ Evaluation received: 0.44
20:23:10.504 CompetitiveModeActivity                                     📊 Evaluation updated: 0.44
20:23:10.504                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.44, emotionalManager: INITIALIZED
20:23:10.504                                                             🎯 First emotional evaluation: 0.44 (threshold: 1.5)
20:23:10.504                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620190s/25s)
20:23:10.504                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:23:11.132 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:23:11.133 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #10)
20:23:11.133                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:23:11.133                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1npp1p/6p1/3p4/3P1B2/5N2/PPQ1PPPP/...
20:23:11.133                                                             🎲 Candidate moves for AI analysis: [d8a5]
20:23:11.134 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:23:11.134                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:23:11.337                                                             📋 Response ID: resp_6858581f24e8819c9041ed027a8857a00b3c89e37e871920
20:23:13.793                                                             🏁 Response completed
20:23:13.794 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d8a5": {"score": 0.0, "reason": "A passive retreat; avoids complexity and ...
20:23:13.795                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
20:23:13.795                                                             🎭 AI preferred moves: []
20:23:13.795                                                             💭 AI reasoning: Alekhine seeks to unsettle his opponents from the first move. He craves the fight and the beautiful, tangled battles that follow.
20:23:13.829 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d8a5
20:23:14.190 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1b1kb1r/pp1npp1p/6p1/q2p4/3P1B2/5N2/PPQ1PPPP/R3KB1R w KQkq - 2 9
20:23:14.196                                                             📜 Move history updated: 16 moves
20:23:14.196 GameHistoryManager                                          Move added: d8a5
20:23:14.196 GameViewModel                                               🔍 Requesting position evaluation...
20:23:14.196                                                             🎭 Updating personality context for move: d8a5
20:23:14.196                                                             ✨ Personality context updated for move d8a5 - This is revolutionary!
20:23:14.197                                                             🔍 Checking game end conditions...
20:23:14.298                                                             ✅ Game continues - no end condition detected
20:23:15.112 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.56 (move=16, alternating=ON, FEN=r1b1kb1r/pp1npp1p/6p)
20:23:15.112                                                             🔄 ALTERNATING: move=16, flip=false, 0.56→0.56 (diff=0.12)
20:23:15.114 GameViewModel                                               ✅ Evaluation received: 0.56
20:23:15.217 CompetitiveModeActivity                                     📊 Evaluation updated: 0.56
20:23:15.217                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.56, emotionalManager: INITIALIZED
20:23:15.217                                                             🎯 First emotional evaluation: 0.56 (threshold: 1.5)
20:23:15.217                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620195s/25s)
20:23:15.217                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:23:22.784                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=5
20:23:22.784                                                             🎯 SQUARE TAPPED: row=4, col=5
20:23:22.784                                                             📝 Player color: white
20:23:22.784                                                             🔍 Selected row/col: -1/-1
20:23:23.139                                                             🎯 Selected piece: B at 4, 5
20:23:23.432                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=3
20:23:23.432                                                             🎯 SQUARE TAPPED: row=6, col=3
20:23:23.432                                                             📝 Player color: white
20:23:23.432                                                             🔍 Selected row/col: 4/5
20:23:23.432                                                             🎯 ATTEMPTING MOVE: f4d2
20:23:23.432                                                             📝 Player color: white
20:23:23.432                                                             🔄 Is player's turn: true
20:23:23.432                                                             🔄 Is white's turn: true
20:23:23.432                                                             ✅ Turn validation passed, making move: f4d2
20:23:23.432 GameViewModel                                               🎯 makePlayerMove called with: f4d2
20:23:23.483                                                             🔍 Validating move: f4d2 (attempt 1)
20:23:23.584                                                             📋 Current position: r1b1kb1r/pp1npp1p/6p1/q2p4/3P1B2/5N2/PPQ1PPPP/R3KB1R w KQkq - 2 9
20:23:23.636                                                             ⚖️ Move f4d2 legality check: LEGAL
20:23:23.636                                                             ✅ Executing validated move: f4d2
20:23:23.839                                                             📍 New position after move: r1b1kb1r/pp1npp1p/6p1/q2p4/3P4/5N2/PPQBPPPP/R3KB1R b KQkq - 3 9
20:23:23.841 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1b1kb1r/pp1npp1p/6p1/q2p4/3P4/5N2/PPQBPPPP/R3KB1R b KQkq - 3 9
20:23:23.846                                                             📜 Move history updated: 17 moves
20:23:23.846 GameHistoryManager                                          Move added: f4d2
20:23:23.940 GameViewModel                                               🔍 Requesting position evaluation...
20:23:23.940                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:23:23.940                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:23:23.940                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:23:23.940                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:23:23.941                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:23:23.941                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:23:23.941                                                             🔧 gameRepository class: GameRepository
20:23:23.941                                                             🔧 Current thread: main
20:23:23.941 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:23:25.151 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.53 (move=17, alternating=ON, FEN=r1b1kb1r/pp1npp1p/6p)
20:23:25.151                                                             🔄 ALTERNATING: move=17, flip=true, -0.53→0.53 (diff=0.03)
20:23:25.152 GameViewModel                                               ✅ Evaluation received: 0.53
20:23:25.253 CompetitiveModeActivity                                     📊 Evaluation updated: 0.53
20:23:25.253                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.53, emotionalManager: INITIALIZED
20:23:25.253                                                             🎯 First emotional evaluation: 0.53 (threshold: 1.5)
20:23:25.253                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620205s/25s)
20:23:25.253                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:23:25.838 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:23:25.839 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #11)
20:23:25.839                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:23:25.839                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1b1kb1r/pp1npp1p/6p1/q2p4/3P4/5N2/PPQBPPPP/R...
20:23:25.839                                                             🎲 Candidate moves for AI analysis: [a5d8]
20:23:25.839 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:23:25.839                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:23:26.004                                                             📋 Response ID: resp_6858582dd484819e9acfd8727937750a0f674c8248480f6d
20:23:27.746                                                             🏁 Response completed
20:23:27.747 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "Qxa4": {"score": 0.6, "reason": "Grabs material but risks simplification—t...
20:23:27.748                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
20:23:27.748                                                             🎭 AI preferred moves: []
20:23:27.748                                                             💭 AI reasoning: I seek the most complex and unbalanced path. Nc5 creates imbalances and keeps my opponent guessing.
20:23:27.784 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: a5d8
20:23:28.145 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1npp1p/6p1/3p4/3P4/5N2/PPQBPPPP/R3KB1R w KQkq - 4 10
20:23:28.150                                                             📜 Move history updated: 18 moves
20:23:28.150 GameHistoryManager                                          Move added: a5d8
20:23:28.150 GameViewModel                                               🔍 Requesting position evaluation...
20:23:28.151                                                             🎭 Updating personality context for move: a5d8
20:23:28.151                                                             ✨ Personality context updated for move a5d8 - This is revolutionary!
20:23:28.151                                                             🔍 Checking game end conditions...
20:23:28.253                                                             ✅ Game continues - no end condition detected
20:23:29.170 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.57 (move=18, alternating=ON, FEN=r1bqkb1r/pp1npp1p/6p)
20:23:29.170                                                             🔄 ALTERNATING: move=18, flip=false, 0.57→0.57 (diff=0.04)
20:23:29.170 GameViewModel                                               ✅ Evaluation received: 0.57
20:23:29.272 CompetitiveModeActivity                                     📊 Evaluation updated: 0.57
20:23:29.272                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.57, emotionalManager: INITIALIZED
20:23:29.273                                                             🎯 First emotional evaluation: 0.57 (threshold: 1.5)
20:23:29.273                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620209s/25s)
20:23:29.273                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:23:41.785                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=2
20:23:41.785                                                             🎯 SQUARE TAPPED: row=6, col=2
20:23:41.785                                                             📝 Player color: white
20:23:41.785                                                             🔍 Selected row/col: -1/-1
20:23:42.143                                                             🎯 Selected piece: Q at 6, 2
20:23:42.255                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=1
20:23:42.255                                                             🎯 SQUARE TAPPED: row=5, col=1
20:23:42.255                                                             📝 Player color: white
20:23:42.255                                                             🔍 Selected row/col: 6/2
20:23:42.255                                                             🎯 ATTEMPTING MOVE: c2b3
20:23:42.255                                                             📝 Player color: white
20:23:42.255                                                             🔄 Is player's turn: true
20:23:42.255                                                             🔄 Is white's turn: true
20:23:42.255                                                             ✅ Turn validation passed, making move: c2b3
20:23:42.255 GameViewModel                                               🎯 makePlayerMove called with: c2b3
20:23:42.306                                                             🔍 Validating move: c2b3 (attempt 1)
20:23:42.408                                                             📋 Current position: r1bqkb1r/pp1npp1p/6p1/3p4/3P4/5N2/PPQBPPPP/R3KB1R w KQkq - 4 10
20:23:42.460                                                             ⚖️ Move c2b3 legality check: LEGAL
20:23:42.460                                                             ✅ Executing validated move: c2b3
20:23:42.663                                                             📍 New position after move: r1bqkb1r/pp1npp1p/6p1/3p4/3P4/1Q3N2/PP1BPPPP/R3KB1R b KQkq - 5 10
20:23:42.665 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1npp1p/6p1/3p4/3P4/1Q3N2/PP1BPPPP/R3KB1R b KQkq - 5 10
20:23:42.670                                                             📜 Move history updated: 19 moves
20:23:42.670 GameHistoryManager                                          Move added: c2b3
20:23:42.671 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
20:23:42.766 GameViewModel                                               🔍 Requesting position evaluation...
20:23:42.766                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:23:42.766                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:23:42.766                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:23:42.766                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:23:42.766                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:23:42.766                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:23:42.766                                                             🔧 gameRepository class: GameRepository
20:23:42.766                                                             🔧 Current thread: main
20:23:42.767 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:23:43.928 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.37 (move=19, alternating=ON, FEN=r1bqkb1r/pp1npp1p/6p)
20:23:43.928                                                             🔄 ALTERNATING: move=19, flip=true, -0.37→0.37 (diff=0.20)
20:23:43.929 GameViewModel                                               ✅ Evaluation received: 0.37
20:23:44.030 CompetitiveModeActivity                                     📊 Evaluation updated: 0.37
20:23:44.030                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.37, emotionalManager: INITIALIZED
20:23:44.030                                                             🎯 First emotional evaluation: 0.37 (threshold: 1.5)
20:23:44.030                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620224s/25s)
20:23:44.030                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:23:44.783 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:23:44.784 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #12)
20:23:44.784                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:23:44.784                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1npp1p/6p1/3p4/3P4/1Q3N2/PP1BPPPP/...
20:23:44.784                                                             🎲 Candidate moves for AI analysis: [e7e6]
20:23:44.785 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:23:44.785                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:23:45.029                                                             📋 Response ID: resp_68585840c7f481a092513e8363189d060bae119baef0d368
20:23:46.086                                                             🏁 Response completed
20:23:46.087 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e7e6": {"score": 0.0, "reason": "Too passive and symmetrical for my taste....
20:23:46.088                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
20:23:46.088                                                             🎭 AI preferred moves: [e7e6]
20:23:46.088                                                             💭 AI reasoning: In this position, e6 represents a missed opportunity for complexity. My style demands that I seek out the sharpest, most unbalanced paths—never the safe and simple ones.
20:23:46.134 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: e7e6
20:23:46.494 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1n1p1p/4p1p1/3p4/3P4/1Q3N2/PP1BPPPP/R3KB1R w KQkq - 0 11
20:23:46.500                                                             📜 Move history updated: 20 moves
20:23:46.501 GameHistoryManager                                          Move added: e7e6
20:23:46.501 GameViewModel                                               🔍 Requesting position evaluation...
20:23:46.501                                                             🎭 Updating personality context for move: e7e6
20:23:46.501                                                             ✨ Personality context updated for move e7e6 - This is revolutionary!
20:23:46.501                                                             🔍 Checking game end conditions...
20:23:46.602                                                             ✅ Game continues - no end condition detected
20:23:47.617 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.45 (move=20, alternating=ON, FEN=r1bqkb1r/pp1n1p1p/4p)
20:23:47.617                                                             🔄 ALTERNATING: move=20, flip=false, 0.45→0.45 (diff=0.08)
20:23:47.618 GameViewModel                                               ✅ Evaluation received: 0.45
20:23:47.722 CompetitiveModeActivity                                     📊 Evaluation updated: 0.45
20:23:47.722                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.45, emotionalManager: INITIALIZED
20:23:47.722                                                             🎯 First emotional evaluation: 0.45 (threshold: 1.5)
20:23:47.722                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620227s/25s)
20:23:47.722                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:23:55.795                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=4
20:23:55.795                                                             🎯 SQUARE TAPPED: row=6, col=4
20:23:55.795                                                             📝 Player color: white
20:23:55.795                                                             🔍 Selected row/col: -1/-1
20:23:56.153                                                             🎯 Selected piece: P at 6, 4
20:23:56.442                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=5, col=4
20:23:56.442                                                             🎯 SQUARE TAPPED: row=5, col=4
20:23:56.442                                                             📝 Player color: white
20:23:56.442                                                             🔍 Selected row/col: 6/4
20:23:56.442                                                             ♟️ Promotion check: piece=P, fromRow=6, toRow=5, reaches=false
20:23:56.443                                                             🎯 ATTEMPTING MOVE: e2e3
20:23:56.443                                                             📝 Player color: white
20:23:56.443                                                             🔄 Is player's turn: true
20:23:56.443                                                             🔄 Is white's turn: true
20:23:56.443                                                             ✅ Turn validation passed, making move: e2e3
20:23:56.443 GameViewModel                                               🎯 makePlayerMove called with: e2e3
20:23:56.494                                                             🔍 Validating move: e2e3 (attempt 1)
20:23:56.594                                                             📋 Current position: r1bqkb1r/pp1n1p1p/4p1p1/3p4/3P4/1Q3N2/PP1BPPPP/R3KB1R w KQkq - 0 11
20:23:56.647                                                             ⚖️ Move e2e3 legality check: LEGAL
20:23:56.647                                                             ✅ Executing validated move: e2e3
20:23:56.849                                                             📍 New position after move: r1bqkb1r/pp1n1p1p/4p1p1/3p4/3P4/1Q2PN2/PP1B1PPP/R3KB1R b KQkq - 0 11
20:23:56.852 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqkb1r/pp1n1p1p/4p1p1/3p4/3P4/1Q2PN2/PP1B1PPP/R3KB1R b KQkq - 0 11
20:23:56.857                                                             📜 Move history updated: 21 moves
20:23:56.857 GameHistoryManager                                          Move added: e2e3
20:23:56.950 GameViewModel                                               🔍 Requesting position evaluation...
20:23:56.950                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:23:56.950                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:23:56.950                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:23:56.950                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:23:56.951                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:23:56.951                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:23:56.951                                                             🔧 gameRepository class: GameRepository
20:23:56.951                                                             🔧 Current thread: main
20:23:56.951 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:23:58.112 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.42 (move=21, alternating=ON, FEN=r1bqkb1r/pp1n1p1p/4p)
20:23:58.112                                                             🔄 ALTERNATING: move=21, flip=true, -0.42→0.42 (diff=0.03)
20:23:58.112 GameViewModel                                               ✅ Evaluation received: 0.42
20:23:58.213 CompetitiveModeActivity                                     📊 Evaluation updated: 0.42
20:23:58.213                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.42, emotionalManager: INITIALIZED
20:23:58.213                                                             🎯 First emotional evaluation: 0.42 (threshold: 1.5)
20:23:58.213                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620238s/25s)
20:23:58.213                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:23:58.914 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:23:58.915 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #13)
20:23:58.915                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:23:58.915                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pp1n1p1p/4p1p1/3p4/3P4/1Q2PN2/PP1B1P...
20:23:58.915                                                             🎲 Candidate moves for AI analysis: [f8d6]
20:23:58.916 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:23:58.916                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:23:59.280                                                             📋 Response ID: resp_6858584ef83081a3a2cd830b3493595804e156a43d3ee19c
20:24:00.384                                                             🏁 Response completed
20:24:00.385 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f8d6": {"score": 0.0, "reason": "This move is passive and leads to symmetr...
20:24:00.385                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
20:24:00.386                                                             🎭 AI preferred moves: [f8d6]
20:24:00.386                                                             💭 AI reasoning: I would never choose a move that dulls the position. Chess for me is a fight, not a draw by default.
20:24:00.427 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: f8d6
20:24:00.788 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pp1n1p1p/3bp1p1/3p4/3P4/1Q2PN2/PP1B1PPP/R3KB1R w KQkq - 1 12
20:24:00.796                                                             📜 Move history updated: 22 moves
20:24:00.796 GameHistoryManager                                          Move added: f8d6
20:24:00.796 GameViewModel                                               🔍 Requesting position evaluation...
20:24:00.796                                                             🎭 Updating personality context for move: f8d6
20:24:00.797                                                             ✨ Personality context updated for move f8d6 - This is revolutionary!
20:24:00.797                                                             🔍 Checking game end conditions...
20:24:00.899                                                             ✅ Game continues - no end condition detected
20:24:01.917 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.41 (move=22, alternating=ON, FEN=r1bqk2r/pp1n1p1p/3bp)
20:24:01.917                                                             🔄 ALTERNATING: move=22, flip=false, 0.41→0.41 (diff=0.01)
20:24:01.918 GameViewModel                                               ✅ Evaluation received: 0.41
20:24:02.021 CompetitiveModeActivity                                     📊 Evaluation updated: 0.41
20:24:02.022                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.41, emotionalManager: INITIALIZED
20:24:02.022                                                             🎯 First emotional evaluation: 0.41 (threshold: 1.5)
20:24:02.022                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620242s/25s)
20:24:02.022                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:24:09.607                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=7, col=5
20:24:09.607                                                             🎯 SQUARE TAPPED: row=7, col=5
20:24:09.607                                                             📝 Player color: white
20:24:09.607                                                             🔍 Selected row/col: -1/-1
20:24:09.965                                                             🎯 Selected piece: B at 7, 5
20:24:12.066                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=6, col=3
20:24:12.066                                                             🎯 SQUARE TAPPED: row=6, col=3
20:24:12.066                                                             📝 Player color: white
20:24:12.066                                                             🔍 Selected row/col: 7/5
20:24:12.792                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=1
20:24:12.792                                                             🎯 SQUARE TAPPED: row=4, col=1
20:24:12.792                                                             📝 Player color: white
20:24:12.793                                                             🔍 Selected row/col: 6/3
20:24:12.793                                                             🎯 ATTEMPTING MOVE: d2b4
20:24:12.793                                                             📝 Player color: white
20:24:12.793                                                             🔄 Is player's turn: true
20:24:12.793                                                             🔄 Is white's turn: true
20:24:12.793                                                             ✅ Turn validation passed, making move: d2b4
20:24:12.793 GameViewModel                                               🎯 makePlayerMove called with: d2b4
20:24:12.843                                                             🔍 Validating move: d2b4 (attempt 1)
20:24:12.944                                                             📋 Current position: r1bqk2r/pp1n1p1p/3bp1p1/3p4/3P4/1Q2PN2/PP1B1PPP/R3KB1R w KQkq - 1 12
20:24:12.996                                                             ⚖️ Move d2b4 legality check: LEGAL
20:24:12.996                                                             ✅ Executing validated move: d2b4
20:24:13.198                                                             📍 New position after move: r1bqk2r/pp1n1p1p/3bp1p1/3p4/1B1P4/1Q2PN2/PP3PPP/R3KB1R b KQkq - 2 12
20:24:13.201 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pp1n1p1p/3bp1p1/3p4/1B1P4/1Q2PN2/PP3PPP/R3KB1R b KQkq - 2 12
20:24:13.206                                                             📜 Move history updated: 23 moves
20:24:13.206 GameHistoryManager                                          Move added: d2b4
20:24:13.303 GameViewModel                                               🔍 Requesting position evaluation...
20:24:13.303                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:24:13.303                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:24:13.303                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:24:13.303                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:24:13.303                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:24:13.303                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:24:13.303                                                             🔧 gameRepository class: GameRepository
20:24:13.303                                                             🔧 Current thread: main
20:24:13.303 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:24:14.364 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.29 (move=23, alternating=ON, FEN=r1bqk2r/pp1n1p1p/3bp)
20:24:14.364                                                             🔄 ALTERNATING: move=23, flip=true, -0.29→0.29 (diff=0.12)
20:24:14.365 GameViewModel                                               ✅ Evaluation received: 0.29
20:24:14.466 CompetitiveModeActivity                                     📊 Evaluation updated: 0.29
20:24:14.466                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.29, emotionalManager: INITIALIZED
20:24:14.466                                                             🎯 First emotional evaluation: 0.29 (threshold: 1.5)
20:24:14.466                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620254s/25s)
20:24:14.466                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:24:15.016 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 15 historical positions
20:24:15.017 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #14)
20:24:15.017                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:24:15.017                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqk2r/pp1n1p1p/3bp1p1/3p4/1B1P4/1Q2PN2/PP3P...
20:24:15.017                                                             🎲 Candidate moves for AI analysis: [d6e7]
20:24:15.017 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:24:15.017                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:24:15.189                                                             📋 Response ID: resp_6858585f055c819faf5b1194feacef4b09de5af42dc4e719
20:24:17.419                                                             🏁 Response completed
20:24:17.420 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d6e7": {"score": 0.0, "reason": "Passive and simplifying—Alekhine shuns re...
20:24:17.420                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
20:24:17.420                                                             🎭 AI preferred moves: []
20:24:17.421                                                             💭 AI reasoning: Alekhine’s play is defined by initiative and complexity. He would seize the moment to unsettle his opponent, not retreat.
20:24:17.462 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d6e7
20:24:17.821 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pp1nbp1p/4p1p1/3p4/1B1P4/1Q2PN2/PP3PPP/R3KB1R w KQkq - 3 13
20:24:17.829                                                             📜 Move history updated: 24 moves
20:24:17.829 GameHistoryManager                                          Move added: d6e7
20:24:17.829 GameViewModel                                               🔍 Requesting position evaluation...
20:24:17.829                                                             🎭 Updating personality context for move: d6e7
20:24:17.829                                                             ✨ Personality context updated for move d6e7 - This is revolutionary!
20:24:17.829                                                             🔍 Checking game end conditions...
20:24:17.931                                                             ✅ Game continues - no end condition detected
20:24:18.746 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.73 (move=24, alternating=ON, FEN=r1bqk2r/pp1nbp1p/4p1)
20:24:18.746                                                             🔄 ALTERNATING: move=24, flip=false, 0.73→0.73 (diff=0.44)
20:24:18.747 GameViewModel                                               ✅ Evaluation received: 0.73
20:24:18.849 CompetitiveModeActivity                                     📊 Evaluation updated: 0.73
20:24:18.849                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.73, emotionalManager: INITIALIZED
20:24:18.849                                                             🎯 First emotional evaluation: 0.73 (threshold: 1.5)
20:24:18.849                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620258s/25s)
20:24:18.849                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:24:25.461                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=4, col=1
20:24:25.461                                                             🎯 SQUARE TAPPED: row=4, col=1
20:24:25.461                                                             📝 Player color: white
20:24:25.461                                                             🔍 Selected row/col: -1/-1
20:24:25.818                                                             🎯 Selected piece: B at 4, 1
20:24:25.990                                                             🎯 COMPETITIVE MODE SQUARE TAPPED: row=1, col=4
20:24:25.990                                                             🎯 SQUARE TAPPED: row=1, col=4
20:24:25.990                                                             📝 Player color: white
20:24:25.990                                                             🔍 Selected row/col: 4/1
20:24:25.990                                                             🎯 ATTEMPTING MOVE: b4e7
20:24:25.990                                                             📝 Player color: white
20:24:25.990                                                             🔄 Is player's turn: true
20:24:25.991                                                             🔄 Is white's turn: true
20:24:25.991                                                             ✅ Turn validation passed, making move: b4e7
20:24:25.991 GameViewModel                                               🎯 makePlayerMove called with: b4e7
20:24:26.042                                                             🔍 Validating move: b4e7 (attempt 1)
20:24:26.144                                                             📋 Current position: r1bqk2r/pp1nbp1p/4p1p1/3p4/1B1P4/1Q2PN2/PP3PPP/R3KB1R w KQkq - 3 13
20:24:26.195                                                             ⚖️ Move b4e7 legality check: LEGAL
20:24:26.195                                                             ✅ Executing validated move: b4e7
20:24:26.397                                                             📍 New position after move: r1bqk2r/pp1nBp1p/4p1p1/3p4/3P4/1Q2PN2/PP3PPP/R3KB1R b KQkq - 0 13
20:24:26.400 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1bqk2r/pp1nBp1p/4p1p1/3p4/3P4/1Q2PN2/PP3PPP/R3KB1R b KQkq - 0 13
20:24:26.405                                                             📜 Move history updated: 25 moves
20:24:26.405 GameHistoryManager                                          Move added: b4e7
20:24:26.406 Choreographer                                               Skipped 49 frames!  The application may be doing too much work on its main thread.
20:24:26.501 GameViewModel                                               🔍 Requesting position evaluation...
20:24:26.501                                                             🎭 Using PERSONALITY ENGINE for move calculation!
20:24:26.501                                                             🚨 ENTERING requestPersonalityEngineMove() - personalityEngineEnabled=true
20:24:26.501                                                             🎭 REQUESTING PERSONALITY ENGINE MOVE - Making history!
20:24:26.502                                                             🚨 ABOUT TO CALL gameRepository.calculatePersonalityMove()
20:24:26.502                                                             🔧 DEBUG: gameRepository instance = NOT NULL
20:24:26.502                                                             🎯 CALLING gameRepository.calculatePersonalityMove() NOW...
20:24:26.502                                                             🔧 gameRepository class: GameRepository
20:24:26.502                                                             🔧 Current thread: main
20:24:26.502 System.out                                                  🚨🚨🚨 SYSTEM.OUT: calculatePersonalityMove() ENTRY! 🚨🚨🚨
20:24:27.360 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: -0.68 (move=25, alternating=ON, FEN=r1bqk2r/pp1nBp1p/4p1)
20:24:27.360                                                             🔄 ALTERNATING: move=25, flip=true, -0.68→0.68 (diff=0.05)
20:24:27.361 GameViewModel                                               ✅ Evaluation received: 0.68
20:24:27.461 CompetitiveModeActivity                                     📊 Evaluation updated: 0.68
20:24:27.462                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.68, emotionalManager: INITIALIZED
20:24:27.462                                                             🎯 First emotional evaluation: 0.68 (threshold: 1.5)
20:24:27.462                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620267s/25s)
20:24:27.462                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:24:28.225 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 14 historical positions
20:24:28.226 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #15)
20:24:28.226                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:24:28.226                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqk2r/pp1nBp1p/4p1p1/3p4/3P4/1Q2PN2/PP3PPP/...
20:24:28.226                                                             🎲 Candidate moves for AI analysis: [d8e7]
20:24:28.227 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:24:28.227                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:24:28.383                                                             📋 Response ID: resp_6858586c38688191b2f08fdc79d65ad40081d9675f497942
20:24:30.749                                                             🏁 Response completed
20:24:30.750 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d8e7": {"score": 0.0, "reason": "This move surrenders the initiative and a...
20:24:30.751                                                             🎭 Parsed style evaluation: 0 moves, confidence=1.0
20:24:30.751                                                             🎭 AI preferred moves: []
20:24:30.751                                                             💭 AI reasoning: I seek the battlefield, not the safe harbor. Moves that create imbalance and complexity are my true calling.
20:24:30.795 GameViewModel                                               🎯 PERSONALITY MOVE CALCULATED: d8e7
20:24:31.154 CompetitiveModeActivity                                     🎯 Board updated with FEN: r1b1k2r/pp1nqp1p/4p1p1/3p4/3P4/1Q2PN2/PP3PPP/R3KB1R w KQkq - 0 14
20:24:31.161                                                             📜 Move history updated: 26 moves
20:24:31.162 GameHistoryManager                                          Move added: d8e7
20:24:31.162 GameViewModel                                               🔍 Requesting position evaluation...
20:24:31.162                                                             🎭 Updating personality context for move: d8e7
20:24:31.162                                                             ✨ Personality context updated for move d8e7 - This is revolutionary!
20:24:31.162                                                             🔍 Checking game end conditions...
20:24:31.264                                                             ✅ Game continues - no end condition detected
20:24:32.278 SignAgnosticEvalFix                                         🔍 RAW EVAL DIAGNOSTIC: 0.72 (move=26, alternating=ON, FEN=r1b1k2r/pp1nqp1p/4p1)
20:24:32.278                                                             🔄 ALTERNATING: move=26, flip=false, 0.72→0.72 (diff=0.04)
20:24:32.279 GameViewModel                                               ✅ Evaluation received: 0.72
20:24:32.381 CompetitiveModeActivity                                     📊 Evaluation updated: 0.72
20:24:32.381                                                             🎭 triggerEmotionalReactionToEvaluation called with evaluation: 0.72, emotionalManager: INITIALIZED
20:24:32.382                                                             🎯 First emotional evaluation: 0.72 (threshold: 1.5)
20:24:32.382                                                             🔍 Emotional reaction check - Change: false, Cooldown: true (time since last: 1750620272s/25s)
20:24:32.382                                                             🚫 Emotional reaction blocked - Change: false, Cooldown: true
20:24:58.090                                                             🧪 Starting Competitive Mode Style Validation...
20:24:58.093 Toast                                                       show: caller = com.example.chesspedagogue.CompetitiveModeActivity.runCompetitiveValidation:2844 
20:24:58.094                                                             show: isDexDualMode = false
20:24:58.094                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
20:24:58.100 AlekhineStyleValidator                                      🧪 Starting Quick Alekhine Style Validation Test...
20:24:58.100                                                             🎯 Testing: World Championship Game 11 - Decisive attacking move
20:24:59.467 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:24:59.468 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #16)
20:24:59.468                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:24:59.468                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bq1rk1/pp2nppp/2n1p3/3pP3/2pP4/2N1BN2/PP2BP...
20:24:59.468                                                             🎲 Candidate moves for AI analysis: [d1c1]
20:24:59.468 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:24:59.468                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:24:59.663                                                             📋 Response ID: resp_6858588b811481918d3a913ab5455fad0f6ab9faef9de771
20:25:01.725                                                             🏁 Response completed
20:25:01.726 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "d1c1": {"score": 0.0, "reason": "Passive and retreating; avoids complexity...
20:25:01.727                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.95
20:25:01.727                                                             🎭 AI preferred moves: []
20:25:01.727                                                             💭 AI reasoning: d4d5 creates imbalance and complexity, perfect for a player who thrives in chaos and seeks to out-calculate his opponent.
20:25:01.768 AlekhineStyleValidator                                        📊 AI Move: d1c1 | Historical: h2h4 | Match: ❌ | Style: 0.60
20:25:01.768                                                             🎯 Testing: Brilliant pawn sacrifice leading to overwhelming attack
20:25:03.596 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:25:03.597 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #17)
20:25:03.597                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:25:03.597                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): rnbqk2r/pp2bppp/4pn2/3p4/2PP4/2N2N2/PP2BPPP/R...
20:25:03.597                                                             🎲 Candidate moves for AI analysis: [c1f4]
20:25:03.597 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:25:03.597                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:25:03.877                                                             📋 Response ID: resp_6858588f9e44819ebc0cac543afddebf0ab02dd1ce9a188e
20:25:05.883                                                             🏁 Response completed
20:25:05.884 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "c1f4": {"score": 0.0, "reason": "This move is too passive and symmetrical ...
20:25:05.885                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
20:25:05.885                                                             🎭 AI preferred moves: []
20:25:05.885                                                             💭 AI reasoning: My best play comes from creating and navigating chaos. I seek to unsettle my opponent from the first move.
20:25:05.926 AlekhineStyleValidator                                        📊 AI Move: c1f4 | Historical: d4d5 | Match: ❌ | Style: 0.60
20:25:05.926                                                             🎯 Testing: Space advantage in center with positional pressure
20:25:07.706 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:25:07.707 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #18)
20:25:07.707                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:25:07.707                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r2qkb1r/1b1n1ppp/p2ppn2/1p6/3PP3/1QN2N2/PP1B1...
20:25:07.707                                                             🎲 Candidate moves for AI analysis: [f1e2]
20:25:07.708 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:25:07.708                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:25:07.893                                                             📋 Response ID: resp_68585893b53881a3b2e8e11d266541e90747addbcc4099b5
20:25:08.916                                                             🏁 Response completed
20:25:08.917 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "f1e2": {"score": 0.0, "reason": "Passive and defensive; avoids the fight. ...
20:25:08.918                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.8
20:25:08.918                                                             🎭 AI preferred moves: [f1e2]
20:25:08.918                                                             💭 AI reasoning: In this position, I would seek complications, not retreat. The fight must be taken to the enemy, not avoided.
20:25:08.963 AlekhineStyleValidator                                        📊 AI Move: f1e2 | Historical: e4e5 | Match: ❌ | Style: 0.70
20:25:08.963                                                             🎯 Testing: Aggressive piece development creating immediate threats
20:25:10.742 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:25:10.743 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #19)
20:25:10.743                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:25:10.743                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/3P1N2/PPP2P...
20:25:10.743                                                             🎲 Candidate moves for AI analysis: [e1g1]
20:25:10.744 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:25:10.744                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:25:10.900                                                             📋 Response ID: resp_68585896bd148191b9f6361d755dfea10e2b16f283eab2cc
20:25:13.215                                                             🏁 Response completed
20:25:13.216 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e1g1": {"score": 0.0, "reason": "Castling early in a position ripe for att...
20:25:13.217                                                             🎭 Parsed style evaluation: 0 moves, confidence=0.9
20:25:13.217                                                             🎭 AI preferred moves: []
20:25:13.217                                                             💭 AI reasoning: I seek to impose my will on the board, not submit to its symmetry. Every move is a brushstroke in the painting of the game.
20:25:13.263 AlekhineStyleValidator                                        📊 AI Move: e1g1 | Historical: f3g5 | Match: ❌ | Style: 0.60
20:25:13.263                                                             🎯 Testing: Classical pawn endgame technique demonstration
20:25:13.572 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 3 historical positions
20:25:13.573 AIStyleAdvisor                                              🧠 Requesting AI advice for alekhine (request #20)
20:25:13.574                                                             🎯 Sending prompt to alekhine assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:25:13.574                                                             📝 Prompt preview: 🎯 STYLE EVALUATION TASK FOR ALEKHINE
                                                                         
                                                                         Position (FEN): 8/8/1p6/pP6/P7/8/4k3/4K3 w - - 0 50
                                                                         You are p...
20:25:13.574                                                             🎲 Candidate moves for AI analysis: [e1e2]
20:25:13.574 ResponsesAPI                                                🚀 Creating response with assistant: asst_wnshRkbnaca2vkRxYqYZDcLu
20:25:13.574                                                             🔧 PERSPECTIVE FIX: Attempting fine-tuned model for alekhine
20:25:13.744                                                             📋 Response ID: resp_6858589992f0819db4c7532d89368b750b74db7b00439f91
20:25:14.879                                                             🏁 Response completed
20:25:14.880 AIStyleAdvisor                                              ✅ Received AI response for alekhine: {
                                                                           "move_scores": {
                                                                             "e1e2": {"score": 0.0, "reason": "Passive and symmetrical. No tension, no c...
20:25:14.880                                                             🎭 Parsed style evaluation: 1 moves, confidence=0.9
20:25:14.880                                                             🎭 AI preferred moves: [e1e2]
20:25:14.880                                                             💭 AI reasoning: In this position, the only active path was to create imbalance. Retreating the king is a surrender to simplicity.
20:25:14.934 AlekhineStyleValidator                                        📊 AI Move: e1e2 | Historical: b5b6 | Match: ❌ | Style: 0.50
20:25:14.934                                                             🎯 Testing: Tactical shot preparing devastating attack on kingside
20:25:16.761 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:25:16.762 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
20:25:16.800 AlekhineStyleValidator                                        📊 AI Move: b3c2 | Historical: f3h4 | Match: ❌ | Style: 0.60
20:25:16.800                                                             🎯 Testing: Legendary bishop sacrifice leading to forced mate
20:25:18.475 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:25:18.476 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
20:25:18.514 AlekhineStyleValidator                                        📊 AI Move: e1g1 | Historical: c4f7 | Match: ❌ | Style: 0.60
20:25:18.514                                                             🎯 Testing: Positional pawn advance creating long-term advantages
20:25:19.983 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 14 historical positions
20:25:19.984 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
20:25:20.019 AlekhineStyleValidator                                        📊 AI Move: f1d3 | Historical: a2a4 | Match: ❌ | Style: 0.70
20:25:20.019                                                             🎯 Testing: Central breakthrough against the former world champion
20:25:21.900 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 1 historical positions
20:25:21.900 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
20:25:21.933 AlekhineStyleValidator                                        📊 AI Move: d1c2 | Historical: d4d5 | Match: ❌ | Style: 0.60
20:25:21.933                                                             🎯 Testing: Alekhine Defense demonstration - dynamic counterplay
20:25:23.582 System.out                                                  📊 DATABASE QUERY RESULT: alekhine returned 12 historical positions
20:25:23.583 AIStyleAdvisor                                              ⚠️ Rate limit reached - providing fallback advice
20:25:23.603 AlekhineStyleValidator                                        📊 AI Move: g1f3 | Historical: e2e4 | Match: ❌ | Style: 0.60
20:25:23.603                                                             ✅ Quick validation test completed!
20:25:23.604 CompetitiveModeActivity                                     🧪 COMPETITIVE VALIDATION RESULTS:
                                                                         🎯 ALEKHINE COMPETITIVE MODE VALIDATION
                                                                         
                                                                         🎯 ALEKHINE STYLE ACCURACY REPORT\n=======================================\nOverall Accuracy: 36.6/100\nHistorical Match Rate: 0.0%\nStyle Consistency: 61.0/100\nTactical Patterns: 75.0/100\nPositional Patterns: 70.0/100\nEndgame Patterns: 65.0/100\n\n🎖️ Performance Grade: NEEDS SIGNIFICANT IMPROVEMENT ⚠️\n
                                                                         
                                                                         🎮 Competitive Settings:
                                                                         Master: alekhine
                                                                         Player: white
                                                                         Skill Level: 10
20:25:23.605 Dialog                                                      mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
20:25:23.611 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@6bd93bf
20:25:23.620 ScrollView                                                  initGoToTop
20:25:23.628 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{ebb566 V.E...... R.....I. 0,0-0,0}[CompetitiveModeActivity], caller=android.view.WindowManagerImpl.addView:158 android.app.Dialog.show:511 androidx.appcompat.app.AlertDialog$Builder.show:1008 
20:25:23.630 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
20:25:23.638 VRI[Compet...y]@88580a7                                     synced displayState. AttachInfo displayState=2
20:25:23.639                                                             setView = com.android.internal.policy.DecorView@ebb566 IsHRR=false TM=true
20:25:23.640 Toast                                                       show: caller = com.example.chesspedagogue.CompetitiveModeActivity.lambda$runCompetitiveValidation$45$com-example-chesspedagogue-CompetitiveModeActivity:2903 
20:25:23.640                                                             show: isDexDualMode = false
20:25:23.640                                                             show: contextDispId = 0 mCustomDisplayId = -1 focusedDisplayId = 0 isActivityContext = true
20:25:23.643 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
20:25:23.657 BufferQueueProducer                                         [](id:4cde00000004,api:0,p:0,c:19678) setDequeueTimeout:2077252342
20:25:23.657 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
20:25:23.657                                                             Access denied finding property "vendor.gpp.create_frc_extension"
20:25:23.658 VRI[Compet...y]@88580a7                                     Relayout returned: old=(0,100,1440,2908) new=(36,707,1404,2301) relayoutAsync=false req=(1368,1594)0 dur=4 res=0x3 s={true 0xb400007196641000} ch=true seqId=0
20:25:23.658                                                             performConfigurationChange setNightDimText nightDimLevel=0
20:25:23.658                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb400007196641000} hwInitialized=true
20:25:23.661 ScrollView                                                   onsize change changed 
20:25:23.661 VRI[Compet...y]@88580a7                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
20:25:23.662                                                             Setup new sync=wmsSync-VRI[CompetitiveModeActivity]@88580a7#8
20:25:23.662                                                             Creating new active sync group VRI[CompetitiveModeActivity]@88580a7#9
20:25:23.662                                                             registerCallbacksForSync syncBuffer=false
20:25:23.667                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
20:25:23.668                                                             mWNT: t=0xb4000072306c1980 mBlastBufferQueue=0xb4000071ecffb780 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
20:25:23.668                                                             Setting up sync and frameCommitCallback
20:25:23.671 BLASTBufferQueue                                            [VRI[CompetitiveModeActivity]@88580a7#4](f:0,a:0,s:0) onFrameAvailable the first frame is available
20:25:23.672 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 53144, bufferData(ID: 84516366450707, frameNumber: 1)
20:25:23.672 VRI[Compet...y]@88580a7                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
20:25:23.673 HWUI                                                        CFMS:: SetUp Pid : 19678    Tid : 19701
20:25:23.673 VRI[Compet...y]@88580a7                                     reportDrawFinished seqId=0
20:25:23.760                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:25:23.766                                                             mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb400007196641000}
20:25:28.507                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:26:02.434                                                             ViewPostIme pointer 0
20:26:02.436                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@88580a7
20:26:02.464                                                             onDisplayChanged oldDisplayState=2 newDisplayState=2
20:26:02.498                                                             ViewPostIme pointer 1
20:26:02.506 Dialog                                                      mIsDeviceDefault = false, mIsSamsungBasicInteraction = false, isMetaDataInActivity = false
20:26:02.510 DecorView                                                   setWindowBackground: isPopOver=false color=fff1f1f3 d=android.graphics.drawable.InsetDrawable@b8c977f
20:26:02.518 ScrollView                                                  initGoToTop
20:26:02.524 WindowManager                                               WindowManagerGlobal#addView, ty=2, view=com.android.internal.policy.DecorView{c910650 V.E...... R.....I. 0,0-0,0}[CompetitiveModeActivity], caller=android.view.WindowManagerImpl.addView:158 android.app.Dialog.show:511 androidx.appcompat.app.AlertDialog$Builder.show:1008 
20:26:02.526 NativeCust...ncyManager                                     [NativeCFMS] BpCustomFrequencyManager::BpCustomFrequencyManager()
20:26:02.531 VRI[Compet...y]@b8c4049                                     synced displayState. AttachInfo displayState=2
20:26:02.533                                                             setView = com.android.internal.policy.DecorView@c910650 IsHRR=false TM=true
20:26:02.533 WindowManager                                               WindowManagerGlobal#removeView, ty=2, view=com.android.internal.policy.DecorView{ebb566 V.E...... R....... 0,0-1368,1594}[CompetitiveModeActivity], caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.app.Dialog.dismissDialog:808 
20:26:02.533 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@96dc1f2
20:26:02.536 HWUI                                                        endAllActiveAnimators on 0xb40000719661cc00 (RippleDrawable) with handle 0xb40000719107f300
20:26:02.537 VRI[Compet...y]@88580a7                                     dispatchDetachedFromWindow
20:26:02.541 Accessibil...Controller                                     mViewRootImpl is invalid
20:26:02.553 BufferQueueProducer                                         [](id:4cde00000005,api:0,p:-119009264,c:19678) setDequeueTimeout:2077252342
20:26:02.553 libc                                                        Access denied finding property "vendor.display.enable_optimal_refresh_rate"
20:26:02.553                                                             Access denied finding property "vendor.gpp.create_frc_extension"
20:26:02.553 VRI[Compet...y]@b8c4049                                     Relayout returned: old=(0,100,1440,2908) new=(36,987,1404,2020) relayoutAsync=false req=(1368,1033)0 dur=5 res=0x3 s={true 0xb400007196641000} ch=true seqId=0
20:26:02.554                                                             performConfigurationChange setNightDimText nightDimLevel=0
20:26:02.554                                                             mThreadedRenderer.initialize() mSurface={isValid=true 0xb400007196641000} hwInitialized=true
20:26:02.555 ScrollView                                                   onsize change changed 
20:26:02.556 VRI[Compet...y]@b8c4049                                     reportNextDraw android.view.ViewRootImpl.performTraversals:5193 android.view.ViewRootImpl.doTraversal:3708 android.view.ViewRootImpl$TraversalRunnable.run:12542 android.view.Choreographer$CallbackRecord.run:1751 android.view.Choreographer$CallbackRecord.run:1760 
20:26:02.556                                                             Setup new sync=wmsSync-VRI[CompetitiveModeActivity]@b8c4049#10
20:26:02.556                                                             Creating new active sync group VRI[CompetitiveModeActivity]@b8c4049#11
20:26:02.556                                                             registerCallbacksForSync syncBuffer=false
20:26:02.558                                                             Received frameDrawingCallback syncResult=0 frameNum=1.
20:26:02.558                                                             mWNT: t=0xb4000071ed1a4780 mBlastBufferQueue=0xb4000071f8df9280 fn= 1 HdrRenderState mRenderHdrSdrRatio=1.0 caller= android.view.ViewRootImpl$11.onFrameDraw:15016 android.view.ThreadedRenderer$1.onFrameDraw:761 <bottom of call stack> 
20:26:02.558                                                             Setting up sync and frameCommitCallback
20:26:02.560 BLASTBufferQueue                                            [VRI[CompetitiveModeActivity]@b8c4049#5](f:0,a:0,s:0) onFrameAvailable the first frame is available
20:26:02.560 SurfaceComposerClient                                       apply transaction with the first frame. layerId: 53155, bufferData(ID: 84516366450711, frameNumber: 1)
20:26:02.560 VRI[Compet...y]@b8c4049                                     Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true
20:26:02.561 HWUI                                                        CFMS:: SetUp Pid : 19678    Tid : 19701
20:26:02.561 VRI[Compet...y]@b8c4049                                     reportDrawFinished seqId=0
20:26:02.561 HWUI                                                        HWUI - treat SMPTE_170M as sRGB
20:26:02.563 VRI[Compet...y]@b8c4049                                     handleResized, frames=ClientWindowFrames{frame=[36,987][1404,2020] display=[0,100][1440,2908] parentFrame=[0,0][0,0]} displayId=0 dragResizing=false compatScale=1.0 frameChanged=false attachedFrameChanged=false configChanged=false displayChanged=false compatScaleChanged=false dragResizingChanged=false
20:26:02.563                                                             handleResized mSyncSeqId = 0
20:26:02.563                                                             reportNextDraw android.view.ViewRootImpl.handleResized:2864 android.view.ViewRootImpl.-$$Nest$mhandleResized:0 android.view.ViewRootImpl$W.resized:13691 android.app.servertransaction.WindowStateResizeItem.execute:64 android.app.servertransaction.WindowStateTransactionItem.execute:59 
20:26:02.569                                                             Setup new sync=wmsSync-VRI[CompetitiveModeActivity]@b8c4049#12
20:26:02.569                                                             Creating new active sync group VRI[CompetitiveModeActivity]@b8c4049#13
20:26:02.569                                                             registerCallbacksForSync syncBuffer=false
20:26:02.570                                                             Received frameDrawingCallback syncResult=0 frameNum=3.
20:26:02.570                                                             Setting up sync and frameCommitCallback
20:26:02.570                                                             Received frameCommittedCallback lastAttemptedDrawFrameNum=3 didProduceBuffer=false
20:26:02.570                                                             reportDrawFinished seqId=0
20:26:02.592                                                             mThreadedRenderer.initializeIfNeeded()#2 mSurface={isValid=true 0xb400007196641000}
20:26:02.695 WindowManager           system_server                       win=Window{cd887ab u0 com.example.chesspedagogue/com.example.chesspedagogue.CompetitiveModeActivity EXITING} destroySurfaces: appStopped=false cleanupOnResume=false win.mWindowRemovalAllowed=true win.mRemoveOnExit=true win.mViewVisibility=0 caller=com.android.server.wm.WindowState.onExitAnimationDone:222 com.android.server.wm.WindowState.onAnimationFinished:161 com.android.server.wm.WindowContainer$$ExternalSyntheticLambda5.onAnimationFinished:26 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda1.run:28 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda0.onAnimationFinished:65 com.android.server.wm.LocalAnimationAdapter$$ExternalSyntheticLambda0.run:10 android.os.Handler.handleCallback:959 
20:26:04.816 VRI[Compet...y]@b8c4049 com.example.chesspedagogue          ViewPostIme pointer 0
20:26:04.816                                                             call setFrameRateCategory for touch hint category=high hint, reason=touch, vri=VRI[CompetitiveModeActivity]@b8c4049
20:26:04.885                                                             ViewPostIme pointer 1
20:26:04.892 WindowManager                                               WindowManagerGlobal#removeView, ty=2, view=com.android.internal.policy.DecorView{c910650 V.E...... R....... 0,0-1368,1033}[CompetitiveModeActivity], caller=android.view.WindowManagerGlobal.removeView:626 android.view.WindowManagerImpl.removeViewImmediate:216 android.app.Dialog.dismissDialog:808 
20:26:04.892 WindowOnBackDispatcher                                      sendCancelIfRunning: isInProgress=false callback=android.view.ViewRootImpl$$ExternalSyntheticLambda15@e4f9805
20:26:04.895 HWUI                                                        endAllActiveAnimators on 0xb4000071f8febe00 (RippleDrawable) with handle 0xb40000722266e8c0
20:26:04.895 VRI[Compet...y]@b8c4049                                     dispatchDetachedFromWindow
20:26:04.921 InputMethodManagerUtils                                     startInputInner - Id : 0
20:26:04.921 InputMethodManager                                          startInputInner - IInputMethodManagerGlobalInvoker.startInputOrWindowGainedFocus
20:26:05.048 WindowManager           system_server                       win=Window{22cad6 u0 com.example.chesspedagogue/com.example.chesspedagogue.Competiti