package com.grepp.smartwatcha.infra.config

import jakarta.persistence.EntityManagerFactory
import org.neo4j.driver.Driver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionTemplate

// 트랜잭션 매니저 설정 클래스
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    transactionManagerRef = "jpaTransactionManager",         // JPA 트랜잭션 매니저 지정
    basePackages = ["com.grepp.smartwatcha"]
)
@EnableNeo4jRepositories(
    transactionManagerRef = "neo4jTransactionManager",       // Neo4j 트랜잭션 매니저 지정
    basePackages = ["com.grepp.smartwatcha"]
)
class TransactionManagerConfig {

    // JPA 트랜잭션 매니저
    @Bean(name = ["jpaTransactionManager"])
    @Primary
    fun jpaTransactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(emf)
    }

    // Neo4j 트랜잭션 매니저
    @Bean(name = ["neo4jTransactionManager"])
    fun neo4jTransactionManager(driver: Driver): PlatformTransactionManager {
        return Neo4jTransactionManager(driver)
    }

    // JPA 전용 트랜잭션 템플릿
    @Bean(name = ["jpaTransactionTemplate"])
    fun transactionTemplate(
        @Qualifier("jpaTransactionManager") transactionManager: PlatformTransactionManager
    ): TransactionTemplate {
        return TransactionTemplate(transactionManager)
    }

    // Neo4j 전용 트랜잭션 템플릿
    @Bean(name = ["neo4jTransactionTemplate"])
    fun neo4jTransactionTemplate(
        @Qualifier("neo4jTransactionManager") transactionManager: PlatformTransactionManager
    ): TransactionTemplate {
        return TransactionTemplate(transactionManager)
    }
}
