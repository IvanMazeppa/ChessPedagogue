#!/usr/bin/env python3
"""
ChessPedagogue AI Enhancement Server
===================================

A lightweight HTTP server that provides AI enhancement services to the Android app.
This server receives requests from the Java app and returns enhanced responses.

Usage:
    python3 enhancement_server.py [--port 8080] [--host localhost]
"""

import json
import http.server
import socketserver
import urllib.parse
import logging
import sys
import argparse
from typing import Dict, Any

from chess_enhancement_core import (
    ChessAIEnhancer, ChessContext, SkillLevel, EmotionalContext,
    EnhancementConfig
)

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class EnhancementRequestHandler(http.server.BaseHTTPRequestHandler):
    """HTTP request handler for AI enhancement requests"""
    
    def __init__(self, *args, enhancer=None, **kwargs):
        self.enhancer = enhancer or ChessAIEnhancer()
        super().__init__(*args, **kwargs)
    
    def do_POST(self):
        """Handle POST requests for response enhancement"""
        try:
            if self.path == '/enhance':
                self._handle_enhancement_request()
            elif self.path == '/health':
                self._handle_health_check()
            else:
                self._send_error_response(404, "Endpoint not found")
                
        except Exception as e:
            logger.error(f"Error handling request: {e}")
            self._send_error_response(500, f"Internal server error: {str(e)}")
    
    def do_GET(self):
        """Handle GET requests"""
        if self.path == '/health':
            self._handle_health_check()
        else:
            self._send_error_response(404, "Endpoint not found")
    
    def _handle_enhancement_request(self):
        """Handle response enhancement request"""
        try:
            # Read request body
            content_length = int(self.headers['Content-Length'])
            post_data = self.rfile.read(content_length)
            request_data = json.loads(post_data.decode('utf-8'))
            
            # Validate request
            if not self._validate_enhancement_request(request_data):
                self._send_error_response(400, "Invalid request format")
                return
            
            # Parse request
            context = self._parse_chess_context(request_data)
            original_response = request_data['original_response']
            
            # Enhance response
            enhanced_response = self.enhancer.enhance_response(original_response, context)
            
            # Send response
            response_data = {
                "success": True,
                "original_response": original_response,
                "enhanced_response": enhanced_response,
                "master": context.master_name,
                "skill_level": context.user_skill.value,
                "enhancements_applied": [
                    "personality_amplification",
                    "difficulty_adaptation", 
                    "emotional_intelligence",
                    "historical_context"
                ]
            }
            
            self._send_json_response(200, response_data)
            
        except json.JSONDecodeError:
            self._send_error_response(400, "Invalid JSON in request body")
        except KeyError as e:
            self._send_error_response(400, f"Missing required field: {e}")
        except Exception as e:
            logger.error(f"Enhancement error: {e}")
            self._send_error_response(500, f"Enhancement failed: {str(e)}")
    
    def _handle_health_check(self):
        """Handle health check request"""
        response_data = {
            "status": "healthy",
            "service": "ChessPedagogue AI Enhancement",
            "version": "1.0.0"
        }
        self._send_json_response(200, response_data)
    
    def _validate_enhancement_request(self, data: Dict[str, Any]) -> bool:
        """Validate enhancement request format"""
        required_fields = [
            'original_response', 'master_name', 'fen', 
            'user_skill_level', 'position_evaluation'
        ]
        
        for field in required_fields:
            if field not in data:
                logger.warning(f"Missing required field: {field}")
                return False
        
        return True
    
    def _parse_chess_context(self, data: Dict[str, Any]) -> ChessContext:
        """Parse request data into ChessContext object"""
        # Parse skill level
        skill_mapping = {
            "beginner": SkillLevel.BEGINNER,
            "intermediate": SkillLevel.INTERMEDIATE,
            "advanced": SkillLevel.ADVANCED, 
            "expert": SkillLevel.EXPERT
        }
        
        user_skill = skill_mapping.get(
            data.get('user_skill_level', 'intermediate').lower(),
            SkillLevel.INTERMEDIATE
        )
        
        # Determine position phase from FEN (simplified)
        fen = data['fen']
        position_phase = self._determine_position_phase(fen)
        
        # Extract tactical themes (can be expanded with actual analysis)
        tactical_themes = data.get('tactical_themes', ['general'])
        
        # Create context
        context = ChessContext(
            fen=fen,
            move_history=data.get('move_history', []),
            current_move=data.get('current_move'),
            evaluation=float(data.get('position_evaluation', 0.0)),
            master_name=data['master_name'].lower(),
            user_skill=user_skill,
            position_phase=position_phase,
            tactical_themes=tactical_themes,
            emotional_weight=EmotionalContext.NEUTRAL
        )
        
        return context
    
    def _determine_position_phase(self, fen: str) -> str:
        """Determine game phase from FEN (simplified analysis)"""
        # Count pieces to determine phase
        position_part = fen.split()[0]
        piece_count = sum(1 for char in position_part if char.isalpha())
        
        if piece_count > 20:
            return "opening"
        elif piece_count > 10:
            return "middlegame"
        else:
            return "endgame"
    
    def _send_json_response(self, status_code: int, data: Dict[str, Any]):
        """Send JSON response"""
        self.send_response(status_code)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
        
        response_json = json.dumps(data, indent=2)
        self.wfile.write(response_json.encode('utf-8'))
    
    def _send_error_response(self, status_code: int, message: str):
        """Send error response"""
        error_data = {
            "success": False,
            "error": message,
            "status_code": status_code
        }
        self._send_json_response(status_code, error_data)
    
    def log_message(self, format, *args):
        """Override to use our logger"""
        logger.info("%s - %s" % (self.address_string(), format % args))


class EnhancementServer:
    """Main server class for AI enhancement service"""
    
    def __init__(self, host: str = 'localhost', port: int = 8080):
        self.host = host
        self.port = port
        self.enhancer = ChessAIEnhancer()
        
    def start(self):
        """Start the enhancement server"""
        # Create custom handler with enhancer
        def handler_factory(*args, **kwargs):
            return EnhancementRequestHandler(*args, enhancer=self.enhancer, **kwargs)
        
        with socketserver.TCPServer((self.host, self.port), handler_factory) as httpd:
            logger.info(f"🚀 ChessPedagogue AI Enhancement Server starting on {self.host}:{self.port}")
            logger.info("📡 Endpoints available:")
            logger.info(f"   POST http://{self.host}:{self.port}/enhance - Enhance chess responses")
            logger.info(f"   GET  http://{self.host}:{self.port}/health  - Health check")
            logger.info("🎯 Ready to enhance chess master responses!")
            
            try:
                httpd.serve_forever()
            except KeyboardInterrupt:
                logger.info("🛑 Server shutting down...")
                httpd.shutdown()


def main():
    """Main entry point"""
    parser = argparse.ArgumentParser(description="ChessPedagogue AI Enhancement Server")
    parser.add_argument("--host", default="localhost", help="Server host (default: localhost)")
    parser.add_argument("--port", type=int, default=8080, help="Server port (default: 8080)")
    parser.add_argument("--debug", action="store_true", help="Enable debug logging")
    
    args = parser.parse_args()
    
    if args.debug:
        logging.getLogger().setLevel(logging.DEBUG)
    
    # Start server
    server = EnhancementServer(host=args.host, port=args.port)
    server.start()


if __name__ == "__main__":
    main()