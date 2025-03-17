package com.ohgiraffers.collaboprojectbe.domain.user.dto;

public class UserUpdateDTO {
    private String nickname;
    private String statusMessage;

    public UserUpdateDTO() {}

    public UserUpdateDTO(String nickname, String statusMessage) {
        this.nickname = nickname;
        this.statusMessage = statusMessage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "UserUpdateDTO{" +
                "nickname='" + nickname + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
