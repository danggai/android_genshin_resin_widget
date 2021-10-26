package danggai.app.resinwidget.util

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and

object CommonFunction {

//    fun now(): String {
//        return SimpleDateFormat(Constant.DATE_FORMAT_BEFORE).format(Date())
//    }

    fun encodeToMD5(str: String): String {
        var md5: String? = ""
        md5 = try {
            val md: MessageDigest = MessageDigest.getInstance("MD5")
            md.update(str.toByteArray(charset("UTF-8")))
            val byteData: ByteArray = md.digest()
            val sb = StringBuffer()
            for (i in byteData.indices) sb.append(
                ((byteData[i] and 0xff.toByte()) + 0x100).toString(16).substring(1)
            )
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }

        return md5?.lowercase()?:""
    }
}