package br.ce.wcaquino.services;

import br.ce.wcaquino.entities.Filme;
import br.ce.wcaquino.entities.Locacao;
import br.ce.wcaquino.entities.Usuario;
import org.junit.Test;

import java.util.Date;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class LocacaoServiceTest {

    // cenario
    LocacaoService service = new LocacaoService();
    Usuario usuario = new Usuario("Usuario 1");
    Filme filme = new Filme("Filme 1", 2, 5.0);

    // ação
    Locacao locacao = service.alugarFilme(usuario, filme);

    @Test
    public void testeLocacaoValor() {
        // verificação
        assertThat(locacao.getValor(), is(equalTo(5.0)));
    }

    @Test
    public void testeDataLocacao() {
        assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
    }

    @Test
    public void testeDataRetorno() {
        assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)),
                is(true));
    }
}
