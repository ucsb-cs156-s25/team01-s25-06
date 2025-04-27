package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

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

import java.time.LocalDate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase {

        @MockBean
        RecommendationRequestRepository rrrepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/recommendationrequest/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/recommendationrequest/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/recommendationrequest/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/recommendationrequest?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/recommendationrequest/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/recommendationrequest/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/recommendationrequest/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        //Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("aryan@ucsb.edu")
                                .professorEmail("blah@ucsb.edu")
                                .explanation("INeed a recommendation for a job")
                                .dateRequested(LocalDate.parse("2022-01-03"))
                                .dateNeeded(LocalDate.parse("2022-01-10"))
                                .done(true)
                                .build();

                when(rrrepository.findById(eq(7L))).thenReturn(Optional.of(rr1));

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rrrepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(rr1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(rrrepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(rrrepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_recommendationrequest() throws Exception {

                // arrange
                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("aryan@ucsb.edu")
                                .professorEmail("blah@ucsb.edu")
                                .explanation("INeed a recommendation for a job")
                                .dateRequested(LocalDate.parse("2022-01-03"))
                                .dateNeeded(LocalDate.parse("2022-01-10"))
                                .done(true)
                                .build();


                ArrayList<RecommendationRequest> expectedRecommendationRequest = new ArrayList<>();
                expectedRecommendationRequest.addAll(Arrays.asList(rr1));

                when(rrrepository.findAll()).thenReturn(expectedRecommendationRequest);

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequest/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rrrepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRecommendationRequest);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_recommendationrequest() throws Exception {
                // arrange

                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("aryan@ucsb.edu")
                                .professorEmail("blah@ucsb.edu")
                                .explanation("INeed a recommendation for a job")
                                .dateRequested(LocalDate.parse("2022-01-03"))
                                .dateNeeded(LocalDate.parse("2022-01-10"))
                                .done(true)
                                .build();

                when(rrrepository.save(eq(rr1))).thenReturn(rr1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/recommendationrequest/post?requesterEmail=aryan@ucsb.edu&professorEmail=blah@ucsb.edu&explanation=INeed a recommendation for a job&dateRequested=2022-01-03&dateNeeded=2022-01-10&done=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rrrepository, times(1)).save(rr1);
                String expectedJson = mapper.writeValueAsString(rr1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_recommendationrequest() throws Exception {
                // arrange

                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("aryan@ucsb.edu")
                                .professorEmail("blah@ucsb.edu")
                                .explanation("INeed a recommendation for a job")
                                .dateRequested(LocalDate.parse("2022-01-03"))
                                .dateNeeded(LocalDate.parse("2022-01-10"))
                                .done(true)
                                .build();

                when(rrrepository.findById(eq(15L))).thenReturn(Optional.of(rr1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rrrepository, times(1)).findById(15L);
                verify(rrrepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("Recommendation Request with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_recommendationrequest_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(rrrepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rrrepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_recommendationrequest() throws Exception {
                // arrange


                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("aryan@ucsb.edu")
                                .professorEmail("blah@ucsb.edu")
                                .explanation("INeed a recommendation for a job")
                                .dateRequested(LocalDate.parse("2022-01-03"))
                                .dateNeeded(LocalDate.parse("2022-01-10"))
                                .done(true)
                                .build();

                RecommendationRequest rr2 = RecommendationRequest.builder()
                                .requesterEmail("aryan_edited@ucsb.edu")
                                .professorEmail("blah_edited@ucsb.edu")
                                .explanation("INeed a recommendation for a job_edited")
                                .dateRequested(LocalDate.parse("2025-01-01"))
                                .dateNeeded(LocalDate.parse("2025-01-15"))
                                .done(false)
                                .build();

                String requestBody = mapper.writeValueAsString(rr2);

                when(rrrepository.findById(eq(67L))).thenReturn(Optional.of(rr1));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/recommendationrequest?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rrrepository, times(1)).findById(67L);
                verify(rrrepository, times(1)).save(rr2); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_ucsbdate_that_does_not_exist() throws Exception {
                // arrange


                 RecommendationRequest rr2 = RecommendationRequest.builder()
                                .requesterEmail("aryan_edited@ucsb.edu")
                                .professorEmail("blah_edited@ucsb.edu")
                                .explanation("INeed a recommendation for a job_edited")
                                .dateRequested(LocalDate.parse("2025-01-01"))
                                .dateNeeded(LocalDate.parse("2025-01-15"))
                                .done(true)
                                .build();
                String requestBody = mapper.writeValueAsString(rr2);

                when(rrrepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/recommendationrequest?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rrrepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 67 not found", json.get("message"));

        }
}
