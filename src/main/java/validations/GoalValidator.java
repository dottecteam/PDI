package validations;

import com.dottec.pdi.project.pdi.model.Goal;

public class GoalValidator{
	public boolean goalValidator(Goal goal) {
		if(goal == null) {
			return false;
		}

		boolean deadlineDateValid = StringValidator.dateValidate(goal.getDeadline());					//Ja funciona corretamente
		boolean nameValid = StringValidator.descriptionValidate(goal.getName()); 						//usei o descriptionValidate por não ser um nome próprio
		boolean descriptionValid = StringValidator.descriptionValidate(goal.getDescription());			//Ja funciona corretamente(de acordo com o pattern)
		boolean categoryValid = StringValidator.categoryValidate(goal.getCategory());					//Ajustar a função categoryValidate depois
		
		
		return deadlineDateValid && nameValid && descriptionValid && categoryValid; 
	}
}