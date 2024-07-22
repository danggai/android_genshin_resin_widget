package danggai.domain.local

enum class Server(val value: Int) {
    ASIA(0),
    USA(1),
    EUROPE(2),
    CHT(3);

    companion object {
        fun fromValue(value: Int): Server? {
            return Server.values().find { it.value == value }
        }
    }
}