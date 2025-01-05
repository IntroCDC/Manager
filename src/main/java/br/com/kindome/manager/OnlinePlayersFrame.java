package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 15:06
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class OnlinePlayersFrame extends JFrame {

    private final JLabel onlineLabel;
    private final JPanel serversPanel;

    public OnlinePlayersFrame() {
        super("Jogadores Online");

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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel labelImage = new JLabel(icon);
        labelImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        topPanel.add(labelImage, BorderLayout.WEST);

        onlineLabel = new JLabel("Jogadores Online: Carregando...");
        onlineLabel.setFont(onlineLabel.getFont().deriveFont(Font.BOLD, 15f));
        onlineLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        topPanel.add(onlineLabel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        serversPanel = new JPanel();
        serversPanel.setLayout(new BoxLayout(serversPanel, BoxLayout.Y_AXIS));
        serversPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        serversPanel.setOpaque(false);

        JScrollPane scrollPrincipal = new JScrollPane(serversPanel);
        scrollPrincipal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                scrollPrincipal.getBorder()
        ));
        scrollPrincipal.setOpaque(false);
        scrollPrincipal.getViewport().setOpaque(false);

        mainPanel.add(scrollPrincipal, BorderLayout.CENTER);

        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/icone.png"));
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
        }

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

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
            JsonArray jsonArray = ManagerMain.readJson("serverstatus").getAsJsonArray();
            Map<String, List<String>> info = new HashMap<>();

            for (JsonElement element : jsonArray) {
                JsonObject object = element.getAsJsonObject();
                String server = object.get("servername").getAsString();

                if (server.equalsIgnoreCase("Proxy")) {
                    continue;
                }

                List<String> players = new ArrayList<>();
                for (JsonElement subElement : object.get("players").getAsJsonArray()) {
                    players.add(subElement.getAsString());
                }
                info.put(server, players);
            }

            int total = info.values().stream().mapToInt(List::size).sum();

            SwingUtilities.invokeLater(() -> updateInterface(total, info));

        } catch (final IOException exception) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    OnlinePlayersFrame.this,
                    "Não foi possível conectar no banco de dados!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            ));
        }
    }

    private void updateInterface(int totalOnline, Map<String, List<String>> servers) {
        onlineLabel.setText("Jogadores Online: " + totalOnline);

        serversPanel.removeAll();

        for (Map.Entry<String, List<String>> entry : servers.entrySet()) {
            String server = entry.getKey();
            List<String> players = entry.getValue();

            JPanel serverPanel = new JPanel();
            serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.Y_AXIS));

            TitledBorder titledBorder = BorderFactory.createTitledBorder(
                    "Servidor: " + server + " (" + players.size() + " jogadores)"
            );
            titledBorder.setTitleFont(
                    titledBorder.getTitleFont().deriveFont(Font.BOLD, 13f)
            );
            serverPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(0, 0, 10, 0),
                    titledBorder
            ));
            serverPanel.setOpaque(false);

            JTextArea playersTxt = new JTextArea();
            playersTxt.setEditable(false);
            playersTxt.setFont(new Font("Monospaced", Font.PLAIN, 13));

            StringBuilder sb = new StringBuilder();
            for (String j : players) {
                sb.append(j).append("\n");
            }
            playersTxt.setText(sb.toString());

            JScrollPane scrollJogadores = new JScrollPane(playersTxt);
            scrollJogadores.setPreferredSize(
                    new Dimension(300, Math.min(Math.max(players.size(), 2) * 20, 200))
            );
            serverPanel.add(scrollJogadores);

            serversPanel.add(serverPanel);
        }

        serversPanel.revalidate();
        serversPanel.repaint();
    }

}
