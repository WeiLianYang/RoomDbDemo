package com.android.example.record

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import java.text.SimpleDateFormat
import java.util.*

/**
 * author：William
 * date：4/18/21 11:11 AM
 * description：
 */
fun formatRecords(records: List<Record>): Spanned {
    val sb = StringBuilder()
    sb.apply {
        append("以下是用户以往操作记录<br>")
        append("----------一条单纯的分割线---------<br>")
        records.forEach {
            append("<br>")
            append("上班时间：")
            append("\t${formatDate(it.startTime)}<br>")
            if (it.endTime > it.startTime) {
                append("下班时间：")
                append("\t${formatDate(it.endTime)}<br>")
            }
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

@SuppressLint("SimpleDateFormat")
fun formatDate(time: Long): String? {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(Date(time))
}