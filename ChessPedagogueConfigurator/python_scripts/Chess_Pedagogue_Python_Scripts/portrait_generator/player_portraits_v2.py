#!/usr/bin/env python3
"""
Dynamic Portrait Hunter with Live Search
Intelligently searches multiple sources for the best chess master portraits
"""

import cv2
import numpy as np
from PIL import Image, ImageDraw, ImageFilter
import os
import requests
import time
import json
from urllib.parse import quote, unquote
import re

class DynamicPortraitHunter:
    def __init__(self, project_path):
        self.project_path = project_path
        self.drawable_xxhdpi = os.path.join(project_path, "app", "src", "main", "res", "drawable-xxhdpi")
        self.drawable_xhdpi = os.path.join(project_path, "app", "src", "main", "res", "drawable-xhdpi")
        self.drawable_hdpi = os.path.join(project_path, "app", "src", "main", "res", "drawable-hdpi")
        self.drawable_mdpi = os.path.join(project_path, "app", "src", "main", "res", "drawable-mdpi")
        
        # Initialize face detector
        self.face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
        self.profile_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_profileface.xml')
        
        # Chess masters with comprehensive search terms
        self.chess_masters = {
            "alekhine": {
                "name": "Alexander Alekhine",
                "search_terms": ["Alexander Alekhine", "Alekhine chess", "Алехин", "Alexandre Alekhine"],
                "birth_year": "1892",
                "keywords": ["portrait", "photo", "chess", "grandmaster", "1920s", "1930s"]
            },
            "anand": {
                "name": "Viswanathan Anand",
                "search_terms": ["Viswanathan Anand", "Anand chess", "Vishy Anand"],
                "birth_year": "1969",
                "keywords": ["portrait", "photo", "chess", "grandmaster", "world champion"]
            },
            "botvinnik": {
                "name": "Mikhail Botvinnik",
                "search_terms": ["Mikhail Botvinnik", "Botvinnik chess", "Ботвинник"],
                "birth_year": "1911",
                "keywords": ["portrait", "photo", "chess", "soviet", "1950s", "1960s"]
            },
            "capablanca": {
                "name": "Jose Raul Capablanca",
                "search_terms": ["Jose Raul Capablanca", "Capablanca chess", "José Raúl Capablanca"],
                "birth_year": "1888",
                "keywords": ["portrait", "photo", "chess", "cuban", "1920s"]
            },
            "carlsen": {
                "name": "Magnus Carlsen",
                "search_terms": ["Magnus Carlsen", "Carlsen chess", "Magnus Øen Carlsen"],
                "birth_year": "1990",
                "keywords": ["portrait", "photo", "chess", "norwegian", "world champion"]
            },
            "fischer": {
                "name": "Bobby Fischer",
                "search_terms": ["Bobby Fischer", "Robert Fischer", "Fischer chess"],
                "birth_year": "1943",
                "keywords": ["portrait", "photo", "chess", "american", "1960s", "1970s"]
            },
            "karpov": {
                "name": "Anatoly Karpov",
                "search_terms": ["Anatoly Karpov", "Karpov chess", "Карпов"],
                "birth_year": "1951",
                "keywords": ["portrait", "photo", "chess", "soviet", "1970s", "1980s"]
            },
            "kasparov": {
                "name": "Garry Kasparov",
                "search_terms": ["Garry Kasparov", "Kasparov chess", "Гарри Каспаров"],
                "birth_year": "1963",
                "keywords": ["portrait", "photo", "chess", "soviet", "1980s", "1990s"]
            },
            "kramnik": {
                "name": "Vladimir Kramnik",
                "search_terms": ["Vladimir Kramnik", "Kramnik chess", "Крамник"],
                "birth_year": "1975",
                "keywords": ["portrait", "photo", "chess", "russian", "world champion"]
            },
            "lasker": {
                "name": "Emanuel Lasker",
                "search_terms": ["Emanuel Lasker", "Lasker chess", "Emmanuel Lasker"],
                "birth_year": "1868",
                "keywords": ["portrait", "photo", "chess", "german", "1900s", "1910s"]
            },
            "morphy": {
                "name": "Paul Morphy",
                "search_terms": ["Paul Morphy", "Morphy chess", "Paul Charles Morphy"],
                "birth_year": "1837",
                "keywords": ["portrait", "photo", "chess", "american", "1850s", "1860s"]
            },
            "tal": {
                "name": "Mikhail Tal",
                "search_terms": ["Mikhail Tal", "Tal chess", "Михаил Таль"],
                "birth_year": "1936",
                "keywords": ["portrait", "photo", "chess", "latvian", "soviet", "1960s"]
            }
        }

    def search_wikimedia_commons(self, master_key, max_results=10):
        """Dynamically search Wikimedia Commons for chess master images"""
        master_info = self.chess_masters[master_key]
        found_urls = []
        
        print(f"🔍 Searching Wikimedia Commons for {master_info['name']}...")
        
        # Multiple search strategies
        search_strategies = [
            # Strategy 1: Name + chess
            f"{master_info['name']} chess",
            # Strategy 2: Last name + portrait
            f"{master_info['name'].split()[-1]} portrait",
            # Strategy 3: Full name + photo
            f"{master_info['name']} photo",
            # Strategy 4: Alternative search terms
            *master_info['search_terms']
        ]
        
        for search_term in search_strategies:
            try:
                print(f"  🎯 Searching: '{search_term}'")
                
                # Search using Wikimedia API
                api_url = "https://commons.wikimedia.org/w/api.php"
                params = {
                    'action': 'query',
                    'format': 'json',
                    'list': 'search',
                    'srsearch': search_term,
                    'srnamespace': 6,  # File namespace
                    'srlimit': 15,
                    'srprop': 'size|wordcount|timestamp'
                }
                
                response = requests.get(api_url, params=params, timeout=15)
                if response.status_code == 200:
                    data = response.json()
                    
                    if 'query' in data and 'search' in data['query']:
                        for result in data['query']['search']:
                            title = result['title']
                            
                            # Score potential based on title relevance
                            relevance_score = self.score_image_relevance(title, master_info)
                            
                            if relevance_score > 3:  # Minimum relevance threshold
                                # Convert to direct image URL
                                filename = title.replace('File:', '')
                                image_url = f"https://commons.wikimedia.org/wiki/Special:FilePath/{quote(filename)}"
                                
                                found_urls.append({
                                    'url': image_url,
                                    'title': title,
                                    'score': relevance_score
                                })
                                
                                print(f"    ✨ Found: {title} (score: {relevance_score})")
                
                time.sleep(0.5)  # Be respectful to API
                
            except Exception as e:
                print(f"    ❌ Search error for '{search_term}': {e}")
                continue
        
        # Sort by relevance score and remove duplicates
        seen_urls = set()
        unique_results = []
        
        for item in sorted(found_urls, key=lambda x: x['score'], reverse=True):
            if item['url'] not in seen_urls:
                seen_urls.add(item['url'])
                unique_results.append(item)
        
        print(f"  📊 Found {len(unique_results)} potential images")
        return unique_results[:max_results]

    def score_image_relevance(self, title, master_info):
        """Score how relevant an image title is for portraits"""
        title_lower = title.lower()
        score = 0
        
        # Name matching
        name_parts = master_info['name'].lower().split()
        for part in name_parts:
            if part in title_lower:
                score += 3
        
        # Check for portrait keywords
        good_keywords = ['portrait', 'photo', 'photograph', '.jpg', '.jpeg', 'headshot', 'face']
        for keyword in good_keywords:
            if keyword in title_lower:
                score += 2
        
        # Check for chess context
        chess_keywords = ['chess', 'grandmaster', 'champion', 'tournament']
        for keyword in chess_keywords:
            if keyword in title_lower:
                score += 1
        
        # Penalize bad types
        bad_keywords = ['grave', 'tomb', 'statue', 'monument', 'signature', 'autograph', 'board', 'game']
        for keyword in bad_keywords:
            if keyword in title_lower:
                score -= 3
        
        # Bonus for file types that are likely photos
        if any(ext in title_lower for ext in ['.jpg', '.jpeg', '.png']):
            score += 1
        
        return max(0, score)

    def evaluate_portrait_quality(self, image_path):
        """Enhanced portrait quality evaluation"""
        try:
            # Read image
            img = cv2.imread(image_path)
            if img is None:
                return 0
                
            gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            
            # Detect faces with multiple scales
            faces = self.face_cascade.detectMultiScale(
                gray, 
                scaleFactor=1.05,  # More sensitive
                minNeighbors=3,    # More permissive
                minSize=(30, 30)   # Smaller minimum size
            )
            
            profile_faces = self.profile_cascade.detectMultiScale(
                gray, 
                scaleFactor=1.05, 
                minNeighbors=3,
                minSize=(30, 30)
            )
            
            all_faces = list(faces) + list(profile_faces)
            
            if len(all_faces) == 0:
                # Try alternative face detection for historical photos
                return self.evaluate_without_face_detection(img)
            
            # Enhanced scoring for detected faces
            height, width = gray.shape
            best_score = 0
            
            for (x, y, w, h) in all_faces:
                # Face size scoring
                face_area = w * h
                image_area = width * height
                face_ratio = face_area / image_area
                
                # Position scoring (prefer upper-center for portraits)
                center_x = x + w // 2
                center_y = y + h // 2
                
                # Ideal position for portraits is upper-center
                ideal_x = width // 2
                ideal_y = height // 3  # Upper third for portraits
                
                distance_from_ideal = abs(center_x - ideal_x) + abs(center_y - ideal_y)
                max_distance = width + height
                position_score = 1 - (distance_from_ideal / max_distance)
                
                # Image quality assessment
                face_region = gray[y:y+h, x:x+w]
                clarity_score = cv2.Laplacian(face_region, cv2.CV_64F).var() / 1000
                
                # Combined score
                base_score = (face_ratio * 150) + (position_score * 50) + (clarity_score * 20)
                
                # Bonuses
                if face_ratio > 0.05:  # Face is significant portion
                    base_score += 30
                if face_ratio > 0.15:  # Large face (great for portraits)
                    base_score += 50
                if w > 100 and h > 100:  # High resolution face
                    base_score += 20
                
                best_score = max(best_score, base_score)
            
            print(f"    📊 Portrait quality score: {best_score:.1f}")
            return best_score
            
        except Exception as e:
            print(f"    ❌ Error evaluating image: {e}")
            return 0

    def evaluate_without_face_detection(self, img):
        """Fallback scoring for images where face detection fails"""
        try:
            height, width = img.shape[:2]
            
            # Basic image quality
            gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            clarity = cv2.Laplacian(gray, cv2.CV_64F).var()
            
            # Prefer certain aspect ratios (portrait-like)
            aspect_ratio = height / width
            aspect_score = 0
            
            if 1.0 <= aspect_ratio <= 1.5:  # Good for portraits
                aspect_score = 20
            elif 0.8 <= aspect_ratio < 1.0:  # Square-ish, okay
                aspect_score = 10
            
            # Basic scoring
            base_score = (clarity / 100) + aspect_score
            
            print(f"    📊 Fallback quality score: {base_score:.1f}")
            return base_score
            
        except:
            return 1  # Minimal score for any downloadable image

    def download_and_test_image(self, url_info, master_key):
        """Download and test image quality"""
        try:
            url = url_info['url']
            print(f"  🔍 Testing: {url_info['title']}")
            
            headers = {
                'User-Agent': 'ChessPedagogue/1.0 (Educational Chess App) Python/3.x'
            }
            
            response = requests.get(url, headers=headers, timeout=30)
            response.raise_for_status()
            
            # Check if it's actually an image
            content_type = response.headers.get('content-type', '')
            if not any(img_type in content_type.lower() for img_type in ['image', 'jpeg', 'jpg', 'png']):
                print(f"    ❌ Not an image: {content_type}")
                return None, 0
            
            # Save temporarily
            temp_path = f"temp_{master_key}_{hash(url) % 10000}.jpg"
            with open(temp_path, 'wb') as f:
                f.write(response.content)
            
            # Test image quality
            quality_score = self.evaluate_portrait_quality(temp_path)
            
            # Combine with relevance score
            total_score = quality_score + (url_info['score'] * 5)
            
            return temp_path, total_score
            
        except Exception as e:
            print(f"    ❌ Error testing {url}: {e}")
            return None, 0

    def find_best_portrait_dynamic(self, master_key):
        """Dynamically find the best portrait using live search"""
        print(f"\n🎯 Hunting for the perfect portrait of {self.chess_masters[master_key]['name']}...")
        print("=" * 60)
        
        # Search for potential images
        potential_images = self.search_wikimedia_commons(master_key)
        
        if not potential_images:
            print(f"❌ No potential images found for {master_key}")
            return None
        
        print(f"🧪 Testing {len(potential_images)} potential images...")
        
        best_path = None
        best_score = 0
        
        for url_info in potential_images:
            temp_path, score = self.download_and_test_image(url_info, master_key)
            
            if temp_path and score > best_score:
                # Clean up previous best
                if best_path and os.path.exists(best_path):
                    os.remove(best_path)
                
                best_path = temp_path
                best_score = score
                print(f"    🏆 New champion! Score: {score:.1f}")
            elif temp_path:
                # Clean up this test image
                os.remove(temp_path)
            
            time.sleep(1)  # Be respectful
        
        if best_path and best_score > 5:  # Minimum acceptable score
            print(f"✨ Perfect portrait found! Final score: {best_score:.1f}")
            return best_path
        
        print(f"😔 No suitable portrait found for {master_key} (best score: {best_score:.1f})")
        if best_path:
            os.remove(best_path)
        return None

    def create_smart_portrait_crop(self, image_path, target_width, target_height):
        """Create intelligent portrait crop using face detection"""
        try:
            # Read image
            img_cv = cv2.imread(image_path)
            gray = cv2.cvtColor(img_cv, cv2.COLOR_BGR2GRAY)
            
            # Detect faces
            faces = self.face_cascade.detectMultiScale(gray, 1.05, 3, minSize=(30, 30))
            profile_faces = self.profile_cascade.detectMultiScale(gray, 1.05, 3, minSize=(30, 30))
            
            all_faces = list(faces) + list(profile_faces)
            
            # Convert to PIL
            img_pil = Image.open(image_path).convert('RGB')
            width, height = img_pil.size
            
            if len(all_faces) > 0:
                # Use largest face for cropping
                face = max(all_faces, key=lambda f: f[2] * f[3])
                x, y, w, h = face
                
                # Smart crop with generous padding for portrait feel
                padding_factor = 2.2  # More generous for better portraits
                crop_size = max(w, h) * padding_factor
                
                # Center on face
                center_x = x + w // 2
                center_y = y + h // 2
                
                # Calculate crop bounds
                crop_x1 = max(0, int(center_x - crop_size // 2))
                crop_y1 = max(0, int(center_y - crop_size // 2))
                crop_x2 = min(width, int(center_x + crop_size // 2))
                crop_y2 = min(height, int(center_y + crop_size // 2))
                
                cropped = img_pil.crop((crop_x1, crop_y1, crop_x2, crop_y2))
            else:
                # Intelligent center crop
                crop_size = min(width, height)
                left = (width - crop_size) // 2
                top = (height - crop_size) // 2
                cropped = img_pil.crop((left, top, left + crop_size, top + crop_size))
            
            # Resize to target
            final_img = cropped.resize((target_width, target_height), Image.Resampling.LANCZOS)
            return final_img
            
        except Exception as e:
            print(f"    ❌ Error creating smart crop: {e}")
            return None

    def create_circular_portrait(self, image_path, size):
        """Create circular portrait with smart cropping"""
        smart_crop = self.create_smart_portrait_crop(image_path, size, size)
        if not smart_crop:
            return None
        
        # Convert to RGBA
        img = smart_crop.convert('RGBA')
        
        # Create circular mask
        mask = Image.new('L', (size, size), 0)
        draw = ImageDraw.Draw(mask)
        draw.ellipse((2, 2, size-2, size-2), fill=255)
        
        # Apply mask
        img.putalpha(mask)
        return img

    def process_master_dynamic(self, master_key):
        """Process a master with dynamic portrait hunting"""
        # Find best portrait using dynamic search
        best_image_path = self.find_best_portrait_dynamic(master_key)
        
        if not best_image_path:
            print(f"💔 Could not find suitable portrait for {master_key}")
            return False
        
        try:
            print(f"🎨 Creating beautiful portraits for {self.chess_masters[master_key]['name']}...")
            
            # Create various sizes
            sizes = {
                'xxhdpi': (150, 120),  # Card, Speaking
                'xhdpi': (120, 100),
                'hdpi': (90, 80),
                'mdpi': (60, 60)
            }
            
            for density, (card_size, speaking_size) in sizes.items():
                folder = getattr(self, f'drawable_{density}')
                
                # Card portrait
                card_img = self.create_smart_portrait_crop(best_image_path, card_size, card_size)
                if card_img:
                    card_img.save(os.path.join(folder, f"portrait_card_{master_key}.png"), "PNG")
                
                # Speaking portrait
                speaking_img = self.create_circular_portrait(best_image_path, speaking_size)
                if speaking_img:
                    speaking_img.save(os.path.join(folder, f"portrait_speaking_{master_key}.png"), "PNG")
            
            print(f"✅ Successfully created dynamic portraits for {self.chess_masters[master_key]['name']}")
            
            # Clean up
            os.remove(best_image_path)
            return True
            
        except Exception as e:
            print(f"❌ Error processing {master_key}: {e}")
            if os.path.exists(best_image_path):
                os.remove(best_image_path)
            return False

    def hunt_all_portraits(self):
        """Hunt for all chess master portraits dynamically"""
        print("🏹 Dynamic Portrait Hunter - Live Search Mode")
        print("=" * 70)
        print("Searching the entire internet for the perfect chess master portraits!")
        print()
        
        successful = 0
        total = len(self.chess_masters)
        
        for master_key in self.chess_masters.keys():
            if self.process_master_dynamic(master_key):
                successful += 1
            print("\n" + "=" * 70)
            time.sleep(2)  # Be respectful to servers
        
        print(f"\n🎉 Dynamic Portrait Hunt Complete!")
        print(f"✅ Successfully found portraits for: {successful}/{total} masters")
        print(f"🎨 Your chess legends now have stunning, professional headshots!")
        
        if successful < total:
            print(f"\n💡 Tip: The system is very thorough! Even if some masters weren't found")
            print(f"    this time, you can run it again - Wikimedia has thousands of images!")

# Usage
if __name__ == "__main__":
    PROJECT_PATH = r"D:\Users\dilli\AndroidStudioProjects\ChessPedagogue"
    
    print("🏹 Dynamic Portrait Hunter with Live Search")
    print("=" * 50)
    print("This system will search the entire Wikimedia Commons database")
    print("to find the perfect portrait for each chess master!")
    print()
    
    hunter = DynamicPortraitHunter(PROJECT_PATH)
    hunter.hunt_all_portraits()
    
    print("\n🌟 Mission accomplished! Your chess masters are ready for their close-ups!")