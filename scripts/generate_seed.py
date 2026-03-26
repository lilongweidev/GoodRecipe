import json
import os
from typing import Dict, List

CATEGORY_SLUGS = {
    "早餐": "breakfast",
    "午餐": "lunch",
    "晚餐": "dinner",
    "甜点": "dessert",
    "小吃": "snack",
    "汤品": "soup",
    "沙拉": "salad"
}

category_blueprints = {
    "早餐": {
        "prefixes": [
            {"text": "金黄", "slug": "golden", "extraIngredient": "枫糖", "flavor": "金黄", "tags": ["金黄"]},
            {"text": "草本", "slug": "herbal", "extraIngredient": "香草", "flavor": "草本", "tags": ["草本"]},
            {"text": "香甜", "slug": "sweet", "extraIngredient": "果酱", "flavor": "香甜", "tags": ["甜香"]},
            {"text": "微辣", "slug": "spicy", "extraIngredient": "辣椒片", "flavor": "微辣", "tags": ["微辣"]}
        ],
        "cores": [
            {
                "slug": "avocado-toast",
                "name": "牛油果水波蛋吐司",
                "mainIngredients": ["全麦吐司", "牛油果", "鸡蛋"],
                "ingredients": ["全麦吐司", "牛油果", "鸡蛋", "柠檬", "橄榄油", "黑胡椒", "芝麻"],
                "seasonings": ["黑胡椒", "柠檬"],
                "tags": ["健康", "轻盈"],
                "cookTime": 15,
                "servings": 1,
                "method": "平底锅",
                "nutritionalNotesTemplate": "{main} 提供碳水，{main2} 带来健康脂肪，{main3} 补足蛋白。",
                "preparationTipsTemplate": "吐司烤至金黄再抹 {main2}，{method} 上煎 {main3} 时用小火。"
            },
            {
                "slug": "oat-banana-pancake",
                "name": "燕麦香蕉煎饼",
                "mainIngredients": ["燕麦", "香蕉", "鸡蛋"],
                "ingredients": ["燕麦", "香蕉", "鸡蛋", "牛奶", "香草粉", "肉桂"],
                "seasonings": ["肉桂", "香草粉"],
                "tags": ["高纤", "天然甜"],
                "cookTime": 20,
                "servings": 2,
                "method": "平底锅",
                "nutritionalNotesTemplate": "{main} 富含纤维，{main2} 让天然甜味，{main3} 补充蛋白。",
                "preparationTipsTemplate": "面糊不要过度搅拌，{main3} 以小火慢煎出金边。"
            },
            {
                "slug": "mushroom-frittata",
                "name": "蘑菇烤蛋蛋饼",
                "mainIngredients": ["鸡蛋", "蘑菇", "洋葱"],
                "ingredients": ["鸡蛋", "蘑菇", "洋葱", "奶油", "欧芹", "奶酪"],
                "seasonings": ["黑胡椒", "欧芹"],
                "tags": ["高蛋白", "暖胃"],
                "cookTime": 15,
                "servings": 1,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main} 与 {main2} 协作，{main3} 补充植物纤维。",
                "preparationTipsTemplate": "先把 {main2} 与 {main3} 炒至上色，再倒入 {main} 混合烘烤。"
            },
            {
                "slug": "ricotta-toast",
                "name": "蜂蜜瑞可塔吐司",
                "mainIngredients": ["瑞可塔", "吐司", "蜂蜜"],
                "ingredients": ["瑞可塔", "吐司", "蜂蜜", "柠檬皮", "坚果碎"],
                "seasonings": ["柠檬皮", "坚果"],
                "tags": ["甜点感", "聚会"],
                "cookTime": 10,
                "servings": 2,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main} 与 {main2} 提供蛋白与碳水，{main3} 为天然甜。",
                "preparationTipsTemplate": "吐司烤香后抹厚层瑞可塔，淋上 {main3} 再撒 {seasoning}。"
            },
            {
                "slug": "tomato-tofu",
                "name": "番茄煎豆腐",
                "mainIngredients": ["豆腐", "番茄", "香草"],
                "ingredients": ["豆腐", "番茄", "香草", "橄榄油", "芝士", "醋"],
                "seasonings": ["香草", "黑胡椒"],
                "tags": ["清爽", "低脂"],
                "cookTime": 15,
                "servings": 1,
                "method": "煎锅",
                "nutritionalNotesTemplate": "{main} 蛋白质丰富，{main2} 的番茄酸甜与 {main3} 清香。",
                "preparationTipsTemplate": "豆腐先煎至微脆，再盖上 {main2} 与 {main3} 小火焖至入味。"
            }
        ],
        "actionTemplates": [
            "{prefix} 的 {seasoning} 与 {extra} 先抹在 {main} 上，搭配 {main2} 和 {main3}。",
            "用 {method} 慢慢处理主材，让 {core_name} 的香气渗透。",
            "装盘后再滴几滴柠檬或香草，撒上 {extra} 或坚果完成。"
        ]
    },

    "午餐": {
        "prefixes": [
            {"text": "蜜香", "slug": "honey", "extraIngredient": "蜂蜜", "flavor": "蜜汁", "tags": ["甜香"]},
            {"text": "烟熏", "slug": "smoked", "extraIngredient": "熏红椒粉", "flavor": "烟熏", "tags": ["烟熏"]},
            {"text": "柠爽", "slug": "citrus", "extraIngredient": "柚子皮", "flavor": "柠香", "tags": ["清爽"]},
            {"text": "香辣", "slug": "spicy", "extraIngredient": "辣椒酱", "flavor": "香辣", "tags": ["辣味"]}
        ],
        "cores": [
            {
                "slug": "lemon-garlic-chicken",
                "name": "柠香蒜烤鸡",
                "mainIngredients": ["鸡胸肉", "柠檬", "大蒜"],
                "ingredients": ["鸡胸肉", "柠檬", "大蒜", "橄榄油", "迷迭香", "黑胡椒", "海盐"],
                "seasonings": ["迷迭香", "黑胡椒"],
                "tags": ["高蛋白", "低脂"],
                "cookTime": 30,
                "servings": 2,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main} 低脂高蛋白，{main2} 的柠香与 {main3} 的蒜香让整体清爽不腻。",
                "preparationTipsTemplate": "先用 {seasoning} 将 {main} 腌 15 分钟，再用 {method} 慢烤可锁住肉汁。"
            },
            {
                "slug": "cream-mushroom-pasta",
                "name": "奶油野菌意面",
                "mainIngredients": ["意大利面", "野生蘑菇", "帕玛森"],
                "ingredients": ["意大利面", "野生蘑菇", "帕玛森", "黄油", "淡奶油", "大蒜", "欧芹", "白葡萄酒"],
                "seasonings": ["黑胡椒", "欧芹"],
                "tags": ["奶香", "舒适"],
                "cookTime": 25,
                "servings": 2,
                "method": "煮锅",
                "nutritionalNotesTemplate": "{main} 提供碳水，{main2} 与 {main3} 补充膳食纤维与钙，适合中午补能。",
                "preparationTipsTemplate": "煮 {main} 时保留 1 杯面汤，后与酱汁拌匀可防止分离。"
            },
            {
                "slug": "mexican-beef-bowl",
                "name": "墨西哥牛肉玉米碗",
                "mainIngredients": ["碎牛肉", "玉米", "鳄梨"],
                "ingredients": ["碎牛肉", "玉米粒", "黑豆", "洋葱", "红椒", "鳄梨", "切达芝士", "酸奶油"],
                "seasonings": ["孜然粉", "辣椒粉"],
                "tags": ["异国", "高蛋白"],
                "cookTime": 30,
                "servings": 2,
                "method": "煎锅",
                "nutritionalNotesTemplate": "{main} 与 {main2} 提供蛋白与纤维，{main3} 补充健康脂肪。",
                "preparationTipsTemplate": "煎 {main} 时火候要略焦，加入 {seasoning} 后再与 {main2} 拌匀更香。"
            },
            {
                "slug": "mediterranean-quinoa",
                "name": "地中海烤菜藜麦",
                "mainIngredients": ["藜麦", "彩椒", "樱桃番茄"],
                "ingredients": ["藜麦", "彩椒", "樱桃番茄", "茄子", "橄榄油", "柠檬", "羊乳酪", "烤南瓜籽"],
                "seasonings": ["牛至", "迷迭香"],
                "tags": ["植物蛋白", "清爽"],
                "cookTime": 35,
                "servings": 2,
                "method": "烤盘",
                "nutritionalNotesTemplate": "{main} 提供完整蛋白，{main2} 与 {main3} 补充抗氧化与维生素。",
                "preparationTipsTemplate": "先把蔬菜烤至微焦，再与 {main} 拌匀，最后挤入柠檬提亮。"
            },
            {
                "slug": "spiced-tomato-pork",
                "name": "香料番茄炖猪肉",
                "mainIngredients": ["猪肩肉", "番茄", "洋葱"],
                "ingredients": ["猪肩肉", "番茄罐头", "洋葱", "红椒", "蒜", "红酒", "香叶", "胡萝卜"],
                "seasonings": ["肉桂", "辣椒片"],
                "tags": ["慢炖", "家常"],
                "cookTime": 90,
                "servings": 4,
                "method": "炖锅",
                "nutritionalNotesTemplate": "{main} 富含胶原蛋白，{main2} 与 {main3} 给汤汁带来天然酸甜。",
                "preparationTipsTemplate": "先煎 {main} 上色后再放入 {method} 慢炖，最后调入 {seasoning} 层次更丰富。"
            }
        ],
        "actionTemplates": [
            "{prefix} 的调味与 {seasoning} 先抹在 {main} 表面，再把 {extra} 轻抹进缝隙腌 15 分钟。",
            "加热 {method}，将 {main} 与 {main2} 一起烹饪，让 {core_name} 很好释放肉汁。",
            "出锅前撒上 {main3} 与香草，搭配柠檬或香醋拌匀即可。"
        ]
    },
    "晚餐": {
        "prefixes": [
            {"text": "慢炖", "slug": "slow-braise", "extraIngredient": "红酒", "flavor": "慢炖", "tags": ["慢火"]},
            {"text": "焦香", "slug": "caramel", "extraIngredient": "焦糖", "flavor": "焦香", "tags": ["焦香"]},
            {"text": "香草", "slug": "herb", "extraIngredient": "百里香", "flavor": "香草", "tags": ["香草"]},
            {"text": "酥脆", "slug": "crispy", "extraIngredient": "面包屑", "flavor": "酥皮", "tags": ["酥脆"]}
        ],
        "cores": [
            {
                "slug": "red-wine-braise-beef",
                "name": "红酒炖牛腩",
                "mainIngredients": ["牛腩", "红酒", "胡萝卜"],
                "ingredients": ["牛腩", "红酒", "胡萝卜", "洋葱", "西芹", "番茄酱", "香叶", "牛高汤"],
                "seasonings": ["香叶", "迷迭香"],
                "tags": ["暖胃", "浓郁"],
                "cookTime": 120,
                "servings": 4,
                "method": "慢炖锅",
                "nutritionalNotesTemplate": "{main} 含铁量高，搭配 {main2} 的多酚与 {main3} 的纤维适合晚餐补血。",
                "preparationTipsTemplate": "先高温煎 {main} 再入 {method} 与 {seasoning} 慢炖，留意液体不要干掉。"
            },
            {
                "slug": "coconut-curry-shrimp",
                "name": "椰香咖喱虾",
                "mainIngredients": ["大虾", "椰奶", "咖喱酱"],
                "ingredients": ["大虾", "椰奶", "咖喱酱", "洋葱", "姜", "青柠", "辣椒", "香菜"],
                "seasonings": ["姜", "香菜"],
                "tags": ["椰香", "异域"],
                "cookTime": 30,
                "servings": 2,
                "method": "煎锅",
                "nutritionalNotesTemplate": "{main} 提供优质蛋白，{main2} 的脂肪让咖喱更顺滑，{main3} 补充香料。",
                "preparationTipsTemplate": "先炒香姜蒜与 {main3}，再加入 {main} 与 {main2} 轻煮，保留虾的弹性。"
            },
            {
                "slug": "pepper-lamb-chops",
                "name": "铁板黑椒羊排",
                "mainIngredients": ["羊排", "黑胡椒", "蒜"],
                "ingredients": ["羊排", "黑胡椒粒", "蒜", "迷迭香", "橄榄油", "牛油", "红酒醋"],
                "seasonings": ["黑胡椒", "迷迭香"],
                "tags": ["聚餐", "厚切"],
                "cookTime": 20,
                "servings": 2,
                "method": "铁板",
                "nutritionalNotesTemplate": "{main} 含优质脂肪与蛋白，{main2} 和 {main3} 提供香气与暖感。",
                "preparationTipsTemplate": "先撒入粗磨 {seasoning}，中火煎 {main} 保留粉红，再以 {method} 复热出汁。"
            },
            {
                "slug": "korean-spicy-pork-belly",
                "name": "韩式辣酱五花",
                "mainIngredients": ["五花肉", "韩式辣酱", "白芝麻"],
                "ingredients": ["五花肉", "韩式辣酱", "洋葱", "蒜", "白芝麻", "蜂蜜", "葱"],
                "seasonings": ["韩式辣酱", "白芝麻"],
                "tags": ["辣味", "下饭"],
                "cookTime": 35,
                "servings": 3,
                "method": "炒锅",
                "nutritionalNotesTemplate": "{main} 的油脂搭配 {main2} 的甜辣，{main3} 增加香气与口感。",
                "preparationTipsTemplate": "先把 {main} 脂肪煎出，再加入 {main2} 小火慢炖收汁，最后撒 {main3}。"
            },
            {
                "slug": "citrus-baked-cod",
                "name": "香柠烤鳕鱼",
                "mainIngredients": ["鳕鱼", "柠檬", "香草"],
                "ingredients": ["鳕鱼", "柠檬", "香草", "橄榄油", "奶油", "白葡萄酒", "粗盐"],
                "seasonings": ["香草", "黑胡椒"],
                "tags": ["清爽", "海鲜"],
                "cookTime": 25,
                "servings": 2,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main} 是低脂蛋白，{main2} 与 {main3} 提供酸度与香气。",
                "preparationTipsTemplate": "中途淋一次融化奶油，{main2} 的汁液更平均渗入。"
            }
        ],
        "actionTemplates": [
            "{prefix} 的调味与 {seasoning} 先与 {main} 结合，确保 {extra} 在表皮渗入香气。",
            "将 {main} 与 {main2} 放入 {method}，维持中低温让 {core_name} 充分融合。",
            "出锅前将 {main3} 与柠檬或香草点缀，搭配酒香或烤汁享用。"
        ]
    },
    "甜点": {
        "prefixes": [
            {"text": "绵密", "slug": "silky", "extraIngredient": "玉米淀粉", "flavor": "绵密", "tags": ["丝滑"]},
            {"text": "轻盈", "slug": "airy", "extraIngredient": "蛋白", "flavor": "轻盈", "tags": ["轻盈"]},
            {"text": "焦糖", "slug": "caramelized", "extraIngredient": "焦糖酱", "flavor": "焦糖", "tags": ["焦糖"]},
            {"text": "荔枝", "slug": "lychee", "extraIngredient": "荔枝糖浆", "flavor": "荔枝", "tags": ["果香"]}
        ],
        "cores": [
            {
                "slug": "caramel-pudding",
                "name": "焦糖布丁",
                "mainIngredients": ["牛奶", "鸡蛋", "糖"],
                "ingredients": ["牛奶", "鸡蛋", "糖", "香草豆荚", "淡奶油"],
                "seasonings": ["香草", "糖"],
                "tags": ["经典", "顺滑"],
                "cookTime": 40,
                "servings": 4,
                "method": "蒸锅",
                "nutritionalNotesTemplate": "{main} 与 {main2} 提供钙与蛋白，{main3} 造就焦香。",
                "preparationTipsTemplate": "小火蒸可避免气泡，冷藏后再倒扣让焦糖更亮。"
            },
            {
                "slug": "lemon-tart",
                "name": "柠檬挞",
                "mainIngredients": ["黄油", "柠檬", "鸡蛋"],
                "ingredients": ["黄油", "高筋面粉", "糖粉", "柠檬", "鸡蛋", "淡奶油"],
                "seasonings": ["柠檬皮", "香草"],
                "tags": ["清新", "茶点"],
                "cookTime": 35,
                "servings": 6,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main} 与 {main2} 让塔皮酥脆，{main3} 的酸度点亮味蕾。",
                "preparationTipsTemplate": "烤前用叉子叉洞后铺烘豆定型，可保持塔皮形状。"
            },
            {
                "slug": "earl-grey-mousse",
                "name": "伯爵奶冻慕斯",
                "mainIngredients": ["伯爵茶", "奶酪", "淡奶油"],
                "ingredients": ["伯爵茶", "奶酪", "淡奶油", "吉利丁", "蜂蜜", "柠檬皮"],
                "seasonings": ["蜂蜜", "柠檬皮"],
                "tags": ["优雅", "茶香"],
                "cookTime": 20,
                "servings": 4,
                "method": "冷藏",
                "nutritionalNotesTemplate": "{main2} 与 {main3} 提供奶香，{main} 带来抗氧化。",
                "preparationTipsTemplate": "茶汤泡浓后冷却再拌入奶酪以防苦涩。"
            },
            {
                "slug": "black-sesame-panna-cotta",
                "name": "黑芝麻奶冻",
                "mainIngredients": ["黑芝麻", "牛奶", "吉利丁"],
                "ingredients": ["黑芝麻", "牛奶", "吉利丁", "糖", "香草", "椰奶"],
                "seasonings": ["黑芝麻", "糖"],
                "tags": ["养生", "黑色料理"],
                "cookTime": 15,
                "servings": 4,
                "method": "冰箱",
                "nutritionalNotesTemplate": "{main} 富含钙铁，{main2} 融合椰奶让质地顺滑。",
                "preparationTipsTemplate": "将 {main} 研磨细腻再过筛，可避免颗粒感。"
            },
            {
                "slug": "raspberry-mille-feuille",
                "name": "覆盆子千层",
                "mainIngredients": ["酥皮", "覆盆子", "香草卡仕达"],
                "ingredients": ["酥皮", "覆盆子", "香草卡仕达", "糖粉", "柠檬皮"],
                "seasonings": ["糖粉", "覆盆子汁"],
                "tags": ["典雅", "层次"],
                "cookTime": 30,
                "servings": 4,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main3} 有蛋白与钙，{main2} 带来酸度，{main} 口感脆。",
                "preparationTipsTemplate": "烤酥皮时用重物压着防鼓起，出炉后趁热切块再夹奶油。"
            }
        ],
        "actionTemplates": [
            "{prefix} 的香气与 {seasoning} 与 {main} 和 {main2} 搅匀，缓缓加入 {extra} 让甜味柔和。",
            "依据 {method} 或冷藏流程处理，避免 {core_name} 产生气泡。",
            "完成后撒上 {main3} 与糖粉，冷藏定形后再分份。"
        ]
    },
    "小吃": {
        "prefixes": [
            {"text": "花椒", "slug": "szechuan", "extraIngredient": "花椒粉", "flavor": "麻香", "tags": ["麻辣"]},
            {"text": "奶酪", "slug": "cheesy", "extraIngredient": "帕玛森", "flavor": "奶香", "tags": ["奶香"]},
            {"text": "香烤", "slug": "roasted", "extraIngredient": "香料油", "flavor": "香烤", "tags": ["烤香"]},
            {"text": "薄荷", "slug": "mint", "extraIngredient": "薄荷叶", "flavor": "清凉", "tags": ["清爽"]}
        ],
        "cores": [
            {
                "slug": "rosemary-nuts",
                "name": "迷迭香烤坚果",
                "mainIngredients": ["坚果", "迷迭香", "海盐"],
                "ingredients": ["核桃", "杏仁", "腰果", "迷迭香", "海盐", "橄榄油", "蜂蜜"],
                "seasonings": ["迷迭香", "海盐"],
                "tags": ["健康", "可携带"],
                "cookTime": 20,
                "servings": 4,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main} 提供健康油脂，搭配 {main2} 抗氧化与 {main3} 让口感立体。",
                "preparationTipsTemplate": "烤至香气四溢再撒盐，可让表面略带脆。"
            },
            {
                "slug": "spiced-tofu-bites",
                "name": "椒盐豆腐干",
                "mainIngredients": ["豆腐干", "花椒", "辣椒"],
                "ingredients": ["豆腐干", "花椒", "辣椒粉", "五香粉", "生抽", "香油"],
                "seasonings": ["花椒", "五香"],
                "tags": ["下酒", "麻辣"],
                "cookTime": 15,
                "servings": 3,
                "method": "煎锅",
                "nutritionalNotesTemplate": "{main} 富含植物蛋白，{main2} 与 {main3} 增添麻辣刺激。",
                "preparationTipsTemplate": "煎前先挤干水分，撒 {seasoning} 让香料贴合。"
            },
            {
                "slug": "smoked-cheese-tomato",
                "name": "烟熏奶酪番茄",
                "mainIngredients": ["樱桃番茄", "烟熏奶酪", "香草"],
                "ingredients": ["樱桃番茄", "烟熏奶酪", "罗勒", "黑胡椒", "橄榄油", "香醋"],
                "seasonings": ["黑胡椒", "香草"],
                "tags": ["精致", "开胃"],
                "cookTime": 10,
                "servings": 4,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main2} 与 {main} 搭配让脂肪与维生素互补，{main3} 提香。",
                "preparationTipsTemplate": "微烤后淋橄榄油与香醋，趁热撒 {seasoning} 提味。"
            },
            {
                "slug": "sesame-nori-roll",
                "name": "芝麻海苔卷",
                "mainIngredients": ["海苔", "芝麻", "小黄瓜"],
                "ingredients": ["海苔片", "白芝麻", "小黄瓜", "胡萝卜丝", "醋", "酱油"],
                "seasonings": ["芝麻", "酱油"],
                "tags": ["轻食", "爽脆"],
                "cookTime": 10,
                "servings": 2,
                "method": "卷制",
                "nutritionalNotesTemplate": "{main} 富含微量元素，{main2} 与 {main3} 增添咀嚼感。",
                "preparationTipsTemplate": "醋略腌小黄瓜，卷制时用湿布按紧可防散。"
            },
            {
                "slug": "curry-eel-chips",
                "name": "咖喱鳗鱼薄片",
                "mainIngredients": ["鳗鱼", "咖喱粉", "椰片"],
                "ingredients": ["鳗鱼片", "咖喱粉", "椰片", "盐", "黑胡椒", "柠檬"],
                "seasonings": ["咖喱粉", "黑胡椒"],
                "tags": ["创意", "香脆"],
                "cookTime": 20,
                "servings": 3,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main} 提供欧米伽-3，{main2} 与 {main3} 加强香气与食感。",
                "preparationTipsTemplate": "薄片先刷油再撒 {seasoning}，高温快速烤脆。"
            }
        ],
        "actionTemplates": [
            "{prefix} 的香料与 {seasoning} 夹在 {main} 与 {main2} 上，{extra} 让层次更丰富。",
            "把 {main3} 加入，用 {method} 保持酥脆口感或卷制成型。",
            "出炉后撒上剩余调料与香草即可享用。"
        ]
    },
    "汤品": {
        "prefixes": [
            {"text": "浓郁", "slug": "rich", "extraIngredient": "奶油", "flavor": "浓郁", "tags": ["浓汤"]},
            {"text": "清新", "slug": "fresh", "extraIngredient": "柠檬皮", "flavor": "清新", "tags": ["清爽"]},
            {"text": "药草", "slug": "herbal", "extraIngredient": "香菜", "flavor": "药草", "tags": ["香草"]},
            {"text": "奶香", "slug": "milky", "extraIngredient": "奶酪", "flavor": "奶香", "tags": ["奶香"]}
        ],
        "cores": [
            {
                "slug": "pumpkin-cream-soup",
                "name": "南瓜浓汤",
                "mainIngredients": ["南瓜", "椰奶", "洋葱"],
                "ingredients": ["南瓜", "洋葱", "椰奶", "土豆", "奶油", "鸡汤", "肉桂粉"],
                "seasonings": ["肉桂粉", "黑胡椒"],
                "tags": ["暖胃", "浓郁"],
                "cookTime": 40,
                "servings": 4,
                "method": "汤锅",
                "nutritionalNotesTemplate": "{main} 富含 β-胡萝卜素，{main2} 与 {main3} 增加顺滑口感与维生素。",
                "preparationTipsTemplate": "先将 {main} 与 {main3} 炒软，再加入鸡汤慢煮，最后用搅拌棒打细。"
            },
            {
                "slug": "tomato-oxtail-soup",
                "name": "番茄牛尾汤",
                "mainIngredients": ["牛尾", "番茄", "西芹"],
                "ingredients": ["牛尾", "番茄", "西芹", "胡萝卜", "洋葱", "番茄酱", "香叶"],
                "seasonings": ["香叶", "黑胡椒"],
                "tags": ["滋补", "酸甜"],
                "cookTime": 150,
                "servings": 6,
                "method": "慢炖锅",
                "nutritionalNotesTemplate": "{main} 富含胶原，{main2} 和 {main3} 让汤更耐喝。",
                "preparationTipsTemplate": "炖前先汆水去血沫，再用香料慢炖 2 小时。"
            },
            {
                "slug": "miso-tofu-soup",
                "name": "味噌豆腐汤",
                "mainIngredients": ["味噌", "嫩豆腐", "海带"],
                "ingredients": ["味噌", "嫩豆腐", "海带", "豆皮", "青葱", "柴鱼高汤"],
                "seasonings": ["味噌", "柴鱼高汤"],
                "tags": ["日式", "清爽"],
                "cookTime": 15,
                "servings": 2,
                "method": "小锅",
                "nutritionalNotesTemplate": "{main} 含益生菌，{main2} 与 {main3} 提供植物蛋白与矿物质。",
                "preparationTipsTemplate": "不要把味噌直接煮沸，出锅前再调入保持鲜味。"
            },
            {
                "slug": "chicken-corn-soup",
                "name": "鸡茸玉米羹",
                "mainIngredients": ["鸡胸肉", "玉米", "鸡汤"],
                "ingredients": ["鸡胸肉", "玉米粒", "鸡汤", "蛋白", "玉米淀粉", "葱花"],
                "seasonings": ["白胡椒", "葱花"],
                "tags": ["顺滑", "家常"],
                "cookTime": 20,
                "servings": 3,
                "method": "汤锅",
                "nutritionalNotesTemplate": "{main} 低脂，{main2} 补充纤维，{main3} 让味道更浓。",
                "preparationTipsTemplate": "鸡胸肉切小丁后与玉米一起打碎，最后慢慢倒入蛋白做花。"
            },
            {
                "slug": "clam-white-wine-soup",
                "name": "蛤蜊白酒汤",
                "mainIngredients": ["蛤蜊", "白酒", "蒜"],
                "ingredients": ["蛤蜊", "白酒", "蒜", "洋葱", "奶油", "香菜", "鸡汤"],
                "seasonings": ["蒜", "香菜"],
                "tags": ["海味", "清爽"],
                "cookTime": 20,
                "servings": 2,
                "method": "深锅",
                "nutritionalNotesTemplate": "{main} 富含碘，{main2} 提高风味，{main3} 让汤香更圆润。",
                "preparationTipsTemplate": "蛤蜊先吐沙再与 {main2} 同煮，最后撒香菜提味。"
            }
        ],
        "actionTemplates": [
            "{prefix} 的 {seasoning} 与 {main} 先入锅翻炒或焯水，再加入 {main2}、{main3} 与高汤。",
            "把锅移到 {method}，保持小火让香气慢慢释放，{core_name} 中的材料软糯。",
            "最后用 {extra} 或香草点缀，轻轻搅动让汤更浓稠。"
        ]
    },
    "沙拉": {
        "prefixes": [
            {"text": "缤纷", "slug": "colorburst", "extraIngredient": "红椒丁", "flavor": "缤纷", "tags": ["色彩"]},
            {"text": "烟熏", "slug": "smoked", "extraIngredient": "烟熏粉", "flavor": "烟熏", "tags": ["烟熏"]},
            {"text": "清爽", "slug": "bright", "extraIngredient": "薄荷汁", "flavor": "清爽", "tags": ["清爽"]},
            {"text": "饱满", "slug": "hearty", "extraIngredient": "烤南瓜籽", "flavor": "饱满", "tags": ["饱腹"]}
        ],
        "cores": [
            {
                "slug": "roasted-beet-goat-cheese",
                "name": "烤甜菜山羊奶酪",
                "mainIngredients": ["甜菜", "山羊奶酪", "核桃"],
                "ingredients": ["甜菜", "山羊奶酪", "核桃", "芝麻菜", "橄榄油", "蜂蜜", "香醋"],
                "seasonings": ["蜂蜜", "香醋"],
                "tags": ["色彩", "甜点感"],
                "cookTime": 30,
                "servings": 2,
                "method": "烤箱",
                "nutritionalNotesTemplate": "{main} 富含抗氧化，{main2} 与 {main3} 提供蛋白与油脂。",
                "preparationTipsTemplate": "把 {main} 先烤软再切片，趁热拌入 {main2} 和微甜的蜂蜜。"
            },
            {
                "slug": "thai-papaya-salad",
                "name": "泰式青木瓜",
                "mainIngredients": ["青木瓜", "花生", "干辣椒"],
                "ingredients": ["青木瓜", "花生", "干辣椒", "鱼露", "柠檬", "棕榈糖"],
                "seasonings": ["鱼露", "柠檬汁"],
                "tags": ["泰式", "酸辣"],
                "cookTime": 15,
                "servings": 2,
                "method": "拌盘",
                "nutritionalNotesTemplate": "{main} 蛋白质低但富含酶，{main2} 补充好脂肪，{main3} 给口感和辣度。",
                "preparationTipsTemplate": "拍碎柠檬与糖再拌入青木瓜，最后撒 {main2} 与香草。"
            },
            {
                "slug": "citrus-quinoa-salad",
                "name": "柑橘藜麦沙拉",
                "mainIngredients": ["藜麦", "柑橘", "薄荷"],
                "ingredients": ["藜麦", "柑橘", "薄荷", "红洋葱", "橄榄油", "芝麻籽"],
                "seasonings": ["薄荷", "橄榄油"],
                "tags": ["清爽", "植物蛋白"],
                "cookTime": 20,
                "servings": 2,
                "method": "冷藏",
                "nutritionalNotesTemplate": "{main} 粗粮蛋白，{main2} 富含维生素 C，{main3} 提供清新香气。",
                "preparationTipsTemplate": "藜麦冷却后与柑橘片拌匀，撒上薄荷与少量橄榄油定型。"
            },
            {
                "slug": "smoked-salmon-caesar",
                "name": "烟熏三文鱼凯撒",
                "mainIngredients": ["烟熏三文鱼", "罗马生菜", "帕玛森"],
                "ingredients": ["烟熏三文鱼", "罗马生菜", "帕玛森", "面包丁", "凯撒酱", "柠檬"],
                "seasonings": ["黑胡椒", "柠檬皮"],
                "tags": ["经典", "蛋白"],
                "cookTime": 15,
                "servings": 2,
                "method": "拌盘",
                "nutritionalNotesTemplate": "{main1} 与 {main3} 提供优质蛋白与钙，{main2} 补充纤维与爽脆。",
                "preparationTipsTemplate": "趁鲜切片三文鱼，轻拌罗马生菜与凯撒酱避免出水。"
            },
            {
                "slug": "avocado-chia-bowl",
                "name": "牛油果奇亚碗",
                "mainIngredients": ["牛油果", "奇亚籽", "樱桃番茄"],
                "ingredients": ["牛油果", "奇亚籽", "樱桃番茄", "玉米片", "蜂蜜", "酸奶"],
                "seasonings": ["蜂蜜", "酸奶"],
                "tags": ["网红", "能量"],
                "cookTime": 10,
                "servings": 1,
                "method": "组合",
                "nutritionalNotesTemplate": "{main} 提供健康脂肪，{main2} 补充纤维，{main3} 补充维生素。",
                "preparationTipsTemplate": "先泡发奇亚籽再铺上牛油果与番茄，蜂蜜与酸奶调和。"
            }
        ],
        "actionTemplates": [
            "{prefix} 的 {seasoning} 与 {extra} 油醋调成汁，淋在 {main} 与 {main2} 上。",
            "加入 {main3}、坚果或烤菜，轻轻翻拌保持食材完整。",
            "装盘后再点缀香草或籽类，让 {core_name} 呈现清新层次。"
        ]
    }
}


