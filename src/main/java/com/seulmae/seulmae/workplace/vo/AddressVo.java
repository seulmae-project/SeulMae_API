package com.seulmae.seulmae.workplace.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class AddressVo {
    @Column(name = "main_address")
    private String mainAddress;

    @Column(name = "sub_address")
    private String subAddress;

    public AddressVo(String mainAddress, String subAddress) {
        this.mainAddress = mainAddress;
        this.subAddress = subAddress;
    }

    private void validateAddress(String mainAddress, String subAddress) {
        if (mainAddress == null || mainAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Main address cannot be null or empty");
        }
        if (subAddress == null || subAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Sub address cannot be null or empty");
        }
    }
}
