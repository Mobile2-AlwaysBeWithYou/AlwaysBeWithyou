package com.example.alwaysbewithyou.presentation.map.tools

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val NAVER_API_BASE_URL = "https://openapi.naver.com/"

    // 1. HTTP 로깅 인터셉터 생성 (개발 시 네트워크 요청/응답 확인용)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 요청 및 응답 본문까지 모두 로그에 출력
    }

    // 2. OkHttpClient 생성 (로깅 인터셉터 추가)
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // 3. NaverApiService 인스턴스 지연 초기화 (by lazy)
    val naverApiService: NaverApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NAVER_API_BASE_URL) // 네이버 API 기본 URL 설정
            .addConverterFactory(GsonConverterFactory.create()) // JSON -> Kotlin 객체 변환을 위한 Gson 컨버터
            .client(okHttpClient) // 로깅 인터셉터가 포함된 OkHttpClient 설정
            .build()
            .create(NaverApiService::class.java) // NaverApiService 인터페이스 구현체 생성
    }
}