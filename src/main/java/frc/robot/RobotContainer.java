package frc.robot;

import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
// WPILib imports
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
// Local imports
import frc.robot.control.*;
import frc.robot.subsystems.drive.*;
import frc.robot.utils.DriverStationUtils;
import frc.robot.utils.ExtendedStatusSignalCollection;
import frc.robot.utils.PositionedSubsystem;
import frc.robot.utils.SysIdManager;
import frc.robot.utils.TalonFXOrchestra;
import frc.robot.utils.libraries.Elastic;
import frc.robot.utils.libraries.Elastic.Notification;
import frc.robot.utils.libraries.Elastic.NotificationLevel;
import frc.robot.utils.wrappers.SafeAlert;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;
import org.littletonrobotics.junction.networktables.LoggedNetworkBoolean;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
@SuppressWarnings({"java:S1444", "java:S1104", "java:S3010"})
public final class RobotContainer {

    public static final double ROBOT_PERIODIC = 0.02;
    public static final int CONTROL_JOYSTICK_PORT = 2;
    public static final int CONTROL_GAMEPAD_PORT = 0;

    // Min time remaining in which we can auto reset encoders if not already reset during autonomous
    // (20 - 18 = 2 seconds at the start of auto)
    public static final double FMS_AUTO_RESET_ENCODERS_MIN_TIME = 18;

    public static final ExtendedStatusSignalCollection allStatusSignalsToRefresh = new ExtendedStatusSignalCollection();

    public static Drivetrain drivetrain;

    public static TalonFXOrchestra orchestra;

    private static SafeAlert noEncoderResetAlert;

    private static final LoggedDashboardChooser<AbstractControl> driveMode = new LoggedDashboardChooser<>("Drive Mode");
    private static final LoggedNetworkBoolean encoderResetButton =
            new LoggedNetworkBoolean("Autonomous/EncoderReset", false);

    private static AbstractControl defaultControl;
    private static AbstractControl testControl;

    private RobotContainer() {
        initialize();
    }

    /** The container for the robot. Contains subsystems, IO devices, and commands. */
    private static void initialize() {

        RobotController.setBrownoutVoltage(5.75);

        noEncoderResetAlert = new SafeAlert("Encoders not reset!", AlertType.kError);

        orchestra = new TalonFXOrchestra();

        drivetrain = new Drivetrain();

        // Sets up Control scheme chooser
        addControls(new JoystickControls(CONTROL_JOYSTICK_PORT));

        configureTriggers();

        noEncoderResetAlert.set(true);
    }

    public static void teleopInit() {
        DriverStationUtils.teleopInit();
    }

    public static void disabledInit() {}

    public static void autonomousInit() {
        // FMS only reset encoders when enabling autonomous for the first time if not already reset (safety measure)
        // Time limit is so that if the robot restarts during the auto we don't want to reset encoders
        // Don't want to autoreset in teleop because motors keep their positions if you don't power cycle
        if (DriverStation.isFMSAttached()
                && DriverStation.getMatchTime() >= FMS_AUTO_RESET_ENCODERS_MIN_TIME
                && noEncoderResetAlert.get()) {
            resetEncoders();
        }
    }

    public static void disabledExit() {
        // For the generic any enable only reset if not connected to FMS
        // because we don't want to reset encoders if robot restarts in the middle of the match
        // since the motors keep their positions if you don't power cycle
        // for FMS handling see autonomousInit
        // Reset encoders when enabling if not already reset (safety measure)
        if (!DriverStation.isFMSAttached() && noEncoderResetAlert.get()) {
            resetEncoders();
        }
    }

    private static void configureTriggers() {
        new Trigger(encoderResetButton)
                .onTrue(Commands.runOnce(() -> {
                            encoderResetButton.set(false);
                            resetEncoders();
                        })
                        .ignoringDisable(true));
    }
    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public static Command getAutonomousCommand() {
        if (SysIdManager.getProvider().isEnabled()) {
            return SysIdManager.getProvider().createCommand();
        }
        return null;
    }

    public static void robotPeriodic() {
        allStatusSignalsToRefresh.refreshAll();
        PositionedSubsystem.PositionedSubsystemManager.getInstance().update();

        drivetrain.move(getControl().getForward(), getControl().getTurn());
    }

    public static void simulationPeriodic() {}

    public static void resetEncoders() {
        PositionedSubsystem.PositionedSubsystemManager.getInstance().resetAll();

        noEncoderResetAlert.set(false);
        Elastic.sendNotification(
                new Notification(NotificationLevel.INFO, "Encoders reset!", "Successfully reset encoders."));
    }

    /**
     * Initializes the control object
     *
     * @param defaultControl the first term will always be the default control object
     * @param controls any other control objects you want to initialize
     */
    public static void addControls(AbstractControl defaultControl, AbstractControl... controls) {
        RobotContainer.defaultControl = defaultControl;

        // Sets up drive mode options
        driveMode.addDefaultOption(defaultControl.toDisplayName(), defaultControl);
        for (AbstractControl abstractControl : controls) {
            driveMode.addOption(abstractControl.toDisplayName(), abstractControl);
        }
    }

    public static void setTestControl(AbstractControl testControl) {
        if (Constants.RobotState.getMode() != Constants.RobotState.Mode.TEST) return;
        RobotContainer.testControl = testControl;
    }

    public static AbstractControl getControl() {
        if (Constants.RobotState.getMode() == Constants.RobotState.Mode.TEST) {
            if (testControl == null) {
                throw new IllegalStateException("Test control is not set!");
            }
            return testControl;
        }

        if (driveMode.get() == null) return defaultControl;
        return driveMode.get();
    }

    /** frees up all hardware allocations */
    public static void close() {
        drivetrain.close();
    }
}
