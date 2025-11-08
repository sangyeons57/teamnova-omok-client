# 클라이언트 입력 검증 단위 테스트 계획

## 1. 개요
- 목적: RTM 문서의 [C] 범주(클라이언트 검증)에 해당하는 입력 검증 로직에 대한 단위 테스트 범위를 식별하고 실행 계획 수립
- 범위: RTM ID R-DAD-005, R-DAD-006, R-DAD-008, R-DB-001, R-DB-002, R-DB-003, R-DB-004, R-DB-005, R-G-004, R-G-005, R-G-006, R-G-009, R-G-010, R-G-016, R-G-019, R-G-023, R-GID-006, R-GRD-008, R-GRD-009, R-GRD-010, R-GRD-011, R-H-008, R-ID-004, R-JWT-001, R-JWT-002, R-JWT-003, R-JWT-004, R-JWT-005, R-JWT-006, R-JWT-007, R-JWT-008, R-JWT-009, R-JWT-010, R-JWT-011, R-JWT-012, R-JWT-013, R-L-007, R-L-009, R-L-010, R-L-011, R-L-012, R-LoD-005, R-LoD-007, R-LoD-008, R-M-007, R-M-008, R-M-009, R-M-010, R-M-012, R-MD-003, R-MD-004, R-MD-005, R-MD-006, R-RD-005, R-RuD-004, R-SC-004, R-SC-005, R-SC-006, R-SetD-013, R-SetPD-007, R-SetPD-008, R-SetPD-009, R-SetPD-010, R-SetPD-012 총 64건
- 선행 작업: 각 컴포넌트의 의존성 주입을 위해 Stub/Fake 객체 구성, LiveData 관찰 도우미 유틸 준비

## 2. 테스트 환경 및 실행 방법
- 테스트 프레임워크: JUnit4 + Truth/Hamcrest (Android JVM 단위 테스트)
- 모듈별 테스트 디렉터리: `src/test/java/...` 위치에 신규 테스트 클래스 추가
- 실행 명령: `./gradlew testDebugUnitTest` (전체) 또는 `./gradlew :<module>:testDebugUnitTest` 로 모듈 단위 실행
- LiveData 테스트 시 `InstantTaskExecutorRule`, 코루틴이 필요한 경우 `runBlockingTest` 활용 (필요 시)

## 3. 커버리지 맵
| Prefix | RTM IDs | 주요 대상 컴포넌트 |
| --- | --- | --- |
| R-L | R-L-007, R-L-009, R-L-010, R-L-011, R-L-012 | feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingDialogViewModel.java |
| R-H | R-H-008 | application/src/main/java/com/example/application/usecase/HelloHandshakeUseCase.java |
| R-MD | R-MD-003, R-MD-004, R-MD-005, R-MD-006 | application/src/main/java/com/example/application/session/GameInfoStore.java |
| R-M | R-M-007, R-M-008, R-M-009, R-M-010, R-M-012 | application/src/main/java/com/example/application/session/OmokBoardState.java |
| R-RD | R-RD-005 | data/src/main/java/com/example/data/mapper/UserResponseMapper.java |
| R-SC | R-SC-004, R-SC-005, R-SC-006 | feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/ScoreViewModel.java |
| R-RuD | R-RuD-004 | data/src/main/java/com/example/data/mapper/RuleMapper.java |
| R-ID | R-ID-004 | domain/src/main/java/com/example/domain/user/entity/Identity.java |
| R-LoD | R-LoD-005, R-LoD-007, R-LoD-008 | feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/LogoutDialogViewModel.java |
| R-DAD | R-DAD-005, R-DAD-006, R-DAD-008 | feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/DeleteAccountDialogViewModel.java |
| R-SetD | R-SetD-013 | feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingDialogViewModel.java |
| R-SetPD | R-SetPD-007, R-SetPD-008, R-SetPD-009, R-SetPD-010, R-SetPD-012 | feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingProfileDialogViewModel.java |
| R-G | R-G-004, R-G-005, R-G-006, R-G-009, R-G-010, R-G-016, R-G-019, R-G-023 | application/src/main/java/com/example/application/session/GameInfoStore.java<br>application/src/main/java/com/example/application/session/GameTurnState.java |
| R-GRD | R-GRD-008, R-GRD-009, R-GRD-010, R-GRD-011 | data/src/main/java/com/example/data/repository/realtime/codec/PostGameDecisionMessageCodec.java |
| R-GID | R-GID-006 | feature_game/src/main/java/com/example/feature_game/game/presentation/viewmodel/GameInfoDialogViewModel.java |
| R-DB | R-DB-001, R-DB-002, R-DB-003, R-DB-004, R-DB-005 | data/src/main/java/com/example/data/datasource/DefaultPhpServerDataSource.java |
| R-JWT | R-JWT-001, R-JWT-002, R-JWT-003, R-JWT-004, R-JWT-005, R-JWT-006, R-JWT-007, R-JWT-008, R-JWT-009, R-JWT-010, R-JWT-011, R-JWT-012, R-JWT-013 | domain/src/main/java/com/example/domain/user/value/AccessToken.java |

## 4. 테스트 케이스 상세
### R-L 그룹

