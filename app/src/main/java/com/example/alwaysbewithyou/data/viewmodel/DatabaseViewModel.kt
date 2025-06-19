package com.example.alwaysbewithyou.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alwaysbewithyou.data.repository.FirestoreRepository
import com.example.dbtest.data.CheckIn
import com.example.dbtest.data.Consultation
import com.example.dbtest.data.FontSize
import com.example.dbtest.data.GuardianWard
import com.example.dbtest.data.Notification
import com.example.dbtest.data.PillAlarm
import com.example.dbtest.data.Settings
import com.example.dbtest.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted



class DatabaseViewModel : ViewModel() {

    // 사용자
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    //사용자 생성
    fun createUser(user: User, onComplete: () -> Unit) {
        FirestoreRepository.createUser(user) {
            loadUser(user.id)
            onComplete()
        }
    }

    //사용자 불러오기
    fun loadUser(userId: String) {
        FirestoreRepository.getUser(userId) {
            _user.value = it
        }
    }

    //사용자 정보 업데이트
    fun updateUserName(userId: String, newName: String) {
        FirestoreRepository.updateUserName(userId, newName) {
            loadUser(userId)
        }
    }

    //사용자 삭제
    fun deleteUser(userId: String) {
        FirestoreRepository.deleteUser(userId)
        _user.value = null
    }

    // 약물 알림
    private val _pillAlarms = MutableStateFlow<List<PillAlarm>>(emptyList())
    val pillAlarms: StateFlow<List<PillAlarm>> = _pillAlarms

    //약물 알림 불러오기
    fun loadPillAlarms(userId: String) {
        FirestoreRepository.getPillAlarms(userId) {
            _pillAlarms.value = it
        }
    }

    //약물 알림 추가
    fun addPillAlarm(userId: String, alarm: PillAlarm) {
        FirestoreRepository.addPillAlarm(userId, alarm)
        loadPillAlarms(userId)
    }

    //약물 알림 수정
    fun updatePillAlarmEnabled(userId: String, alarmId: String, enabled: Boolean) {
        FirestoreRepository.updatePillAlarmEnabled(userId, alarmId, enabled)
        loadPillAlarms(userId)
    }

    //약물 알림 삭제
    fun deletePillAlarm(userId: String, alarmId: String) {
        FirestoreRepository.deletePillAlarm(userId, alarmId)
        loadPillAlarms(userId)
    }


    // Notification
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    //노인 알림 불러오기
    fun loadNotifications(userId: String) {
        FirestoreRepository.getNotifications(userId) {
            _notifications.value = it
        }
    }

    //노인 알림 추가
    fun addNotification(userId: String, notification: Notification) {
        FirestoreRepository.addNotification(userId, notification)
        loadNotifications(userId)
    }

    // CheckIn
    private val _checkIns = MutableStateFlow<List<CheckIn>>(emptyList())
    val checkIns: StateFlow<List<CheckIn>> = _checkIns

    //노인 알림 확인 불러오기
    fun loadCheckIns(userId: String) {
        FirestoreRepository.getCheckIns(userId) {
            _checkIns.value = it
        }
    }

    //노인 알람 확인 추가
    fun addCheckIn(userId: String, checkIn: CheckIn) {
        FirestoreRepository.addCheckIn(userId, checkIn)
        loadCheckIns(userId)
    }

    // Consultation
    private val _consultations = MutableStateFlow<List<Consultation>>(emptyList())
    val consultations: StateFlow<List<Consultation>> = _consultations

    //상담 신청 불러오기
    fun loadConsultations(userId: String) {
        FirestoreRepository.getConsultations(userId) {
            _consultations.value = it
        }
    }

    //상담 신청
    fun requestConsultation(userId: String, consultation: Consultation) {
        FirestoreRepository.requestConsultation(userId, consultation)
        loadConsultations(userId)
    }

    //상담 신청 추가
    fun updateConsultationStatus(userId: String, consultationId: String, status: String) {
        FirestoreRepository.updateConsultationStatus(userId, consultationId, status)
        loadConsultations(userId)
    }

    // Settings
    private val _settings = MutableStateFlow<Settings?>(null)
    val settings: StateFlow<Settings?> = _settings

    fun loadSettings(userId: String) {
        FirestoreRepository.getUserSettings(userId) {
            _settings.value = it
        }
    }

    fun saveSettings(userId: String, settings: Settings) {
        FirestoreRepository.setUserSettings(userId, settings)
        loadSettings(userId)
    }

    val fontSizeEnum: StateFlow<FontSize> = settings
        .filterNotNull()
        .map { FontSize.fromString(it.font_size) }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            FontSize.MEDIUM
        )

    // 보호자 정보
    private val _guardianWards = MutableStateFlow<List<GuardianWard>>(emptyList())
    val guardianWards: StateFlow<List<GuardianWard>> = _guardianWards

    fun loadGuardianWards(userId: String) {
        FirestoreRepository.getGuardianWards(userId) {
            _guardianWards.value = it
        }
    }


    fun linkGuardianWard(userId: String, relation: GuardianWard) {
        FirestoreRepository.linkGuardianWard(userId, relation)
        loadGuardianWards(userId)
    }

    fun removeGuardianWard(userId: String, relationId: String) {
        FirestoreRepository.removeGuardianWard(userId, relationId)
        loadGuardianWards(userId)
    }
}
