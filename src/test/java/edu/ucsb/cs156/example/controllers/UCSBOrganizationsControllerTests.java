package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationsController.class)
@Import(TestConfig.class)
public class UCSBOrganizationsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBOrganizationsRepository ucsbOrganizationsRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/ucsborganizations/all
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().is(200)); // logged
        }

        // Authorization tests for /api/ucsborganizations/post
        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // Tests with mocks for database actions
        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsborganizations() throws Exception {

                // arrange
                UCSBOrganizations ucsbOrganizations1 = UCSBOrganizations.builder()
                                .orgCode("GR")
                                .orgTranslationShort("Gaucho Race")
                                .orgTranslation("Gaucho Racing")
                                .inactive(true)
                                .build();
                UCSBOrganizations ucsbOrganizations2 = UCSBOrganizations.builder()
                                .orgCode("ACM")
                                .orgTranslationShort("Association of CM")
                                .orgTranslation("Association of Computing Machinery")
                                .inactive(true)
                                .build();
                UCSBOrganizations ucsbOrganizations3 = UCSBOrganizations.builder()
                                .orgCode("C1")
                                .orgTranslationShort("Club")
                                .orgTranslation("Club One")
                                .inactive(true)
                                .build();

                ArrayList<UCSBOrganizations> expectedUCSBOrganizations = new ArrayList<>();
                expectedUCSBOrganizations
                                .addAll(Arrays.asList(ucsbOrganizations1, ucsbOrganizations2, ucsbOrganizations3));

                when(ucsbOrganizationsRepository.findAll()).thenReturn(expectedUCSBOrganizations);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedUCSBOrganizations);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ucsborganization() throws Exception {
                // arrange
                UCSBOrganizations boo = UCSBOrganizations.builder()
                                .orgCode("boo")
                                .orgTranslationShort("booo")
                                .orgTranslation("boooo")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationsRepository.save(eq(boo))).thenReturn(boo);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsborganizations/post?orgCode=boo&orgTranslationShort=booo&orgTranslation=boooo&inactive=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).save(boo);
                String expectedJson = mapper.writeValueAsString(boo);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbOrganizationsRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbOrganizationsRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganizations with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_exist() throws Exception {

                // arrange
                UCSBOrganizations boo = UCSBOrganizations.builder()
                .orgCode("boo")
                .orgTranslationShort("booo")
                .orgTranslation("boooo")
                .inactive(true)
                .build();

                when(ucsbOrganizationsRepository.findById(eq(7L))).thenReturn(Optional.of(boo));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationsRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(boo);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_ucsborganization() throws Exception {
                // arrange

                UCSBOrganizations UCSBOrganizationsOrig = UCSBOrganizations.builder()
                                .orgCode("OG")
                                .orgTranslationShort("Orig")
                                .orgTranslation("Original")
                                .inactive(true)
                                .build();

                UCSBOrganizations UCSBOrganizationsEdited = UCSBOrganizations.builder()
                                .orgCode("ED")
                                .orgTranslationShort("Edit")
                                .orgTranslation("Edited")
                                .inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(UCSBOrganizationsEdited);

                when(ucsbOrganizationsRepository.findById(eq(67L))).thenReturn(Optional.of(UCSBOrganizationsOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganizations?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById(67L);
                verify(ucsbOrganizationsRepository, times(1)).save(UCSBOrganizationsEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_UCSBOrganizations_that_does_not_exist() throws Exception {
                // arrange

                UCSBOrganizations ucsbEditedOrganizations = UCSBOrganizations.builder()
                                .orgCode("Doesn't matter")
                                .orgTranslationShort("Doesn't matter")
                                .orgTranslation("Doesn't matter")
                                .inactive(true)
                                .build();

                String requestBody = mapper.writeValueAsString(ucsbEditedOrganizations);

                when(ucsbOrganizationsRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganizations?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id 67 not found", json.get("message"));

        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_an_organization() throws Exception {
                // arrange

                UCSBOrganizations ucsbOrganization1 = UCSBOrganizations.builder()
                                .orgCode("Org1")
                                .orgTranslationShort("Organize1")
                                .orgTranslation("Organization1")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationsRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbOrganization1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganizations?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById(15L);
                verify(ucsbOrganizationsRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_ucsborganization_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbOrganizationsRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganizations?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id 15 not found", json.get("message"));
        }
}