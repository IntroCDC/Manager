package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 15:14
 */

public class ServerInfo {

    private final String serverName;
    private final String serverGroup;
    private final int onlinePlayers;
    private final int maxPlayers;
    private final double tps;

    public ServerInfo(String serverName, String serverGroup, int onlinePlayers, int maxPlayers, double tps) {
        this.serverName = serverName;
        this.serverGroup = serverGroup;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.tps = tps;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public double getTps() {
        return tps;
    }

}
