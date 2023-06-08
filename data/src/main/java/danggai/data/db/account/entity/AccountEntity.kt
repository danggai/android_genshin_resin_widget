package danggai.data.db.account.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "account")
data class AccountEntity(
    @SerializedName("nickname")
    val accountNickName: String,
    @SerializedName("cookie")
    val cookie: String,
    @PrimaryKey(autoGenerate = false)
    @SerializedName("genshinUid")
    val genshinUid: String,
    @SerializedName("server")
    val server: Int,

    @ColumnInfo(name = "honkaiSrNickname", defaultValue = "")
    val honkaiSrNickName: String,
    @ColumnInfo(name = "honkaiSrUid", defaultValue = "")
    val honkaiSrUid: String,
    @ColumnInfo(name = "honkaiSrServer", defaultValue = "0")
    val honkaiSrServer: Int,

    @SerializedName("enableGenshinCheckin")
    val enableGenshinCheckin: Boolean,
    @SerializedName("enableHonkai3rdCheckin")
    val enableHonkai3rdCheckin: Boolean,
    @SerializedName("enableHonkaiSRCheckin")
    val enableHonkaiSRCheckin: Boolean,
    @SerializedName("enableTotCheckin")
    val enableTotCheckin: Boolean,
)