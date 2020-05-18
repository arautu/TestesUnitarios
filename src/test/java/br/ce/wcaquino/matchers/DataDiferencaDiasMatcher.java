package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {

    private Integer qtdDias;

    public DataDiferencaDiasMatcher(Integer qtdDias) {
        this.qtdDias = qtdDias;
    }

    @Override
    protected boolean matchesSafely(Date data) {

        return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(qtdDias));
    }

    @Override
    public void describeTo(Description description) {

        Date hoje = new Date();
        Calendar data = Calendar.getInstance();
        data.setTime(hoje);
        data.add(Calendar.DATE, qtdDias);
        String dataString = data.getDisplayName(Calendar.DAY_OF_WEEK , Calendar.LONG, Locale.US);
        description.appendText(dataString);
    }
}