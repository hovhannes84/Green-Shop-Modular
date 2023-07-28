package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import com.example.greenshoprest.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private List<CategoryDto> categories;

    @BeforeEach
    public void setup() {
        CategoryDto category1 = new CategoryDto(1, "category1");
        CategoryDto category2 = new CategoryDto(2, "category2");
        categories = Arrays.asList(category1, category2);
    }

    @Test
    public void testCreateCategory() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setId(3);
        requestDto.setName("new category");
        CategoryDto createdCategory = new CategoryDto(3, "new category");
        when(categoryService.addCategory(any(CreateCategoryRequestDto.class)))
                .thenReturn(ResponseEntity.ok(createdCategory));
        MvcResult mvcResult = mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto resultCategory = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto.class);
        assertThat(resultCategory).isEqualTo(createdCategory);
    }

    @Test
    public void testGetAllCategories() throws Exception {
        when(categoryService.findCategories()).thenReturn(ResponseEntity.ok(categories));
        MvcResult mvcResult = mockMvc.perform(get("/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> resultCategories = Arrays.asList(objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto[].class));

        assertThat(resultCategories).hasSize(categories.size());
        assertThat(resultCategories).containsExactlyInAnyOrderElementsOf(categories);
    }

    @Test
    public void testGetCategoryById() throws Exception {
        int categoryId = 1;
        CategoryDto categoryDto = new CategoryDto(categoryId, "category1");
        when(categoryService.findById(categoryId)).thenReturn(ResponseEntity.ok(categoryDto));
        MvcResult mvcResult = mockMvc.perform(get("/category/{id}", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto resultCategory = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto.class);

        assertThat(resultCategory).isEqualTo(categoryDto);
    }

    @Test
    @WithMockUser("poxos")
    public void testUpdateCategory() throws Exception {
        int categoryId = 1;
        UpdateCategoryRequestDto updateCategoryRequestDto = new UpdateCategoryRequestDto();
        updateCategoryRequestDto.setId(categoryId);
        updateCategoryRequestDto.setName("new name");
        CategoryDto updatedCategoryDto = new CategoryDto(categoryId, "New name");
        when(categoryService.update(eq(categoryId), any(UpdateCategoryRequestDto.class)))
                .thenReturn(ResponseEntity.ok(updatedCategoryDto));

        MvcResult mvcResult = mockMvc.perform(put("/category/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCategoryRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto resultCategory = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto.class);
        assertThat(resultCategory).isEqualTo(updatedCategoryDto);
    }

    @Test
    @WithMockUser("poxos")
    public void testDeleteCategoryById_Success() throws Exception {
        int categoryId = 1;
        given(categoryService.deleteById(categoryId)).willReturn(ResponseEntity.noContent().build());
        mockMvc.perform(delete("/category/{id}", categoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser("poxos")
    public void testDeleteCategoryById_NotFound() throws Exception {
        int categoryId = 1;
        given(categoryService.deleteById(categoryId)).willReturn(ResponseEntity.notFound().build());
        mockMvc.perform(delete("/category/{id}", categoryId))
                .andExpect(status().isNotFound());
    }
}