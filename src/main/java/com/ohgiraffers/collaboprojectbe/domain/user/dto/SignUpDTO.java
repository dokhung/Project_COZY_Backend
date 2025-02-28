package com.ohgiraffers.collaboprojectbe.domain.user.dto;

public class SignUpDTO {
    //계정
    private String email;
    //비번
    private String password;
    // 비번중복 확인
    private String confirmPassword;
    //닉네임
    private String nickname;
    //이메일 수신
    private String receiveEmail;
    // 로봇이 아닌지 맞는지
    private String captcha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getReceiveEmail() {
        return receiveEmail;
    }

    public void setReceiveEmail(String receiveEmail) {
        this.receiveEmail = receiveEmail;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @Override
    public String toString() {
        return "SignUpDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", nickname='" + nickname + '\'' +
                ", receiveEmail='" + receiveEmail + '\'' +
                ", captcha='" + captcha + '\'' +
                '}';
    }
}

