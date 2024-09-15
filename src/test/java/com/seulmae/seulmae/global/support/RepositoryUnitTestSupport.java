package com.seulmae.seulmae.global.support;

import com.seulmae.seulmae.util.MockSetUpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(MockSetUpUtil.class)
public class RepositoryUnitTestSupport {

    @Autowired
    protected MockSetUpUtil mockSetUpUtil;
 }