#### R-L-007 — Google 연동 버튼 초기 클릭 검증
- 테스트 식별자: TC-[C]-R-L-007-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingDialogViewModel.java`
- 테스트 절차:
  - 1) guest 세션을 반환하는 FakeUserSessionStore 로 ViewModel 생성
  - 2) onGoogleSettingClicked() 호출 후 이벤트 및 진행 상태 수집
- 입력 데이터:
  - 초기 세션: provider=AuthProvider.GUEST
  - googleLinkInProgress 초기값=false
- 기대 결과:
  - SettingDialogEvent.REQUEST_GOOGLE_SIGN_IN 이벤트가 1회 발생
  - googleLinkInProgress LiveData 가 true 로 전환
- 실제 결과: _(작성 예정)_

#### R-L-009 — 이미 연동된 계정에 대한 중복 요청 차단
- 테스트 식별자: TC-[C]-R-L-009-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingDialogViewModel.java`
- 테스트 절차:
  - 1) provider=GOOGLE 세션을 주입하여 ViewModel 생성
  - 2) onGoogleSettingClicked() 호출 후 이벤트 및 상태 변화를 관찰
- 입력 데이터:
  - 초기 세션: provider=AuthProvider.GOOGLE
  - googleLinkInProgress 초기값=false
- 기대 결과:
  - 새 SettingDialogEvent 가 방출되지 않음
  - googleLinkInProgress LiveData 가 false 유지
- 실제 결과: _(작성 예정)_

#### R-L-010 — 빈 자격 증명 거부 및 에러 이벤트 전파
- 테스트 식별자: TC-[C]-R-L-010-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingDialogViewModel.java`
- 테스트 절차:
  - 1) ViewModel 생성 후 googleLinkInProgress 값을 false 로 초기화 확인
  - 2) onGoogleCredentialReceived("   ") 호출 후 events LiveData 확인
- 입력 데이터:
  - providerIdToken 입력: 공백 문자열 "   "
- 기대 결과:
  - googleLinkInProgress 가 false 로 유지
  - SettingDialogEvent.SHOW_ERROR 이벤트가 기본 오류 메시지와 함께 발생
- 실제 결과: _(작성 예정)_

#### R-L-011 — 성공 응답 처리 시 링크 완료 플래그 업데이트
- 테스트 식별자: TC-[C]-R-L-011-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingDialogViewModel.java`
- 테스트 절차:
  - 1) 즉시 성공을 반환하는 Fake LinkGoogleAccountUseCase 주입
  - 2) onGoogleCredentialReceived(validToken) 호출 후 future 완료 대기
  - 3) googleLinked / events LiveData 검증
- 입력 데이터:
  - providerIdToken 입력: "ya29.a0AfB_valid_token"
  - UseCase 결과: UResult.Ok(None)
- 기대 결과:
  - googleLinkInProgress 가 false 로 복구
  - isGoogleLinked LiveData 가 true 로 전환
  - SettingDialogEvent.SHOW_SUCCESS 이벤트가 성공 메시지와 함께 발생
- 실제 결과: _(작성 예정)_

#### R-L-012 — 오류 메시지 누락 시 폴백 메시지 적용
- 테스트 식별자: TC-[C]-R-L-012-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingDialogViewModel.java`
- 테스트 절차:
  - 1) ViewModel 생성 후 onGoogleSignInFailed("   ") 호출
  - 2) events LiveData 로 전달된 메시지 확인
- 입력 데이터:
  - 실패 메시지 입력: 공백 문자열
- 기대 결과:
  - SettingDialogEvent.SHOW_ERROR 이벤트가 기본 폴백 메시지를 포함
  - googleLinkInProgress LiveData 가 false 로 유지
- 실제 결과: _(작성 예정)_


### R-H 그룹

#### R-H-008 — 헬로 핸드셰이크 입력 정규화
- 테스트 식별자: TC-[C]-R-H-008-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/usecase/HelloHandshakeUseCase.java`
- 테스트 절차:
  - 1) Mock RealtimeRepository.hello(String) 구성 후 run(null) 실행
  - 2) 전달된 payload 가 "" 인지 검증
  - 3) run(" ping ") 실행 후 payload 가 원본과 동일한지 확인
- 입력 데이터:
  - 입력1: None
  - 입력2: " ping "
- 기대 결과:
  - 첫 호출은 빈 문자열 "" 전송
  - 두 번째 호출은 공백 유지된 " ping " 전송
- 실제 결과: _(작성 예정)_


### R-MD 그룹

#### R-MD-003 — 게임 모드 업데이트 시 null 거부
- 테스트 식별자: TC-[C]-R-MD-003-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameInfoStore.java`
- 테스트 절차:
  - 1) GameInfoStore 생성
  - 2) update(null) 호출 시 예외를 검증
- 입력 데이터:
  - 입력: mode=None
- 기대 결과:
  - IllegalArgumentException("mode == null") 발생
- 실제 결과: _(작성 예정)_

#### R-MD-004 — 매치 상태 IDLE 전환 시 세션 초기화
- 테스트 식별자: TC-[C]-R-MD-004-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameInfoStore.java`
- 테스트 절차:
  - 1) 임의의 GameSessionInfo 주입 후 updateGameSession 호출
  - 2) updateMatchState(MatchState.IDLE) 호출
  - 3) currentGameSession 및 boardStore 상태 확인
