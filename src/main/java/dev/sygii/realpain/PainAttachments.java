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

import java.util.ArrayList;
import java.util.List;

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
            //PainMain.log("Sent packet: " + body.getBodyParts().get(PainMain.id("torso")).getPartHealth());

            body.getBodyParts().forEach((id, part) -> {
                buf.writeIdentifier(id);
                buf.writeFloat(part.getPartHealth());
                //buf.writeFloat(part.getPartMaxHealth());
            });
        }), (ctx, buf) -> {
            //PlayerBody newBody = new PlayerBody((PlayerEntity) MinecraftClient.getInstance().world.getEntityById((Integer) obj));
            List<SyncedAttachment.ContextRunner> runnerList = new ArrayList<>();

            int size = buf.readInt();
            //PainMain.log("Read packet: " + ((PlayerBody)obj));

            for (int i = 0; i < size; i++) {
                Identifier id = buf.readIdentifier();
                float health = buf.readFloat();
                //float maxHealth = buf.readFloat();
                runnerList.add((con , object) -> {
                    ((PlayerBody)object).getPart(id).setHealth(health);
                    return null;
                });
            }
            return (ctx2, object) -> {
                for (SyncedAttachment.ContextRunner run : runnerList) {
                    run.run(ctx2, object);
                }
                //PainMain.log("Read packet: " + ((PlayerBody)object).getBodyParts().get(PainMain.id("torso")).getPartHealth());
                return object;
            };
        });
        body.registerNBTSerializers(
                (nbt, body) -> {
                    NbtCompound partsNbt = new NbtCompound();
                    body.getBodyParts().forEach((id, part) -> {
                        partsNbt.putFloat(id.toString(), part.getPartHealth());
                    });
                    //PainMain.log("wrote nbt: " + partsNbt);

                    nbt.put("parts", partsNbt);
                },
                (nbt, obj) -> {
                    //PlayerBody newBody = new PlayerBody((PlayerEntity) obj);
                    NbtCompound comp = nbt.getCompound("parts");
                    ((PlayerBody)obj).getBodyParts().forEach((id, part) -> {
                        float health = comp.getFloat(id.toString());
                        ((PlayerBody)obj).getPart(id).fillInTheBlank(health);
                    });
                    ((PlayerBody)obj).newlyLoaded = true;
                    return ((PlayerBody)obj);
                });
    }
}
