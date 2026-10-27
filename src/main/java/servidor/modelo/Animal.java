package servidor.modelo;

import comun.modelo.AnimalBase;
import comun.modelo.Especie;
import comun.modelo.Posicion;
import comun.modelo.Zona;
import servidor.constantes.Constantes;
import servidor.juego.Combate;
import servidor.juego.Partida;
import servidor.mapa.Mapa;
import servidor.mapa.Ruta;

// Modelo del animal del servidor; actualizar es el Template Method del tick.
public abstract class Animal extends AnimalBase {

    private final Manada manada;
    private Ruta ruta;
    private long ultimoAtaqueMs;

    protected Animal(int pId, Posicion pPosicion, int pIdZona, Especie pEspecie,
                     int pEnergia, Manada pManada) {
        super(pId, pPosicion, pIdZona, pEspecie, pEnergia, pManada.getId(), pManada.esBot());
        this.manada = pManada;
        this.ultimoAtaqueMs = -Constantes.ENFRIAMIENTO_ATAQUE_MS;
    }

    public final void actualizar(double pDeltaSegundos, long pAhoraMs, Partida pPartida, Combate pCombate) {
        if (!estaVivo()) {
            return;
        }
        avanzar(pDeltaSegundos, pPartida.getMapa());
        actuar(pAhoraMs, pPartida, pCombate);
    }

    protected abstract void actuar(long pAhoraMs, Partida pPartida, Combate pCombate);

    private void avanzar(double pDeltaSegundos, Mapa pMapa) {
        if (ruta == null || ruta.haTerminado() || pDeltaSegundos <= 0) {
            return;
        }
        double distanciaDisponible = Constantes.VELOCIDAD_ANIMAL * pDeltaSegundos;
        while (!ruta.haTerminado()) {
            Posicion destino = ruta.siguientePunto();
            Posicion origen = getPosicion();
            double distancia = origen.distanciaA(destino);

            if (distancia <= distanciaDisponible) {
                moverEnMapa(destino, pMapa);
                ruta.avanzarAlSiguiente();
                distanciaDisponible -= distancia;
                if (distanciaDisponible <= 0) {
                    break;
                }
            } else {
                double proporcion = distanciaDisponible / distancia;
                Posicion nueva = new Posicion(
                    origen.getX() + (destino.getX() - origen.getX()) * proporcion,
                    origen.getY() + (destino.getY() - origen.getY()) * proporcion
                );
                moverEnMapa(nueva, pMapa);
                break;
            }
        }
    }

    private void moverEnMapa(Posicion pPosicion, Mapa pMapa) {
        Zona zona = pMapa.zonaDe(pPosicion);
        // En los caminos entre zonas se conserva la última zona conocida.
        moverA(pPosicion, zona == null ? getIdZona() : zona.getId());
    }

    public void asignarRuta(Ruta pRuta) {
        this.ruta = pRuta;
    }

    // AnimalJugador define su propia respuesta a esta orden.
    public void recibirOrdenAtaque(Animal pObjetivo) {
    }

    public void recibirDanio(int pDanio) {
        if (pDanio > 0) {
            setEnergia(Math.max(0, getEnergia() - pDanio));
        }
    }

    public void curar(int pCantidad, int pMaximo) {
        if (estaVivo() && pCantidad > 0) {
            setEnergia(Math.min(pMaximo, getEnergia() + pCantidad));
        }
    }

    public boolean puedeAtacar(long pAhoraMs) {
        return estaVivo() && pAhoraMs - ultimoAtaqueMs >= Constantes.ENFRIAMIENTO_ATAQUE_MS;
    }

    public void registrarAtaque(long pAhoraMs) {
        ultimoAtaqueMs = pAhoraMs;
    }

    public boolean estaEnRango(Animal pOtro) {
        return pOtro != null && pOtro.estaVivo()
            && getPosicion().distanciaA(pOtro.getPosicion()) <= Constantes.RADIO_ENCUENTRO;
    }

    public Manada getManada() {
        return manada;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public AnimalBase aDatos() {
        return new AnimalBase(this);
    }
}
