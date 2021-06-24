package lb.edu.aust.ict355l.exam2.coapNodeServer;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.Date;

// Creating the Soil Moisture Resource
public class SoilMoistureResource extends CoapResource implements GpioPinListenerDigital {
    // Creating an Instance of GpioController
    private final GpioController gpioController;
    public SoilMoistureResource() {
        // Uri Will be /sensors/soil-moisture
        super("soil-moisture");
        // setting the resource to be observable
        setObservable(true);
        // Setting Some Information about the resource
        getAttributes().setObservable();
        getAttributes().setTitle("Moisture-State");
        getAttributes().addResourceType("Observe");
        // Getting the Actual Instance of the GpioController
        gpioController = GpioFactory.getInstance();
        // Creating an Instance of the Pin that is provisioned for the Soil Moisture Sensor
        final GpioPinDigitalInput rackMoistureSensorPin =
                (GpioPinDigitalInput) gpioController.getProvisionedPin(RaspiPin.GPIO_05);
        // Adding a listener on the pin
        rackMoistureSensorPin.addListener(this); }
    // Method to handle Get Requests from the edge client
    @Override
    public void handleGET(CoapExchange exchange){
        final GpioPinDigitalInput rackMoistureSensorPin =
                (GpioPinDigitalInput) gpioController.getProvisionedPin(RaspiPin.GPIO_05);
        // Getting The state of the Pin provisioned for the moisture sensor to send it in the get request
        final PinState currentState = rackMoistureSensorPin.getState();
        System.out.println("Got a Get Request on " + new Date());
        exchange.respond(currentState.getName()); }
    // Method that notifies the Observers whenever a change has occurred on the resource
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        // Getting the changed state and notifying observers
        final PinState newState = event.getState();
        System.out.println
                ("Motion sensor state changed to " + newState + ", notifying observers at " + new Date());
        changed(); }}
