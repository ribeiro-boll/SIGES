package com.bolota.historicodevendas.SuppliesTest;

import com.bolota.historicodevendas.Controller.UserController;
import com.bolota.historicodevendas.Entities.PersistentEntities.FixedSuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SuppliesControllerFullTest {
    public static Map<String,Object> registerUser   = new HashMap<>();
    public static Map<String,Object> variableSuppliesEntity = new HashMap<>();
    public static Map<String,Object> fixedSuppliesEntity = new HashMap<>();
    public static Map<String,Object> fixedSuppliesEntityUpdate = new HashMap<>();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    public static void populateHashmap(){
        registerUser.put("login", "tester_123456789");
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
    void shouldInsertAndDeleteVariableSupplies() throws Exception{
        MvcResult register = mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUser))).andReturn();

        assertEquals(200, register.getResponse().getStatus());
        String jwt = register.getResponse().getContentAsString();
        assertFalse(jwt.isEmpty());
        assertFalse(jwt.isBlank());

        MvcResult registerSupply = mockMvc.perform(post("/supplies/register").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(objectMapper.writeValueAsString(variableSuppliesEntity))).andReturn();

        assertEquals(200, registerSupply.getResponse().getStatus());
        String uuid = registerSupply.getResponse().getContentAsString();

        MvcResult deleteSupply= mockMvc.perform(delete("/supplies/remove").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(uuid)).andReturn();

        assertEquals(200,deleteSupply.getResponse().getStatus());
    }

    @Test
    void shouldInsertAndUpdateAndDeleteFixedSupplies() throws Exception{
        MvcResult register         = mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUser))).andReturn();
        assertEquals(409, register.getResponse().getStatus());
        registerUser.put("login","tester_1234569");
        register = mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUser))).andReturn();
        String jwt = register.getResponse().getContentAsString();
        assertFalse(jwt.isEmpty());
        assertFalse(jwt.isBlank());

        MvcResult registerSupply = mockMvc.perform(post("/supplies/register_fixed").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(objectMapper.writeValueAsString(fixedSuppliesEntity))).andReturn();

        assertEquals(200, registerSupply.getResponse().getStatus());
        String uuid = registerSupply.getResponse().getContentAsString();
        FixedSuppliesEntityPersistent fsep = new FixedSuppliesEntityPersistent();
        fsep.setUUID(uuid);
        fsep.setCondUpdatePopup(true);
        fsep.setCounterInUseByServices(6);
        fsep.setFixedSupplyDate(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        fsep.setName("Energia elétrica");
        fsep.setDescription("Conta mensal de energia do salão");
        fsep.setSupplyTotalCost(20.0);
        fsep.generateCostPerMinute();

        MvcResult updateSupply= mockMvc.perform(patch("/supplies/edit_fixedSupply").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(objectMapper.writeValueAsString(fsep))).andReturn();

        assertEquals(200,updateSupply.getResponse().getStatus());

        MvcResult deleteSupply= mockMvc.perform(delete("/supplies/remove_fixed").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(uuid)).andReturn();

        assertEquals(406,deleteSupply.getResponse().getStatus());
        fsep.setCounterInUseByServices(0);

        updateSupply= mockMvc.perform(patch("/supplies/edit_fixedSupply").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(objectMapper.writeValueAsString(fsep))).andReturn();
        assertEquals(200,updateSupply.getResponse().getStatus());

        deleteSupply= mockMvc.perform(delete("/supplies/remove_fixed").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(uuid)).andReturn();
        assertEquals(200,deleteSupply.getResponse().getStatus());

    }
}
