package br.ce.wcaquino.services;

import br.ce.wcaquino.entities.Filme;
import br.ce.wcaquino.entities.Locacao;
import br.ce.wcaquino.entities.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
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

public class LocacaoServiceTest {

    private LocacaoService service;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Before
    public void setup() {
        service = new LocacaoService();
    }

    @Test
    public void testeLocacao() throws Exception {
        // cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 2, 5.0);

        List<Filme> filmes = new ArrayList<>();

        filmes.add(new Filme("Filme 1", 2, 5.0));
        filmes.add(new Filme("Filme 2", 3, 5.5));
        filmes.add(new Filme("Filme 3", 1, 1.25));

        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(11.75)));
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

        // ação
        assertThrows("Não deveria haver filme ou outra exceção ocorreu",
                LocadoraException.class, () -> {
                    service.alugarFilme(usuario, filmes);
                }
        );
        System.out.println("Forma nova");
    }
}