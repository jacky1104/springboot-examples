package com.example.testlayer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Test
    void findByTitleReturnsSavedBook() {
        Book saved = repository.save(new Book("Clean Code", "Robert C. Martin"));

        Optional<Book> result = repository.findByTitle("Clean Code");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
        assertThat(result.get().getAuthor()).isEqualTo("Robert C. Martin");
    }
}
