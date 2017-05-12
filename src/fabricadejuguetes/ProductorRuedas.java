/*
 * ProductorRuedas.java
 * Contiene la definicion de la clase de ProductorDeRuedas, que es una especializacion
 * de la clase de Productor. Esta clase es necesaria pues el productor de ruedas necesita
 * usar diferentes productos para producir una rueda.
 */
package fabricadejuguetes;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivan Arno y Ricardo Alustiza
 */
public class ProductorRuedas extends Productor {
    Almacen almacenNeumaticos;
    Almacen almacenBujes;
    Semaphore sPb;
    Semaphore sCb;
    Semaphore sEb;
    
    Semaphore sPn;
    Semaphore sCn;
    Semaphore sEn;   
    
    public ProductorRuedas(Almacen almacenRuedas,Almacen almacenNeumaticos,
                        Almacen almacenBujes,int horasProduccion,Tiempo tiempo,
                        Semaphore sP,Semaphore sC,Semaphore sE,
                        Semaphore sPb,Semaphore sCb,Semaphore sEb,
                        Semaphore sPn,Semaphore sCn,Semaphore sEn) {
        super(almacenRuedas,horasProduccion,tiempo,sP,sC,sE);
        this.almacenNeumaticos  = almacenNeumaticos;
        this.almacenBujes = almacenBujes;
        this.sPb=sPb;
        this.sCb=sCb;
        this.sEb=sEb;
        this.sPn=sPn;
        this.sCn=sCn;
        this.sEn=sEn;
    }
    @Override
    public void run()
    {      
        while (estado!=2) {
            try {
                if(estado==1){
                    this.semHilo.acquire();                    
                }                
                if(estado==0) {
                    sCb.acquire();
                    sEb.acquire();
                    almacenBujes.saca();        // obtiene un buje
                    sEb.release();
                    sPb.release();

                    sCn.acquire();
                    sEn.acquire();
                    almacenNeumaticos.saca();   // obtiene un neumatico
                    sEn.release();
                    sPn.release();
                }
                long horaInicial=tiempo.hora();
                while (tiempo.hora() - horaInicial<horasProduccion
                        && estado!=2) { // ciclo por el tiempo de produccion
                    this.sleep(10); 
                }
                if (estado==0) {
                    sP.acquire();
                    sE.acquire();
                    almacen.guarda();   // guarda el producto obtenido despues de esperar el tiempo de produccion
                    sE.release();
                    sC.release();                                        
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ProductorRuedas.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }                
    }   
}
