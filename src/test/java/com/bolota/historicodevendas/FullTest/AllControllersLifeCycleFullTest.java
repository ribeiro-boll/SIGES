package com.bolota.historicodevendas.FullTest;

import com.bolota.historicodevendas.Controller.UserController;
import com.bolota.historicodevendas.Resource.FixedSuppliesResource;
import com.bolota.historicodevendas.Resource.UserResource;
import com.bolota.historicodevendas.Resource.VariableSuppliesResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AllControllersLifeCycleFullTest {
    public static Map<String,Object> registerUser   = new HashMap<>();
    public static Map<String,Object> variableSuppliesEntity = new HashMap<>();
    public static Map<String,Object> fixedSuppliesEntity = new HashMap<>();
    public static Map<String,Object> fixedSuppliesEntityUpdate = new HashMap<>();
    public static Map<String, Object> productRegister = new HashMap<>();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    public static void populateHashmap(){
        registerUser.put("login", "tester_123");
        registerUser.put("passwordHash", "123456");
        registerUser.put("desiredMonthlyIncome", 6500.0);
        registerUser.put("daysWorkingWeekly", 5);
        registerUser.put("hoursWorkingDaily", 8.0);
        registerUser.put("profitMargin", 120.0);

        variableSuppliesEntity.put("name","Shampoo Proficional");
        variableSuppliesEntity.put("description","Shampoo de limpeza suave para uso em lavatório");
        variableSuppliesEntity.put("productValue",49.9);
        variableSuppliesEntity.put("measure",1.0);

        fixedSuppliesEntity.put("name","Energia elétrica");
        fixedSuppliesEntity.put("description","Conta mensal de energia do salão");
        fixedSuppliesEntity.put("suppliesValue",320.0);

        fixedSuppliesEntityUpdate.put("name","Energia elétrica");
        fixedSuppliesEntityUpdate.put("description","Conta mensal de energia do salão");
        fixedSuppliesEntityUpdate.put("suppliesValue",20.0);




    }

    @Test
    void shouldImitateFullUserExperience() throws Exception{
        MvcResult register = mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUser))).andReturn();

        assertEquals(200, register.getResponse().getStatus());
        String jwt = register.getResponse().getContentAsString();
        assertFalse(jwt.isEmpty());
        assertFalse(jwt.isBlank());

        MvcResult registerSupply = mockMvc.perform(post("/supplies/register").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(objectMapper.writeValueAsString(variableSuppliesEntity))).andReturn();
        assertEquals(200, registerSupply.getResponse().getStatus());
        String uuidVariable = registerSupply.getResponse().getContentAsString();

        MvcResult registerSupplyFixed = mockMvc.perform(post("/supplies/register_fixed").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(objectMapper.writeValueAsString(fixedSuppliesEntity))).andReturn();
        assertEquals(200, registerSupplyFixed.getResponse().getStatus());
        String uuidFixed = registerSupplyFixed.getResponse().getContentAsString();

        productRegister.put("name", "Corte feminino com escova");
        productRegister.put("description", "Atendimento completo com corte, lavagem e finalização escovada.");
        productRegister.put("serviceType", "SERVICE");
        productRegister.put("category", "Cabelo");
        productRegister.put("quantity", 1);
        productRegister.put("averageServiceDurationMinutes", 75);
        productRegister.put("salePrice", 120.0);

        List<String> variableSuppliesUsedUUID = new ArrayList<>();
        variableSuppliesUsedUUID.add(uuidVariable);
        productRegister.put("variableSuppliesUsedUUID", variableSuppliesUsedUUID);

        Map<String, Object> variableSuppliesQuantityUsed = new HashMap<>();
        variableSuppliesQuantityUsed.put(uuidVariable,0.5);
        productRegister.put("variableSuppliesQuantityUsed", variableSuppliesQuantityUsed);

        List<String> fixedSuppliesUsedUUID = new ArrayList<>();
        fixedSuppliesUsedUUID.add(uuidFixed);
        productRegister.put("fixedSuppliesUsedUUID", fixedSuppliesUsedUUID);

        productRegister.put("serviceNotes", "Cliente pediu finalização mais alinhada nas pontas.");

        MvcResult registerService = Objects.requireNonNull(mockMvc.perform(post("/product/register").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(objectMapper.writeValueAsString(productRegister)))).andReturn();
        assertEquals(200,registerService.getResponse().getStatus());
        assertFalse(registerService.getResponse().getContentAsString().isEmpty());
        assertFalse(registerService.getResponse().getContentAsString().isBlank());

        String serviceUUID = registerService.getResponse().getContentAsString();

        MvcResult metricsService = Objects.requireNonNull(mockMvc.perform(get("/metrics/supplies").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(""))).andReturn();
        assertEquals(200,metricsService.getResponse().getStatus());
        assertFalse(metricsService.getResponse().getContentAsString().isBlank());
        assertFalse(metricsService.getResponse().getContentAsString().isEmpty());

        MvcResult metricsVariableSupply = Objects.requireNonNull(mockMvc.perform(get("/metrics/services").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(""))).andReturn();
        assertEquals(200,metricsVariableSupply.getResponse().getStatus());
        assertFalse(metricsVariableSupply.getResponse().getContentAsString().isBlank());
        assertFalse(metricsVariableSupply.getResponse().getContentAsString().isEmpty());

        MvcResult metricsFixedSupply = Objects.requireNonNull(mockMvc.perform(get("/metrics/supplies_fixed").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(""))).andReturn();
        assertEquals(200,metricsFixedSupply.getResponse().getStatus());
        assertFalse(metricsFixedSupply.getResponse().getContentAsString().isBlank());
        assertFalse(metricsFixedSupply.getResponse().getContentAsString().isEmpty());

        MvcResult metricsPDf = Objects.requireNonNull(mockMvc.perform(get("/metrics/download_pdf").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).param("month",String.valueOf(LocalDate.now(ZoneId.of("America/Sao_Paulo")).getMonth().getValue())).param("year",String.valueOf(LocalDate.now(ZoneId.of("America/Sao_Paulo")).getYear())))).andReturn();
        assertEquals(200,metricsPDf.getResponse().getStatus());
        assertEquals("application/pdf",metricsPDf.getResponse().getContentType());
        assertNotNull(metricsPDf.getResponse().getContentAsString());

        // delete
        MvcResult deleteService = Objects.requireNonNull(mockMvc.perform(delete("/product/remove").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(serviceUUID))).andReturn();
        assertEquals(200,deleteService.getResponse().getStatus());

        MvcResult deleteFixedSupply = Objects.requireNonNull(mockMvc.perform(delete("/supplies/remove_fixed").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(uuidFixed))).andReturn();
        assertEquals(200,deleteFixedSupply.getResponse().getStatus());

        MvcResult deleteVariableSupply = Objects.requireNonNull(mockMvc.perform(delete("/supplies/remove").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(uuidVariable))).andReturn();
        assertEquals(200,deleteVariableSupply.getResponse().getStatus());


    }
}