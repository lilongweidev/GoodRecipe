package com.goodrecipe

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.HiltAndroidApp
import com.goodrecipe.data.seed.SeedDataInitializer

@HiltAndroidApp
class GoodRecipeApp : Application() {

    @Inject
    lateinit var seedDataInitializer: SeedDataInitializer

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            seedDataInitializer.seedIfNeeded()
        }
    }
}
