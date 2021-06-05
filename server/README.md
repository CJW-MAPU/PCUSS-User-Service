# 구현 목록

- [x] 의존성 추가
- [x] yml 파일 작성
- [x] api 작성
- [x] ControllerAdvice 생성
- [x] auth controller 작업
- [x] user controller 작업
- [x] Test Coverage 90%

# my todo

- [x] UserNotFoundException 테스트
- [x] AuthService login 메서드에 jwt 추가
- [x] security 추가 (filter 추가, httpSecurity, authentication 추가)
- [x] 로그아웃 구현
- [x] Role Endpoint 추가
- [x] Test Assertions 문 expected, actual 자리 변경하기
- [x] Entity, Dto 이름 변경하기 : profileUrl -> profileImage
- [x] User update 추가
- [x] 회원 탈퇴 추가
- [x] Coverage 80% 확인하기
- [ ] 리팩토링
- [ ] 권한 확인 추가

# 기능

- 사용자 회원가입
- 사용자 로그인
- Silent-Refresh
  - refresh_token 쿠키에 존재하는 토큰 파싱 -> access_token 만료 -> 새로운 access_token 생성 후 ResponseEntity 반환
- 로그아웃
- Role -> RoleRepository -> SecurityService -> SecurityController
  - 쿠키로부터 토큰 파싱 : `String parseTokenStringFromCookies(Cookie[] cookies, TokenType tokenType);`
    - TokenType.ACCESS_TOKEN
    - TokenType.REFRESH_TOKEN
- 사용자
  - 닉네임, 프로필 수정
  - 회원 탈퇴
  - 파싱된 토큰의 소유자가 가지고 있는 Roles `List<Role> getRolesFromUserId(String userId);`

# controller

- controller advice
    - UserNotFoundException
    - InvalidTokenException
    - TokenExpiredException
    - EmptyTokenException
- auth controller
    - **로그인** GET : login/{UUID}
    - **회원가입** POST : register
    - **로그아웃** GET : logout
    - **Silent-Refresh** GET : silent-refresh
- user controller
    - **사용자 정보 변경** PATCH : users/{id}
      - nickname 변경하기
      - profileImage 변경하기
    - **회원 탈퇴** DELETE : users/{id}
      - unregisteredAt 추가하기

# 인지 사항

- 현재 포트는 90000 번으로 Random port 로 지정해야 함
- 존재하는 사용자가 회원가입을 할 때 처리도 필요할듯