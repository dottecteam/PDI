package com.dottec.pdi.project.pdi.model.Controller;

// ---------------- Creating the Category Controller CRUD to manage the categories while linked to the database -----------

import com.dottec.pdi.project.pdi.dao.CategoryDAO;
import com.dottec.pdi.project.pdi.model.Category;
import com.dottec.pdi.project.pdi.model.enums.CategoryType;

public class CategoryController {

    private  final CategoryDAO  categoryDAO;
    public CategoryController(CategoryDAO dao) {
        this.categoryDAO = dao;
    }

    public void addCategory(Category category){
        categoryDAO.insert(category);
    }

    public void deleteCategory(Category category){
        categoryDAO.delete(category.getId());
    }

    public void findCategory(Category category){
        category = categoryDAO.findById(category.getId());
    }

    public void updateCategory( int id , String newName , CategoryType newType){
        // Fetching the category by its ID

        Category category = categoryDAO.findById(id);

        // Making a verification to see if the category has an empty content or not
        if(category == null){
            System.out.println("Categoria não encontrada");
            return;
        }

        // Changing the attributes
        category.setName(newName);
        category.setType(newType);

        // Persisting changes to the database
        categoryDAO.update(category);
        System.out.println("Categoria atualizada"); // Confirmation message

    }

}