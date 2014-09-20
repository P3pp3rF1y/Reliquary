package xreliquary.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * Created by Xeno on 9/20/2014.
 */
public class HandgunRecoilPacketHandler implements IMessageHandler<ReliquaryMessage, IMessage> {

    @Override
    public IMessage onMessage(ReliquaryMessage message, MessageContext ctx) {
        //right now this method is pretty unintuitive.
        //it could later be adapted to handle different kinds of packets
        //but right now, it only serves one purpose.

        //it's going to cause the gun to recoil when fired by the player. This is to prevent server/client desync issues and fugliness.

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        int recoil = (player.experienceLevel >= 20 ? 5 : (25 - player.experienceLevel));

        player.rotationPitch -= recoil;
        return null;
    }
}
