package lb.edu.aust.ict355l.exam2.coapNodeServer;

import com.pi4j.io.gpio.*;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.nio.charset.StandardCharsets;
// UV-Lights Resource
public class UVLightResource extends CoapResource {
    private final GpioController gpioController;
    public UVLightResource() {
        super("uv-light");
        setObservable(true);
        gpioController = GpioFactory.getInstance(); }
    // Handling GET Request and responding with current state of pin
    @Override
    public void handleGET(CoapExchange exchange) {
        final GpioPinDigitalOutput uvLightPin = (GpioPinDigitalOutput) gpioController.getProvisionedPin(RaspiPin.GPIO_06);
        final PinState currentState = uvLightPin.getState();
        exchange.respond(currentState.getName()); }
    // Handling PUT Requests and changing pin states accordingly
    @Override
    public void handlePUT(CoapExchange exchange) {
        final String requestedState = new String(exchange.getRequestPayload(), StandardCharsets.UTF_8);
        final PinState pinState = PinState.valueOf(requestedState);
        final GpioPinDigitalOutput uvLightPin = (GpioPinDigitalOutput) gpioController.getProvisionedPin(RaspiPin.GPIO_06);
        uvLightPin.setState(pinState);
        exchange.respond(CoAP.ResponseCode._UNKNOWN_SUCCESS_CODE); }}
