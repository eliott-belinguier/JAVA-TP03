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
    private final JButton addBoutton;

    public JFAdduser(final CountDownLatch countDownLatch) {
        final JPanel panel = new JPanel();
        final JLabel idLabel = new JLabel("    User ID:");
        final JLabel nameLabel = new JLabel("    User Name:");

        this.countDownLatch = countDownLatch;
        this.idField = new JTextField();
        this.nameField = new JTextField();
        this.addBoutton = new JButton("Ajouter");
        this.addBoutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                final User user;

                try {
                    user = UserManager.addUser(Long.parseLong(JFAdduser.this.idField.getText()), JFAdduser.this.nameField.getText()).complete();
                } catch (Exception ignored) {}
            }
        });
        panel.setLayout(new GridLayout(3, 2));
        setTitle("Ajout d'un utilisateur");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel.add(idLabel);
        panel.add(this.idField);
        panel.add(nameLabel);
        panel.add(this.nameField);
        panel.add(this.addBoutton);

        add(panel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                super.windowClosing(event);
                JFAdduser.this.countDownLatch.countDown();
            }
        });
    }

}
