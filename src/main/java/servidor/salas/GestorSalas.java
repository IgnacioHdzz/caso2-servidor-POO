package servidor.salas;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import servidor.red.SesionCliente;

/*
Patrón Singleton: un solo gestor para todo el servidor, para que todas las
conexiones encuentren las mismas salas.
 */

public class GestorSalas {

    private static final GestorSalas INSTANCIA = new GestorSalas();         // Único objeto. 

    private final ConcurrentHashMap<String, Sala> salas;    // El equivalente a un diccionario, para que guarde el código y la sala a la que esta asociada. 
                                                           // Concurrent, para que no hayan problemas de concurrencia al acceder a la misma sala desde distintos hilos.
    private final GeneradorCodigo generador;

    private GestorSalas() {  //Constructor privado, parte del patrón.
        this.salas = new ConcurrentHashMap<String, Sala>();
        this.generador = new GeneradorCodigo(new Random());
    }

    public static GestorSalas getInstancia() {
        return INSTANCIA;
    }

    // Crea una sala con un código que no esté en uso y mete adentro al creador. 
    public Sala crearSala(SesionCliente pCreador) {
        Sala sala = new Sala(generador.generar(), pCreador);
        while (salas.putIfAbsent(sala.getCodigo(), sala) != null) { // Validación de que el código generado no esté en uso. Si lo está, genera otro código y vuelve a intentar.
            sala = new Sala(generador.generar(), pCreador);
        }
        pCreador.setSala(sala);
        return sala;
    }

    // Busca sin importar mayúsculas o minúsculas. Devuelve null si no existe. 
    public Sala buscarSala(String pCodigo) {
        if (pCodigo == null) {
            return null;
        }
        return salas.get(pCodigo.trim().toUpperCase());
    }

    public void eliminarSala(String pCodigo) {
        salas.remove(pCodigo);
    }
}
