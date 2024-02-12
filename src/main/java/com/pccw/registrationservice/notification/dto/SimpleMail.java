package com.pccw.registrationservice.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimpleMail {

    private String mailTo;

    private String subject;

    private String body;

}
