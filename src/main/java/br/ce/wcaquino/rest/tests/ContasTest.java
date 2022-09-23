package br.ce.wcaquino.rest.tests;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class ContasTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso() {
        given()
            .body("{\"nome\": \"Conta Inserida\"}")
        .when()
            .post("contas")
        .then()
            .statusCode(201);
    }

    @Test
    public void deveAlterarContaComSucesso() {
       given()
            .body("{\"nome\": \"Conta Alterada\"}")
            .pathParam("id", getIdContaPeloNome("Conta para alterar"))
        .when()
            .put("contas/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("nome", Matchers.is("Conta Alterada"));
    }

    @Test
    public void naoDeveIncluirContaComNomeRepetido(){
        given()
            .body("{\"nome\": \"Conta mesmo nome\"}")
        .when()
            .post("contas")
        .then()
            .log().all()
            .statusCode(400)
            .body("error", Matchers.is("Já existe uma conta com esse nome!"));
    }

    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
    }

}
