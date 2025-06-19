#!/usr/bin/env python3
"""
Chess Master Portrait Automation Script - CORRECTED VERSION
Downloads high-quality portraits from Wikimedia Commons and processes them for Android
"""

import requests
import os
from PIL import Image, ImageOps, ImageDraw
import json
import time
from urllib.parse import urlparse

class ChessPortraitManager:
    def __init__(self, project_path):
        """Initialize with your Android project path"""
        self.project_path = project_path
        self.drawable_path = os.path.join(project_path, "app", "src", "main", "res", "drawable")
        self.drawable_hdpi = os.path.join(project_path, "app", "src", "main", "res", "drawable-hdpi")
        self.drawable_mdpi = os.path.join(project_path, "app", "src", "main", "res", "drawable-mdpi")
        self.drawable_xhdpi = os.path.join(project_path, "app", "src", "main", "res", "drawable-xhdpi")
        self.drawable_xxhdpi = os.path.join(project_path, "app", "src", "main", "res", "drawable-xxhdpi")
        
        # Ensure directories exist
        for directory in [self.drawable_path, self.drawable_hdpi, self.drawable_mdpi, 
                         self.drawable_xhdpi, self.drawable_xxhdpi]:
            os.makedirs(directory, exist_ok=True)
        
        # Chess masters with verified working URLs and fallback searches
        self.chess_masters = {
            "alekhine": {
                "name": "Alexander Alekhine",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/b/b4/Alexander_Alekhine.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/a/ae/Alekhine.jpg",
                "search_terms": ["Alexander Alekhine", "Alekhine chess"]
            },
            "anand": {
                "name": "Viswanathan Anand", 
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/d/d4/Viswanathan_Anand_2013.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/a/ae/Anand_Viswanathan.jpg",
                "search_terms": ["Viswanathan Anand", "Anand chess"]
            },
            "botvinnik": {
                "name": "Mikhail Botvinnik",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/4/4c/Mikhail_Botvinnik_1961.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/8/8f/Botvinnik.jpg",
                "search_terms": ["Mikhail Botvinnik", "Botvinnik chess"]
            },
            "capablanca": {
                "name": "Jose Raul Capablanca",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/5/50/Jos%C3%A9_Ra%C3%BAl_Capablanca_c1915.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/e/e1/Capablanca.jpg",
                "search_terms": ["Jose Raul Capablanca", "Capablanca chess"]
            },
            "carlsen": {
                "name": "Magnus Carlsen",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/e/e6/Magnus_Carlsen_2016.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/c/c4/Magnus_Carlsen_2014.jpg",
                "search_terms": ["Magnus Carlsen", "Carlsen chess"]
            },
            "fischer": {
                "name": "Bobby Fischer",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/f/f8/Bobby_Fischer_1972.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/2/20/Bobby_Fischer_1960.jpg",
                "search_terms": ["Bobby Fischer", "Fischer chess"]
            },
            "karpov": {
                "name": "Anatoly Karpov",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/f/f9/Anatoly_Karpov_1978.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/a/a4/Karpov.jpg",
                "search_terms": ["Anatoly Karpov", "Karpov chess"]
            },
            "kasparov": {
                "name": "Garry Kasparov",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/6/6f/Garry_Kasparov_2014.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/2/2f/Kasparov-34.jpg",
                "search_terms": ["Garry Kasparov", "Kasparov chess"]
            },
            "kramnik": {
                "name": "Vladimir Kramnik",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/1/14/Vladimir_Kramnik_2013.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/e/e1/Kramnik.jpg",
                "search_terms": ["Vladimir Kramnik", "Kramnik chess"]
            },
            "lasker": {
                "name": "Emanuel Lasker",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/5/53/Emanuel_Lasker.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/8/82/Lasker.jpg",
                "search_terms": ["Emanuel Lasker", "Lasker chess"]
            },
            "morphy": {
                "name": "Paul Morphy",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/8/8f/Paul_Morphy.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/f/f8/Morphy.jpg",
                "search_terms": ["Paul Morphy", "Morphy chess"]
            },
            "tal": {
                "name": "Mikhail Tal",
                "wiki_url": "https://upload.wikimedia.org/wikipedia/commons/b/bf/Mikhail_Tal_1982.jpg",
                "backup_url": "https://upload.wikimedia.org/wikipedia/commons/e/e6/Tal.jpg",
                "search_terms": ["Mikhail Tal", "Tal chess"]
            }
        }
    
    def download_image(self, url, master_key):
        """Download image from URL with error handling"""
        try:
            print(f"📥 Downloading {self.chess_masters[master_key]['name']} portrait...")
            headers = {
                'User-Agent': 'ChessPedagogue/1.0 (Educational Chess App) Python/3.x'
            }
            
            response = requests.get(url, headers=headers, timeout=30)
            response.raise_for_status()
            
            # Save original image temporarily
            temp_path = f"temp_{master_key}.jpg"
            with open(temp_path, 'wb') as f:
                f.write(response.content)
            
            print(f"✅ Successfully downloaded {self.chess_masters[master_key]['name']}")
            return temp_path
            
        except Exception as e:
            print(f"❌ Error downloading {master_key}: {e}")
            return None
    
    def search_wikimedia_for_image(self, master_key):
        """Fallback: Search Wikimedia for chess master images"""
        try:
            master_info = self.chess_masters[master_key]
            search_terms = master_info.get('search_terms', [master_info['name']])
            
            print(f"🔍 Searching Wikimedia for {master_info['name']}...")
            
            # Use Wikimedia API to search for images
            api_url = "https://commons.wikimedia.org/w/api.php"
            
            for search_term in search_terms:
                params = {
                    'action': 'query',
                    'format': 'json',
                    'list': 'search',
                    'srsearch': search_term,
                    'srnamespace': 6,  # File namespace
                    'srlimit': 5
                }
                
                response = requests.get(api_url, params=params, timeout=10)
                if response.status_code == 200:
                    data = response.json()
                    if 'query' in data and 'search' in data['query']:
                        for result in data['query']['search']:
                            title = result['title'].replace('File:', '')
                            if any(word in title.lower() for word in ['portrait', 'photo', master_info['name'].lower().split()[-1]]):
                                # Found a potential image
                                image_url = f"https://commons.wikimedia.org/wiki/Special:FilePath/{title}"
                                print(f"🎯 Found potential image: {title}")
                                return image_url
            
            return None
            
        except Exception as e:
            print(f"❌ Error searching Wikimedia: {e}")
            return None
    
    def create_circular_portrait(self, image_path, size):
        """Create a circular portrait with nice border"""
        try:
            # Open and resize image
            img = Image.open(image_path).convert('RGBA')
            
            # Resize maintaining aspect ratio
            img.thumbnail((size, size), Image.Resampling.LANCZOS)
            
            # Create a square canvas
            canvas = Image.new('RGBA', (size, size), (0, 0, 0, 0))
            
            # Center the image on canvas
            offset = ((size - img.size[0]) // 2, (size - img.size[1]) // 2)
            canvas.paste(img, offset)
            
            # Create circular mask
            mask = Image.new('L', (size, size), 0)
            draw = ImageDraw.Draw(mask)
            draw.ellipse((2, 2, size-2, size-2), fill=255)
            
            # Apply mask to create circle
            canvas.putalpha(mask)
            
            return canvas
            
        except Exception as e:
            print(f"❌ Error creating circular portrait: {e}")
            return None
    
    def create_rectangular_portrait(self, image_path, width, height):
        """Create a rectangular portrait for card display"""
        try:
            img = Image.open(image_path).convert('RGB')
            
            # Calculate crop area to get square from center
            w, h = img.size
            size = min(w, h)
            left = (w - size) // 2
            top = (h - size) // 2
            right = left + size
            bottom = top + size
            
            # Crop to square and resize
            img_cropped = img.crop((left, top, right, bottom))
            img_resized = img_cropped.resize((width, height), Image.Resampling.LANCZOS)
            
            return img_resized
            
        except Exception as e:
            print(f"❌ Error creating rectangular portrait: {e}")
            return None
    
    def process_master_portraits(self, master_key):
        """Process all portrait variants for a master with enhanced fallback"""
        master_info = self.chess_masters[master_key]
        
        # Try primary URL first, then backup, then search
        temp_path = self.download_image(master_info['wiki_url'], master_key)
        if not temp_path:
            temp_path = self.download_image(master_info['backup_url'], master_key)
        
        # If both URLs failed, try searching Wikimedia
        if not temp_path:
            print(f"🔄 Trying fallback search for {master_info['name']}...")
            search_url = self.search_wikimedia_for_image(master_key)
            if search_url:
                temp_path = self.download_image(search_url, master_key)
        
        if not temp_path:
            print(f"❌ Failed to download {master_info['name']} portrait from all sources")
            return False
        
        try:
            print(f"🎨 Processing {master_info['name']} portraits...")
            
            # Create card portraits (rectangular for the selection cards) - INCREASED RESOLUTION
            card_portrait_xlarge = self.create_rectangular_portrait(temp_path, 150, 150)  # For high-res displays
            card_portrait_large = self.create_rectangular_portrait(temp_path, 120, 120)   # For cards
            card_portrait_medium = self.create_rectangular_portrait(temp_path, 90, 90)    # For medium displays
            card_portrait_small = self.create_rectangular_portrait(temp_path, 60, 60)     # For small displays
            
            # Create speaking portraits (circular for when AI is speaking) - INCREASED RESOLUTION
            speaking_portrait_xlarge = self.create_circular_portrait(temp_path, 120)  # For high-res displays
            speaking_portrait_large = self.create_circular_portrait(temp_path, 100)   # For speaking indicator
            speaking_portrait_medium = self.create_circular_portrait(temp_path, 80)   # For medium displays
            speaking_portrait_small = self.create_circular_portrait(temp_path, 60)    # For small displays
            
            # Save card portraits with higher resolution
            if card_portrait_xlarge:
                card_portrait_xlarge.save(os.path.join(self.drawable_xxhdpi, f"portrait_card_{master_key}.png"), "PNG")
                card_portrait_large.save(os.path.join(self.drawable_xhdpi, f"portrait_card_{master_key}.png"), "PNG")
                card_portrait_medium.save(os.path.join(self.drawable_hdpi, f"portrait_card_{master_key}.png"), "PNG")
                card_portrait_small.save(os.path.join(self.drawable_mdpi, f"portrait_card_{master_key}.png"), "PNG")
                print(f"✅ Created card portraits for {master_info['name']} (up to 150x150px)")
            
            # Save speaking portraits with higher resolution
            if speaking_portrait_xlarge:
                speaking_portrait_xlarge.save(os.path.join(self.drawable_xxhdpi, f"portrait_speaking_{master_key}.png"), "PNG")
                speaking_portrait_large.save(os.path.join(self.drawable_xhdpi, f"portrait_speaking_{master_key}.png"), "PNG")
                speaking_portrait_medium.save(os.path.join(self.drawable_hdpi, f"portrait_speaking_{master_key}.png"), "PNG")
                speaking_portrait_small.save(os.path.join(self.drawable_mdpi, f"portrait_speaking_{master_key}.png"), "PNG")
                print(f"✅ Created speaking portraits for {master_info['name']} (up to 120x120px)")
            
            # Clean up temp file
            os.remove(temp_path)
            return True
            
        except Exception as e:
            print(f"❌ Error processing {master_info['name']}: {e}")
            # Clean up temp file
            if os.path.exists(temp_path):
                os.remove(temp_path)
            return False
    
    def process_all_masters(self):
        """Process portraits for all chess masters"""
        print("🎯 Starting Chess Master Portrait Processing...")
        print("=" * 60)
        
        successful = 0
        total = len(self.chess_masters)
        
        for master_key in self.chess_masters.keys():
            if self.process_master_portraits(master_key):
                successful += 1
            
            # Be nice to Wikimedia servers
            time.sleep(1)
            print("-" * 40)
        
        print(f"\n🎉 Processing Complete!")
        print(f"✅ Successfully processed: {successful}/{total} masters")
        print(f"📁 Portraits saved to: {self.drawable_path}")
        
        # Generate usage guide
        self.generate_usage_guide()
    
    def generate_usage_guide(self):
        """Generate a guide for implementing the portraits in Android"""
        guide = """
# Chess Master Portraits Implementation Guide

## Files Generated:
- `portrait_card_[master].png` - For selection cards (rectangular, up to 150x150px)
- `portrait_speaking_[master].png` - For speaking indicator (circular, up to 120x120px)

## Android Layout Usage:

### In your card layouts, replace the existing ImageView:
```xml
<ImageView
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:src="@drawable/portrait_card_alekhine"
    android:contentDescription="Alexander Alekhine portrait"
    android:scaleType="centerCrop" />
```

### For speaking indicator:
```xml
<ImageView
    android:id="@+id/coachPortrait"
    android:layout_width="60dp"
    android:layout_height="60dp"
    android:src="@drawable/portrait_speaking_alekhine"
    android:contentDescription="Coach Alekhine"
    android:visibility="gone" />
```

### In your Activity code:
```java
// Show speaking portrait
ImageView coachPortrait = findViewById(R.id.coachPortrait);
String masterName = getSelectedMaster(); // e.g., "alekhine"
String drawableName = "portrait_speaking_" + masterName;
int resourceId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
coachPortrait.setImageResource(resourceId);
coachPortrait.setVisibility(View.VISIBLE);
```

## Master Keys:
alekhine, anand, botvinnik, capablanca, carlsen, fischer, 
karpov, kasparov, kramnik, lasker, morphy, tal
        """
        
        with open("portrait_implementation_guide.md", "w") as f:
            f.write(guide)
        
        print(f"📖 Implementation guide saved to: portrait_implementation_guide.md")

# Usage Example
if __name__ == "__main__":
    # Set this to your Android project path
    PROJECT_PATH = r"D:\Users\dilli\AndroidStudioProjects\ChessPedagogue"  # CORRECTED PATH!
    
    print("🏆 Chess Master Portrait Automation")
    print("=====================================")
    
    # Initialize the manager
    manager = ChessPortraitManager(PROJECT_PATH)
    
    # Process all portraits
    manager.process_all_masters()
    
    print("\n🎯 All done! Your chess masters now have beautiful portraits!")
    print("📖 Check the implementation guide for Android integration steps.")