package Db_Connection;

import java.util.HashMap;
import java.util.Set;
import entity.*;
import java.util.ArrayList;

/**
 * @author Alessandro Basile
 * @version 1.0
 *
 * Interfaccia per implementare le Transizioni con il DB
 */
public interface TransazioniDB {
    //Metodo che legge le stopword da DB e ne restiuisce un SET 
    //per la fase successiva di differenza insiemistica con la BOW

    public Set<String> read_stopWords();

    //Metodo che legge il lessico dal DB e ne restiuisce una
    //hashMap<chiave, valore> dove rispettivamente:
    //  chiave = parola del lessico;
    //  valore = numero di documenti della collezione che la contengo
    //  
    //  lex_title = lessico ricavato dall'analisi del titolo;
    //  lex_abstr = lessico ricavato dall'analisi dell abstract;
    public boolean read_lexicon(HashMap<String, Integer> lex_title, HashMap<String, Integer> lex_abstr);

    //Metodo che effettua una scrittura nel DB del lexicon con le parole trovate
    //nella collezione esaminata e le rispettive occorrenze nei testi;
    // @param HashMap<chiave, valore> (vedi sopra)
    public void write_lexicon(HashMap<String, Integer> lex_title, HashMap<String, Integer> lex_abstr);

    //FUNZIONI PER LA LETTURA DAL DB DEGLI ABSTRACT DEI PAPERS
    public ArrayList<Paper> read_papers();

    //Funzioni per leggere e scrivere i vettori precedentamente analizati
    public void write_Vectors(ArrayList<Paper> articoli);

    public void read_Vectors(ArrayList<Paper> articoli);

    //Controlla se nel db è già stata eseguita l'analisi degli abstract
    public boolean dbpieno();
    //Informa se l'id del paper cercato è presente nella tabella index_vector

    public boolean idPaperPresenteIndexVector(String id);

    //Metodi per la lettura degli autori
    public Set<Integer> read_papers_for_author(int id_author);

    //Metodo per la lettura dei paper dei coautori di un dato autore
    public ArrayList<Integer> read_papers_for_coathor(int id_author);

    //Metodo per la lettura degli Autori dal DB
    public HashMap<Integer, String> read_authors();
    
    //Metodo per la lettura dei Coautori dal DB
    public HashMap<Integer, String> read_coauthors();
    
     //Metodo per sapere quanti autori ci sono nel db
    public int get_num_authors();
    
     //Metodo per sapere quanti coautori ci sono nel db
    public int get_num_coauthors();
}
