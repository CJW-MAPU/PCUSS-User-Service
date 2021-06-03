package kr.ac.pcu.cyber.userservice.errors;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super(token);
    }
}
