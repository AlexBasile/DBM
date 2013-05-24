package TextAnalizer;

import entity.Lexicon;
import entity.Paper;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 *
 * @author Alessandro Basile
 * @version 1.0
 */
public class Analizzatore {

    //Imposto gli strumenti per l'analisi dei Papers
    private ArrayList<Paper> articoli;
    private EnglishAnalyzer reader;
    private Lexicon lessico_articoli;
    private Lexicon lessico_titoli;

    public Analizzatore(Set<String> stop_word, ArrayList<Paper> papers) {

        //inizializzo le variabili per l'analisi
        this.articoli = papers;
        this.lessico_articoli = new Lexicon();
        this.lessico_titoli = new Lexicon();
        this.reader = new EnglishAnalyzer(Version.LUCENE_30, stop_word);

    }

    public ArrayList<Paper> getAnalizedPaper() {
        return this.articoli;
    }

    //Normalizza i pesi TF in base alla cardinalita' del vettore 
    private void normalizeTf(HashMap<String, Double> vector, int n_word) {
        //Considero tutte le parole del vettore
        Set<String> bow = vector.keySet();
        //Considero il numero di parole presenti
        Double card_bow = new Double(n_word);

        for (String key : bow) {
            //normalizzo il tf con la cardinalita' della bow
            Double tf_norm = vector.get(key) / card_bow;

            //salvo il peso normalizzato
            vector.put(key, new Double(roundToSignificantFigures(tf_norm, 6)));
        }
    }

    //normalizza il tf con Salton e Buckley - usato solo per il titolo
    private void normalizeTf_SB(HashMap<String, Double> vector, int n_word) {

        //Considero tutte le parole del vettore
        Set<String> bow = vector.keySet();

        //Considero il numero di parole presenti
        Double card_bow = new Double(n_word);

        double tf_max = 0;

        //Calcolo il tf-Max per il titolo
        for (String word : bow) {
            double tf_t = vector.get(word).doubleValue();
            if (tf_t > tf_max) {
                tf_max = tf_t;
            }
        }

        for (String word : bow) {
            //Considero il numero di occorenze nel testo
            double tf_t = vector.get(word).doubleValue();
            //normalizzo la frequenza nel testo
            double tf_n = tf_t / card_bow.doubleValue();
            //normalizzo con Salton e Buckley
            double tf_sb = (0.5 + 0.5 * (tf_n / tf_max));

            //salvo il peso normalizzato
            vector.put(word, new Double(roundToSignificantFigures(tf_sb, 6)));
        }
    }

    //tokenizza l'abstract del paper
    private void tokenize_abstract(Paper articolo) {
        //Imposto la sTringa da tokenizzare
        TokenStream tokenizer = reader.tokenStream("abstract",
                new StringReader(articolo.getAbstract()));

        //Imposto il lettore dei termini del tokenizzatore
        CharTermAttribute term = tokenizer.addAttribute(CharTermAttribute.class);

        //Struttura per memorizzare temporaneamente i token;
        HashMap<String, Double> vector = new HashMap<String, Double>();

        int n_word = 0;

        try {
            while (tokenizer.incrementToken()) {
                //considero la parola tokenizzata
                String word = term.toString();
                n_word++;

                //Se il dizionario contiene gia' la parola considerata
                if (vector.containsKey(word)) {
                    //sovrascrivo la chiave con il nuovo valore di occorrenza
                    vector.put(word, new Double(vector.get(word) + 1));
                } else {
                    //altrimenti inserisco la parola nel dizionario con tf 
                    vector.put(word, new Double(1));
                    //aggiungo la nuova parola al lessico
                    //Per successivo calcolo dell'IDF
                    this.getLessico_articoli().addWord(word);
                }
            }

            //normalizzo i pesi tf dei termini del vettore
            this.normalizeTf(vector, n_word);


            Set<String> bow = vector.keySet();

            //Aggingo tutte i token con tf normalizzato al paper;
            for (String key : bow) {
                articolo.addSummaryToken(key, vector.get(key));
            }

        } catch (Exception e) {
            System.err.println("Tokenizer fail for paper: " + articolo.getCod_id());
        }
    }

    //Come normalizzazione abstract
    private void tokenize_title(Paper articolo) {

        try {

            //Imposto la sTringa da tokenizzare
            TokenStream tokenizer = reader.tokenStream("title",
                    new StringReader(articolo.getTitle()));

            //Imposto il lettore dei termini del tokenizzatore
            CharTermAttribute term = tokenizer.addAttribute(CharTermAttribute.class);

            //Struttura per memorizzare temporaneamente i token;
            HashMap<String, Double> vector = new HashMap<String, Double>();

            int n_word = 0;

            while (tokenizer.incrementToken()) {
                //considero la parola tokenizzata
                String word = term.toString();
                n_word++;

                //Se il dizionario contiene gia' la parola considerata
                if (vector.containsKey(word)) {
                    //sovrascrivo la chiave con il nuovo valore di occorrenza
                    vector.put(word, new Double(vector.get(word) + 1));
                } else {
                    //altrimenti inserisco la parola nel dizionario con tf 
                    vector.put(word, new Double(1));
                    //aggiungo la nuova parola al lessico
                    //Per successivo calcolo dell'IDF
                    this.getLessico_titoli().addWord(word);
                }
            }

            //normalizzo i pesi tf dei termini del vettore
            this.normalizeTf_SB(vector, n_word);

            Set<String> bow = vector.keySet();

            //Aggingo tutte i token con tf normalizzato al paper;
            for (String key : bow) {
                articolo.addTitleToken(key, vector.get(key));
            }

        } catch (Exception e) {
            System.err.println("Tokenizer TITTLE fail for paper: " + articolo.getCod_id());
            System.err.println(e.toString());

        }
    }

    //Effettua l'analisi dei documenti presenti in articoli
    public void start_analizing() {

        for (Paper p : articoli) {
            //Tokenizzo il titolo dell'articolo
            this.tokenize_title(p);

            if (p.getAbstract() != null) //Tokenizzo l'abstract dell'articolo
            {
                this.tokenize_abstract(p);
            }
        }

    }

    /**
     * @return the lessico_articoli
     */
    public Lexicon getLessico_articoli() {
        return lessico_articoli;
    }

    /**
     * @param lessico_articoli the lessico_articoli to set
     */
    public void setLessico_articoli(Lexicon lessico_articoli) {
        this.lessico_articoli = lessico_articoli;
    }

    /**
     * @return the lessico_titoli
     */
    public Lexicon getLessico_titoli() {
        return lessico_titoli;
    }

    /**
     * @param lessico_titoli the lessico_titoli to set
     */
    public void setLessico_titoli(Lexicon lessico_titoli) {
        this.lessico_titoli = lessico_titoli;
    }

    public static double roundToSignificantFigures(double num, int n) {
        if (num == 0) {
            return 0;
        }

        final double d = Math.ceil(Math.log10(num < 0 ? -num : num));
        final int power = n - (int) d;

        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }
}
