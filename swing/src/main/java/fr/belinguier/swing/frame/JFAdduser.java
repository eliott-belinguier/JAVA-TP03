package fr.belinguier.swing.frame;

import fr.belinguier.swing.user.User;
import fr.belinguier.swing.user.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

public class JFAdduser extends JFrame {

    private final CountDownLatch countDownLatch;
    private final JTextField idField;
    private final JTextField nameField;
    private final JButton addButton;
    private final JButton deleteButton;

    public JFAdduser(final CountDownLatch countDownLatch) {
        final JPanel panel = new JPanel();
        final JLabel idLabel = new JLabel("    User ID:");
        final JLabel nameLabel = new JLabel("    User Name:");

        this.countDownLatch = countDownLatch;
        this.idField = new JTextField();
        this.nameField = new JTextField();
        this.addButton = new JButton("Ajouter");
        this.deleteButton = new JButton("Supprimer");
        this.addButton.addActionListener(actionEvent -> UserManager.addUser(Long.parseLong(JFAdduser.this.idField.getText()), JFAdduser.this.nameField.getText()).queue(user -> {
            JOptionPane.showMessageDialog(JFAdduser.this, "User " + user.name  + " added.", "Add user", JOptionPane.INFORMATION_MESSAGE);
        }, this::displayError));
        this.deleteButton.addActionListener(actionEvent -> UserManager.deleteUser(Long.parseLong(JFAdduser.this.idField.getText())).queue(isDelete -> {
            JOptionPane.showMessageDialog(JFAdduser.this, "User removed.", "Add user", JOptionPane.INFORMATION_MESSAGE);
        }, this::displayError));
        panel.setLayout(new GridLayout(3, 2));
        setTitle("Ajout d'un utilisateur");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel.add(idLabel);
        panel.add(this.idField);
        panel.add(nameLabel);
        panel.add(this.nameField);
        panel.add(this.addButton);
        panel.add(this.deleteButton);

        add(panel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                super.windowClosing(event);
                JFAdduser.this.countDownLatch.countDown();
            }
        });
    }


    private void displayError(final Throwable throwable) {
        JOptionPane.showMessageDialog(this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

}
