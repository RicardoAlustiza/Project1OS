/*
 * Almacen.java 
 * Contiene la definicion de la clase de Almacen.
 * Esta clase se encarga de manejar los almacenes para los diferentes productos 
 * de la fabrica y controla el acceso a los productos, bloquendo el acceso para 
 * que solo un hilo a la vez pueda usarlo
 */
package fabricadejuguetes;

/**
 *
 * @author Ivan Arno y Ricardo Alustiza
 */
public class Almacen {
    private int capacidad;      // Capacidad total del almacen
    private int cantidad;       // Numero de productos en el almacen
    private int [] vec;
    private int apuntP;
    private int apuntC;
    
    public Almacen(int capacidad) {
        this.capacidad=capacidad;   
        this.cantidad=0;
        this.vec=new int[capacidad];
        this.apuntP=0;
        this.apuntC=0;
    }
    // saca  (retira) un producto del almacen, permite el acceso de un solo hilo
    public void saca() {
        this.vec[apuntC] = 0;
        apuntC=(apuntC+1)%capacidad;
        cantidad=getCantidad();
    }
    // guarda un producto en el almacen, permite el acceso de un solo hilo
    public void guarda() {
        this.vec[apuntP] = 1;
        apuntP=(apuntP+1)%capacidad;
        cantidad=getCantidad();
    }
    // regresa True si el almacen esta lleno
    public boolean lleno() {
        return (cantidad==capacidad);
    }
    
    // regresa el numero de productos en almacen
    public int getCantidad() {
        int aux=0;
        for (int i = 0; i < capacidad; i++) {
            if(vec[i]==1){
                aux++;
            } 
        }        
        return aux;       
    }
}
