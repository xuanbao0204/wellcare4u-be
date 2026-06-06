package vn.wellcare4u.exception;

public class SlotAlreadyBookedException extends RuntimeException {

    public SlotAlreadyBookedException(String message) {
        super(message);
    }
}