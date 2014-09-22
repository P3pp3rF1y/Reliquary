package xreliquary.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import xreliquary.lib.Reference;

/**
 * Created by Xeno on 9/20/2014.
 */
public class RecoilAnimationPacket implements IMessage, IMessageHandler<RecoilAnimationPacket, IMessage> {

    private int messageType;
    private float recoilCoefficient;
    public RecoilAnimationPacket() {

    }

    public RecoilAnimationPacket(int i, float f) {
        messageType = i;
        recoilCoefficient = f;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        messageType = buf.readInt();
        recoilCoefficient = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(messageType);
        buf.writeFloat(recoilCoefficient);
    }

    @Override
    public IMessage onMessage(RecoilAnimationPacket message, MessageContext ctx) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return null;
        if (message.messageType == Reference.RECOIL_PACKET_ID) {
            float recoil = (float)Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + (float)Reference.HANDGUN_RECOIL_SKILL_OFFSET - (float)Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM);
            recoil *= message.recoilCoefficient;
            float rotationPitch = player.prevRotationPitch - recoil;

            Vec3 playerLookVector = player.getLookVec();
            float knockbackPercent = 1F - (player.isSneaking() ? ((float)Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM) / (float)Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + (float)Reference.HANDGUN_KNOCKBACK_SKILL_OFFSET) : 0F);

            double xDiff = -playerLookVector.xCoord / 2 * knockbackPercent;
            double yDiff = -playerLookVector.yCoord / 2 * knockbackPercent;
            double zDiff = -playerLookVector.zCoord / 2 * knockbackPercent;

            player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, rotationPitch);
            player.setVelocity(xDiff, yDiff, zDiff);
        }
        if (message.messageType == Reference.RECOIL_COMPENSATION_PACKET_ID) {
            float recoil = (float)Reference.PLAYER_HANDGUN_SKILL_MAXIMUM + (float)Reference.HANDGUN_RECOIL_SKILL_OFFSET - (float)Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM);
            recoil *= message.recoilCoefficient;
            recoil /= 3;
            float compensationPercentage = (Math.min(player.experienceLevel, Reference.PLAYER_HANDGUN_SKILL_MAXIMUM) / (float)Reference.PLAYER_HANDGUN_SKILL_MAXIMUM * 0.75F) + 0.25F;
            float rotationPitch = player.prevRotationPitch + (recoil * compensationPercentage);

            player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, rotationPitch);
        }
        return null;
    }
}
