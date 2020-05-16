package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Date;

public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {

    private Integer diaSemana;

    public DiaSemanaMatcher(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    @Override
    public void describeTo(Description description) {

    }

    @Override
    protected boolean matchesSafely(Date data) {
        return DataUtils.verificarDiaSemana(data, diaSemana);
    }
}
