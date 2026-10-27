package servidor.mapa;

import java.util.ArrayList;
import java.util.List;
import comun.modelo.Camino;
import comun.modelo.MapaBase;
import comun.modelo.Zona;
import servidor.modelo.Bandera;
import servidor.modelo.IInteractuable;
import servidor.modelo.TazaCafe;

/** El mapa en el servidor: la forma (MapaBase) más las tazas y las banderas. */
public class Mapa extends MapaBase {

    private final List<TazaCafe> tazas;
    private final List<Bandera> banderas;

    public Mapa(List<Zona> pZonas, List<Camino> pCaminos) {
        super(pZonas, pCaminos);
        this.tazas = new ArrayList<>();
        this.banderas = new ArrayList<>();
    }

    public void agregarTaza(TazaCafe pTaza) {
        tazas.add(pTaza);
    }

    public void quitarTaza(TazaCafe pTaza) {
        tazas.remove(pTaza);
    }

    public void agregarBandera(Bandera pBandera) {
        banderas.add(pBandera);
    }

    public List<IInteractuable> getInteractuables() {
        List<IInteractuable> resultado = new ArrayList<>();
        resultado.addAll(tazas);
        resultado.addAll(banderas);
        return resultado;
    }

    public List<TazaCafe> getTazas() {
        return tazas;
    }

    public List<Bandera> getBanderas() {
        return banderas;
    }

    /** Solo la forma del mapa: es lo que se envía al cliente al iniciar. */
    public MapaBase aDatos() {
        return new MapaBase(this);
    }
}
