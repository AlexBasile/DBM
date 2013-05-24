package entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Alessandro Basile
 * @version 1.0
 *
 * Classe per la gestione del lessico delle parole trovate nei testi (da
 * considerare se salvare nel DB o la lasciare in Memoria Centrale)
 *
 * Ogni istanza del lessico viene considerata come la coppia: - parola - tf_d =
 * numero di documenti che la contangono
 */
public class Lexicon {

    private HashMap<String, Integer> lessico;
    private int max_tf;

    public Lexicon() {
        lessico = new HashMap<String, Integer>();
        this.max_tf = 0;
    }

    //Aggiunge una parola al lessico se e' gia' presente ne incrementa il tf_d
    public void addWord(String word) {

        //Se sono al primao inserimento imposto il tf_max a 1;
        if (this.getMax_tf() == 0) {
            this.setMax_tf(1);
        }

        if (this.getLessico().containsKey(word)) {
            //Se lo contiene incremento il valore del tf_d
            int occorrenze = this.getLessico().get(word).intValue();

            //Se sto aggiornato il termine con tf_d max allora incremento anche max_tf 
            if (this.getMax_tf() == occorrenze) {
                this.setMax_tf(this.getMax_tf() + 1);
            }

            this.getLessico().put(word, new Integer(occorrenze + 1));
        } else {
            //altrimenti lo agigungo con tf_d = 1;
            this.getLessico().put(word, new Integer(1));
        }

    }

    //Aggiunge un intera bag of word di un testo processato al lessico
    //richiama addWord() - Vedere sopra
    public void addBow(Collection<String> bow) {
        for (String word : bow) {
            this.addWord(word);
        }
    }

    //Restituisce il numero di docuemnti che contengono la parola data
    public int getTf_d(String word) {

        Integer tf = this.lessico.get(word);
        return tf.intValue();
    }

    //Restituisce la massima frequanza tra i termini presenti nel lessico
    public int getMaxTf() {
        return this.getMax_tf();
    }

    @Override
    public String toString() {
        return this.getLessico().toString();
    }

    /**
     * @return the lessico
     */
    public HashMap<String, Integer> getLessico() {
        return lessico;
    }

    /**
     * @param lessico the lessico to set
     */
    public void setLessico(HashMap<String, Integer> lessico) {
        this.lessico = lessico;
    }

    /**
     * @return the max_tf
     */
    public int getMax_tf() {
        return max_tf;
    }

    /**
     * @param max_tf the max_tf to set
     */
    public void setMax_tf(int max_tf) {
        this.max_tf = max_tf;
    }

    public void imposta_Max_tf() {
        Set<String> keys = this.lessico.keySet();
        int max = 0;
        for (String k : keys) {
            if (this.lessico.get(k).intValue() > max) {
                max = this.lessico.get(k).intValue();
            }
        }
        this.max_tf = max;
    }
}
