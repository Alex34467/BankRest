package com.example.bankcards.controller;

import com.example.bankcards.config.WebTestConfig;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.TestUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@Import(WebTestConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getAllCardsTest_success() throws Exception {
        Mockito.when(cardService.findAllCards())
                .thenReturn(TestUtil.getCardsDtos());

        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").exists())
                .andExpect(jsonPath("$.[0].id").exists());
        Mockito.verify(cardService, Mockito.times(1)).findAllCards();
    }

    @Test
    void findCardByIdTest_success() throws Exception {
        Mockito.when(cardService.findById(anyLong()))
                .thenReturn(TestUtil.getCardDto(1L));

        mockMvc.perform(get("/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        Mockito.verify(cardService, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void findCardsByOwnerTest_success() throws Exception {
        Mockito.when(cardService.findCardsByOwner(anyLong()))
                .thenReturn(TestUtil.getCardsDtos());

        mockMvc.perform(get("/cards/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").exists())
                .andExpect(jsonPath("$.[0].id").exists());
        Mockito.verify(cardService, Mockito.times(1)).findCardsByOwner(anyLong());
    }

    @Test
    void findCurrentUserCardsTest_success() throws Exception {
        Mockito.when(jwtService.extractUsername(anyString()))
                        .thenReturn("User");
        Mockito.when(jwtService.isTokenValid(anyString(), any()))
                        .thenReturn(true);
        Mockito.when(userService.getUserDetailsService())
                        .thenReturn(username -> TestUtil.getUserDetails());
        Mockito.when(cardService.searchCardsByOwnerAndNumber(anyLong(), anyString(), any()))
                .thenReturn(TestUtil.getCardsDtos());

        mockMvc.perform(
                get("/cards/owner/search?searchNumber=1234&page=0&size=1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").exists())
                .andExpect(jsonPath("$.[0].id").exists());
        Mockito.verify(cardService, Mockito.times(1)).searchCardsByOwnerAndNumber(anyLong(), anyString(), any());
    }

    @Test
    void saveCardTest_success() throws Exception {
        Mockito.when(cardService.saveCard(any()))
                .thenReturn(TestUtil.getCardDto(1L));
        String json = new ObjectMapper().writeValueAsString(TestUtil.getCardDto(1L));

        mockMvc.perform(post("/cards").contentType("application/json").content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        Mockito.verify(cardService, Mockito.times(1)).saveCard(any());
    }

    @Test
    void deleteCardTest_success() throws Exception {
        mockMvc.perform(delete("/cards/1"))
                .andExpect(status().isOk());
        Mockito.verify(cardService, Mockito.times(1)).deleteCard(anyLong());
    }

    @Test
    void makeTransferBetweenCardsTest_success() throws Exception {
        Mockito.when(jwtService.extractUsername(anyString()))
                .thenReturn("User");
        Mockito.when(jwtService.isTokenValid(anyString(), any()))
                .thenReturn(true);
        Mockito.when(userService.getUserDetailsService())
                .thenReturn(username -> TestUtil.getUserDetails());
        Mockito.when(cardService.transferMoney(anyLong(), anyString(), anyString(), any()))
                .thenReturn(Pair.of(TestUtil.getCardDto(1L), TestUtil.getCardDto(2L)));
        String json = new ObjectMapper().writeValueAsString(TestUtil.getMoneyTransferRequest("11", "22"));

        mockMvc.perform(post("/cards/transfer")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceCardNumber").isString())
                .andExpect(jsonPath("$.sourceCardBalance").isNumber())
                .andExpect(jsonPath("$.targetCardNumber").isString())
                .andExpect(jsonPath("$.targetCardBalance").isNumber());
        Mockito.verify(cardService, Mockito.times(1)).transferMoney(anyLong(), anyString(), anyString(), any());
    }
}
