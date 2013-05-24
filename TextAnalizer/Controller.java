/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TextAnalizer;

import Db_Connection.DBAccess;
import Db_Connection.DB_Stream;
import Db_Connection.TransazioniDB;
import Interface.Finestra;
import entity.Lexicon;
import entity.Paper;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import matlabcontrol.*;

/**
 * @author Alessandro Basile
 * @version 1.0
 *
 * Classe che controlla il flusso delle operazioni da poter effettuare
 */
public class Controller {

    private TransazioniDB connector;
    private ArrayList<Paper> articoli;
    private Analizzatore reader;
    private Finestra gui;
    private final int annoMax = 2013;
    private final int annoMin = 1980;
    private MatlabProxy proxy;

    public Controller(Finestra main) {

        if (DBAccess.checkConnection()) {
            //inizializzo le variabili per usarle durante tutta la sessione;
            this.connector = new DB_Stream();
            this.articoli = connector.read_papers();
            this.reader = new Analizzatore(connector.read_stopWords(), articoli);
            this.gui = main;
            this.gui.setVisible(true);
            this.gui.set_DB_Status(this.connector.dbpieno());
            //Se il DB e' stato precedentemente analizzato carico dal DB i vettori
            if (this.connector.dbpieno()) {
                this.connector.read_Vectors(articoli);
                this.connector.read_lexicon(this.reader.getLessico_titoli().getLessico(),
                        this.reader.getLessico_articoli().getLessico());
                this.reader.getLessico_articoli().imposta_Max_tf();
                this.reader.getLessico_titoli().imposta_Max_tf();

            }
            try {
                //MatlabProxyFactory factory = new MatlabProxyFactory();
                //this.proxy = factory.getProxy();
                //WINDOWS
                // proxy.eval("cd (\'\\Users\\Alberto\\Documents\\MATLAB\\DBM\\')");
                //TOBIA
                //proxy.eval("cd /Users/tobiagiani/NetBeansProjects/DBM_Project/");
                //ALEX
                //proxy.eval("cd /Users/WatchTower/Sviluppo/DBM_Project/");
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }

    public boolean DB_Analizzato() {
        return this.connector.dbpieno();
    }

    public void analisi_DB() {

        this.articoli = connector.read_papers();
        this.reader = new Analizzatore(connector.read_stopWords(), articoli);

        //inizio l'analisi dei documenti caricati dal DB
        this.reader.start_analizing();
        //scrivo i risultati nel DB - vettori e lessico
        this.connector.write_Vectors(articoli);
        this.connector.write_lexicon(reader.getLessico_titoli().getLessico(),
                reader.getLessico_articoli().getLessico());

        if (this.gui.get_details()) {
            for (Paper a : articoli) {
                this.gui.stampa_testo(true, a.printPaper());
            }
        }

        this.connector.read_Vectors(articoli);
        this.connector.read_lexicon(reader.getLessico_titoli().getLessico(), reader.getLessico_articoli().getLessico());
        this.reader.getLessico_articoli().imposta_Max_tf();
        this.reader.getLessico_titoli().imposta_Max_tf();
        this.gui.stampa_testo(true,
                "Analisi completata su " + articoli.size() + " documenti.\n"
                + "I risultati sono stati scritti nel database.\n");

        this.gui.stampa_testo(true, reader.getLessico_titoli().getLessico().size() + " " + reader.getLessico_articoli().getLessico().size());
        this.gui.set_DB_Status(connector.dbpieno());
    }

    public void print_paper_vector(int id_paper) {
        boolean trovato = false;
        //controllo che sia presente nella lista degli articoli e lo stampo
        for (Paper p : articoli) {
            if (p.getCod_id() == id_paper) {
                this.gui.stampa_testo(true, p.printPaper() + "\n");
                trovato = true;
                break;
            }
        }
        //Se non e' stato trovato stampo un messaggio di errore
        if (!trovato) {
            this.gui.stampa_testo(true, "ATTENZIONE - Paper non presente nel DB\n");
        }
    }

    //appende all'arraylist delle keyword del paper p, il valore tfidf calcolato 
    public void calculate_tfidf_vector(HashMap<String, ArrayList<Double>> titolo, HashMap<String, ArrayList<Double>> summary) {
        int n_paper = this.articoli.size();

        Set<String> keys = titolo.keySet();
        for (String k : keys) {
            double tf = titolo.get(k).get(0);
            double idf = Math.log((double) n_paper / (double) this.reader.getLessico_titoli().getTf_d(k));
            double tf_idf_norm = tf * (idf);
            if (titolo.get(k).size() < 2) {
                titolo.get(k).add(new Double(Analizzatore.roundToSignificantFigures(tf_idf_norm, 6)));
            } else {
                titolo.get(k).set(1, new Double(Analizzatore.roundToSignificantFigures(tf_idf_norm, 6)));
            }
        }

        keys = summary.keySet();
        for (String k : keys) {
            double tf = summary.get(k).get(0);
            double idf = Math.log((double) n_paper / (double) this.reader.getLessico_articoli().getTf_d(k));
            double max_idf = Math.log((double) n_paper / (double) this.reader.getLessico_articoli().getMaxTf());
            double tf_idf_norm = tf * (idf / max_idf);
            if (summary.get(k).size() < 2) {
                summary.get(k).add(new Double(Analizzatore.roundToSignificantFigures(tf_idf_norm, 6)));
            } else {
                summary.get(k).set(1, new Double(Analizzatore.roundToSignificantFigures(tf_idf_norm, 6)));
            }
        }

    }

    public void print_tf_vector(int id_paper) {
        boolean trovato = false;

        //controllo che sia presente nella lista degli articoli e lo stampo
        for (Paper p : articoli) {
            if (p.getCod_id() == id_paper) {

                HashMap<String, ArrayList<Double>> titolo = p.getTitle_vector();
                HashMap<String, ArrayList<Double>> summary = p.getAbstract_vector();
                calculate_tfidf_vector(titolo, summary);
                this.gui.stampa_testo(true, "CODICE PAPER: " + p.getCod_id() + "\n\n");
                this.gui.stampa_testo(true, "VETTORE TITOLO: \n\n");

                if (this.gui.get_tipologiaTF() == 0) {
                    //STAMPA VETTORE TITOLO
                    this.gui.stampa_testo(true, "WORD\t| TF\n");
                    Set<String> keys = titolo.keySet();
                    for (String k : keys) {
                        double tf = titolo.get(k).get(0);
                        this.gui.stampa_testo(true, k + "\t|  " + tf + "\n");
                    }
                    this.gui.stampa_testo(true, "\n");
                    //STAMPA VETTORE ABSTRACT;
                    this.gui.stampa_testo(true, "VETTORE ABSTRACT: \n\n");
                    this.gui.stampa_testo(true, "WORD\t| TF\n");
                    keys = summary.keySet();
                    for (String k : keys) {
                        double tf = summary.get(k).get(0);
                        this.gui.stampa_testo(true, k + "\t|  " + tf + "\n");
                    }
                } else {
                    this.gui.stampa_testo(true, "WORD\t| TF-IDF\n");
                    Set<String> keys = titolo.keySet();
                    for (String k : keys) {
                        double tfidf = titolo.get(k).get(1);
                        this.gui.stampa_testo(true, k + "\t|  " + tfidf + "\n");
                    }
                    this.gui.stampa_testo(true, "\n");
                    //STAMPA VETTORE ABSTRACT;
                    this.gui.stampa_testo(true, "VETTORE ABSTRACT: \n\n");
                    this.gui.stampa_testo(true, "WORD\t| TF-IDF\n");
                    keys = summary.keySet();
                    for (String k : keys) {
                        double tfidf = summary.get(k).get(1);
                        this.gui.stampa_testo(true, k + "\t|  " + tfidf + "\n");
                    }
                }
                trovato = true;
                break;
            }
        }
        //Se non e' stato trovato stampo un messaggio di errore
        if (!trovato) {
            this.gui.stampa_testo(true, "ATTENZIONE - Paper non presente nel DB\n");
        }
    }

    private ArrayList<Paper> extract_written(Set<Integer> written) {
        ArrayList<Paper> scritti = new ArrayList<Paper>();
        for (Paper p : this.articoli) {
            if (written.contains(new Integer(p.getCod_id()))) {
                scritti.add(p);
            }
        }
        return scritti;
    }

    /////////////////////SEZIONE COMBINED AUTHOR VECTOR/////////////////////////////
    private void calulate_author_vector(HashMap<String, Double> combined_ti,
            HashMap<String, Double> combined_ab, ArrayList<Paper> scritti) {

        for (Paper p : scritti) {
            //calcolo il peso del paper scalandolo sull'intervallo di anni di pubblicazione
            int anno = Integer.parseInt(p.getYear());
            double peso = 1 - ((double) (this.annoMax - anno) / (double) (this.annoMax - (this.annoMin - 1)));

            //calcoliamo vettore dei titoli;
            Set<String> key = p.getTitle_Vector().keySet();
            for (String k : key) {
                double weight;
                double tf = p.getTitle_Vector().get(k).get(0);
                double tf_n = peso * tf;

                //Calcolo il peso in base al cosa e' stato selezionato
                if (this.gui.get_tipologia2TF() == 0) { //considero i TF
                    weight = tf_n / scritti.size();
                } else { //considero gli IDF
                    double idf = Math.log((double) this.articoli.size() / (double) this.reader.getLessico_titoli().getTf_d(k));
                    double tf_idf_n = tf_n * idf;
                    weight = tf_idf_n / scritti.size();
                }

                weight = Analizzatore.roundToSignificantFigures(weight, 6);
                //Aggiungo al comined vector dei titoli; 
                if (combined_ti.containsKey(k)) { //Se il combined vector contiene gia' la parola;
                    combined_ti.put(k, combined_ti.get(k).doubleValue() + weight);
                } else {                     //Altrimenti primo inserimento;
                    combined_ti.put(k, weight);
                }
            }

            //calcoliamo gli abstract
            key = p.getAbstract_Vector().keySet();
            for (String k : key) {
                double weight;
                double tf = p.getAbstract_Vector().get(k).get(0);
                double tf_n = peso * tf;

                //Calcolo il peso in base al cosa e' stato selezionato
                if (this.gui.get_tipologia2TF() == 0) { //considero i TF
                    weight = tf_n;
                } else { //considero gli IDF
                    double idf = Math.log((double) this.articoli.size() / (double) this.reader.getLessico_articoli().getTf_d(k));
                    double max_idf = Math.log((double) this.articoli.size() / (double) this.reader.getLessico_articoli().getMaxTf());
                    double tf_idf_n = tf_n * (idf / max_idf);
                    weight = tf_idf_n;
                }

                weight = Analizzatore.roundToSignificantFigures(weight, 6);
                //Aggiungo al comined vector dei titoli; 
                if (combined_ab.containsKey(k)) { //Se il combined vector contiene gia' la parola;
                    combined_ab.put(k, combined_ab.get(k).doubleValue() + weight);
                } else {                     //Altrimenti primo inserimento;
                    combined_ab.put(k, weight);
                }
            }
        }

    }

    //Funzione di stampa del combined Author Vector - Es 1 - task 2
    public void print_author_vector(int id_author) {
        Set<Integer> written = this.connector.read_papers_for_author(id_author);
        if (written.size() > 0) {
            ArrayList<Paper> scritti = this.extract_written(written);

            HashMap<String, Double> combined_ti;
            combined_ti = new HashMap<String, Double>();
            HashMap<String, Double> combined_ab = new HashMap<String, Double>();
            //calcolo i combined vector
            this.calulate_author_vector(combined_ti, combined_ab, scritti);
            //Modulo di stampa
            this.gui.stampa_testo(false, "Autore: " + id_author + "\n");
            this.gui.stampa_testo(true, "\nCOMBINED VECTOR TITLES:\n\n");
            this.gui.stampa_testo(true, "|  WORD\t|  PESO\n\n");
            Set<String> keys = combined_ti.keySet();
            for (String k : keys) {
                this.gui.stampa_testo(true, "| " + k + "\t| " + combined_ti.get(k) + "\n");
            }
            this.gui.stampa_testo(true, "\nCOMBINED VECTOR ABSTRACTS:\n\n");
            this.gui.stampa_testo(true, "|  WORD\t|  PESO\n\n");
            keys = combined_ab.keySet();
            for (String k : keys) {
                this.gui.stampa_testo(true, "| " + k + "\t| " + combined_ab.get(k) + "\n");
            }
            this.gui.stampa_testo(true, "\n\n");

        } else {
            this.gui.stampa_testo(false, "ATTENZIONE - NON E' STATA TROVATA ALCUNA \nPUBBLICAZIONE"
                    + "PER L'ID AUTORE INSERITO");
        }
    }
    ////////////////////////////////////////////////////////////////////////////////

    /////////////////////SEZIONE COMBINED AUTHOR VECTOR - PF / TF-IDF2 /////////////
    //funzione che genera un sottoinsieme del lessico di tutti i paper per
    //calcolare l'IDF delle keyword sui documenti dell'autore e dei suoi coautori
    //  type = true     - creo lessico dei titoli
    //  type = false    - creo lessico degli abastract
    private Lexicon create_coauthor_paper_lexicon_ti(Collection<Integer> written, boolean type) {
        Lexicon ti = new Lexicon();
        //considero tutti i documenti 
        for (Paper p : this.articoli) {
            //Se il paper e' stato scritto dall'autore considerato o dai sui coautori
            if (written.contains(new Integer(p.getCod_id()))) {
                //Salvo tutte le parole del titolo nel lessico;
                if (type) {
                    ti.addBow(p.getTitle_vector().keySet());
                } else {
                    ti.addBow(p.getAbstract_vector().keySet());
                }
            }
        }
        return ti;
    }

    private ArrayList<Integer> only_coauthor_papers(ArrayList<Paper> author,
            ArrayList<Integer> co_self) {
        //DIFFERENZA FRA I DUE ARRAY LIST
        ArrayList<Integer> co_paper = (ArrayList<Integer>) co_self.clone();
        for (Paper p : author) {
            co_paper.remove(new Integer(p.getCod_id()));
        }
        return co_paper;
    }

    private int paper_not_contain_key(String key, ArrayList<Integer> papers, boolean type) {

        int count = 0;
        for (Paper p : this.articoli) {
            if (papers.contains(new Integer(p.getCod_id()))) {
                if (type) {  //CONSIDERO IL VETTORE DEI TITOLI
                    if (!(p.getTitle_Vector().keySet().contains(key))) {
                        count++;
                    }
                } else {    //CONSIDERO IL VETTORE DEGLI ABSTRACT
                    if (!(p.getAbstract_Vector().keySet().contains(key))) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    //Funzione che calcola il combined author vector pesati con PF o Tf-IDF2
    private void calulate_PF_TFIDF2(int id_author,
            ArrayList<Paper> scritti,
            HashMap<String, Double> combined_ti,
            HashMap<String, Double> combined_ab,
            int tipo) {

        //Considero i paper scritti da tutti i suoi coautori per IDF
        ArrayList<Integer> paper_coauthors = this.connector.read_papers_for_coathor(id_author);
        double n_i = paper_coauthors.size();
        //Considero i paper scritti solo dai coautori dell'autore
        ArrayList<Integer> only_coauthors = this.only_coauthor_papers(scritti, paper_coauthors);
        double r_i = only_coauthors.size();

        //Creo i lessici per il calcolo degli IDF 
        Lexicon co_papers_ti = this.create_coauthor_paper_lexicon_ti(paper_coauthors, true);
        Lexicon co_papers_ab = this.create_coauthor_paper_lexicon_ti(paper_coauthors, false);

        for (Paper p : scritti) {

            //calcoliamo vettore dei titoli;
            Set<String> key = p.getTitle_Vector().keySet();
            for (String k : key) {
                double weight;
                double tf = p.getTitle_Vector().get(k).get(0);

                //Calcolo il peso in base al cosa e' stato selezionato
                if (tipo == 1) { //considero i TF-IDF2
                    double idf = Math.log((double) paper_coauthors.size() / (double) co_papers_ti.getTf_d(k));
                    double tf_idf2 = tf * idf;
                    weight = tf_idf2;
                } else { //considero i PF per i titoli
                    //r_ij = tutti i titoli dei paper dei soli coautori che non contegono la chiave;
                    double r_ij = (double) this.paper_not_contain_key(k, only_coauthors, true);
                    //n_ij = tutti i titoli dei paper dei coauthori piu' me che non contengono la chiave
                    double n_ij = (double) this.paper_not_contain_key(k, paper_coauthors, true);

                    //calcolo parti della formula del PF - VEDI SLIDE
                    double num_log = (r_ij / (r_i - r_ij + 1));
                    double den_log = ((n_ij - r_ij + 0.5) / (n_i - n_ij - r_i + r_ij + 1));
                    double log = Math.log(1 + num_log / den_log);
                    double abs = Math.abs((r_ij / r_i) - ((n_ij - r_ij) / (n_i - r_i)));
                    double u_ij = log * abs;

                    weight = u_ij;
                }

                //weight = Analizzatore.roundToSignificantFigures(weight, 6);
                //Aggiungo al comined vector dei titoli; 
                if (combined_ti.containsKey(k)) { //Se il combined vector contiene gia' la parola;
                    combined_ti.put(k, combined_ti.get(k).doubleValue() + weight);
                } else {                     //Altrimenti primo inserimento;
                    combined_ti.put(k, weight);
                }
            }

            //calcoliamo gli abstract
            key = p.getAbstract_Vector().keySet();
            for (String k : key) {
                double weight;
                double tf = p.getAbstract_Vector().get(k).get(0);

                //Calcolo il peso in base al cosa e' stato selezionato
                if (tipo == 1) { //considero i TF-IDF2
                    double idf = Math.log((double) paper_coauthors.size() / (double) co_papers_ab.getTf_d(k));
                    double max_idf = Math.log((double) paper_coauthors.size() / (double) co_papers_ab.getMax_tf());
                    double tf_idf2 = tf * (idf / max_idf);
                    weight = tf_idf2;
                } else { //Considero persi PF degli abstract
                    //r_ij = tutti gli abstract dei paper dei soli coautori che non contegono la chiave;
                    double r_ij = (double) this.paper_not_contain_key(k, only_coauthors, false);
                    //n_ij = tutti gli abstract paper dei coauthori piu' me che non contengono la chiave
                    double n_ij = (double) this.paper_not_contain_key(k, paper_coauthors, false);

                    //!!!! IMPORTANTE
                    //calcolo parti della formula del PF - VEDI SLIDE // DA RIVEDERE LE COSTANTI
                    double num_log = ((r_ij) / (r_i - r_ij + 1));
                    double den_log = ((n_ij - r_ij + 0.5) / (n_i - n_ij - r_i + r_ij + 1));

                    double log = Math.log(1 + num_log / den_log);
                    double abs = Math.abs((r_ij / r_i) - ((n_ij - r_ij) / (n_i - r_i)));
                    double u_ij = log * abs;
                    weight = u_ij;
                }

                //Aggiungo al comined vector dei titoli; 
                if (combined_ab.containsKey(k)) { //Se il combined vector contiene gia' la parola;
                    combined_ab.put(k, combined_ab.get(k).doubleValue() + weight);
                } else {                     //Altrimenti primo inserimento;
                    combined_ab.put(k, weight);
                }
            }
        }

        //NORMALIZZAZIONE Combined Key vector;
        Set<String> key = combined_ti.keySet();
        for (String k : key) {
            combined_ti.put(k, new Double((combined_ti.get(k).doubleValue()) / scritti.size()));
        }

        key = combined_ab.keySet();
        for (String k : key) {
            combined_ab.put(k, new Double((combined_ab.get(k).doubleValue()) / scritti.size()));
        }
    }

    public void print_PF_TFIDF2(int id_author, int tipo) {

        Set<Integer> written = this.connector.read_papers_for_author(id_author);
        if (written.size() > 0) {
            //Consdiero i paper scritti dall'autore inserito per il TF
            ArrayList<Paper> scritti = this.extract_written(written);

            //Combined Vector per i titoli e per gli abstract
            HashMap<String, Double> combined_ti;
            combined_ti = new HashMap<String, Double>();
            HashMap<String, Double> combined_ab;
            combined_ab = new HashMap<String, Double>();


            this.calulate_PF_TFIDF2(id_author, scritti, combined_ti, combined_ab, tipo);

            //MODULO DI STAMPA A VIDEO
            this.gui.stampa_testo(false, "Autore: " + id_author + "\n");
            this.gui.stampa_testo(true, "\nVECTOR COAUTHOR - TITLES:\n\n");
            this.gui.stampa_testo(true, "|  WORD\t|  PESO\n\n");
            Set<String> keys = combined_ti.keySet();
            for (String k : keys) {
                this.gui.stampa_testo(true, "| " + k + "\t| " + combined_ti.get(k) + "\n");
            }
            this.gui.stampa_testo(true, "\nVECTOR COAUTHORS - ABSTRACTS:\n\n");
            this.gui.stampa_testo(true, "|  WORD\t|  PESO\n\n");
            keys = combined_ab.keySet();
            for (String k : keys) {
                this.gui.stampa_testo(true, "| " + k + "\t| " + combined_ab.get(k) + "\n");
            }
            this.gui.stampa_testo(true, "\n\n");

        } else {
            this.gui.stampa_testo(false, "ATTENZIONE - NON E' STATA TROVATA ALCUNA \nPUBBLICAZIONE"
                    + "PER L'ID AUTORE INSERITO");
        }
    }
    ////////////////////////////////////////////////////////////////////////////////

    public double[][] svd_matlab(int id_author) {

        double[][] v = new double[0][0];
        Set<Integer> written = this.connector.read_papers_for_author(id_author);
        if (written.size() > 0) {
            //Consdiero i paper scritti dall'autore inserito per il TF
            ArrayList<Paper> scritti = this.extract_written(written);
            Lexicon fusion = new Lexicon();
            fusion.addBow(this.reader.getLessico_articoli().getLessico().keySet());
            fusion.addBow(this.reader.getLessico_titoli().getLessico().keySet());
            Set<String> key = fusion.getLessico().keySet();

            double[][] matrix = new double[scritti.size()][key.size()];
            int riga = 0;
            int colonna;
            for (Paper p : scritti) {
                HashMap<String, ArrayList<Double>> summary = p.getAbstract_Vector();
                HashMap<String, ArrayList<Double>> titoli = p.getTitle_Vector();
                this.calculate_tfidf_vector(titoli, summary);
                colonna = 0;
                for (String s : key) {
                    double value = 0;
                    if (summary.containsKey(s)) {
                        value += summary.get(s).get(1).doubleValue() * 0.7;
                    }
                    if (titoli.containsKey(s)) {
                        value += titoli.get(s).get(1).doubleValue() * 0.3;
                    }
                    matrix[riga][colonna] = value;
                    colonna++;
                }
                riga++;
            }

            String csv = matrixToCVS(matrix);
            try {

                //Salvo il file in in CSV per Matlab
                this.CSV_to_file(csv, "X");
                proxy.eval("svd1(" + 5 + ")");
                v = this.read_CSV_result("V", 5);

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return v;
    }

    public void pca_svd_matlab(int id_author) {

        Set<Integer> written = this.connector.read_papers_for_author(id_author);
        if (written.size() > 0) {
            //Consdiero i paper scritti dall'autore inserito per il TF
            ArrayList<Paper> scritti = this.extract_written(written);
            Lexicon fusion = new Lexicon();
            fusion.addBow(this.reader.getLessico_articoli().getLessico().keySet());
            fusion.addBow(this.reader.getLessico_titoli().getLessico().keySet());
            Set<String> key = fusion.getLessico().keySet();

            double[][] matrix = new double[scritti.size()][key.size()];
            int riga = 0;
            int colonna;
            for (Paper p : scritti) {
                HashMap<String, ArrayList<Double>> summary = p.getAbstract_Vector();
                HashMap<String, ArrayList<Double>> titoli = p.getTitle_Vector();
                this.calculate_tfidf_vector(titoli, summary);
                colonna = 0;
                for (String s : key) {
                    double value = 0;
                    if (summary.containsKey(s)) {
                        value += summary.get(s).get(1).doubleValue() * 0.8;
                    }
                    if (titoli.containsKey(s)) {
                        value += titoli.get(s).get(1).doubleValue() * 0.2;
                    }
                    matrix[riga][colonna] = value;
                    colonna++;
                }
                riga++;
            }

            String csv = matrixToCVS(matrix);
            try {
                //Salvo il file in in CSV per Matlab
                this.CSV_to_file(csv, "X_" + id_author);
                //proxy.eval("pca_svd("+ id_author + ")");

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    private void CSV_to_file(String cvs, String filename)
            throws FileNotFoundException, IOException {
        //Creo il buffer di scrittura sul File
        //MAC
        String path = "./data/";
        //WINDOWS
        //String path = ".\\data\\";
        //BufferedOutputStream output = new BufferedOutputStream(
        //        new FileOutputStream(new File(path + filename + ".csv")));
        //WINDOWS
        BufferedOutputStream output = new BufferedOutputStream(
                //      new FileOutputStream(new File("\\Users\\Alberto\\Documents\\MATLAB\\DBM\\"+filename+".csv")));
                new FileOutputStream(new File(path + filename + ".csv")));


        output.write(cvs.getBytes());
        output.flush();
        output.close();

    }

    //Legge il risultato dell'elaborazione dell SVD di MatLab
    private double[][] read_CSV_result(String filename, int k)
            throws FileNotFoundException, IOException {

        File f = new File(filename + ".csv");
        if (f.exists()) {
            //Creo il buffer di lettura dei file;
            //MAC
            //BufferedReader input = new BufferedReader(
            //        new InputStreamReader(new FileInputStream(f)));
            //WINDOWS
            BufferedReader input = new BufferedReader(
                    //        new InputStreamReader(new FileInputStream(new File("\\Users\\Alberto\\Documents\\MATLAB\\DBM\\"+filename+".csv"))));
                    new InputStreamReader(new FileInputStream(f)));


            String linea;
            linea = input.readLine();
            String[] colonne = linea.split(",");
            //creo la matrice
            double[][] mat = new double[k][colonne.length];
            int j = 0;
            for (String s : colonne) {
                mat[0][j] = Double.parseDouble(s);
                j++;
            }
            int i = 1;
            while ((linea = input.readLine()) != null && i < k) {
                //Splitto i valori numeri presenti nella stringa 
                colonne = linea.split(",");
                //Aggiungo tutti i valori della stringa all'array di riga
                j = 0;
                for (String s : colonne) {
                    mat[i][j] = Double.parseDouble(s);
                    j++;
                }
                i++;
            }

            input.close();
            return mat;
        } else {
            return new double[0][0];
        }
    }

    private String matrixToCVS(double[][] matrix) {
        String s = "";
        for (int i = 0; i < matrix.length; i++) {
            int j;
            for (j = 0; j < matrix[i].length - 1; j++) {
                s += matrix[i][j] + ", ";
            }
            s += matrix[i][j] + "\n";
        }
        return s;
    }

    //calcolo la similiratia' del coseno tra due vettore
    //length(v) == length(q)
    private double calculate_cosSim(double[] v, double[] q) {
        double num = 0.0, norm_v = 0.0, norm_q = 0.0;
        for (int i = 0; i < v.length; i++) {
            num += v[i] * q[i];
            norm_v += v[i] * v[i];
            norm_q += q[i] * q[i];
        }
        if (num == 0) {
            return num;
        } else {
            return (num) / (Math.sqrt(norm_v) * Math.sqrt(norm_q));
        }
    }

    //implementazione classe interna per la comparazione tra elementi MAP
    private class ValueComparator implements Comparator<Integer> {

        Map<Integer, Double> base;

        public ValueComparator(Map<Integer, Double> in) {
            this.base = in;
        }

        @Override
        public int compare(Integer t, Integer t1) {
            if (base.get(t) >= base.get(t1)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private double[] extract_word_vector(HashMap<String, Double> vector, boolean type) {

        int size;
        Set<String> keys;
        if (type) {
            size = this.reader.getLessico_articoli().getLessico().size();
            keys = this.reader.getLessico_articoli().getLessico().keySet();
        } else {
            size = this.reader.getLessico_titoli().getLessico().size();
            keys = this.reader.getLessico_titoli().getLessico().keySet();
        }

        double[] result = new double[size];
        int i = 0;
        for (String k : keys) {
            if (vector.containsKey(k)) {
                result[i] = vector.get(k).doubleValue();
            } else {
                result[i] = 0.0;
            }
            i++;
        }
        return result;
    }

    private double[] extract_word_vector2(HashMap<String, ArrayList<Double>> vector, boolean type) {
        int size;
        Set<String> keys;
        if (type) {
            size = this.reader.getLessico_articoli().getLessico().size();
            keys = this.reader.getLessico_articoli().getLessico().keySet();
        } else {
            size = this.reader.getLessico_titoli().getLessico().size();
            keys = this.reader.getLessico_titoli().getLessico().keySet();
        }

        double[] result = new double[size];
        int i = 0;
        for (String k : keys) {
            if (vector.containsKey(k)) {
                result[i] = vector.get(k).get(1).doubleValue();
            } else {
                result[i] = 0.0;
            }
            i++;
        }
        return result;
    }

    private double[][] keyvector_10similar(int id_author, Set<Integer> autori) {

        double[][] result = new double[10][2];
        Set<Integer> written = this.connector.read_papers_for_author(id_author);
        if (written.size() > 0) {
            ArrayList<Paper> scritti = this.extract_written(written);
            HashMap<String, Double> ti_a = new HashMap<String, Double>();
            HashMap<String, Double> ab_a = new HashMap<String, Double>();
            this.calulate_author_vector(ti_a, ab_a, scritti);

            //calcolo i vettori del titolo e degli abstract
            double[] v_ti = this.extract_word_vector(ti_a, false);
            double[] v_ab = this.extract_word_vector(ab_a, true);

            //Strutture per la similarita'
            HashMap<Integer, Double> similar = new HashMap<Integer, Double>();
            TreeMap<Integer, Double> order = new TreeMap<Integer, Double>(new ValueComparator(similar));

            HashMap<String, Double> ti_2;
            HashMap<String, Double> ab_2;

            double simTi;
            double simAb;
            for (Integer i : autori) {
                simTi = 0.0;
                simAb = 0.0;
                Set<Integer> written2 = this.connector.read_papers_for_author(i);
                if (written2.size() > 0) {
                    //Estraggo le componenti dell'autore da confrontare
                    ti_2 = new HashMap<String, Double>();
                    ab_2 = new HashMap<String, Double>();
                    this.calulate_author_vector(ti_2, ab_2,
                            this.extract_written(written2));

                    double[] v_ti2 = this.extract_word_vector(ti_2, false);
                    double[] v_ab2 = this.extract_word_vector(ab_2, true);
                    
                    //Calcolo la similarita' del Coseno fra i vettori
                    simTi = this.calculate_cosSim(v_ti, v_ti2);
                    simAb = this.calculate_cosSim(v_ab, v_ab2);

                }

                double avg = simTi * 0.2 + simAb * 0.8;
                similar.put(i, avg);
            }
            //estraggo i dieci autori con similarita' piu' alta;
            order.putAll(similar);
            Set<Integer> chiavi = order.keySet();
            int cont = 0;
            for (Integer k : chiavi) {
                if (cont < 10) {
                    result[cont][0] = k.doubleValue();
                    result[cont][1] = similar.get(k).doubleValue();
                    cont++;
                } else {
                    break;
                }
            }
        }
        return result;
    }

    private double[][] tfidf2_pf_10similar(int id_author, Set<Integer> autori, int tipo) {
        double[][] result = new double[10][2];
        Set<Integer> written = this.connector.read_papers_for_author(id_author);
        if (written.size() > 0) {

            ArrayList<Paper> scritti = this.extract_written(written);
            HashMap<String, Double> ti_a = new HashMap<String, Double>();
            HashMap<String, Double> ab_a = new HashMap<String, Double>();
            /*
            Lexicon fusion = new Lexicon();
            fusion.addBow(this.reader.getLessico_articoli().getLessico().keySet());
            fusion.addBow(this.reader.getLessico_titoli().getLessico().keySet());
            Set<String> keyLessico = fusion.getLessico().keySet();
            */
            this.calulate_PF_TFIDF2(id_author, scritti, ti_a, ab_a, tipo);

            //calcolo i vettori del titolo e degli abstract
            //contene tutte le parole presenti nei titoli e negli abstract
            double[] v_ti = this.extract_word_vector(ti_a, false);
            double[] v_ab = this.extract_word_vector(ab_a, true);


            //Strutture per la similarita'
            HashMap<Integer, Double> similar = new HashMap<Integer, Double>();
            TreeMap<Integer, Double> order = new TreeMap<Integer, Double>(new ValueComparator(similar));

            HashMap<String, Double> ti_2;
            HashMap<String, Double> ab_2;

            double simTi;
            double simAb;
            for (Integer i : autori) {
                simTi = 0.0;
                simAb = 0.0;
                Set<Integer> written2 = this.connector.read_papers_for_author(i);
                if (written2.size() > 0) {
                    //Estraggo le componenti dell'autore da confrontare
                    ti_2 = new HashMap<String, Double>();
                    ab_2 = new HashMap<String, Double>();
                    this.calulate_PF_TFIDF2(i, this.extract_written(written2), ti_2, ab_2, tipo);

                    double[] v_ti2 = this.extract_word_vector(ti_2, false);
                    double[] v_ab2 = this.extract_word_vector(ab_2, true);

                    //Calcolo la similarita' del Coseno fra i vettori
                    simTi = this.calculate_cosSim(v_ti, v_ti2);
                    simAb = this.calculate_cosSim(v_ab, v_ab2);

                }

                double avg = simTi * 0.2 + simAb * 0.8;
                similar.put(i, avg);
            }
            //estraggo i dieci autori con similarita' piu' alta;
            order.putAll(similar);
            Set<Integer> chiavi = order.keySet();
            int cont = 0;
            for (Integer k : chiavi) {
                if (cont < 10) {
                    result[cont][0] = k.doubleValue();
                    result[cont][1] = similar.get(k).doubleValue();
                    cont++;
                } else {
                    break;
                }
            }
        }
        return result;

    }

    //è come la print_PF_TFIDF2 ma qui non gestirò stampe e agglomererò subito il vettore dei titoli e degli abstract in uno
    //solo per poterli confrontare tra i vari autori
    //qui gli chiediamo esplicitamente dalla funzione richiamante se vogliamo il tf-idf2 o il pf
    public HashMap<String, Double> get_PF_TFIDF2(int id_author, int tipologiaRB) {
        double result = 0;
        //Combined Vector per i titoli e per gli abstract
        HashMap<String, Double> combined_ti;
        combined_ti = new HashMap<String, Double>();
        HashMap<String, Double> combined_ab;
        combined_ab = new HashMap<String, Double>();
        HashMap<String, String> allTitle = new HashMap<String, String>();
        HashMap<String, String> allAbstract = new HashMap<String, String>();
        Set<Integer> written = this.connector.read_papers_for_author(id_author);
        if (written.size() > 0) {
            //Consdiero i paper scritti dall'autore inserito per il TF
            ArrayList<Paper> scritti = this.extract_written(written);
            //Considero i paper scritti da tutti i suoi coautori per IDF
            ArrayList<Integer> paper_coauthors = this.connector.read_papers_for_coathor(id_author);
            double n_i = paper_coauthors.size();
            //Considero i paper scritti solo dai coautori dell'autore
            ArrayList<Integer> only_coauthors = this.only_coauthor_papers(scritti, paper_coauthors);
            double r_i = only_coauthors.size();

            //Creo i lessici per il calcolo degli IDF 
            Lexicon co_papers_ti = this.create_coauthor_paper_lexicon_ti(paper_coauthors, true);
            Lexicon co_papers_ab = this.create_coauthor_paper_lexicon_ti(paper_coauthors, false);
            for (Paper p : scritti) {
                //calcoliamo vettore dei titoli;
                Set<String> key = p.getTitle_Vector().keySet();
                //fondo al vettore dei titoli pre-esistenti tutti i valori del nuovo vettore appena trovato
                for (String k : key) {
                    //mi memorizzerà tutti i titoli di tutti i documenti dell'autore selezionato
                    allTitle.put(k, k);
                    double weight;
                    double tf = p.getTitle_Vector().get(k).get(0);

                    //Calcolo il peso in base al cosa e' stato selezionato
                    if (tipologiaRB == 1) { //considero i TF-IDF2
                        double idf = Math.log((double) paper_coauthors.size() / (double) co_papers_ti.getTf_d(k));
                        double tf_idf2 = tf * idf;
                        weight = tf_idf2;
                    } else { //considero i PF per i titoli
                        //r_ij = tutti i titoli dei paper dei soli coautori che non contegono la chiave;
                        double r_ij = (double) this.paper_not_contain_key(k, only_coauthors, true);
                        //n_ij = tutti i titoli dei paper dei coauthori piu' me che non contengono la chiave
                        double n_ij = (double) this.paper_not_contain_key(k, paper_coauthors, true);

                        //calcolo parti della formula del PF - VEDI SLIDE
                        double num_log = (r_ij / (r_i - r_ij + 1));
                        double den_log = ((n_ij - r_ij + 0.5) / (n_i - n_ij - r_i + r_ij + 1));
                        double log = Math.log(1 + num_log / den_log);
                        double abs = Math.abs((r_ij / r_i) - ((n_ij - r_ij) / (n_i - r_i)));
                        double u_ij = log * abs;
                        weight = u_ij;
                    }
                    //weight = Analizzatore.roundToSignificantFigures(weight, 6);
                    //Aggiungo al comined vector dei titoli; 
                    if (combined_ti.containsKey(k)) { //Se il combined vector contiene gia' la parola;
                        combined_ti.put(k, combined_ti.get(k).doubleValue() + weight);
                    } else {                     //Altrimenti primo inserimento;
                        combined_ti.put(k, weight);
                    }
                }

                //calcoliamo gli abstract
                key = p.getAbstract_Vector().keySet();
                //fondo al vettore degli abstract pre-esistenti tutti i valori del nuovo vettore appena trovato
                for (String k : key) {
                    //mi memorizzerà tutti i titoli di tutti i documenti dell'autore selezionato
                    allAbstract.put(k, k);
                    double weight;
                    double tf = p.getAbstract_Vector().get(k).get(0);

                    //Calcolo il peso in base al cosa e' stato selezionato
                    if (tipologiaRB == 1) { //considero i TF-IDF2
                        double idf = Math.log((double) paper_coauthors.size() / (double) co_papers_ab.getTf_d(k));
                        double max_idf = Math.log((double) paper_coauthors.size() / (double) co_papers_ab.getMax_tf());
                        double tf_idf2 = tf * (idf / max_idf);
                        weight = tf_idf2;
                    } else { //Considero persi PF degli abstract
                        //r_ij = tutti gli abstract dei paper dei soli coautori che non contegono la chiave;
                        double r_ij = (double) this.paper_not_contain_key(k, only_coauthors, false);
                        //n_ij = tutti gli abstract paper dei coauthori piu' me che non contengono la chiave
                        double n_ij = (double) this.paper_not_contain_key(k, paper_coauthors, false);

                        //!!!! IMPORTANTE
                        //calcolo parti della formula del PF - VEDI SLIDE // DA RIVEDERE LE COSTANTI
                        double num_log = ((r_ij) / (r_i - r_ij + 1));
                        double den_log = ((n_ij - r_ij + 0.5) / (n_i - n_ij - r_i + r_ij + 1));

                        double log = Math.log(1 + num_log / den_log);
                        double abs = Math.abs((r_ij / r_i) - ((n_ij - r_ij) / (n_i - r_i)));
                        double u_ij = log * abs;
                        weight = u_ij;
                    }
                    //Aggiungo al combined vector degli abstract; 
                    if (combined_ab.containsKey(k)) { //Se il combined vector contiene gia' la parola;
                        combined_ab.put(k, combined_ab.get(k).doubleValue() + weight);
                    } else {                     //Altrimenti primo inserimento;
                        combined_ab.put(k, weight);
                    }
                }
            }

            //NORMALIZZAZIONE Combined Key vector;
            Set<String> key = combined_ti.keySet();
            for (String k : key) {
                combined_ti.put(k, new Double((combined_ti.get(k).doubleValue()) / scritti.size()));
            }

            key = combined_ab.keySet();
            for (String k : key) {
                combined_ab.put(k, new Double((combined_ab.get(k).doubleValue()) / scritti.size()));
            }

            //unisco il vettore dei titoli e quello degli abstract in un solo vettore dando un peso maggiore ai titoli che agli abstract
            //ciclo sul vettore delle chiavi dei titoli e vedo se compaiono nella Hashmap del vettore degli abstract, se si gli aggiungo
            //il valore del tf-idf2/pf dei titoli con peso 0,7

            //ciclo sul keyset dei titoli e ne aggiungo ogni componente che trovo dalla hashmap dei titoli a quella 
            //degli abstract
            //mi memorizzo l'insieme di chiavi dei titoli e degli abstract per potervi eseguire i titoli
            Set<String> keyTitoli = allTitle.keySet();
            for (String s : keyTitoli) {
                if (!allAbstract.containsKey(s)) {//se la parola del titolo non compare 
                    allAbstract.put(s, s);
                }
            }
            //ottengo tutte le parole dei testi scritti dall'autore            
            Set<String> keyAbstract = allAbstract.keySet();
            for (String s : keyAbstract) {
                double value = 0;
                if (combined_ti.containsKey(s)) {
                    value += combined_ti.get(s).doubleValue() * 0.7;
                }
                if (combined_ab.containsKey(s)) {
                    value += combined_ab.get(s).doubleValue() * 0.3;
                }
                combined_ab.put(s, value);
            }
            //IN keyabstract ho tutte le parole contenute da combined_ab, che sono anche tutte le parole
            //dei testi dell'autore
        }
        return combined_ab;
    }

    public void calculate_SVD_PCA() {
        //Considero tutti gli altri autori
        HashMap<Integer, String> autori = this.connector.read_authors();
        //Considero i codoci autori ed eliminito l'autori di partenza
        Set<Integer> key = autori.keySet();

        for (Integer k : key) {
            //System.out.println(k);
            new Thread(new CalculateRunnable(k.intValue())).start();
        }

    }

    //Inserire il boolean tipo true per calcolare la PCA, e false per calcolare l'sdv
    private double[][] pca_svd_10similar(int id_author, Set<Integer> autori, boolean tipo) {
        //MAC
        String path = "./data/";
        //WINDOWS
        //String path=".\\data\\";

        if (tipo) {
            //MAC
            path += "PCA/W_";
            //WINDOWS
            //path+="PCA\\W_";
        } else {
            //MAC
            path += "SVD/V_";
            //WINDOWS
            //path+="SVD\\V_";
        }
        double[][] self_mat = new double[0][0];
        try {

            self_mat = this.read_CSV_result(path + id_author, 5);
            if (self_mat.length != 0) {
                HashMap<Integer, Double> mat_similar = new HashMap<Integer, Double>();
                ValueComparator bvc = new ValueComparator(mat_similar);
                //Creo una TreeMap Ordinata per
                TreeMap<Integer, Double> order = new TreeMap<Integer, Double>(bvc);
                for (Integer i : autori) {
                    double[][] temp_mat;
                    try {
                        temp_mat = this.read_CSV_result(path + i, 5);
                        if (temp_mat.length != 0) {
                            double sim = 0;
                            for (int j = 0; j < 5; j++) {
                                sim += this.calculate_cosSim(self_mat[j], temp_mat[j]);
                            }
                            sim = sim / self_mat.length;

                            mat_similar.put(i, new Double(sim));
                        }
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                order.putAll(mat_similar);
                Set<Integer> key = order.keySet();

                double[][] result = new double[10][2];
                int index = 0;
                for (Integer k : key) {
                    if (index < 10) {
                        result[index][0] = (double) k;
                        result[index][1] = mat_similar.get(k).doubleValue();
                    } else {
                        break;
                    }
                    index++;
                }

                return result;
            } else {
                this.gui.stampa_testo(true, "Errore: L'autore ricercato non ha pubblicato alcun articolo.");
                return self_mat;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }



        return self_mat;
    }

    public double[][] get10MostSimilarAuthor(int id_author, int tipoSim) {

        //Considero tutti gli altri autori
        HashMap<Integer, String> autori = this.connector.read_authors();
        //Considero i codoci autori ed eliminito l'autori di partenza
        Set<Integer> key = autori.keySet();
        String nome_authors = autori.get(new Integer(id_author));
        key.remove(new Integer(id_author));
        //matrice dei 10 autori piu' simili
        double[][] sim = new double[10][2];

        switch (tipoSim) {
            case 0:
                sim = this.keyvector_10similar(id_author, key);
                //restituisce i 10 autori più simili in ordine secondo il keyword vector
                this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base ai KW-vector:" + "\n\n");
                break;
            case 1:
                sim = this.tfidf2_pf_10similar(id_author, key, 1);
                //restituisce i 10 autori più simili in ordine secondo la tfidf2

                this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base a  TF-IDF2:" + "\n\n");
                break;
            case 2:
                sim = this.tfidf2_pf_10similar(id_author, key, 0);
                //restituisce i 10 autori più simili in ordine secondo la pf
                this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base a PF:" + "\n\n");
                break;

            case 3:
                sim = pca_svd_10similar(id_author, key, true);
                //restituisce i 10 autori più simili in ordine secondo la pca
                this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base a PCA:" + "\n\n");
                break;
            case 4:
                sim = pca_svd_10similar(id_author, key, false);
                //restituisce i 10 autori più simili in ordine secondo la svd
                this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base a SVD:" + "\n\n");
                break;
        }

        for (int i = 0; i < sim.length; i++) {
            this.gui.stampa_testo(true, "sim: " + Analizzatore.roundToSignificantFigures(sim[i][1], 4) + "\t" + "autore: " + (int) sim[i][0] + "   -   " + autori.get(new Integer((int) sim[i][0])) + "\n");
        }

        return sim;
    }

    private double[][] keyvector_relevant_paper(int id_author, ArrayList<Paper> wr, ArrayList<Paper> n_wr) {
        //Matrix da restituire
        double[][] sim = new double[n_wr.size()][2];
        //Calcolo il Combined Key-word vector dell'autore
        HashMap<String, Double> ti_a = new HashMap<String, Double>();
        HashMap<String, Double> ab_a = new HashMap<String, Double>();
        this.calulate_author_vector(ti_a, ab_a, wr);
        //Estraggo il vettore dei pesi dei vettori delle keyword
        double[] v_ti = this.extract_word_vector(ti_a, false);
        double[] v_ab = this.extract_word_vector(ab_a, true);

        //Strutture per la similarita'
        HashMap<Integer, Double> similar = new HashMap<Integer, Double>();
        TreeMap<Integer, Double> order = new TreeMap<Integer, Double>(new ValueComparator(similar));

        double simTi;
        double simAb;
        for (Paper p : n_wr) {
            //estraggo i vettori del paper comparare 
            double[] v_ti2 = this.extract_word_vector2(p.getTitle_Vector(), false);
            double[] v_ab2 = this.extract_word_vector2(p.getAbstract_Vector(), true);
            //Calcolo la similarita' del Coseno fra i vettori
            simTi = this.calculate_cosSim(v_ti, v_ti2);
            simAb = this.calculate_cosSim(v_ab, v_ab2);
            double avg = simTi * 0.2 + simAb * 0.8;
            similar.put(p.getCod_id(), avg);
        }

        //Ordino gli elementi in base alla similarita';
        order.putAll(similar);
        Set<Integer> keys = order.keySet();
        int cont = 0;
        for (Integer k : keys) {
            sim[cont][0] = k.doubleValue();
            sim[cont][1] = similar.get(k).doubleValue();
            cont++;
        }
        return sim;
    }
    
    private double[][] tfidf2_pf_relevant_paper(int id_author, ArrayList<Paper> wr, ArrayList<Paper> n_wr, int tipo) {
        
        //Matrix da restituire
        double[][] sim = new double[n_wr.size()][2];
        //Calcolo del TF-IDF
        HashMap<String, Double> ti_a = new HashMap<String, Double>();
        HashMap<String, Double> ab_a = new HashMap<String, Double>();
        this.calulate_PF_TFIDF2(id_author, wr, ti_a, ab_a, tipo);

        //calcolo i vettori del titolo e degli abstract
        //contene tutte le parole presenti nei titoli e negli abstract
        double[] v_ti = this.extract_word_vector(ti_a, false);
        double[] v_ab = this.extract_word_vector(ab_a, true);


        //Strutture per la similarita'
        HashMap<Integer, Double> similar = new HashMap<Integer, Double>();
        TreeMap<Integer, Double> order = new TreeMap<Integer, Double>(new ValueComparator(similar));

        double simTi;
        double simAb;
        
        for (Paper p : n_wr) {
            //estraggo i vettori del paper comparare 
            double[] v_ti2 = this.extract_word_vector2(p.getTitle_Vector(), false);
            double[] v_ab2 = this.extract_word_vector2(p.getAbstract_Vector(), true);
            //Calcolo la similarita' del Coseno fra i vettori
            simTi = this.calculate_cosSim(v_ti, v_ti2);
            simAb = this.calculate_cosSim(v_ab, v_ab2);
            double avg = simTi * 0.2 + simAb * 0.8;
            similar.put(p.getCod_id(), avg);
        }
        
        //Ordino gli elementi in base alla similarita';
        order.putAll(similar);
        Set<Integer> keys = order.keySet();
        int cont = 0;
        for (Integer k : keys) {
            sim[cont][0] = k.doubleValue();
            sim[cont][1] = similar.get(k).doubleValue();
            cont++;
        }
        
        return sim;
    }
    
    public void get10MostRelevantPapers(int id_author, int tipoSim) {

        //Considero tutti gli altri autori
        HashMap<Integer, String> autori = this.connector.read_authors();
        String nome_authors = autori.get(new Integer(id_author));

        Set<Integer> written = this.connector.read_papers_for_author(id_author);
        if (written.size() > 0) {
            //Array degli articoli non scritti dall'autore
            ArrayList<Paper> not_written = new ArrayList<Paper>();
            ArrayList<Paper> scritti = this.extract_written(written);
            for (Paper p : this.articoli) {
                if (!written.contains(new Integer(p.getCod_id()))) {
                    this.calculate_tfidf_vector(p.getTitle_Vector(), p.getAbstract_Vector());
                    not_written.add(p);
                }
            }

            double[][] sim = new double[not_written.size()][2];


            switch (tipoSim) {
                case 0:
                    sim = this.keyvector_relevant_paper(id_author, scritti, not_written);
                    //restituisce gli articoli non scritti dall'autore in ordine secondo il keyword vector
                    this.gui.stampa_testo(true, "Gli articoli piu` simili a " + id_author + " - " + nome_authors + "\nin base ai KW-vector:" + "\n\n");
                    break;
                case 1:
                    sim = this.tfidf2_pf_relevant_paper(id_author, scritti, not_written, 1);
                    //restituisce i 10 autori più simili in ordine secondo la tfidf2
                    this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base a  TF-IDF2:" + "\n\n");
                    break;
                case 2:
                    sim = this.tfidf2_pf_relevant_paper(id_author, scritti, not_written, 0);
                    //restituisce i 10 autori più simili in ordine secondo la pf
                    this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base a PF:" + "\n\n");
                    break;

                case 3:
                    //sim = pca_svd_10similar(id_author, key, true);
                    //restituisce i 10 autori più simili in ordine secondo la pca
                    this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base a PCA:" + "\n\n");
                    break;
                case 4:
                    //sim = pca_svd_10similar(id_author, key, false);
                    //restituisce i 10 autori più simili in ordine secondo la svd
                    this.gui.stampa_testo(true, "I 10 autori più simili a " + id_author + " - " + nome_authors + "\nin base a SVD:" + "\n\n");
                    break;
                default:
                    sim = new double[0][0];
            }
            
            for(int i = 0; i <sim.length; i++) {
                this.gui.stampa_testo(true, "sim: " + Analizzatore.roundToSignificantFigures(sim[i][1], 4) + "\t\t" + "Paper: " + (int) sim[i][0] + "\n");
            }

        } else {
            this.gui.stampa_testo(false, "L'autore " + id_author + "non ha scritto alcun articolo");
        }



    }

    private double[][] getAuthorAuthor_matrix() {

        HashMap<Integer, String> autori = this.connector.read_authors();
        Set<Integer> key = autori.keySet();
        Integer[] auth1 = new Integer[autori.size()];
        key.toArray(auth1);    //Righe matrice;
        Integer[] auth2 = (Integer[]) auth1.clone();    //Colonne Matrice;
        double[][] matrix = new double[auth1.length][auth2.length];

        for (int r = 0; r < auth1.length; r++) {
            Set<Integer> written = this.connector.read_papers_for_author(auth1[r].intValue());
            if (written.size() > 0) {
                //Estraggo le informazioni per l'autore considerato
                ArrayList<Paper> scritti = this.extract_written(written);
                HashMap<String, Double> ti_a = new HashMap<String, Double>();
                HashMap<String, Double> ab_a = new HashMap<String, Double>();
                //Calcolo il suo KW-Vector
                this.calulate_author_vector(ti_a, ab_a, scritti);
                double[] v_ti = this.extract_word_vector(ti_a, false);
                double[] v_ab = this.extract_word_vector(ab_a, true);

                for (int c = r; c < auth2.length; c++) {
                    if (r == c) {
                        matrix[r][c] = 1;
                    } else {

                        double simTi = 0.0;
                        double simAb = 0.0;
                        Set<Integer> written2 = this.connector.read_papers_for_author(auth2[c].intValue());
                        if (written2.size() > 0) {
                            //estraggo i vettori dell'altro autore da confrontare
                            ArrayList<Paper> scritti2 = this.extract_written(written2);
                            HashMap<String, Double> ti_2 = new HashMap<String, Double>();
                            HashMap<String, Double> ab_2 = new HashMap<String, Double>();
                            //Calcolo il suo KW-Vector
                            this.calulate_author_vector(ti_2, ab_2, scritti2);
                            double[] v_ti2 = this.extract_word_vector(ti_2, false);
                            double[] v_ab2 = this.extract_word_vector(ab_2, true);

                            //Calcolo la similarita' del Coseno fra i vettori
                            simTi = this.calculate_cosSim(v_ti, v_ti2);
                            simAb = this.calculate_cosSim(v_ab, v_ab2);

                            //inserisco il vbalore di similarita' nella matrice
                            //faccio due inserimenti perche la matrice e' simmetrica
                            double avg = simTi * 0.2 + simAb * 0.8;
                            matrix[r][c] = avg;
                            matrix[c][r] = avg;
                        } else {
                            matrix[r][c] = 0;
                        }
                    }
                }
            } else {
                //metto la riga a 0 - xke l'autore non ha scritto nulla
                for (int c = 0; c < auth2.length; c++) {
                    if (r == c) {
                        matrix[r][c] = 1;
                    } else {
                        matrix[r][c] = 0;
                    }
                }
            }
        }

        String csv = this.matrixToCVS(matrix);

        try {
            this.CSV_to_file(csv, "m_sim_aa");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return matrix;
    }

    public void get3SVD_authorAuthor() throws FileNotFoundException, IOException {
        //Pathname per WINDOWS
        //String filename = ".\\data\\m_sim_aa";
        //Pathname per MAC
        String filename = "./data/m_sim_aa";
        File f = new File(filename + ".csv");
        double[][] test;
        if (f.exists()) {
            //se la matrice già esiste leggo il file .csv che la contiene
            //test = this.read_CSV_result(filename, annoMax)
            int numAuthors = this.connector.get_num_authors();
            test = this.read_CSV_result(filename, numAuthors);
        } else {
            //Se la matrice di similarità autori-autori non esiste la creo e la salvo
            test = this.getAuthorAuthor_matrix();
        }

    }

    private double[][] getCoauthorCoauthor_matrix() {
        HashMap<Integer, String> coautori = this.connector.read_coauthors();
        Set<Integer> key = coautori.keySet();
        Integer[] coauth1 = new Integer[coautori.size()];
        key.toArray(coauth1);    //Righe matrice;
        Integer[] coauth2 = (Integer[]) coauth1.clone();    //Colonne Matrice;
        double[][] matrix = new double[coauth1.length][coauth2.length];

        for (int r = 0; r < coauth1.length; r++) {
            Set<Integer> written = this.connector.read_papers_for_author(coauth1[r].intValue());
            if (written.size() > 0) {
                //Estraggo le informazioni per l'autore considerato
                ArrayList<Paper> scritti = this.extract_written(written);
                HashMap<String, Double> ti_a = new HashMap<String, Double>();
                HashMap<String, Double> ab_a = new HashMap<String, Double>();
                //Calcolo il suo KW-Vector
                this.calulate_author_vector(ti_a, ab_a, scritti);
                double[] v_ti = this.extract_word_vector(ti_a, false);
                double[] v_ab = this.extract_word_vector(ab_a, true);

                for (int c = r; c < coauth2.length; c++) {
                    if (r == c) {
                        matrix[r][c] = 1;
                    } else {

                        double simTi = 0.0;
                        double simAb = 0.0;
                        Set<Integer> written2 = this.connector.read_papers_for_author(coauth2[c].intValue());
                        if (written2.size() > 0) {
                            //estraggo i vettori dell'altro autore da confrontare
                            ArrayList<Paper> scritti2 = this.extract_written(written2);
                            HashMap<String, Double> ti_2 = new HashMap<String, Double>();
                            HashMap<String, Double> ab_2 = new HashMap<String, Double>();
                            //Calcolo il suo KW-Vector
                            this.calulate_author_vector(ti_2, ab_2, scritti2);
                            double[] v_ti2 = this.extract_word_vector(ti_2, false);
                            double[] v_ab2 = this.extract_word_vector(ab_2, true);

                            //Calcolo la similarita' del Coseno fra i vettori
                            simTi = this.calculate_cosSim(v_ti, v_ti2);
                            simAb = this.calculate_cosSim(v_ab, v_ab2);

                            //inserisco il vbalore di similarita' nella matrice
                            //faccio due inserimenti perche la matrice e' simmetrica
                            double avg = simTi * 0.2 + simAb * 0.8;
                            matrix[r][c] = avg;
                            matrix[c][r] = avg;
                        } else {
                            matrix[r][c] = 0;
                        }
                    }
                }
            } else {
                //metto la riga a 0 - xke l'autore non ha scritto nulla
                for (int c = 0; c < coauth2.length; c++) {
                    if (r == c) {
                        matrix[r][c] = 1;
                    } else {
                        matrix[r][c] = 0;
                    }
                }
            }
        }

        String csv = this.matrixToCVS(matrix);

        try {
            this.CSV_to_file(csv, "m_sim_cc");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return matrix;
    }

    public void get3SVD_coauthorCoauthor() throws FileNotFoundException, IOException {
        //Pathname per WINDOWS
        //String filename = ".\\data\\m_sim_cc";
        //Pathname per MAC 
        String filename = "./data/m_sim_cc";
        File f = new File(filename + ".csv");
        double[][] test;
        if (f.exists()) {
            //se la matrice già esiste leggo il file .csv che la contiene
            //test = this.read_CSV_result(filename, annoMax)
            int numCoauthors = this.connector.get_num_coauthors();
            test = this.read_CSV_result(filename, numCoauthors);

        } else {
            //Se la matrice di similarità coautori-coautori non esiste la creo e la salvo
            test = this.getCoauthorCoauthor_matrix();
        }
    }

    private class CalculateRunnable implements Runnable {

        Integer id_author;

        public CalculateRunnable(Integer id_author) {
            this.id_author = id_author;
        }

        @Override
        public void run() {
            //Calcolo la pca per quell'autore
            pca_svd_matlab(id_author.intValue());
        }
    }
}
