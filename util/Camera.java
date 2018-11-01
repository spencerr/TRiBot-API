package scripts.api.util;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;

import java.awt.*;

/**
 * Created by Spencer on 8/13/2017.
 */
public class Camera {

    public static int tileToAngle(Positionable tile) {
        return 100 - (Player.getPosition().distanceTo(tile) * 4);
    }

    private static void adjustCamera(int angle, int degrees, int i) {
        int yMovement = (int) ((Camera.getCameraAngle() - angle) * General.randomDouble(0.48, 0.52));
        int currentRotation = getCameraRotation();

        int provisional = degrees - currentRotation;
        int turn = provisional <= -180 ? (provisional + 360) : provisional > 180 ? (provisional - 360) : provisional;
        int xMovement = 3 * turn;
        if (Math.abs(turn) >= 5) {
            if (Options.isMouseCameraOn() || Options.setMouseCamera(true)) {
                Point pos = Mouse.getPos();

                int xDragStart = pos.x;
                int yDragStart = pos.y;

                /*int xDragStart;
                if (xMovement > 0) xDragStart = pos.x - xMovement >= 0 ? pos.x : xMovement + General.random(10, 100);
                else xDragStart = pos.x - xMovement <= Screen.getViewport().getWidth() ? pos.x : (int) (Screen.getViewport().getWidth() + xMovement - General.random(10, 100));

                int yDragStart;
                if (yMovement > 0) yDragStart = pos.y + yMovement <= Screen.getViewport().getHeight() ? pos.y : (int) (Screen.getViewport().getHeight() - yMovement) - General.random(10, 100);
                else yDragStart = pos.y + yMovement > 0 ? pos.y : -yMovement + General.random(10, 100);*/


                int xDragEnd = xDragStart - xMovement;// + General.random(-10, 10);
                int yDragEnd = yDragStart + yMovement;// + General.random(-10, 10);

                General.println("Drag Start: " + xDragStart + ", " + yDragStart);
                General.println("Drag End: " + xDragEnd + ", " + yDragEnd);

                //Mouse.drag(new Point(xDragStart, yDragStart), new Point(xDragEnd, yDragEnd), 2);
                Mouse.move(new Point(xDragStart, yDragStart));
                Mouse.sendPress(new Point(xDragStart, yDragStart), 2);

                Mouse.move(new Point(xDragEnd, yDragEnd));
                Mouse.sendRelease(new Point(xDragEnd, yDragEnd), 2);

                if(i <= 3) adjustCamera(angle, degrees, ++i);
            }
        }
    }

    public static void quickFocus(Positionable tile) {
        int angle = tileToAngle(tile);
        int degrees = org.tribot.api2007.Camera.getTileAngle(tile);
        adjustCamera(angle, degrees, 4);
    }

    public static void turnToTile(Positionable tile) {
        int angle = tileToAngle(tile);
        int degrees = org.tribot.api2007.Camera.getTileAngle(tile);
        adjustCamera(angle, degrees, 0);
    }

    public static void setCameraRotation(int degrees) {
        adjustCamera(Camera.getCameraAngle(), degrees, 0);
    }

    public static void setCameraAngle(int angle) {
        adjustCamera(angle, Camera.getCameraRotation(), 0);
    }

    private static org.tribot.api2007.Camera.ROTATION_METHOD getRotationMethod() {
        return org.tribot.api2007.Camera.getRotationMethod();
    }

    private static int getCameraRotation() {
        return org.tribot.api2007.Camera.getCameraRotation();
    }

    private static int getCameraAngle() {
        return org.tribot.api2007.Camera.getCameraAngle();
    }

}
