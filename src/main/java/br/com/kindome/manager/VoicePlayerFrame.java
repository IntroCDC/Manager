package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 25/12/2024 - 20:07
 */

import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VoicePlayerFrame extends JFrame {

    private final JTextField textField;
    private Player currentPlayer;

    public VoicePlayerFrame() {
        super("Reprodutor de Voz | Manager v" + ManagerMain.VERSION);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        ImageIcon icon = null;
        try {
            icon = new ImageIcon(getClass().getResource("/logo.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Não foi possível carregar a imagem: " + e.getMessage());
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel logoLabel = new JLabel(icon);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        topPanel.add(logoLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Reprodutor de Voz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        TitledBorder textBorder = BorderFactory.createTitledBorder("Digite o texto para sintetizar");
        textBorder.setTitleFont(new Font("Arial", Font.BOLD, 12));
        centerPanel.setBorder(textBorder);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        centerPanel.add(textField, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setOpaque(false);

        JButton playButton = new JButton("Reproduzir");
        playButton.setFont(new Font("Arial", Font.BOLD, 13));
        playButton.addActionListener(event -> playVoice());
        bottomPanel.add(playButton);

        JButton playAndSaveButton = new JButton("Reproduzir e Salvar Áudio");
        playAndSaveButton.setFont(new Font("Arial", Font.BOLD, 13));
        playAndSaveButton.addActionListener(event -> playAndSaveVoice());
        bottomPanel.add(playAndSaveButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void playVoice() {
        String typedText = textField.getText().trim();
        if (typedText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Digite algum texto para reproduzir!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (currentPlayer != null) {
                currentPlayer.close();
            }

            String encodedText = URLEncoder.encode(typedText, StandardCharsets.UTF_8.toString());
            String audioUrl = "http://api.kindome.com.br/voice/" + encodedText;

            Thread playbackThread = new Thread(() -> {
                try (InputStream is = new URL(audioUrl).openStream()) {
                    currentPlayer = new Player(is);
                    currentPlayer.play();
                } catch (Exception exception) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            VoicePlayerFrame.this,
                            "Não foi possível reproduzir o áudio:\n" + exception.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    ));
                }
            });
            playbackThread.start();

        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao tentar reproduzir: " + exception.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void playAndSaveVoice() {
        String typedText = textField.getText().trim();
        if (typedText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Digite algum texto para reproduzir e salvar!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar arquivo de áudio (MP3)");
        fileChooser.setSelectedFile(new File("voz.mp3"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File chosenFile = fileChooser.getSelectedFile();
        if (chosenFile == null) {
            return;
        }

        try {
            if (currentPlayer != null) {
                currentPlayer.close();
            }

            String encodedText = URLEncoder.encode(typedText, StandardCharsets.UTF_8.toString());
            String audioUrl = "http://api.kindome.com.br/voice/" + encodedText;

            Thread playbackThread = new Thread(() -> {
                try (InputStream is = new URL(audioUrl).openStream()) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(data)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }

                    try (FileOutputStream fos = new FileOutputStream(chosenFile)) {
                        fos.write(buffer.toByteArray());
                    }

                    ByteArrayInputStream inMemoryStream = new ByteArrayInputStream(buffer.toByteArray());
                    currentPlayer = new Player(inMemoryStream);
                    currentPlayer.play();

                } catch (Exception exception) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            VoicePlayerFrame.this,
                            "Não foi possível salvar e/ou reproduzir o áudio:\n" + exception.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    ));
                }
            });
            playbackThread.start();

        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao tentar reproduzir e salvar: " + exception.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
