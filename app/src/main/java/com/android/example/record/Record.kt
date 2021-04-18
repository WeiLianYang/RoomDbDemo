package com.android.example.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * author：William
 * date：4/18/21 11:11 AM
 * description：操作记录数据表 数据库中类型，integer 整型，real 浮点型，text 文本，blob 二进制
 */
@Entity(tableName = "user_operation_records_table")
data class Record(
    @PrimaryKey(autoGenerate = true)
    var recordId: Long = 0L,

    @ColumnInfo(name = "start_time")
    var startTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time")
    var endTime: Long = startTime,

    @ColumnInfo(name = "start_record")
    var startRecord: Boolean = false,

    @ColumnInfo(name = "end_record")
    var endRecord: Boolean = false
)

@Entity(tableName = "new_table")
data class NewTable(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "time")
    var time: Long = 0L
)

@Entity(tableName = "new_table2")
data class NewTable2(

    @PrimaryKey(autoGenerate = true)
    var id2: Long = 0L,

    @ColumnInfo(name = "time2")
    var time2: Long = 0L
)