/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appTodo;

import java.awt.SystemTray;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yobelchris
 */
public class todo extends javax.swing.JFrame {

    Connection con;
    String username ="";
    Check check = null;
    /**
     * Creates new form todo
     */
    public todo(String username) {
        initComponents();
        this.username = username;
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/todo","root","");
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error"+e,"Gagal",JOptionPane.ERROR_MESSAGE);
        }
        TableModelListener tb = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 1) {
                    TableModel model = (TableModel) e.getSource();
                    Boolean checked = (Boolean) model.getValueAt(row, column);
                    int stat = (checked) ? 1 : 0;
                    String sql = "UPDATE task SET status="+stat+" WHERE id="+model.getValueAt(row, 0);
                    try{
                        Statement st = con.createStatement();
                        int status = st.executeUpdate(sql);
                        if(status<1){
                            JOptionPane.showMessageDialog(null, "Gagal Mengupdate","Gagal",JOptionPane.ERROR_MESSAGE);
                        }
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(null, "Error : "+ex,"Gagal",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            
        };
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.addTableModelListener(tb);
        select();
        check = new Check();
        check.setDaemon(true);
        check.start();
    }
    
    public class Check extends Thread{

        @Override
        public void run() {
            while(!this.isInterrupted()){
                try{
                    String sql = "SELECT * FROM task WHERE username = '"+username+"'";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){
                        LocalDate dead = rs.getDate(5).toLocalDate();
                        LocalDate now = LocalDate.now();
                        
                        long days = ChronoUnit.DAYS.between(now, dead);
                        if(Math.toIntExact(days)==1 || Math.toIntExact(days)==0){
                            if (SystemTray.isSupported()) {
                                 SystemTray tray = SystemTray.getSystemTray();

                                //If the icon is a file
                                Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
                                //Alternative (if the icon is on the classpath):
                                //Image image = Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));
                                TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
                                //Let the system resizes the image if needed
                                trayIcon.setImageAutoSize(true);
                                //Set tooltip text for the tray icon
                                trayIcon.setToolTip("System tray icon demo");
                                tray.add(trayIcon);
                                trayIcon.displayMessage("Attention!!!", "You got task "+rs.getString(4)+" on "+rs.getDate(5).toString(), MessageType.INFO);
                            } else {
                                System.err.println("System tray not supported!");
                            }
                        }
                    }
                    Thread.sleep(1800000);
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                    this.interrupt();
                } catch (Exception ex) {
                    Logger.getLogger(todo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        taskText = new javax.swing.JTextField();
        deadline = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null,  new Boolean(false), null, null},
                {null,  new Boolean(false), null, null},
                {null,  new Boolean(false), null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "", "Keterangan", "Deadline"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 120, 550, 320));
        getContentPane().add(taskText, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 210, 30));

        deadline.setDateFormatString("yyyy-dd-MM");
        getContentPane().add(deadline, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 150, 30));

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        setBounds(0, 0, 570, 491);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try{
            if(!taskText.getText().isEmpty()){
                java.sql.Date dead = new java.sql.Date(deadline.getDate().getTime());
                String sql = "INSERT INTO task (username,status,keterangan,deadline) VALUES ("
                    + "'yobelchris',"
                    + 0 +",'"
                    + taskText.getText()+"','"
                    + dead + "')";
                Statement st = con.createStatement();
                int status = st.executeUpdate(sql);
                if(status==1){
                    JOptionPane.showMessageDialog(null, "Task berhasil ditambahkan!");
                }else{
                    JOptionPane.showMessageDialog(null, "Gagal Menambahkan","Gagal",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null, "Tidak boleh ada yang kosong","Gagal",JOptionPane.ERROR_MESSAGE);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error : "+e,"Gagal",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }catch(NullPointerException ex){
            JOptionPane.showMessageDialog(null, "Tidak boleh ada yang kosong","Gagal",JOptionPane.ERROR_MESSAGE);
        }
        select();
    }//GEN-LAST:event_jButton1ActionPerformed

    public void select(){
        try{
            String sql = "SELECT * FROM task WHERE username = '"+username+"'";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            for (int i = model.getRowCount()-1; i >= 0; i--) {
                model.removeRow(i);
            }
            while(rs.next()){
                int id = rs.getInt("id");
                boolean stat = false;
                if(rs.getBoolean(3)){
                    stat = true;
                }
                String ket = rs.getString(4);
                String deadline = rs.getDate(5).toString();
                Object[] rowData = {id,stat,ket,deadline};
                model.addRow(rowData);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(todo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(todo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(todo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(todo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
      
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser deadline;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField taskText;
    // End of variables declaration//GEN-END:variables
}
