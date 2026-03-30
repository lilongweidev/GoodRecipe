package com.goodrecipe.viewmodel

import com.goodrecipe.data.repository.Recipe
import kotlin.random.Random

data class RandomPickResult(
    val selection: Recipe?,
    val changed: Boolean
)

class RandomRecipePicker(
    private val random: Random = Random.Default
) {
    fun pickDifferent(
        options: List<Recipe>,
        previous: Recipe?
    ): RandomPickResult {
        if (options.isEmpty()) {
            return RandomPickResult(selection = null, changed = false)
        }

        if (previous == null) {
            return RandomPickResult(
                selection = options.random(random),
                changed = true
            )
        }

        val candidates = options.filter { it.id != previous.id }
        if (candidates.isEmpty()) {
            return RandomPickResult(selection = previous, changed = false)
        }

        return RandomPickResult(
            selection = candidates.random(random),
            changed = true
        )
    }
}
