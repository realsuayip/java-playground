package org.spotify_backup;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridLayout;

public class Configure {
    private JFrame frame;
    private JPanel container;
    private String clientID;
    private String clientSecret;
    private String playlistID;

    public Configure() {
        initialize();
        setComponents();
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public JFrame getFrame() {
        return frame;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    private void initialize() {
        JFrame frame = new JFrame("Spotify Backup Configuration");
        JPanel container = new JPanel();
        container.setPreferredSize(new Dimension(360, 120));

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.setContentPane(container);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.frame = frame;
        this.container = container;
    }

    private void setComponents() {
        JPanel clientIDPane = new JPanel(new GridLayout(1, 2));
        JPanel clientSecretPane = new JPanel(new GridLayout(1, 2));

        JLabel clientIDLabel = new JLabel("Client ID: ", JLabel.TRAILING);
        JTextField clientIDText = new JTextField();
        clientIDLabel.setLabelFor(clientIDText);

        clientIDPane.add(clientIDLabel);
        clientIDPane.add(clientIDText);

        JLabel clientSecretLabel = new JLabel("Client Secret: ", JLabel.TRAILING);
        JTextField clientSecretText = new JTextField();
        clientSecretLabel.setLabelFor(clientSecretText);
        clientSecretPane.add(clientSecretLabel);
        clientSecretPane.add(clientSecretText);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            this.clientID = clientIDText.getText();
            this.clientSecret = clientSecretText.getText();
            this.frame.setVisible(false);
        });

        mainPanel.add(clientIDPane);
        mainPanel.add(clientSecretPane);
        mainPanel.add(saveButton);
        container.add(mainPanel);
    }
}
