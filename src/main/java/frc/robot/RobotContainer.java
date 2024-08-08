package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.Joystick;

import frc.robot.devices.Gyro;
import frc.robot.devices.LimeLight;
import frc.robot.devices.Pixy;
import frc.robot.devices.Sensor;

/*** subsystems ***/

import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Transfer;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Shaft;

/*** commands ***/

import frc.robot.commands.JoystickSwerve;
import frc.robot.commands.GetNote;
import frc.robot.commands.JoystickIntake;
import frc.robot.commands.ShootSpeaker;
import frc.robot.commands.ShootAmp;
import frc.robot.commands.JoystickShooter;
import frc.robot.commands.JoystickShaft;

public class RobotContainer {
  private final Swerve swerve = new Swerve();
  private final Intake intake = new Intake();
  private final Transfer transfer = new Transfer();
  private final Shooter shooter = new Shooter();
  private final Shaft shaft = new Shaft();

  private final Joystick js1 = new Joystick(Constants.OperatorConstants.Player1Port);
  private final Joystick js2 = new Joystick(Constants.OperatorConstants.Player2Port);

  public RobotContainer() {
    // swerve
    swerve.setDefaultCommand(new JoystickSwerve(
      swerve, 
      ()->js1.getX(),
      ()->js1.getY(), 
      ()->js1.getRawAxis(4))
    );

    // shooter
    shooter.setDefaultCommand(new JoystickShooter(shooter, ()->js2.getRawAxis(2), ()->js2.getRawAxis(3)).withInterruptBehavior(InterruptionBehavior.kCancelIncoming));

    // shaft
    shaft.setDefaultCommand(new JoystickShaft(shaft, ()->js2.getY()).withInterruptBehavior(InterruptionBehavior.kCancelIncoming));

    // intake
    intake.setDefaultCommand(new JoystickIntake(intake, ()->js2.getPOV()));

    initialize();
    configureBindings();
  }

  private void configureBindings() {
    // shoot speaker
    new JoystickButton(js1, 5).whileTrue(new ShootSpeaker(shooter, transfer, shaft).withInterruptBehavior(InterruptionBehavior.kCancelSelf));

    // shoot AMP
    new JoystickButton(js1, 6).whileTrue(new ShootAmp(shooter, transfer, shaft).withInterruptBehavior(InterruptionBehavior.kCancelSelf));

    // get note
    new JoystickButton(js1, 1).whileTrue(new GetNote(intake, swerve, transfer, ()->js1.getX(), ()->js1.getY(), ()->js1.getRawAxis(4)).withInterruptBehavior(InterruptionBehavior.kCancelSelf));

    // transfer
    new JoystickButton(js2, 1).whileTrue(transfer.intakeIn.withInterruptBehavior(InterruptionBehavior.kCancelIncoming));
    new JoystickButton(js2, 3).whileTrue(transfer.intakeOut.withInterruptBehavior(InterruptionBehavior.kCancelIncoming));
    new JoystickButton(js2, 2).whileTrue(transfer.shooterOut.withInterruptBehavior(InterruptionBehavior.kCancelIncoming));
    new JoystickButton(js2, 4).whileTrue(transfer.shooterIn.withInterruptBehavior(InterruptionBehavior.kCancelIncoming));
  }

  private void initialize(){
    Pixy.init();
    Gyro.init();
    Sensor.init();
    LimeLight.init();
  }

  public Command getAutonomousCommand() {
    return Commands.parallel();
  }
}
