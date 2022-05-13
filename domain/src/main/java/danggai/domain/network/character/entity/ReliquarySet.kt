package danggai.domain.network.character.entity

data class ReliquarySet(
    val id: Int,
    val name: String,            // 세트 명
    val affixes: List<Affixe>,   // 발동 세트 옵션
) {
    data class Affixe(
        val activation_number: Int,
        val effect: String
    )
}