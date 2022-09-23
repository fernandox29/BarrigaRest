package br.ce.wcaquino.rest.tests;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.utils.DateUtils;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class MovimentacoesTest extends BaseTest {

    @Test
    public void deveInserirMovimentacaoComSucesso() {
        Movimentacao mov = getMovimentacao();
        given()
            .body(mov)
        .when()
            .post("transacoes")
        .then()
            .statusCode(201);
    }

    @Test
    public void deveValidarCamposObrigaoriosMovimentacao(){
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
    public void naoDeveInserirMovimentacaoComDataFutura(){
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
    public void naoDeveRemoverContaComMovimentacao(){
        given()
            .pathParam("id", getIdContaPeloNome("Conta com movimentacao"))
        .when()
            .delete("contas/{id}")
        .then()
            .statusCode(500)
            .body("constraint", Matchers.is("transacoes_conta_id_foreign"));
    }

    @Test
    public void deveRemoverMovimentacaoConta(){
        given()
            .pathParam("id", getIdMovimentacaoPelaDescricao("Movimentacao para exclusao"))
        .when()
            .delete("transacoes/{id}")
        .then()
            .statusCode(204);
    }

    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
    }

    public Integer getIdMovimentacaoPelaDescricao(String desc) {
        return RestAssured.get("/transacoes?nome=" + desc).then().extract().path("id[0]");
    }
    private Movimentacao getMovimentacao(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
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
