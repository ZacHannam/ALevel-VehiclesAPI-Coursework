package uk.hannam.vehiclesapi.vehicles.components.frame;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import uk.hannam.vehiclesapi.utils.Point2D;

public interface Frame {

	double getLongestBodyLength();
	double getShortestBodyLength();
	Point2D[] getFrameCorners();
	void setHeight(double paramHeight);
	double getHeight();
	void setLength(double paramLength);
	double getLength();
	void setWidth(double paramWidth);
	double getWidth();
	void setOffset(Vector paramOffset);
	Vector getOffset();
	void setYawOffset(double paramYaw);
	double getYawOffset();
	Location getLocation();
}
