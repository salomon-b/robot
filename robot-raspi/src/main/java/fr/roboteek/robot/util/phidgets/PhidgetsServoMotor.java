package fr.roboteek.robot.util.phidgets;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.ErrorEvent;
import com.phidget22.ErrorListener;
import com.phidget22.PhidgetException;
import com.phidget22.RCServo;
import com.phidget22.RCServoPositionChangeEvent;
import com.phidget22.RCServoPositionChangeListener;
import com.phidget22.RCServoTargetPositionReachedEvent;
import com.phidget22.RCServoTargetPositionReachedListener;
import com.phidget22.RCServoVelocityChangeEvent;
import com.phidget22.RCServoVelocityChangeListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implémentation d'un moteur servo-moteur via le servo-contrôleur Phidgets.
 *
 * @author Java Developer
 */
public class PhidgetsServoMotor implements AttachListener, DetachListener, RCServoPositionChangeListener, RCServoTargetPositionReachedListener, ErrorListener, RCServoVelocityChangeListener {

    /**
     * Moteur Phidget associé.
     */
    private RCServo rcServo;

    /**
     * Position initiale du moteur.
     */
    private double positionInitiale;

    /**
     * Position minimale du moteur.
     */
    private double positionMin;

    /**
     * Position maximale du moteur.
     */
    private double positionMax;

    /**
     * Vitesse par défaut.
     */
    private double vitesseParDefaut;

    /**
     * Accélération par défaut.
     */
    private double accelerationParDefaut;

    /**
     * Flag indiquant que la position est atteinte.
     */
    private AtomicBoolean positionAtteinte = new AtomicBoolean(true);

