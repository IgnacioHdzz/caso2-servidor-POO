package servidor.salas;

import comun.modelo.Especie;
import comun.protocolo.Mensaje;
import comun.protocolo.TipoMensaje;
import comun.protocolo.datos.DatosError;
import servidor.constantes.Constantes;
import servidor.juego.comandos.Comando;
import servidor.red.SesionCliente;

// Patrón State: cada etapa de la sala es una subclase.

// Es clase abstracta (y no interfaz) para dar un comportamiento por defecto a los métodos que no se sobrescriben, que es "rechazar". 
// Cada estado sobrescribe solo lo que sí acepta. Así, agregar un pedido nuevo no obliga a tocar todos los estados.


public abstract class EstadoSala {

    public void unirJugador(Sala pSala, SesionCliente pSesion) {
        rechazar(pSesion);
    }

    public void seleccionarEspecie(Sala pSala, SesionCliente pSesion, Especie pEspecie) {
        rechazar(pSesion);
    }

    public void recibirComando(Sala pSala, Comando pComando) {
        rechazar(pComando.getSolicitante());
    }

    // Cada estado está obligado a decidir qué pasa si alguien se desconecta. Porque se puede tratar de maneras diferentes dependiendo del estado.
    public abstract void alDesconectarse(Sala pSala, SesionCliente pSesion);

    protected void rechazar(SesionCliente pSesion) {
        enviarError(pSesion, Constantes.TEXTO_ACCION_NO_PERMITIDA);
    }

    protected void enviarError(SesionCliente pSesion, String pTexto) {
        pSesion.enviar(new Mensaje(TipoMensaje.ERROR, new DatosError(pTexto)));
    }
}