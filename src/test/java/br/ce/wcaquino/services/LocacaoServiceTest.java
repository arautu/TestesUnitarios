package br.ce.wcaquino.services;

import br.ce.wcaquino.entities.Filme;
import br.ce.wcaquino.entities.Locacao;
import br.ce.wcaquino.entities.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

@SuppressWarnings("unused")
public class LocacaoServiceTest {

    private LocacaoService service;

    @Rule
    public final ErrorCollector error = new ErrorCollector();

    @Before
    public void setup() {
        service = new LocacaoService();
    }

    @SuppressWarnings("unused")
    @Test
    public void testeLocacao() throws Exception {
        // cenario
        Usuario usuario = new Usuario("Usuario 1");

        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));

        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(10.5)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(),
                obterDataComDiferencaDias(1)), is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void testLocacao_filmeSemEstoque() throws Exception {
        // cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));
        filmes.add(new Filme("Filme 3", 0, 1.25));

        // ação
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void testLocacao_usuarioVazio() throws FilmeSemEstoqueException {
        // cenario
        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));
        filmes.add(new Filme("Filme 3", 1, 1.25));

        // ação
        try {
            service.alugarFilme(null, filmes);
            fail("Esperado que não houvesse usuário");
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário vazio"));
        }
        System.out.println("Forma robusta");
    }

    @Test
    public void testLocacao_FilmeVazio() {
        // cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = new ArrayList<>();

        // ação e Verificação
        assertThrows("Não deveria haver filme ou outra exceção ocorreu",
                LocadoraException.class, () -> service.alugarFilme(usuario, filmes)
        );
        System.out.println("Forma nova");
    }

    @Test
    public void testLocacao_2filmes() throws FilmeSemEstoqueException, LocadoraException {

        // Cenário
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));

        // Açao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // Verificação
        Assert.assertEquals(5.0 + 5.5, locacao.getValor(), 0.01);
    }

    @Test
    public void testLocacao_3filmes() throws FilmeSemEstoqueException, LocadoraException {

        // Cenário
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));
        filmes.add(new Filme("Filme 3", 1, 8.5));

        // Ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // Verificaçao
        Assert.assertEquals(5.0 + 5.5 + 8.5 * (1 - 0.25), locacao.getValor(), 0.01);
    }

    @Test
    public void testLocacao_4filmes() throws FilmeSemEstoqueException, LocadoraException {

        // Cenário
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));
        filmes.add(new Filme("Filme 3", 1, 8.5));
        filmes.add(new Filme("Filme 4", 5, 12.0));

        // Ação
        Locacao locacacao = service.alugarFilme(usuario, filmes);

        // Verificação
        Assert.assertEquals("Esperado descontos de 50% no 4° e 25% no 3° livro",
                22.875, locacacao.getValor(), 0.01);
    }

    @Test
    public void testLocacao_5filmes() throws FilmeSemEstoqueException, LocadoraException {

        // Cenário
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));
        filmes.add(new Filme("Filme 3", 1, 8.5));
        filmes.add(new Filme("Filme 4", 5, 12.0));
        filmes.add(new Filme("Filme 5", 5, 1.5));

        // Ação
        Locacao locacacao = service.alugarFilme(usuario, filmes);

        // Verificação
        Assert.assertEquals("Esperado descontos de 75% no 5°, 50% no 4° e 25% no " +
                        "3° livro",23.25, locacacao.getValor(), 0.01);
    }

    @Test
    public void testLocacao_6filmes() throws FilmeSemEstoqueException, LocadoraException {

        // Cenário
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));
        filmes.add(new Filme("Filme 3", 1, 8.5));
        filmes.add(new Filme("Filme 4", 5, 12.0));
        filmes.add(new Filme("Filme 5", 8, 1.5));
        filmes.add(new Filme("Filme 6", 6, 4.5));

        // Ação
        Locacao locacacao = service.alugarFilme(usuario, filmes);

        // Verificação
        Assert.assertEquals("Esperado descontos de 75% no 5°, 50% no 4° e 25% no " +
                "3° livro",23.25, locacacao.getValor(), 0.01);
    }
    @Test
    public void testLocacao_7filmes() throws FilmeSemEstoqueException, LocadoraException {

        // Cenário
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));
        filmes.add(new Filme("Filme 3", 1, 8.5));
        filmes.add(new Filme("Filme 4", 5, 12.0));
        filmes.add(new Filme("Filme 5", 8, 1.5));
        filmes.add(new Filme("Filme 6", 6, 4.5));
        filmes.add(new Filme("Filme 7", 5, 9.0));

        // Ação
        Locacao locacacao = service.alugarFilme(usuario, filmes);

        // Verificação
        Assert.assertEquals("Esperado descontos de 75% no 5°, 50% no 4° e 25% no " +
                "3° livro",32.25, locacacao.getValor(), 0.01);
    }
}