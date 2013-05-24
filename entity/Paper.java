package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Alessandro Basile
 * @version 1.0
 *
 * Classe per la gestione dei paper del DB
 */
public class Paper {

    //Informazioni del papaer
    private int cod_id;
    private String title;
    private String year;
    private String summary;
    //Strutture per gestire i surrogati dei testi elaborati;
    //La struttura definita permette  
    private HashMap<String, ArrayList<Double>> abstract_vector;
    private HashMap<String, ArrayList<Double>> title_vector;

    public Paper(int id, String titolo, String anno, String abst) {
        this.cod_id = id;
        this.title = titolo;
        this.year = anno;
        this.summary = abst;
        this.abstract_vector = new HashMap<String, ArrayList<Double>>();
        this.title_vector = new HashMap<String, ArrayList<Double>>();
    }

    public int getCod_id() {
        return this.cod_id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAbstract() {
        return this.getSummary();
    }

    public HashMap<String, ArrayList<Double>> getTitle_Vector() {
        return this.getTitle_vector();
    }

    public HashMap<String, ArrayList<Double>> getAbstract_Vector() {
        return this.getAbstract_vector();
    }

    public void setElementTitle_Vector(String parola, Double num) {
        ArrayList<Double> temp = new ArrayList<Double>();
        temp.add(num);
        this.getTitle_vector().put(parola, temp);
    }

    public void setElementAbstract_Vector(String parola, Double num) {
        ArrayList<Double> temp = new ArrayList<Double>();
        temp.add(num);
        this.getAbstract_vector().put(parola, temp);
    }
    //Aggiunge un token al vettore dei termini con il relativo peso TF

    public void addSummaryToken(String word, double tf) {
        //Crea la lista che conterra' i pesi del TF e IDF
        ArrayList<Double> pesi = new ArrayList<Double>(2);
        //Imposto solo il TF
        pesi.add(new Double(tf));
        //IDF si puo' calcolare  solo dopo aver formato il lessico per intero
        this.getAbstract_vector().put(word, pesi);
    }

    //come addSummaryToken - Per titotlo
    public void addTitleToken(String word, double tf) {
        //Crea la lista che conterra' i pesi del TF e IDF
        ArrayList<Double> pesi = new ArrayList<Double>(2);
        //Imposto solo il TF
        pesi.add(new Double(tf));
        //IDF si puo' calcolare  solo dopo aver formato il lessico per intero
        this.getTitle_vector().put(word, pesi);
    }

    @Override
    public String toString() {
        return "cod_id " + this.getCod_id() + "\n titolo: \n" + this.getTitle() + "\n abstract:\n" + this.getSummary() + "\n";
    }

    public String printPaper() {

        String text = "";
        text = text + "cod_id " + this.getCod_id() + "\n";


        text = text + "Vettore titolo: " + "\n";
        Set<String> keys = this.getTitle_vector().keySet();
        for (String chiave : keys) {
            text = text + "word: " + chiave + " - tf: " + this.getTitle_vector().get(chiave).get(0) + "\n";
        }

        text = text + "-------" + "\n";
        text = text + "Vettore abstract: " + "\n";
        keys = this.getAbstract_vector().keySet();

        double test = 0;
        for (String chiave : keys) {
            text = text + "word: " + chiave + " - tf: " + this.getAbstract_vector().get(chiave).get(0) + "\n";
            test += this.getAbstract_vector().get(chiave).get(0).doubleValue();
        }

        text = text + test + "  --|-----------|------------|--\n";
        return text;
    }

    /**
     * @param cod_id the cod_id to set
     */
    public void setCod_id(int cod_id) {
        this.cod_id = cod_id;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the abstract_vector
     */
    public HashMap<String, ArrayList<Double>> getAbstract_vector() {
        return abstract_vector;
    }

    /**
     * @param abstract_vector the abstract_vector to set
     */
    public void setAbstract_vector(HashMap<String, ArrayList<Double>> abstract_vector) {
        this.abstract_vector = abstract_vector;
    }

    /**
     * @return the title_vector
     */
    public HashMap<String, ArrayList<Double>> getTitle_vector() {
        return title_vector;
    }

    /**
     * @param title_vector the title_vector to set
     */
    public void setTitle_vector(HashMap<String, ArrayList<Double>> title_vector) {
        this.title_vector = title_vector;
    }
}
