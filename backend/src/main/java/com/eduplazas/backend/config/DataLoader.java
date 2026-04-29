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
            OfertaRepository ofertaRepo,
            EstudianteRepository estudianteRepo,
            RepresentanteUniversidadRepository representanteRepo,
            CriterioAdmisionRepository criterioRepo,
            UsuarioRepository usuarioRepo) {

        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Convocatoria
            Convocatoria conv = new Convocatoria();
            conv.setCursoAcademico("2025-2026");
            conv.setFechaApertura(LocalDate.of(2025, 6, 1));
            conv.setFechaCierreConvocatoria(LocalDate.of(2025, 6, 30));
            conv.setEstado(EstadoConvocatoriaEnum.ABIERTA);
            convocatoriaRepo.save(conv);

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
            repUpm.setNombre("Luis");
            repUpm.setApellidos("García Pérez");
            repUpm.setDni("33333333C");
            repUpm.setEmailInstitucional("luis@upm.es");
            repUpm.setPassword(encoder.encode("1234"));
            repUpm.setUniversidad(upm);
            representanteRepo.save(repUpm);

            RepresentanteUniversidad repUcm = new RepresentanteUniversidad();
            repUcm.setNombre("María");
            repUcm.setApellidos("López Sanz");
            repUcm.setDni("44444444D");
            repUcm.setEmailInstitucional("maria@ucm.es");
            repUcm.setPassword(encoder.encode("1234"));
            repUcm.setUniversidad(ucm);
            representanteRepo.save(repUcm);

            // Ofertas con criterios
            Oferta oferta1 = new Oferta();
            oferta1.setGrado("Ingeniería Informática");
            oferta1.setTotalPlazas(120);
            oferta1.setUniversidad(upm);
            oferta1.setConvocatoria(conv);
            ofertaRepo.save(oferta1);

            CriterioAdmision c1 = new CriterioAdmision();
            c1.setAsignatura("Matemáticas II");
            c1.setPeso(0.2);
            c1.setOferta(oferta1);
            criterioRepo.save(c1);

            CriterioAdmision c2 = new CriterioAdmision();
            c2.setAsignatura("Física");
            c2.setPeso(0.1);
            c2.setOferta(oferta1);
            criterioRepo.save(c2);

            Oferta oferta2 = new Oferta();
            oferta2.setGrado("Medicina");
            oferta2.setTotalPlazas(80);
            oferta2.setUniversidad(ucm);
            oferta2.setConvocatoria(conv);
            ofertaRepo.save(oferta2);

            CriterioAdmision c3 = new CriterioAdmision();
            c3.setAsignatura("Biología");
            c3.setPeso(0.2);
            c3.setOferta(oferta2);
            criterioRepo.save(c3);

            CriterioAdmision c4 = new CriterioAdmision();
            c4.setAsignatura("Química");
            c4.setPeso(0.2);
            c4.setOferta(oferta2);
            criterioRepo.save(c4);

            Oferta oferta3 = new Oferta();
            oferta3.setGrado("Derecho");
            oferta3.setTotalPlazas(650);
            oferta3.setUniversidad(ucm);
            oferta3.setConvocatoria(conv);
            ofertaRepo.save(oferta3);

            CriterioAdmision c5 = new CriterioAdmision();
            c5.setAsignatura("Historia del Arte");
            c5.setPeso(0.2);
            c5.setOferta(oferta3);
            criterioRepo.save(c5);

            CriterioAdmision c6 = new CriterioAdmision();
            c6.setAsignatura("Latín");
            c6.setPeso(0.2);
            c6.setOferta(oferta3);
            criterioRepo.save(c6);

            Oferta oferta4 = new Oferta();
            oferta4.setGrado("Psicología");
            oferta4.setTotalPlazas(350);
            oferta4.setUniversidad(uam);
            oferta4.setConvocatoria(conv);
            ofertaRepo.save(oferta4);

            CriterioAdmision c7 = new CriterioAdmision();
            c7.setAsignatura("Biología");
            c7.setPeso(0.2);
            c7.setOferta(oferta4);
            criterioRepo.save(c7);

            CriterioAdmision c8 = new CriterioAdmision();
            c8.setAsignatura("Física");
            c8.setPeso(0.2);
            c8.setOferta(oferta4);
            criterioRepo.save(c8);

            Oferta oferta5 = new Oferta();
            oferta5.setGrado("Bellas Artes");
            oferta5.setTotalPlazas(70);
            oferta5.setUniversidad(urjc);
            oferta5.setConvocatoria(conv);
            ofertaRepo.save(oferta5);

            CriterioAdmision c9 = new CriterioAdmision();
            c9.setAsignatura("Dibujo Técnico II");
            c9.setPeso(0.2);
            c9.setOferta(oferta5);
            criterioRepo.save(c9);

            CriterioAdmision c10 = new CriterioAdmision();
            c10.setAsignatura("Historia del Arte");
            c10.setPeso(0.2);
            c10.setOferta(oferta5);
            criterioRepo.save(c10);

            // Estudiantes de ejemplo
            Estudiante ana = new Estudiante();
            ana.setNombre("Ana");
            ana.setApellidos("García López");
            ana.setEmail("ana@eduplazas.es");
            ana.setPassword(encoder.encode("1234"));
            ana.setDni("11111111A");
            ana.setIdEvau("MAD-2025-001");
            ana.setNotaBase(0.0);
            estudianteRepo.save(ana);

            Estudiante carlos = new Estudiante();
            carlos.setNombre("Carlos");
            carlos.setApellidos("Martínez Ruiz");
            carlos.setEmail("carlos@eduplazas.es");
            carlos.setPassword(encoder.encode("1234"));
            carlos.setDni("22222222B");
            carlos.setIdEvau("MAD-2025-002");
            carlos.setNotaBase(0.0);
            estudianteRepo.save(carlos);

            // Admin
            Admin admin = new Admin();
            admin.setNombre("Admin");
            admin.setApellidos("EduPlazas");
            admin.setEmail("admin@eduplazas.es");
            admin.setPassword(encoder.encode("admin1234"));
            admin.setDni("00000000A");
            usuarioRepo.save(admin);

            

            System.out.println("Datos de ejemplo cargados correctamente");
        };
    }
}