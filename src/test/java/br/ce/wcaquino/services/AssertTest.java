package br.ce.wcaquino.services;

import br.ce.wcaquino.entities.Usuario;
import org.junit.Assert;
import org.junit.Test;

public class AssertTest {

    @Test
    public void test() {
        Assert.assertTrue(true);
        Assert.assertFalse(false);

        Assert.assertEquals(0.51, 0.512, 0.01);

        int i = 5;
        Integer i2 = 5;
        Assert.assertEquals("Erro de comparação", Integer.valueOf(i), i2);
        Assert.assertEquals(i, i2.intValue());

        Assert.assertEquals("bola", "bola");
        Assert.assertNotEquals("bola", "casa");
        Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
        Assert.assertTrue("bola".startsWith("bo"));

        Usuario u1 = new Usuario("Usuario 1");
        Usuario u2 = new Usuario("Usuario 1");
        Usuario u3 = null;


        Assert.assertEquals(u1, u2);
        Assert.assertSame(u2, u2);
        Assert.assertNotSame(u2, u3);
        Assert.assertNull(u3);
        Assert.assertNotNull(u2);
    }
}
