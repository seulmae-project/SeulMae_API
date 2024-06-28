package com.seulmae.seulmae.user.service;

import com.seulmae.seulmae.global.util.SmsCertificationUtil;
import com.seulmae.seulmae.user.dto.request.SmsCertificationRequest;
import com.seulmae.seulmae.global.dao.SmsCertificationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {
    private final SmsCertificationUtil smsCertificationUtil;
    private final SmsCertificationDao smsCertificationDao;


    public void sendSMS(String phoneNumber) {
        String authCode = String.valueOf((int) ((Math.random() * 900_000) + 100_000));
        smsCertificationUtil.sendOne(phoneNumber, authCode);
        smsCertificationDao.createSmsCertification(phoneNumber, authCode);

        // 최대 3번만 보내게 해야해.

    }

    public void verifySMS(SmsCertificationRequest request) {
        // 레디스에 있는 번호와 넘어온 번호가 일치하는지 확인
        // 일치하지 않으면 에러 터뜨리기
        if (!isVerify(request.setPhoneNumber(request.getPhoneNumber()), request.getAuthCode())) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // 일치하면, 쓸모없는 데이터를  redis에서 지우기
        smsCertificationDao.removeSmsCertification(request.getPhoneNumber());

    }

    public boolean isVerify(String phoneNumber, String authCode) {
        // 해당 키를 가지고 있어야 하고,
        // 해당 키의 값이, 들어온  auth코드와 일치해야함.
        return (smsCertificationDao.hasKey(phoneNumber) &&
                smsCertificationDao.getSmsCertification(phoneNumber).equals(authCode));
    }
}
