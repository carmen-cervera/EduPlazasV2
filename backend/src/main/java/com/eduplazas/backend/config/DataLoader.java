package com.eduplazas.backend.config;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

@Configuration
public class DataLoader {

    private static final String[] NOMBRES = {
        "Ana","Carlos","Laura","Pablo","María","Carmen","Javier","Lucía",
        "Alejandro","Sofía","Diego","Elena","Adrián","Marta","Sergio","Paula",
        "Iván","Natalia","Roberto","Cristina","Hugo","Raquel","Marcos","Irene",
        "Daniel","Patricia","Álvaro","Nuria","Miguel","Sandra","Andrés","Beatriz",
        "Jorge","Rebeca","David","Alicia","Rubén","Claudia","Óscar","Miriam",
        "Víctor","Mónica","Guillermo","Rosa","Fernando","Pilar","Rafael","Teresa",
        "Antonio","Isabel"
    };

    private static final String[] APELLIDOS = {
        "García","López","Martínez","Sánchez","Pérez","Fernández","Gómez","Ruiz",
        "Díaz","Torres","Romero","Álvarez","Moreno","Jiménez","Molina","Muñoz",
        "Herrera","Castro","Vega","Ortega","Ramos","Serrano","Blanco","Iglesias",
        "Medina","Campos","Aguirre","Pardo","Rubio","Cano","Vargas","Fuentes",
        "Delgado","Peña","Reyes","Moya","Núñez","Gutiérrez","Cruz","Navarro"
    };

