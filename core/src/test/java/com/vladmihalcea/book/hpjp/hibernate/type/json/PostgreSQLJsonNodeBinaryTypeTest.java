package com.vladmihalcea.book.hpjp.hibernate.type.json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.book.hpjp.util.AbstractPostgreSQLIntegrationTest;

/**
 * @author Vlad Mihalcea
 */
public class PostgreSQLJsonNodeBinaryTypeTest extends AbstractPostgreSQLIntegrationTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[] {
            Book.class
        };
    }

    @Test
    public void test() {

        doInJPA(entityManager -> {

            Book configuration = new Book();
            configuration.setIsbn( "978-9730228236" );
            configuration.setProperties(
                JacksonUtil.toJsonNode(
                    "{" +
                    "   \"title\": \"High-Performance Java Persistence\"," +
                    "   \"author\": \"Vlad Mihalcea\"," +
                    "   \"publisher\": \"Amazon\", " +
                    "   \"price\": 44.99" +
                    "}"
                )
            );

            entityManager.persist( configuration );
        });
        doInJPA(entityManager -> {
            Session session = entityManager.unwrap( Session.class );
            Book configuration = session
                .bySimpleNaturalId( Book.class )
                .load( "978-9730228236" );

            LOGGER.info( "Book details; {}", configuration.getProperties() );
        });
    }

    @Entity(name = "Book")
    @Table(name = "book")
    @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class)
    public static class Book {

        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        private String isbn;

        @Type( type = "jsonb-node" )
        @Column(columnDefinition = "jsonb")
        private JsonNode properties;

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public JsonNode getProperties() {
            return properties;
        }

        public void setProperties(JsonNode properties) {
            this.properties = properties;
        }
    }
}