- 입력 데이터:
  - 초기 세션: sessionId="session-123"
  - 매치 상태 입력: MatchState.IDLE
- 기대 결과:
  - currentGameSession 이 null 로 초기화
  - boardStore.getCurrentBoardState() 가 빈 보드로 초기화
- 실제 결과: _(작성 예정)_

#### R-MD-005 — 턴 상태 업데이트 시 normalize 적용
- 테스트 식별자: TC-[C]-R-MD-005-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameInfoStore.java`
- 테스트 절차:
  - 1) remainingSeconds=-5 를 가진 GameTurnState.active(...) 생성
  - 2) updateTurnState(state) 호출 후 turnStateStream 값 확인
- 입력 데이터:
  - currentPlayerId: "user-1"
  - remainingSeconds: -5
- 기대 결과:
  - turnStateStream 이 remainingSeconds=0 으로 정규화된 상태를 방송
- 실제 결과: _(작성 예정)_

#### R-MD-006 — 활성 규칙 업데이트 시 null 거부
- 테스트 식별자: TC-[C]-R-MD-006-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameInfoStore.java`
- 테스트 절차:
  - 1) GameInfoStore 생성
  - 2) updateActiveRules(null) 호출 시 예외 확인
- 입력 데이터:
  - 입력: rules=None
- 기대 결과:
  - IllegalArgumentException("rules == null") 발생
- 실제 결과: _(작성 예정)_


### R-M 그룹

#### R-M-007 — 오목 보드 생성 시 기본 셀 초기화
- 테스트 식별자: TC-[C]-R-M-007-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/OmokBoardState.java`
- 테스트 절차:
  - 1) OmokBoardState.create(19, 19) 호출
  - 2) isEmpty(), getPlacements() 로 초기 상태 검증
- 입력 데이터:
  - 보드 크기: 19x19
- 기대 결과:
  - 모든 셀이 OmokStoneType.EMPTY
  - getPlacements() 가 빈 리스트 반환
- 실제 결과: _(작성 예정)_

#### R-M-008 — 보드 인덱스 범위 검증
- 테스트 식별자: TC-[C]-R-M-008-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/OmokBoardState.java`
- 테스트 절차:
  - 1) OmokBoardState.create(15, 15) 생성
  - 2) withStone(placement) 에서 x=20, y=0 입력 시 예외 확인
- 입력 데이터:
  - placement: (20, 0, OmokStoneType.BLACK)
- 기대 결과:
  - IndexOutOfBoundsException 발생
- 실제 결과: _(작성 예정)_

#### R-M-009 — 돌 제거 시 셀 초기화 확인
- 테스트 식별자: TC-[C]-R-M-009-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/OmokBoardState.java`
- 테스트 절차:
  - 1) 빈 보드에서 withStone(3,4,BLACK) 호출해 돌 배치
  - 2) withoutStone(3,4) 호출 후 해당 좌표 조회
- 입력 데이터:
  - placement: (3, 4, OmokStoneType.BLACK)
- 기대 결과:
  - withoutStone 이후 getStone(3,4) 가 OmokStoneType.EMPTY 반환
- 실제 결과: _(작성 예정)_

#### R-M-010 — 보드 초기화(clear) 확인
- 테스트 식별자: TC-[C]-R-M-010-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/OmokBoardState.java`
- 테스트 절차:
  - 1) 임의의 돌 여러 개 배치
  - 2) clear() 호출 후 isEmpty() 확인
- 입력 데이터:
  - 배치: (0,0,BLACK), (1,1,WHITE)
- 기대 결과:
  - clear() 결과 모든 셀이 EMPTY, isEmpty()=true
- 실제 결과: _(작성 예정)_

#### R-M-012 — 셀 배열 길이 불일치 시 예외
- 테스트 식별자: TC-[C]-R-M-012-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/OmokBoardState.java`
- 테스트 절차:
  - 1) OmokStoneType[3] 배열 준비
  - 2) fromCells(2,2,cells) 호출 시 예외 확인
- 입력 데이터:
  - width=2, height=2, cells length=3
- 기대 결과:
  - IllegalArgumentException("cells length ...") 발생
- 실제 결과: _(작성 예정)_


### R-RD 그룹

#### R-RD-005 — 랭킹 리스트 파싱 시 입력 검증
- 테스트 식별자: TC-[C]-R-RD-005-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/mapper/UserResponseMapper.java`
- 테스트 절차:
  - 1) ranking 키에 Map 객체가 아닌 값을 포함한 body 전달
  - 2) mapRankingEntries(body, maxEntries=5) 호출
  - 3) 잘못된 항목이 무시되는지 확인
- 입력 데이터:
  - ranking 입력: [{"rank":1,"user_id":"user-1","display_name":"Alpha","score":1200}, "잘못된값"]
- 기대 결과:
  - 반환 리스트에 첫 번째 항목만 포함되고, 잘못된 값은 무시됨
- 실제 결과: _(작성 예정)_


### R-SC 그룹

