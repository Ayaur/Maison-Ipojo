package com.example.binary.follow.me;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import fr.liglab.adele.icasa.device.light.DimmerLight;

public class BinaryFollowMeImpl implements DeviceListener {

	//The name of the LOCATION property
	public static final String LOCATION_PROPERTY_NAME = "Location";
	//The name of the location for unknown value
	public static final String LOCATION_UNKNOWN = "unknown";

	// Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	//  Field for binaryLights dependency */
	private BinaryLight[] binaryLights;

	private int maxLightsToTurnOnPerRoom = 1;

	// Field for dimmerLights dependency */
	private DimmerLight[] dimmerLights;

	
	
	//####################### Bind/Unbind ###################

	public synchronized void bindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
		System.out.println("bind binary light " + binaryLight.getSerialNumber());
		binaryLight.addListener(this);
	}

	public synchronized void unbindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
		System.out.println("unbind binary light " + binaryLight.getSerialNumber());
		binaryLight.removeListener(this);
	}

	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map<Object, Object> properties) {
		System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.addListener(this);
	}

	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		System.out.println("unbind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.removeListener(this);
	}

	public synchronized void bindDimmerLight(DimmerLight dimmerLight, Map properties) {
		System.out.println("bind dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.addListener(this);
	}

	public synchronized void unbindDimmerLight(DimmerLight dimmerLight, Map properties) {
		System.out.println("unbind dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.removeListener(this);
	}
	
	
	
	//##################### Start/Stop Lifecycle #####################
	public synchronized void stop() {
		System.out.println("Component is stopping...");
		for (PresenceSensor sensor : presenceSensors)
			sensor.removeListener(this);
		for (BinaryLight sensor : binaryLights)
			sensor.removeListener(this);
		for (DimmerLight sensor : dimmerLights)
			sensor.removeListener(this);
	}

	public void start() {
		System.out.println("Component is starting...");
	}



	
	//################### List of device in one location ####################
	// Return all BinaryLight from the given location
	private synchronized List<BinaryLight> getBinaryLightFromLocation(String location) {
		List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
		for (BinaryLight binLight : binaryLights) {
			if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				binaryLightsLocation.add(binLight);
			}
		}
		return binaryLightsLocation;
	}
	
	
	private synchronized List<PresenceSensor> getPresenceSensorFromLocation(String location) {
		List<PresenceSensor> presenceSensorLocation = new ArrayList<PresenceSensor>();
		for (PresenceSensor preSensor : presenceSensors) {
			if (preSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				presenceSensorLocation.add(preSensor);
			}
		}
		return presenceSensorLocation;
	}
	
	
	private synchronized List<DimmerLight> getDimmerLightFromLocation(String location) {
		List<DimmerLight> dimmerLightLocation = new ArrayList<DimmerLight>();
		for (DimmerLight dimLight : dimmerLights) {
			if (dimLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				dimmerLightLocation.add(dimLight);
			}
		}
		return dimmerLightLocation;
	}


	
	
	
	// ###################### If a device move ####################
	/**
	 * This method is part of the DeviceListener interface and is called when a
	 * subscribed device property is modified.
	 * @param device
	 *            is the device whose property has been modified.
	 * @param propertyName
	 *            is the name of the modified property.
	 * @param oldValue
	 *            is the old value of the property
	 * @param newValue
	 *            is the new value of the property
	 */
	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
	if (device instanceof PresenceSensor ){	
		PresenceSensor changingSensor = (PresenceSensor) device;
		// check the change is related to presence sensing
		int numberLightOn = 0;
		if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
			// get the location where the sensor is:
			String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
			// if the location is known :
			if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
				// get the related binary lights
				List<BinaryLight> sameLocationLights = getBinaryLightFromLocation(detectorLocation);
				List<DimmerLight> sameLocationDimmerLigths = getDimmerLightFromLocation(detectorLocation);
				for (BinaryLight binaryLight : sameLocationLights) {
					// and switch them on/off depending on the sensed presence
					if (changingSensor.getSensedPresence()) {
						if (numberLightOn < maxLightsToTurnOnPerRoom){
							binaryLight.setPowerStatus(true);
							numberLightOn++;
						}
						else {
							binaryLight.setPowerStatus(false);
						}
					}
					else 
						binaryLight.setPowerStatus(false);
				}
				for (DimmerLight dimmerL : sameLocationDimmerLigths) {
					if (changingSensor.getSensedPresence()){
						dimmerL.setPowerLevel(1);
					}
					else {
						dimmerL.setPowerLevel(0);
					}
				}
			}
		}
	}
	
	else if (device instanceof BinaryLight){
		BinaryLight lightSensor = (BinaryLight) device;
		int numberLightOn = 0;
		// get the location where the sensor is:
		String detectorLocation = (String) lightSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
		String detectorLocationSensor;
		List<BinaryLight> sameLocationLights;
			if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
				for (PresenceSensor preSensor : presenceSensors) {
					detectorLocationSensor = (String) preSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
					sameLocationLights = getBinaryLightFromLocation(detectorLocationSensor);
					for (BinaryLight binaryLight : sameLocationLights) {
						if (preSensor.getSensedPresence()){
							if (numberLightOn < maxLightsToTurnOnPerRoom){
								binaryLight.setPowerStatus(true);
								numberLightOn++;
							}
							else {
								binaryLight.setPowerStatus(false);
							}
						}
						else {
							binaryLight.setPowerStatus(false);
						}
					}
				}
			}
		}		
		

	else if (device instanceof DimmerLight){
		DimmerLight dimmer = (DimmerLight) device;
		String detectorLocation = (String) dimmer.getPropertyValue(LOCATION_PROPERTY_NAME);
		List<DimmerLight> sameLocationDimmerLigths; //= getDimmerLightFromLocation(detectorLocation);
		String detectorLocationSensor;
		if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
		for (PresenceSensor preSensor : presenceSensors) {
			detectorLocationSensor = (String) preSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
			sameLocationDimmerLigths = getDimmerLightFromLocation(detectorLocationSensor);
			for (DimmerLight dimmerL : sameLocationDimmerLigths) {
				if (preSensor.getSensedPresence()){
					dimmerL.setPowerLevel(1);
				}
				else {
					dimmerL.setPowerLevel(0);
				}
			}

		}
		}	}
}
		
		
	
	//################ Méthod for the Interface ####################
	@Override
	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub
	}

}