    @Bean
    ApplicationRunner loadData(
            ConvocatoriaRepository convocatoriaRepo,
            UniversidadRepository universidadRepo,
            EstudianteRepository estudianteRepo,
            RepresentanteUniversidadRepository representanteRepo,
            OfertaRepository ofertaRepo,
            CriterioAdmisionRepository criterioRepo,
            UsuarioRepository usuarioRepo,
            NotaAsignaturaRepository notaRepo,
            SolicitudRepository solicitudRepo,
            PreferenciaRepository preferenciaRepo) {

        return args -> {

            if (universidadRepo.count() > 0) {
                System.out.println("Base de datos ya inicializada, omitiendo DataLoader.");
                return;
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Random rnd = new Random(42);

            // ── Universidades ──
            Universidad ucm  = univ(universidadRepo, "Universidad Complutense de Madrid", "ucm.es");
            Universidad upm  = univ(universidadRepo, "Universidad Politécnica de Madrid", "upm.es");
            Universidad uam  = univ(universidadRepo, "Universidad Autónoma de Madrid", "uam.es");
            Universidad uc3m = univ(universidadRepo, "Universidad Carlos III de Madrid", "uc3m.es");
            Universidad urjc = univ(universidadRepo, "Universidad Rey Juan Carlos", "urjc.es");

            // ── Representantes ──
            rep(representanteRepo, encoder, "Luis",  "García Pérez",   "33333333C", "luis@upm.es",   upm);
            rep(representanteRepo, encoder, "María", "López Sanz",     "44444444D", "maria@ucm.es",  ucm);
            rep(representanteRepo, encoder, "Pedro", "Martínez Gil",   "55555555E", "pedro@uam.es",  uam);
            rep(representanteRepo, encoder, "Ana",   "Fernández Ruiz", "66666666F", "ana@uc3m.es",   uc3m);
            rep(representanteRepo, encoder, "Laura", "Sánchez Mora",   "77777777G", "laura@urjc.es", urjc);

            // ── Admin ──
            Admin admin = new Admin();
            admin.setNombre("Admin"); admin.setApellidos("EduPlazas");
            admin.setEmail("admin@eduplazas.es"); admin.setPassword(encoder.encode("admin1234"));
            admin.setDni("00000000A");
            usuarioRepo.save(admin);

            // ── Convocatoria ──
            Convocatoria conv = new Convocatoria();
            conv.setCursoAcademico("2025-2026");
            conv.setFechaApertura(LocalDate.now());
            conv.setFechaCierreConvocatoria(LocalDate.now().plusDays(30));
            conv.setEstado(EstadoConvocatoriaEnum.ABIERTA);
            convocatoriaRepo.save(conv);

            // ── Ofertas ──
            Oferta oMedUcm  = oferta(ofertaRepo, criterioRepo, conv, ucm,  "Medicina",                           "Ciencias de la Salud",      80,  new String[]{"Biología:0.2","Química:0.2"});
            Oferta oDerUcm  = oferta(ofertaRepo, criterioRepo, conv, ucm,  "Derecho",                            "Ciencias Sociales",         200, new String[]{"Historia de España:0.2","Latín II:0.1"});
            Oferta oPsiUcm  = oferta(ofertaRepo, criterioRepo, conv, ucm,  "Psicología",                         "Ciencias de la Salud",      150, new String[]{"Biología:0.2","Historia de la Filosofía:0.1"});
            Oferta oMatUcm  = oferta(ofertaRepo, criterioRepo, conv, ucm,  "Matemáticas",                        "Ciencias",                  100, new String[]{"Matemáticas II:0.2"});
            Oferta oBelUcm  = oferta(ofertaRepo, criterioRepo, conv, ucm,  "Bellas Artes",                       "Arte y Humanidades",        60,  new String[]{"Historia del Arte:0.2","Dibujo Artístico II:0.2"});
            Oferta oInfUpm  = oferta(ofertaRepo, criterioRepo, conv, upm,  "Ingeniería Informática",             "Ingeniería y Arquitectura", 120, new String[]{"Matemáticas II:0.2","Física:0.1"});
            Oferta oIndUpm  = oferta(ofertaRepo, criterioRepo, conv, upm,  "Ingeniería Industrial",              "Ingeniería y Arquitectura", 100, new String[]{"Matemáticas II:0.2","Física:0.2"});
            Oferta oAerUpm  = oferta(ofertaRepo, criterioRepo, conv, upm,  "Ingeniería Aeroespacial",            "Ingeniería y Arquitectura", 80,  new String[]{"Matemáticas II:0.2","Física:0.2"});
            Oferta oTelUpm  = oferta(ofertaRepo, criterioRepo, conv, upm,  "Ingeniería de Telecomunicación",     "Ingeniería y Arquitectura", 90,  new String[]{"Matemáticas II:0.2","Física:0.1"});
            Oferta oBioUam  = oferta(ofertaRepo, criterioRepo, conv, uam,  "Biología",                           "Ciencias de la Salud",      120, new String[]{"Biología:0.2","Química:0.1"});
            Oferta oFisUam  = oferta(ofertaRepo, criterioRepo, conv, uam,  "Física",                             "Ciencias",                  80,  new String[]{"Física:0.2","Matemáticas II:0.1"});
            Oferta oQuiUam  = oferta(ofertaRepo, criterioRepo, conv, uam,  "Química",                            "Ciencias",                  80,  new String[]{"Química:0.2","Biología:0.1"});
            Oferta oEcoUam  = oferta(ofertaRepo, criterioRepo, conv, uam,  "Economía",                           "Ciencias Sociales",         150, new String[]{"Matemáticas Apl. CC. Soc. II:0.2"});
            Oferta oAdeUc3m = oferta(ofertaRepo, criterioRepo, conv, uc3m, "Administración y Dirección de Empresas", "Ciencias Sociales",    180, new String[]{"Matemáticas Apl. CC. Soc. II:0.2","Empresa y Diseño de Modelos de Negocio:0.1"});
            Oferta oInfUc3m = oferta(ofertaRepo, criterioRepo, conv, uc3m, "Ingeniería Informática",             "Ingeniería y Arquitectura", 110, new String[]{"Matemáticas II:0.2","Tecnología e Ingeniería II:0.1"});
            Oferta oPolUc3m = oferta(ofertaRepo, criterioRepo, conv, uc3m, "Ciencias Políticas",                 "Ciencias Sociales",         100, new String[]{"Historia de España:0.2","Geografía:0.1"});
            Oferta oPerUc3m = oferta(ofertaRepo, criterioRepo, conv, uc3m, "Periodismo",                         "Ciencias Sociales",         90,  new String[]{"Historia de España:0.1","Historia de la Filosofía:0.1"});
            Oferta oComUrjc = oferta(ofertaRepo, criterioRepo, conv, urjc, "Comunicación Audiovisual",           "Arte y Humanidades",        100, new String[]{"Historia del Arte:0.2","Fundamentos Artísticos:0.1"});
            Oferta oEnfUrjc = oferta(ofertaRepo, criterioRepo, conv, urjc, "Enfermería",                         "Ciencias de la Salud",      120, new String[]{"Biología:0.2","Química:0.1"});
            Oferta oTurUrjc = oferta(ofertaRepo, criterioRepo, conv, urjc, "Turismo",                            "Ciencias Sociales",         130, new String[]{"Geografía:0.2","Historia de España:0.1"});

            List<Oferta> todasOfertas = List.of(
                oMedUcm, oDerUcm, oPsiUcm, oMatUcm, oBelUcm,
                oInfUpm, oIndUpm, oAerUpm, oTelUpm,
                oBioUam, oFisUam, oQuiUam, oEcoUam,
                oAdeUc3m, oInfUc3m, oPolUc3m, oPerUc3m,
                oComUrjc, oEnfUrjc, oTurUrjc
            );

            // ── Generar 410 entradas EvAU (400 registrados + 10 libres) ──
            List<Map<String, String>> evauEntries = new ArrayList<>();
            List<String[]> estudiantesData = new ArrayList<>();

            int idx = 0;
            for (int i = 1; i <= 410; i++) {
                String idEvau    = String.format("MAD-2025-%03d", i);
                String nombre    = NOMBRES[idx % NOMBRES.length];
                String apellido1 = APELLIDOS[(idx * 3) % APELLIDOS.length];
                String apellido2 = APELLIDOS[(idx * 7 + 5) % APELLIDOS.length];
                String apellidos = apellido1 + " " + apellido2;
                idx++;

                Map<String, String> entry = new LinkedHashMap<>();
                entry.put("id", idEvau);
                entry.put("nombre", nombre);
                entry.put("apellidos", apellidos);
                evauEntries.add(entry);

                if (i <= 400) {
                    estudiantesData.add(new String[]{idEvau, nombre, apellidos});
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            File evauFile = new File("src/main/resources/evau.json");
            if (!evauFile.getParentFile().exists()) evauFile.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(evauFile, evauEntries);
            System.out.println("evau.json generado con 410 entradas.");

            // ── Crear 400 estudiantes ──
            for (int i = 0; i < estudiantesData.size(); i++) {
                String[] datos    = estudiantesData.get(i);
                String idEvau     = datos[0];
                String nombre     = datos[1];
                String apellidos  = datos[2];
                String email      = "estudiante" + (i + 1) + "@eduplazas.es";
                String dni        = String.format("%08dZ", i + 1);

                double bachillerato = round(rnd, 5.0, 10.0);
                double evauGeneral  = round(rnd, 4.0, 10.0);
                double notaBase     = Math.round((0.6 * bachillerato + 0.4 * evauGeneral) * 100.0) / 100.0;

                Estudiante est = new Estudiante();
                est.setNombre(nombre); est.setApellidos(apellidos);
                est.setEmail(email); est.setPassword(encoder.encode("1234"));
                est.setDni(dni); est.setIdEvau(idEvau); est.setNotaBase(notaBase);
                estudianteRepo.save(est);

                nota(notaRepo, est, "Bachillerato",                  bachillerato);
                nota(notaRepo, est, "Lengua Castellana",             round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Historia de España",            round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Inglés",                        round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Matemáticas",                   round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Matemáticas II",                round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Física",                        round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Biología",                      round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Química",                       round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Historia del Arte",             round(rnd, 4.0, 10.0));
                nota(notaRepo, est, "Matemáticas Apl. CC. Soc. II", round(rnd, 4.0, 10.0));

                // Últimos 5 → BORRADOR
                if (i >= 395) {
                    solicitud(solicitudRepo, preferenciaRepo, conv, est,
                        EstadoSolicitudEnum.BORRADOR,
                        List.of(oAerUpm, oInfUpm, oTelUpm));
                    continue;
                }

                // Distribución de perfiles:
                // i   0- 89 → aeroespacial (llena los 80)
                // i  90-169 → informática  (UPM + UC3M)
                // i 170-229 → salud        (Medicina, Enfermería, Biología)
                // i 230-289 → social       (Derecho, ADE, Economía)
                // i 290-339 → ciencias     (Física, Química, Matemáticas)
                // i 340-369 → humanidades  (Bellas Artes, Comunicación, Turismo)
                // i 370-394 → mixto aleatorio
                List<Oferta> prefs;
                if (i < 90) {
                    prefs = List.of(oAerUpm, oInfUpm, oTelUpm, oIndUpm, oInfUc3m);
                } else if (i < 170) {
                    prefs = List.of(oInfUpm, oInfUc3m, oTelUpm, oIndUpm, oAdeUc3m);
                } else if (i < 230) {
                    prefs = List.of(oMedUcm, oEnfUrjc, oBioUam, oPsiUcm, oQuiUam);
                } else if (i < 290) {
                    prefs = List.of(oDerUcm, oAdeUc3m, oEcoUam, oPolUc3m, oPerUc3m);
                } else if (i < 340) {
                    prefs = List.of(oFisUam, oQuiUam, oMatUcm, oBioUam, oInfUpm);
                } else if (i < 370) {
                    prefs = List.of(oBelUcm, oComUrjc, oTurUrjc, oPerUc3m, oPolUc3m);
                } else {
                    List<Oferta> shuffled = new ArrayList<>(todasOfertas);
                    Collections.shuffle(shuffled, rnd);
                    prefs = shuffled.subList(0, Math.min(5, shuffled.size()));
                }

                solicitud(solicitudRepo, preferenciaRepo, conv, est,
                    EstadoSolicitudEnum.ENTREGADA, prefs);
            }

            System.out.println("400 estudiantes creados con notas y solicitudes.");
            System.out.println("Datos de ejemplo cargados correctamente.");
        };
    }

    // ── Helpers ──

    private Universidad univ(UniversidadRepository repo, String nombre, String ext) {
        Universidad u = new Universidad();
        u.setNombre(nombre); u.setExtensionEmail(ext);
        return repo.save(u);
    }

    private void rep(RepresentanteUniversidadRepository repo, BCryptPasswordEncoder enc,
                     String nombre, String apellidos, String dni, String email, Universidad uni) {
        RepresentanteUniversidad r = new RepresentanteUniversidad();
        r.setNombre(nombre); r.setApellidos(apellidos); r.setDni(dni);
        r.setEmail(email); r.setEmailInstitucional(email);
        r.setPassword(enc.encode("1234")); r.setUniversidad(uni);
        repo.save(r);
    }

    private Oferta oferta(OfertaRepository ofertaRepo, CriterioAdmisionRepository criterioRepo,
                          Convocatoria conv, Universidad uni, String grado, String rama,
                          int plazas, String[] criterios) {
        Oferta o = new Oferta();
        o.setGrado(grado); o.setRama(rama);
        o.setTotalPlazas(plazas); o.setUniversidad(uni); o.setConvocatoria(conv);
        ofertaRepo.save(o);
        for (String c : criterios) {
            String[] p = c.split(":");
            CriterioAdmision cr = new CriterioAdmision();
            cr.setAsignatura(p[0]); cr.setPeso(Double.parseDouble(p[1])); cr.setOferta(o);
            criterioRepo.save(cr);
        }
        return o;
    }

    private void nota(NotaAsignaturaRepository repo, Estudiante est, String asig, double val) {
        NotaAsignatura n = new NotaAsignatura();
        n.setAsignatura(asig); n.setNota(val); n.setEstudiante(est);
        repo.save(n);
    }

    private void solicitud(SolicitudRepository solRepo, PreferenciaRepository prefRepo,
                           Convocatoria conv, Estudiante est, EstadoSolicitudEnum estado,
                           List<Oferta> ofertas) {
        Solicitud s = new Solicitud();
        s.setEstudiante(est); s.setConvocatoria(conv);
        s.setEstado(estado); s.setFechaPresentacion(LocalDate.now());
        solRepo.save(s);
        for (int i = 0; i < ofertas.size(); i++) {
            Preferencia p = new Preferencia();
            p.setSolicitud(s); p.setOferta(ofertas.get(i)); p.setOrdenPreferencia(i + 1);
            prefRepo.save(p);
        }
    }

    private double round(Random rnd, double min, double max) {
        double val = min + rnd.nextDouble() * (max - min);
        return Math.round(val * 100.0) / 100.0;
    }
}