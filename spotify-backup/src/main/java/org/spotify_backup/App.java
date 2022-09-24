package org.spotify_backup;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class App {
    private JFrame frame;
    private JPanel container;
    private JPanel progressPanel;
    private JPanel formPanel;
    private JTextField playlistIDText;
    private JButton cancelButton;
    private JProgressBar progressBar;
    private Configure configure;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void initialize() {
        JFrame frame = new JFrame("Spotify Backup");
        JPanel container = new JPanel();

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.setContentPane(container);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame = frame;
        this.container = container;
    }

    private void setComponents() {
        JPanel infoPanel = new JPanel(new GridLayout(1, 0));
        JPanel formPanel = new JPanel(new GridLayout(1, 2));
        JPanel progressPanel = new JPanel(new GridLayout(2, 0));

        infoPanel.setPreferredSize(new Dimension(400, 30));
        formPanel.setPreferredSize(new Dimension(400, 30));
        progressPanel.setPreferredSize(new Dimension(300, 50));

        // Cancellation components
        JProgressBar progressBar = new JProgressBar();
        JButton cancelButton = new JButton("Cancel");
        progressPanel.add(progressBar);
        progressPanel.add(cancelButton);

        // Top info panel
        this.configure = new Configure();
        JLabel label = new JLabel("Enter playlist ID:");
        label.setLabelFor(playlistIDText);
        JButton configureButton = new JButton("Configure");
        infoPanel.add(label);
        infoPanel.add(configureButton);

        configureButton.addActionListener(e -> {
            JFrame configureFrame = configure.getFrame();
            if (!configureFrame.isVisible()) {
                configureFrame.setVisible(true);
            } else {
                configureFrame.setAlwaysOnTop(true);
                configureFrame.requestFocus();
            }
        });

        // Form Panel
        this.playlistIDText = new JTextField();
        JButton actionButton = new JButton("Start");
        formPanel.add(playlistIDText);
        formPanel.add(actionButton);

        actionButton.addActionListener(this::startProcessing);
        playlistIDText.addActionListener(this::startProcessing);

        this.cancelButton = cancelButton;
        this.progressBar = progressBar;

        this.formPanel = formPanel;
        this.progressPanel = progressPanel;
        setPanelEnabled(progressPanel, false);

        // Add everything to the container
        container.add(infoPanel);
        container.add(formPanel);
        container.add(progressPanel);
    }

    private void startProcessing(ActionEvent e) {
        switchPanels(true);
        Thread thread = new Thread(() -> {
            try {
                process();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "An error occurred during processing."
                        + " Check your credentials.", "Spotify Backup", JOptionPane.ERROR_MESSAGE);
            }
            switchPanels(false);
        });
        cancelButton.addActionListener(v -> cancelProcessing(thread));
        thread.start();
    }

    private void process() throws IOException, InterruptedException {
        this.configure.setPlaylistID(playlistIDText.getText());
        PlaylistWriter writer = new PlaylistWriter(progressBar, this.configure);
        writer.write();
    }

    private void cancelProcessing(Thread thread) {
        thread.interrupt();
    }

    private void setPanelEnabled(JPanel panel, boolean b) {
        for (Component component : panel.getComponents()) {
            component.setEnabled(b);
        }
    }

    private void switchPanels(boolean b) {
        setPanelEnabled(progressPanel, b);
        setPanelEnabled(formPanel, !b);
    }

    public void run() {
        initialize();
        setComponents();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
