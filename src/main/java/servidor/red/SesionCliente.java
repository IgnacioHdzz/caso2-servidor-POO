package servidor.red;

import java.util.concurrent.ExecutorService;
import comun.protocolo.ConvertidorJson;
import comun.protocolo.Mensaje;
import comun.red.Conexion;
import comun.red.EscritorMensajes;
import comun.red.IReceptorMensajes;
import comun.red.LectorMensajes;
import servidor.procesadores.DespachadorMensajes;
import servidor.salas.Sala;

/**
 * Un cliente conectado: su línea, sus hilos de lectura y escritura, su nickname
 * y la sala en la que está. Existe desde que se conecta hasta que se desconecta.
 */
public class SesionCliente implements IReceptorMensajes {

    private final Conexion conexion;
    private final LectorMensajes lector;
    private final EscritorMensajes escritor;
    private final DespachadorMensajes despachador;
    private String nickname;
    private volatile Sala sala;

    public SesionCliente(Conexion pConexion, ConvertidorJson pConvertidor, DespachadorMensajes pDespachador) {
        this.conexion = pConexion;
        this.despachador = pDespachador;
        this.lector = new LectorMensajes(pConexion, pConvertidor, this);    
        this.escritor = new EscritorMensajes(pConexion, pConvertidor);
    }

       public void iniciar(ExecutorService pHilos) {
        pHilos.execute(lector);
        pHilos.execute(escritor);
    }

    // Lo pueden llamar varios hilos, el mensaje queda en la cola del escritor. 
    public void enviar(Mensaje pMensaje) {
        escritor.enviar(pMensaje);
    }

    @Override
    public void recibir(Mensaje pMensaje) {         // Cuando el lector escucha algo llama a recibir de esta sesión, por que se paso como parametro en el LectorMensajes. 
        despachador.despachar(pMensaje, this);
    }

    @Override
    public void alDesconectarse() {
        Sala salaActual = sala; // Se copia una variable local aqui, porque entre la pregunta del if y su uso otro hilo podría poner "sala" en null (por ejemplo, el motor al cerrar la sala). Entonces de esta forma se guarda el valor para la pregunta.
        if (salaActual != null) {
            salaActual.alDesconectarse(this); // El estado de la sala decide qué hacer, si avisar al otro jugador o cerrar la sala.
        }
        escritor.detener();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String pNickname) {
        this.nickname = pNickname;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala pSala) {
        this.sala = pSala;
    }

    public boolean estaConectada() {
        return !conexion.estaCerrada();
    }
}
