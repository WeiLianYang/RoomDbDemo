package com.android.example.record

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

/**
 * author：William
 * date：4/18/21 11:11 AM
 * description：签到列表
 */
class MainActivity : AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var database: RecordDatabaseDao
    private var currentRecord: Record? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        startButton.isEnabled = true
        endButton.isEnabled = false
    }

    private fun initData() {
        database = RecordDatabase.getInstance(this).recordDatabaseDao
        scope.launch {
            currentRecord = getCurrentRecordFromDb()
            setRecordContent()
        }
    }

    private suspend fun setRecordContent() {
        val allRecords = getAllRecordsFromDb()
        allRecords?.apply {
            withContext(Dispatchers.Main) {
                if (count() > 0) {
                    val lastRecord = get(0)
                    setButtonEnabler(lastRecord)
                }
                recordContent.text = formatRecords(allRecords)
            }
        }
    }

    private fun setButtonEnabler(record: Record) {
        startButton.isEnabled = !record.startRecord
        endButton.isEnabled = record.startRecord
    }

    private fun initEvent() {
        startButton.setOnClickListener {
            startRecord()
        }

        endButton.setOnClickListener {
            stopRecord()
        }

        clearButton.setOnClickListener {
            clearRecords()
        }
    }

    private fun startRecord() {
        scope.launch {
            val record = Record()
            record.startRecord = true
            Log.i("Room", "startRecord 1")
            insertRecord(record)
            Log.i("Room", "startRecord 2")
            currentRecord = getCurrentRecordFromDb()
            Log.i("Room", "startRecord 3")
            setRecordContent()
            Log.i("Room", "startRecord 4")
        }
    }

    private fun stopRecord() {
        scope.launch {
            val record = currentRecord ?: return@launch
            record.startRecord = false
            record.endTime = System.currentTimeMillis()
            Log.i("Room", "stopRecord 1")
            updateRecord(record)
            Log.i("Room", "stopRecord 2")
            setRecordContent()
            Log.i("Room", "stopRecord 3")
        }
    }

    private fun clearRecords() {
        scope.launch {
            Log.i("Room", "clearRecords")
            clear()
            currentRecord = null
            recordContent.text = null
            Log.i("Room", "clearRecords ends up")
        }
    }

    private suspend fun getCurrentRecordFromDb(): Record? {
        return withContext(Dispatchers.IO) {
            val record = database.getCurrentRecord()
            Log.i("Room", "getCurrentRecordFromDatabase: ${record.toString()}")
            record
        }
    }

    private suspend fun getAllRecordsFromDb(): List<Record>? {
        return withContext(Dispatchers.IO) {
            val records = database.getRecords()
            Log.i("Room", "getAllRecordsFromDatabase")
            records
        }
    }

    private suspend fun insertRecord(record: Record) {
        withContext(Dispatchers.IO) {
            Log.i("Room", "insertRecord")
//            Thread.sleep(3000)
            database.insert(record)
        }
    }

    private suspend fun updateRecord(record: Record) {
        withContext(Dispatchers.IO) {
            Log.i("Room", "updateRecord")
            database.update(record)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            Log.i("Room", "clear")
            database.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}