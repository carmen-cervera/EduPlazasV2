import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './UniversidadInicio.module.css';
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png';

const UniversidadInicio = () => {
  const navigate = useNavigate();
  const [grados, setGrados] = useState([]);
  useEffect(() => {
    const ofertasGuardadas = JSON.parse(localStorage.getItem('misOfertas')) || [];
    setGrados(ofertasGuardadas);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/universidades/login');
  };

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src={logo} alt="Logo" className={styles.logoImg} />
        <h1 className={styles.tituloHeader}>Bienvenid@</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.uLogoSection}>
            <div className={styles.avatar}>🏛️</div>
            <p className={styles.uNombre}>Universidad Politécnica de Madrid</p>
          </div>

          <nav className={styles.navLinks}>
            <button
              className={styles.sidebarButton}
              onClick={() => navigate('/universidad/publicar')}
            >
              Publicar oferta
            </button>
            <button className={styles.sidebarButton}>Panel de demanda</button>
          </nav>

          <button className={styles.logoutBtn} onClick={handleLogout}>Log out</button>
        </aside>

        <main className={styles.main}>
          {/* Banner superior */}
          <div className={styles.banner}>
            <p className={styles.bannerText}>Convocatoria abierta:</p>
            <h3 className={styles.bannerTitle}>EvAU junio 2026</h3>
          </div>

          {/* Contenedor de la tabla */}
          <div className={styles.tableCard}>
            <h2 className={styles.tableTitle}>Grados publicados</h2>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Comunidad Autónoma</th>
                  <th>Rama</th>
                  <th>Plazas</th>
                </tr>
              </thead>
              <tbody>
                { }
                {grados.map((grado, index) => (
                  <tr key={index}>
                    <td>{grado.nombre}</td>
                    <td>{grado.comunidad}</td>
                    <td>{grado.rama}</td>
                    <td>{grado.plazas}</td>
                  </tr>
                ))}

                { }
                <tr>
                  <td>Ingeniería Informática</td>
                  <td>Comunidad de Madrid</td>
                  <td>Ingeniería y Arquitectura</td>
                  <td>150</td>
                </tr>
                <tr>
                  <td>Arquitectura</td>
                  <td>Comunidad de Madrid</td>
                  <td>Ingeniería y Arquitectura</td>
                  <td>120</td>
                </tr>


              </tbody>
            </table>
          </div>
        </main>
      </div>
    </div>
  );
};

export default UniversidadInicio;
