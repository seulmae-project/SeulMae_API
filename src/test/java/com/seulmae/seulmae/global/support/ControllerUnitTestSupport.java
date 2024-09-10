package com.seulmae.seulmae.global.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.global.config.TestSecurityConfig;
import com.seulmae.seulmae.user.controller.UserController;
import com.seulmae.seulmae.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = {UserController.class})
@Import(TestSecurityConfig.class)
public class ControllerUnitTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserService userService;
}
