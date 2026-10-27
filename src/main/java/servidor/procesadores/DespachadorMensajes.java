package servidor.procesadores;

import java.util.EnumMap;
import java.util.Map;
import comun.protocolo.Mensaje;
import comun.protocolo.TipoMensaje;
import servidor.red.SesionCliente;
import servidor.constantes.Constantes;

/**
 * Busca el procesador de cada tipo de mensaje en un mapa, en lugar de usar un
 * switch sobre el tipo (la misma idea del Casino Game Server de la Semana 8-9).
 */
public class DespachadorMensajes {

    private final Map<TipoMensaje, IProcesadorMensaje> procesadores;

    public DespachadorMensajes() {
        this.procesadores = new EnumMap<>(TipoMensaje.class);
        procesadores.put(TipoMensaje.CREATE_MATCH, new ProcesadorCrearPartida());
        procesadores.put(TipoMensaje.JOIN_MATCH, new ProcesadorUnirsePartida());
        procesadores.put(TipoMensaje.SELECT_ANIMAL, new ProcesadorSeleccionarEspecie());
        procesadores.put(TipoMensaje.MOVE_ANIMALS, new ProcesadorMoverAnimales());
        procesadores.put(TipoMensaje.ATTACK, new ProcesadorAtacar());
        procesadores.put(TipoMensaje.VIEW_ZONE, new ProcesadorVerZona());
    }

    public void despachar(Mensaje pMensaje, SesionCliente pSesion) {
        IProcesadorMensaje procesador = procesadores.get(pMensaje.getTipo());
        if (procesador == null) {
            pSesion.enviarError(Constantes.TEXTO_TIPO_DESCONOCIDO);
            return;
        }
        procesador.procesar(pMensaje, pSesion);
    }
}
