package lb.edu.aust.ict355l.exam2.coapNodeServer;

import com.pi4j.io.gpio.*;
import lb.edu.aust.ict355l.raspberrypi.gpio.emulation.GUISimulatedGpioProvider;
import org.eclipse.californium.core.CoapServer;
// Main Class That starts the Coap Node Server
public class Main {
    public static void main(String[] args) {
        // Setting the GPIO Provider to be the Simulator
        GpioFactory.setDefaultProvider
                (new GUISimulatedGpioProvider(() -> GpioFactory.getInstance().shutdown()));
        // Getting an instance of the GPIO Provider
        final GpioController gpioController = GpioFactory.getInstance();
        // Provisioning the pins for their corresponding Sensors
        gpioController.provisionDigitalInputPin
                (RaspiPin.GPIO_05, "Moisture Senor", PinPullResistance.PULL_DOWN);
        gpioController.provisionDigitalOutputPin
                (RaspiPin.GPIO_06, "UV Lights", PinState.LOW);
        gpioController.provisionDigitalOutputPin
                (RaspiPin.GPIO_00, "Water Pump", PinState.LOW);
        // Creating am instance of the Coap Server
        final CoapServer server = new CoapServer(5683);
        // Creating the main resource that holds the other child resources
        final SensorsResource sensorsResource = new SensorsResource();
        // adding the resource to the server
        server.add(sensorsResource);
        System.out.println("Registered the sensors resource along with all of its children.");
        System.out.println("CoAP server starting on UDP protocol");
        // Starting the server
        server.start();
    }}


