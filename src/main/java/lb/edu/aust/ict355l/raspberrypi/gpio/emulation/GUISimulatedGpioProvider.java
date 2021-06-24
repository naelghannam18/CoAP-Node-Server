/*
 * **************************************************************
 * ORGANIZATION: American University of Science & Technology
 * BRANCH: Achrafieh, Lebanon
 * FACULTY: Faculty of Arts and Sciences
 * DEPARTMENT: Department of Information & Communications Technology
 *
 * This file is part of the ICT355 Lab.
 * For more information, please visit the university's website: https://aust.edu.lb.
 * **************************************************************
 * %%
 * Copyright ©2020-2021 American University of Science & Technology - Department of Information & Communications Technology.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lb.edu.aust.ict355l.raspberrypi.gpio.emulation;


import com.pi4j.io.gpio.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2/12/2021
 *
 *
 * <p><b>IMPORTANT NOTE:</b> Please don't judge,
 * I know it's spaghetti code, but I only worked on it for a couple of hours and
 * I wanted to have something that at least works.
 * </p>
 *
 * @author Julien Saab, jps00001@students.aust.edu.lb
 */
public class GUISimulatedGpioProvider extends SimulatedGpioProvider {

    private final JFrame mainFrame;
    private final JPanel mainPanel;

    private final List<Cell> cells = Arrays.asList(
            new Cell("5V"),
            new Cell("5V"),
            new Cell("GND"),
            new Cell("GPIO 14", RaspiPin.GPIO_15, "OUT", "GPIO 14 (UART TX), Pin 15"),
            new Cell("GPIO 15", RaspiPin.GPIO_16, "OUT", "GPIO 15 (UART TX), Pin 16"),
            new Cell("GPIO 18", RaspiPin.GPIO_01, "OUT", "GPIO 18 (PCM CLK), Pin 01"),
            new Cell("GND"),
            new Cell("GPIO 23", RaspiPin.GPIO_04, "OUT", "GPIO 23, Pin 04"),
            new Cell("GPIO 24", RaspiPin.GPIO_05, "OUT", "GPIO 24, Pin 05"),
            new Cell("GND"),
            new Cell("GPIO 25", RaspiPin.GPIO_06, "OUT", "GPIO 25, Pin 06"),
            new Cell("GPIO 08", RaspiPin.GPIO_10, "OUT", "GPIO 08 (SPI0 CE0), Pin 10"),
            new Cell("GPIO 07", RaspiPin.GPIO_11, "OUT", "GPIO 07 (SPI0 CE1), Pin 11"),
            new Cell("GPIO 01", RaspiPin.GPIO_31, "OUT", "GPIO 01 (EEPROM SCL), Pin 31"),
            new Cell("GND"),
            new Cell("GPIO 12", RaspiPin.GPIO_26, "OUT", "GPIO 12 (PWM0), Pin 26"),
            new Cell("GND"),
            new Cell("GPIO 16", RaspiPin.GPIO_27, "OUT", "GPIO 16, Pin 27"),
            new Cell("GPIO 20", RaspiPin.GPIO_28, "OUT", "GPIO 20 (PCM DIN), Pin 28"),
            new Cell("GPIO 21", RaspiPin.GPIO_29, "OUT", "GPIO 21 (PCM DOUT), Pin 29"),
            new Cell("3v3"),
            new Cell("GPIO 02", RaspiPin.GPIO_08, "OUT", "GPIO 02 (I2C1 SDA), Pin 08"),
            new Cell("GPIO 03", RaspiPin.GPIO_09, "OUT", "GPIO 03 (I2C1 SCL), Pin 09"),
            new Cell("GPIO 04", RaspiPin.GPIO_07, "OUT", "GPIO 04 (GPCLK0), Pin 07"),
            new Cell("GND"),
            new Cell("GPIO 17", RaspiPin.GPIO_00, "OUT", "GPIO 17, Pin 00"),
            new Cell("GPIO 27", RaspiPin.GPIO_02, "OUT", "GPIO 27, Pin 02"),
            new Cell("GPIO 22", RaspiPin.GPIO_03, "OUT", "GPIO 22, Pin 03"),
            new Cell("3v3"),
            new Cell("GPIO 10", RaspiPin.GPIO_12, "OUT", "GPIO 10 (SPI0 MOSI), Pin 12"),
            new Cell("GPIO 09", RaspiPin.GPIO_13, "OUT", "GPIO 09 (SPI0 MISO), Pin 13"),
            new Cell("GPIO 11", RaspiPin.GPIO_14, "OUT", "GPIO 11 (SPI0 SCLK), Pin 14"),
            new Cell("GND"),
            new Cell("GPIO 00", RaspiPin.GPIO_30, "OUT", "GPIO 00 (EEPROM SDA), Pin 30"),
            new Cell("GPIO 05", RaspiPin.GPIO_21, "OUT", "GPIO 05, Pin 21"),
            new Cell("GPIO 06", RaspiPin.GPIO_22, "OUT", "GPIO 06, Pin 22"),
            new Cell("GPIO 13", RaspiPin.GPIO_23, "OUT", "GPIO 13 (PWM1), Pin 23"),
            new Cell("GPIO 19", RaspiPin.GPIO_24, "OUT", "GPIO 19 (PCM FS), Pin 24"),
            new Cell("GPIO 26", RaspiPin.GPIO_25, "OUT", "GPIO 26, Pin 25"),
            new Cell("GND")
    );

