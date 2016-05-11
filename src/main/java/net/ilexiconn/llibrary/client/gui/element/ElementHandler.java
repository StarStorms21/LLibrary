package net.ilexiconn.llibrary.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.client.ClientProxy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public enum ElementHandler {
    INSTANCE;

    private Map<GuiScreen, List<Element<?>>> elementMap = new HashMap<>();

    public <T extends GuiScreen> void addElement(T gui, Element<T> element) {
        if (this.elementMap.containsKey(gui)) {
            this.elementMap.get(gui).add(element);
        } else {
            List<Element<?>> elementList = new ArrayList<>();
            elementList.add(element);
            this.elementMap.put(gui, elementList);
        }
    }

    public <T extends GuiScreen> void removeElement(T gui, Element<T> element) {
        if (this.elementMap.containsKey(gui)) {
            this.elementMap.get(gui).remove(element);
        }
    }

    public <T extends GuiScreen> void clearElements(T gui) {
        if (this.elementMap.containsKey(gui)) {
            this.elementMap.remove(gui);
        }
    }

    public <T extends GuiScreen> boolean isElementOnTop(T gui, Element<T> element) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            float mouseX = this.getPreciseMouseX(gui);
            float mouseY = this.getPreciseMouseY(gui);
            for (Element<T> e : Lists.reverse(elementList)) {
                if (e.isVisible() && mouseX >= e.getPosX() && mouseY >= e.getPosY() && mouseX < e.getPosX() + e.getWidth() && mouseY < e.getPosY() + e.getHeight()) {
                    return element == e || (element.getParent() != null && element.getParent() == e);
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public <T extends GuiScreen> void init(T gui) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            new ArrayList<>(elementList).stream().filter(Element::isVisible).forEach(Element::init);
        }
    }

    public <T extends GuiScreen> void update(T gui) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            elementList.stream().filter(Element::isVisible).forEach(Element::update);
        }
    }

    public <T extends GuiScreen> void render(T gui, float partialTicks) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            float mouseX = this.getPreciseMouseX(gui);
            float mouseY = this.getPreciseMouseY(gui);
            elementList.stream().filter(element -> !(element instanceof WindowElement)).filter(Element::isVisible).forEach(element -> element.render(mouseX, mouseY, partialTicks));
            elementList.stream().filter(element -> element instanceof WindowElement).filter(Element::isVisible).forEach(element -> element.render(mouseX, mouseY, partialTicks));
        }
    }

    public <T extends GuiScreen> void mouseClicked(T gui, int button) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            float mouseX = this.getPreciseMouseX(gui);
            float mouseY = this.getPreciseMouseY(gui);
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible() && element.isEnabled()) {
                    if (element.mouseClicked(mouseX, mouseY, button)) {
                        return;
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> void mouseDragged(T gui, int button, long timeSinceClick) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            float mouseX = this.getPreciseMouseX(gui);
            float mouseY = this.getPreciseMouseY(gui);
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible() && element.isEnabled()) {
                    if (element.mouseDragged(mouseX, mouseY, button, timeSinceClick)) {
                        return;
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> void mouseReleased(T gui, int button) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            float mouseX = this.getPreciseMouseX(gui);
            float mouseY = this.getPreciseMouseY(gui);
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible() && element.isEnabled()) {
                    if (element.mouseReleased(mouseX, mouseY, button)) {
                        return;
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> void keyPressed(T gui, char character, int key) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible() && element.isEnabled()) {
                    if (element.keyPressed(character, key)) {
                        return;
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> float getPreciseMouseX(T gui) {
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        return (float) Mouse.getX() / scaledResolution.getScaleFactor();
    }

    public <T extends GuiScreen> float getPreciseMouseY(T gui) {
        return gui.height - (float) Mouse.getY() * gui.height / (float) gui.mc.displayHeight - 1.0F;
    }
}
