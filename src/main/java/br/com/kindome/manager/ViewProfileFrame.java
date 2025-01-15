package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 14:55
 */

import com.google.gson.JsonObject;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class ViewProfileFrame extends JFrame {

    private final JTextField searchTxt;

    private final JLabel nickTxt;
    private final JLabel kuidTxt;
    private final JLabel groupTxt;
    private final JLabel whoGaveGroupTxt;
    private final JLabel whenReceivedTxt;
    private final JLabel ipTxt;
    private final JLabel locationTxt;
    private final JLabel tagTxt;
    private final JLabel firstLoginTxt;
    private final JLabel lastLoginTxt;
    private final JLabel descriptionTxt;

    public ViewProfileFrame() {
        super("Visualizar Perfil | Manager v" + ManagerMain.VERSION);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
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

        JLabel labelImagem = new JLabel(icon);
        labelImagem.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        topPanel.add(labelImagem, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Consultar Perfil de Usuário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Usuário:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        searchTxt = new JTextField(20);
        JButton loadButton = new JButton("Carregar");
        loadButton.setFont(new Font("Arial", Font.BOLD, 13));

        searchPanel.add(userLabel);
        searchPanel.add(searchTxt);
        searchPanel.add(loadButton);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setOpaque(false);

        TitledBorder infoBorder = BorderFactory.createTitledBorder("Informações do Usuário");
        infoBorder.setTitleFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.setBorder(infoBorder);

        nickTxt = new JLabel("Nick: ");
        kuidTxt = new JLabel("KUID: ");
        groupTxt = new JLabel("Grupo: ");
        whoGaveGroupTxt = new JLabel("Quem deu o grupo: ");
        whenReceivedTxt = new JLabel("Quando foi dado o grupo: ");
        ipTxt = new JLabel("IP: ");
        locationTxt = new JLabel("Localização: ");
        tagTxt = new JLabel("Tag: ");
        firstLoginTxt = new JLabel("Primeiro login: ");
        lastLoginTxt = new JLabel("Último login: ");
        descriptionTxt = new JLabel("Descrição: ");

        Font infoFont = new Font("Arial", Font.PLAIN, 13);
        nickTxt.setFont(infoFont);
        kuidTxt.setFont(infoFont);
        groupTxt.setFont(infoFont);
        whoGaveGroupTxt.setFont(infoFont);
        whenReceivedTxt.setFont(infoFont);
        ipTxt.setFont(infoFont);
        locationTxt.setFont(infoFont);
        tagTxt.setFont(infoFont);
        firstLoginTxt.setFont(infoFont);
        lastLoginTxt.setFont(infoFont);
        descriptionTxt.setFont(infoFont);

        infoPanel.add(nickTxt);
        infoPanel.add(kuidTxt);
        infoPanel.add(groupTxt);
        infoPanel.add(whoGaveGroupTxt);
        infoPanel.add(whenReceivedTxt);
        infoPanel.add(ipTxt);
        infoPanel.add(locationTxt);
        infoPanel.add(tagTxt);
        infoPanel.add(firstLoginTxt);
        infoPanel.add(lastLoginTxt);
        infoPanel.add(descriptionTxt);

        centerPanel.add(infoPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        loadButton.addActionListener(event -> {
            String userText = searchTxt.getText().trim();
            if (userText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        ViewProfileFrame.this,
                        "Por favor, insira o nome do usuário.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String nick;
            String kuid;
            String group;
            String whoGaveGroup;
            String whenReceived;
            String ip;
            String location;
            String tag;
            String firstLogin;
            String lastLogin;
            String description;

            try {
                JsonObject account = ManagerMain.readJson("account/" + userText).getAsJsonObject();
                if (account.has("error")) {
                    JOptionPane.showMessageDialog(
                            ViewProfileFrame.this,
                            "Conta não encontrada!",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                nick = account.get("name").getAsString();
                kuid = account.get("kuid").getAsString();
                group = account.get("group").getAsString();

                JsonObject packages = ManagerMain.readJson(
                        "packages/" + kuid + "/whoGaveGroup,tag,description"
                ).getAsJsonObject();

                JsonObject statistics = ManagerMain.readJson(
                        "statistics/" + kuid + "/whenReceivedGroup,firstLogin,lastLogin"
                ).getAsJsonObject();

                if (packages.has("whoGaveGroup")) {
                    JsonObject gave = ManagerMain
                            .readJson("account/" + packages.get("whoGaveGroup").getAsString())
                            .getAsJsonObject();
                    whoGaveGroup = gave.get("name").getAsString();
                } else {
                    whoGaveGroup = "Não encontrado";
                }

                if (statistics.has("whenReceivedGroup")) {
                    whenReceived = ManagerMain.toDate(statistics.get("whenReceivedGroup").getAsLong());
                } else {
                    whenReceived = "Não Encontrado";
                }

                JsonObject ipObject = ManagerMain.readJson("34978g5yb4394/" + kuid).getAsJsonObject();
                ip = ipObject.get("ip").getAsString();
                if (ip.equalsIgnoreCase("Não encontrado")
                        || ip.equalsIgnoreCase("127.0.0.1")
                        || ip.startsWith("192.168.0")) {
                    location = "IP não encontrado";
                } else {
                    try {
                        JsonObject ipInfo = ManagerMain.readJson("http://ip-api.com/json/" + ip).getAsJsonObject();
                        location = ipInfo.get("city").getAsString()
                                + "/" + ipInfo.get("regionName").getAsString()
                                + " (" + ipInfo.get("country").getAsString() + ")";
                    } catch (Exception exception) {
                        location = "Ocorreu um erro ao localizar o IP";
                    }
                }

                if (packages.has("tag")) {
                    tag = packages.get("tag").getAsString();
                } else {
                    tag = group;
                }

                if (statistics.has("firstLogin")) {
                    firstLogin = ManagerMain.toDate(statistics.get("firstLogin").getAsLong());
                } else {
                    firstLogin = "Não encontrado";
                }

                if (statistics.has("lastLogin")) {
                    lastLogin = ManagerMain.toDate(statistics.get("lastLogin").getAsLong());
                } else {
                    lastLogin = "Não encontrado";
                }

                if (packages.has("description")) {
                    description = packages.get("description").getAsString();
                } else {
                    description = "Não definido";
                }

            } catch (IOException exception) {
                JOptionPane.showMessageDialog(
                        ViewProfileFrame.this,
                        "Não foi possível conectar no banco de dados!",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            nickTxt.setText("Nick: " + nick);
            kuidTxt.setText("KUID: " + kuid);
            groupTxt.setText("Grupo: " + group);
            whoGaveGroupTxt.setText("Quem deu o grupo: " + whoGaveGroup);
            whenReceivedTxt.setText("Quando foi dado o grupo: " + whenReceived);
            ipTxt.setText("IP: " + ip);
            locationTxt.setText("Localização: " + location);
            tagTxt.setText("Tag: " + tag);
            firstLoginTxt.setText("Primeiro login: " + firstLogin);
            lastLoginTxt.setText("Último login: " + lastLogin);
            descriptionTxt.setText("Descrição: " + description);
        });

        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/icone.png"));
            setIconImage(frameIcon.getImage());
        } catch (Exception ignored) {
        }

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

}
