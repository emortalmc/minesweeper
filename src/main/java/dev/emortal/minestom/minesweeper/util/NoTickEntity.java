package dev.emortal.minestom.minesweeper.util;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

public class NoTickEntity extends Entity {

    public NoTickEntity(EntityType entityType) {
        super(entityType);
    }

    @Override
    public void tick(long time) {

    }
}
