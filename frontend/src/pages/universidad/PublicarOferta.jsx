import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './PublicarOferta.module.css';
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

const PublicarOferta = () => {
  const navigate = useNavigate();
  const [nombre, setNombre] = useState('');
  const [plazas, setPlazas] = useState('');
  const [comunidad, setComunidad] = useState('');
  const [rama, setRama] = useState('');
  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/universidades/login');
  };
  const handleSubmit = (e) => {
    e.preventDefault(); // Detenemos el envío automático

    // VALIDACIÓN
    // Comprobamos que el nombre no esté vacío, que haya plazas y que se haya elegido comunidad y rama
    if (!nombre.trim()) {
      alert("❌ Error: Debes introducir el nombre del grado.");
      return;
    }

    if (plazas <= 0) {
      alert("❌ Error: El número de plazas debe ser mayor que 0.");
      return;
    }

    if (!comunidad) {
      alert("❌ Error: Por favor, selecciona una Comunidad Autónoma.");
      return;
    }

    if (!rama) {
      alert("❌ Error: Por favor, selecciona una Rama de conocimiento.");
      return;
    }
    const nuevaOferta = {
      nombre: nombre.trim(),
      plazas,
      comunidad,
      rama,
      id: Date.now()
    };

    const ofertasGuardadas = JSON.parse(localStorage.getItem('misOfertas')) || [];
    localStorage.setItem('misOfertas', JSON.stringify([nuevaOferta, ...ofertasGuardadas]));

    navigate('/universidad/inicio');
  };

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img src="/src/assets/LogoPequeño_FondoBlanco_SinGorro.png" alt="Logo" className={styles.logoImg} />
        <h1 className={styles.tituloHeader}>Publicar oferta</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <div className={styles.avatar}>🏛️</div>
            <p className={styles.email}>Universidad Politécnica de Madrid</p>
          </div>
          <nav>
            <button className={styles.primaryButton}>Publicar Oferta</button>
            <button className={styles.button} onClick={() => navigate('/universidad/inicio')} >Grados Publicados</button>
          </nav>
          <button className={styles.logoutBtn} onClick={handleLogout}>Log Out</button>
        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            <h2 className={styles.sectionTitle}>PUBLICAR OFERTA</h2>
            <p className={styles.convocatoria}>Convocatoria: EvAU Junio 2026</p>

            <form className={styles.formulario} onSubmit={handleSubmit}>
              <label className={styles.label}>Nombre del grado</label>
              <input type="text" className={styles.input} placeholder="Ej: Grado en Ingeniería Aeroespacial" value={nombre} onChange={(e) => setNombre(e.target.value)} />

              <label className={styles.label}>Número de plazas disponibles</label>
              <input type="number" className={styles.input} placeholder="0" value={plazas} onChange={(e) => setPlazas(e.target.value)} />

              <label className={styles.label}>Comunidad Autónoma</label>
              <select className={styles.input} value={comunidad} onChange={(e) => setComunidad(e.target.value)}>
                <option value="">Selecciona una comunidad</option>
                <option value="Andalucía">Andalucía</option>
                <option value="Aragón">Aragón</option>
                <option value="Cantabria">Cantabria</option>
                <option value="Castilla La Mancha">Castilla La Mancha</option>
                <option value="Castilla y León">Castilla y León</option>
                <option value="Cataluña">Cataluña</option>
                <option value="Comunidad de Madrid">Comunidad de Madrid</option>
                <option value="Comunidad Foral de Navarra">Comunidad Foral de Navarra</option>
                <option value="Comunidad Valenciana">Comunidad Valenciana</option>
                <option value="Extremadura">Extremadura</option>
                <option value="Galicia">Galicia</option>
                <option value="Islas Baleares">Islas Baleares</option>
                <option value="Islas Canarias">Islas Canarias</option>
                <option value="La Rioja">La Rioja</option>
                <option value="Murcia">Murcia</option>
                <option value="País Vasco">País Vasco</option>
                <option value="Principado de Asturias">Principado de Asturias</option>
              </select>

              <label className={styles.label}>Rama de conocimiento</label>
              <select className={styles.input} value={rama} onChange={(e) => setRama(e.target.value)}>
                <option value="">Selecciona una rama</option>
                <option value="Artes y Humanidades">Artes y Humanidades</option>
                <option value="Ciencias">Ciencias</option>
                <option value="Ciencias de la Salud">Ciencias de la Salud</option>
                <option value="Ciencias Sociales y Jurídicas">Ciencias Sociales y Jurídicas</option>
                <option value="Ingeniería y Arquitectura">Ingeniería y Arquitectura</option>
              </select>


              <div className={styles.footerButtons}>
                <button type="submit" className={styles.btnEnviarFormulario} style={{ maxWidth: '300px' }}>
                  PUBLICAR GRADO
                </button>
              </div>
            </form>
          </div>
        </main>
      </div>
    </div>
  );
};

export default PublicarOferta;