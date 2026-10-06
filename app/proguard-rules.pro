# 크래시 리포트 스택트레이스용 줄 번호 유지
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Gson/Retrofit이 제네릭, 어노테이션 정보를 읽는다
-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod

# Gson으로 저장하는 설정 JSON, API 응답, Room 엔티티는 필드명이 곧 저장 키라서 이름을 유지한다
-keep class danggai.domain.** { *; }
-keep class danggai.data.** { *; }

# Gson 2.8.6은 자체 규칙이 없다
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# OkHttp가 선택적으로 참조하는 보안 라이브러리(앱에는 포함되지 않음)
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
