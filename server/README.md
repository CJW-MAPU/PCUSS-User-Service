# 구현 목록

- [x] 의존성 추가
- [x] yml 파일 작성
- [ ] api 작성
- [ ] ControllerAdvice 생성
- [ ] auth controller 작업
- [ ] user controller 작업
- [ ] Test Coverage 100% 

# my todo

- [x] UserNotFoundException 테스트
- [x] AuthService login 메서드에 jwt 추가
- [ ] security 추가 ()
- [ ] 로그아웃 구현

# 기능

- 사용자 회원가입
  - 회원 저장
  - **존재하는 사용자인가 또한 확인하면 좋을듯**
- 사용자 로그인
  - 존재하지 않는 사용자
- Silent-Refresh
- 로그아웃
- 닉네임, 프로필 수정
- 회원 탈퇴

# controller

- controller advice
    - UserNotFoundException
    - InvalidTokenException
    - TokenExpiredException
    - EmptyTokenException
- auth controller
    - **로그인** GET : login/{UUID}
    - **회원가입** POST : register
    - **로그아웃** POST : logout
    - **Silent-Refresh** GET : silent-refresh
- user controller
    - **사용자 정보 변경** PATCH : users/{user_id}
    - **회원 탈퇴** DELETE : users/{user_id}

# 인지 사항

- 현재 포트는 90000 번으로 Random port 로 지정해야 함
- 존재하는 사용자가 회원가입을 할 때 처리도 필요할듯