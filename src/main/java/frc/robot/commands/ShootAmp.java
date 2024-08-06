package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Shaft;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.Transfer;
import frc.robot.Constants;
import frc.robot.devices.LimeLight;

public class ShootAmp extends Command {
  private Shooter m_shooter;
  private Transfer m_transfer;
  private Shaft m_shaft;
  private Swerve m_swerve;

  private double shooterPitch, shooterYaw;
  private double robotYaw;

  public ShootAmp(Shooter shooter, Transfer transfer, Shaft shaft) {
    m_shooter = shooter;
    m_transfer = transfer;
    m_shaft = shaft;

    addRequirements(m_shooter, m_transfer, m_shaft);
  }

  @Override
  public void execute() {

    //get value
    robotYaw = LimeLight.getAmpYawDegrees();

    //1. Is the robot's position able to shoot note into speaker?
    if(robotYaw > Constants.OperatorConstants.MaxShootAmpYawDegrees){//yaw
      m_shooter.shoot(0);
      m_swerve.move(-0.3, 0, 0);
      return;
    }
    if(robotYaw < 360 - Constants.OperatorConstants.MaxShootAmpYawDegrees){//yaw
      m_shooter.shoot(0);
      m_swerve.move(0.3, 0, 0);
      return;
    }


    //get value
    shooterPitch = m_shaft.getEncValue()[0] - Constants.OperatorConstants.ShootAmpPitchDegrees;

    //2. Is the shooter's direction able to shoot note into speaker?
    if(shooterPitch > 3 || shooterPitch < -3){//pitch
      m_shaft.setPosition(Constants.OperatorConstants.ShootAmpPitchDegrees);
      m_shooter.shoot(0);
      return;
    }
    if(shooterYaw > 3 || shooterYaw < -3){//yaw
      m_shooter.shoot(0);
      return;
    }

    //get ready to shoot!
    m_shooter.shoot(Constants.OperatorConstants.ShootAmpPwr);
    m_transfer.front();
    m_transfer.back();
  }

  @Override
  public void end(boolean interrupted) {
    m_shooter.shoot(0);
  }

  @Override
  public boolean isFinished() {
    if(!LimeLight.isDetectedSpeaker()){
      return true;
    }
    else return false;
  }
}
