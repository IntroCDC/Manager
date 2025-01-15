package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 15:10
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class RunningRoomsFrame extends JFrame {

    private final JLabel roomsLabel;
    private final JPanel roomsPanel;

    public RunningRoomsFrame() {
        super("Salas Rodando | Manager v" + ManagerMain.VERSION);

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
        } catch (Exception e) {
            System.err.println("Não foi possível carregar a imagem: " + e.getMessage());
        }

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel labelImage = new JLabel(icon);
        labelImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        headerPanel.add(labelImage, BorderLayout.WEST);

        roomsLabel = new JLabel("Salas rodando: Carregando...");
        roomsLabel.setFont(roomsLabel.getFont().deriveFont(Font.BOLD, 14f));
        roomsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        headerPanel.add(roomsLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));
        roomsPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(roomsPanel);
        scroll.setBorder(BorderFactory.createTitledBorder("Lista de Salas Rodando"));
        mainPanel.add(scroll, BorderLayout.CENTER);

        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/icone.png"));
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        setContentPane(mainPanel);

        updateInfo();

        Timer updateTimer = new Timer(10000, event -> {
            if (isVisible()) {
                updateInfo();
            }
        });
        updateTimer.start();
    }

    private void updateInfo() {
        try {
            JsonArray array = ManagerMain.readJson("allrunninggames").getAsJsonArray();
            final List<RoomInfo> info = new ArrayList<>();
            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                info.add(new RoomInfo(
                        object.get("id").getAsString(),
                        object.get("minigame").getAsString(),
                        object.get("map").getAsString(),
                        object.get("onlinePlayers").getAsInt(),
                        object.get("state").getAsString()
                ));
            }

            SwingUtilities.invokeLater(() -> updateInterface(info));
        } catch (IOException exception) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    RunningRoomsFrame.this,
                    "Não foi possível conectar no banco de dados!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            ));
        }
    }

    private void updateInterface(List<RoomInfo> rooms) {
        roomsLabel.setText("Salas rodando: " + rooms.size());

        roomsPanel.removeAll();

        for (RoomInfo room : rooms) {
            JPanel roomPanel = new JPanel(new BorderLayout());
            roomPanel.setOpaque(false);
            roomPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(0, 0, 10, 0),
                    BorderFactory.createTitledBorder(room.getId())
            ));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);

            JPanel minigameMapaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            minigameMapaPanel.setOpaque(false);
            JLabel lblMinigame = new JLabel("MiniGame: " + room.getMinigame());
            lblMinigame.setFont(lblMinigame.getFont().deriveFont(Font.PLAIN, 12f));
            JLabel lblMapa = new JLabel(" | Mapa: " + room.getMapa());
            lblMapa.setFont(lblMapa.getFont().deriveFont(Font.PLAIN, 12f));
            minigameMapaPanel.add(lblMinigame);
            minigameMapaPanel.add(lblMapa);

            JPanel statusPlayersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            statusPlayersPanel.setOpaque(false);
            JLabel lblStatus = new JLabel("Status: " + room.getStatus());
            lblStatus.setFont(lblStatus.getFont().deriveFont(Font.PLAIN, 12f));
            JLabel amountLabel = new JLabel(" | Jogadores: " + room.getPlayers());
            amountLabel.setFont(amountLabel.getFont().deriveFont(Font.PLAIN, 12f));
            statusPlayersPanel.add(lblStatus);
            statusPlayersPanel.add(amountLabel);

            infoPanel.add(minigameMapaPanel);
            infoPanel.add(statusPlayersPanel);
            roomPanel.add(infoPanel, BorderLayout.NORTH);

            roomsPanel.add(roomPanel);
        }

        roomsPanel.revalidate();
        roomsPanel.repaint();
    }

}
