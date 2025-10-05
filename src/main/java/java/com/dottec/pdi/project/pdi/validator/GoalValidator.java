package com.dottec.pdi.project.pdi.validator;

import com.dottec.pdi.project.pdi.model.Goal;

public class GoalValidator{
	public boolean goalValidator(Goal goal) {
		if(goal == null /*|| goal.getStatus == null*/ ) {							//Remover comentarios caso necessario verificar o status
			return false;
		}
		return	Validator.dateValidate(goal.getDeadline()) &&					//Ja funciona corretamente
				Validator.descriptionValidate(goal.getName()) &&				//usei o descriptionValidate por não ser um nome próprio
				Validator.descriptionValidate(goal.getDescription()) &&		//Ja funciona corretamente(de acordo com o pattern)
				this.goalValidatorCategory(goal.getCategory());				//Ajustar a função categoryValidate depois 
	}
	public boolean goalValidatorDate(String date) {
		return Validator.dateValidate(date);
	}
	public boolean goalValidatorName(String name) {
		return Validator.descriptionValidate(name);
	}
	public boolean goalValidatorDescription(String name) {
		return Validator.descriptionValidate(name);
	}
	public boolean goalValidatorStatus(String status) {
		return Validator.statusValidateString(status); //solução temporaria
	}
	public boolean goalValidatorCategory(Category category) {
		CategoryDAO dao = new CategoryDAO();
		List<Category> categorias = dao.readAll();
		if(!categorias.contains(category)) return false;
		return true;
	}
}