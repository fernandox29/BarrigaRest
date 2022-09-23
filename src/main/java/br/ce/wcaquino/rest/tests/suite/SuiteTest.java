package br.ce.wcaquino.rest.tests.suite;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.ContasTest;
import br.ce.wcaquino.rest.tests.MovimentacoesTest;
import br.ce.wcaquino.rest.tests.SaldoTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ContasTest.class,
        MovimentacoesTest.class,
        SaldoTest.class
})
public class SuiteTest extends BaseTest {

    @BeforeClass
    public static void loginToken() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "fernandox29@gmail.com");
        login.put("senha", "123456");

        String TOKEN = given()
            .body(login)
        .when()
            .post("signin")
        .then()
            .statusCode(200)
            .extract().path("token");
        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
        RestAssured.get("/reset").then().statusCode(200);
    }
}
