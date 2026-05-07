package com.eduplazas.backend.config;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    ApplicationRunner loadData(
            ConvocatoriaRepository convocatoriaRepo,
            UniversidadRepository universidadRepo,
            EstudianteRepository estudianteRepo,
            RepresentanteUniversidadRepository representanteRepo,
            OfertaRepository ofertaRepo,
            CriterioAdmisionRepository criterioRepo,
            UsuarioRepository usuarioRepo) {

        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Universidades
            Universidad ucm = new Universidad();
            ucm.setNombre("Universidad Complutense de Madrid");
            ucm.setExtensionEmail("ucm.es");
            universidadRepo.save(ucm);

            Universidad upm = new Universidad();
            upm.setNombre("Universidad Politécnica de Madrid");
            upm.setExtensionEmail("upm.es");
            universidadRepo.save(upm);

            Universidad uam = new Universidad();
            uam.setNombre("Universidad Autónoma de Madrid");
            uam.setExtensionEmail("uam.es");
            universidadRepo.save(uam);

            Universidad uc3m = new Universidad();
            uc3m.setNombre("Universidad Carlos III de Madrid");
            uc3m.setExtensionEmail("uc3m.es");
            universidadRepo.save(uc3m);

            Universidad urjc = new Universidad();
            urjc.setNombre("Universidad Rey Juan Carlos");
            urjc.setExtensionEmail("urjc.es");
            universidadRepo.save(urjc);

            // Representantes
            RepresentanteUniversidad repUpm = new RepresentanteUniversidad();
            repUpm.setNombre("Luis"); repUpm.setApellidos("García Pérez");
            repUpm.setDni("33333333C"); repUpm.setEmail("luis@upm.es");
            repUpm.setEmailInstitucional("luis@upm.es");
            repUpm.setPassword(encoder.encode("1234")); repUpm.setUniversidad(upm);
            representanteRepo.save(repUpm);

            RepresentanteUniversidad repUcm = new RepresentanteUniversidad();
            repUcm.setNombre("María"); repUcm.setApellidos("López Sanz");
            repUcm.setDni("44444444D"); repUcm.setEmail("maria@ucm.es");
            repUcm.setEmailInstitucional("maria@ucm.es");
            repUcm.setPassword(encoder.encode("1234")); repUcm.setUniversidad(ucm);
            representanteRepo.save(repUcm);

            RepresentanteUniversidad repUam = new RepresentanteUniversidad();
            repUam.setNombre("Pedro"); repUam.setApellidos("Martínez Gil");
            repUam.setDni("55555555E"); repUam.setEmail("pedro@uam.es");
            repUam.setEmailInstitucional("pedro@uam.es");
            repUam.setPassword(encoder.encode("1234")); repUam.setUniversidad(uam);
            representanteRepo.save(repUam);

            RepresentanteUniversidad repUc3m = new RepresentanteUniversidad();
            repUc3m.setNombre("Ana"); repUc3m.setApellidos("Fernández Ruiz");
            repUc3m.setDni("66666666F"); repUc3m.setEmail("ana@uc3m.es");
            repUc3m.setEmailInstitucional("ana@uc3m.es");
            repUc3m.setPassword(encoder.encode("1234")); repUc3m.setUniversidad(uc3m);
            representanteRepo.save(repUc3m);

            RepresentanteUniversidad repUrjc = new RepresentanteUniversidad();
            repUrjc.setNombre("Laura"); repUrjc.setApellidos("Sánchez Mora");
            repUrjc.setDni("77777777G"); repUrjc.setEmail("laura@urjc.es");
            repUrjc.setEmailInstitucional("laura@urjc.es");
            repUrjc.setPassword(encoder.encode("1234")); repUrjc.setUniversidad(urjc);
            representanteRepo.save(repUrjc);

            // Estudiantes
            Estudiante ana = new Estudiante();
            ana.setNombre("Ana"); ana.setApellidos("García López");
            ana.setEmail("ana@eduplazas.es"); ana.setPassword(encoder.encode("1234"));
            ana.setDni("11111111A"); ana.setIdEvau("MAD-2025-001"); ana.setNotaBase(0.0);
            estudianteRepo.save(ana);

            Estudiante carlos = new Estudiante();
            carlos.setNombre("Carlos"); carlos.setApellidos("Martínez Ruiz");
            carlos.setEmail("carlos@eduplazas.es"); carlos.setPassword(encoder.encode("1234"));
            carlos.setDni("22222222B"); carlos.setIdEvau("MAD-2025-002"); carlos.setNotaBase(0.0);
            estudianteRepo.save(carlos);

            // Admin
            Admin admin = new Admin();
            admin.setNombre("Admin"); admin.setApellidos("EduPlazas");
            admin.setEmail("admin@eduplazas.es"); admin.setPassword(encoder.encode("admin1234"));
            admin.setDni("00000000A");
            usuarioRepo.save(admin);

            // Convocatoria de ejemplo
            Convocatoria conv = new Convocatoria();
            conv.setCursoAcademico("2025-2026");
            conv.setFechaApertura(LocalDate.now());
            conv.setFechaCierreConvocatoria(LocalDate.now().plusDays(30));
            conv.setEstado(EstadoConvocatoriaEnum.ABIERTA);
            convocatoriaRepo.save(conv);

            // UCM
            oferta(ofertaRepo, criterioRepo, conv, ucm, "Medicina", 80,
                new String[]{"Biología:0.2", "Química:0.2"});
            oferta(ofertaRepo, criterioRepo, conv, ucm, "Derecho", 200,
                new String[]{"Historia de España:0.2", "Latín II:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, ucm, "Psicología", 150,
                new String[]{"Biología:0.2", "Historia de la Filosofía:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, ucm, "Matemáticas", 100,
                new String[]{"Matemáticas II:0.2"});
            oferta(ofertaRepo, criterioRepo, conv, ucm, "Bellas Artes", 60,
                new String[]{"Historia del Arte:0.2", "Dibujo Artístico II:0.2"});

            // UPM
            oferta(ofertaRepo, criterioRepo, conv, upm, "Ingeniería Informática", 120,
                new String[]{"Matemáticas II:0.2", "Física:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, upm, "Ingeniería Industrial", 100,
                new String[]{"Matemáticas II:0.2", "Física:0.2"});
            oferta(ofertaRepo, criterioRepo, conv, upm, "Ingeniería Aeroespacial", 80,
                new String[]{"Matemáticas II:0.2", "Física:0.2"});
            oferta(ofertaRepo, criterioRepo, conv, upm, "Ingeniería de Telecomunicación", 90,
                new String[]{"Matemáticas II:0.2", "Física:0.1"});

            // UAM
            oferta(ofertaRepo, criterioRepo, conv, uam, "Biología", 120,
                new String[]{"Biología:0.2", "Química:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, uam, "Física", 80,
                new String[]{"Física:0.2", "Matemáticas II:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, uam, "Química", 80,
                new String[]{"Química:0.2", "Biología:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, uam, "Economía", 150,
                new String[]{"Matemáticas Apl. CC. Soc. II:0.2"});

            // UC3M
            oferta(ofertaRepo, criterioRepo, conv, uc3m, "Administración y Dirección de Empresas", 180,
                new String[]{"Matemáticas Apl. CC. Soc. II:0.2", "Empresa y Diseño de Modelos de Negocio:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, uc3m, "Ingeniería Informática", 110,
                new String[]{"Matemáticas II:0.2", "Tecnología e Ingeniería II:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, uc3m, "Ciencias Políticas", 100,
                new String[]{"Historia de España:0.2", "Geografía:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, uc3m, "Periodismo", 90,
                new String[]{"Historia de España:0.1", "Historia de la Filosofía:0.1"});

            // URJC
            oferta(ofertaRepo, criterioRepo, conv, urjc, "Comunicación Audiovisual", 100,
                new String[]{"Historia del Arte:0.2", "Fundamentos Artísticos:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, urjc, "Enfermería", 120,
                new String[]{"Biología:0.2", "Química:0.1"});
            oferta(ofertaRepo, criterioRepo, conv, urjc, "Turismo", 130,
                new String[]{"Geografía:0.2", "Historia de España:0.1"});

            System.out.println("Datos de ejemplo cargados correctamente");
        };
    }

    private void oferta(OfertaRepository ofertaRepo, CriterioAdmisionRepository criterioRepo,
                        Convocatoria conv, Universidad uni, String grado, int plazas,
                        String[] criterios) {
        Oferta o = new Oferta();
        o.setGrado(grado);
        o.setTotalPlazas(plazas);
        o.setUniversidad(uni);
        o.setConvocatoria(conv);
        ofertaRepo.save(o);

        for (String c : criterios) {
            String[] partes = c.split(":");
            CriterioAdmision criterio = new CriterioAdmision();
            criterio.setAsignatura(partes[0]);
            criterio.setPeso(Double.parseDouble(partes[1]));
            criterio.setOferta(o);
            criterioRepo.save(criterio);
        }
    }
}