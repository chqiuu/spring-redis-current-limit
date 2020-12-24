package com.github.chqiuu.redis.limit.demo.controller;

import com.github.chqiuu.redis.limit.demo.SpringRedisCurrentLimitDemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringRedisCurrentLimitDemoApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class MethodCurrentLimitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void localTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            String url = "/method/local";
            MvcResult mvcResult = mockMvc
                    .perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            System.out.printf("%s %s %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), url, response.getContentAsString());
            Thread.sleep(500);
        }
    }

    @Test
    public void ipTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            String url = "/method/ip";
            MvcResult mvcResult = mockMvc
                    .perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            System.out.printf("%s %s %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), url, response.getContentAsString());
            Thread.sleep(800);
        }
    }

    @Test
    public void userTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            String url = "/method/user";
            MvcResult mvcResult = mockMvc
                    .perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            System.out.printf("%s %s %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), url, response.getContentAsString());
            Thread.sleep(500);
        }
    }

    @Test
    public void sessionTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            String url = "/method/session";
            MvcResult mvcResult = mockMvc
                    .perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            System.out.printf("%s %s %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), url, response.getContentAsString());
            Thread.sleep(500);
        }
    }

    @Test
    public void customTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            String url = "/method/custom";
            MvcResult mvcResult = mockMvc
                    .perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            System.out.printf("%s %s %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), url, response.getContentAsString());
            Thread.sleep(500);
        }
    }

    @Test
    public void typeTotalTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            String url = "/type/total/haveParam";
            MvcResult mvcResult = mockMvc
                    .perform(MockMvcRequestBuilders.get(url).param("param", "test param"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            System.out.printf("%s %s %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), url, response.getContentAsString());

            url = "/type/total/noParam";
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            response = mvcResult.getResponse();
            System.out.printf("%s %s %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), url, response.getContentAsString());
            Thread.sleep(500);
        }
    }
}
