# StyleByWeather

날씨 기반 옷 스타일링 추천 모바일 앱.

## 도메인 개요

사용자의 위치(또는 검색한 도시)의 현재 날씨를 OpenWeatherMap에서 가져와, 기온·날씨 상태·성별·선호 스타일에 따라 적절한 옷차림을 추천한다.

## 추천 로직

추천은 두 축을 결합해 결정한다.

1. **기온 구간 (min_temp ~ max_temp)** — 옷의 두께/레이어를 결정
2. **날씨 상태 (weather_condition)** — 비/눈/맑음 등 부가 아이템(우산, 방수 외투 등) 결정

성별(`gender`)과 스타일(`style`, 예: 캐주얼/포멀)은 동일 기온/날씨 조건에서 후보 옷을 필터링하는 데 사용된다.

## 데이터 모델

- **Clothes**: `id, name, category, image_url, gender, style`
  - `category` enum: TOP, INNER, BOTTOM, OUTER, SHOES, SOCKS, ACCESSORY
  - `gender` enum: MALE, FEMALE, UNISEX
  - `style` enum: CASUAL, FORMAL, SPORTY, STREET
- **WeatherStyle**: `id, min_temp, max_temp, weather_condition`
  - 하나의 기온 구간 + 날씨 조건 조합을 표현
  - `weather_condition` enum: OpenWeatherMap의 `weather[].main` 값과 매칭 (CLEAR, CLOUDS, RAIN, DRIZZLE, THUNDERSTORM, SNOW, MIST, FOG, HAZE)
- **StyleClothes**: `id, weather_style_id, clothes_id, priority` — WeatherStyle ↔ Clothes 다대다 매핑 (조인 엔티티)
  - `(weather_style_id, clothes_id)`에 유니크 제약 (`uk_style_clothes`) — 같은 조합 중복 금지
  - `priority`: 동일 기온/날씨 조건 안에서 추천 순위 (낮을수록 우선). 기본값 0.

다대다는 `@ManyToMany` 대신 별도 엔티티(`StyleClothes`)로 풀어둠. 추후 `priority` 외에 추천 가중치/노출 메타데이터 추가 시 마이그레이션 부담 없이 컬럼만 늘리면 된다.

새 옷/스타일 추가 시 이 세 테이블의 일관성을 유지해야 한다. 특히 기온 구간이 겹치거나 빈 구간이 생기지 않도록 주의.

## REST API

- `GET /api/weather?lat={위도}&lon={경도}` — 좌표 기반 현재 날씨
- `GET /api/weather?city={도시명}` — 도시명 기반 현재 날씨
- `GET /api/recommend?temp={기온}&condition={날씨}&gender={성별}&style={스타일}` — 추천 결과
- `GET /api/clothes`, `GET /api/clothes/{category}` — 옷 카탈로그 조회

추천 API는 `temp + condition`으로 매칭되는 `WeatherStyle`을 찾고, 그에 연결된 `Clothes` 중 `gender + style` 필터를 통과한 항목을 반환한다.

## 화면

- **홈**: GPS 위치 기반 현재 날씨 + 오늘의 추천 스타일
- **검색**: 도시명 검색 → 해당 도시 날씨 + 추천
- **설정**: 온도 단위, 성별, 선호 스타일 (AsyncStorage에 저장)

## 외부 의존성

- **OpenWeatherMap (무료 티어)**: 현재 날씨 + 주간 예보. API 키는 환경변수 `OPENWEATHERMAP_API_KEY`로 주입하며 절대 커밋하지 않는다.
  - 호출 시 `units=metric`, `lang=kr` 고정 — 온도는 항상 섭씨로 처리.
  - 외부의 `weather[].main` 문자열을 우리 `WeatherCondition` enum으로 매핑할 때, enum에 없는 값(Smoke, Dust, Tornado 등)은 `CLEAR`로 fallback. 추천 로직에 빈 결과가 들어가지 않게 하기 위함.

## 향후 확장

- 주간 예보 기반 주간 스타일 추천 — `WeatherStyle` 매칭을 N일치 반복 적용
- 옷 이미지/아이콘 — `Clothes.image_url` 활용

## 기술 스택

- Backend: Spring Boot 4.1.0-SNAPSHOT, Java 17, JPA, H2(개발)/MySQL(운영)
- Frontend: React Native 0.84.1 (CLI, Expo 아님), TypeScript
- 테스트: iOS Simulator (Xcode)
