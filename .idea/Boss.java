package cr.ac.ucr.paraiso.losesobrad.model;
import java.util.ArrayList;
import java.util.List;
import cr.ac.ucr.paraiso.losesobrad.model.Enemy

    public class Boss {
    public class boss extends Enemy {
        private int phases;
        private List<String> specialAbilities=new ArrayList<>();

        public void triggerPhase(int phaseNumber){
            System.out.println("Peligro! El jefe ha cambiado a la fase  "+phaseNumber);
        }
        public void useSpecialAbility(String abilityName){
            if(this.specialAbilities.contains(abilityName)){
                System.out.println("el jefe usara su habilidad especial  "+abilityName+"  ten cuidado D:");
            } else{
                System.out.println("El jefe esta sin energia, por lo tanto no puede usar su habilidad especial");
            }
        }
        public int getPhases(){
            return this.phases;
        }
        public void setPhases(int phases){
            this.phases=phases;
        }
        public void setSpecialAbilities(List<String> specialAbilities){
            this.specialAbilities=specialAbilities;

        }

    }
}