package com.example.testlayer;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService service;

    @Test
    void listReturnsBooks() throws Exception {
        Book first = new Book("Refactoring", "Martin Fowler");
        first.setId(1L);
        Book second = new Book("The Pragmatic Programmer", "Andy Hunt");
        second.setId(2L);

        when(service.findAll()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Refactoring"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].author").value("Andy Hunt"));
    }

    @Test
    void createReturnsCreatedBook() throws Exception {
        Book created = new Book("Working Effectively with Legacy Code", "Michael Feathers");
        created.setId(5L);

        when(service.create(org.mockito.ArgumentMatchers.any(Book.class))).thenReturn(created);

        String payload = "{\"title\":\"Working Effectively with Legacy Code\",\"author\":\"Michael Feathers\"}";

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.title").value("Working Effectively with Legacy Code"));
    }
}
