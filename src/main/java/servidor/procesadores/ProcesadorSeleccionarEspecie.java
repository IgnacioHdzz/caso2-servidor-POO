package servidor.procesadores;

import comun.protocolo.Mensaje;
import comun.protocolo.datos.DatosSeleccionarEspecie;
import servidor.red.SesionCliente;
import servidor.salas.Sala;

// SELECT_ANIMAL para que el cliente eliga una especie. 
public class ProcesadorSeleccionarEspecie extends ProcesadorEnSala {

    @Override
    protected void procesarEnSala(Mensaje pMensaje, SesionCliente pSesion, Sala pSala) {
        DatosSeleccionarEspecie datos = pMensaje.getDatos(DatosSeleccionarEspecie.class);
        pSala.seleccionarEspecie(pSesion, datos.getEspecie());
    }
}