package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 13:37
 */

import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class LoginFrame extends JFrame {

    private final JTextField userText;
    private final JPasswordField passwordTxt;
    private final JCheckBox save;

    private static final File LOGIN_FILE = new File("login.json");

    public LoginFrame() {
        super("Login | Manager v" + ManagerMain.VERSION);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }

        String userSaved = null;
        String passwordSaved = null;
        if (LOGIN_FILE.exists()) {
            try {
                JsonObject jsonObject = ManagerMain.PARSER
                        .parse(new FileReader(LOGIN_FILE))
                        .getAsJsonObject();
                userSaved = jsonObject.get("user").getAsString();
                passwordSaved = jsonObject.get("password").getAsString();
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            }
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        ImageIcon icon = null;
        try {
            icon = new ImageIcon(getClass().getResource("/logo.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Não foi possível carregar a imagem: " + e.getMessage());
        }

        JPanel panelImage = new JPanel();
        panelImage.setLayout(new BoxLayout(panelImage, BoxLayout.Y_AXIS));
        panelImage.setOpaque(false);

        JLabel labelImagem = new JLabel(icon);
        labelImagem.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        panelImage.add(labelImagem);

        JPanel panelLogin = new JPanel(new GridBagLayout());
        panelLogin.setOpaque(false);

        TitledBorder loginBorder = BorderFactory.createTitledBorder("Credenciais");
        loginBorder.setTitleFont(loginBorder.getTitleFont().deriveFont(Font.BOLD, 12f));
        panelLogin.setBorder(loginBorder);

        GridBagConstraints bag = new GridBagConstraints();
        bag.insets = new Insets(8, 8, 8, 8);
        bag.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Usuário:");
        JLabel passwordLabel = new JLabel("Senha:");

        userText = new JTextField(15);
        passwordTxt = new JPasswordField(15);

        if (userSaved != null) {
            userText.setText(userSaved);
        }
        if (passwordSaved != null) {
            passwordTxt.setText(passwordSaved);
        }

        save = new JCheckBox("Lembrar login");
        save.setSelected(userSaved != null);

        JButton loginButton = new JButton("Entrar");
        JButton cancelButton = new JButton("Cancelar");

        Font commonFont = new Font("Arial", Font.PLAIN, 13);
        userLabel.setFont(commonFont);
        passwordLabel.setFont(commonFont);
        save.setFont(commonFont);
        loginButton.setFont(commonFont.deriveFont(Font.BOLD));
        cancelButton.setFont(commonFont.deriveFont(Font.BOLD));

        bag.gridx = 0;
        bag.gridy = 0;
        panelLogin.add(userLabel, bag);

        bag.gridx = 1;
        bag.gridy = 0;
        panelLogin.add(userText, bag);

        bag.gridx = 0;
        bag.gridy = 1;
        panelLogin.add(passwordLabel, bag);

        bag.gridx = 1;
        bag.gridy = 1;
        panelLogin.add(passwordTxt, bag);

        bag.gridx = 1;
        bag.gridy = 2;
        panelLogin.add(save, bag);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(loginButton);
        buttonsPanel.add(cancelButton);

        bag.gridx = 1;
        bag.gridy = 3;
        panelLogin.add(buttonsPanel, bag);

        loginButton.addActionListener(event -> {
            String user = userText.getText();
            char[] password = passwordTxt.getPassword();
            String passwordText = new String(password);
            String passwordSha;
            if (passwordText.length() == 128) {
                passwordSha = passwordText;
            } else {
                passwordSha = ManagerMain.hashSha512(passwordText);
            }
            boolean remember = save.isSelected();
            String kuid;

            try {
                JsonObject object = ManagerMain
                        .readJson("account/" + user)
                        .getAsJsonObject();

                if (object.has("error")) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Conta não encontrada!",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                kuid = object.get("kuid").getAsString();
                ArrayList<String> allowedGroup = new ArrayList<>();
                allowedGroup.add("Suporte");
                allowedGroup.add("Designer");
                allowedGroup.add("Builder");
                allowedGroup.add("TrialMod");
                allowedGroup.add("YouTuber+");
                allowedGroup.add("YouTuber");
                allowedGroup.add("Streamer+");
                allowedGroup.add("Streamer");
                allowedGroup.add("TikToker+");
                allowedGroup.add("TikToker");
                allowedGroup.add("Moderador");
                allowedGroup.add("Administrador");
                allowedGroup.add("Gerente");
                allowedGroup.add("Dono");
                allowedGroup.add("Kindome");
                allowedGroup.add("IntroCDC");

                JsonObject loginInfo = ManagerMain
                        .readJson("password/" + kuid + "/" + passwordSha)
                        .getAsJsonObject();

                if (loginInfo.get("correct").getAsBoolean()) {
                    if (!allowedGroup.contains(object.get("group").getAsString())) {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Você não tem permissão de logar no manager!",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login bem-sucedido!");

                    if (remember) {
                        saveLogin(user, passwordSha);
                    } else {
                        if (LOGIN_FILE.exists()) {
                            LOGIN_FILE.delete();
                        }
                    }
                    dispose();

                    SwingUtilities.invokeLater(() -> new PrincipalFrame(
                            object.get("name").getAsString(),
                            object.get("group").getAsString()
                    ).setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Usuário ou senha inválidos!",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Não foi possível conectar à API do servidor!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(event -> dispose());

        mainPanel.add(panelImage, BorderLayout.WEST);
        mainPanel.add(panelLogin, BorderLayout.CENTER);

        setContentPane(mainPanel);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void saveLogin(String usuario, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user", usuario);
        jsonObject.addProperty("password", password);

        try (FileWriter writer = new FileWriter(LOGIN_FILE)) {
            writer.write(jsonObject.toString());
        } catch (IOException exception) {
            System.err.println("Não foi possível salvar o arquivo login.json: " + exception.getMessage());
        }
    }

}
