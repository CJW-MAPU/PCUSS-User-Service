package kr.ac.pcu.cyber.userservice.errors;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String uuid) {
        super("[" + uuid + "] 는 존재하지 않는 회원입니다.");
    }
}
