import os
import json
from PIL import Image
from collections import Counter
import sys

def quantise(c, q=16):
    return (c // q) * q

def get_dominant_color(image_path):
    try:
        img = Image.open(image_path).convert('RGBA')
        pixels = img.getdata()

        # Filter out transparent pixels
        opaque_pixels = [p for p in pixels if p[3] > 128]
        if not opaque_pixels:
            return None

        quantised_pixels = [
            (
                quantise(r),
                quantise(g),
                quantise(b),
            )
            for r, g, b, a in opaque_pixels
        ]

        # Find most common colour
        (r, g, b), _ = Counter(quantised_pixels).most_common(1)[0]

        return f"0xFF{r:02X}{g:02X}{b:02X}"

    except Exception as e:
        print(f"Error processing {image_path}: {e}")
        return None

def process_textures(textures_path):
    block_colors = {}
    
    blocks_path = os.path.join(textures_path, 'block')
    
    if not os.path.exists(blocks_path):
        print(f"Block textures not found at: {blocks_path}")
        return block_colors
    
    for filename in os.listdir(blocks_path):
        if filename.endswith('.png'):
            material_name = filename[:-4].upper()
            
            texture_path = os.path.join(blocks_path, filename)
            color = get_dominant_color(texture_path)
            
            if color:
                block_colors[material_name] = color
                print(f"Processed: {material_name} -> {color}")
    
    return block_colors

def save_to_json(colors, output_file='src/main/resources/block_colours.json'):
    with open(output_file, 'w') as f:
        json.dump(colors, f, indent=2)
    print(f"\nSaved {len(colors)} colors to {output_file}")

def main():
    # Path to your extracted Minecraft jar textures
    # You need to extract the jar file first:
    # 1. Find minecraft.jar (in .minecraft/versions/[version]/[version].jar)
    # 2. Extract the jar with unzip
    # 3. Navigate to assets/minecraft/textures
    
    textures_path = sys.argv[1]
    
    if not os.path.exists(textures_path):
        print("Assets not found, please ensure the minecraft version jar has been extracted.")
        return
    
    colors = process_textures(textures_path)
    
    if colors:
        save_to_json(colors)
    else:
        print("No colors extracted!")

if __name__ == "__main__":
    main()