    private final Map<Pin, GPIOButtonPanel> buttons;

    public GUISimulatedGpioProvider(ProviderShutdownListener providerShutdownListener) {

        this.mainFrame = new JFrame("GUI Simulated GPIO");
        this.buttons = new HashMap<>();

        this.mainFrame.setVisible(true);
        this.mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.mainFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                providerShutdownListener.onProviderShutdown();
            }
        });


        JPanel panel = new JPanel(new BorderLayout());

        final GridLayout gridLayout = new GridLayout(2, 20);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        this.mainPanel = new JPanel(gridLayout);

        panel.add(mainPanel, BorderLayout.CENTER);

        final JLabel label = new JLabel("Created by: Julien Saab, jps00001@students.aust.edu.lb", SwingConstants.RIGHT);
        label.setEnabled(false);
        panel.add(label, BorderLayout.SOUTH);

        for (Cell cell : cells) {

            updateInitialState(cell);

            final JComponent component = createComponent(cell);

            if (cell.hasPin() && component instanceof GPIOButtonPanel) {
                buttons.put(cell.getPin(), (GPIOButtonPanel) component);
            }

            this.mainPanel.add(component);
        }


        this.mainFrame.add(panel);
        this.mainFrame.pack();

        new Thread(() -> {

            try {
                Thread.sleep(750);
            } catch (InterruptedException ignored) {}

            mainFrame.pack();
        }).start();
    }

    @Override
    public void setState(Pin pin, PinState state) {

        super.setState(pin, state);
        findPinAndUpdate(pin);
    }

    @Override
    public void setMode(Pin pin, PinMode mode) {

        super.setMode(pin, mode);
        findPinAndUpdate(pin);
    }

    @Override
    public void setValue(Pin pin, double value) {

        super.setValue(pin, value);
        findPinAndUpdate(pin);
    }

    @Override
    public void setPwm(Pin pin, int value) {

        super.setPwm(pin, value);
        findPinAndUpdate(pin);
    }

    @Override
    public void setPwmRange(Pin pin, int range) {

        super.setPwmRange(pin, range);
        findPinAndUpdate(pin);
    }

    @Override
    public void setPullResistance(Pin pin, PinPullResistance resistance) {

        super.setPullResistance(pin, resistance);
        findPinAndUpdate(pin);
    }

    private void updateInitialState(final Cell cell) {

        if (cell.hasPin()) {
            updateInitialState(cell.getPin());
        }
    }

    private PinState updateInitialState(final Pin pin) {

        if (!isPinEnabled(pin)) {
            return null;
        }

        PinState state = getState(pin);
        final PinMode mode = getMode(pin);
        final PinDirection direction = mode.getDirection();

        if (state == null && direction == PinDirection.IN) {
            final PinPullResistance pullResistance = getPullResistance(pin);
            if (pullResistance == PinPullResistance.PULL_DOWN) {
                state = PinState.LOW;
            } else {
                state = PinState.HIGH;
            }

            super.setState(pin, state);

            return state;
        }

        return state;
    }

    private JComponent createComponent(Cell cell) {

        if (cell.hasPin()) {
            return createButton(cell);
        } else {
            return createLabel(cell);
        }
    }

    private JLabel createLabel(Cell cell) {

        return new JLabel(cell.getDisplayName(), SwingConstants.CENTER);
    }

    private boolean isPinEnabled(Pin pin) {

        final boolean enabled = hasPin(pin);

        if (enabled) {
            try {
                getState(pin);
                getMode(pin);
            } catch (Exception ignored) {
                return false;
            }
        }

        return enabled;
    }

    private GPIOButtonPanel updateButton(Cell cell, GPIOButtonPanel button) {

        final Pin pin = cell.getPin();

        button.setPinNumber(pin.getAddress());

        final boolean enabled = isPinEnabled(pin);

        if (enabled) {
            final PinState state = updateInitialState(pin);
            final PinMode mode = getMode(pin);
            final PinDirection direction = mode.getDirection();


            button.setText(String.format("<html>%s</html>", cell.getDisplayName()/*, direction, state*/));
            final boolean isInput = direction == PinDirection.IN;

            button.setDirection(direction);
            button.setState(state);

            button.setEnabled(true);

            if (isInput) {
                button.setStateBackground(null);

                if (state == PinState.HIGH) {
                    button.setStateBackground(Color.GREEN);
                } else {
                    button.setStateBackground(Color.RED);
                }
            } else {

                button.setStateBackground(Color.DARK_GRAY);

                if (state == PinState.HIGH) {
                    button.setStateBackground(Color.GREEN);
                } else {
                    button.setStateBackground(Color.RED);
                }
            }

        } else {
            button.setText(String.format("<html>%s<br></html>", cell.getDisplayName()));
            button.setStateBackground(Color.LIGHT_GRAY);
            button.setEnabled(false);
        }


        return button;
    }

    private void findPinAndUpdate(Pin pin) {

        for (Cell cell : cells) {

            if (cell.hasPin() && cell.getPin() == pin) {
                final GPIOButtonPanel jButton = buttons.get(pin);
                updateButton(cell, jButton);
                return;
            }
        }
    }

    private GPIOButtonPanel createButton(final Cell cell) {

        final GPIOButtonPanel button = new GPIOButtonPanel();
        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (!button.isEnabled()) {
                    return;
                }

                handleStateSwitch(cell);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (!button.isEnabled()) {
                    return;
                }

                handleStateSwitch(cell);
            }
        });
        return updateButton(cell, button);
    }

    private void handleStateSwitch(Cell cell) {

        final Pin pin = cell.getPin();
        final PinMode mode = getMode(pin);
        final PinDirection direction = mode.getDirection();

        if (direction == PinDirection.OUT) {
            return;
        }

        final PinState state = getState(pin);
        setState(pin, PinState.getInverseState(state));
    }

    /**
     * Created on 2/13/2021
     *
     * @author Julien Saab, jps00001@students.aust.edu.lb
     */
    public interface ProviderShutdownListener {

        void onProviderShutdown();
    }

    private static class Cell {

        private final String displayName;
        private String displayValue;
        private String description;
        private Pin pin;

        public Cell(String displayName) {

            this.displayName = displayName;
        }

        public Cell(String displayName, Pin pin) {

            this(displayName);
            this.pin = pin;
        }

        public Cell(String displayName, Pin pin, String displayValue, String description) {

            this.displayName = displayName;
            this.displayValue = displayValue;
            this.description = description;
            this.pin = pin;
        }

        public String getDisplayName() {

            return displayName;
        }

        public String getDisplayValue() {

            return displayValue;
        }

        public void setDisplayValue(String displayValue) {

            this.displayValue = displayValue;
        }

        public Pin getPin() {

            return pin;
        }

        public void setPin(Pin pin) {

            this.pin = pin;
        }

        public boolean hasPin() {

            return pin != null;
        }

        public String getDescription() {

            return description;
        }

        public void setDescription(String description) {

            this.description = description;
        }
    }

    private static class GPIOButtonPanel extends JPanel {

        private final JPanel statePanel;
        private final JButton gpioButton;
        private final JLabel gpioLabel;
        private final JLabel pinLabel;
        private final JLabel typeLabel;
        private PinDirection direction;
        private PinState state;

        public GPIOButtonPanel() {

            setLayout(new BorderLayout());

            this.statePanel = new JPanel();
            this.gpioButton = new JButton();
            this.gpioLabel = new JLabel("", SwingConstants.CENTER);
            this.pinLabel = new JLabel("Pin ?", SwingConstants.CENTER);
            this.typeLabel = new JLabel("DISABLED", SwingConstants.CENTER);

            this.add(gpioButton, BorderLayout.CENTER);
            this.add(statePanel, BorderLayout.NORTH);


            final JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.add(typeLabel, BorderLayout.NORTH);
            southPanel.add(pinLabel, BorderLayout.SOUTH);

            this.add(southPanel, BorderLayout.SOUTH);

            //		this.gpioButton.setOpaque(true);
            this.gpioButton.setBorderPainted(true);
            this.pinLabel.setEnabled(false);
            this.typeLabel.setEnabled(false);
            this.setBorder(new LineBorder(Color.BLACK));
        }

        public void setPinNumber(int pinNumber) {

            this.pinLabel.setText(String.format("Pin %02d", pinNumber));
        }


        public void setText(String text) {

            this.gpioButton.setText(text);
            this.gpioLabel.setText(text);
        }

        public void setDirection(PinDirection direction) {

            this.direction = direction;

            if (direction == PinDirection.IN) {
                remove(gpioLabel);
                add(gpioButton, BorderLayout.CENTER);
            } else if (direction == PinDirection.OUT) {
                remove(gpioButton);
                add(gpioLabel, BorderLayout.CENTER);
                //			setBackground(Color.GRAY);
            } else {
                setEnabled(false);
            }

            update();
        }

        @Override
        public void setEnabled(boolean enabled) {

            if (!enabled) {
                remove(gpioButton);
                add(gpioLabel, BorderLayout.CENTER);
            }

            super.setEnabled(enabled);
        }

        public void setState(PinState state) {

            this.state = state;

            update();
        }

        public void setStateBackground(Color bg) {

            if (direction == null) {
                return;
            }

            this.statePanel.setBackground(bg);
        }

        public void setTextForeground(Color fg) {

            this.gpioButton.setForeground(fg);
        }

        @Override
        public synchronized void addMouseListener(MouseListener l) {

            this.gpioButton.addMouseListener(l);
        }

        protected boolean isAdded(Component target) {

            final Component[] components = getComponents();
            for (Component component : components) {
                if (component == target) {
                    return true;
                }
            }
            return false;
        }

        private void update() {

            String text = "";

            if (direction != null) {
                text += direction;
            } else {
                text = "DISABLED";
            }

            if (state != null) {
                text += "=";
                text += state;
            }

            typeLabel.setText(text);
        }
    }
}
