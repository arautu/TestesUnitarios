package br.ce.wcaquino.services;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entities.Filme;
import br.ce.wcaquino.entities.Locacao;
import br.ce.wcaquino.entities.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.mockito.Mockito;

import java.util.*;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class LocacaoServiceTest {

    @Rule
    public final ErrorCollector error = new ErrorCollector();

    private LocacaoService service;
    private LocacaoDAO dao;
    private SPCService spc;

    @Before
    public void setup() {
        service = new LocacaoService();
        dao = mock(LocacaoDAO.class);
        service.setLocacaoDAO(dao);
        spc = mock(SPCService.class);
        service.setSpcService(spc);
    }

    @Test
    public void deveAlugarFilme() throws Exception {

        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        // cenario
        Usuario usuario = umUsuario().agora();

        List<Filme> filmes = new ArrayList<>();

        filmes.add(umFilme().comValor(5.0).agora());

        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws Exception {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = new ArrayList<>();

        filmes.add(umFilme().semEstoque().agora());

        // ação
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        // cenario
        List<Filme> filmes = new ArrayList<>();

        filmes.add(umFilme().agora());
        filmes.add(umFilme().agora());
        filmes.add(umFilme().agora());

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
    public void naoDeveAlugarFilmeSemFilme() {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = new ArrayList<>();

        // ação e Verificação
        assertThrows("Não deveria haver filme ou outra exceção ocorreu",
                LocadoraException.class, () -> service.alugarFilme(usuario, filmes)
        );
        System.out.println("Forma nova");
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException,
            LocadoraException {

        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        // Ação
        Locacao retorno = service.alugarFilme(usuario, filmes);

        // Verificação
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() {

        // Cenário
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario 2").agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenReturn(true);

        // Ação e verificação
        assertThrows("Usuário negativado", LocadoraException.class,
                () -> service.alugarFilme(usuario, filmes));
    }
}