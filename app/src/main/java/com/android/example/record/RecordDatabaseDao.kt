package com.android.example.record

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * author：William
 * date：4/18/21 11:11 AM
 * description：Record 数据库 Dao
 */
@Dao
interface RecordDatabaseDao {

    @Insert
    fun insert(record: Record)

    @Update
    fun update(record: Record)

    @Query("DELETE FROM user_operation_records_table")
    fun clear()

    @Query("SELECT * FROM user_operation_records_table WHERE recordId = :key")
    fun get(key: Long): Record?

    @Query("SELECT * FROM user_operation_records_table ORDER BY recordId DESC ")
    fun getRecords(): List<Record>?

    @Query("SELECT * FROM user_operation_records_table ORDER BY recordId DESC LIMIT 1")
    fun getCurrentRecord(): Record?

    @Insert
    fun insertData(record: NewTable)

}