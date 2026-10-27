package servidor.salas;

import comun.protocolo.Mensaje;
import comun.protocolo.TipoMensaje;
import comun.protocolo.datos.DatosPartidaUnida;
import servidor.red.SesionCliente;

// La sala tiene un solo jugador y espera al segundo.
public class EstadoEsperandoRival extends EstadoSala {

    @Override
    public void unirJugador(Sala pSala, SesionCliente pSesion) {
        pSala.agregarSesion(pSesion);
        SesionCliente creador = pSala.getRival(pSesion);

        // Cada uno recibe el nickname del otro.
        pSesion.enviar(new Mensaje(TipoMensaje.MATCH_JOINED, new DatosPartidaUnida(pSala.getCodigo(), creador.getNickname())));
        creador.enviar(new Mensaje(TipoMensaje.MATCH_JOINED, new DatosPartidaUnida(pSala.getCodigo(), pSesion.getNickname())));

        pSala.cambiarEstado(new EstadoSeleccionEspecies());
        pSala.enviarSeleccionEspecies();
    }

    @Override
    public void alDesconectarse(Sala pSala, SesionCliente pSesion) {
        // Estaba solo, no hay a quién avisar. Se cierra la sala y el código queda libre.
        pSala.cerrar();
    }
}