package Db_Connection;

import entity.Paper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alessandro Basile
 * @version 1.0
 *
 * Classe che implementa le operazioni sul DB
 */
public class DB_Stream implements TransazioniDB {

    private Set<String> sWord;
    private HashMap<String, Integer> lexicon;

    public DB_Stream() {
        //Se la connessione non esiste la creo
        if (!DBAccess.checkConnection()) {
            DBAccess.initConnection();
        }
    }

    @Override
    public Set<String> read_stopWords() {

        //Inizializzo l'insieme delle stopWord uso un HashSet in modo da avere 
        //tutti metodi dell'interfaccia set gia' implementati;
        this.sWord = new HashSet<String>();
        final String query = "SELECT * FROM stopword";

        //Preparo l'esecuzione della query e la lettura del risultato
        Statement stmt;
        ResultSet result;
        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);

            //Leggo il resultSet e memorizzo le parole nel SET delle stop_Word
            while (result.next()) {
                String word = result.getString("word");
                sWord.add(word);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sWord;
    }

    @Override
    public boolean read_lexicon(HashMap<String, Integer> lex_title, HashMap<String, Integer> lex_abstr) {

        final String query = "SELECT * FROM lexicon";

        //Preparo l'esecuzione della query e la lettura del risultato
        Statement stmt;
        ResultSet result;
        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);

