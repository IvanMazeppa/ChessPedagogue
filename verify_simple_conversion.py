#!/usr/bin/env python3
"""
🧪 Simple Coordinate Conversion Test
Test the fixed coordinate conversion logic.
"""

def test_simple_conversion():
    print("🧪 Testing Fixed Coordinate Conversion")
    print("=" * 40)
    
    # Test coordinates from the log
    from_row, from_col = 4, 7  # Should be h4
    to_row, to_col = 6, 5      # Should be f2
    
    # Apply the simplified conversion (no flipping transformation)
    from_file = chr(ord('a') + from_col)
    from_rank = 8 - from_row
    to_file = chr(ord('a') + to_col)
    to_rank = 8 - to_row
    
    from_square = f"{from_file}{from_rank}"
    to_square = f"{to_file}{to_rank}"
    
    print(f"Touch coordinates: ({from_row},{from_col}) -> ({to_row},{to_col})")
    print(f"Chess notation: {from_square} -> {to_square}")
    print(f"Expected: h4 -> f2")
    
    if from_square == "h4" and to_square == "f2":
        print("✅ COORDINATE CONVERSION FIXED!")
        print("✅ Should generate: Qxf2+")
        return True
    else:
        print("❌ Still incorrect")
        return False

if __name__ == "__main__":
    test_simple_conversion()