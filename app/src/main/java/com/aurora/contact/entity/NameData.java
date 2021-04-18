package com.aurora.contact.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NameData {

    private String fullName;
    private String firstName;
    private String surname;
    private String namePrefix;
    private String middleName;
    private String nameSuffix;
    private String phoneticFirst;
    private String phoneticMiddle;
    private String phoneticLast;
}