def format_list(items: List[str]) -> str:
    return "、".join(items)


def build_context(prefix: Dict[str, str], core: Dict[str, object]) -> Dict[str, str]:
    mains = core["mainIngredients"]
    main = mains[0]
    main2 = mains[1] if len(mains) > 1 else main
    main3 = mains[2] if len(mains) > 2 else main2
    seasonings = core.get("seasonings", [])
    seasoning = seasonings[0] if seasonings else ""
    extra = prefix.get("extraIngredient") or seasoning
    flavor = prefix.get("flavor", prefix["text"])
    return {
        "prefix": prefix["text"],
        "flavor": flavor,
        "main": main,
        "main1": main,
        "main2": main2,
        "main3": main3,
        "seasoning": seasoning,
        "method": core.get("method", ""),
        "extra": extra,
        "core_name": core["name"],
        "category": core.get("category", "")
    }


def build_description(title: str, context: Dict[str, str]) -> str:
    method_phrase = f"{context['method']} " if context["method"] else ""
    seasoning_phrase = f"和 {context['seasoning']} " if context["seasoning"] else ""
    return (
        f"{title} 以 {context['main']} 与 {context['main2']} 为骨架，"
        f"{context['flavor']} 风味在{method_phrase}里慢慢渗出，{seasoning_phrase}带来层次与 {context['core_name']} 的触感。"
    )


