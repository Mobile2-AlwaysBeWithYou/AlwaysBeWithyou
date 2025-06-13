package com.example.alwaysbewithyou.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.dbtest.data.*
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

object FirestoreRepository {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    // --- 사용자 관련 ---

    fun createUser(user: User, onComplete: () -> Unit) {
        db.collection("users").document(user.id).set(user)
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error creating user", it)
            }
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                callback(doc.toObject(User::class.java))
            }
    }

    fun updateUserName(userId: String, newName: String, onComplete: () -> Unit) {
        db.collection("users").document(userId)
            .update("name", newName)
            .addOnSuccessListener { onComplete() }
    }

    fun deleteUser(userId: String) {
        db.collection("users").document(userId).delete()
    }

    // 사용자 로그인 (비밀번호 해시 비교)
    fun login(userId: String, inputPassword: String, callback: (Boolean) -> Unit) {
        getUser(userId) { user ->
            if (user != null) {
                val hashedInput = hashPassword(inputPassword)
                callback(user.password == hashedInput)
            } else {
                callback(false)
            }
        }
    }

    // 비밀번호 해시 함수
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }


    fun addPillAlarm(userId: String, alarm: PillAlarm) {
        db.collection("users").document(userId)
            .collection("pill_alarms")
            .add(alarm)
            .addOnSuccessListener { document ->
                val generatedId = document.id
                val alarmWithId = alarm.copy(id = generatedId)
                document.set(alarmWithId)
            }
    }

    fun getPillAlarms(userId: String, callback: (List<PillAlarm>) -> Unit) {
        db.collection("users").document(userId)
            .collection("pill_alarms").get()
            .addOnSuccessListener { result ->
                val alarms = result.documents.mapNotNull { it.toObject(PillAlarm::class.java) }
                callback(alarms)
            }
    }

    fun updatePillAlarmEnabled(userId: String, alarmId: String, enabled: Boolean) {
        db.collection("users").document(userId)
            .collection("pill_alarms").document(alarmId)
            .update("enabled", enabled)
    }

    fun deletePillAlarm(userId: String, alarmId: String) {
        db.collection("users").document(userId)
            .collection("pill_alarms").document(alarmId)
            .delete()
    }


    fun addNotification(userId: String, notification: Notification) {
        db.collection("users").document(userId)
            .collection("notifications")
            .add(notification)
    }

    fun getNotifications(userId: String, callback: (List<Notification>) -> Unit) {
        db.collection("users").document(userId)
            .collection("notifications").get()
            .addOnSuccessListener { result ->
                val notifications = result.documents.mapNotNull { it.toObject(Notification::class.java) }
                callback(notifications)
            }
    }


    fun addCheckIn(userId: String, checkIn: CheckIn) {
        db.collection("users").document(userId)
            .collection("check_ins")
            .add(checkIn)
    }

    fun getCheckIns(userId: String, callback: (List<CheckIn>) -> Unit) {
        db.collection("users").document(userId)
            .collection("check_ins").get()
            .addOnSuccessListener { result ->
                val checkIns = result.documents.mapNotNull { it.toObject(CheckIn::class.java) }
                callback(checkIns)
            }
    }


    fun requestConsultation(userId: String, consultation: Consultation) {
        db.collection("users").document(userId)
            .collection("consultations")
            .add(consultation)
    }

    fun updateConsultationStatus(userId: String, consultationId: String, status: String) {
        db.collection("users").document(userId)
            .collection("consultations").document(consultationId)
            .update("status", status)
    }

    fun getConsultations(userId: String, callback: (List<Consultation>) -> Unit) {
        db.collection("users").document(userId)
            .collection("consultations").get()
            .addOnSuccessListener { result ->
                val consultations = result.documents.mapNotNull { it.toObject(Consultation::class.java) }
                callback(consultations)
            }
    }


    fun setUserSettings(userId: String, settings: Settings) {
        db.collection("users").document(userId)
            .collection("settings").document("config")
            .set(settings)
    }

    fun getUserSettings(userId: String, callback: (Settings?) -> Unit) {
        db.collection("users").document(userId)
            .collection("settings").document("config").get()
            .addOnSuccessListener {
                callback(it.toObject(Settings::class.java))
            }
    }


    fun linkGuardianWard(userId: String, relation: GuardianWard) {
        db.collection("users").document(userId)
            .collection("guardian_wards")
            .add(relation)
    }

    fun removeGuardianWard(userId: String, relationId: String) {
        db.collection("users").document(userId)
            .collection("guardian_wards").document(relationId)
            .delete()
    }

    fun getGuardianWards(userId: String, callback: (List<GuardianWard>) -> Unit) {
        db.collection("users").document(userId)
            .collection("guardian_wards").get()
            .addOnSuccessListener { result ->
                val relations = result.documents.mapNotNull { it.toObject(GuardianWard::class.java) }
                callback(relations)
            }
    }
}
