"""옷 28종에 대한 placeholder SVG 생성 스크립트.

카테고리 색상 + 이모지 + 한글 라벨로 단순 placeholder 생성.
실제 옷 이미지로 교체 시 동일 슬러그.svg → 동일 슬러그.png(또는 svg) 로 덮어쓰면 됨.

사용:
    python3 scripts/generate_clothes_svg.py

출력:
    backend/src/main/resources/static/images/clothes/<slug>.svg  (28개)
"""
from pathlib import Path

OUT_DIR = Path(__file__).resolve().parent.parent / "backend/src/main/resources/static/images/clothes"

CATEGORY_BG = {
    "TOP": "#FFE5D9",
    "INNER": "#E8F4F8",
    "BOTTOM": "#D5E8D4",
    "OUTER": "#F8E5C8",
    "SHOES": "#E8DCC4",
    "SOCKS": "#F4D9E0",
    "ACCESSORY": "#E5DDF5",
}

# (한글이름, 카테고리, 이모지, 슬러그)
CLOTHES = [
    ("히트텍",       "INNER",     "🥼", "heattech"),
    ("흰색 반팔티",   "TOP",       "👕", "tshirt-white"),
    ("긴팔 셔츠",     "TOP",       "👔", "shirt-long"),
    ("후디",         "TOP",       "👕", "hoodie"),
    ("니트",         "TOP",       "🧶", "knit"),
    ("린넨 셔츠",     "TOP",       "👔", "shirt-linen"),
    ("피케 셔츠",     "TOP",       "👕", "shirt-pique"),
    ("원피스",       "TOP",       "👗", "dress"),
    ("정장 셔츠",     "TOP",       "👔", "shirt-formal"),
    ("청바지",       "BOTTOM",    "👖", "jeans"),
    ("면바지",       "BOTTOM",    "👖", "pants-cotton"),
    ("면 반바지",     "BOTTOM",    "🩳", "shorts"),
    ("플리츠 스커트", "BOTTOM",    "👗", "skirt-pleated"),
    ("정장 바지",     "BOTTOM",    "👖", "pants-formal"),
    ("패딩 점퍼",     "OUTER",     "🧥", "padding"),
    ("코트",         "OUTER",     "🧥", "coat"),
    ("트렌치코트",    "OUTER",     "🧥", "trenchcoat"),
    ("야상",         "OUTER",     "🧥", "field-jacket"),
    ("카디건",       "OUTER",     "🧥", "cardigan"),
    ("우비",         "OUTER",     "🧥", "raincoat"),
    ("운동화",       "SHOES",     "👟", "sneakers"),
    ("구두",         "SHOES",     "👞", "dress-shoes"),
    ("발열 양말",     "SOCKS",     "🧦", "socks-thermal"),
    ("우산",         "ACCESSORY", "🌂", "umbrella"),
    ("캡모자",       "ACCESSORY", "🧢", "cap"),
    ("비니",         "ACCESSORY", "🧢", "beanie"),
    ("목도리",       "ACCESSORY", "🧣", "scarf"),
    ("장갑",         "ACCESSORY", "🧤", "gloves"),
]

SVG_TEMPLATE = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" width="200" height="200">
  <rect width="200" height="200" rx="20" fill="{bg}"/>
  <text x="100" y="120" font-size="86" text-anchor="middle" font-family="Apple Color Emoji, Segoe UI Emoji, Noto Color Emoji, sans-serif">{emoji}</text>
  <text x="100" y="170" font-size="18" text-anchor="middle" fill="#333" font-weight="500" font-family="Apple SD Gothic Neo, sans-serif">{name}</text>
</svg>
"""


def main() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    for name, category, emoji, slug in CLOTHES:
        svg = SVG_TEMPLATE.format(bg=CATEGORY_BG[category], emoji=emoji, name=name)
        (OUT_DIR / f"{slug}.svg").write_text(svg, encoding="utf-8")
    print(f"generated {len(CLOTHES)} SVGs at {OUT_DIR}")


if __name__ == "__main__":
    main()