def generate_steps(action_templates: List[str], context: Dict[str, str]) -> List[str]:
    return [template.format(**context) for template in action_templates]


def generate_dynamic_entry(category: str, prefix: Dict[str, str], core: Dict[str, object], action_templates: List[str]) -> Dict[str, object]:
    slug = f"{CATEGORY_SLUGS[category]}-{prefix['slug']}-{core['slug']}"
    title = f"{prefix['text']}{core['name']}"
    context = build_context(prefix, core)
    steps = generate_steps(action_templates, context)
    ingredients = list(dict.fromkeys(core["ingredients"] + ([prefix.get("extraIngredient")] if prefix.get("extraIngredient") else [])))
    tags = list(dict.fromkeys(core.get("tags", []) + prefix.get("tags", [])))
    description = build_description(title, context)
    nutritional = core["nutritionalNotesTemplate"].format(**context)
    tips = core["preparationTipsTemplate"].format(**context)
    return {
        "slug": slug,
        "title": title,
        "description": description,
        "ingredients": ingredients,
        "steps": steps,
        "category": category,
        "cookTimeMinutes": core["cookTime"],
        "servings": core["servings"],
        "tags": tags,
        "nutritionalNotes": nutritional,
        "preparationTips": tips
    }


def generate_entries_for_category(category: str, blueprint: Dict[str, object]) -> List[Dict[str, object]]:
    prefixes = blueprint["prefixes"]
    cores = blueprint["cores"]
    templates = blueprint["actionTemplates"]
    entries = []
    for prefix in prefixes:
        for core in cores:
            entry = generate_dynamic_entry(category, prefix, core, templates)
            entries.append(entry)
    if len(entries) != 20:
        raise ValueError(f"{category} should have 20 generated entries, but got {len(entries)}")
    return entries


def generate_all_dynamic_recipes() -> Dict[str, List[Dict[str, object]]]:
    return {category: generate_entries_for_category(category, blueprint) for category, blueprint in category_blueprints.items()}


def main():
    category_recipes = generate_all_dynamic_recipes()
    recipes = []
    for category, entries in category_recipes.items():
        for entry in entries:
            entry["imageUrl"] = f"https://example.com/images/{entry['slug']}.jpg"
            entry["isFavorite"] = False
            recipes.append(entry)

    output_path = os.path.join("app", "src", "main", "res", "raw", "good_recipe_seed.json")
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(recipes, f, ensure_ascii=False, indent=2)
    print(f"生成 {len(recipes)} 条菜谱数据 -> {output_path}")


if __name__ == "__main__":
    main()
