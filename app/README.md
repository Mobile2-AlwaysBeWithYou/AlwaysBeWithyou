firebase db 관련 설정

1. google-services.json 추가
2. 프로젝트 수준 빌드에 id("com.google.gms.google-services") version "4.4.2" apply false
3. 모듈 수준 빌드 plugins에 id("com.google.gms.google-services")
4. 모듈 수준 빌드 dependency에 implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
   implementation("com.google.firebase:firebase-analytics")
