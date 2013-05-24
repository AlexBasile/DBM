/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import TextAnalizer.Controller;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tobiagiani
 */
public class Finestra extends javax.swing.JFrame {

    private boolean details;
    private Controller controller;
    //serve per la prima funzione per discriminare se voglio che sia mostrato il tf o il tf-idf
    //0 sta per tf e 1 per tf-idf
    private int tipologiaTF = 0;
    private int tipologia2TF = 0;
    private int tipologia3TF = 0;
    private int tipologiaRBF2T1b = 0;
    private int tipologiaRBF2T1c = 0;

    /**
     * Creates new form Finestra
     */
    public Finestra() {
        initComponents();
        check_details.setSelected(false);
        details = false;
        buttonGroupTipoTF.add(optionbuttonTFIDF);
        buttonGroupTipoTF.add(optionbuttonTF);
        tipologiaTF = 0;
        buttonGroupTask2.add(radioButtonFunc2TF);
        buttonGroupTask2.add(radioButtonFunc2IDF);
        tipologia2TF = 0;
        buttonGroupTask3.add(radiobutton3TF);
        buttonGroupTask3.add(radiobutton3IDF);
        tipologia3TF = 0;
        buttonGroupFase2Task1b.add(jRBKWF2T1b);
        buttonGroupFase2Task1b.add(jRBTFIDF2F2T1b);
        buttonGroupFase2Task1b.add(jRBPFF2T1b);
        buttonGroupFase2Task1b.add(jRBPCAF2T1b);
        buttonGroupFase2Task1b.add(jRBSVDF2T1b);
        //il valore 0 di tipologiaRBF2T1b indica i keyword-vector
        //il valore 1 di tipologiaRBF2T1b indica il TF-IDF2
        //il valore 2 di tipologiaRBF2T1b indica il PF
        //il valore 3 di tipologiaRBF2T1b indica il PCA
        //il valore 4 di tipologiaRBF2T1b indica l' SVD
        tipologiaRBF2T1b = 0;
        buttonGroupFase2Task1c.add(jRBKWF2T1c);
        buttonGroupFase2Task1c.add(jRBTFIDF2F2T1c);
        buttonGroupFase2Task1c.add(jRBPFF2T1c);
        buttonGroupFase2Task1c.add(jRBPCAF2T1c);
        buttonGroupFase2Task1c.add(jRBSVDF2T1c);

        //il valore 0 di tipologiaRBF2T1c indica i keyword-vector
        //il valore 1 di tipologiaRBF2T1c indica il TF-IDF
        //il valore 2 di tipologiaRBF2T1c indica il TF-IDF2
        //il valore 3 di tipologiaRBF2T1c indica il PF
        //il valore 4 di tipologiaRBF2T1c indica il PCA
        //il valore 4 di tipologiaRBF2T1c indica l' SVD
        tipologiaRBF2T1c = 0;
    }

    public void set_DB_Status(boolean status) {
        if (status) {
            jLabel1.setText("Database Analizzato");
        } else {
            jLabel1.setText("Database da Analizzare");
        }
    }

    public boolean get_details() {
        return this.details;
    }

    public int get_tipologiaTF() {
        return this.tipologiaTF;
    }

    public int get_tipologia2TF() {
        return this.tipologia2TF;
    }

    public int get_tipologia3TF() {
        return this.tipologia3TF;
    }

    public int get_tipologiaF2T1b() {
        return this.tipologiaRBF2T1b;
    }

    public int get_tipologiaF2T1c() {
        return this.tipologiaRBF2T1c;
    }

    public void set_Controller(Controller main) {
        this.controller = main;
    }

