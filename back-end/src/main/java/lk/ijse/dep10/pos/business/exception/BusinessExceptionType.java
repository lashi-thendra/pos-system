package lk.ijse.dep10.pos.business.exception;

public enum BusinessExceptionType {

    BUSINESS(1000, "Business Exception"),
    DUPLICATE_RECORD(1001, "Duplicate Entry Found");

    private final int code;
    private final String message;

    BusinessExceptionType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode(){return code;}

    public String getMessage(){
        return message;
    }

}
