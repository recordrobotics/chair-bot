package frc.robot.subsystems.drive;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotMap;

@SuppressWarnings({"PMD.SystemPrintln", "PMD.FieldNamingConventions"})
public class Drivetrain extends SubsystemBase {

    SparkMax leftLeader;
    SparkMax leftFollower;
    SparkMax rightLeader;
    SparkMax rightFollower;

    private DifferentialDrive _differentialDrive;

    public Drivetrain() {
        leftLeader = new SparkMax(RobotMap.DriveTrain.LEFT_FRONT_MOTOR_PORT, SparkMax.MotorType.kBrushless);
        leftFollower = new SparkMax(RobotMap.DriveTrain.LEFT_BACK_MOTOR_PORT, SparkMax.MotorType.kBrushless);
        rightLeader = new SparkMax(RobotMap.DriveTrain.RIGHT_FRONT_MOTOR_PORT, SparkMax.MotorType.kBrushless);
        rightFollower = new SparkMax(RobotMap.DriveTrain.RIGHT_BACK_MOTOR_PORT, SparkMax.MotorType.kBrushless);

        SparkMaxConfig leftFollowerConfig = new SparkMaxConfig();
        leftFollowerConfig.follow(leftLeader);
        leftFollower.configure(leftFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        SparkMaxConfig rightFollowerConfig = new SparkMaxConfig();
        rightFollowerConfig.follow(rightLeader);
        rightFollower.configure(rightFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        leftLeader.set(0);
        rightLeader.set(0);

        _differentialDrive = new DifferentialDrive(leftLeader, rightLeader);

        SparkMaxConfig rightLeaderConfig = new SparkMaxConfig();
        rightLeader.configure(rightLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void move(double forward, double turn) { // both measured in [-1, 1]
        _differentialDrive.arcadeDrive(-forward, turn);
    }

    public void close() {
        leftLeader.close();
        leftFollower.close();
        rightLeader.close();
        rightFollower.close();
    }
}
