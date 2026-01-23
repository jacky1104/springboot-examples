package com.example.testlayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookService service;

    @Test
    void createDelegatesToRepository() {
        Book input = new Book("Domain-Driven Design", "Eric Evans");
        Book saved = new Book("Domain-Driven Design", "Eric Evans");
        saved.setId(10L);

        when(repository.save(input)).thenReturn(saved);

        Book result = service.create(input);

        assertThat(result.getId()).isEqualTo(10L);
        verify(repository).save(input);
    }
}
