package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.config.JwtAuthenticationFilter;
import com.ummbatin.service_management.dtos.ChildDto;
import com.ummbatin.service_management.dtos.KindergartenDto;
import com.ummbatin.service_management.repositories.KindergartenRepository;
import com.ummbatin.service_management.services.KindergartenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = KindergartenController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                SecurityAutoConfiguration.class
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class)
)
@Import(TestConfig.class)
class KindergartenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KindergartenService kindergartenService;

    @MockBean   // ←  NEW ‑ provides the missing bean
    private KindergartenRepository kindergartenRepository;

    @Test
    @WithMockUser
    void getAllKindergartens_ShouldReturnList() throws Exception {
        ChildDto child1 = new ChildDto();
        child1.setChildId(1);
        child1.setName("ילד1");

        ChildDto child2 = new ChildDto();
        child2.setChildId(2);
        child2.setName("ילד2");

        ChildDto child3 = new ChildDto();
        child3.setChildId(3);
        child3.setName("ילד3");

        KindergartenDto kg1 = new KindergartenDto();
        kg1.setKindergartenId(1);
        kg1.setName("אלאפ");
        kg1.setChildren(Arrays.asList(child1, child2));

        KindergartenDto kg2 = new KindergartenDto();
        kg2.setKindergartenId(2);
        kg2.setName("בית");
        kg2.setChildren(Arrays.asList(child3));

        List<KindergartenDto> kindergartens = Arrays.asList(kg1, kg2);

        Mockito.when(kindergartenService.getAllKindergartens()).thenReturn(kindergartens);

        mockMvc.perform(get("/api/kindergartens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("אלאפ"))
                .andExpect(jsonPath("$[0].children.length()").value(2))
                .andExpect(jsonPath("$[0].children[0].name").value("ילד1"))
                .andExpect(jsonPath("$[1].name").value("בית"))
                .andExpect(jsonPath("$[1].children.length()").value(1))
                .andExpect(jsonPath("$[1].children[0].name").value("ילד3"));
    }
}