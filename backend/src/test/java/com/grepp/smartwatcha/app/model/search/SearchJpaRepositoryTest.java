package com.grepp.smartwatcha.app.model.search;

import com.grepp.smartwatcha.app.model.search.repository.SearchJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class SearchJpaRepositoryTest {
    @Autowired
    private SearchJpaRepository searchJpaRepository;

    @Test
    @Transactional(transactionManager = "jpaTransactionManager")
    void findByTitle() {
        System.out.println(searchJpaRepository.findById(510L));
    }
}