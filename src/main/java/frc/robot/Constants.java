// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.utils.AutoLogLevel;
import frc.robot.utils.SysIdManager;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    public static final int TEAM_NUMBER = 6731;

    public static final double GRAVITY = 9.81; // m/s^2

    private Constants() {}

    public static final class Control {

        // Sensitivity for speed meter
        public static final double DIRECTIONAL_SPEED_METER_LOW = 0.25;
        public static final double SPIN_SPEED_METER_LOW = 0.5;

        // Sensitivies for directional controls (XY) and spin (theta)
        public static final double JOYSTICK_XY_SENSITIVITY = 1;
        public static final double JOYSTICK_SPIN_SENSITIVITY = 1;
        public static final double JOYSTICK_XY_THRESHOLD = 0.25;
        public static final double JOYSTICK_SPIN_THRESHOLD = 0.3;
        public static final double JOYSTICK_XY_EXPONENT = 2;
        public static final double JOYSTICK_SPIN_EXPONENT = 2;

        public static final double SPIN_KP = 4;
        public static final double SPIN_KD = 0.28;
        public static final double JOYSTICK_ABSOLUTE_SPIN_THRESHOLD = 0.9;
        public static final double JOYSTICK_ABSOLUTE_SPIN_THRESHOLD_PACMAN = 0.2;

        public static final double RELATIVE_SPIN_MAX_ANGULAR_VELOCITY = Units.degreesToRadians(140);

        private Control() {}
    }

    public static final class Frame {

        /**
         * Distance between wheels (width aka between left and right and length aka between front and
         * back). Used for calculating wheel locations on the robot
         */
        public static final double ROBOT_WHEEL_DISTANCE_WIDTH = 0.514350;

        public static final double ROBOT_WHEEL_DISTANCE_LENGTH = 0.514350;

        public static final double FRAME_WIDTH = Inches.of(27).in(Meters);
        public static final double FRAME_LENGTH = Inches.of(27).in(Meters);

        public static final double FRAME_WITH_BUMPER_WIDTH =
                FRAME_WIDTH + Inches.of(8.264).in(Meters);
        public static final double FRAME_WITH_BUMPER_LENGTH =
                FRAME_LENGTH + Inches.of(7.5).in(Meters);

        public static final double ROBOT_MASS_KG = 60.668; // kg
        public static final double ROBOT_MOI = 6.4482549519; // kg*m^2

        private Frame() {}
    }

    public final class RobotState {

        public static final boolean MOTOR_LOGGING_ENABLED = false;

        public static final AutoLogLevel.Level AUTO_LOG_LEVEL = getAutoLogLevel();

        /**
         * <p>
         * Enable NT and Advantage Scope for unit tests.
         * </p>
         * <p>
         * WARNING! ONLY ENABLE THIS IF RUNNING A SINGLE UNIT TEST
         * </p>
         * <p>
         * RUNNING TESTS IN PARALLEL IS NOT SUPPORTED
         * </p>
         *
         * Example for running just one test: {@code ./gradlew test --tests "INSERT_TEST_NAME_HERE"}
         */
        public static final boolean UNIT_TESTS_ENABLE_ADVANTAGE_SCOPE = false;

        private static boolean runningAsUnitTest = false;

        private RobotState() {}

        public static void setRunningAsUnitTest() {
            runningAsUnitTest = true;
        }

        public static Mode getMode() {
            if (RobotBase.isReal()) return Mode.REAL;
            if (runningAsUnitTest) return Mode.TEST;
            return Mode.SIM; // change to REPLAY when replaying
        }

        private static AutoLogLevel.Level getAutoLogLevel() {
            if (RobotBase.isReal()) {
                return SysIdManager.getProvider().isEnabled() ? AutoLogLevel.Level.SYSID : AutoLogLevel.Level.REAL;
            } else {
                return AutoLogLevel.Level.SIM;
            }
        }

        public enum Mode {
            REAL(true),
            SIM(true),
            REPLAY(false),
            TEST(false);

            final boolean realtime;

            Mode(boolean realtime) {
                this.realtime = realtime;
            }

            public boolean isRealtime() {
                return realtime;
            }
        }
    }
}
