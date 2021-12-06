package uk.hannam.vehiclesapi.vehicles.components.seat.driverseat;

import uk.hannam.vehiclesapi.vehicles.components.seat.Seat;

public interface DriverSeat extends Seat {

	void setShowSteeringDisplay(boolean paramShowSteeringDisplay);
	boolean isShowSteeringDisplay();
	void setNumberOfCharacters(int paramNumberOfCharacters);
	int getNumberOfCharacters();
	void setMaxSteeringAngle(float paramMaxSteeringAngle);
	float getMaxSteeringAngle();
	void setSteeringAngleRange(float paramSteeringAngleRange);
	float getSteeringAngleRange();

}