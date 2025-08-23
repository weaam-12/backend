package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.dtos.ChildDto;
import com.ummbatin.service_management.dtos.KindergartenDto;
import com.ummbatin.service_management.services.KindergartenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KindergartenController.class)
@Import(TestConfig.class)
class KindergartenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KindergartenService kindergartenService;

    @Test
    @WithMockUser // أضف هذا annotation
    void getAllKindergartens_ShouldReturnList() throws Exception {
        // 1. إعداد الأطفال (ChildDto)
        ChildDto child1 = new ChildDto();
        child1.setChildId(1);
        child1.setName("الطفل الأول");

        ChildDto child2 = new ChildDto();
        child2.setChildId(2);
        child2.setName("الطفل الثاني");

        ChildDto child3 = new ChildDto();
        child3.setChildId(3);
        child3.setName("الطفل الثالث");

        // 2. إعداد الروضات (KindergartenDto)
        KindergartenDto kg1 = new KindergartenDto();
        kg1.setKindergartenId(1);
        kg1.setName("روضة الأمل");
        kg1.setChildren(Arrays.asList(child1, child2));

        KindergartenDto kg2 = new KindergartenDto();
        kg2.setKindergartenId(2);
        kg2.setName("روضة المستقبل");
        kg2.setChildren(Arrays.asList(child3));

        List<KindergartenDto> kindergartens = Arrays.asList(kg1, kg2);

        // 3. Mock الخدمة
        when(kindergartenService.getAllKindergartens()).thenReturn(kindergartens);

        // 4. تنفيذ الاختبار
        mockMvc.perform(get("/api/kindergartens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("روضة الأمل"))
                .andExpect(jsonPath("$[0].children.length()").value(2))
                .andExpect(jsonPath("$[0].children[0].name").value("الطفل الأول"))
                .andExpect(jsonPath("$[1].name").value("روضة المستقبل"))
                .andExpect(jsonPath("$[1].children.length()").value(1))
                .andExpect(jsonPath("$[1].children[0].name").value("الطفل الثالث"));
    }
}