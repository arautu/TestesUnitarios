package br.ce.wcaquino.services;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entities.Filme;
import br.ce.wcaquino.entities.Locacao;
import br.ce.wcaquino.entities.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.xml.crypto.Data;
import java.util.*;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.verificarDiaSemana;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocacaoService.class })
public class LocacaoServiceTest {

    @Rule
    public final ErrorCollector error = new ErrorCollector();
    @InjectMocks
    private LocacaoService service;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private SPCService spc;
    @Mock
    private EmailService email;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        service = PowerMockito.spy(service);
    }

    @Test
    public void deveAlugarFilme() throws Exception {

        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = new ArrayList<>();
        filmes.add(umFilme().comValor(5.0).agora());
//        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.APRIL, 28);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//        error.checkThat(locacao.getDataLocacao(), ehHoje());
//        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
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
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());
//        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.APRIL, 29);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        // Ação
        Locacao retorno = service.alugarFilme(usuario, filmes);

        // Verificação
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
//        PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
        PowerMockito.verifyStatic(Calendar.class, Mockito.times(2));
        Calendar.getInstance();
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

        // Ação e verificação
        Exception exception = assertThrows(LocadoraException.class,
                () -> service.alugarFilme(usuario, filmes));

        assertThat(exception.getMessage(), is("Usuário negativado"));

        verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {

        // Cenário
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
        List<Locacao> locacoes = Arrays.asList(
                umLocacao().comUsuario(usuario).atrasada().agora(),
                umLocacao().comUsuario(usuario2).agora(),
                umLocacao().comUsuario(usuario3).atrasada().agora(),
                umLocacao().comUsuario(usuario3).atrasada().agora());

        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        // Ação
        service.notificarAtrasos();

        // Verificação
        verify(email, times(3)).notificarAtraso(any(Usuario.class));
        verify(email).notificarAtraso(usuario);
        verify(email, atLeastOnce()).notificarAtraso(usuario3);
        verify(email, never()).notificarAtraso(usuario2);
        verifyNoMoreInteractions(email);
    }

    @Test
    public void deveTratarErronoSPC() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));

        // Ação e verificação
        Exception exception = assertThrows(LocadoraException.class,
                () -> service.alugarFilme(usuario, filmes));

        assertThat(exception.getMessage(), is("Problemas com SPC, tente novamente"));
    }

    @Test
    public void deveProrrogarUmaLocacao() {

        // Cenário
        Locacao locacao = umLocacao().agora();

        // Ação
        service.prorrogarLocacao(locacao, 3);

        // Verificação
        ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCapt.capture());
        Locacao locacaoRetornada = argCapt.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
    }

    @Test
    public void deveAlugarFilme_SemCalcularValor() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());
        PowerMockito.doReturn(1.0)
                .when(service, "calcularValorLocacao", filmes);

        // Ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // Verificação
        assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(service)
                .invoke("calcularValorLocacao", filmes);
    }
}