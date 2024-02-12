package com.pccw.registrationservice.user.util;

public class ValidationConstants {

    public static final String USERNAME_PATTERN = "^[a-z0-9_-]{5,15}$";

    public static final String NAME_PATTERN = "^[A-Za-z\\- ]{1,30}$";

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$";
}