#### R-SC-004 — 사용자 점수 하한 보정
- 테스트 식별자: TC-[C]-R-SC-004-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/ScoreViewModel.java`
- 테스트 절차:
  - 1) 음수 점수를 가진 User 로 updateScoreFromUser 실행
  - 2) currentScore LiveData 값 확인
- 입력 데이터:
  - 입력 점수: -50
- 기대 결과:
  - currentScore 값이 MIN_SCORE(0) 으로 보정
- 실제 결과: _(작성 예정)_

#### R-SC-005 — 점수 구간 인덱스 계산 검증
- 테스트 식별자: TC-[C]-R-SC-005-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/ScoreViewModel.java`
- 테스트 절차:
  - 1) resolveMilestoneIndexForLimit 호출 (limitScore=0, 150, 3200)
- 입력 데이터:
  - limitScore 입력: 0 / 150 / 3200
- 기대 결과:
  - 0 → 0번 인덱스, 150 → 2번 인덱스, 3200 → 최댓값 인덱스로 클램프
- 실제 결과: _(작성 예정)_

#### R-SC-006 — 룰 코드 할당 시 누락 처리
- 테스트 식별자: TC-[C]-R-SC-006-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/ScoreViewModel.java`
- 테스트 절차:
  - 1) assignRulesToMilestones({1:["RULE_A"], 5:["RULE_B"]}) 호출
  - 2) milestones LiveData 갱신 값 확인
- 입력 데이터:
  - 코드 맵: {1:["RULE_A"], 5:["RULE_B"]}
- 기대 결과:
  - 지정된 인덱스만 코드가 채워지고 나머지는 빈 리스트 유지
- 실제 결과: _(작성 예정)_


### R-RuD 그룹

#### R-RuD-004 — 룰 코드 정규화
- 테스트 식별자: TC-[C]-R-RuD-004-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/mapper/RuleMapper.java`
- 테스트 절차:
  - 1) code="speed-game" 을 가진 RuleDto 구성
  - 2) toDomain(dto) 호출 후 RuleCode 확인
- 입력 데이터:
  - 입력 코드: "speed-game"
- 기대 결과:
  - RuleCode 가 SPEED_GAME 으로 정규화되어 매핑
- 실제 결과: _(작성 예정)_


### R-ID 그룹

#### R-ID-004 — Identity 생성 시 토큰 검증
- 테스트 식별자: TC-[C]-R-ID-004-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/entity/Identity.java`
- 테스트 절차:
  - 1) AccessToken/RefreshToken 이 유효한 문자열로 Identity.of 호출
  - 2) AccessToken 포맷이 잘못된 문자열로 재호출 시 예외 확인
- 입력 데이터:
  - 유효 토큰: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiMTIzNCJ9.SflKxwRJS"
  - 부적절 토큰: "not-a-jwt"
- 기대 결과:
  - 유효 입력은 Identity 생성 성공
  - 부적절 토큰은 RuntimeException 발생
- 실제 결과: _(작성 예정)_


### R-LoD 그룹

#### R-LoD-005 — 로그아웃 다이얼로그 기본 메시지 초기화
- 테스트 식별자: TC-[C]-R-LoD-005-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/LogoutDialogViewModel.java`
- 테스트 절차:
  - 1) ViewModel 생성 후 initialize("로그아웃 하시겠습니까?") 호출
  - 2) 같은 메시지를 다시 전달해도 값이 변경되지 않는지 확인
- 입력 데이터:
  - 기본 메시지: "로그아웃 하시겠습니까?"
- 기대 결과:
  - 첫 호출 후 message LiveData 가 기본 메시지 보관
  - 두 번째 호출은 상태 변화 없음
- 실제 결과: _(작성 예정)_

#### R-LoD-007 — 진행 상태 토글 입력 검증
- 테스트 식별자: TC-[C]-R-LoD-007-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/LogoutDialogViewModel.java`
- 테스트 절차:
  - 1) setInProgress(true) 호출
  - 2) 이어서 setInProgress(false) 호출하며 LiveData 변화 확인
- 입력 데이터:
  - 입력 상태: true → false
- 기대 결과:
  - inProgress LiveData 가 true, 이후 false 로 업데이트
- 실제 결과: _(작성 예정)_

#### R-LoD-008 — 커스텀 메시지 입력 반영
- 테스트 식별자: TC-[C]-R-LoD-008-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/LogoutDialogViewModel.java`
- 테스트 절차:
  - 1) setMessage("다른 계정으로 로그인하시겠습니까?") 호출
  - 2) message LiveData 값 검증
- 입력 데이터:
  - 입력 메시지: "다른 계정으로 로그인하시겠습니까?"
- 기대 결과:
  - message LiveData 가 입력 문자열로 갱신
- 실제 결과: _(작성 예정)_


### R-DAD 그룹

#### R-DAD-005 — 계정 삭제 다이얼로그 기본 문구 적용
- 테스트 식별자: TC-[C]-R-DAD-005-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/DeleteAccountDialogViewModel.java`
- 테스트 절차:
  - 1) initialize("계정을 삭제하시겠습니까?") 호출
  - 2) message LiveData 초기값 확인
- 입력 데이터:
  - 기본 메시지: "계정을 삭제하시겠습니까?"
- 기대 결과:
  - message LiveData 가 기본 메시지를 유지
- 실제 결과: _(작성 예정)_

#### R-DAD-006 — 동의 체크 토글 입력 검증
- 테스트 식별자: TC-[C]-R-DAD-006-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/DeleteAccountDialogViewModel.java`
- 테스트 절차:
  - 1) setAcknowledged(true) 호출
  - 2) setAcknowledged(false) 호출하며 LiveData 변화 확인
