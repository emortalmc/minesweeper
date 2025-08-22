package dev.emortal.minestom.minesweeper.util;

import com.google.protobuf.ByteString;

import dev.emortal.api.model.gamedata.V1MinesweeperProfile;
import dev.emortal.api.service.gameplayerdata.GamePlayerDataService;
import dev.emortal.api.utils.GrpcStubCollection;
import net.minestom.server.entity.Player;

public class Profiles {

    public static void save(byte[] saveData, Player host, Player[] players) {
        V1MinesweeperProfile.Builder saveBuilder = V1MinesweeperProfile.newBuilder()
        .setOwnerId(host.getUuid().toString())
        .setSaveData(ByteString.copyFrom(saveData));

        for (int i = 0; i < players.length; i++) {
            saveBuilder.setPlayerIds(i, players[i].getUuid().toString());
        }
        V1MinesweeperProfile save = saveBuilder.build();
        GamePlayerDataService gamePlayerService = GrpcStubCollection.getGamePlayerDataService().orElse(null);
        gamePlayerService.setMinesweeperProfile(save);
    }

    public static byte[] load(String profileId) {
        GamePlayerDataService gamePlayerService = GrpcStubCollection.getGamePlayerDataService().orElse(null);
        V1MinesweeperProfile profile = gamePlayerService.getMinesweeperProfile(profileId);
        return profile.getSaveData().toByteArray();
    }

}

