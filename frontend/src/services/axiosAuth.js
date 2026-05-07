import axiosLib from 'axios';

const axiosAuth = axiosLib.create({
    baseURL: 'http://localhost:8080',
});

axiosAuth.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default axiosAuth;