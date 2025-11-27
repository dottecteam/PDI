package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.NotificationDAO;
import com.dottec.pdi.project.pdi.model.Notification;
import com.dottec.pdi.project.pdi.enums.NotificationType;
import java.util.List;

public final class NotificationController {

    private NotificationController(){}

    public static boolean addNotification(Notification notification) {
        try {
            NotificationDAO.insert(notification);
            return true;
        }
        catch (Exception e) {
            System.err.println("Erro ao inserir notificação: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateNotification(Notification notification) {
        if (NotificationDAO.findById(notification.getNotId()) == null) {
            System.err.println("Erro: notificação com ID " + notification.getNotId() + " não encontrada para atualização.");
            return false;
        }

        try {
            NotificationDAO.update(notification);
            return true;
        }
        catch (Exception e) {
            System.err.println("Erro ao atualizar notificação: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteNotification(int id) {
        if (NotificationDAO.findById(id) == null) {
            System.err.println("Erro: notificação com ID " + id + " não encontrada para exclusão.");
            return false;
        }

        try {
            NotificationDAO.deleteById(id);
            return true;
        }
        catch (Exception e) {
            System.err.println("Erro ao deletar notificação: " + e.getMessage());
            return false;
        }
    }

    public static Notification findNotificationById(int id) {
        return NotificationDAO.findById(id);
    }

    public static List<Notification> findAllNotifications() {
        return NotificationDAO.readAll();
    }

    public static boolean markAsRead(int id) {
        Notification notification = NotificationDAO.findById(id);
        if (notification == null) {
            System.err.println("Erro: notificação com ID " + id + " não encontrada para marcação como lida.");
            return false;
        }

        try {
            notification.setNotIsRead(true);
            NotificationDAO.update(notification);
            return true;
        } 
        catch (Exception e) {
            System.err.println("Erro ao marcar notificação como lida: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateNotificationDetails(int id, String newMessage, NotificationType newType) {
        Notification notification = NotificationDAO.findById(id);
        if (notification == null) {
            System.err.println("Erro: notificação com ID " + id + " não encontrada para atualização.");
            return false;
        }

        notification.setNotMessage(newMessage);
        notification.setNotType(newType);

        return updateNotification(notification);
    }
}