- 입력 데이터:
  - 입력 상태: true → false
- 기대 결과:
  - acknowledged LiveData 가 true, 이후 false 로 전환
- 실제 결과: _(작성 예정)_

#### R-DAD-008 — 진행 상태 토글 검증
- 테스트 식별자: TC-[C]-R-DAD-008-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/DeleteAccountDialogViewModel.java`
- 테스트 절차:
  - 1) setInProgress(true) 호출 후 LiveData 확인
  - 2) setInProgress(false) 호출하여 해제 확인
- 입력 데이터:
  - 입력 상태: true → false
- 기대 결과:
  - inProgress LiveData 가 true, 이후 false 로 갱신
- 실제 결과: _(작성 예정)_


### R-SetD 그룹

#### R-SetD-013 — Google 연동 버튼 활성화 조건 검증
- 테스트 식별자: TC-[C]-R-SetD-013-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingDialogViewModel.java`
- 테스트 절차:
  - 1) 초기 상태에서 isGoogleButtonEnabled LiveData 값 확인
  - 2) googleLinkInProgress=true 로 설정 후 상태 확인
  - 3) 링크 성공 뒤 버튼 비활성화 여부 확인
- 입력 데이터:
  - 초기 세션: provider=AuthProvider.GUEST
  - 후속 단계: googleLinkInProgress=true, isGoogleLinked=true
- 기대 결과:
  - 진행 중에는 버튼 비활성화
  - 연동 완료 후에도 중복 요청 방지를 위해 버튼 비활성화 유지
- 실제 결과: _(작성 예정)_


### R-SetPD 그룹

#### R-SetPD-007 — 닉네임 입력 반영
- 테스트 식별자: TC-[C]-R-SetPD-007-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingProfileDialogViewModel.java`
- 테스트 절차:
  - 1) onNicknameChanged("Nova") 호출
  - 2) nickname LiveData 값 확인
- 입력 데이터:
  - 닉네임 입력: "Nova"
- 기대 결과:
  - nickname LiveData 가 "Nova" 로 갱신
- 실제 결과: _(작성 예정)_

#### R-SetPD-008 — 프로필 아이콘 선택 반영
- 테스트 식별자: TC-[C]-R-SetPD-008-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingProfileDialogViewModel.java`
- 테스트 절차:
  - 1) onIconSelected(12) 호출
  - 2) selectedIcon LiveData 확인
- 입력 데이터:
  - 아이콘 코드: 12
- 기대 결과:
  - selectedIcon LiveData 가 12 로 갱신
- 실제 결과: _(작성 예정)_

#### R-SetPD-009 — 아이콘 선택 해제
- 테스트 식별자: TC-[C]-R-SetPD-009-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingProfileDialogViewModel.java`
- 테스트 절차:
  - 1) onIconSelected(7) 호출로 선택 후
  - 2) clearIconSelection() 호출
- 입력 데이터:
  - 초기 선택: 7
- 기대 결과:
  - clearIconSelection 이후 selectedIcon LiveData 가 null 로 초기화
- 실제 결과: _(작성 예정)_

#### R-SetPD-010 — 성공 메시지 상태 반영
- 테스트 식별자: TC-[C]-R-SetPD-010-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingProfileDialogViewModel.java`
- 테스트 절차:
  - 1) showSuccess("저장되었습니다") 호출
  - 2) status LiveData 가 성공 메시지를 보관하는지 확인
- 입력 데이터:
  - 메시지: "저장되었습니다"
- 기대 결과:
  - status LiveData 가 isSuccess=true, message="저장되었습니다" 값으로 갱신
- 실제 결과: _(작성 예정)_

#### R-SetPD-012 — 에러 메시지 표시 및 초기화
- 테스트 식별자: TC-[C]-R-SetPD-012-UT01
- 대상 컴포넌트: `feature_home/src/main/java/com/example/feature_home/presentation/viewmodel/SettingProfileDialogViewModel.java`
- 테스트 절차:
  - 1) showError("네트워크 오류") 호출
  - 2) clearStatus() 호출 후 status LiveData 확인
- 입력 데이터:
  - 오류 메시지: "네트워크 오류"
- 기대 결과:
  - showError 후 status 가 isSuccess=false, message="네트워크 오류"
  - clearStatus 실행 후 status 가 null 로 초기화
- 실제 결과: _(작성 예정)_


### R-G 그룹

#### R-G-004 — 활성 턴 상태 생성 시 플레이어 ID 검증
- 테스트 식별자: TC-[C]-R-G-004-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameTurnState.java`
- 테스트 절차:
  - 1) GameTurnState.active(null, ...) 호출 시 예외 확인
- 입력 데이터:
  - currentPlayerId 입력: null
- 기대 결과:
  - NullPointerException 발생 (Objects.requireNonNull)
- 실제 결과: _(작성 예정)_

#### R-G-005 — 남은 시간 음수 보정
- 테스트 식별자: TC-[C]-R-G-005-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameTurnState.java`
- 테스트 절차:
  - 1) GameTurnState.active("user", -10, ...) 생성
  - 2) getRemainingSeconds() 값 확인
