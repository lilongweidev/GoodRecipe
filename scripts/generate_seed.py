import json
import os

categories = [
    {
        "name": "Breakfast",
        "display": "早餐",
        "prefix": "阳光早餐",
        "ingredients": ["鸡蛋", "牛奶", "全麦面包"],
        "steps": [
            "将主要食材切好",
            "烧热煎锅，加入少许油",
            "煎至两面金黄即可"
        ],
        "tags": ["清爽", "快速", "蛋白质"],
        "notes": "为一天提供稳定能量。",
        "tips": "提前准备食材可缩短早晨时间。"
    },
    {
        "name": "Lunch",
        "display": "午餐",
        "prefix": "午间能量",
        "ingredients": ["鸡胸肉", "糙米", "蔬菜"],
        "steps": [
            "将鸡胸肉切块腌制",
            "糙米提前泡水",
            "与蔬菜一同翻炒或蒸熟"
        ],
        "tags": ["高蛋白", "低脂", "平衡"],
        "notes": "适合需要保持精力的午间时段。",
        "tips": "用蒸的方式锁住水分。"
    },
    {
        "name": "Dinner",
        "display": "晚餐",
        "prefix": "夜间精致",
        "ingredients": ["三文鱼", "杏鲍菇", "芝士"],
        "steps": [
            "鱼肉稍微腌制",
            "烤箱或平底锅加热",
            "稍微烤/煎至熟即可"
        ],
        "tags": ["温和", "养胃", "蛋白质"],
        "notes": "适合轻食但饱足感强的夜晚。",
        "tips": "不宜过多调味。"
    },
    {
        "name": "Dessert",
        "display": "甜点",
        "prefix": "甜蜜时刻",
        "ingredients": ["草莓", "酸奶", "燕麦"],
        "steps": [
            "将水果切块",
            "混合酸奶及燕麦",
            "冷藏后食用"
        ],
        "tags": ["轻甜", "水果", "可提前准备"],
        "notes": "适合饭后或下午茶轻松享用。",
        "tips": "水果根据季节更换。"
    },
    {
        "name": "Snack",
        "display": "小吃",
        "prefix": "闲暇小咸",
        "ingredients": ["玉米", "芝士", "黑胡椒"],
        "steps": [
            "玉米粒清洗",
            "加调料拌匀",
            "烤箱或锅热后烘烤"
        ],
        "tags": ["解馋", "分享", "香脆"],
        "notes": "适合看剧或聚会共享。",
        "tips": "提前腌制可以让味道更入味。"
    },
    {
        "name": "Soup",
        "display": "汤品",
        "prefix": "滋补汤",
        "ingredients": ["排骨", "萝卜", "枸杞"],
        "steps": [
            "材料焯水",
            "与香料一同炖煮",
            "加盐调味"
        ],
        "tags": ["养胃", "滋补", "低温慢炖"],
        "notes": "充满暖意的家庭滋补。",
        "tips": "用小火慢炖保留营养。"
    },
    {
        "name": "Salad",
        "display": "沙拉",
        "prefix": "彩虹沙律",
        "ingredients": ["生菜", "樱桃番茄", "鳄梨"],
        "steps": [
            "蔬菜洗净",
            "切块摆盘",
            "淋上酱汁"
        ],
        "tags": ["清爽", "低卡", "健康"],
        "notes": "下午/晚餐前的小轻快。",
        "tips": "酱汁可自调为橄榄油加柠檬。"
    }
]

recipes = []

for category in categories:
    base_ingredients = category["ingredients"]
    for idx in range(1, 21):
        title = f"{category['prefix']} {idx}"
        description = f"{category['display']}主题第 {idx} 款，搭配{category['tags'][0]}风味。"
        ingredients = base_ingredients + [f"{category['display']}专属材料 {idx}"]
        steps = [
            *category["steps"],
            f"装盘时撒上香草或坚果。"
        ]
        recipe = {
            "title": title,
            "description": description,
            "ingredients": ingredients,
            "steps": steps,
            "category": category["display"],
            "cookTimeMinutes": 10 + idx,
            "servings": (idx % 4) + 1,
            "imageUrl": f"https://example.com/images/{category['name'].lower()}_{idx}.jpg",
            "isFavorite": idx % 5 == 0,
            "tags": category["tags"],
            "nutritionalNotes": f"{category['notes']} 推荐搭配一杯温水。",
            "preparationTips": f"{category['tips']} (版本 {idx})"
        }
        recipes.append(recipe)

output_path = os.path.join("app", "src", "main", "res", "raw", "good_recipe_seed.json")
with open(output_path, "w", encoding="utf-8") as f:
    json.dump(recipes, f, ensure_ascii=False, indent=2)
print(f"生成 {len(recipes)} 条菜谱数据 -> {output_path}")
