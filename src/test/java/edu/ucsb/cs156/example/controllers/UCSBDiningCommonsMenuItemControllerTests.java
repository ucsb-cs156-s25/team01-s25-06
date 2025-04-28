package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemControllerTests extends ControllerTestCase {

    @MockBean
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization Tests

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                .andExpect(status().is(403));
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=1"))
                .andExpect(status().is(403));
    }

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                .andExpect(status().is(403));
    }

    // Behavior Tests

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all() throws Exception {
        UCSBDiningCommonsMenuItem item1 = UCSBDiningCommonsMenuItem.builder()
                .diningCommonsCode("ortega")
                .name("Pizza")
                .station("Pizza Station")
                .build();

        UCSBDiningCommonsMenuItem item2 = UCSBDiningCommonsMenuItem.builder()
                .diningCommonsCode("dlg")
                .name("Tacos")
                .station("Taco Station")
                .build();

        ArrayList<UCSBDiningCommonsMenuItem> expectedItems = new ArrayList<>(Arrays.asList(item1, item2));

        when(ucsbDiningCommonsMenuItemRepository.findAll()).thenReturn(expectedItems);

        MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                .andExpect(status().isOk()).andReturn();

        verify(ucsbDiningCommonsMenuItemRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedItems);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_by_id_when_it_exists() throws Exception {
        UCSBDiningCommonsMenuItem item = UCSBDiningCommonsMenuItem.builder()
                .diningCommonsCode("dlg")
                .name("Pancakes")
                .station("Breakfast")
                .build();

        when(ucsbDiningCommonsMenuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=1"))
                .andExpect(status().isOk()).andReturn();

        verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(1L);
        String expectedJson = mapper.writeValueAsString(item);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_get_by_id_returns_404_when_not_found() throws Exception {
        when(ucsbDiningCommonsMenuItemRepository.findById(999L)).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=999"))
                .andExpect(status().isNotFound()).andReturn();

        verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(999L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("UCSBDiningCommonsMenuItem with id 999 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_post_a_new_menu_item() throws Exception {
        UCSBDiningCommonsMenuItem newItem = UCSBDiningCommonsMenuItem.builder()
                .diningCommonsCode("portola")
                .name("Pasta")
                .station("Pasta Station")
                .build();

        when(ucsbDiningCommonsMenuItemRepository.save(any())).thenReturn(newItem);

        MvcResult response = mockMvc.perform(
                post("/api/ucsbdiningcommonsmenuitem/post")
                        .param("diningcommonscode", "portola")
                        .param("name", "Pasta")
                        .param("station", "Pasta Station")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(any());
        String expectedJson = mapper.writeValueAsString(newItem);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}