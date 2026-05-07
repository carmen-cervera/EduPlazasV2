package com.eduplazas.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Notificación 1: solicitud presentada
    public void enviarConfirmacionSolicitud(String emailEstudiante, String nombreEstudiante,
                                             String cursoAcademico, List<String> gradosEnOrden) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(emailEstudiante);
        mensaje.setSubject("EduPlazas — Solicitud registrada correctamente");

        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(nombreEstudiante).append(",\n\n");
        sb.append("Tu solicitud para la convocatoria ").append(cursoAcademico)
          .append(" ha sido registrada correctamente.\n\n");
        sb.append("Tus preferencias en orden:\n");
        for (int i = 0; i < gradosEnOrden.size(); i++) {
            sb.append(i + 1).append(". ").append(gradosEnOrden.get(i)).append("\n");
        }
        sb.append("\nUn saludo,\nEl equipo de EduPlazas");

        mensaje.setText(sb.toString());
        mailSender.send(mensaje);
    }

    // Notificación 2: estudiante asignado
    public void enviarResultadoAsignado(String emailEstudiante, String nombreEstudiante,
                                         String cursoAcademico, String grado,
                                         String universidad, Double notaCorte) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(emailEstudiante);
        mensaje.setSubject("EduPlazas — Resultados de admisión");

        String texto = "Hola " + nombreEstudiante + ",\n\n" +
            "Ya están disponibles los resultados de la convocatoria " + cursoAcademico + ".\n\n" +
            "¡Enhorabuena! Has sido admitido/a en:\n" +
            "  Grado: " + grado + "\n" +
            "  Universidad: " + universidad + "\n" +
            "  Nota de corte: " + String.format("%.3f", notaCorte) + "\n\n" +
            "Accede a EduPlazas para más información.\n\n" +
            "Un saludo,\nEl equipo de EduPlazas";

        mensaje.setText(texto);
        mailSender.send(mensaje);
    }

    // Notificación 3: estudiante rechazado
    public void enviarResultadoRechazado(String emailEstudiante, String nombreEstudiante,
                                          String cursoAcademico) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(emailEstudiante);
        mensaje.setSubject("EduPlazas — Resultados de admisión");

        String texto = "Hola " + nombreEstudiante + ",\n\n" +
            "Ya están disponibles los resultados de la convocatoria " + cursoAcademico + ".\n\n" +
            "Lamentablemente, no has obtenido plaza en ninguna de tus opciones " +
            "en esta convocatoria.\n\n" +
            "Accede a EduPlazas para más información.\n\n" +
            "Un saludo,\nEl equipo de EduPlazas";

        mensaje.setText(texto);
        mailSender.send(mensaje);
    }

    // Notificación 4: oferta publicada
    public void enviarConfirmacionOferta(String emailRepresentante, String nombreRepresentante,
                                          String grado, int totalPlazas,
                                          List<String> asignaturas) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(emailRepresentante);
        mensaje.setSubject("EduPlazas — Oferta publicada correctamente");

        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(nombreRepresentante).append(",\n\n");
        sb.append("La siguiente oferta ha sido publicada correctamente:\n\n");
        sb.append("  Grado: ").append(grado).append("\n");
        sb.append("  Plazas: ").append(totalPlazas).append("\n");
        if (!asignaturas.isEmpty()) {
            sb.append("  Asignaturas que ponderan: ").append(String.join(", ", asignaturas)).append("\n");
        }
        sb.append("\nUn saludo,\nEl equipo de EduPlazas");

        mensaje.setText(sb.toString());
        mailSender.send(mensaje);
    }
}