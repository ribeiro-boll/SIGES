package com.bolota.historicodevendas.UserTest;

import java.util.HashMap;
import java.util.Map;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.UserResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerFullTest {
    public static Map<String,Object> register = new HashMap<>();
    public static Map<String,Object> login = new HashMap<>();
    public static Map<String,Object> update = new HashMap<>();
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @BeforeAll
    public static void populateHashmap(){
        register.put("login", "tester_12345");
        register.put("passwordHash", "123456");
        register.put("desiredMonthlyIncome", 6500.0);
        register.put("daysWorkingWeekly", 5);
        register.put("hoursWorkingDaily", 8.0);
        register.put("profitMargin", 120.0);

        update.put("login", "tester_12345");
        update.put("passwordHash", "123456");
        update.put("desiredMonthlyIncome", 7000.0);
        update.put("daysWorkingWeekly", 4.0);
        update.put("hoursWorkingDaily", 4.0);
        update.put("profitMargin", 50.0);

        login.put("login", "tester_12345");
        login.put("password", "123456");

    }
    @Test
    public void shouldCreateVariableSupply() throws Exception {
        //register
        MvcResult result        = mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(register))).andReturn();
        assertEquals(200,result.getResponse().getStatus());
        String jwt = result.getResponse().getContentAsString().trim();
        assertFalse(jwt.isEmpty());
        assertFalse(jwt.isBlank());
        //login
        MvcResult resultLogin   = mockMvc.perform(post("/user/login"   ).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(login))).andReturn();
        assertEquals(200,resultLogin.getResponse().getStatus());
        jwt = resultLogin.getResponse().getContentAsString().trim();
        assertFalse(jwt.isEmpty());
        assertFalse(jwt.isBlank());
        //updateInfo
        MvcResult resultUpdate = mockMvc.perform(patch("/user/update").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(objectMapper.writeValueAsString(update))).andReturn();
        assertEquals(200,resultUpdate.getResponse().getStatus());
    }


}
