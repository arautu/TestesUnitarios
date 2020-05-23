package br.ce.wcaquino.daos;

import br.ce.wcaquino.entities.Locacao;

import java.util.List;

public class LocacaoDAOFake implements LocacaoDAO {

    @Override
    public void salvar(Locacao locacao) {

    }

    @Override
    public List<Locacao> obterLocacoesPendentes() {
        return null;
    }
}
