import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainFrame extends javax.swing.JDialog {


    private JPanel contentPane;
    private JButton buttonScan;
    private JButton buttonCancel;
    private JEditorPane editorPane1;
    RFIDScanner rfid;
    Purse mainPurse;


    public MainFrame() {
        mainPurse= new Purse("Lukas", new ArrayList<MoneyPiece>());
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonScan);
        setPreferredSize(new Dimension(450, 300));

        buttonScan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        rfid.scan();
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MainFrame dialog = new MainFrame();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void addTransaction(String trans) {
        try {
            Document paneDoc = editorPane1.getDocument();
            paneDoc.insertString(paneDoc.getLength(), trans, null);
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
    }
}
