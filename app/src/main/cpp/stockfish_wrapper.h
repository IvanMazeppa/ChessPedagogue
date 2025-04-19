#ifndef STOCKFISH_WRAPPER_H
#define STOCKFISH_WRAPPER_H

#include <string>

// Initialize the Stockfish engine
bool stockfish_init();

// Send a UCI command to Stockfish and get the response
std::string stockfish_command(const std::string& cmd);

// Clean up the Stockfish engine
void stockfish_quit();

#endif // STOCKFISH_WRAPPER_H