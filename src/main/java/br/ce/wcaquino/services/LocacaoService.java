package br.ce.wcaquino.services;

import br.ce.wcaquino.entities.Filme;
import br.ce.wcaquino.entities.Locacao;
import br.ce.wcaquino.entities.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

public class LocacaoService {

    public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws
            FilmeSemEstoqueException, LocadoraException {

        if (usuario == null) {
            throw new LocadoraException("Usuário vazio");
        }

        if (filmes.isEmpty()) {
            throw new LocadoraException("Filme vazio");
        }

        for (Filme filme : filmes) {
            if (filme.getEstoque() == 0) {
                throw new FilmeSemEstoqueException();
            }
        }

        Locacao locacao = new Locacao();
        locacao.setFilmes(filmes);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());
        locacao.setValor(somaValor(filmes));

        // Entrega no dia seguinte
        Date dataEntrega = new Date();
        dataEntrega = adicionarDias(dataEntrega, 1);
        locacao.setDataRetorno(dataEntrega);

        return locacao;
    }

    private Double somaValor(List<Filme> filmes) {

        Double soma = 0.0;

        for (Filme filme : filmes) {
            soma += filme.getPrecoLocacao();
        }
        return soma;
    }
}