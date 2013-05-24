/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbm_project;

import Db_Connection.*;
import Interface.Finestra;

import TextAnalizer.Controller;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WatchTower
 */
public class DBM_Project {

    public static void main(String[] args) throws IOException {

        //Inizializzo la connessione al DB all'avvio dell'applicazione
        DBAccess.initConnection();
        //Schedulo la creazione del controller principale in nuovo Thread 
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    //Creo il controller principale
                    Controller main_controller;
                    //Crea la finestra pricipale
                    Finestra main_window = new Finestra();
                    //Imposto la finestra che deve controllare il controller
                    main_controller = new Controller(main_window);
                    //setto nella gui a quale controller fare riferimento
                    main_window.set_Controller(main_controller);
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(DBM_Project.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DBM_Project.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