    /**
     * Constructeur d'un moteur Phidget.
     *
     * @param index index du moteur sur le contrôleur
     */
    public PhidgetsServoMotor(int index, double positionInitiale, double positionMin, double positionMax, double vitesseParDefaut, double accelerationParDefaut) {
        try {
            this.positionInitiale = positionInitiale;
            this.positionMin = positionMin;
            this.positionMax = positionMax;
            this.vitesseParDefaut = vitesseParDefaut;
            this.accelerationParDefaut = accelerationParDefaut;
            rcServo = new RCServo();
            rcServo.addAttachListener(this);
            rcServo.addDetachListener(this);
            rcServo.addErrorListener(this);
            rcServo.addPositionChangeListener(this);
            rcServo.addTargetPositionReachedListener(this);
            rcServo.addVelocityChangeListener(this);

            // Configuration
            rcServo.setDeviceSerialNumber(561050);
            rcServo.setHubPort(0);
            rcServo.setChannel(index);

            // Ouverture du moteur
            rcServo.open(5000);

        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Récupère la position cible du moteur.
     *
     * @return la position cible du moteur
     */
    public double getPositionCible() {
        try {
            return rcServo.getTargetPosition();
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public synchronized void setPositionCible(double position, Double vitesse, Double acceleration, boolean waitForPosition) {
        try {
            //System.out.println("POSITION DEMANDEE à " + System.currentTimeMillis() + " = " + position);
            positionAtteinte.set(false);
            setAcceleration(acceleration);
            rcServo.setTargetPosition(position);
            setVitesse(vitesse);
            while (waitForPosition && !positionAtteinte.get()) ;
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Récupère la position réelle du moteur.
     *
     * @return la position réelle du moteur
     */
    public double getPositionReelle() {
        try {
            return rcServo.getPosition();
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            System.out.println("Plantage position reelle");
            e.printStackTrace();
            return 0;
        }
    }

    public void rotate(double angle, Double vitesse, Double acceleration, boolean waitForPosition) {
        setPositionCible(getPositionReelle() + angle, vitesse, acceleration, waitForPosition);
    }

    public void forward(Double vitesse, Double acceleration, boolean waitForPosition) {
        setPositionCible(positionMax, vitesse, acceleration, waitForPosition);
    }

    public void backward(Double vitesse, Double acceleration, boolean waitForPosition) {
        setPositionCible(positionMin, vitesse, acceleration, waitForPosition);
    }

    public void stop() {
        try {
            rcServo.setVelocityLimit(0);
            // TODO A voir si nécessaire
            //rcServo.setTargetPosition(rcServo.getPosition());
            positionAtteinte.set(true);
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            rcServo.close();
        } catch (PhidgetException e) {
            e.printStackTrace();
        }
    }

    public double getPositionMax() {
        return positionMax;
    }

    public void setPositionMax(double position) {
        this.positionMax = position;
    }

    public double getPositionMin() {
        return positionMin;
    }

    public void setPositionMin(double position) {
        this.positionMin = position;
    }

    public boolean isEngaged() {
        try {
            return rcServo.getEngaged();
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void setEngaged(boolean state) {
        try {
            rcServo.setEngaged(state);
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean isSpeedRampingState() {
        try {
            return rcServo.getSpeedRampingState();
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void setSpeedRampingState(boolean state) {
        try {
            rcServo.setSpeedRampingState(state);
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean isStopped() {
        try {
            return positionAtteinte.get() && rcServo.getVelocity() == 0;
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    private void setVitesse(Double vitesse) {
        try {
            if (vitesse != null) {
                rcServo.setVelocityLimit(vitesse.doubleValue());
            } else {
                rcServo.setVelocityLimit(vitesseParDefaut);
            }
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setAcceleration(Double acceleration) {
        try {
            if (acceleration != null) {
                rcServo.setAcceleration(acceleration.doubleValue());
            } else {
                rcServo.setAcceleration(accelerationParDefaut);
            }
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(AttachEvent attachEvent) {
        // Une fois que le moteur est attaché, on l'active
        try {
            if (attachEvent.getSource().equals(rcServo)) {
                //rcServo.setDataInterval(32);
                rcServo.setAcceleration(accelerationParDefaut);
                rcServo.setTargetPosition(positionInitiale);
                rcServo.setVelocityLimit(vitesseParDefaut);
                rcServo.setEngaged(true);
                System.out.println("Servo " + rcServo.getChannel() + " attached");
            }
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onTargetPositionReached(RCServoTargetPositionReachedEvent event) {
        if (event.getSource().equals(rcServo)) {
            //try {
            positionAtteinte.set(true);
            //System.out.println("POSITION REACHED = time = " + System.currentTimeMillis() + ", event = " + event.getPosition() + ", servo = " + rcServo.getChannel() + ":" + rcServo.getPosition());
//			} catch (PhidgetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
        ;
    }

    @Override
    public void onPositionChange(RCServoPositionChangeEvent event) {
        if (event.getSource().equals(rcServo)) {
//			// Envoi d'un évènement à l'ensemble des écouteurs
//			if (listeEcouteursChangementPosition != null && !listeEcouteursChangementPosition.isEmpty()) {
//				final MotorPositionChangeEvent evenement = new MotorPositionChangeEvent(this, event.getPosition());
//				for (MotorPositionChangeListener ecouteur : listeEcouteursChangementPosition) {
//					ecouteur.onPositionchanged(evenement);
//				}
//			}
        }

    }

    @Override
    public void onError(ErrorEvent errorEvent) {
        System.out.println("Error: " + errorEvent.getDescription());
    }

    @Override
    public void onDetach(DetachEvent event) {
        if (event.getSource().equals(rcServo)) {
            try {
                System.out.println("Servo " + rcServo.getChannel() + " detached");
            } catch (PhidgetException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        final PhidgetsServoMotor moteurG = new PhidgetsServoMotor(0, 90, 50, 150, 100, 2000);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        moteurG.setSpeedRampingState(true);
        moteurG.setPositionCible(130, null, null, true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        moteurG.setSpeedRampingState(true);
        moteurG.setPositionCible(60, null, null, true);
        System.exit(0);
    }

    @Override
    public void onVelocityChange(RCServoVelocityChangeEvent event) {
        if (event.getSource() == rcServo) {
            System.out.println("Changement Vitesse = time = " + System.currentTimeMillis() + ", " + event.getVelocity());
        }
        ;
    }
}
