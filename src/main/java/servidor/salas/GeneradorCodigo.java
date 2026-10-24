package servidor.salas;

import java.util.Random;
import comun.constantes.ConstantesComunes;
import servidor.constantes.Constantes;

// Genera códigos de partida de 4 caracteres alfanuméricos. 
public class GeneradorCodigo {

    private final Random random; // Random como clase y no math.random(), para poder tener control del codigo generado en las pruebas.
 
    public GeneradorCodigo(Random pRandom) {
        this.random = pRandom;
    }

    public String generar() {
        StringBuilder codigo = new StringBuilder(); // StringBuilder para no crear muchos Strings en memoria.
        for (int i = 0; i < ConstantesComunes.LONGITUD_CODIGO; i++) {
            int posicion = random.nextInt(Constantes.CARACTERES_CODIGO.length());
            codigo.append(Constantes.CARACTERES_CODIGO.charAt(posicion));
        }
        return codigo.toString();
    }
}