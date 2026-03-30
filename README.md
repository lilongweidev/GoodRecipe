# 🍳 GoodRecipe

一款使用 **Jetpack Compose** 构建的 Android 菜谱管理应用。

## 技术栈

| 技术 | 用途 |
|------|------|
| Jetpack Compose | 声明式 UI |
| Navigation Compose | 页面路由与导航 |
| ViewModel + StateFlow | UI 状态管理 |
| Hilt | 依赖注入 |
| Room | 本地数据库持久化 |
| Kotlin Coroutines + Flow | 异步与响应式数据流 |

## 项目结构

```
app/src/main/java/com/goodrecipe/
├── GoodRecipeApp.kt          # Application（Hilt 入口）
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── GoodRecipeDatabase.kt
│   │   ├── dao/RecipeDao.kt
│   │   └── entity/RecipeEntity.kt
│   └── repository/
│       ├── Recipe.kt         # 领域模型 + 枚举
│       └── RecipeRepository.kt
├── di/
│   └── AppModule.kt          # Hilt 模块
├── navigation/
│   └── Navigation.kt         # 路由定义 + NavHost
├── ui/
│   ├── components/RecipeCard.kt
│   ├── screens/
│   │   ├── home/HomeScreen.kt
│   │   ├── detail/RecipeDetailScreen.kt
│   │   └── add/AddRecipeScreen.kt
│   └── theme/Theme.kt
└── viewmodel/
    ├── HomeViewModel.kt
    ├── RecipeDetailViewModel.kt
    └── AddRecipeViewModel.kt
```

## 功能

- ✅ 浏览所有菜谱
- ✅ 按分类筛选（早餐 / 午餐 / 晚餐 / 汤品）
- ✅ 搜索菜谱
- ✅ 收藏 / 取消收藏
- ✅ 添加新菜谱（食材 + 步骤）
- ✅ 编辑已有菜谱
- ✅ 删除菜谱
- ✅ 深色模式支持

## 快速开始

1. Clone 项目
```bash
git clone https://github.com/lilongweidev/GoodRecipe.git
```

2. 用 Android Studio（Hedgehog 或更高版本）打开项目

3. 等待 Gradle Sync 完成后运行

> 要求：minSdk 26，targetSdk 35，JVM 17
