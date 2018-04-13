package com.quote;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class NdQuoteApplication {


	@Autowired
	private EntityManagerFactory entityManagerFactory;

	
	public static void main(String[] args) {
		SpringApplication.run(NdQuoteApplication.class, args);
	}
	

	@Bean(name="sessionFactory")
    public SessionFactory sessionFactory() {
        return ((HibernateEntityManagerFactory) this.entityManagerFactory).getSessionFactory();
    }
}