- 입력 데이터:
  - remainingSeconds 입력: -10
- 기대 결과:
  - remainingSeconds 가 0 으로 보정되어 저장
- 실제 결과: _(작성 예정)_

#### R-G-006 — deactivate 호출 시 Idle 상태 유지
- 테스트 식별자: TC-[C]-R-G-006-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameTurnState.java`
- 테스트 절차:
  - 1) GameTurnState.active("user", 15, ...) 생성
  - 2) deactivate() 호출 후 isActive/remainingSeconds 확인
- 입력 데이터:
  - remainingSeconds 초기값: 15
- 기대 결과:
  - deactivate 결과 isActive=false, remainingSeconds=15 유지
- 실제 결과: _(작성 예정)_

#### R-G-009 — normalize 가 비활성 상태를 idle 로 정규화
- 테스트 식별자: TC-[C]-R-G-009-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameTurnState.java`
- 테스트 절차:
  - 1) GameTurnState.idle().withRemainingSeconds(8) 준비
  - 2) normalize() 호출 결과 검증
- 입력 데이터:
  - 입력 상태: active=false, remainingSeconds=8
- 기대 결과:
  - normalize 결과 isActive=false, remainingSeconds=8 유지하되 idleWithSeconds 로 반환
- 실제 결과: _(작성 예정)_

#### R-G-010 — ensureActive 비활성 상태 예외
- 테스트 식별자: TC-[C]-R-G-010-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameTurnState.java`
- 테스트 절차:
  - 1) GameTurnState.idle() 생성
  - 2) ensureActive() 호출 시 예외 확인
- 입력 데이터:
  - 입력 상태: active=false
- 기대 결과:
  - IllegalStateException("Cannot ensure active ...") 발생
- 실제 결과: _(작성 예정)_

#### R-G-016 — 잔여 시간 업데이트 시 정규화
- 테스트 식별자: TC-[C]-R-G-016-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameInfoStore.java`
- 테스트 절차:
  - 1) GameInfoStore 생성 후 updateTurnState(GameTurnState.active(...remainingSeconds=20)) 호출
  - 2) updateRemainingSeconds(-3) 호출 후 turnStateStream 확인
- 입력 데이터:
  - remainingSeconds 조정 입력: -3
- 기대 결과:
  - turnStateStream 이 remainingSeconds=0 으로 보정된 상태 방송
- 실제 결과: _(작성 예정)_

#### R-G-019 — 참가자 정보 없는 경우 setTurnState 가 idle 로 리셋
- 테스트 식별자: TC-[C]-R-G-019-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameInfoStore.java`
- 테스트 절차:
  - 1) currentGameSession 을 null 로 유지한 상태에서 setTurnState 호출
  - 2) turnStateStream 값 확인
- 입력 데이터:
  - currentGameSession: None
- 기대 결과:
  - setTurnState 호출 후 turnStateStream 이 idle 상태로 세팅
- 실제 결과: _(작성 예정)_

#### R-G-023 — 현재 턴 참가자 조회 검증
- 테스트 식별자: TC-[C]-R-G-023-UT01
- 대상 컴포넌트: `application/src/main/java/com/example/application/session/GameInfoStore.java`
- 테스트 절차:
  - 1) 두 참가자를 가진 GameSessionInfo 주입
  - 2) GameTurnState.active 로 turnState 설정
  - 3) getCurrentTurnParticipant() 결과 검증
- 입력 데이터:
  - 참가자: userA, userB
  - active 턴: userB
- 기대 결과:
  - getCurrentTurnParticipant 가 userB 참가자 정보를 반환
- 실제 결과: _(작성 예정)_


### R-GRD 그룹

#### R-GRD-008 — PostGameDecision encode 입력 null 거부
- 테스트 식별자: TC-[C]-R-GRD-008-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/repository/realtime/codec/PostGameDecisionMessageCodec.java`
- 테스트 절차:
  - 1) encode(null) 호출 시 예외 확인
- 입력 데이터:
  - decision 입력: null
- 기대 결과:
  - IllegalArgumentException("decision == null") 발생
- 실제 결과: _(작성 예정)_

#### R-GRD-009 — UNKNOWN 결정 거부
- 테스트 식별자: TC-[C]-R-GRD-009-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/repository/realtime/codec/PostGameDecisionMessageCodec.java`
- 테스트 절차:
  - 1) encode(PostGameDecisionOption.UNKNOWN) 호출 시 예외 확인
- 입력 데이터:
  - decision 입력: UNKNOWN
- 기대 결과:
  - IllegalArgumentException("decision must be REMATCH or LEAVE") 발생
- 실제 결과: _(작성 예정)_

#### R-GRD-010 — 빈 페이로드 디코딩 처리
- 테스트 식별자: TC-[C]-R-GRD-010-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/repository/realtime/codec/PostGameDecisionMessageCodec.java`
- 테스트 절차:
  - 1) decodeAck(new byte[0]) 호출
- 입력 데이터:
  - payload: 빈 배열
- 기대 결과:
  - 반환값이 PostGameDecisionAck.unknown 로 매핑
- 실제 결과: _(작성 예정)_

