package com.seulmae.seulmae.global.config.oauth2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OICDPublicKey {
    private final String kty;
    private final String kid;
    private final String alg;
    private final String use;
    private final String n;
    private final String e;

    public boolean isSameAlg(final String alg) {
        return this.alg.equals(alg);
    }

    public boolean isSameKid(final String kid) {
        return this.kid.equals(kid);
    }

    @JsonCreator
    public OICDPublicKey(@JsonProperty("kty") final String kty,
                         @JsonProperty("kid") final String kid,
                         @JsonProperty("alg") final String alg,
                         @JsonProperty("use") final String use,
                         @JsonProperty("n") final String n,
                         @JsonProperty("e") final String e) {
        this.kty = kty;
        this.kid = kid;
        this.alg = alg;
        this.use = use;
        this.n = n;
        this.e = e;
    }
}
