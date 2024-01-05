package com.gonanoio.redis.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonanoio.redis.BaseIntegrationIT;
import com.gonanoio.redis.model.HashEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class HashControllerIT extends BaseIntegrationIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldSaveQuote() throws Exception {
        final HashEntry quote = new HashEntry();
        quote.setName("Samwise Gamgee");
        quote.setValue("There's Some Good In The World, Mr. Frodo.");
        MvcResult mvcResult = mockMvc.perform(post("/maps/quotes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(quote)))
                .andExpect(status().isOk())
                .andReturn();
        final String actualResponseBody = mvcResult.getResponse().getContentAsString();
        final String expectedResponseBody = objectMapper.writeValueAsString(quote);
        Assertions.assertEquals(expectedResponseBody, actualResponseBody);
    }

    @Test
    void shouldGetQuote() throws Exception {
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/maps/quotes/{name}", "Bilbo Baggins")
                                .contentType("application/json"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void shouldGetAllQuotes() throws Exception {
        final MvcResult mvcResult =
                this.mockMvc.perform(
                                MockMvcRequestBuilders.get("/maps/quotes")
                                        .contentType("application/json"))
                        .andDo(print()).andExpect(status().isOk())
                        .andExpect(status().isOk())
                        .andReturn();

        final List<HashEntry> entries = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(entries, hasItem(hasProperty("name", is("Bilbo Baggins"))));
        assertThat(entries, hasItem(hasProperty("name", is("Frodo Baggins"))));
        assertThat(entries, hasItem(hasProperty("value", is("I don't know half of you half as well as I should like; and I like less than half of you half as well as you deserve"))));
        assertThat(entries, hasItem(hasProperty("value", is("It's Useless To Meet Revenge With Revenge: It Will Heal Nothing"))));
    }
}
