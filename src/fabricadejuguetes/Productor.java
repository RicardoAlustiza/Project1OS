/*
 * Productor.java
 * Contiene la definicion de la clase de Productor, que representa un productor
 * en la fabrica de juguetes.
 * 
 */
package fabricadejuguetes;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivan Arno y Ricardo Alustiza
 */
public class Productor extends Thread {
    Almacen almacen;
    int horasProduccion;
    Tiempo tiempo;
    final Semaphore semHilo;   // Semaforo usado para controlar ejecucion del hilo

    Semaphore sP;
    Semaphore sC;
    Semaphore sE;   
    
    int estado; // estado del hilo 0==corriendo, 1==pausado, 2==terminado
    
    public Productor(Almacen almacen,int horasProduccion,Tiempo tiempo,Semaphore sP,Semaphore sC,Semaphore sE) {
        this.almacen=almacen;
        this.horasProduccion=horasProduccion;
        this.tiempo=tiempo;
        this.sP=sP;
        this.sC=sC;
        this.sE=sE;
        semHilo = new Semaphore(1); // permite solo una coneccion al mismo tiempo
        estado=0;   // corriendo
    }
    @Override
    public void run()
    {      
        while (estado!=2) { // mientras que el hilo este corriendo o pausado
             try {
                if(estado==1){
                    this.semHilo.acquire();                    
                }                
                 
                long horaInicial=tiempo.hora();
                while (tiempo.hora() - horaInicial<horasProduccion &&
                        estado!=2) { // ciclo por el tiempo de produccion                
                    this.sleep(10); 
                }
                if (estado==0) {
                    sP.acquire();
                    sE.acquire();
                    almacen.guarda();   // al terminar el tiempo de produccion, guarda el producto en almacen
                    sE.release();
                    sC.release();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Productor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }                
    }
    //pausa el productor
    public void pausa() {
        if(estado==1)   // si el estado es pausado
            return;
        estado=1;   // establece estado como pausado
        try {
            semHilo.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Productor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //reanuda el productor
    public void reanuda() {
        if(estado==0)
            return;
        estado=0;   // establece estado como corriendo
        semHilo.release();
    }
    public void detiene() {
        if(estado==2)
            return;
        estado=2;   // pone estado en 2 para que el hilo termine
    }
}