#### R-GRD-011 — ERROR 응답 파싱
- 테스트 식별자: TC-[C]-R-GRD-011-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/repository/realtime/codec/PostGameDecisionMessageCodec.java`
- 테스트 절차:
  - 1) status="ERROR", reason="TIMEOUT" 을 가진 JSON 페이로드 전달
  - 2) decodeAck 호출 결과 확인
- 입력 데이터:
  - payload: b'{"status":"ERROR","reason":"TIMEOUT"}'
- 기대 결과:
  - PostGameDecisionAck.error 로 파싱되어 reason=TIMEOUT 반환
- 실제 결과: _(작성 예정)_


### R-GID 그룹

#### R-GID-006 — 게임 정보 다이얼로그 활성 룰 코드 정리
- 테스트 식별자: TC-[C]-R-GID-006-UT01
- 대상 컴포넌트: `feature_game/src/main/java/com/example/feature_game/game/presentation/viewmodel/GameInfoDialogViewModel.java`
- 테스트 절차:
  - 1) 공백과 null 이 섞인 룰 코드 리스트를 가진 GameInfoStore 스텁 구성
  - 2) ViewModel 생성 후 activeRuleCodes LiveData 관찰
- 입력 데이터:
  - 활성 코드 입력: [" RULE_A ", None, "", "RULE_B"]
- 기대 결과:
  - LiveData 가 ["RULE_A", "RULE_B"] 로 정리된 스냅샷을 방출
- 실제 결과: _(작성 예정)_


### R-DB 그룹

#### R-DB-001 — HTTP 응답 null 처리
- 테스트 식별자: TC-[C]-R-DB-001-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/datasource/DefaultPhpServerDataSource.java`
- 테스트 절차:
  - 1) parseResponse(null) 호출 시 예외 확인
- 입력 데이터:
  - response: None
- 기대 결과:
  - IOException("response == null") 발생
- 실제 결과: _(작성 예정)_

#### R-DB-002 — 성공 응답 JSON 파싱
- 테스트 식별자: TC-[C]-R-DB-002-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/datasource/DefaultPhpServerDataSource.java`
- 테스트 절차:
  - 1) body="{"ok":true}" 인 HttpResponse 스텁 전달
  - 2) parseResponse 호출 후 body Map 확인
- 입력 데이터:
  - status:200, body:'{"ok":true}'
- 기대 결과:
  - Response.body() 가 {"ok": True} 로 매핑
- 실제 결과: _(작성 예정)_

#### R-DB-003 — 비 JSON 응답 보존
- 테스트 식별자: TC-[C]-R-DB-003-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/datasource/DefaultPhpServerDataSource.java`
- 테스트 절차:
  - 1) body="<!doctype html>..." 인 HttpResponse 전달
  - 2) parseResponse 호출
- 입력 데이터:
  - status:500, body:'<!doctype html>error'
- 기대 결과:
  - Response.body() 가 {'body': '<!doctype html>error'} 로 구성
- 실제 결과: _(작성 예정)_

