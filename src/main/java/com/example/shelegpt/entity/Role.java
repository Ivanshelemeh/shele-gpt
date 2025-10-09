package com.example.shelegpt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Role {

    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system");

    private final String roleNam;

    public static Role getRole(String roleName) {
        return Arrays.stream(Role.values()).filter(role1 -> role1.roleNam.equals(roleName))
                     .findFirst()
                     .orElseThrow();
    }

}
