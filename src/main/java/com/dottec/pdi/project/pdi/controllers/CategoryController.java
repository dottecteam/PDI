package com.dottec.pdi.project.pdi.controllers;

// ---------------- Creating the Category Controller CRUD to manage the categories while linked to the database -----------

import com.dottec.pdi.project.pdi.dao.CategoryDAO;
import com.dottec.pdi.project.pdi.model.Category;
import com.dottec.pdi.project.pdi.enums.CategoryType;



public class CategoryController {

    private  final CategoryDAO  categoryDAO;

    public CategoryController(CategoryDAO dao) {
        this.categoryDAO = dao;
    }

    public void addCategory(Category category){

        Category existingCategory  = categoryDAO.findById(category.getId());

        if( existingCategory != null) {
            System.out.println(existingCategory + " já existe no banco de dados.");
            return;
        }
        try{
            this.categoryDAO.insert(category);
            System.out.println("Categoria adicionada com sucesso");
        } catch (Exception e) {
            System.out.println("Houve um erro ao adicionar sua categoria" + e.getMessage());
        }
    }

    public void deleteCategory(int id) {

        Category existingCategory = this.categoryDAO.findById(id);

        // Making verification to see if the category exists before deleting

        if (existingCategory == null) {
            System.out.println("Categoria não encontrada");
            return;
        }

        try {
            this.categoryDAO.delete(id);// Calling the delete method from the CategoryDAo class
            System.out.println("Categoria deletada");

        } catch (Exception e) {
            System.out.println("Erro ao deletar categoria" + e.getMessage());
        }

    }

    public void findCategory(int id){
        this.categoryDAO.findById(id); // Calling the findById method from the given CategoryDAO class

    }

    public void updateCategory( int id , String newName , CategoryType newType){
        // Fetching the category by its ID

        Category existingCategory = this.categoryDAO.findById(id);

        // Making a verification to see if the category has an empty content or not

        try {
            if (existingCategory == null) {
                System.out.println("Categoria não encontrada");
                return;
            }

            // Changing the attributes
            existingCategory.setName(newName);
            existingCategory.setType(newType);

            // Persisting changes to the database
            this.categoryDAO.update(existingCategory);
            System.out.println("Categoria atualizada"); // Confirmation message
        } catch (Exception e ) {
            System.out.println("Erro ao atualizar categoria" + e.getMessage() );
        }
    }


}