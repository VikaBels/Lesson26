package com.example.lesson26.repositories

import android.database.sqlite.SQLiteDatabase
import bolts.CancellationToken
import bolts.Task
import com.example.lesson26.models.Point
import com.example.lesson26.models.Notification
import com.example.lesson26.models.Track

class DataRepository(
    private val db: SQLiteDatabase
) {
    companion object {
        private const val TRACK_ID = "track_id"
        private const val TRACK_BEGINS_AT = "track_begins_at"
        private const val TRACK_TIME = "track_time"
        private const val TRACK_DISTANCE = "track_distance"

        private const val NOTIFICATION_TIME = "notification_time"
        private const val NOTIFICATION_TEXT = "notification_text"

        private const val POINT_LNG = "point_lng"
        private const val POINT_LAT = "point_lat"

        private const val IS_EXIST = "isExist"

        private const val MAX_ID_TRACK = "max_id"

        private const val COLON = ":"
    }

    private fun getAllTrack(tokenUser: String): List<Track> {
        val listTracks = mutableListOf<Track>()

        val cursor = db.rawQuery(
            "SELECT t.time as $TRACK_TIME, t.distance as $TRACK_DISTANCE, " +
                    "t.begins_at as $TRACK_BEGINS_AT, " +
                    "t._id as $TRACK_ID " +
                    "FROM track t " +
                    "JOIN user u ON u.token = t.user_token " +
                    "WHERE u.token = ? " +
                    "ORDER BY t.time DESC",
            arrayOf(
                refactorToken(tokenUser)
            )
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                val idCursor = cursor.getColumnIndexOrThrow(TRACK_ID)
                val beginsAtCursor = cursor.getColumnIndexOrThrow(TRACK_BEGINS_AT)
                val timeCursor = cursor.getColumnIndexOrThrow(TRACK_TIME)
                val distanceCursor = cursor.getColumnIndexOrThrow(TRACK_DISTANCE)

                do {
                    val track = Track(
                        cursor.getInt(idCursor),
                        cursor.getLong(beginsAtCursor),
                        cursor.getDouble(timeCursor),
                        cursor.getDouble(distanceCursor).toLong()
                    )

                    listTracks.add(track)
                } while (cursor.moveToNext())
            }
        }
        return listTracks
    }

    private fun getAllNotification(tokenUser: String): List<Notification> {
        val listNotification = mutableListOf<Notification>()

        val cursor = db.rawQuery(
            "SELECT n.time AS $NOTIFICATION_TIME, n.text AS $NOTIFICATION_TEXT " +
                    "FROM notification n " +
                    "JOIN user u ON u.token = n.user_token " +
                    "WHERE u.token = ? " +
                    "ORDER BY n.time DESC",
            arrayOf(
                refactorToken(tokenUser)
            )
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                val timeCursor = cursor.getColumnIndexOrThrow(NOTIFICATION_TIME)
                val textCursor = cursor.getColumnIndexOrThrow(NOTIFICATION_TEXT)

                do {
                    val notification = Notification(
                        cursor.getString(textCursor),
                        cursor.getLong(timeCursor),
                    )

                    listNotification.add(notification)

                } while (cursor.moveToNext())
            }
        }

        return listNotification
    }

    private fun getAllPoints(tokenUser: String, idTrack: Long): List<Point> {
        val listPoints = mutableListOf<Point>()

        val cursor = db.rawQuery(
            "SELECT p.lng AS $POINT_LNG, p.lat AS $POINT_LAT " +
                    "FROM point p " +
                    "JOIN track t " +
                    "ON t.user_token = p.user_token " +
                    "AND t._id = p.track_id " +
                    "WHERE t.user_token = ? AND t._id = ?",
            arrayOf(
                refactorToken(tokenUser),
                idTrack.toString()
            )
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                val lngCursor = cursor.getColumnIndexOrThrow(POINT_LNG)
                val latCursor = cursor.getColumnIndexOrThrow(POINT_LAT)

                do {
                    val point = Point(
                        cursor.getDouble(lngCursor),
                        cursor.getDouble(latCursor)
                    )

                    listPoints.add(point)

                } while (cursor.moveToNext())
            }
        }

        return listPoints
    }

    private fun addNewUser(
        name: String,
        lastName: String,
        email: String,
        password: String,
        tokenUser: String
    ) {
        val statementAddUser =
            db.compileStatement(
                "INSERT INTO user (name, last_name, email, password, token) " +
                        "VALUES (?, ?, ?, ?, ?)"
            )

        statementAddUser.apply {
            bindString(1, name)
            bindString(2, lastName)
            bindString(3, email)
            bindString(4, password)
            bindString(5, refactorToken(tokenUser))
        }.executeInsert()
    }

    private fun addNewTrack(
        beginsAt: Long,
        time: Double,
        distance: Long,
        tokenUser: String,
    ) {
        val statementAddTrack =
            db.compileStatement(
                "INSERT INTO track (begins_at, time, distance, user_token) VALUES (?,?,?,?)"
            )

        statementAddTrack.apply {
            bindLong(1, beginsAt)
            bindDouble(2, time)
            bindLong(3, distance)
            bindString(4, refactorToken(tokenUser))
        }.executeInsert()
    }

    private fun addNewPoints(
        listPoints: List<Point>,
        tokenUser: String
    ) {
        val currentId = getMaxIdTrack()

        if (currentId != null) {
            listPoints.forEach {
                val statementAddPoint =
                    db.compileStatement(
                        "INSERT INTO point (lng, lat, track_id, user_token) VALUES (?,?,?,?)"
                    )

                statementAddPoint.apply {
                    bindDouble(1, it.lng)
                    bindDouble(2, it.lat)
                    bindLong(3, currentId)
                    bindString(4, refactorToken(tokenUser))
                }.executeInsert()
            }
        }
    }

    private fun addNewNotification(
        time: Long,
        text: String,
        tokenUser: String
    ) {
        val statementAddNotification =
            db.compileStatement(
                "INSERT INTO notification (time, text, user_token) VALUES (?,?,?)"
            )

        statementAddNotification.apply {
            bindLong(1, time)
            bindString(2, text)
            bindString(3, refactorToken(tokenUser))
        }.executeInsert()
    }

    private fun getMaxIdTrack(): Long? {
        var id: Long? = 0L

        val cursor = db.rawQuery(
            "SELECT MAX(_id) as $MAX_ID_TRACK FROM track",
            null
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                val maxId = cursor.getColumnIndexOrThrow(MAX_ID_TRACK)

                do {
                    id = cursor.getLong(maxId)
                } while (cursor.moveToNext())
            }
        }
        return id
    }

    private fun isUserExist(
        email: String,
        password: String,
    ): Int {
        var isExist = 0

        val cursor = db.rawQuery(
            "SELECT count(token) as $IS_EXIST FROM user WHERE email=? AND password=?",
            arrayOf(
                email,
                password
            )
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                val existCursor = cursor.getColumnIndexOrThrow(IS_EXIST)

                do {
                    isExist = cursor.getInt(existCursor)
                } while (cursor.moveToNext())
            }
        }

        return isExist
    }

    private fun deleteNotification(
        time: Long,
        text: String,
        tokenUser: String
    ) {
        val statementDeleteNotification =
            db.compileStatement(
                "DELETE FROM notification WHERE time = ? AND text = ? AND user_token = ?"
            )

        statementDeleteNotification.apply {
            bindLong(1, time)
            bindString(2, text)
            bindString(3, refactorToken(tokenUser))
        }.executeUpdateDelete()
    }

    //change newText and newTime in SQL
    private fun changeNotification(
        time: Long,
        text: String,
        newTime: Long,
        newText: String
    ) {
        val statementChangeNotification =
            db.compileStatement(
                "UPDATE notification " +
                        "SET time = $newTime, text = \"$newText\" " +
                        "WHERE time = ? AND text = ?"
            )

        statementChangeNotification.apply {
            bindLong(1, time)
            bindString(2, text)
        }.executeUpdateDelete()
    }

    fun changeNotificationTask(
        cancellationToken: CancellationToken,
        time: Long,
        text: String,
        newTime: Long,
        newText: String
    ): Task<Unit> {
        return Task.callInBackground({
            changeNotification(
                time,
                text,
                newTime,
                newText
            )
        }, cancellationToken)
    }

    //this change
    private fun refactorToken(tokenUser: String): String {
        return tokenUser.split(COLON.toRegex()).toTypedArray()[0]
    }

    fun getAllTrackTask(
        cancellationToken: CancellationToken,
        tokenUser: String
    ): Task<List<Track>> {
        return Task.callInBackground({
            getAllTrack(tokenUser)
        }, cancellationToken)
    }

    fun getAllNotificationTask(
        cancellationToken: CancellationToken,
        tokenUser: String
    ): Task<List<Notification>> {
        return Task.callInBackground({
            getAllNotification(tokenUser)
        }, cancellationToken)
    }

    fun getAllPointsTask(
        cancellationToken: CancellationToken,
        tokenUser: String,
        idTrack: Long
    ): Task<List<Point>> {
        return Task.callInBackground({
            getAllPoints(
                tokenUser,
                idTrack
            )
        }, cancellationToken)
    }

    fun addNewPointsTask(
        cancellationToken: CancellationToken,
        listPoints: List<Point>,
        userToken: String
    ): Task<Unit> {
        return Task.callInBackground({
            addNewPoints(
                listPoints,
                userToken
            )
        }, cancellationToken)
    }

    fun addNewTrackTask(
        cancellationToken: CancellationToken,
        beginsAt: Long,
        time: Double,
        distance: Long,
        userToken: String
    ): Task<Unit> {
        return Task.callInBackground({
            addNewTrack(
                beginsAt,
                time,
                distance,
                userToken
            )
        }, cancellationToken)
    }

    fun addNewUserTask(
        cancellationToken: CancellationToken,
        name: String,
        lastName: String,
        email: String,
        password: String,
        tokenUser: String
    ): Task<Unit> {
        return Task.callInBackground({
            addNewUser(
                name,
                lastName,
                email,
                password,
                tokenUser
            )
        }, cancellationToken)
    }

    fun addNewNotificationTask(
        cancellationToken: CancellationToken,
        time: Long,
        text: String,
        tokenUser: String
    ): Task<Unit> {
        return Task.callInBackground({
            addNewNotification(
                time,
                text,
                tokenUser
            )
        }, cancellationToken)
    }

    fun getIsExistUserTask(
        cancellationToken: CancellationToken,
        email: String,
        password: String,
    ): Task<Int> {
        return Task.callInBackground({
            isUserExist(email, password)
        }, cancellationToken)
    }

    fun deleteNotificationTask(
        cancellationToken: CancellationToken,
        time: Long,
        text: String,
        tokenUser: String
    ): Task<Unit> {
        return Task.callInBackground({
            deleteNotification(
                time,
                text,
                tokenUser
            )
        }, cancellationToken)
    }
}