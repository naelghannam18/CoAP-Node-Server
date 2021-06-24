package lb.edu.aust.ict355l.exam2.coapNodeServer;

import org.eclipse.californium.core.CoapResource;

// Main resource that holds other Resources as children
public class SensorsResource extends CoapResource {
    // Creating constructor
    public SensorsResource() {
        //Main coap URI will start with /sensors
        super("sensors");
        // Adding Sub-resources
        add(new SoilMoistureResource());
        add(new UVLightResource());
        add(new WaterPumpResource());
    }
}