//################ Trash #######################



/* #############################

//Code permettant d'éteindre ou d'allumer les lampes lrosqu'on les changes de place. 
// PB ne prend pas en compte la pièce de départ
//Par exemple si la lampe bougé est allumé, le fait qu'il n'y ait
//plus de lampes allumé dans la pièce initiale n'est pas pris en compte.


else if (device instanceof BinaryLight){
BinaryLight lightSensor = (BinaryLight) device;
boolean presence = false;
int numberLightOn = 0;
// check the change is related to presence sensing
	//if (propertyName.equals(BinaryLight.BINARY_LIGHT_POWER_STATUS)) {
	//}
	// get the location where the sensor is:
		String detectorLocation = (String) lightSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
		if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
			List<BinaryLight> sameLocationLights = getBinaryLightFromLocation(detectorLocation);
			for (PresenceSensor preSensor : presenceSensors) {
				if (preSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals(detectorLocation)) {
					presence = preSensor.getSensedPresence();
					break;
				}	
			}
			for (BinaryLight binaryLight : sameLocationLights) {
				if (presence) {
					if (numberLightOn < maxLightsToTurnOnPerRoom){
						binaryLight.setPowerStatus(true);
						numberLightOn++;
					}
					else {
						binaryLight.setPowerStatus(false);
					}
				}
				else 
					binaryLight.setPowerStatus(false);
		}
	}
}
}
####################################### */