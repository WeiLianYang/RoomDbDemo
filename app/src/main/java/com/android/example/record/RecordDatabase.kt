package com.android.example.record

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * author：William
 * date：4/18/21 11:11 AM
 * description：Record 数据库抽象层
 */
@Database(
    entities = [Record::class, NewTable::class, NewTable2::class],
    version = 3,
    exportSchema = false
)
abstract class RecordDatabase : RoomDatabase() {

    abstract val recordDatabaseDao: RecordDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: RecordDatabase? = null

        fun getInstance(context: Context): RecordDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecordDatabase::class.java,
                        "record_operation_database"
                    )
                        .fallbackToDestructiveMigration() // 允许在迁移路径缺失的情况下丢失现有数据
                        .addMigrations(Migration_1_2, Migration_2_3) // 添加迁移规则
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        /**
         * 升级数据库 1->2：
         * 1. 创建新表 new_table
         * 2. 给 user_operation_records_table 表 添加 end_record 字段，并迁移历史数据
         */
        private val Migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.i("tag", "Migration_1_2")

                // 创建新表new_table
                createNewTable(database)

                // 给旧表增加字段 end_record
                database.execSQL("ALTER TABLE user_operation_records_table ADD COLUMN end_record INTEGER NOT NULL DEFAULT 0")

                // 迁移user_operation_records_table 表中的数据
                migrateRecordData(database)
            }
        }

        private fun createNewTable(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                            CREATE TABLE new_table (
                                id INTEGER PRIMARY KEY NOT NULL,
                                time INTEGER NOT NULL DEFAULT 111
                            )
                            """.trimIndent()
            )
        }

        /**
         * 迁移 user_operation_records_table 表中的数据
         */
        private fun migrateRecordData(database: SupportSQLiteDatabase) {
            // 创建新表
            database.execSQL(
                """
                    CREATE TABLE new_record (
                        recordId INTEGER PRIMARY KEY NOT NULL,
                        start_time INTEGER NOT NULL DEFAULT 111,
                        end_time INTEGER NOT NULL DEFAULT 222,
                        start_record INTEGER NOT NULL DEFAULT 10,
                        end_record INTEGER NOT NULL DEFAULT 11
                    )
                    """.trimIndent()
            )
            // 迁移旧表中的数据到新表中
            val columnSQL =
                "recordId, start_time, end_time, start_record, end_record"
            database.execSQL(
                """
                    INSERT INTO new_record ($columnSQL)
                    SELECT $columnSQL FROM user_operation_records_table
                    """.trimIndent()
            )
            // 删除旧表
            database.execSQL("DROP TABLE user_operation_records_table")
            // 将新表名称改回旧表名称，完成迁移
            database.execSQL("ALTER TABLE new_record RENAME TO user_operation_records_table")
        }

        /**
         * 升级数据库 2->3
         * 建新表new_table2
         */
        private val Migration_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                            CREATE TABLE new_table2 (
                                id2 INTEGER PRIMARY KEY NOT NULL,
                                time2 INTEGER NOT NULL DEFAULT 111
                            )
                            """.trimIndent()
                )
            }
        }
    }
}