    public void stampa_testo(boolean append, String msg) {
        if (append) {
            jTextArea1.append(msg);
        } else {
            jTextArea1.setText(msg);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupTipoTF = new javax.swing.ButtonGroup();
        buttonGroupTask2 = new javax.swing.ButtonGroup();
        buttonGroupTask3 = new javax.swing.ButtonGroup();
        buttonGroupFase2Task1b = new javax.swing.ButtonGroup();
        buttonGroupFase2Task1c = new javax.swing.ButtonGroup();
        jLabel7 = new javax.swing.JLabel();
        Pannello = new javax.swing.JPanel();
        Bottone_Analisi = new javax.swing.JButton();
        Campo_task1 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cerca_task1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        bottone_svuota_console = new javax.swing.JButton();
        check_details = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        optionbuttonTF = new javax.swing.JRadioButton();
        optionbuttonTFIDF = new javax.swing.JRadioButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        Campo_task2 = new javax.swing.JTextField();
        radioButtonFunc2TF = new javax.swing.JRadioButton();
        radioButtonFunc2IDF = new javax.swing.JRadioButton();
        Cerca_task2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        Campo_task3 = new javax.swing.JTextField();
        radiobutton3TF = new javax.swing.JRadioButton();
        radiobutton3IDF = new javax.swing.JRadioButton();
        Cerca_task3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        CampoF2Task1b = new javax.swing.JTextField();
        jRBKWF2T1b = new javax.swing.JRadioButton();
        jRBTFIDF2F2T1b = new javax.swing.JRadioButton();
        jRBPFF2T1b = new javax.swing.JRadioButton();
        jRBPCAF2T1b = new javax.swing.JRadioButton();
        jRBSVDF2T1b = new javax.swing.JRadioButton();
        jButtonFase2Task1b = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        CampoF2Task1c = new javax.swing.JTextField();
        jRBKWF2T1c = new javax.swing.JRadioButton();
        jRBTFIDF2F2T1c = new javax.swing.JRadioButton();
        jRBPFF2T1c = new javax.swing.JRadioButton();
        jRBPCAF2T1c = new javax.swing.JRadioButton();
        jRBSVDF2T1c = new javax.swing.JRadioButton();
        jButtonFase2Task1c = new javax.swing.JButton();
        Matrice_AA = new javax.swing.JButton();
        Button_Matrix_CC = new javax.swing.JButton();

        jLabel7.setText("jLabel7");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Pannello.setLayout(null);

        Bottone_Analisi.setText("Analizza");
        Bottone_Analisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Bottone_AnalisiActionPerformed(evt);
            }
        });
        Pannello.add(Bottone_Analisi);
        Bottone_Analisi.setBounds(10, 10, 97, 29);

        Campo_task1.setText("338360");
        Campo_task1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Campo_task1ActionPerformed(evt);
            }
        });
        Campo_task1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Campo_task1KeyPressed(evt);
            }
        });
        Pannello.add(Campo_task1);
        Campo_task1.setBounds(30, 100, 85, 28);
        Pannello.add(jSeparator1);
        jSeparator1.setBounds(9, 47, 370, 12);

        cerca_task1.setText("Cerca");
        cerca_task1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerca_task1ActionPerformed(evt);
            }
        });
        Pannello.add(cerca_task1);
        cerca_task1.setBounds(310, 100, 79, 29);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        Pannello.add(jScrollPane1);
        jScrollPane1.setBounds(394, 47, 470, 650);

        bottone_svuota_console.setText("clear");
        bottone_svuota_console.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bottone_svuota_consoleActionPerformed(evt);
            }
        });
        Pannello.add(bottone_svuota_console);
        bottone_svuota_console.setBounds(800, 10, 70, 29);

        check_details.setText("Mostra dettagli");
        check_details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_detailsActionPerformed(evt);
            }
        });
        Pannello.add(check_details);
        check_details.setBounds(670, 10, 127, 23);

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(1, 1, 1));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Pannello.add(jLabel1);
        jLabel1.setBounds(110, 10, 253, 29);

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("ID Paper - Paper Vector");
        Pannello.add(jLabel2);
        jLabel2.setBounds(10, 70, 380, 21);

        optionbuttonTF.setSelected(true);
        optionbuttonTF.setText("TF");
        optionbuttonTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionbuttonTFActionPerformed(evt);
            }
        });
        Pannello.add(optionbuttonTF);
        optionbuttonTF.setBounds(130, 100, 47, 23);

        optionbuttonTFIDF.setText("TF-IDF");
        optionbuttonTFIDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionbuttonTFIDFActionPerformed(evt);
            }
        });
        Pannello.add(optionbuttonTFIDF);
        optionbuttonTFIDF.setBounds(190, 100, 76, 23);
        Pannello.add(jSeparator2);
        jSeparator2.setBounds(10, 310, 360, 10);

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("ID Author - Combined Author Vector");
        Pannello.add(jLabel4);
        jLabel4.setBounds(10, 150, 380, 21);

        Campo_task2.setText("1632506");
        Campo_task2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Campo_task2ActionPerformed(evt);
            }
        });
        Campo_task2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Campo_task2KeyPressed(evt);
            }
        });
        Pannello.add(Campo_task2);
        Campo_task2.setBounds(30, 180, 85, 28);

        radioButtonFunc2TF.setSelected(true);
        radioButtonFunc2TF.setText("TF");
        radioButtonFunc2TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonFunc2TFActionPerformed(evt);
            }
        });
        Pannello.add(radioButtonFunc2TF);
        radioButtonFunc2TF.setBounds(130, 180, 47, 23);

        radioButtonFunc2IDF.setText("TF-IDF");
        radioButtonFunc2IDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonFunc2IDFActionPerformed(evt);
            }
        });
        Pannello.add(radioButtonFunc2IDF);
        radioButtonFunc2IDF.setBounds(190, 180, 76, 23);

        Cerca_task2.setText("Cerca");
        Cerca_task2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cerca_task2ActionPerformed(evt);
            }
        });
        Pannello.add(Cerca_task2);
        Cerca_task2.setBounds(310, 180, 79, 29);

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("ID Author - relevance paper other authors");
        Pannello.add(jLabel3);
        jLabel3.setBounds(-10, 440, 400, 26);

        Campo_task3.setText("1632506");
        Pannello.add(Campo_task3);
        Campo_task3.setBounds(30, 260, 85, 28);

        radiobutton3TF.setSelected(true);
        radiobutton3TF.setText("PF");
        radiobutton3TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobutton3TFActionPerformed(evt);
            }
        });
        Pannello.add(radiobutton3TF);
        radiobutton3TF.setBounds(130, 260, 46, 23);

        radiobutton3IDF.setText("TF-IDF2");
        radiobutton3IDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobutton3IDFActionPerformed(evt);
            }
        });
        Pannello.add(radiobutton3IDF);
        radiobutton3IDF.setBounds(190, 260, 84, 23);

        Cerca_task3.setText("Cerca");
        Cerca_task3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cerca_task3ActionPerformed(evt);
            }
        });
        Pannello.add(Cerca_task3);
        Cerca_task3.setBounds(310, 260, 79, 29);

        jButton1.setText("Genera CSV");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        Pannello.add(jButton1);
        jButton1.setBounds(400, 10, 110, 29);

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("ID Author - Combined Author-Coauthor ");
        Pannello.add(jLabel5);
        jLabel5.setBounds(10, 230, 380, 26);

        CampoF2Task1b.setText("1632506");
        Pannello.add(CampoF2Task1b);
        CampoF2Task1b.setBounds(30, 350, 80, 28);

        jRBKWF2T1b.setSelected(true);
        jRBKWF2T1b.setText("KW-Vector");
        jRBKWF2T1b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBKWF2T1bActionPerformed(evt);
            }
        });
        Pannello.add(jRBKWF2T1b);
        jRBKWF2T1b.setBounds(130, 350, 99, 23);

        jRBTFIDF2F2T1b.setText("TF-IDF2");
        jRBTFIDF2F2T1b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBTFIDF2F2T1bActionPerformed(evt);
            }
        });
        Pannello.add(jRBTFIDF2F2T1b);
        jRBTFIDF2F2T1b.setBounds(130, 380, 84, 23);

        jRBPFF2T1b.setText("PF");
        jRBPFF2T1b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBPFF2T1bActionPerformed(evt);
            }
        });
        Pannello.add(jRBPFF2T1b);
        jRBPFF2T1b.setBounds(220, 380, 46, 23);

        jRBPCAF2T1b.setText("PCA");
        jRBPCAF2T1b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBPCAF2T1bActionPerformed(evt);
            }
        });
        Pannello.add(jRBPCAF2T1b);
        jRBPCAF2T1b.setBounds(130, 410, 57, 23);

        jRBSVDF2T1b.setText("SVD");
        jRBSVDF2T1b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSVDF2T1bActionPerformed(evt);
            }
        });
        Pannello.add(jRBSVDF2T1b);
        jRBSVDF2T1b.setBounds(220, 410, 57, 23);

        jButtonFase2Task1b.setText("Cerca");
        jButtonFase2Task1b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFase2Task1bActionPerformed(evt);
            }
        });
        Pannello.add(jButtonFase2Task1b);
        jButtonFase2Task1b.setBounds(310, 350, 79, 29);

        jLabel6.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("ID Author - 10 most similar Author");
        Pannello.add(jLabel6);
        jLabel6.setBounds(-10, 320, 400, 26);

        CampoF2Task1c.setText("1632506");
        Pannello.add(CampoF2Task1c);
        CampoF2Task1c.setBounds(30, 470, 80, 28);

        jRBKWF2T1c.setSelected(true);
        jRBKWF2T1c.setText("KW-Vector");
        jRBKWF2T1c.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBKWF2T1cActionPerformed(evt);
            }
        });
        Pannello.add(jRBKWF2T1c);
        jRBKWF2T1c.setBounds(120, 470, 99, 23);

        jRBTFIDF2F2T1c.setText("TF-IDF2");
        jRBTFIDF2F2T1c.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBTFIDF2F2T1cActionPerformed(evt);
            }
        });
        Pannello.add(jRBTFIDF2F2T1c);
        jRBTFIDF2F2T1c.setBounds(120, 500, 84, 23);

        jRBPFF2T1c.setText("PF");
        jRBPFF2T1c.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBPFF2T1cActionPerformed(evt);
            }
        });
        Pannello.add(jRBPFF2T1c);
        jRBPFF2T1c.setBounds(220, 500, 123, 23);

        jRBPCAF2T1c.setText("PCA");
        jRBPCAF2T1c.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBPCAF2T1cActionPerformed(evt);
            }
        });
        Pannello.add(jRBPCAF2T1c);
        jRBPCAF2T1c.setBounds(120, 530, 57, 23);

        jRBSVDF2T1c.setText("SVD");
        jRBSVDF2T1c.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSVDF2T1cActionPerformed(evt);
            }
        });
        Pannello.add(jRBSVDF2T1c);
        jRBSVDF2T1c.setBounds(220, 530, 57, 23);

        jButtonFase2Task1c.setText("Cerca");
        jButtonFase2Task1c.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFase2Task1cActionPerformed(evt);
            }
        });
        Pannello.add(jButtonFase2Task1c);
        jButtonFase2Task1c.setBounds(310, 470, 79, 29);

        Matrice_AA.setText("Matrix_AA");
        Matrice_AA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Matrice_AAActionPerformed(evt);
            }
        });
        Pannello.add(Matrice_AA);
        Matrice_AA.setBounds(30, 610, 109, 29);

        Button_Matrix_CC.setText("Matrix_CC");
        Button_Matrix_CC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Matrix_CCActionPerformed(evt);
            }
        });
        Pannello.add(Button_Matrix_CC);
        Button_Matrix_CC.setBounds(140, 610, 109, 29);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, Pannello, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, Pannello, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        controller.calculate_SVD_PCA();
        this.stampa_testo(true, "SVD e PCA Calcolate e Salvate");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void Cerca_task3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cerca_task3ActionPerformed
        if (Campo_task3.getText().compareTo("") == 0) {
            this.stampa_testo(true, "Errore: nessun id di author inserito\n");
        } else {
            if (!this.controller.DB_Analizzato()) {
                this.stampa_testo(true, "Errore: Il database non è stato analizzato.\n");
            } else {
                this.controller.print_PF_TFIDF2(Integer.parseInt(Campo_task3.getText()), tipologia3TF);
            }
        }
        this.stampa_testo(true, "\n\n\n");
        this.repaint();
    }//GEN-LAST:event_Cerca_task3ActionPerformed

    private void radiobutton3IDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobutton3IDFActionPerformed
        tipologia3TF = 1;
    }//GEN-LAST:event_radiobutton3IDFActionPerformed

    private void radiobutton3TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobutton3TFActionPerformed
        tipologia3TF = 0;
    }//GEN-LAST:event_radiobutton3TFActionPerformed

    private void Cerca_task2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cerca_task2ActionPerformed
        if (Campo_task2.getText().compareTo("") == 0) {
            this.stampa_testo(true, "Errore: nessun id di author inserito\n");
        } else {
            if (!this.controller.DB_Analizzato()) {
                this.stampa_testo(true, "Errore: Il database non è stato analizzato.\n");
            } else {
                this.controller.print_author_vector(Integer.parseInt(Campo_task2.getText()));
            }
        }
        this.stampa_testo(true, "\n\n\n");
        this.repaint();
    }//GEN-LAST:event_Cerca_task2ActionPerformed

    private void radioButtonFunc2IDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonFunc2IDFActionPerformed
        tipologia2TF = 1;
    }//GEN-LAST:event_radioButtonFunc2IDFActionPerformed

    private void radioButtonFunc2TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonFunc2TFActionPerformed
        tipologia2TF = 0;
    }//GEN-LAST:event_radioButtonFunc2TFActionPerformed

    private void Campo_task2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Campo_task2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Cerca_task2ActionPerformed(null);
        }
    }//GEN-LAST:event_Campo_task2KeyPressed

    private void Campo_task2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Campo_task2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Campo_task2ActionPerformed

    private void optionbuttonTFIDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionbuttonTFIDFActionPerformed
        tipologiaTF = 1;
    }//GEN-LAST:event_optionbuttonTFIDFActionPerformed

    private void optionbuttonTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionbuttonTFActionPerformed
        tipologiaTF = 0;
    }//GEN-LAST:event_optionbuttonTFActionPerformed

    private void check_detailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_detailsActionPerformed
        details = check_details.isSelected();
    }//GEN-LAST:event_check_detailsActionPerformed

    private void bottone_svuota_consoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bottone_svuota_consoleActionPerformed
        jTextArea1.setText("");
    }//GEN-LAST:event_bottone_svuota_consoleActionPerformed

    private void cerca_task1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerca_task1ActionPerformed

        if (Campo_task1.getText().compareTo("") == 0) {
            this.stampa_testo(true, "Errore: nessun id di paper inserito\n");
        } else {
            if (!this.controller.DB_Analizzato()) {
                this.stampa_testo(true, "Errore: Il database non è stato analizzato.\n");
            } else {
                this.controller.print_tf_vector(Integer.parseInt(Campo_task1.getText()));
            }
        }
        this.stampa_testo(true, "\n\n\n");
        this.repaint();
    }//GEN-LAST:event_cerca_task1ActionPerformed

    private void Campo_task1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Campo_task1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cerca_task1ActionPerformed(null);
        }
    }//GEN-LAST:event_Campo_task1KeyPressed

    private void Campo_task1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Campo_task1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Campo_task1ActionPerformed

    private void Bottone_AnalisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Bottone_AnalisiActionPerformed
        this.controller.analisi_DB();
        this.repaint();
    }//GEN-LAST:event_Bottone_AnalisiActionPerformed

    private void jButtonFase2Task1bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFase2Task1bActionPerformed
        if (CampoF2Task1b.getText().compareTo("") == 0) {
            this.stampa_testo(true, "Errore: nessun id di author inserito\n");
        } else {
            if (!this.controller.DB_Analizzato()) {
                this.stampa_testo(true, "Errore: Il database non è stato analizzato.\n");
            } else {
                this.controller.get10MostSimilarAuthor(Integer.parseInt(CampoF2Task1b.getText()), tipologiaRBF2T1b);
            }
        }
        this.stampa_testo(true, "\n\n\n");
        this.repaint();
    }//GEN-LAST:event_jButtonFase2Task1bActionPerformed

    private void jRBTFIDF2F2T1bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBTFIDF2F2T1bActionPerformed
        tipologiaRBF2T1b = 1;
    }//GEN-LAST:event_jRBTFIDF2F2T1bActionPerformed

    private void jRBPFF2T1bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBPFF2T1bActionPerformed
        tipologiaRBF2T1b = 2;
    }//GEN-LAST:event_jRBPFF2T1bActionPerformed

    private void jRBPCAF2T1bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBPCAF2T1bActionPerformed
        tipologiaRBF2T1b = 3;
    }//GEN-LAST:event_jRBPCAF2T1bActionPerformed

    private void jRBSVDF2T1bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSVDF2T1bActionPerformed
        tipologiaRBF2T1b = 4;
    }//GEN-LAST:event_jRBSVDF2T1bActionPerformed

    private void jRBKWF2T1bActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBKWF2T1bActionPerformed
        tipologiaRBF2T1b = 0;
    }//GEN-LAST:event_jRBKWF2T1bActionPerformed

    private void jButtonFase2Task1cActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFase2Task1cActionPerformed
        if (CampoF2Task1c.getText().compareTo("") == 0) {
            this.stampa_testo(true, "Errore: nessun id di author inserito\n");
        } else {
            if (!this.controller.DB_Analizzato()) {
                this.stampa_testo(true, "Errore: Il database non è stato analizzato.\n");
            } else {
                this.controller.get10MostRelevantPapers(Integer.parseInt(CampoF2Task1c.getText()), tipologiaRBF2T1c);
            }
        }
        this.stampa_testo(true, "\n\n\n");
        this.repaint();
    }//GEN-LAST:event_jButtonFase2Task1cActionPerformed

    private void jRBKWF2T1cActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBKWF2T1cActionPerformed
        tipologiaRBF2T1c = 0;
    }//GEN-LAST:event_jRBKWF2T1cActionPerformed

    private void jRBTFIDF2F2T1cActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBTFIDF2F2T1cActionPerformed
        tipologiaRBF2T1c = 1;
    }//GEN-LAST:event_jRBTFIDF2F2T1cActionPerformed

    private void jRBPFF2T1cActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBPFF2T1cActionPerformed
        tipologiaRBF2T1c = 2;
    }//GEN-LAST:event_jRBPFF2T1cActionPerformed

    private void jRBPCAF2T1cActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBPCAF2T1cActionPerformed
        tipologiaRBF2T1c = 3;
    }//GEN-LAST:event_jRBPCAF2T1cActionPerformed

    private void jRBSVDF2T1cActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSVDF2T1cActionPerformed
        tipologiaRBF2T1c = 4;
    }//GEN-LAST:event_jRBSVDF2T1cActionPerformed

    private void Matrice_AAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Matrice_AAActionPerformed
        try {
            this.controller.get3SVD_authorAuthor();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Matrice_AAActionPerformed

    private void Button_Matrix_CCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Matrix_CCActionPerformed
        try {
            this.controller.get3SVD_coauthorCoauthor();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Finestra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Button_Matrix_CCActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Bottone_Analisi;
    private javax.swing.JButton Button_Matrix_CC;
    private javax.swing.JTextField CampoF2Task1b;
    private javax.swing.JTextField CampoF2Task1c;
    private javax.swing.JTextField Campo_task1;
    private javax.swing.JTextField Campo_task2;
    private javax.swing.JTextField Campo_task3;
    private javax.swing.JButton Cerca_task2;
    private javax.swing.JButton Cerca_task3;
    private javax.swing.JButton Matrice_AA;
    private javax.swing.JPanel Pannello;
    private javax.swing.JButton bottone_svuota_console;
    private javax.swing.ButtonGroup buttonGroupFase2Task1b;
    private javax.swing.ButtonGroup buttonGroupFase2Task1c;
    private javax.swing.ButtonGroup buttonGroupTask2;
    private javax.swing.ButtonGroup buttonGroupTask3;
    private javax.swing.ButtonGroup buttonGroupTipoTF;
    private javax.swing.JButton cerca_task1;
    private javax.swing.JCheckBox check_details;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonFase2Task1b;
    private javax.swing.JButton jButtonFase2Task1c;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton jRBKWF2T1b;
    private javax.swing.JRadioButton jRBKWF2T1c;
    private javax.swing.JRadioButton jRBPCAF2T1b;
    private javax.swing.JRadioButton jRBPCAF2T1c;
    private javax.swing.JRadioButton jRBPFF2T1b;
    private javax.swing.JRadioButton jRBPFF2T1c;
    private javax.swing.JRadioButton jRBSVDF2T1b;
    private javax.swing.JRadioButton jRBSVDF2T1c;
    private javax.swing.JRadioButton jRBTFIDF2F2T1b;
    private javax.swing.JRadioButton jRBTFIDF2F2T1c;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JRadioButton optionbuttonTF;
    private javax.swing.JRadioButton optionbuttonTFIDF;
    private javax.swing.JRadioButton radioButtonFunc2IDF;
    private javax.swing.JRadioButton radioButtonFunc2TF;
    private javax.swing.JRadioButton radiobutton3IDF;
    private javax.swing.JRadioButton radiobutton3TF;
    // End of variables declaration//GEN-END:variables
}