#### R-DB-004 — Request 헤더 기본 값 검증
- 테스트 식별자: TC-[C]-R-DB-004-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/datasource/DefaultPhpServerDataSource.java`
- 테스트 절차:
  - 1) post(Request.defaultRequest(Path.LOGIN)) 호출 시 전송되는 HttpRequest 캡처
  - 2) Content-Type, Accept, X-Request-ID 헤더 확인
- 입력 데이터:
  - Path: LOGIN
- 기대 결과:
  - 3개 헤더 값이 예상 값으로 설정됨 (application/json, application/json, UUID)
- 실제 결과: _(작성 예정)_

#### R-DB-005 — 요청 경로 매핑 검증
- 테스트 식별자: TC-[C]-R-DB-005-UT01
- 대상 컴포넌트: `data/src/main/java/com/example/data/datasource/DefaultPhpServerDataSource.java`
- 테스트 절차:
  - 1) Path.INFO 로 Request 생성 후 post 호출
  - 2) HttpRequest.url 이 https://bamsol.net/public/info.php 인지 확인
- 입력 데이터:
  - Path: INFO
- 기대 결과:
  - 요청 URL 이 Path.toBasePath() 결과와 일치
- 실제 결과: _(작성 예정)_


### R-JWT 그룹

#### R-JWT-001 — JWT 기본 포맷 허용
- 테스트 식별자: TC-[C]-R-JWT-001-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of(validToken) 호출
  - 2) getValue() 결과가 입력과 동일한지 확인
- 입력 데이터:
  - 유효 토큰: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTIzNCIsInJvbGUiOiJndWVzdCJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
- 기대 결과:
  - AccessToken 인스턴스가 생성되고 값이 동일하게 유지
- 실제 결과: _(작성 예정)_

#### R-JWT-002 — 세그먼트 누락 토큰 거부
- 테스트 식별자: TC-[C]-R-JWT-002-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of("invalid.token") 호출
- 입력 데이터:
  - 입력 토큰: "invalid.token"
- 기대 결과:
  - RuntimeException("Invalid JWT token format.") 발생
- 실제 결과: _(작성 예정)_

#### R-JWT-003 — 허용되지 않는 문자 포함 토큰 거부
- 테스트 식별자: TC-[C]-R-JWT-003-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of("abc.def.ghi*") 호출
- 입력 데이터:
  - 입력 토큰: "abc.def.ghi*"
- 기대 결과:
  - RuntimeException("Invalid JWT token format.") 발생
- 실제 결과: _(작성 예정)_

#### R-JWT-004 — 패딩 포함 토큰 허용
- 테스트 식별자: TC-[C]-R-JWT-004-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.abc=") 호출
  - 2) 예외 없이 인스턴스 생성되는지 확인
- 입력 데이터:
  - 입력 토큰: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.abc="
- 기대 결과:
  - AccessToken 인스턴스 생성 성공
- 실제 결과: _(작성 예정)_

#### R-JWT-005 — splitToken 세그먼트 추출
- 테스트 식별자: TC-[C]-R-JWT-005-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of(validToken) 생성
  - 2) splitToken() 호출 후 배열 길이와 각 파트 값 확인
- 입력 데이터:
  - 토큰: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTIzNCIsInJvbGUiOiJndWVzdCJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
- 기대 결과:
  - 반환 배열 길이=3, 두 번째 요소가 payload 세그먼트와 일치
- 실제 결과: _(작성 예정)_

#### R-JWT-006 — 서명 파트 누락 토큰 거부
- 테스트 식별자: TC-[C]-R-JWT-006-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of("header.payload.") 호출
- 입력 데이터:
  - 입력 토큰: "header.payload."
- 기대 결과:
  - RuntimeException("Invalid JWT token format.") 발생
- 실제 결과: _(작성 예정)_

#### R-JWT-007 — payload 디코딩 및 매핑
- 테스트 식별자: TC-[C]-R-JWT-007-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) "eyJ1c2VyX2lkIjoiMTIzNCIsInJhbmsiOjV9" payload 를 가진 토큰으로 AccessToken.of 생성
  - 2) getPayload() 결과의 키/값 검증
- 입력 데이터:
  - 토큰: "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMTIzNCIsInJhbmsiOjV9.signature"
- 기대 결과:
  - payload 맵이 {'user_id': '1234', 'rank': 5} 로 파싱
- 실제 결과: _(작성 예정)_

#### R-JWT-008 — payload JSON 파싱 실패 처리
- 테스트 식별자: TC-[C]-R-JWT-008-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) payload 가 "e30=" (빈 JSON) 이 아닌 잘못된 Base64 문자열인 토큰으로 getPayload 호출
- 입력 데이터:
  - 토큰: "eyJhbGciOiJIUzI1NiJ9.eyJub3RfSlNPTiI6ImJhZCJ9@@@"
- 기대 결과:
  - getPayload 호출 시 JSONException 이 발생하나 catch 되어 빈 맵 반환
- 실제 결과: _(작성 예정)_

#### R-JWT-009 — EMPTY 토큰 사용 제한
- 테스트 식별자: TC-[C]-R-JWT-009-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.EMPTY.splitToken() 호출
- 입력 데이터:
  - 입력: AccessToken.EMPTY
- 기대 결과:
  - RuntimeException("Invalid JWT token format: Expected 3 parts, got 1") 발생
- 실제 결과: _(작성 예정)_

#### R-JWT-010 — null 토큰 거부
- 테스트 식별자: TC-[C]-R-JWT-010-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of(None) 호출 시 NullPointerException 검증
- 입력 데이터:
  - 입력: None
- 기대 결과:
  - NullPointerException("value") 발생
- 실제 결과: _(작성 예정)_

#### R-JWT-011 — payload 재호출 시 독립 맵 반환
- 테스트 식별자: TC-[C]-R-JWT-011-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of(validToken) 생성 후 getPayload() 호출해 map 수정
  - 2) getPayload() 를 다시 호출하여 수정이 반영되지 않는지 확인
- 입력 데이터:
  - 토큰: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTIzNCIsInJvbGUiOiJndWVzdCJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
- 기대 결과:
  - 두 번째 호출 결과가 새 Map 인스턴스로 반환되어 이전 수정이 반영되지 않음
- 실제 결과: _(작성 예정)_

#### R-JWT-012 — Base64 디코딩 오류 전파
- 테스트 식별자: TC-[C]-R-JWT-012-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) payload 부분이 잘못된 Base64 문자열("@@@") 인 토큰으로 getPayload 호출
- 입력 데이터:
  - 토큰: "eyJhbGciOiJIUzI1NiJ9.@@@.signature"
- 기대 결과:
  - IllegalArgumentException 발생하여 테스트에서 예외를 기대
- 실제 결과: _(작성 예정)_

#### R-JWT-013 — splitToken 세그먼트 내용 검증
- 테스트 식별자: TC-[C]-R-JWT-013-UT01
- 대상 컴포넌트: `domain/src/main/java/com/example/domain/user/value/AccessToken.java`
- 테스트 절차:
  - 1) AccessToken.of(validToken) 생성
  - 2) splitToken()[1] 을 Base64 디코딩하여 payload JSON 과 일치하는지 확인
- 입력 데이터:
  - 토큰: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTIzNCIsInJvbGUiOiJndWVzdCJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
- 기대 결과:
  - 디코딩된 payload 가 {"user_id":"1234","role":"guest"} 와 일치
- 실제 결과: _(작성 예정)_

