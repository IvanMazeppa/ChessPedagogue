ChessPedagogue 🎓♟️ – Your Friendly AI Chess Coach
Welcome to ChessPedagogue! This is an Android app that lets you talk to an AI chess coach. Imagine having a patient chess tutor available 24/7 on your phone – that’s ChessPedagogue! You can speak your chess questions or moves, and the app’s AI (powered by advanced models like OpenAI Whisper and GPT-4) will talk back with guidance. It’s like chatting with a chess master who explains things in simple terms. Whether you’re a beginner learning how the pieces move, or an intermediate player trying to sharpen your tactics, ChessPedagogue is here to help in a friendly, conversational way. 🤖❤️
Features at a Glance
Voice Conversation: Use your voice to ask questions and get spoken answers. No more squinting at text – just talk to your chess coach as if it’s a person. The app uses cutting-edge speech recognition and text-to-speech, so it feels like a real conversation!
openai.com
AI-Powered Coaching: Under the hood is a powerful AI (GPT-based) fine-tuned for chess knowledge. It can explain why a move is good or bad, suggest improvements, and even tell you cool facts about chess openings or famous games if you ask.
Interactive Board: Play out positions on a built-in chess board. You (and eventually the AI) can make moves on the board. Ask “Why was that move a mistake?” or “What’s a better move here?”, and get instant feedback.
Beginner-Friendly Advice: The AI has been trained on chess materials and tailored to speak at a beginner’s level. That means explanations use clear language and avoid jargon (or explain the jargon when needed). We want you to learn, not be intimidated.
Offline Capabilities (Planned): We are working on allowing some features to run offline. For now, an internet connection is required for the AI to function (since it calls cloud APIs), but the long-term goal is to make basic coaching available without internet.
(Note: ChessPedagogue is currently in beta – it’s under active development. You might encounter the occasional bug or incomplete feature. We appreciate your patience and feedback!)
Screenshots
【❗️*(Project maintainer: Consider adding a couple of screenshots of the app UI here – e.g., the chess board screen with a microphone button, and maybe a conversation transcript. Users love visuals.)*】
Getting Started (Installation & Setup)
So, you’re ready to try out ChessPedagogue? Awesome! Follow these steps to get the app up and running on your device or emulator.
Prerequisites
An Android device or emulator running Android 8.0 (API 26) or above.
Internet connection for the AI features to work (the app will call AI services online).
(Developers) Android Studio Dolphin or later if you plan to compile the app yourself.
Installation
Option 1: Install from APK (Easy) – Download the latest APK from the Releases page (look for the file ending in .apk under the latest release). On your Android device, you may need to enable “Install from unknown sources” in settings, then open the APK to install. Launch the app, and you’re good to go! Option 2: Build from source (For Developers) – If you want to poke around the code or modify ChessPedagogue:
Clone this repository:
bash
Copy
Edit
git clone https://github.com/IvanMazeppa/ChessPedagogue.git
Make sure to switch to the research-ready-v0.6.3 branch (or the latest stable branch) after cloning:
bash
Copy
Edit
git checkout research-ready-v0.6.3
Open the project in Android Studio. It should automatically import the Gradle settings. (Give it a minute to sync.)
You’ll need an OpenAI API key for the AI services to work. ChessPedagogue uses OpenAI’s Whisper, GPT, and voice APIs.
Create a file (or use an existing config) to provide your API key. For example, you can add it to the local.properties (which is not committed to Git) like:
properties
Copy
Edit
OPENAI_API_KEY=sk-yourkeyhere123456
The app code will load this key (the exact method may vary; check Constants.java or similar for how the key is accessed). Never commit your API key!
github.com
Connect an Android device via USB (with USB debugging enabled) or start an emulator.
Hit the Run ▶️ button in Android Studio. The app should compile (it includes some native C++ code, so the first build might take a couple of minutes) and install on your device/emulator.
Upon first launch, the app will ask for microphone permission (since it needs to record your voice for the speech input). Grant the permission.
That’s it – you should see the main ChessPedagogue screen with a chess board and a mic button!
Usage Instructions
Using ChessPedagogue is as simple as having a conversation. Here’s how you can interact:
Start a New Session: When you open the app, you’ll see a chess board. You can set up any position you like or use the default starting position. Let’s assume you want to play from the start of a game.
Talk to the AI Coach: Tap the 🎤 Microphone button and ask your question or make your move. For example:
“What should I do in this position?”
“Why was that move a bad idea?”
“I’m thinking of moving my knight to f3. Is that good?”
Or even “Can you explain what a fork is?” (if you’re looking for general knowledge).
Speak clearly, and for best results, limit each question to a single topic. You’ll see a waveform or indicator that recording is happening. When you’re done speaking, tap the mic again to stop (or it will auto-stop after a few seconds of silence).
Wait for the Response: The app will convert your speech to text and send it to the AI. You’ll see a “thinking” indicator while the AI formulates a reply (this might take a couple of seconds – it’s a really smart AI, so hang tight).
Hear the Answer: The AI coach will talk back to you! You’ll hear a voice like a friendly mentor explaining things. The corresponding text might also appear on screen (useful if you missed part of the audio). By default, the voice is female and warm, but we might add voice options later.
Continue the Conversation: You can ask follow-up questions. The AI remembers the recent conversation context. For example, if you asked “Why was that move bad?” and it answered, you can then ask “What would have been better?” without re-describing the whole situation – it knows you’re referring to the same position.
Making Moves on the Board: You or the AI can play moves on the board. If you want the AI to suggest a move for your side, just ask! If you want it to play as an opponent, you can say something like “Your turn, make a move as Black.” (Playing full games with the AI is experimental, but possible.) After a move is made on the board, feel free to ask for analysis: “Why did you/ I move that bishop?” etc.
End or Reset a Session: There’s a reset button (🔄) to clear the board and conversation if you want to start fresh (for example, begin a new game or scenario). We recommend resetting when you switch to a completely new topic or position, to give the AI a clean slate.
Example Interaction:
You: "Hi coach, I'm brand new. How does the knight move?"
AI: "Hello! Welcome to chess. Knights move in an L-shape - two squares in one direction and one square perpendicular. For example, from the center of the board, a knight can jump to 8 different spots. It's the only piece that can 'jump' over others!"
You: "Cool, and what's a good first move with knights?"
AI: "A common strategy is to move a knight toward the center early. For instance, you can move your knight from g1 to f3. This helps control the center. Give it a try!"
(You then physically move the knight on the app's board from g1 to f3.)
AI: "Great move! Now your knight is influencing the center of the board..." and so on.
Feel free to ask anything chess-related. The AI is tuned to be helpful and patient. If it says something you don’t understand, you can ask for clarification. If the answer seems off, you can ask again or report it (we’re constantly improving the AI’s training).
Contributing 🙌
First off, thank you for considering contributing to ChessPedagogue! This project is open to beginners, and we mean it. Whether you’re a student learning Android development, a chess enthusiast with coding skills, or just someone excited about AI, you are welcome here. We appreciate all kinds of contributions – code, documentation, testing, ideas, or even just using the app and giving feedback.
How Can You Help?
🐛 Report Bugs: If you find a bug (the app crashes, or the AI says something really strange, or a chess rule is implemented incorrectly), please check the Issues page. If it’s not already reported, open a new issue. Try to include details like what you did, what happened, and what you expected. Screenshots or logs are helpful too.
🌟 Suggest Improvements: Have an idea for a new feature or a better way to do something? Open an issue for discussion or start a discussion thread. Don’t worry if you’re new – all ideas are welcome. Maybe you want more voice choices, or a way to practice specific endgames, or support for another language. Let’s talk!
📝 Improve Documentation: Not ready to dive into code? You can still contribute big time by improving this README or the wiki. If something was confusing during setup, you can help clarify it for the next person. Even fixing typos or adding a screenshot is valuable. Documentation contributions are highly appreciated (and a great way for first-time contributors to make a pull request!).
💻 Contribute Code: If you want to tackle a coding task, we maintain a list of issues ideal for newcomers. Look for issues labeled “good first issue” or “help wanted”. (If you don’t see any, feel free to ask – we’ll gladly guide you to something suitable.) We’re happy to mentor new contributors through the process. No contribution is too small – even cleaning up code style or refactoring a function is useful.
✅ Test and Give Feedback: Simply using the app and telling us what you think is super valuable. If something in the UI was hard to use, or the AI gave an explanation you still didn’t get, let us know. This helps us make the app better for learners. You can open an issue or join our Discord/Slack (planned) to chat informally.
Development Tips for Beginners
We understand not everyone is familiar with Android or AI programming. That’s okay! This project can be a learning playground. Some tips:
The code is split between Java/Kotlin (for Android UI and app logic) and C++ (for the chess engine and possibly ML). If you’re not comfortable with C++, you can focus on the Java/Kotlin parts (e.g., improving the UI, adding a new screen). If mobile is new but you know algorithms, you might find the chess logic in C++ interesting to work on.
We try to keep the code reasonably commented, and we’re working on a wiki to explain how things work under the hood (like the voice pipeline). If you’re unsure about a part of the code, ask! You can open a draft pull request or an issue with your question. We promise to be nice – there are no stupid questions. 🙂
Setting up the development environment can be the hardest part. If you run into trouble building the project, feel free to open an issue about it or reach out. We’ve all been stuck on configuration issues before, and we’ll help you through it.
When you contribute code, please follow the existing code style. (We’re not super strict, but for example, keep indentations and naming consistent). If you’re adding a new file or function, add a comment at the top explaining what it’s for – this helps everyone.
If you’re adding a new feature, consider writing a quick note in the Pull Request about how it works or how to test it. This helps us merge it faster.
Remember, contributing to open source is a two-way street: you help improve the project, and you learn & gain experience in return. We strive to make ChessPedagogue a welcoming project for newbies. 🌱 Don’t hesitate to reach out if you need any guidance on making your first contribution. We were all beginners once! (Maintainers are available via GitHub issues and will set up a Discord channel if there’s interest, to chat and mentor in real-time.)
manishearth.github.io
Code of Conduct
Please note, we want a positive, harassment-free experience for everyone. Be respectful and kind in all interactions. We haven’t formalized a code of conduct yet, but we adhere to basic principles: be welcoming, patient, and assume good intent. Discrimination or rudeness have no place here
manishearth.github.io
. Let’s make this project fun and educational for all!
Project Status and Roadmap
ChessPedagogue is in active development (current version 0.6.3, “Research Ready”). Here’s where we stand and where we’re headed:
Core Voice Feature: Completed. You can have a full spoken conversation with the AI. (We continue to refine the voice recognition accuracy and the naturalness of the TTS.)
Chess Engine Integration: Partially done. The app includes a built-in chess engine for move validation and basic analysis. We plan to deepen this integration so the AI’s advice is always backed by a strong engine (especially for tactical positions).
Fine-Tuned AI Coach: In progress. We are training a custom version of the AI on chess-specific material (thousands of chess puzzles, Wikipedia articles, etc.). The goal is to make the AI coach even more knowledgeable and tailored to chess learners. Fine-tuning should help the AI give more precise advice and use appropriate difficulty in explanations
medium.com
. Expect an update on this soon.
Bugs and Stability: In progress. We know of a few bugs (for example, sometimes the app might not recognize it’s checkmate – so it might keep coaching beyond the game’s end). Issue #1 is tracking an input bug where occasionally the user can’t make a move on the board
github.com
. We are investigating these. If you encounter a bug, please report it as mentioned above.
Planned Features:
Adjustable Coach Personalities: We’d like to offer different coaching styles (e.g., a strict coach, a motivational coach, etc.) that you can switch between for fun.
Lesson Mode: A guided tutorial mode for absolute beginners (covering rules, basic tactics) in a structured way.
Multilingual Support: We aim to support other languages for both input and output, so you could learn chess in your native language. This will leverage Whisper’s multilingual speech-to-text and appropriate voice models.
UI Improvements: Polishing the interface, adding dark mode, improving accessibility (for example, ensuring the app is usable for visually impaired with screen readers – since it’s voice-based, it has potential here).
Offline Mode: Possibly using smaller offline models for basic Q&A when you have no internet. This is exploratory.
Collaboration & Community: Right now, it’s a solo project with help from AI, but we’d love to build a community around ChessPedagogue. If the project gains traction, we’ll set up communication channels (Discord, etc.), and maybe hold chess AI hackathons! The roadmap is open – community interest will shape it.
Check the Projects or Roadmap file (if available) for more details on what’s being worked on. And feel free to suggest new ideas or priorities.
License
(No open-source license has been chosen yet for ChessPedagogue.) This means the code is copyrighted by the author (IvanMazeppa) for now. We are evaluating open-source licenses and will update here once decided. For contributors: By contributing, you agree that your contributions can be included under the project’s future license. We’re leaning towards a permissive license (MIT or Apache 2.0) to encourage wide use, but need to confirm. If you need clarification, please raise the topic – we want to choose a license that fosters collaboration while respecting the effort put in. (Once a license is decided, a proper LICENSE file will be added. In the meantime, please ask if you have questions about usage of this code.)
Thank you for reading this far! We hope you enjoy using ChessPedagogue and maybe even contributing to it. This project started from a passion for chess and AI, and it’s incredible to see them come together. Our vision is to make learning chess less intimidating and more interactive. With your help, we can make ChessPedagogue a fantastic learning tool for chess lovers around the world. Happy coding, and happy chess-playing! ♟️🤖🎉
