package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Module;

public class DrivetrainSubsystem extends SubsystemBase {
    private static DrivetrainSubsystem instance = null;

    double wheelBase = 31.625 * 0.0254;
    // Locations for the swerve drive modules relative to the robot center.
    Translation2d m_frontLeftLocation = new Translation2d(wheelBase, wheelBase);
    Translation2d m_frontRightLocation = new Translation2d(wheelBase, -wheelBase);
    Translation2d m_backLeftLocation = new Translation2d(-wheelBase, wheelBase);
    Translation2d m_backRightLocation = new Translation2d(-wheelBase, -wheelBase);

    // Creating my kinematics object using the module locations
    SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
            m_frontLeftLocation,
            m_frontRightLocation,
            m_backLeftLocation,
            m_backRightLocation);

    Joystick js0 = new Joystick(0);

    Module leftModule = new Module("FrontLeft", 7, 8, 14);
    Module rightModule = new Module("FrontRight", 2, 1, 11);
    Module backRightModule = new Module("BackRight", 4, 3, 12);
    Module backLeftModule = new Module("BackLeft", 6, 5, 13);

    ShuffleboardTab drivetrainTab;

    public DrivetrainSubsystem() {
    }

    // private DrivetrainSubsystem() {
    // drivetrainTab = Shuffleboard.getTab("drivetrain");

    // }

    // public static DrivetrainSubsystem getInstance() {
    // if (instance == null) {
    // instance = new DrivetrainSubsystem();
    // }
    // return instance;
    // }

    // public ShuffleboardTab getTab() {
    // return drivetrainTab;
    // }

    // this is alled every loop of the scheduler (~20ms)
    @Override
    public void periodic() {
        DriveWithJoystick(js0);
    }

    public static double convertTicksToDegrees(double ticks) {
        double degrees = ticks * (1.0 / 2048.0) * (1.0 / (150 / 7)) * (360.0 / 1.0);
        return degrees;
    }

    public static double convertDegreesToTicks(double degrees) {

        double ticks = degrees * 1 / ((1.0 / 2048.0) * (1.0 / (150 / 7)) * (360.0 / 1.0));
        return ticks;
    }

    public void DriveWithJoystick(Joystick js) {

        double leftRightDir = -1 * js.getRawAxis(0); // positive number means left
        double fwdBackDir = -1 * js.getRawAxis(1); // positive number means fwd
        double turn = -1 * js.getRawAxis(4); // positive number means clockwise

        double deadband = .05;

        if (-deadband < leftRightDir && leftRightDir < deadband) {
            leftRightDir = 0;
        }

        if (-deadband < fwdBackDir && fwdBackDir < deadband) {
            fwdBackDir = 0;
        }

        if (-deadband < turn && turn < deadband) {
            turn = 0;
        }

        // button 8 on xbox is three lines button
        if (js.getRawButton(8)) {
            leftModule.resetTurnEncoders();
            rightModule.resetTurnEncoders();
            backRightModule.resetTurnEncoders();
            backLeftModule.resetTurnEncoders();
        }

        // Example chassis speeds: 1 meter per second forward, 3 meters
        // per second to the left, and rotation at 1.5 radians per second
        // counteclockwise.
        ChassisSpeeds speeds = new ChassisSpeeds(fwdBackDir, leftRightDir, turn);

        // Convert to module states
        SwerveModuleState[] moduleStates = m_kinematics.toSwerveModuleStates(speeds);

        // Front left module state
        SwerveModuleState frontLeft = moduleStates[0];
        SwerveModuleState frontRight = moduleStates[1];
        SwerveModuleState backRight = moduleStates[3];
        SwerveModuleState backLeft = moduleStates[2];

        leftModule.setModuleState(frontLeft);
        rightModule.setModuleState(frontRight);
        backRightModule.setModuleState(backRight);
        backLeftModule.setModuleState(backLeft);

    }

}
