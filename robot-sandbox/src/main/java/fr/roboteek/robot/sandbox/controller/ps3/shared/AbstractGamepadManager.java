package fr.roboteek.robot.sandbox.controller.ps3.shared;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGamepadManager<L extends GamepadListener> {

    protected List<L> listeners = new ArrayList<>();

    public abstract void start();

    public void addListener(L listener) {
        listeners.add(listener);
    }
}
