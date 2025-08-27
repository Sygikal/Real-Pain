package dev.sygii.realpain;

import dev.sygii.attachmentsapi.attachment.Attachment;
import dev.sygii.attachmentsapi.attachment.AttachmentIdentifier;
import dev.sygii.attachmentsapi.attachment.synced.SyncedAttachment;
import dev.sygii.attachmentsapi.registry.AttachmentDeclarer;
import dev.sygii.attachmentsapi.registry.AttachmentInitializer;
import dev.sygii.attachmentsapi.registry.AttachmentRegistrar;
import dev.sygii.attachmentsapi.registry.SyncedAttachmentRegistrar;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.entity.PlayerBody;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PainAttachments implements AttachmentInitializer {
    public static AttachmentIdentifier bodyId = AttachmentIdentifier.of(PainMain.MOD_ID, "body");
    public static SyncedAttachment<PlayerBody> body;

    @Override
    public void declareAttachments(AttachmentDeclarer declarer) {
        declarer.declareAttachment(
                bodyId,
                FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_1657"),
                PlayerBody.class, null
        );
    }

    @Override
    public void registerAttachments(AttachmentRegistrar registrar) {
        //body = registrar.registerAttachment(bodyId);
    }

    @Override
    public void registerSyncedAttachments(SyncedAttachmentRegistrar syncedRegistrar) {
        body = syncedRegistrar.registerSyncedAttachment(bodyId, ((buf, body) -> {
            buf.writeInt(body.getBodyParts().size());
            body.getBodyParts().forEach((id, part) -> {
                buf.writeIdentifier(id);
                buf.writeFloat(part.getPartHealth());
                //buf.writeFloat(part.getPartMaxHealth());
            });
        }), (buf, obj) -> {
            PlayerBody newBody = new PlayerBody((PlayerEntity) MinecraftClient.getInstance().world.getEntityById((Integer) obj));
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                Identifier id = buf.readIdentifier();
                float health = buf.readFloat();
                //float maxHealth = buf.readFloat();
                newBody.getPart(id).setHealth(health);
            }
            return newBody;
        });
        body.registerNBTSerializers(
                (nbt, body) -> {
                    NbtCompound partsNbt = new NbtCompound();
                    body.getBodyParts().forEach((id, part) -> {
                        partsNbt.putFloat(id.toString(), part.getPartHealth());
                    });
                    nbt.put("parts", partsNbt);
                },
                (nbt, obj) -> {
                    PlayerBody newBody = new PlayerBody((PlayerEntity) obj);
                    NbtCompound comp = nbt.getCompound("parts");
                    newBody.getBodyParts().forEach((id, part) -> {
                        float health = comp.getFloat(id.toString());
                        newBody.getPart(id).setHealth(health);
                    });
                    return newBody;
                });
    }
}
