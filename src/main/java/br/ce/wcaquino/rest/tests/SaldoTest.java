package br.ce.wcaquino.rest.tests;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class SaldoTest extends BaseTest {

    @Test
    public void deveCalcularSaldoContas(){
        Integer CONTA_ID = getIdContaPeloNome("Conta para saldo");
        given()
        .when()
            .get("saldo")
        .then()
            .statusCode(200)
            .body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("534.00"));
    }

    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
    }

}
