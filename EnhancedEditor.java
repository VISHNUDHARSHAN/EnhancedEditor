import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.io.*;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;


public class EnhancedEditor extends Frame {
    String filename;
    TextArea tx;
    Clipboard clip = getToolkit().getSystemClipboard();

    EnhancedEditor() {
        setLayout(new GridLayout(1, 1));
        tx = new TextArea();
        add(tx);
        setTitle("untitled");
        MenuBar mb = new MenuBar();
        Menu F = new Menu("File");
        MenuItem n = new MenuItem("New");
        MenuItem o = new MenuItem("Open");
        MenuItem s = new MenuItem("Save");
        MenuItem e = new MenuItem("Exit");
        n.addActionListener(new New());
        F.add(n);
        o.addActionListener(new Open());
        F.add(o);
        s.addActionListener(new Save());
        F.add(s);
        e.addActionListener(new Exit());
        F.add(e);
        mb.add(F);
        Menu E = new Menu("Edit");
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        MenuItem find = new MenuItem("Find"); 
        MenuItem replace = new MenuItem("Replace"); 
        cut.addActionListener(new Cut());
        E.add(cut);
        copy.addActionListener(new Copy());
        E.add(copy);
        paste.addActionListener(new Paste());
        E.add(paste);
        find.addActionListener(new Find()); // Added Find action
        E.add(find);
        replace.addActionListener(new Replace()); // Added Replace action
        E.add(replace);
        mb.add(E);
        Menu formatMenu = new Menu("Format"); 
        MenuItem textColorMenuItem = new MenuItem("Text Color");
        textColorMenuItem.addActionListener(new TextColor()); 
        formatMenu.add(textColorMenuItem);
        MenuItem bgColorMenuItem = new MenuItem("Background Color");
        bgColorMenuItem.addActionListener(new BackgroundColor()); 
        formatMenu.add(bgColorMenuItem);
        mb.add(formatMenu);
        setMenuBar(mb);

        mylistener mylist = new mylistener();
        addWindowListener(mylist);
    }

    class mylistener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
            dispose();
        }
    }

    class New implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            filename = "untitled";
            tx.setText("");
            setTitle(filename);
        }
    }

    class Open implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            FileDialog fd = new FileDialog(EnhancedEditor.this, "Select File", FileDialog.LOAD);
            fd.show();
            if (fd.getFile() != null) {
                filename = fd.getDirectory() + fd.getFile();
                setTitle(filename);
                ReadFile();
            }
            tx.requestFocus();
        }
    }

    class Save implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            FileDialog fd = new FileDialog(EnhancedEditor.this, "Save File", FileDialog.SAVE);
            fd.show();
            if (fd.getFile() != null) {
                filename = fd.getDirectory() + fd.getFile();
                setTitle(filename);
                try {
                    DataOutputStream d = new DataOutputStream(new FileOutputStream(filename));
                    String line = tx.getText();
                    BufferedReader br = new BufferedReader(new StringReader(line));
                    while ((line = br.readLine()) != null) {
                        d.writeBytes(line + "\r\n");
                    }
                    d.close();
                } catch (Exception ex) {
                    System.out.println("File not found");
                }
                tx.requestFocus();
            }
        }
    }

    class Exit implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    void ReadFile() {
        BufferedReader d;
        StringBuffer sb = new StringBuffer();
        try {
            d = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = d.readLine()) != null)
                sb.append(line + "\n");
            tx.setText(sb.toString());
            d.close();
        } catch (FileNotFoundException fe) {
            System.out.println("File not found");
        } catch (IOException ioe) {
        }
    }

    class Cut implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String sel = tx.getSelectedText();
            StringSelection ss = new StringSelection(sel);
            clip.setContents(ss, ss);
            tx.replaceRange("", tx.getSelectionStart(), tx.getSelectionEnd());
        }
    }

    class Copy implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String sel = tx.getSelectedText();
            StringSelection clipString = new StringSelection(sel);
            clip.setContents(clipString, clipString);
        }
    }

    class Paste implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Transferable cliptran = clip.getContents(EnhancedEditor.this);
            try {
                String sel = (String) cliptran.getTransferData(DataFlavor.stringFlavor);
                tx.replaceRange(sel, tx.getSelectionStart(), tx.getSelectionEnd());
            } catch (Exception exc) {
                System.out.println("Not String flavor");
            }
        }
    }

    class Find implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchText = JOptionPane.showInputDialog("Enter text to find:");
            if (searchText != null && !searchText.isEmpty()) {
                String content = tx.getText();
                int index = content.indexOf(searchText);
                if (index != -1) {
                    tx.select(index, index + searchText.length());
                } else {
                    JOptionPane.showMessageDialog(EnhancedEditor.this, "Text not found");
                }
            }
        }
    }

    class Replace implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchText = JOptionPane.showInputDialog("Enter text to find:");
            String replaceText = JOptionPane.showInputDialog("Enter text to replace:");
            if (searchText != null && !searchText.isEmpty() && replaceText != null) {
                String content = tx.getText();
                content = content.replace(searchText, replaceText);
                tx.setText(content);
            }
        }
    }

    class TextColor implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Color textColor = JColorChooser.showDialog(null, "Choose Text Color", tx.getForeground());
            tx.setForeground(textColor);
        }
    }

    class BackgroundColor implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Color bgColor = JColorChooser.showDialog(null, "Choose Background Color", tx.getBackground());
            tx.setBackground(bgColor);
        }
    }

    public static void main(String args[]) {
        Frame f = new EnhancedEditor();
        f.setSize(500, 400);
        f.setVisible(true);
        f.show();
    }
}
