package danggai.domain.network.githubRaw.entity

import danggai.domain.local.RecentCharacter

data class RecentGenshinCharacters(
    val characters: List<RecentCharacter>
) {
    companion object {
        val EMPTY = RecentGenshinCharacters(listOf())
    }
}