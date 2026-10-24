package servidor.salas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Random;
import org.junit.jupiter.api.Test;
import comun.constantes.ConstantesComunes;
import servidor.constantes.Constantes;

class GeneradorCodigoTest {

    @Test
    void generaCodigosDeCuatroCaracteresPermitidos() {
        GeneradorCodigo generador = new GeneradorCodigo(new Random(42));
        for (int i = 0; i < 100; i++) {
            String codigo = generador.generar();
            assertEquals(ConstantesComunes.LONGITUD_CODIGO, codigo.length());
            for (char caracter : codigo.toCharArray()) {
                assertTrue(Constantes.CARACTERES_CODIGO.indexOf(caracter) >= 0);
            }
        }
    }

    @Test
    void conLaMismaSemillaGeneraElMismoCodigo() {
        GeneradorCodigo primero = new GeneradorCodigo(new Random(7));
        GeneradorCodigo segundo = new GeneradorCodigo(new Random(7));
        assertEquals(primero.generar(), segundo.generar());
    }
}