            //Leggo il resultSet e memorizzo nell'Hash_Map del lessico
            while (result.next()) {
                String word = result.getString("word");
                Integer tf_title = new Integer(result.getInt("tf_title"));
                Integer tf_abstr = new Integer(result.getInt("tf_abstract"));

                //costruisco i lessici in base alle occorrenze dei termini
                if (tf_title.intValue() != 0) {
                    lex_title.put(word, tf_title);
                }
                if (tf_abstr.intValue() != 0) {
                    lex_abstr.put(word, tf_abstr);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
        //se almeno uno dei due lessici e' vuoto bisogna rianalizzare la collezione di documenti
        return (lex_title.isEmpty() || lex_abstr.isEmpty());
    }

    @Override
    public void write_lexicon(HashMap<String, Integer> lex_title, HashMap<String, Integer> lex_abstr) {

        //creo la query di cancellazione degli elementi gia' presenti;
        final String delete = "DELETE FROM lexicon WHERE TRUE";
        //creo la stringa delle parole da inserire
        String update = "INSERT INTO lexicon (word, tf_title, tf_abstract) VALUES ";

        //Preparo l'esecuzione delle query
        Statement stmt;
        ResultSet result;
        try {
            stmt = DBAccess.getConnection().createStatement();
            //eseguo query cancellazione;
            stmt.executeUpdate(delete);

            //Considero le parole chiavi presenti nei due dizionari
            Set<String> key_abstr = lex_abstr.keySet();
            Set<String> key_title = lex_title.keySet();

            Integer tf_abstr;
            Integer tf_title;


            //ciclo sulle chiavi del dizionario di abstract
            for (String key : key_abstr) {
                //considero l'occorrenza della parola negli abstract
                tf_abstr = lex_abstr.get(key);

                //controllo che la medesima parola faccia parte del lessico dei titoli
                if (lex_title.containsKey(key)) {
                    //Se si imposto la frequenza
                    tf_title = lex_title.get(key);
                    //cancello la parola dal set di chiavi per non considerarla di nuovo
                    key_title.remove(key);
                } //altrimenti la imposto con 0 occorrenze
                else {
                    tf_title = new Integer(0);
                }

                //Aggiungo i valori da inserire nella query
                String values = "(\"" + key + "\", " + tf_title.intValue() + ", " + tf_abstr.intValue() + "),";
                update += values;
            }

            //ciclo su gli eventuali elementi rimanenti nel lessico del testo
            for (String key : key_title) {
                //come sopra
                tf_abstr = new Integer(0);
                tf_title = lex_title.get(key);
                String values = "(\"" + key + "\", " + tf_title.intValue() + ", " + tf_abstr.intValue() + "),";
                update += values;
            }

            //Server per rimuovere l'ultima ',' della stringa di update;
            String query = update.substring(0, update.length() - 1);
            //System.err.println(query);
            //eseguo l'update sul DB
            stmt.executeUpdate(query);

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Paper> read_papers() {

        //Struttura per memorizzare i papaer;
        ArrayList<Paper> papers = new ArrayList<Paper>();
        final String query = "SELECT paperid, title, year, abstract from papers";

        //Preparo l'esecuzione della query e la lettura del risultato
        Statement stmt;
        ResultSet result;

        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);

            //Leggo il resultSet e memorizzo nell'Hash_Map del lessico
            while (result.next()) {
                //estraggo le info dal resultset
                int id = result.getInt("paperid");
                String titolo = result.getString("title");
                String year = result.getString("year");
                String abstr = result.getString("abstract");
                //inserisco il paper nella lista di papaer;
                papers.add(new Paper(id, titolo, year, abstr));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }

        return papers;
    }

    private void write_article_vector(int id, HashMap<String, ArrayList<Double>> titolo,
            HashMap<String, ArrayList<Double>> abstr) {

        Statement stmt;
        ResultSet result;
        String query = "INSERT INTO index_vector (id_paper, word, tf_title, tf_abstract) VALUES ";

        try {
            stmt = DBAccess.getConnection().createStatement();
            Set<String> key_ti = titolo.keySet();
            Set<String> key_ab = abstr.keySet();
            double tf_t;
            double tf_a;

            for (String key : key_ab) {
                tf_a = abstr.get(key).get(0).doubleValue();

                if (key_ti.contains(key)) {
                    tf_t = titolo.get(key).get(0).doubleValue();
                    key_ti.remove(key);
                } else {
                    tf_t = 0.0;
                }

                String value = "(" + id + "," + "\"" + key + "\"," + tf_t + "," + tf_a + "),";
                query += value;
            }

            for (String key : key_ti) {
                tf_a = 0.0;
                tf_t = titolo.get(key).get(0).doubleValue();
                String value = "(" + id + "," + "\"" + key + "\"," + tf_t + "," + tf_a + "),";
                query += value;
            }

            String update = query.substring(0, query.length() - 1);

            //eseguo l'update nel DB
            stmt.executeUpdate(update);

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void write_Vectors(ArrayList<Paper> articoli) {

        //Cancello quello che c'era prima nella tabella dei vettori degli articoli
        try {
            //Preparo l'esecuzione della query e la lettura del risultato
            Statement stmt;
            ResultSet result;
            String query = "DELETE FROM index_vector WHERE true";
            stmt = DBAccess.getConnection().createStatement();
            //eseguo query cancellazione;
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Per ogni articolo presente nella lista salvo i rispettivi vettori nel DB
        for (Paper p : articoli) {
            HashMap<String, ArrayList<Double>> titolo = p.getTitle_Vector();
            HashMap<String, ArrayList<Double>> abstr = p.getAbstract_Vector();
            this.write_article_vector(p.getCod_id(), titolo, abstr);
        }
    }

    @Override
    public void read_Vectors(ArrayList<Paper> articoli) {
        try {
            Statement stmt;
            ResultSet result;
            String temp;
            double temp1;
            double temp2;
            stmt = DBAccess.getConnection().createStatement();
            String query;
            //per ogni articolo ne prendo l'identificatore e recupero i dati ralativi ai tf del titolo
            //e dell'abstract per ogni parola presente sul database
            for (Paper p : articoli) {
                query = "SELECT * FROM index_vector WHERE id_paper=" + p.getCod_id();
                result = stmt.executeQuery(query);
                while (result.next()) {
                    temp = result.getString("word");
                    temp1 = result.getDouble("tf_title");
                    temp2 = result.getDouble("tf_abstract");
                    if (temp1 != 0) {
                        p.setElementTitle_Vector(temp, temp1);
                    }
                    if (temp2 != 0) {
                        p.setElementAbstract_Vector(temp, temp2);
                    }
                    //NB in MYSQL in Lexicon e in Index_vector al posto di usare int per i tf_title e tf_abstract
                    //conviene usare delle stringhe e convertire in double la stringhe prima di usarle su java}
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*controlla se il database è già stato riempito con dati analizzati e mi restituisce un mex in merito*/
    @Override
    public boolean dbpieno() {
        boolean risp = false;
        try {
            Statement stmt;
            ResultSet result;
            stmt = DBAccess.getConnection().createStatement();

            String query = "SELECT * FROM index_vector WHERE true";
            result = stmt.executeQuery(query);
            if (result.next()) {
                risp = true;
            } else {
                risp = false;
            }

            query = "SELECT * FROM lexicon WHERE true";
            result = stmt.executeQuery(query);
            if (result.next()) {
                risp = true;
            } else {
                risp = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return risp;
    }

    @Override
    public boolean idPaperPresenteIndexVector(String id) {
        try {
            Statement stmt;
            ResultSet result;
            stmt = DBAccess.getConnection().createStatement();
            String query = "SELECT id_paper FROM index_vector WHERE id_paper ='" + id + "'";
            result = stmt.executeQuery(query);
            if (result.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Set<Integer> read_papers_for_author(int id_author) {

        final String query = "SELECT paperid FROM writtenby WHERE personid = " + id_author;
        Set<Integer> papers_written = new HashSet<Integer>();
        //Preparo l'esecuzione della query e la lettura del risultato
        Statement stmt;
        ResultSet result;
        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);

            //Leggo il resultSet e memorizzo
            while (result.next()) {
                Integer id_p = new Integer(result.getInt("paperid"));
                papers_written.add(id_p);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }

        return papers_written;
    }

    @Override
    public ArrayList<Integer> read_papers_for_coathor(int id_author) {

        final String query = "SELECT DISTINCT paperid FROM writtenby "
                + "JOIN coauthors on (personid = personid2) "
                + "WHERE personid1 = " + id_author;

        ArrayList<Integer> articoli = new ArrayList<Integer>();
        //Preparo l'esecuzione della query e la lettura del risultato
        Statement stmt;
        ResultSet result;
        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);

            //Leggo il resultSet e memorizzo 
            while (result.next()) {
                Integer id_p = new Integer(result.getInt("paperid"));
                articoli.add(id_p);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }

        return articoli;
    }

    @Override
    public HashMap<Integer, String> read_authors() {

        final String query = "SELECT * from authors";
        HashMap<Integer, String> autori = new HashMap<Integer, String>();
        //Preparo per l'esecuzione della query
        Statement stmt;
        ResultSet result;
        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);

            //Leggo il resultSet e memorizzo 
            while (result.next()) {
                Integer id_p = new Integer(result.getInt("personid"));
                String nome = result.getString("name");
                autori.put(id_p, nome);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return autori;
    }
    
    @Override
    public HashMap<Integer, String> read_coauthors() {
        final String query = "SELECT DISTINCT personid2,name FROM ( coauthors JOIN authors ON personid2 =personid ) ";
        HashMap<Integer, String> coautori = new HashMap<Integer, String>();
        //Preparo per l'esecuzione della query
        Statement stmt;
        ResultSet result;
        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);

            //Leggo il resultSet e memorizzo 
            while (result.next()) {
                Integer id_p = new Integer(result.getInt("personid2"));
                String nome = result.getString("name");
                coautori.put(id_p, nome);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return coautori;
    }
    
    @Override
    public int get_num_coauthors() {
        final String query = "SELECT  DISTINCT personid2 FROM  coauthors ";
        int risultato = 0;
        //Preparo per l'esecuzione della query
        Statement stmt;
        ResultSet result;

        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);
            if(result.next()){
                result.last();
                risultato = result.getRow();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return risultato;
    }
    
     @Override
    public int get_num_authors() {
        final String query = "SELECT  DISTINCT personid FROM  authors ";
        int risultato = 0;
        //Preparo per l'esecuzione della query
        Statement stmt;
        ResultSet result;

        try {
            stmt = DBAccess.getConnection().createStatement();
            result = stmt.executeQuery(query);
            if(result.next()){
                result.last();
                risultato = result.getRow();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB_Stream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return risultato;
    }
    
}
