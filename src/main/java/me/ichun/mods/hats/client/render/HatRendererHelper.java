package me.ichun.mods.hats.client.render;

import me.ichun.mods.hats.client.core.HatInfoClient;
import me.ichun.mods.hats.client.core.ProxyClient;
import me.ichun.mods.hats.common.Hats;
import me.ichun.mods.hats.common.core.HatHandler;
import me.ichun.mods.ichunutil.client.module.tabula.model.ModelTabula;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class HatRendererHelper
{
    public static void renderHat(HatInfoClient info, float alpha, float hatScale, float mobRenderScaleX, float mobRenderScaleY, float mobRenderScaleZ, float renderYawOffset, float rotationYaw, float rotationPitch, float rotationRoll, float rotatePointVert, float rotatePointHori, float rotatePointSide, float offsetVert, float offsetHori, float offsetSide, boolean isPlayer, boolean bindTexture, float renderTick)
    {
        if(info == null)//hat names are lower case
        {
            return;
        }
        ModelTabula model = ProxyClient.models.get(info.hatName);

        if(model != null)
        {
            if(Hats.eventHandlerClient.currentHatRenders >= Hats.config.maxHatRenders && !isPlayer)
            {
                return;
            }

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);

            GlStateManager.pushMatrix();

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.scale(1.001F, 1.001F, 1.001F);
            GlStateManager.scale(mobRenderScaleX, mobRenderScaleY, mobRenderScaleZ);

            GlStateManager.rotate(renderYawOffset, 0.0F, -1.0F, 0.0F);

            GlStateManager.translate(-rotatePointSide, 0.0F, rotatePointHori);

            GlStateManager.rotate(renderYawOffset, 0.0F, 1.0F, 0.0F);

            GlStateManager.translate(0.0F, rotatePointVert, 0.0F);

            GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);

            GlStateManager.rotate(rotationYaw, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(rotationPitch, -1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rotationRoll, 0.0F, 0.0F, 1.0F);

            GlStateManager.translate(0.0F, 1F, 0.0F);

            GlStateManager.translate(offsetSide, offsetVert, -offsetHori);

            GlStateManager.translate(0.0F, -1F, 0.0F);
            GlStateManager.scale(hatScale, hatScale, hatScale);
            GlStateManager.translate(0.0F, 1F, 0.0F);

            if(bindTexture)
            {
                if(info.recolour > 0)
                {
                    float diffR = info.colourR - info.prevColourR;
                    float diffG = info.colourG - info.prevColourG;
                    float diffB = info.colourB - info.prevColourB;
                    float diffA = info.alpha - info.prevAlpha;

                    diffR *= (info.recolour - renderTick) / 20F;
                    diffG *= (info.recolour - renderTick) / 20F;
                    diffB *= (info.recolour - renderTick) / 20F;
                    diffA *= (info.recolour - renderTick) / 20F;

                    GlStateManager.color((info.colourR - diffR) / 255.0F, (info.colourG - diffG) / 255.0F, (info.colourB - diffB) / 255.0F, MathHelper.clamp(alpha * ((info.alpha - diffA) / 255.0F), 0.0F, 1.0F));
                }
                else
                {
                    GlStateManager.color((float)info.colourR / 255.0F, (float)info.colourG / 255.0F, (float)info.colourB / 255.0F, MathHelper.clamp(alpha * ((float)info.alpha / 255.0F), 0.0F, 1.0F));
                }
            }
            else
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, MathHelper.clamp(alpha, 0.0F, 1.0F));
            }

            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            model.render(0.0625F, bindTexture, false);

            GlStateManager.disableBlend();

            GlStateManager.popMatrix();

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

            Hats.eventHandlerClient.currentHatRenders++;
        }
        else if(!HatHandler.reloadingHats)
        {
            if(Hats.eventHandlerClient.requestedHats.add(info.hatName))
            {
                HatHandler.requestHat(info.hatName, null);
            }
        }
    }
}
