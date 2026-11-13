package frc.robot.control;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.utils.SimpleMath;

@SuppressWarnings({"java:S109"})
public class JoystickControls implements AbstractControl {

    private Joystick joystick;

    public JoystickControls(int joystickPort) {
        joystick = new Joystick(joystickPort);
    }

    public Double getSpeedLevel() {
        // Remaps speed meter from -1 -> 1 to 0 -> 1, then returns
        double speed = SimpleMath.remap(joystick.getRawAxis(3), 1, -1, 0, 1);

        return speed;
    }

    @Override
    public void update() {}

    @Override
    public double getForward() {
        return -joystick.getY() * getSpeedLevel();
    }

    @Override
    public double getTurn() { // positive to right
        return joystick.getX() * getSpeedLevel();
    }

    @Override
    public boolean hasUserInput() {
        // ignores speed level BUT WHY???
        return AbstractControl.hasUserInput(joystick, 3);
    }

    @Override
    public String toDisplayName() {
        return "Joystick";
    }
}
