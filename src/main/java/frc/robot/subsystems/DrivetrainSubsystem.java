package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Module;

public class DrivetrainSubsystem extends SubsystemBase {
    private static DrivetrainSubsystem instance = null;

    // Locations for the swerve drive modules relative to the robot center.
    Translation2d m_frontLeftLocation = new Translation2d(0.381, 0.381);
    Translation2d m_frontRightLocation = new Translation2d(0.381, -0.381);
    Translation2d m_backLeftLocation = new Translation2d(-0.381, 0.381);
    Translation2d m_backRightLocation = new Translation2d(-0.381, -0.381);

    // Creating my kinematics object using the module locations
    SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
            m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation);

    Joystick js0 = new Joystick(0);

    // TalonFX driveMotorfl = new TalonFX(7);

    // TalonFX turnMotorfl = new TalonFX(8);

    TalonFX turnMotorfr = new TalonFX(1);

    TalonFX driveMotorfr = new TalonFX(2);

    // CANCoder encfl = new CANCoder(4);

    CANCoder encfr = new CANCoder(1);

    int counter = 0;

    Module leftModule = new Module(7, 8, 4);

    Module rightModule = new Module(1, 2, 1);

    Module backRightModule = new Module(4, 3, 2);

    Module backLeftModule = new Module(6, 5, 3);

    public DrivetrainSubsystem() {

        // encfl.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
        // encfl.setPosition(0);

        encfr.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
        encfr.setPosition(0);

        // turnMotorfl.configFactoryDefault();

        turnMotorfr.configFactoryDefault();
        // // set motor encoder to 0 when robot code starts
        // turnMotorfl.setSelectedSensorPosition(convertDegreesToTicks(encfl.getPosition()));
        // turnMotorfl.setInverted(true);

        turnMotorfr.setSelectedSensorPosition(convertDegreesToTicks(encfr.getPosition()));
        turnMotorfr.setInverted(true);

        // driveMotor.setInverted(true);

        double turnMotorKp = .2;
        double turnMotorKI = 0;
        double turnMotorKD = 0.1;

        // turnMotorfl.config_kP(0, turnMotorKp);
        // turnMotorfl.config_kI(0, turnMotorKI);
        // turnMotorfl.config_kD(0, turnMotorKD);

        turnMotorfr.config_kP(0, turnMotorKp);
        turnMotorfr.config_kI(0, turnMotorKI);
        turnMotorfr.config_kD(0, turnMotorKD);

        driveMotorfr.setInverted(true);
        // driveMotorfl.setInverted(true);

    }

    @Override
    public void periodic() {
        DriveWithJoystick(js0);
    }

    public double convertTicksToDegrees(double ticks) {
        double degrees = ticks * (1.0 / 2048.0) * (1.0 / (150 / 7)) * (360.0 / 1.0);
        return degrees;
    }

    public static double convertDegreesToTicks(double degrees) {

        double ticks = degrees * 1 / ((1.0 / 2048.0) * (1.0 / (150 / 7)) * (360.0 / 1.0));
        return ticks;
    }

    public void DriveWithJoystick(Joystick js) {

        double leftRightDir = -1 * js.getRawAxis(0);

        double fwdBackDir = -1 * js.getRawAxis(1);

        double z = js.getRawAxis(2);

        if (-0.1 < leftRightDir && leftRightDir < 0.1) {
            leftRightDir = 0;
        }

        if (-0.1 < fwdBackDir && fwdBackDir < 0.1) {
            fwdBackDir = 0;
        }

        if (-0.1 < z && z < 0.1) {
            z = 0;
        }

        SmartDashboard.putNumber("leftRightDir number", leftRightDir);

        SmartDashboard.putNumber("fwdBackDir number", fwdBackDir);

        SmartDashboard.putNumber("Z number", z);
        // Example chassis speeds: 1 meter per second forward, 3 meters
        // per second to the left, and rotation at 1.5 radians per second
        // counteclockwise.
        ChassisSpeeds speeds = new ChassisSpeeds(fwdBackDir, leftRightDir, z);

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

        // if (fwdBackDir != 0 || leftRightDir != 0) {
        // // before optimized
        SmartDashboard.putNumber("before optimized speed", frontLeft.speedMetersPerSecond);

        SmartDashboard.putNumber("before optimized heading", frontLeft.angle.getDegrees());

        // if (counter++ % 100 == 0)
        // frontLeft = SwerveModuleState.optimize(frontLeft, new
        // Rotation2d(encfl.getPosition()));
        // // after optimized

        SmartDashboard.putNumber("after optimized speed", frontLeft.speedMetersPerSecond);

        SmartDashboard.putNumber("after optimized heading", frontLeft.angle.getDegrees());
        // }

        // Front right module state
        // SwerveModuleState frontRight = moduleStates[1];

        // Back left module state
        // SwerveModuleState backLeft = moduleStates[2];

        // Back right module state
        // SwerveModuleState backRight = moduleStates[3];

        SmartDashboard.putNumber("front left speed", frontLeft.speedMetersPerSecond);

        SmartDashboard.putNumber("front left heading", frontLeft.angle.getDegrees());

        // driveMotorfl.set(ControlMode.PercentOutput, frontLeft.speedMetersPerSecond);

        driveMotorfr.set(ControlMode.PercentOutput, frontRight.speedMetersPerSecond);
        // frontLeft.angle.getDegrees();

        double desiredDegrees = frontLeft.angle.getDegrees();
        double desiredTicks = convertDegreesToTicks(desiredDegrees);
        SmartDashboard.putNumber("Desired degrees", desiredDegrees);
        SmartDashboard.putNumber("Desired ticks", desiredTicks);

        // turnMotorfl.set(ControlMode.Position, desiredTicks);

        turnMotorfr.set(ControlMode.Position, convertDegreesToTicks(frontRight.angle.getDegrees()));
        // SmartDashboard.putNumber("PID Error", turnMotor.getClosedLoopError());

        // turnMotor.set(ControlMode.PercentOutput, .5);

        // t2.set(ControlMode.Position,frontLeft.speedMetersPerSecond);

        // t2.getSelectedSensorPosition()

        // SmartDashboard.putNumber("actual tick function",
        // convertDegreesToTicks(frontLeft.angle.getDegrees()));

        // double actualTicks = turnMotorfl.getSelectedSensorPosition();
        // double actualDegrees = convertTicksToDegrees(actualTicks);

        // double actualTicks = turnMotorfr.getSelectedSensorPosition();
        // double actualDegrees = convertTicksToDegrees(actualTicks);

        // SmartDashboard.putNumber("Actual degrees", actualDegrees);
        // SmartDashboard.putNumber("Actual ticks", actualTicks);

        // SmartDashboard.putNumber("encoder degrees", encfl.getPosition());

        // SmartDashboard.putString("enc this is the error", enc.getLastError().name());

    }

}
