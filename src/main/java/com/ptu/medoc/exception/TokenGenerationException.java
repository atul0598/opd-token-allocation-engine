package com.ptu.medoc.exception;

import com.ptu.medoc.entity.Patient;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter

public class TokenGenerationException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorCode errorCode;

    // PRIVATE constructor
    public TokenGenerationException(
            String message,
            HttpStatus status,
            ErrorCode errorCode) {

        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }


//    Doctor not found exception
    public static TokenGenerationException doctorNotFound(Long id){
        return new TokenGenerationException(
                "Doctor not found with id: " + id,
                HttpStatus.NOT_FOUND,
                ErrorCode.DOCTOR_NOT_FOUND
        );
    }

    public static TokenGenerationException doctorNotFound(String name){
        return new TokenGenerationException(
                "Doctor not found with name: " + name,
                HttpStatus.NOT_FOUND,
                ErrorCode.DOCTOR_NOT_FOUND
        );
    }


    //    Slot not found exception
    public static TokenGenerationException slotNotFound(Long doctorId){
        return new TokenGenerationException(
                "No slots available for doctor id: " + doctorId,
                HttpStatus.NOT_FOUND,
                ErrorCode.SLOT_NOT_FOUND
        );
    }

//    Slot full exception
    public static TokenGenerationException slotFull(Long slotId){
        return new TokenGenerationException(
                "Slot capacity reached for slot id: " + slotId,
                HttpStatus.CONFLICT,
                ErrorCode.SLOT_FULL
        );
    }

//    Duplicate token exception
    public static TokenGenerationException duplicateToken(Patient patient){
        return new TokenGenerationException(
                "Token already exists for patient: " + patient.getName(),
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_TOKEN
        );
    }

//    Token not found exception
    public static TokenGenerationException tokenNotFound(int tokenNumber){
        return new TokenGenerationException(
                "Token not found with id: " + tokenNumber,
                HttpStatus.NOT_FOUND,
                ErrorCode.TOKEN_NOT_FOUND
        );
    }

//    allocation failed exception
    public static TokenGenerationException allocationFailed(){
        return new TokenGenerationException(
                "Token allocation failed",
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.TOKEN_ALLOCATION_FAILED
        );
    }

    public static Exception invalidTokenState(String s) {
        return new TokenGenerationException(
                "Invalid token state: " + s,
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_TOKEN_STATE
        );
    }
}
