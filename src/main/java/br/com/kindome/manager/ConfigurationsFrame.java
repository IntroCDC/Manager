package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 25/12/2024 - 19:50
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ConfigurationsFrame extends JFrame {

    public ConfigurationsFrame() {
        super("Configurações | Manager v" + ManagerMain.VERSION);

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

        JLabel labelImagem = new JLabel(icon);
        labelImagem.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        topPanel.add(labelImagem, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Lista de Configurações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] colunas = {"Propriedade", "Valor"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            JsonArray configArray = ManagerMain.readJson("configs").getAsJsonArray();
            for (JsonElement element : configArray) {
                JsonObject object = element.getAsJsonObject();
                String name = object.get("name").getAsString();
                String value = object.get("value").getAsString();
                model.addRow(new Object[]{name, value});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível ler as configurações.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        JTable table = new JTable(model);
        table.setRowHeight(22);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        TitledBorder tableBorder = BorderFactory.createTitledBorder("Configurações do Servidor");
        tableBorder.setTitleFont(new Font("Arial", Font.BOLD, 12));
        tablePanel.setBorder(tableBorder);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

}

