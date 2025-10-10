package com.dottec.pdi.project.pdi.utils;

import com.dottec.pdi.project.pdi.model.Goal;

public class GoalValidator{
	public boolean goalValidator(Goal goal) {
		if(goal == null /*|| goal.getStatus == null*/ ) {							//Remover comentarios caso necessario verificar o status
			return false;
		}
		return	StringValidator.dateValidate(goal.getDeadline()) &&					//Ja funciona corretamente
				StringValidator.descriptionValidate(goal.getName()) &&				//usei o descriptionValidate por não ser um nome próprio
				StringValidator.descriptionValidate(goal.getDescription()) &&		//Ja funciona corretamente(de acordo com o pattern)
				StringValidator.categoryValidate(goal.getCategory());				//Ajustar a função categoryValidate depois 
	}
}