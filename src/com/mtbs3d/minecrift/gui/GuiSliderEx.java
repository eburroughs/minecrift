package com.mtbs3d.minecrift.gui;

import com.mtbs3d.minecrift.gui.GuiButtonEx;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EnumOptions;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GuiSliderEx extends GuiButtonEx
{
    /** The relative value of this slider control. */
    private float sliderValue = 1.0F;

    /** Is this slider control being dragged. */
    private boolean dragging = false;

    /** Additional ID for this slider control. */
    private EnumOptions idFloat = null;

    /** The maximum actual value of this slider control. */
    private float maxValue = 1.0f;

    /** The minimum actual value of this slider control. */
    private float minValue = 0.0f;

    /** The allowable increment of the actual value of this slider control. */
    private float increment;

    /** The last actual value of this slider control */
    private float lastValue;

    /** The last known value x position of the mouse pointer */
    private int lastMouseX = -1;

    GuiEventEx _eventHandler = null;

    public GuiSliderEx(int par1, int par2, int par3,
                       EnumOptions par4EnumOptions, String par5Str,
                       float minValue, float maxValue, float increment, float currentValue)
    {
        super(par1, par2, par3, 150, 20, par5Str);
        this.idFloat = par4EnumOptions;
        this.increment = increment;
        this.lastValue = Math.round(currentValue / this.increment) * this.increment;

        this.minValue = minValue;
        this.maxValue = maxValue;
        if (this.lastValue > this.maxValue)
            this.lastValue = this.maxValue;
        else if (this.lastValue < this.minValue)
            this.lastValue = this.minValue;

        float range = this.maxValue - this.minValue;
        this.sliderValue = (this.lastValue - this.minValue) / range;
    }

    void setEventHandler(GuiEventEx eventHandler)
    {
        _eventHandler = eventHandler;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean par1)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            if (this.dragging && par2 != this.lastMouseX)
            {
                this.lastMouseX = -1;
                this.sliderValue = (float)(par2 - (this.xPosition + 4)) / (float)(this.width - 8);

                if (this.sliderValue < 0.0F)
                {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 1.0F)
                {
                    this.sliderValue = 1.0F;
                }

                float range = this.maxValue - this.minValue;
                this.lastValue = this.minValue + (this.sliderValue * range);
                this.lastValue = Math.round(this.lastValue / this.increment) * this.increment;
                par1Minecraft.gameSettings.setOptionFloatValue(this.idFloat, this.lastValue);
                this.displayString = par1Minecraft.gameSettings.getKeyBinding(this.idFloat);

                if (_eventHandler != null)
                    _eventHandler.event(GuiEventEx.ID_VALUE_CHANGED, this.idFloat);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        if (super.mousePressed(par1Minecraft, par2, par3))
        {
            float tempSliderValue = (float)(par2 - (this.xPosition + 4)) / (float)(this.width - 8);

            if (tempSliderValue < 0.0F)
            {
                tempSliderValue = 0.0F;
            }

            if (tempSliderValue > 1.0F)
            {
                tempSliderValue = 1.0F;
            }

            float range = this.maxValue - this.minValue;
            float tempValue = this.minValue + (tempSliderValue * range);
            tempValue = Math.round(tempValue / this.increment) * this.increment;

            // For a mouse press only (before the mouse is dragged), we want a single
            // increment increase or decrease, if possible.
            if (tempValue > this.lastValue)
                this.lastValue += increment;
            else if (tempValue < this.lastValue)
                this.lastValue -= increment;

            if (this.lastValue > this.maxValue)
                this.lastValue = this.maxValue;
            else if (this.lastValue < this.minValue)
                this.lastValue = this.minValue;

            this.sliderValue = (this.lastValue - this.minValue) / range;
            par1Minecraft.gameSettings.setOptionFloatValue(this.idFloat, this.lastValue);
            this.displayString = par1Minecraft.gameSettings.getKeyBinding(this.idFloat);
            this.lastMouseX = par2;
            this.dragging = true;


            if (_eventHandler != null)
                _eventHandler.event(GuiEventEx.ID_VALUE_CHANGED, this.idFloat);

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int par1, int par2)
    {
        this.lastMouseX = -1;
        float range = this.maxValue - this.minValue;
        this.sliderValue = (this.lastValue - this.minValue) / range;  // Sync slider pos with last (actual) value
        this.dragging = false;
    }
}
