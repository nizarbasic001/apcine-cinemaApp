import urllib.request
import re
import json

url = "https://cinestream-movie-str-4phw.bolt.host/assets/index-rADGTg5E.js"
req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
content = urllib.request.urlopen(req).read().decode("utf-8")

print(f"Bundle length: {len(content)}")

# Supabase URLs
urls = list(set(re.findall(r"https://[a-zA-Z0-9.-]+\.supabase\.co", content)))
print("Supabase URLs:", urls)

# Anon keys
keys = list(set(re.findall(r"eyJ[a-zA-Z0-9_\-\.]+", content)))
print("Keys found:", [k[:25] + "..." + k[-10:] for k in keys if len(k) > 50])

# Tables
tables = list(set(re.findall(r'\.from\(["\']([a-zA-Z0-9_\-]+)["\']\)', content)))
print("Database tables:", tables)

# Search for select queries
selects = list(set(re.findall(r'\.select\(["\']([^"\']+)["\']\)', content)))
print("Select queries:", selects)

# Search for watch URLs or video players
watch_matches = re.findall(r'(\w*watch\w*|\w*player\w*|\w*embed\w*|\w*video\w*)', content, re.IGNORECASE)
print("Keywords count:", len(watch_matches))

# Find occurrences of createClient
clients = re.findall(r'createClient\([^)]+\)', content)
print("createClient:", clients)

# Print any occurrences of tables with context
for tbl in tables:
    matches = [m.start() for m in re.finditer(re.escape(f'.from("{tbl}")'), content)]
    for idx in matches[:3]:
        print(f"--- Context for {tbl} ---")
        print(content[max(0, idx - 100):min(len(content), idx + 200)])

with open("/tmp/cinestream_bundle.js", "w") as f:
    f.write(content)
print("Saved bundle to /tmp/cinestream_bundle.js")
