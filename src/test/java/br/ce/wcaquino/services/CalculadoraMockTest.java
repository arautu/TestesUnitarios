package br.ce.wcaquino.services;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class CalculadoraMockTest {

    @Test
    public void teste() {
        Calculadora calc = mock(Calculadora.class);
        when(calc.somar(eq(1), anyInt())).thenReturn(5);

        System.out.println(calc.somar(1, 8));
    }
}
