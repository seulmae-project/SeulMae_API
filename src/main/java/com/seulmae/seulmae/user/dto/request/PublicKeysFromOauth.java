package com.seulmae.seulmae.user.dto.request;

import com.seulmae.seulmae.global.config.oauth2.OICDPublicKey;

import java.util.List;

public interface PublicKeysFromOauth<T extends OICDPublicKey>  {
    List<T> getKeys();

    T getMatchingKey(final String alg, final String kid);

}

