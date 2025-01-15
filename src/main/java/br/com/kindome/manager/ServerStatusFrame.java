package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 15:11
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ServerStatusFrame extends JFrame {

    private final DefaultTableModel model;

    public ServerStatusFrame() {
        super("Status dos Servidores | Manager v" + ManagerMain.VERSION);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/icone.png"));
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone: " + e.getMessage());
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon icon = null;
        try {
            icon = new ImageIcon(getClass().getResource("/logo.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar a imagem: " + e.getMessage());
        }
        JLabel labelImage = new JLabel(icon);
        labelImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        topPanel.add(labelImage, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Status dos Servidores");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        TitledBorder tableBorder = BorderFactory.createTitledBorder("Lista de Servidores");
        tableBorder.setTitleFont(tableBorder.getTitleFont().deriveFont(Font.BOLD, 14f));
        tablePanel.setBorder(tableBorder);

        model = new DefaultTableModel(new Object[]{
                "Servidor", "Grupo", "Jogadores Online", "Máximo Jogadores", "TPS"
        }, 0);

        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setRowHeight(22);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(220, 240, 255));

        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        tablePanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JButton updateButton = new JButton("Atualizar");
        updateButton.setFont(new Font("Arial", Font.BOLD, 13));
        updateButton.addActionListener(e -> updateInfo());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(updateButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
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
            final List<ServerInfo> info = new ArrayList<>();

            for (JsonElement element : jsonArray) {
                JsonObject object = element.getAsJsonObject();
                info.add(new ServerInfo(
                        object.get("servername").getAsString(),
                        object.get("category").getAsString(),
                        object.get("online").getAsInt(),
                        object.get("max").getAsInt(),
                        object.get("tps").getAsDouble()
                ));
            }

            SwingUtilities.invokeLater(() -> updateInterface(info));

        } catch (IOException exception) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(ServerStatusFrame.this,
                        "Não foi possível conectar ao banco de dados!",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            });
        }
    }

    private void updateInterface(List<ServerInfo> servers) {
        model.setRowCount(0);
        for (ServerInfo si : servers) {
            model.addRow(new Object[] {
                    si.getServerName(),
                    si.getServerGroup(),
                    si.getOnlinePlayers(),
                    si.getMaxPlayers(),
                    si.getTps()
            });
        }
    }

}
