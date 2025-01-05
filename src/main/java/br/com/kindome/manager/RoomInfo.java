package br.com.kindome.manager;
/*
 * Written by IntroCDC, Bruno Coêlho at 12/12/2024 - 15:07
 */

public class RoomInfo {

    private final String id;
    private final String minigame;
    private final String mapa;
    private final int players;
    private final String status;

    public RoomInfo(String id, String minigame, String mapa, int players, String status) {
        this.id = id;
        this.minigame = minigame;
        this.mapa = mapa;
        this.players = players;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getMinigame() {
        return minigame;
    }

    public String getMapa() {
        return mapa;
    }

    public int getPlayers() {
        return players;
    }

    public String getStatus() {
        return status;
    }

}

