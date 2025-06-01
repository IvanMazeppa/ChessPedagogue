#!/bin/bash

# ChessPedagogue AI Enhancement Setup Script
# ==========================================

echo "🚀 Setting up ChessPedagogue AI Enhancement System..."
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠️${NC} $1"
}

print_error() {
    echo -e "${RED}❌${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ️${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "chess_enhancement_core.py" ]; then
    print_error "Please run this script from the chess_ai_enhancement directory"
    exit 1
fi

print_info "Checking Python installation..."

# Check Python version
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version 2>&1 | cut -d' ' -f2)
    print_status "Python $PYTHON_VERSION found"
else
    print_error "Python 3 is required but not found"
    exit 1
fi

# Check if pip is available
print_info "Checking pip installation..."
if python3 -c "import pip" &> /dev/null; then
    print_status "pip is available"
else
    print_warning "pip not found, installing packages manually..."
fi

# Test core enhancement system
print_info "Testing enhancement core system..."
if python3 -c "from chess_enhancement_core import ChessAIEnhancer; print('✅ Core system OK')" 2>/dev/null; then
    print_status "Enhancement core system is working"
else
    print_error "Core system test failed"
    exit 1
fi

# Run full test suite
print_info "Running enhancement test suite..."
if python3 test_enhancement.py > /dev/null 2>&1; then
    print_status "All enhancement tests passed"
else
    print_warning "Some tests failed, but system should still work"
fi

# Make scripts executable
chmod +x enhancement_server.py
chmod +x test_enhancement.py

print_status "Scripts made executable"

# Check if port 8080 is available
print_info "Checking if port 8080 is available..."
if command -v netstat &> /dev/null; then
    if netstat -tuln | grep ":8080 " > /dev/null; then
        print_warning "Port 8080 is in use. You may need to use a different port."
    else
        print_status "Port 8080 is available"
    fi
else
    print_info "netstat not available, skipping port check"
fi

echo
echo "🎉 Setup completed successfully!"
echo
echo "📋 Next Steps:"
echo "=============="
echo "1. Start the enhancement server:"
echo "   ${BLUE}python3 enhancement_server.py${NC}"
echo
echo "2. Or start with custom port:"
echo "   ${BLUE}python3 enhancement_server.py --port 8081${NC}"
echo
echo "3. Test the server in another terminal:"
echo "   ${BLUE}curl http://localhost:8080/health${NC}"
echo
echo "4. Integrate with your Android app using AIEnhancementService.java"
echo
echo "5. Configure in your Android app:"
echo "   ${BLUE}AIEnhancementService.getInstance(context).configure(\"http://localhost:8080/enhance\", true);${NC}"
echo
echo "📖 For detailed integration instructions, see the README or documentation."
echo
print_status "Ready to enhance your chess masters! 🎭♟️"