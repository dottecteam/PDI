package com.dottec.pdi.project.pdi.model;

import com.dottec.pdi.project.pdi.model.enums.Status;

public class Teste {
    public static void main(String[] args){
        Collaborator gabriel = new Collaborator(1,"gabriel", "gabriel@email.com", "12345677828", 32, "Sem experiência", "Mal funcionario", Status.active);

        System.out.println(gabriel.toString());
    }
}
