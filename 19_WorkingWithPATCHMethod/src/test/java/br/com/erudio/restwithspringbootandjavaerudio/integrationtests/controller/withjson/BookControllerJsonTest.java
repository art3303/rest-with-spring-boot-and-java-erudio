package br.com.erudio.restwithspringbootandjavaerudio.integrationtests.controller.withjson;

import br.com.erudio.restwithspringbootandjavaerudio.configs.TestConfigs;
import br.com.erudio.restwithspringbootandjavaerudio.data.vo.v1.security.TokenVO;
import br.com.erudio.restwithspringbootandjavaerudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.restwithspringbootandjavaerudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.restwithspringbootandjavaerudio.integrationtests.vo.BookVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static BookVO book;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("arthur", "admin123");

        var accessToken =  given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class)
                .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    public void testCreate() throws JsonMappingException, JsonProcessingException {
        mockBook();

        var content =  given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .body(book)
                    .when()
                    .post()
                .then()
                     .statusCode(200)
                        .extract()
                        .body()
                            .asString();

        book = objectMapper.readValue(content, BookVO.class);

        assertNotNull(book);

        assertNotNull(book.getId());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getPrice());
        assertNotNull(book.getTitle());

        assertTrue(book.getId() > 0);


        assertEquals("Hanni", book.getAuthor());
        assertEquals(78.22,book.getPrice());
        assertEquals("Newjeans",book.getTitle());
    }

    @Test
    @Order(2)
    public void testUpdate() throws JsonMappingException, JsonProcessingException {
        book.setTitle("Newjeans with Hanni");

        var content =  given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        book = objectMapper.readValue(content, BookVO.class);

        assertNotNull(book);

        assertNotNull(book.getId());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getPrice());
        assertNotNull(book.getTitle());

        assertTrue(book.getId() > 0);


        assertEquals("Hanni", book.getAuthor());
        assertEquals(78.22,book.getPrice());
        assertEquals("Newjeans with Hanni",book.getTitle());
    }

    @Test
    @Order(3)
    public void testFindBy() throws JsonMappingException, JsonProcessingException {
        mockBook();

        var content =  given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        book = objectMapper.readValue(content, BookVO.class);

        assertNotNull(book);

        assertNotNull(book.getId());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getPrice());
        assertNotNull(book.getTitle());

        assertTrue(book.getId() > 0);


        assertEquals("Hanni", book.getAuthor());
        assertEquals(78.22,book.getPrice());
        assertEquals("Newjeans with Hanni",book.getTitle());
    }

    @Test
    @Order(4)
    public void testDelete() throws JsonMappingException, JsonProcessingException {

        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", book.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    public void testFindAll() throws JsonMappingException, JsonProcessingException {

        var content =  given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .queryParams("page", 0 , "limit", 5, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        List<BookVO> books = objectMapper.readValue(content, new TypeReference<List<BookVO>>(){});

        BookVO foundBookOne = books.get(0);

        assertNotNull(foundBookOne.getId());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getPrice());
        assertTrue(foundBookOne.getId() > 0);
        assertEquals("Working effectively with legacy code", foundBookOne.getTitle());
        assertEquals("Michael C. Feathers", foundBookOne.getAuthor());
        assertEquals(49.00, foundBookOne.getPrice());

        BookVO foundBookFive = books.get(4);

        assertNotNull(foundBookFive.getId());
        assertNotNull(foundBookFive.getTitle());
        assertNotNull(foundBookFive.getAuthor());
        assertNotNull(foundBookFive.getPrice());
        assertTrue(foundBookFive.getId() > 0);
        assertEquals("Code complete", foundBookFive.getTitle());
        assertEquals("Steve McConnell", foundBookFive.getAuthor());
        assertEquals(58.0, foundBookFive.getPrice());
    }

    @Test
    @Order(6)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

            given().spec(specificationWithoutToken)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(403);
    }

    private void mockBook() {
        book.setAuthor("Hanni");
        book.setLaunchDate(new Date());
        book.setPrice(78.22);
        book.setTitle("Newjeans");
    }
}

