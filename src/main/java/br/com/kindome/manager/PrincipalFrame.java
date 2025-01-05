package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 14:07
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PrincipalFrame extends JFrame {

    public PrincipalFrame(String user, String group) {
        super("Tela Principal");

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        ImageIcon icon = null;
        try {
            icon = new ImageIcon(getClass().getResource("/logo.png"));
            setIconImage(icon.getImage());
        } catch (Exception exception) {
            System.err.println("Não foi possível carregar a imagem: " + exception.getMessage());
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel labelImage = new JLabel(icon);
        labelImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        topPanel.add(labelImage, BorderLayout.WEST);

        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);

        TitledBorder userInfoBorder = BorderFactory.createTitledBorder("Dados do Usuário");
        userInfoBorder.setTitleFont(userInfoBorder.getTitleFont().deriveFont(Font.BOLD, 12f));
        userInfoPanel.setBorder(userInfoBorder);

        JLabel userLabel = new JLabel("Usuário: " + user);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel groupLabel = new JLabel("Grupo: " + group);
        groupLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        userInfoPanel.add(userLabel);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(groupLabel);

        topPanel.add(userInfoPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonsPanel.setOpaque(false);

        JButton profileButton = new JButton("Visualizar Perfil");
        JButton onlinePlayersButton = new JButton("Jogadores Online");
        JButton runningRoomsButton = new JButton("Salas Rodando");
        JButton serverStatusButton = new JButton("Status dos Servidores");
        JButton configButton = new JButton("Configurações");
        JButton voiceButton = new JButton("Voz do Serjão");

        Font buttonFont = new Font("Arial", Font.BOLD, 13);
        profileButton.setFont(buttonFont);
        onlinePlayersButton.setFont(buttonFont);
        runningRoomsButton.setFont(buttonFont);
        serverStatusButton.setFont(buttonFont);
        configButton.setFont(buttonFont);
        voiceButton.setFont(buttonFont);

        profileButton.addActionListener(event -> SwingUtilities.invokeLater(() ->
                new ViewProfileFrame().setVisible(true)));

        onlinePlayersButton.addActionListener(event -> SwingUtilities.invokeLater(() ->
                new OnlinePlayersFrame().setVisible(true)));

        runningRoomsButton.addActionListener(event -> SwingUtilities.invokeLater(() ->
                new RunningRoomsFrame().setVisible(true)));

        serverStatusButton.addActionListener(event -> SwingUtilities.invokeLater(() ->
                new ServerStatusFrame().setVisible(true)));

        configButton.addActionListener(event -> SwingUtilities.invokeLater(() ->
                new ConfigurationsFrame().setVisible(true)));

        voiceButton.addActionListener(event -> SwingUtilities.invokeLater(() ->
                new VoicePlayerFrame().setVisible(true)));

        buttonsPanel.add(profileButton);
        buttonsPanel.add(onlinePlayersButton);
        buttonsPanel.add(runningRoomsButton);
        buttonsPanel.add(serverStatusButton);
        buttonsPanel.add(configButton);
        buttonsPanel.add(voiceButton);

        mainPanel.add(buttonsPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
