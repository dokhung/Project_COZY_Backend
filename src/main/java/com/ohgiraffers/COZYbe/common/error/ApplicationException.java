package com.ohgiraffers.COZYbe.common.error;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApplicationException extends RuntimeException {

    private ErrorCode errorCode;
    private String timestamp;


    public ApplicationException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.timestamp = String.valueOf(LocalDateTime.now());
    }
}