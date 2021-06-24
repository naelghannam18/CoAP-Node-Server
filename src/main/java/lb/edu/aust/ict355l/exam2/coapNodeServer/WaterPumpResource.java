package lb.edu.aust.ict355l.exam2.coapNodeServer;

import com.pi4j.io.gpio.*;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import java.nio.charset.StandardCharsets;

// water pump resource
public class WaterPumpResource extends CoapResource {
    private final GpioController gpioController;
    public WaterPumpResource() {
        super("water-pump"); // Coap URI: /sensors/water-pump
        gpioController = GpioFactory.getInstance();
    }
    // method that handles GET request by responding with the current states of the pins
    @Override
    public void handleGET(CoapExchange exchange) {
        final GpioPinDigitalOutput waterPumpPin = (GpioPinDigitalOutput) gpioController.getProvisionedPin
                (RaspiPin.GPIO_00);
        final PinState currentState = waterPumpPin.getState();
        exchange.respond(currentState.getName());
    }
    // PUT Method That changes the state of the water pump pin according to the PUT request received
    @Override
    public void handlePUT(CoapExchange exchange) {
        // Getting the payload of the the Put Message
        final String requestedState = new String(exchange.getRequestPayload(), StandardCharsets.UTF_8);
        // Converting it to a Pin state
        final PinState pinState = PinState.valueOf(requestedState);
        // Creating an Instance of the water pump Ping
        final GpioPinDigitalOutput waterPumpPin = (GpioPinDigitalOutput) gpioController.getProvisionedPin
                (RaspiPin.GPIO_00);
        waterPumpPin.setState(pinState); // Changing the state of the water pump pin per put request payload
        exchange.respond(CoAP.ResponseCode._UNKNOWN_SUCCESS_CODE); // Responding with a Success Code
    }
}


