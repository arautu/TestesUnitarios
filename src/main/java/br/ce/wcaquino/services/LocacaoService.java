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

        // Aplica descontos
        switch (filmes.size()) {
            default:
            case 6:
                Double valorComDesconto = aplicaDesconto(
                        filmes.get(5).getPrecoLocacao(),
                        100.0);
                filmes.get(5).setPrecoLocacao(valorComDesconto);
            case 5:
                valorComDesconto = aplicaDesconto(
                        filmes.get(4).getPrecoLocacao(),
                        75.0);
                filmes.get(4).setPrecoLocacao(valorComDesconto);
            case 4:
                valorComDesconto = aplicaDesconto(
                        filmes.get(3).getPrecoLocacao(),
                        50.0);
                filmes.get(3).setPrecoLocacao(valorComDesconto);
            case 3:
                valorComDesconto = aplicaDesconto(
                        filmes.get(2).getPrecoLocacao(),
                        25.0);
                filmes.get(2).setPrecoLocacao(valorComDesconto);
            case 2:
            case 1:
            case 0:
        }
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

    private Double aplicaDesconto(Double precoItem, Double descontoPorCento) {
        return precoItem * (1 - descontoPorCento / 100);
    }


}