package br.ce.wcaquino.rest.tests;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.utils.DateUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {
    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

    @BeforeClass
    public static void loginToken(){
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
    }

    @Test
    public void t01_deveIncluirContaComSucesso(){
        CONTA_ID = given()
                .body("{\"nome\": \""+CONTA_NAME+"\"}")
        .when()
                .post("contas")
        .then()
                .statusCode(201)
                .extract().path("id");
    }
    @Test
    public void t02_deveAlterarContaComSucesso() {
        given()
            .body("{\"nome\": \""+CONTA_NAME+" Alterada\"}")
            .pathParam("id", CONTA_ID)
        .when()
            .put("contas/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("nome", Matchers.is(CONTA_NAME+" Alterada"));
    }

    @Test
    public void t03_naoDeveIncluirContaComNomeRepetido(){
        given()
            .body("{\"nome\": \""+CONTA_NAME+" Alterada\"}")
        .when()
            .post("contas")
        .then()
            .log().all()
            .statusCode(400)
            .body("error", Matchers.is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void t04_deveInserirMovimentacaoComSucesso(){
        Movimentacao mov = getMovimentacao();
        MOV_ID = given()
            .body(mov)
        .when()
            .post("transacoes")
        .then()
            .log().all()
            .statusCode(201)
            .extract().path("id");
    }

    @Test
    public void t05_deveValidarCamposObrigaoriosMovimentacao(){
        given()
            .body("{}")
        .when()
            .post("transacoes")
        .then()
//            .log().all()
            .statusCode(400)
            .body("$", Matchers.hasSize(8))
            .body("msg[0]", Matchers.is("Data da Movimentação é obrigatório")) //Opção para checar mensagem por mensagem.
            .body("msg[1]", Matchers.is("Data do pagamento é obrigatório"))
            //Opção para verificar todas:
            .body("msg", Matchers.hasItems(
                "Data da Movimentação é obrigatório",
                "Data do pagamento é obrigatório",
                "Descrição é obrigatório",
                "Interessado é obrigatório",
                "Valor é obrigatório",
                "Valor deve ser um número",
                "Conta é obrigatório",
                "Situação é obrigatório"
                ));
    }

    @Test
    public void t06_naoDeveInserirMovimentacaoComDataFutura(){
        Movimentacao mov = getMovimentacao();
        mov.setData_transacao(DateUtils.getDataDiferencaDias(2));

        given()
            .body(mov)
        .when()
            .post("transacoes")
        .then()
            .statusCode(400)
            .body("msg[0]", Matchers.is("Data da Movimentação deve ser menor ou igual à data atual"));
    }

    @Test
    public void t07_naoDeveRemoverContaComMovimentacao(){
        given()
            .pathParam("id", CONTA_ID)
        .when()
            .delete("contas/{id}")
        .then()
            .statusCode(500)
            .body("constraint", Matchers.is("transacoes_conta_id_foreign"));
    }

    @Test
    public void t08_deveCalcularSaldoContas(){
        given()
        .when()
            .get("saldo")
        .then()
            .statusCode(200)
            .body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("400.00"));
    }

    @Test
    public void t09_deveRemoverMovimentacaoConta(){
        given()
            .pathParam("id", MOV_ID)
        .when()
            .delete("transacoes/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    public void t10_naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
            .get("contas")
        .then()
            .log().all()
            .statusCode(401);
    }

    private Movimentacao getMovimentacao(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(CONTA_ID);
        mov.setDescricao("Descrição da Movimentação - Fernando Xavier");
        mov.setEnvolvido("Teste Adicionando Movimento");
        mov.setTipo("REC");
        mov.setData_transacao(DateUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DateUtils.getDataDiferencaDias(5));
        mov.setValor(400f);
        mov.setStatus(true);
        return mov;
    }


    }


