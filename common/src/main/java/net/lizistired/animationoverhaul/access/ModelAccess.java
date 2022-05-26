package net.lizistired.animationoverhaul.access;

import net.minecraft.client.model.ModelPart;

public interface ModelAccess {
    ModelPart getModelPart(String identifier);
    ModelPart getRootModelPart();
}
