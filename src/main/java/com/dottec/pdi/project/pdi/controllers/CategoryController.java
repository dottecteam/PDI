package com.dottec.pdi.project.pdi.controllers;

// ---------------- Creating the Category Controller CRUD to manage the categories while linked to the database -----------

import com.dottec.pdi.project.pdi.dao.TagDAO;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.enums.TagType;



public class CategoryController {

    private  final TagDAO tagDAO;

    public CategoryController(TagDAO dao) {
        this.tagDAO = dao;
    }

    public void addCategory(Tag tag){

        Tag existingTag = tagDAO.findById(tag.getId());

        if( existingTag != null) {
            System.out.println(existingTag + " já existe no banco de dados.");
            return;
        }
        try{
            this.tagDAO.insert(tag);
            System.out.println("Categoria adicionada com sucesso");
        } catch (Exception e) {
            System.out.println("Houve um erro ao adicionar sua categoria" + e.getMessage());
        }
    }

    public void deleteCategory(int id) {

        Tag existingTag = this.tagDAO.findById(id);

        // Making verification to see if the category exists before deleting

        if (existingTag == null) {
            System.out.println("Categoria não encontrada");
            return;
        }

        try {
            this.tagDAO.delete(id);// Calling the delete method from the CategoryDAo class
            System.out.println("Categoria deletada");

        } catch (Exception e) {
            System.out.println("Erro ao deletar categoria" + e.getMessage());
        }

    }

    public void findCategory(int id){
        this.tagDAO.findById(id); // Calling the findById method from the given CategoryDAO class

    }

    public void updateCategory( int id , String newName , TagType newType){
        // Fetching the category by its ID

        Tag existingTag = this.tagDAO.findById(id);

        // Making a verification to see if the category has an empty content or not

        try {
            if (existingTag == null) {
                System.out.println("Categoria não encontrada");
                return;
            }

            // Changing the attributes
            existingTag.setName(newName);
            existingTag.setType(newType);

            // Persisting changes to the database
            this.tagDAO.update(existingTag);
            System.out.println("Categoria atualizada"); // Confirmation message
        } catch (Exception e ) {
            System.out.println("Erro ao atualizar categoria" + e.getMessage() );
        }
    }


}